import React, { Fragment, useState } from "react";
import { useParams } from "react-router-dom";
import { useTranslation } from "react-i18next";
import { useQueryClient } from "@tanstack/react-query";
import { Header, Card, CardSectionHeader, PDFSvg, Loader, StatusTable, Menu, ActionBar, SubmitBar, Modal, CardText } from "@nudmcdgnpm/digit-ui-react-components";
import ApplicationDetailsTemplate from "../../../../../templates/ApplicationDetails";
import { format } from "date-fns";

const Heading = (props) => {
  return <h1 className="heading-m">{props.label}</h1>;
};

const Close = () => (
  <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="#FFFFFF">
    <path d="M0 0h24v24H0V0z" fill="none" />
    <path d="M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12 19 6.41z" />
  </svg>
);

const CloseBtn = (props) => {
  return (
    <div className="icon-bg-secondary" onClick={props.onClick}>
      <Close />
    </div>
  );
};

const formatDateSafe = (dateVal, formatStr = 'dd/MM/yyyy') => {
  if (!dateVal) return "NA";
  const num = Number(dateVal);
  const date = isNaN(num) ? new Date(dateVal) : new Date(num);
  return isNaN(date.getTime()) ? "NA" : format(date, formatStr);
};

const DocumentDetails = ({ t, data, documents, paymentDetails }) => {
  return (
    <Fragment>
      {data?.map((document, index) => {
        const docPath = documents?.[document?.fileStoreId];
        const docUrl = docPath?.split(",")?.[0];
        const docName = docUrl ? decodeURIComponent(docUrl.split("?")?.[0]?.split("/")?.pop()?.slice(13)) : "";
        return (
          <Fragment key={index}>
            <div style={{maxWidth: "940px", padding: "8px", borderRadius: "4px", border: "1px solid #D6D5D4", background: "#FAFAFA", marginBottom: "32px"}}>
              <div style={{fontSize: "16px", fontWeight: 700}}>{t(`BPA_${document?.documentType}`)}</div>
              {docUrl && (
                <a target="_" href={docUrl}>
                  <PDFSvg />
                </a>
              )}
              {docName && <span> {docName} </span>}
            </div>
          </Fragment>
        );
      })}
    </Fragment>
  );
};

const MessageDetails = () => {
  const { id } = useParams();
  const { t } = useTranslation();
  const navigate = Digit.Hooks.useCustomNavigate();
  const queryClient = useQueryClient();
  const updateEventMutation = Digit.Hooks.events.useUpdateEvent();
  const [showModal, setShowModal] = useState(false);
  const [displayMenu, setDisplayMenu] = useState(false);
  const tenantId = Digit.ULBService.getCurrentTenantId();


  const { isLoading, data } = Digit.Hooks.events.useEventDetails(tenantId, { ids: id }, {
    select: (data) => {
      const details = [{
        title: "",
        asSectionHeader: true,
        values: [
          { title: "EVENTS_ULB_LABEL", value: data?.tenantId },
          { title: "EVENTS_NAME_LABEL", value: data?.name },
          { title: "PUBLIC_BRDCST_TITLE_LABEL", value: data?.description },
          { title: "EVENTS_FROM_DATE_LABEL", value: formatDateSafe(data?.eventDetails?.fromDate) },
          { title: "EVENTS_TO_DATE_LABEL", value: formatDateSafe(data?.eventDetails?.toDate) },
          { title: "CS_COMMON_DOCUMENTS", belowComponent: () => <DocumentDetails t={t} data={data?.eventDetails?.documents} documents={data?.uploadedFilesData?.data} /> }
        ]
      }]
      return {
        applicationData: data,
        applicationDetails: details,
        tenantId: tenantId
      }
    }
  });

  function onActionSelect(action) {
    // setSelectedAction(action);
    if (action === "EDIT") {
      navigate(`/upyog-ui/employee/engagement/messages/inbox/edit/${id}`)
    }
    if (action === "DELETE") {
      setShowModal(true);
    }
    setDisplayMenu(false);
  }

  const handleDelete = () => {
    const eventObj = data?.applicationData || data;
    if (!eventObj || Object.keys(eventObj).length === 0) {
      return;
    }
    const { uploadedFilesData, ...ogData } = eventObj;
    const details = {
      events: [
        {
          ...ogData,
          status: "CANCELLED",
        },
      ],
    };
    updateEventMutation.mutate(details, {
      onSuccess: (responseData) => {
        queryClient.clear();
        navigate("/upyog-ui/employee/engagement/messages/response?delete=true", { state: { isSuccess: true, data: responseData } });
      },
      onError: (error) => {
        navigate("/upyog-ui/employee/engagement/messages/response?delete=true", { state: { isSuccess: false, error } });
      }
    });
  };

  if (isLoading || updateEventMutation.isPending) {
    return <Loader />;
  }

  return (
    <Fragment>
      <div>
        <Header>{t("ES_TITLE_APPLICATION_DETAILS")}</Header>
      </div>
      <ApplicationDetailsTemplate
        applicationDetails={data}
        isLoading={isLoading || updateEventMutation.isPending}
        isDataLoading={isLoading || updateEventMutation.isPending}
        // workflowDetails={workflowDetails}
        // businessService={
        //   workflowDetails?.data?.applicationBusinessService
        //     ? workflowDetails?.data?.applicationBusinessService
        //     : data?.applicationData?.businessService
        // }
      />
      <ActionBar>
        {displayMenu ? (
          <Menu
            localeKeyPrefix={"ES_PUBLIC_BRDCST"}
            options={['EDIT', 'DELETE']}
            t={t}
            onSelect={onActionSelect}
          />
        ) : null}
        <SubmitBar label={t("ES_COMMON_TAKE_ACTION")} onSubmit={() => setDisplayMenu(!displayMenu)} />
      </ActionBar>
      {showModal &&
        <Modal
        headerBarMain={<Heading label={t('PUBLIC_BRDCST_DELETE_POPUP_HEADER')} />}
        headerBarEnd={<CloseBtn onClick={() => setShowModal(false)} />}
        actionCancelLabel={t("CS_COMMON_CANCEL")}
        actionCancelOnSubmit={() => setShowModal(false)}
        actionSaveLabel={t('PUBLIC_BRDCST_DELETE')}
        actionSaveOnSubmit={handleDelete}
        >
          <Card style={{ boxShadow: "none" }}>
            <CardText>{t(`PUBLIC_BRDCST_DELETE_TEXT`)}</CardText>
          </Card>
        </Modal>
      }
    </Fragment>
  );
};

export default MessageDetails;