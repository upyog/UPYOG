function switchTab(tab, e) {

    document.querySelectorAll('.tab')
        .forEach(t => t.classList.remove('active'));

    document.querySelectorAll('.tab-content')
        .forEach(c => c.classList.remove('active'));

    e.target.classList.add('active');

    document.getElementById(tab)
        .classList.add('active');
}

// =========================
// EXECUTION PROGRESS
// =========================

let progressInterval = null;

function startProgressPolling() {
    if (progressInterval) {
        clearInterval(progressInterval);
    }

    const progressDiv = document.getElementById("executionProgress");
    if (progressDiv) progressDiv.style.display = "block";

    progressInterval = setInterval(async function () {
        try {
            const response = await fetch("/api/module/progress");
            if (!response.ok) {
                return;
            }

            const progress = await response.json();
            const total = progress.totalTestCases || 0;
            const completed = progress.completedTestCases || 0;
            const currentTest = progress.currentTestCase || "";
            const currentMod = progress.currentModule || "";
            const isRunning = progress.executionRunning;
            const executionMode = progress.executionMode || "STANDARD";
            const sourceName = progress.sourceName || "";

            const modeBadge = document.getElementById("executionModeBadge");
            if (modeBadge) {
                if (executionMode === "EXCEL") {
                    modeBadge.style.background = "#eef2ff";
                    modeBadge.style.color = "#4338ca";
                    modeBadge.style.borderColor = "#c7d2fe";
                    modeBadge.innerHTML = `Source: Excel (${sourceName || 'Test Data'})`;
                } else {
                    modeBadge.style.background = "#f1f5f9";
                    modeBadge.style.color = "#475569";
                    modeBadge.style.borderColor = "#cbd5e1";
                    modeBadge.innerHTML = `Source: Standard Config (${sourceName || 'dev.properties'})`;
                }
            }

            if (total > 0) {
                const currentNumber = Math.min(completed + 1, total);
                const percentage = Math.min(100, Math.round((completed / total) * 100));

                const progressText = document.getElementById("executionProgressText");
                if (progressText) {
                    if (isRunning) {
                        progressText.innerHTML = `Running Test Case <strong>${currentNumber}</strong> of <strong>${total}</strong>`;
                    } else {
                        progressText.innerHTML = `Execution Completed — <strong>${total}</strong> of <strong>${total}</strong>`;
                    }
                }

                const currentTestEl = document.getElementById("executionCurrentTest");
                if (currentTestEl) {
                    if (isRunning && currentTest) {
                        currentTestEl.innerHTML = `Active Test: <strong>${currentTest}</strong> ${currentMod ? `(${currentMod})` : ''} <span style="color:#0284c7;">[In Progress]</span>`;
                    } else if (!isRunning) {
                        currentTestEl.innerHTML = `All <strong>${total}</strong> test case(s) completed successfully.`;
                    }
                }

                const progressBar = document.getElementById("executionProgressBar");
                if (progressBar) {
                    progressBar.style.width = percentage + "%";
                }

                const progressPercentage = document.getElementById("executionProgressPercentage");
                if (progressPercentage) {
                    progressPercentage.innerText = percentage + "%";
                }

                const completedCount = document.getElementById("executionCompletedCount");
                if (completedCount) {
                    completedCount.innerText = `${completed} / ${total} Completed`;
                }

                const badge = document.getElementById("executionProgressBadge");
                if (badge) {
                    if (isRunning) {
                        badge.style.background = "#e0f2fe";
                        badge.style.color = "#0284c7";
                        badge.innerText = `Running (${percentage}%)`;
                    } else {
                        badge.style.background = "#dcfce7";
                        badge.style.color = "#15803d";
                        badge.innerText = "Completed (100%)";
                    }
                }
            }

            if (!isRunning) {
                clearInterval(progressInterval);
                progressInterval = null;

                if (total > 0) {
                    const progressBar = document.getElementById("executionProgressBar");
                    if (progressBar) progressBar.style.width = "100%";

                    const progressPercentage = document.getElementById("executionProgressPercentage");
                    if (progressPercentage) progressPercentage.innerText = "100%";

                    const completedCount = document.getElementById("executionCompletedCount");
                    if (completedCount) completedCount.innerText = `${total} / ${total} Completed`;

                    const badge = document.getElementById("executionProgressBadge");
                    if (badge) {
                        badge.style.background = "#dcfce7";
                        badge.style.color = "#15803d";
                        badge.innerText = "Completed (100%)";
                    }
                }
            }

        } catch (error) {
            console.error("Progress polling failed:", error);
        }
    }, 400);
}

// =========================
// MODULE FORM SUBMIT
// =========================

