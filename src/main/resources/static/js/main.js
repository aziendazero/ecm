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

