var picPaths = '';
var imageCount = 0;

function showImage() {
    console.log('showImage');
    picPaths = jQuery('#hiddenPicPaths').text().split('^_^');
    if (picPaths.length < 1) {
        jQuery('#gImageContent').html('#{generalMsg.imageNA}');
    } else {
        jQuery('#vocabImage').attr("src", picPaths[imageCount]);

        if (picPaths.length == 1) {jQuery('#nextImage').hide();}
        else {jQuery('#nextImage').show();}
    }
}

function changeImg() {
    imageCount = imageCount + 1;
    if (imageCount >= picPaths.length) imageCount = 0;

    jQuery('#vocabImage').attr("src", picPaths[imageCount]);
    setFocus(jQuery('#inputForm\\:answer'));
    setFocus(jQuery('#practiceDiv\\:inputForm\\:answer'));
}
