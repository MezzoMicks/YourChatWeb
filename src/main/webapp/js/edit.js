$(function() {

    function toolifyImgUpload($img) {
        $img.load(function() {
            if (!$img.parent().hasClass("editor-wrapper")) {
                var options = $img.data('editor'),
                    action = options.action;
                $img.data('original-src', $img.attr('src'));
                $img.wrap('<div class="editor-wrapper"></div>');
                $form = $('<form class="editor-img" action="' + action + '" method="POST">');
                $form.css('width', $img.outerWidth(false) + 'px');
                $form.css('top', ($img.position().top + $img.outerHeight(false) - 20) + 'px');
                $form.css('left', $img.position().left + 'px');
                $input = $('<input name="image" type="file" class="editor-input">');
                $reset = $('<a href="#" class="editor-action editor-reset">Reset</a>');
                $upload = $('<a href="#" class="editor-action editor-upload">Upload</a>');
                $submit = $('<a href="#" class="editor-action editor-submit">Submit</a>');
                if (options != null) {
                    if (options.resetLabel != null) {
                        $reset.text(options.resetLabel);
                    }
                    if (options.resetClass != null) {
                        $reset.addClass(options.resetClass);
                    }

                    if (options.uploadLabel != null) {
                        $upload.text(options.uploadLabel);
                    }
                    if (options.uploadClass != null) {
                        $upload.addClass(options.uploadClass);
                    }

                    if (options.submitLabel != null) {
                        $submit.text(options.submitLabel);
                    }
                    if (options.submitClass != null) {
                        $submit.addClass(options.submitClass);
                    }
                }
                $input.change(function(){
                    if (this.files && this.files[0]) {
                        var reader = new FileReader();
                        reader.onload = function (e) {
                            $img.attr('src', e.target.result);
                        }
                        if (this.files != null && this.files[0] != null && this.files[0] != "") {
                            reader.readAsDataURL(this.files[0])
                            $form.addClass('editor-data-available');
                        } else {
                            $img.attr('src', $img.data('original-src'));
                            $form.removeClass('editor-data-available');
                        }
                    }
                });
                $reset.click(function() {
                    $img.attr('src', $img.data('original-src'));
                    $form.removeClass('editor-data-available');
                    $input.val('');
                });
                $upload.click(function() {
                    $input.click();
                });
                $submit.click(function() {
                    $form.submit();
                });
                $form.append($input)
                    .append($reset)
                    .append($upload)
                    .append($submit);
                $form.insertAfter($img);
                $form.ajaxForm({
                    success:function(data) {
                        $img.attr('id', 'image' + data.id);
                        $img.attr('src', data.url);
                        $img.data('original-src', $img.attr('src'));
                        $form.removeClass('editor-data-available');
                        $input.val('');
                    },
                    resetForm: true ,
                    uploadProgress: function(e, pos, total, percent) {
                        console.log(percent);
                    }
                });
            }
        });
    }

    $("[data-editor]").each(function(ix, element) {
        var $editable = $(element),
            type = $editable.data('editor').type;
        if (type === "img-upload") {
            toolifyImgUpload($editable);
        }
    });
});