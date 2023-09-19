import React, { useState,useEffect } from "react";
import { useParams } from "react-router-dom";
import EditForm from "./EditForm";
import { Loader } from "@egovernments/digit-ui-react-components";
const EditCitizen = ({ parentUrl, heading }) => {
  // const [data1,setData]=useState();
  // const isupdate = Digit.SessionStorage.get("isupdate");
  // const param = useParams();
  const { id,tenantId } = useParams();
  // console.log("param ",id)
  const { isLoading, isError, error, data, ...rest } = Digit?.Hooks?.wms?.cm?.useWmsCMCount(id);
console.log("data ",data)

  // useEffect(() => {
  //   if (cmGetSingleData.data && cmGetSingleData.data.WMSContractorApplications){
  //       setData(...cmGetSingleData.data.WMSContractorApplications);
  //   } 
  // }, [cmGetSingleData?.data?.WMSContractorApplications]);

  // const { tenantId: tenantId } = useParams();
  // const { isLoading, isError, error, data, ...rest } = Digit.Hooks.hrms.useHRMSSearch({ codes: employeeId }, tenantId, isupdate);
  // console.log("data s ",data)
  // const tenantId = Digit.ULBService.getCurrentTenantId();    
  const data1 = {"Employees":[{"vendor_id": 7598,
            "vendor_type": "CLASS B",
            "vendor_sub_type": "Registered Partnership (Business)",
            "vendor_name": "EY",
            "vendor_status": "Inactive",
            "pfms_vendor_code": "PFMS_00984_09878",
            "payto": "EY Noida",
            "mobile_number": 1234567890,
            "email": "ey@in.ey.com",
            "uid_number": 323456789098,
            "gst_number": 34456788900,
            "pan_number": "04632345",
            "bank_branch_ifsc_code": "HDFC",
            "bank_account_number": 54363543534,
            "function": "Function B",
            "primary_account_head": "State Bank of India",
            "vendor_class": "CLASS B",
            "address": "EY, noida 126",
            "epfo_account_number": "34567890987",
            "vat_number": 433374923,
            "allow_direct_payment": "Yes",}]
}
            // const isLoading = false;
            // console.log("data s ",data)
  if (isLoading) {
    return <Loader />;
  }
  // return <EditForm data={data1?.Employees[0]} tenantId={tenantId} />;
  return <EditForm data={data?.WMSContractorApplications[0]} tenantId={tenantId} />;
};

export default EditCitizen;
