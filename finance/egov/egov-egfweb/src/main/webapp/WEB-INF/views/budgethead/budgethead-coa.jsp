<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="/WEB-INF/tags/cdn.tld" prefix="cdn" %>
<style>
    span {
        cursor: pointer;
    }

    :focus {
        outline: 1px dashed green;
    }
</style>
<div class="panel-heading">
    <div class="panel-title">
        <spring:message code="lbl.budget.coa" text="COA details" />
    </div>
</div>
<div class="panel-body">
    <table class="table table-bordered" id="tbldebitdetails">
        <thead>
            <tr>
                <th>
                    <spring:message code="lbl.account.code" text="Account Code" />
                </th>
                <th>
                    <spring:message code="lbl.account.head" text="Account Head" />
                </th>
                <th>
                    <spring:message code="lbl.action" text="Action" />
                </th>
            </tr>
        </thead>
        <tbody>
            <tr id="debitdetailsrow">
                <td>
                    <input type="text" id="tempDebitDetails[0].debitGlcode" name="tempDebitDetails[0].debitGlcode"
                        class="form-control table-input debitDetailGlcode debitGlcode"
                        data-errormsg="Account Code is mandatory!" data-idx="0" data-optional="0"
                        placeholder="Type first 3 letters of Account code">
                    <form:hidden path="" name="tempDebitDetails[0].glcode" id="tempDebitDetails[0].glcode"
                        class="form-control table-input hidden-input debitaccountcode" />
                    <form:hidden path="" name="tempDebitDetails[0].glcodeid" id="tempDebitDetails[0].glcodeid"
                        class="form-control table-input hidden-input debitdetailid" />
                    <form:hidden path="" name="tempDebitDetails[0].isSubLedger" id="tempDebitDetails[0].isSubLedger"
                        class="form-control table-input hidden-input debitIsSubLedger" />
                    <form:hidden path="" name="tempDebitDetails[0].detailTypeId" id="tempDebitDetails[0].detailTypeId"
                        class="form-control table-input hidden-input debitDetailTypeId" />
                    <form:hidden path="" name="tempDebitDetails[0].detailKeyId" id="tempDebitDetails[0].detailKeyId"
                        class="form-control table-input hidden-input debitDetailKeyId" />
                    <form:hidden path="" name="tempDebitDetails[0].detailTypeName"
                        id="tempDebitDetails[0].detailTypeName"
                        class="form-control table-input hidden-input debitDetailTypeName" />
                    <form:hidden path="" name="tempDebitDetails[0].detailKeyName" id="tempDebitDetails[0].detailKeyName"
                        class="form-control table-input hidden-input debitDetailKeyName" />
                </td>
                <td>
                    <input type="text" id="tempDebitDetails[0].debitAccountHead"
                        name="tempDebitDetails[0].debitAccountHead" class="form-control debitdetailname" disabled>
                </td>
                <td class="text-center"><span style="cursor:pointer;" onclick="addDebitDetailsRow();" tabindex="0"
                        id="tempDebitDetails[0].addButton" data-toggle="tooltip" title=""
                        data-original-title="press SPACE to Add!" aria-hidden="true"><i class="fa fa-plus"></i></span>
                    <span class="add-padding debit-delete-row" onclick="deleteDebitDetailsRow(this);"><i
                            class="fa fa-trash" aria-hidden="true" data-toggle="tooltip" title=""
                            data-original-title="Delete!"></i></span> </td>
            </tr>
        </tbody>
    </table>
</div>