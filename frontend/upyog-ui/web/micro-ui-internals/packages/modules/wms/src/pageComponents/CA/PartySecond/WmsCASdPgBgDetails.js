import React, { useState, useEffect, Fragment, useContext } from "react";
import { CardLabelError, CheckBox, DatePicker, Loader, RadioButtons, TextInput, UploadFile } from "@egovernments/digit-ui-react-components";
import { Dropdown, LabelFieldPair, CardLabel } from "@egovernments/digit-ui-react-components";
import { useLocation } from "react-router-dom";
import { useFieldArray, Controller, useForm } from "react-hook-form";
import WmsCATextTest from "./WmsCATextTest";
// import { StepContext } from "../../../pages/citizen/CA";
// import {StepContext}
const WmsCASdPgBgDetails = ({ t, config, onSelect, formData = {}, userType, /*setError,*/ clearErrors, formState, onBlur, onSubmit, onNext, draftData }) => {
  const { control, getValues, setValue, watch, reset } = useForm(
    {defaultValues:{
        // sdpgbg: [{"account_no":"","bank_branch_ifsc_code":"","deposit_amount": "","deposit_type": "","particulars": "","payment_mode": "","valid_from_date": "","valid_till_date": ""}],
        sdpgbg: (draftData[0]?.sDPGBGDetails?.length>0) ? [...draftData[0]?.sDPGBGDetails] : [{"account_no":"","bank_branch_ifsc_code":"","deposit_amount": "","deposit_type": "","particulars": "","payment_mode": "","valid_from_date": "","valid_till_date": ""}],
    },}
  );
  const formValue = watch();
  const getData = getValues();
  console.log("onSelect TLInfo formValue ", formValue);
  console.log("getValues ", getValues());
  const { fields, append, remove } = useFieldArray({
    control,
    name: "sdpgbg",
  });
  const tenantId = Digit.ULBService.getCurrentTenantId();

  const { pathname: url } = useLocation();
  const editScreen = url.includes("/modify-application/");
  const { data: citizenTypes, isLoading } = Digit?.Hooks?.wms?.te?.useWMSTEMaster(tenantId, "WMS_DEPARTMENT_ALL_RECORD") || {};
  const [primaryPartyData, SetPrimaryPartyData] = useState([]);

  // const citizenTypes = Digit?.Hooks?.wms?.cm?.useWMSMaster(tenantId, "WMS_V_TYPE") || {};
  // const [citizenType, setcitizenType] = useState(formData?.WmsCASdPgBgDetails);
  const [citizenType, setcitizenType] = useState([...draftData]);
  // const [citizenType, setcitizenType] = useState();

  // const [citizenTypeList, setcitizenTypeList] = useState()// it open
  const [citizenTypeList, setcitizenTypeList_] = useState([
    {
      //it will delete
      id: 1,
      deposit_type: "Deposit Type 1",
      status: "status1",
    },
    {
      id: 2,
      deposit_type: "Deposit Type 2",
      status: "status2",
    },
  ]);
  const [citizenTypeList_1, setcitizenTypeList_1] = useState([
    {
      //it will delete
      id: 1,
      particulars: "particulars 1",
      status: "status_1",
    },
    {
      id: 2,
      particulars: "particulars 2",
      status: "status_2",
    },
  ]);
  const [citizenTypeList_2, setcitizenTypeList_2] = useState([
    {
      //it will delete
      id: 1,
      BankBranchIFSCCode: "HDFC, NOIDA & HDFC0098",
      status: "status_1",
    },
    {
      id: 2,
      BankBranchIFSCCode: "SBI, NOIDA & SBIN0234",
      status: "status_2",
    },
  ]);
  const [citizenTypeList_3, setcitizenTypeList_3] = useState([
    {
      //it will delete
      id: 1,
      paymentMode: "Bank transfers",
      status: "status_1",
    },
    {
      id: 2,
      paymentMode: "Cheques",
      status: "status_2",
    },
  ]);
  
  const [isTrue, setisTrue] = useState(false);
  function SelectcitizenType(value, name, index) {
    setFormData(value, name, index);
    // setFormData(value, "name", index)
    if (!value?.name) {
      setisTrue(true);
    } else {
      setisTrue(false);
      setcitizenType(value);
    }
  }

  useEffect(() => {
    // setcitizenTypeList(citizenTypes)
    onSelect(config.key, citizenType);
    // onSelect(config.key,
    //   [{
    //     "id":1,
    //     "name": "name1",
    //     "status": "status1"
    //   },{
    //     "id":2,
    //     "name": "name1",
    //     "status": "status1"
    //   }]);
  }, [citizenType]);

  useEffect(() => {
    let fData = [];
    if (citizenTypes?.WMSDepartmentApplications?.length > 0) {
      const filterData = citizenTypes?.WMSDepartmentApplications.filter((res) => res.dept_status == "Active");
      filterData.forEach((element) => {
        fData.push({
          id: element?.dept_id,
          name: element?.dept_name,
          status: element?.dept_status,
        });
      });
      setcitizenTypeList(fData);
    }
  }, [citizenTypes]);


  //########################### text field code start
  function setFormData(value, input, index) {
    console.log("value?.fileStoreId ",value?.fileStoreId)
    input === "deposit_type" && setValue("sdpgbg[" + index + "].deposit_type", value?.deposit_type);
    input === "deposit_amount" && setValue("sdpgbg[" + index + "].deposit_amount", value);
    input === "account_no" && setValue("sdpgbg[" + index + "].account_no", value);
    input === "particulars" && setValue("sdpgbg[" + index + "].particulars", value?.particulars);
    input === "valid_from_date" && setValue("sdpgbg[" + index + "].valid_from_date", value);
    input === "valid_till_date" && setValue("sdpgbg[" + index + "].valid_till_date", value);
    input === "bank_branch_ifsc_code" && setValue("sdpgbg[" + index + "].bank_branch_ifsc_code", value?.bank_branch_ifsc_code);
    input === "payment_mode" && setValue("sdpgbg[" + index + "].payment_mode", value?.payment_mode);
  }

  return (
    <>
    <div style={{backgroundColor: "rgb(238 238 238)",marginBottom:"5px",lineHeight: "30px",color: "#000",fontSize: "12px",fontWeight: "600"}} onClick={()=>onNext(0)}><strong style={{backgroundColor: "#ad2f33",display: "inline-block",color: "#fff",width: "30px",textAlign:"center",fontSize: "20px",lineHeight: "30px",float: "left", marginRight: "5px"}}> + </strong>Party II {'(Contractor)'}</div>
    <div style={{backgroundColor: "rgb(238 238 238)",marginBottom:"5px",lineHeight: "30px",color: "#000",fontSize: "12px",fontWeight: "600"}} onClick={()=>onNext(1)}><strong style={{backgroundColor: "#ad2f33",display: "inline-block",color: "#fff",width: "30px",textAlign:"center",fontSize: "20px",lineHeight: "30px",float: "left", marginRight: "5px"}}> + </strong>Agreement</div>
    <div style={{backgroundColor: "rgb(238 238 238)",marginBottom:"5px",lineHeight: "30px",color: "#000",fontSize: "12px",fontWeight: "600"}} onClick={()=>onNext(2)}><strong style={{backgroundColor: "#ad2f33",display: "inline-block",color: "#fff",width: "30px",textAlign:"center",fontSize: "20px",lineHeight: "30px",float: "left", marginRight: "5px"}}> - </strong>SD / PG / BG Details</div>
      <table style={{marginBottom:"15px"}}>
        <thead style={{backgroundColor:"#eee",lineHeight:"40px",textAlign:"center",fontSize:"12px"}} >
          <th style={{border:  "1px solid #b3b3b3", padding:"4px"}} width="12%">Deposit type</th>
          <th style={{border:  "1px solid #b3b3b3", padding:"4px"}} width="10%">Deposit Amount</th>
          <th style={{border:  "1px solid #b3b3b3", padding:"4px"}} width="10%">Account No</th>
          <th style={{border:  "1px solid #b3b3b3", padding:"4px"}} width="12%">Particulars</th>
          <th style={{border:  "1px solid #b3b3b3", padding:"4px"}} width="12%">Valid from date</th>
          <th style={{border:  "1px solid #b3b3b3", padding:"4px"}} width="12%">Valid till date</th>
          <th style={{border:  "1px solid #b3b3b3", padding:"4px"}} width="12%">Bank, Branch & IFSC Code</th>
          <th style={{border:  "1px solid #b3b3b3", padding:"4px"}} width="12%">Payment Mode</th>
          <th style={{border:  "1px solid #b3b3b3", padding:"4px",textAlign:"center"}} width="8%">Action</th>
        </thead>
        <tbody>
          {/* {(citizenType?.length>0 ? (citizenType[0]?.party2?.contractors) : fields).map((item, index) => ( */}
          {fields?.map((item, index) => (
            <tr key={index}>
              <td style={{border:  "1px solid #b3b3b3", padding:"4px"}} >
                <Controller
                  name={`sdpgbg[${index}].deposit_type`}
                  control={control}
                  // defaultValue={item.name}
                  // defaultValue={item?.deposit_type && {"deposit_type":item?.deposit_type}}
                  defaultValue={item?.deposit_type && item?.deposit_type}
                  render={({ field }) => (
                    <Dropdown
                      {...field}
                      // className="form-field"
                      selected={item?.deposit_type && item}
                      // option={citizenTypeList?.["egov-hrms"]?.item}
                      option={citizenTypeList != undefined && citizenTypeList}
                      // option={vendorType}
                      select={(e) => SelectcitizenType(e, "deposit_type", index)}
                      // onBlur={()=>SelectcitizenType(val,index)}
                      // onChange={(e) => setFormData(value?.name, 'name', index)}
                      // onChange={(e) => setFormData(value?.name, 'name', index)}
                      optionKey="deposit_type"
                      // optionKey={`party2.contractor[${index}].deposit_type`}

                      defaultValue={undefined}
                      placeholder="Select Deposit Type"
                      // t={t}
                    />
                  )}
                />
              </td>
              <td style={{border:  "1px solid #b3b3b3", padding:"4px"}} >
                <Controller
                  name={`sdpgbg[${index}].deposit_amount`}
                  control={control}
                  // defaultValue={item.department}
                  defaultValue={item?.deposit_amount && item?.deposit_amount}
                  
                  render={({ field }) => (
                    <TextInput
                      {...field}
                      key={"deposit_amount"}
                      value={formData && formData[config.key] ? formData[config.key][input.name] : undefined}
                      onChange={(e) => setFormData(e.target.value, "deposit_amount", index)}
                      // onBlur={blurValue}
                      style={{height:"40px!important",width:"100%",lineHeight:"40px!important"}}
                      disable={false}
                      defaultValue={item?.deposit_amount && item?.deposit_amount}
                    />
                  )}
                />
              </td>
              <td style={{border:  "1px solid #b3b3b3", padding:"4px"}} >
                <Controller
                  name={`sdpgbg[${index}].account_no`}
                  control={control}
                  // defaultValue={item.department}
                  defaultValue={item?.account_no && item?.account_no}
                  
                  render={({ field }) => (
                    <TextInput
                      {...field}
                      key={"account_no"}
                      value={formData && formData[config.key] ? formData[config.key][input.name] : undefined}
                      onChange={(e) => setFormData(e.target.value, "account_no", index)}
                      // onBlur={blurValue}
                      style={{height:"40px!important",width:"100%",lineHeight:"40px!important"}}
                      disable={false}
                      defaultValue={item?.account_no && item?.account_no}
                    />
                  )}
                />
              </td>
              <td style={{border:  "1px solid #b3b3b3", padding:"4px"}} >
                <Controller
                  name={`sdpgbg[${index}].particulars`}
                  control={control}
                  // defaultValue={item.name}
                  // defaultValue={item?.particulars && {"particulars":item?.particulars}}
                  defaultValue={item?.particulars && item?.particulars}
                  render={({ field }) => (
                    <Dropdown
                      {...field}
                      // className="form-field"
                      // selected={{particulars:"Vendor Name 2"}}
                      selected={item?.particulars && item}
                      // option={citizenTypeList?.["egov-hrms"]?.item}
                      option={citizenTypeList_1 != undefined && citizenTypeList_1}
                      // option={vendorType}
                      select={(e) => SelectcitizenType(e, "particulars", index)}
                      onBlur={(e)=> SelectcitizenType(e, "particulars", index)}
                      // onChange={(e) => setFormData(value?.name, 'name', index)}
                      // onChange={(e) => setFormData(value?.name, 'name', index)}
                      optionKey="particulars"
                      // optionKey={`contractor[${index}].name`}

                      defaultValue={undefined}
                      placeholder="Select Particulars"
                      // t={t}
                    />
                  )}
                />
              </td>

              <td style={{border:  "1px solid #b3b3b3", padding:"4px"}} >
              <Controller
                  name={`sdpgbg[${index}].valid_from_date`}
                  control={control}
                  defaultValue={item.valid_from_date && item.valid_from_date}
                  render={({field,value,onChange}) => <DatePicker date={value} onChange={(data)=>setFormData(data, "valid_from_date", index)} />}
                />
                </td>
                <td style={{border:  "1px solid #b3b3b3", padding:"4px"}} >
              <Controller
                  name={`sdpgbg[${index}].valid_till_date`}
                  control={control}
                  defaultValue={item.valid_till_date && item.valid_till_date}
                  render={({field,value,onChange}) => <DatePicker date={value} onChange={(data)=>setFormData(data, "valid_till_date", index)} />}
                />
                </td>

                <td style={{border:  "1px solid #b3b3b3", padding:"4px"}} >
                <Controller
                  name={`sdpgbg[${index}].bank_branch_ifsc_code`}
                  control={control}
                  // defaultValue={item.name}
                  // defaultValue={item?.bank_branch_ifsc_code && {"bank_branch_ifsc_code":item?.bank_branch_ifsc_code}}
                  defaultValue={item?.bank_branch_ifsc_code && item?.bank_branch_ifsc_code}
                  render={({ field }) => (
                    <Dropdown
                      {...field}
                      // className="form-field"
                      selected={item?.bank_branch_ifsc_code && item}
                      // option={citizenTypeList?.["egov-hrms"]?.item}
                      option={citizenTypeList_2 != undefined && citizenTypeList_2}
                      // option={vendorType}
                      select={(e) => SelectcitizenType(e, "bank_branch_ifsc_code", index)}
                      // onBlur={()=>SelectcitizenType(val,index)}
                      // onChange={(e) => setFormData(value?.name, 'name', index)}
                      // onChange={(e) => setFormData(value?.name, 'name', index)}
                      optionKey="BankBranchIFSCCode"
                      // optionKey={`party2.contractor[${index}].bank_branch_ifsc_code`}

                      defaultValue={undefined}
                      placeholder="Select Bank Branch & IFSC Code"
                      // t={t}
                    />
                  )}
                />
              </td>

              <td style={{border:  "1px solid #b3b3b3", padding:"4px"}} >
                <Controller
                  name={`sdpgbg[${index}].payment_mode`}
                  control={control}
                  // defaultValue={item.name}
                  // defaultValue={item?.payment_mode && {"payment_mode":item?.payment_mode}}
                  defaultValue={item?.payment_mode && item?.payment_mode}
                  render={({ field }) => (
                    <Dropdown
                      {...field}
                      // className="form-field"
                      selected={item?.payment_mode && item}
                      // option={citizenTypeList?.["egov-hrms"]?.item}
                      option={citizenTypeList_3 != undefined && citizenTypeList_3}
                      // option={vendorType}
                      select={(e) => SelectcitizenType(e, "payment_mode", index)}
                      // onBlur={()=>SelectcitizenType(val,index)}
                      // onChange={(e) => setFormData(value?.name, 'name', index)}
                      // onChange={(e) => setFormData(value?.name, 'name', index)}
                      optionKey="paymentMode"
                      // optionKey={`party2.contractor[${index}].payment_mode`}

                      defaultValue={undefined}
                      placeholder="Select Payment Mode"
                      // t={t}
                    />
                  )}
                />
              </td>
              <td style={{border:  "1px solid #b3b3b3", padding:"4px",textAlign:"center"}}>
                {index!==0&&<button type="button" style={{backgroundColor:"#b2383c",color:"#fff",borderRadius:"4px",padding:"5px 10px"}} onClick={() => remove(index)}>
                  Delete
                </button>}
              </td>
            </tr>
          ))}
          {/* {isTrue&&<CardLabelError style={{ width: "100%", marginTop: '-15px', fontSize: '16px', marginBottom: '12px'}}>{"Require Field"}</CardLabelError>} */}
          

        </tbody>
      </table>
      <div  style={{textAlign:"right"}}>
      <button style={{backgroundColor:"#573d6c",color:"#fff",borderRadius:"4px",padding:"5px 10px"}}
            type="button"
            onClick={() => {
              append({ name: `item ${fields.length + 1}` });
            }}
          >
            Add
          </button>
      </div>
      
      <div style={{textAlign:"center",marginBottom:"15px"}}>
            <button type="button" style={{backgroundColor:"#b2383c",color:"#fff",padding:"5px 10px"}}  onClick={() => onSubmit(getData)}>
            Save as Draft
            </button>{' '}
            <button type="button" style={{backgroundColor:"#ccc",color:"#fff",padding:"5px 10px"}} onClick={() => onNext(1)}>
              Prev
            </button>{' '}
            <button type="button" style={{backgroundColor:"#ccc",color:"#fff",padding:"5px 10px"}} onClick={() => onNext(3)}>
              Next
            </button>
            
          </div>
          <div style={{backgroundColor: "rgb(238 238 238)",marginBottom:"5px",lineHeight: "30px",color: "#000",fontSize: "12px",fontWeight: "600"}} onClick={()=>onNext(3)}><strong style={{backgroundColor: "#ad2f33",display: "inline-block",color: "#fff",width: "30px",textAlign:"center",fontSize: "20px",lineHeight: "30px",float: "left", marginRight: "5px"}}> + </strong> ULB Party 1</div>
          <div style={{backgroundColor: "rgb(238 238 238)",marginBottom:"5px",lineHeight: "30px",color: "#000",fontSize: "12px",fontWeight: "600"}} onClick={()=>onNext(4)}><strong style={{backgroundColor: "#ad2f33",display: "inline-block",color: "#fff",width: "30px",textAlign:"center",fontSize: "20px",lineHeight: "30px",float: "left", marginRight: "5px"}}> + </strong>Terms and Conditions</div>
    </>
  );
};
export default WmsCASdPgBgDetails;