document.getElementById('moduleForm').addEventListener('submit', async (e) => {

    e.preventDefault();

    const runBtn = document.getElementById("runBtn");
    const statusDiv = document.getElementById("testStatus");
    const statusSpinner = document.getElementById("statusSpinner");
    const statusText = document.getElementById("statusText");
    const statusSubText = document.getElementById("statusSubText");

    // Start Status
    runBtn.disabled = true;
    runBtn.innerHTML = "Running...";

    statusDiv.style.display = "block";
    statusSpinner.style.display = "block";

    statusText.innerHTML = "Test Started...";
    statusSubText.innerHTML =
        "Running automation. Please don't refresh this page.";

    const formData = new FormData(e.target);
    const data = Object.fromEntries(formData);

    const selectedModules =
        Array.from(
            document.querySelectorAll(
                '#moduleOptions1 input[name="moduleTestingModules"]:checked'
            )
        ).map(cb => cb.value);

    data.moduleName = selectedModules.join(",");

    let result = null;

    // Clean up old artifacts before starting a fresh run
    try {
        await fetch("/api/report/cleanup-all", { method: "POST" });
    } catch (ignored) {}

    // Instantly query test plan so total test cases and mode are visible immediately
    let testPlan = null;
    try {
        const planRes = await fetch(`/api/module/test-plan?modules=${encodeURIComponent(data.moduleName)}`);
        if (planRes.ok) {
            testPlan = await planRes.json();
        }
    } catch (err) {
        console.warn("Could not pre-fetch test plan:", err);
    }

    const totalCases = (testPlan && testPlan.totalTestCases > 0) ? testPlan.totalTestCases : (selectedModules.length || 1);
    const firstTestCase = (testPlan && testPlan.firstTestCase) ? testPlan.firstTestCase : (selectedModules[0] || "Test Case 1");
    const firstModule = (testPlan && testPlan.firstModule) ? testPlan.firstModule : (selectedModules[0] || "");
    const isExcel = testPlan && testPlan.executionMode === "EXCEL";
    const sourceName = (testPlan && testPlan.sourceName) ? testPlan.sourceName : (isExcel ? "Uploaded Excel" : "dev.properties");

    try {

        // Show execution progress card
        const executionProgress = document.getElementById("executionProgress");
        if (executionProgress) executionProgress.style.display = "block";

        const modeBadge = document.getElementById("executionModeBadge");
        if (modeBadge) {
            if (isExcel) {
                modeBadge.style.background = "#eef2ff";
                modeBadge.style.color = "#4338ca";
                modeBadge.style.borderColor = "#c7d2fe";
                modeBadge.innerHTML = `Source: Excel (${sourceName})`;
            } else {
                modeBadge.style.background = "#f1f5f9";
                modeBadge.style.color = "#475569";
                modeBadge.style.borderColor = "#cbd5e1";
                modeBadge.innerHTML = `Source: Standard Config (${sourceName})`;
            }
        }

        const completedCountEl = document.getElementById("executionCompletedCount");
        if (completedCountEl) {
            completedCountEl.innerText = `0 / ${totalCases} Completed`;
        }

        const badgeEl = document.getElementById("executionProgressBadge");
        if (badgeEl) {
            badgeEl.style.background = "#e0f2fe";
            badgeEl.style.color = "#0284c7";
            badgeEl.innerText = "Running (0%)";
        }

        const progressTextEl = document.getElementById("executionProgressText");
        if (progressTextEl) {
            progressTextEl.innerHTML = `Running Test Case <strong>1</strong> of <strong>${totalCases}</strong>`;
        }

        const currentTestEl = document.getElementById("executionCurrentTest");
        if (currentTestEl) {
            currentTestEl.innerHTML = `Active Test: <strong>${firstTestCase}</strong> ${firstModule ? `(${firstModule})` : ''} <span style="color:#0284c7;">[In Progress]</span>`;
        }

        const progressBarEl = document.getElementById("executionProgressBar");
        if (progressBarEl) progressBarEl.style.width = "0%";

        const progressPercentageEl = document.getElementById("executionProgressPercentage");
        if (progressPercentageEl) progressPercentageEl.innerText = "0%";

        startProgressPolling();

        const response = await fetch('/api/module/run', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(data)
        });

        result = await response.json();

        if (!response.ok || !Array.isArray(result)) {
            const errorMsg = result && (result.message || result.error) ? (result.message || result.error) : `Execution failed with status ${response.status}`;
            throw new Error(errorMsg);
        }

        let html = `
<div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:12px;">
    <h3 style="margin:0;">Execution Summary</h3>
    <span style="font-size:0.85rem; font-weight:600; padding:3px 10px; border-radius:12px; background:${isExcel ? '#eef2ff' : '#f1f5f9'}; color:${isExcel ? '#4338ca' : '#475569'}; border:1px solid ${isExcel ? '#c7d2fe' : '#cbd5e1'};">
        ${isExcel ? `Excel: ${sourceName}` : `Config: ${sourceName}`}
    </span>
</div>`;

        let pass = 0;
        let fail = 0;

        result.forEach(r => {

            if (r.status === "PASS") {
                pass++;
            } else {
                fail++;
            }

            const moduleName = (r.module || "")
                .replaceAll("_", " ")
                .toLowerCase()
                .replace(/\b\w/g, c => c.toUpperCase());

            const isExcelRow = r.testCase && r.testCase !== r.module;

            html += `
<div style="
    display:flex;
    justify-content:space-between;
    align-items:center;
    margin:10px 0;
    padding:12px 16px;
    border:1px solid #e2e8f0;
    border-radius:8px;
    background:#ffffff;
    box-shadow:0 1px 3px rgba(0,0,0,0.02);
">

    <div>
        <div style="font-weight:700; color:#1e293b; font-size:0.95rem;">
            ${moduleName}
            ${r.testCase ? `<span style="margin-left:8px; font-size:0.85rem; font-family:monospace; background:#f1f5f9; color:#475569; padding:2px 8px; border-radius:4px; border:1px solid #e2e8f0;">${r.testCase}</span>` : ''}
        </div>
        <div style="font-size:0.8rem; color:#64748b; margin-top:3px;">
            ${isExcelRow ? 'Excel-Driven Test Case' : 'Standard Module Execution'}
            ${r.failedStep ? ` • Failed Step: <span style="color:#ef4444; font-weight:600;">${r.failedStep}</span>` : ''}
        </div>
    </div>

    <span style="
        padding:5px 14px;
        border-radius:20px;
        font-weight:700;
        font-size:0.85rem;
        color:white;
        background:${r.status === "PASS" ? "#16a34a" : "#dc2626"};
    ">
        ${r.status}
    </span>

</div>
    `;
        });

        html += `
<hr style="border:none; border-top:1px solid #e2e8f0; margin:15px 0;">

<div style="display:flex; gap:12px; margin-top:10px; flex-wrap:wrap;">

    <div style="padding:10px 18px; background:#f1f5f9; border-radius:8px; font-size:0.9rem;">
        <strong>Total:</strong> ${result.length}
    </div>

    <div style="padding:10px 18px; background:#dcfce7; color:#15803d; border-radius:8px; font-size:0.9rem;">
        <strong>Passed:</strong> ${pass}
    </div>

    <div style="padding:10px 18px; background:#fee2e2; color:#b91c1c; border-radius:8px; font-size:0.9rem;">
        <strong>Failed:</strong> ${fail}
    </div>

</div>
`;

        document.getElementById("moduleResult").innerHTML = html;
        document.getElementById("moduleReportButtons").style.display = "block";
        if (fail > 0) {

            statusText.innerHTML =
                "Test Failed";

            statusSubText.innerHTML =
                `${fail} test case(s) failed. Please check the report for details.`;

        } else {

            statusText.innerHTML =
                "Test Completed Successfully";

            statusSubText.innerHTML =
                "Automation completed successfully. Report is ready to view or download.";

        }

    } catch (error) {

        // Failure
        statusText.innerHTML =
            "Test Failed";

        statusSubText.innerHTML =
            error.message || "An error occurred during test execution.";

        const reportBtns = document.getElementById("moduleReportButtons");
        if (reportBtns) {
            reportBtns.style.display = "block";
        }

    } finally {

        if (progressInterval) {
            clearInterval(progressInterval);
            progressInterval = null;
        }

        // Mark execution progress as completed
        const progressBar =
            document.getElementById("executionProgressBar");

        const progressPercentage =
            document.getElementById("executionProgressPercentage");

        const progressText =
            document.getElementById("executionProgressText");

        const currentTest =
            document.getElementById("executionCurrentTest");

        const completedCount =
            document.getElementById("executionCompletedCount");

        const badge =
            document.getElementById("executionProgressBadge");

        const finalTotal = (result && Array.isArray(result) && result.length > 0)
            ? result.length
            : (totalCases || 1);

        if (progressBar) {
            progressBar.style.width = "100%";
        }

        if (progressPercentage) {
            progressPercentage.innerText = "100%";
        }

        if (progressText) {
            progressText.innerHTML = `Execution Completed — <strong>${finalTotal}</strong> of <strong>${finalTotal}</strong>`;
        }

        if (currentTest) {
            currentTest.innerHTML = `All <strong>${finalTotal}</strong> test case(s) completed.`;
        }

        if (completedCount) {
            completedCount.innerText = `${finalTotal} / ${finalTotal} Completed`;
        }

        if (badge) {
            badge.style.background = "#dcfce7";
            badge.style.color = "#15803d";
            badge.innerText = "Completed (100%)";
        }

        statusSpinner.style.display = "none";

        runBtn.disabled = false;
        runBtn.innerHTML = "Run Test";
    }

});

