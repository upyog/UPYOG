import React, { useState,useEffect } from "react";
import { useParams } from "react-router-dom";
import EditForm from "./EditForm";
import { Loader } from "@egovernments/digit-ui-react-components";
const EditCitizen = ({ parentUrl, heading }) => {
  const { id,tenantId } = useParams();
  const { isLoading, isError, error, data, ...rest } = Digit?.Hooks?.wms?.cm?.useWmsCMCount(id);
  if (isLoading) {
    return <Loader />;
  }
  return <EditForm data={data?.WMSContractorApplications[0]} tenantId={tenantId} />;
};
export default EditCitizen;
