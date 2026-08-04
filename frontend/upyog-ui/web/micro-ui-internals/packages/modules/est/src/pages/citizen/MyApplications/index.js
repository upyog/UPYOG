/**
 * Citizen My Applications — card list with inline search.
 * Data source: allotment _search only.
 * Page load / empty search → { tenantId } so all allotments from API show.
 * Status filter: Paid | Pending for payment (client-side on Allotments[].status).
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
import {
  getAllotmentPaymentStatus,
  normalizeCitizenPaymentStatus,
} from "../../../utils/estDisplayUtils";
import styles from "../../../styles/ESTMyApplications.module.scss";

export const ESTMyApplications = () => {
  const { t } = useTranslation();
  const location = useLocation();
  const tenantId =
    Digit.ULBService.getCitizenCurrentTenant(true) ||
    Digit.ULBService.getCurrentTenantId();
  const stateId = Digit.ULBService.getStateId();

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
  const allotmentNoFilter = filters.find(
    (f) => f.name === "allotmentNo" || f.name === "estateNo"
  );
  const statusFilter = filters.find(
    (f) =>
      f.name === "paymentStatus" ||
      f.name === "assetAllotmentStatus" ||
      f.name === "assetStatus" ||
      f.name === "status"
  );

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

  // Allotment _search with tenantId only — allotmentNo / status filtered client-side.
  const allotmentSearchFilters = useMemo(
    () => ({ tenantId }),
    [tenantId]
  );

  const { isLoading, isSuccess, data, isFetching } =
    Digit.Hooks.estate.useESTApplicationSearch({
      filters: allotmentSearchFilters,
      config: {
        enabled: Boolean(hasSearched && tenantId),
        structuralSharing: false,
      },
    });

  const applications = useMemo(() => {
    const list = data?.Allotments || data?.allotments || [];
    const paymentFilter = String(searchFilters.paymentStatus || "").toUpperCase();
    const allotmentNoFilterValue = String(searchFilters.allotmentNo || "")
      .trim()
      .toUpperCase();
    let rows = Array.isArray(list) ? [...list] : [];

    if (allotmentNoFilterValue) {
      rows = rows.filter((item) => {
        const no = String(
          item?.allotmentNo ?? item?.additionalDetails?.allotmentNo ?? ""
        )
          .trim()
          .toUpperCase();
        return no.includes(allotmentNoFilterValue);
      });
    }

    // Empty status → show every matching allotment.
    if (!paymentFilter) return rows;

    return rows.filter((item) => {
      const rowPayment = normalizeCitizenPaymentStatus(
        getAllotmentPaymentStatus(item)
      );
      return rowPayment === paymentFilter;
    });
  }, [data, searchFilters.paymentStatus, searchFilters.allotmentNo]);

  const visibleApplications = useMemo(() => {
    if (!pathOffset) return applications;
    return applications.slice(pathOffset, pathOffset + pageSize);
  }, [applications, pathOffset, pageSize]);

  const handleSearch = useCallback(() => {
    const trimmedSearchTerm = searchTerm.trim();
    const paymentStatus = status?.code || undefined;
    setHasSearched(true);

    if (!trimmedSearchTerm && !paymentStatus) {
      setSearchFilters({});
      return;
    }

    setSearchFilters({
      allotmentNo: trimmedSearchTerm || undefined,
      paymentStatus,
    });
  }, [searchTerm, status]);

  const handleClear = useCallback(() => {
    setSearchTerm("");
    setStatus(null);
    setHasSearched(true);
    setSearchFilters({});
  }, []);

  const statusMasterName =
    statusFilter?.dataSource?.masterName || "PaymentStatus";
  const statusModuleName =
    statusFilter?.dataSource?.moduleName || "Estate";

  const { data: paymentStatusMdms = [] } = Digit.Hooks.useEnabledMDMS(
    stateId,
    statusModuleName,
    [{ name: statusMasterName }],
    {
      select: (mdms) => mdms?.[statusModuleName]?.[statusMasterName] || [],
    }
  );

  const statusOptions = useMemo(() => {
    const wanted = ["PAID", "PENDING_FOR_PAYMENT"];
    const byCode = {};
    paymentStatusMdms.forEach((item) => {
      const code = String(item.code || "").toUpperCase();
      if (wanted.includes(code)) {
        byCode[code] = {
          code,
          name: t(item.i18nKey || item.name) || item.name || code,
        };
      }
    });
    return wanted.map(
      (code) =>
        byCode[code] || {
          code,
          name:
            code === "PAID"
              ? t("EST_PAYMENT_STATUS_PAID") || "Paid"
              : t("EST_PAYMENT_STATUS_PENDING_FOR_PAYMENT") ||
                "Pending for payment",
        }
    );
  }, [paymentStatusMdms, t]);

  if (isLoading || (isFetching && !data)) {
    return <Loader />;
  }

  const totalCount = applications.length;
  const nextOffset = pathOffset + pageSize;
  const hasMore = pathOffset > 0 && totalCount > nextOffset;

  return (
    <>
      <Header>{`${t(config.header)} (${totalCount})`}</Header>

      <Card>
        <div className={styles["est-myapps__container"]}>
          <div className={styles["est-myapps__search-row"]}>
            {allotmentNoFilter ? (
              <div className={styles["est-myapps__field-col"]}>
                <div className={styles["est-myapps__field-inner"]}>
                  <CardLabel>{t(allotmentNoFilter.key)}</CardLabel>
                  <TextInput
                    placeholder={t(
                      allotmentNoFilter.placeholder || allotmentNoFilter.key
                    )}
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
          visibleApplications.map((allotment, index) => (
            <div
              key={`${allotment.allotmentId || allotment.allotmentNo || allotment.assetNo || "row"}-${index}`}
            >
              <EstateApplication
                application={allotment}
                allotment={allotment}
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