// =========================
// CITIZEN FORM SUBMIT
// =========================

document.getElementById('citizenForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    const formData = new FormData(e.target);

    const selectedModules =
        Array.from(
            document.querySelectorAll(
                '#moduleOptions input[name="modules"]:checked'
            )
        ).map(cb => cb.value);

    const data = Object.fromEntries(formData);

    // Send comma-separated values to backend
    data.moduleName = selectedModules.join(",");

    const resultDiv = document.getElementById('citizenResult');
    resultDiv.style.display = 'block';
    resultDiv.className = 'result';
    resultDiv.textContent = 'Running test...';
    document.getElementById(
        "citizenReportButtons"
    ).style.display = "block";

    try {
        window.open('http://65.0.8.57:8000', '_blank');

        const response = await fetch('/api/test/citizen', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data)
        });
        const result = await response.text();
        resultDiv.className = 'result success';
        resultDiv.textContent = result;
    } catch (error) {
        resultDiv.className = 'result error';
        resultDiv.textContent = 'Error: ' + error.message;
    }
});

// =========================
// EMPLOYEE FORM SUBMIT
// =========================

document.getElementById('employeeForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    const formData = new FormData(e.target);
    const data = Object.fromEntries(formData);

    const resultDiv = document.getElementById('employeeResult');
    resultDiv.style.display = 'block';
    resultDiv.className = 'result';
    resultDiv.textContent = 'Running test...';
    document.getElementById(
        "employeeReportButtons"
    ).style.display = "block";

    try {
        window.open('http://65.0.8.57:8080', '_blank');

        const response = await fetch('/api/test/employee', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data)
        });
        const result = await response.text();
        resultDiv.className = 'result success';
        resultDiv.textContent = result;
    } catch (error) {
        resultDiv.className = 'result error';
        resultDiv.textContent = 'Error: ' + error.message;
    }
});

// =========================
// VENDOR FORM SUBMIT
// =========================
document.getElementById('vendorForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    const formData = new FormData(e.target);
    const data = Object.fromEntries(formData);


    const resultDiv = document.getElementById('vendorResult');
    resultDiv.style.display = 'block';
    resultDiv.className = 'result';
    resultDiv.textContent = 'Running test...';
    document.getElementById(
        "vendorReportButtons"
    ).style.display = "block";


    try {
        window.open('http://65.0.8.57:8000', '_blank');
        const response = await fetch('/api/test/vendor', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data)
        });
        const result = await response.text();
        resultDiv.className = 'result success';
        resultDiv.textContent = result;
    } catch (error) {
        resultDiv.className = 'result error';
        resultDiv.textContent = 'Error: ' + error.message;
    }
});
// =========================
// AUTO-FILL LOGIC + MULTI-SELECT DROPDOWN
// =========================

// Employee/Vendor dropdown autofill

