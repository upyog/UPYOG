import React from "react";
import { useTranslation } from "react-i18next";
import { StatusTable, Row, BreakLine } from "@upyog/digit-ui-react-components";
import { useMemo } from "react";

// const estimate8PercentOfAPV = (taxHeadEstimates) => {
//   let val = 0;
//   if(taxHeadEstimates && taxHeadEstimates.length>0) {
//     val = taxHeadEstimates
//       .filter(i =>
//         ["PT_TAX", "PT_MODEOFPAYMENT_REBATE", "PT_VACANT_LAND_EXEMPTION", "PT_COMPLEMENTARY_REBATE"]
//           .includes(i.taxHeadCode)
//       )
//       .reduce((sum, i) => sum + (i.estimateAmount || 0), 0);

//   }
//   return val.toFixed(2)
// }
const calculateTaxableAPV = (taxHeadEstimates) => {
  let val = 0;
  if(taxHeadEstimates && taxHeadEstimates.length>0) {
    val = taxHeadEstimates
      .filter(i =>
        ["PT_TAX", "PT_VACANT_LAND_EXEMPTION"]
          .includes(i.taxHeadCode)
      )
      .reduce((sum, i) => sum + (i.estimateAmount || 0), 0);

  }
  return val.toFixed(2)
}
const calculateApplicableRebate = (taxHeadEstimates, taxableAPV) => {
  let filteredData;
  if(taxHeadEstimates && taxHeadEstimates.length>0) {
    filteredData = taxHeadEstimates
      .filter(i =>
        ["PT_MODEOFPAYMENT_REBATE"]
          .includes(i.taxHeadCode)
      )
      //.reduce((sum, i) => sum + (i.estimateAmount || 0), 0);

  }
  // console.log("calculatedPercentage==",filteredData)
  let val = 0;
  if(filteredData && filteredData.length>0) {
    let per = filteredData[0]?.calculatedPercentage / 100;
    val = taxableAPV * per
  }
  // console.log("val==",val);
  return val.toFixed(2);

  // val = val.toFixed(2);

}
const calculateAnnualPropertyTax = (taxableAPV, applicableRebate) => {
  
  let val = 0;
  if(taxableAPV) {
    val = taxableAPV
  }
  // console.log("val==",val);
  let APT = val * 0.08;
  return APT.toFixed(2);

  // val = val.toFixed(2);

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
    // const percentOfAPV = estimate8PercentOfAPV(base);
    const taxableAPV = calculateTaxableAPV(base);
    const applicableRebate = calculateApplicableRebate(base, taxableAPV);
    const annualPropertyTax = calculateAnnualPropertyTax(taxableAPV, applicableRebate);
    if(taxableAPV) {

    }
    // clone, then inject BEFORE the last item (or at end if length < 1)
    const arr = [...base];

    // avoid duplicates
    // const already = arr.some(e => e.taxHeadCode === "PT_8_PERCENT_APV");
    
    // if (!already) {
    //   const insertAt = Math.max(arr.length - 1, 0);
    //   arr.splice(insertAt, 0, {
    //     taxHeadCode: "PT_8_PERCENT_APV",
    //     estimateAmount: percentOfAPV,
    //     category: "REBATE",
    //   });
    // } else {
    //   // if it already exists, update the amount (optional)
    //   const idx = arr.findIndex(e => e.taxHeadCode === "PT_8_PERCENT_APV");
    //   if (idx > -1) arr[idx] = { ...arr[idx], estimateAmount: percentOfAPV };
    // }
    const alreadyTaxableAPV = arr.some(e => e.taxHeadCode === "PT_TAXABLE_APV");
    if (!alreadyTaxableAPV) {
      const insertAt = Math.max(arr.length - 4, 0);
      arr.splice(insertAt, 0, {
        taxHeadCode: "PT_TAXABLE_APV",
        estimateAmount: taxableAPV,
        category: "REBATE",
      });
    } else {
      // if it already exists, update the amount (optional)
      const idx = arr.findIndex(e => e.taxHeadCode === "PT_TAXABLE_APV");
      if (idx > -1) arr[idx] = { ...arr[idx], estimateAmount: taxableAPV };
    }
    // const applicableRebateData = arr.some(e => e.taxHeadCode === "PT_APPLICABLE_REBATE");
    // if (!applicableRebateData) {
    //   const insertAt = Math.max(arr.length - 4, 0);
    //   arr.splice(insertAt, 0, {
    //     taxHeadCode: "PT_APPLICABLE_REBATE",
    //     estimateAmount: applicableRebate,
    //     category: "REBATE",
    //   });
    // } else {
    //   // if it already exists, update the amount (optional)
    //   const idx = arr.findIndex(e => e.taxHeadCode === "PT_APPLICABLE_REBATE");
    //   if (idx > -1) arr[idx] = { ...arr[idx], estimateAmount: applicableRebate };
    // }

    const APT = arr.some(e => e.taxHeadCode === "PT_ANNUAL_PROPERTY_TAX");
    if (!APT) {
      const insertAt = Math.max(arr.length - 4, 0);
      arr.splice(insertAt, 0, {
        taxHeadCode: "PT_ANNUAL_PROPERTY_TAX",
        estimateAmount: annualPropertyTax,
        category: "REBATE",
      });
    } else {
      // if it already exists, update the amount (optional)
      const idx = arr.findIndex(e => e.taxHeadCode === "PT_ANNUAL_PROPERTY_TAX");
      if (idx > -1) arr[idx] = { ...arr[idx], estimateAmount: annualPropertyTax };
    }

    return arr;
  }, [base]);
  return (
    <div style={{ marginTop: "40px" }}>
      <StatusTable>
        <Row label={t("ES_PT_TITLE_TAX_HEADS")} text={t("ES_PT_TITLE_AMOUNT")} className="border-none" textStyle={{ fontWeight: "bold" }} />
        <BreakLine style={{ margin: "16px 0", width: "40%" }} />
        {taxHeadEstimates?.filter(e => e.taxHeadCode !== "PT_COMPLEMENTARY_REBATE" && e.taxHeadCode !== "PT_ROUNDOFF")?.map((estimate, index) => {
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
        <div style={{ fontSize: "12px", fontStyle: "italic", marginTop: "8px", display: "flex", alignItems: "flex-start", gap: "12px" }}>
          <div style={{ fontWeight: "bold", minWidth: "20px" }}>N.B.:</div>
          <ol style={{ margin: 0 }}>
            <li>1. Property Tax Due Amount is rounded off to the nearest ten rupees.</li>
            <li>2. Minimum payable amount to reach ULB's notified lower limit is applicable when assessed property tax is lesser than the ULB's notified lower limit.</li>
          </ol>
        </div>
      </StatusTable>
    </div>
  );
}

export default PropertyEstimates;
