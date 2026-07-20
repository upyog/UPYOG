<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ include file="/includes/taglibs.jsp"%>
<script src="https://cdnjs.cloudflare.com/ajax/libs/xlsx/0.18.5/xlsx.full.min.js"></script>
<form:form role="form" modelAttribute="accountEntity" action="createFromUpload" id="accountEntityForm"
           class="form-horizontal form-groups-bordered"  method="post">

    <div class="row">
        <div class="col-md-12">
            <div class="panel panel-primary" data-collapsed="0">
                <div class="panel-heading">
                    <div class="panel-title"><spring:message code="lbl.user.defined.code" text=" User Defined Code"/></div>
                </div>
                <div class="panel-body">
                    <div class="form-group">
                        <label class="col-sm-3 control-label text-right"><spring:message
                                code="lbl.accountdetailtype" text="Account detail type"/> <span class="mandatory"></span>
                        </label>
                        <div class="col-sm-3 add-margin">
                            <form:select path="accountdetailtype" id="accountdetailtype"
                                         cssClass="form-control" cssErrorClass="form-control error"
                                         required="required">
                                <form:option value="">
                                    <spring:message code="lbl.select" text="Select"/>
                                </form:option>
                                <form:options items="${accountdetailtypes}" itemValue="id"
                                              itemLabel="name" />
                            </form:select>
                            <form:errors path="accountdetailtype" cssClass="error-msg" />
                        </div>

                        <input type="hidden" name="accountEntities" id="accountEntities" />



                    </div>

                    <div class="col-sm-3 add-margin">

                        <s:file name="accountEntityFile"
                                id="accountEntityFile" />
                        <span id="fileError"></span>

                    </div>
                </div>
            </div>
        </div>
    </div>
</form:form>

<div class="text-center">
    <button type='submit' class='btn btn-primary' id="buttonSubmit" onclick="return validateForm();" >
        <spring:message code='lbl.create' text="Create"/>
    </button>
    <a href='javascript:void(0)' class='btn btn-default' onclick="javascript:window.parent.postMessage('close','*');"><spring:message code='lbl.close' text="Close"/></a>
</div>

<script>

</script>

<script>


   function validateForm() {

    const fileInput = document.getElementById("accountEntityFile");
    const file = fileInput.files[0];

    const fileError = document.getElementById("fileError");

    if (!file) {
    fileError.innerHTML = "Upload a valid XLX file."
    return;
    }

    const fileName = file.name;
      const validExtensions = [".xlsx", ".xls"];
      const isValid = validExtensions.some(ext => fileName.endsWith(ext));

      if (!isValid) {
        fileError.innerHTML = "Invalid file format. Please upload an Excel file (.xlsx or .xls)";
        return;
      }

      const reader = new FileReader();

      reader.onload = function (e) {
        const data = new Uint8Array(e.target.result);
        const workbook = XLSX.read(data, { type: "array" });

        const sheetName = workbook.SheetNames[0];
        const sheet = workbook.Sheets[sheetName];
        const json = XLSX.utils.sheet_to_json(sheet, { raw: true, defval:"", header: ['Sr No*', 'Account detail type*', 'Name*', 'Code*','Narration'] });

        const rawJson = JSON.stringify(json);

        console.log("Excel Data:", JSON.stringify(json));
        alert("File is valid and parsed. Check console for data.");

        document.getElementById("accountEntities").value = rawJson;

        document.getElementById("accountEntityForm").submit();

      };

      reader.readAsArrayBuffer(file);


    }


</script>