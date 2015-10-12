package com.gci.util.http;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadBase.FileSizeLimitExceededException;
import org.apache.commons.fileupload.FileUploadBase.IOFileUploadException;
import org.apache.commons.fileupload.FileUploadBase.InvalidContentTypeException;
import org.apache.commons.fileupload.FileUploadBase.SizeLimitExceededException;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.ProgressListener;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileCleaningTracker;

import com.gci.util.GeneralHelper;
import com.gci.util.LStrSet;

/***
 * 文件上传器
 * 
 * @author hgq
 * 
 */
public class FileUploader {
	/** 不限制文件上传总大小的 Size Max 常量 */
	public static final long NO_LIMIT_SIZE_MAX = -1;
	/** 不限制文件上传单个文件大小的 File Size Max 常量 */
	public static final long NO_LIMIT_FILE_SIZE_MAX = -1;
	/** 默认的写文件阀值 */
	public static final int DEFAULT_SIZE_THRESHOLD = DiskFileItemFactory.DEFAULT_SIZE_THRESHOLD;
	/** 默认的文件名生成器 */
	public static final FileNameGenerator DEFAULT_FILE_NAME_GENERATOR = new CommonFileNameGenerator();

	private String savePath;
	private long sizeMax = NO_LIMIT_SIZE_MAX;
	private long fileSizeMax = NO_LIMIT_FILE_SIZE_MAX;
	private Set<String> acceptTypes = new LStrSet();
	private Map<String, String[]> paramFields = new HashMap<String, String[]>();
	private Map<String, FileInfo[]> fileFields = new HashMap<String, FileInfo[]>();

	private FileNameGenerator fileNameGenerator = DEFAULT_FILE_NAME_GENERATOR;

	private int factorySizeThreshold = DEFAULT_SIZE_THRESHOLD;
	private String factoryRepository;
	private FileCleaningTracker factoryCleaningTracker;
	private String servletHeaderencoding;
	private ProgressListener servletProgressListener;

	private Throwable cause;

	public FileUploader() {

	}

	/**
	 * 构造函数
	 * 
	 * @param savePath
	 *            : 上传文件的保存路径（不包含文件名），参考：
	 *            {@link FileUploader#setSavePath(String)}
	 */
	public FileUploader(String savePath) {
		this(savePath, null);
	}

	/**
	 * 构造函数
	 * 
	 * @param savePath
	 *            : 上传文件的保存路径（不包含文件名），参考：
	 *            {@link FileUploader#setSavePath(String)}
	 * @param sizeMax
	 *            : 文件上传的总文件大小限制，默认：{@link FileUploader#NO_LIMIT_SIZE_MAX}
	 * @param fileSizeMax
	 *            : 文件上传的单个文件大小限制，默认：{@link FileUploader#NO_LIMIT_FILE_SIZE_MAX}
	 */
	public FileUploader(String savePath, long sizeMax, long fileSizeMax) {
		this(savePath, null, sizeMax, fileSizeMax);
	}

	/**
	 * 构造函数
	 * 
	 * @param savePath
	 *            : 上传文件的保存路径（不包含文件名），参考：
	 *            {@link FileUploader#setSavePath(String)}
	 * @param acceptTypes
	 *            : 可接受的上传文件类型集合，默认：不限制
	 */
	public FileUploader(String savePath, String[] acceptTypes) {
		this(savePath, acceptTypes, NO_LIMIT_SIZE_MAX, NO_LIMIT_FILE_SIZE_MAX);
	}

	/**
	 * 构造函数
	 * 
	 * @param savePath
	 *            : 上传文件的保存路径（不包含文件名），参考：
	 *            {@link FileUploader#setSavePath(String)}
	 * @param acceptTypes
	 *            : 可接受的上传文件类型集合，默认：不限制
	 * @param sizeMax
	 *            : 文件上传的总文件大小限制，默认：{@link FileUploader#NO_LIMIT_SIZE_MAX}
	 * @param fileSizeMax
	 *            : 文件上传的单个文件大小限制，默认：{@link FileUploader#NO_LIMIT_FILE_SIZE_MAX}
	 */
	public FileUploader(String savePath, String[] acceptTypes, long sizeMax,
			long fileSizeMax) {
		this.savePath = savePath;
		this.sizeMax = sizeMax;
		this.fileSizeMax = fileSizeMax;

		if (acceptTypes != null)
			setAcceptTypes(acceptTypes);
	}

