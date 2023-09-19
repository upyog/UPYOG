import React from "react";
import { useParams } from "react-router-dom";
import EditForm from "./EditForm";
import { Loader } from "@egovernments/digit-ui-react-components";

const VendorTypeEdit =()=>{
    const { id,tenantId } = useParams();
    console.log("param ",id)
    const { isLoading, isError, error, data, ...rest } = Digit?.Hooks?.wms?.cm?.useWMSMaster(id,"WMS_V_TYPE_SINGLE_RECORD");
  console.log("data ",data)

//   const dataDummy={
//     name: 'Noida',
//     }
  if (isLoading) {
    return <Loader />;
  }
  return <EditForm data={data} tenantId={tenantId} />;
//   return <EditForm data={dataDummy} tenantId={tenantId} />;

}
export default VendorTypeEdit