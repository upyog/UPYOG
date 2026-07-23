/**
 * Citizen My Applications — card list with inline search (matches niuatt layout).
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
} from "@nudmcdgnpm/digit-ui-react-components";
import { Link, useLocation } from "react-router-dom";
import { useTranslation } from "react-i18next";
import EstateApplication from "./est-application";
import styles from "../../../styles/ESTMyApplications.module.scss";

const PAGE_SIZE = 50;

export const ESTMyApplications = () => {
  const { t } = useTranslation();
  const location = useLocation();
  const tenantId =
    Digit.ULBService.getCitizenCurrentTenant(true) ||
    Digit.ULBService.getCurrentTenantId();
  const user = Digit.UserService.getUser()?.info;
  const mobileNumber = user?.mobileNumber;

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
    setHasSearched(true);
    setSearchFilters({});
  }, []);

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
    () => applications.slice(pathOffset, pathOffset + PAGE_SIZE),
    [applications, pathOffset]
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

  const { data: assetStatusMdms = [] } = Digit.Hooks.useEnabledMDMS(
    Digit.ULBService.getStateId(),
    "Estate",
    [{ name: "AssetStatus" }],
    {
      select: (mdms) => mdms?.Estate?.AssetStatus || [],
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
  const nextOffset = pathOffset + PAGE_SIZE;
  const hasMore = totalCount > nextOffset;

  return (
    <>
      <Header>{`${t("EST_MY_APPLICATIONS")} (${totalCount})`}</Header>

      <Card>
        <div className={styles["est-myapps__container"]}>
          <div className={styles["est-myapps__search-row"]}>
            <div className={styles["est-myapps__field-col"]}>
              <div className={styles["est-myapps__field-inner"]}>
                <CardLabel>{t("EST_ASSET_NUMBER")}</CardLabel>
                <TextInput
                  placeholder={t("EST_ENTER_ASSET_NUMBER")}
                  value={searchTerm}
                  onChange={(e) => setSearchTerm(e.target.value)}
                  className={styles["est-myapps__text-input"]}
                />
              </div>
            </div>
            <div className={styles["est-myapps__field-col"]}>
              <div className={styles["est-myapps__field-inner"]}>
                <CardLabel>{t("PT_COMMON_TABLE_COL_STATUS_LABEL")}</CardLabel>
                <Dropdown
                  className={`form-field ${styles["est-myapps__dropdown"]}`}
                  selected={status}
                  select={setStatus}
                  option={statusOptions}
                  placeholder={t("EST_SELECT_STATUS")}
                  optionKey="name"
                  t={t}
                />
              </div>
            </div>
            <div>
              <div className={styles["est-myapps__search-btn-wrap"]}>
                <SubmitBar label={t("ES_COMMON_SEARCH")} onSubmit={handleSearch} />
                <p
                  className={`link ${styles["est-myapps__clear-link"]}`}
                  onClick={handleClear}
                >
                  {t("ES_COMMON_CLEAR_ALL")}
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
          <p className={styles["est-myapps__msg"]}>{t("EST_NO_APPLICATION_FOUND_MSG")}</p>
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
