/*!
 * bootstrap wizard plugin
 * version 1.0
 * Authors: hgq
 */
;(function($) {
var bootstrapWizardCreate = function(element, options) {
	var element = $(element);
	var obj = this;
	
	var $settings = $.extend({}, $.fn.bootstrapWizard.defaults, options);
	var $activeStep = null;
	var $navigation = null;
	var $boxContent = null;
	
	this.rebindClick = function(selector, fn)
	{
		selector.unbind('click', fn).bind('click', fn);
	}

	this.fixNavigationButtons = function() {
		// 获取当前active step
		if(!$activeStep.length) {
			//选择第一个步骤
			$navigation.find('li:first').addClass("active");
			$activeStep = $navigation.find('li:first');
		}
		obj.showContent(0);

		$($settings.previousSelector, element).toggleClass('disabled', (obj.firstIndex() >= obj.currentIndex()));
		//$($settings.nextSelector, element).toggleClass('disabled', (obj.currentIndex() >= obj.navigationLength()));

		obj.rebindClick($($settings.nextSelector, element), obj.next);
		obj.rebindClick($($settings.previousSelector, element), obj.previous);
		obj.rebindClick($($settings.lastSelector, element), obj.last);
		obj.rebindClick($($settings.firstSelector, element), obj.first);

		if($settings.onStepShow && typeof $settings.onStepShow === 'function' && $settings.onStepShow($activeStep, $navigation, obj.currentIndex())===false){
			return false;
		}
	};

	this.next = function(e) {
		if(element.hasClass('last')) {4
			return false;
		}

		if($settings.onNext && typeof $settings.onNext === 'function' && $settings.onNext($activeStep, $navigation, obj.nextIndex())===false){
			return false;
		}

		var nextIndex = obj.nextIndex();
		var currentIndex=obj.currentIndex()
		if(nextIndex > obj.navigationLength()) {
		} else {
			obj.activateStep(nextIndex,currentIndex);
			
			obj.hideContent(currentIndex);
			obj.showContent(nextIndex);
			$($settings.previousSelector, element).toggleClass('disabled', (obj.firstIndex() >= obj.currentIndex()));
			if (obj.currentIndex() >= obj.navigationLength()){
				$($settings.nextSelector, element).text($settings.nextButtonLastText);
			}else{
				$($settings.nextSelector, element).text($settings.nextButtonText);
			}
			
		}
	};

	this.previous = function(e) {

		if(element.hasClass('first')) {
			return false;
		}

		if($settings.onPrevious && typeof $settings.onPrevious === 'function' && $settings.onPrevious($activeStep, $navigation, obj.previousIndex())===false){
			return false;
		}

		var previousIndex = obj.previousIndex();
		var currentIndex=obj.currentIndex()
		if(previousIndex < 0) {
		} else {
			obj.activateStep(previousIndex,currentIndex);
			
			obj.hideContent(currentIndex);
			obj.showContent(previousIndex);
			
			$($settings.previousSelector, element).toggleClass('disabled', (obj.firstIndex() >= obj.currentIndex()));
			if (obj.currentIndex()+1 >= obj.navigationLength()){
				$($settings.nextSelector, element).text($settings.nextButtonText);
			}
		}
	};

	this.first = function(e) {
		if($settings.onFirst && typeof $settings.onFirst === 'function' && $settings.onFirst($activeStep, $navigation, obj.firstIndex())===false){
			return false;
		}

		if(element.hasClass('disabled')) {
			return false;
		}

	};
	this.last = function(e) {
		if($settings.onLast && typeof $settings.onLast === 'function' && $settings.onLast($activeStep, $navigation, obj.lastIndex())===false){
			return false;
		}

		if(element.hasClass('disabled')) {
			return false;
		}
	};
	
	/**
	 * 显示对应步骤下的内容
	 */
	this.showContent=function(index){
		$boxContent.eq(index).show();
	};
	
	/**
	 * 隐藏对应步骤下的内容
	 */
	this.hideContent=function(index){
		$boxContent.eq(index).hide();
	};
	
	this.activateStep=function(nextIndex,currentIndex){
		var $li=$navigation.find('li:eq('+nextIndex+')');
		$li.addClass("active");
		var $a=$li.find("a");
		var $span=$a.find("span");
		if ($span.length>0){
			$a.html($a.find("span").text());
			$a.append($("<i class='icon-caret-right'></i>"));
		}	
		
		$navigation.find('li:eq('+currentIndex+')').removeClass();
		$activeStep=$li;
	};
	
	this.currentIndex = function() {
		return $navigation.find('li').index($activeStep);
	};
	this.firstIndex = function() {
		return 0;
	};
	this.lastIndex = function() {
		return obj.navigationLength();
	};
	this.getIndex = function(e) {
		return $navigation.find('li').index(e);
	};
	this.nextIndex = function() {
		return $navigation.find('li').index($activeStep) + 1;
	};
	this.previousIndex = function() {
		return $navigation.find('li').index($activeStep) - 1;
	};
	this.navigationLength = function() {
		return $navigation.find('li').length - 1;
	};
	this.activeStep = function() {
		return $activeStep;
	};
	this.nextStep = function() {
		return $navigation.find('li:eq('+(obj.currentIndex()+1)+')').length ? $navigation.find('li:eq('+(obj.currentIndex()+1)+')') : null;
	};
	this.previousStep = function() {
		if(obj.currentIndex() <= 0) {
			return null;
		}
		return $navigation.find('li:eq('+parseInt(obj.currentIndex()-1)+')');
	};
	this.show = function(index) {
		return element.find('li:eq(' + index + ') a').addClass("active");
	};
	this.disable = function(index) {
		$navigation.find('li:eq('+index+')').addClass('disabled');
	};
	this.enable = function(index) {
		$navigation.find('li:eq('+index+')').removeClass('disabled');
	};
	this.hide = function(index) {
		$navigation.find('li:eq('+index+')').hide();
	};
	this.display = function(index) {
		$navigation.find('li:eq('+index+')').show();
	};
	this.remove = function(args) {
		var $index = args[0];
		var $removeStepPane = typeof args[1] != 'undefined' ? args[1] : false;
		var $item = $navigation.find('li:eq('+$index+')');

		if($removeStepPane) {
			var $href = $item.find('a').attr('href');
			$($href).remove();
		}

		$item.remove();
	};

	$navigation = element.find('ul:first', element);
	$activeStep = $navigation.find('li.active', element);

	if(!$navigation.hasClass($settings.stepClass)) {
		$navigation.addClass($settings.stepClass);
	}
	
	$boxContent = element.find('.form-container');
	$boxContent.hide();
	
	// Load onInit
	if($settings.onInit && typeof $settings.onInit === 'function'){
		$settings.onInit($activeStep, $navigation, 0);
	}

	// Load onShow
	if($settings.onShow && typeof $settings.onShow === 'function'){
		$settings.onShow($activeStep, $navigation, obj.nextIndex());
	}

	obj.fixNavigationButtons();

	$('a', $navigation).on('click', function (e) {
		
		if ($(this).find("i").length==0) return false;
		// 获取点击的步骤的indexs
		var clickedIndex = $navigation.find('li').index($(e.currentTarget).parent('li'));

		if($settings.onStepClick && typeof $settings.onStepClick === 'function' && $settings.onStepClick($activeStep, $navigation, obj.currentIndex(), clickedIndex)===false){
			return false;
		}
		
		var currentIndex=obj.currentIndex();
		
		obj.activateStep(clickedIndex,currentIndex);
		
		obj.hideContent(currentIndex);
		obj.showContent(clickedIndex);
		
		$($settings.previousSelector, element).toggleClass('disabled', (obj.firstIndex() >= obj.currentIndex()));
		if (clickedIndex >= obj.navigationLength()){
			$($settings.nextSelector, element).text($settings.nextButtonLastText);
		}else{
			$($settings.nextSelector, element).text($settings.nextButtonText);
		}
		
	});

	$('a', $navigation).on('shown', function (e) {  // use shown instead of show to help prevent double firing
		$element = $(e.target).parent();
		var nextStep = $navigation.find('li').index($element);

		if($element.hasClass('disabled')) {
			return false;
		}

		if($settings.onStepChange && typeof $settings.onStepChange === 'function' && $settings.onStepChange($activeStep, $navigation, obj.currentIndex(), nextStep)===false){
				return false;
		}

		$activeStep = $element; 
		obj.fixNavigationButtons();
	});
};
$.fn.bootstrapWizard = function(options) {
	if (typeof options == 'string') {
		var args = Array.prototype.slice.call(arguments, 1)
		if(args.length === 1) {
			args.toString();
		}
		return this.data('bootstrapWizard')[options](args);
	}
	return this.each(function(index){
		var element = $(this);
		if (element.data('bootstrapWizard')) return;
		// 传参数给插件的构造函数
		var wizard = new bootstrapWizardCreate(element, options);
		// 保存插件对象
		element.data('bootstrapWizard', wizard);
	});
};

//暴露的参数选项
$.fn.bootstrapWizard.defaults = {
	nextButtonLastText: '提交',
	nextButtonText:		'下一步 →',
	stepClass:          'steps-nav nav nav-pills',
	nextSelector:     	'.form-actions button.next',
	previousSelector:   '.form-actions button.previous',
	firstSelector:      '.form-actions button.first',
	lastSelector:       '.form-actions button.last',
	onShow:             null,
	onInit:             null,
	onNext:             null,
	onPrevious:         null,
	onLast:             null,
	onFirst:            null,
	onStepChange:       null, 
	onStepClick:        null,
	onStepShow:         null
};

})(jQuery);
