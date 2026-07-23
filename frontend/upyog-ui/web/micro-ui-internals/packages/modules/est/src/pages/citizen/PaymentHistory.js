/**
 * Citizen EST Payment History — config-driven filters + result cards.
 * Data: allotments (billing) + assets (building name) + collection receipts.
 */
import React, { useCallback, useMemo, useState } from "react";
import {
  Header,
  Loader,
  TextInput,
  SubmitBar,
  Card,
  CardLabel,
  DatePicker,
  KeyNote,
  Table,
  useIsMobile,
} from "@nudmcdgnpm/digit-ui-react-components";
import { useTranslation } from "react-i18next";
import LOCAL_PAYMENT_HISTORY_CONFIG from "../../config/paymentHistoryConfig";
import styles from "../../styles/ESTPaymentHistory.module.scss";

const formatDate = (value) => {
  if (!value && value !== 0) return "N/A";
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return "N/A";
  return date.toLocaleDateString("en-GB");
};

const toStartOfDay = (value) => {
  if (!value) return null;
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return null;
  date.setHours(0, 0, 0, 0);
  return date;
};

const toEndOfDay = (value) => {
  const date = toStartOfDay(value);
  if (!date) return null;
  date.setHours(23, 59, 59, 999);
  return date;
};

const toBillingCycleLabel = (value, t) => {
  const code =
    typeof value === "object"
      ? String(value?.code || value?.name || "").trim().toUpperCase()
      : String(value || "").trim().toUpperCase();
  return code ? t(`EST_BILLING_CYCLE_${code}`) : "N/A";
};

const sortByOrder = (items = []) =>
  [...items].sort((a, b) => (a.order ?? 0) - (b.order ?? 0));

const formatFieldValue = (row, field) => {
  const raw = row?.[field.accessor];
  if (raw === undefined || raw === null || raw === "") return "N/A";
  if (field.format === "currency") return `₹${raw}`;
  return raw;
};

const resolvePaymentHistoryConfig = (mdmsData) => {
  const entry = Array.isArray(mdmsData)
    ? mdmsData[0]
    : Array.isArray(mdmsData?.body)
      ? mdmsData.body[0]
      : mdmsData;
  if (!entry || typeof entry !== "object") return LOCAL_PAYMENT_HISTORY_CONFIG;
  return {
    ...LOCAL_PAYMENT_HISTORY_CONFIG,
    ...entry,
    emptyState: {
      ...LOCAL_PAYMENT_HISTORY_CONFIG.emptyState,
      ...(entry.emptyState || {}),
    },
    filters:
      Array.isArray(entry.filters) && entry.filters.length > 0
        ? entry.filters
        : LOCAL_PAYMENT_HISTORY_CONFIG.filters,
    resultFields:
      Array.isArray(entry.resultFields) && entry.resultFields.length > 0
        ? entry.resultFields
        : LOCAL_PAYMENT_HISTORY_CONFIG.resultFields,
    actionButton: {
      ...LOCAL_PAYMENT_HISTORY_CONFIG.actionButton,
      ...(entry.actionButton || {}),
    },
  };
};

