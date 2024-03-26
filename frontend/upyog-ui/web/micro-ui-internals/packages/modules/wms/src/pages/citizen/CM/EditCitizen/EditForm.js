import { FormComposer, Toast,Loader } from "@egovernments/digit-ui-react-components";
import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useHistory } from "react-router-dom";
import { newConfig } from "../../../../config/contactMaster/contactMasterConfig";

// import { convertEpochToDate } from "../../components/Utils";

const EditForm = ({ tenantId, data }) => {
  const { t } = useTranslation();
  const history = useHistory();
  const [canSubmit, setSubmitValve] = useState(true);
  const [showToast, setShowToast] = useState(false);
  const [mdmsData, setmdmsData] = useState();
  const [editData, seteditData] = useState();
  
  const closeToast = () => {
    setShowToast(false);
  };
  useEffect(()=>{
    if(showToast){
    setTimeout(() => {
      closeToast();
      //history.push('/wms/cm-home/')
      history.replace('/upyog-ui/citizen/wms/cm-home')
    }, 5000);
  }
  },[showToast])
  const {mutate,isSuccess,isError,error,isLoading,...rest} = Digit?.Hooks?.wms?.cm?.useWmsCMEdit();
  console.log({isSuccess,isError,error, rest})
  const defaultValues = {
    tenantId: tenantId,
    WmsCMPFMSVendorID:{pfms_vendor_code:data?.pfms_vendor_code},
    WmsCMVendorType:{name:data?.vendor_type},
    WmsCMVendorName:{vendor_name:data?.vendor_name},
    WmsCMMobileNumber:{mobile_number:data?.mobile_number},
    WmsCMUIDNumber:{uid_number:data?.uid_number},
    WmsCMVendorStatus:{name:data?.vendor_status },
    WmsCMVATNumber:{VATNumber:data?.vat_number},
    WmsCMBankBranchIFSCCode:{name:data?.bank_branch_ifsc_code},
    WmsCMFunction:{name:data?.function},
    WmsCMVendorClass:{name:data?.vendor_class},
    WmsCMPFAccountNumber:{epfo_account_number:data?.epfo_account_number},
    WmsCMSubType:{name:data?.vendor_sub_type},
    WmsCMPayTo:{payto:data?.payto},
    WmsCMEmailId:{email:data?.email},
    WmsCMGSTNumber:{gst_number:data?.gst_number},
    WmsCMPANNumber:{pan_number:data?.pan_number},
    WmsCMBankAccountNumber:{bank_account_number:data?.bank_account_number},
    WmsCMPrimaryAccountHead:{name:data?.primary_account_head},
    WmsCMAddress:{address:data?.address},
    WmsCMAllowDirectPayment:{allow_direct_payment:data?.allow_direct_payment},
  };

  const onFormValueChange = (setValue = true, formData) => { };

  const onSubmit = async(input) => {
    // console.log("onSubmit input ", input);

    const updatedRecord = {"WMSContractorApplication": [{
            "vendor_type":input.WmsCMVendorType.name,
            "vendor_sub_type":input.WmsCMSubType.name,
            "vendor_name":input.WmsCMVendorName.vendor_name,
            "vendor_status":input.WmsCMVendorStatus.name,
            "pfms_vendor_code":input.WmsCMPFMSVendorID.pfms_vendor_code,
            "payto":input.WmsCMPayTo.name,
            "mobile_number":input.WmsCMMobileNumber.mobile_number,
            "email":input.WmsCMEmailId.email,
            "uid_number":input.WmsCMUIDNumber.uid_number,
            "gst_number":input.WmsCMGSTNumber.gst_number,
            "pan_number":input.WmsCMPANNumber.pan_number,
            "bank_branch_ifsc_code":input.WmsCMBankBranchIFSCCode.name,
            "bank_account_number":input.WmsCMBankAccountNumber.bank_account_number,
            "function":input.WmsCMFunction.name,
            "primary_account_head":input.WmsCMPrimaryAccountHead.name,
            "vendor_class":input.WmsCMVendorClass.name,
            "address":input.WmsCMAddress.address,
            "epfo_account_number":input.WmsCMPFAccountNumber.epfo_account_number,
            "vat_number":input.WmsCMVATNumber.VATNumber,
            "allow_direct_payment":input.WmsCMAllowDirectPayment.allow_direct_payment,
            "vendor_id":data?.vendor_id
    }]}
    console.log("onSubmit input ", {input,updatedRecord});
    // seteditData(updatedRecord)
    await mutate(updatedRecord)
  setShowToast(true)
    
    /* use customiseUpdateFormData hook to make some chnages to the Employee object */
    // Employees=Digit?.Customizations?.HRMS?.customiseUpdateFormData?Digit.Customizations.HRMS.customiseUpdateFormData(data,Employees):Employees;
    
    // history.replace("/upyog-ui/employee/hrms/response", { Employees, key: "UPDATE", action: "UPDATE" });
  };


  // if (isLoading) {
  //   return <Loader />;
  // }
  const config =mdmsData?.config?mdmsData.config: newConfig;

  return (
    <div>
      <FormComposer
        // heading={t("HR_COMMON_EDIT_EMPLOYEE_HEADER")}
        heading={"Contractor Master Update"}
        isDisabled={!canSubmit}
        // label={t("HR_COMMON_BUTTON_SUBMIT")}
        label={"Update"}
        config={config.map((config) => {
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
      {showToast&&isError && <Toast error={showToast.key} label={t('Something went wrong!')} onClose={closeToast} />}
      {showToast&&isSuccess && <Toast label={t('Record updated successfully')} onClose={closeToast} />}
    </div>
  );
};
export default EditForm;
