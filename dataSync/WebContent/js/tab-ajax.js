/*
 让bootstrap的tab可以ajax加载页面
*/
(function ($, window, undefined) {
    $.fn.ajaxTab = function () {
        var $this = $(this);
        var tbCnt = $this.next("div.tab-content");


        if (tbCnt.length === 0) {
            $this.parent().append($("<div class='tab-content'></div>"));
            tbCnt = $this.next("div.tab-content");
        } 
        
        $this.find("li>a").each(function (idx, el) {
        	var $el = $(el);
            var href = $el.attr("href");
            //不需要ajax加载的不处理
            if (href.indexOf("#")<0){
	            var newHref = href + $this.selector + "-content-"+idx;
	            $el.attr("href", newHref);
            }
        });
        

        $this.bind("show", function (e) {
        	 var $anchor = $(e.target);
             var href = $anchor.attr("href");
             var hash = href.indexOf("#");

            if (hash!==0){
                var target = href.substr(hash);
                href = href.substr(0, hash);
                
            	$anchor.attr("href",target);
	    		tbCnt.find("div.active").removeClass("active");
	    		//判断是否已经有tab-pane，没有就添加
	        	if ($(target).length===0){
	        		tbCnt.append($("<div class='tab-pane active' id='" + target.substr(1)+ "'></div>"));	        		
	        	}
	    		$(target).addClass("active");
	    		$(target).html($("<i class='icon-spinner icon-spin'></i>"));
	    		 $.get(href, function (data, statusText, jqXHR) {
	                   $(target).html(data);
	            });    
            }
        });
    }
})(jQuery, window, undefined);