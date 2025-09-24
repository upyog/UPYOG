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
        window.onload = function () {
            // Clone the hidden template
            const template = document.getElementById('budgetRowTemplate');
            const firstRow = template.cloneNode(true);
            firstRow.removeAttribute('id');
            firstRow.style.display = '';

            // Disable delete button
            const deleteBtn = firstRow.querySelector('.delete-button');
            if (deleteBtn) {
                deleteBtn.disabled = true;
                deleteBtn.style.opacity = 0.5;
                deleteBtn.style.cursor = "not-allowed";
            }

            // Add the first row to the table body
            document.getElementById('tableBody').appendChild(firstRow);
        };
        
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

        function populateMunicipality() {
            try {
                const tenantId = localStorage.getItem("tenant-id");
                const empTenantId = localStorage.getItem("employee-tenant-id");
                const municipalityName = empTenantId || tenantId || "";

                if (municipalityName) {
                    const municipalityField = document.getElementById("municipalityName");
                    if (municipalityField) {
                        municipalityField.value = municipalityName;
                        municipalityField.readOnly = true;   
                        municipalityField.style.background = "#edeaea"; 
                        municipalityField.style.cursor = "not-allowed";
                    }
                }
            } catch (err) {
                console.error("Error populating Municipality Name:", err);
            }
        }
		
        // Function to handle manual budget submission
		function submitManualData(event) {
            event.preventDefault(); // Stop default form submission
            
            let isValid = true;
            let message = "";
            document.getElementById("msg").innerHTML = "";
            document.getElementById("Errors").innerHTML = "";

            const municipalityName = document.getElementById("municipalityName").value;
            const reYear = document.getElementById("reYear").value;
            const beYear = document.getElementById("beYear").value;

            if (!municipalityName || !reYear || !beYear) {
                bootbox.alert("Please fill all the Municipality details (Name, RE Year, BE Year).");
                return false;
            }

            const budgetRows = [];
            let rowIndex = 0;

            jQuery("#budgetTable tbody tr").each(function () {
                const $row = jQuery(this);

                // Skip the hidden template row
                if ($row.attr("id") === "budgetRowTemplate") return;
                

                const fundCode = $row.find('select[name*="fundCode"]').val();
                const departmentCode = $row.find('select[name*="departmentCode"]').val();
                const functionCode = $row.find('select[name*="functionCode"]').val();
                const chartOfAccountCode = $row.find('select[name*="chartOfAccountCode"]').val();
                const majorCode = $row.find('input[name*="majorCode"]').val();
                const minorCode = $row.find('input[name*="minorCode"]').val();
                const reAmount = $row.find('input[name*="reAmount"]').val();
                const beAmount = $row.find('input[name*="beAmount"]').val();
                const lastYearApproved = $row.find('input[name*="lastYearApproved"]').val();
                const currentApproved = $row.find('input[name*="currentApproved"]').val();
                const percentageChange = $row.find('input[name*="percentageChange"]').val();
                const planningPercentage = $row.find('input[name*="planningPercentage"]').val();

                // Skip if all fields are empty (row not filled by user)
                if (
                    !fundCode && !departmentCode && !functionCode &&
                    (!chartOfAccountCode || chartOfAccountCode === "0") &&
                    !majorCode && !minorCode &&
                    !reAmount && !beAmount &&
                    !lastYearApproved && !currentApproved &&
                    !percentageChange && !planningPercentage
                ) {
                    return;
                }

                //  Validate required fields
                if (
                    !fundCode || !departmentCode || !functionCode || chartOfAccountCode === "0" ||
                    !majorCode || !minorCode || !reAmount || !beAmount ||
                    !lastYearApproved || !currentApproved || !percentageChange || !planningPercentage
                ) {
                    isValid = false;
                    message = `Please fill all fields in the rows .`;
                    return false; // Break .each loop
                }

                const row = {
                    fundCode,
                    departmentCode,
                    functionCode,
                    chartOfAccountCode,
                    majorCode,
                    minorCode,
                    reAmount,
                    beAmount,
                    lastYearApproved,
                    currentApproved,
                    percentageChange,
                    planningPercentage
                };

                budgetRows.push(row);
                rowIndex++;
            });

            if (!isValid) {
                bootbox.alert(message);
                return false;
            }

            if (budgetRows.length === 0) {
                bootbox.alert("No budget rows found. Please add at least one entry.");
                return false;
            }

            // ✅ Create final object
            const fullData = {
                municipalityName: municipalityName,
                reYear: reYear,
                beYear: beYear,
                budgetRows: budgetRows
            };

            // ✅ Create and submit the dynamic form
            const form = $('<form>', {
                action: '/services/EGF/budget/budgetLoad-manualSubmit.action',
                method: 'POST'
            });

            form.append($('<input>', {
                type: 'hidden',
                name: 'budgetData',
                value: JSON.stringify(fullData)
            }));

            // CSRF token handling
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
            if (currentRows >= 50) {
                bootbox.alert("* Only 50 entries are allowed at a time.");
                return;
            }

            const template = document.getElementById('budgetRowTemplate');
            const clone = template.cloneNode(true);
            clone.removeAttribute('id');
            clone.style.display = '';

            $(clone).find('input, select').each(function () {
                if (this.tagName.toLowerCase() === 'select') {
                    this.selectedIndex = 0;
                } else {
                    this.value = '';
                }
            });

            // Enable delete button
            const deleteBtn = $(clone).find('.delete-button');
            deleteBtn.prop('disabled', false)
                    .css({ opacity: 1, cursor: 'pointer' });

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
            populateMunicipality();
        });


        function onChartOfAccountChange(selectElement) {
            const selectedGlcode = selectElement.value;            
            // Get the current row
            const row = selectElement.closest('tr');            
            // Get hidden select and inputs within the same row
            const allCoAs = row.querySelector('.hiddenChartOfAccounts');
            const majorCodeInput = row.querySelector('.major-code');
            const minorCodeInput = row.querySelector("input[name$='minorCode']");

            if (!allCoAs || !majorCodeInput) {
                return;
            }
            for (let i = 0; i < allCoAs.options.length; i++) {
                const option = allCoAs.options[i];
                if (option.value === selectedGlcode) {
                    const majorCode = option.getAttribute("data-majorcode");
                    majorCodeInput.value = majorCode || "";

                    // Populate Minor Code by extracting 4th and 5th characters
                    if (selectedGlcode.length >= 5 && minorCodeInput) {
                        const minorCode = selectedGlcode.substring(3, 5); // index 3 and 4
                        minorCodeInput.value = minorCode;
                    }
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
                //percentageInput.value = change.toFixed(2);
                percentageInput.value = Math.round(change);  // No decimal points
            } else if (current !== 0) {
                percentageInput.value = "∞";
            } else {
                percentageInput.value = "0";
            }
        }

        function validateReYear(input) {
			const errorMsg = document.getElementById('reYearError');
			const submitBtn = document.getElementById('submitBtn');
			const value = input.value.trim();

			const regex = /^\d{4}-\d{2}$/;

			if (!regex.test(value)) {
				errorMsg.textContent = "Format should be YYYY-YY (e.g., 2024-25)";
				errorMsg.style.display = "block";
				input.setCustomValidity("Invalid format");
				submitBtn.disabled = true;
				return;
			}

			const parts = value.split("-");
			const startYear = parseInt(parts[0], 10);
			const endYearShort = parseInt(parts[1], 10);
			const expectedEndYearShort = (startYear + 1) % 100;

			if (endYearShort !== expectedEndYearShort) {
				errorMsg.textContent = "Please provide valid Financial Year e.g. 2024-25";
				errorMsg.style.display = "block";
				input.setCustomValidity("Year range invalid");
				submitBtn.disabled = true;
			} else {
				errorMsg.textContent = "";
				errorMsg.style.display = "none";
				input.setCustomValidity("");
				submitBtn.disabled = false;
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
                                <!-- <td><input type="button" value="<s:text name='lbl.close'/>" onclick="javascript:window.close()" class="buttonsubmit" /></td> -->
                                <td><input type="button" value="Close" onclick="window.location.reload();" class="buttonsubmit" /></td>	
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
                            <input type="text"
                                   id="reYear"
                                   name="reYear"
                                   class="form-control"
                                   style="width: 200px;"
                                   oninput="validateReYear(this)"  
                                   required />
                            <small id="reYearError" style="color: red; display: none;"></small>
                        </div>                                                              
                        <div class="form-group" style="text-align: left; margin-top: 10px;">
                            <label for="beYear" >BE Year(YYYY-YY)</label><br>
                            <input type="text" id="beYear" name="beYear" class="form-control" style="width: 200px; background: #edeaea;" readonly/>
                        </div>                        
                    </div>

                    <!-- BOX 2: Table and Add Row Button -->
                    <div style= "width: 100%; overflow-x: auto; border: 2px solid #fff; padding: 20px;">
                        <!-- Your existing table -->
                        <table border="1" cellspacing="0" cellpadding="5" id="budgetTable" style="width: 100%; border-collapse: collapse; font-family: Arial, sans-serif; font-size: 14px; box-shadow: 0 2px 10px rgba(0,0,0,0.1);">
                            <thead>
                                <tr>
                                    <th style="background-color: #f2851f; color: #fff; padding: 10px; text-align: center; border: 1px solid #ddd;">Fund</th>
                                    <th style="background-color: #f2851f; color: #fff; padding: 10px; text-align: center; border: 1px solid #ddd;">Department </th>
                                    <th style="background-color: #f2851f; color: #fff; padding: 10px; text-align: center; border: 1px solid #ddd;">Function </th>
                                    <th style="background-color: #f2851f; color: #fff; padding: 10px; text-align: center; border: 1px solid #ddd;" >Chart of Account </th>
                                    <th style="background-color: #f2851f; color: #fff; padding: 10px; text-align: center; border: 1px solid #ddd;" >Major Code </th>
                                    <th style="background-color: #f2851f; color: #fff; padding: 10px; text-align: center; border: 1px solid #ddd;" >Minor Code </th>
                                    <th style="background-color: #f2851f; color: #fff; padding: 10px; text-align: center; border: 1px solid #ddd;">RE Amount (Rs)</th>
                                    <th style="background-color: #f2851f; color: #fff; padding: 10px; text-align: center; border: 1px solid #ddd;">BE Amount (Rs)</th>
                                    <th style="background-color: #f2851f; color: #fff; padding: 10px; text-align: center; border: 1px solid #ddd;">Last Year Approved Budget</th>
                                    <th style="background-color: #f2851f; color: #fff; padding: 10px; text-align: center; border: 1px solid #ddd;">Current Year Approved Budget</th>
                                    <th style="background-color: #f2851f; color: #fff; padding: 10px; text-align: center; border: 1px solid #ddd;">% Increase / Decrease in Budget Head</th>
                                    <th style="background-color: #f2851f; color: #fff; padding: 10px; text-align: center; border: 1px solid #ddd;">Planning Percentage</th>
                                    <th style="background-color: #f2851f; color: #fff; padding: 10px; text-align: center; border: 1px solid #ddd;">Action</th>
                                </tr>
                            </thead>				
                            
                            <tbody>
                                <tr id="budgetRowTemplate" style="display: none;">
                                    <td style="padding: 8px; border: 1px solid #ddd; text-align: center;">
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
                                    <td style="padding: 8px; border: 1px solid #ddd; text-align: center;">
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
                                    <td style="padding: 8px; border: 1px solid #ddd; text-align: center;">
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
                                    <td style="padding: 8px; border: 1px solid #ddd; text-align: center;">
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
                                    <td style="padding: 8px; border: 1px solid #ddd; text-align: center;">
                                        <input type="text" id="majorCode" name="budgetData[new].majorCode" class="form-control major-code" style="width: 100px; background:#edeaea;" readonly />
                                    </td>       
                                    <td style="padding: 8px; border: 1px solid #ddd; text-align: center;">
                                        <s:textfield 
                                            name="budgetData[new].minorCode"
                                            class="form-control"
                                            style="width: 100px; background: #edeaea;"
                                            readonly="true"
                                        />
                                    </td>                             
                                    <td style="padding: 8px; border: 1px solid #ddd; text-align: center;">
                                        <s:textfield 
                                            name="budgetData[new].reAmount" 
                                            oninput="this.value=this.value.replace(/[^0-9]/g,'')" 
                                            class="form-control"
                                            style="width: 100px;"
                                        />
                                    </td>
                                    <td style="padding: 8px; border: 1px solid #ddd; text-align: center;">
                                            <s:textfield 
                                                name="budgetData[new].beAmount" 
                                                oninput="this.value=this.value.replace(/[^0-9]/g,'')" 
                                                class="form-control" 
                                                style="width: 100px;"
                                            />
                                    </td>
                                    <td style="padding: 8px; border: 1px solid #ddd; text-align: center;"><s:textfield name="budgetData[new].lastYearApproved"  class="form-control" style="width: 100px;" onblur="calculatePercentage(this)" /></td>
                                    <td style="padding: 8px; border: 1px solid #ddd; text-align: center;"><s:textfield name="budgetData[new].currentApproved" class="form-control" onblur="calculatePercentage(this)" /></td>
                                    <td style="padding: 8px; border: 1px solid #ddd; text-align: center;"><s:textfield name="budgetData[new].percentageChange"  class="form-control" style="width: 100px; background: #edeaea" readonly="true"/></td>
                                    <td style="padding: 8px; border: 1px solid #ddd; text-align: center;">
                                        <s:textfield 
                                            name="budgetData[new].planningPercentage"
                                            oninput="this.value=this.value.replace(/[^0-9]/g,'')" 
                                            class="form-control"
                                            style="width: 100px;"
                                        />
                                    </td>
                                    <td style="padding: 8px; border: 1px solid #ddd; text-align: center;">
                                        <button type="button" class="btn btn-sm delete-button" style="background-color: #fe7a51; color: #ffffff; margin-left:5px ;" onclick="deleteRow(this)">Delete</button>
                                    </td>
                                </tr>
                            </tbody>
                            
                            <tbody id="tableBody">
                                <!-- rows will be dynamically added here -->
                            </tbody>                            
                        </table>

                        <!-- Add row button -->
                        <div style="margin-top: 10px;">
                            <button type="button" onclick="addNewRow()"
                                style="background-color:#f88865; 
                                       color: white; 
                                       border: none; 
                                       padding: 6px 16px; 
                                       font-size: 12px; 
                                       margin-top: 10px;
                                       border-radius: 4px; 
                                       cursor: pointer; 
                                       box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
                                       transition: background-color 0.2s ease-in-out;"
                                onmouseover="this.style.backgroundColor='#e65c2c';"
                                onmouseout="this.style.backgroundColor='#f76c3c';">
                                Add New Entry
                            </button>
                        </div>                        

                        <!-- Limit note -->
                        <div style="color: red; margin-top: 15px; font-size: 10px;">* Only 50 entries are allowed at a time.</div>
                    </div>

                    <div class="buttonbottom" id="buttondiv">
                        <table>
                            <tr>
                                <td><s:submit type="submit" cssClass="buttonsubmit" key="lbl.submit" name="upload" method="upload" onclick="return submitManualData(event);" id="submitBtn" /></td>
                                <!-- <td><input type="button" value="<s:text name='lbl.close'/>" onclick="javascript:window.close()" class="buttonsubmit" /></td> -->
                                <!-- <td><input type="button" value="Close" onclick="window.history.back();" class="buttonsubmit"/></td> -->
                                <td><input type="button" value="Close" onclick="window.location.reload();" class="buttonsubmit" /></td>	                             
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
                <button type="button" id="hideEnterManuallyButton" onclick="hideEnterManually()" style="font-size: 12px;" class="btn btn-secondary" style="display:none;">Upload Budget File</button>
            </center>

            <!-- Enter Manually Button -->
            <center>
                <button type="button" id="enterManuallyButton" onclick="showEnterManually()" style="font-size: 12px;" class="btn btn-primary">Enter Manually</button>
            </center>

		</div>
		</div>

        </div>

    </s:form>
</body>
</html>
