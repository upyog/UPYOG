import React, { useState } from "react";
import { useTranslation } from "react-i18next";
import ESTSearchApplication from "../../components/ESTSearchApplication";

// EST Search Application Component
// This component provides a search interface for estate assets, allowing users to filter results
// based on various criteria such as estate number, asset category, status, and locality. It displays the search
// results and handles loading and error states.

const SearchApp = ({ path }) => {
  const { t } = useTranslation();
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const [payload, setPayload] = useState(null);
  const [hasSearched, setHasSearched] = useState(false);

  function onSubmit(data) {
    setHasSearched(true);
    const { offset, limit, sortBy, sortOrder, ...searchCriteria } = data || {};
    // Allow search with or without filters — results render in the table; no toast.
    setPayload(searchCriteria || {});
  }

  const { isLoading, isSuccess, data } = Digit.Hooks.estate.useESTAssetSearch(
    {
      tenantId,
      filters: {
        AssetSearchCriteria: {
          estateNo: payload?.estateNo,
          assetParentCategory: payload?.assetParentCategory,
          assetStatus: payload?.assetStatus,
          localityCode: payload?.localityCode,
        },
      },
    },
    {
      enabled: hasSearched && payload !== null,
    }
  );

  const searchResult = data?.Assets || [];
  const count = searchResult.length;

  return (
    <ESTSearchApplication
      t={t}
      isLoading={isLoading}
      tenantId={tenantId}
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
  );
};

export default SearchApp;