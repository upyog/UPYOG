import { FormComposer, Loader } from "@upyog/digit-ui-react-components";
import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useHistory } from "react-router-dom";
import { useParams, useLocation } from "react-router-dom";
import { editProcurementConfig } from "../../../../config/Create/editProcurementConfig";

const EditProcurementRequest= () => {
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const stateTenantId = Digit.ULBService.getStateId();
  const { t } = useTranslation();
  const [canSubmit, setCanSubmit] = useState(false);
  const defaultValues = {};
  const history = useHistory();
  const { id: applicationNo } = useParams();
  
  const location = useLocation();

  const { isLoading, isSuccess, isError, data: { ProcurementRequests: searchResult, Count: count } = {} } = Digit.Hooks.asset.useProcurementList({
    tenantId,
    filters: { requestId: applicationNo },
  });
  
  const convertDateToEpoch = (dateString) => {
    // Parse the date string into a Date object
    const date = new Date(dateString);

    // Check if the date is valid
    if (!isNaN(date)) {
      // Return the epoch time in seconds (divide by 1000 to convert from milliseconds to seconds)
      return Math.floor(date.getTime() / 1000);
    } else {
      return null; // Return null if the input date is invalid
    }
  };

  const convertToEpoch = (dateString) => {
    const [year, month, day] = dateString.split("-").map(Number);
    return new Date(year, month - 1, day).getTime();
  };

  const onFormValueChange = (setValue, formData, formState) => {
    setCanSubmit(!Object.keys(formState.errors).length);
  };

  const onSubmit = (data) => {
    const procurementData = data?.editProcurementReq?.editProcurementReq;
    
    const formData = {
      requestId: procurementData?.requestId,
      item: procurementData?.item?.code,
      itemType: procurementData?.itemType?.code,
      quantity: procurementData?.quantity,
      identificationNo: procurementData?.identificationNo,
      requestIdNo: procurementData?.requestIdNo,
      tenantId: tenantId,
      assetApplicationNumber: procurementData?.assetApplicationNumber,
      description:procurementData?.description,
      status: procurementData?.status?.code,
    };
    history.replace("/upyog-ui/employee/asset/assetservice/edit-procurement-response", { ProcurementDetail: formData });
  };

  if (isLoading) {
    return <Loader />;
  }

  const configs = editProcurementConfig;

  return (
    <FormComposer
      heading={t("EDIT_PROCUREMENT_REQUEST")}
      // isDisabled={!canSubmit}
      label={t("ES_COMMON_APPLICATION_SUBMIT")}
      config={configs.map((config) => {
        return {
          ...config,
          body: config.body.filter((a) => !a.hideInEmployee),
        };
      })}
      defaultValues={{ procurementReq: searchResult?.[0] }}
      fieldStyle={{ marginRight: 0 }}
      cardStyle={{ Width: 60 }}
      onSubmit={onSubmit}
      onFormValueChange={onFormValueChange}
    />
  );
};

export default EditProcurementRequest
