
<%@page import="com.sun.xml.internal.bind.v2.schemagen.xmlschema.List"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html>
<html lang="en-us">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=urf-8">
<title>调度任务列表</title>
<link href="${pageContext.request.contextPath}/components/bootstrap/bootstrap.css" type="text/css" media="screen" rel="stylesheet">
<link href="${pageContext.request.contextPath}/components/bootstrap/bootstrap-responsive.css" type="text/css" media="screen" rel="stylesheet">
<link href="${pageContext.request.contextPath}/components/bootstrap/bootstrap-my.css" type="text/css" media="screen" rel="stylesheet">
<link href="${pageContext.request.contextPath}/components/font-awesome/css/font-awesome.css" type="text/css" media="screen" rel="stylesheet">
<link href="${pageContext.request.contextPath}/styles/main.css" type="text/css" media="screen" rel="stylesheet">
<link href="${pageContext.request.contextPath}/styles/modal.css" type="text/css" media="screen" rel="stylesheet">
<link href="${pageContext.request.contextPath}/styles/form.css" type="text/css" media="screen" rel="stylesheet">
<link href="${pageContext.request.contextPath}/styles/editable.css" type="text/css" media="screen" rel="stylesheet">
<style type="text/css">
.btn-toolbar {
	margin-top: 0;
}

#content-block.full-content {
	margin-left: 0;
}
</style>

