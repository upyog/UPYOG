import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { useTranslation } from "react-i18next";
import { LinkButton } from "@demodigit/digit-ui-react-components";
import {FormViewer} from "./FormViewer"
import { LOCALIZATION_KEY } from "../../constants/Localization";

import {
  Card,
  Header,
  CardSubHeader,
  StatusTable,
  Row,
  TextArea,
  SubmitBar,
  DisplayPhotos,
  ImageViewer,
  Loader,
  Toast,
  Dropdown
} from "@demodigit/digit-ui-react-components";

import TimeLine from "../../components/TimeLine";

const WorkflowComponent = ({ complaintDetails, id, getWorkFlow, zoomImage }) => {
  const tenantId = Digit.SessionStorage.get("CITIZEN.COMMON.HOME.CITY")?.code || complaintDetails.service.tenantId;
  let workFlowDetails = Digit.Hooks.useWorkflowDetails({ tenantId: tenantId, id, moduleCode: "PGR" });
  const { data: ComplainMaxIdleTime, isLoading: ComplainMaxIdleTimeLoading } = Digit.Hooks.pgr.useMDMS.ComplainClosingTime(tenantId?.split(".")[0]);

  useEffect(() => {
    getWorkFlow(workFlowDetails.data);
  }, [workFlowDetails.data]);

  useEffect(() => {
    workFlowDetails.revalidate();
  }, []);

  return (
    !workFlowDetails.isLoading && (
      <TimeLine
        // isLoading={workFlowDetails.isLoading}
        data={workFlowDetails.data}
        serviceRequestId={id}
        complaintWorkflow={complaintDetails.workflow}
        rating={complaintDetails.audit.rating}
        zoomImage={zoomImage}
        complaintDetails={complaintDetails}
        ComplainMaxIdleTime={ComplainMaxIdleTime}
      />
    )
  );
};

