import { Banner, Card, CardText, LinkButton, LinkLabel, Loader, Row, StatusTable, SubmitBar } from "@upyog/digit-ui-react-components";
import React, { useEffect } from "react";
import { useTranslation } from "react-i18next";
import { Link } from "react-router-dom";
import { treePruningPayload, APPLICATION_PATH } from "../../../utils";
import getTPAcknowledgementData from "../../../utils/getTPAcknowledgementData";

/* This component, MTAcknowledgement, is responsible for displaying the acknowledgement 
 of a service request submission. 

 Key functionalities include:
 1. **Data Submission**: On component mount (using useEffect), it prepares the data 
    by extracting the tenantId from the address and formats it using the `svPayloadData` 
    utility function. It then calls the `useSvCreateApi` hook to submit this data.
 2. **Loading State**: While the data submission is in progress, a Loader component 
    is displayed to indicate that the request is being processed.
 3. **Acknowledgement Banner**: Once the data submission is complete, a Banner 
    component is rendered to show the result of the submission, which can either 
    indicate success or failure based on the response from the API.
 4. **Navigation**: A LinkButton is provided to navigate back to the home page 
    after the acknowledgement is displayed.
*/

const GetActionMessage = (props) => {
    const { t } = useTranslation();
    if (props.isSuccess) {
      return t("TP_SUBMIT_SUCCESSFULL");
    }
    else if (props.isLoading){
      return t("TP_APPLICATION_PENDING");
    }
    else if (!props.isSuccess)
    return t("TP_APPLICATION_FAILED");
  };


//style object to pass inside row container which shows the application ID and status of application of banner image
const rowContainerStyle = {
  padding: "4px 0px",
  justifyContent: "space-between",
};

const BannerPicker = (props) => {
  console.log("BannerPicker",props);
  return (
    <Banner
      message={GetActionMessage(props)}
      applicationNumber={props.data?.treePruningBookingDetail?.bookingNo}
      info={props.isSuccess ? props.t("TP_BOOKING_NO") : ""}
      successful={props.isSuccess}
      style={{width: "100%"}}
    />
  );
};

const TPAcknowledgement = ({ data, onSuccess }) => {
  const { t } = useTranslation();
  const tenantId = Digit.ULBService.getCitizenCurrentTenant(true) || Digit.ULBService.getCurrentTenantId();
  const mutation = Digit.Hooks.wt.useTreePruningCreateAPI(tenantId); 
  const user = Digit.UserService.getUser().info;
  const { data: storeData } = Digit.Hooks.useStore.getInitData();
  const { tenants } = storeData || {};
 
  useEffect(() => {
    try {
      data.tenantId = tenantId;
      // if()
      let formdata = treePruningPayload(data);
      mutation.mutate(formdata, {onSuccess});
    } catch (err) {
    }
  }, []);

  /*custom hook to prevent going back in Acknowledgement /success response page
  * if you click Back then it will redirect you to Home page 
  */
  Digit.Hooks.useCustomBackNavigation({
    redirectPath: '${APPLICATION_PATH}/citizen'
  })
  
    /**
     * Handles the generation and download of the Mobile Toilet Acknowledgement PDF.
     * 
     * - Fetches the mobile toilet booking details from the mutation response.
     * - Retrieves the tenant information based on the tenant ID from the booking details.
     * - Prepares the acknowledgement data using the `getTPAcknowledgementData` utility function.
     * - Generates and downloads the PDF using the prepared data.
     */
  const handleDownloadPdf = async () => {
    let treePruningDetail = mutation.data?.treePruningBookingDetail;
    const tenantInfo = tenants.find((tenant) => tenant.code === treePruningDetail.tenantId);
    let tenantId = treePruningDetail.tenantId || tenantId;
    const data = await getTPAcknowledgementData({...treePruningDetail }, tenantInfo, t);
    Digit.Utils.pdf.generate(data);
  };

  return mutation.isLoading || mutation.isIdle ? (
    <Loader />
  ) : (
    <Card>
      <BannerPicker t={t} data={mutation.data} isSuccess={mutation.isSuccess} isLoading={mutation.isIdle || mutation.isLoading} />
      <StatusTable>
        {mutation.isSuccess && (
          <Row
            rowContainerStyle={rowContainerStyle}
            last       
            textStyle={{ whiteSpace: "pre", width: "60%" }}
          />
        )}
      </StatusTable>
      {mutation.isSuccess && <SubmitBar label={t("TP_DOWNLOAD_ACKNOWLEDGEMENT")} onSubmit={handleDownloadPdf} />}
      {user?.type==="CITIZEN"?
      <Link to={`${APPLICATION_PATH}/citizen`}>
        <LinkButton label={t("CORE_COMMON_GO_TO_HOME")} />
      </Link>
      :
      <Link to={`${APPLICATION_PATH}/employee`}>
        <LinkButton label={t("CORE_COMMON_GO_TO_HOME")} />
      </Link>}
    </Card>
  );
};

export default TPAcknowledgement;