</head>
<body class="change-list" style="padding-top: 59px;">
	<!-- Header -->
	<div id="top-nav" class="navbar navbar-inverse navbar-fixed-top"
		style="position: fixed;">
		<div class="navbar-inner">
			<div class="container-fluid">
				<a class="brand" href="#">GCI数据交换同步</a> <a class="btn btn-navbar"
					data-toggle="collapse" data-target=".nav-menu.nav-collapse"> <span
					class="icon-list"></span> </a> <a class="btn btn-navbar"
					data-toggle="collapse" data-target=".search-menu.nav-collapse">
					<span class="icon-search"></span> </a> <a class="btn btn-navbar"
					data-toggle="collapse" data-target=".add-menu.nav-collapse"> <span
					class="icon-plus"></span> </a>
				<div class="nav-menu nav-collapse collapse">
					<ul class="nav pull-right">
						<li><a> <strong>Welcome,admin</strong> <b class="caret"></b>
						</a></li>
					</ul>
				</div>

				<div class="add-menu nav-collapse collapse">
					<ul class="nav pull-right">
						<li class="dropdown g-add"><a class="dropdown-toggle"
							role="button" data-toggle="dropdown" href="#"> <i
								class="icon-plus icon-white"></i> 新增 <b class="caret"></b> </a>
							<ul id="g-add-menu" class="dropdown-menu" role="menu">
								<li><a href="/hosts/accessrecord/add/"><i
										class="icon-plus"></i> 新增任务调度</a></li>
								<li><a href="/xadmin/bookmark/add/"><i
										class="icon-plus"></i> Add Bookmark</a></li>
							</ul>
						</li>
					</ul>
				</div>
			</div>
		</div>
	</div>
	<div id="body-content" class="container-fluid">
		<div class="row-fluid">
			<div id="content-block" class="span12 full-content">
				<div class="navbar">
					<div class="navbar-inner">
						<a class="brand" href="#"><i class="icon-book"></i> 任务列表</a>
						<div class="navbar-search">
							<div class="input-append">
								<ul class="dropdown-menu pull-right">
									<li><a href="">Search By User</a>
									</li>
								</ul>
								<input id="txtSearch" type="text" name="_q_" value="" placeholder="Search 任务">
								<button id="cmdSearch" class="btn btn-primary" type="submit">
									<i class="icon-search icon-white"></i>
								</button>
							</div>
						</div>
						<div class="btn-group pull-right">
							<a data-res-uri="${pageContext.request.contextPath}/jsp/add.jsp"
								data-submit_uri="" title="添加任务调度"
								class="btn btn-primary details-handler"><i
								class="icon-plus icon-white"></i> 添加调度任务</a>
						</div>
					</div>
				</div>

				<div class="btn-toolbar pull-right">
					<a id="cmdBatchDel" class="btn btn-small"><i class="icon-trash"></i>
						删除 </a>
					<div class="btn-group export">
						<a class="dropdown-toggle btn btn-small" data-toggle="dropdown"
							href="#"> <i class="icon-share"></i> 导出 <b class="caret"></b>
						</a>
						<ul class="dropdown-menu" role="menu" aria-labelledby="dLabel">
							<li><a><i class="icon-circle-arrow-down"></i> 导出 CSV</a></li>

							<li><a><i class="icon-circle-arrow-down"></i> 导出 XML</a></li>
						</ul>
					</div>
					<div class="btn-group layout-btns" data-toggle="buttons-radio">
						<button type="button" class="btn btn-small layout-normal">
							<i class="icon-th-large"></i>
						</button>
						<button type="button"
							class="btn btn-small layout-condensed active">
							<i class="icon-th"></i>
						</button>

					</div>

				</div>
				<div class="pagination pagination-small pagination-left">

					<ul>
						<li><span><b id="txtSum">${num}</b> 条任务调度</span>
						</li>
					</ul>
				</div>

				<div class="results">
					<table
						class="table table-bordered table-striped table-hover table-condensed">
						<thead>
							<tr>
								<th scope="col" class="action-checkbox-column"><input
									type="checkbox" id="action-toggle"></th>
								<th scope="col">
									<div>
										<a>任务名称</a>
									</div></th>
								<th scope="col"><div>
										<a>任务分组</a>
									</div>
								</th>
								<th scope="col"><div>
										<a>下次执行时间</a>
									</div>
								</th>
								<th scope="col"><div>
										<a>上次执行时间</a>
									</div>
								</th>
								<th scope="col"><div>
										<a>优先级</a>
									</div>
								</th>
								<th scope="col"><div>
										<a>任务状态</a>
									</div>
								</th>
								<th scope="col"><div>
										<a>任务类型</a>
									</div>
								</th>
								<th scope="col"><div>
										<a>开始时间</a>
									</div>
								</th>
								<th scope="col"><div>
										<a>结束时间</a>
									</div>
								</th>
								<th scope="col"><div>
										<a>动作命令</a>
									</div>
								</th>
							</tr>
						</thead>
						<tbody>
							<c:forEach var="map" items="${list}" varStatus="stat">
								<tr class="row${stat.count%2==0 ? 2:1}">
									<td class="action-checkbox"><input class="action-select"
										name="_selected_action" type="checkbox"
										value="${map.trigger_name}">
									</td>
									<td>${map.display_name}</td>
									<td>${map.trigger_group}</td>
									<td><div class="btn-group pull-right">
											<a class="editable-handler" title=""
												data-editable-field="guarantee_date"
												data-editable-loadurl="${pageContext.request.contextPath}/jsp/edit.jsp"
												data-original-title="Enter guarantee date"><i
												class="icon-edit"></i> </a>
										</div> ${map.next_fire_time}</td>
									<td>${map.prev_fire_time}</td>
									<td>${map.priority*-1}</td>
									<td><c:if test='${map.trigger_state=="WAITING"}'>
											<span class="label label-success">${map.statu}</span>
										</c:if> <c:if test='${map.trigger_state=="PAUSED"}'>
											<span class="label label-warning">${map.statu}</span>
										</c:if> <c:if test='${map.trigger_state=="ACQUIRED"}'>
											<span class="label label-danger">${map.statu}</span>
										</c:if></td>
									<td>${map.trigger_type}</td>
									<td>${map.start_time}</td>
									<td>${map.end_time}</td>
									<td>
										<button type="button" class="btn btn-warning"
											onclick="doCmd('execute','${map.trigger_name}','${map.trigger_group}','${map.trigger_state}')">立即执行</button>
										<button type="button" class="btn btn-primary"
											onclick="doCmd('pause','${map.trigger_name}','${map.trigger_group}','${map.trigger_state}')">暂停</button>
										<button type="button" class="btn btn-info"
											onclick="doCmd('resume','${map.trigger_name}','${map.trigger_group}','${map.trigger_state}')">恢复</button>
										<button type="button" class="btn btn-danger"
											onclick="doCmd('delete','${map.trigger_name}','${map.trigger_group}','${map.trigger_state}')">删除</button>
										<a data-res-uri="${pageContext.request.contextPath}/jsp/log.jsp"
												data-obj_data="${map.trigger_name}"
												data-width="1224"
												data-submit_uri="" title="日志监控"
												class="btn btn-success details-handler"><i
												class="icon-plus icon-white"></i> 日志</a>
										<button type="button" target="__blank" class="btn btn-link" onclick="javascript:encodeLink('${map.trigger_name}')">下载</button>
									</td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
				</div>

			</div>
		</div>
		<hr>
		<footer>
			<p>© GCI 2013</p>
		</footer>
	</div>
	<script type="text/javascript" src="${pageContext.request.contextPath}/components/jquery/jquery-1.8.3.min.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/components/bootstrap/bootstrap.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/js/list.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/js/modal.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/js/editable.js"></script>
		<script type="text/javascript">
	$(function() {
	
		$("#cmdSearch").click(function(){
			var content=$.trim($("#txtSearch").val());
			var $tr=$(".table tbody tr");
			var num=0;
			$tr.each(function(){
				var txt=$(this).find("td:eq(1)").text();
				if (txt.indexOf(content)>-1){
					$(this).show();
					num+=1;
				}else{
					$(this).hide();
				}
			});
			$("#txtSum").text(num);
		})
	
		
		$("#cmdBatchDel").click(function() {
			var $checked=$(".table tbody tr input.action-select:checked");
	    	if ($checked.length==0){
	    		alert("请先选择要删除的任务!");
	    		return;
	    	}
			if (!confirm("您确认要删除吗？"))
				return;
			
	    	var arr=[]
	    	$checked.each(function(){
	    		var obj={};
	    		obj.name=$(this).val();
	    		obj.group=$(this).parent().parent().find("td:eq(2)").text();
	    		arr.push(obj);
	    	})
	    	var param= encodeURIComponent(encodeURIComponent(JSON.stringify(arr)));
							$.ajax({
										url : "${pageContext.request.contextPath}/JobProcessServlet?action=batchDelete&param="
												+ param,
										type : 'post',
										//dataType: 'xml',
										// timeout: 3000,
										error : function(msg) {
											alert("执行失败:" + msg);
										},
										success : function(xml) {
											if (xml == 0) {
												alert("执行成功！");
												window.location.reload();
											} else {
												alert("执行失败！");
											}
										}
									});
						})
	});
	
	
	function encodeLink(triggerName) {
		//客户端两次编码，服务端再解码，否测中文乱码 
		triggerName = encodeURIComponent(encodeURIComponent(triggerName));
		location.href = "${pageContext.request.contextPath}/JobProcessServlet?action=downloadCsv&triggerName="
				+ triggerName
	}

	function doCmd(state, triggerName, group, triggerState) {
		if (state == 'delete' && !confirm("您确认要删除吗？"))
			return;

		if (state == 'pause' && triggerState == 'PAUSED') {
			alert("该任务己经暂停！");
			return;
		}

		if (state == 'resume' && triggerState != 'PAUSED') {
			alert("该任务正在运行中！");
			return;
		}

		//客户端两次编码，服务端再解码，否测中文乱码 
		triggerName = encodeURIComponent(encodeURIComponent(triggerName));
		group = encodeURIComponent(encodeURIComponent(group));
		$.ajax({
					url : '${pageContext.request.contextPath}/JobProcessServlet?action='
							+ state
							+ '&triggerName='
							+ triggerName
							+ '&group='
							+ group,
					type : 'post',
					//dataType: 'xml',
					// timeout: 3000,
					error : function() {
						alert("执行失败！");
					},
					success : function(xml) {
						if (xml == 0) {
							alert("执行成功！");
							window.location.reload();
						} else {
							alert("执行失败！");
						}
					}
				});
	}
</script>
</body>
</html>
