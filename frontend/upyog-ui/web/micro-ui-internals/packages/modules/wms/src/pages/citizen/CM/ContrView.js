import React, { useEffect, useState } from "react";
import { Card, CardLabel, CardLabelError, CardText, CheckBox, DatePicker, Dropdown, Header, LabelFieldPair, Modal, MultiSelectDropdown, PopUp, RadioButtons, SearchOnRadioButtons, SectionalDropdown, SubmitBar, TextInput } from "@egovernments/digit-ui-react-components";
import { Controller, useForm } from "react-hook-form";
import { useTranslation } from "react-i18next";

//C:\UpyogProjectDev\UPYOG\frontend\upyog-ui\web\micro-ui-internals\packages\modules\bills\src\components\citizen\SearchCitizen.js
//C:\UpyogProjectDev\UPYOG\frontend\upyog-ui\web\micro-ui-internals\packages\modules\bills\src\components\CancelBill\CancelBillModal.js
import { Link } from "react-router-dom";
import axios from 'axios';
// import { isValid, format, startOfToday } from 'date-fns';


const ContrMasterView = () =>{
  const tenantId = Digit.ULBService.getCurrentTenantId();
  console.log("tenantId ", tenantId)
    const { t } = useTranslation();
    const { register, handleSubmit,formState: { errors, touched }, reset, watch, control, setError, clearErrors, formState } = useForm();
//    const [data, setData] = useState();
   const [message, setMessage] = useState('');
   
//    useEffect(() => {
//     const config = {
//         url: Urls.Authenticate,
//         method: "post",
//         params,
//         headers: {
//           authorization: `Basic ${window?.globalConfigs?.getConfig("JWT_TOKEN")||"ZWdvdi11c2VyLWNsaWVudDo="}`,
//           "Content-Type": "application/x-www-form-urlencoded",
//         },
//       };
//     (async () => {
//       const result = await axios(`http://localhost:5000/test`);
//       setData(result.data);
//       console.log("gooo" ,result.data);
//     })();
//   }, [params.id]);
useState(()=>{
  const getTenant=JSON.parse(sessionStorage.getItem('Digit.Citizen.tenantId'));
  console.log("getTenant ",getTenant)
  console.log("getTenant 2 ",getTenant.value)
},[])
    const onSubmitInput = async(data) => {
      // const getTenant=JSON.parse(sessionStorage.getItem('Digit.Citizen.tenantId'));
      // if(data)
       const allowDirectPayment = data?.allow_direct_payment===true ? "Yes" : "No"
      let data1 = {...data,"bank_branch_ifsc_code":data?.bank_branch_ifsc_code?.name,"function":data?.function?.name,"primary_account_head":data?.primary_account_head?.name,"vendor_class":data?.vendor_class?.name,"vendor_sub_type":data?.vendor_sub_type?.name,"vendor_type":data?.vendor_type?.name,"vendor_status":data?.vendor_status?.name,"allow_direct_payment":allowDirectPayment,"tenantId": tenantId}
      console.log("data1 ",data1)
      // return false 
      const dataOne = {
        "RequestInfo":{
        "action":"approve",
        "apiId":"AP123",
        "authToken":"{{token}}",
        "correlationId": null,
        "did":null,
        "key":null,
        "msgId":null,
        "ts":0,
        "userInfo":{
        "emailId":null,
        "id":9836,
        "mobileNumber": "9901888381",
        "name":"APIAUTO",
        "roles":[
        {
        "name":"Birth and Death User",
        "code":"BND_CEMP",
        "tenantId":"pg.citya"
        }
        ],
        "tenantId":"pg.citya",
        "type":"EMPLOYEE",
        "userName":"BDCEMP",
        "uuid":"53735c54-d665-48e7-9e66-db67fac50568"
        },
        "ver":null
        },
        "WMSContractorApplication":[data1
        // {
        // "vendor_type":"Contractor",
        // "vendor_sub_type":"Electronics & Comm",
        // "vendor_name":"Info Solutions Inc.",
        // "vendor_status":"Active",
        // "pfms_vendor_code":"VENDOR456",
        // "payto":"Tech Solutions Inc.",
        // "mobile_number": 2234567890,
        // "email": "vendor@info.com",
        // "uid_number":4987654321,
        // "gst_number":23123456789,
        // "pan_number":"ABCDE1234F1",
        // "bank_branch_ifsc_code":"SBI Bharat Nagar Branch ICIC1234567",
        // "bank_account_number":489876543210,
        // "function":"Procurement",
        // "primary_account_head":"Accounts Payable",
        // "vendor_class":"Class B",
        // "address":"1234 Huda Street,City",
        // "epfo_account_number":101010123454,
        // "tenantId":"pg"
        // }
        ]
        }
      console.log("Data ", dataOne)

        try {
        if(data){
            const config = {
              
              url: 'http://10.216.36.152:8484/wms/wms-services/v1/contractor/_create',
              // url: 'http://localhost:5000/test',
              method: "post",
              data:dataOne,
              headers: {
                'Content-Type': 'application/json',
                'Access-Control-Allow-Origin': 'http://localhost:3000',
                'Access-Control-Allow-Credentials': 'true'
            },
            };
            // axios.post('/user', {
            //     firstName: 'Fred',
            //     lastName: 'Flintstone'
            //   })
            //   .then(function (response) {
            //     console.log(response);
            //   })
            //   .catch(function (error) {
            //     console.log(error);
            //   });
            const res = await axios(config);
            
                if(res?.status===200){
                    setShowDialog(true);
                    reset({
                      "vendor_type": "",
                    "vendor_sub_type": "",
                    "vendor_name": "",
                    "vendor_status": "",
                    "pfms_vendor_code": "",
                    "payto": "",
                    "mobile_number":"" ,
                    "email": "",
                    "uid_number":"" ,
                    "gst_number":"" ,
                    "pan_number": "",
                    "bank_branch_ifsc_code": "",
                    "bank_account_number": "",
                    "function": "",
                    "primary_account_head": "",
                    "vendor_class": "",
                    "address": "",
                    "epfo_account_number": "",
                    "VATNumber":""
                })
                    setMessage("Records Added Succefully")
                }else{
                    setShowDialog(true);
                    setMessage("Something Went Wrong!")
                } 
            } 
            } catch (error) {
                setShowDialog(true);
                setMessage(error)
                
            }
           
        
        
      };

    //   const isValidDate = (date) => {
    //     if (!isValid(new Date(formData?.fromDate)) || !isValid(new Date(date))) return false;
    //     if (new Date(`${formData?.fromDate} ${formData?.fromTime}`) < new Date()) {
    //       setError('fromDate', { type: 'isValidFromDate' }, { shouldFocus: true });
    //       return false;
    //     }
    //     if (new Date(`${date} ${formData?.toTime}`) < new Date()) return false;
    //     return new Date(`${formData?.fromDate} ${formData?.fromTime}`) <= new Date(`${date} ${formData?.toTime}`);
    //   }
     

     
    
     
const vendorType = [
        {
            "code": "CONTRACT_MASTER_CONTRACTOR",
            "name": "Contractor",
            "module": "rainmaker-tl",
            "locale": "en_IN"
        },
        {
            "code": "CONTRACT_MASTER_SUPPLIER",
            "name": "Supplier",
            "module": "rainmaker-tl",
            "locale": "en_IN"
        },
        {
            "code": "CONTRACT_MASTERTENANT",
            "name": "Tenant",
            "module": "rainmaker-tl",
            "locale": "en_IN"
        }
    ];
    const SubType = [{"name":"Non-Specified Hindu Undivided Family"},{"name":"Individual"},{"name":"Registered Partnership (Business)"},{"name":"Association of Persons"},{"name":"Body of Individuals"},{"name":"A Domestic Company (Public subs. interested)"},{"name":"A company other than a Domestic Company"},{"name":"Registered Partnership (Professional)"},{"name":"A Domestic Company (Public non-subs. interested)"},{"name":"Specified Hindu Undivided Family"}
        // {
        //     "code": "CONTRACT_MASTER_CONTRACTOR",
        //     "name": "Contractor",
        //     "module": "rainmaker-tl",
        //     "locale": "en_IN"
        // }
    ];
    const bankType = [
        {
            "code": "CONTRACT_MASTER_SBI",
            "name": "State Bank of India",
            "module": "rainmaker-tl",
            "locale": "en_IN"
        },
        {
            "code": "CONTRACT_MASTER_HDFC",
            "name": "HDFC",
            "module": "rainmaker-tl",
            "locale": "en_IN"
        }
    ];
    const primaryAccountHeadType = [
        {
            "code": "CONTRACT_MASTER_SBI",
            "name": "State Bank of India",
            "module": "rainmaker-tl",
            "locale": "en_IN"
        },
        {
            "code": "CONTRACT_MASTER_HDFC",
            "name": "HDFC",
            "module": "rainmaker-tl",
            "locale": "en_IN"
        }
    ];
    const vendorClassType = [
        {
            "code": "CONTRACT_MASTER_CLASSA",
            "name": "CLASS A",
            "module": "rainmaker-tl",
            "locale": "en_IN"
        },
        {
            "code": "CONTRACT_MASTER_CLASSB",
            "name": "CLASS B",
            "module": "rainmaker-tl",
            "locale": "en_IN"
        },
        {
            "code": "CONTRACT_MASTER_CLASSC",
            "name": "CLASS C",
            "module": "rainmaker-tl",
            "locale": "en_IN"
        },
    ];
    const functionType = [
        {
            "code": "CONTRACT_MASTER_FUNCTIONA",
            "name": "Function A",
            "module": "rainmaker-tl",
            "locale": "en_IN"
        },{
            "code": "CONTRACT_MASTER_FUNCTIONB",
            "name": "Function B",
            "module": "rainmaker-tl",
            "locale": "en_IN"
        }
    ];
    const vendorStatusType = [{"name":"Active"},{"name":"Inactive"}];

    
    const [showDialog, setShowDialog] = useState(false);
    const handleOnCancel = () => {
    setShowDialog(false);
  }
    const Heading = (props) => {
        return <h1 className="heading-m">{props.label}</h1>;
      };
      const Close = () => (
        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="#FFFFFF">
          <path d="M0 0h24v24H0V0z" fill="none" />
          <path d="M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12 19 6.41z" />
        </svg>
      );
    const CloseBtn = (props) => {
        return (
          <div onClick={props?.onClick} style={ props?.isMobileView ? { padding: 5} : null}>
            {
              props?.isMobileView
                ? (<CloseSvg />)
                : (<div className={"icon-bg-secondary"} style={{ backgroundColor: '#505A5F'}}> <Close /> </div>)
            }
          </div>
        )
      };

  const mobileDeviceWidth = 780;
  const [isMobileView, setIsMobileView] = React.useState(window.innerWidth <= mobileDeviceWidth);
  const onResize = () => {
    if (window.innerWidth <= mobileDeviceWidth) {
      if (!isMobileView) {
        setIsMobileView(true);
      }
    } else {
      if (isMobileView) {
        setIsMobileView(false);
      }
    }
  }
  React.useEffect(() => {
    window.addEventListener("resize", () => {
      onResize();
    });
    return () => {
      window.addEventListener("resize", () => {
        onResize();
      });
    };
  });
    return (
    <React.Fragment>
{(isMobileView
      ? <Modal
        popupStyles={{
          height: "174px",
          maxHeight: "174px",
          width: "324px",
          position: "absolute",
          top: "50%",
          left: "50%",
          transform: 'translate(-50%, -50%)',
        }}
        popupModuleActionBarStyles={{
          display: "flex",
          flex: 1,
          justifyContent: "flex-start",
          width: "100%",
          position: "absolute",
          left: 0,
          bottom: 0,
          padding: "18px",
        }}
        style={{
          flex: 1,
        }}
        popupModuleMianStyles={{
          padding: "18px",
        }}
        headerBarMain={<Heading label={t("AM")} />}
        headerBarEnd={<CloseBtn onClick={handleOnCancel} isMobileView={isMobileView} />}
        actionCancelLabel={t("BM")}
        actionCancelOnSubmit={handleOnCancel}
        actionSaveLabel={t("CM")}
        // actionSaveOnSubmit={onSelect}
        formId="modal-action">
        <div>
          <CardText style={{ margin: 0 }}>
            {t("DM") + " "}
          </CardText>
        </div>
      </Modal>
      :showDialog && <Modal
        popupModuleMianStyles={{
          paddingTop: "30px",
        }}
        headerBarMain={<Heading label={t("Contract Master")} />}
        headerBarEnd={<CloseBtn onClick={handleOnCancel} isMobileView={false} />}
        // actionCancelLabel={t("Cancel")}
        // actionCancelOnSubmit={handleOnCancel}
        actionSaveLabel={t("OK")}
        actionSaveOnSubmit={handleOnCancel}
        formId="modal-action">
        <div>
          <CardText style={{ marginBottom: "54px", marginLeft: "8px", marginRight: "8px" }}>
            {t(message) + " "}
            {/* <strong>{t("D")}?</strong> */}
          </CardText>
        </div>
      </Modal>
  )}
    <Card>
    <Header>Vendor Master</Header>
    <form onSubmit={handleSubmit(onSubmitInput)}>

{/* C:\UpyogProjectDev\UPYOG\frontend\upyog-ui\web\micro-ui-internals\packages\modules\engagement\src\components\Events\SelectToDate.js */}
{/* <div>
<Controller render={(props) => <DatePicker 
// isRequired={true} 
date={props.value} onChange={props.onChange} />}
                    name="date"
                    control={control}
        rules={{ required: true}}
        
                />
            
          {errors && errors?.date && errors?.date?.type === "required" && <CardLabelError>{t(`EVENTS_TO_DATE_ERROR_REQUIRED`)}</CardLabelError>}
          // {errors && errors?.date && errors?.date?.type === "isValidDate" && <CardLabelError>{t(`EVENTS_TO_DATE_ERROR_INVALID`)}</CardLabelError>} 
        </div> */}
    
    <LabelFieldPair style={{ ["flex-wrap"]: "wrap","display":"flex" }}>
    <div style={{"flex": "0 0 50%","padding": "10px 10px 0px 10px",["margin-bottom"]:"-10px"}}>
    <CardLabel>PFMS Vendor ID</CardLabel>
    <Controller 
        render={(props) => {
        return (
            <div className="field-container">
            <TextInput
                {...props.fields}
                onChange={props.onChange} 
                value={props.value}
                inputRef={register()}
                watch={watch}
                shouldUpdate={true}
            />
            </div>
            );
            }} 
        name="pfms_vendor_code"
        control={control}
        defaultValue={""}
        rules={{ required: true }}
        />
              {errors && errors?.pfms_vendor_code && errors?.pfms_vendor_code.type==="required" && (<CardLabelError>Required</CardLabelError>)}
     </div>
     </LabelFieldPair>
     <LabelFieldPair style={{ ["flex-wrap"]: "wrap","display":"flex" }}>
        <div style={{"flex": "0 0 50%","padding": "10px"}}>
     <CardLabel>Vendor Type <sup>*</sup></CardLabel>
     <Controller
            control={control}
            rules={{ required: t("REQUIRED_FIELD") }}
            name="vendor_type"
            render={(props) => (
              <Dropdown name="vendor_type" t={t} option={vendorType} onBlur={props.onBlur} selected={props.value}  select={props.onChange} optionKey={"name"} optionCardStyles={{zIndex:"20"}}  />
            )}
/>
{errors && errors?.vendor_type && errors?.vendor_type.type==="required" && (<CardLabelError>Required</CardLabelError>)}

     <CardLabel>Vendor Name</CardLabel>
     <Controller 
        render={(props) => {
        return (
            <div className="field-container">
            <TextInput
                {...props.fields}
                onChange={props.onChange} 
                value={props.value}
                inputRef={register()}
                watch={watch}
                shouldUpdate={true}
            />
            </div>
            );
            }} 
        name="vendor_name"
        control={control}
        defaultValue={""}
        rules={{ required: true }}
        />
              {errors && errors?.vendor_name && errors?.vendor_name.type==="required" && (<CardLabelError>Required</CardLabelError>)}
     <CardLabel>Mobile Number</CardLabel>
     <Controller 
        render={(props) => {
        return (
            <div className="field-container">
            <TextInput
                {...props.fields}
                onChange={props.onChange} 
                value={props.value}
                inputRef={register()}
                watch={watch}
                shouldUpdate={true}
            />
            </div>
            );
            }} 
        name="mobile_number"
        control={control}
        defaultValue={""}
        rules={{ required: true }}
        />
              {errors && errors?.mobile_number && errors?.mobile_number.type==="required" && (<CardLabelError>Required</CardLabelError>)}
     <CardLabel>UID Number</CardLabel>
     <Controller 
        render={(props) => {
        return (
            <div className="field-container">
            <TextInput
                {...props.fields}
                onChange={props.onChange} 
                value={props.value}
                inputRef={register()}
                watch={watch}
                shouldUpdate={true}
            />
            </div>
            );
            }} 
        name="uid_number"
        control={control}
        defaultValue={""}
        rules={{ required: true }}
        />
              {errors && errors?.uid_number && errors?.uid_number.type==="required" && (<CardLabelError>Required</CardLabelError>)}
      <CardLabel>Vendor Status</CardLabel>
<Controller
            control={control}
            // rules={{ required: t("REQUIRED_FIELD") }}
            name="vendor_status"
            render={(props) => (
              <Dropdown name="vendor_status" t={t} option={vendorStatusType} onBlur={props.onBlur} selected={props.value}  select={props.onChange} optionKey={"name"} optionCardStyles={{zIndex:"20"}}  />
            )}
/>
        {/* {errors && errors?.vendor_status && errors?.vendor_status.type==="required" && (<CardLabelError>Required</CardLabelError>)} */}
        <CardLabel>VAT Number</CardLabel>
     <Controller 
        render={(props) => {
        return (
            <div className="field-container">
            <TextInput
                {...props.fields}
                onChange={props.onChange} 
                value={props.value}
                inputRef={register()}
                watch={watch}
                shouldUpdate={true}
            />
            </div>
            );
            }} 
        name="vat_number"
        control={control}
        defaultValue={""}
        // rules={{ required: true }}
        /> 

            {/* {errors && errors?.vat_number && errors?.vat_number.type==="required" && (<CardLabelError>Required</CardLabelError>)} */}

     <CardLabel>Bank, Branch & IFSC Code</CardLabel>
     <Controller
            control={control}
            rules={{ required: t("REQUIRED_FIELD") }}
            name="bank_branch_ifsc_code"
            render={(props) => (
              <Dropdown name="bank_branch_ifsc_code" t={t} option={bankType} onBlur={props.onBlur} selected={props.value}  select={props.onChange} optionKey={"name"} optionCardStyles={{zIndex:"20"}}  />
            )}
/>

      <CardLabel>Function</CardLabel>
     <Controller
            control={control}
            rules={{ required: t("REQUIRED_FIELD") }}
            name="function"
            render={(props) => (
              <Dropdown name="function" t={t} option={functionType} onBlur={props.onBlur} selected={props.value}  select={props.onChange} optionKey={"name"} optionCardStyles={{zIndex:"20"}}  />
            )}
/>
            {/* {errors && errors?.function && errors?.function.type==="required" && (<CardLabelError>Required</CardLabelError>)} */}

     <CardLabel>Vendor Class</CardLabel>
     <Controller
            control={control}
            rules={{ required: t("REQUIRED_FIELD") }}
            name="vendor_class"
            render={(props) => (
              <Dropdown name="vendor_class" t={t} option={vendorClassType} onBlur={props.onBlur} selected={props.value}  select={props.onChange} optionKey={"name"} optionCardStyles={{zIndex:"20"}}  />
            )}
/>
{errors && errors?.vendor_class && errors?.vendor_class.type==="required" && (<CardLabelError>Required</CardLabelError>)}

     <CardLabel>PF Account Number</CardLabel>
     <Controller 
        render={(props) => {
        return (
            <div className="field-container">
            <TextInput
                {...props.fields}
                onChange={props.onChange} 
                value={props.value}
                inputRef={register()}
                watch={watch}
                shouldUpdate={true}
            />
            </div>
            );
            }} 
        name="epfo_account_number"
        control={control}
        defaultValue={""}
        rules={{ required: true }}
        />
              {errors && errors?.epfo_account_number && errors?.epfo_account_number.type==="required" && (<CardLabelError>Required</CardLabelError>)}
     </div>
     <div style={{"flex": "0 0 50%","padding": "10px"}}>
     <CardLabel>Sub Type</CardLabel>
     <Controller
            control={control}
            rules={{ required: t("REQUIRED_FIELD") }}
            name="vendor_sub_type"
            render={(props) => (
              <Dropdown name="vendor_sub_type" t={t} option={SubType} onBlur={props.onBlur} selected={props.value}  select={props.onChange} optionKey={"name"} optionCardStyles={{zIndex:"20"}}  />
            )}
/>
{errors && errors?.vendor_sub_type && errors?.vendor_sub_type.type==="required" && (<CardLabelError>Required</CardLabelError>)}

     <CardLabel>Pay To</CardLabel>
     <Controller 
        render={(props) => {
        return (
            <div className="field-container">
            <TextInput
                {...props.fields}
                onChange={props.onChange} 
                value={props.value}
                inputRef={register()}
                watch={watch}
                shouldUpdate={true}
            />
            </div>
            );
            }} 
        name="payto"
        control={control}
        defaultValue={""}
        rules={{ required: true }}
        />
              {/* {errors && errors?.payto && errors?.payto.type==="required" && (<CardLabelError>Required</CardLabelError>)} */}
     <CardLabel>Email Id</CardLabel>
     <Controller 
        render={(props) => {
        return (
            <div className="field-container">
            <TextInput
                {...props.fields}
                onChange={props.onChange} 
                value={props.value}
                inputRef={register()}
                watch={watch}
                shouldUpdate={true}
            />
            </div>
            );
            }} 
        name="email"
        control={control}
        defaultValue={""}
        rules={{ required: true }}
        />
              {/* {errors && errors?.email && errors?.email.type==="required" && (<CardLabelError>Required</CardLabelError>)} */}
     <CardLabel>GST Number</CardLabel>
     <Controller 
        render={(props) => {
        return (
            <div className="field-container">
            <TextInput
                {...props.fields}
                onChange={props.onChange} 
                value={props.value}
                inputRef={register()}
                watch={watch}
                shouldUpdate={true}
            />
            </div>
            );
            }} 
        name="gst_number"
        control={control}
        defaultValue={""}
        rules={{ required: true }}
        />
              {/* {errors && errors?.gst_number && errors?.gst_number.type==="required" && (<CardLabelError>Required</CardLabelError>)} */}
     <CardLabel>PAN Number</CardLabel>
     <Controller 
        render={(props) => {
        return (
            <div className="field-container">
            <TextInput
                {...props.fields}
                onChange={props.onChange} 
                value={props.value}
                inputRef={register()}
                watch={watch}
                shouldUpdate={true}
            />
            </div>
            );
            }} 
        name="pan_number"
        control={control}
        defaultValue={""}
        rules={{ required: true }}
        />
              {/* {errors && errors?.pan_number && errors?.pan_number.type==="required" && (<CardLabelError>Required</CardLabelError>)} */}
     <CardLabel>Bank Account Number</CardLabel>
     <Controller 
        render={(props) => {
        return (
            <div className="field-container">
            <TextInput
                {...props.fields}
                onChange={props.onChange} 
                value={props.value}
                inputRef={register()}
                watch={watch}
                shouldUpdate={true}
            />
            </div>
            );
            }} 
        name="bank_account_number"
        control={control}
        defaultValue={""}
        rules={{ required: true }}
        />
              {errors && errors?.bank_account_number && errors?.bank_account_number.type==="required" && (<CardLabelError>Required</CardLabelError>)}
     <CardLabel>Primary Account Head</CardLabel>
     <Controller
            control={control}
            rules={{ required: t("REQUIRED_FIELD") }}
            name="primary_account_head"
            render={(props) => (
              <Dropdown name="primary_account_head" t={t} option={primaryAccountHeadType} onBlur={props.onBlur} selected={props.value}  select={props.onChange} optionKey={"name"} optionCardStyles={{zIndex:"20"}}  />
            )}
/>
{/* {errors && errors?.primary_account_head && errors?.primary_account_head.type==="required" && (<CardLabelError>Required</CardLabelError>)} */}

     <CardLabel>Address</CardLabel>
     <Controller 
        render={(props) => {
        return (
            <div className="field-container">
            <TextInput
                {...props.fields}
                onChange={props.onChange} 
                value={props.value}
                inputRef={register()}
                watch={watch}
                shouldUpdate={true}
            />
            </div>
            );
            }} 
        name="address"
        control={control}
        defaultValue={""}
        rules={{ required: true }}
        />
              {errors && errors?.address && errors?.address.type==="required" && (<CardLabelError>Required</CardLabelError>)}
     <CardLabel>Allow Direct Payment</CardLabel>
     {/* <CheckBox
      onChange={(e) => onAssignmentChange(e, status)}
      checked={fsmfilters?.applicationStatus.filter((e) => e.name === status.name).length !== 0 ? true : false}
      label={`${t(status.name)} (${count || 0})`}
    /> */}
      <Controller 
        render={(props) => {
        return (
            <div className="field-container">
            <CheckBox
                {...props.fields}
                onChange={props.onChange} 
                value={props.value}
                inputRef={register()}
                watch={watch}
                shouldUpdate={true}
        name="allowDirectPayment"

            />
            </div>
            );
            }} 
        name="allow_direct_payment"
        control={control}
        defaultValue={""}
        // rules={{ required: true }}
        />
              {/* {errors && errors?.allow_direct_payment && errors?.allow_direct_payment.type==="required" && (<CardLabelError>Required</CardLabelError>)}  */}
     </div>
    </LabelFieldPair>


    {/* <CardText>Card Text 1</CardText>
    <CardText>Card Text 2</CardText>
    <CardLabel>Card Label 1</CardLabel>
    <CardLabel>Card Label 2</CardLabel>
    <CardLabelError>Card Label Error 1</CardLabelError>
    <CardLabelError>Card Label Error 2</CardLabelError> */}
                <SubmitBar label="Add Contractor" submit />
                
</form>
    </Card>
    </React.Fragment>
    )
}
export default ContrMasterView