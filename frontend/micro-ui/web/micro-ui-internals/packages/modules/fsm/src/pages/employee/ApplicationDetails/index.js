import React, { Fragment, useEffect, useState } from "react";
import { useTranslation } from "react-i18next";

import {
  BreakLine,
  Card,
  CardSubHeader,
  StatusTable,
  Row,
  SubmitBar,
  Loader,
  CardSectionHeader,
  ConnectingCheckPoints,
  CheckPoint,
  ActionBar,
  Menu,
  LinkButton,
  Toast,
  Rating,
  ActionLinks,
  Header,
  ImageViewer,
} from "@upyog/digit-ui-react-components";

import ActionModal from "./Modal";
import TLCaption from "../../../components/TLCaption";

import { useQueryClient } from "react-query";

import { Link, useHistory, useParams } from "react-router-dom";
import { ViewImages } from "../../../components/ViewImages";

const ApplicationDetails = (props) => {
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const state = Digit.ULBService.getStateId();
  const { t } = useTranslation();
  const history = useHistory();
  const queryClient = useQueryClient();
  let { id: applicationNumber } = useParams();
  const [displayMenu, setDisplayMenu] = useState(false);
  const [selectedAction, setSelectedAction] = useState(null);
  const [config, setCurrentConfig] = useState({});
  const [showModal, setShowModal] = useState(false);
  const [showToast, setShowToast] = useState(null);
  const [imageZoom, setImageZoom] = useState(null);
  const DSO = Digit.UserService.hasAccess(["FSM_DSO"]) || false;

  const { isLoading, isError, data: applicationDetails, error } = Digit.Hooks.fsm.useApplicationDetail(
    t,
    tenantId,
    applicationNumber,
    {},
    props.userType
  );
  const { isLoading: isDataLoading, isSuccess, data: applicationData } = Digit.Hooks.fsm.useSearch(
    tenantId,
    { applicationNos: applicationNumber },
    { staleTime: Infinity }
  );

  const {
    isLoading: updatingApplication,
    isError: updateApplicationError,
    data: updateResponse,
    error: updateError,
    mutate,
  } = Digit.Hooks.fsm.useApplicationActions(tenantId);

  const workflowDetails = Digit.Hooks.useWorkflowDetails({
    tenantId: applicationDetails?.tenantId || tenantId,
    id: applicationNumber,
    moduleCode:
      applicationData?.paymentPreference === "POST_PAY"
        ? "FSM_POST_PAY_SERVICE"
        : applicationData?.advanceAmount === 0
        ? "PAY_LATER_SERVICE"
        : applicationData?.advanceAmount > 0
        ? "FSM_ADVANCE_PAY_SERVICE"
        : applicationData?.paymentPreference === null &&
          applicationData?.additionalDetails?.tripAmount === 0 &&
          applicationData?.advanceAmount === null
        ? "FSM_ZERO_PAY_SERVICE"
        : "FSM",
    role: DSO ? "FSM_DSO" : "FSM_EMPLOYEE",
    serviceData: applicationDetails,
    getTripData: DSO ? false : true,
  });

  useEffect(() => {
    if (showToast) {
      workflowDetails.revalidate();
    }
  }, [showToast]);

  function onActionSelect(action) {
    setSelectedAction(action);
    setDisplayMenu(false);
  }

  useEffect(() => {
    switch (selectedAction) {
      case DSO && "SCHEDULE":
      case "DSO_ACCEPT":
      case "ACCEPT":
      case "ASSIGN":
      case "GENERATE_DEMAND":
      case "FSM_GENERATE_DEMAND":
      case "REASSIGN":
      case "COMPLETE":
      case "COMPLETED":
      case "CANCEL":
      case "SENDBACK":
      case "DSO_REJECT":
      case "REJECT":
      case "DECLINE":
      case "REASSING":
      case "UPDATE":
        return setShowModal(true);
      case "SUBMIT":
      case "FSM_SUBMIT":
      case !DSO && "SCHEDULE":
        return history.push("/digit-ui/employee/fsm/modify-application/" + applicationNumber);
      case "PAY":
      case "FSM_PAY":
      case "ADDITIONAL_PAY_REQUEST":
        return history.push(`/digit-ui/employee/payment/collect/FSM.TRIP_CHARGES/${applicationNumber}?workflow=FSM`);
      default:
        break;
    }
  }, [selectedAction]);

  const closeModal = () => {
    setSelectedAction(null);
    setShowModal(false);
  };

  const closeToast = () => {
    setShowToast(null);
  };

  const submitAction = (data) => {
    mutate(data, {
      onError: (error, variables) => {
        setShowToast({ key: "error", action: error });
        setTimeout(closeToast, 5000);
      },
      onSuccess: (data, variables) => {
        setShowToast({ key: "success", action: selectedAction });
        setTimeout(closeToast, 5000);
        queryClient.invalidateQueries("FSM_CITIZEN_SEARCH");
        const inbox = queryClient.getQueryData("FUNCTION_RESET_INBOX");
        inbox?.revalidate();
      },
    });
    closeModal();
  };

  function zoomImageWrapper(imageSource, index) {
    setImageZoom(imageSource);
  }

  function onCloseImageZoom() {
    setImageZoom(null);
  }

  const getTimelineCaptions = (checkpoint) => {
    const __comment = checkpoint?.comment?.split("~");
    const reason = __comment ? __comment[0] : null;
    const reason_comment = __comment ? __comment[1] : null;
    if (checkpoint.status === "CREATED") {
      const caption = {
        date: checkpoint?.auditDetails?.created,
        name: checkpoint?.assigner,
        mobileNumber: applicationData?.citizen?.mobileNumber,
        source: applicationData?.source || "",
      };
      return <TLCaption data={caption} />;
    } else if (
      checkpoint.status === "PENDING_APPL_FEE_PAYMENT" ||
      checkpoint.status === "DSO_REJECTED" ||
      checkpoint.status === "CANCELED" ||
      checkpoint.status === "REJECTED"
    ) {
      const caption = {
        date: checkpoint?.auditDetails?.created,
        name: checkpoint?.assigner,
        comment: reason ? t(`ES_ACTION_REASON_${reason}`) : null,
        otherComment: reason_comment ? reason_comment : null,
      };
      return <TLCaption data={caption} />;
    } else if (checkpoint.status === "DSO_INPROGRESS") {
      const caption = {
        name: checkpoint?.assigner,
        mobileNumber: checkpoint?.assigner?.mobileNumber,
        date: `${t("CS_FSM_EXPECTED_DATE")} ${Digit.DateUtils.ConvertTimestampToDate(applicationData?.possibleServiceDate)}`,
      };
      return <TLCaption data={caption} />;
    } else if (checkpoint.status === "COMPLETED") {
      return (
        <div>
          <Rating withText={true} text={t(`ES_FSM_YOU_RATED`)} currentRating={checkpoint.rating} />
          <Link to={`/digit-ui/employee/fsm/rate-view/${applicationNumber}`}>
            <ActionLinks>{t("CS_FSM_RATE_VIEW")}</ActionLinks>
          </Link>
        </div>
      );
    } else if (
      checkpoint.status === "WAITING_FOR_DISPOSAL" ||
      checkpoint.status === "DISPOSAL_IN_PROGRESS" ||
      checkpoint.status === "DISPOSED" ||
      checkpoint.status === "CITIZEN_FEEDBACK_PENDING"
    ) {
      const caption = {
        date: checkpoint?.auditDetails?.created,
        name: checkpoint?.assigner,
        mobileNumber: checkpoint?.assigner?.mobileNumber,
      };
      if (checkpoint?.numberOfTrips) caption.comment = `${t("NUMBER_OF_TRIPS")}: ${checkpoint?.numberOfTrips}`;
      return <TLCaption data={caption} />;
    }
  };

  if (isLoading) {
    return <Loader />;
  }

  return (
    <React.Fragment>
      {!isLoading ? (
        <React.Fragment>
          <Header style={{ marginBottom: "16px" }}>{t("ES_TITLE_APPLICATION_DETAILS")}</Header>
          <Card className="fsm" style={{ position: "relative" }}>
            {/* {!DSO && (
              <LinkButton
                label={<span style={{ color: "#0f4f9e", marginLeft: "8px" }}>{t("ES_APPLICATION_DETAILS_VIEW_AUDIT_TRAIL")}</span>}
                style={{ position: "absolute", top: 0, right: 20 }}
                onClick={() => {
                  history.push(props.parentRoute + "/application-audit/" + applicationNumber);
                }}
              />
            )} */}
            {applicationDetails?.applicationDetails.map((detail, index) => (
              <React.Fragment key={index}>
                {index === 0 ? null : ( // <CardSubHeader style={{ marginBottom: "16px" }}>{t(detail.title)}</CardSubHeader>
                  <CardSectionHeader style={{ marginBottom: "16px", marginTop: "32px" }}>{t(detail.title)}</CardSectionHeader>
                )}
                <StatusTable>
                  {detail?.values?.map((value, index) => {
                    if (value === null) return;
                    if (value.map === true && value.value !== "N/A") {
                      return <Row key={t(value.title)} label={t(value.title)} text={<img src={t(value.value)} alt="" />} />;
                    }
                    return (
                      <Row
                        key={t(value.title)}
                        label={t(value.title)}
                        text={t(value.value) || "N/A"}
                        last={index === detail?.values?.length - 1}
                        caption={value.caption}
                        className="border-none"
                      />
                    );
                  })}
                </StatusTable>
              </React.Fragment>
            ))}
            {applicationData?.pitDetail?.additionalDetails?.fileStoreId?.CITIZEN?.length && (
              <>
                <CardSectionHeader style={{ marginBottom: "16px", marginTop: "32px" }}>{t("ES_FSM_SUB_HEADING_CITIZEN_UPLOADS")}</CardSectionHeader>
                <ViewImages
                  fileStoreIds={applicationData?.pitDetail?.additionalDetails?.fileStoreId?.CITIZEN}
                  tenantId={state}
                  onClick={(source, index) => zoomImageWrapper(source, index)}
                />
              </>
            )}
            {applicationData?.pitDetail?.additionalDetails?.fileStoreId?.FSM_DSO?.length && (
              <>
                <CardSectionHeader style={{ marginBottom: "16px", marginTop: "32px" }}>{t("ES_FSM_SUB_HEADING_DSO_UPLOADS")}</CardSectionHeader>
                <ViewImages
                  fileStoreIds={applicationData?.pitDetail?.additionalDetails?.fileStoreId?.FSM_DSO}
                  tenantId={tenantId}
                  onClick={(source, index) => zoomImageWrapper(source, index)}
                />
              </>
            )}
            {imageZoom ? <ImageViewer imageSrc={imageZoom} onClose={onCloseImageZoom} /> : null}

            <BreakLine />
            {(workflowDetails?.isLoading || isDataLoading) && <Loader />}
            {!workflowDetails?.isLoading && !isDataLoading && (
              <Fragment>
                <CardSectionHeader style={{ marginBottom: "16px", marginTop: "32px" }}>
                  {t("ES_APPLICATION_DETAILS_APPLICATION_TIMELINE")}
                </CardSectionHeader>
                {workflowDetails?.data?.timeline && workflowDetails?.data?.timeline?.length === 1 ? (
                  <CheckPoint
                    isCompleted={true}
                    label={t("CS_COMMON_" + workflowDetails?.data?.timeline[0]?.status)}
                    customChild={getTimelineCaptions(workflowDetails?.data?.timeline[0])}
                  />
                ) : (
                  <ConnectingCheckPoints>
                    {workflowDetails?.data?.timeline &&
                      workflowDetails?.data?.timeline.map((checkpoint, index, arr) => {
                        return (
                          <React.Fragment key={index}>
                            <CheckPoint
                              keyValue={index}
                              isCompleted={index === 0}
                              label={t("CS_COMMON_FSM_" + `${checkpoint.performedAction === "UPDATE" ? "UPDATE_" : ""}` + checkpoint.status)}
                              customChild={getTimelineCaptions(checkpoint)}
                            />
                          </React.Fragment>
                        );
                      })}
                  </ConnectingCheckPoints>
                )}
              </Fragment>
            )}
          </Card>
          {showModal ? (
            <ActionModal
              t={t}
              action={selectedAction}
              tenantId={tenantId}
              state={state}
              id={applicationNumber}
              closeModal={closeModal}
              submitAction={submitAction}
              actionData={workflowDetails?.data?.timeline}
              module={workflowDetails?.data?.applicationBusinessService}
            />
          ) : null}
          {showToast && (
            <Toast
              error={showToast.key === "error" ? true : false}
              label={t(showToast.key === "success" ? `ES_FSM_${showToast.action}_UPDATE_SUCCESS` : showToast.action)}
              onClose={closeToast}
            />
          )}
          {!workflowDetails?.isLoading && workflowDetails?.data?.nextActions?.length === 1 && (
            <ActionBar style={{ zIndex: "19" }}>
              <SubmitBar
                label={t(`ES_FSM_${workflowDetails?.data?.nextActions[0].action}`)}
                onSubmit={() => onActionSelect(workflowDetails?.data?.nextActions[0].action)}
              />
            </ActionBar>
          )}
          {!workflowDetails?.isLoading && workflowDetails?.data?.nextActions?.length > 1 && (
            <ActionBar style={{ zIndex: "19" }}>
              {displayMenu && workflowDetails?.data?.nextActions ? (
                <Menu
                  localeKeyPrefix={"ES_FSM"}
                  options={workflowDetails?.data?.nextActions.map((action) => action.action)}
                  t={t}
                  onSelect={onActionSelect}
                />
              ) : null}
              <SubmitBar label={t("ES_COMMON_TAKE_ACTION")} onSubmit={() => setDisplayMenu(!displayMenu)} />
            </ActionBar>
          )}
        </React.Fragment>
      ) : (
        <Loader />
      )}
    </React.Fragment>
  );
};

export default ApplicationDetails;
