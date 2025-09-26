/*
 *    eGov  SmartCity eGovernance suite aims to improve the internal efficiency,transparency,
 *    accountability and the service delivery of the government  organizations.
 *
 *     Copyright (C) 2017  eGovernments Foundation
 *
 *     The updated version of eGov suite of products as by eGovernments Foundation
 *     is available at http://www.egovernments.org
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program. If not, see http://www.gnu.org/licenses/ or
 *     http://www.gnu.org/licenses/gpl.html .
 *
 *     In addition to the terms of the GPL license to be adhered to in using this
 *     program, the following additional terms are to be complied with:
 *
 *         1) All versions of this program, verbatim or modified must carry this
 *            Legal Notice.
 *            Further, all user interfaces, including but not limited to citizen facing interfaces, 
 *            Urban Local Bodies interfaces, dashboards, mobile applications, of the program and any 
 *            derived works should carry eGovernments Foundation logo on the top right corner.
 *
 *            For the logo, please refer http://egovernments.org/html/logo/egov_logo.png.
 *            For any further queries on attribution, including queries on brand guidelines, 
 *            please contact contact@egovernments.org
 *
 *         2) Any misrepresentation of the origin of the material is prohibited. It
 *            is required that all modified versions of this material be marked in
 *            reasonable ways as different from the original version.
 *
 *         3) This license does not grant any rights to any user of the program
 *            with regards to rights under trademark law for use of the trade names
 *            or trademarks of eGovernments Foundation.
 *
 *   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 *
 */

