<%--
  ~    eGov  SmartCity eGovernance suite aims to improve the internal efficiency,transparency,
  ~    accountability and the service delivery of the government  organizations.
  ~
  ~     Copyright (C) 2017  eGovernments Foundation
  ~
  ~     The updated version of eGov suite of products as by eGovernments Foundation
  ~     is available at http://www.egovernments.org
  ~
  ~     This program is free software: you can redistribute it and/or modify
  ~     it under the terms of the GNU General Public License as published by
  ~     the Free Software Foundation, either version 3 of the License, or
  ~     any later version.
  ~
  ~     This program is distributed in the hope that it will be useful,
  ~     but WITHOUT ANY WARRANTY; without even the implied warranty of
  ~     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  ~     GNU General Public License for more details.
  ~
  ~     You should have received a copy of the GNU General Public License
  ~     along with this program. If not, see http://www.gnu.org/licenses/ or
  ~     http://www.gnu.org/licenses/gpl.html .
  ~
  ~     In addition to the terms of the GPL license to be adhered to in using this
  ~     program, the following additional terms are to be complied with:
  ~
  ~         1) All versions of this program, verbatim or modified must carry this
  ~            Legal Notice.
  ~            Further, all user interfaces, including but not limited to citizen facing interfaces,
  ~            Urban Local Bodies interfaces, dashboards, mobile applications, of the program and any
  ~            derived works should carry eGovernments Foundation logo on the top right corner.
  ~
  ~            For the logo, please refer http://egovernments.org/html/logo/egov_logo.png.
  ~            For any further queries on attribution, including queries on brand guidelines,
  ~            please contact contact@egovernments.org
  ~
  ~         2) Any misrepresentation of the origin of the material is prohibited. It
  ~            is required that all modified versions of this material be marked in
  ~            reasonable ways as different from the original version.
  ~
  ~         3) This license does not grant any rights to any user of the program
  ~            with regards to rights under trademark law for use of the trade names
  ~            or trademarks of eGovernments Foundation.
  ~
  ~   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
  ~
  --%>


