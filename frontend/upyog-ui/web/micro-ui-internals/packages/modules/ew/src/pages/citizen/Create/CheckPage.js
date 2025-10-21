import {
  Card,
  CardHeader,
  CardSubHeader,
  CheckBox,
  LinkButton,
  Row,
  StatusTable,
  SubmitBar,
} from "@upyog/digit-ui-react-components";
import React, { useState } from "react";
import { useTranslation } from "react-i18next";
import { useHistory } from "react-router-dom";
import { checkForNA } from "../../../utils";
import Timeline from "../../../components/EWASTETimeline";
import ApplicationTable from "../../../components/inbox/ApplicationTable";

/**
 * Navigation button component for editing form sections
 * 
 * @param {Object} props Component properties
 * @param {string} props.jumpTo Target route path
 * @returns {JSX.Element} Link button for navigation
 */
const ActionButton = ({ jumpTo }) => {
  const { t } = useTranslation();
  const history = useHistory();
  function routeTo() {
    history.push(jumpTo);
  }

  return <LinkButton label={t("CS_COMMON_CHANGE")} className="check-page-link-button" onClick={routeTo} />;
};

/**
 * Review page for E-Waste collection request submission.
 * Displays all entered information for verification before final submission.
 * Includes product details, owner information, and address details with edit options.
 *
 * @param {Object} props Component properties
 * @param {Function} props.onSubmit Handler for form submission
 * @param {Object} props.value Form data containing address, owner, and product details
 * @returns {JSX.Element} Review page with form summary
 */
