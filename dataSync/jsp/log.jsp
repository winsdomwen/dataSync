<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
	
<!DOCTYPE html>
<html lang="en-us">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=urf-8">
<title>log file</title>
<link href="${pageContext.request.contextPath}/components/bootstrap-datepicker/css/datepicker.css" type="text/css" media="screen" rel="stylesheet">
</head>
<body>
	<div class="navbar">
		<div class="navbar-inner">
			<a class="brand" href="#"><i class="icon-book"></i> 日志列表</a>
			<div class="navbar-search">
				<div class="input-append">
					<ul class="dropdown-menu pull-right">
						<li><a href="">Search By User</a>
						</li>
					</ul>
					<input id="txtSearch" type="text" name="_q_" value=""
						placeholder="Search 任务">
					<button id="cmdSearch" class="btn btn-primary" type="submit">
						<i class="icon-search icon-white"></i>
					</button>
				</div>
				<div class="input-append">
					<div class="input-append date bootstrap-datepicker">
						<input class="date-field admindatewidget" id="txtDate"
							name="date" size="10" type="text"> <span
							class="add-on"><i class="icon-calendar"></i> </span>
						<button id="btnNow" class="btn" type="button">今日</button>
						<button type="button" id="btnPre" class="btn btn-success btn-xs">&larr; </button>
						<button type="button" id="btnNext" class="btn btn-success btn-xs">&rarr;</button>
					</div>
				</div>
			</div>
			<div class="btn-group pull-right">
				<a id="cmdLog" class="btn btn-primary"><i class="icon-plus icon-white"></i>显示</a>
			</div>
		</div>
	</div>
	<div class="controls">
		<textarea class="textarea-field" cols="10"
			id="txtLog" name="description" rows="15">
		</textarea>
	</div>
<script type="text/javascript" src="${pageContext.request.contextPath}/components/bootstrap-datepicker/js/bootstrap-datepicker.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/datetime.js"></script>
<script type="text/javascript">
	$(function(){
		$("#cmdLog").click(function(){
			var date=$("#txtDate").val();
			if (date==""){
				alert("请先选择日期！")
				return;
			}
			var jobName=encodeURIComponent($("#obj_data").val());
			$.ajax({
					url : "${pageContext.request.contextPath}/JobProcessServlet?action=log&date="+date+"&job="+jobName,
					type : 'post',
					error : function(msg) {
						alert("执行失败:" + msg);
					},
					success : function(msg) {
						if (msg=="0"){
							alert("不存在该天的日志文件！")
						}else{
							$("#txtLog").val(msg);
						}	
					}
				});
		})
			
		});
</script>
</body>
</html>