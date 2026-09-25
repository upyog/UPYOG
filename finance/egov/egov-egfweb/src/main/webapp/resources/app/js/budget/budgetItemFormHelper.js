/**
 * Budget Item Form Helper - Client-side JavaScript for budget item entry form.
 *
 * Key Features:
 * - Budget head autocomplete with typeahead.js (searches by function)
 * - Dynamic budget code generation (function code + budget head code)
 * - Scheme autocomplete for program-based budget heads
 * - Dynamic row add/delete for multiple budget items
 * - State budget code generation (combines state codes from budget head and scheme)
 * - Duplicate budget code validation
 * - Form field validation (required fields, invalid codes)
 *
 * Autocomplete Endpoints:
 * - Budget Head: /budgethead/ajaxBudgetHead/{functionId}
 * - Scheme: /scheme/ajaxSchemes
 *
 * Validation Rules:
 * - Maximum 80 budget item rows per form
 * - Budget codes must be unique within the form
 * - Scheme is mandatory for program-based budget heads
 * - Budget head must be selected before submission
 *
 * Auto-generated Fields:
 * - Budget Code: functionCode-budgetHeadCode
 * - Budget Group: Mapped from account type (RR/RE → Revenue, CR/CE → Capital)
 * - State Budget Code: stateCode or stateCode-schemeStateCode (if scheme selected)
 */




$(document).ready(function () {
    $.i18n.properties({
        name: 'message',
        path: '/services/EGF/resources/app/messages/',
        mode: 'both',
        async: true,
        cache: true,
        language: getLocale("locale"),
        callback: function () {
            console.log('File loaded successfully');
        }
    });

    budgethead_initialize();
    //scheme_initialize();
});

function getCookie(name) {
    let cookies = document.cookie;
    if (cookies.search(name) != -1) {
        var keyValue = cookies.match('(^|;) ?' + name + '=([^;]*)(;|$)');
        return keyValue ? keyValue[2] : null;
    }
}

function getLocale(paramName) {
    return getCookie(paramName) ? getCookie(paramName) : navigator.language;
}

