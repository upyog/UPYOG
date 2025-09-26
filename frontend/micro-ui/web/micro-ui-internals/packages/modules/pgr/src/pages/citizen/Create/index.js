import React, { useState, useEffect, useMemo } from "react";
import { useTranslation } from "react-i18next";
import { useDispatch } from "react-redux";
import { Dropdown, Loader,ImageUploadHandler } from "@demodigit/digit-ui-react-components";
import { useRouteMatch, useHistory } from "react-router-dom";
import { useQueryClient } from "react-query";
import  LocationSearch  from "../../../../../../react-components/src/atoms/LocationSearch";
import { FormComposer } from "../../../components/FormComposer";
import { createComplaint } from "../../../redux/actions/index";
import SelectImages from "./Steps/SelectImages"
export const CreateComplaint = ({ parentUrl }) => {
  const cities = Digit.Hooks.pgr.useTenants();
  const { t } = useTranslation();
console.log("parentUrlparentUrl",parentUrl,cities,Digit.ULBService.getCurrentTenantId(),window.Digit.SessionStorage.get("CITIZEN.COMMON.HOME.CITY")?.code)
  const getCities = () => cities?.filter((e) => e.code === window.Digit.SessionStorage.get("CITIZEN.COMMON.HOME.CITY")?.code) || [];
  console.log("getCities",getCities)
  const propetyData=localStorage.getItem("pgrProperty") 
  const [complaintType, setComplaintType] = useState(JSON?.parse(sessionStorage.getItem("complaintType")) || {});
  const [subTypeMenu, setSubTypeMenu] = useState([]);
  const [subType, setSubType] = useState(JSON?.parse(sessionStorage.getItem("subType")) || {});
  const [priorityLevel, setPriorityLevel]=useState(JSON?.parse(sessionStorage.getItem("PriorityLevel"))||{})
  const [pincode, setPincode] = useState("");
  const [mobileNumber, setMobileNumber] = useState(sessionStorage.getItem("mobileNumber") || "");
  const [fullName, setFullName] = useState(sessionStorage.getItem("name") || "");
  const [emailId, setEmail] = useState(sessionStorage.getItem("emailId") || "");
  const [selectedCity, setSelectedCity] = useState(getCities()[0] ? getCities()[0] : null);
  const [isMapPopupOpen, setIsMapPopupOpen] = useState(false);

const [propertyId, setPropertyId]= useState("")
const [description, setDescription] = useState("")
  const { data: fetchedLocalities } = Digit.Hooks.useBoundaryLocalities(
    getCities()[0]?.code,
    "admin",
    {
      enabled: !!getCities()[0],
    },
    t
  );

  const [localities, setLocalities] = useState(fetchedLocalities);
  const [selectedLocality, setSelectedLocality] = useState(null);
  const [canSubmit, setSubmitValve] = useState(false);
  const [submitted, setSubmitted] = useState(false);
  const [property,setPropertyData]=useState(null)
  const [pincodeNotValid, setPincodeNotValid] = useState(false);
  const [params, setParams] = useState({});
  const tenantId = window.Digit.SessionStorage.get("CITIZEN.COMMON.HOME.CITY")?.code;
  const menu = Digit.Hooks.pgr.useComplaintTypes({ stateCode: "pg" });
  console.log("menumenu",menu)
  const [uploadedImagesn, setUploadedImagesIds] = useState("");


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
  const dispatch = useDispatch();
  const match = useRouteMatch();
  const history = useHistory();
  const serviceDefinitions = Digit.GetServiceDefinitions;
  const client = useQueryClient();
  useEffect(() => {
    if (complaintType?.key && subType?.key && selectedCity?.code && selectedLocality?.code && priorityLevel?.code ) {
      setSubmitValve(true);
    } else {
      setSubmitValve(false);
    }
  }, [complaintType, subType, priorityLevel, selectedCity, selectedLocality]);

  useEffect(() => {
    setLocalities(fetchedLocalities);
  }, [fetchedLocalities]);
  const handleUpload = (ids) => {
    setUploadedImagesIds(ids);
    // Digit.SessionStorage.set("PGR_CREATE_IMAGES", ids);
  };
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

  async function selectedType(value) {
    if (value.key !== complaintType.key) {
      if (value.key === "Others") {
        setSubType({ name: "" });
        setComplaintType(value);
        sessionStorage.setItem("complaintType",JSON.stringify(value))
        setSubTypeMenu([{ key: "Others", name: t("SERVICEDEFS.OTHERS") }]);
      } else {
        setSubType({ name: "" });
        setComplaintType(value);
        sessionStorage.setItem("complaintType",JSON.stringify(value))
        setSubTypeMenu(await serviceDefinitions.getSubMenu(tenantId, value, t));
      }
    }
  }
  async function selectedPriorityLevel(value){
    sessionStorage.setItem("priorityLevel", JSON.stringify(value))
    setPriorityLevel(value);
    //setPriorityMenu(await serviceDefinitions.getSubMen)
  }

  function selectedSubType(value) {
    sessionStorage.setItem("subType",JSON.stringify(value))
    setSubType(value);
  }

  // city locality logic
  const selectCity = async (city) => {
    // if (selectedCity?.code !== city.code) {}
    return;
  };

  function selectLocality(locality) {
    setSelectedLocality(locality);
  }

  const wrapperSubmit = (data) => {
    if (!canSubmit) return;
    setSubmitted(true);
    !submitted && onSubmit(data);
  };
  //On SUbmit
  const onSubmit = async (data) => {
    if (!canSubmit) return;
    const cityCode = selectedCity.code;
    const city = selectedCity.city.name;
    const district = selectedCity.city.name;
    const region = selectedCity.city.name;
    const localityCode = selectedLocality.code;
    const localityName = selectedLocality.name;
    const landmark = data?.landmark;
    const { key } = subType;
    const complaintType = key;
    //const prioritylevel=priorityLevel.code;
    const mobileNumber = data?.mobileNumber;
    const name = data?.name;
    const uploadedImages = uploadedImagesn?.map((url) => ({
      documentType: "PHOTO",
      fileStoreId: url,
      documentUid: "",
      additionalDetails: {},
    }));
    const emailId=data?.emailId;
    
    const formData = { ...data, cityCode, city, district, region, localityCode, localityName, landmark, complaintType, priorityLevel, mobileNumber, name,emailId,uploadedImages};
    await dispatch(createComplaint(formData));
    await client.refetchQueries(["fetchInboxData"]);
    localStorage.removeItem("pgrProperty");
    history.push(`/digit-ui/citizen/pgr/response`);
  };

  const handlePincode = (event) => {
    const { value } = event.target;
    setPincode(value);
    if (!value) {
      setPincodeNotValid(false);
    }
  };
  const handleMobileNumber = (event) => {
 
    const { value } = event.target;
    setMobileNumber(value);
  
  };
  const handleName = (event) => {
    const { value } = event.target;
    setFullName(value);
  };
  const handleEmail = (event) => {
    const { value } = event.target;
    setEmail(value);
  };
  const handleDescription = (event) => {
    const { value } = event.target;
    setDescription(value);
  };
  
  const isPincodeValid = () => !pincodeNotValid;

  const config = [

    {
      head: t("CS_COMPLAINT_DETAILS_COMPLAINT_DETAILS"),
      body: [
        {
          label: t("CS_COMPLAINT_DETAILS_COMPLAINT_TYPE"),
          isMandatory: true,
          type: "dropdown",
          populators: <Dropdown option={menu} optionKey="name" id="complaintType" selected={complaintType} select={selectedType} />,
        },
        {
          label: t("CS_COMPLAINT_DETAILS_COMPLAINT_SUBTYPE"),
          isMandatory: true,
          type: "dropdown",
          menu: { ...subTypeMenu },
          populators: <Dropdown option={subTypeMenu} optionKey="name" id="complaintSubType" selected={subType} select={selectedSubType} />,
        },
        {
          
         label: t("CS_COMPLAINT_DETAILS_COMPLAINT_PRIORITY_LEVEL"),
            isMandatory: true,
            type: "dropdown",
            populators: <Dropdown option={priorityMenu} optionKey="name" id="priorityLevel" selected={priorityLevel} select={selectedPriorityLevel} />,
          
        },
        {
          label: t("CS_COMPLAINT_DETAILS_ADDITIONAL_DETAILS"),
          type: "textarea",
          onChange: handleDescription,
          value:description,
          populators: {
            name: "description",
            onChange: handleDescription,
          },
        },
        {
          label: t("CS_ADDCOMPLAINT_EVIDENCE"),
          type: "custom",
          populators: (
            <ImageUploadHandler tenantId={tenantId} uploadedImages={uploadedImagesn} onPhotoChange={handleUpload} />
          ),
        },
        {
          //label: t("WS_COMMON_PROPERTY_DETAILS"),
          "isEditConnection": true,
          "isCreateConnection": true,
          "isModifyConnection": true,
          "isEditByConfigConnection": true,
          "isProperty":subType?.key?.includes("Property")?true:false,
          component: "CPTPropertySearchNSummary",
          key: "cpt",
          type: "component",
          "body": [
              {
                  "component": "CPTPropertySearchNSummary",
                  "withoutLabel": true,
                  "key": "cpt",
                  "type": "component",
                  "hideInCitizen": true
              }
          ]
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
            validation: { pattern: /^[1-9][0-9]{5}$/, validate: isPincodeValid },
            error: t("CORE_COMMON_PINCODE_INVALID"),
            onChange: handlePincode,
            componentInFront: (
              <div style={{ display: "flex", alignItems: "center" }} onClick={() => setIsMapPopupOpen(true)}>
                <span style={{ marginRight: "8px" }}>📍</span>
              </div>
            ),
            
          },
        }
,        
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
              select={selectCity}
              optionKey="i18nKey"
              t={t}
            />
          ),
        },
        {
          label: t("CS_CREATECOMPLAINT_MOHALLA"),
          type: "dropdown",
          isMandatory: true,
          dependency: selectedCity && localities ? true : false,
          populators: (
            <Dropdown isMandatory selected={selectedLocality} optionKey="i18nkey" id="locality" option={localities} select={selectLocality} t={t} />
          ),
        },
        {
          label: t("CS_COMPLAINT_DETAILS_LANDMARK"),
          type: "textarea",
          populators: {
            name: "landmark",
          },
        },
      ],
    },

    {
      head: t("ES_CREATECOMPLAINT_PROVIDE_COMPLAINANT_DETAILS"),
      body: [
        {
          label: t("ES_CREATECOMPLAINT_MOBILE_NUMBER"),
          isMandatory: true,
          type: "text",
          value:mobileNumber || window.Digit.SessionStorage.get("User")?.info?.mobileNumber,
          onChange: handleMobileNumber,
          populators: {
            name: "mobileNumber",
            onChange: handleMobileNumber,
            validation: {
              required: true,
              pattern: /^[6-9]\d{9}$/,  
            },
            // componentInFront: <div className="employee-card-input employee-card-input--front">+91</div>,
            error: t("CORE_COMMON_MOBILE_ERROR"),
          },
        },
        {
          label: t("ES_CREATECOMPLAINT_COMPLAINT_NAME"),
          isMandatory: true,
          type: "text",
          value:fullName || window.Digit.SessionStorage.get("User")?.info?.name,
          populators: {
            name: "name",
            onChange: handleName,
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
          value:emailId || window.Digit.SessionStorage.get("User")?.info?.emailId ,
          populators: {
            name: "emailId",
            onChange: handleEmail,
            validation: {
              //required: true,
              pattern: /[A-Za-z0-9._%+-]+@[a-z0-9.-]+\.[a-z]{2,4}$/,
            },
            error: t("CS_ADDCOMPLAINT_EMAIL_ERROR"),
          },
        },
      ],
    }
  ];
    useEffect(()=>{
      console.log("heloo world",propetyData )
      if(propetyData !== "undefined"   && propetyData !== null)
      {
       let data =JSON.parse(propetyData)
       console.log("stp 1",propetyData)
       setPropertyData(data)
        setPropertyId(data?.propertyId)
      }
    },[])
  useEffect(()=>{
    console.log("step 2",propetyData,property,typeof(propetyData))
    if(property !== "undefined" && property !== null )
    {
      let data =property
     
      setPincode(data?.address?.pincode || "")
      
      let b= localities.filter((item)=>{
        return item.code === data?.address?.locality?.code
      })
      setSelectedLocality(b?.[0])
      setDescription(data?.propertyId)
      console.log("pgrProperty",localities,data?.propertyId,data)
    }
   
  },[propertyId])
  return (
    <React.Fragment>
<FormComposer
  heading={t("ES_CREATECOMPLAINT_NEW_COMPLAINT")}
  config={config}
  onSubmit={wrapperSubmit}
  isDisabled={!canSubmit && !submitted}
  label={t("CS_ADDCOMPLAINT_ADDITIONAL_DETAILS_SUBMIT_COMPLAINT")}
/>
{isMapPopupOpen && (
  <div className="map-popup-overlay">
    <div className="map-popup">
      <h3>{t("CS_ADDCOMPLAINT_SELECT_GEOLOCATION_TEXT")}</h3>
      <LocationSearch onChange={(code) => {
        setPincode(code);
        setIsMapPopupOpen(false); // close popup after selection
      }} />
      <button className="close-btn" onClick={() => setIsMapPopupOpen(false)}>
        {t("CORE_COMMON_CLOSE")}
      </button>
    </div>
  </div>
)}

<style>
{`
.map-popup-overlay {
  position: fixed;
  top: 0; left: 0;
  width: 100%; height: 100%;
  background: rgba(0,0,0,0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 2000;
}

.map-popup {
  background: #fff;
  padding: 20px;
  border-radius: 12px;
  max-width: 600px;
  width: 90%;
  box-shadow: 0 4px 12px rgba(0,0,0,0.2);
}

.map-popup h3 {
  margin-bottom: 12px;
}

.map-popup .close-btn {
  margin-top: 12px;
  background: #0056ad;
  color: #fff;
  border: none;
  padding: 8px 16px;
  border-radius: 8px;
  cursor: pointer;
}

/* ---------- Overall Form ---------- */
.styled-form {
  background: #fff;
  border-radius: 16px;
  padding: 24px 32px;
  box-shadow: 0 6px 20px rgba(0,0,0,0.08);
  max-width: 1200px;
  margin: 0 auto;
}

/* ---------- Page Heading ---------- */
.form-header {
  font-size: 24px;
  font-weight: 700;
  color: #222;
  margin-bottom: 28px;
  display: flex;
  align-items: center;
  gap: 10px;
}
.form-header::before {
  content: "📋";
  font-size: 24px;
}

/* ---------- Section Header ---------- */
.section-header {
  font-size: 18px;
  font-weight: 600;
  color: #444;
  margin-bottom: 12px;
}
.section-divider {
  border: none;
  border-top: 1px solid #eee;
  margin: 0 0 24px 0;
}

/* ---------- Grid Layout ---------- */
.form-section-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr); /* 👈 3 columns */
  gap: 20px;
  margin-bottom: 32px;
}
.form-field-wrapper {
  display: flex;
  flex-direction: column;
}
.form-field-wrapper label {
  font-weight: 500;
  margin-bottom: 6px;
  color: #555;
}
.card .card-label, .card-emp .card-label {
  font-size: 16px;
  line-height: 23px;
  --text-opacity:1;
  color: #0b0c0c;
  color: rgba(11, 12, 12, var(--text-opacity));
  margin-bottom: 8px; }


/* ---------- Error ---------- */
.card-label-error {
  color: #ff4d4f;
  font-size: 12px;
  margin-top: 4px;
}

/* ---------- Sticky Action Bar ---------- */
.sticky-action-bar {
  position: sticky;
  bottom: 0;
  background: #fff;
  border-top: 1px solid #eee;
  padding: 16px 24px;
  display: flex;
  justify-content: flex-end;
  z-index: 10;
  border-radius: 0 0 16px 16px;
}
html, body {
  scroll-behavior: smooth;
}
.styled-form {
  scroll-behavior: smooth;
}
.styled-form {
  -webkit-overflow-scrolling: touch;
  overflow-y: auto; /* only if height is fixed */
}
/* ---------- Responsive ---------- */
@media (max-width: 992px) {
  .form-section-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}
@media (max-width: 600px) {
  .form-section-grid {
    grid-template-columns: 1fr;
  }
}
@media (min-width: 780px) {
  .bill-citizen, .bills-citizen-wrapper, .citizen-all-services-wrapper, .citizen-obps-wrapper, .engagement-citizen-wrapper, .fsm-citizen-wrapper, .mcollect-citizen, .payer-bills-citizen-wrapper, .pgr-citizen-wrapper, .pt-citizen, .selection-card-wrapper, .tl-citizen, .ws-citizen-wrapper {
    width: 100%;
    padding-right: 16px;
    margin-top: 2rem; } }
`}
</style>
</React.Fragment>
  );
};
