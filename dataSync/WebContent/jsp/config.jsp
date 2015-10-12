<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>

<!DOCTYPE html>
<html lang="en-us">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=urf-8">
<title>添加任务</title>
<link href="${pageContext.request.contextPath}/components/bootstrap-datepicker/css/datepicker.css" type="text/css" media="screen" rel="stylesheet">
<link href="${pageContext.request.contextPath}/components/bootstrap-timepicker/css/bootstrap-timepicker.css" type="text/css" media="screen" rel="stylesheet">
</head>
<body>
	<div id="wizard">
		<ul class="steps-nav nav nav-pills">
			<li class="active"><a> 1. 任务信息 <i class="icon-caret-right"></i></a></li>
			<li><a> <span class="muted"> 2. 来源数据库 </span> </a></li>
			<li><a> <span class="muted"> 3. 目标数据库 </span> </a></li>
			<li><a> <span class="muted"> 4. 同步内容设置 </span> </a></li>
			<li><a> <span class="muted"> 5. 计划调度设置 </span> </a></li>
			<li><a> <span class="muted"> 6. 发送通知 </span> </a></li>
			<li><a> <span class="muted"> 7. 其它设置 </span> </a></li>
		</ul>

		<div class="form-container row-fluid form-horizontal" id="step-0">
			<div class="box well fieldset unsort no_title" id="box-0">
				<div class="box-content ">
					<div id="div_id_step_0-job_name" class="control-group">
						<label for="job_name" class="control-label"> 任务名称</label>
						<div class="controls">
							<input class="text-field" id="job_name"
								maxlength="256" name="job_name" type="text" check-type="required" required-message="请填写任务名称。">
						</div>
					</div>
					<div id="div_id_step_0-description" class="control-group">
						<label for="description" class="control-label">说明</label>
						<div class="controls">
							<textarea class="textarea-field " cols="40"
								id="description" name="description" rows="10"></textarea>
						</div>
					</div>
					<div id="div_id__step_0-is_activate" class="control-group">
						<div class="controls">
							<label for="is_activate" class="checkbox "> 
							<input class="checkboxinput" id="is_activate" name="is_activate"
								type="checkbox" checked="checked"> 激活任务 </label>
						</div>
					</div>
				</div>
			</div>
		</div>
		
		<div class="form-container row-fluid form-horizontal" id="step-1">
			<div class="box well fieldset unsort no_title" id="box-1">
				<div class="box-content ">
					<div id="div_id_step_1-sourceDbType" class="control-group">
						<label for="sourceDbType" class="control-label">数据库类型 </label>
						<div id="sourceDbType" class="controls">
							<select class="form-control" check-type="required" required-message="请选择数据库类型。">
							  <option>---------</option>
							  <option>Oracle</option>
							  <option>Mysql</option>
							  <option>Sql Server</option>
							  <option>Redis</option>
							</select>
						</div>
					</div>
					<div id="div_id_step_1-sourceIP" class="control-group">
						<label for="sourceIP" class="control-label ">
							主机名或IP地址</label>
						<div class="controls">
							<input class="text-field " id="sourceIP"
								maxlength="32" name="sourceIP" type="text" check-type="required" required-message="请填写主机名或IP地址。">
						</div>
					</div>
					<div id="div_id_step_1-sourcePort" class="control-group">
						<label for="sourcePort" class="control-label ">
							端口 </label>
						<div class="controls">
							<input class="text-field " id="sourcePort"
								maxlength="32" name="sourcePort" type="text" check-type="required number" required-message="请填写端口。">
						</div>
					</div>
					<div id="div_id_step_1-sourceDbName" class="control-group">
						<label for="sourceDbName" class="control-label ">
							数据库名称 </label>
						<div class="controls">
							<input class="text-field " id="sourceDbName"
								maxlength="128" name="sourceDbName" type="text" check-type="required" required-message="请填写数据库名称。">
						</div>
					</div>
					<div id="div_id_step_1-sourceUserName" class="control-group">
						<label for="sourceUserName" class="control-label ">
							用户名 </label>
						<div class="controls">
							<input class="text-field " id="sourceUserName" 
							maxlength="128" name="sourceUserName" type="text" check-type="required" required-message="请填写用户名。">
						</div>
					</div>
					<div id="div_id_step_1-sourcePassword" class="control-group">
						<label for="sourcePassword" class="control-label ">
							密码 </label>
						<div class="controls">
							<input class="text-field " id="sourcePassword" 
							maxlength="128" name="sourcePassword" type="text" check-type="required" required-message="请填写密码。">
						</div>
					</div>
				</div>
			</div>
		</div>
		<div class="form-container row-fluid form-horizontal" id="step-2">
			<div class="box well fieldset unsort no_title" id="box-2">
				<div class="box-content ">
					<div id="div_id_step_1-targetDbType" class="control-group">
						<label for="targetDbType" class="control-label ">
							数据库类型 </label>
						<div class="controls">
							<select id="targetDbType" class="form-control" check-type="required" required-message="请选择数据库类型。">
							  <option>---------</option>
							  <option>Oracle</option>
							  <option>Mysql</option>
							  <option>Sql Server</option>
							  <option>Redis</option>
							</select>
						</div>
					</div>
					<div id="div_id_step_2-targetIP" class="control-group">
						<label for="targetIP" class="control-label ">
							主机名或IP地址</label>
						<div class="controls">
							<input class="text-field " id="targetIP"
								maxlength="32" name="targetIP" type="text" check-type="required" required-message="请填写主机名或IP地址。">
						</div>
					</div>
					<div id="div_id_step_2-targetPort" class="control-group">
						<label for="targetPort" class="control-label ">
							端口 </label>
						<div class="controls">
							<input class="text-field " id="targetPort"
								maxlength="32" name="targetPort" type="text" check-type="required number" required-message="请填写端口。">
						</div>
					</div>
					<div id="div_id_step_2-targetDbName" class="control-group">
						<label for="targetDbName" class="control-label ">
							数据库名称 </label>
						<div class="controls">
							<input class="text-field " id="targetDbName"
								maxlength="128" name="targetDbName" type="text" check-type="required" required-message="请填写数据库名称。">
						</div>
					</div>
					<div id="div_id_step_2-targetUserName" class="control-group">
						<label for="targetUserName" class="control-label ">
							用户名 </label>
						<div class="controls">
							<input class="text-field "
								id="targetUserName" maxlength="128" name="targetUserName"
								type="text"  check-type="required" required-message="请填写用户名。">
						</div>
					</div>
					<div id="div_id_step_2-targetPassword" class="control-group">
						<label for="targetPassword" class="control-label ">
							密码 </label>
						<div class="controls">
							<input class="text-field "
								id="targetPassword" maxlength="128" name="targetPassword"
								type="text" check-type="required" required-message="请填写密码。">
						</div>
					</div>
				</div>
			</div>
		</div>
		<div class="form-container row-fluid form-horizontal" id="step-3">
			<div class="box well fieldset unsort no_title" id="box-3">
				<div class="box-content ">
					<div id="div_id_step_3-sourcedb_select_sql" class="control-group">
						<label for="sourcedb_select_sql"
							class="control-label"> 从数据源获取数据的sql</label>
						<div class="controls">
							<textarea class="textarea-field " cols="40"
								id="sourcedb_select_sql" name="sourcedb_select_sql" rows="5"  check-type="required" required-message="请填写sql语句。"></textarea>
						</div>
					</div>
					<div id="div_id_step_3-targetdb_insert_sql" class="control-group">
						<label for="targetdb_insert_sql"
							class="control-label "> 插入数据到目标数据库的sql</label>
						<div class="controls">
							<textarea class="textarea-field " cols="40"
								id="targetdb_insert_sql" name="targetdb_insert_sql" rows="5"></textarea>
						</div>
					</div>
					<div id="div_id_step_3-targetdb_before_sql" class="control-group">
						<label for="targetdb_before_sql"
							class="control-label "> 同步前执行的sql或存储过程</label>
						<div class="controls">
							<textarea class="textarea-field " cols="40"
								id="targetdb_before_sql" name="targetdb_before_sql" rows="5"></textarea>
						</div>
					</div>
					<div id="div_id_step_3-targetdb_after_sql" class="control-group">
						<label for="targetdb_after_sql"
							class="control-label"> 同步后执行的sql或存储过程</label>
						<div class="controls">
							<textarea class="textarea-field" cols="40"
								id="targetdb_after_sql" name="targetdb_after_sql" rows="5"></textarea>
						</div>
					</div>
				</div>
			</div>
		</div>
		
		<div class="form-container row-fluid form-horizontal" id="step-4">
			<div class="box well fieldset">
				<div class="box-content">					
					<div id="div_id_step_4-job_name" class="control-group">
						<label for="job_type" class="control-label">计划类型</label>
						<div class="controls">
							<select id="job_type" class="form-control">
							  <option value="0">重复执行</option>
							  <option value="1">只执行一次</option>
							</select>
						</div>
					</div>
				</div>
			</div>

			<div id="div_frequency" class="box well fieldset">
				<h4 class="box-title"><i class="icon chevron icon-chevron-up"></i>频率 </h4>
				<div class="box-content">					
					<div id="div_id_step_4-frequency" class="control-group">
						<label for="frequency" class="control-label">执行</label>
						<div class="controls">
							<select id="frequency" class="form-control">
							  <option value="0">每天</option>
							  <option value="1">每周</option>
							</select>
						</div>
					</div>
					<div  id="div_week" style="display:none" class="control-group">
						<label for="interval" class="control-label">执行间隔<span class="asteriskField">*</span></label>
						<!--  
						<div class="controls">
							<div class="input-append" style="width:90px;">
							  <input style="width:50px;" id="appendedInput" type="text" value="1">
							  <span class="add-on">周</span>
							</div>
							<span class="help-inline">,在</span>
						</div>
						-->

						<div class="controls">
							<div class="multiChk">
								<label class="checkbox inline">
								  <input type="checkbox" value="2">星期一
								</label>
								<label class="checkbox inline">
								  <input type="checkbox" value="3">星期二
								</label>
								<label class="checkbox inline">
								  <input type="checkbox" value="4">星期三
								</label>
								<label class="checkbox inline">
								  <input type="checkbox" value="5">星期四
								</label>
								<label class="checkbox inline">
								  <input type="checkbox" value="6">星期五
								</label>
								<label class="checkbox inline">
								  <input type="checkbox" value="7">星期六
								</label>
								<label class="checkbox inline">
								  <input type="checkbox" value="1">星期日
								</label>
							</div>
						</div>
					</div>
					<div id="div_day" class="control-group">
						<label for="day_interval" class="control-label">执行间隔</label>
						<div class="controls">
							<div class="input-append">
							  <input class="span4" id="day_interval" type="text" value="1" title="1<=执行间隔<=31" check-type="required number" required-message="请填写执行间隔。">
							  <span class="add-on">天</span>
							</div>
						</div>
					</div>
				</div>
			</div>
			
			<div id="div_dayfrequency" class="box well fieldset">
				<h4 class="box-title"><i class="icon chevron icon-chevron-up"></i>每天频率 </h4>
				<div class="box-content">					
					<div class="control-group">
						<label for="frequency" class="control-label">
							<input type="radio" name="excuteRadios" value="0" checked> 执行一次，时间为<span class="asteriskField">*</span></label>
						<div class="controls">
								<div class="input-append time bootstrap-timepicker">
										<input class="time-field admintimewidget"
											id="dayfrequency_time" name="id_time_once" size="8"
											type="text"  value="00:00:00"> <span class="add-on"><i
											class="icon-time"></i>
										</span>
								</div> 
						</div>
					</div>
					<div class="control-group">
						<label for="interval" class="control-label">
							<input type="radio" name="excuteRadios" value="1"> 执行间隔<span class="asteriskField">*</span></label>
						<div id="div_controls_interval" class="controls">
							<div class="input-append">
							  <input class="span4" id="dayfrequency_interval" title="小时:<24; 分钟：<60;秒：<60" type="text">
							  <div class="btn-group">
							    <button id="btn_interval" class="btn dropdown-toggle" data-toggle="dropdown">
							      <span id="timeUnit">小时</span>
							       <span class="caret"></span>
							    </button>
							    <ul id="ul_interval" class="dropdown-menu">
				                  <li attr="h"><a>小时</a></li>
				                  <li attr="m"><a>分钟</a></li>
				                  <li attr="s"><a>秒</a></li>
				                </ul>
							  </div>
							</div>
							<!--  
							<div class="input-append">
								<label> 开始时间
									<div class="input-append time bootstrap-timepicker">
										<input class="time-field admintimewidget"
											id="id_datetime_widget_1" name="datetime_widget_1" size="8"
											type="text" value="00:00:00"> <span class="add-on"><i
											class="icon-time"></i>
										</span>
									</div> 
								</label> 
								<label> 结束时间
									<div class="input-append time bootstrap-timepicker">
										<input class="time-field admintimewidget"
											id="id_datetime_widget_1" name="datetime_widget_1" size="8"
											type="text" value="23:59:59"> <span class="add-on"><i
											class="icon-time"></i>
										</span>
									</div> 
								</label>
							</div>
							-->
						</div>
					</div>
				</div>
			</div>

			<div id="div_duration" class="box well fieldset">
				<h4 class="box-title">
					<i class="icon chevron icon-chevron-up"></i>持续时间
				</h4>
				<div class="box-content">
					<div class="form-row num2">
						<div class="control-group">
							<label for="beginDate" class="control-label">开始日期</label>
							<div class="controls">
								<div class="input-append date bootstrap-datepicker">
									<input class="date-field" id="beginDate"
										name="beginDate" size="10" type="text"> <span
										class="add-on"><i class="icon-calendar"></i> </span>
								</div>
							</div>
						</div>
						<div class="control-group">
							<label for="endDate" class="control-label">
								<input type="radio" id="radEndDate" name="dateRadios" value="0"> 结束日期</label>
							<div class="controls">
								<div id="div-endDate" class="input-append date bootstrap-datepicker">
									<input class="date-field" id="endDate" 
										name="endDate" size="10" type="text"  disabled> <span
										class="add-on"><i class="icon-calendar"></i> </span>
								</div>
							</div>
						</div>
					</div>
					<div class="form-row num2">
						<div class="control-group">
							<div class="controls">
							</div>
						</div>
						<div class="control-group">

							<label for="endDate" class="control-label">
								<input type="radio" name="dateRadios" value="1" checked> 无结束日期
							</label>
							<div class="controls">
								
							</div>
						</div>
					</div>
					<div class="control-group">
						
					</div>
				</div>
			</div>
			
			<div id="div_once" style="display:none" id="div_summary" class="box well fieldset">
				<h4 class="box-title"><i class="icon chevron icon-chevron-up"></i>执行一次</h4>
				<div class="box-content">					
					<div class="control-group">
						<label for="once_date" class="control-label">时间</label>
						<div class="controls">
							<div class="datetime">
								<div class="input-append date bootstrap-datepicker">
									<input class="date-field admindatewidget"
										id="once_date" name="once_date" size="10"
										type="text"> <span class="add-on"><i
										class="icon-calendar"></i>
									</span>
								</div>
								-
								<div class="input-append time bootstrap-timepicker">
									<input class="time-field admintimewidget"
										id="once_time" name="once_time" size="8"
										type="text" value="00:00:00"> <span class="add-on"><i
										class="icon-time"></i>
									</span>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
			
			
			<div id="div_summary" class="box well fieldset">
				<h4 class="box-title"><i class="icon chevron icon-chevron-up"></i>摘要</h4>
				<div class="box-content">					
					<div id="div_id_step_4-summary" class="control-group">
						<label for="summary" class="control-label">说明</label>
						<div class="controls">
							<textarea class="textarea-field" cols="40"
								id="summary" name="summary" rows="5" disabled></textarea>
						</div>
					</div>
				</div>
			</div>
			

		</div>

		<div class="form-actions">
			<button id="wizard_goto_step" name="wizard_goto_step"
				class="btn previous disabled" type="submit" value="-1">←
				上一步</button>
			<button name="_save" type="submit" class="btn btn-primary next">下一步 →</button>
		</div>
		
	</div>
	<script type="text/javascript" src="${pageContext.request.contextPath}/js/wizard.js"></script>
	<!--<script type="text/javascript" src="${pageContext.request.contextPath}/js/form.js"></script> -->
	<script type="text/javascript" src="${pageContext.request.contextPath}/components/bootstrap-validation.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/components/bootstrap-datepicker/js/bootstrap-datepicker.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/components/bootstrap-timepicker/js/bootstrap-timepicker.js"></script>

	<script type="text/javascript" src="${pageContext.request.contextPath}/js/cron.js"></script>