function budgethead_initialize() {
    var functionid = document.getElementById("functionid")?.value;
    var custom = new Bloodhound({
        datumTokenizer: Bloodhound.tokenizers.obj.whitespace('code', 'name'),
        queryTokenizer: Bloodhound.tokenizers.whitespace,
        remote: {
            url: `/services/EGF/budgethead/ajaxBudgetHead/${functionid}?query=%QUERY`,
            wildcard: '%QUERY',
            dataType: "json",
            transform: function (response) {
                // Response is already parsed JSON (array of BudgetHead)
                console.log(response);
                return $.map(response, function (ct) {
                    return {
                        id: ct.id,
                        name: ct.name,
                        code: ct.code,
                        accountType: ct.accountType,
                        accountTypeCode: ct.accountTypeCode,
                        program: ct.program,
                        category: ct.category,
                        stateCode: ct.stateCode
                    };
                });
            }
        }
    });

    custom.initialize();

    var bc = $('.budgetcode').typeahead(
        {
            hint: true,
            highlight: true,
            minLength: 2 // show suggestions faster
        },
        {
            name: 'budgethead',
            display: function (item) {
                return item.code + ' - ' + item.name;
            },
            source: custom.ttAdapter(),
            limit: 20,
            templates: {
                suggestion: function (data) {
                    return `<div>${data.code} - ${data.name}</div>`;
                }
            }
        }
    ).on('typeahead:selected typeahead:autocompleted', function (event, data) {
        // console.log("Selected data:", data);
        // console.log("Selected event:", event);

        var originalBudgetHeadcode = data.code;
        var functionCode = document.getElementById("functionCode")?.value;
        var budgetHeadId = data.id;

        var flag = false;


        $('#dynamicTable  > tbody > tr:visible[id="budgetdetailsrow"]').each(function (index) {

            var budgetheadcode = document.getElementById('items[' + index + '].budgetheadcode').value;
            var budgetcode = document.getElementById('items[' + index + '].genBudgetCode').value;
            var budgetgroup = document.getElementById('items[' + index + '].budgetGroup').value;
            var budgetheadid = document.getElementById('items[' + index + '].budgetHeadId').value;

            if (budgetheadcode != "" && originalBudgetHeadcode == budgetheadcode) {
                flag = true
            }

            //            budgetheadcode.value = originalBudgetHeadcode;
            //            budgetcode.value = functionCode + '-' + originalBudgetHeadcode;
            //            budgetgroup.value = getBudgetGroup(data.accountTypeCode);
            //            budgetheadid.value = budgetHeadId;
        });

        if (flag) {
            bootbox.alert($.i18n.prop('msg.budget.code.already.added'), function () {
                var index = bc.length - 1;
                if (document.getElementById('items[' + index + '].budgetcode'))
                    document.getElementById('items[' + index + '].budgetcode').value = "";
            }
            );
        } else {
            //            $(this).parents("tr:first").find('.debitdetailname').val(data.name);
            $(this).parents("tr:first").find('.budgetheadcode').val(data.code);
            //            $(this).parents("tr:first").find('.budgetcode').val(functionCode + '-' + data.code);
            $(this).parents("tr:first").find('.genBudgetCode').val(functionCode + '-' + data.code);
            $(this).parents("tr:first").find('.budgetGroup').val(getBudgetGroup(data.accountTypeCode));
            $(this).parents("tr:first").find('.budgetHeadId').val(data.id);
            $(this).parents("tr:first").find('.stateCode').val(data.stateCode);

            $(this).parents("tr:first").find('.stateBudgetCode').val(data.stateCode);

            var row = $(this).parents("tr:first");
            var program = data.program;

            // Show/hide only inside this row
            if (program === "Yes") {
                row.find('.scheme-container').show();
                row.find('.scheme-input')
                    .attr('required', 'required')
                    .attr('data-optional', '0')
                    .attr('data-errormsg', 'Scheme is mandatory!')
                    .attr('data-idx', '0');

                //  scheme_initialize(row);

                row.find('.scheme-input').typeahead('destroy').unbind();

            } else {
                row.find('.scheme-container').hide();

                row.find('.scheme-input')
                    .removeAttr('required')
                    .attr('data-optional', '1')
                    .removeAttr('data-errormsg')
                    .removeAttr('data-idx');

                row.find('.scheme-input').typeahead('destroy').unbind();
            }

        }

        $(this).removeClass("is-invalid");
    });

    // Clear hidden fields *whenever user types*
    $('.budgetcode').on('input', function () {
        var row = $(this).parents("tr:first");

        row.find(".budgetHeadId").val("");
        row.find(".budgetheadcode").val("");
        row.find(".budgetGroup").val("");
        row.find(".genBudgetCode").val("");
        row.find(".schemeId").val("");
        row.find(".stateCode").val("");
        row.find(".stateBudgetCode").val("");

        $(this).removeClass("is-invalid");
    });

    // Simple invalid code check
    $('.budgetcode').on('blur', function () {
        var row = $(this).parents("tr:first");
        var budgetHeadId = row.find(".budgetHeadId").val();

        if (!budgetHeadId) {
            $(this).addClass("is-invalid");
            row.find(".budgetcode").val("");
            bootbox.alert("Invalid Budget Code!");
        } else {
            $(this).removeClass("is-invalid");
        }
    });

}


function addBudgetDetailsRow() {
    $('.budgetcode').typeahead('destroy');
    $('.budgetcode').unbind();

    $('.scheme-input').typeahead('destroy');
    $('.scheme-input').unbind();

    var rowcount = $("#dynamicTable tbody tr").length;
    if (rowcount < 80) {
        if (document.getElementById('budgetdetailsrow') != null) {
            addRow('dynamicTable', 'budgetdetailsrow');
            $('#dynamicTable tbody tr:eq(' + rowcount + ')').find('.budgetHeadcode').val('');
            $('#dynamicTable tbody tr:eq(' + rowcount + ')').find('.scheme-container').hide();
            budgethead_initialize();
            //  addCustomEvent(rowcount, 'items[index].addButton', 'keydown', shortKeyFunForAddButton);
        }
    } else {
        bootbox.alert($.i18n.prop('msg.limit.reached'));
    }
}