function autoFillEmployeeFields(selectElement) {
    const select = selectElement || document.querySelector("#employeeModuleDropdown");
    if (!select) return;
    const form = select.closest("form");
    if (!form) return;

    const mobileField = form.querySelector("input[name='mobileNumber']");
    const otpField = form.querySelector("input[name='otp']");
    const baseUrlField = form.querySelector("[name='baseUrl']");
    const cityField = form.querySelector("select[name='cityName']");

    const baseUrl = baseUrlField ? baseUrlField.value : "";
    const moduleVal = select.value;

    if (cityField) {
        if (moduleVal === "COMMUNITY_HALL_BOOKING") {
            cityField.value = "Mohali";
            cityField.dispatchEvent(new Event('change', { bubbles: true }));
        } else if (moduleVal === "STREET_VENDING") {
            cityField.value = "Kurali";
            cityField.dispatchEvent(new Event('change', { bubbles: true }));
        }
    }

    if (mobileField && otpField) {
        let mobile = "7906413996";
        let otp = "123456";

        if (
            moduleVal === "ONLINE_BUILDING_PLAN_APPROVAL_SYSTEM" ||
            moduleVal === "ONLINE_BUILDING_PLAN_APPROVAL_SYSTEM_OC"
        ) {
            if (baseUrl.includes("niuatt.niua")) {
                mobile = "7272727216";
            } else if (baseUrl.includes("upyog")) {
                mobile = "8888888881";
            }
        } else if (moduleVal === "STREET_VENDING") {
            if (baseUrl.includes("niuatt.niua.in/sv-ui") || baseUrl.includes("sv-ui")) {
                mobile = "8010012414";
            }
        }

        mobileField.value = mobile;
        otpField.value = otp;
        mobileField.dispatchEvent(new Event('input', { bubbles: true }));
        mobileField.dispatchEvent(new Event('change', { bubbles: true }));
    }
}

function autoFillVendorFields(selectElement) {
    const select = selectElement || document.querySelector("#vendorModuleDropdown");
    if (!select) return;
    const form = select.closest("form");
    if (!form) return;

    const cityField = form.querySelector("select[name='cityName']");
    const moduleVal = select.value;

    if (cityField) {
        if (moduleVal === "COMMUNITY_HALL_BOOKING") {
            cityField.value = "Mohali";
            cityField.dispatchEvent(new Event('change', { bubbles: true }));
        } else if (moduleVal === "STREET_VENDING") {
            cityField.value = "Kurali";
            cityField.dispatchEvent(new Event('change', { bubbles: true }));
        }
    }
}

document.querySelectorAll("select[name='moduleName']").forEach(select => {
    select.addEventListener("change", function () {
        if (this.id === "employeeModuleDropdown") {
            autoFillEmployeeFields(this);
        } else if (this.id === "vendorModuleDropdown") {
            autoFillVendorFields(this);
        } else {
            autoFillEmployeeFields(this);
        }
    });
});

document.querySelectorAll("select[name='baseUrl']").forEach(select => {
    select.addEventListener("change", function () {
        const form = this.closest("form");
        if (!form) return;
        if (form.id === "citizenForm") {
            const checkedCb = form.querySelector("input[name='modules']:checked");
            if (checkedCb) {
                autoFillCitizenFields(checkedCb);
            }
        } else if (form.id === "employeeForm") {
            const moduleSelect = form.querySelector("select[name='moduleName']");
            if (moduleSelect) {
                autoFillEmployeeFields(moduleSelect);
            }
        } else if (form.id === "vendorForm") {
            const moduleSelect = form.querySelector("select[name='moduleName']");
            if (moduleSelect) {
                autoFillVendorFields(moduleSelect);
            }
        }
    });
});

// Citizen multi-select dropdown

function toggleDropdownById(dropdownId, event) {
    event.stopPropagation();
    document.getElementById(dropdownId).classList.toggle("show");
}
function toggleDropdown(event) {
    event.stopPropagation();
    document.getElementById("moduleDropdown").classList.toggle("show");
}

function filterModules() {
    const search = document.getElementById("moduleSearch").value.toLowerCase();
    const labels = document.querySelectorAll("#moduleOptions label");

    labels.forEach(label => {
        const text = label.textContent.toLowerCase();
        label.style.display = text.includes(search)
            ? "flex"
            : "none";
    });
}
function filterReportModules() {

    const search = document
        .getElementById("moduleSearch2")
        .value
        .toLowerCase();

    const labels = document.querySelectorAll(
        "#moduleOptions2 label"
    );

    labels.forEach(label => {

        const text = label.textContent.toLowerCase();

        label.style.display =
            text.includes(search)
                ? "flex"
                : "none";
    });
}

document.querySelectorAll("input[name='modules']").forEach(cb => {

    cb.addEventListener("click", function (e) {
        e.stopPropagation();
    });

    cb.addEventListener("change", function () {
        updateSelectedModules();
        autoFillCitizenFields(this);
    });

});

function updateSelectedModules() {
    const selected = Array.from(
        document.querySelectorAll("input[name='modules']:checked")
    ).map(cb => cb.parentElement.textContent.trim());

    document.getElementById("selectedModulesText").textContent =
        selected.length > 0
            ? selected.join(", ")
            : "Select Modules";
}
document.querySelectorAll(
    "input[name='moduleTestingModules']"
).forEach(cb => {

    cb.addEventListener("change", function () {
        updateSelectedModulesModule();
    });

});

document.querySelectorAll(
    '#moduleOptions2 input[name="reportModules"]'
).forEach(cb => {
    cb.addEventListener("change", updateSelectedReportModules);
});

function updateSelectedModulesModule() {

    const selected = Array.from(
        document.querySelectorAll(
            "input[name='moduleTestingModules']:checked"
        )
    ).map(cb => cb.parentElement.textContent.trim());

    document.getElementById("selectedModulesText1").textContent =
        selected.length > 0
            ? selected.join(", ")
            : "Select Modules";
}

function updateSelectedReportModules() {

    const checked = document.querySelectorAll(
        '#moduleOptions2 input[name="reportModules"]:checked'
    );

    const text = document.getElementById("selectedModulesReport");

    if (checked.length === 0) {
        text.innerHTML = "Select Modules";
        return;
    }

    text.innerHTML = Array.from(checked)
        .map(c => c.parentElement.textContent.trim())
        .join(", ");
}

