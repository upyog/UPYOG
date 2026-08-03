import {
    Modal,
    Loader,CloseSvg
  } from "@nudmcdgnpm/digit-ui-react-components";
  import React from "react";
  import { useTranslation } from "react-i18next";
  
  export const PayWSBillModal = ({ setShowToast, billData }) => {
    const { t } = useTranslation();
    const consumerCode = billData?.consumerCode;
    const businessService = billData?.businessService;
    const tenantId = billData?.tenantId;
  
    const { data: demandData, isLoading, isError } = Digit.Hooks.useDemandSearch({
      consumerCode,
      businessService,
      tenantId,
    });
  
    const closeModal = () => setShowToast();
    const setModal = () => console.log("Pay API");
  
    const Heading = ({ label }) => <h1 className="heading-m">{label}</h1>;
    const CloseBtn = ({ onClick }) => <div className="icon-bg-secondary" onClick={onClick}><CloseSvg /></div>;
  
    const formatDate = (ts) => {
      const date = new Date(ts);
      return date.toLocaleDateString("en-GB");
    };
  
    const totals = {
      demandTax: 0,
      demandInterest: 0,
      demandPenalty: 0,
      collectionTax: 0,
      collectionInterest: 0,
      collectionPenalty: 0,
      balanceTax: 0,
      balanceInterest: 0,
      balancePenalty: 0
    };
  
    const getTableRows = () => {
      return demandData?.Demands?.map((demand, index) => {
        const period = `${formatDate(demand.taxPeriodFrom)} - ${formatDate(demand.taxPeriodTo)}`;
  
        let demandTax = 0, demandInterest = 0, demandPenalty = 0;
        let collectionTax = 0, collectionInterest = 0, collectionPenalty = 0;
  
        demand.demandDetails.forEach((detail) => {
          const code = detail.taxHeadMasterCode || "";
          const amount = detail.taxAmount || 0;
          const collected = detail.collectionAmount || 0;
  
          if (code.includes("CHARGE")) {
            demandTax += amount;
            collectionTax += collected;
          } else if (code.includes("INTEREST")) {
            demandInterest += amount;
            collectionInterest += collected;
          } else if (code.includes("PENALTY")) {
            demandPenalty += amount;
            collectionPenalty += collected;
          }
        });
  
        const balanceTax = demandTax - collectionTax;
        const balanceInterest = demandInterest - collectionInterest;
        const balancePenalty = demandPenalty - collectionPenalty;
  
        totals.demandTax += demandTax;
        totals.demandInterest += demandInterest;
        totals.demandPenalty += demandPenalty;
        totals.collectionTax += collectionTax;
        totals.collectionInterest += collectionInterest;
        totals.collectionPenalty += collectionPenalty;
        totals.balanceTax += balanceTax;
        totals.balanceInterest += balanceInterest;
        totals.balancePenalty += balancePenalty;
  
        return (
          <tr key={index}>
            <td style={cellStyleLeft}>{period}</td>
            <td style={cellStyleRight}>{demandTax.toFixed(2)}</td>
            <td style={cellStyleRight}>{demandInterest.toFixed(2)}</td>
            <td style={cellStyleRight}>{demandPenalty.toFixed(2)}</td>
            <td style={cellStyleRight}>{collectionTax.toFixed(2)}</td>
            <td style={cellStyleRight}>{collectionInterest.toFixed(2)}</td>
            <td style={cellStyleRight}>{collectionPenalty.toFixed(2)}</td>
            <td style={cellStyleRight}>{balanceTax.toFixed(2)}</td>
            <td style={cellStyleRight}>{balanceInterest.toFixed(2)}</td>
            <td style={cellStyleRight}>{balancePenalty.toFixed(2)}</td>
          </tr>
        );
      });
    };
  
    const cellStyleRight = { border: "1px solid #ddd", padding: "6px 8px", textAlign: "right" };
    const cellStyleLeft = { border: "1px solid #ddd", padding: "6px 8px", textAlign: "left" };
  
    return (
      <Modal
        headerBarMain={
          <Heading
            label={t(
              `Pay Due Amounts for ${billData?.businessService === "WS" ? "Water Connection" : "Sewerage Connection"}`
            )}
          />
        }
        headerBarEnd={<CloseBtn onClick={closeModal} />}
        actionCancelLabel={t("CANCEL")}
        actionCancelOnSubmit={closeModal}
        actionSaveLabel={t("PAY")}
        actionSaveOnSubmit={setModal}
        formId="modal-action"
        popupStyles={{ width: "90%", marginTop: "5px" }}
      >
        {isLoading ? (
          <Loader />
        ) : isError ? (
          <p style={{ color: "red", padding: "1rem" }}>{t("ERROR_LOADING_DATA")}</p>
        ) : (
          <div style={{ overflowX: "auto" }}>
            <table
              style={{
                width: "100%",
                borderCollapse: "collapse",
                fontSize: "14px",
                marginTop: "1rem"
              }}
            >
              <thead>
                <tr style={{ backgroundColor: "#f0f0f0" }}>
                  <th rowSpan={2} style={cellStyleLeft}>Installments</th>
                  <th colSpan={3} style={{ ...cellStyleRight, textAlign: "center" }}>Demand</th>
                  <th colSpan={3} style={{ ...cellStyleRight, textAlign: "center" }}>Collection</th>
                  <th colSpan={3} style={{ ...cellStyleRight, textAlign: "center" }}>Balance</th>
                </tr>
                <tr style={{ backgroundColor: "#f9f9f9" }}>
                  <th style={cellStyleRight}>Tax</th>
                  <th style={cellStyleRight}>Interest</th>
                  <th style={cellStyleRight}>Penalty</th>
                  <th style={cellStyleRight}>Tax</th>
                  <th style={cellStyleRight}>Interest</th>
                  <th style={cellStyleRight}>Penalty</th>
                  <th style={cellStyleRight}>Tax</th>
                  <th style={cellStyleRight}>Interest</th>
                  <th style={cellStyleRight}>Penalty</th>
                </tr>
              </thead>
              <tbody>
                {getTableRows()}
                <tr style={{ backgroundColor: "#efefef", fontWeight: "bold" }}>
                  <td style={cellStyleLeft}>Total</td>
                  <td style={cellStyleRight}>{totals.demandTax.toFixed(2)}</td>
                  <td style={cellStyleRight}>{totals.demandInterest.toFixed(2)}</td>
                  <td style={cellStyleRight}>{totals.demandPenalty.toFixed(2)}</td>
                  <td style={cellStyleRight}>{totals.collectionTax.toFixed(2)}</td>
                  <td style={cellStyleRight}>{totals.collectionInterest.toFixed(2)}</td>
                  <td style={cellStyleRight}>{totals.collectionPenalty.toFixed(2)}</td>
                  <td style={cellStyleRight}>{totals.balanceTax.toFixed(2)}</td>
                  <td style={cellStyleRight}>{totals.balanceInterest.toFixed(2)}</td>
                  <td style={cellStyleRight}>{totals.balancePenalty.toFixed(2)}</td>
                </tr>
              </tbody>
            </table>
          </div>
        )}
      </Modal>
    );
  };
  