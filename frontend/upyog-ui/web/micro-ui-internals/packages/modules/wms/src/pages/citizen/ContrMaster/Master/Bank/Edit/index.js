import React, { useState,useEffect } from "react";
import { useParams } from "react-router-dom";
import EditForm from "./EditForm";
import { Loader } from "@egovernments/digit-ui-react-components";

const AppEditBank = ({ parentUrl, heading }) => {
    const { id,tenantId } = useParams();
    console.log("param ",id)
    const { isLoading, isError, error, data, ...rest } = Digit?.Hooks?.wms?.cm?.useWmsCMCount(id);
  console.log("data ",data)
  const dataDummy={bank_branch: 'Noida',
    bank_ifsc_code: 'SBIN00012',
    bank_name: 'State Bank Of India',
    name:'Active'}
//   if (isLoading) {
//     return <Loader />;
//   }
//   return <EditForm data={data?.WMSContractorApplications[0]} tenantId={tenantId} />;
  return <EditForm data={dataDummy} tenantId={tenantId} />;

}
export default AppEditBank;