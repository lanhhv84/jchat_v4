'use strict';

var nameInput = $('#name');
var roomInput = $('#room-id');
var usernamePage = document.querySelector('#username-page');
var chatPage = document.querySelector('#chat-page');
var usernameForm = document.querySelector('#usernameForm');
var messageForm = document.querySelector('#messageForm');
var messageInput = document.querySelector('#message');
var messageArea = document.querySelector('#messageArea');
var connectingElement = document.querySelector('.connecting');
var roomIdDisplay = document.querySelector('#room-id-display');

var stompClient = null;
var currentSubscription;
var username = null;
var roomId = null;
var topic = null;

var colors = [
    '#2196F3', '#32c787', '#00BCD4', '#ff5652',
    '#ffc107', '#ff85af', '#FF9800', '#39bbb0'
];

function connect(event) {
    username = nameInput.val().trim();
    Cookies.set('name', username);
    if (username) {
        usernamePage.classList.add('hidden');
        chatPage.classList.remove('hidden');

        var socket = new SockJS('/ws');
        stompClient = Stomp.over(socket);

        stompClient.connect({}, onConnected, onError);
    }
    event.preventDefault();
}

// Leave the current room and enter a new one.
function enterRoom(newRoomId) {
    roomId = newRoomId;
    Cookies.set('roomId', roomId);
    roomIdDisplay.textContent = roomId;
    topic = `/app/chat/${newRoomId}`;

    if (currentSubscription) {
        currentSubscription.unsubscribe();
    }
    currentSubscription = stompClient.subscribe(`/channel/${roomId}`, onMessageReceived);

    stompClient.send(`${topic}/addUser`,
        {},
        JSON.stringify({sender: username, type: 'JOIN'})
    );
}

function onConnected() {
    enterRoom(roomInput.val());
    connectingElement.classList.add('hidden');
}

function onError(error) {
    connectingElement.textContent = 'Could not connect to WebSocket server. Please refresh this page to try again!';
    connectingElement.style.color = 'red';
}

function sendMessage(event) {
    var messageContent = messageInput.value.trim();
    if (messageContent.startsWith('/join ')) {
        var newRoomId = messageContent.substring('/join '.length);
        enterRoom(newRoomId);
        while (messageArea.firstChild) {
            messageArea.removeChild(messageArea.firstChild);
        }
    } else if (messageContent && stompClient) {
        var chatMessage = {
            sender: username,
            content: messageInput.value,
            type: 'CHAT'
        };
        stompClient.send(`${topic}/sendMessage`, {}, JSON.stringify(chatMessage));
    }
    messageInput.value = '';
    event.preventDefault();
}

function onMessageReceived(payload) {
    var message = JSON.parse(payload.body);

    var messageElement = document.createElement('li');

    if (message.type == 'JOIN') {
        messageElement.classList.add('event-message');
        message.content = message.sender + ' joined!';
    } else if (message.type == 'LEAVE') {
        messageElement.classList.add('event-message');
        message.content = message.sender + ' left!';
    } else {
        messageElement.classList.add('chat-message');

        var avatarElement = document.createElement('i');
        var avatarText = document.createTextNode(message.sender[0]);
        avatarElement.appendChild(avatarText);
        avatarElement.style['background-color'] = getAvatarColor(message.sender);

        messageElement.appendChild(avatarElement);

        var usernameElement = document.createElement('span');
        var usernameText = document.createTextNode(message.sender);
        usernameElement.appendChild(usernameText);
        messageElement.appendChild(usernameElement);
    }

    var textElement = document.createElement('p');
//  var messageText = document.createTextNode(message.content);
    textElement.innerHTML = message.content;
//  textElement.appendChild(messageText);

    messageElement.appendChild(textElement);

    messageArea.appendChild(messageElement);
    messageArea.scrollTop = messageArea.scrollHeight;
}

function getAvatarColor(messageSender) {
    var hash = 0;
    for (var i = 0; i < messageSender.length; i++) {
        hash = 31 * hash + messageSender.charCodeAt(i);
    }
    var index = Math.abs(hash % colors.length);
    return colors[index];
}

$(document).ready(function() {
    var savedName = Cookies.get('name');
    if (savedName) {
        nameInput.val(savedName);
    }

    var savedRoom = Cookies.get('roomId');
    if (savedRoom) {
        roomInput.val(savedRoom);
    }

    usernamePage.classList.remove('hidden');
    usernameForm.addEventListener('submit', connect, true);
    messageForm.addEventListener('submit', sendMessage, true);
});


var singleUploadForm = document.querySelector('#singleUploadForm');
var singleFileUploadInput = document.querySelector('#singleFileUploadInput');
var singleFileUploadError = document.querySelector('#singleFileUploadError');
var singleFileUploadSuccess = document.querySelector('#singleFileUploadSuccess');

function uploadSingleFile(file) {
    var formData = new FormData();
    formData.append("file", file);

    var xhr = new XMLHttpRequest();
    xhr.open("POST", "/uploadFile");

    xhr.onload = function() {
        console.log(xhr.responseText);
        var response = JSON.parse(xhr.responseText);
        if(xhr.status == 200) {
            singleFileUploadError.style.display = "none";
            singleFileUploadSuccess.innerHTML = "<p>File Uploaded Successfully.</p>";
            singleFileUploadSuccess.style.display = "block";
            sendMessage = "File : <a href='" + response.fileDownloadUri + "' target='_blank'>" + response.fileName + "</a><br>Filetype :"+response.fileType+ "<br>Size :"+response.size;

            if (stompClient) {
                var chatMessage = {
                    sender: username,
                    content: sendMessage,
                    type: 'CHAT'
                };
                stompClient.send(`${topic}/sendMessage`, {}, JSON.stringify(chatMessage));
            }

        } else {
            singleFileUploadSuccess.style.display = "none";
            singleFileUploadError.innerHTML = (response && response.message) || "Some Error Occurred";
        }
    }

    xhr.send(formData);
}

singleUploadForm.addEventListener('submit', function(event){
    var files = singleFileUploadInput.files;
    if(files.length === 0) {
        singleFileUploadError.innerHTML = "Please select a file";
        singleFileUploadError.style.display = "block";
    }
    uploadSingleFile(files[0]);
    event.preventDefault();
}, true);
