let socket = new WebSocket('ws://' + window.location.host + '/socket');
socket.onmessage = renderMessages;


function sendform() {
	var data = document.getElementById("myText").value;
	socket.send(data);
}

socket.addEventListener('message', function (event) {
    console.log('Message from server ', event.data);
});