async function loadReports() {

    const selectedModules = Array.from(
        document.querySelectorAll(
            '#moduleOptions2 input[name="reportModules"]:checked'
        )
    ).map(cb => cb.value);

    if (selectedModules.length === 0) {
        alert("Please select at least one module.");
        return;
    }

    const tbody = document.getElementById("reportTableBody");
    tbody.innerHTML = "";

    const responses = await Promise.all(

        selectedModules.map(async module => {

            const response =
                await fetch(`/api/report/module/${module}`);

            const reports =
                await response.json();

            return reports.map(r => ({
                ...r,
                module
            }));

        })

    );

    const allReports = responses.flat();

    allReports.sort((a, b) => {

        const d1 = new Date(`${a.date} ${a.time}`);
        const d2 = new Date(`${b.date} ${b.time}`);

        return d2 - d1;

    });

    allReports.forEach(report => {

        tbody.innerHTML += `
<tr style="
    background:${report.status === "PASS"
                ? "#d4edda"
                : "#f8d7da"};
">

    <td><b>${report.module}</b></td>

    <td>${report.date}</td>

    <td>${report.time}</td>

    <td>
        <b style="
            color:${report.status === "PASS"
                ? "green"
                : "red"};
        ">
            ${report.status}
        </b>
    </td>

    <td>
        <a href="/api/report/view/${report.fileName}" target="_blank" style="margin-right: 8px; text-decoration: none; display: inline-flex; align-items: center; gap: 3px;">
            <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path><polyline points="14 2 14 8 20 8"></polyline><line x1="16" y1="13" x2="8" y2="13"></line><line x1="16" y1="17" x2="8" y2="17"></line></svg>
            View
        </a>
        <a href="/api/report/download/${report.fileName}" style="text-decoration: none; display: inline-flex; align-items: center; gap: 3px;">
            <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"></path><polyline points="7 10 12 15 17 10"></polyline><line x1="12" y1="15" x2="12" y2="3"></line></svg>
            HTML
        </a>
    </td>

    <td>
        <a href="/api/report/manual/${report.module}" target="_blank" style="margin-right: 8px; font-weight: 600; text-decoration: none; display: inline-flex; align-items: center; gap: 3px;">
            <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M2 3h6a4 4 0 0 1 4 4v14a3 3 0 0 0-3-3H2z"></path><path d="M22 3h-6a4 4 0 0 0-4 4v14a3 3 0 0 1 3-3h7z"></path></svg>
            View Manual
        </a>
        <a href="/api/report/manual/download/${report.module}" style="color: #4f46e5; font-weight: 600; text-decoration: none; display: inline-flex; align-items: center; gap: 3px;">
            <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="#4f46e5" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="16.5" y1="9.4" x2="7.5" y2="4.21"></line><path d="M21 16V8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16z"></path><polyline points="3.27 6.96 12 12.01 20.73 6.96"></polyline><line x1="12" y1="22.08" x2="12" y2="12"></line></svg>
            Download ZIP
        </a>
    </td>

    <td>
        <a href="/api/report/screenshots/download/${report.module}" style="color: #059669; font-weight: 600; text-decoration: none; display: inline-flex; align-items: center; gap: 3px;">
            <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="#059669" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="3" width="18" height="18" rx="2" ry="2"></rect><circle cx="8.5" cy="8.5" r="1.5"></circle><polyline points="21 15 16 10 5 21"></polyline></svg>
            Download (ZIP)
        </a>
    </td>

    <td>
        <a href="javascript:void(0)" onclick="viewLatestRecordingModal('${report.module}')" style="color: #e11d48; font-weight: 600; margin-right: 8px; text-decoration: none; display: inline-flex; align-items: center; gap: 3px;">
            <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="#e11d48" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polygon points="23 7 16 12 23 17 23 7"></polygon><rect x="1" y="5" width="15" height="14" rx="2" ry="2"></rect></svg>
            View
        </a>
        <a href="/api/report/video/download/module/${report.module}" style="color: #0891b2; font-weight: 600; text-decoration: none; display: inline-flex; align-items: center; gap: 3px;">
            <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="#0891b2" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"></path><polyline points="7 10 12 15 17 10"></polyline><line x1="12" y1="15" x2="12" y2="3"></line></svg>
            MP4
        </a>
    </td>

</tr>
`;
    });
    document.getElementById("reportTableContainer").style.display = "block";

}

function autoFillCitizenFields(moduleCheckbox) {
    const form = moduleCheckbox.closest("form");
    if (!form) return;

    const mobileField = form.querySelector("input[name='mobileNumber']");
    const otpField = form.querySelector("input[name='otp']");
    const baseUrlField = form.querySelector("[name='baseUrl']");
    const cityField = form.querySelector("select[name='cityName']");

    const baseUrl = baseUrlField ? baseUrlField.value : "";
    const selectedModule = moduleCheckbox.value;

    if (cityField && moduleCheckbox.checked) {
        if (selectedModule === "COMMUNITY_HALL_BOOKING") {
            cityField.value = "Mohali";
            cityField.dispatchEvent(new Event('change', { bubbles: true }));
        } else if (selectedModule === "STREET_VENDING") {
            cityField.value = "Kurali";
            cityField.dispatchEvent(new Event('change', { bubbles: true }));
        }
    }

    if (!mobileField || !otpField) return;

    let mobile = "7906413996";
    let otp = "123456";

    if (
        selectedModule === "ONLINE_BUILDING_PLAN_APPROVAL_SYSTEM" ||
        selectedModule === "ONLINE_BUILDING_PLAN_APPROVAL_SYSTEM_OC"
    ) {
        if (baseUrl.includes("niuatt.niua")) {
            mobile = "7272727216";
        } else if (baseUrl.includes("upyog")) {
            mobile = "8888888881";
        }
    }
    if (
        selectedModule === "STREET_VENDING"
    ) {
        if (baseUrl.includes("niuatt.niua.in/sv-ui/citizen/login") || baseUrl.includes("sv-ui")) {
            mobile = "8010012414";
        }
    }

    mobileField.value = mobile;
    otpField.value = otp;

    mobileField.dispatchEvent(new Event('input', { bubbles: true }));
    mobileField.dispatchEvent(new Event('change', { bubbles: true }));
}

