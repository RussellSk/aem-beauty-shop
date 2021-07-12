$(function() {
    console.log("Likes component JS library loaded");

    $('.likeButton').click(function () {
        sendRequest($(this), 'true');
    });

    $('.dislikeButton').click(function () {
        sendRequest($(this), 'false');
    });

    function sendRequest(element, likeType) {
        let productId = element.siblings('input[type=hidden]').val();
        $.ajax({
            type: 'POST',
            url: '/json/likes',
            data: 'product=' + productId + '&like_type=' + likeType,
            success: function (data) {
                element.parent('div').find('.likes-count-value').text(data.likesCount);
                element.parent('div').find('.dislikes-count-value').text(data.dislikesCount);
            },
        });
    }
});