export const ESTPaymentHistory = () => {
  const { t } = useTranslation();
  const isMobile = useIsMobile();
  const tenantId =
    Digit.ULBService.getCitizenCurrentTenant(true) ||
    Digit.ULBService.getCurrentTenantId();
  const user = Digit.UserService.getUser()?.info;
  const mobileNumber = user?.mobileNumber;
  const stateId = Digit.ULBService.getStateId();

  const { data: mdmsPaymentHistory } = Digit.Hooks.useEnabledMDMS(
    stateId,
    "Estate",
    [{ name: "PaymentHistoryConfig" }],
    {
      select: (mdms) => mdms?.Estate?.PaymentHistoryConfig || null,
    }
  );

  const config = useMemo(
    () => resolvePaymentHistoryConfig(mdmsPaymentHistory),
    [mdmsPaymentHistory]
  );

  const filters = useMemo(() => sortByOrder(config.filters), [config.filters]);
  const resultFields = useMemo(
    () => sortByOrder(config.resultFields),
    [config.resultFields]
  );

  const [draftFilters, setDraftFilters] = useState({
    assetNo: "",
    fromDate: "",
    toDate: "",
  });
  const [appliedFilters, setAppliedFilters] = useState({
    assetNo: "",
    fromDate: "",
    toDate: "",
  });

  const {
    data: allotmentResponse,
    isLoading: isAllotmentsLoading,
  } = Digit.Hooks.estate.useESTApplicationSearch({
    filters: { tenantId, mobileNo: mobileNumber, limit: 100 },
    config: {
      enabled: Boolean(tenantId && mobileNumber),
    },
  });

  const {
    data: assetResponse,
    isLoading: isAssetsLoading,
  } = Digit.Hooks.estate.useESTAssetSearch({
    tenantId,
    filters: {
      AssetSearchCriteria: {
        tenantId,
        mobileNumber,
      },
    },
    config: {
      enabled: Boolean(tenantId && mobileNumber),
    },
  });

  const allotments = allotmentResponse?.Allotments || [];
  const assets = assetResponse?.Assets || [];

  const assetNos = useMemo(
    () => [...new Set(allotments.map((item) => item?.assetNo).filter(Boolean))],
    [allotments]
  );
  const consumerCodes = useMemo(() => assetNos.join(","), [assetNos]);

  const allotmentByAssetNo = useMemo(
    () =>
      allotments.reduce((acc, item) => {
        const key = item?.assetNo;
        if (key && !acc[key]) acc[key] = item;
        return acc;
      }, {}),
    [allotments]
  );

  const assetByEstateNo = useMemo(
    () =>
      assets.reduce((acc, item) => {
        const key = item?.estateNo || item?.assetNo;
        if (key && !acc[key]) acc[key] = item;
        return acc;
      }, {}),
    [assets]
  );

  const {
    data: receiptResponse,
    isLoading: isReceiptsLoading,
  } = Digit.Hooks.useRecieptSearch(
    {
      tenantId,
      businessService: config.businessService || "est-services",
      consumerCodes,
      isEmployee: false,
    },
    {
      enabled: Boolean(tenantId && consumerCodes),
      retry: false,
    }
  );

  const paymentData = useMemo(() => {
    const payments = receiptResponse?.Payments || [];
    return payments
      .map((payment) => {
        const detail = payment?.paymentDetails?.[0] || {};
        const consumerCode =
          detail?.bill?.consumerCode || payment?.consumerCode || "";
        const allotment = allotmentByAssetNo[consumerCode] || {};
        const asset = assetByEstateNo[consumerCode] || {};

        return {
          receiptNumber: detail?.receiptNumber || "N/A",
          receiptDate: detail?.receiptDate,
          receiptDateLabel: formatDate(detail?.receiptDate),
          assetNo: consumerCode || "N/A",
          buildingName:
            asset?.buildingName ||
            asset?.assetName ||
            allotment?.buildingName ||
            allotment?.assetName ||
            "N/A",
          billingCycle: toBillingCycleLabel(allotment?.billingCycle, t),
          amountPaid: payment?.totalAmountPaid ?? detail?.totalAmountPaid ?? 0,
          paymentMode: payment?.paymentMode || "N/A",
        };
      })
      .sort((a, b) => Number(b.receiptDate || 0) - Number(a.receiptDate || 0));
  }, [receiptResponse, allotmentByAssetNo, assetByEstateNo, t]);

  const filteredData = useMemo(() => {
    const normalizedSearch = String(appliedFilters.assetNo || "")
      .trim()
      .toLowerCase();
    const from = toStartOfDay(appliedFilters.fromDate);
    const to = toEndOfDay(appliedFilters.toDate);

    return paymentData.filter((item) => {
      const matchesAssetNo =
        !normalizedSearch ||
        String(item.assetNo || "").toLowerCase().includes(normalizedSearch) ||
        String(item.buildingName || "")
          .toLowerCase()
          .includes(normalizedSearch);

      const receiptDate = item.receiptDate ? new Date(item.receiptDate) : null;
      const matchesFrom = !from || (receiptDate && receiptDate >= from);
      const matchesTo = !to || (receiptDate && receiptDate <= to);

      return matchesAssetNo && matchesFrom && matchesTo;
    });
  }, [paymentData, appliedFilters]);

  const tableColumns = useMemo(
    () =>
      resultFields.map((field) => ({
        Header: t(field.key),
        accessor: field.accessor,
        Cell:
          field.format === "currency"
            ? ({ row }) => formatFieldValue(row.original, field)
            : undefined,
      })),
    [resultFields, t]
  );

  const handleFilterChange = useCallback((name, value) => {
    setDraftFilters((prev) => ({ ...prev, [name]: value }));
  }, []);

  const handleSearch = useCallback(() => {
    setAppliedFilters({ ...draftFilters });
  }, [draftFilters]);

  const handleClear = useCallback(() => {
    const blank = { assetNo: "", fromDate: "", toDate: "" };
    setDraftFilters(blank);
    setAppliedFilters(blank);
  }, []);

  const isLoading = isAllotmentsLoading || isAssetsLoading || isReceiptsLoading;
  if (isLoading) return <Loader />;

  const useCards = config.resultMode === "cards" || isMobile;

  return (
    <div className={styles["est-payhist__page"]}>
      <Header>{`${t(config.header)} (${filteredData.length})`}</Header>

      <Card className={styles["est-payhist__search-card"]}>
        <div className={styles["est-payhist__search-row"]}>
          {filters.map((field) => (
            <div key={field.name} className={styles["est-payhist__field-col"]}>
              <div className={styles["est-payhist__field-inner"]}>
                <CardLabel>{t(field.key)}</CardLabel>
                {field.type === "date" ? (
                  <DatePicker
                    date={draftFilters[field.name] || ""}
                    onChange={(value) => handleFilterChange(field.name, value)}
                  />
                ) : (
                  <TextInput
                    placeholder={t(field.placeholder || field.key)}
                    value={draftFilters[field.name] || ""}
                    onChange={(e) =>
                      handleFilterChange(field.name, e.target.value)
                    }
                    className={styles["est-payhist__text-input"]}
                  />
                )}
              </div>
            </div>
          ))}

          <div className={styles["est-payhist__actions"]}>
            <SubmitBar
              label={t(config.actionButton?.search || "ES_COMMON_SEARCH")}
              onSubmit={handleSearch}
            />
            <p
              className={styles["est-payhist__clear-link"]}
              onClick={handleClear}
            >
              {t(config.actionButton?.clear || "ES_COMMON_CLEAR_ALL")}
            </p>
          </div>
        </div>
      </Card>

      {filteredData.length === 0 ? (
        <p className={styles["est-payhist__msg"]}>
          {t(config.emptyState?.message || "EST_NO_APPLICATION_FOUND_MSG")}
        </p>
      ) : useCards ? (
        <div className={styles["est-payhist__list"]}>
          {filteredData.map((row, index) => (
            <Card
              key={`${row.receiptNumber}-${row.assetNo}-${index}`}
              className={styles["est-payhist__card"]}
            >
              {resultFields.map((field) => (
                <KeyNote
                  key={field.key}
                  keyValue={t(field.key)}
                  note={formatFieldValue(row, field)}
                  noteStyle={
                    field.emphasize || field.format === "currency"
                      ? { fontSize: "22px", fontWeight: "700" }
                      : undefined
                  }
                />
              ))}
            </Card>
          ))}
        </div>
      ) : (
        <div className={styles["est-payhist__table-wrap"]}>
          <Table
            t={t}
            data={filteredData}
            columns={tableColumns}
            getCellProps={() => ({
              style: {
                minWidth: "120px",
                padding: "12px 8px",
                textAlign: "center",
              },
            })}
            disableSort={false}
            onSort={() => {}}
            manualPagination={false}
            isPaginationRequired={false}
            totalRecords={filteredData.length}
          />
        </div>
      )}
    </div>
  );
};

export default ESTPaymentHistory;