// Outside click close for all multi-select dropdowns
document.addEventListener("click", function (e) {

    const dropdowns = [
        "moduleDropdown",
        "moduleDropdown1",
        "moduleDropdown2"
    ];

    dropdowns.forEach(function (dropdownId) {

        const dropdown =
            document.getElementById(dropdownId);

        if (!dropdown) return;

        const container =
            dropdown.closest(".multi-select-container");

        if (
            !container ||
            !container.contains(e.target)
        ) {
            dropdown.classList.remove("show");
        }
    });
});

// Result Output

async function viewLatestReport(containerId) {

    const response = await fetch('/api/report/list');

    const reports = await response.json();

    let html = "";

    reports.forEach(report => {

        html += `
            <div>
                <a href="/api/report/view/${report}"
                   target="_blank">
                    ${report}
                </a>
            </div>
        `;
    });

    document.getElementById(containerId).innerHTML = html;
}

function downloadTestDataTemplate() {
    window.location.href = "/api/module/download-template";
}
async function handleExcelUpload(input) {

    const statusElement =
        document.getElementById("excelUploadStatus");

    if (!input.files || input.files.length === 0) {
        statusElement.innerText = "";
        return;
    }

    const file = input.files[0];

    if (!file.name.toLowerCase().endsWith(".xlsx")) {

        statusElement.innerText =
            "Please upload an Excel .xlsx file.";

        input.value = "";
        return;
    }

    statusElement.innerText =
        "Uploading " + file.name + "...";

    const formData = new FormData();

    formData.append("file", file);

    try {

        const response = await fetch(
            "/api/module/upload-excel",
            {
                method: "POST",
                body: formData
            }
        );

        const message =
            await response.text();

        if (!response.ok) {
            throw new Error(message);
        }

        const selectedModules = Array.from(
            document.querySelectorAll('#moduleOptions1 input[name="moduleTestingModules"]:checked')
        ).map(cb => cb.value);

        let planInfo = "";
        try {
            const planRes = await fetch(`/api/module/test-plan?modules=${encodeURIComponent(selectedModules.join(","))}`);
            if (planRes.ok) {
                const plan = await planRes.json();
                if (plan && plan.totalTestCases > 0) {
                    const tcNames = plan.testCases.map(t => t.testCase).join(", ");
                    planInfo = ` — Detected ${plan.totalTestCases} executable test case(s): <strong>[${tcNames}]</strong>`;
                }
            }
        } catch (e) {
            console.warn("Could not query plan after upload:", e);
        }

        statusElement.innerHTML = `<span style="color:#16a34a; font-weight:600;">${message}</span><span style="color:#4338ca; font-size:0.9em; margin-left:6px;">${planInfo}</span>`;

    } catch (error) {

        statusElement.innerHTML =
            `<span style="color:#dc2626; font-weight:600;">Upload failed: ${error.message}</span>`;

        input.value = "";
    }
}

function getCurrentOrSelectedModule() {
    // 1. Check active tab first if possible
    const activeTab = document.querySelector('.tab-content.active');
    if (activeTab) {
        if (activeTab.id === 'citizen') {
            const citizenChecked = Array.from(
                document.querySelectorAll('#moduleOptions input[name="modules"]:checked')
            ).map(cb => cb.value);
            if (citizenChecked.length > 0 && citizenChecked[0]) return citizenChecked[0];
        } else if (activeTab.id === 'employee') {
            const empSelect = document.getElementById('employeeModuleDropdown');
            if (empSelect && empSelect.value) return empSelect.value;
        } else if (activeTab.id === 'vendor') {
            const venSelect = document.getElementById('vendorModuleDropdown');
            if (venSelect && venSelect.value) return venSelect.value;
        } else if (activeTab.id === 'module') {
            const moduleChecked = Array.from(
                document.querySelectorAll('#moduleOptions1 input[name="moduleTestingModules"]:checked')
            ).map(cb => cb.value);
            if (moduleChecked.length > 0 && moduleChecked[0]) return moduleChecked[0];
        } else if (activeTab.id === 'report') {
            const repChecked = Array.from(
                document.querySelectorAll('#moduleOptions2 input[name="reportModules"]:checked')
            ).map(cb => cb.value).filter(v => v !== 'ALL');
            if (repChecked.length > 0 && repChecked[0]) return repChecked[0];
        }
    }

    // 2. Check checkboxes from module testing
    const checked = Array.from(
        document.querySelectorAll('#moduleOptions1 input[name="moduleTestingModules"]:checked')
    ).map(cb => cb.value);
    if (checked.length > 0 && checked[0]) {
        return checked[0];
    }

    // 3. Check checkboxes from citizen testing
    const citizenChecked = Array.from(
        document.querySelectorAll('#moduleOptions input[name="modules"]:checked')
    ).map(cb => cb.value);
    if (citizenChecked.length > 0 && citizenChecked[0]) {
        return citizenChecked[0];
    }

    // 4. Check select inputs in forms (Employee / Vendor)
    const selects = document.querySelectorAll('select[name="moduleName"], select[name="module"]');
    for (const sel of selects) {
        if (sel.value && sel.value !== "" && sel.value !== "Select Module") {
            return sel.value;
        }
    }

    // 5. Check checkboxes from reports tab
    const reportChecked = Array.from(
        document.querySelectorAll('#moduleOptions2 input[name="reportModules"]:checked')
    ).map(cb => cb.value).filter(v => v !== 'ALL');
    if (reportChecked.length > 0 && reportChecked[0]) {
        return reportChecked[0];
    }

    // 6. Try parsing latest report link text if available
    const reportList = document.getElementById("reportListContainer");
    if (reportList && reportList.innerText.trim().length > 0) {
        const text = reportList.innerText.trim();
        const parts = text.split("_");
        if (parts.length > 0 && parts[0].length > 1) {
            return parts[0];
        }
    }

    return "ALL";
}

