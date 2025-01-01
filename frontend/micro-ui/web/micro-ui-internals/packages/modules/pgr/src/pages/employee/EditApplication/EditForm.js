import React, { useState, useEffect, useMemo,Fragment  } from "react";
import { useTranslation } from "react-i18next";
import { useDispatch } from "react-redux";
import { Dropdown, Loader,Header } from "@nudmcdgnpm/digit-ui-react-components";
import { useRouteMatch, useHistory } from "react-router-dom";
import { useQueryClient } from "react-query";
import { FormComposer } from "../../../components/FormComposer";
import {updateComplaints} from "../../../redux/actions/index";


const EditForm = ({ applicationData, details ,complaintDetails}) => {
  complaintDetails.workflow.action = "EDIT";
  complaintDetails.workflow.assignes =  null;
  complaintDetails.workflow.comments = null;
  complaintDetails.workflow.verificationDocuments= [];
  console.log("complaintDetails_inside_edit",complaintDetails)
  const { t } = useTranslation();
  const history = useHistory();
  const [canSubmit, setSubmitValve] = useState(true);
  const cities = Digit.Hooks.pgr.useTenants();
  const getCities = () => cities?.filter((e) => e.code === Digit.ULBService.getCurrentTenantId()) || [];
  const [pincode, setPincode] = useState("");
  const [complaintType, setComplaintType] = useState( {});
  const [subTypeMenu, setSubTypeMenu] = useState([]);
  const [subType, setSubType] = useState({});
  const [priorityLevel, setPriorityLevel]=useState(("")||{})
  const [selectedCity, setSelectedCity] = useState(getCities()[0] ? getCities()[0] : null);
  const { data: fetchedLocalities } = Digit.Hooks.useBoundaryLocalities(
    getCities()[0]?.code,
    "admin",
    {
      enabled: !!getCities()[0],
    },
    t
  );

  const [localities, setLocalities] = useState(fetchedLocalities);
  const [selectedLocality, setSelectedLocality] = useState();
  const [submitted, setSubmitted] = useState(false);
  const [pincodeNotValid, setPincodeNotValid] = useState(false);
  const tenantId = window.Digit.SessionStorage.get("Employee.tenantId");
  const menu = Digit.Hooks.pgr.useComplaintTypes({ stateCode: tenantId });
  const dispatch = useDispatch();
  const serviceDefinitions = Digit.GetServiceDefinitions;
  const client = useQueryClient();

  const  priorityMenu= 
  [
    {
      "name": "LOW",
      "code": "LOW",
      "active": true
    },
    {
      "name": "MEDIUM",
      "code": "MEDIUM",
      "active": true
    },
    {
      "name": "HIGH",
      "code": "HIGH",
      "active": true
    }
  ]
 
 
  useEffect(() => {
    setLocalities(fetchedLocalities);
  }, [fetchedLocalities]);

  useEffect(() => {
    const city = cities.find((obj) => obj.pincode?.find((item) => item == pincode));
    if (city?.code === getCities()[0]?.code) {
      setPincodeNotValid(false);
      setSelectedCity(city);
      setSelectedLocality(null);
      const __localityList = fetchedLocalities;
      const __filteredLocalities = __localityList.filter((city) => city["pincode"] == pincode);
      setLocalities(__filteredLocalities);
    } else if (pincode === "" || pincode === null) {
      setPincodeNotValid(false);
      setLocalities(fetchedLocalities);
    } else {
      setPincodeNotValid(true);
    }
  }, [pincode]);
 
  
  useEffect(()=>{ 
        if(menu!=undefined){
        const complaintValue= menu.filter(item=>item.name.toLocaleLowerCase()==details?.CS_ADDCOMPLAINT_COMPLAINT_TYPE.split("SERVICEDEFS.")[1].toLocaleLowerCase())[0];
        selectedType(complaintValue);
        }

        const priorityValue=priorityMenu.filter(item=>item.name === applicationData?.priority)[0];
        setPriorityLevel(priorityValue)

        if(localities!==undefined){
          const localityValue=localities.filter(item=>item.code===applicationData?.address?.locality?.code)[0];
          setSelectedLocality(localityValue)
        }             
},[localities]) 


  async function selectedPriorityLevel(value){
        setPriorityLevel(value);
    }


  const isPincodeValid = () => !pincodeNotValid;

  async function selectedType(value) {
    if (value.key !== complaintType.key) {
      if (value.key === "Others") {
        setSubType({ name: "" });
        setComplaintType(value);
        setSubTypeMenu([{ key: "Others", name: t("SERVICEDEFS.OTHERS") }]);
        const subTypeMenuVal=[{ key: "Others", name: t("SERVICEDEFS.OTHERS") }];
        const complaintSubValue= subTypeMenuVal.filter(item=>item.key.toLocaleLowerCase()==details?.CS_ADDCOMPLAINT_COMPLAINT_SUB_TYPE.split("SERVICEDEFS.")[1].toLocaleLowerCase())[0];
        setSubType(complaintSubValue);
      } else {
        setSubType({ name: "" });
        setComplaintType(value);
        setSubTypeMenu(await serviceDefinitions.getSubMenu(tenantId, value, t));
        const subTypeMenuVal=await serviceDefinitions.getSubMenu(tenantId, value, t)
        const complaintSubValue= subTypeMenuVal.filter(item=>item.key.toLocaleLowerCase()==details?.CS_ADDCOMPLAINT_COMPLAINT_SUB_TYPE.split("SERVICEDEFS.")[1].toLocaleLowerCase())[0];
        setSubType(complaintSubValue);
      }
    }
    
  }

  const config = [
    {
      head: t("ES_CREATECOMPLAINT_PROVIDE_COMPLAINANT_DETAILS"),
      body: [
        {
          label: t("ES_CREATECOMPLAINT_MOBILE_NUMBER"),
          isMandatory: true,
          type: "text",
          populators: {
            name: "mobileNumber",
            defaultValue:applicationData?.citizen?.mobileNumber,
            disabled:true,
            //onChange: handleMobileNumber,
            validation: {
              required: true,
              pattern: /^[6-9]\d{9}$/,  
            },
            componentInFront: <div className="employee-card-input employee-card-input--front">+91</div>,
            error: t("CORE_COMMON_MOBILE_ERROR"),
          },
        },
        {
          label: t("ES_CREATECOMPLAINT_COMPLAINT_NAME"),
          isMandatory: true,
          type: "text",
          populators: {
            name: "name",
            defaultValue: applicationData?.citizen?.name,
            disabled:true,
            //onChange: handleName,
            validation: {
              required: true,
              pattern: /^[A-Za-z]/,
            },
            error: t("CS_ADDCOMPLAINT_NAME_ERROR"),
          },
        },
        {
          label: t("ES_MAIL_ID"),
          isMandatory: false,
          type: "text",
          populators: {
            name: "emailId",
            defaultValue: applicationData?.citizen?.emailId,
            disabled:true,
            //onChange: handleEmail,
            validation: {
              //required: true,
              pattern: /[A-Za-z0-9._%+-]+@[a-z0-9.-]+\.[a-z]{2,4}$/,
            },
            error: t("CS_ADDCOMPLAINT_EMAIL_ERROR"),
          },
        },
      ],
    },
    {
      head: t("CS_COMPLAINT_DETAILS_COMPLAINT_DETAILS"),
      body: [
        {
          label: t("CS_COMPLAINT_DETAILS_COMPLAINT_TYPE"),
          isMandatory: true,
          type: "dropdown",
          populators: <Dropdown disable={true} option={menu} optionKey="name" id="complaintType" selected={complaintType} 
          //select={selectedType}  
          />,
        },
        {
          label: t("CS_COMPLAINT_DETAILS_COMPLAINT_SUBTYPE"),
          isMandatory: true,
          type: "dropdown",
          menu: { ...subTypeMenu },
          populators: <Dropdown disable={true} option={subTypeMenu} optionKey="name" id="complaintSubType" selected={subType} 
          //select={selectedSubType} 
          />,
        },
        {
          
         label: t("CS_COMPLAINT_DETAILS_COMPLAINT_PRIORITY_LEVEL"),
            isMandatory: true,
            type: "dropdown",
            populators: <Dropdown   option={priorityMenu} optionKey="name" id="priorityLevel" selected={priorityLevel} select={selectedPriorityLevel} />,
          
        }     
      ],
    },
    {
      head: t("CS_ADDCOMPLAINT_LOCATION"),
      body: [
        {
          label: t("CORE_COMMON_PINCODE"),
          type: "text",
          populators: {
            name: "pincode",
            defaultValue: applicationData?.address?.pincode,
            disabled:true,
            validation: { pattern: /^[1-9][0-9]{5}$/, validate: isPincodeValid },
            error: t("CORE_COMMON_PINCODE_INVALID"),
           // onChange: handlePincode,
          },
        },
        {
          label: t("CS_COMPLAINT_DETAILS_CITY"),
          isMandatory: true,
          type: "dropdown",
          populators: (
            <Dropdown
              isMandatory
              selected={selectedCity}
              freeze={true}
              option={getCities()}
              id="city"
              //select={selectCity}
              optionKey="i18nKey"
              t={t}
              disable={true}
            />
          ),
        },
        {
          label: t("CS_CREATECOMPLAINT_MOHALLA"),
          type: "dropdown",
          isMandatory: true,
          populators: (
            <Dropdown disable={true} selected={selectedLocality} optionKey="i18nkey" id="locality" option={localities} 
            //select={selectLocality} 
            t={t} />
          ),
        },
        {
          label: t("CS_COMPLAINT_DETAILS_LANDMARK"),
          type: "textarea",
          populators: {
            name: "landmark",
            value:applicationData?.address?.landmark,
            disabled:true,
            //onchange: handleLandmark,
          },
        },
      ],
    },
    {
      head: t("CS_COMPLAINT_DETAILS_ADDITIONAL_DETAILS"),
      body: [
        {
          label: t("CS_COMPLAINT_DETAILS_ADDITIONAL_DETAILS"),
          type: "textarea",
          isDisabled:true,
          populators: {
            name: "description",
            value: applicationData?.description,
            disabled:true,
           // onChange: handleDescription,
          },
        },
      ],
    },
  ];
  function redirectToPage(redirectingUrl){
    window.location.href=redirectingUrl;
  }

  const onSubmit = async () => {
    delete complaintDetails.details;
    complaintDetails.service.priority=priorityLevel.code;
    await dispatch(updateComplaints(complaintDetails));
    await client.refetchQueries(["fetchInboxData"]);
    let redirectingUrl=window.location.href.split("modify")[0]+"response";
    redirectToPage(redirectingUrl);
  };
  

  return (
    <FormComposer
      heading={t("ES_EDIT_NEW_COMPLAINT")}
      config={config}
      onSubmit={onSubmit}
      isDisabled={!canSubmit && !submitted}
      //defaultValues={defaultValues}
      //onFormValueChange={onFormValueChange}
      label={t("CS_UPDATE_COMPLAINT")}
    />
  );
}
export default EditForm;