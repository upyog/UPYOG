import React from "react";
import { useTranslation } from "react-i18next";
import { StatusTable, Row, BreakLine } from "@nudmcdgnpm/digit-ui-react-components";

function PropertyEstimates({ taxHeadEstimatesCalculation }) {
  const { taxHeadEstimates } = taxHeadEstimatesCalculation;
  const { t } = useTranslation();

  return (
    <div className="tmpl-property-estimates-top-spacing">
      <StatusTable>
        <Row label={t("ES_PT_TITLE_TAX_HEADS")} text={t("ES_PT_TITLE_AMOUNT")} className="border-none" textStyle={{ fontWeight: "bold" }} />
        <BreakLine className="tmpl-property-estimates-spacing" />
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
        <BreakLine className="tmpl-property-estimates-spacing" />
        <Row
          label={t("ES_PT_TITLE_TOTAL_DUE_AMOUNT")}
          text={`₹ ${taxHeadEstimatesCalculation?.totalAmount}` || "N/A"}
          className="border-none"
          textStyle={{ fontSize: "24px", fontWeight: "bold" }}
        />
      </StatusTable>
    </div>
  );
}

export default PropertyEstimates;
