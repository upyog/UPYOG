import { Card, CardHeader, CardSubHeader, CardText, CheckBox, LinkButton, Row, StatusTable, SubmitBar } from "@upyog/digit-ui-react-components";
import React, { useState, useEffect } from "react";
import { useTranslation } from "react-i18next";
import { useHistory } from "react-router-dom";
//import { VendorData } from "../../../utils"

import { checkForNA } from "../../../utils";
import Timeline from "../../../components/VENDORTimeline";

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
  const [categoriesWiseData, setCategoriesWiseData] = useState();

  //  * get @param city & state id
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const stateTenantId = Digit.ULBService.getStateId();

  const { vendordet, documents } = value;
  console.log("vendordetailssppsss", vendordet);
  console.log("documents", documents);
  const [agree, setAgree] = useState(false);
  const setdeclarationhandler = () => {
    setAgree(!agree);
  };

  return (
    <React.Fragment>
      {window.location.href.includes("/employee") ? <Timeline currentStep={5} /> : null}
      <Card>
        <CardHeader>{t("VENDOR_CHECK_DETAILS")}</CardHeader>
        <div>
          <br></br>

          <CardSubHeader>{t("VENDOR_ADDITIONAL_DETAILS")}</CardSubHeader>

          <StatusTable>
            <React.Fragment>
              <Row
                label={t("VENDOR_ID")}
                text={`${t(checkForNA(vendordet?.VendorId))}`}
                actionButton={<ActionButton jumpTo={`/upyog-ui/employee/vendor/registry/additionaldetails/vendor-details`} />}
              />
              <Row
                label={t("VENDOR_BANK_IFSC_CODE")}
                text={`${t(checkForNA(vendordet?.IFSC))}`}
                actionButton={<ActionButton jumpTo={`/upyog-ui/employee/vendor/registry/additionaldetails/vendor-details`} />}
              />
              <Row
                label={t("VENDOR_BANK_NAME")}
                text={`${t(checkForNA(vendordet?.Bank))}`}
                actionButton={<ActionButton jumpTo={`/upyog-ui/employee/vendor/registry/additionaldetails/vendor-details`} />}
              />
              <Row
                label={t("VENDOR_BANK_BRANCH")}
                text={`${t(checkForNA(vendordet?.BankbranchName))}`}
                actionButton={<ActionButton jumpTo={`/upyog-ui/employee/vendor/registry/additionaldetails/vendor-details`} />}
              />
              <Row
                label={t("VENDOR_MICR_NO")}
                text={`${t(checkForNA(vendordet?.micrNo))}`}
                actionButton={<ActionButton jumpTo={`/upyog-ui/employee/vendor/registry/additionaldetails/vendor-details`} />}
              />
              <Row
                label={t("BANK_ACCONT_NO")}
                text={`${t(checkForNA(vendordet?.AccountNo))}`}
                actionButton={<ActionButton jumpTo={`/upyog-ui/employee/vendor/registry/additionaldetails/vendor-details`} />}
              />

              {/* <Row
                label={t("PHONE_NO")}
                text={`${t(checkForNA(vendordet?.AccountNo))}`}
                actionButton={<ActionButton jumpTo={`/upyog-ui/employee/vendor/registry/additionaldetails/vendor-details`} />}
              />

              <Row
                label={t("CONTACT_PERSON")}
                text={`${t(checkForNA(vendordet?.AccountNo))}`}
                actionButton={<ActionButton jumpTo={`/upyog-ui/employee/vendor/registry/additionaldetails/vendor-details`} />}
              />

              <Row
                label={t("COMPANY_NAME")}
                text={`${t(checkForNA(vendordet?.AccountNo))}`}
                actionButton={<ActionButton jumpTo={`/upyog-ui/employee/vendor/registry/additionaldetails/vendor-details`} />}
              /> */}

              <Row
                label={t("PAN_NO")}
                text={`${t(checkForNA(vendordet?.PanNo))}`}
                actionButton={<ActionButton jumpTo={`/upyog-ui/employee/vendor/registry/additionaldetails/vendor-details`} />}
              />
              <Row
                label={t("GST_NO")}
                text={`${t(checkForNA(vendordet?.GstNo))}`}
                actionButton={<ActionButton jumpTo={`/upyog-ui/employee/vendor/registry/additionaldetails/vendor-details`} />}
              />
              <Row
                label={t("GST_REGISTERED_STATE/UT")}
                text={`${t(checkForNA(vendordet?.GstState))}`}
                actionButton={<ActionButton jumpTo={`/upyog-ui/employee/vendor/registry/additionaldetails/vendor-detailss`} />}
              />
              <Row
                label={t("REGISTRATION_NO")}
                text={`${t(checkForNA(vendordet?.RegistrationNo))}`}
                actionButton={<ActionButton jumpTo={`/upyog-ui/employee/vendor/registry/additionaldetails/vendor-details`} />}
              />

              <Row
                label={t("EPF_NO")}
                text={`${t(checkForNA(vendordet?.EpfNo))}`}
                actionButton={<ActionButton jumpTo={`/upyog-ui/employee/vendor/registry/additionaldetails/vendor-details`} />}
              />

              <Row
                label={t("ESI_NO")}
                text={`${t(checkForNA(vendordet?.EsiNo))}`}
                actionButton={<ActionButton jumpTo={`/upyog-ui/employee/vendor/registry/additionaldetails/vendor-details`} />}
              />

              <Row
                label={t("VENDOR_TYPE")}
                text={`${t(checkForNA(vendordet?.VendorType?.code))}`}
                actionButton={<ActionButton jumpTo={`/upyog-ui/employee/vendor/registry/additionaldetails/vendor-details`} />}
              />

              <Row
                label={t("VENDOR_CATEGORY")}
                text={`${t(checkForNA(vendordet?.VendorCategory?.code))}`}
                actionButton={<ActionButton jumpTo={`/upyog-ui/employee/vendor/registry/additionaldetails/vendor-details`} />}
              />

              <Row
                label={t("VENDOR_STATUS")}
                text={`${t(checkForNA(vendordet?.Status?.code))}`}
                actionButton={<ActionButton jumpTo={`/upyog-ui/employee/vendor/registry/additionaldetails/vendor-details`} />}
              />
              {/* {formJson.map((row, index) => (
                <Row
                  key={index}
                  label={t(row.code)}
                  text={`${extractValue(row.name)}`}
                  actionButton={<ActionButton jumpTo={`/upyog-ui/employee/vendor/registry/additionaldetails/vendor-details`} />}
                />
              ))} */}
            </React.Fragment>
          </StatusTable>

          <br></br>
          <CardSubHeader>{t("VENDOR_DOCUMENTS_DETAILS")}</CardSubHeader>

          <StatusTable>
            <Row
              label={t("VENDOR_DOCUMENTS")}
              text={
                documents?.documents?.length > 0
                  ? documents.documents.map((doc, index) => (
                      <div key={index}>
                        <a
                          href={`/upyog-ui/employee/vendor/registry/additionaldetails/documents/${doc.filestoreId}`}
                          target="_blank"
                          rel="noopener noreferrer"
                        >
                          {t(doc.documentType)}
                        </a>
                      </div>
                    ))
                  : t("CS_NA")
              }
            />
          </StatusTable>

          <br></br>

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
