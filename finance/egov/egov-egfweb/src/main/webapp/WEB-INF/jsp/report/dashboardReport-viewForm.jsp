<%@ taglib prefix="s" uri="/WEB-INF/tags/struts-tags.tld"%>
<div xmlns:s="http://www.w3.org/1999/XSL/Transform">
    <h3>Dashboard</h3>


    <form id="dashboardReportForm"  action="/services/EGF/report/dashboardReport-viewFilteredReport.action" method="get" >


        <div class="error-block" style="color: red; align: left">
            <s:actionerror />
        </div>


        <div class="" style="margin-top: 20px;">
            <div class="row">
                <!-- Start Date -->
                <div class="col-md-6">
                    <label for="startDate"><s:text name="Start Date" /> <span class="mandatory1">*</span></label>
                    <s:date name="startDate" format="dd/MM/yyyy" var="tempStartDate" />
                    <s:textfield
                            id="startDate"
                            name="startDate"
                            value="%{tempStartDate}"
                            data-date-end-date="0d"
                            onkeyup="DateFormat(this,this.value,event,false,'3')"
                            placeholder="DD/MM/YYYY"
                            cssClass="form-control datepicker"
                            data-inputmask="'mask': 'd/m/y'"
                            autocomplete="off"
                    />
                    <s:fielderror fieldName="startDate" />
                </div>

                <!-- End Date -->
                <div class="col-md-6">
                    <label for="endDate"><s:text name="End Date" /> <span class="mandatory1">*</span></label>
                    <s:date name="endDate" format="dd/MM/yyyy" var="tempEndDate" />
                    <s:textfield
                            id="endDate"
                            name="endDate"
                            value="%{tempEndDate}"
                            data-date-end-date="0d"
                            onkeyup="DateFormat(this,this.value,event,false,'3')"
                            placeholder="DD/MM/YYYY"
                            cssClass="form-control datepicker"
                            data-inputmask="'mask': 'd/m/y'"
                            autocomplete="off"
                    />
                    <s:fielderror fieldName="endDate" />
                </div>
            </div>
        </div>


        <div style="display:table; width:200px; table-layout:fixed; margin: 0 auto; padding-top: 20px;">
            <div style="display:table-cell; width:50%; border-radius:0; padding-right:10px;">
                <s:submit key="lbl.search"
                          onclick="return validateAndSubmit()"
                          cssClass="btn btn-primary"
                          style="width: 100%;"
                />
            </div>

            <div style="display:table-cell; width:50%; border-radius:0; padding-left:10px;" >
                <input type="button"
                       value='<s:text name="lbl.close"/>'
                       onclick="javascript:window.parent.postMessage('close','*');"
                       class="btn btn-default"
                style="width: 100%;"
                />
            </div>

        </div>


        <div style="display:table; width:200px; table-layout:fixed; margin: 0 auto; padding-top: 20px;">

            <div style="display:table-cell; width:50%; border-radius:0; margin:0;" >
                <input type="button"
                       value="View Current Financial Year"
                       onclick="viewCurrentFy()"
                       class="btn btn-primary"
                />
            </div>

        </div>


    </form>

    <div id="report" >

    </div>

    <!--    <s:iterator value="contractors" var="contractor">-->
    <!--        <p>Contractor Name: <s:property value="name" /><
        },
        {/p>-->
    <!--    </s:iterator>-->


</div>

<script>

    function validateAndSubmit() {


    var startDateInput = document.getElementById("startDate");
    var endDateInput = document.getElementById("endDate");

    const startDate = new Date(startDateInput.value + 'T00:00:00');
    const endDate = new Date(endDateInput.value + 'T00:00:00');

    // Check if fields are empty
    if (!startDateInput.value || !endDateInput.value) {
        alert("Please select both Start Date and End Date.");
        return false;
    }

    // Check if Start Date is after End Date
    if (startDate > endDate) {
        alert("Start Date cannot be after End Date.");
        return false;
    }



    return true;

}


    function viewCurrentFy() {

    document.getElementById('dashboardReportForm').action = "/services/EGF/report/dashboardReport-viewReport.action"

    document.getElementById('dashboardReportForm').submit();

    }


</script>
