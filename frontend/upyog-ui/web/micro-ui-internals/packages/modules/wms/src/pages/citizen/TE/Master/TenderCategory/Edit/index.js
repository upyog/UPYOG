import React from "react";
import { useParams } from "react-router-dom";
import EditForm from "./EditForm";
import { Loader } from "@egovernments/digit-ui-react-components";

const AccountHeadEdit =()=>{
    const { id,tenantId } = useParams();
    console.log("param ",id)
    const { isLoading, isError, error, data, ...rest } = Digit?.Hooks?.wms?.te?.useWMSTEMaster(id,"WMS_TENDER_CATEGORY_SINGLE_RECORD");
  console.log("data edit index ",data, isLoading)
// const data1=[
//   {"vendor_id":1,"vendor_class_name":"Tesh","vendor_class_status":"Active"}
// ]
  if (isLoading) {
    return <Loader />;
  }
  return <EditForm data={data?.WMSTenderCategoryApplications[0]} tenantId={tenantId} />;
  // return <EditForm data={data1[0]} tenantId={tenantId} />;

}
export default AccountHeadEdit