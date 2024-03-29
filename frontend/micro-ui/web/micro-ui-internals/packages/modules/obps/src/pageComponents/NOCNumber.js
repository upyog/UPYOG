import React, { useState } from "react";
import { FormStep} from "@upyog/digit-ui-react-components";
import Timeline from "../components/Timeline";

const NOCNumber = ({ t, config, onSelect, userType, formData, setError: setFormError, clearErrors: clearFormErrors, formState }) => {
    const [nocNumber,setValueNoc]=useState(null);
    const tenantId = Digit.ULBService.getCurrentTenantId();

    function onChange(data) {
        setValueNoc(data.target.value);              
      }

      function onClick(){
        console.log("inside_search")
      }

      function getusageCategoryAPI(arr){
        let usageCat = ""
        arr.map((ob,i) => {
            usageCat = usageCat + (i !==0?",":"") + ob.code;
        });
        return usageCat;
    }

      function getUnitsForAPI(subOccupancyData){
        const ob = subOccupancyData?.subOccupancy;
        const blocksDetails = subOccupancyData?.data?.edcrDetails?.planDetail?.blocks || [];
        let units=[];
        if(ob) {
            let result = Object.entries(ob);
            result.map((unit,index)=>{
                units.push({
                    blockIndex:index,
                    floorNo:unit[0].split("_")[1],
                    unitType:"Block",
                    occupancyType: blocksDetails?.[index]?.building?.occupancies?.[0]?.typeHelper?.type?.code || "A", 
                    usageCategory:getusageCategoryAPI(unit[1]),
                });
            })
        }
        return units;
    }

    function getBlockIds(arr){
        let blockId = {};
        arr.map((ob,ind)=>{
            blockId[`Block_${ob.floorNo}`]=ob.id;
        });
        return blockId;
    }

      const handleSubmit = () => {
            let owner = formData.owners;
            let ownerStep;
            ownerStep = { ...owner};

            if (!formData?.id) {
                //setIsDisable(true);
                //for owners conversion
                let ownershipCategory=formData?.owners?.ownershipCategory;
                let conversionOwners = [];
                ownerStep?.owners?.map(owner => {
                    conversionOwners.push({
                        ...owner,
                        active:true,
                        name: owner.name,
                        emailId:owner.emailId,
                        mobileNumber: owner.mobileNumber,
                        isPrimaryOwner: owner.isPrimaryOwner,
                        gender: owner.gender.code,
                        fatherOrHusbandName: "NAME"
                    })
                });
                let payload = {};
                payload.edcrNumber = formData?.edcrNumber?.edcrNumber ? formData?.edcrNumber?.edcrNumber :formData?.data?.scrutinyNumber?.edcrNumber;
                payload.riskType = formData?.data?.riskType;
                payload.applicationType = formData?.data?.applicationType;
                payload.serviceType = formData?.data?.serviceType;

                const userInfo = Digit.UserService.getUser();
                const accountId = userInfo?.info?.uuid;
                payload.tenantId = formData?.address?.city?.code;
                payload.workflow = { action: "INITIATE", assignes : [userInfo?.info?.uuid] };
                payload.accountId = accountId;
                payload.documents = null;

                // Additonal details
                payload.additionalDetails = {GISPlaceName:formData?.address?.placeName};
                payload.additionalDetails.boundaryWallLength = formData?.data?.boundaryWallLength || "NA";
                payload.additionalDetails.area  = (formData?.data.edcrDetails.planDetail.planInformation.plotArea).toString()||  "NA";
                payload.additionalDetails.height  = (formData?.data.edcrDetails.planDetail.blocks[0].building.buildingHeight).toString() || "NA";
                payload.additionalDetails.usage  = formData?.data.occupancyType  || "NA";
                payload.additionalDetails.builtUpArea =(formData?.data.edcrDetails.planDetail.blocks[0].building.totalBuitUpArea).toString();
                payload.additionalDetails.nocNumber=nocNumber;
                if (formData?.data?.holdingNumber) payload.additionalDetails.holdingNo = formData?.data?.holdingNumber;
                if (formData?.data?.registrationDetails) payload.additionalDetails.registrationDetails = formData?.data?.registrationDetails;
                if (formData?.data?.applicationType) payload.additionalDetails.applicationType = formData?.data?.applicationType;
                if (formData?.data?.serviceType) payload.additionalDetails.serviceType = formData?.data?.serviceType;
                //For LandInfo
                payload.landInfo = {};
                //For Address
                payload.landInfo.address = {};
                if (formData?.address?.city?.code) payload.landInfo.address.city = formData?.address?.city?.code;
                if (formData?.address?.locality?.code) payload.landInfo.address.locality = { code: formData?.address?.locality?.code };
                if (formData?.address?.pincode) payload.landInfo.address.pincode = formData?.address?.pincode;
                if (formData?.address?.landmark) payload.landInfo.address.landmark = formData?.address?.landmark;
                if (formData?.address?.street) payload.landInfo.address.street = formData?.address?.street;
                if (formData?.address?.geoLocation) payload.landInfo.address.geoLocation = formData?.address?.geoLocation;

                payload.landInfo.owners = conversionOwners;
                payload.landInfo.ownershipCategory = ownershipCategory.code;
                payload.landInfo.tenantId = formData?.address?.city?.code;

                //for units
                const blockOccupancyDetails = formData;
                payload.landInfo.unit = getUnitsForAPI(blockOccupancyDetails);

                let nameOfAchitect = sessionStorage.getItem("BPA_ARCHITECT_NAME");
                let parsedArchitectName = nameOfAchitect ? JSON.parse(nameOfAchitect) : "ARCHITECT";
                payload.additionalDetails.typeOfArchitect = parsedArchitectName;
                let isSelfCertificationRequired=sessionStorage.getItem("isSelfCertificationRequired");
                if(isSelfCertificationRequired==="undefined"){
                    isSelfCertificationRequired="false";
                }
                payload.additionalDetails.isSelfCertificationRequired = isSelfCertificationRequired.toString();
                // create BPA call
                if(isSelfCertificationRequired===true && formData?.data.occupancyType==="Residential" && (parsedArchitectName=="ARCHITECT" || parsedArchitectName=="ENGINEER"|| parsedArchitectName=="DESIGNER" || parsedArchitectName=="SUPERVISOR")){
                    if(formData?.data.edcrDetails.planDetail.blocks[0].building.buildingHeight > 15){
                        alert("Height should not be more than 15 metres");
                    }
                    else if((parsedArchitectName=="ARCHITECT" || parsedArchitectName=="ENGINEER") && formData?.data.edcrDetails.planDetail.planInformation.plotArea>500){
                            alert("Architect/Engineer can apply for area less then 500 sq. yards. in self declaration")
                        }
                    else if((parsedArchitectName=="DESIGNER" || parsedArchitectName=="SUPERVISOR") && formData?.data.edcrDetails.planDetail.planInformation.plotArea>250){
                            alert("Designer/Supervisor can apply for area less then 500 sq. yards. in self declaration")
                        }
                    else{
                            Digit.OBPSService.create({ BPA: payload }, tenantId)
                            .then((result, err) => {
                                if (result?.BPA?.length > 0) {
                                    result?.BPA?.[0]?.landInfo?.owners?.forEach(owner => {
                                        owner.gender = { code: owner.gender, active: true, i18nKey: `COMMON_GENDER_${owner.gender}` }
                                    });
                                    result.BPA[0].owners = { ...owner, owners: result?.BPA?.[0]?.landInfo?.owners, ownershipCategory: ownershipCategory };
                                    result.BPA[0].address = result?.BPA?.[0]?.landInfo?.address;
                                    result.BPA[0].address.city = formData.address.city;
                                    result.BPA[0].address.locality = formData.address.locality;
                                    result.BPA[0].placeName = formData?.address?.placeName;
                                    result.BPA[0].data = formData.data;
                                    result.BPA[0].BlockIds = getBlockIds(result.BPA[0].landInfo.unit);
                                    result.BPA[0].subOccupancy= formData?.subOccupancy;
                                    result.BPA[0].uiFlow = formData?.uiFlow;
                                    //setIsDisable(false);
                                    onSelect("", result.BPA[0], "", true);
                                }
                            })
                            .catch((e) => {
                               // setIsDisable(false);
                                setShowToast({ key: "true", error: true, message: e?.response?.data?.Errors[0]?.message });
                            });
                        }                  
                    
                }
                else{
                    Digit.OBPSService.create({ BPA: payload }, tenantId)
                    .then((result, err) => {
                        if (result?.BPA?.length > 0) {
                            result?.BPA?.[0]?.landInfo?.owners?.forEach(owner => {
                                owner.gender = { code: owner.gender, active: true, i18nKey: `COMMON_GENDER_${owner.gender}` }
                            });
                            result.BPA[0].owners = { ...owner, owners: result?.BPA?.[0]?.landInfo?.owners, ownershipCategory: ownershipCategory };
                            result.BPA[0].address = result?.BPA?.[0]?.landInfo?.address;
                            result.BPA[0].address.city = formData.address.city;
                            result.BPA[0].address.locality = formData.address.locality;
                            result.BPA[0].placeName = formData?.address?.placeName;
                            result.BPA[0].data = formData.data;
                            result.BPA[0].BlockIds = getBlockIds(result.BPA[0].landInfo.unit);
                            result.BPA[0].subOccupancy= formData?.subOccupancy;
                            result.BPA[0].uiFlow = formData?.uiFlow;
                           // setIsDisable(false);
                            onSelect("", result.BPA[0], "", true);
                        }
                    })
                    .catch((e) => {
                        //setIsDisable(false);
                        setShowToast({ key: "true", error: true, message: e?.response?.data?.Errors[0]?.message });
                    });
                }
            } else {
                onSelect(config.key, ownerStep);
            }
    };

      return (
        <div>
            <Timeline currentStep={3} />
            <FormStep
                    t={t}
                    config={config}
                    onSelect={handleSubmit}
                    isDisabled={!nocNumber}
                    onChange={onChange}
                >
                <span  onClick={onClick} style={{alignSelf:'flex-start', marginBottom:'5px', backgroundColor:'maroon', border:'1px solid marron',color:'white',padding:'5px 10px',borderRadius:'3px',cursor:'pointer'}}>SEARCH NOC</span> 
                </FormStep> 
        </div>

      );
}

export default NOCNumber