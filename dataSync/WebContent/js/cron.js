!function($){
	"use strict";
	
	var Cron=function(element,options){
		this.$element=$(element);
		this.options=options;
		this.description="";
	}
	
	var Unit={"h":"小时","m":"分钟","s":"秒"}
	var Week = ['星期日','星期一','星期二','星期三','星期四','星期五','星期六'];  
	
	var getWeekStr=function(days){
		var rtnVal=[];
		var arr=days.split(",")
		for(var i=0;i<arr.length;i++){
			rtnVal.push(Week[arr[i]-1]);
		}
		return rtnVal.toString();
	}
	
	Cron.prototype={
		constructor: Cron,
		/*
		 * 计划只执行一次
		 */
		generateOnce:function(){
			if(this.options.onceDateTime.length<19){
				alert("日期时间的格式不对！");
				return "";
			}
			var dt=this.options.onceDateTime.split(" ");
			var dateArr=dt[0].split("-");
			var timeArr=dt[1].split(":");
			this.description="在"+dt[0]+"的"+dt[1]+"执行";
			return timeArr[2]+' '+timeArr[1]+' '+timeArr[0]+' '+dateArr[2]+' '+dateArr[1]+' '+dateArr[0];
		},
		
		/*
		 * 频率为每天，每天频率为执行一次
		 */
		generateByDayForOnce:function(){
			//每天固定时间执行
			if (this.options.dayfrequencyTime<8){
				alert("时间格式不对！");
				return "";
			}
			var timeArr=this.options.dayfrequencyTime.split(":");
			this.description="在每"+(this.options.dayInterval=="1" ? "":this.options.dayInterval)+ "天的"
							+this.options.dayfrequencyTime+"执行。"+this.createDurationStr();
			return timeArr[2]+' '+timeArr[1]+' '+timeArr[0]+' /'+this.options.dayInterval+' * ?';
		},
		
		/*
		 * 频率为每天，每天频率为按时间间隔（秒<60，分<60，时<24）执行
		 */
		generateByDay:function(){
			//if (this.options.dayInterval>31){
			//	alert("天数间隔不能大于31.");
			//	return；
			//}
			var strCron='';
			switch(this.options.timeUnit){
			case 'h':
				strCron='0 0 /'+this.options.dayfrequencyInterval+' /'+this.options.dayInterval+' * ?';
				break;
			case 'm':
				strCron='0 /'+this.options.dayfrequencyInterval+' * /'+this.options.dayInterval+' * ?';
				break;
			case 's':
				strCron='/'+this.options.dayfrequencyInterval+' * * /'+this.options.dayInterval+' * ?';
				break;
			}
			this.description="在每"+(this.options.dayInterval=="1" ? "":this.options.dayInterval)+ "天,每"
							+this.options.dayfrequencyInterval+Unit[this.options.timeUnit]+"执行一次。"
							+this.createDurationStr();
			return strCron;
		},
		
		/*
		 * 频率为每周，每天的频率为执行一次
		 * （1~7 1=SUN 或 SUN，MON，TUE，WED，THU，FRI，SAT）
		 */
		generateByWeekForOnce:function(){
			var timeArr=this.options.dayfrequencyTime.split(":");
			this.description="在每周"+getWeekStr(this.options.weekInterval)+"的"+this.options.dayfrequencyTime+"执行。"+this.createDurationStr();
			return timeArr[2]+' '+timeArr[1]+' '+timeArr[0]+' ? * '+this.options.weekInterval;
		},
		
		/*
		 * 频率为每周，每天频率为按时间间隔（秒<60，分<60，时<24）执行
		 */
		generateByWeek:function(){
			/*
			if (this.options.dayInterval>31){
				alert("天数间隔不能大于31.");
				return；
			}*/
			var strCron='';
			switch(this.options.timeUnit){
			case 'h':
				strCron='0 0 /'+this.options.dayfrequencyInterval+' ? * '+this.options.weekInterval;
				break;
			case 'm':
				strCron='0 /'+this.options.dayfrequencyInterval+' * ? * '+this.options.weekInterval;
				break;
			case 's':
				strCron='/'+this.options.dayfrequencyInterval+' * * ? * '+this.options.weekInterval;
				break;
			}
			this.description="在每周"+getWeekStr(this.options.weekInterval)+",每"
							+this.options.dayfrequencyInterval+Unit[this.options.timeUnit]+"执行一次。"
							+this.createDurationStr();
			return strCron;
		},
		
		generateForType:function(){
			var strCron="";
			if (this.options.jobType=="1"){//执行一次
				strCron=this.generateOnce();
			}else{//重复执行
				if (this.options.frequency=="0"){//频率为每天
					if (this.options.dayfrequency=="0"){ //每天执行一次
						strCron=this.generateByDayForOnce();
					}else{//每天按时间间隔执行
						strCron=this.generateByDay();
					}
				}else{//频率为每周
					if (this.options.dayfrequency=="0"){ //频率为每周，每天的频率为执行一次
						strCron=this.generateByWeekForOnce();
					}else{//频率为每周，每天频率为按时间间隔执行
						strCron=this.generateByWeek();
					}
				}
			}
			this.$element.val(this.description);
			this.addHiddenInput(strCron);
		},
		
		createDurationStr:function(){
			return "将从"+this.options.beginTime+"开始执行计划"+(this.options.endTime=="" ? "。":",到"+this.options.endTime+"结束。");
		},
		
		addHiddenInput:function(cron){
			if ($("#hiddenCron").length==0){
				this.$element.after("<input type='hidden' id='hiddenCron' value='"+cron+"'>");
			}else{
				$("#hiddenCron").val(cron);
			}
		},
		
		test:function(){
			alert("test");
		}
		
	}
	
	/*cron插件定义
	  * ======================= */

	  var old = $.fn.cron

	  $.fn.cron = function (option) {
	    return this.each(function () {
	      var $this = $(this)
	        //, data = $this.data('cron')
	        , options = $.extend({}, $.fn.cron.defaults, $this.data(), typeof option == 'object' && option)
	        , data= new Cron(this, options)
	     // if (!data) $this.data('cron', (data = new Cron(this, options)))
	      if (typeof option == 'string') data[option]()
	      else data.generateForType()
	    })
	  }

	  $.fn.cron.defaults = {
		  jobType: "0" //0:重复执行; 1:执行一次
	    , frequency: "0" //0:每天;1:每周
	    , dayInterval: "1" //频率为每天的时间间隔
	    , weekInterval:"" //频率为每周的选项，如：周一、周二、周三：2,3,4
	    , dayfrequency:"0" //0:执行一次; 1:执行间隔
	    , timeUnit:"h" //h:小时; m:分钟;s:秒
	    , dayfrequencyTime:"" //每天频率中的执行一次的时间
	    , dayfrequencyInterval:"" //每天频率中的间隔
	    , onceDateTime:"" //任务只执行一次的日期时间
	    , beginTime:""
	    , endTime:""
	  }

	  $.fn.cron.Constructor = Cron


	 /* CRON NO CONFLICT
	  * ================= */

	  $.fn.cron.noConflict = function () {
	    $.fn.cron = old
	    return this
	  }

}(window.jQuery);