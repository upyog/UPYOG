/**
 * Citizen My Applications — card list with inline search (matches niuatt layout).
 * Filters / labels come from MDMS Estate.CitizenMyApplicationsConfig.
 * List order comes from the backend (no client-side re-sort).
 */
import React, { useCallback, useEffect, useMemo, useState } from "react";
import {
  Header,
  Loader,
  TextInput,
  Dropdown,
  SubmitBar,
  CardLabel,
  Card,
  sortByOrder,
} from "@nudmcdgnpm/digit-ui-react-components";
import { Link, useLocation } from "react-router-dom";
import { useTranslation } from "react-i18next";
import EstateApplication from "./est-application";
import { resolveCitizenMyApplicationsConfig } from "../../../utils/estMdmsUtils";
import styles from "../../../styles/ESTMyApplications.module.scss";

export const ESTMyApplications = () => {
  const { t } = useTranslation();
  const location = useLocation();
  const tenantId =
    Digit.ULBService.getCitizenCurrentTenant(true) ||
    Digit.ULBService.getCurrentTenantId();
  const stateId = Digit.ULBService.getStateId();
  const user = Digit.UserService.getUser()?.info;
  const mobileNumber = user?.mobileNumber;

  const { data: mdmsMyApps } = Digit.Hooks.useEnabledMDMS(
    stateId,
    "Estate",
    [{ name: "CitizenMyApplicationsConfig" }],
    {
      select: (mdms) => mdms?.Estate?.CitizenMyApplicationsConfig || null,
    }
  );

  const config = useMemo(
    () => resolveCitizenMyApplicationsConfig(mdmsMyApps),
    [mdmsMyApps]
  );

  const filters = useMemo(() => sortByOrder(config.filters), [config.filters]);
  const pageSize = config.paginationDefaults?.limit || 50;
  const estateNoFilter = filters.find((f) => f.name === "estateNo");
  const statusFilter = filters.find((f) => f.name === "assetStatus");

  const [searchTerm, setSearchTerm] = useState("");
  const [status, setStatus] = useState(null);
  const [searchFilters, setSearchFilters] = useState({});
  const [hasSearched, setHasSearched] = useState(false);

  const pathOffset = useMemo(() => {
    const segment = location.pathname.split("/").pop();
    const parsed = parseInt(segment, 10);
    return Number.isNaN(parsed) ? 0 : parsed;
  }, [location.pathname]);

  useEffect(() => {
    if (config.autoSearch) {
      setHasSearched(true);
      setSearchFilters({});
    }
  }, [config.autoSearch]);

  const { isLoading, isSuccess, data } = Digit.Hooks.estate.useESTAssetSearch({
    tenantId,
    filters: {
      AssetSearchCriteria: {
        tenantId,
        mobileNumber,
        ...(searchFilters.estateNo && { estateNo: searchFilters.estateNo }),
        ...(searchFilters.assetStatus && { assetStatus: searchFilters.assetStatus }),
      },
    },
    config: {
      enabled: Boolean(hasSearched && tenantId && mobileNumber),
    },
  });

  // One allotment search for the tenant — cards read propertyType / billingCycle from this map.
  const { data: allotmentResponse } = Digit.Hooks.estate.useESTApplicationSearch({
    filters: { tenantId },
    config: {
      enabled: Boolean(hasSearched && tenantId && mobileNumber),
    },
  });

  const allotmentByAssetNo = useMemo(() => {
    const map = {};
    (allotmentResponse?.Allotments || allotmentResponse?.allotments || []).forEach((item) => {
      const key = item?.assetNo || item?.estateNo;
      if (key && !map[key]) map[key] = item;
    });
    return map;
  }, [allotmentResponse]);

  const applications = useMemo(() => data?.Assets || [], [data]);

  const visibleApplications = useMemo(
    () => applications.slice(pathOffset, pathOffset + pageSize),
    [applications, pathOffset, pageSize]
  );

  const handleSearch = useCallback(() => {
    const trimmedSearchTerm = searchTerm.trim();
    setHasSearched(true);
    setSearchFilters({
      estateNo: trimmedSearchTerm || undefined,
      assetStatus: status?.code || undefined,
    });
  }, [searchTerm, status]);

  const handleClear = useCallback(() => {
    setSearchTerm("");
    setStatus(null);
    setHasSearched(true);
    setSearchFilters({});
  }, []);

  const statusMasterName =
    statusFilter?.dataSource?.masterName || "AssetStatus";
  const statusModuleName =
    statusFilter?.dataSource?.moduleName || "Estate";

  const { data: assetStatusMdms = [] } = Digit.Hooks.useEnabledMDMS(
    stateId,
    statusModuleName,
    [{ name: statusMasterName }],
    {
      select: (mdms) => mdms?.[statusModuleName]?.[statusMasterName] || [],
    }
  );

  const statusOptions = useMemo(
    () =>
      assetStatusMdms.map((item) => ({
        code: item.code,
        name: t(item.name) || item.name || item.code,
      })),
    [assetStatusMdms, t]
  );

  if (isLoading) {
    return <Loader />;
  }

  const totalCount = applications.length;
  const nextOffset = pathOffset + pageSize;
  const hasMore = totalCount > nextOffset;

  return (
    <>
      <Header>{`${t(config.header)} (${totalCount})`}</Header>

      <Card>
        <div className={styles["est-myapps__container"]}>
          <div className={styles["est-myapps__search-row"]}>
            {estateNoFilter ? (
              <div className={styles["est-myapps__field-col"]}>
                <div className={styles["est-myapps__field-inner"]}>
                  <CardLabel>{t(estateNoFilter.key)}</CardLabel>
                  <TextInput
                    placeholder={t(estateNoFilter.placeholder || estateNoFilter.key)}
                    value={searchTerm}
                    onChange={(e) => setSearchTerm(e.target.value)}
                    className={styles["est-myapps__text-input"]}
                  />
                </div>
              </div>
            ) : null}
            {statusFilter ? (
              <div className={styles["est-myapps__field-col"]}>
                <div className={styles["est-myapps__field-inner"]}>
                  <CardLabel>{t(statusFilter.key)}</CardLabel>
                  <Dropdown
                    className={`form-field ${styles["est-myapps__dropdown"]}`}
                    selected={status}
                    select={setStatus}
                    option={statusOptions}
                    placeholder={t(statusFilter.placeholder || statusFilter.key)}
                    optionKey="name"
                    t={t}
                  />
                </div>
              </div>
            ) : null}
            <div>
              <div className={styles["est-myapps__search-btn-wrap"]}>
                <SubmitBar
                  label={t(config.actionButton?.search || "ES_COMMON_SEARCH")}
                  onSubmit={handleSearch}
                />
                <p
                  className={`link ${styles["est-myapps__clear-link"]}`}
                  onClick={handleClear}
                >
                  {t(config.actionButton?.clear || "ES_COMMON_CLEAR_ALL")}
                </p>
              </div>
            </div>
          </div>
        </div>
      </Card>

      <div>
        {isSuccess &&
          visibleApplications.map((application, index) => (
            <div key={application.assetId || application.estateNo || index}>
              <EstateApplication
                application={application}
                allotment={
                  allotmentByAssetNo[application?.estateNo] ||
                  allotmentByAssetNo[application?.assetNo] ||
                  null
                }
                tenantId={tenantId}
                buttonLabel={t("EST_SUMMARY")}
              />
            </div>
          ))}

        {isSuccess && totalCount === 0 && (
          <p className={styles["est-myapps__msg"]}>
            {t(config.emptyState?.message || "EST_NO_APPLICATION_FOUND_MSG")}
          </p>
        )}

        {totalCount > 0 && hasMore && (
          <div>
            <p className={styles["est-myapps__msg"]}>
              <span className="link">
                <Link to={`/upyog-ui/citizen/est/my-applications/${nextOffset}`}>
                  {t("EST_LOAD_MORE_MSG")}
                </Link>
              </span>
            </p>
          </div>
        )}
      </div>
    </>
  );
};

export default ESTMyApplications;
