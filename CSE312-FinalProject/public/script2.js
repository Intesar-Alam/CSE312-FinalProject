function upvote(x) {
    var xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function() {
        if (this.readyState == 4 && this.status == 200) {
            document.getElementById("currVotes".concat(x.toString())).innerHTML =
            this.responseText;
        }
    };
    var votes = document.getElementById("currVotes".concat(x.toString())).innerHTML;
    xhttp.open("GET", "upvote".concat(votes.toString()));
    xhttp.send();
}

function downvote(x) {
    var xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function() {
        if (this.readyState == 4 && this.status == 200) {
            document.getElementById("currVotes".concat(x.toString())).innerHTML =
            this.responseText;
        }
    };
    var votes = document.getElementById("currVotes".concat(x.toString())).innerHTML;
    xhttp.open("GET", "downvote".concat(votes.toString()));
    xhttp.send();
}

function sendMessageWithForm() {
	const formElement = document.getElementById("myForm");
 	const formData = new FormData(formElement);
 	const request = new XMLHttpRequest();
 	var cht = document.getElementById("body");
 	
 	request.onreadystatechange = function(){
   	if(this.readyState	===	4	&&	this.status	===	200){
     	console.log(this.response);
    	 cht.innerHTML = this.response;
  	}
 	};
 	
 	request.open("POST", "send-message-form");
 	request.send(formData);
}

