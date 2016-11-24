function showNotify(title, text, type){
	new PNotify({
		title: title,
		text: text,
		type: type,
		hide: false,
		buttons: {
			closer_hover: false,
			sticker: false,
			sticker_hover: false
		}
	});
}

//Workaround bug bootstrap dropdown menu touchscreen
$('.dropdown-toggle').click(function(e) {
	e.preventDefault();
	setTimeout($.proxy(function() {
		if ('ontouchstart' in document.documentElement) {
			$(this).siblings('.dropdown-backdrop').off().remove();
		}
	}, this), 0);
});

