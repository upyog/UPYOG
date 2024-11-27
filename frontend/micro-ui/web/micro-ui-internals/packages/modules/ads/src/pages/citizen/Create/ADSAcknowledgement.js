import { Banner, Card, Loader, Row, StatusTable, SubmitBar } from "@upyog/digit-ui-react-components";
import React, { useEffect } from "react";
import { useTranslation } from "react-i18next";
import { Link, useRouteMatch,useHistory } from "react-router-dom";
import { ADSDataConvert } from "../../../utils";

const GetActionMessage = (props) => {
  const { t } = useTranslation();
  if (props.isSuccess) {
    return !window.location.href.includes("editbookads") ? t("ES_ADS_RESPONSE_CREATE_ACTION") : t("CS_ADS_UPDATE_BOOKING_SUCCESS");
  } else if (props.isLoading) {
    return !window.location.href.includes("editbookads") ? t("CS_ADS_BOOKING_PENDING") : t("CS_ADS_UPDATE_BOOKING_PENDING");
  } else if (!props.isSuccess) {
    return !window.location.href.includes("editbookads") ? t("CS_ADS_BOOKING_FAILED") : t("CS_ADS_UPDATE_BOOKING_FAILED");
  }
};

const rowContainerStyle = {
  padding: "4px 0px",
  justifyContent: "space-between",
};
const BannerPicker = (props) => {
  return (
    <Banner
      message={GetActionMessage(props)}
      applicationNumber={props.data?.bookingApplication[0].bookingNo}
      info={props.isSuccess ? props.t("ADS_BOOKING_NO") : ""}
      successful={props.isSuccess}
      style={{ width: "100%" }}
    />
  );
};

/**
 * ADSAcknowledgement component displays the acknowledgment of an advertisement 
 * booking request. It shows the status of the booking operation, including 
 * success or failure messages.The component handles the mutation of 
 * booking data and manages loading states effectively.
 */
const ADSAcknowledgement = ({ data, onSuccess }) => {
  const { t } = useTranslation();

  const tenantId = Digit.ULBService.getCitizenCurrentTenant(true) || Digit.ULBService.getCurrentTenantId();
  const mutation = Digit.Hooks.ads.useADSCreateAPI(tenantId);
  const { data: storeData } = Digit.Hooks.useStore.getInitData();
  const match = useRouteMatch();
  const { tenants } = storeData || {};
  const history = useHistory();
  const user = Digit.UserService.getUser().info;
  const slotSearchData = Digit.Hooks.ads.useADSSlotSearch();
  let formdata = {
    advertisementSlotSearchCriteria: {
      bookingId:mutation.data?.bookingApplication[0].bookingId,
      addType: mutation.data?.bookingApplication[0]?.cartDetails?.[0]?.addType,
      bookingStartDate:mutation.data?.bookingApplication[0]?.cartDetails?.[0]?.bookingDate,
      bookingEndDate: mutation.data?.bookingApplication[0]?.cartDetails?.[mutation.data?.bookingApplication[0].cartDetails.length - 1]?.bookingDate,
      faceArea: mutation.data?.bookingApplication[0]?.cartDetails?.[0]?.faceArea,
      tenantId: tenantId,
      location: mutation.data?.bookingApplication[0]?.cartDetails?.[0]?.location,
      nightLight: mutation.data?.bookingApplication[0]?.cartDetails?.[0]?.nightLight,
      isTimerRequired: true
    }
  };
 
    const handleMakePayment = async () => {
      try {
        // Await the mutation and capture the result directly
        const result = await slotSearchData.mutateAsync(formdata);
        const isSlotBooked = result?.advertisementSlotAvailabiltityDetails?.some((slot) => slot.slotStaus === "BOOKED");
        const timerValue=result?.advertisementSlotAvailabiltityDetails[0].timerValue;
        if (isSlotBooked) {
          setShowToast({ error: true, label: t("ADS_ADVERTISEMENT_ALREADY_BOOKED") });
        } else {
          history.push({
            pathname: `/digit-ui/citizen/payment/my-bills/${"adv-services"}/${ mutation.data?.bookingApplication[0]?.bookingNo}`,
            state: { tenantId:tenantId, bookingNo: mutation.data?.bookingApplication[0]?.bookingNo, timerValue:timerValue },
          });
        }
    } catch (error) {
        console.error("Error making payment:", error);
    }
    };
  useEffect(() => {
    try {
      data.tenantId = tenantId;
      let formdata = ADSDataConvert(data);
      mutation.mutate(formdata, {
        onSuccess,
      });
    } catch (err) {}
  }, []);


  return mutation.isLoading || mutation.isIdle ? (
    <Loader />
  ) : (
    <Card>
      <BannerPicker t={t} data={mutation.data} isSuccess={mutation.isSuccess} isLoading={mutation.isIdle || mutation.isLoading} />
      <StatusTable>
        {mutation.isSuccess && <Row rowContainerStyle={rowContainerStyle} last textStyle={{ whiteSpace: "pre", width: "60%" }} />}
      </StatusTable>
      {mutation.isSuccess && (
      <div style={{ display: 'flex', flexDirection: 'row', gap: '20px' }}>
        {user.type==="EMPLOYEE" &&(<Link to={`/digit-ui/employee`}>
        <SubmitBar label={t("CORE_COMMON_GO_TO_HOME")} />
         </Link>)}
         {user.type==="CITIZEN" &&(<Link to={`/digit-ui/citizen`}>
        <SubmitBar label={t("CORE_COMMON_GO_TO_HOME")} />
         </Link>)}
        {user.type==="EMPLOYEE" &&(
         <Link to={`/digit-ui/employee/payment/collect/${"adv-services"}/${mutation.data?.bookingApplication[0].bookingNo}`}>
          <SubmitBar label={t("CS_APPLICATION_DETAILS_MAKE_PAYMENT")} />
          </Link> )}
          {user.type==="CITIZEN" &&(
            <SubmitBar label={t("CS_APPLICATION_DETAILS_MAKE_PAYMENT")} onSubmit={handleMakePayment} />)}
      </div>
    )}
    {!mutation.isSuccess && user.type==="CITIZEN" &&(
      <Link to={`/digit-ui/citizen`}>
      <SubmitBar label={t("CORE_COMMON_GO_TO_HOME")} />
       </Link>
     )}
     {!mutation.isSuccess && user.type==="EMPLOYEE" &&(
      <Link to={`/digit-ui/employee`}>
      <SubmitBar label={t("CORE_COMMON_GO_TO_HOME")} />
       </Link>
     )}
    </Card>
  );
};

export default ADSAcknowledgement;
