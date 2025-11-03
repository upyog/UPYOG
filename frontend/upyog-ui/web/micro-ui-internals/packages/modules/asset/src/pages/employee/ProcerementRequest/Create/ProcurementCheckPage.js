import { Card, CardHeader, CardSubHeader, CardText, CheckBox, LinkButton, Row, StatusTable, SubmitBar } from "@upyog/digit-ui-react-components";
import React, { useState, useEffect } from "react";
import { useTranslation } from "react-i18next";
import { useHistory } from "react-router-dom";

import { checkForNA } from "../../../../utils";
import Timeline from "../../../../components/ProcerementTimeline";

const ActionButton = ({ jumpTo }) => {
  const { t } = useTranslation();
  const history = useHistory();
  function routeTo() {
    history.push(jumpTo);
  }

  return <LinkButton label={t("CS_COMMON_CHANGE")} className="check-page-link-button" onClick={routeTo} />;
};

const ProcurementCheckPage = ({ onSubmit, value = {} }) => {
  const { t } = useTranslation();
  const history = useHistory();
  const [agree, setAgree] = useState(false);
  const [categoriesWiseData, setCategoriesWiseData] = useState();

  //  * get @param city & state id
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const stateTenantId = Digit.ULBService.getStateId();

  const {procurementReq, index, isEdit, isUpdate } = value;

  const typeOfApplication = !isEdit && !isUpdate ? `new-application` : `edit-application`;

  const setdeclarationhandler = () => {
    setAgree(!agree);
  };
  

  function extractValue(key) {
    var vl = assetDetails[key];
    if (typeof vl === "object") {
      return vl.code;
    }
    return vl;
  }

  return (
    <React.Fragment>
      <Timeline currentStep={2} />
      <Card>
        <CardHeader>{t("AST_CHECK_DETAILS")}</CardHeader>
        <div>
          <br></br>

          <CardSubHeader>{t("ADD_INVENTORY_PROCEREMENT_REQUEST")}</CardSubHeader>
          <StatusTable>
            <Row
              label={t("PROC_ITEM_PARENTCAT")}
              text={`${t(checkForNA(procurementReq?.parentCategory?.code))}`}
              actionButton={<ActionButton jumpTo={`/upyog-ui/employee/asset/assetservice-up/procurement/create`} />}
            />
            <Row
              label={t("PROC_ITEM_SUBCAT")}
              text={`${t(checkForNA(procurementReq?.subCategory.code))}`}
              actionButton={<ActionButton jumpTo={`/upyog-ui/employee/asset/assetservice-up/procurement/create`} />}
            />
            <Row
              label={t("PROC_QUANTITY")}
              text={`${t(checkForNA(procurementReq?.quantity))}`}
              actionButton={<ActionButton jumpTo={`/upyog-ui/employee/asset/assetservice-up/procurement/create`} />}
            />

            <Row
              label={t("PROC_IDENTIFICATION_NO")}
              text={`${t(checkForNA(procurementReq?.identificationNo))}`}
              actionButton={<ActionButton jumpTo={`/upyog-ui/employee/asset/assetservice-up/procurement/create`} />}
            />
    
          </StatusTable>

          <CheckBox
            label={t("AST_FINAL_DECLARATION_MESSAGE")}
            onChange={setdeclarationhandler}
            styles={{ height: "auto" }}
            //disabled={!agree}
          />
        </div>
        <br></br>
        <SubmitBar label={t("COMMON_BUTTON_SUBMIT")} onSubmit={onSubmit} disabled={!agree} />
      </Card>
    </React.Fragment>
  );
};

export default ProcurementCheckPage;
