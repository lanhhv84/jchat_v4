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

    console.log(obj);

    //at this point might occur some custom validation based on retrieved Object

    var XHR = new XMLHttpRequest();
    var urlEncodedData = "";
    var urlEncodedDataPairs = [];

    for(var name in retrieved) {
        urlEncodedDataPairs.push(encodeURIComponent(name) + '=' + encodeURIComponent(retrieved[name]));
    }
    urlEncodedData = urlEncodedDataPairs.join('&').replace(/%20/g, '+'); // parse object into urlEncoded String

    XHR.addEventListener('load', function(event) {
        var response = event.explicitOriginalTarget.response;
        var value = JSON.parse(response)["value"];
        var notification = document.querySelector("#signup-notification");
        console.log(typeof(value));
        console.log(value);
        if (value === true) {
            notification.innerHTML = "Signing up success. Login?";
        }
        else {
            notification.innerHTML = "Username already existed.";
        }


    });
    XHR.addEventListener('error', function(event) {
        console.log('Oops! Something goes wrong.');
    });

    XHR.open('POST', '/user/add');
    XHR.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
    XHR.send(urlEncodedData); // send the form

});