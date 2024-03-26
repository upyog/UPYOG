import React, { useState,useEffect } from "react";
import { useParams } from "react-router-dom";
import EditForm from "./EditForm";
import { Loader } from "@egovernments/digit-ui-react-components";

const AppEdit = ({ parentUrl, heading }) => {
    const { id,tenantId } = useParams();
    console.log("param ",id)
    const { isLoading, isError, error, data, ...rest } = Digit?.Hooks?.wms?.cm?.useWMSMaster(id,"WMS_SUB_TYPE_SINGLE_RECORD");
  // console.log("data ",data)
  // console.log("data ",data?.WMSContractorSubTypeApplications[0])
  // const dataDummy=
  // {bank_name: 'Noida',
  //   status: 'Active',
  //   }

    
  if (isLoading) {
    return <Loader />;
  }
  return <EditForm data={data?.WMSContractorSubTypeApplications[0]} tenantId={tenantId} />;
  // return <EditForm data={dataDummy} tenantId={tenantId} />;

}
export default AppEdit;