<html>
<%@ include file="/includes/taglibs.jsp"%>
<%@ page language="java"%>

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title><s:text name="budgetload" /></title>

    <!-- Include jQuery (if not already included) -->
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>

    <script type="text/javascript">
        function validate(event) {
            event.preventDefault();
            document.getElementById("msg").innerHTML = "";
            document.getElementById("Errors").innerHTML = "";

            if (document.getElementById("budgetInXls").value == "") {
                bootbox.alert("<s:text name='msg.select.file.to.upload'/>");
                return false;
            }
            document.budgetLoad.action = '/services/EGF/budget/budgetLoad-upload.action';
            jQuery(budgetLoad).append(jQuery('<input>', {
                type: 'hidden',
                name: '${_csrf.parameterName}',
                value: '${_csrf.token}'
            }));
            document.budgetLoad.submit();
            return true;
        }
		
		
        // Function to handle manual budget submission
		function submitManualData(event) {
			event.preventDefault();
			document.getElementById("msg").innerHTML = "";
			document.getElementById("Errors").innerHTML = "";

			var municipalityName = document.getElementById("municipalityName").value;
			var reYear = document.getElementById("reYear").value;
			var beYear = document.getElementById("beYear").value;

			if (municipalityName === "" || reYear === "" || beYear === "") {
				bootbox.alert("<s:text name='msg.all.fields.required'/>");
				return false;
			}

			var budgetRows = [];

			$("#budgetTable tbody tr").each(function () {
				var row = {};
				row["fundCode"] = $(this).find('select[name*="fundCode"]').val();
				row["departmentCode"] = $(this).find('select[name*="departmentCode"]').val();
				row["functionCode"] = $(this).find('select[name*="functionCode"]').val();
				row["chartOfAccountCode"] = $(this).find('select[name*="chartOfAccountCode"]').val();
                // row["maxCode"] = $(this).find('select[name*="maxCode"]').val();
                // row["minCode"] = $(this).find('select[name*="minCode"]').val();
				row["reAmount"] = $(this).find('input[name*="reAmount"]').val();
				row["beAmount"] = $(this).find('input[name*="beAmount"]').val();
				row["planningPercentage"] = $(this).find('input[name*="planningPercentage"]').val();
				budgetRows.push(row);
			});

			if (budgetRows.length === 0) {
				bootbox.alert("<s:text name='msg.no.budget.rows'/>");
				return false;
			}

			// Combine all data into one object
			var fullData = {
				municipalityName: municipalityName,
				reYear: reYear,
				beYear: beYear,
				budgetRows: budgetRows
			};

			// Create and submit form with one hidden input
			var form = $('<form>', {
				//method: 'POST',
				action: '/services/EGF/budget/budgetLoad-manualSubmit.action'
			});

			form.append($('<input>', {
				type: 'hidden',
				name: 'budgetData',
				value: JSON.stringify(fullData) // All data in one variable
			}));

			// CSRF token
			form.append($('<input>', {
				type: 'hidden',
				name: '${_csrf.parameterName}',
				value: '${_csrf.token}'
			}));

			$('body').append(form);
			form.submit();

			return true;
		}


		
        function urlLoad(fileStoreId) {
            var sUrl = "/services/egi/downloadfile?fileStoreId=" + fileStoreId + "&moduleName=EGF";
            window.location = sUrl;
        }

        function showEnterManually() {
            $('#enterManuallySection').show();
            $('#enterManuallyButton').hide();
            $('#orSeparator').hide();
            $('#hideEnterManuallyButton').show();
            $('.upload-section').hide();
        }

        function hideEnterManually() {
            $('#enterManuallySection').hide();
            $('#enterManuallyButton').show();
            $('#orSeparator').show();
            $('#hideEnterManuallyButton').hide();
            $('.upload-section').show();
        }       

        function addNewRow() {
            const currentRows = $('#tableBody tr').length;
            if (currentRows >= 49) {
                bootbox.alert("* Only 50 entries are allowed at a time.");
                return;
            }

            const template = document.getElementById('budgetRowTemplate');
            const clone = template.cloneNode(true);
            clone.removeAttribute('id');
            clone.style.display = ''; // make it visible

            $(clone).find('input, select').each(function () {
                if (this.tagName.toLowerCase() === 'select') {
                    this.selectedIndex = 0;
                } else {
                    this.value = '';
                }
            });

            document.getElementById('tableBody').appendChild(clone);
        }

        function deleteRow(button) {
            $(button).closest('tr').remove();
        }

        jQuery(document).ready(function() {
            $('#beYear').prop('readonly', true);
            $('#reYear').on('input', function () {
                const reYear = $(this).val().trim();
                const regex = /^(\d{4})-(\d{2})$/;
                const match = reYear.match(regex);

                if (match) {
                    const reStartYear = parseInt(match[1].trim(), 10);
                    if (!isNaN(reStartYear)) {
                        const beStartYear = reStartYear + 1;
                        const beEndYear = (beStartYear + 1).toString().slice(-2);  
                        const beYear = beStartYear + '-' + beEndYear;
                        $('#beYear').val(beYear);
                    } else {
                        $('#beYear').val('');
                    }
                } else {
                    $('#beYear').val('');
                }
            });
            $('#enterManuallySection').hide();
            $('#orSeparator').hide();
            $('#hideEnterManuallyButton').hide();
            var fileformats = ['xls'];
            
            jQuery('#budgetInXls').on('change.bs.fileinput', function(e) {
                var myfile = jQuery(this).val();
                var ext = myfile.split('.').pop();
                if (jQuery.inArray(ext, fileformats) > -1) {
                } else {
                    bootbox.alert(ext + "<s:text name='msg.file.format.not.allowed'/>");
                    jQuery(this).val("");
                    return;
                }
            });
        });

        // function onChartOfAccountChange(selectElement) {
        //     const selectedGlcode = selectElement.value; // glcode selected from visible dropdown
        //     const allCoAs = document.getElementById('hiddenChartOfAccounts');

        //     for (let i = 0; i < allCoAs.options.length; i++) {
        //         const option = allCoAs.options[i];
        //         if (option.value === selectedGlcode) {
        //             const majorCode = option.getAttribute("data-majorcode");

        //             // If you want to show only majorCode:
        //             document.getElementById("majorCode").value = majorCode || "";

        //             break;
        //         }
        //     }
        // }

        function onChartOfAccountChange(selectElement) {
            console.log("inside onchangecoa")
            const selectedGlcode = selectElement.value;

            // Get the current row
            const row = selectElement.closest('tr');
            console.log("row",row)

            // Get hidden select and input within the same row
            const allCoAs = row.querySelector('.hiddenChartOfAccounts');
            const majorCodeInput = row.querySelector('.major-code');
            console.log("allCoAs",allCoAs,"majorCodeInput",majorCodeInput)
            if (!allCoAs || !majorCodeInput) {
                console.warn("Missing .hiddenChartOfAccounts or .major-code");
                return;
            }
            for (let i = 0; i < allCoAs.options.length; i++) {
                const option = allCoAs.options[i];
                if (option.value === selectedGlcode) {
                    const majorCode = option.getAttribute("data-majorcode");
                    majorCodeInput.value = majorCode || "";
                    break;
                }
            }
        }


        document.getElementById("reYear").addEventListener("input", function () {
            const input = this.value;
            const pattern = /^\d{4}-\d{2}$/;
            if (!pattern.test(input)) {
                this.setCustomValidity("Format must be YYYY-YY (e.g., 2024-25)");
            } else {
                this.setCustomValidity(""); 
            }
        });


 
        // function calculatePercentage(elem, index) {
        //     var last = parseFloat(document.getElementsByName("budgetData[" + index + "].lastYearApproved")[0]?.value) || 0;
        //     var current = parseFloat(document.getElementsByName("budgetData[" + index + "].currentApproved")[0]?.value) || 0;

        //     var percentageField = document.getElementsByName("budgetData[" + index + "].percentageChange")[0];

        //     if (last !== 0) {
        //         var change = ((current - last) / last) * 100;
        //         percentageField.value = change.toFixed(2);
        //     } else if (current !== 0) {
        //         percentageField.value = "∞";
        //     } else {
        //         percentageField.value = "0.00";
        //     }
        // }

        function calculatePercentage(elem) {
            const row = elem.closest('tr');
            if (!row) return;

            const lastInput = row.querySelector('[name$=".lastYearApproved"]');
            const currentInput = row.querySelector('[name$=".currentApproved"]');
            const percentageInput = row.querySelector('[name$=".percentageChange"]');

            const last = parseFloat(lastInput?.value) || 0;
            const current = parseFloat(currentInput?.value) || 0;

            if (!percentageInput) return;

            if (last !== 0) {
                const change = ((current - last) / last) * 100;
                percentageInput.value = change.toFixed(2);
            } else if (current !== 0) {
                percentageInput.value = "∞";
            } else {
                percentageInput.value = "0.00";
            }
        }



    </script>
