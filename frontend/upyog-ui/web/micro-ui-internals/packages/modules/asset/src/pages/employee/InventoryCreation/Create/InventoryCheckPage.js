import { Card, CardHeader, CardSubHeader, CardText, CheckBox, LinkButton, Row, StatusTable, SubmitBar } from "@upyog/digit-ui-react-components";
import React, { useState, useEffect } from "react";
import { useTranslation } from "react-i18next";
import { useHistory } from "react-router-dom";

import { checkForNA } from "../../../../utils";
import Timeline from "../../../../components/InventoryCreationTimeLine";

const ActionButton = ({ jumpTo }) => {
  const { t } = useTranslation();
  const history = useHistory();
  function routeTo() {
    history.push(jumpTo);
  }

  return <LinkButton label={t("CS_COMMON_CHANGE")} className="check-page-link-button" onClick={routeTo} />;
};

const InventoryCheckPage = ({ onSubmit, value = {} }) => {
  const { t } = useTranslation();
  const history = useHistory();
  const [agree, setAgree] = useState(false);
  const [categoriesWiseData, setCategoriesWiseData] = useState();

  //  * get @param city & state id
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const stateTenantId = Digit.ULBService.getStateId();

  const {inventory, index } = value;
console.log("inventory", inventory )
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

          <CardSubHeader>{t("ADD_INVENTORY_CREATION_REQUEST")}</CardSubHeader>
          <StatusTable>
            <Row
              label={t("PROCUREMENT_REQUEST_ID")}
              text={`${t(checkForNA(inventory?.requestId?.code))}`}
              actionButton={<ActionButton jumpTo={`/upyog-ui/employee/asset/assetservice-up/inventory-creation/create`} />}
            />
            <Row
              label={t("INV_OFFICE")}
              text={`${t(checkForNA(inventory?.office))}`}
              actionButton={<ActionButton jumpTo={`/upyog-ui/employee/asset/assetservice-up/inventory-creation/create`} />}
            />
            <Row
              label={t("INV_CATEGORY")}
              text={`${t(checkForNA(inventory?.item))}`}
              actionButton={<ActionButton jumpTo={`/upyog-ui/employee/asset/assetservice-up/inventory-creation/create`} />}
            />

            <Row
              label={t("INV_ITEM_NAME")}
              text={`${t(checkForNA(inventory?.itemType))}`}
              actionButton={<ActionButton jumpTo={`/upyog-ui/employee/asset/assetservice-up/inventory-creation/create`} />}
            />

            <Row
              label={t("INV_ITEM_DESCRIPTION")}
              text={`${t(checkForNA(inventory?.itemDescription))}`}
              actionButton={<ActionButton jumpTo={`/upyog-ui/employee/asset/assetservice-up/inventory-creation/create`} />}
            />
            <Row
              label={t("INV_ATTRIBUTES")}
              text={`${t(checkForNA(inventory?.attributes))}`}
              actionButton={<ActionButton jumpTo={`/upyog-ui/employee/asset/assetservice-up/inventory-creation/create`} />}
            />

            <Row
              label={t("INV_QUANTITY")}
              text={`${t(checkForNA(inventory?.quantity))}`}
              actionButton={<ActionButton jumpTo={`/upyog-ui/employee/asset/assetservice-up/inventory-creation/create`} />}
            />
            <Row
              label={t("INV_UAIN")}
              text={`${t(checkForNA(inventory?.assetApplicationNumber))}`}
              actionButton={<ActionButton jumpTo={`/upyog-ui/employee/asset/assetservice-up/inventory-creation/create`} />}
            />
            <Row
              label={t("INV_ENTRY_DATETIME")}
              text={`${t(checkForNA(inventory?.entryDatetime))}`}
              actionButton={<ActionButton jumpTo={`/upyog-ui/employee/asset/assetservice-up/inventory-creation/create`} />}
            />
            <Row
              label={t("INV_PURCHASE_DATE")}
              text={`${t(checkForNA(inventory?.purchaseDate))}`}
              actionButton={<ActionButton jumpTo={`/upyog-ui/employee/asset/assetservice-up/inventory-creation/create`} />}
            />
            <Row
              label={t("INV_PURCHASE_MODE")}
              text={`${t(checkForNA(inventory?.purchaseMode?.code))}`}
              actionButton={<ActionButton jumpTo={`/upyog-ui/employee/asset/assetservice-up/inventory-creation/create`} />}
            />
            <Row
              label={t("INV_VENDOR_DETAILS")}
              text={`${t(checkForNA(inventory?.vendorDetails?.code))}`}
              actionButton={<ActionButton jumpTo={`/upyog-ui/employee/asset/assetservice-up/inventory-creation/create`} />}
            />
            <Row
              label={t("INV_DELIVERY_DATE")}
              text={`${t(checkForNA(inventory?.deliveryDate))}`}
              actionButton={<ActionButton jumpTo={`/upyog-ui/employee/asset/assetservice-up/inventory-creation/create`} />}
            />
            <Row
              label={t("INV_END_OF_LIFE")}
              text={`${t(checkForNA(inventory?.endOfLife))}`}
              actionButton={<ActionButton jumpTo={`/upyog-ui/employee/asset/assetservice-up/inventory-creation/create`} />}
            />
            <Row
              label={t("INV_END_OF_SUPPORT")}
              text={`${t(checkForNA(inventory?.endOfSupport))}`}
              actionButton={<ActionButton jumpTo={`/upyog-ui/employee/asset/assetservice-up/inventory-creation/create`} />}
            />
            <Row
              label={t("INV_UNIT_PRICE")}
              text={`${t(checkForNA(inventory?.unitPrice))}`}
              actionButton={<ActionButton jumpTo={`/upyog-ui/employee/asset/assetservice-up/inventory-creation/create`} />}
            />
            <Row
              label={t("INV_TOTAL_PRICE")}
              text={`${t(checkForNA(inventory?.totalPrice))}`}
              actionButton={<ActionButton jumpTo={`/upyog-ui/employee/asset/assetservice-up/inventory-creation/create`} />}
            />
            <Row
              label={t("INV_INSURANCE_APPLICABILITY")}
              text={`${t(checkForNA(inventory?.insuranceApplicability?.code))}`}
              actionButton={<ActionButton jumpTo={`/upyog-ui/employee/asset/assetservice-up/inventory-creation/create`} />}
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

export default InventoryCheckPage;
