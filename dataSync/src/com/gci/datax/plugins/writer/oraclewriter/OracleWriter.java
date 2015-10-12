package com.gci.datax.plugins.writer.oraclewriter;

import com.gci.datax.common.constants.Constants;
import com.gci.datax.common.exception.DataExchangeException;
import com.gci.datax.common.exception.ExceptionTracker;
import com.gci.datax.common.plugin.*;
import com.gci.datax.common.util.EnvUtils;
import com.gci.datax.common.util.StrUtils;

import org.apache.log4j.Logger;

import java.util.List;

public class OracleWriter extends Writer {

	/* \001 is field split, \002 is line split */
	private static final char[] replaceChars = { '\001', 0, '\002', 0 };

	private Logger logger = Logger.getLogger(OracleWriter.class);

	private String password;

	private String username;

	private String dbname;

	private String table;

	private static final char SEP = '\001';

	private static final char BREAK = '\002';

	private String pre;

	private String post;

	private String dtfmt;

	private String encoding;

	private String colorder;

	private String limit;

	private long concurrency;

	private String logon;

	private long p;

	private int save2server;

	private int commit2server;

	private long skipindex;

	static {
		String writerSharedObjectPath = Constants.DATAX_LOCATION
				+ "/plugins/writer/oraclewriter/";
		System.setProperty("java.library.path",
				System.getProperty("java.library.path") + ":"
						+ writerSharedObjectPath);
		EnvUtils.putEnv("LD_LIBRARY_PATH", writerSharedObjectPath + ":"
				+ EnvUtils.getEnv("LD_LIBRARY_PATH"));
	}

	@Override
	public List<PluginParam> split(PluginParam param) {
		OracleWriterSplitter spliter = new OracleWriterSplitter();
		spliter.setParam(param);
		spliter.init();
		return spliter.split();
	}

	@Override
	public int prepare(PluginParam param) {
		try {
			if (!pre.isEmpty()) {
				this.logger.info(String
						.format("OracleWriter starts to execute pre-sql %s .",
								this.pre));
				p = OracleWriterJni.getInstance().oracle_dumper_init(logon,
						table, String.valueOf(SEP), pre, post, dtfmt, encoding,
						colorder, limit, concurrency, skipindex);
				OracleWriterJni.getInstance().oracle_dumper_connect(p);
				OracleWriterJni.getInstance().oracle_dumper_predump(p, 0);
				OracleWriterJni.getInstance().oracle_dumper_finish(p, 1);
			}
		} catch (Exception e) {
			logger.error(ExceptionTracker.trace(e));
			throw new DataExchangeException(e.getCause());
		}

		return PluginStatus.SUCCESS.value();
	}

	@Override
	public int post(PluginParam param) {
		try {
			if (!post.isEmpty()) {
				this.logger.info(String.format(
						"OracleWriter starts to execute post-sql %s .",
						this.post));
				p = OracleWriterJni.getInstance().oracle_dumper_init(logon,
						table, String.valueOf(SEP), pre, post, dtfmt, encoding,
						colorder, limit, concurrency, skipindex);
				OracleWriterJni.getInstance().oracle_dumper_connect(p);
				OracleWriterJni.getInstance().oracle_dumper_finish(p, 3); // end
			}
			return PluginStatus.SUCCESS.value();
		} catch (Exception e) {
			logger.error(ExceptionTracker.trace(e));
			throw new DataExchangeException(e.getCause());
		}

	}

	@Override
	public int init() {
		password = param.getValue(ParamKey.password, "");
		username = param.getValue(ParamKey.username, "");

		dbname = param.getValue(ParamKey.dbname, "");
		table = param.getValue("schema") + "."
				+ param.getValue(ParamKey.table, "");
		dtfmt = StrUtils.removeSpace(param.getValue(ParamKey.dtfmt, ""), ",");
		pre = param.getValue(ParamKey.pre, "");
		post = param.getValue(ParamKey.post, "");
		encoding = param.getValue(ParamKey.encoding, "UTF-8");
		colorder = StrUtils.removeSpace(param.getValue(ParamKey.colorder, ""),
				",");
		limit = param.getValue(ParamKey.limit, "");
		concurrency = param.getIntValue(ParamKey.concurrency, 1);

		commit2server = 50000;
		save2server = 1000;
		skipindex = 0;
		logon = username + "/" + password + "@" + dbname;

		return PluginStatus.SUCCESS.value();
	}

	@Override
	public int connect() {
		try {
			p = OracleWriterJni.getInstance().oracle_dumper_init(logon, table,
					String.valueOf(SEP), "", post, dtfmt, encoding, colorder,
					limit, concurrency, skipindex);
			OracleWriterJni.getInstance().oracle_dumper_connect(p);
		} catch (Exception e) {
			logger.error(ExceptionTracker.trace(e));
			throw new DataExchangeException(e.getCause());
		}
		return PluginStatus.SUCCESS.value();
	}

	@Override
	public int startWrite(LineReceiver resultHandler) {
		try {
			OracleWriterJni.getInstance().oracle_dumper_predump(p, 1);
			Line line = null;
			String field;
			int iCount = 0;
			int iCount1 = 0;

			StringBuilder sb = new StringBuilder(1024000);
			while ((line = resultHandler.getFromReader()) != null) {
				int num = line.getFieldNum();
				for (int i = 0; i < num; i++) {
					field = line.getField(i);
					if (null != field) {
						sb.append(StrUtils.replaceChars(field, replaceChars));
					} /*
					 * else { sb.append(""); }
					 */
					sb.append(SEP);
				}
				sb.delete(sb.length() - 1, sb.length());
				sb.append(BREAK);

				if (iCount == save2server) {
					OracleWriterJni.getInstance().oracle_dumper_dump(p,
							sb.toString());
					sb.setLength(0);
					iCount = 0;
				}
				iCount++;

				if (iCount1 == commit2server) {
					sb.append(BREAK + "1y9i8x7i0a3o2*5" + BREAK);
					OracleWriterJni.getInstance().oracle_dumper_dump(p,
							sb.toString());
					sb.setLength(0);
					commit();
					iCount1 = 0;
				}
				iCount1++;
			}
			sb.append(BREAK + "1y9i8x7i0a3o2*5" + BREAK);
			OracleWriterJni.getInstance().oracle_dumper_dump(p, sb.toString());

			return PluginStatus.SUCCESS.value();
		} catch (Exception e) {
			logger.error(ExceptionTracker.trace(e));
			throw new DataExchangeException(e.getCause());
		}
	}

	@Override
	public int commit() {
		try {
			int ret = OracleWriterJni.getInstance().oracle_dumper_commit(p);
			if (0 != ret) {
				return PluginStatus.FAILURE.value();
			}
			return PluginStatus.SUCCESS.value();
		} catch (Exception e) {
			logger.error(ExceptionTracker.trace(e));
			throw new DataExchangeException(e.getCause());
		}
	}

	@Override
	public int finish() {
		try {
			int discard = OracleWriterJni.getInstance().oracle_dumper_finish(p,
					1);
			this.getMonitor().setFailedLines(discard);
			return PluginStatus.SUCCESS.value();
		} catch (Exception e) {
			logger.error(ExceptionTracker.trace(e));
			throw new DataExchangeException(e.getCause());
		}
	}
}