	/** 获取上传文件的保存路径（不包含文件名） */
	public String getSavePath() {
		return savePath;
	}

	/**
	 * 设置上传文件的保存路径（不包含文件名）
	 * 
	 * @param savePath
	 *            : 文件路径，可能是绝对路径或相对路径<br>
	 *            1) 绝对路径：以根目录符开始（如：'/'、'D:\'），是服务器文件系统的路径<br>
	 *            2) 相对路径：不以根目录符开始，是相对于 WEB 应用程序 Context 的路径，（如：mydir 是指
	 *            '${WEB-APP-DIR}/mydir'）<br>
	 *            3) 规则：上传文件前会检查该路径是否存在，如果不存在则会尝试生成该路径，如果生成失败则 上传失败并返回
	 *            {@link Result#INVALID_SAVE_PATH}
	 * 
	 */
	public void setSavePath(String savePath) {
		this.savePath = savePath;
	}

	/** 获取文件上传的单个文件大小限制 */
	public long getFileSizeMax() {
		return fileSizeMax;
	}

	/** 设置文件上传的单个文件大小限制 */
	public void setFileSizeMax(long fileSizeMax) {
		this.fileSizeMax = fileSizeMax;
	}

	/** 获取文件上传的总文件大小限制 */
	public long getSizeMax() {
		return sizeMax;
	}

	/** 设置文件上传的总文件大小限制 */
	public void setSizeMax(long sizeMax) {
		this.sizeMax = sizeMax;
	}

	/** 获取可接受的上传文件类型集合 */
	public Set<String> getAcceptTypes() {
		return acceptTypes;
	}

	/** 设置可接受的上传文件类型集合 */
	public void setAcceptTypes(Set<String> acceptTypes) {
		this.acceptTypes.clear();

		for (String type : acceptTypes)
			addAcceptType(type);
	}

	/** 设置可接受的上传文件类型集合 */
	public void setAcceptTypes(String[] acceptTypes) {
		this.acceptTypes.clear();

		for (String type : acceptTypes)
			addAcceptType(type);
	}

	/** 添加一个可接受的上传文件类型 */
	public boolean addAcceptType(String acceptType) {
		acceptType = adjustAcceptType(acceptType);

		if (acceptType.length() > 1)
			return this.acceptTypes.add(acceptType);

		return false;
	}

	/** 删除一个可接受的上传文件类型 */
	public boolean removeAcceptType(String acceptType) {
		acceptType = adjustAcceptType(acceptType);

		if (acceptType.length() > 1)
			return this.acceptTypes.remove(acceptType);

		return false;
	}

	private String adjustAcceptType(String acceptType) {
		int index = acceptType.lastIndexOf(".");

		if (index != -1)
			acceptType = acceptType.substring(index, acceptType.length());
		else
			acceptType = "." + acceptType;

		return acceptType;
	}

	/**
	 * 获取所有非文件表单域的映射
	 * 
	 * @return : key -> 表单域名称，value -> 表单内容，类型为 {@link String}[ ]
	 * 
	 */
	public Map<String, String[]> getParamFields() {
		return paramFields;
	}

	private void addParamField(String name, String value) {
		String[] array = paramFields.get(name);
		array = addField(array, name, value);
		paramFields.put(name, array);
	}

	@SuppressWarnings("unchecked")
	private <T> T[] addField(T[] array, String name, T value) {
		if (array == null) {
			array = (T[]) Array.newInstance(value.getClass(), 1);
			array[0] = value;
		} else {
			T[] array2 = (T[]) Array.newInstance(value.getClass(),
					array.length + 1);
			System.arraycopy(array, 0, array2, 0, array.length);
			array2[array.length] = value;
			array = array2;
		}

		return array;
	}

	/**
	 * 获取所有文件表单域的映射
	 * 
	 * @return : key -> 表单域名称，value -> {@link FileInfo}[ ]
	 * 
	 */
	public Map<String, FileInfo[]> getFileFields() {
		return fileFields;
	}

	private void addFileField(String name, FileInfo value) {
		FileInfo[] array = fileFields.get(name);
		array = addField(array, name, value);
		fileFields.put(name, array);
	}

	/** 获取上传文件过程中的临时文件存放位置，参考：{@link DiskFileItemFactory#getRepository()} */
	public String getFactoryRepository() {
		return factoryRepository;
	}

