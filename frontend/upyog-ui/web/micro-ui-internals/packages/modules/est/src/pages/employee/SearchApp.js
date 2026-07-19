import React, { useMemo, useState } from "react";
import { useTranslation } from "react-i18next";
import ESTSearchApplication from "../../components/ESTSearchApplication";

/**
 * Build AssetSearchCriteria from DynamicForm search payload.
 * Accepts both UI names (assetType/locality) and API names (assetParentCategory/localityCode).
 */
const buildAssetSearchCriteria = (payload = {}) => {
  const estateNo = String(payload.estateNo || "").trim();
  const assetParentCategory = String(
    payload.assetParentCategory || payload.assetType || ""
  ).trim();
  const localityCode = String(
    payload.localityCode ||
      (typeof payload.locality === "object" ? payload.locality?.code : payload.locality) ||
      ""
  ).trim();
  const assetStatus = String(payload.assetStatus || "").trim();

  const criteria = {};
  if (estateNo) criteria.estateNo = estateNo;
  if (assetParentCategory) {
    // Backend may key off either name depending on service version.
    criteria.assetParentCategory = assetParentCategory;
    criteria.assetType = assetParentCategory;
  }
  if (localityCode) criteria.localityCode = localityCode;
  if (assetStatus) criteria.assetStatus = assetStatus;
  return criteria;
};

/** Client-side safety net when the search API ignores some criteria. */
const applyClientFilters = (assets = [], criteria = {}) => {
  if (!Array.isArray(assets) || !assets.length) return [];
  let list = assets;

  if (criteria.estateNo) {
    const q = criteria.estateNo.toLowerCase();
    list = list.filter((a) =>
      String(a.estateNo || a.assetNo || "")
        .toLowerCase()
        .includes(q)
    );
  }

  if (criteria.assetParentCategory || criteria.assetType) {
    const type = String(criteria.assetParentCategory || criteria.assetType).toUpperCase();
    list = list.filter(
      (a) =>
        String(a.assetParentCategory || a.assetType || "").toUpperCase() === type
    );
  }

  if (criteria.localityCode) {
    const loc = String(criteria.localityCode).toUpperCase();
    list = list.filter((a) => {
      const code = String(
        a.localityCode ||
          (typeof a.locality === "object" ? a.locality?.code : a.locality) ||
          a.serviceType ||
          ""
      ).toUpperCase();
      return code === loc || code.includes(loc);
    });
  }

  if (criteria.assetStatus) {
    const status = String(criteria.assetStatus).toUpperCase();
    list = list.filter(
      (a) => String(a.assetStatus || a.status || "").toUpperCase() === status
    );
  }

  return list;
};

const SearchApp = () => {
  const { t } = useTranslation();
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const [payload, setPayload] = useState(null);
  const [hasSearched, setHasSearched] = useState(false);

  const searchCriteria = useMemo(
    () => buildAssetSearchCriteria(payload || {}),
    [payload]
  );

  function onSubmit(data) {
    setHasSearched(true);
    const { offset, limit, sortBy, sortOrder, ...rest } = data || {};
    setPayload(rest || {});
  }

  const { isLoading, isSuccess, data } = Digit.Hooks.estate.useESTAssetSearch({
    tenantId,
    filters: {
      AssetSearchCriteria: {
        tenantId,
        ...searchCriteria,
      },
    },
    config: {
      enabled: hasSearched && payload !== null,
    },
  });

  const searchResult = useMemo(
    () => applyClientFilters(data?.Assets || [], searchCriteria),
    [data, searchCriteria]
  );
  const count = searchResult.length;

  return (
    <ESTSearchApplication
      t={t}
      isLoading={isLoading}
      tenantId={tenantId}
      onSubmit={onSubmit}
      data={
        hasSearched && isSuccess && !isLoading
          ? searchResult.length > 0
            ? searchResult
            : { display: "ES_COMMON_NO_DATA" }
          : ""
      }
      count={count}
    />
  );
};

export default SearchApp;