function downloadLatestReport() {
    window.open('/api/report/download', '_blank');
}

function viewLatestUserManual(mod, workflow) {
    const targetModule = mod || getCurrentOrSelectedModule();
    let url = `/api/report/manual/${encodeURIComponent(targetModule)}`;
    if (workflow) {
        url += `?testCase=${encodeURIComponent(workflow)}`;
    }
    window.open(url, '_blank');
}

function downloadLatestUserManualPackage(mod, workflow) {
    const targetModule = mod || getCurrentOrSelectedModule();
    let downloadUrl = `/api/report/manual/download/${encodeURIComponent(targetModule)}`;
    if (workflow) {
        downloadUrl += `?testCase=${encodeURIComponent(workflow)}`;
    }

    // Use an invisible link click to trigger browser direct download
    const a = document.createElement('a');
    a.href = downloadUrl;
    a.download = `${targetModule}_User_Manual.zip`;
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
}

function downloadLatestScreenshots(mod, workflow) {
    const targetModule = mod || getCurrentOrSelectedModule();
    let downloadUrl = `/api/report/screenshots/download/${encodeURIComponent(targetModule)}`;
    if (workflow) {
        downloadUrl += `?testCase=${encodeURIComponent(workflow)}`;
    }

    // Use an invisible link click to trigger browser direct download
    const a = document.createElement('a');
    a.href = downloadUrl;
    a.download = `${targetModule}_Screenshots.zip`;
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
}

function downloadUpdatedExcel() {
    window.open("/api/module/download-result", "_blank");
}

// =========================================================
// SCREEN RECORDING VIDEO MODAL & DOWNLOAD
// =========================================================

// =========================================================
// SCREEN RECORDING VIDEO MODAL & DOWNLOAD
// =========================================================

let currentActiveRecording = null;

function viewLatestRecordingModal(mod) {
    const targetModule = mod || getCurrentOrSelectedModule();
    const modal = document.getElementById("videoModal");
    const videoPlayer = document.getElementById("modalVideoPlayer");
    const videoTitle = document.getElementById("modalVideoTitle");
    const downloadCurrentBtn = document.getElementById("modalVideoDownloadBtn");
    const downloadAllBtn = document.getElementById("modalVideoDownloadAllBtn");
    const playlistContainer = document.getElementById("videoPlaylistItems");
    const videoCountBadge = document.getElementById("videoCountBadge");
    const activeVideoName = document.getElementById("activeVideoName");
    const activeVideoMeta = document.getElementById("activeVideoMeta");

    const displayMod = (targetModule && targetModule !== 'ALL') ? targetModule.replace(/_/g, ' ') : 'All Modules';
    if (videoTitle) {
        videoTitle.innerText = `Screen Recordings — ${displayMod}`;
    }

    // Set up Download All button
    if (downloadAllBtn) {
        downloadAllBtn.onclick = function() {
            downloadAllRecordings(targetModule);
        };
    }

    if (modal) {
        modal.style.display = "flex";
    }

    // Fetch list of all recordings for this module
    const listUrl = `/api/report/video/list?module=${encodeURIComponent(targetModule || 'ALL')}&t=${new Date().getTime()}`;
    fetch(listUrl)
        .then(res => res.json())
        .then(recordings => {
            if (playlistContainer) playlistContainer.innerHTML = '';
            if (videoCountBadge) videoCountBadge.innerText = recordings.length;

            if (!recordings || recordings.length === 0) {
                // Fallback single stream
                if (playlistContainer) {
                    playlistContainer.innerHTML = '<div style="padding:16px; text-align:center; color:#94a3b8; font-size:0.8rem;">No recordings saved yet.</div>';
                }
                const fallbackUrl = (targetModule && targetModule !== "ALL")
                    ? `/api/report/video/module/${encodeURIComponent(targetModule)}`
                    : `/api/report/video/latest`;
                if (videoPlayer) {
                    videoPlayer.src = fallbackUrl;
                    videoPlayer.load();
                }
                if (activeVideoName) activeVideoName.innerText = 'Latest recording';
                return;
            }

            // Populate playlist items
            recordings.forEach((rec, idx) => {
                const itemEl = document.createElement('div');
                itemEl.className = 'playlist-item' + (idx === 0 ? ' active' : '');
                itemEl.id = `playlist-item-${idx}`;

                const baseName = rec.fileName || rec.name || `Recording_${idx + 1}.mp4`;
                const sizeMb = rec.fileSizeKb ? (rec.fileSizeKb / 1024).toFixed(2) + ' MB' : (rec.size ? (rec.size / (1024 * 1024)).toFixed(2) + ' MB' : '');
                const timeStr = rec.lastModified || rec.timestamp || '';
                const titleStr = rec.displayName || baseName.replace(/\.mp4$/i, '').replace(/_/g, ' ');
                const downloadUrl = rec.downloadUrl || `/api/report/video/download/${encodeURIComponent(baseName)}`;

                itemEl.innerHTML = `
                    <div class="playlist-item-left">
                        <div class="playlist-item-icon">
                            <svg width="14" height="14" viewBox="0 0 24 24" fill="currentColor"><polygon points="5 3 19 12 5 21 5 3"></polygon></svg>
                        </div>
                        <div class="playlist-item-info">
                            <div class="playlist-item-name" title="${baseName}">${titleStr}</div>
                            <div class="playlist-item-meta">${timeStr} ${sizeMb ? '• ' + sizeMb : ''}</div>
                        </div>
                    </div>
                    <button type="button" class="playlist-download-icon-btn" title="Download this video" onclick="event.stopPropagation(); window.open('${downloadUrl}', '_blank')">
                        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"></path><polyline points="7 10 12 15 17 10"></polyline><line x1="12" y1="15" x2="12" y2="3"></line></svg>
                    </button>
                `;

                itemEl.onclick = function() {
                    selectRecordingToPlay(rec, idx, recordings.length);
                };

                playlistContainer.appendChild(itemEl);
            });

            // Automatically play the first recording
            selectRecordingToPlay(recordings[0], 0, recordings.length);
        })
        .catch(err => {
            console.warn("Failed to fetch recording list:", err);
            const fallbackUrl = (targetModule && targetModule !== "ALL")
                ? `/api/report/video/module/${encodeURIComponent(targetModule)}`
                : `/api/report/video/latest`;
            if (videoPlayer) {
                videoPlayer.src = fallbackUrl;
                videoPlayer.load();
            }
        });
}

