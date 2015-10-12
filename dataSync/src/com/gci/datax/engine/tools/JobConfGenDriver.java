package com.gci.datax.engine.tools;

import com.gci.datax.common.constants.Constants;

import org.apache.commons.io.FileUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

public class JobConfGenDriver {

	public enum PluginType {
		Reader, Writer;
	}

	private static List<String> getPluginDirAsList(String path) {
		return Arrays.asList(new File(path).list());
	}

	private static Map<PluginType, List<String>> filterPluginType(String path) {
		Map<PluginType, List<String>> ret = new HashMap<PluginType, List<String>>();

		ret.put(PluginType.Reader, getPluginDirAsList(path + "/reader/"));
		ret.put(PluginType.Writer, getPluginDirAsList(path + "/writer/"));

		return ret;
	}
	
	private static int showChoice(String header, List<String> plugins) {
		System.out.println(header);

		int idx = 0;
		for (String plugin : plugins) {
			System.out
					.println(String.format(
							"%d\t%s",
							idx++,
							plugin.toLowerCase().replace("reader", "")
									.replace("writer", "")));
		}

		System.out.print(String.format("请选择 [%d-%d]: ", 0,
				plugins.size() - 1));

		try {
			idx = Integer.valueOf(new Scanner(System.in).nextLine());
		} catch (Exception e) {
			// TODO: handle exception
			idx = -1;
		}

		return idx;
	}
	
	private static int genXmlFileByCsv(String fileName,Map<String,String> readerMap,Map<String,String> writerMap) throws IOException{
		Document document = DocumentHelper.createDocument();
		Element jobsElement = document.addElement("jobs");
		Element jobElement = jobsElement.addElement("job");
		if ("oracle".equals(writerMap.get("db_type"))){
			writerMap.put("db_type", writerMap.get("db_type")+"jdbc");
		}
		String id = readerMap.get("db_type") + "reader_to_" + writerMap.get("db_type") + "writer_job";
		jobElement.addAttribute("id", id);
		
		//生成reader部分的xml文件
		Element readerElement = jobElement.addElement("reader");
		Element plugin_Element = readerElement.addElement("plugin");
		plugin_Element.setText(readerMap.get("db_type")+"reader");

		Element tempElement = null;

		readerMap.remove("db_type");
		for (Entry<String, String> m : readerMap.entrySet()) {
			tempElement = readerElement.addElement("param");
			tempElement.addAttribute("key", m.getKey());
			tempElement.addAttribute("value", m.getValue());
		}
		
		//生成writer部分的xml文件
		Element writerElement = jobElement.addElement("writer");
		plugin_Element = writerElement.addElement("plugin");
		plugin_Element.setText(writerMap.get("db_type")+"writer");

		writerMap.remove("db_type");
        for (Entry<String, String> m : writerMap.entrySet()) {
			tempElement = writerElement.addElement("param");
			tempElement.addAttribute("key", m.getKey());
			tempElement.addAttribute("value", m.getValue());
		}
        
        try {
			OutputFormat format = OutputFormat.createPrettyPrint();
			format.setEncoding("UTF-8");
			XMLWriter writerOfXML = new XMLWriter(new FileWriter(new File(
					fileName)), format);
			writerOfXML.write(document);
			writerOfXML.close();
		} catch (Exception ex) {
			throw new IOException(ex.getCause());
		}
		
		return 0;
	}
	
