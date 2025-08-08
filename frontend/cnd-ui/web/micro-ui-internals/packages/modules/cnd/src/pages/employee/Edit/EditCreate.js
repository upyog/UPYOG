import { FormComposer, Loader } from "@nudmcdgnpm/digit-ui-react-components";
import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useHistory } from "react-router-dom";
import { useParams } from "react-router-dom";
import { EditConfig } from "../../../config/editConfig";
import { ApplicationProvider } from "./ApplicationContext";
import { CND_VARIABLES } from "../../../utils";
import { cndStyles } from "../../../utils/cndStyles";

/**
 * Parent Component For Edit Case, 
 * Here we have used Formcomposer as well as ApplicationContext (useContext API) to pass the data from Parent to Child Component to pre-fill the data.
 *  When Employee Submit the Edited the Application the payload will go and URL will be redirected to the response page 
 */


const EditCreate = () => {
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const isUserDetailRequired=true;
  const { id: applicationNumber } = useParams();
  const { t } = useTranslation();
  const [canSubmit, setSubmitValve] = useState(false);
  const [isLoading, setIsLoading] = useState(true);
  const history = useHistory();
  const { data: applicationDetails } = Digit.Hooks.cnd.useCndApplicationDetails(t, tenantId, applicationNumber,isUserDetailRequired);
   const [_formData, setFormData,_clear] = Digit.Hooks.useSessionStorage("store-data",null);
   const [mutationHappened, setMutationHappened, clear] = Digit.Hooks.useSessionStorage("EMPLOYEE_MUTATION_HAPPENED", false);
  const [successData, setsuccessData, clearSuccessData] = Digit.Hooks.useSessionStorage("EMPLOYEE_MUTATION_SUCCESS_DATA", { });


  useEffect(() => {
    setMutationHappened(false);
    clearSuccessData();
  }, []);

  useEffect(() => {
    if (applicationDetails) {
      setIsLoading(false);
    }
  }, [applicationDetails]);
  
  if (isLoading) {
    return <Loader />;
  }


  
  const onFormValueChange = (setValue, formData, formState) => {
    // Check if waste type quantity is filled and greater than zero
    let hasValidWasteQuantity = false;
    // Check if total waste quantity field is filled with value > 0
    if (formData?.wasteType?.wasteQuantity) {
      // Extract numeric value from "0 Tons" format
      const match = formData.wasteType.wasteQuantity.toString().match(/(\d+(\.\d+)?)/);
      const numericValue = match ? parseFloat(match[0]) : 0;
      if (numericValue > 0) {
        hasValidWasteQuantity = true;
      }
    }
    setSubmitValve(!Object.keys(formState.errors).length && hasValidWasteQuantity);
  };

    // Extract just the numeric value from the waste quantity string
  const extractNumericValue = (quantityString) => {
    if (!quantityString) return "";
    // Match numeric values (including decimals)
    const match = quantityString.toString().match(/(\d+(\.\d+)?)/);
    return match ? match[0] : "";
  };


  const onSubmit = (data) => {
    const user = Digit.UserService.getUser();
    const formData = {
      tenantId: applicationDetails?.applicationData?.applicationData?.tenantId,
        additionalDetails: null,
        applicationType: "REQUEST_FOR_PICKUP",
        applicationStatus: applicationDetails?.applicationData?.applicationData?.applicationStatus,
        depositCentreDetails: "",
        description: "",
        locality: applicationDetails?.applicationData?.applicationData?.addressDetail?.locality,
        applicationId: applicationDetails?.applicationData?.applicationData?.applicationId,
        applicationNumber: applicationDetails?.applicationData?.applicationData?.applicationNumber,
        vehicleType: "",
        location: "",
        completedOn:"",
        constructionFromDate: data?.propertyNature?.constructionFrom || applicationDetails?.applicationData?.applicationData?.constructionFromDate,
        constructionToDate:data?.propertyNature?.constructionTo || applicationDetails?.applicationData?.applicationData?.constructionToDate,
        propertyType: data?.propertyNature?.propertyUsage?.code||applicationDetails?.applicationData?.applicationData?.propertyType,
        houseArea: data?.propertyNature?.houseArea || applicationDetails?.applicationData?.applicationData?.houseArea,
        applicantDetailId: applicationDetails?.applicationData?.applicationData?.applicantDetailId,
        totalWasteQuantity: extractNumericValue(data?.wasteType?.wasteQuantity) || applicationDetails?.applicationData?.applicationData?.totalWasteQuantity,
        typeOfConstruction: data?.propertyNature?.constructionType?.code || applicationDetails?.applicationData?.applicationData?.typeOfConstruction,
        noOfTrips: 0,
        pickupDate:data?.wasteType?.pickupDate,
        requestedPickupDate:data?.wasteType?.pickupDate || applicationDetails?.applicationData?.applicationData?.requestedPickupDate,
        facilityCenterDetail: {
            disposalId: "",
            netWeight: extractNumericValue(data?.wasteType?.wasteQuantity) || applicationDetails?.applicationData?.applicationData?.totalWasteQuantity
        },
        wasteTypeDetails: data?.wasteType?.wasteMaterialType?.map(item => {
          const matchedWaste = applicationDetails?.applicationData?.applicationData?.wasteTypeDetails?.find(
            (w) => w.wasteType === item.code
          );
        
          return {
            applicationId: matchedWaste?.applicationId || "",
            wasteTypeId: matchedWaste?.wasteTypeId || "",
            enteredByUserType: user?.info?.type,
            wasteType: item.code,
            quantity: data?.wasteType?.wasteDetails?.[item.code]?.quantity || 0,
            metrics: data?.wasteType?.wasteDetails?.[item.code]?.unit || "",
            auditDetails: matchedWaste?.auditDetails || null
          };
        }) || [],
        documentDetails: [
            ...(data?.wasteType?.siteMediaPhoto ? [{
                documentDetailId: "",
                applicationId: "",
                documentType: CND_VARIABLES.SITE_MEDIA_PHOTO,
                uploadedByUserType: user?.info?.type,
                fileStoreId: data.wasteType.siteMediaPhoto,
                auditDetails: null
            }] : []),
            ...(data?.wasteType?.siteStack ? [{
                documentDetailId: "",
                applicationId: "",
                documentType: CND_VARIABLES.SITE_STACK_PHOTO,
                uploadedByUserType: user?.info?.type,
                fileStoreId: data.wasteType.siteStack,
                auditDetails: null
            }] : [])
        ],
        workflow: {
        action: "APPROVE",
        comments: "",
        businessService: "cnd",
        moduleName: "cnd-service"
        },
        applicantDetail: {
            nameOfApplicant: applicationDetails?.applicationData?.applicationData?.applicantDetail?.nameOfApplicant,
            mobileNumber: applicationDetails?.applicationData?.applicationData?.applicantDetail?.mobileNumber,
            alternateMobileNumber: applicationDetails?.applicationData?.applicationData?.applicantDetail?.alternateMobileNumber,
            emailId: applicationDetails?.applicationData?.applicationData?.applicantDetail?.emailId
        },
        addressDetail: {
            houseNumber: applicationDetails?.applicationData?.applicationData?.addressDetail?.houseNumber,
            addressLine1: applicationDetails?.applicationData?.applicationData?.addressDetail?.addressLine1,
            addressLine2: applicationDetails?.applicationData?.applicationData?.addressDetail?.addressLine2,
            landmark: applicationDetails?.applicationData?.applicationData?.addressDetail?.landmark,
            floorNumber: null,
            locality: applicationDetails?.applicationData?.applicationData?.addressDetail?.locality,
            city: applicationDetails?.applicationData?.applicationData?.addressDetail?.city,
            pinCode: applicationDetails?.applicationData?.applicationData?.addressDetail?.pinCode
        }
    };

    history.replace("/cnd-ui/employee/cnd/edit-response", { cndApplication: formData }); 
    
  };

  
  return (
    <ApplicationProvider applicationDetails={applicationDetails?.applicationData?.applicationData}>
    <FormComposer
      heading={t("CND_EDIT_PAGE")}
      isDisabled={!canSubmit}
      label={t("ES_COMMON_APPLICATION_SUBMIT")}
      config={EditConfig.map((config) => {
       
        return {
          ...config,
          body: config.body.filter((a) => !a.hideInEmployee),
        };
      })}
      fieldStyle={cndStyles.fieldStyle}
      cardStyle={cndStyles.cardStyle}
      onSubmit={onSubmit}
      onFormValueChange={onFormValueChange}
    />
    </ApplicationProvider>
  );
};

export default EditCreate;



