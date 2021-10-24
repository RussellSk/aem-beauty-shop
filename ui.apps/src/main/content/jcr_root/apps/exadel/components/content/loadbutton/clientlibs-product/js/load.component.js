$(document).ready(function() {
    console.log("Products component JS library loaded");

    let page = 1;
    $('#loadMoreBtn').click(function () {
        sendRequest(page++);
    });

    function sendRequest(currentPage) {
        $.get('/content/exadel/us/en/main-home/jcr:content/root/container/productlist.html', {page: currentPage}, function (data) {
            $('#additional-products-container').append(data);
        })
    }
});