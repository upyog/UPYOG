/*
 * Budget Head COA (Chart of Accounts) Mapping Helper
 * Manages dynamic account code selection for linking budget heads to GL accounts
 * - Implements account code autocomplete using Bloodhound with subledger support
 * - Validates duplicate account code entries per budget head
 * - Supports dynamic row add/delete operations with 40 row limit
 * - Integrates with /common/getaccountcodesforaccountdetailtype endpoint
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

    debitGlcode_initialize();

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

function debitGlcode_initialize() {
    var custom = new Bloodhound({
        datumTokenizer: function (d) { return d.tokens; },
        queryTokenizer: Bloodhound.tokenizers.whitespace,
        remote: {
            url: '/services/EGF/common/getaccountcodesforaccountdetailtype?glcode=',
            dataType: "json",
            replace: function (url, query) {
                var subLedgerType = $('#subLedgerType').val();
                if (subLedgerType == null || subLedgerType == "")
                    subLedgerType = "0";
                if (subLedgerType != null || subLedgerType != "")
                    return url + query + '&accountDetailType=' + subLedgerType;
            },
            filter: function (data) {
                var responseObj = JSON.parse(data);
                return $.map(responseObj, function (ct) {
                    return {
                        id: ct.id,
                        name: ct.name,
                        glcode: ct.glcode,
                        issubledger: ct.isSubLedger,
                        glcodesearch: ct.glcode + ' ~ ' + ct.name
                    };
                });
            }
        }
    });

    custom.initialize();
    var dt = $('.debitGlcode').typeahead({
        hint: true,
        highlight: true,
        minLength: 3

    }, {
        displayKey: 'glcodesearch',
        source: custom.ttAdapter()
    }).on('typeahead:selected typeahead:autocompleted', function (event, data) {

        var originalglcodeid = data.id;
        var originaldetailtypeid = $('#subLedgerType').val();
        var originaldetailkeyid = $("#detailkeyId").val();
        var flag = false;
        $('#tbldebitdetails  > tbody > tr:visible[id="debitdetailsrow"]').each(function (index) {
            var glcodeid = document.getElementById('tempDebitDetails[' + index + '].glcodeid').value;
            var detailtypeid = document.getElementById('tempDebitDetails[' + index + '].detailTypeId').value;
            var detailkeyid = document.getElementById('tempDebitDetails[' + index + '].detailKeyId').value;
            if (glcodeid != "" && originalglcodeid == glcodeid && originaldetailtypeid == detailtypeid && originaldetailkeyid == detailkeyid) {
                flag = true;
            }
        });
        if (data.issubledger && originaldetailtypeid != '' && originaldetailkeyid == '') {
            bootbox.alert($.i18n.prop('msg.please.enter', subLedgerDisplayName), function () {
                var index = dt.length - 1;
                if (document.getElementById('tempDebitDetails[' + index + '].debitGlcode'))
                    document.getElementById('tempDebitDetails[' + index + '].debitGlcode').value = "";
            });
        } else if (flag) {
            bootbox.alert($.i18n.prop('msg.debit.code.already.added'), function () {
                var index = dt.length - 1;
                if (document.getElementById('tempDebitDetails[' + index + '].debitGlcode'))
                    document.getElementById('tempDebitDetails[' + index + '].debitGlcode').value = "";
            });
        } else {
            $(this).parents("tr:first").find('.debitdetailname').val(data.name);
            $(this).parents("tr:first").find('.debitaccountcode').val(data.glcode);
            $(this).parents("tr:first").find('.debitdetailid').val(data.id);
        }
    });
}

function addDebitDetailsRow() {
    $('.debitGlcode').typeahead('destroy');
    $('.debitGlcode').unbind();
    var rowcount = $("#tbldebitdetails tbody tr").length;
    if (rowcount < 40) {
        if (document.getElementById('debitdetailsrow') != null) {
            addRow('tbldebitdetails', 'debitdetailsrow');
            $('#tbldebitdetails tbody tr:eq(' + rowcount + ')').find('.debitDetailGlcode').val('');
            $('#tbldebitdetails tbody tr:eq(' + rowcount + ')').find('.debitdetailname').val('');
            debitGlcode_initialize();
            addCustomEvent(rowcount, 'tempDebitDetails[index].addButton', 'keydown', shortKeyFunForAddButton);
        }
    } else {
        bootbox.alert($.i18n.prop('msg.limit.reached'));
    }
}

function deleteDebitDetailsRow(obj) {
    var rowcount = $("#tbldebitdetails tbody tr").length;
    if (rowcount <= 1) {
        bootbox.alert($.i18n.prop('msg.this.row.can.not.be.deleted'));
        return false;
    } else if (confirm("Are you sure you want to Delete")) {
        deleteRow(obj, 'tbldebitdetails');
        --debitAmountrowcount;
        return true;
    } else {
        return false
    }

    resetDebitCodes();
}
function shortKeyFunForAddButton(zEvent) {
    var currId = zEvent.target.id;
    if (currId.startsWith('tempDebitDetails') && zEvent.keyCode == 32) {
        zEvent.preventDefault();
        addDebitDetailsRow();
    }
    zEvent.stopPropagation();
}