	/** 设置上传文件过程中的临时文件存放位置，参考：{@link DiskFileItemFactory#setRepository(File)} */
	public void setFactoryRepository(String factoryRepository) {
		this.factoryRepository = factoryRepository;
	}

	/**
	 * 获取写文件阀值，当上传的内容超过该值就把内容写到磁盘中，参考：
	 * {@link DiskFileItemFactory#getSizeThreshold()}
	 */
	public int getFactorySizeThreshold() {
		return factorySizeThreshold;
	}

	/**
	 * 设置写文件阀值，当上传的内容超过该值就把内容写到磁盘中，参考：
	 * {@link DiskFileItemFactory#setSizeThreshold(int)}
	 */
	public void setFactorySizeThreshold(int factorySizeThreshold) {
		this.factorySizeThreshold = factorySizeThreshold;
	}

	/** 获取临时文件跟踪器，参考：{@link DiskFileItemFactory#getFileCleaningTracker()} */
	public FileCleaningTracker getFactoryCleaningTracker() {
		return factoryCleaningTracker;
	}

	/**
	 * 设置临时文件跟踪器，参考：
	 * {@link DiskFileItemFactory#setFileCleaningTracker(FileCleaningTracker)}
	 */
	public void setFactoryCleaningTracker(
			FileCleaningTracker factoryCleaningTracker) {
		this.factoryCleaningTracker = factoryCleaningTracker;
	}

	/** 获取上传组件解析上传内容的编码格式，参考：{@link ServletFileUpload#getHeaderEncoding()} */
	public String getServletHeaderencoding() {
		return servletHeaderencoding;
	}

	/** 设置上传组件解析上传内容的编码格式，参考：{@link ServletFileUpload#setHeaderEncoding(String)} */
	public void setServletHeaderencoding(String servletHeaderencoding) {
		this.servletHeaderencoding = servletHeaderencoding;
	}

	/** 获取上传组件的处理进程监听器，参考：{@link ServletFileUpload#getProgressListener()} */
	public ProgressListener getServletProgressListener() {
		return servletProgressListener;
	}

	/**
	 * 设置上传组件的处理进程监听器，参考：
	 * {@link ServletFileUpload#setProgressListener(ProgressListener)}
	 */
	public void setServletProgressListener(
			ProgressListener servletProgressListener) {
		this.servletProgressListener = servletProgressListener;
	}

	/** 获取文件名生成器，参考：{@link FileNameGenerator} */
	public FileNameGenerator getFileNameGenerator() {
		return fileNameGenerator;
	}

	/** 设置文件名生成器，参考：{@link FileNameGenerator} */
	public void setFileNameGenerator(FileNameGenerator fileNameGenerator) {
		this.fileNameGenerator = fileNameGenerator;
	}

	/** 获取文件上传失败的原因（文件上传失败时使用） */
	public Throwable getCause() {
		return cause;
	}

	private void reset() {
		cause = null;
		fileFields.clear();
		paramFields.clear();
	}

	private void clean(List<FileItemInfo> fiis, int count) {
		reset();

		for (int i = 0; i < count; i++) {
			File file = fiis.get(i).file;

			try {
				file.delete();
			} catch (SecurityException e) {

			}
		}
	}

	/**
	 * 执行上传
	 * 
	 * @param request
	 *            : {@link HttpServletRequest} 对象
	 * @param response
	 *            : {@link HttpServletResponse} 对象
	 * 
	 * @return : 成功：返回 {@link Result#SUCCESS} ，失败：返回其他结果， 失败原因通过
	 *         {@link FileUploader#getCause()} 获取
	 * 
	 */
	public Result upload(HttpServletRequest request,
			HttpServletResponse response) {
		reset();

		String absolutePath = getAbsoluteSavePath(request);
		if (absolutePath == null) {
			cause = new FileNotFoundException(String.format(
					"path '%s' not found or is not directory", savePath));
			return Result.INVALID_SAVE_PATH;
		}

		ServletFileUpload sfu = getFileUploadComponent();
		List<FileItemInfo> fiis = new ArrayList<FileItemInfo>();

		List<FileItem> items = null;
		Result result = Result.SUCCESS;

		String encoding = servletHeaderencoding != null ? servletHeaderencoding
				: request.getCharacterEncoding();
		FileNameGenerator fnGenerator = fileNameGenerator != null ? fileNameGenerator
				: DEFAULT_FILE_NAME_GENERATOR;

		try {
			items = (List<FileItem>) sfu.parseRequest(request);
		} catch (FileUploadException e) {
			cause = e;

			if (e instanceof FileSizeLimitExceededException)
				result = Result.FILE_SIZE_EXCEEDED;
			else if (e instanceof SizeLimitExceededException)
				result = Result.SIZE_EXCEEDED;
			else if (e instanceof InvalidContentTypeException)
				result = Result.INVALID_CONTENT_TYPE;
			else if (e instanceof IOFileUploadException)
				result = Result.FILE_UPLOAD_IO_EXCEPTION;
			else
				result = Result.OTHER_PARSE_REQUEST_EXCEPTION;
		}

		if (result == Result.SUCCESS) {
			result = parseFileItems(items, fnGenerator, absolutePath, encoding,
					fiis);
			if (result == Result.SUCCESS)
				result = writeFiles(fiis);
		}

		return result;
	}

