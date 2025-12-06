import React, { Fragment, useEffect, useState } from "react";
import { useTranslation } from "react-i18next";

import {
  Card,
  StatusTable,
  Row,
  SubmitBar,
  Loader,
  CardSectionHeader,
  ActionBar,
  Menu,
  Toast,
  Header,
  EditIcon,
  DeleteIcon,
  Modal,
  CardText,
  Dropdown,
  AddIcon,
  AddNewIcon,
} from "@egovernments/digit-ui-react-components";

import { useQueryClient } from "react-query";

import { useHistory, useParams } from "react-router-dom";
import ConfirmationBox from "../../../../components/Confirmation";
import { ViewImages } from "../../../../components/ViewImages";

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

const WorkerDetails = (props) => {
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const state = Digit.ULBService.getStateId();
  const { t } = useTranslation();
  const history = useHistory();
  const queryClient = useQueryClient();
  const searchParams = new URLSearchParams(location.search);
  const id = searchParams.get("id");
  const [displayMenu, setDisplayMenu] = useState(false);
  const [selectedAction, setSelectedAction] = useState(null);
  const [config, setCurrentConfig] = useState({});
  const [showModal, setShowModal] = useState(false);
  const [showToast, setShowToast] = useState(null);
  const [vendors, setVendors] = useState([]);

  const [selectedOption, setSelectedOption] = useState({});

  const { data: workerData, isLoading: isWorkerLoading, refetch } = Digit.Hooks.fsm.useWorkerDetails({
    tenantId,
    params: {
      offset: 0,
      limit: 100,
    },
    details: {
      Individual: {
        individualId: id,
      },
    },
    t
  });

  // const { data: driverData, isLoading: isLoading, isSuccess: isDsoSuccess, error: dsoError, refetch } = Digit.Hooks.fsm.useDriverDetails(tenantId, { ids: id });

  const { data: vendorData, isLoading: isVendorLoading, isSuccess: isVendorSuccess, error: vendorError } = Digit.Hooks.fsm.useDsoSearch(
    tenantId,
    { sortBy: "name", sortOrder: "ASC", status: "ACTIVE", agencyType: workerData?.[0]?.agencyType },
    {},
    t
  );

  const {
    isLoading: ismutateUpdateWorkerLoading,
    isError: vendorCreateError,
    data: updateResponse,
    error: updateError,
    mutate: mutateUpdateWorker,
  } = Digit.Hooks.fsm.useWorkerUpdate(tenantId);

  const {
    isLoading: isDeleteWorkerLoading,
    isError: isDeleteWorkerError,
    data: deleteWorkerResponse,
    error: deleteWorkerError,
    mutate: mutateDeleteWorker,
  } = Digit.Hooks.fsm.useWorkerDelete(tenantId);

  const {
    isLoading: isVendorUpdateLoading,
    isError: isVendorUpdateError,
    data: vendorUpdateResponse,
    error: vendorUpdateError,
    mutate: mutateVendor,
  } = Digit.Hooks.fsm.useVendorUpdate(tenantId);

  function onActionSelect(action) {
    setSelectedAction(action);
    setDisplayMenu(false);
  }
  useEffect(() => {
    if (vendorData) {
      let vendors = vendorData.map((data) => data.dsoDetails);
      setVendors(vendors);
    }
  }, [vendorData]);

  useEffect(() => {
    refetch();
  }, []);

  useEffect(() => {
    switch (selectedAction) {
      case "DELETE_SW":
      case "ENABLE_SW":
      case "ADD_VENDOR":
      case "EDIT_VENDOR":
      case "DELETE_VENDOR":
        return setShowModal(true);
      case "EDIT":
        return history.push(`/${window?.contextPath}/employee/fsm/registry/edit-worker?id=${id}`);
      case "HOME":
        return history.push(`/${window?.contextPath}/employee/fsm/registry?selectedTabs=WORKER`);
      default:
        break;
    }
  }, [selectedAction]);

  const closeToast = () => {
    setShowToast(null);
  };

  const handleModalAction = () => {
    switch (selectedAction) {
      case "DELETE_SW":
      case "ENABLE_SW":
        return handleDeleteWorker();
      case "DELETE_VENDOR":
        return handleDeleteVendor();
      case "ADD_VENDOR":
        return handleAddVendor();
      case "EDIT_VENDOR":
        return handleEditVendor();
      default:
        break;
    }
  };

  const handleDeleteWorker = () => {
    let workerdetails = workerData?.[0]?.workerData;
    const formData = {
      Individual: {
        ...workerdetails,
        isSystemUserActive: selectedAction === "ENABLE_SW" ? true : false,
      },
    };

    mutateUpdateWorker(formData, {
      onError: (error, variables) => {
        setShowToast({ key: "error", action: selectedAction === "ENABLE_SW" ? "ENABLE_WORKER_FAILED" : "DISABLE_WORKER_FAILED" });
        setTimeout(closeToast, 5000);
      },
      onSuccess: (data, variables) => {
        setShowToast({ key: "success", action: selectedAction === "ENABLE_SW" ? "ENABLE_WORKER" : "DISABLE_WORKER" });
        queryClient.invalidateQueries("DSO_SEARCH");

        setTimeout(() => {
          closeToast, history.push(`/${window?.contextPath}/employee/fsm/registry?selectedTabs=WORKER`);
        }, 5000);
      },
    });
    setShowModal(false);
  };

  const handleDeleteVendor = () => {
    let formData = {};
    let dsoDetails = workerData?.[0]?.vendorDetails;
    let getWorkerVendorDetails = dsoDetails?.workers;

    getWorkerVendorDetails = getWorkerVendorDetails.map((data) => {
      if (data.individualId === workerData?.[0]?.workerData?.id) {
        data.vendorWorkerStatus = "INACTIVE";
      }
      return data;
    });

    formData = {
      vendor: {
        ...dsoDetails,
        workers: getWorkerVendorDetails,
      },
    };

    mutateVendor(formData, {
      onError: (error, variables) => {
        setShowToast({ key: "error", action: "DELETE_VENDOR_FAILED" });
        setTimeout(closeToast, 5000);
      },
      onSuccess: (data, variables) => {
        setShowToast({ key: "success", action: "DELETE_VENDOR" });
        queryClient.invalidateQueries("DSO_SEARCH");
        refetch();
        setTimeout(closeToast, 5000);
      },
    });
    setShowModal(false);
  };

  const handleAddVendor = () => {
    let dsoDetails = selectedOption;
    let workerDetails = workerData?.[0]?.workerData;
    // workerDetails.vendorDriverStatus = "ACTIVE";
    const formData = {
      vendor: {
        ...dsoDetails,
        workers: dsoDetails.workers
          ? [...dsoDetails.workers, { individualId: workerDetails?.id, vendorWorkerStatus: "ACTIVE" }]
          : [{ individualId: workerDetails?.id, vendorWorkerStatus: "ACTIVE" }],
      },
    };
    mutateVendor(formData, {
      onError: (error, variables) => {
        setShowToast({ key: "error", action: "ADD_VENDOR_FAILED" });
        refetch();
        setTimeout(closeToast, 5000);
      },
      onSuccess: (data, variables) => {
        setShowToast({ key: "success", action: "ADD_VENDOR" });
        queryClient.invalidateQueries("DSO_SEARCH");
        refetch();
        setTimeout(closeToast, 5000);
      },
    });
    setShowModal(false);
    setSelectedAction(null);
    setSelectedOption({});
  };

  const handleEditVendor = () => {
    let dsoDetails = selectedOption;
    let workerDetails = workerData?.[0]?.workerData;
    // driverDetails.vendorDriverStatus = "ACTIVE";

    const formData = {
      vendor: {
        ...dsoDetails,
        workers: dsoDetails.workers
          ? [...dsoDetails.workers, { individualId: workerDetails?.id, vendorWorkerStatus: "ACTIVE" }]
          : [{ individualId: workerDetails?.id, vendorWorkerStatus: "ACTIVE" }],
      },
    };

    mutateVendor(formData, {
      onError: (error, variables) => {
        setShowToast({ key: "error", action: "EDIT_VENDOR_FAILED" });
        setTimeout(closeToast, 5000);
      },
      onSuccess: (data, variables) => {
        setShowToast({ key: "success", action: "EDIT_VENDOR" });
        refetch();
        queryClient.invalidateQueries("DSO_SEARCH");
        setTimeout(closeToast, 5000);
      },
    });
    setShowModal(false);
    setSelectedAction(null);
    setSelectedOption({});
  };

  const closeModal = () => {
    setSelectedAction(null);
    setSelectedOption({});
    setShowModal(false);
  };

  const modalHeading = () => {
    switch (selectedAction) {
      case "DELETE_SW":
        return "ES_FSM_REGISTRY_DELETE_SW_POPUP_HEADER";
      case "ENABLE_SW":
        return "ES_FSM_REGISTRY_ENABLE_SW_POPUP_HEADER";
      case "DELETE_VENDOR":
        return "ES_FSM_REGISTRY_DELETE_POPUP_HEADER";
      case "ADD_VENDOR":
        return "ES_FSM_REGISTRY_ADD_VENDOR_POPUP_HEADER";
      case "EDIT_VENDOR":
        return "ES_FSM_REGISTRY_ADD_VENDOR_POPUP_HEADER";
      default:
        break;
    }
  };

  const renderModalContent = () => {
    if (selectedAction === "DELETE_SW" || selectedAction === "ENABLE_SW") {
      return (
        <ConfirmationBox
          t={t}
          title={workerData?.[0]?.workerData?.isSystemUserActive ? "ES_FSM_REGISTRY_DELETE_SW_TEXT" : "ES_FSM_REGISTRY_ENABLE_SW_TEXT"}
          styles={{
            height: "6rem",
          }}
        />
        // <div className="confirmation_box">
        //   <span>{t(`ES_FSM_REGISTRY_DELETE_TEXT`)} </span>
        // </div>
      );
    }
    if (selectedAction === "DELETE_VENDOR") {
      return (
        <ConfirmationBox
          t={t}
          title={"ES_FSM_REGISTRY_DELETE_TEXT"}
          styles={{
            height: "6rem",
          }}
        />
        // <div className="confirmation_box">
        //   <span>{t(`ES_FSM_REGISTRY_DELETE_TEXT`)} </span>
        // </div>
      );
    }
    if (selectedAction === "ADD_VENDOR") {
      return (
        <>
          <CardText>{t(`ES_FSM_REGISTRY_SELECT_VENODOR`)}</CardText>
          <Dropdown t={t} option={vendors} value={selectedOption} selected={selectedOption} select={setSelectedOption} optionKey={"name"} />
        </>
      );
    }
    if (selectedAction === "EDIT_VENDOR") {
      return (
        <>
          <CardText>{t(`ES_FSM_REGISTRY_SELECT_VENODOR`)}</CardText>
          <Dropdown t={t} option={vendors} value={selectedOption} selected={selectedOption} select={setSelectedOption} optionKey={"name"} />
        </>
      );
    }
  };
  const isMobile = window.Digit.Utils.browser.isMobile();

  if (isWorkerLoading) {
    return <Loader />;
  }

  return (
    <React.Fragment>
      {!isWorkerLoading ? (
        <React.Fragment>
          <Header style={{ marginBottom: "16px" }}>{t("ES_FSM_REGISTRY_WORKER_DETAILS_ID", { ID: id })}</Header>
          <div style={!isMobile ? { marginLeft: "-15px" } : {}}>
            <Card style={{ position: "relative" }} className="page-padding-fix">
              {workerData?.[0]?.employeeResponse?.map((detail, index) => (
                <React.Fragment key={index}>
                  <CardSectionHeader style={index > 0 ? { marginBottom: "16px", marginTop: "32px" } : { marginBottom: "16px" }}>{t(detail.title)}</CardSectionHeader>
                  <StatusTable>
                    {detail?.values?.map((value, index) =>
                      value?.type === "custom" ? (
                        <>
                          <div className={`${index === detail?.values?.length - 1 ? "row last" : "row"} border-none`}>
                            <h2>{t(value.title)}</h2>
                            <div className="value" style={{  display: "flex" }}>
                              {value.value === "ES_FSM_REGISTRY_DETAILS_ADD_VENDOR" && (
                                <span onClick={() => onActionSelect("ADD_VENDOR")}>
                                  <div className="search-add-icon" style={{ marginLeft: 0, marginRight: "10px", cursor: "pointer" }}>
                                    <AddIcon className="" />
                                  </div>
                                </span>
                              )}
                              {t(value.value) || "N/A"}
                              {value.value != "ES_FSM_REGISTRY_DETAILS_ADD_VENDOR" && (
                                <span onClick={() => onActionSelect("EDIT_VENDOR")}>
                                  <EditIcon
                                    style={{
                                      cursor: "pointer",
                                      marginLeft: "20px",
                                    }}
                                  />
                                </span>
                              )}
                              {value.value != "ES_FSM_REGISTRY_DETAILS_ADD_VENDOR" && (
                                <span onClick={() => onActionSelect("DELETE_VENDOR")}>
                                  <DeleteIcon
                                    className="delete"
                                    fill="#f47738"
                                    style={{
                                      cursor: "pointer",
                                      marginLeft: "20px",
                                    }}
                                  />
                                </span>
                              )}
                            </div>
                          </div>
                        </>
                      ) : (
                        <Row
                          key={t(value.title)}
                          label={t(value.title)}
                          text={t(value.value) || "N/A"}
                          last={index === detail?.values?.length - 1}
                          caption={value.caption}
                          className="border-none"
                        />
                      )
                    )}
                    {detail?.isPhoto && (
                      <>
                        <CardSectionHeader style={index > 0 ? { marginBottom: "16px", marginTop: "32px" } : { marginBottom: "16px" }}>{t(detail.titlee)}</CardSectionHeader>
                        <Row
                          className="border-none check-page-uploaded-images"
                          // label={t(`${detail?.titlee}`)}
                          text={
                            <ViewImages
                              fileStoreIds={detail?.photo}
                              // tenantId={state}
                              tenantId={tenantId}
                              onClick={(source, index) => window.open(source, "_blank")}
                            />
                          }
                        />
                      </>
                    )}
                    {/* {detail?.isPhoto && (
                      
                          <ViewImages
                            fileStoreIds={detail?.photo}
                            tenantId={state}
                            onClick={(source, index) =>
                              window.open(source, '_blank')
                            }
                          />
                      
                    )} */}
                    {detail?.child?.map((data, index) => {
                      return (
                        <Card className="card-with-background" style={{ maxWidth: "45%", marginLeft: "0px" }}>
                          <div className="card-head">
                            <h2>
                              {t(`ES_SW_${detail.type}`)} {index + 1}
                            </h2>
                          </div>
                          {data?.FUNCTIONAL_ROLE && (
                            <Row
                              key={t(data?.FUNCTIONAL_ROLE)}
                              label={`${t("Functional role")}`}
                              text={`${t(data?.FUNCTIONAL_ROLE)}` || "N/A"}
                              className="border-none"
                              rowContainerStyle={{ gap: "7rem" }}
                              labelStyle={{ width: "50%", fontWeight: "700" }}
                            />
                          )}
                          {data?.EMPLOYMENT_TYPE && (
                            <Row
                              key={t(data?.EMPLOYMENT_TYPE)}
                              label={`${t("Employment type")}`}
                              text={`${t(data?.EMPLOYMENT_TYPE)}` || "N/A"}
                              className="border-none"
                              rowContainerStyle={{ gap: "7rem" }}
                              labelStyle={{ width: "50%", fontWeight: "700" }}
                            />
                          )}
                          {data?.LICENSE_NUMBER && (
                            <Row
                              key={t(data?.LICENSE_NUMBER)}
                              label={`${t("License Number")}`}
                              text={`${t(data?.LICENSE_NUMBER)}` || "N/A"}
                              className="border-none"
                              rowContainerStyle={{ gap: "7rem" }}
                              labelStyle={{ width: "50%", fontWeight: "700" }}
                            />
                          )}
                        </Card>
                      );
                    })}
                  </StatusTable>
                </React.Fragment>
              ))}
            </Card>
          </div>
          {showModal && (
            <Modal
              headerBarMain={<Heading label={t(modalHeading())} />}
              headerBarEnd={<CloseBtn onClick={closeModal} />}
              actionCancelLabel={t("CS_COMMON_CANCEL")}
              actionCancelOnSubmit={closeModal}
              actionSaveLabel={t(
                selectedAction === "DELETE_SW"
                  ? "ES_EVENT_DELETE_SW"
                  : selectedAction === "ENABLE_SW"
                  ? "ES_EVENT_ENABLE_SW"
                  : selectedAction === "DELETE_VENDOR"
                  ? "ES_EVENT_DELETE"
                  : "CS_COMMON_SUBMIT"
              )}
              actionSaveOnSubmit={handleModalAction}
              formId="modal-action"
              headerBarMainStyle={{ marginBottom: "0px" }}
            >
              {selectedAction === "DELETE_SW" || selectedAction === "ENABLE_SW" || selectedAction === "DELETE_VENDOR" ? (
                renderModalContent()
              ) : (
                <Card style={{ boxShadow: "none" }}>{renderModalContent()}</Card>
              )}
            </Modal>
          )}
          {showToast && (
            <Toast
              error={showToast.key === "error" ? true : false}
              label={t(showToast.key === "success" ? `ES_FSM_REGISTRY_${showToast.action}_SUCCESS` : showToast.action)}
              onClose={closeToast}
              // style={{ marginBottom: "1rem" }}
            />
          )}
          <ActionBar style={{ zIndex: "19" }}>
            {displayMenu ? (
              <Menu
                localeKeyPrefix={"ES_FSM_REGISTRY_ACTION"}
                options={["EDIT", workerData?.[0]?.workerData?.isSystemUserActive ? "DELETE_SW" : "ENABLE_SW", "HOME"]}
                t={t}
                onSelect={onActionSelect}
              />
            ) : null}
            <SubmitBar label={t("ES_COMMON_TAKE_ACTION")} onSubmit={() => setDisplayMenu(!displayMenu)} />
          </ActionBar>
        </React.Fragment>
      ) : (
        <Loader />
      )}
    </React.Fragment>
  );
};

export default WorkerDetails;
