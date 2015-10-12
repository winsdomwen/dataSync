jQuery(function($){
    $('.layout-btns .layout-normal').click(function(e){
        $('.results table').removeClass('table-condensed');
    });

    $('.layout-btns .layout-condensed').click(function(e){
        $('.results table').addClass('table-condensed');
    });
    
    var $actionCheckboxes=$("tr input.action-select");
    
    $("#action-toggle").click(function() {
    	$actionCheckboxes.trigger('checker', $(this).is(":checked"));
    });
    $actionCheckboxes.bind('checker', function(e, checked){
        $(this).prop("checked", checked)
            .parent().parent().toggleClass("warning", checked);
    });
    
    lastChecked = null;
    $actionCheckboxes.click(function(event) {
        if (!event) { var event = window.event; }
        var target = event.target ? event.target : event.srcElement;

        if (lastChecked && $.data(lastChecked) != $.data(target) && event.shiftKey == true) {
            var inrange = false;
            $(lastChecked).trigger('checker', target.checked);
            $actionCheckboxes.each(function() {
                if ($.data(this) == $.data(lastChecked) || $.data(this) == $.data(target)) {
                    inrange = (inrange) ? false : true;
                }
                if (inrange) {
                    $(this).trigger('checker', target.checked);
                }
            });
        }

        $(target).trigger('checker', target.checked);
        lastChecked = target;
    });
    

    


    
});