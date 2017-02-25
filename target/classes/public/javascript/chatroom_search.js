
function searchFrom(chatrooms) {
    var input = document.getElementById("search-input").value;

    for (var i = 0; i < chatrooms.length; i++) {
        var name = chatrooms[i].name;
        
        if (name.includes(input)) {
            if (document.getElementById(name).style.display == "none") {
                document.getElementById(name).removeAttribute("style");
            }
        } else {
            document.getElementById(name).style.display = "none";
        }
    }

}