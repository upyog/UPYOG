import React, { useEffect, useMemo, useState } from "react";
import { useTranslation } from "react-i18next";
import { useLocation } from "react-router-dom";
import ESTSearchApplication from "../../components/ESTSearchApplication";

/** Dedicated key — do not reuse EST_ASSIGN_ASSETS (allotment draft). */
const EST_SEARCH_SESSION_KEY = "EST_SEARCH_APPLICATIONS";

const EMPTY_SEARCH_SESSION = Object.freeze({
  hasSearched: false,
  payload: null,
  formValues: null,
});

const readSearchSession = () => {
  try {
    const data = Digit.SessionStorage.get(EST_SEARCH_SESSION_KEY);
    if (data && typeof data === "object" && data.hasSearched) {
      return {
        hasSearched: true,
        payload: data.payload ?? {},
        formValues: data.formValues ?? null,
      };
    }
  } catch (_) {
    /* ignore */
  }
  return { ...EMPTY_SEARCH_SESSION };
};

const writeSearchSession = (next) => {
  try {
    Digit.SessionStorage.set(EST_SEARCH_SESSION_KEY, next);
  } catch (_) {
    /* ignore */
  }
};

const clearSearchSessionStorage = () => {
  try {
    Digit.SessionStorage.del(EST_SEARCH_SESSION_KEY);
  } catch (_) {
    /* ignore */
  }
};

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
  const location = useLocation();
  const tenantId = Digit.ULBService.getCurrentTenantId();

  // Read directly from Digit.SessionStorage (not useSessionStorage) so remount
  // after Back always picks up the last search. Re-read on location.key too.
  const [searchSession, setSearchSession] = useState(readSearchSession);

  useEffect(() => {
    setSearchSession(readSearchSession());
  }, [location.key]);

  const payload = searchSession.hasSearched ? searchSession.payload ?? {} : null;
  const formValues = searchSession.hasSearched ? searchSession.formValues : null;
  const hasSearched = Boolean(searchSession.hasSearched);

  const searchCriteria = useMemo(
    () => buildAssetSearchCriteria(payload || {}),
    [payload]
  );

  const onSubmit = (data) => {
    if (data == null) {
      clearSearchSessionStorage();
      setSearchSession({ ...EMPTY_SEARCH_SESSION });
      return;
    }
    const { offset, limit, sortBy, sortOrder, _formValues, ...rest } = data || {};
    const next = {
      hasSearched: true,
      payload: rest || {},
      formValues: _formValues || null,
    };
    writeSearchSession(next);
    setSearchSession(next);
  };

  const { isLoading, isSuccess, data, isFetching } = Digit.Hooks.estate.useESTAssetSearch({
    tenantId,
    filters: {
      AssetSearchCriteria: {
        tenantId,
        ...searchCriteria,
      },
    },
    config: {
      enabled: hasSearched,
    },
  });

  const searchResult = useMemo(
    () => applyClientFilters(data?.Assets || [], searchCriteria),
    [data, searchCriteria]
  );
  const count = searchResult.length;
  const showResults = hasSearched && isSuccess && !isLoading && !isFetching;

  return (
    <ESTSearchApplication
      t={t}
      isLoading={isLoading || (hasSearched && isFetching)}
      tenantId={tenantId}
      onSubmit={onSubmit}
      initialSearchFilters={payload || {}}
      initialFormValues={formValues || {}}
      data={
        showResults
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
