<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>

<!DOCTYPE html>
<html lang="en-us">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=urf-8">
<title>添加任务</title>
</head>
<body>
	<ul id="tabs" class="nav nav-tabs chart-tab">
		<li class="active"><a href="#csvfile-upload" data-toggle="tab">上传CSV文件</a>
		</li>
		<li class=""><a href="#chart-user_count" data-toggle="tab">上传XML文件</a>
		</li>
		<li class=""><a href="${pageContext.request.contextPath}/jsp/config.jsp" data-toggle="tab">填写表单</a></li>
	</ul>
	<div class="tab-content">
		<div id="csvfile-upload"
			class="form-container row-fluid form-horizontal tab-pane active">
			<form id="uploadFile_form" class="exform rended"
				action="${pageContext.request.contextPath}/FileUploadServlet.action?action=uploadFile"
				method="post" enctype="multipart/form-data">
				<div class="box well fieldset" id="box-0">
					<h4 class="box-title">上传CSV文件添加任务调度</h4>
					<div class="box-content ">
						<div id="div_id_name" class="clearfix value control-group">
							<div class="control-label">CSV文件</div>
							<div class="controls">
								<input type="file" " name="fileCsv">
								<button type="button" target="__blank" class="btn btn-link"
									onclick="javascript:downloadCsv()">下载CSV模板</button>
							</div>
						</div>
						<div id="div_id_order" class="clearfix value control-group">
							<div class="control-label"></div>
							<div class="controls">
								<button type="submit" class="btn btn-success btn-small">上传</button>
							</div>
						</div>
					</div>
				</div>
			</form>
		</div>
	</div>
	<script type="text/javascript" src="${pageContext.request.contextPath}/js/tab-ajax.js"></script>
<script type="text/javascript">
	function downloadCsv(){
		location.href = "${pageContext.request.contextPath}/JobProcessServlet?action=downloadCsvTemplet";
	}
	
	$(function(){
	 	$("#tabs").ajaxTab();
	})
</script>
</body>
</html>