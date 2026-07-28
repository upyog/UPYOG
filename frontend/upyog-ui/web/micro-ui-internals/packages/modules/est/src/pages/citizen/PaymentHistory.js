/**
 * Citizen EST Payment History — same layout as My Applications
 * (search card + StatusTable result cards).
 * Data: allotments (billing) + assets (building name) + collection receipts.
 * Receipt consumerCodes use allotmentNo (same as Make Payment billing key).
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
  Row,
  StatusTable,
  sortByOrder,
} from "@nudmcdgnpm/digit-ui-react-components";
import { useTranslation } from "react-i18next";
import { resolvePaymentHistoryConfig } from "../../utils/estMdmsUtils";
import {
  formatReceiptDate,
  toBillingCycleLabel,
} from "../../utils/estDisplayUtils";
import styles from "../../styles/ESTMyApplications.module.scss";

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

const formatFieldValue = (row, field) => {
  const raw = row?.[field.accessor];
  if (raw === undefined || raw === null || raw === "") return "N/A";
  if (field.format === "currency") return `₹${raw}`;
  return raw;
};

export const ESTPaymentHistory = () => {
  const { t } = useTranslation();
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

  // Billing consumerCodes are allotmentNo (same key as Make Payment / fetchBill).
  const consumerCodes = useMemo(
    () =>
      [
        ...new Set(
          allotments
            .map((item) => String(item?.allotmentNo || "").trim())
            .filter(Boolean)
        ),
      ].join(","),
    [allotments]
  );

  const allotmentByConsumerCode = useMemo(
    () =>
      allotments.reduce((acc, item) => {
        const key = String(item?.allotmentNo || "").trim();
        if (key && !acc[key]) acc[key] = item;
        return acc;
      }, {}),
    [allotments]
  );

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
    return payments.map((payment) => {
      const detail = payment?.paymentDetails?.[0] || {};
      const consumerCode =
        detail?.bill?.consumerCode || payment?.consumerCode || "";
      const allotment =
        allotmentByConsumerCode[consumerCode] ||
        allotmentByAssetNo[consumerCode] ||
        {};
      const assetKey = allotment?.assetNo || consumerCode;
      const asset = assetByEstateNo[assetKey] || {};

      return {
        receiptNumber: detail?.receiptNumber || "N/A",
        receiptDate: detail?.receiptDate,
        receiptDateLabel: formatReceiptDate(detail?.receiptDate),
        assetNo: allotment?.assetNo || consumerCode || "N/A",
        allotmentNo: allotment?.allotmentNo || consumerCode || "N/A",
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
    });
  }, [
    receiptResponse,
    allotmentByConsumerCode,
    allotmentByAssetNo,
    assetByEstateNo,
    t,
  ]);

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
        String(item.allotmentNo || "")
          .toLowerCase()
          .includes(normalizedSearch) ||
        String(item.buildingName || "")
          .toLowerCase()
          .includes(normalizedSearch);

      const receiptDate = item.receiptDate ? new Date(item.receiptDate) : null;
      const matchesFrom = !from || (receiptDate && receiptDate >= from);
      const matchesTo = !to || (receiptDate && receiptDate <= to);

      return matchesAssetNo && matchesFrom && matchesTo;
    });
  }, [paymentData, appliedFilters]);

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

  return (
    <>
      <Header>{`${t(config.header)} (${filteredData.length})`}</Header>

      <Card>
        <div className={styles["est-myapps__container"]}>
          <div className={styles["est-myapps__search-row"]}>
            {filters.map((field) => (
              <div key={field.name} className={styles["est-myapps__field-col"]}>
                <div className={styles["est-myapps__field-inner"]}>
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
                      className={styles["est-myapps__text-input"]}
                    />
                  )}
                </div>
              </div>
            ))}

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
        {filteredData.map((row, index) => (
          <Card
            key={`${row.receiptNumber}-${row.allotmentNo}-${index}`}
            className={styles["est-myapps__card"]}
          >
            <StatusTable>
              {resultFields.map((field) => (
                <Row
                  key={field.key}
                  className="border-none"
                  label={t(field.key)}
                  text={formatFieldValue(row, field)}
                />
              ))}
            </StatusTable>
          </Card>
        ))}

        {filteredData.length === 0 ? (
          <p className={styles["est-myapps__msg"]}>
            {t(config.emptyState?.message || "EST_NO_APPLICATION_FOUND_MSG")}
          </p>
        ) : null}
      </div>
    </>
  );
};

export default ESTPaymentHistory;
