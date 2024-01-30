var condition = document.getElementById('conditionCheckbox');
var policy = document.getElementById('privacyCheckbox');
var contbtn = document.getElementById('continuebtn');

function checkRadio(){
    contbtn.disabled = !(condition.checked && policy.checked);
}

function checkSubmit() {
    var condition = document.getElementById('conditionCheckbox');
    var policy = document.getElementById('privacyCheckbox');

    // Check if both checkboxes are checked
    if (condition.checked && policy.checked) {
        // Notify Android code about user's acceptance
        jsBridge.postMessage("userconsent", "Accepted");

        // Proceed with your action (e.g., redirecting to MainMenu)
        AndroidInterface.redirectToMainMenu();
    } else {
        // Handle the case where user has not accepted the consent
        jsBridge.postMessage("userconsent", "Dismissed");
        alert("Please accept both conditions and privacy policy before continuing.");
    }
}

function checkDismiss() {
    jsBridge.postMessage("userconsent","Dismissed");
     jsBridge.postMessage("CloseButtonClicked", "Clicked");
        jsBridge.logConsole("Close button clicked");
        window.close();
}