function selectRecordingToPlay(rec, index, totalCount) {
    if (!rec) return;
    currentActiveRecording = rec;
    const videoPlayer = document.getElementById("modalVideoPlayer");
    const downloadCurrentBtn = document.getElementById("modalVideoDownloadBtn");
    const activeVideoName = document.getElementById("activeVideoName");
    const activeVideoMeta = document.getElementById("activeVideoMeta");

    const baseName = rec.fileName || rec.name || `Recording_${index + 1}.mp4`;
    const streamUrl = rec.viewUrl || rec.streamUrl || `/api/report/video/view/${encodeURIComponent(baseName)}`;
    const downloadUrl = rec.downloadUrl || `/api/report/video/download/${encodeURIComponent(baseName)}`;

    // Update active class in playlist
    for (let i = 0; i < totalCount; i++) {
        const el = document.getElementById(`playlist-item-${i}`);
        if (el) {
            if (i === index) {
                el.classList.add('active');
            } else {
                el.classList.remove('active');
            }
        }
    }

    if (activeVideoName) {
        activeVideoName.innerText = rec.displayName || baseName;
    }
    if (activeVideoMeta) {
        const sizeMb = rec.fileSizeKb ? (rec.fileSizeKb / 1024).toFixed(2) + ' MB' : (rec.size ? (rec.size / (1024 * 1024)).toFixed(2) + ' MB' : '');
        const timeStr = rec.lastModified || rec.timestamp || '';
        activeVideoMeta.innerText = `${timeStr} ${sizeMb ? '• ' + sizeMb : ''}`;
    }

    if (downloadCurrentBtn) {
        downloadCurrentBtn.onclick = function() {
            window.open(downloadUrl, '_blank');
        };
    }

    if (videoPlayer) {
        videoPlayer.pause();
        videoPlayer.muted = true;
        videoPlayer.onerror = function() {
            console.warn("Video playback encountered an issue:", videoPlayer.error);
            if (activeVideoMeta) {
                activeVideoMeta.innerHTML = `${timeStr} ${sizeMb ? '• ' + sizeMb : ''} &nbsp;<span style="color:#f87171;">(Stream pending — click <a href="${downloadUrl}" target="_blank" style="color:#38bdf8; text-decoration:underline;">Download MP4</a>)</span>`;
            }
        };
        videoPlayer.src = streamUrl;
        videoPlayer.load();
        const playPromise = videoPlayer.play();
        if (playPromise !== undefined) {
            playPromise.catch(e => {
                console.log("Autoplay waiting for user gesture or codec ready:", e);
            });
        }
    }
}

function closeVideoModal() {
    const modal = document.getElementById("videoModal");
    const videoPlayer = document.getElementById("modalVideoPlayer");
    if (videoPlayer) {
        videoPlayer.pause();
        videoPlayer.removeAttribute("src");
        videoPlayer.load();
    }
    if (modal) {
        modal.style.display = "none";
    }
}

function downloadLatestRecordingVideo(mod) {
    const targetModule = mod || getCurrentOrSelectedModule();
    // Downloads all session recordings package (ZIP) so all clips are included
    downloadAllRecordings(targetModule);
}

function downloadAllRecordings(mod) {
    const targetModule = mod || getCurrentOrSelectedModule() || "ALL";
    const downloadUrl = `/api/report/video/download-all?module=${encodeURIComponent(targetModule)}`;
    const a = document.createElement('a');
    a.href = downloadUrl;
    a.download = `Recordings_${targetModule}.zip`;
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
}

// Close modal on Escape key press
document.addEventListener("keydown", function(e) {
    if (e.key === "Escape" || e.keyCode === 27) {
        closeVideoModal();
    }
});

// =========================================================
// ACTION BUTTONS DOWNLOAD DROPDOWN
// =========================================================

function toggleDownloadDropdown(menuId, event) {
    if (event) {
        event.stopPropagation();
    }
    const menu = document.getElementById(menuId);
    if (!menu) return;

    const isVisible = menu.style.display === "block" || menu.classList.contains("show");

    // Close any other open dropdowns first
    closeAllDownloadDropdowns();

    if (!isVisible) {
        menu.style.display = "block";
        menu.classList.add("show");
    }
}

function closeAllDownloadDropdowns() {
    document.querySelectorAll(".download-dropdown-menu").forEach(menu => {
        menu.style.display = "none";
        menu.classList.remove("show");
    });
}

// Close dropdown when clicking anywhere outside
document.addEventListener("click", function(e) {
    if (!e.target.closest(".dropdown-wrapper")) {
        closeAllDownloadDropdowns();
    }
});

// Automatically clean up previous temporary test artifacts on page load / refresh
window.addEventListener("DOMContentLoaded", function () {
    fetch("/api/report/cleanup-all", { method: "POST" }).catch(() => {});
});