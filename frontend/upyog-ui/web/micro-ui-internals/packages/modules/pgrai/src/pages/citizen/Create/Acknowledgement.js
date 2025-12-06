import { Banner, Card, Loader, Row, StatusTable, SubmitBar,Toast } from "@upyog/digit-ui-react-components";
import React, {useState, useEffect } from "react";
import { useTranslation } from "react-i18next";
import { Link, useRouteMatch,useHistory } from "react-router-dom";
import { DataConvert } from "../../../utils";

const GetActionMessage = (props) => {
  const { t } = useTranslation();
  if (props.isSuccess) {
    return  t("PGR_AI_REQUEST_CREATED");
  } else if (props.isLoading) {
    return  t("CS_ PGR_AI_PENDING");
  } else if (!props.isSuccess) {
    return t("CS_ PGR_AI_FAILED");
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
      applicationNumber={props.data?.ServiceWrappers?.[0]?.service?.serviceRequestId}
      info={props.isSuccess ? props.t("PGR_AI_BOOKING_NO") : ""}
      successful={props.isSuccess}
      style={{ width: "100%" }}
    />
  );
};
/**
 * PGR_AIAcknowledgement component displays the acknowledgment of an advertisement 
 * booking request. It shows the status of the booking operation, including 
 * success or failure messages.The component handles the mutation of 
 * booking data and manages loading states effectively.
 */
const Acknowledgement = ({ data, onSuccess }) => {
  const { t } = useTranslation();

  const tenantId = Digit.ULBService.getCitizenCurrentTenant(true) || Digit.ULBService.getCurrentTenantId();
  const mutation = Digit.Hooks.pgrAi.useCreate(tenantId);
  const { data: storeData } = Digit.Hooks.useStore.getInitData();
  const match = useRouteMatch();
  const { tenants } = storeData || {};
  const history = useHistory();
  const user = Digit.UserService.getUser().info;
  const [showToast, setShowToast] = useState(null);

  useEffect(() => {
    try {
      data.tenantId = tenantId;
      let formdata = DataConvert(data,user);
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
        {user.type==="EMPLOYEE" &&(<Link to={`/upyog-ui/employee`}>
        <SubmitBar label={t("CORE_COMMON_GO_TO_HOME")} />
         </Link>)}
         {user.type==="CITIZEN" &&(<Link to={`/upyog-ui/citizen`}>
        <SubmitBar label={t("CORE_COMMON_GO_TO_HOME")} />
         </Link>)}
      </div>
    )}
    {!mutation.isSuccess && user.type==="CITIZEN" &&(
      <Link to={`/upyog-ui/citizen`}>
      <SubmitBar label={t("CORE_COMMON_GO_TO_HOME")} />
       </Link>
     )}
     {!mutation.isSuccess && user.type==="EMPLOYEE" &&(
      <Link to={`/upyog-ui/employee`}>
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

export default Acknowledgement;
