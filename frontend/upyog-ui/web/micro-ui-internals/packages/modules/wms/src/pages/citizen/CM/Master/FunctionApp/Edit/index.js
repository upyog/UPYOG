import React from "react";
import { useParams } from "react-router-dom";
import EditForm from "./EditForm";
import { Loader } from "@egovernments/digit-ui-react-components";

const FunctionAppEdit =()=>{
    const { id,tenantId } = useParams();
    console.log("param ",id)
    const { isLoading, isError, error, data, ...rest } = Digit?.Hooks?.wms?.cm?.useWMSMaster(id,"WMS_FUNCTION_APP_SINGLE_RECORD");
  console.log("data edit index ",data, isLoading)
const data1=[
  {
  "function_name":"Test32",
  "function_code":"0987",
  "function_level":"4",
  "name":"Active"}
]
  if (isLoading) {
    return <Loader />;
  }
  return <EditForm data={data?.WMSFunctionApplications[0]} tenantId={tenantId} />;
  // return <EditForm data={data1[0]} tenantId={tenantId} />;

}
export default FunctionAppEdit