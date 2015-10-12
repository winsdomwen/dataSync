;(function($) {
	$.date_local = {
		days : [ "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六", "星期日" ],
		daysShort : [ "周日", "周一", "周二", "周三", "周四", "周五", "周六", "周日" ],
		daysMin : [ "日", "一", "二", "三", "四", "五", "六", "日" ],
		months : [ "一月", "二月", "三月", "四月", "五月", "六月", "七月", "八月", "九月", "十月",
				"十一月", "十二月" ],
		monthsShort : [ "一月", "二月", "三月", "四月", "五月", "六月", "七月", "八月", "九月",
				"十月", "十一月", "十二月" ],
		today : "今日"
	}

	$.fn.datepicker.dates['zh-CN'] = $.date_local;

	$('.input-append.date').each(function(e) {
		var $this=$(this);
		var dp = $this.datepicker({
			format : "yyyy-mm-dd",
			language : 'zh-CN',
			todayBtn : "linked",
			autoclose : true
		}).data('datepicker');
		//显示当前日期，如果设置了开始日期，就显示之
		var startDate=$this.data('date-startdate');
		if (startDate){
			dp.setDate(new Date(startDate));
		}else{
			dp.setDate(new Date());
		}

		dp.hide();
		//今日
		$("#btnNow").click(function(e) {
			// dp.setDate(new Date());
			dp.update(new Date());
			dp.hide();
		});
		//上一天
		$("#btnPre").click(function(e){
			var dd = new Date($("#txtDate").val()); 
			dd.setDate(dd.getDate()+(-1));
			dp.update(dd);
			dp.hide();
		});
		//下一天
		$("#btnNext").click(function(e){
			var dd = new Date($("#txtDate").val()); 
			dd.setDate(dd.getDate()+1);
			dp.update(dd);
			dp.hide();
		});
		
	})
	if ($.fn.timepicker) {
		$('.input-append.time').each(function(e) {
			var el = $(this).find('input');
			var value = el.val();
			var tp = el.timepicker({
				minuteStep : 1,
				showSeconds : true,
				showMeridian : false,
				defaultTime : false,
			}).data('timepicker');
			$(".input-append.time>.btn").click(function(e) {
				tp.$element.val("");
				tp.setDefaultTime('current');
				tp.update();
			})
		})
	}

})(jQuery)