const ComplaintDetailsPage = (props) => {
  let { t } = useTranslation();
  let { id } = useParams();

  let tenantId = Digit.SessionStorage.get("CITIZEN.COMMON.HOME.CITY")?.code || Digit.ULBService.getCurrentTenantId(); // ToDo: fetch from state
  const { isLoading, error, isError, complaintDetails, revalidate } = Digit.Hooks.pgr.useComplaintDetails({ tenantId, id });

  const [imageShownBelowComplaintDetails, setImageToShowBelowComplaintDetails] = useState({});

  const [imageZoom, setImageZoom] = useState(null);

  const [comment, setComment] = useState("");

  const [toast, setToast] = useState(false);

  const [commentError, setCommentError] = useState(null);

  const [disableComment, setDisableComment] = useState(true);

  const [loader, setLoader] = useState(false);
  const [viewTimeline, setViewTimeline]=useState(false);

  useEffect(() => {
    (async () => {
      if (complaintDetails) {
        setLoader(true);
        await revalidate();
        setLoader(false);
      }
    })();
  }, []);

  function zoomImage(imageSource, index) {
    setImageZoom(imageSource);
  }
  function zoomImageWrapper(imageSource, index) {
    zoomImage(imageShownBelowComplaintDetails?.fullImage[index]);
  }

  function onCloseImageZoom() {
    setImageZoom(null);
  }
  
  const handleViewTimeline=()=>{ 
    const timelineSection=document.getElementById('timeline');
      if(timelineSection){
        timelineSection.scrollIntoView({behavior: 'smooth'});
      } 
      setViewTimeline(true);   
  };
  const onWorkFlowChange = (data) => {
    let timeline = data?.timeline;
    timeline && timeline[0].timeLineActions?.filter((e) => e === "COMMENT").length ? setDisableComment(false) : setDisableComment(true);
    if (timeline) {
      const actionByCitizenOnComplaintCreation = timeline.find((e) => e?.performedAction === "APPLY");
      const { thumbnailsToShow } = actionByCitizenOnComplaintCreation;
      setImageToShowBelowComplaintDetails(thumbnailsToShow);
    }
  };

  const submitComment = async () => {
    let detailsToSend = { ...complaintDetails };
    delete detailsToSend.audit;
    delete detailsToSend.details;
    detailsToSend.workflow = { action: "COMMENT", comments: comment };
    let tenantId = Digit.ULBService.getCurrentTenantId();
    try {
      setCommentError(null);
      const res = await Digit.PGRService.update(detailsToSend, tenantId);
      if (res.ServiceWrappers.length) setComment("");
      else throw true;
    } catch (er) {
      setCommentError(true);
    }
    setToast(true);
    setTimeout(() => {
      setToast(false);
    }, 30000);
  };

  if (isLoading || loader) {
    return <Loader />;
  }

  if (isError) {
    return <h2>Error</h2>;
  }
  const viewConfig = [
    {
      head: t("CS_COMPLAINT_DETAILS_COMPLAINT_DETAILS"),
      body: [
        {
          label: t("CS_COMPLAINT_DETAILS_COMPLAINT_TYPE"),
          isMandatory: true,
          type: "text",
          key: "complaintType", // key from API data
        },
        {
          label: t("CS_COMPLAINT_DETAILS_COMPLAINT_SUBTYPE"),
          isMandatory: true,
          type: "text",
          key: "complaintSubType",
        },
        {
          label: t("CS_COMPLAINT_DETAILS_COMPLAINT_PRIORITY_LEVEL"),
          isMandatory: true,
          type: "text",
          key: "priorityLevel",
        },
        {
          label: t("CS_COMPLAINT_DETAILS_ADDITIONAL_DETAILS"),
          type: "textarea",
          key: "description",
        },
        {
          label: t("CS_ADDCOMPLAINT_EVIDENCE"),
          type: "custom",
          key: "uploadedImages", // we can show images in read-only mode
        },
      ],
    },
    {
      head: t("CS_ADDCOMPLAINT_LOCATION"),
      body: [
        {
          label: t("CORE_COMMON_PINCODE"),
          type: "text",
          key: "pincode",
        },
        {
          label: t("CS_COMPLAINT_DETAILS_CITY"),
          type: "text",
          key: "city",
        },
        {
          label: t("CS_CREATECOMPLAINT_MOHALLA"),
          type: "text",
          key: "locality",
        },
        {
          label: t("CS_COMPLAINT_DETAILS_LANDMARK"),
          type: "textarea",
          key: "landmark",
        },
      ],
    },
    {
      head: t("ES_CREATECOMPLAINT_PROVIDE_COMPLAINANT_DETAILS"),
      body: [
        {
          label: t("ES_CREATECOMPLAINT_MOBILE_NUMBER"),
          type: "text",
          key: "mobileNumber",
        },
        {
          label: t("ES_CREATECOMPLAINT_COMPLAINT_NAME"),
          type: "text",
          key: "name",
        },
        {
          label: t("ES_MAIL_ID"),
          type: "text",
          key: "emailId",
        },
      ],
    },
  ];
  console.log("imageShownBelowComplaintDetails",complaintDetails)
  return (
    <React.Fragment>
      <div className="complaint-summary">
        <div style={{display:"flex",justifyContent:"space-between",maxWidth:"960px"}}>
        {/* <Header>{t(`${LOCALIZATION_KEY.CS_HEADER}_COMPLAINT_SUMMARY`)}</Header> */}
        {/* <div style={{ color:"#A52A2A"}}>
        <LinkButton label={t("VIEW_TIMELINE")}  onClick={handleViewTimeline} ></LinkButton>
        </div> */}
        </div>
        {Object.keys(complaintDetails).length > 0 ? (
    //       <React.Fragment>
    //         <Card>
    //           <CardSubHeader>{t(`SERVICEDEFS.${complaintDetails.audit.serviceCode.toUpperCase()}`)}</CardSubHeader>
    //           <StatusTable>
    //             {Object.keys(complaintDetails.details).map((flag, index, arr) => (
    //               <Row
    //                 key={index}
    //                 label={t(flag)}
    //                 text={
    //                   Array.isArray(complaintDetails.details[flag])
    //                     ? complaintDetails.details[flag].map((val) => (typeof val === "object" ? t(val?.code) : t(val)))
    //                     : t(complaintDetails.details[flag]) || "N/A"
    //                 }
    //                 last={index === arr.length - 1}
    //               />
    //             ))}
    //           </StatusTable>
    //           {imageShownBelowComplaintDetails?.thumbs ? (
    //             <DisplayPhotos srcs={imageShownBelowComplaintDetails?.thumbs} onClick={(source, index) => zoomImageWrapper(source, index)} />
    //           ) : null}
    //           {imageZoom ? <ImageViewer imageSrc={imageZoom} onClose={onCloseImageZoom} /> : null}
    //         </Card>
    //         <Card>
    //         <div id="timeline">
    //           {complaintDetails?.service && (
    //             <WorkflowComponent getWorkFlow={onWorkFlowChange} complaintDetails={complaintDetails} id={id} zoomImage={zoomImage} />
    //           )}
    //           </div>
    //         </Card>
    //         {/* <Card>
    //   <CardSubHeader>{t(`${LOCALIZATION_KEY.CS_COMMON}_COMMENTS`)}</CardSubHeader>
    //   <TextArea value={comment} onChange={(e) => setComment(e.target.value)} name="" />
    //   <SubmitBar disabled={disableComment || comment.length < 1} onSubmit={submitComment} label={t("CS_PGR_SEND_COMMENT")} />
    // </Card> */}
    //         {toast && (
    //           <Toast
    //             error={commentError}
    //             label={!commentError ? t(`CS_COMPLAINT_COMMENT_SUCCESS`) : t(`CS_COMPLAINT_COMMENT_ERROR`)}
    //             onClose={() => setToast(false)}
    //           />
    //         )}{" "}
    //       </React.Fragment>
    <React.Fragment>
      <FormViewer
  heading={t(`SERVICEDEFS.${complaintDetails.audit.serviceCode.toUpperCase()}`)}
  config={viewConfig} // same config you provided
  apiData={{
    complaintType: t(complaintDetails?.details?.CS_ADDCOMPLAINT_COMPLAINT_TYPE),
    complaintSubType: t(complaintDetails?.details?.CS_ADDCOMPLAINT_COMPLAINT_SUB_TYPE),
    priorityLevel: t(complaintDetails?.details?.CS_ADDCOMPLAINT_PRIORITY_LEVEL) || "",
    description: complaintDetails?.service?.description || "",
    pincode: complaintDetails?.service?.address.pincode || "",
    city: t(complaintDetails?.details?.ES_CREATECOMPLAINT_ADDRESS[2]) || "",
    locality: t(complaintDetails?.details?.ES_CREATECOMPLAINT_ADDRESS[1]),
    landmark: complaintDetails?.service?.address?.landmark,
    mobileNumber: complaintDetails?.service?.citizen?.mobileNumber,
    name: complaintDetails?.service?.citizen?.name,
    emailId: complaintDetails?.service?.citizen?.emailId,
    uploadedImages: imageShownBelowComplaintDetails || { thumbs: [], fullImage: [] },
  }}
/>
         <Card className="styled-form">
             <div id="timeline">
               {complaintDetails?.service && (
                 <WorkflowComponent getWorkFlow={onWorkFlowChange} complaintDetails={complaintDetails} id={id} zoomImage={zoomImage} />
              )}
              </div>
            </Card>
    </React.Fragment>
        ) : (
          <Loader />
        )}
      </div>
    </React.Fragment>
  );
};

export default ComplaintDetailsPage;