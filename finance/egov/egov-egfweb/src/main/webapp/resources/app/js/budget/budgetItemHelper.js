/**
 * Budget Item Helper - Client-side JavaScript for function selection in budget item forms.
 *
 * Key Features:
 * - Function autocomplete using typeahead.js
 * - Searches functions by name or code via AJAX endpoint
 * - Auto-populates hidden function ID field on selection
 * - Clears function ID when user manually edits the field
 *
 * Autocomplete Endpoint:
 * - GET /function/getByNameOrCode?query={search}
 * - Returns functions with budget heads configured (via FunctionBudgetHeadService)
 *
 * Response Format:
 * - "CODE - NAME ~ ID" (e.g., "F001 - Education ~ 123")
 *
 * Form Fields:
 * - #function: Visible input for function name/code (typeahead enabled)
 * - #functionId: Hidden input storing selected function ID
 *
 * Validation:
 * - Minimum 3 characters required to trigger autocomplete
 * - Function ID cleared if user types manually (ensures valid selection)
 */




$(document).ready(function () {
    var functionName = new Bloodhound({
        datumTokenizer: function (datum) {
            return Bloodhound.tokenizers.whitespace(datum.value);
        },
        queryTokenizer: Bloodhound.tokenizers.whitespace,
        remote: {
            url: '/services/EGF/function/getByNameOrCode?query=%QUERY', // '/services/EGF/common/ajaxfunctionnames?name=%QUERY',
            filter: function (data) {
                return $.map(data, function (ct) {
                    return {
                        code: ct.split("~")[0].split("-")[0],
                        name: ct.split("~")[0].split("-")[1],
                        id: ct.split("~")[1],
                        codeName: ct
                    };
                });
            }
        }
    });

    functionName.initialize();
    $('#function').typeahead({
        hint: true,
        highlight: true,
        minLength: 3
    }, {
        displayKey: 'name',
        source: functionName.ttAdapter()
    }).on('typeahead:selected', function (event, data) {
        $("#functionId").val(data.id);
    });

});
//$('#function').blur(function () {
//    if ($('.cfunction').val() == "") {
//        bootbox.alert("Please select function from dropdown values", function () {
//            $('#function').val("");
//        });
//    }
//});

// Clear ID if user manually edits
$('#functionName').on('input', function () {
    $('#functionId').val("");
});