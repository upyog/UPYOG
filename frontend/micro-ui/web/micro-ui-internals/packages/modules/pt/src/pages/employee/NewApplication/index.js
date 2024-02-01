import { FormComposer, Loader,Modal ,Card , CardHeader, StatusTable,Row } from "@egovernments/digit-ui-react-components";
import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useHistory } from "react-router-dom";
import { newConfig } from "../../../config/Create/config";

const NewApplication = () => {
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const tenants = Digit.Hooks.pt.useTenants();
  const { t } = useTranslation();
  const [canSubmit, setSubmitValve] = useState(false);
  const defaultValues = { };
  const history = useHistory();
  const [showToast, setShowToast] = useState(null);
  const [searchData, setSearchData] = useState({});
  const { data: propertyData, isLoading: propertyDataLoading, error, isSuccess, billData } = Digit.Hooks.pt.usePropertySearchWithDue({
    tenantId: searchData?.city,
    filters: searchData?.filters,
    auth: true /*  to enable open search set false  */,
    configs: { enabled: Object.keys(searchData).length > 0, retry: false, retryOnMount: false, staleTime: Infinity },
  });
  const [formData,setFormData]=useState({})
  // delete
  // const [_formData, setFormData,_clear] = Digit.Hooks.useSessionStorage("store-data",null);
  const [mutationHappened, setMutationHappened, clear] = Digit.Hooks.useSessionStorage("EMPLOYEE_MUTATION_HAPPENED", false);
  const [successData, setsuccessData, clearSuccessData] = Digit.Hooks.useSessionStorage("EMPLOYEE_MUTATION_SUCCESS_DATA", { });
  const { data: commonFields, isLoading } = Digit.Hooks.pt.useMDMS(Digit.ULBService.getStateId(), "PropertyTax", "CommonFieldsConfig");
  useEffect(() => {
    setMutationHappened(false);
    clearSuccessData();
  }, []);

  const onFormValueChange = (setValue, formData, formState) => {
    setSubmitValve(!Object.keys(formState.errors).length);
    let addressError= formData?.address?.street == "" || formData?.address?.doorNo == "" || !formData?.address?.doorNo || !formData?.address?.street || Object.keys(formState.errors).length? setSubmitValve(false): setSubmitValve(true);
    if (Object.keys(formState.errors).length === 1 && (formState.errors?.units?.message.includes("arv")|| formState.errors?.units?.message.includes("RentedMonths") ) ){
      setSubmitValve(!formData?.units.some((unit) => unit.occupancyType === "RENTED" && !unit.arv));
    }
    if (formData?.ownershipCategory?.code?.includes("MULTIPLEOWNERS") && formData?.owners?.length < 2) {
      setSubmitValve(false);
    }
    let pincode = formData?.address?.pincode;
    if (pincode) {
      if (!Digit.Utils.getPattern("Pincode").test(pincode)) setSubmitValve(false);
      const foundValue = tenants?.find((obj) => obj.pincode?.find((item) => item.toString() === pincode));
      if (!foundValue) {
        setSubmitValve(false);
      }
    }
  };

  const onSubmit = (data) => {
    let dataNew = data?.units?.map((value)=>{
      let additionalDetails ={"structureType" : value.structureType,"ageOfProperty":value.ageOfProperty }
      return {...value,additionalDetails}
    })
    data.units = dataNew
    const formData = {
      tenantId,
      address: {
        ...data?.address,
        city: data?.address?.city?.name,
        locality: { code: data?.address?.locality?.code, area: data?.address?.locality?.area },
      },
      usageCategory: data?.usageCategoryMajor.code,
      usageCategoryMajor: data?.usageCategoryMajor?.code.split(".")[0],
      usageCategoryMinor: data?.usageCategoryMajor?.code.split(".")[1] || null,
      landArea: Number(data?.landarea),
      superBuiltUpArea: Number(data?.landarea),
      propertyType: data?.PropertyType?.code,
      noOfFloors: Number(data?.noOfFloors),
      ownershipCategory: data?.ownershipCategory?.code,
      additionalDetails:{
      RentedMonths: data?.units[0]?.RentedMonths,
      NonRentedMonthsUsage: data?.units[0]?.NonRentedMonthsUsage,
      // ageOfProperty:data?.units[0]?.ageOfProperty,
      // structureType:data?.units[0]?.structureType,
      electricity:data?.electricity,
      uid:data?.uid
      },
      owners: data?.owners.map((owner,index) => {
        let {
          name,
          mobileNumber,
          designation,
          altContactNumber,
          emailId,
          correspondenceAddress,
          isCorrespondenceAddress,
          ownerType,
          fatherOrHusbandName,
        } = owner;
        let __owner;

        if (!data?.ownershipCategory?.code.includes("INDIVIDUAL")) {
          __owner = { name, mobileNumber, designation, altContactNumber, emailId, correspondenceAddress, isCorrespondenceAddress, ownerType };
        } else {
          __owner = {
            name,
            mobileNumber,
            correspondenceAddress,
            permanentAddress: data?.address?.locality?.name,
            relationship: owner?.relationship.code,
            fatherOrHusbandName,
            gender: owner?.gender.code,
            emailId,
            additionalDetails:{ownerSequence:index, ownerName:owner?.name}
            
          };
        }

        if (!__owner?.correspondenceAddress) __owner.correspondenceAddress = "";

        const _owner = {
          ...__owner,
          ownerType: owner?.ownerType?.code,
        };
        if (_owner.ownerType !== "NONE") {
          const { documentType, documentUid } = owner?.documents;
          _owner.documents = [
            { documentUid: documentUid, documentType: documentType.code, fileStoreId: documentUid },
            data?.documents?.documents?.find((e) => e.documentType?.includes("OWNER.IDENTITYPROOF")),
          ];
        } else {
          _owner.documents = [data?.documents?.documents?.find((e) => e.documentType?.includes("OWNER.IDENTITYPROOF"))];
        }
        return _owner;
      }),

      channel: "CFC_COUNTER", // required
      creationReason: "CREATE", // required
      source: "MUNICIPAL_RECORDS", // required
      units: data?.PropertyType?.code !== "VACANT" ? data?.units : [],
      documents: data?.documents?.documents,
      applicationStatus: "CREATE",
    };
    let tempObject={
      "mobileNumber":formData.owners?.[0].mobileNumber,
      "name":formData.owners?.[0].name,
      "doorNo": formData.address.doorNo,
      "locality": formData.address.locality.code,
      "isRequestForDuplicatePropertyValidation":true
    }
   
    if (!data?.ownershipCategory?.code.includes("INDIVIDUAL")) {
      formData.institution = {
        name: data.owners?.[0].institution.name,
        type: data.owners?.[0].institution.type?.code?.split(".")[1],
        designation: data.owners?.[0].designation,
        nameOfAuthorizedPerson: data.owners?.[0].name,
        tenantId: Digit.ULBService.getCurrentTenantId(),
      };
    }
  setFormData(formData)
  setSearchData({ city: Digit.ULBService.getCurrentTenantId(), filters: tempObject });
  };
 
  useEffect(() => {  
    if(propertyDataLoading && propertyData?.Properties.length >0)  
    {  
      //alert("property exist"),  
      setShowToast(true) 
    }  
    else if(propertyDataLoading && propertyData?.Properties.length === 0) {  
      setShowToast(false)  
      history.replace("/digit-ui/employee/pt/response", { Property: formData }); //current wala
    }  
    }, [propertyData]);
  /* use newConfig instead of commonFields for local development in case needed */

  const configs = commonFields?commonFields:newConfig;
  if (isLoading) {
    return <Loader />;
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
      <div className="icon-bg-secondary" onClick={props.onClick}>
        <Close />
      </div>
    );
  };
  const closeModal =() =>{
    setShowToast(false)
  }
  const setModal=()=>{
      setShowToast(false)   
      history.replace("/digit-ui/employee/pt/response", { Property: formData })
    }

  return (
    <div>   
    <FormComposer
      heading={t("ES_TITLE_NEW_PROPERTY_APPLICATION")}
      isDisabled={!canSubmit}
      label={t("ES_COMMON_APPLICATION_SUBMIT")}
      config={configs.map((config) => {   
        return {
          ...config,
          body: config.body.filter((a) => !a.hideInEmployee),
        };
      })}
      fieldStyle={{ marginRight: 0 }}
      onSubmit={onSubmit}
      defaultValues={defaultValues}
      onFormValueChange={onFormValueChange}
    />
    <div>
    { showToast &&   <Modal
    headerBarMain={<Heading label={t("CR_PROPERTY_NUMBER")} />}
    headerBarEnd={<CloseBtn onClick={closeModal} />}
    actionCancelLabel={"Cancel"}
    actionCancelOnSubmit={closeModal}
    actionSaveLabel={"Proceed"}
    actionSaveOnSubmit={setModal}
    formId="modal-action"
  >  <div style={{ width: "100%" }}>
  <Card>
      <CardHeader>Property Details</CardHeader>
   
          <StatusTable>
              <Row label={t("CR_PROPERTY_NUMBER")} text={propertyData?.Properties?.[0]?.propertyId || "NA"} textStyle={{ whiteSpace: "pre" }} />
              <Row label={t("CR_OWNER_NAME")} text={propertyData?.Properties?.[0]?.owners?.[0].name || "NA"} />
              <Row label={t("CR_MOBILE_NUMBER")} text={propertyData?.Properties?.[0]?.owners?.[0].mobileNumber|| "NA"} /> 
              <Row label={t("CR_ADDRESS")}    text={( propertyData?.Properties?.[0]?.address?.doorNo +", "+ propertyData?.Properties?.[0]?.address?.locality?.name +", "+ propertyData?.Properties?.[0]?.address?.city ) || "NA"}/>      
          </StatusTable>
  </Card>
</div>
    </Modal>}
  </div>
  </div>
  );
};

export default NewApplication;