import React, { useState, useEffect, Fragment, useContext } from "react";
import { Dropdown, LabelFieldPair, CardLabel, CardLabelError, CheckBox, DatePicker, Loader, RadioButtons, TextInput, UploadFile, TextArea } from "@egovernments/digit-ui-react-components";

import { useLocation } from "react-router-dom";
import { useFieldArray, Controller, useForm } from "react-hook-form";
import WmsCATextTest from "./WmsCATextTest";
// import { StepContext } from "../../../pages/citizen/CA";
// import {StepContext}
const WmsCATermsAndConditions = ({ t, config, onSelect, formData = {}, userType, /*setError,*/ clearErrors, formState, onBlur, onSubmit, onNext, draftData,formError }) => {
  const { control, getValues, setValue, watch, reset } = useForm(
    {defaultValues:{
        // termAndCondition: [{"terms_and_conditions": ""}],
        termAndCondition: (draftData[0]?.termsAndConditions?.length>0) ? [...draftData[0]?.termsAndConditions] : [{"terms_and_conditions": ""}],
    },}
  );
  const formValue = watch();
  const getData = getValues();
  console.log("onSelect TLInfo formValue ", formValue);
  console.log("getValues ", getValues());
  const { fields, append, remove } = useFieldArray({
    control,
    name: "termAndCondition",
  });
  const tenantId = Digit.ULBService.getCurrentTenantId();

  const { pathname: url } = useLocation();
  const editScreen = url.includes("/modify-application/");
  const { data: citizenTypes, isLoading } = Digit?.Hooks?.wms?.te?.useWMSTEMaster(tenantId, "WMS_DEPARTMENT_ALL_RECORD") || {};
  const [primaryPartyData, SetPrimaryPartyData] = useState([]);

  // const citizenTypes = Digit?.Hooks?.wms?.cm?.useWMSMaster(tenantId, "WMS_V_TYPE") || {};
  // const [citizenType, setcitizenType] = useState(formData?.WmsCATermsAndConditions);
  const [citizenType, setcitizenType] = useState([...draftData]);
  // const [citizenType, setcitizenType] = useState();

  // const [citizenTypeList, setcitizenTypeList] = useState()// it open
  const [citizenTypeList, setcitizenTypeList] = useState([
    {
      //it will delete
      id: 1,
      vendor_type: "Vendor Type 1",
      status: "status1",
    },
    {
      id: 2,
      vendor_type: "Vendor Type 2",
      status: "status2",
    },
  ]);
  const [citizenTypeList_1, setcitizenTypeList_1] = useState([
    {
      //it will delete
      id: 1,
      vendor_name: "Vendor Name 1",
      status: "status_1",
    },
    {
      id: 2,
      vendor_name: "Vendor Name 2",
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
    input === "terms_and_conditions" && setValue("termAndCondition[" + index + "].terms_and_conditions", value);
   
  }

  return (
    <>
    <div style={{backgroundColor:(formError?.contractor) ? "rgb(255 0 0)": "rgb(238 238 238)",marginBottom:"5px",lineHeight: "30px",color:(formError?.contractor) ? "#fff": "#000",fontSize: "12px",fontWeight: "600"}} onClick={()=>onNext(0)}><strong style={{backgroundColor: "#ad2f33",display: "inline-block",color: "#fff",width: "30px",textAlign:"center",fontSize: "20px",lineHeight: "30px",float: "left", marginRight: "5px"}}> + </strong> Party II {'(Contractor)'}</div>
    <div style={{backgroundColor: (formError?.agreement) ? "rgb(255 0 0)":"rgb(238 238 238)",marginBottom:"5px",lineHeight: "30px",color: (formError?.agreement) ? "#fff":"#000",fontSize: "12px",fontWeight: "600"}} onClick={()=>onNext(1)}><strong style={{backgroundColor: "#ad2f33",display: "inline-block",color: "#fff",width: "30px",textAlign:"center",fontSize: "20px",lineHeight: "30px",float: "left", marginRight: "5px"}}> + </strong> Agreement</div>
    <div style={{backgroundColor: "rgb(238 238 238)",marginBottom:"5px",lineHeight: "30px",color: "#000",fontSize: "12px",fontWeight: "600"}} onClick={()=>onNext(2)}><strong style={{backgroundColor: "#ad2f33",display: "inline-block",color: "#fff",width: "30px",textAlign:"center",fontSize: "20px",lineHeight: "30px",float: "left", marginRight: "5px"}}> + </strong> SD / PG / BG Details</div>
    <div style={{backgroundColor:(formError?.partyone) ? "rgb(255 0 0)": "rgb(238 238 238)",marginBottom:"5px",lineHeight: "30px",color:(formError?.partyone) ? "#fff": "#000",fontSize: "12px",fontWeight: "600"}} onClick={()=>onNext(3)}><strong style={{backgroundColor: "#ad2f33",display: "inline-block",color: "#fff",width: "30px",textAlign:"center",fontSize: "20px",lineHeight: "30px",float: "left", marginRight: "5px"}}> + </strong> ULB Party 1</div>
    {/* <div style={{backgroundColor:"rgb(238 238 238)",marginBottom:"5px",lineHeight: "30px",color:"#000",fontSize: "12px",fontWeight: "600"}} onClick={()=>onNext(3)}><strong style={{backgroundColor: "#ad2f33",display: "inline-block",color: "#fff",width: "30px",textAlign:"center",fontSize: "20px",lineHeight: "30px",float: "left", marginRight: "5px"}}> + </strong> ULB Party 1</div> */}
    <div style={{backgroundColor: "rgb(238 238 238)",marginBottom:"5px",lineHeight: "30px",color: "#000",fontSize: "12px",fontWeight: "600"}} onClick={()=>onNext(4)}><strong style={{backgroundColor: "#ad2f33",display: "inline-block",color: "#fff",width: "30px",textAlign:"center",fontSize: "20px",lineHeight: "30px",float: "left", marginRight: "5px"}}> - </strong> Terms and Conditions</div>
      
    <table style={{marginBottom:"15px"}}>
        <thead style={{backgroundColor:"#eee",lineHeight:"40px",textAlign:"center",fontSize:"12px"}} >
          <th style={{border:  "1px solid #b3b3b3", padding:"4px"}} width="20%">Terms & Conditions</th>
          {/* <th style={{border:  "1px solid #b3b3b3", padding:"4px"}} width="20%">Document Description</th>
          <th style={{border:  "1px solid #b3b3b3", padding:"4px"}} width="20%">Upload Document</th> */}
          <th style={{border:  "1px solid #b3b3b3", padding:"4px",textAlign:"center"}} width="10%">Action</th>
        </thead>
        <tbody>
          {/* {(citizenType?.length>0 ? (citizenType[0]?.party2?.contractors) : fields).map((item, index) => ( */}
          {fields?.map((item, index) => (
            <tr key={index}>

              
<td style={{border:  "1px solid #b3b3b3", padding:"4px"}} >
                <Controller
                  name={`termAndCondition[${index}].terms_and_conditions`}
                  control={control}
                  // defaultValue={item.department}
                  defaultValue={item?.terms_and_conditions && item?.terms_and_conditions}
                  
                  render={({ field }) => (
                    <TextArea
                      {...field}
                      key={"terms_and_conditions"}
                      value={formData && formData[config.key] ? formData[config.key][input.name] : undefined}
                      onChange={(e) => setFormData(e.target.value, "terms_and_conditions", index)}
                      // onBlur={blurValue}
                      style={{height:"40px!important",width:"100%",lineHeight:"40px!important"}}
                      disable={false}
                      defaultValue={item?.terms_and_conditions && item?.terms_and_conditions}
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
            <button type="button" style={{backgroundColor:"#b2383c",color:"#fff",padding:"5px 10px"}}  onClick={() => {onSubmit(getData)}}>
              Submit
            </button>{' '}
            <button type="button" style={{backgroundColor:"#ccc",color:"#fff",padding:"5px 10px"}} onClick={() => onNext(3)}>
              Prev
            </button>{' '}
            {/* <button type="button" style={{backgroundColor:"#ccc",color:"#fff",padding:"5px 10px"}} onClick={() => onNext(4)}>
              Next
            </button> */}
            
          </div>
    </>
  );
};
export default WmsCATermsAndConditions;