jQuery(document).ready(function($) {

    console.log("Browser Language ", navigator.language);

    // i18n properties loading
    $.i18n.properties({ 
        name: 'message', 
        path: '/services/EGF/resources/app/messages/', 
        mode: 'both',
        async: true,
        cache: true,
        language: getLocale("locale"),
        callback: function() {
            console.log('File loaded successfully');
        }
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

    // Set default date in voucherDate
    $("#voucherDate").datepicker().datepicker("setDate", new Date());

    // ------------------ Global functions ------------------
    // Expose global functions
    window.loadBank = function(fund) {
        populatefromBankId({
            fundId: fund.options[fund.selectedIndex].value,
            typeOfAccount: "RECEIPTS_PAYMENTS,RECEIPTS"
        });
        checkInterFund();
    };

    window.loadFromDepartment = function() {
        var selectedFund = $("#fundId option:selected").text();
        var dep = $("#vouchermis\\.departmentid");
        if (selectedFund === '01-Municipal Fund' || selectedFund === '02-Capital Fund') {
            dep.prop("disabled", false).val(1);
        } else if (selectedFund === '03-Elementary Education Fund') {
            dep.val(31).prop("disabled", true);
        } else {
            dep.prop("disabled", false).val("");
        }
    };

    window.loadToDepartment = function() {
        var selectedFund = $("#toFundId option:selected").text();
        var dep = $("#contraBean\\.toDepartment");
        if (selectedFund === '01-Municipal Fund' || selectedFund === '02-Capital Fund') {
            dep.prop("disabled", false).val(1);
        } else if (selectedFund === '03-Elementary Education Fund') {
            dep.val(31).prop("disabled", true);
        } else {
            dep.prop("disabled", false).val("");
        }
    };

    window.loadToBank = function(fund) {
        populatetoBankId({
            fundId: fund.options[fund.selectedIndex].value,
            typeOfAccount: "RECEIPTS_PAYMENTS,PAYMENTS"
        });
    };

    window.loadFromAccNum = function(branch) {
        var fundObj = document.getElementById('fundId');
        var bankbranchId = branch.options[branch.selectedIndex].value;
        var index = bankbranchId.indexOf("-");
        var brId = bankbranchId.substring(index + 1);
        populatefromAccountNumber({
            fundId: fundObj.options[fundObj.selectedIndex].value,
            branchId: brId,
            typeOfAccount: "RECEIPTS_PAYMENTS,RECEIPTS"
        });
    };

    window.loadToAccNum = function(branch) {
        var fundObj = document.getElementById('toFundId');
        var bankbranchId = branch.options[branch.selectedIndex].value;
        var index = bankbranchId.indexOf("-");
        var brId = bankbranchId.substring(index + 1);
        populatetoAccountNumber({
            fundId: fundObj.options[fundObj.selectedIndex].value,
            branchId: brId,
            typeOfAccount: "RECEIPTS_PAYMENTS,PAYMENTS"
        });
    };

    window.populatefromNarration = function(accnumObj) {
        var accnum = accnumObj.options[accnumObj.selectedIndex].value;
        var bankbranchId = $("#fromBankId").val();
        var brId = bankbranchId.split("-")[1];
        var csrfToken = $("#csrfTokenValue").val();
        var url = '../voucher/common-loadAccNumNarration.action?accnum=' + accnum + '&_csrf=' + csrfToken + '&branchId=' + brId;
        YAHOO.util.Connect.asyncRequest('POST', url, postTypeFrom, null);
    };

    window.populatetoNarration = function(accnumObj) {
        var accnum = accnumObj.options[accnumObj.selectedIndex].value;
        var bankbranchId = $("#toBankId").val();
        var brId = bankbranchId.split("-")[1];
        var csrfToken = $("#csrfTokenValue").val();
        var url = '../voucher/common-loadAccNumNarration.action?accnum=' + accnum + '&_csrf=' + csrfToken + '&branchId=' + brId;
        YAHOO.util.Connect.asyncRequest('POST', url, postTypeTo, null);
    };

    var postTypeFrom = {
        success: function(o) { $("#fromAccnumnar").val(o.responseText); },
        failure: function(o) { bootbox.alert('failure'); }
    };

    var postTypeTo = {
        success: function(o) { $("#toAccnumnar").val(o.responseText); },
        failure: function(o) { bootbox.alert('failure'); }
    };

    window.nextChqNo = function() {
        var obj = $("#fromAccountNumber")[0];
        var bankBr = $("#fromBankId")[0];
        if (bankBr.selectedIndex === -1) { bootbox.alert("Select Bank Branch and Account No!!"); return; }
        if (obj.selectedIndex === -1) { bootbox.alert("Select Account No!!"); return; }
        var accNo = obj.options[obj.selectedIndex].text;
        var accNoId = obj.options[obj.selectedIndex].value;
        var sRtn = showModalDialog("../HTML/SearchNextChqNo.html?accntNo=" + accNo + "&accntNoId=" + accNoId, "", "dialogLeft=300;dialogTop=210;dialogWidth=305pt;dialogHeight=300pt;status=no;");
        if (sRtn !== undefined) $("#chequeNum").val(sRtn);
    };

    window.checkInterFund = function() {
        var fromFund = $("#fundId").val();
        var toFund = $("#toFundId").val();
        if (fromFund && toFund && fromFund !== toFund) {
            for (var i = 0; i < fund_map.length; i++) {
                var splitStr = fund_map[i].split("_");
                if (splitStr.length === 2) setDefaultValues(splitStr[0], splitStr[1]);
            }
            $("#interFundRow1,#interFundRow2,#interFundRow3").css("visibility", "visible");
        } else {
            $("#interFundRow1,#interFundRow2,#interFundRow3").css("visibility", "hidden");
        }
    };

    window.loadFromBalance = function(obj) {
        if (!$("#voucherDate").val()) { bootbox.alert("Please Select the Voucher Date!!"); obj.options.value = -1; return; }
        if (obj.options[obj.selectedIndex].value == -1) { $("#fromBankBalance").val(''); }
        else populatefromBankBalance({ bankaccount: obj.options[obj.selectedIndex].value, voucherDate: $("#voucherDate").val() + '&date=' + new Date() });
    };

    window.loadToBalance = function(obj) {
        if (!$("#voucherDate").val()) { bootbox.alert("Please Select the Voucher Date!!"); obj.options.value = -1; return; }
        if (obj.options[obj.selectedIndex].value == -1) $("#toBankBalance").val('');
        else populatetoBankBalance({ bankaccount: obj.options[obj.selectedIndex].value, voucherDate: $("#voucherDate").val() + '&date=' + new Date() });
    };

    window.disableControls = function(frmIndex, isDisable) {
        $("form").eq(frmIndex).find(":input").prop("disabled", isDisable);
    };

    window.enableAll = function() { $("form").eq(0).find(":input").prop("disabled", false); };

    window.validate = function() {
        var voucher_date = $("#voucherDate").val();
        var fundFlowDateChkStr = $("#startDateForBalanceCheckStr").val();
        var vh_split = voucher_date.split('/');
        var sp_date = fundFlowDateChkStr.split('-');
        var app_config_Date_value = new Date(sp_date[2], getMonthNo(sp_date[1]) - 1, sp_date[0]);
        var voucherDateChk = new Date(vh_split[2], vh_split[1] - 1, vh_split[0]);

        if ($("#amount").val() <= 0) { bootbox.alert("Amount should be greater than zero "); return false; }

        if (parseFloat($("#amount").val()) > parseFloat($("#fromBankBalance").val())) {
            if (voucherDateChk >= app_config_Date_value) {
                if ($("#bankBalanceMandatory").val() === 'true') { bootbox.alert("There is no sufficient bank balance."); return false; }
                else { if (confirm("Do you want to continue?")) $("#fromFundId").prop("disabled", false); else return false; }
            }
        }

	document.cbtbform.action='/services/EGF/contra/contraBTB-create.action';
	document.cbtbform.action='/services/EGF/contra/contraBTB-create.action';
	//document.cbtbform.submit();
        document.cbtbform.action='/services/EGF/contra/contraBTB-create.action';
	//document.cbtbform.submit();
        return true;
    };

    window.validateReverse = function() { return true; };

    window.setDefaultValues = function(fnd, objinterfnd) {
        var srcFund = $("#fundId").val();
        var desFund = $("#toFundId").val();
        if (srcFund === fnd) $("#sourceGlcode").val(objinterfnd || "-1");
        else if (desFund === fnd) $("#destinationGlcode").val(objinterfnd || "-1");
    };

    window.getMonthNo = function(month) {
        var map = { Jan:1, Feb:2, Mar:3, Apr:4, May:5, Jun:6, Jul:7, Aug:8, Sep:9, Oct:10, Nov:11, Dec:12 };
        return map[month] || month;
    };

});