const CheckPage = ({ onSubmit, value = {} }) => {
  const { t } = useTranslation();
  const history = useHistory();

  const { address, ownerKey, ewdet } = value;

  /**
   * Configuration for product details table columns
   */
  const productcolumns = [
    { Header: t("PRODUCT_NAME"), accessor: "name" },
    { Header: t("PRODUCT_QUANTITY"), accessor: "quantity" },
    { Header: t("UNIT_PRICE"), accessor: "unit_price" },
    { Header: t("TOTAL_PRODUCT_PRICE"), accessor: "total_price" },
  ];

  /**
   * Transforms product data for table display
   */
  const productRows =
    ewdet?.prlistName?.map((product, index) => ({
      name: product.code,
      quantity: ewdet?.prlistQuantity[index].code,
      unit_price: product.price,
      total_price: parseFloat(
        (ewdet?.prlistQuantity[index].code * product.price).toFixed(2)
      ),
      })) || [];

  const [agree, setAgree] = useState(false);
  const setdeclarationhandler = () => {
    setAgree(!agree);
  };
  return (
    <React.Fragment>
      {/* Display the timeline if the user is on the citizen portal */}
      {window.location.href.includes("/citizen") ? <Timeline currentStep={5} /> : null}

      <Card>
        {/* Header for the check page */}
        <CardHeader>{t("EWASTE_CHECK_YOUR_DETAILS")}</CardHeader>
        <div>
          {/* Section for product details */}
          <CardSubHeader>{t("EWASTE_TITLE_PRODUCT_DETAILS")}</CardSubHeader>
          <div style={{ border: "2px solid #ccc", borderRadius: "8px", padding: "20px", margin: "20px 0" }}>
            <ApplicationTable
              t={t}
              data={productRows}
              columns={productcolumns}
              getCellProps={(cellInfo) => ({
                style: {
                  minWidth: "150px",
                  padding: "10px",
                  fontSize: "16px",
                  paddingLeft: "20px",
                },
              })}
              isPaginationRequired={false}
              totalRecords={productRows.length}
            />
            <br />
            {/* Displaying the net price */}
            <StatusTable style={{ marginLeft: "20px" }}>
              <Row
                label={t("EWASTE_NET_PRICE")}
                text={<div style={{ marginLeft: "295px" }}>{"₹ " + ewdet?.calculatedAmount}</div>}
                actionButton={<ActionButton jumpTo={`${`/upyog-ui/citizen/ew/raiseRequest/productdetails`}`} />}
              />
            </StatusTable>
          </div>

          {/* Section for owner details */}
          <CardSubHeader>{t("EWASTE_TITLE_OWNER_DETAILS")}</CardSubHeader>
          <br />
          <StatusTable>
            <Row
              label={t("EWASTE_APPLICANT_NAME")}
              text={`${t(checkForNA(ownerKey?.applicantName))}`}
              actionButton={<ActionButton jumpTo={`${`/upyog-ui/citizen/ew/raiseRequest/owner-details`}`} />}
            />
            <Row
              label={t("EWASTE_MOBILE_NUMBER")}
              text={`${t(checkForNA(ownerKey?.mobileNumber))}`}
              actionButton={<ActionButton jumpTo={`${`/upyog-ui/citizen/ew/raiseRequest/owner-details`}`} />}
            />
            <Row
              label={t("EWASTE_EMAIL_ID")}
              text={`${t(checkForNA(ownerKey?.emailId))}`}
              actionButton={<ActionButton jumpTo={`${`/upyog-ui/citizen/ew/raiseRequest/owner-details`}`} />}
            />
          </StatusTable>
          <br />

          {/* Section for address details */}
          <CardSubHeader>{t("EWASTE_TITLE_ADDRESS_DETAILS")}</CardSubHeader>
          <br />
          <StatusTable>
            <Row
              label={t("EWASTE_SEARCH_PINCODE")}
              text={`${t(checkForNA(address?.pincode))}`}
              actionButton={<ActionButton jumpTo={`${`/upyog-ui/citizen/ew/raiseRequest/pincode`}`} />}
            />
            <Row
              label={t("EWASTE_SEARCH_CITY")}
              text={`${t(checkForNA(address?.city?.name))}`}
              actionButton={<ActionButton jumpTo={`${`/upyog-ui/citizen/ew/raiseRequest/address`}`} />}
            />
            <Row
              label={t("EWASTE_SEARCH_STREET_NAME")}
              text={`${t(checkForNA(address?.street))}`}
              actionButton={<ActionButton jumpTo={`${`/upyog-ui/citizen/ew/raiseRequest/street`}`} />}
            />
            <Row
              label={t("EWASTE_SEARCH_HOUSE_NO")}
              text={`${t(checkForNA(address?.doorNo))}`}
              actionButton={<ActionButton jumpTo={`${`/upyog-ui/citizen/ew/raiseRequest/street`}`} />}
            />
            <Row
              label={t("EWASTE_SEARCH_HOUSE_NAME")}
              text={`${t(checkForNA(address?.buildingName))}`}
              actionButton={<ActionButton jumpTo={`${`/upyog-ui/citizen/ew/raiseRequest/street`}`} />}
            />
            <Row
              label={t("EWASTE_SEARCH_ADDRESS_LINE1")}
              text={`${t(checkForNA(address?.addressLine1))}`}
              actionButton={<ActionButton jumpTo={`${`/upyog-ui/citizen/ew/raiseRequest/street`}`} />}
            />
            <Row
              label={t("EWASTE_SEARCH_ADDRESS_LINE2")}
              text={`${t(checkForNA(address?.addressLine2))}`}
              actionButton={<ActionButton jumpTo={`${`/upyog-ui/citizen/ew/raiseRequest/street`}`} />}
            />
            <Row
              label={t("EWASTE_SEARCH_LANDMARK")}
              text={`${t(checkForNA(address?.landmark))}`}
              actionButton={<ActionButton jumpTo={`${`/upyog-ui/citizen/ew/raiseRequest/street`}`} />}
            />
          </StatusTable>
          <br />

          {/* Declaration checkbox */}
          <CheckBox label={t("EWASTE_FINAL_DECLARATION_MESSAGE")} onChange={setdeclarationhandler} styles={{ height: "auto" }} />
        </div>

        {/* Submit button */}
        <SubmitBar label={t("EWASTE_COMMON_BUTTON_SUBMIT")} onSubmit={onSubmit} disabled={!agree} />
      </Card>
    </React.Fragment>
  );
};

export default CheckPage; // Exporting the component