	private Result writeFiles(List<FileItemInfo> fiis) {
		for (int i = 0; i < fiis.size(); i++) {
			FileItemInfo fii = fiis.get(i);

			try {
				fii.item.write(fii.file);
			} catch (Exception e) {
				clean(fiis, i);

				cause = e;
				return Result.WRITE_FILE_FAIL;
			}
		}

		return Result.SUCCESS;
	}

	private Result parseFileItems(List<FileItem> items,
			FileNameGenerator fnGenerator, String absolutePath,
			String encoding, List<FileItemInfo> fiis) {
		for (FileItem item : items) {
			if (item.isFormField())
				parseFormField(item, encoding);
			else {
				if (GeneralHelper.isStrEmpty(item.getName()))
					continue;

				Result result = parseFileField(item, absolutePath, fnGenerator,
						fiis);
				if (result != Result.SUCCESS) {
					reset();

					cause = new InvalidParameterException(String.format(
							"file '%s' not accepted", item.getName()));
					return result;
				}
			}
		}

		return Result.SUCCESS;
	}

	private Result parseFileField(FileItem item, String absolutePath,
			FileNameGenerator fnGenerator, List<FileItemInfo> fiis) {
		String suffix = null;
		String uploadFileName = item.getName();
		boolean isAcceptType = acceptTypes.isEmpty();
		int stuffPos = uploadFileName.lastIndexOf(".");

		if (stuffPos != -1) {
			suffix = uploadFileName
					.substring(stuffPos, uploadFileName.length()).toLowerCase();

			if (!isAcceptType)
				isAcceptType = acceptTypes.contains(suffix);
		}

		if (!isAcceptType)
			return Result.INVALID_FILE_TYPE;

		String saveFileName = fnGenerator.generate(item, suffix);

		if (suffix != null && !saveFileName.endsWith(suffix))
			saveFileName += suffix;

		String fullFileName = absolutePath + File.separator + saveFileName;
		File saveFile = new File(fullFileName);
		FileInfo info = new FileInfo(uploadFileName, saveFile);

		fiis.add(new FileItemInfo(item, saveFile));
		addFileField(item.getFieldName(), info);

		return Result.SUCCESS;
	}

	private void parseFormField(FileItem item, String encoding) {
		String name = item.getFieldName();
		String value = item.getString();

		if (!GeneralHelper.isStrEmpty(value) && encoding != null) {
			try {
				value = new String(value.getBytes("ISO-8859-1"), encoding);
			} catch (UnsupportedEncodingException e) {

			}
		}

		addParamField(name, value);
	}

	private ServletFileUpload getFileUploadComponent() {
		DiskFileItemFactory dif = new DiskFileItemFactory();

		if (factorySizeThreshold != DEFAULT_SIZE_THRESHOLD)
			dif.setSizeThreshold(factorySizeThreshold);
		if (factoryRepository != null)
			dif.setRepository(new File(factoryRepository));
		if (factoryCleaningTracker != null)
			dif.setFileCleaningTracker(factoryCleaningTracker);

		ServletFileUpload sfu = new ServletFileUpload(dif);

		if (sizeMax != NO_LIMIT_SIZE_MAX)
			sfu.setSizeMax(sizeMax);
		if (fileSizeMax != NO_LIMIT_FILE_SIZE_MAX)
			sfu.setFileSizeMax(fileSizeMax);
		if (servletHeaderencoding != null)
			sfu.setHeaderEncoding(servletHeaderencoding);
		if (servletProgressListener != null)
			sfu.setProgressListener(servletProgressListener);

		return sfu;
	}

