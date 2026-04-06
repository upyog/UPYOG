//var finalBillCategory;
var taxAmount;
var agencyType = "";
$(document).ready(function(){
	
});



function populateRate(x) {

    // ✅ Robust index extraction — works for row 0,1,2...99
    var selectedrow = x.id;
    var match = selectedrow.match(/\[(\d+)\]/);
    if (!match) return;
    var creditrowindex = match[1];

    $.ajax({
        method: "GET",
        url: "/services/EGF/common/getrate",
        data: {},
        async: true
    }).done(function(response) {

        var $rateDropdown = $('#tempCreditDetails\\[' + creditrowindex + '\\]\\.rate');
        var $creditAmount = $('#creditDetails\\[' + creditrowindex + '\\]\\.creditamount');

        // Clear existing options
        $rateDropdown.empty();

        if (response.length == 1) {
            // Single rate — auto fill and lock amount
            $creditAmount.prop("readonly", true);
            $creditAmount.prop("required", true);
            $rateDropdown.append(
                $('<option>').val(response[0]).text(response[0] + "%")
            );
            $rateDropdown.val(response[0]);
        } else {
            // Multiple rates — user selects, amount is editable
            $rateDropdown.append($("<option value='0'>Select</option>"));
            $creditAmount.prop("readonly", false);
            $creditAmount.prop("required", false);

            $.each(response, function(index, value) {
                $rateDropdown.append(
                    $('<option>').val(value).text(value + "%")
                );
            });
        }

        calcualteNetpaybleAmount();
    });
}

function getTotalDebitAmount() {
    var total = 0;

    $('.debitAmount').each(function () {
        var val = parseFloat($(this).val());
        if (!isNaN(val)) {
            total += val;
        }
    });

    return total;
}


function calculateCreditFromRate(rateDropdown) {

    var selectedrow = rateDropdown.id;
    var match = selectedrow.match(/\[(\d+)\]/);
    if (!match) return;

    var index = match[1];

    var rate = parseFloat($(rateDropdown).val());
    if (isNaN(rate) || rate == 0) return;

    // ✅ Get total debit
    var totalDebit = getTotalDebitAmount();

    // ✅ Calculate percentage
    var calculatedAmount = (totalDebit * rate) / 100;

    // ✅ Set value in credit amount field
    $('#creditDetails\\[' + index + '\\]\\.creditamount')
        .val(calculatedAmount.toFixed(2));

    calcualteNetpaybleAmount();
}












