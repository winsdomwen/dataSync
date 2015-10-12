<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>

<!DOCTYPE html>
<html lang="en-us">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=urf-8">
<title>修改时间</title>
<link href="${pageContext.request.contextPath}/components/bootstrap-datepicker/css/datepicker.css" type="text/css" media="screen" rel="stylesheet">
</head>
<body>
	<div class="popover-content">
		<form method="post" action="/hosts/host/5/patch/" class="rended">
			<div id="div_id_guarantee_date" class="control-group">
				<label for="id_guarantee_date" class="control-label requiredField">
					Guarantee date<span class="asteriskField">*</span>
				</label>
				<div class="controls">
					<div class="input-append date bootstrap-datepicker">
						<input class="date-field admindatewidget" id="txtDate"
							name="date" size="10" type="text"> <span
							class="add-on"><i class="icon-calendar"></i> </span>
						<button id="btnNow" class="btn" type="button">今日</button>
					</div>
				</div>
			</div>
			<button type="submit" class="btn btn-success btn-block btn-small">保存</button>
		</form>
	</div>
<script type="text/javascript" src="${pageContext.request.contextPath}/components/bootstrap-datepicker/js/bootstrap-datepicker.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/datetime.js"></script>
		<script type="text/javascript">
	$(function(){
			alert($(".popover-content").length);
		});
</script>
</body>
</html>