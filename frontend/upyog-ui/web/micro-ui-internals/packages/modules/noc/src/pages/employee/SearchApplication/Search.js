/**
 * Controller component for NOC application search.
 * Configures search form methods, table structures, and switches between desktop and mobile search views.
 */
import React, { Fragment, useCallback, useMemo, useEffect, useState, useReducer } from "react";
import { useForm, FormProvider } from "react-hook-form";
import SearchFormFieldsComponent from "./SearchFormFieldsComponent";
import useSearchApplicationTableConfig from "./useTableConfig";
import SearchApplicationMobileView from "./SearchAtom/mobile";
import SearchApplicationDesktopView from "./SearchAtom/desktop";

const SearchApplication = ({ tenantId, t, onSubmit, data, error, isLoading, Count }) => {
  const [showToast, setShowToast] = useState(null);

  const methods = useForm({
    defaultValues: {
      // applicationNo: "",
      // mobileNumber: "",
      // fromDate: "",
      // toDate: "",
      // status: "",
      // offset: 0,
      // limit: 10,
      // sortBy: "commencementDate",
      // sortOrder: "DESC",
      // applicationType: {
      //   code: "BUILDING_PLAN_SCRUTINY",
      //   i18nKey: "WF_BPA_BUILDING_PLAN_SCRUTINY",
      // },
      // serviceType: {
      //   applicationType: ["BUILDING_PLAN_SCRUTINY", "BUILDING_OC_PLAN_SCRUTINY"],
      //   code: "NEW_CONSTRUCTION",
      //   i18nKey: "BPA_SERVICETYPE_NEW_CONSTRUCTION",
      // },
    },
  });

  useEffect(() => {
    methods.register("offset");
    methods.register("limit");
    methods.register("sortBy");
    methods.register("sortOrder");
  }, [methods.register]);

  const columns = useSearchApplicationTableConfig();

  const isMobile = window.Digit.Utils.browser.isMobile();

  // const searchFormFieldsComponentProps = { t, previousPage };

  const getRedirectionLink = () => {
    let redirectBS = "noc/inbox/application-overview";
    return redirectBS;
  };
  const propsMobileInboxCards = useMemo(
    () =>
      data?.map((data) => ({
        [t("NOC_APP_NO_LABEL")]: data.applicationNo,
        [t("NOC_COMMON_TABLE_COL_APP_DATE_LABEL")]: Digit.DateUtils.ConvertEpochToDate(data.auditDetails?.createdTime) || "-",
        [t("NOC_APPLICANTS_NAME_LABEL")]: data?.additionalDetails?.applicantName || "-",
        [t("NOC_SOURCE_MODULE_LABEL")]: data.source ? t(`MODULE_${data.source}`) : "-",
        [t("NOC_SOURCE_MODULE_NUMBER")]: data?.sourceRefId || "-",
        [t("WF_INBOX_HEADER_CURRENT_OWNER")]: data?.additionalDetails?.currentOwner || "-",
        [t("NOC_STATUS_LABEL")]: data.applicationStatus ? t(`${data.applicationStatus}`) : "-",
      })),
    [data]
  );

  if (isMobile) return <FormProvider {...methods}><SearchApplicationMobileView {...{SearchFormFieldsComponent, propsMobileInboxCards, isLoading, data, getRedirectionLink, onSubmit}} /></FormProvider>

  return <FormProvider {...methods}><SearchApplicationDesktopView {...{columns, SearchFormFieldsComponent, onSubmit, data, error, isLoading, Count}} /></FormProvider>
};

export default SearchApplication;
