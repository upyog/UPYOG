import React from "react";
import { useParams } from "react-router-dom";
import EditForm from "./EditForm";
import { Loader } from "@egovernments/digit-ui-react-components";

const VendorClassEdit =()=>{
    const { id,tenantId } = useParams();
    console.log("param ",id)
    const { isLoading, isError, error, data, ...rest } = Digit?.Hooks?.wms?.cm?.useWMSMaster(id,"WMS_V_CLASS_SINGLE_RECORD");
  console.log("data edit index ",data, isLoading)
  if (isLoading) {
    return <Loader />;
  }
  return <EditForm data={data?.WMSVendorClassApplications[0]} tenantId={tenantId} />;
}
export default VendorClassEdit