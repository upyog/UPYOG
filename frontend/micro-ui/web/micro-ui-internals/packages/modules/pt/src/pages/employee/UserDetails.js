import React, { useEffect, useState } from "react"
import { TextInput, Label, SubmitBar, LinkLabel, ActionBar, CloseSvg, DatePicker, CardLabelError, SearchForm, SearchField, Dropdown, Toast, CardLabel } from "@upyog/digit-ui-react-components";
import { useForm, Controller } from "react-hook-form";
import { useParams,useHistory } from "react-router-dom"
import { useTranslation } from "react-i18next";


const UserDetails = ({path}) => {
    
    const { userId } = useParams();
    const tenantId = Digit.ULBService.getCurrentTenantId();
    const history = useHistory();
    const { t } = useTranslation();
    const [loading, setLoading] = useState(false);
    const [employeeData, setEmployeeData] = useState(null);

    const [empId, setEmpId] = useState(userId || null);
    console.log("UserDetails==empId==",empId)

    const [employeeTypes, setEmployeeTypes] = useState([
        { name: "Document Verifier", code: "Document Verifier" },
        { name: "Field Inspector", code: "Field Inspector" },
        { name: "Approver", code: "Approver" },
        {name: "Executing Officer", code: "Executing Officer"},
    ]);

    // const [employeeType, setEmployeeType] = useState(employeeData?.employeeType || "");
    // const [userName, setUserName] = useState(employeeData?.userName || "");
    // const [employeeName, setEmployeeName] = useState(employeeData?.employeeName || "");
    // const [employeeEmail, setEmployeeEmail] = useState(employeeData?.employeeEmail || "");
    // const [employeeMobileNo, setEmployeeMobileNo] = useState(employeeData?.employeeMobileNo || "");

    useEffect(() => {
        if(empId) {
            fetchEmployeeById(empId);
        }
        return () => {
            
        };
    }, [empId]);

    const fetchEmployeeById = (empId=null) => {
        setLoading(true);
        // Simulate API call to fetch employee data based on filters
        // setTimeout(() => {
            // For demonstration, using static data. Replace with actual API response.
             Digit.HRMSService.search(tenantId, null, {phone: '7900653172'}).then((result) => {
                console.log("Employee data fetched by ID==",result);
                if (result?.Employees?.length > 0) {
                    setEmployeeData(result.Employees[0]);
                } else {
                // setPhonecheck(true);
                }
            });
            
        // }, 1000);
    }

    
    return <React.Fragment>
        <div >
            <h1>Employee Details</h1>
            {employeeData && (
                <div>
                    <div className="card" style={{maxWidth: "100%"}}>
                        <div className="row">
                            <div className="col-sm-3" style={{ display: "inline-block" }}>
                                <CardLabel>{`${t("Employee Type")}`}</CardLabel>
                                <Dropdown
                                isMandatory
                                optionCardStyles={{ zIndex: 111111 }}
                                selected={employeeData?.employeeType}
                                optionKey="name"
                                option={employeeTypes}
                                select={setEmployeeType}
                                t={t}
                                />
                            </div>
                            <div className="col-sm-3" style={{ display: "inline-block" }}>
                                <CardLabel>{`${t("User Name")}`}</CardLabel>
                                
                                <TextInput
                                name="userName"
                                id="userName"
                                className="field desktop-w-full"
                                value={employeeData?.userName}
                                onChange={(e) =>{ setUserName(e.target.value)}}
                                disable={false}
                                defaultValue={undefined}
                                />
                            </div>
                            <div className="col-sm-3" style={{ display: "inline-block" }}>
                                <CardLabel>{`${t("Employee Name")}`}</CardLabel>
                                <TextInput
                                name="employeeName"
                                id="employeeName"
                                className="field desktop-w-full"
                                value={employeeData?.employeeName}
                                onChange={(e) =>{ setEmployeeName(e.target.value)}}
                                disable={false}
                                defaultValue={undefined}
                                />
                            </div>
                            <div className="col-sm-3" style={{ display: "inline-block" }}>
                                <CardLabel>{`${t("Employee Email")}`}</CardLabel>
                                <TextInput
                                name="employeeEmail"
                                id="employeeEmail"
                                className="field desktop-w-full"
                                value={employeeData?.employeeEmail}
                                onChange={(e) =>{ setEmployeeEmail(e.target.value)}}
                                disable={false}
                                defaultValue={undefined}
                                />
                            </div>
                        </div>
                        <div className="row">
                            <div className="col-sm-3" style={{ display: "inline-block" }}>
                                <CardLabel>{`${t("Employee Mobile No.")}`}</CardLabel>
                                <TextInput
                                name="employeeMobileNo"
                                id="employeeMobileNo"
                                className="field desktop-w-full"
                                key={"employeeMobileNo"}
                                value={employeeData?.employeeMobileNo}
                                onChange={(e) =>{ setEmployeeMobileNo(e.target.value)}}
                                disable={false}
                                defaultValue={undefined}
                                />
                            </div>
                            <div className="col-sm-9" style={{ display: "inline-block", top: "25px" }}>
                                <div style={{ display: "inline-flex", justifyContent: "end", marginTop: "10px" }}>
                                    <div style={{ display: "inline" }}>
                                    <button
                                        onClick={onSearch}
                                        className="submit-bar"
                                        style={{
                                        color: "white",
                                        float: "right",
                                        marginLeft: "10px",
                                        }}
                                    >
                                        {t("Search")}
                                    </button>
                                    <button
                                        onClick={onReset}
                                        className="submit-bar"
                                        style={{
                                        color: "white",
                                        float: "right",
                                        marginLeft: "10px",
                                        }}
                                    >
                                        {t("Reset")}
                                    </button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            )}
        </div>
    </React.Fragment>

}

export default UserDetails