document.querySelector('#login-form').addEventListener('submit', function(e) {
    e.preventDefault();
    var data = new FormData(this); // create new FormData object
    data = data.entries();
    var obj = data.next(); // access Iterator
    var retrieved = {};
    while(undefined !== obj.value) {
        retrieved[obj.value[0]] = obj.value[1];
        obj = data.next();
    } // parse FormData into simple Object


    var request = $.ajax({
        url: '/user/login',
        type: "GET",
        data: {username: retrieved["username"], password: retrieved["password"]},

    });

    var notification = document.querySelector("#login-notification");

    request.done(function(msg) {
        if (msg["value"] === true) {
            sessionStorage.setItem("username", retrieved["username"]);
            window.location.replace("/index.html");
        }
        else {
            notification.innerHTML = "Incorrect identification";
        }
    });

});