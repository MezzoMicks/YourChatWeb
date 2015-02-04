$().ready(function() {
	$complMain = $('.offcanvas');
	var breakpoint = $('<div class="offcanvas-breakpoint" />').insertAfter($complMain);
	var duration = 500;
	breakpoint.remove();
	$('.offcanvas').each(function(ix, element) {
		$element = $(element);
		$element.addClass('hidden');
	});
	$('.offcanvas-button').click(function() {
		var $target = $($(this).attr('href'));
		$target.toggleClass('hidden', duration);
		return false;
	});
	$('html').addClass('js-available');
});