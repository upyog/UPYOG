import React, { useState, useEffect, Fragment, useContext } from "react";
import { CardLabelError, CheckBox, Loader, RadioButtons, TextInput, UploadFile } from "@egovernments/digit-ui-react-components";
import { Dropdown, LabelFieldPair, CardLabel } from "@egovernments/digit-ui-react-components";
import { useLocation } from "react-router-dom";
import { useFieldArray, Controller, useForm } from "react-hook-form";
import WmsCATextTest from "./WmsCATextTest";
// import { StepContext } from "../../../pages/citizen/CA";
// import {StepContext}
const WmsCAContractor = ({ t, config, onSelect, formData = {}, userType, /*setError, clearErrors,*/ onBlur, onSubmit, onNext, draftData,formError }) => {
  // const {formData}=useContext(StepContext)

  console.log("onSelect Contractor TLInfo ", {
    draftData,
    "draftData?.length": draftData?.length,
    config,
    formData,
    userType /*,setError, clearErrors*/,
    onBlur,
    onSelect,
  });
  // console.log("draftData[0]?.contractors ", [...draftData[0]?.contractors]);

  const {
    control,
    register,
    handleSubmit,
    getValues,
    setValue,
    clearErrors,
    setError: setErrors,
    trigger,
    watch,
    reset,
    formState: { errors },
  } = useForm(
    // {defaultValues:()=>{console.log("dData in ",dData), {"party2":{"contractors":[...dData]}}}}
    // {defaultValues:{"party2":{"contractors":[...dData]}}}
    // (draftData && draftData?.length>0) ? ([...draftData[0]?.party2?.contractors], [...draftData[0]?.party2?.party2_witness]):([{ vendor_type: "", vendor_name: "", primary_party: "", represented_by: "", upload_doc: "" }],[{ witness_name: "", address: "", aadhaarNo: "", upload_thumb: "" }])
    //   defaultValues: ()=>{
    //     (draftData && draftData?.length>0) ? ([...draftData[0]?.party2?.contractors], [...draftData[0]?.party2?.party2_witness]):([{ vendor_type: "", vendor_name: "", primary_party: "", represented_by: "", upload_doc: "" }],[{ witness_name: "", address: "", aadhaarNo: "", upload_thumb: "" }])
    // },

    {
      defaultValues: {
        // party2:{
        contractors:
          draftData[0]?.contractors?.length > 0
            ? [...draftData[0]?.contractors]
            : [{ vendor_type: "", vendor_name: "", primary_party: "", represented_by: "" /*, upload_doc: ""*/ }],
        // contractors: (draftData[0]?.contractors?.length>0) ? [{ vendor_type:draftData[0]?.contractors?.vendor_type, vendor_name:draftData[0]?.contractors?.vendor_name, primary_party:draftData[0]?.contractors?.primary_party, represented_by:draftData[0]?.contractors?.represented_by/*, upload_doc:draftData[0]?.contractors?.upload_doc*/}] : [{ vendor_type: "", vendor_name: "", primary_party: "", represented_by: ""/*, upload_doc: ""*/ }],
        // contractors: (draftData[0]?.contractors?.length>0) ? draftData[0]?.contractors?.map(item=>({ "vendor_type":item.vendor_type, "vendor_name":item.vendor_name, "primary_party":item.primary_party, "represented_by":item.represented_by/*, "upload_doc":"Vendor Type 1"*/}))
        //  : [{ vendor_type: "", vendor_name: "", primary_party: "", represented_by: ""/*, upload_doc: ""*/ }],
        // citizenType?.length>0 ? (citizenType[0]?.party2?.contractors
        // party2_witness: [{ name: "", address: "", aadhaarNo: "", upload_thumb: "" }],
        party2_witness:
          draftData[0]?.party2_witness?.length > 0
            ? [...draftData[0]?.party2_witness]
            : [{ witness_name: "", address: "", aadhaarNo: "" /*, upload_thumb: "" */ }],
        // }
      },
      mode: "all",
    }
  );

  // const formErrors = formState?.errors;
  const formValue = watch();
  const getData = getValues();
  // console.log("onSelect TLInfo formValue ", formValue);
  // console.log("getValues ", getValues());
  console.log("getValues getData", getData);
  const { fields: party2fields, append: party2append, remove: party2remove } = useFieldArray({
    control,
    name: "contractors",
  });

  const { fields: party2_witness_fields, append: party2_witness_append, remove: party2_witness_remove } = useFieldArray({
    control,
    name: "party2_witness",
  });
  useEffect(() => {
    onNext(0);
  }, []);
  // console.log("party2fields ds ",party2fields)

  // useEffect(()=>{
  // setValue("contractors.vendor_type","Vendor Type 2",{shouldValidate:true,shouldDirty:true,shouldTouch:true})
  // input === "vendor_type" && setValue("party2.contractors[" + index + "].vendor_type", value?.vendor_type);
  // if(draftData?.length>0){
  //   setValue("vendor_type","Vendor Type 2")
  //   // setValue("vendor_type",draftData[0]?.party2?.contractors?.vendor_type)
  // }
  // { vendor_type: "", vendor_name: "", primary_party: "", represented_by: "", upload_doc: "" }

  //     if(draftData?.length){
  //       const indexs=0;
  //     alert("true")
  //     setValue("party2.contractors["+0+"].vendor_type","Vendor Type 2");
  //     setValue("party2.contractors["+0+"].vendor_name","Vendor Name 1");
  //     setValue("party2.contractors["+0+"].represented_by","ssssssssss");

  //     setValue("contractors["+0+"].vendor_type","Vendor Type 2");
  //     setValue("contractors["+0+"].vendor_name","Vendor Name 1");
  //     setValue("contractors["+0+"].represented_by","yyyyyyyy");

  //     setValue("contractors[0].vendor_type","Vendor Type 2");
  //     setValue("contractors[0].vendor_name","Vendor Name 1");
  //     setValue("contractors[0].represented_by","jjjjj");

  //     setValue("contractors["+indexs+"].vendor_type","Vendor Type 2");
  //     setValue("contractors["+indexs+"].vendor_name","Vendor Name 1");
  //     setValue("contractors["+indexs+"].represented_by","yyyyyyyy");

  //     // setValue("contractors.vendor_type","Vendor Type 2",{shouldValidate:true,shouldDirty:true,shouldTouch:true})

  //     setDdata([...draftData[0]?.party2?.contractors])
  //   }else{
  //     alert("false")
  // setDdata([{ vendor_type: "Vendor Type 2", vendor_name: "", primary_party: "", represented_by: "", upload_doc: "" }],[{ witness_name: "", address: "", aadhaarNo: "", upload_thumb: "" }])
  //   }

  // },[draftData?.length])

  const tenantId = Digit.ULBService.getCurrentTenantId();

  const { pathname: url } = useLocation();
  const editScreen = url.includes("/modify-application/");
  const { data: citizenTypes, isLoading } = Digit?.Hooks?.wms?.te?.useWMSTEMaster(tenantId, "WMS_DEPARTMENT_ALL_RECORD") || {};
  const [primaryPartyData, SetPrimaryPartyData] = useState([]);

  // const citizenTypes = Digit?.Hooks?.wms?.cm?.useWMSMaster(tenantId, "WMS_V_TYPE") || {};
  // const [citizenType, setcitizenType] = useState(formData?.WmsCAContractor);
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

  //   const vendorType = [
  //     {
  //         "code": "CONTRACT_MASTER_CLASSA",
  //         "name": "CLASS A",
  //         "module": "rainmaker-tl",
  //         "locale": "en_IN"
  //     },
  //     {
  //         "code": "CONTRACT_MASTER_CLASSB",
  //         "name": "CLASS B",
  //         "module": "rainmaker-tl",
  //         "locale": "en_IN"
  //     },
  //     {
  //         "code": "CONTRACT_MASTER_CLASSC",
  //         "name": "CLASS C",
  //         "module": "rainmaker-tl",
  //         "locale": "en_IN"
  //     },
  // ];
  // const inputs = [
  //   {
  //     label: "WMS_TE_COMMON_DEPARTMENT",
  //     type: "text",
  //     name: "department",
  //     validation: {
  //       isRequired: true,
  //       title: "Vendor Type Require",

  //     },
  //     isMandatory: true,
  //   },
  // ];

  //################### upload file
  const tenantIdCode = Digit.ULBService.getCurrentTenantId();
  const [documents, setDocuments] = useState(formData?.supportingDocuments?.[0] ? [formData?.supportingDocuments?.[0]] : []);
  const [file, setFile] = useState(null);
  const [uploadedFile, setUploadedFile] = useState(null);
  const [error, setError] = useState(null);

  useEffect(() => {
    const uploadFu = (async () => {
      setError(null);
      if (file?.file) {
        const allowedFileTypesRegex = /(.*?)(jpg|jpeg|png|image|pdf)$/i;
        if (file?.file.size >= 5242880) {
          setError("CS_MAXIMUM_UPLOAD_SIZE_EXCEEDED");
          // setError(t("CS_MAXIMUM_UPLOAD_SIZE_EXCEEDED"));
        } else if (file?.file?.type && !allowedFileTypesRegex.test(file?.file?.type)) {
          // setError(t(`NOT_SUPPORTED_FILE_TYPE`))
          setError(`NOT_SUPPORTED_FILE_TYPE`);
        } else {
          // try {
          // var formdata = new FormData();
          // formdata.append("file", file);
          // formdata.append("tenantId", "pg");
          // formdata.append("module", "wms");
          // let  requestOptions = {
          //   method: 'POST',
          //   body: formdata,
          //   redirect: 'follow'
          // };

          // fetch("http://10.216.36.152:8484/wms/wms-services/v1/tenderentry/_upload", requestOptions)
          // .then(response => response.text())
          // .then(result => {
          //   return (
          //       setUploadedFile(result),

          //       localStorage.setItem("imagePath",result),
          //   console.log("image uploaded ", result)
          // )})
          // .catch(error => console.log('errorrrrrr ', error));
          // } catch (error) {
          // alert("Something wrong!")
          // }

          {
            /*
              IT ONLY WILL BE USE FOR IMAGE UPLOAD
              */
          }
          try {
            // C:\UpyogProjectDev\UPYOG-LIVE\UPYOG\frontend\upyog-ui\web\micro-ui-internals\packages\libraries\src\services\atoms\UploadServices.js
            const response = await Digit.UploadServices.Filestorage("WS", file?.file, tenantIdCode);
            console.log("response file ", response);
            if (response?.data?.files?.length > 0) {
              setFormData(response?.data?.files[0], file?.name, file?.index);

              // setUploadedFile(response?.data?.files[0]);
              // localStorage.setItem("imagePath",JSON.stringify(response?.data?.files[0]?.fileStoreId))
              // setUploadedFile(response?.data?.files[0]?.fileStoreId);
            } else {
              // setError(t("CS_FILE_UPLOAD_ERROR"));
              setError("CS_FILE_UPLOAD_ERROR ELSE");
            }
          } catch (err) {
            // setError(t("CS_FILE_UPLOAD_ERROR"));
            setError("CS_FILE_UPLOAD_ERROR CATCH");
          }
        }
      }
    })();
    return () => uploadFu;
  }, [file?.file]);

  useEffect(() => {
    if (uploadedFile) {
      setDocuments((prev) => {
        return [{ fileStoreId: uploadedFile, documentUid: uploadedFile }];
      });
    }
  }, [uploadedFile]);

  function selectfile(e, name, index) {
    // setFile(e.target.files[0]);
    setFile({ file: e.target.files[0], name, index });
  }
  useEffect(() => {
    onSelect(config.key, { ...formData[config.key], ...documents });
  }, [documents]);
  // useEffect(()=>{
  //   party2append({ name: `item ${party2fields.length + 1}` });
  //   party2_witness_append({ name: `item ${party2_witness_fields.length + 1}` });
  // },[])

  //########################### text field code start
  function setFormData(value, input, index) {
    console.log("getValues getData setFormData ", { value, input, index });
    input === "represented_by" && setValue("party2.contractors[" + index + "].represented_by", value);
    input === "vendor_type" && setValue("party2.contractors[" + index + "].vendor_type", value?.vendor_type);
    input === "vendor_name" && setValue("party2.contractors[" + index + "].vendor_name", value?.vendor_name);
    input === "primary_party" &&
      (setValue("party2.contractors[" + index + "].primary_party", value),
      SetPrimaryPartyData((prevState) => {
        const newArray = [...prevState];
        newArray[index] = value;
        return newArray;
      }));
      input === "witness_name" && setValue("party2.party2_witness[" + index + "].witness_name", value);
      input === "address" && setValue("party2.party2_witness[" + index + "].address", value);
      input === "aadhaarNo" && setValue("party2.party2_witness[" + index + "].aadhaarNo", value);
      // input === "upload_thumb" && setValue("party2.party2_witness[" + index + "].upload_thumb", value?.fileStoreId);
  
    // input === "upload_doc" && setValue("party2.contractors[" + index + "].upload_doc", value?.fileStoreId!==undefined?value?.fileStoreId:"");

   
    // uploadedFile >= 0 && setValue('contractor['+index+'].upload_doc',uploadedFile.fileStoreId)
    // if(value){setisTrue(false)}else{setisTrue(true)}
    // clearErrors("party2.contractors[" + index + "]."+input)
    // clearErrors("party2.party2_witness[" + index + "]."+input)
    // setErrors("party2.contractors[" + index + "]."+input,{})
    trigger("party2.contractors["+index+"]."+input);
    // if (!value) {
    //   console.log("party2.party2_witness["+index+"]." + input)
    //   // trigger(["party2.contractors[" + index + "]." + input, "party2.party2_witness[" + index + "]." + input]);
    //   trigger("party2.party2_witness["+index+"]."+input);
    // }
  }

  // useEffect(()=>{
  //   onSelect(config.key, { ...formData[config.key], ...owners})
  // },[owners])
  //########################### text field code end
  // if (isLoading) {
  //   return <Loader />;
  // }

  const submitData = (data) => {
    onSubmit(data);
  };
  useEffect(()=>{if(formError?.contractor){trigger();
    // setTimeout(()=>{if(Object.keys(errors).length > 0){formError(errors)}},1000)
    
  };
    console.log("Clicked another comp contractor formError ",formError)},[formError])
  return (
    <>
      <div
        style={{ backgroundColor: (formError?.contractor) ? "rgb(255 0 0)":"rgb(238 238 238)", marginBottom: "5px", lineHeight: "30px", color: (formError?.contractor)?"#fff":"#000", fontSize: "12px", fontWeight: "600" }}
      >
        <strong
          style={{
            backgroundColor: "#ad2f33",
            display: "inline-block",
            color: "#fff",
            width: "30px",
            textAlign: "center",
            fontSize: "20px",
            lineHeight: "30px",
            float: "left",
            marginRight: "5px",
          }}
        >
          {" "}
          -{" "}
        </strong>{" "}
        Party II {"(Contractor)"}
      </div>
      <form onSubmit={handleSubmit(submitData)}>
        <table style={{ marginBottom: "15px" }}>
          <thead style={{ backgroundColor: "#eee", lineHeight: "40px", textAlign: "center", fontSize: "12px" }}>
            <th style={{ border: "1px solid #b3b3b3", padding: "4px" }} width="20%">
              Party Type <sup style={{ color: "red" }}>*</sup>
            </th>
            <th style={{ border: "1px solid #b3b3b3", padding: "4px" }} width="20%">
              Party Name <sup style={{ color: "red" }}>*</sup>
            </th>
            <th style={{ border: "1px solid #b3b3b3", padding: "4px" }} width="20%">
              Represented By{" "}
            </th>
            <th style={{ border: "1px solid #b3b3b3", padding: "4px" }} width="10%">
              Primary Party <sup style={{ color: "red" }}>*</sup>
            </th>
            {/* <th style={{border:  "1px solid #b3b3b3", padding:"4px"}} width="20%">Photo/Thumb/Upload Document</th> */}
            <th style={{ border: "1px solid #b3b3b3", padding: "4px", textAlign: "center" }} width="10%">
              Action
            </th>
          </thead>
          <tbody>
            {/* {(citizenType?.length>0 ? (citizenType[0]?.party2?.contractors) : party2fields).map((item, index) => ( */}
            {party2fields?.map((item, index) => (
              <tr key={index}>
                <td style={{ border: "1px solid #b3b3b3", padding: "4px" }}>
                  {/* <Controller
              name={`contractor[${index}].email`}
              control={control}
              defaultValue={item.email}
              render={({ field }) => 
              <TextInput
                {...field}
                key={"email"}

                value={formData && formData[config.key] ? formData[config.key][input.name] : undefined}

                onChange={(e) => setFormData(e.target.value, 'email', index)}
                // onBlur={blurValue}

                disable={false}

                defaultValue={undefined}

                {...input.validation}

              />}
            /> */}
                  <Controller
                    name={`party2.contractors[${index}].vendor_type`}
                    control={control}
                    rules={{ required: "Required field" }}

                    // defaultValue={item?.vendor_type && {"vendor_type":item?.vendor_type}}
                    defaultValue={item?.vendor_type && item?.vendor_type}
                    render={({ field }) => (
                      <>
                        <Dropdown
                          {...field}
                          // className="form-field"
                          selected={item?.vendor_type && item}
                          // option={citizenTypeList?.["egov-hrms"]?.item}
                          option={citizenTypeList != undefined && citizenTypeList}
                          // option={vendorType}
                          select={(e) => SelectcitizenType(e, "vendor_type", index)}
                          onBlur={(e) => SelectcitizenType(e, "vendor_type", index)}
                          // onChange={(e) => setFormData(value?.name, 'name', index)}
                          // onChange={(e) => setFormData(value?.name, 'name', index)}
                          optionKey="vendor_type"
                          // optionKey={`party2.contractor[${index}].vendor_type`}

                          defaultValue={undefined}
                          placeholder="Select Vendor Type"
                          // t={t}
                        />
                        {Object.keys(errors).length === 0
                      ? ""
                      : errors?.party2?.contractors[index]?.vendor_type?.type==="required" && 
                      <CardLabelError>{errors?.party2?.contractors[index]?.vendor_type?.message}</CardLabelError>
                      }
                      </>
                    )}
                  />
                  {/* {Object.keys(errors).length === 0
                    ? ""
                    : errors?.party2?.contractors[index]?.vendor_type?.type === "required" && (
                        <CardLabelError>{errors?.party2?.contractors[index]?.vendor_type?.message}</CardLabelError>
                      )} */}
                </td>
                <td style={{ border: "1px solid #b3b3b3", padding: "4px" }}>
                  <Controller
                    name={`party2.contractors[${index}].vendor_name`}
                    control={control}
                    rules={{ required: "Required field" }}
                    // defaultValue={item.name}
                    // defaultValue={item?.vendor_name && {"vendor_name":item?.vendor_name}}
                    defaultValue={item?.vendor_name && item?.vendor_name}
                    render={({ field }) => (
                      <>
                        <Dropdown
                          {...field}
                          // className="form-field"
                          // selected={{vendor_name:"Vendor Name 2"}}
                          selected={item?.vendor_name && item}
                          // option={citizenTypeList?.["egov-hrms"]?.item}
                          option={citizenTypeList_1 != undefined && citizenTypeList_1}
                          // option={vendorType}
                          select={(e) => SelectcitizenType(e, "vendor_name", index)}
                          onBlur={(e) => SelectcitizenType(e, "vendor_name", index)}
                          // onChange={(e) => setFormData(value?.name, 'name', index)}
                          // onChange={(e) => setFormData(value?.name, 'name', index)}
                          optionKey="vendor_name"
                          // optionKey={`contractor[${index}].name`}

                          defaultValue={undefined}
                          placeholder="Select Vendor Name"
                          // t={t}
                        />
                        {Object.keys(errors).length === 0
                          ? ""
                          : errors?.party2?.contractors[index]?.vendor_name?.type === "required" && (
                              <CardLabelError>{errors?.party2?.contractors[index]?.vendor_name?.message}</CardLabelError>
                            )}

                        {/* <p>{errors?.(`vendor_name`) && errors?.(`vendor_name`).message}</p> */}
                      </>
                    )}
                  />
                </td>
                <td style={{ border: "1px solid #b3b3b3", padding: "4px" }}>
                  <Controller
                    name={`party2.contractors[${index}].represented_by`}
                    control={control}
                    
                    // defaultValue={item.department}
                    defaultValue={item?.represented_by && item?.represented_by}
                    rules={{ required: "Required field" }}
                    render={(props) => (
                      <>
                        <TextInput
                          {...props}
                          // // {...register(`party2.contractors[${index}].represented_by`)}
                          // name={`party2.contractors[${index}].represented_by`}
                          // ref={register()}
                          // key={"represented_by"}
                          // // value={formData && formData[config.key] ? formData[config.key][input.name] : undefined}
                          onChange={(e) => setFormData(e.target.value, "represented_by", index)}
                          errorStyle={errors?.party2?.contractors[index]?.represented_by}
                          // // onBlur={blurValue}
                          // style={{height:"40px!important",width:"100%",lineHeight:"40px!important"}}
                          // disable={false}
                          // defaultValue={item?.represented_by && item?.represented_by}
                        />
                        {Object.keys(errors).length === 0
                          ? ""  
                            : errors?.party2?.contractors[index]?.represented_by?.type === "required" && (
                              <CardLabelError>{errors?.party2?.contractors[index]?.represented_by?.message}</CardLabelError>
                          )}
                        {/* {console.log("fieldState ",fieldState)}
                    {fieldState?.error && (
                      <span>{fieldState.error?.message}</span>
                    )} */}
                      </>
                    )}
                  />

                </td>
                <td style={{ border: "1px solid #b3b3b3", padding: "4px" }} textAlign="center">
                  <Controller
                    name={`party2.contractors[${index}].primary_party`}
                    control={control}
                    rules={{required: "Required field"}}
                    // defaultValue={item.department}
                    defaultValue={item?.primary_party && item?.primary_party}
                    render={({ field, fieldState }) => (
                      <>
                        <RadioButtons
                          {...field}
                          key={"primary_party"}
                          // selectedOption={primaryPartyData[index]}
                          selectedOption={item?.primary_party ? item?.primary_party : primaryPartyData[index]}
                          // onSelect={()=>SetPrimaryPartyData("two")}
                          onSelect={() => setFormData("Primary Party", "primary_party", index)}
                          // options={[primaryPartyData]}
                          // options={["CS_PAYMENT_FULL_AMOUNT","two","three"]}
                          options={["Primary Party"]}
                          inputStyle={{ display: "none" }}
                          // innerStyles={{ display: "inline-block", margin: "0px auto" }}
                          style={{ margin: "auto", width: "40px" }}
                        />
                      
                      {Object.keys(errors).length === 0
                    ? ""
                    : errors?.party2?.contractors[index]?.primary_party?.type === "required" && (
                        <CardLabelError>{errors?.party2?.contractors[index]?.primary_party?.message}</CardLabelError>
                      )}
                      </>
                    )}
                  />
                  

                  {/* {formErrors && formErrors?.primary_party && formErrors?.primary_party?.type === "required" && (
                    <CardLabelError>{`ddddddddddddddddddddddddddddddddddd`}</CardLabelError>)} */}
                  {/* <p>{errors?.(`party2.contractors[${index}].primary_party`) && errors?.(`party2.contractors[${index}].primary_party`).message}</p> */}

                  {/* {errors?.(`party2.contractors[${index}].primary_party`) && (<span>ssssssssssssssssssssss</span>)} */}
                </td>
                {/* <td style={{border:  "1px solid #b3b3b3", padding:"4px"}} >
                <div className="field">
                  <Controller
                    name={`party2.contractors[${index}].upload_doc`}
                    control={control}
                    defaultValue={item.upload_doc && item.upload_doc}
                    render={({ field }) => (
                      <UploadFile
                        {...field}
                        key={"upload_doc"}
                        // id={id}
                        onUpload={(e) => {
                          selectfile(e, "upload_doc", index);
                        }}
                        // onUpload={(e) => { setFormData(uploadedFile, "upload_doc",index) }}
                        onDelete={() => {
                          setUploadedFile(null);
                        }}
                        message={uploadedFile ? `1 ${`CS_ACTION_FILEUPLOADED`}` : `CS_ACTION_NO_FILEUPLOADED`}
                        // message={uploadedFile ? `1 ${t(`CS_ACTION_FILEUPLOADED`)}` : t(`CS_ACTION_NO_FILEUPLOADED`)}
                        textStyles={{ width: "100%" }}
                        inputStyles={{ width: "250px" }}
                        buttonType="button"
                        accept="image/*, .pdf, .png, .jpeg, .jpg"
                        iserror={error}
                      />
                    )}
                  />
                {item?.upload_doc ? <img src={ item?.upload_doc} style={{width:"50px",height:"50px",margin:" 5px auto 0px auto"}} />
                :<img src={ item?.upload_doc} style={{width:"50px",height:"50px",margin:" 5px auto 0px auto"}} />}
</div>
              </td> */}
              {console.log("index index ",index)}
                <td style={{ border: "1px solid #b3b3b3", padding: "4px", textAlign: "center" }}>
                  {index!==0&&<button
                    type="button"
                    style={{ backgroundColor: "#b2383c", color: "#fff", borderRadius: "4px", padding: "5px 10px" }}
                    onClick={() => party2remove(index)}
                  >
                    Delete
                  </button>}
                </td>
              </tr>
            ))}
            {/* {isTrue&&<CardLabelError style={{ width: "100%", marginTop: '-15px', fontSize: '16px', marginBottom: '12px'}}>{"Require Field"}</CardLabelError>} */}
          </tbody>
        </table>
        <div style={{ textAlign: "right" }}>
          <button
            style={{ backgroundColor: "#573d6c", color: "#fff", borderRadius: "4px", padding: "5px 10px" }}
            type="button"
            onClick={() => {
              party2append({ name: `item ${party2fields.length + 1}` });
            }}
          >
            Add
          </button>
        </div>

        <div>Witness</div>
        <table style={{ marginBottom: "15px" }}>
          <thead style={{ backgroundColor: "#eee", lineHeight: "40px", textAlign: "center", fontSize: "12px" }}>
            <th style={{ border: "1px solid #b3b3b3", padding: "4px" }} width="20%">
              Name
            </th>
            <th style={{ border: "1px solid #b3b3b3", padding: "4px" }} width="30%">
              Address
            </th>
            <th style={{ border: "1px solid #b3b3b3", padding: "4px" }} width="20%">
              Aadhaar No
            </th>
            {/* <th style={{border:  "1px solid #b3b3b3", padding:"4px"}} width="20%">Photo/Thumb</th> */}
            <th style={{ border: "1px solid #b3b3b3", padding: "4px", textAlign: "center" }} width="10%">
              Action
            </th>
          </thead>
          <tbody>
            {/* {(citizenType?.length>0 ? (citizenType[0]?.party2?.party2_witness) : party2_witness_fields)?.map((item, index) => ( */}
            {party2_witness_fields?.map((item, index) => (
              <tr key={item.id}>
                <td style={{ border: "1px solid #b3b3b3", padding: "4px" }}>
                  <Controller
                    name={`party2.party2_witness[${index}].witness_name`}
                    control={control}
                    // rules={{ required: "Required field" }}

                    // defaultValue={item.department}
                    defaultValue={item?.witness_name && item?.witness_name}
                    render={(props) => (
                      <>
                      <TextInput
                        {...props}
                        // key={"witness_name"}
                        // value={formData && formData[config.key] ? formData[config.key][input.name] : undefined}
                        onChange={(e) => setFormData(e.target.value, "witness_name", index)}
                        // onBlur={blurValue}
                        // style={{height:"40px!important",width:"100%",lineHeight:"40px!important"}}
                        // errorStyle={errors?.party2?.party2_witness[index]?.witness_name}

                        // disable={false}
                        // defaultValue={item?.witness_name && item?.witness_name}
                      />
                      {/* {Object.keys(errors).length === 0
                        ? ""
                        : errors?.party2?.party2_witness[index]?.witness_name?.type === "required" && (
                            <CardLabelError>{errors?.party2?.party2_witness[index]?.witness_name?.message}</CardLabelError>
                          )} */}
                          </>
                    )}
                  />
                  {console.log("Re errors sec ", errors , typeof Object.keys(errors).length)}
                  
                  
                </td>
                <td style={{ border: "1px solid #b3b3b3", padding: "4px" }}>
                  <Controller
                    name={`party2.party2_witness[${index}].address`}
                    control={control}
                    defaultValue={item?.address && item?.address}
                    // rules={{ required: "Required field" }}
                    render={(props) => (
                      <TextInput
                        {...props}
                        // key={"address"}
                        // value={formData && formData[config.key] ? formData[config.key][input.name] : undefined}
                        onChange={(e) => setFormData(e.target.value, "address", index)}
                        // onBlur={blurValue}
                        // style={{ height: "40px!important", width: "100%", lineHeight: "40px!important" }}
                        // disable={false}
                        // defaultValue={item?.address && item?.address}
                      />
                    )}
                  />
                  {/* {Object.keys(errors).length === 0
                    ? ""
                    : errors?.party2?.party2_witness[index]?.address?.type === "required" && (
                        <CardLabelError>{errors?.party2?.party2_witness[index]?.address?.message}</CardLabelError>
                      )} */}
                </td>
                <td style={{ border: "1px solid #b3b3b3", padding: "4px" }}>
                  <Controller
                    name={`party2.party2_witness[${index}].aadhaarNo`}
                    control={control}
                    defaultValue={item?.aadhaarNo && item?.aadhaarNo}
                    // rules={{ pattern:{value:/^[2-9]{1}[0-9]{3}[0-9]{4}[0-9]{4}$/,message:"Aadhar should be this formate 0000-0000-0000"} }}
                    render={(props) => (
                      <TextInput
                        {...props}
                        // key={"aadhaarNo"}
                        // value={formData && formData[config.key] ? formData[config.key][input.name] : undefined}
                        onChange={(e) => setFormData(e.target.value, "aadhaarNo", index)}
                        // onBlur={blurValue}
                        // style={{ height: "40px!important", width: "100%", lineHeight: "40px!important" }}
                        // disable={false}
                        // defaultValue={item?.aadhaarNo && item?.aadhaarNo}
                        placeholder={"0000-0000-0000"}
                      />
                    )}
                  />
                  {/* {Object.keys(errors).length === 0
                    ? ""
                    : errors?.party2?.party2_witness[index]?.aadhaarNo?.type && (
                        <CardLabelError>{errors?.party2?.party2_witness[index]?.aadhaarNo?.message}</CardLabelError>
                      )} */}
                </td>

                {/* <td style={{border:  "1px solid #b3b3b3", padding:"4px"}} >
                <div className="field">
                  <Controller
                    name={`party2.party2_witness[${index}].upload_thumb`}
                    control={control}
                    defaultValue={item?.upload_thumb && item?.upload_thumb}
                    render={({ field }) => (
                      <UploadFile
                        {...field}
                        key={"upload_thumb"}
                        // id={id}
                        onUpload={(e) => {
                          selectfile(e, "upload_thumb", index);
                        }}
                        // onUpload={(e) => { setFormData(uploadedFile, "upload_thumb",index) }}
                        onDelete={() => {
                          setUploadedFile(null);
                        }}
                        message={uploadedFile ? `1 ${`CS_ACTION_FILEUPLOADED`}` : `CS_ACTION_NO_FILEUPLOADED`}
                        // message={uploadedFile ? `1 ${t(`CS_ACTION_FILEUPLOADED`)}` : t(`CS_ACTION_NO_FILEUPLOADED`)}
                        textStyles={{ width: "100%" }}
                        inputStyles={{ width: "250px" }}
                        buttonType="button"
                        accept="image/*, .pdf, .png, .jpeg, .jpg"
                        iserror={error}
                      />
                    )}
                  />
                  {Boolean(item?.upload_doc) ? <img src={ item?.upload_doc} style={{width:"50px",height:"50px",margin:" 5px auto 0px auto"}} />
                :<img src={ 'responseImagepath'} style={{width:"50px",height:"50px",margin:" 5px auto 0px auto"}} />}

                  {* <img src={formData?.WmsTEUploadDocuments?.upload_document} width="25" /> *}
                </div>
              </td> */}
                <td style={{ border: "1px solid #b3b3b3", padding: "4px", textAlign: "center" }}>
                  {index!==0&&<button
                    type="button"
                    style={{ backgroundColor: "#b2383c", color: "#fff", borderRadius: "4px", padding: "5px 10px" }}
                    onClick={() => party2_witness_remove(index)}
                  >
                    Delete
                  </button>}
                </td>
              </tr>
            ))}
            {/* {isTrue&&<CardLabelError style={{ width: "100%", marginTop: '-15px', fontSize: '16px', marginBottom: '12px'}}>{"Require Field"}</CardLabelError>} */}
          </tbody>
        </table>
        <div style={{ textAlign: "right" }}>
          <button
            style={{ backgroundColor: "#573d6c", color: "#fff", borderRadius: "4px", padding: "5px 10px" }}
            type="button"
            onClick={() => {
              party2_witness_append({ name: `item ${party2_witness_fields.length + 1}` });
            }}
          >
            Add
          </button>
        </div>
        <div style={{ textAlign: "center", marginBottom: "15px" }}>
          {/* <button type="button" style={{backgroundColor:"#b2383c",color:"#fff",padding:"5px 10px"}}  onClick={() => onSubmit(getData)}> */}
          <button type="submit" style={{ backgroundColor: "#b2383c", color: "#fff", padding: "5px 10px" }}>
            Save as Draft
          </button>{" "}
          <button type="button" style={{ backgroundColor: "#ccc", color: "#fff", padding: "5px 10px" }} onClick={() => onNext(1)}>
            Next
          </button>
        </div>

        <div
          style={{ backgroundColor:(formError?.agreement) ? "rgb(255 0 0)": "rgb(238 238 238)", marginBottom: "5px", lineHeight: "30px", color:(formError?.agreement) ? "#fff": "#000", fontSize: "12px", fontWeight: "600" }}
          onClick={() => onNext(1)}
        >
          <strong
            style={{
              backgroundColor: "#ad2f33",
              display: "inline-block",
              color: "#fff",
              width: "30px",
              textAlign: "center",
              fontSize: "20px",
              lineHeight: "30px",
              float: "left",
              marginRight: "5px",
            }}
          >
            {" "}
            +{" "}
          </strong>{" "}
          Agreement
        </div>
        <div
          style={{ backgroundColor: "rgb(238 238 238)", marginBottom: "5px", lineHeight: "30px", color: "#000", fontSize: "12px", fontWeight: "600" }}
          onClick={() => onNext(2)}
        >
          <strong
            style={{
              backgroundColor: "#ad2f33",
              display: "inline-block",
              color: "#fff",
              width: "30px",
              textAlign: "center",
              fontSize: "20px",
              lineHeight: "30px",
              float: "left",
              marginRight: "5px",
            }}
          >
            {" "}
            +{" "}
          </strong>{" "}
          SD / PG / BG Details
        </div>
        <div
          style={{ backgroundColor:(formError?.partyone) ? "rgb(255 0 0)": "rgb(238 238 238)", marginBottom: "5px", lineHeight: "30px", color: (formError?.partyone) ? "#fff":"#000", fontSize: "12px", fontWeight: "600" }}
          onClick={() => onNext(3)}
        >
          <strong
            style={{
              backgroundColor: "#ad2f33",
              display: "inline-block",
              color: "#fff",
              width: "30px",
              textAlign: "center",
              fontSize: "20px",
              lineHeight: "30px",
              float: "left",
              marginRight: "5px",
            }}
          >
            {" "}
            +{" "}
          </strong>{" "}
          ULB Party 1
        </div>
        <div
          style={{ backgroundColor: "rgb(238 238 238)", marginBottom: "5px", lineHeight: "30px", color: "#000", fontSize: "12px", fontWeight: "600" }}
          onClick={() => onNext(4)}
        >
          <strong
            style={{
              backgroundColor: "#ad2f33",
              display: "inline-block",
              color: "#fff",
              width: "30px",
              textAlign: "center",
              fontSize: "20px",
              lineHeight: "30px",
              float: "left",
              marginRight: "5px",
            }}
          >
            {" "}
            +{" "}
          </strong>{" "}
          Terms and Conditions
        </div>

        {/* <div style={{backgroundColor: "rgb(238 238 238)",marginBottom:"5px",padding: "5px",color: "#000",fontSize: "12px"}} onClick={()=>onNext(1)}><strong>+ Agreement</strong></div>
    <div style={{backgroundColor: "rgb(238 238 238)",marginBottom:"5px",padding: "5px",color: "#000",fontSize: "12px"}} onClick={()=>onNext(2)}><strong>+ SD / PG / BG Details</strong></div>
    <div style={{backgroundColor: "rgb(238 238 238)",marginBottom:"5px",padding: "5px",color: "#000",fontSize: "12px"}} onClick={()=>onNext(3)}><strong>+ ULB Party 1</strong></div>
    <div style={{backgroundColor: "rgb(238 238 238)",marginBottom:"5px",padding: "5px",color: "#000",fontSize: "12px"}} onClick={()=>onNext(4)}><strong>+ Terms and Conditions</strong></div> */}
      </form>
    </>
  );
};
export default WmsCAContractor;
