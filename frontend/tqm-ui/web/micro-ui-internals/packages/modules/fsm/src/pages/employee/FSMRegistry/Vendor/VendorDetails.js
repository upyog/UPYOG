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
  Dropdown,
  CardLabel,
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

const VendorDetails = (props) => {
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const state = Digit.ULBService.getStateId();
  const { t } = useTranslation();
  const history = useHistory();
  const queryClient = useQueryClient();
  let { id: dsoId } = useParams();
  const [displayMenu, setDisplayMenu] = useState(false);
  const [selectedAction, setSelectedAction] = useState(null);
  const [config, setCurrentConfig] = useState({});
  const [showModal, setShowModal] = useState(false);
  const [showPopUp, setShowPopUp] = useState(null);
  const [showToast, setShowToast] = useState(null);
  const [vehicles, setVehicles] = useState([]);
  const [drivers, setDrivers] = useState([]);
  const [selectedOption, setSelectedOption] = useState({});

  const { data: dsoData, isLoading: isLoading, isSuccess: isDsoSuccess, error: dsoError, refetch: refetchDso } = Digit.Hooks.fsm.useDsoSearch(
    tenantId,
    { ids: dsoId },
    { staleTime: 0 },
    t
  );

  const { data: vehicleData, isLoading: isVehicleDataLoading, isSuccess: isVehicleSuccess, error: vehicleError, refetch: refetchVehicle } = Digit.Hooks.fsm.useVehiclesSearch({
    tenantId,
    filters: {
      status: "ACTIVE",
      sortBy: "registrationNumber",
      sortOrder: "ASC",
      vehicleWithNoVendor: true,
    },
  });

  const { data: driverData, isLoading: isDriverDataLoading, isSuccess: isDriverSuccess, error: driverError, refetch: refetchDriver } = Digit.Hooks.fsm.useDriverSearch({
    tenantId,
    filters: {
      sortBy: "name",
      sortOrder: "ASC",
      status: "ACTIVE",
      driverWithNoVendor: true,
    },
  });

  const { data: workerData, isLoading: WorkerLoading } = Digit.Hooks.fsm.useWorkerSearch({
    tenantId,
    params: {
      offset: 0,
      limit: 1000,
    },
    details: {
      Individual: {
        roleCodes: ["SANITATION_WORKER"],
      },
    },
    config: {
      staleTime: 0,
      cacheTime: 0,
      enabled: !isLoading,
      select: (data) => {
        const individuals = data?.Individual
        const filteredData = individuals?.filter(entry =>
          entry.additionalFields &&
          entry.additionalFields.fields &&
          entry.additionalFields.fields.some(field =>
            field.key === "EMPLOYER" && field.value === dsoData?.[0]?.dsoDetails?.agencyType
          )
        );
        return filteredData?.map((i) => ({ optionsKey: `${i.name.givenName} / ${i.individualId}`, ...i }));
      },
    },
  });

  const { isLoading: isUpdateLoading, isError: vendorCreateError, data: updateResponse, error: updateError, mutate } = Digit.Hooks.fsm.useVendorUpdate(tenantId);

  function onActionSelect(action) {
    setDisplayMenu(false);
    setSelectedAction(action);
  }

  useEffect(() => {
    switch (selectedAction) {
      case "DELETE":
      case "ADD_VEHICLE":
      case "ADD_DRIVER":
      case "ADD_WORKER":
        return setShowModal(true);
      case "EDIT":
        return history.push(`/${window?.contextPath}/employee/fsm/registry/modify-vendor/` + dsoId);
      case "HOME":
        return history.push(`/${window?.contextPath}/employee/fsm/registry?selectedTabs=VENDOR`);
      default:
        break;
    }
  }, [selectedAction]);

  useEffect(() => {
    if (vehicleData) setVehicles(vehicleData?.vehicle || []);
  }, [vehicleData]);

  useEffect(() => {
    if (driverData) setDrivers(driverData?.driver || []);
  }, [driverData]);

  const closeToast = () => {
    setShowToast(null);
  };

  const closeModal = () => {
    setSelectedAction(null);
    setSelectedOption({});
    setShowModal(false);
    setShowPopUp(false);
  };

  const handleVendorUpdate = () => {
    let dsoDetails = dsoData?.[0]?.dsoDetails;
    let formData = {};
    if (selectedAction === "DELETE") {
      formData = {
        vendor: {
          ...dsoDetails,
          status: "INACTIVE",
        },
      };
    }
    if (selectedAction === "ADD_VEHICLE") {
      let selectedVehicle = selectedOption;
      selectedVehicle.vendorVehicleStatus = "ACTIVE";
      formData = {
        vendor: {
          ...dsoDetails,
          vehicles: dsoDetails.vehicles ? [...dsoDetails.vehicles, selectedVehicle] : [selectedVehicle],
        },
      };
    }
    if (selectedAction === "ADD_DRIVER") {
      let selectedDriver = selectedOption;
      selectedDriver.vendorDriverStatus = "ACTIVE";
      formData = {
        vendor: {
          ...dsoDetails,
          drivers: dsoDetails.drivers ? [...dsoDetails.drivers, selectedDriver] : [selectedDriver],
        },
      };
    }
    if (selectedAction === "ADD_WORKER") {
      let selectedWorker = selectedOption;
      // selectedDriver.vendorDriverStatus = 'ACTIVE';
      formData = {
        vendor: {
          ...dsoDetails,
          workers: dsoDetails.workers
            ? [...dsoDetails.workers, { individualId: selectedWorker?.id, vendorWorkerStatus: "ACTIVE" }]
            : [{ individualId: selectedWorker?.id, vendorWorkerStatus: "ACTIVE" }],
        },
      };
    }

    mutate(formData, {
      onError: (error, variables) => {
        setShowToast({ key: "error", action: error });
        setTimeout(closeToast, 5000);
      },
      onSuccess: (data, variables) => {
        setShowToast({
          key: "success",
          action: selectedAction === "DELETE" ? "DELETE_VENDOR" : selectedAction,
        });
        queryClient.invalidateQueries("DSO_SEARCH");
        refetchDso();
        refetchVehicle();
        refetchDriver();
        setTimeout(() => {
          closeToast();
          if (selectedAction === "DELETE") history.push(`/${window?.contextPath}/employee/fsm/registry?selectedTabs=VENDOR`);
        }, 5000);
      },
    });
    setShowModal(false);
    setSelectedAction(null);
    setSelectedOption({});
  };

  const onEdit = (details, type, id) => {
    if (type === "ES_FSM_REGISTRY_DETAILS_TYPE_WORKER") {
      history.push(`/${window?.contextPath}/employee/fsm/registry/edit-worker?id=${id}`);
    } else {
      let registrationNumber = details?.values?.find((ele) => ele.title === "ES_FSM_REGISTRY_VEHICLE_NUMBER")?.value;
      history.push(`/${window?.contextPath}/employee/fsm/registry/modify-vehicle/` + registrationNumber);
    }
  };

  const onDelete = ({ details, type, id, individualId }) => {
    let formData = {};
    // if (type === 'ES_FSM_REGISTRY_DETAILS_TYPE_DRIVER') {
    //   let dsoDetails = dsoData?.[0]?.dsoDetails;
    //   let drivers = dsoDetails?.drivers;

    //   drivers = drivers.map((data) => {
    //     if (data.id === id) {
    //       data.vendorDriverStatus = 'INACTIVE';
    //     }
    //     return data;
    //   });
    //   formData = {
    //     vendor: {
    //       ...dsoDetails,
    //       drivers: drivers,
    //     },
    //   };
    // }
    if (type === "ES_FSM_REGISTRY_DETAILS_TYPE_WORKER") {
      let dsoDetails = dsoData?.[0]?.dsoDetails;
      let workers = dsoDetails?.workers;

      workers = workers.map((data) => {
        if (data.individualId === details.swid) {
          data.vendorWorkerStatus = "INACTIVE";
        }
        return data;
      });

      formData = {
        vendor: {
          ...dsoDetails,
          workers: workers,
        },
      };
    } else {
      let dsoDetails = dsoData?.[0]?.dsoDetails;
      let vehicles = dsoDetails?.vehicles;
      let registrationNumber = details?.values?.find((ele) => ele.title === "ES_FSM_REGISTRY_VEHICLE_NUMBER")?.value;
      vehicles = vehicles.map((data) => {
        if (data.registrationNumber === registrationNumber) {
          data.vendorVehicleStatus = "INACTIVE";
        }
        return data;
      });
      formData = {
        vendor: {
          ...dsoDetails,
          vehicles: vehicles,
        },
      };
    }
    mutate(formData, {
      onError: (error, variables) => {
        setShowToast({ key: "error", action: error });
        setTimeout(closeToast, 5000);
      },
      onSuccess: (data, variables) => {
        setShowToast({
          key: "success",
          action: type === "ES_FSM_REGISTRY_DETAILS_TYPE_WORKER" ? "DELETE_WORKER" : "DELETE_VEHICLE",
        });
        queryClient.invalidateQueries("DSO_SEARCH");
        refetchDso();
        refetchVehicle();
        refetchDriver();
        setTimeout(() => {
          closeToast();
        }, 5000);
      },
    });
    setShowPopUp(null);
  };

  const renderModalContent = () => {
    if (selectedAction === "DELETE") {
      return <ConfirmationBox t={t} title={"ES_FSM_REGISTRY_DELETE_TEXT"} />;
    }
    if (selectedAction === "ADD_VEHICLE") {
      return (
        <>
          <CardLabel>{t(`ES_FSM_REGISTRY_SELECT_VEHICLE`)}</CardLabel>
          <Dropdown t={t} option={vehicles} value={selectedOption} selected={selectedOption} select={setSelectedOption} optionKey={"registrationNumber"} />
        </>
      );
    }
    if (selectedAction === "ADD_DRIVER") {
      return (
        <>
          <CardLabel>{t(`ES_FSM_REGISTRY_SELECT_DRIVER`)}</CardLabel>
          <Dropdown t={t} option={drivers} value={selectedOption} selected={selectedOption} select={setSelectedOption} optionKey={"name"} />
        </>
      );
    }
    if (selectedAction === "ADD_WORKER") {
      return (
        <>
          <CardLabel>{t(`ES_FSM_REGISTRY_SELECT_WORKER`)}</CardLabel>
          <Dropdown
            t={t}
            option={
              dsoData?.[0]?.dsoDetails?.workers ? workerData.filter((item1) => !dsoData?.[0]?.dsoDetails?.workers.some((item2) => item1.id === item2.individualId)) : workerData
            }
            value={selectedOption}
            selected={selectedOption}
            select={setSelectedOption}
            optionKey={"optionsKey"}
            isSW={"name"}
          />
        </>
      );
    }
  };
  const isMobile = window.Digit.Utils.browser.isMobile();

  if (isLoading) {
    return <Loader />;
  }

  return (
    <React.Fragment>
      {!isLoading ? (
        <React.Fragment>
          <Header style={{ marginBottom: "16px" }}>{t("ES_FSM_REGISTRY_VENDOR_DETAILS")}</Header>
          <div>
            <Card style={{ position: "relative" }} className="page-padding-fix">
              {dsoData?.[0]?.employeeResponse?.map((detail, index) => (
                <React.Fragment key={index}>
                  {index > 0 && <CardSectionHeader style={{ marginBottom: "16px", marginTop: "32px" }}>{t(detail.title)}</CardSectionHeader>}
                  <div>
                    <StatusTable>
                      {detail?.values?.map((value, index) => {
                        return (
                          <Row
                            key={t(value.title)}
                            label={t(value.title)}
                            text={t(value.value) || "N/A"}
                            last={index === detail?.values?.length - 1}
                            caption={value.caption}
                            className={`border-none ${!isMobile ? "vendor-details-row" : ""}`}
                          />
                        );
                      })}
                      {detail?.child?.map((data, index) => {
                        return (
                          <Card className="card-with-background">
                            <div className="card-head">
                              <h2>
                                {t(detail.type)} {index + 1}
                              </h2>
                              <div style={{ display: "flex" }}>
                                <span onClick={() => onEdit(data, detail.type, data.id)}>
                                  <EditIcon
                                    style={{
                                      cursor: "pointer",
                                      marginRight: "20px",
                                    }}
                                    className="edit"
                                    fill="#f47738"
                                  />
                                </span>
                                <span
                                  onClick={() =>
                                    setShowPopUp({
                                      details: data,
                                      type: detail.type,
                                      id: data.id,
                                      individualId: data.individualId ? data.individualId : null,
                                    })
                                  }
                                >
                                  <DeleteIcon style={{ cursor: "pointer" }} className="delete" fill="#f47738" />
                                </span>
                              </div>
                            </div>
                            {data?.values?.map((value, index) =>
                              value ? (
                                value.isPhoto ? (
                                  <Row
                                    className="border-none check-page-uploaded-images"
                                    // label={t(`${detail?.titlee}`)}
                                    text={
                                      <ViewImages
                                        fileStoreIds={value?.photo}
                                        title={t("ES_FSM_PHOTOGRAPH")}
                                        // tenantId={state}
                                        docPreview={true}
                                        tenantId={tenantId}
                                        onClick={(source, index) => window.open(source, "_blank")}
                                      />
                                    }
                                  />
                                ) : (
                                  <Row
                                    key={t(value.title)}
                                    label={t(value.title)}
                                    text={t(value.value) || "N/A"}
                                    last={index === detail?.values?.length - 1}
                                    caption={value.caption}
                                    className="border-none"
                                    textStyle={value.value === "ACTIVE" ? { color: "green" } : {}}
                                  />
                                )
                              ) : null
                            )}
                          </Card>
                        );
                      })}
                      {detail.type && (
                        <div
                          style={{
                            color: "#f47738",
                            cursor: "pointer",
                          }}
                          onClick={() => onActionSelect(detail.type === "ES_FSM_REGISTRY_DETAILS_TYPE_WORKER" ? "ADD_WORKER" : "ADD_VEHICLE")}
                        >
                          {t(`${detail.type}_ADD`)}
                        </div>
                      )}
                    </StatusTable>
                  </div>
                </React.Fragment>
              ))}
            </Card>
          </div>

          {showModal && (
            <Modal
              headerBarMain={
                <Heading
                  label={t(
                    selectedAction === "DELETE"
                      ? "ES_FSM_REGISTRY_DELETE_POPUP_HEADER"
                      : selectedAction === "ADD_VEHICLE"
                      ? "ES_FSM_REGISTRY_ADD_VEHICLE_POPUP_HEADER"
                      : "ES_FSM_REGISTRY_ADD_WORKER_POPUP_HEADER"
                  )}
                />
              }
              headerBarEnd={<CloseBtn onClick={closeModal} />}
              actionCancelLabel={t("CS_COMMON_CANCEL")}
              isDisabled={selectedOption?.optionsKey || selectedOption?.registrationNumber ? false : true}
              actionCancelOnSubmit={closeModal}
              actionSaveLabel={t(selectedAction === "DELETE" ? "ES_EVENT_DELETE" : "CS_COMMON_SUBMIT")}
              actionSaveOnSubmit={handleVendorUpdate}
            >
              <Card style={{ boxShadow: "none" }}>{renderModalContent()}</Card>
            </Modal>
          )}
          {showPopUp && (
            <Modal
              headerBarMain={<Heading label={t("ES_FSM_REGISTRY_DELETE_POPUP_HEADER")} />}
              headerBarEnd={<CloseBtn onClick={closeModal} />}
              actionCancelLabel={t("CS_COMMON_CANCEL")}
              actionCancelOnSubmit={closeModal}
              actionSaveLabel={t("ES_EVENT_DELETE")}
              actionSaveOnSubmit={() => onDelete(showPopUp)}
            >
              <Card style={{ boxShadow: "none" }}>{t("ES_FSM_REGISTRY_DELETE_TEXT")}</Card>
            </Modal>
          )}
          {showToast && (
            <Toast
              error={showToast.key === "error" ? true : false}
              label={t(showToast.key === "success" ? `ES_FSM_REGISTRY_${showToast.action}_SUCCESS` : showToast.action)}
              onClose={closeToast}
            />
          )}
          <ActionBar style={{ zIndex: "19" }}>
            {displayMenu ? <Menu localeKeyPrefix={"ES_FSM_REGISTRY_ACTION"} options={["EDIT", "DELETE", "HOME"]} t={t} onSelect={onActionSelect} /> : null}
            <SubmitBar label={t("ES_COMMON_TAKE_ACTION")} onSubmit={() => setDisplayMenu(!displayMenu)} />
          </ActionBar>
        </React.Fragment>
      ) : (
        <Loader />
      )}
    </React.Fragment>
  );
};

export default VendorDetails;
