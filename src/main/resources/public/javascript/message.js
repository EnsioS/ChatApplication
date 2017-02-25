
function scrollDown() {
    var div = document.getElementById("messages");
    div.scrollTop = div.scrollHeight;
}

// want to execute this function onload
scrollDown();

function validateMessage(nickname, nicknames, content) {

    if (!validateNickname(nickname, nicknames)) {
        console.log("not valid niokname: " + nickname);
        return false;
    }

    if (!validateContent(content)) {
        console.log("not valid content: " + content);
        return false;
    }

    return true;
}

function validateNickname(nickname, nicknames) {

    if (nickname.length < 2) {
        document.getElementById("nickname-error").innerHTML = "nickname may not be shorter than 2";
        return false;
    } else if (nickname.length > 30) {
        document.getElementById("nickname-error").innerHTML = "nickname may not be longer than 30";
        return false;
    }

    for (var i = 0; i < nicknames.length; i++) {
        if (nickname == nicknames[i]) {
            document.getElementById("nickname-error").innerHTML = "this nickname is not free";
            return false;
        }
    }

    document.getElementById("nickname-error").innerHTML = "";
    return true;
}

function validateContent(content) {
    if (content.length == 0) {
        document.getElementById("content-error").innerHTML = "message may not be empty";
        return false;
    } else if (content.length > 500) {
        document.getElementById("content-error").innerHTML = "message may not be longer than 500";
        return false;
    }

    document.getElementById("content-error").innerHTML = "";
    return true;
}

function displayMessage(message) {
    var div = document.createElement("div");

    displayNickname(div, message);
    displayDate(div, message);
    displayContent(div, message);
}

function displayNickname(div, message) {
    var span = document.createElement("span");
    var nameNode = document.createTextNode(message.nickname);
    span.appendChild(nameNode);
    span.setAttribute("class", "message-nickname");
    div.appendChild(span);
    
}

function displayDate(div, message) {
    var date = new Date(message.timestamp);
    var span = document.createElement("span");
    var timeNode = document.createTextNode(date.toLocaleDateString() + " " + date.toLocaleTimeString().substring(0, 5));
    span.appendChild(timeNode);
    span.setAttribute("class", "message-time");
    div.appendChild(span);
}

function displayContent(div, message) {
    var br = document.createElement("br");
    div.appendChild(br);

    var span = document.createElement("span");
    var contentNode = document.createTextNode(message.content);
    span.appendChild(contentNode);
    span.setAttribute("class", "message-content");
    div.appendChild(span);

    document.getElementById("messages").appendChild(div);
}