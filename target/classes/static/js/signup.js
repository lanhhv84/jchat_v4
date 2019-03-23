document.querySelector('#signup-form').addEventListener('submit', function(e) {
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
        url: '/user/add',
        type: "GET",
        data: {username: retrieved["username"], password: retrieved["password"]},

    });



    var notification = document.querySelector("#signup-notification");
    var notification2 = document.querySelector("#signup-notification2");



    request.done(function(msg) {
        if (msg["value"] === true) {
            notification.innerHTML = "Signing up success. Login?";
            notification2.innerHTML = "";
        }
        else {
            notification.innerHTML = "Username already existed.";
            notification2.innerHTML = "Already have an account. Sign up now?";
        }
    });

    console.log(obj);

    //at this point might occur some custom validation based on retrieved Object



});