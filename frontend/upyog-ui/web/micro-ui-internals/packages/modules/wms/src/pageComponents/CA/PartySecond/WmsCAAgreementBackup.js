import React, { useState, useEffect, Fragment } from "react";
import { CardLabelError, CheckBox, DatePicker, DateWrap, Loader, RadioButtons, TextInput, UploadFile } from "@egovernments/digit-ui-react-components";
import { Dropdown, LabelFieldPair, CardLabel } from "@egovernments/digit-ui-react-components";
import { useLocation } from "react-router-dom";
import { useFieldArray, Controller, useForm } from "react-hook-form";
import WmsCATextTest from "./WmsCATextTest";
const WmsCAAgreement = ({
  t,
  config,
  onSelect,
  formData = {},
  userType,
  /*setError, clearErrors,*/ /*formState,*/ onBlur,
  onSubmit,
  onNext,
  draftData,
}) => {
  console.log("CA draftData Agrement ", draftData);
  console.log("onSelect TLInfo  ", { config, formData, userType /*,setError, clearErrors*/, /*formState,*/ onBlur, onSelect });
  const {
    control,
    getValues,
    setValue,
    watch,
    trigger,
    setError,
    clearErrors,
    formState: { errors },
  } = useForm({
    defaultValues: {
      agreement:
        draftData[0]?.agreement?.length > 0
          ? [...draftData[0]?.agreement]
          : [
              {
                agreement_no: "",
                agreement_date: "",
                department_name_ai: "",
                loa_no: "",
                resolution_no: "",
                resolution_date: "",
                tender_no: "",
                tender_date: "",
                agreement_type: "",
                defect_liability_period: "",
                contract_period: "",
                agreement_amount: "",
                payment_type: "",
              },
            ],
      // agreement:[{agreement_no:"",agreement_date: "",department_name_ai: "",loa_no: "",resolution_no:"" ,resolution_date: "",tender_no:"",tender_date: "",agreement_type: "",defect_liability_period: "",contract_period: "",agreement_amount:"",payment_type: "" }],
      // agreement: [],
    },
    mode: "all",
  });

  const formValue = watch();
  const getData = getValues();
  console.log("onSelect TLInfo formValue ", formValue);
  // console.log("onSelect TLInfo formValue formValue?.agreement ", formValue?.agreement);
  // console.log("onSelect TLInfo formValue formValue?.agreement?.length ", formValue?.agreement?.length);
  console.log("getValues ", getData);
  // console.log("getValues getData?.agreement ", getData?.agreement);
  // console.log("getValues getData?.agreement?.length ", getData?.agreement?.length);

  const { fields: agreementfields, append: agreementappend, remove: agreementremove } = useFieldArray({
    control,
    name: "agreement",
  });

  const tenantId = Digit.ULBService.getCurrentTenantId();

  const { pathname: url } = useLocation();
  const editScreen = url.includes("/modify-application/");
  const { data: citizenTypes, isLoading } = Digit?.Hooks?.wms?.te?.useWMSTEMaster(tenantId, "WMS_DEPARTMENT_ALL_RECORD") || {};
  const [primaryPartyData, SetPrimaryPartyData] = useState([]);

  // const citizenTypes = Digit?.Hooks?.wms?.cm?.useWMSMaster(tenantId, "WMS_V_TYPE") || {};
  const [citizenType, setcitizenType] = useState(formData?.WmsCAAgreement);
  // const [citizenType, setcitizenType] = useState();

  // const [citizenTypeList, setcitizenTypeList] = useState()// it open
  const [citizenTypeList, setcitizenTypeList_] = useState([
    {
      //it will delete
      id: 1,
      department_name: "Department Name 1",
      status: "status1",
    },
    {
      id: 2,
      department_name: "Department Name 2",
      status: "status2",
    },
  ]);
  const [citizenTypeList_1, setcitizenTypeList_1] = useState([
    {
      //it will delete
      id: 1,
      payment_type: "Payment Type 1",
      status: "status_1",
    },
    {
      id: 2,
      payment_type: "Payment Type 2",
      status: "status_2",
    },
  ]);
  const [isTrue, setisTrue] = useState(false);
  function SelectcitizenType(value, name, index) {
    // console.log("value, name, index ",{value, name, index})
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
      // setcitizenTypeList(fData);
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
  const [error, setErrorImg] = useState(null);

  useEffect(() => {
    const uploadFu = (async () => {
      setErrorImg(null);
      if (file?.file) {
        const allowedFileTypesRegex = /(.*?)(jpg|jpeg|png|image|pdf)$/i;
        if (file?.file.size >= 5242880) {
          setErrorImg("CS_MAXIMUM_UPLOAD_SIZE_EXCEEDED");
          // setErrorImg(t("CS_MAXIMUM_UPLOAD_SIZE_EXCEEDED"));
        } else if (file?.file?.type && !allowedFileTypesRegex.test(file?.file?.type)) {
          // setErrorImg(t(`NOT_SUPPORTED_FILE_TYPE`))
          setErrorImg(`NOT_SUPPORTED_FILE_TYPE`);
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
            console.log("response ", response);
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
  // agreementappend({ name: `item ${agreementfields.length + 1}` });
  // agreement_witness_append({ name: `item ${agreement_witness_fields.length + 1}` });
  // },[])

  //########################### text field code start

  // const [errorss, setErrors] = React.useState({});
  // const [agreementNo, setAgreementNo] = React.useState(false);
  // const agreementNo=[]
  // useState(()=>{

  //   // console.log("agreement ",agreement)
  //   setError('agreement'+[0]?.agreement_no, {})
  // },[setError])
  const [isError, setIsError] = useState();
  // console.log("isError isError ", isError);
  // console.log("isError isError agNo ", isError?.agNo);

  // console.log("isError isError Boolean ", Boolean(isError));
  // console.log("isError isError null ", isError === null);

  function setFormData(value, input, index) {
    console.log("radio ", { value, input, index });
    console.log("value, value.length, input, index ", { value, "value.length": value.length,"value?.department_name.length":value?.department_name?.length,"value?.payment_type.length":value?.payment_type?.length, input, index });

    input == "agreement_no" && setValue("agreement[" + index + "].agreement_no", value);
    input === "agreement_date" && setValue("agreement[" + index + "].agreement_date", value);
    input === "department_name_ai" && setValue("agreement[" + index + "].department_name_ai", value?.department_name);
    input === "loa_no" && setValue("agreement[" + index + "].loa_no", value);
    input === "resolution_no" && setValue("agreement[" + index + "].resolution_no", value);
    input === "resolution_date" && setValue("agreement[" + index + "].resolution_date", value);
    input === "tender_no" && setValue("agreement[" + index + "].tender_no", value);
    input === "tender_date" && setValue("agreement[" + index + "].tender_date", value);
    input === "agreement_type" && setValue("agreement[" + index + "].agreement_type", value);
    input === "defect_liability_period" && setValue("agreement[" + index + "].defect_liability_period", value);
    input === "contract_period" && setValue("agreement[" + index + "].contract_period", value);
    input === "agreement_amount" && setValue("agreement[" + index + "].agreement_amount", value);
    input === "payment_type" && setValue("agreement[" + index + "].payment_type", value?.payment_type);

    if (value?.length > 0 || value?.department_name?.length > 0 || value?.payment_type?.length > 0) {
      switch (input) {
        case "agreement_no":
          clearErrors(`agreement[${index}].${input}`);
          break;
        case "agreement_date":
          clearErrors(`agreement_date[${index}].${input}`);
          break;
        case "department_name_ai":
          clearErrors(`department_name_ai[${index}].${input}`);
          break;
        case "loa_no":
          clearErrors(`loa_no[${index}].${input}`);
          break;
          case "tender_no":
          clearErrors(`tender_no[${index}].${input}`);
          break;
          case "tender_date":
          clearErrors(`tender_date[${index}].${input}`);
          break;
          case "agreement_type":
          clearErrors(`agreement_type[${index}].${input}`);
          break;
          case "contract_period":
          clearErrors(`contract_period[${index}].${input}`);
          break;
          case "agreement_amount":
          clearErrors(`agreement_amount[${index}].${input}`);
          break;
          case "payment_type":
          clearErrors(`payment_type[${index}].${input}`);
          break;
      }
      agreementremove(11111111111111);
      // setIsError(false)
      // alert("value.length>0 IF 2")
    } else {
      switch (input) {
        case "agreement_no":
          setError(`agreement[${index}].${input}`, {
            type: "manual",
            message: "Require Field",
          });

          setIsError({ ...isError, caAgreeNo: true });
          break;
        case "agreement_date":
          setError(`agreement_date[${index}].${input}`, {
            type: "manual",
            message: "Require Field",
          });
          setIsError({ ...isError, caDate: true });
          break;
        case "department_name_ai":
          setError(`department_name_ai[${index}].${input}`, {
            type: "manual",
            message: "Require Field",
          });
          setIsError({ ...isError, caName: true });
          break;

          case "loa_no":
          setError(`loa_no[${index}].${input}`, {
            type: "manual",
            message: "Require Field",
          });
          setIsError({ ...isError, caLoaNo: true });
          break;
          case "tender_no":
          setError(`tender_no[${index}].${input}`, {
            type: "manual",
            message: "Require Field",
          });
          setIsError({ ...isError, caTenderNo: true });
          break;
          case "tender_date":
          setError(`tender_date[${index}].${input}`, {
            type: "manual",
            message: "Require Field",
          });
          setIsError({ ...isError, caTenderDate: true });
          break;
          case "agreement_type":
          setError(`agreement_type[${index}].${input}`, {
            type: "manual",
            message: "Require Field",
          });
          setIsError({ ...isError, caAgreeType: true });
          break;
          case "contract_period":
          setError(`contract_period[${index}].${input}`, {
            type: "manual",
            message: "Require Field",
          });
          setIsError({ ...isError, caContractPeriod: true });
          break;
          case "agreement_amount":
          setError(`agreement_amount[${index}].${input}`, {
            type: "manual",
            message: "Require Field",
          });
          setIsError({ ...isError, caAgreeAmo: true });
          break;
          case "payment_type":
          setError(`payment_type[${index}].${input}`, {
            type: "manual",
            message: "Require Field",
          });
          setIsError({ ...isError, caPayType: true });
          break;
          
      }

      agreementremove(11111111111111);
    }
  }
  useEffect(() => {
    // This effect runs whenever the fields array changes
    // Update your data or perform any actions here
    console.log("Fields updated:", agreementfields);
  }, [agreementfields]);

  // useEffect(()=>{
  //   onSelect(config.key, { ...formData[config.key], ...owners})
  // },[owners])
  //########################### text field code end
  // if (isLoading) {
  //   return <Loader />;
  // }
// useEffect(()=>{trigger()},[watch("agreement["+1+"]?.agreement_no")])
  let validation = {};
  return (
    <>
    <div style={{backgroundColor: "rgb(238 238 238)",marginBottom:"5px",lineHeight: "30px",color: "#000",fontSize: "12px",fontWeight: "600"}} onClick={()=>onNext(0)}><strong style={{backgroundColor: "#ad2f33",display: "inline-block",color: "#fff",width: "30px",textAlign:"center",fontSize: "20px",lineHeight: "30px",float: "left", marginRight: "5px"}}> + </strong> Party II {"(Contractor)"}</div>
    <div style={{backgroundColor: "rgb(238 238 238)",marginBottom:"5px",lineHeight: "30px",color: "#000",fontSize: "12px",fontWeight: "600"}} onClick={()=>onNext(1)}><strong style={{backgroundColor: "#ad2f33",display: "inline-block",color: "#fff",width: "30px",textAlign:"center",fontSize: "20px",lineHeight: "30px",float: "left", marginRight: "5px"}}> - </strong> Agreement</div>
      <div style={{ maxWidth: "100%", overflowX: "scroll",minHeight:"300px",marginBottom:"10px" }}>
        <table style={{ marginBottom: "15px" }}>
          <thead style={{ backgroundColor: "#eee", lineHeight: "40px", textAlign: "center", fontSize: "12px" }}>
            <th style={{ border: "1px solid #b3b3b3", padding: "4px" }}>
              <div style={{ width: "100px" }}>
                Agreement No <sup style={{ color: "red" }}>*</sup>
              </div>
            </th>
            <th style={{ border: "1px solid #b3b3b3", padding: "4px" }}>
              <div style={{ width: "150px" }}>
                Agreement Date <sup style={{ color: "red" }}>*</sup>
              </div>
            </th>
            <th style={{ border: "1px solid #b3b3b3", padding: "4px" }}>
              <div style={{ width: "150px" }}>
                Department Name <sup style={{ color: "red" }}>*</sup>
              </div>
            </th>
            <th style={{ border: "1px solid #b3b3b3", padding: "4px" }}>
              <div style={{ width: "100px" }}>
                LOA No. <sup style={{ color: "red" }}>*</sup>
              </div>
            </th>
            <th style={{ border: "1px solid #b3b3b3", padding: "4px" }}>
              <div style={{ width: "100px" }}>Resolution No.</div>
            </th>
            <th style={{ border: "1px solid #b3b3b3", padding: "4px" }}>
              <div style={{ width: "100px" }}>Resolution Date</div>
            </th>
            <th style={{ border: "1px solid #b3b3b3", padding: "4px" }}>
              <div style={{ width: "100px" }}>
                Tender No. <sup style={{ color: "red" }}>*</sup>
              </div>
            </th>
            <th style={{ border: "1px solid #b3b3b3", padding: "4px" }}>
              <div style={{ width: "150px" }}>
                Tender Date <sup style={{ color: "red" }}>*</sup>
              </div>
            </th>
            <th style={{ border: "1px solid #b3b3b3", padding: "4px" }}>
              <div style={{ width: "100px" }}>
                {" "}
                Agreement Type <sup style={{ color: "red" }}>*</sup>
              </div>
            </th>
            <th style={{ border: "1px solid #b3b3b3", padding: "4px" }}>
              <div style={{ width: "100px" }}>Defect Liability Period</div>
            </th>
            <th style={{ border: "1px solid #b3b3b3", padding: "4px" }}>
              <div style={{ width: "100px" }}>
                {" "}
                Contract Period <sup style={{ color: "red" }}>*</sup>
              </div>
            </th>
            <th style={{ border: "1px solid #b3b3b3", padding: "4px" }}>
              <div style={{ width: "100px" }}>
                {" "}
                Agreement Amount <sup style={{ color: "red" }}>*</sup>
              </div>
            </th>
            <th style={{ border: "1px solid #b3b3b3", padding: "4px" }}>
              <div style={{ width: "150px" }}>
                {" "}
                Payment Type <sup style={{ color: "red" }}>*</sup>
              </div>
            </th>
            <th style={{ border: "1px solid #b3b3b3", padding: "4px", textAlign: "center" }}>
              <div style={{ width: "75px" }}>Action</div>
            </th>
          </thead>
          <tbody>
            {console.log("item item agreementfields ", agreementfields)}
            {agreementfields?.map((item, index, arr) => (
              <tr key={item.id}>
                <td style={{ border: "1px solid #b3b3b3", padding: "4px" }}>
                  <Controller
                    name={`agreement[${index}].agreement_no`}
                    control={control}
                    defaultValue={item?.agreement_no && item?.agreement_no}
                    // rules={{ required: "Required fields dddddddddddddddddddd" }}
                    // rules={{ required: true }}
                    render={({ field, fieldState }) => (
                      <div>
                        <TextInput
                          {...field}
                          key={"agreement_no"}
                          value={formData && formData[config.key] ? formData[config.key][input.name] : undefined}
                          onChange={(e) => setFormData(e.target.value, "agreement_no", index)}
                          // onBlur={blurValue}
                          style={{ height: "40px!important", width: "100%", lineHeight: "40px!important" }}
                          disable={false}
                          defaultValue={item?.agreement_no && item?.agreement_no}
                          // isMandatory={false}
                          // {...(validation = {
                          //   isRequired: true,
                          //   pattern: "^[a-zA-Z-.`' ]*$",
                          //   type: "text",
                          //   title: ("CORE_COMMON_PROFILE_NAME_ERROR_MESSAGE"),
                          // })}
                        />
                      </div>
                    )}
                  />
                  {/* {
                    Object.keys(errors).length === 0 ?"":
                    errors && errors?.agreement[index]?.agreement_no?.type==="required" ?
                       (agreementfields[index]?.agreement_no === "" ||
                          agreementfields[index]?.agreement_no === undefined ||
                          agreementfields[index]?.agreement_no === null) && <CardLabelError> {"Require Field"} </CardLabelError>
                      : ""
                  } */}
                  {isError?.caAgreeNo
                    ? 
                    (agreementfields[index]?.agreement_no === "" ||
                        agreementfields[index]?.agreement_no === undefined ||
                        agreementfields[index]?.agreement_no === null) && <CardLabelError> {"Require Field"} </CardLabelError>
                    : ""}
                </td>
                <td style={{ border: "1px solid #b3b3b3", padding: "4px" }}>
                  <Controller
                    name={`agreement[${index}].agreement_date`}
                    control={control}
                    defaultValue={item.agreement_date && item.agreement_date}
                    rules={{ required: true,message: "Required fields" }}

                    //render={({ field }) => (
                    // <DatePicker
                    //   {...field}
                    //   key={"agreement_date"}
                    //   value={formData && formData[config.key] ? formData[config.key][input.name] : undefined}
                    //   onChange={(e) => setFormData(e.target.value, "agreement_date", index)}
                    //   // onBlur={blurValue}
                    //   style={{height:"40px!important",width:"100%",lineHeight:"40px!important"}}
                    //   defaultValue={item.agreement_date && item.agreement_date}
                    // />
                    //)}
                    // item?.agreement_date ? item?.agreement_date:
                    render={({ field, value, onChange }) => (
                      <DatePicker date={value} onChange={(data) => setFormData(data, "agreement_date", index)} />
                    )}
                  />
                  {/* {
                  Object.keys(errors).length === 0 ?"":
                  errors && errors?.agreement[index]?.agreement_date?.type==="required" ?
                    (agreementfields[index]?.agreement_date === "" ||
                        agreementfields[index]?.agreement_date === undefined ||
                        agreementfields[index]?.agreement_date === null) && <CardLabelError> {"Require Field"} </CardLabelError>
                    : ""
                    } */}

{isError?.caDate
                    ? 
                    (agreementfields[index]?.agreement_date === "" ||
                        agreementfields[index]?.agreement_date === undefined ||
                        agreementfields[index]?.agreement_date === null) && <CardLabelError> {"Require Field"} </CardLabelError>
                    : ""}
                </td>

                <td style={{ border: "1px solid #b3b3b3", padding: "4px" }}>
                  <Controller
                    name={`agreement[${index}].department_name_ai`}
                    control={control}
                    // defaultValue={item?.department_name_ai && { department_name_ai: item?.department_name_ai }}
                    defaultValue={item?.department_name_ai && item?.department_name_ai }
                    render={({ field }) => (
                      <Dropdown
                        {...field}
                        // className="form-field"
                        // selected={citizenType}
                        selected={item?.department_name_ai && item}
                        // option={citizenTypeList?.["egov-hrms"]?.citizenType}
                        option={citizenTypeList != undefined && citizenTypeList}
                        // option={vendorType}
                        select={(e) => SelectcitizenType(e, "department_name_ai", index)}
                        // onBlur={()=>SelectcitizenType(val,index)}
                        // onChange={(e) => setFormData(value?.name, 'name', index)}
                        // onChange={(e) => setFormData(value?.name, 'name', index)}
                        optionKey="department_name"
                        // optionKey={`agreement.contractor[${index}].department_name_ai`}

                        defaultValue={undefined}
                        placeholder="Department Name"
                        // t={t}
                      />
                    )}
                  />
                  {isError?.caName
                    ? 
                    (agreementfields[index]?.department_name_ai === "" ||
                        agreementfields[index]?.department_name_ai === undefined ||
                        agreementfields[index]?.department_name_ai === null) && <CardLabelError> {"Require Field"} </CardLabelError>
                    : ""}
                </td>
                <td style={{ border: "1px solid #b3b3b3", padding: "4px" }}>
                  <Controller
                    name={`agreement[${index}].loa_no`}
                    control={control}
                    defaultValue={item?.loa_no && item?.loa_no}
                    render={({ field }) => (
                      <TextInput
                        {...field}
                        key={"loa_no"}
                        value={formData && formData[config.key] ? formData[config.key][input.name] : undefined}
                        onChange={(e) => setFormData(e.target.value, "loa_no", index)}
                        // onBlur={blurValue}
                        style={{ height: "40px!important", width: "100%", lineHeight: "40px!important" }}
                        disable={false}
                        defaultValue={item?.loa_no && item?.loa_no}
                      />
                    )}
                  />
                  {isError?.caLoaNo
                    ? (agreementfields[index]?.loa_no === "" ||
                        agreementfields[index]?.loa_no === undefined ||
                        agreementfields[index]?.loa_no === null) && <CardLabelError> {"Require Field"} </CardLabelError>
                    : ""}
                </td>
                <td style={{ border: "1px solid #b3b3b3", padding: "4px" }}>
                  <Controller
                    name={`agreement[${index}].resolution_no`}
                    control={control}
                    defaultValue={item?.resolution_no && item?.resolution_no}
                    render={({ field }) => (
                      <TextInput
                        {...field}
                        key={"resolution_no"}
                        value={formData && formData[config.key] ? formData[config.key][input.name] : undefined}
                        onChange={(e) => setFormData(e.target.value, "resolution_no", index)}
                        // onBlur={blurValue}
                        style={{ height: "40px!important", width: "100%", lineHeight: "40px!important" }}
                        disable={false}
                        defaultValue={item?.resolution_no && item?.resolution_no}
                      />
                    )}
                  />
                </td>
                <td style={{ border: "1px solid #b3b3b3", padding: "4px" }}>
                  <Controller
                    name={`agreement[${index}].resolution_date`}
                    control={control}
                    defaultValue={item?.resolution_date && item?.resolution_date}
                    render={({ field }) => (
                      <TextInput
                        {...field}
                        key={"resolution_date"}
                        value={formData && formData[config.key] ? formData[config.key][input.name] : undefined}
                        onChange={(e) => setFormData(e.target.value, "resolution_date", index)}
                        // onBlur={blurValue}
                        style={{ height: "40px!important", width: "100%", lineHeight: "40px!important" }}
                        disable={false}
                        defaultValue={item?.resolution_date && item?.resolution_date}
                      />
                    )}
                  />
                </td>
                <td style={{ border: "1px solid #b3b3b3", padding: "4px" }}>
                  <Controller
                    name={`agreement[${index}].tender_no`}
                    control={control}
                    defaultValue={item?.tender_no && item?.tender_no}
                    render={({ field }) => (
                      <TextInput
                        {...field}
                        key={"tender_no"}
                        value={formData && formData[config.key] ? formData[config.key][input.name] : undefined}
                        onChange={(e) => setFormData(e.target.value, "tender_no", index)}
                        // onBlur={blurValue}
                        style={{ height: "40px!important", width: "100%", lineHeight: "40px!important" }}
                        disable={false}
                        defaultValue={item?.tender_no && item?.tender_no}
                      />
                    )}
                  />
                  {isError?.caTenderNo
                    ? (agreementfields[index]?.tender_no === "" ||
                        agreementfields[index]?.tender_no === undefined ||
                        agreementfields[index]?.tender_no === null) && <CardLabelError> {"Require Field"} </CardLabelError>
                    : ""}
                </td>
                <td style={{ border: "1px solid #b3b3b3", padding: "4px" }}>
                  <Controller
                    name={`agreement[${index}].tender_date`}
                    control={control}
                    defaultValue={item?.tender_date && item?.tender_date}
                    // render={({ field }) => (
                    //   <DatePicker
                    //     {...field}
                    //     key={"tender_date"}
                    //     value={formData && formData[config.key] ? formData[config.key][input.name] : undefined}
                    //     onChange={(e) => setFormData(e.target.value, "tender_date", index)}
                    //     // onBlur={blurValue}
                    //     style={{height:"40px!important",width:"100%",lineHeight:"40px!important"}}
                    //     defaultValue={item?.tender_date && item?.tender_date}
                    //   />
                    // )}
                    render={({ field, value, onChange }) => <DatePicker date={value} onChange={(data) => setFormData(data, "tender_date", index)} />}
                  />
                  {isError?.caTenderDate
                    ? (agreementfields[index]?.tender_date === "" ||
                        agreementfields[index]?.tender_date === undefined ||
                        agreementfields[index]?.tender_date === null) && <CardLabelError> {"Require Field"} </CardLabelError>
                    : ""}
                </td>
                <td style={{ border: "1px solid #b3b3b3", padding: "4px" }}>
                  <Controller
                    name={`agreement[${index}].agreement_type`}
                    control={control}
                    defaultValue={item?.agreement_type && item?.agreement_type}
                    render={({ field }) => (
                      <TextInput
                        {...field}
                        key={"agreement_type"}
                        value={formData && formData[config.key] ? formData[config.key][input.name] : undefined}
                        onChange={(e) => setFormData(e.target.value, "agreement_type", index)}
                        // onBlur={blurValue}
                        style={{ height: "40px!important", width: "100%", lineHeight: "40px!important" }}
                        disable={false}
                        defaultValue={item?.agreement_type && item?.agreement_type}
                        disabled={false}
                      />
                    )}
                  />
                  {isError?.caAgreeType
                    ? (agreementfields[index]?.agreement_type === "" ||
                        agreementfields[index]?.agreement_type === undefined ||
                        agreementfields[index]?.agreement_type === null) && <CardLabelError> {"Require Field"} </CardLabelError>
                    : ""}
                </td>
                <td style={{ border: "1px solid #b3b3b3", padding: "4px" }}>
                  <Controller
                    name={`agreement[${index}].defect_liability_period`}
                    control={control}
                    defaultValue={item?.defect_liability_period && item?.defect_liability_period}
                    render={({ field }) => (
                      <TextInput
                        {...field}
                        key={"defect_liability_period"}
                        value={formData && formData[config.key] ? formData[config.key][input.name] : undefined}
                        onChange={(e) => setFormData(e.target.value, "defect_liability_period", index)}
                        // onBlur={blurValue}
                        style={{ height: "40px!important", width: "100%", lineHeight: "40px!important" }}
                        defaultValue={item?.defect_liability_period && item?.defect_liability_period}
                      />
                    )}
                  />
                </td>
                <td style={{ border: "1px solid #b3b3b3", padding: "4px" }}>
                  <Controller
                    name={`agreement[${index}].contract_period`}
                    control={control}
                    defaultValue={item?.contract_period && item?.contract_period}
                    render={({ field }) => (
                      <TextInput
                        {...field}
                        key={"contract_period"}
                        value={formData && formData[config.key] ? formData[config.key][input.name] : undefined}
                        onChange={(e) => setFormData(e.target.value, "contract_period", index)}
                        // onBlur={blurValue}
                        style={{ height: "40px!important", width: "100%", lineHeight: "40px!important" }}
                        disable={false}
                        defaultValue={item?.contract_period && item?.contract_period}
                      />
                    )}
                  />
                  {isError?.caContractPeriod
                    ? (agreementfields[index]?.contract_period === "" ||
                        agreementfields[index]?.contract_period === undefined ||
                        agreementfields[index]?.contract_period === null) && <CardLabelError> {"Require Field"} </CardLabelError>
                    : ""}
                </td>

                <td style={{ border: "1px solid #b3b3b3", padding: "4px" }}>
                  <Controller
                    name={`agreement[${index}].agreement_amount`}
                    control={control}
                    defaultValue={item?.agreement_amount && item?.agreement_amount}
                    render={({ field }) => (
                      <TextInput
                        {...field}
                        key={"agreement_amount"}
                        value={formData && formData[config.key] ? formData[config.key][input.name] : undefined}
                        onChange={(e) => setFormData(e.target.value, "agreement_amount", index)}
                        // onBlur={blurValue}
                        style={{ height: "40px!important", width: "100%", lineHeight: "40px!important" }}
                        disable={false}
                        defaultValue={item?.agreement_amount && item?.agreement_amount}
                        disabled={false}
                      />
                    )}
                  />
                  {isError?.caAgreeAmo
                    ? (agreementfields[index]?.agreement_amount === "" ||
                        agreementfields[index]?.agreement_amount === undefined ||
                        agreementfields[index]?.agreement_amount === null) && <CardLabelError> {"Require Field"} </CardLabelError>
                    : ""}
                </td>

                <td style={{ border: "1px solid #b3b3b3", padding: "4px" }}>
                  <Controller
                    name={`agreement[${index}].payment_type`}
                    control={control}
                    // defaultValue={item?.payment_type && { payment_type: item?.payment_type }}
                    defaultValue={item?.payment_type && item?.payment_type}
                    render={({ field }) => (
                      <Dropdown
                        {...field}
                        // className="form-field"
                        selected={item?.payment_type && item}
                        // selected={citizenType}
                        // option={citizenTypeList?.["egov-hrms"]?.citizenType}
                        option={citizenTypeList_1 != undefined && citizenTypeList_1}
                        // option={vendorType}
                        select={(e) => SelectcitizenType(e, "payment_type", index)}
                        // onBlur={(e)=> SelectcitizenType(e, "payment_type", index)}
                        // onChange={(e) => setFormData(value?.name, 'name', index)}
                        // onChange={(e) => setFormData(value?.name, 'name', index)}
                        optionKey="payment_type"
                        // optionKey={`contractor[${index}].name`}

                        defaultValue={undefined}
                        placeholder="Payment Type"
                        // t={t}
                      />
                    )}
                  />
                  {isError?.caPayType
                    ? (agreementfields[index]?.payment_type === "" ||
                        agreementfields[index]?.payment_type === undefined ||
                        agreementfields[index]?.payment_type === null) && <CardLabelError> {"Require Field"} </CardLabelError>
                    : ""}
                </td>
                <td style={{ border: "1px solid #b3b3b3", padding: "4px", textAlign: "center" }}>
                  <button
                    type="button"
                    style={{ backgroundColor: "#b2383c", color: "#fff", borderRadius: "4px", padding: "5px 10px" }}
                    onClick={() => agreementremove(index)}
                  >
                    Delete
                  </button>
                </td>
              </tr>
            ))}
            {/* {isTrue&&<CardLabelError style={{ width: "100%", marginTop: '-15px', fontSize: '16px', marginBottom: '12px'}}>{"Require Field"}</CardLabelError>} */}
          </tbody>
        </table>
      </div>
      <div style={{ textAlign: "right" }}>
        <button
          style={{ backgroundColor: "#573d6c", color: "#fff", borderRadius: "4px", padding: "5px 10px" }}
          type="button"
          onClick={() => {
            agreementappend({ name: `item ${agreementfields.length + 1}` });
          }}
        >
          Add
        </button>
      </div>

      <div style={{ textAlign: "center", marginBottom: "15px" }}>
        {console.log("errors errors ",errors)}
        {Object.keys(errors).length > 0 ? (
          <button type="button" disabled={true} style={{ backgroundColor: "gray", color: "#fff", padding: "5px 10px" }}>
            Save as Draft
          </button>
        ) : 
        (
          <button type="button" style={{ backgroundColor: "#b2383c", color: "#fff", padding: "5px 10px" }} onClick={() => {Object.keys(errors).length === 0?trigger():onSubmit(getData)}}>
          {/* // <button type="button" style={{ backgroundColor: "#b2383c", color: "#fff", padding: "5px 10px" }} onClick={() => onSubmit(getData)}> */}
            Save as Draft
          </button>
        )
         }{" "}
        <button type="button" style={{ backgroundColor: "#ccc", color: "#fff", padding: "5px 10px" }} onClick={() => trigger()}>
        Trigger
        </button>{" "}
        {console.log("Errosssss ",errors)}
        <button type="button" style={{ backgroundColor: "#ccc", color: "#fff", padding: "5px 10px" }} onClick={() => onNext(0)}>
          Prev
        </button>{" "}
        <button type="button" style={{ backgroundColor: "#ccc", color: "#fff", padding: "5px 10px" }} onClick={() => onNext(2)}>
          Next
        </button>
      </div>

      <div style={{backgroundColor: "rgb(238 238 238)",marginBottom:"5px",lineHeight: "30px",color: "#000",fontSize: "12px",fontWeight: "600"}} onClick={()=>onNext(2)}><strong style={{backgroundColor: "#ad2f33",display: "inline-block",color: "#fff",width: "30px",textAlign:"center",fontSize: "20px",lineHeight: "30px",float: "left", marginRight: "5px"}}> + </strong> SD / PG / BG Details</div>
      <div style={{backgroundColor: "rgb(238 238 238)",marginBottom:"5px",lineHeight: "30px",color: "#000",fontSize: "12px",fontWeight: "600"}} onClick={()=>onNext(3)}><strong style={{backgroundColor: "#ad2f33",display: "inline-block",color: "#fff",width: "30px",textAlign:"center",fontSize: "20px",lineHeight: "30px",float: "left", marginRight: "5px"}}> + </strong> ULB Party 1</div>
      <div style={{backgroundColor: "rgb(238 238 238)",marginBottom:"5px",lineHeight: "30px",color: "#000",fontSize: "12px",fontWeight: "600"}} onClick={()=>onNext(0)}><strong style={{backgroundColor: "#ad2f33",display: "inline-block",color: "#fff",width: "30px",textAlign:"center",fontSize: "20px",lineHeight: "30px",float: "left", marginRight: "5px"}}> + </strong> Terms and Conditions</div>
    </>
  );
};
export default WmsCAAgreement;
