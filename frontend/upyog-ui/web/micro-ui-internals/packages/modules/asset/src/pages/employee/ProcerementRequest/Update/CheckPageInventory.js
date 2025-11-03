import { Card, CardHeader, CardSubHeader, CardText, CheckBox, LinkButton, Row, StatusTable, SubmitBar } from "@upyog/digit-ui-react-components";
import React, { useState, useEffect } from "react";
import { useTranslation } from "react-i18next";
import { useHistory } from "react-router-dom";

import { checkForNA } from "../../../../utils";
import Timeline from "../../../../components/InventoryTimeLine";

const ActionButton = ({ jumpTo }) => {
  const { t } = useTranslation();
  const history = useHistory();
  function routeTo() {
    history.push(jumpTo);
  }

  return <LinkButton label={t("CS_COMMON_CHANGE")} className="check-page-link-button" onClick={routeTo} />;
};

const CheckPageInventory = ({ onSubmit, value = {} }) => {
  const { t } = useTranslation();
  const history = useHistory();
  const [agree, setAgree] = useState(false);
  const [categoriesWiseData, setCategoriesWiseData] = useState();

  //  * get @param city & state id
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const stateTenantId = Digit.ULBService.getStateId();

  const {inventoryVendor, index, isEdit, isUpdate } = value;

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

          <CardSubHeader>{t("ADD_INVENTORY_VENDOR")}</CardSubHeader>
          <StatusTable>
            <Row
              label={t("INV_VENDOR_NAME")}
              text={`${t(checkForNA(inventoryVendor?.vendorName))}`}
              actionButton={<ActionButton jumpTo={`/upyog-ui/employee/asset/assetservice-up/create-inventory/create`} />}
            />
            <Row
              label={t("INV_CONTACT_PERSON")}
              text={`${t(checkForNA(inventoryVendor?.contactPerson))}`}
              actionButton={<ActionButton jumpTo={`/upyog-ui/employee/asset/assetservice-up/create-inventory/create`} />}
            />
            <Row
              label={t("INV_CONTACT_NUMBER")}
              text={`${t(checkForNA(inventoryVendor?.contactNumber))}`}
              actionButton={<ActionButton jumpTo={`/upyog-ui/employee/asset/assetservice-up/create-inventory/create`} />}
            />

            <Row
              label={t("INV_CONTACT_EMAIL")}
              text={`${t(checkForNA(inventoryVendor?.contactEmail))}`}
              actionButton={<ActionButton jumpTo={`/upyog-ui/employee/asset/assetservice-up/create-inventory/create`} />}
            />

            <Row
              label={t("INV_GSTN")}
              text={`${t(checkForNA(inventoryVendor?.gstNumber))}`}
              actionButton={<ActionButton jumpTo={`/upyog-ui/employee/asset/assetservice-up/create-inventory/create`} />}
            />
            <Row
              label={t("INV_PAN")}
              text={`${t(checkForNA(inventoryVendor?.panNumber))}`}
              actionButton={<ActionButton jumpTo={`/upyog-ui/employee/asset/assetservice-up/create-inventory/create`} />}
            />

            <Row
              label={t("INV_VENDOR_ADDRESS")}
              text={`${t(checkForNA(inventoryVendor?.vendorAddress))}`}
              actionButton={<ActionButton jumpTo={`/upyog-ui/employee/asset/assetservice-up/create-inventory/create`} />}
            />
            <Row
              label={t("INV_UNIQUE_VENDOR_IDENTIFICATION_NO")}
              text={`${t(checkForNA(inventoryVendor?.identificationNo))}`}
              actionButton={<ActionButton jumpTo={`/upyog-ui/employee/asset/assetservice-up/create-inventory/create`} />}
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

export default CheckPageInventory;
