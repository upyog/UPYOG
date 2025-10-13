import React from "react";
import { useTranslation } from "react-i18next";
import { StatusTable, Row, BreakLine } from "@upyog/digit-ui-react-components";
import { useMemo } from "react";

const estimate8PercentOfAPV = (taxHeadEstimates) => {
  let val = 0;
  if(taxHeadEstimates && taxHeadEstimates.length>0) {
    val = taxHeadEstimates
      .filter(i =>
        ["PT_TAX", "PT_MODEOFPAYMENT_REBATE", "PT_VACANT_LAND_EXEMPTION", "PT_COMPLEMENTARY_REBATE"]
          .includes(i.taxHeadCode)
      )
      .reduce((sum, i) => sum + (i.estimateAmount || 0), 0);

  }
  return val.toFixed(2)
}
function PropertyEstimates({ taxHeadEstimatesCalculation }) {
  // const { taxHeadEstimates } = taxHeadEstimatesCalculation;
  const { t } = useTranslation();
  const base = taxHeadEstimatesCalculation?.taxHeadEstimates || [];
  // const percentOfAPV = estimate8PercentOfAPV(taxHeadEstimates)
  // console.log("percentOfAPV==",percentOfAPV)
  // taxHeadEstimates.splice(taxHeadEstimates.length - 1, 0, {taxHeadCode: "PT_8_PERCENT_APV", estimateAmount: percentOfAPV, category: "REBATE"});
  const taxHeadEstimates = useMemo(() => {
    // compute % only from the base data
    const percentOfAPV = estimate8PercentOfAPV(base);

    // clone, then inject BEFORE the last item (or at end if length < 1)
    const arr = [...base];

    // avoid duplicates
    const already = arr.some(e => e.taxHeadCode === "PT_8_PERCENT_APV");
    if (!already) {
      const insertAt = Math.max(arr.length - 1, 0);
      arr.splice(insertAt, 0, {
        taxHeadCode: "PT_8_PERCENT_APV",
        estimateAmount: percentOfAPV,
        category: "REBATE",
      });
    } else {
      // if it already exists, update the amount (optional)
      const idx = arr.findIndex(e => e.taxHeadCode === "PT_8_PERCENT_APV");
      if (idx > -1) arr[idx] = { ...arr[idx], estimateAmount: percentOfAPV };
    }

    return arr;
  }, [base]);
  return (
    <div style={{ marginTop: "40px" }}>
      <StatusTable>
        <Row label={t("ES_PT_TITLE_TAX_HEADS")} text={t("ES_PT_TITLE_AMOUNT")} className="border-none" textStyle={{ fontWeight: "bold" }} />
        <BreakLine style={{ margin: "16px 0", width: "40%" }} />
        {taxHeadEstimates?.map((estimate, index) => {
          return (
            <Row
              key={t(estimate.taxHeadCode)}
              label={t(estimate.taxHeadCode)}
              text={`₹ ${estimate.estimateAmount}` || "N/A"}
              last={index === taxHeadEstimates?.length - 1}
              className="border-none"
              textStyle={{ color: "#505A5F" }}
              labelStyle={{ color: "#505A5F" }}
            />
          );
        })}
        <BreakLine style={{ margin: "16px 0", width: "40%" }} />
        <Row
          label={t("ES_PT_TITLE_TOTAL_DUE_AMOUNT")}
          text={`₹ ${taxHeadEstimatesCalculation?.totalAmount}` || "N/A"}
          className="border-none"
          textStyle={{ fontSize: "16px", fontWeight: "bold" }}
        />
      </StatusTable>
    </div>
  );
}

export default PropertyEstimates;