<script type="text/javascript">
	$('#wizard').bootstrapWizard({
		onNext : function(tab, navigation, index) {
			 //2.最后要调用 valid()方法。
			if ($(".box:visible").valid() == false) {
				return false;
			}
			if (index==4){
				cron();
			}
		}
	});
	
	function cron(){
		var weekInterval=[];
				$("#div_week").find(".controls input:checked").each(function(){
					weekInterval.push($(this).val());
				})
				
		$("#summary").cron({
			jobType: $("#job_type").val() //0:重复执行; 1:执行一次
		    , frequency: $("#frequency").val() //0:每天;1:每周
		    , dayInterval: $("#day_interval").val() //频率为每天的时间间隔
		    , weekInterval:weekInterval.toString() //频率为每周的选项，如：周一、周二、周三：2,3,4
		    , dayfrequency:$('input[name="excuteRadios"]:checked').val() //0:执行一次; 1:执行间隔
		    , timeUnit:$("#timeUnit").attr("attr") //h:小时; m:分钟;s:秒
		    , dayfrequencyTime:$("#dayfrequency_time").val() //每天频率中的执行一次的时间
		    , dayfrequencyInterval:$("#dayfrequency_interval").val() //每天频率中的间隔
		    , onceDateTime:$("#once_date").val()+" "+$("#once_time").val() //任务只执行一次的日期时间
		    , beginTime:$("#beginDate").val()
   			, endTime:$('input[name="dateRadios"]:checked').val()=="1" ? "":$("#endDate").val()
		})
		//$("#summary").cron("test");
	}
	
	
	$(function() {
		//当前日期加一天
		var d = new Date(new Date().valueOf() + 1*24*60*60*1000)
		$("#div-endDate").attr("data-date-startdate",d.getFullYear()+"-"+(d.getMonth() + 1)+"-"+d.getDate());
		$(".form-horizontal").validation();
		$("#job_type").on("change",function(){
				$("#div_frequency").toggle();
				$("#div_dayfrequency").toggle();
				$("#div_duration").toggle();
				//$("#div_summary").toggle();
				$("#div_once").toggle();
				cron();
		})
		
		//触发生成cron	
		$("div.multiChk").find("input[type='checkbox']").on('change',function(){
			cron();
		})
		
		
		$("#dayfrequency_interval").on("blur",function(){
			cron();
		})
		
		$("#beginDate,#endDate,#once_date,#once_time,#dayfrequency_time").on("change",function(){
			cron();
		})
		
		$('#div_duration input[name="dateRadios"]').on("change",function(){
				cron();
		})
		//------
		
		
		$("#frequency").on("change",function(){
			var $divWeek=$("#div_week");
			$divWeek.toggle();
			$("#div_day").toggle();
			if ($(this).val()=="1"){
				$divWeek.find(".multiChk").attr("check-type","required").attr("required-message","请选择星期。")
			}else{
				$divWeek.find(".multiChk").removeAttr("check-type").removeAttr("required-message");
				$divWeek.find(".help-block").remove();
				$divWeek.removeClass("error");
				$divWeek.removeClass("success");		
			}
			cron();
		})
		
		$("#div_controls_interval").find("input,button").each(function(){
			$(this).attr("disabled","disabled");
		})
		
		//每天频率
		$("#div_dayfrequency :radio").on("change",function(){
			var $this=$(this);
			var radioVal=$(this).val();
			var $boxContent=$this.parent().parent().parent();
			var $controlGroups=$boxContent.find(">.control-group");		
			var $controlGroup1=$controlGroups.eq(0);
			var $controlGroup2=$controlGroups.eq(1);
			
			$controlGroup1.find(">.controls").find("input,button").each(function(){
				$(this).attr("disabled",!(radioVal==0));
			})
			
			$controlGroup2.find(">.controls").find("input,button").each(function(){
				$(this).attr("disabled",!(radioVal==1));
			})

			if (radioVal=="1"){
				$("#dayfrequency_interval").attr("check-type","required number").attr("required-message","请填写执行间隔。")
			}else{
				$("#dayfrequency_interval").removeAttr("check-type").removeAttr("required-message");
			}
			$controlGroup2.find(".help-block").remove();
			$controlGroup2.removeClass("error");
			$controlGroup2.removeClass("success");
		});
		
		//持续时间
		$("#div_duration :radio").on("change",function(){
			$("#endDate").attr("disabled",!($(this).val()==0));
		})
		
		//执行间隔选择
		$("#ul_interval li").on("click",function(){
			var txt=$(this).text();
			$("#timeUnit").text(txt);
			$("#timeUnit").attr("attr",$(this).attr("attr"));
		})
		
	})
	/*
		one: 45 3 3 * * ?
		H: 0 0 /5 * * ?     --<24
		M: 0 /7 * * * ?    --<60
		S: /30 * * * * ?  --<60
		D:  <=31
		W:0 0 12 ? * 2,3,4 * 
	*/
</script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/js/datetime.js"></script>
</body>
</html>