	private static int genXmlFile(String filename, ClassNode reader,
			ClassNode writer) throws IOException {

		Document document = DocumentHelper.createDocument();
		Element jobsElement = document.addElement("jobs");
		Element jobElement = jobsElement.addElement("job");
		String id = reader.getName() + "_to_" + writer.getName() + "_job";
		jobElement.addAttribute("id", id);

		/**
		 * 生成reader部分的xml文件
		 */
		Element readerElement = jobElement.addElement("reader");
		Element plugin_Element = readerElement.addElement("plugin");
		plugin_Element.setText(reader.getName());

		ClassNode readerNode = reader;
		Element tempElement = null;

		List<ClassMember> members = readerNode.getAllMembers();
		for (ClassMember member : members) {
			StringBuilder command = new StringBuilder("\n");

			Set<String> set = member.getAllKeys();
			String value = "";
			for (String key : set) {
				value = member.getAttr("default");
				command.append(key).append(":").append(member.getAttr(key))
						.append("\n");
			}
			readerElement.addComment(command.toString());

			String keyName = member.getName();
			keyName = keyName.substring(1, keyName.length() - 1);
			tempElement = readerElement.addElement("param");
			tempElement.addAttribute("key", keyName);

			if (value == null || "".equals(value)) {
				value = "?";
			}
			tempElement.addAttribute("value", value);
		}

		/**
		 * 生成writer部分的xml文件
		 */
		Element writerElement = jobElement.addElement("writer");
		plugin_Element = writerElement.addElement("plugin");
		plugin_Element.setText(writer.getName());

        members = writer.getAllMembers();
		for (ClassMember member : members) {
			StringBuilder command = new StringBuilder("\n");
			Set<String> set = member.getAllKeys();

			String value = "";
			for (String key : set) {
				value = member.getAttr("default");
				command.append(key).append(":").append(member.getAttr(key))
						.append("\n");
			}
			writerElement.addComment(command.toString());

			String keyName = member.getName();
			keyName = keyName.substring(1, keyName.length() - 1);
			tempElement = writerElement.addElement("param");
			tempElement.addAttribute("key", keyName);

			if (value == null || "".equals(value)) {
				value = "?";
			}
			tempElement.addAttribute("value", value);
		}

		try {
			OutputFormat format = OutputFormat.createPrettyPrint();
			format.setEncoding("UTF-8");
			XMLWriter writerOfXML = new XMLWriter(new FileWriter(new File(
					filename)), format);
			writerOfXML.write(document);
			writerOfXML.close();
		} catch (Exception ex) {
			throw new IOException(ex.getCause());
		}
		
		return 0;
	}

	public static int produceXmlConf() throws IOException {

		Map<PluginType, List<String>> plugins = filterPluginType(Constants.DATAX_LOCATION
				+ "/src/com/gci/datax/plugins/");

		int readerIdx = -1;
		do {
			readerIdx = showChoice("数据源列表 :", plugins.get(PluginType.Reader));
		} while (readerIdx < 0
				|| readerIdx >= plugins.get(PluginType.Reader).size());

		int writerIdx = -1;
		do {
			writerIdx = showChoice("目的数据源列表 :", plugins.get(PluginType.Writer));
		} while (writerIdx < 0
				|| writerIdx >= plugins.get(PluginType.Writer).size());

		String readerName = plugins.get(PluginType.Reader).get(readerIdx);
		String readerPath = Constants.DATAX_LOCATION + "/src/com/gci/datax/plugins/reader/"
				+ readerName + "/ParamKey.java";
		ClassNode reader = ParseUtils.parse(readerName,
				FileUtils.readFileToString(new File(readerPath)));

		String writerName = plugins.get(PluginType.Writer).get(writerIdx);
		String writerPath = Constants.DATAX_LOCATION + "/src/com/gci/datax/plugins/writer/"
				+ writerName + "/ParamKey.java";
		ClassNode writer = ParseUtils.parse(writerName,
				FileUtils.readFileToString(new File(writerPath)));
		
		String filename = System.getProperty("user.dir") + "/jobs/"
				+ reader.getName() + "_to_" + writer.getName() + "_"
				+ System.currentTimeMillis() + ".xml";
		
		if (0 != genXmlFile(filename, reader, writer)) {
			return -1;
		}
		
		System.out.println(String.format("生成 %s 成功 .", filename));
		return 0;
	}
	
	public static int produceXmlConfByCsv(String fileName,Map<String,String> readerMap,Map<String,String> writerMap) throws IOException {	
		//String filename = Constants.DATAX_LOCATION + "/jobs/" + fileName + ".xml";
		
		if (0 != genXmlFileByCsv(fileName, readerMap, writerMap)) {
			return -1;
		}
		
		System.out.println(String.format("生成 %s 成功 .", fileName));
		return 0;
	}

}
