function loadingAnchorImg(anchor) {
	var img = a.childNodes[1];
	if (img.src != null) {
		img.src = "";
	}
	else {
		alert("Do not found target image");
		return false;
	}
	return true;
}

function abc(a) {
	alert(a.childNodes.length);
	var b = a.childNodes[1];
	alert(b);
	b.src="http://l.yimg.com/mq/i/dic/041.gif";											
};

function bcd(a) {
	a.removeChild(a.childNodes[0]);
};
