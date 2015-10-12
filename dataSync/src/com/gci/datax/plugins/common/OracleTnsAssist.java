package com.gci.datax.plugins.common;

import org.apache.commons.lang.StringUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 *
 * A assistant class to parse a tnsfile
 * 
 * Usage:
 * OracleTnsAssist assist = OracleTnsAssist.newInstance("/home/hedgehog/tnsnames.ora");
 * List<Map<String, String>> details = assist.search("tnsname");
 * 
 * details:
 * [{'SID'='dwdb', 'HOST'='192.168.0.1', 'PORT'='1521'}, 
 *  {'SID'='dwdb', 'HOST'='192.168.0.2', 'PORT'='1521'}, 
 *  {'SID'='dwdb', 'HOST'='192.168.0.3', 'PORT'='1521'},]
 *  
 * */
public class OracleTnsAssist {
	public static final String HOST_KEY = "HOST";

	public static final String PORT_KEY = "PORT";

	public static final String SID_KEY = "SID";

	private static final Pattern TNS_NAME_PATTERN = Pattern
			.compile("^(\\S+)\\s*=");

	private static final Pattern TNS_LOCATION_PATTERN = Pattern
			.compile(
					"\\(\\s*HOST\\s*=\\s*([^)\\s]+)\\s*\\)\\s*\\(\\s*PORT\\s*=\\s*(\\d+)\\s*\\)",
					Pattern.CASE_INSENSITIVE);

	private static final Pattern TNS_SID_PATTERN = Pattern.compile(
			"(SERVICE_NAME|SID)\\s*=\\s*([^)\\s]+)", Pattern.CASE_INSENSITIVE);

	private String tnsFile;

	private OracleTnsAssist(String filename) {
		this.tnsFile = filename;
	}

	/**
	 * Factory method to create a new {@link OracleTnsAssist}.
	 * 
	 * @param	filename
	 * 			tns file
	 * 
	 * @return	a new instance of {@link OracleTnsAssist}
	 * */
	public static OracleTnsAssist newInstance(String filename) {
		return new OracleTnsAssist(filename);
	}
	
	/**
	 * Search one tns-id in one tns file.
	 * 
	 * @param	tnsId
	 * 			tns id to search 
	 * 
	 * @return
	 * 			a list of tns configuration <br> 
	 * 			{"HOST": "hostname", "PORT": "port", "SID": "sid"}.
	 * @throws IOException 
	 * 
	 * */
	public List<Map<String, String>> search(String tnsId) throws IOException {
		return this.parseConnDetail(this.grepConnDetail(tnsId));
	}

	private List<String> parseTnsFile(String tnsFile) throws IOException {
		List<String> tns = new ArrayList<String>();
		BufferedReader reader = null;
		StringBuilder sb = new StringBuilder();
		String line = null;

		try {
			reader = new BufferedReader(new FileReader(tnsFile));
			while ((line = reader.readLine()) != null) {
				
				/* ignore comment line */
				if (isComment(line))
					continue;
				
				/* new line begins 
				 * NOTE:  a non-blank start line indicates new tns item begins
				 * 1 one blank line 
				 * 2 new tns identifier
				 * */
				if (TNS_NAME_PATTERN.matcher(line).find() &&
						sb.length() > 0) {
					tns.add(sb.toString());
					sb.setLength(0);
					sb.append(line);
					continue;
					/* */
				}
				sb.append(line);
			}
			if (sb.length() > 0) {
				tns.add(sb.toString());
			}
			return tns;
		} finally {
			if (reader != null)
				reader.close();
		}
	}

	private String grepConnDetail(String dbname) throws IOException {
		List<String> connDetails = this.parseTnsFile(this.tnsFile);
		for (String perConnDetail : connDetails) {
			Matcher matcher = TNS_NAME_PATTERN.matcher(perConnDetail);
			if (matcher.find() && matcher.group(1).equalsIgnoreCase(dbname)) {
				return perConnDetail;
			}
		}
		return "";
	}

	private List<Map<String, String>> parseConnDetail(String content) {
		List<Map<String, String>> connDetails = new ArrayList<Map<String, String>>();
		
		if (StringUtils.isBlank(content))
			return connDetails;

		Matcher sidMatcher = TNS_SID_PATTERN.matcher(content);
		if (!sidMatcher.find())
			throw new IllegalArgumentException(String.format(
					"Illegal TNS format, options SID missing : %s .", content));
		
		String sid = sidMatcher.group(2);
		Matcher locationMatcher = TNS_LOCATION_PATTERN.matcher(content);
		if (!locationMatcher.find())
			throw new IllegalArgumentException(String.format(
					"Illegal TNS format, options HOST/PORT missing : %s .", content));
		do {
			String host = locationMatcher.group(1);
			String port = locationMatcher.group(2);
			Map<String, String> location = new HashMap<String, String>();
			location.put(HOST_KEY, host);
			location.put(PORT_KEY, port);
			location.put(SID_KEY, sid);
			connDetails.add(location);
		} while (locationMatcher.find());
		
		return connDetails;
	}

