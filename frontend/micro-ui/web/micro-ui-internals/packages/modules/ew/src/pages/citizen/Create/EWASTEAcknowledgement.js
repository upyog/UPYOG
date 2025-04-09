// Importing necessary components and hooks from external libraries and local files
import { Banner, Card, CardText, LinkButton, LinkLabel, Loader, Row, StatusTable, SubmitBar } from "@nudmcdgnpm/digit-ui-react-components";
import React, { useEffect } from "react";
import { useTranslation } from "react-i18next"; // Hook for translations
import { Link, useRouteMatch } from "react-router-dom"; // React Router components for navigation
import getEwAcknowledgementData from "../../../utils/getEwAcknowledgementData"; // Utility function to fetch acknowledgment data
import { EWDataConvert } from "../../../utils"; // Utility function to convert E-Waste data

// Component to determine the action message to display in the banner
const GetActionMessage = (props) => {
  const { t } = useTranslation(); // Translation hook
  if (props.isSuccess) {
    // Message for successful application creation or update
    return !window.location.href.includes("edit-application") ? t("ES_EWASTE_RESPONSE_CREATE_ACTION") : t("CS_EWASTE_UPDATE_APPLICATION_SUCCESS");
  } else if (props.isLoading) {
    // Message for pending application creation or update
    return !window.location.href.includes("edit-application") ? t("CS_EWASTE_APPLICATION_PENDING") : t("CS_EWASTE_UPDATE_APPLICATION_PENDING");
  } else if (!props.isSuccess) {
    // Message for failed application creation or update
    return !window.location.href.includes("edit-application") ? t("CS_EWASTE_APPLICATION_FAILED") : t("CS_EWASTE_UPDATE_APPLICATION_FAILED");
  }
};

// Style object for row container
const rowContainerStyle = {
  padding: "4px 0px",
  justifyContent: "space-between",
};

// Component to render the banner with the appropriate message
const BannerPicker = (props) => {
  return (
    <Banner
      message={GetActionMessage(props)} // Displaying the action message
      applicationNumber={props.data?.EwasteApplication[0].requestId} // Displaying the application number
      info={props.isSuccess ? props.t("EWASTE_APPLICATION_NO") : ""} // Displaying additional info if successful
      successful={props.isSuccess} // Indicating success status
      style={{ width: "100%" }}
    />
  );
};

// Main component for the E-Waste acknowledgment page
const EWASTEAcknowledgement = ({ data, onSuccess }) => {
  const { t } = useTranslation(); // Translation hook

  // Fetching the tenant ID for the current user
  const tenantId = Digit.ULBService.getCitizenCurrentTenant(true) || Digit.ULBService.getCurrentTenantId();

  // Mutation hook to handle the E-Waste creation API call
  const mutation = Digit.Hooks.ew.useEWCreateAPI(data?.address?.city?.code);

  // Fetching initial store data
  const { data: storeData } = Digit.Hooks.useStore.getInitData();
  const { tenants } = storeData || {}; // Extracting tenants from store data

  // Effect to trigger the API call on component mount
  useEffect(() => {
    try {
      data.tenantId = tenantId; // Setting the tenant ID in the data
      let formdata = EWDataConvert(data); // Converting the data to the required format

      // Triggering the mutation with the converted data
      mutation.mutate(formdata, {
        onSuccess, // Callback for successful mutation
      });
    } catch (err) {
      // Handle errors if any
    }
  }, []);

  // Function to handle the download of the acknowledgment PDF
  const handleDownloadPdf = async () => {
    const { EwasteApplication = [] } = mutation.data; // Extracting the application data from the mutation response
    let EW = (EwasteApplication && EwasteApplication[0]) || {}; // Extracting the first application
    const tenantInfo = tenants.find((tenant) => tenant.code === EW.tenantId); // Finding the tenant info
    let tenantId = EW.tenantId || tenantId;

    // Fetching acknowledgment data and generating the PDF
    const data = await getEwAcknowledgementData({ ...EW }, tenantInfo, t);
    Digit.Utils.pdf.generateTable(data);
  };

  // Rendering the loader while the mutation is in progress
  return mutation.isLoading || mutation.isIdle ? (
    <Loader />
  ) : (
    <Card>
      {/* Rendering the banner with the appropriate message */}
      <BannerPicker t={t} data={mutation.data} isSuccess={mutation.isSuccess} isLoading={mutation.isIdle || mutation.isLoading} />

      {/* Rendering the status table */}
      <StatusTable>
        {mutation.isSuccess && (
          <Row
            rowContainerStyle={rowContainerStyle}
            last
            textStyle={{ whiteSpace: "pre", width: "60%" }}
          />
        )}
      </StatusTable>

      {/* Rendering the download acknowledgment button if the mutation is successful */}
      {mutation.isSuccess && <SubmitBar label={t("EWASTE_DOWNLOAD_ACK_FORM")} onSubmit={handleDownloadPdf} />}

      {/* Link to navigate back to the home page */}
      <Link to={`/digit-ui/citizen`}>
        <LinkButton label={t("CORE_COMMON_GO_TO_HOME")} />
      </Link>
    </Card>
  );
};

export default EWASTEAcknowledgement; // Exporting the component