</head>

<body>
    <s:form action="budgetLoad" theme="css_xhtml" name="budgetLoad" id="budgetLoad" enctype="multipart/form-data" method="post">
        <div class="formmainbox">
            <div class="formheading"></div>
            <div class="subheadnew">
                <s:text name="budgetload" />
            </div>

            <div align="center">
                <font style='color: red;'>
                    <div id="msg">
                        <s:property value="message" />
                    </div>
                    <p class="error-block" id="lblError"></p>
                </font>
            </div>
            <span class="mandatory1">
                <div id="Errors">
                    <s:actionerror />
                    <s:fielderror />
                </div> <s:actionmessage />
            </span>

            <!-- Upload Excel Section -->
            <div class="upload-section">
                <center>
                    <table border="0" width="100%" cellspacing="0" cellpadding="0">
                        <tr>
                            <th width="50%"></th>
                            <th width="25%" class="bluebgheadtd" style="text-align: center"><s:text name="lbl.original.files"/> </th>
                            <th width="25%" class="bluebgheadtd" style="text-align: center"><s:text name="lbl.output.files"/></th>
                        </tr>
                        <tr>
                            <td>
                                <table border="0" width="100%" cellspacing="0" cellpadding="0">
                                    <tr>
                                        <td class="greybox" width="20%"></td>
                                        <td class="greybox"></td>
                                        <td class="greybox"><s:text name="budgetupload" /> <span class="mandatory1">*</span></td>
                                        <td class="greybox"><s:file name="budgetInXls" id="budgetInXls" /></td>
                                    </tr>
                                </table>
                            </td>
                            <td>
                                <table border="1" width="100%" cellspacing="0" cellpadding="0">
                                    <s:iterator var="scheme" value="originalFiles">
                                        <tr>
                                            <td align="center"><a href="#" onclick="urlLoad('<s:property value="%{fileStoreId}" />');"><s:label value="%{fileName}" /></a></td>
                                        </tr>
                                    </s:iterator>
                                </table>
                            </td>
                            <td>
                                <table border="1" width="100%" cellspacing="0" cellpadding="0">
                                    <s:iterator var="scheme" value="outPutFiles">
                                        <tr>
                                            <td align="center"><a href="#" onclick="urlLoad('<s:property value="%{fileStoreId}" />');"><s:label value="%{fileName}" /></a></td>
                                        </tr>
                                    </s:iterator>
                                </table>
                            </td>
                        </tr>
                        <tr>
                            <td class="greybox" colspan="2"><a href="/services/EGF/resources/app/formats/Budget_Data_Template.xls"><s:text name="lbl.download.template"/></a></td>
                        </tr>
                    </table>

                    <div class="text-left error-msg" style="color: #C00000">&nbsp;* <s:text name="msg.only.xls.files.supprted"/>.</div>

                    <div class="buttonbottom" id="buttondiv">
                        <table>
                            <tr>
                                <td><s:submit type="submit" cssClass="buttonsubmit" key="lbl.upload.budget" name="upload" method="upload" onclick="return validate(event);" /></td>
                                <td><input type="button" value="<s:text name='lbl.close'/>" onclick="javascript:window.close()" class="buttonsubmit" /></td>
                            </tr>
                        </table>
                    </div>
            </div>
                </center>				

            

            <!-- Enter Manually Section -->
            <div id="enterManuallySection" style="display:none;">
                <!-- <center> -->
                    <div style="border: 2px solid #f3f0f0;  padding: 20px; margin-bottom: 20px; text-align: left;" >
                        <div class="form-group" style="text-align: left;">
                            <label for="municipalityName" >Name of Municipality</label><br>
                            <input type="text" id="municipalityName" name="municipalityName" class="form-control" style="width:500px;" />
                        </div>
                        <div class="form-group" style="text-align: left; margin-top: 10px;">
                            <label for="reYear">RE Year (YYYY-YY)</label><br>
                            <input type="text" id="reYear" name="reYear" class="form-control" 
                                   style="width: 200px;" 
                                   pattern="^\d{4}-\d{2}$"
                                   title="Enter year in YYYY-YY format (e.g., 2024-25)" 
                                   required />
                        </div>                        
                        <div class="form-group" style="text-align: left; margin-top: 10px;">
                            <label for="beYear" >BE Year(YYYY-YY)</label><br>
                            <input type="text" id="beYear" name="beYear" class="form-control" style="width: 200px;" readonly/>
                        </div>                        
                    </div>

                    <!-- BOX 2: Table and Add Row Button -->
                    <div style="border: 2px solid #fff; padding: 20px;">
                        <!-- Your existing table -->
                        <table border="1" width="80%" cellspacing="0" cellpadding="5" id="budgetTable">
                            <thead>
                                <tr>
                                    <th style="text-align:center; background-color: #f2851f; border-radius: 5px; color: #ffffff; width: 100px;">Fund </th>
                                    <th style="text-align:center; background-color: #f2851f; border-radius: 5px; color: #ffffff; width: 100px;">Department </th>
                                    <th style="text-align:center; background-color: #f2851f; border-radius: 5px; color: #ffffff; width: 100px;">Function </th>
                                    <th style="text-align:center; background-color: #f2851f; border-radius: 5px; color: #ffffff; width: 100px;" >Chart of Account </th>
                                    <th style="text-align:center; background-color: #f2851f; border-radius: 5px; color: #ffffff; width: 100px;" >Major Code </th>
                                    <th style="text-align:center; background-color: #f2851f; border-radius: 5px;color: #ffffff; width: 100px;">RE Amount (Rs)</th>
                                    <th style="text-align:center;background-color: #f2851f; border-radius: 5px;color: #ffffff; width: 100px;">BE Amount (Rs)</th>
                                    <th style="text-align:center;background-color: #f2851f; border-radius: 5px;color: #ffffff; width: 100px;">Last Year Approved Budget</th>
                                    <th style="text-align:center;background-color: #f2851f; border-radius: 5px;color: #ffffff; width: 100px;">Current Approved Budget</th>
                                    <th style="text-align:center;background-color: #f2851f; border-radius: 5px;color: #ffffff; width: 100px;">% Increase / Decrease in Budget Head</th>
                                    <th style="text-align:center;background-color: #f2851f; border-radius: 5px;color: #ffffff; width: 100px;">Planning Percentage</th>
                                    <th style="text-align:center;background-color: #f2851f; border-radius: 5px;color: #ffffff; width: 100px;">Action</th>
                                </tr>
                            </thead>				
                            
                            <tbody>
                                <tr id="budgetRowTemplate">
                                    <td >
                                    <s:select 
                                        list="%{dropdownData.fundList != null && !dropdownData.fundList.isEmpty() ? dropdownData.fundList : {}}" 
                                        listKey="code" 
                                        listValue="name"
                                        name="budgetData[new].fundCode"
                                        headerKey="" 
                                        headerValue="%{getText('lbl.choose.options')}" 
                                        class="form-control"
                                        style="width: 100px;"
                                    />                                      
                                    </td>                               
                                    <td>
                                        <s:select 
                                            list="%{dropdownData.executingDepartmentList != null && !dropdownData.executingDepartmentList.isEmpty() ? dropdownData.executingDepartmentList : {}}" 
                                            listKey="code" 
                                            listValue="name"
                                            name="budgetData[new].departmentCode"
                                            headerKey="" 
                                            headerValue="%{getText('lbl.choose.options')}"
                                            class="form-control" 
                                            style="width: 100px;"
                                        />
                                    </td>
                                    <td>
                                        <s:select 
                                            list="%{dropdownData.functionList != null && !dropdownData.functionList.isEmpty() ? dropdownData.functionList : {}}" 
                                            listKey="code" 
                                            listValue="name"
                                            name="budgetData[new].functionCode"
                                            headerKey="" 
                                            headerValue="%{getText('lbl.choose.options')}"
                                            class="form-control" 
                                            style="width: 100px;"
                                        />
                                    </td>                                
                                    <!-- <td>
                                        <s:select list="dropdownData.chartOfAccountList"
                                            listKey="glcode"
                                            listValue="%{glcode + ' - ' + name}"
                                            name="budgetData[new].chartOfAccountCode"
                                            headerKey="0"
                                            headerValue="%{getText('lbl.choose.options')}"
                                            id="budgetDetail_chartOfAccount"
                                            onchange="onChartOfAccountChange(this)"
                                            class="form-control"
                                            style="width: 100px;"
                                            />

                                            <select id="hiddenChartOfAccounts" style="display:none;">
                                                <c:forEach var="coa" items="${dropdownData.chartOfAccountList}">
                                                    <option value="${coa.glcode}"
                                                            data-majorcode="${coa.majorCode}">
                                                        ${coa.glcode}
                                                    </option>
                                                </c:forEach>
                                            </select>                                                                                   
                                                                                                      
                                    </td>
                                    <td><input type="text" id="majorCode" name="majorCode" class="form-control" style="width: 100px;" readonly /></td>  -->
                                    <td>
                                        <s:select list="dropdownData.chartOfAccountList"
                                            listKey="glcode"
                                            listValue="%{glcode + ' - ' + name}"
                                            name="budgetData[new].chartOfAccountCode"
                                            headerKey="0"
                                            headerValue="%{getText('lbl.choose.options')}"
                                            class="form-control"
                                            style="width: 100px;"
                                            onchange="onChartOfAccountChange(this)"
                                        />                                    
                                        <select class="hiddenChartOfAccounts" style="display:none;">
                                            <c:forEach var="coa" items="${dropdownData.chartOfAccountList}">
                                                <option value="${coa.glcode}" data-majorcode="${coa.majorCode}">
                                                    ${coa.glcode}
                                                </option>
                                            </c:forEach>
                                        </select>      
                                    </td>                              
                                    <td>
                                        <input type="text" id="majorCode" name="majorCode" class="form-control major-code" style="width: 100px;" readonly />
                                      </td>                                      
                                    <td>
                                        <s:textfield 
                                            name="budgetData[new].reAmount" 
                                            oninput="this.value=this.value.replace(/[^0-9]/g,'')" 
                                            class="form-control"
                                            style="width: 100px;"
                                        />
                                    <td>
                                            <s:textfield 
                                                name="budgetData[new].beAmount" 
                                                oninput="this.value=this.value.replace(/[^0-9]/g,'')" 
                                                class="form-control" 
                                                style="width: 100px;"
                                            />
                                    </td>
                                    <td><s:textfield name="budgetData[new].lastYearApproved"  class="form-control" style="width: 100px;" onblur="calculatePercentage(this)" /></td>
                                    <!-- <td><s:textfield name="budgetData[new].currentApproved"  class="form-control"  style="width: 100px;" onblur="calculatePercentage(this,'new')" /></td> -->
                                    <td><s:textfield name="budgetData[new].currentApproved" class="form-control" onblur="calculatePercentage(this)" /></td>
                                    <td><s:textfield name="budgetData[new].percentageChange"  class="form-control" style="width: 100px;" /></td>
                                    <td>
                                        <s:textfield 
                                            name="budgetData[new].planningPercentage"
                                            oninput="this.value=this.value.replace(/[^0-9]/g,'')" 
                                            class="form-control"
                                            style="width: 100px;"
                                        />
                                    </td>
                                    <td>
                                        <button type="button" class="btn btn-sm" style="background-color: #fe7a51; color: #ffffff; margin-left:5px ;" onclick="deleteRow(this)">Delete</button>
                                    </td>
                                </tr>
                            </tbody>
                            
                            <tbody id="tableBody">
                                <!-- rows will be dynamically added here -->
                            </tbody>
                            
                        </table>

                        <!-- Add row button -->
                        <div style="margin-top: 10px;">
                            <button type="button" class="buttonsubmit" onclick="addNewRow()">Add New Entry</button>
                        </div>

                        <!-- Limit note -->
                        <div style="color: red; margin-top: 15px; font-size: 10px;">* Only 50 entries are allowed at a time.</div>
                    </div>

                    <div class="buttonbottom" id="buttondiv">
                        <table>
                            <tr>
                                <td><s:submit type="submit" cssClass="buttonsubmit" key="lbl.submit" name="upload" method="upload" onclick="return submitManualData(event);" /></td>
                                <!-- <td><input type="button" value="<s:text name='lbl.close'/>" onclick="javascript:window.close()" class="buttonsubmit" /></td> -->
                                <td><input type="button" value="Close" onclick="window.history.back();" class="buttonsubmit"/></td>
                            </tr>
                        </table>
                    </div>					
                <!-- </center> -->
            </div>
			
			<!-- OR Separator -->
            <center>
                <div id="orSeparator2" style="margin-top: 20px; text-align: center;">
                    <hr />
                    <span><strong>OR</strong></span>
                </div>
            </center>

            <!-- Hide Enter Manually Section Button -->
            <center>
                <button type="button" id="hideEnterManuallyButton" onclick="hideEnterManually()" class="btn btn-secondary" style="display:none;">Upload Budget File</button>
            </center>

            <!-- Enter Manually Button -->
            <center>
                <button type="button" id="enterManuallyButton" onclick="showEnterManually()" class="btn btn-primary">Enter Manually</button>
            </center>

		</div>
		</div>

        </div>

    </s:form>
</body>
</html>
