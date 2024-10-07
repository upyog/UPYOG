import React from "react";
import { Card, Row, CardHeader, StatusTable } from "@nudmcdgnpm/digit-ui-react-components";
import { useTranslation } from "react-i18next";
const AssetsQRCode = ({ path }) => {
    const { t } = useTranslation();
    const convertEpochToDate = (dateEpoch) => {
        // Returning NA in else case because new Date(null) returns Current date from calender
        if (dateEpoch) {
            const dateFromApi = new Date(dateEpoch);
            let month = dateFromApi.getMonth() + 1;
            let day = dateFromApi.getDate();
            let year = dateFromApi.getFullYear();
            month = (month > 9 ? "" : "0") + month;
            day = (day > 9 ? "" : "0") + day;
            return `${day}/${month}/${year}`;
        } else {
            return "NA";
        }
    };
    const convertToLocale = (value = "", key = "") => {
        let convertedValue = convertDotValues(value);
        if (convertedValue == "NA") {
            return "PT_NA";
        }
        return `${key}_${convertedValue}`;
    };

    const convertDotValues = (value = "") => {
        return (
            (checkForNotNull(value) && ((value.replaceAll && value.replaceAll(".", "_")) || (value.replace && stringReplaceAll(value, ".", "_")))) || "NA"
        );
    };
    const checkForNotNull = (value = "") => {
        return value && value != null && value != undefined && value != "" ? true : false;
    };
    const getFinancialYears = (from, to) => {
        const fromDate = new Date(from);
        const toDate = new Date(to);
        if (toDate.getYear() - fromDate.getYear() != 0) {
            return `FY${fromDate.getYear() + 1900}-${toDate.getYear() - 100}`;
        }
        return `${fromDate.toLocaleDateString()}-${toDate.toLocaleDateString()}`;
    };

    const myUrl = new URL(window.location.href);
    const params = new URLSearchParams(myUrl.search);
    const tenantId = params.get("tenantId")
    const applicationNo = params.get("applicationNo")
    console.log(tenantId, " tenantId ")
    console.log(applicationNo, " applicationNo ")
    const { data: PaymentReceipt, isLoading: recieptDataLoading } = Digit.Hooks.useAssetQrCode(
        {
            tenantId: tenantId,
            applicationNo: applicationNo,

        },
    );
    return (
        <React.Fragment>
            <div style={{ width: "100%" }}>
                <Card>
                    <CardHeader>Assets Summary </CardHeader>
                    {!recieptDataLoading ?
                        <StatusTable>
                            <Row label={t("Id")} text={PaymentReceipt?.Assets[0]?.applicationNo || "NA"}  />
                            <Row label={t("Reference NO")} text={PaymentReceipt?.Assets[0]?.assetBookRefNo || "NA"}  />
                            <Row label={t("Assets Name")} text={PaymentReceipt?.Assets[0]?.assetName || "NA"}  />
                            <Row label={t("City")} text={PaymentReceipt?.Assets[0]?.addressDetails?.city || "NA"}  />
                            <Row label={t("Purchase Cost")} text={PaymentReceipt?.Assets[0]?.additionalDetails?.purchaseCost || "NA"}  />
                            {/* <Row label={t("CR_RECEIPT_PAYMENT_DATE")} text={convertEpochToDate(PaymentReceipt?.Payments[0]?.paymentDetails[0].receiptDate) || "NA"} />
                            <Row label={t("CR_RECEIPT_PAYER_NAME")} text={PaymentReceipt?.Payments[0].payerName || "NA"} />
                            <Row label={t("CR_RECEIPT_PAYER_NUMBER")} text={PaymentReceipt?.Payments[0].mobileNumber || "NA"} />
                            <Row
                                label={t("CR_RECEIPT_SERVICE_TYPE")}
                                text={t(convertToLocale(PaymentReceipt?.Payments[0]?.paymentDetails[0].businessService, "BILLINGSERVICE_BUSINESSSERVICE")) || "NA"}
                            />
                            <Row
                                label={t("CR_RECEIPT_BILL_PERIOD")}
                                text={
                                    getFinancialYears(
                                        PaymentReceipt?.Payments[0]?.paymentDetails[0].bill?.billDetails[0]?.fromPeriod,
                                        PaymentReceipt?.Payments[0]?.paymentDetails[0].bill?.billDetails[0]?.toPeriod
                                    ) || "NA"
                                }
                            />
                            <Row label={t("CR_RECEIPT_AMOUNT")} text={"₹" + PaymentReceipt?.Payments[0].totalAmountPaid || "NA"} />
                            <Row label={t("CR_RECEIPT_PENDING_AMOUNT")} text={"₹" + PaymentReceipt?.Payments[0].totalDue || "₹0"} />
                            <Row
                                label={t("CR_RECEIPT_PAYMENT_MODE")}
                                text={PaymentReceipt?.Payments[0].paymentMode ? t(`COMMON_MASTERS_PAYMENTMODE_${PaymentReceipt?.Payments[0].paymentMode}`) || "NA" : "NA"}
                            />
                            <Row label={t("CR_RECEIPT_TXN_ID")} text={PaymentReceipt?.Payments[0].transactionNumber || "NA"} />
                            <Row label={t("CR_RECEIPT_G8_RECEIPT_NO")} text={PaymentReceipt?.Payments[0]?.manualReceiptNumber || "NA"} />
                            <Row label={t("CR_RECEIPT_G8_RECEIPT_DATE")} text={convertEpochToDate(PaymentReceipt?.Payments[0]?.manualReceiptDate) || "NA"} /> */}
                        </StatusTable> : ""}
                </Card>
            </div>
        </React.Fragment>
    );
};
export default AssetsQRCode;