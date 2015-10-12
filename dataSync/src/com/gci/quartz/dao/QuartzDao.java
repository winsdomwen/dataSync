package com.gci.quartz.dao;

import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.gci.Constant;

@Repository("quartzDao")
@SuppressWarnings("unchecked")
public class QuartzDao {

	private DataSource dataSource;

	@Autowired
	public void setDataSource(@Qualifier("dataSource") DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	public int getMaxPriority() {
		return getJdbcTemplate().queryForInt("SELECT Min(PRIORITY) from qrtz_triggers");
	}
	
	public int addStat(Map params) {
		return getJdbcTemplate().update("insert into DATASYNC_STAT (JOB_ID ,SOURCE_TYPE ,SOURCE_DB ,SOURCE_TABLE ,SINK_TYPE ," +
				"SINK_DB ,SINK_TABLE ,READ_RECORDS ,WRITE_RECORDS ,DISCARD_RECORDS ,BEGINE_TIME ,END_TIME ," +
				"READ_BYTES ,WRITE_BYTES ,DISCARD_BYTES ) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",new Object[]{
				params.get("JOB_ID"),params.get("SOURCE_TYPE"),params.get("SOURCE_DB"),params.get("SOURCE_TABLE"),params.get("SINK_TYPE"),
				params.get("SINK_DB"),params.get("SINK_TABLE"),params.get("READ_RECORDS"),params.get("WRITE_RECORDS"),params.get("DISCARD_RECORDS"),
				params.get("BEGINE_TIME"),params.get("END_TIME"),params.get("READ_BYTES"),params.get("WRITE_BYTES"),params.get("DISCARD_BYTES")
		});
	}

	public List<Map<String, Object>> getQrtzTriggers() {
		List<Map<String, Object>> results = getJdbcTemplate().queryForList("select * from QRTZ_TRIGGERS order by start_time");
		long val = 0;
		String temp = null;
		for (Map<String, Object> map : results) {
			temp = MapUtils.getString(map, "trigger_name");
			if(StringUtils.indexOf(temp, "&") != -1){
				map.put("display_name", StringUtils.substringBefore(temp, "&"));
			}else{
				map.put("display_name", temp);
			}
			
			val = MapUtils.getLongValue(map, "next_fire_time");
			if (val > 0) {
				map.put("next_fire_time", DateFormatUtils.format(val, "yyyy-MM-dd HH:mm:ss"));
			}

			val = MapUtils.getLongValue(map, "prev_fire_time");
			if (val > 0) {
				map.put("prev_fire_time", DateFormatUtils.format(val, "yyyy-MM-dd HH:mm:ss"));
			}

			val = MapUtils.getLongValue(map, "start_time");
			if (val > 0) {
				map.put("start_time", DateFormatUtils.format(val, "yyyy-MM-dd HH:mm:ss"));
			}
			
			val = MapUtils.getLongValue(map, "end_time");
			if (val > 0) {
				map.put("end_time", DateFormatUtils.format(val, "yyyy-MM-dd HH:mm:ss"));
			}
			
			map.put("statu",Constant.status.get(MapUtils.getString(map, "trigger_state")));
		}

		return results;
	}
	
	

	private JdbcTemplate getJdbcTemplate() {
		return new JdbcTemplate(this.dataSource);
	}
}
