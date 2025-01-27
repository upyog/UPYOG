import { Banner, Card, Loader, Row, StatusTable, SubmitBar,Toast } from "@upyog/digit-ui-react-components";
import React, {useState, useEffect } from "react";
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
  const [showToast, setShowToast] = useState(null);
  const slotSearchData = Digit.Hooks.ads.useADSSlotSearch();
  let formdata = {
    advertisementSlotSearchCriteria: mutation.data?.bookingApplication[0]?.cartDetails.map((item) => ({
      bookingId: mutation.data?.bookingApplication[0].bookingId,
      addType: item?.addType,
      bookingStartDate: item?.bookingDate,
      bookingEndDate: item?.bookingDate,
      faceArea: item?.faceArea,
      tenantId: tenantId,
      location: item?.location,
      nightLight: item?.nightLight,
      isTimerRequired: true,
    })),
  };
 
    const handleMakePayment = async () => {
      try {
        // Await the mutation and capture the result directly
        const result = await slotSearchData.mutateAsync(formdata);
        let SlotSearchData={
          bookingId: mutation.data?.bookingApplication[0].bookingId,
          tenantId: tenantId,
          cartDetails: mutation.data?.bookingApplication[0]?.cartDetails,
        };
        const isSlotBooked = result?.advertisementSlotAvailabiltityDetails?.some((slot) => slot.slotStaus === "BOOKED");
        const timerValue=result?.advertisementSlotAvailabiltityDetails[0].timerValue;
        if (isSlotBooked) {
          setShowToast({ error: true, label: t("ADS_ADVERTISEMENT_ALREADY_BOOKED") });
        }else if(user.type==="CITIZEN") {
          history.push({
            pathname: `/digit-ui/citizen/payment/my-bills/${"adv-services"}/${ mutation.data?.bookingApplication[0]?.bookingNo}`,
            state: { tenantId:tenantId, bookingNo: mutation.data?.bookingApplication[0]?.bookingNo, timerValue:timerValue , SlotSearchData:SlotSearchData},
          });
        }
          else if(user.type==="EMPLOYEE") {
            history.push({
              pathname: `/digit-ui/employee/payment/collect/${"adv-services"}/${mutation.data?.bookingApplication[0]?.bookingNo}`,
              state: { tenantId:tenantId, bookingNo: mutation.data?.bookingApplication[0]?.bookingNo, timerValue:timerValue , SlotSearchData:SlotSearchData},
            });
          }
    } catch (error) {
      setShowToast({ error: true, label: t("CS_SOMETHING_WENT_WRONG") });
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

  
useEffect(() => {
    if (showToast) {
      const timer = setTimeout(() => {
        setShowToast(null);
      }, 2000); // Close toast after 2 seconds
      return () => clearTimeout(timer); // Clear timer on cleanup
    }
  }, [showToast]);
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
          <SubmitBar label={t("CS_APPLICATION_DETAILS_MAKE_PAYMENT")} onSubmit={handleMakePayment} />
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
     {showToast && (
             <Toast
               error={showToast.error}
               warning={showToast.warning}
               label={t(showToast.label)}
               onClose={() => {
                 setShowToast(null);
               }}
             />
      )}
    </Card>
  );
};

export default ADSAcknowledgement;
