import React, { useState } from "react";
import { useTranslation } from "react-i18next";
import ArrearTable from "./arrear-table";

const ArrearSummary = ({ bill = {} }) => {
  const { t } = useTranslation();
  const formatTaxHeaders = (billDetail = {}) => {
    let formattedFees = {};
    const { billAccountDetails = [] } = billDetail;
    billAccountDetails.map((taxHead) => {
      formattedFees[taxHead.taxHeadCode] = { value: taxHead.amount, order: taxHead.order };
    });
    formattedFees["CS_BILL_NO"] = { value: billDetail?.billNumber || "NA", order: -2 };
    formattedFees["CS_BILL_DUEDATE"] = {
      value: (billDetail?.expiryDate && new Date(billDetail?.expiryDate).toLocaleDateString()) || "NA",
      order: -1,
    };
    formattedFees["TL_COMMON_TOTAL_AMT"] = { value: billDetail.amount, order: 10 };
    return formattedFees;
  };

  const getFinancialYears = (from, to) => {
    const fromDate = new Date(from);
    const toDate = new Date(to);
    if (toDate.getYear() - fromDate.getYear() != 0) {
      return `FY${fromDate.getYear() + 1900}-${toDate.getYear() - 100}`;
    }
    return `${fromDate.toLocaleDateString()}-${toDate.toLocaleDateString()}`;
  };

  let fees = {};
  let sortedBillDetails = bill?.billDetails?.sort((a, b) => b.fromPeriod - a.fromPeriod) || [];
  sortedBillDetails = [...sortedBillDetails];
  const arrears = sortedBillDetails?.reduce((total, current, index) => (index === 0 ? total : total + current.amount), 0) || 0;
  let arrearsAmount = `₹ ${arrears?.toFixed?.(0) || Number(0).toFixed(0)}`;

  sortedBillDetails.shift();
  sortedBillDetails.map((bill) => {
    let fee = formatTaxHeaders(bill);
    fees[getFinancialYears(bill.fromPeriod, bill.toPeriod)] = fee;
  });

  let head = {};
  fees
    ? Object.keys(fees).map((key, ind) => {
        let value = [];
        Object.keys(fees[key]).map((key1) => {
          head[key1] = (fees[key] && fees[key][key1] && fees[key][key1].order) || 0;
        });
      })
    : "NA";
  let keys = [];

  keys = Object.keys(head);
  keys.sort((x, y) => head[x] - head[y]);

  const [showArrear, setShowArrear] = useState(false);

  if (arrears == 0 || arrears < 0) {
    return null;
  }
  return (
    <div className="cmn-arrears-section">
      <div className="cmn-arrears-header-row">
        <div className="cmn-arrears-title">{t("CS_ARREARS_DETAILS")}</div>
        <button
          type="button"
          className="cmn-arrears-toggle-btn"
          onClick={() => setShowArrear(!showArrear)}
        >
          {showArrear ? (
            <>
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round">
                <polyline points="18 15 12 9 6 15" />
              </svg>
              {t("CS_HIDE_CARD")}
            </>
          ) : (
            <>
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round">
                <polyline points="6 9 12 15 18 9" />
              </svg>
              {t("CS_SHOW_CARD")}
            </>
          )}
        </button>
      </div>
      {showArrear && <ArrearTable headers={[...keys]} values={fees} arrears={arrearsAmount} />}
    </div>
  );
};

export default ArrearSummary;

