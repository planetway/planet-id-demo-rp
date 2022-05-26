function checkFormIsSubmittable(formName, submitName)
{
    var formElements = document.forms[formName].elements;
    var canSubmit = true;

    for (var i = 0; i < formElements.length; i++) {
        var formElement = formElements[i];
        if (formElement.required && formElement.value.length === 0) {
            canSubmit = false;
        }
    }
    document.getElementById(submitName).disabled = !canSubmit;
}
