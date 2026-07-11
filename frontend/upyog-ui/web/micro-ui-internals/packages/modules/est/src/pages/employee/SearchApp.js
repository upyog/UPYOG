import React, { useState } from "react";
import { Toast } from "@nudmcdgnpm/digit-ui-react-components";
import { useTranslation } from "react-i18next";
import ESTSearchApplication from "../../components/ESTSearchApplication";

// EST Search Application Component
// This component provides a search interface for estate assets, allowing users to filter results 
// based on various criteria such as estate number, asset category, status, and locality. It displays the search 
// results and handles loading and error states.

const SearchApp = ({ path }) => {
  const { t } = useTranslation();
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const [payload, setPayload] = useState({});
  const [showToast, setShowToast] = useState(null);
  const [hasSearched, setHasSearched] = useState(false);

  function onSubmit(data) {
    setHasSearched(true);

    const { offset, limit, sortBy, sortOrder, ...searchCriteria } = data || {};
    const hasCriteria = Object.values(searchCriteria).some(
      (val) => val !== undefined && val !== null && val !== ""
    );

    if (hasCriteria) {
      setPayload(searchCriteria);
    } else {
      setShowToast({ error: true, label: "Please enter search criteria" });
    }
  }

  // 🔹 Call estate search with explicit mapping of filters
  const { isLoading, isSuccess, data, error } = Digit.Hooks.estate.useESTAssetSearch(
    {
      tenantId,
      filters: {
         AssetSearchCriteria: {
         estateNo: payload.estateNo,
         assetParentCategory: payload.assetParentCategory,
         assetStatus: payload.assetStatus,
         localityCode: payload.localityCode, 
         },
      },
    },
    {
      enabled: !!(payload && Object.keys(payload).length > 0),
    }
  );

  const searchResult = data?.Assets || [];
  const count = searchResult.length;

  return (
    <React.Fragment>
      
      <ESTSearchApplication
        t={t}
        isLoading={isLoading}
        tenantId={tenantId}
        setShowToast={setShowToast}
        onSubmit={onSubmit}
        data={
          hasSearched && isSuccess && !isLoading
            ? searchResult?.length > 0
              ? searchResult
              : { display: "ES_COMMON_NO_DATA" }
            : ""
        }
        count={count}
      />

      {showToast && (
        <Toast
          error={showToast.error}
          warning={showToast.warning}
          label={t(showToast.label)}
          isDeleteBtn={true}
          onClose={() => setShowToast(null)}
        />
      )}

      {error && (
        <Toast
          error={true}
          label={`Search failed: ${error.message || "Unknown error"}`}
          isDeleteBtn={true}
          onClose={() => {}}
        />
      )}
    </React.Fragment>
  );
};

export default SearchApp;