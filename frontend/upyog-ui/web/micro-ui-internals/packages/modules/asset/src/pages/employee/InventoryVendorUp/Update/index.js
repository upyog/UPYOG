import { FormComposer, Loader } from "@upyog/digit-ui-react-components";
import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useHistory } from "react-router-dom";
import { useParams, useLocation } from "react-router-dom";
import { editAssetVendorConfig } from "../../../../config/Create/editAssetVendorConfig";

const EditAssetVendorUp = () => {
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const stateTenantId = Digit.ULBService.getStateId();
  const { t } = useTranslation();
  const [canSubmit, setCanSubmit] = useState(false);
  const defaultValues = {};
  const history = useHistory();
  const { id: applicationNo } = useParams();
  const location = useLocation();

  // Use same hook as EditAsset module
  const { isLoading, isSuccess, isError, data: { Vendors: searchResult, Count: count } = {} } = Digit.Hooks.asset.useAssetVendorList({
    tenantId,
    filters: { vendorName: applicationNo },
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
    console.log("DATAA:- ", data?.editAssetVendor?.editAssetVendor?.vendorName);
    const formData = {
      vendorId: searchResult?.[0].vendorNumber,
      tenantId: tenantId,
      vendorName: data?.editAssetVendor?.editAssetVendor?.vendorName,
      contactPerson: data?.editAssetVendor?.editAssetVendor?.contactPerson,
      vendorNumber: data?.editAssetVendor?.editAssetVendor?.vendorNumber,
      contactNumber: data?.editAssetVendor?.editAssetVendor?.contactNumber,
      contactEmail: data?.editAssetVendor?.editAssetVendor?.contactEmail,
      gstin: data?.editAssetVendor?.editAssetVendor?.gstin,
      pan: data?.editAssetVendor?.editAssetVendor?.pan,
      vendorAddress: data?.editAssetVendor?.editAssetVendor?.vendorAddress,
      identificationNo: data?.editAssetVendor?.editAssetVendor?.identificationNo,
      status: "ACTIVE",
    };
    // history.replace("/upyog-ui/employee/asset/assetservice/edit-response", { Vendor: formData });
    history.replace("/upyog-ui/employee/asset/assetservice/edit-vendor-response", { Vendor: formData });
  };

  if (isLoading) {
    return <Loader />;
  }

  const configs = editAssetVendorConfig;

  return (
    <FormComposer
      heading={t("EDIT_INVENTORY_VENDOR")}
      // isDisabled={!canSubmit}
      label={t("ES_COMMON_APPLICATION_SUBMIT")}
      config={configs.map((config) => {
        return {
          ...config,
          body: config.body.filter((a) => !a.hideInEmployee),
        };
      })}
      defaultValues={{ editAssetVendor: searchResult?.[0] }}
      fieldStyle={{ marginRight: 0 }}
      cardStyle={{ Width: 60 }}
      onSubmit={onSubmit}
      onFormValueChange={onFormValueChange}
    />
  );
};

export default EditAssetVendorUp;
