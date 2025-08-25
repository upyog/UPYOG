import { Banner, Card, CardText, LinkButton, LinkLabel, Loader, Row, StatusTable, SubmitBar,Toast } from "@upyog/digit-ui-react-components";
import React, {useState, useEffect } from "react";
import { useTranslation } from "react-i18next";
import { Link, useRouteMatch,useHistory } from "react-router-dom";
import { CHBDataConvert } from "../../../utils";


/**
 * CHBMyApplications Component
 * 
 * This component is responsible for rendering the "My Applications" page for citizens in the CHB module.
 * It allows users to view and search through their submitted applications, with options to filter and sort the results.
 * 
 * Hooks:
 * - `useTranslation`: Provides the `t` function for internationalization.
 * - `Digit.ULBService.getCitizenCurrentTenant`: Fetches the current tenant ID for the citizen.
 * - `Digit.UserService.getUser`: Fetches the current user's information.
 * - `Digit.Hooks.chb.useChbSearch`: Custom hook to fetch application data based on the applied filters.
 * 
 * State Variables:
 * - `searchTerm`: State variable to manage the search input value.
 * - `status`: State variable to manage the selected status filter.
 * - `filters`: State variable to manage the dynamic filters applied to the search query.
 * 
 * Variables:
 * - `filter`: Extracted from the URL to determine the offset or other filter criteria.
 * - `t1`: Calculated limit for the number of results to fetch based on the `filter`.
 * - `off`: Offset value for pagination, derived from the `filter`.
 * - `initialFilters`: Object containing the default filters for the search query, including limit, sort order, sort by, offset, and tenant ID.
 * 
 * Effects:
 * - `useEffect`: Updates the `filters` state whenever the `filter` value changes in the URL.
 * 
 * Logic:
 * - Determines the initial filters based on the URL's last segment.
 * - Uses the `useChbSearch` hook to fetch application data dynamically based on the `filters` state.
 * 
 * Returns:
 * - A page displaying the user's applications, with search and filter functionality.
 * - Displays a loader while the data is being fetched.
 */
const GetActionMessage = (props) => {
  const { t } = useTranslation();
  if (props.isSuccess) {
    return !window.location.href.includes("editbookHall") ? t("ES_CHB_RESPONSE_CREATE_ACTION") : t("CS_CHB_UPDATE_BOOKING_SUCCESS");
  } else if (props.isLoading) {
    return !window.location.href.includes("editbookHall") ? t("CS_CHB_BOOKING_PENDING") : t("CS_CHB_UPDATE_BOOKING_PENDING");
  } else if (!props.isSuccess) {
    return !window.location.href.includes("editbookHall") ? t("CS_CHB_BOOKING_FAILED") : t("CS_CHB_UPDATE_BOOKING_FAILED");
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
      applicationNumber={props.data?.hallsBookingApplication[0].bookingNo}
      info={props.isSuccess ? props.t("CHB_BOOKING_NO") : ""}
      successful={props.isSuccess}
      style={{ width: "100%" }}
    />
  );
};

const CHBAcknowledgement = ({ data, onSuccess }) => {
  const { t } = useTranslation();
  const history = useHistory();
  const tenantId = Digit.ULBService.getCitizenCurrentTenant(true) || Digit.ULBService.getCurrentTenantId();
  const mutation = Digit.Hooks.chb.useChbCreateAPI(tenantId);
  const { data: storeData } = Digit.Hooks.useStore.getInitData();
  const match = useRouteMatch();
  const { tenants } = storeData || {};
  const user = Digit.UserService.getUser().info;
  const [showToast, setShowToast] = useState(null);
  const { data: slotSearchData, refetch } = Digit.Hooks.chb.useChbSlotSearch({
    tenantId:tenantId,
    filters: {
      bookingId:mutation.data?.hallsBookingApplication[0].bookingId,
      communityHallCode: mutation.data?.hallsBookingApplication[0].communityHallCode,
      bookingStartDate: mutation.data?.hallsBookingApplication[0]?.bookingSlotDetails?.[0]?.bookingDate,
      bookingEndDate: mutation.data?.hallsBookingApplication[0]?.bookingSlotDetails?.[mutation.data?.hallsBookingApplication[0].bookingSlotDetails.length - 1]?.bookingDate,
      hallCode:mutation.data?.hallsBookingApplication[0]?.bookingSlotDetails?.[0]?.hallCode,
      isTimerRequired:true,
    },
    enabled: false, // Disable automatic refetch
  });
  const handleMakePayment = async () => {
    try{
    const result = await refetch();
    let SlotSearchData={
      tenantId:tenantId,
      bookingId:mutation.data?.hallsBookingApplication[0].bookingId,
      communityHallCode: mutation.data?.hallsBookingApplication[0].communityHallCode,
      bookingStartDate: mutation.data?.hallsBookingApplication[0]?.bookingSlotDetails?.[0]?.bookingDate,
      bookingEndDate: mutation.data?.hallsBookingApplication[0]?.bookingSlotDetails?.[mutation.data?.hallsBookingApplication[0].bookingSlotDetails.length - 1]?.bookingDate,
      hallCode:mutation.data?.hallsBookingApplication[0]?.bookingSlotDetails?.[0]?.hallCode,
      isTimerRequired:true,
    };
    const isSlotBooked = result?.data?.hallSlotAvailabiltityDetails?.some(
      (slot) => slot.slotStaus === "BOOKED"
    );

    if (isSlotBooked) {
      setShowToast({ error: true, label: t("CHB_COMMUNITY_HALL_ALREADY_BOOKED") });
    } else if(user.type==="CITIZEN") {
      history.push({
        pathname: `/digit-ui/citizen/payment/my-bills/${"chb-services"}/${mutation.data?.hallsBookingApplication[0].bookingNo}`,
        state: { tenantId:tenantId, bookingNo: mutation.data?.hallsBookingApplication[0].bookingNo,timerValue:result?.data?.timerValue,SlotSearchData:SlotSearchData },
      });
    }
      else if(user.type==="EMPLOYEE") {
        history.push({
          pathname: `/digit-ui/employee/payment/collect/${"chb-services"}/${mutation.data?.hallsBookingApplication[0].bookingNo}`,
          state: { tenantId:tenantId, bookingNo: mutation.data?.hallsBookingApplication[0].bookingNo,timerValue:result?.data?.timerValue,SlotSearchData:SlotSearchData },
        });
      }
  }catch (error) {
    setShowToast({ error: true, label: t("CS_SOMETHING_WENT_WRONG") });
  }
  };
  useEffect(() => {
    try {
      data.tenantId = tenantId;
      let formdata = CHBDataConvert(data);
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
        <SubmitBar label={t("CS_APPLICATION_DETAILS_MAKE_PAYMENT")} onSubmit={handleMakePayment}/>
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

export default CHBAcknowledgement;
