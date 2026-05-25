import React, { useEffect, useRef, useState } from "react"
import { TextInput, Label, SubmitBar, LinkLabel, ActionBar, CloseSvg, DatePicker, CardLabelError, SearchForm, SearchField, Dropdown, Toast, Header, CardLabel, PopUp, Card } from "@upyog/digit-ui-react-components";

import { useForm, Controller } from "react-hook-form";
import { useParams, useHistory } from "react-router-dom"
import { useTranslation } from "react-i18next";
import PTSearchAppeal from "../../components/PTSearchAppeal";


const UserManagement = ({path}) => {
    const history = useHistory();
    const tenantId = Digit.ULBService.getCurrentTenantId();
    const employeeTypes = [
        { name: "Document Verifier", code: "Document Verifier" },
        { name: "Field Inspector", code: "Field Inspector" },
        { name: "Approver", code: "Approver" },
        {name: "Executing Officer", code: "Executing Officer"},
    ];
    const { data: cities, isLoading } = Digit.Hooks.useTenants();
    // console.log("cities==", cities);
    const [wardList, setWardList] = useState([]);
    const { t } = useTranslation();
    const [employeeType, setEmployeeType] = useState(null);
    const [employeeName, setEmployeeName] = useState(null);
    const [employeeEmail, setEmployeeEmail] = useState(null);
    const [employeeMobileNo, setEmployeeMobileNo] = useState(null);
    const [userName, setUserName] = useState(null);
    const [email, setEmail] = useState(null);
    const [mobileNo, setMobileNo] = useState(null);
    const [showChangePasswordPopup, setShowChangePasswordPopup] = useState(false);

    const [resetTriggered, setResetTriggered] = useState(false);
    const [dashboardData, setDashboardData] = useState(null);
    const [cityDisable, setCityDisable] = useState(false);

    const [currentPassword, setCurrentPassword] = useState("");
    const [newPassword, setNewPassword] = useState("");
    const [confirmNewPassword, setConfirmNewPassword] = useState("");
    const [showToast, setShowToast] = useState(null);

    const [filteredData, setFilteredData] = useState([]);
    const defaultColumns = [
        { key: "slNo", label: "Sl No" },
        { key: "employeeType", label: "Employee Type" },
        { key: "userName", label: "User Name" },
        { key: "name", label: "Name" },
        { key: "mobileNo", label: "Mobile No" },
        { key: "email", label: "Email" },
        { key: "action", label: "Action" },
    ];
    const [userDataList, setUserDataList] = useState([]);
    const [tableColumnList, setTableColumnList] = useState(defaultColumns);
    const [loading, setLoading] = useState(false);

    const [tableKey, setTableKey] = useState();
    const [isShowTable, setIsShowTable] = useState(false);
    const [isShowTableName, setIsShowTableName] = useState(false);
    const [selectedEmployee, setSelectedEmployee] = useState(null);
    const hasCalledRef = useRef(false);

    useEffect(() => {
        if (!tenantId || hasCalledRef.current) return;

        hasCalledRef.current = true;
        fetchEmployeeData();
   
    }, [tenantId]);

    const formatData = (employeeData) => {
        const filteredUsers = employeeData.filter(employee => employee?.code !== "ASSIGNING_OFFICER").map(employee => {
          console.log("employee==", employee);
            const filteredEmp = employee && employee?.user?.roles?.length > 0 ? employee?.user?.roles?.filter(item => item.code !== "EMPLOYEE") : [];
            console.log("filteredEmp==", filteredEmp);
            employee.employeeType = t(filteredEmp?.[0]?.code ? 'WF_ROLE_' + filteredEmp?.[0]?.code : 'N/A');
            employee.userName = employee?.user?.userName || "N/A";
            employee.name = employee?.user?.name || "N/A";
            employee.mobileNo = employee?.user?.mobileNumber || "N/A";
            employee.email = employee?.user?.emailId || "N/A";
            return employee;
        });
        console.log("filteredUsers==", filteredUsers);
        setUserDataList(filteredUsers);
        setFilteredData(filteredUsers);
        setLoading(false);
    };

    const fetchEmployeeData = (filters={}) => {
        setLoading(true);
        // Simulate API call to fetch employee data based on filters
        // setTimeout(() => {
            // For demonstration, using static data. Replace with actual API response.
             Digit.HRMSService.search(tenantId, null, filters).then((result) => {
                if (result?.Employees?.length > 0) {
                    const employeeData = result.Employees;
                    formatData(employeeData);
                } else {
                // setPhonecheck(true);
                }
            });
            
        // }, 1000);
    };
    
      const formatDate = (data) => {
        const date = new Date(data);
        const formatted = date.toLocaleDateString("en-GB"); // dd/MM/yyyy
        const finalFormat = formatted.replaceAll("/", "-");
        // console.log(finalFormat); // "16-07-2025"
        return finalFormat;
      };
      const onSearch = (e) => {
        e.preventDefault();
        // console.log("onSearch");
        let filters = {};
        if(employeeType) filters["employeeType"] = employeeType.code;
        if(employeeName) filters["name"] = employeeName;
        if(employeeEmail) filters["email"] = employeeEmail;
        if(employeeMobileNo) filters["mobileNo"] = employeeMobileNo;
        if(userName) filters["userName"] = userName;
        console.log("filters==", filters);
        const filteredUsers =
        userDataList?.length > 0
            ? userDataList.filter((employee) => {
                if (filters.employeeType && employee?.employeeType !== filters.employeeType) return false;
                if (filters.name && !employee?.name?.toLowerCase().includes(filters.name.toLowerCase())) return false;
                if (filters.email && !employee?.email?.toLowerCase().includes(filters.email.toLowerCase())) return false;
                if (filters.mobileNo && !String(employee?.mobileNo || "").includes(String(filters.mobileNo))) return false;
                if (filters.userName && !employee?.userName?.toLowerCase().includes(filters.userName.toLowerCase())) return false;

                return true;
            })
            : [];
        console.log("filteredUsers==", filteredUsers);
        setFilteredData(filteredUsers);
      };
      const onReset = (e) => {
        e.preventDefault();
        
        setEmployeeType(null);
        setUserName(null);
        setEmployeeName(null);
        setEmployeeEmail(null);
        setEmployeeMobileNo(null);
        setFilteredData(userDataList);

        // setResetTriggered(true);
      };
      useEffect(() => {
        if (resetTriggered) {
          fetchEmployeeData();
          setResetTriggered(false); // reset flag
        }
      }, [resetTriggered]);
      
    const loadDashboardData = () => {
        // console.log("loadDashboardData==",city,ward,fromDate,toDate);
    }

    // const handleEdit = (record, index) => {
    //     console.log("Edit record==",record, index);
    //     if(record?.mobileNo)
    //     history.push(`/digit-ui/employee/pt/user-details/${record?.mobileNo}`);
    //     else Toast.error(t("User Mobile Number Not Found"));
    // }

    const handleChangePassword = (record, index) => {
        console.log("Change Password record==",record, index);
        setSelectedEmployee(record);
        setShowChangePasswordPopup(true);
        // if(record?.mobileNo)
        // history.push(`/digit-ui/employee/pt/user-details/${record?.mobileNo}`);
        // else Toast.error(t("User Mobile Number Not Found"));
    }

    useEffect(() => {
        if(showToast){
            setTimeout(() => {
                setShowToast(null);
            }, 3000);
        }
    }, [showToast]);
    useEffect(() => {
        setCurrentPassword("");
        setNewPassword("");
        setConfirmNewPassword("");
    }, [showChangePasswordPopup]);

    const onSubmitNewPassword = async () => {
        console.log("New Password record==",currentPassword, newPassword, confirmNewPassword);
        if(!currentPassword || !newPassword || !confirmNewPassword){
          alert(1)
            setShowToast({ error: true, warning: true, label: "Please fill all the fields" });
            return;
        }
        if(newPassword !== confirmNewPassword){
            setShowToast({ error: true, warning: true, label: "New Password and Confirm New Password do not match" });
            return;
        }
        console.log("selectedEmployee==", selectedEmployee);
        let emp = {
          userName: "MMPTB",
          tenantId: 'mn',
          type: "EMPLOYEE",
          newPassword: newPassword,
          existingPassword: currentPassword,
          selfUpdate: false
        }
        try {
          const res = await Digit.UserService.updateEmployee(emp);
          console.log("updateEmployee res==", res);
          setShowChangePasswordPopup(false);
          setShowToast({ error: false, warning: false, label: "Password updated successfully" });
        } catch (e) {
          console.log("updateEmployee error==", e);
          setShowToast({ error: true, warning: true, label: e?.response?.data?.error?.message || "Failed to update password" });
        }
        // const res = await Digit.UserService.updateEmployee(emp);
        

        
        // if(record?.mobileNo)
        // history.push(`/digit-ui/employee/pt/user-details/${record?.mobileNo}`);
        // else Toast.error(t("User Mobile Number Not Found"));
    }

    
    return <React.Fragment>
        <div className="dashboard-filter">
        <div className="">
          <form>
            <div id="form-print">
                <div className="card" style={{maxWidth: "100%", display: "flex", justifyContent: "space-between", flexDirection: "row", alignItems: "center"}}>
                    <div style={{ fontSize: "20px", fontWeight: "500", fontFamily: "Open Sans", display: "inline-block" }}>
                      {t("Employee Management")}
                    </div>
                    <button className="btn btn-primary"> Add Employee </button>
                </div>
              
            <div >
                <div className="card" style={{maxWidth: "100%"}}>
                  <div className="row">
                    <div className="col-sm-3" style={{ display: "inline-block" }}>
                      <CardLabel>{`${t("Employee Type")}`}</CardLabel>
                      <Dropdown
                        isMandatory
                        optionCardStyles={{ zIndex: 111111 }}
                        selected={employeeType}
                        optionKey="name"
                        option={employeeTypes}
                        select={setEmployeeType}
                        disable={cityDisable}
                        t={t}
                      />
                    </div>
                    <div className="col-sm-3" style={{ display: "inline-block" }}>
                      <CardLabel>{`${t("User Name")}`}</CardLabel>
                      
                      <TextInput
                        name="userName"
                        id="userName"
                        className="field desktop-w-full"
                        value={userName}
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
                        value={employeeName}
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
                        value={employeeEmail}
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
                        value={employeeMobileNo}
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
                  <hr />
                  
                </div>
              </div>
            </div>

            {/* <div className="card">
                    
                </div> */}
          </form>
        </div>
      </div>
        <div className="">
            <div style={{ width: '100%', padding: "10px", boxShadow: "0px 0px 2px 0px", marginTop: "12px", borderRadius: "5px", background: '#f5f5f5' }} className="mt-4 p-4 box-shadow">
                <p style={{color: "#013272", fontSize: "18px", fontFamily: "Open Sans"}}>{t("Employee List")}</p>
                <table style={{ width: '100%', border: '1px solid #b7b7b7'}}>
                    <thead>
                    <tr style={{background: '#eaeaea', lineHeight: '35px', padding: "0 10px"}}>
                        {tableColumnList.map((e, indx)=>{
                        return (<th key={e.key} style={{paddingLeft: "10px"}}>{e.label}</th>)
                        })}
                        {/* <th style={{paddingLeft: "10px"}}>Sr. No.</th>
                        <th>Property ID</th>
                        <th>Property Type</th>
                        <th>Property Address</th>
                        <th>Owner Details</th> */}
                    </tr>
                    </thead>
                    {filteredData && filteredData.length>0 && filteredData.map((e, indx)=>{
                    return (<tbody key={e?.userName}><tr style={{padding: "0px 10px", borderBottom: "1px solid #bbb9b9"}} key={e?.userName}>
                        <td style={{paddingLeft: "10px", maxWidth: "50px"}}>{indx + 1}</td>
                        {tableColumnList.slice(1,-1).map((clmn, index)=>{
                        return (<td key={clmn.key} style={{paddingLeft: "10px",maxWidth: "140px"}}>{e?.[clmn.key]}</td>)
                        })}
                        <td key={'action'} style={{paddingLeft: "10px",maxWidth: "140px"}}><button className="btn btn-primary" style={{margin: "10px 0px"}} onClick={(el) => handleChangePassword(e, indx)}>Change Password</button></td>
                        {/* <td style={{paddingLeft: "10px",maxWidth: "140px"}}>{e?.propertyId}</td>
                        <td style={{paddingLeft: "10px",maxWidth: "400px"}}><p>{e?.ownerName}</p></td>
                        <td style={{paddingLeft: "10px",maxWidth: "400px"}}><p>{e?.wardNo}</p></td>
                        <td style={{paddingLeft: "10px",maxWidth: "400px"}}><p>{e?.localityName}</p></td>
                        <td style={{paddingLeft: "10px",maxWidth: "160px"}}>{e?.dateOfRegistration}</td> */}
                    </tr></tbody>)
                    })}
                </table>
            </div>
        </div>


        {  showChangePasswordPopup && 
      <PopUp>
        <div style={{width:"auto",textAlign:"-webkit-center",position:"fixed",top:"50%",left:"50%",transform:"translate(-50%,-50%)"}}>
          <Card style={{backgroundColor:"#FAFAFA"}}>
            <div style={{display:"flex",justifyContent:"space-between",alignItems:"center"}}>
              <Header style={{fontSize:"20px"}}>{t("Change Password")}</Header>
              <CloseSvg onClick={()=>setShowChangePasswordPopup(false)} style={{cursor:"pointer"}}/>
            </div>
            <hr style={{margin:"10px 0"}}/>
            <div className="row">
                <div className="col-sm-12" style={{ display: "inline-block" }}>
                  <CardLabel>{`${t("Current Password *")}`}</CardLabel>
                  <TextInput
                    name="currentPassword"
                    id="currentPassword"
                    className="field desktop-w-full"
                    type="password"
                    value={currentPassword}
                    isMandatory={true}
                    onChange={(e) => setCurrentPassword(e.target.value)}
                  />
                </div>
              </div>
              <div className="row">
                <div className="col-sm-12" style={{ display: "inline-block" }}>
                  <CardLabel>{`${t("New Password *")}`}</CardLabel>
                  <TextInput
                    name="newPassword"
                    id="newPassword"
                    className="field desktop-w-full"
                    type="password"
                    value={newPassword}
                    isMandatory={true}
                    onChange={(e) => setNewPassword(e.target.value)}
                  />
                </div>
              </div>
              <div className="row">
                <div className="col-sm-12" style={{ display: "inline-block" }}>
                  <CardLabel>{`${t("Confirm New Password *")}`}</CardLabel>
                  <TextInput
                    name="confirmNewPassword"
                    id="confirmNewPassword"
                    className="field desktop-w-full"
                    type="password"
                    value={confirmNewPassword}
                    isMandatory={true}
                    onChange={(e) => setConfirmNewPassword(e.target.value)}
                  />
                </div>
              </div>
              <div style={{ display: "inline-flex", justifyContent: "end", marginTop: "10px" }}>
                <div style={{ display: "inline" }}>
                  <button className="submit-bar" style={{ color: "white", float: "right", marginLeft: "10px" }} onClick={onSubmitNewPassword}>
                    {t("Submit")}
                  </button>
                </div>
              </div>
          </Card>
        </div>
      </PopUp>
      }

      {showToast && (
        <Toast
          error={showToast.error}
          isDleteBtn={true}
          warning={showToast.warning}
          label={t(showToast.label)}
          onClose={() => {
            setShowToast(null);
            setShowToast(null);
          }}
        />
      )}
        
    </React.Fragment>

}

export default UserManagement