	private String getAbsoluteSavePath(HttpServletRequest request) {
		String path = null;
		File file = new File(savePath);

		if (!file.isAbsolute())
			file = new File(HttpHelper.getRequestRealPath(request, savePath));
		if (file.isDirectory())
			path = file.getAbsolutePath();
		else if (!file.exists()) {
			try {
				synchronized (FileUploader.class) {
					if (file.exists() || file.mkdirs())
						path = file.getAbsolutePath();
				}
			} catch (SecurityException e) {

			}
		}

		return path;
	}

	/**
	 * 文件名生成器接口
	 * 
	 * 每次保存一个上传文件前都需要调用该接口的 {@link FileNameGenerator#generate} 方法生成要保存的文件名
	 * 
	 */
	public static interface FileNameGenerator {
		/**
		 * 文件名生成方法
		 * 
		 * @param item
		 *            : 上传文件对应的 {@link FileItem} 对象
		 * @param suffix
		 *            : 上传文件的后缀名
		 * 
		 */
		String generate(FileItem item, String suffix);
	}

	/**
	 * 通用文件名生成器
	 * 
	 * 实现 {@link FileNameGenerator} 接口，根据序列值和时间生成唯一文件名
	 * 
	 */
	public static class CommonFileNameGenerator implements FileNameGenerator {
		private static final int MAX_SERIAL = 999999;
		private static final AtomicInteger atomic = new AtomicInteger();

		private static int getNextInteger() {
			int value = atomic.incrementAndGet();
			if (value >= MAX_SERIAL)
				atomic.set(0);

			return value;
		}

		/** 根据序列值和时间生成 'XXXXXX_YYYYYYYYYYYYY' 格式的唯一文件名 */
		@Override
		public String generate(FileItem item, String suffix) {
			int serial = getNextInteger();
			long millsec = System.currentTimeMillis();

			return String.format("%06d_%013d", serial, millsec);
		}
	}

	/** 上传文件信息结构体 */
	public static class FileInfo {
		private String uploadFileName;
		private File saveFile;

		FileInfo() {

		}

		FileInfo(String uploadFileName, File saveFile) {
			this.uploadFileName = uploadFileName;
			this.saveFile = saveFile;
		}

		/**
		 * 获取上传文件的原始文件名
		 * 
		 * （对于不同的客户端浏览器，可能包含也可能不包含文件路径）
		 * 
		 */
		public String getUploadFileName() {
			return uploadFileName;
		}

		/** 获取上传文件的原始文件名（不包含文件路径） */
		public String getUploadFileSimpleName() {
			if (uploadFileName != null) {
				String path = uploadFileName;
				if (!GeneralHelper.isWindowsPlatform())
					path = path.replace('\\', File.separatorChar);

				return new File(path).getName();
			}

			return null;
		}

		void setUploadFileName(String uploadFileName) {
			this.uploadFileName = uploadFileName;
		}

		/** 获取被保存的上传文件的 {@link File} 对象 */
		public File getSaveFile() {
			return saveFile;
		}

		void setSaveFile(File saveFile) {
			this.saveFile = saveFile;
		}
	}

	private class FileItemInfo {
		FileItem item;
		File file;

		public FileItemInfo(FileItem item, File file) {
			this.item = item;
			this.file = file;
		}
	}

	/** 文件上传结果枚举值 */
	public static enum Result {
		/** 成功 */
		SUCCESS,
		/** 失败：文件总大小超过限制 */
		SIZE_EXCEEDED,
		/** 失败：单个文件大小超过限制 */
		FILE_SIZE_EXCEEDED,
		/** 失败：请求表单类型不正确 */
		INVALID_CONTENT_TYPE,
		/** 失败：文件上传 IO 错误 */
		FILE_UPLOAD_IO_EXCEPTION,
		/** 失败：解析上传请求其他异常 */
		OTHER_PARSE_REQUEST_EXCEPTION,
		/** 失败：文件类型不正确 */
		INVALID_FILE_TYPE,
		/** 失败：文件写入失败 */
		WRITE_FILE_FAIL,
		/** 失败：文件保存路径不正确 */
		INVALID_SAVE_PATH;
	}
}