function deleteBudgetDetailsRow(obj) {
    var rowcount = $("#dynamicTable tbody tr").length;
    if (rowcount <= 1) {
        bootbox.alert($.i18n.prop('msg.this.row.can.not.be.deleted'));
        return false;
    } else if (confirm("Are you sure you want to Delete")) {
        deleteRow(obj, 'dynamicTable');
        return true;
    } else {
        return false
    }
}

function shortKeyFunForAddButton(zEvent) {
    var currId = zEvent.target.id;
    if (currId.startsWith('items') && zEvent.keyCode == 32) {
        zEvent.preventDefault();
        addBudgetDetailsRow();
    }
    zEvent.stopPropagation();
}

function getBudgetGroup(code) {
    const budgetMap = {
        RR: 'Revenue_Budget',
        RE: 'Revenue_Budget',
        CR: 'Capital_Budget',
        CE: 'Capital_Budget'
    };

    return budgetMap[code] || 'Unknown';
}

function scheme_initialize(row) {
    var scheme = new Bloodhound({
        datumTokenizer: Bloodhound.tokenizers.obj.whitespace('code', 'name'),
        queryTokenizer: Bloodhound.tokenizers.whitespace,
        remote: {
            url: '/services/EGF/scheme/ajaxSchemes?query=%QUERY',
            wildcard: '%QUERY',
            dataType: "json",
            transform: function (response) {
                return $.map(response, function (ct) {
                    return {
                        id: ct.id,
                        name: ct.name,
                        code: ct.code,
                        isactive: ct.isactive,
                        stateCode: ct.stateCode
                    };
                });
            }
        }
    });

    scheme.initialize();

    // apply typeahead ONLY to scheme input in this row
    row.find('.scheme-input').typeahead(
        {
            hint: true,
            highlight: true,
            minLength: 2
        },
        {
            name: 'schemes',
            display: function (item) {
                return item.code + ' - ' + item.name;
            },
            source: scheme.ttAdapter(),
            limit: 20,
            templates: {
                suggestion: function (data) {
                    return `<div>${data.code} - ${data.name}</div>`;
                }
            }
        }
    ).on('typeahead:selected typeahead:autocompleted', function (event, data) {

        console.log('scheme selected:', data);
        console.log('this element:', this);
        console.log('closest .scheme-container:', $(this).closest('.scheme-container').length);
        console.log('closest tr:', $(this).closest('tr').length);
        console.log('parent html:', $(this).parent().prop('outerHTML'));

        row.find('.schemeId').val(data.id);

        var statecode = row.find('.stateCode').val();

        //row.find('.stateBudgetCode').val(statecode + "-" + data.stateCode);
        row.find('.stateBudgetCode').val(statecode + "-" + (data.stateCode || "").trim());
    });

    // addSchemeValidations();
}


// function addSchemeValidations() {
//     // Clear hidden fields *whenever user types*
//     $('.scheme-input').on('input', function () {
//         var row = $(this).parents("tr:first");

//         var stateCode = row.find('.stateCode').val();

//         row.find(".schemeId").val("");
//         row.find(".stateBudgetCode").val(stateCode);

//         $(this).removeClass("is-invalid");

//     });

//     // Simple invalid code check
//     $('.scheme-input').on('blur', function () {
//         var row = $(this).parents("tr:first");
//         var stateCode = row.find('.stateCode').val();
//         var schemeId = row.find('.schemeId').val();

//         if (!schemeId) {
//             $(this).addClass("is-invalid");
//             row.find(".schemeId").val("");
//             row.find(".scheme-input").val("");
//             row.find(".stateBudgetCode").val(stateCode);
//             bootbox.alert("Invalid Scheme !");
//         } else {
//             $(this).removeClass("is-invalid");
//         }
//     });
// }

$(document).on('change blur', '.scheme-input', function () {

    var row = $(this).parents("tr:first");

    var schemeId = $(this).val();
    var schemeStateCode = $(this).find(':selected').data('statecode') || '';
    var stateCode = row.find('.stateCode').val();

    if (schemeId) {
        row.find('.stateBudgetCode')
            .val(stateCode + '-' + schemeStateCode);
    } else {
        row.find('.stateBudgetCode').val(stateCode);

        row.find('.scheme-input')
            .attr('required', 'required')
            .attr('data-optional', '0')
            .attr('data-errormsg', 'Scheme is mandatory!')
            .attr('data-idx', '0');
    }
});


