
function confirmInputs() {
    corfirmNickname();
    confirmPassword();
}

function corfirmNickname() {
    var nickname = document.getElementById("nickname").value;

    if (nickname.length == 0) {
        document.getElementById("nickname-error").innerHTML = "may not be empty";
    }
}

function confirmPassword() {
    var password = document.getElementById("pwd").value;
    var confirmPassword = document.getElementById("pwd-confirm").value;

    if (password.length < 6) {
        document.getElementById("password-error").innerHTML = "password may not be shorter than 6";
    } else if (password.length > 20) {
        document.getElementById("password-error").innerHTML = "password may not be longer than 20";
    } else if (confirmPassword != password) {
        document.getElementById("confirm-error").innerHTML = "must be same as password";
    } else {
        document.getElementById("signup-form").submit();
    }
}


