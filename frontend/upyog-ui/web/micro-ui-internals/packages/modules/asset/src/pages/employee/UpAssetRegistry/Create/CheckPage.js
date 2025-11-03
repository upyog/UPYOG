import { Card, CardHeader, CardSubHeader, CardText, CheckBox, LinkButton, Row, StatusTable, SubmitBar } from "@upyog/digit-ui-react-components";
import React, { useState, useEffect } from "react";
import { useTranslation } from "react-i18next";
import { useHistory } from "react-router-dom";

import { checkForNA } from "../../../../utils";
import Timeline from "../../../../components/ASTTimeLineUp";

const ActionButton = ({ jumpTo }) => {
  const { t } = useTranslation();
  const history = useHistory();
  function routeTo() {
    history.push(jumpTo);
  }

  return <LinkButton label={t("CS_COMMON_CHANGE")} className="check-page-link-button" onClick={routeTo} />;
};

const CheckPage = ({ onSubmit, value = {} }) => {
  const { t } = useTranslation();
  const history = useHistory();
  const [agree, setAgree] = useState(false);
  const [categoriesWiseData, setCategoriesWiseData] = useState();

  //  * get @param city & state id
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const stateTenantId = Digit.ULBService.getStateId();

  // This call with stateTenantId (Get state-level data)
  const stateResponseObject = Digit.Hooks.useCustomMDMS(stateTenantId, "ASSET", [{ name: "AssetParentCategoryFields" }], {
    select: (data) => {
      const formattedData = data?.["ASSET"]?.["AssetParentCategoryFields"];
      return formattedData;
    },
  });

  const {assetDetails, index, isEdit, isUpdate } = value;

  const typeOfApplication = !isEdit && !isUpdate ? `new-application` : `edit-application`;

  const setdeclarationhandler = () => {
    setAgree(!agree);
  };
  useEffect(() => {
    let combinedData;
    // if city level master is not available then fetch  from state-level
    if (stateResponseObject?.data) {
      combinedData = stateResponseObject.data;
    } else {
      combinedData = [];
    }
    setCategoriesWiseData(combinedData);
  }, [stateResponseObject]);

  let formJson = [];
  if (Array.isArray(categoriesWiseData)) {
    formJson = categoriesWiseData
      .filter((category) => {
        const isMatch = category.assetParentCategory === assetDetails?.assettype?.code;
        return isMatch;
      })
      .map((category) => category.fields) // Extract the fields array
      .flat() // Flatten the fields array
      .filter((field) => field.active === true); // Filter by active status
  }

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

          <CardSubHeader>{t("ADD_ASSET_REGISTRY_DETAILS")}</CardSubHeader>
          <StatusTable>
            <Row
              label={t("AST_PARENT_CATEGORY_LV1")}
              text={`${t(checkForNA(assetDetails?.assetclassification?.code))}`}
              actionButton={<ActionButton jumpTo={`/upyog-ui/employee/asset/assetservice-up/asset-registry/create`} />}
            />
            <Row
              label={t("AST_PARENT_CATEGORY_LV2")}
              text={`${t(checkForNA(assetDetails?.assettype?.code))}`}
              actionButton={<ActionButton jumpTo={`/upyog-ui/employee/asset/assetservice-up/asset-registry/create`} />}
            />
            <Row
              label={t("AST_PARENT_CATEGORY_LV3")}
              text={`${t(checkForNA(assetDetails?.assetsubtype?.code))}`}
              actionButton={<ActionButton jumpTo={`/upyog-ui/employee/asset/assetservice-up/asset-registry/create`} />}
            />

            <Row
              label={t("AST_PARENT_CATEGORY_LV4")}
              text={`${t(checkForNA(assetDetails?.otherCategory))}`}
              actionButton={<ActionButton jumpTo={`/upyog-ui/employee/asset/assetservice-up/asset-registry/create`} />}
            />

            <Row
              label={t("AST_NAME")}
              text={`${t(checkForNA(assetDetails?.assetName))}`}
              actionButton={<ActionButton jumpTo={`/upyog-ui/employee/asset/assetservice-up/asset-registry/create`} />}
            />
            <Row
              label={t("ASSET_DESCRIPTION")}
              text={`${t(checkForNA(assetDetails?.assetDescription))}`}
              actionButton={<ActionButton jumpTo={`/upyog-ui/employee/asset/assetservice-up/asset-registry/create`} />}
            />

            <Row
              label={t("AST_ACQUISITION_COST")}
              text={`${t(checkForNA(assetDetails?.acquisitionCost))}`}
              actionButton={<ActionButton jumpTo={`/upyog-ui/employee/asset/assetservice-up/asset-registry/create`} />}
            />
            <Row
              label={t("AST_ACQUISITION_DATE")}
              text={`${t(checkForNA(assetDetails?.acquisitionDate))}`}
              actionButton={<ActionButton jumpTo={`/upyog-ui/employee/asset/assetservice-up/asset-registry/create`} />}
            />
          
          </StatusTable>
          <br></br>

          <CardSubHeader>{t("AST_DETAILS")}</CardSubHeader>
          <StatusTable>
            <React.Fragment>
              {formJson.map((row, index) => (
                <Row
                  key={index}
                  label={t(row.code+ "_INFO")}
                  text={`${extractValue(row.name)}`}
                  actionButton={<ActionButton jumpTo={`/upyog-ui/employee/asset/assetservice-up/asset-registry/create`} />}
                />
              ))}
            </React.Fragment>
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

export default CheckPage;