	private boolean isBlankLine(String line) {
        return StringUtils.isBlank(line);
    }

	private boolean isComment(String line) {
		return line.trim().startsWith("#");
	}

	public static void main(String[] args) throws IOException {
		OracleTnsAssist assist = OracleTnsAssist.newInstance("/home/hedgehog/tnsnames.ora");
		System.out.println(assist.search("DWDB1"));
		System.out.println(assist.search("DWDB2"));
		System.out.println(assist.search("DWDB3"));
		System.out.println(assist.search("DWDB4"));
		System.out.println(assist.search("DWDB5"));
		System.out.println(assist.search("DWDB6"));
		System.out.println(assist.search("DWDB1_CM4"));
		System.out.println(assist.search("DWDB2_CM4"));
		System.out.println(assist.search("DWDB3_CM4"));
		System.out.println(assist.search("DWDB4_CM4"));
		System.out.println(assist.search("DWDB5_CM4"));
		System.out.println(assist.search("DWDB6_CM4"));
		System.out.println(assist.search("DWDB"));
		System.out.println(assist.search("ETLJOB"));
		System.out.println(assist.search("DWDB4_CM4_LZ"));
		System.out.println(assist.search("DWDB45"));
		System.out.println(assist.search("COMM_STB"));
		System.out.println(assist.search("COMM2_STB"));
		System.out.println(assist.search("DBC_STB"));
		System.out.println(assist.search("DB1_STB"));
		System.out.println(assist.search("MISC_STB"));
		System.out.println(assist.search("CRM_STB"));
		System.out.println(assist.search("DIGITAL_STB"));
		System.out.println(assist.search("B2C_STB"));
		System.out.println(assist.search("ET_STB"));
		System.out.println(assist.search("SHOP"));
		System.out.println(assist.search("sngps_dwdb"));
		System.out.println(assist.search("heart_stb"));
		System.out.println(assist.search("lake"));
		System.out.println(assist.search("lake_pri"));
		System.out.println(assist.search("DATAC_STB"));
		System.out.println(assist.search("DATAC"));
		System.out.println(assist.search("DATAC2"));
		System.out.println(assist.search("DATAC_PRI"));
		System.out.println(assist.search("icdb0_stb"));
		System.out.println(assist.search("icdb1_stb"));
		System.out.println(assist.search("icdb2_stb"));
		System.out.println(assist.search("icdb3_stb"));
		System.out.println(assist.search("icdb4_stb"));
		System.out.println(assist.search("icdb5_stb"));
		System.out.println(assist.search("icdb6_stb"));
		System.out.println(assist.search("icdb7_stb"));
		System.out.println(assist.search("icdb8_stb"));
		System.out.println(assist.search("icdb9_stb"));
		System.out.println(assist.search("icdb10_stb"));
		System.out.println(assist.search("icdb11_stb"));
		System.out.println(assist.search("icdb12_stb"));
		System.out.println(assist.search("icdb13_stb"));
		System.out.println(assist.search("icdb14_stb"));
		System.out.println(assist.search("icdb15_stb"));
		System.out.println(assist.search("kgbdw"));
		System.out.println(assist.search("alimm"));
		System.out.println(assist.search("alimm_dg"));
		System.out.println(assist.search("alimm_pri"));
		System.out.println(assist.search("alimm_product"));
		System.out.println(assist.search("HK"));
		System.out.println(assist.search("TW"));
		System.out.println(assist.search("CN"));
		System.out.println(assist.search("dumpcn"));
		System.out.println(assist.search("dumptw"));
		System.out.println(assist.search("dumphk"));
		System.out.println(assist.search("DM1"));
		System.out.println(assist.search("ALIPAY_DW"));
		System.out.println(assist.search("midpay"));
		System.out.println(assist.search("TBCALL"));
		System.out.println(assist.search("LPDB"));
		System.out.println(assist.search("dict"));
		System.out.println(assist.search("DWDB_OLD"));
		System.out.println(assist.search("DATACOPY"));
		System.out.println(assist.search("ETLJOB_OLD"));
		System.out.println(assist.search("ADHOC"));
		System.out.println(assist.search("ALERT"));
		System.out.println(assist.search("RAC_20_2"));
		System.out.println(assist.search("dataplat"));
		System.out.println(assist.search("SNGPS_SIMU"));
		System.out.println(assist.search("oradb_dataplat1"));
		System.out.println(assist.search("oradb_dataplat_aliyun"));
		System.out.println(assist.search("TBPRE"));
		
		return;
	}
}
