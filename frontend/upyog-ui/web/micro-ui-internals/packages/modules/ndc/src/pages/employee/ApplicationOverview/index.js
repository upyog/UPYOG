import {
  Row,
  StatusTable,
  Card,
  CardSubHeader,
  ActionBar,
  SubmitBar,
  Menu,
  Toast,
  FilterFormField,
  RadioButtons,
  CardLabel,
  TextInput,
  LinkButton,
  MultiLink,
} from "@nudmcdgnpm/digit-ui-react-components";
import { Controller, useForm } from "react-hook-form";
import React, { Fragment, useEffect, useState, useRef } from "react";
import { useTranslation } from "react-i18next";
import { useParams } from "react-router-dom";
import NDCDocument from "../../../pageComponents/NDCDocument";
import NDCModal from "../../../pageComponents/NDCModal";
import { Loader } from "../../../components/Loader";
import NewApplicationTimeline from "../../../../../templates/ApplicationDetails/components/NewApplicationTimeline";
import getAcknowledgementData from "../../../getAcknowlegment";
import { EmployeeData } from "../../../utils";
const availableOptions = [
  { code: "yes", name: "Yes" },
  { code: "no", name: "No" },
];

const ApplicationOverview = () => {
  const { id } = useParams();
  const { t } = useTranslation();
  const navigate = Digit.Hooks.useCustomNavigate();
  const toastRef = useRef(null);
  const tenantId = window.localStorage.getItem("Employee.tenant-id");
  const state = tenantId?.split(".")[0];
  const [showToast, setShowToast] = useState(null);
  const [error, setError] = useState(null);
  const [getLable, setLable] = useState(false);
  const { control, handleSubmit, setValue } = useForm();
  const [showErrorToast, setShowErrorToastt] = useState(null);
  const [errorOne, setErrorOne] = useState(null);
  const [displayData, setDisplayData] = useState({});
  const [isDetailsLoading, setIsDetailsLoading] = useState(false);
  const [markedPending, setMarkedPending] = useState(false);
  const [amounts, setAmounts] = useState({});
  const [getWorkflowService, setWorkflowService] = useState([]);
  const [getLoader, setLoader] = useState(false);
  const [getEmployees, setEmployees] = useState([]);
  const [displayMenu, setDisplayMenu] = useState(false);
  const [selectedAction, setSelectedAction] = useState(null);
  const [showModal, setShowModal] = useState(false);
  const [getPropertyId, setPropertyId] = useState(null);
  const [approver, setApprover] = useState(null);
  const [approverStatement, setApproverStatement] = useState(null);

  const [showOptions, setShowOptions] = useState(false);
  const handleMarkPending = (consumerCode, value, index) => {
    setMarkedPending((prev) => {
      const updated = { ...prev, [consumerCode]: value === "yes" };

      if (updated[consumerCode]) {
      } else {
        setAmounts((prevAmounts) => ({
          ...prevAmounts,
          [consumerCode]: 0,
        }));
        setValue(`amount[${index}]`, 0);
        // TODO: Call API to undo marking
      }

      return updated;
    });
  };

  const { isLoading, data: applicationDetails } = Digit.Hooks.ndc.useSearchEmployeeApplication({ applicationNo: id }, tenantId);

  const workflowDetails = Digit.Hooks.useWorkflowDetails({
    tenantId: tenantId,
    id: id,
    moduleCode: "ndc-services",
    role: "EMPLOYEE",
  });

  if (workflowDetails?.data?.actionState?.nextActions && !workflowDetails.isLoading)
    workflowDetails.data.actionState.nextActions = [...workflowDetails?.data?.nextActions];

  if (workflowDetails && workflowDetails.data && !workflowDetails.isLoading) {
    workflowDetails.data.initialActionState = workflowDetails?.data?.initialActionState || { ...workflowDetails?.data?.actionState } || {};
    workflowDetails.data.actionState = { ...workflowDetails.data };
  }

  let workflowDetailsTemp = {
    data: {
      actionState: {
        nextActions: [
          {
            action: "APPROVE",
            roles: ["NDC_ADMIN"],
            tenantId: "pg",
            assigneeRoles: [],
            isTerminateState: false,
          },
          {
            action: "ASSIGN",
            roles: ["NDC_ADMIN"],
            tenantId: "pg",
            assigneeRoles: ["NDC_ADMIN"],
            isTerminateState: false,
          },
          {
            action: "REJECT",
            roles: ["NDC_ADMIN"],
            tenantId: "pg",
            assigneeRoles: [],
            isTerminateState: true,
          },
        ],
      },
    },
  };

  useEffect(() => {
    let WorkflowService = null;
    (async () => {
      setLoader(true);
      WorkflowService = await Digit.WorkflowService.init(tenantId, "ndc-services");
      setLoader(false);
      setWorkflowService(WorkflowService?.BusinessServices?.[0]?.states);
      // setComplaintStatus(applicationStatus);
    })();
  }, [tenantId]);

  const empData = EmployeeData(tenantId, approver);

  const { data: reciept_data, isLoading: recieptDataLoading } = Digit.Hooks.useRecieptSearch(
    {
      tenantId: tenantId,
      businessService: "NDC",
      consumerCodes: id,
      isEmployee: false,
    },
    { enabled: id ? true : false }
  );

  async function getRecieptSearch({ tenantId, payments, ...params }) {
    setLoader(true);
    try {
      let response = null;
      let application = applicationDetails?.Applications?.[0];
      if (payments?.fileStoreId) {
        response = { filestoreIds: [payments?.fileStoreId] };
      } else {
        response = await Digit.PaymentService.generatePdf(
          tenantId,
          {
            Payments: [
              {
                ...(payments || {}),
                ...application,
              },
            ],
          },
          "ndc-receipt"
        );
      }
      const fileStore = await Digit.PaymentService.printReciept(tenantId, {
        fileStoreIds: response.filestoreIds[0],
      });
      window.open(fileStore[response?.filestoreIds[0]], "_blank");
      setLoader(false);
    } catch (error) {
      console.error(error);
      setLoader(false);
    }
  }
  const dowloadOptions = [];

  if (
    applicationDetails?.Applications?.[0]?.applicationStatus === "APPROVED" ||
    applicationDetails?.Applications?.[0]?.applicationStatus === "REJECTED"
  ) {
    dowloadOptions.push({
      label: t("DOWNLOAD_CERTIFICATE"),
      onClick: () => handleDownloadPdf(),
    });
    if (reciept_data && reciept_data?.Payments.length > 0 && !recieptDataLoading) {
      dowloadOptions.push({
        label: t("PTR_FEE_RECIEPT"),
        onClick: () => getRecieptSearch({ tenantId: reciept_data?.Payments[0]?.tenantId, payments: reciept_data?.Payments[0] }),
      });
    }
  }
  let user = Digit.UserService.getUser();
  const menuRef = useRef();
  if (window.location.href.includes("/obps") || window.location.href.includes("/noc")) {
    const userInfos = sessionStorage.getItem("Digit.citizen.userRequestObject");
    const userInfo = userInfos ? JSON.parse(userInfos) : {};
    user = userInfo?.value;
  }
  const userRoles = user?.info?.roles?.map((e) => e.code);
  const isCemp = user?.info?.roles.find((role) => role.code === "CEMP")?.code;

  let actions =
    workflowDetails?.data?.actionState?.nextActions?.filter((e) => {
      return userRoles?.some((role) => e.roles?.includes(role)) || !e.roles;
    }) ||
    workflowDetailsTemp?.data?.nextActions?.filter((e) => {
      return userRoles?.some((role) => e.roles?.includes(role)) || !e.roles;
    });

  const closeMenu = () => {
    setDisplayMenu(false);
  };

  Digit.Hooks.useClickOutside(menuRef, closeMenu, displayMenu);

  const closeToast = () => {
    setShowToast(null);
  };

  const closeToastOne = () => {
    setShowErrorToastt(null);
  };

  const removeDuplicatesByUUID = (arr) => {
    const seen = new Set();
    return arr.filter((item) => {
      if (seen.has(item.uuid)) {
        return false;
      } else {
        seen.add(item.uuid);
        return true;
      }
    });
  };

  useEffect(() => {
    const ndcObject = applicationDetails?.Applications?.[0];
    if (ndcObject) {
      const applicantData = {
        name: ndcObject?.owners?.[0]?.name,
        mobile: ndcObject?.owners?.[0]?.mobileNumber,
        email: ndcObject?.owners?.[0]?.emailId,
        address: ndcObject?.NdcDetails?.[0]?.additionalDetails?.propertyAddress,
        // createdDate: ndcObject?.owners?.[0]?.createdtime ? format(new Date(ndcObject?.owners?.[0]?.createdtime), "dd/MM/yyyy") : "",
        applicationNo: ndcObject?.applicationNo,
      };
      const Documents = removeDuplicatesByUUID(ndcObject?.Documents || []);
      const NdcDetails = removeDuplicatesByUUID(ndcObject?.NdcDetails || [])?.map((item) => ({
        businessService:
          item?.businessService === "WS"
            ? "NDC_WATER_SERVICE_CONNECTION"
            : item?.businessService === "SW"
            ? "NDC_SEWERAGE_SERVICE_CONNECTION"
            : item?.businessService === "PT"
            ? "NDC_PROPERTY_TAX"
            : item?.businessService,
        consumerCode: item?.consumerCode || "",
        status: item?.status || "",
        dueAmount: item?.dueAmount || 0,
        propertyType: item?.additionalDetails?.propertyType || "",
        isDuePending: item?.isDuePending,
      }));

      setDisplayData({ applicantData, Documents, NdcDetails });
    }
  }, [applicationDetails?.Applications]);

  useEffect(() => {
    if (applicationDetails) {
      setIsDetailsLoading(true);
      const { Applicant: details } = applicationDetails?.Applications?.[0];
      setIsDetailsLoading(false);
    }
  }, [applicationDetails]);

  const handleDownloadPdf = async () => {
    try {
      setLoader(true);
      const Property = applicationDetails;
      const owners = propertyDetailsFetch?.Properties?.[0]?.owners || [];
      const propertyOwnerNames = owners.map((owner) => owner?.name).filter(Boolean);

      Property.propertyOwnerNames = propertyOwnerNames;

      const tenantInfo = tenants?.find((tenant) => tenant?.code === Property?.Applications?.[0]?.tenantId);
      const ulbType = tenantInfo?.city?.ulbType;
      let acknowledgementData;

      if (empData) {
        acknowledgementData = await getAcknowledgementData(Property, formattedAddress, tenantInfo, t, approver, ulbType, empData, approverStatement);
      }
      setTimeout(() => {
        Digit.Utils.pdf.generateNDC(acknowledgementData);
        setLoader(false);
      }, 0);
    } catch (error) {
      setLoader(false);
    }
  };
  function onActionSelect(action) {
    const ndcDetails = applicationDetails?.Applications?.[0]?.NdcDetails || [];
    const hasDuePending = ndcDetails?.some((item) => item.isDuePending === true);
    const filterNexState = action?.state?.actions?.filter((item) => item.action == action?.action);
    const filterRoles = getWorkflowService?.filter((item) => item?.uuid == filterNexState[0]?.nextState);
    const checkactionApp = action?.action == "APPROVE";

    if (hasDuePending && checkactionApp) {
      console.log("alwasy coming appprve");
      setLable("You Can Not Approve This Application, Because It Has Pending Dues. Please Send It To Required Department");
      setError(true);
      setShowToast(true);

      return;
    }

    setEmployees(filterRoles?.[0]?.actions);

    const payload = {
      Licenses: [action],
    };
    const appNo = displayData?.applicantData?.applicationNo;
    if (action?.action == "APPLY") {
      submitAction(payload);
    } else if (action?.action == "PAY") {
      navigate(`/upyog-ui/employee/payment/collect/NDC/${appNo}/${tenantId}?tenantId=${tenantId}`);
    } else if (action?.action == "EDIT") {
      navigate(`/upyog-ui/employee/ndc/create/${appNo}`);
    } else {
      setShowModal(true);
      setSelectedAction(action);
    }
  }

  const submitAction = async (data) => {
    // setShowModal(false);
    // setSelectedAction(null);

    const payloadData = applicationDetails?.Applications[0];

    const updatedApplicant = {
      ...payloadData,
      NdcDetails: payloadData.NdcDetails.map((detail) => {
        const isPending = markedPending[detail.consumerCode]; //  define inside map

        return {
          ...detail,
          isDuePending: isPending || detail.isDuePending || false,
          dueAmount:
            isPending === false
              ? 0 // Force 0 when "No"
              : amounts?.[detail.consumerCode] !== undefined
              ? Number(amounts[detail.consumerCode]) // from input box
              : detail?.dueAmount || 0, // fallback to API value
        };
      }),
      workflow: {},
    };

    const filtData = data?.Licenses?.[0];

    let checkAssigne;
    if (filtData.action == "SENDBACKTOCITIZEN") {
      checkAssigne = [payloadData?.owners?.[0]?.uuid];
    }

    updatedApplicant.workflow = {
      action: filtData.action,
      assignes: filtData?.assignee || checkAssigne,
      comment: filtData?.comment,
      documents: filtData?.wfDocuments,
    };

    if (
      !filtData?.assignee &&
      filtData.action !== "SENDBACKTOCITIZEN" &&
      filtData.action !== "APPROVE" &&
      filtData.action !== "REJECT" &&
      filtData.action !== "SENDBACK"
    ) {
      setErrorOne("Assignee is Mandatory");
      setShowErrorToastt(true);

      return;
    } else if (!String(filtData?.comment || "").trim()) {
      setErrorOne("Comment is Mandatory");
      setShowErrorToastt(true);
      return;
    }

    const finalPayload = {
      Applications: [updatedApplicant],
    };

    // return;

    try {
      const response = await Digit.NDCService.NDCUpdate({ tenantId, details: finalPayload });

      // Show success first
      // setShowToast({ key: "success", message: "Successfully updated the status" });
      setLable("Successfully updated the status");
      setError(false);
      setShowToast(true);

      workflowDetails.revalidate();

      //  Delay navigation so toast shows
      setTimeout(() => {
        navigate("/upyog-ui/employee/ndc/inbox");
        window.location.reload();
      }, 2000);

      setSelectedAction(null);
      setShowModal(false);
    } catch (err) {
      setErrorOne("Something went wrong");
      setShowErrorToastt(true);
      // setShowToast({ key: "error", message: "Something went wrong" });
      // setError("Something went wrong");
    }
  };

  const closeModal = () => {
    setSelectedAction(null);
    setShowModal(false);
  };

  useEffect(() => {
    if (workflowDetails) {
      const approveInstance = workflowDetails?.data?.processInstances?.find((pi) => pi?.action === "APPROVE" || pi?.action === "REJECT");

      const name = approveInstance?.assigner?.name || "NA";
      const status = applicationDetails?.Applications?.[0]?.applicationStatus;
      setApproverStatement(status ? `${t(status)} By` : "");
      setApprover(name);
    }
  }, [workflowDetails]);
  const { data: storeData } = Digit.Hooks.useStore.getInitData();
  const { tenants } = storeData || {};
  useEffect(() => {
    if (displayData) {
      const checkProperty = displayData?.NdcDetails?.filter((item) => item?.businessService == "NDC_PROPERTY_TAX");
      setPropertyId(checkProperty?.[0]?.consumerCode);
    }
  }, [displayData]);

  const { isLoading: checkLoading, isError, error: checkError, data: propertyDetailsFetch } = Digit.Hooks.pt.usePropertySearch(
    { filters: { propertyIds: getPropertyId }, tenantId: tenantId },
    {
      filters: { propertyIds: getPropertyId },
      tenantId: tenantId,
      enabled: getPropertyId ? true : false,
      privacy: Digit.Utils.getPrivacyObject(),
    }
  );

  useEffect(() => {
    const handleClickOutside = (event) => {
      if (toastRef.current && !toastRef.current.contains(event.target)) {
        setShowToast(null); // Close toast
      }
    };

    if (showToast) {
      document.addEventListener("mousedown", handleClickOutside);
    }

    return () => {
      document.removeEventListener("mousedown", handleClickOutside);
    };
  }, [showToast]);
  let address, formattedAddress;

  if (!checkLoading && propertyDetailsFetch?.Properties?.length > 0) {
    address = propertyDetailsFetch.Properties[0].address;
    formattedAddress = [
      address?.doorNo,
      address?.buildingName, // colony/building
      address?.street,
      address?.locality?.name, // locality name
      address?.city,
    ]
      .filter(Boolean)
      .join(", ");
  }

  if (isLoading || isDetailsLoading || recieptDataLoading) {
    return <Loader />;
  }

  return (
    <div className={"employee-main-application-details"}>
      {/* <div>
        <Header styles={{ fontSize: "32px" }}>{t("NDC_APP_OVER_VIEW_HEADER")}</Header>
      </div> */}
      <div style={{ display: "flex", justifyContent: "end", alignItems: "center", padding: "16px" }}>
        {isCemp && (
          <div className="cardHeaderWithOptions ral-app-details-header">
            {getLoader && <Loader />}
            {dowloadOptions && dowloadOptions.length > 0 && (
              <MultiLink
                className="multilinkWrapper"
                onHeadClick={() => setShowOptions(!showOptions)}
                displayOptions={showOptions}
                options={dowloadOptions}
              />
            )}
          </div>
        )}
      </div>
      <Card>
        <CardSubHeader>{t("NDC_APPLICATION_DETAILS_OVERVIEW")}</CardSubHeader>
        <StatusTable>
          {displayData?.applicantData &&
            Object.entries(displayData?.applicantData)?.map(([key, value]) => (
              <Row
                key={key}
                label={t(`${key?.toUpperCase()}`)}
                text={
                  Array.isArray(value)
                    ? value.map((item) => (typeof item === "object" ? t(item?.code || "N/A") : t(item || "N/A"))).join(", ")
                    : typeof value === "object"
                    ? t(value?.code || "N/A")
                    : t(value || "N/A")
                }
              />
            ))}
        </StatusTable>
      </Card>
      <Card>
        <CardSubHeader>{t("NDC_APPLICATION_NDC_DETAILS_OVERVIEW")}</CardSubHeader>
        {displayData?.NdcDetails?.map((detail, index) => {
          const isPT = detail?.businessService === "NDC_PROPERTY_TAX";
          const isSW = detail?.businessService === "NDC_SEWERAGE_SERVICE_CONNECTION";
          const isWS = detail?.businessService === "NDC_WATER_SERVICE_CONNECTION";
          const canRaiseFlag = (isPT && userRoles?.includes("NDC_PT_VERIFIER")) || ((isSW || isWS) && userRoles?.includes("NDC_WS_SW_VERIFIER"));

          const isMarked = markedPending[detail.consumerCode] || detail?.isDuePending;
          const dueAmount = amounts?.[detail.consumerCode] || detail?.dueAmount || 0;
          const isRed = detail.dueAmount > 0;

          return (
            <div key={index} className="ndc-emp-app-overview">
              <StatusTable>
                <Row label={t("NDC_BUSINESS_SERVICE")} text={t(`${detail.businessService}`) || detail.businessService} />
                <Row label={t("NDC_CONSUMER_CODE")} text={detail.consumerCode || "N/A"} />
                {/* <Row label={t("NDC_STATUS")} text={t(detail.status) || detail.status} /> */}

                {(!canRaiseFlag || !isMarked) && (
                  <div
                    style={{
                      background: isRed ? "red" : "none",
                      color: isRed ? "white" : "black",
                      paddingTop: isRed ? "8px" : "0",
                      paddingLeft: isRed ? "10px" : "0",
                    }}
                  >
                    <Row
                      rowContainerStyle={{
                        backgroundColor: isRed ? "red" : "none",
                      }}
                      label={t("NDC_DUE_AMOUNT")}
                      // text={detail.dueAmount?.toString() || "0"}
                      text={(markedPending[detail.consumerCode] === false
                        ? "0"
                        : amounts?.[detail.consumerCode] || detail?.dueAmount || 0
                      ).toString()}
                    />
                  </div>
                )}

                {canRaiseFlag && isMarked && (
                  <div>
                    <Row
                      label="Due Amount"
                      text={
                        <Controller
                          key={index}
                          control={control}
                          name={`amount[${index}]`}
                          defaultValue={markedPending[detail.consumerCode] === false ? 0 : amounts?.[detail.consumerCode] || detail?.dueAmount || 0}
                          render={(props) => (
                            <TextInput
                              type="number"
                              value={props.value}
                              onChange={(e) => {
                                props.onChange(e.target.value);
                                const newValue = e.target.value;
                                setAmounts((prev) => ({
                                  ...prev,
                                  [detail.consumerCode]: newValue,
                                }));
                              }}
                              style={{ maxWidth: "200px" }}
                              onBlur={props.onBlur}
                              disabled={markedPending[detail.consumerCode] === false}
                            />
                          )}
                        />
                      }
                    />
                  </div>
                )}

                <Row label={t("NDC_PROPERTY_TYPE")} text={t(detail.propertyType) || detail.propertyType} />
                {isPT && propertyDetailsFetch?.Properties && (
                  <>
                    <Row
                      label={t("CHB_DISCOUNT_REASON")}
                      text={t(
                        `${
                          applicationDetails?.Applications?.[0]?.reason == "OTHERS"
                            ? applicationDetails?.Applications?.[0]?.NdcDetails?.find((item) => item?.businessService === "PT")?.additionalDetails
                                ?.reason
                            : applicationDetails?.Applications?.[0]?.reason
                        }`
                      )}
                    />
                    <Row label={t("City")} text={propertyDetailsFetch?.Properties?.[0]?.address?.city} />
                    <Row label={t("House No")} text={propertyDetailsFetch?.Properties?.[0]?.address?.doorNo} />
                    <Row label={t("Colony Name")} text={propertyDetailsFetch?.Properties?.[0]?.address?.buildingName} />
                    <Row label={t("Street Name")} text={propertyDetailsFetch?.Properties?.[0]?.address?.street} />
                    {/* <Row label={t("Mohalla")} text={propertyDetailsFetch?.Properties?.[0]?.address?.city} /> */}
                    <Row label={t("Pincode")} text={propertyDetailsFetch?.Properties?.[0]?.address?.pincode || "N/A"} />
                    {/* <Row label={t("Existing Pid")} text={propertyDetailsFetch?.Properties?.[0]?.address?.city} /> */}
                    <Row label={t("Survey Id/UID")} text={propertyDetailsFetch?.Properties?.[0]?.surveyId} />
                    <Row
                      label={t("Year of creation of Property")}
                      text={propertyDetailsFetch?.Properties?.[0]?.additionalDetails?.yearConstruction}
                    />
                    <Row
                      label={t("Remarks")}
                      text={
                        applicationDetails?.Applications?.[0]?.NdcDetails?.find((item) => item?.businessService === "PT")?.additionalDetails
                          ?.remarks || "N/A"
                      }
                    />
                  </>
                )}
              </StatusTable>
              {canRaiseFlag && (
                <div className="mychallan-custom">
                  <CardLabel className="card-label-smaller ndc_card_labels">
                    <b> Pending Dues</b>
                  </CardLabel>
                  <FilterFormField className="radioButtonSection">
                    <Controller
                      name={`assignee${index}`}
                      control={control}
                      defaultValue={detail?.isDuePending ? "yes" : "no"}
                      render={(props) => (
                        <RadioButtons
                          onSelect={(e) => {
                            props.onChange(e.code);
                            handleMarkPending(detail.consumerCode, e.code, index);
                          }}
                          selectedOption={availableOptions.filter((option) => option.code === props.value)[0]}
                          optionsKey="name"
                          options={availableOptions}
                        />
                      )}
                    />
                  </FilterFormField>
                </div>
              )}
            </div>
          );
        })}
      </Card>

      <Card>
        <CardSubHeader>{t("NDC_APPLICATION_DOCUMENTS_OVERVIEW")}</CardSubHeader>
        <div>
          {Array.isArray(displayData?.Documents) && displayData?.Documents?.length > 0 ? (
            <NDCDocument value={{ workflowDocs: displayData?.Documents }}></NDCDocument>
          ) : (
            <div>{t("TL_NO_DOCUMENTS_MSG")}</div>
          )}
        </div>
      </Card>
      <NewApplicationTimeline workflowDetails={workflowDetails} t={t} />

      {applicationDetails?.Applications?.[0]?.applicationStatus !== "INITIATED" && actions?.length && (
        <ActionBar>
          {displayMenu && (workflowDetails?.data?.actionState?.nextActions || workflowDetails?.data?.nextActions) ? (
            <Menu
              localeKeyPrefix={`WF_EDITRENEWAL`}
              options={actions}
              optionKey={"action"}
              t={t}
              onSelect={onActionSelect}
              // style={MenuStyle}
            />
          ) : null}
          <SubmitBar ref={menuRef} label={t("WF_TAKE_ACTION")} onSubmit={() => setDisplayMenu(!displayMenu)} />
        </ActionBar>
      )}

      {applicationDetails?.Applications?.[0]?.applicationStatus == "INITIATED" && (
        <ActionBar>
          <SubmitBar
            label={t("COMMON_EDIT")}
            onSubmit={() => {
              const id = applicationDetails?.Applications?.[0]?.applicationNo;
              navigate(`/upyog-ui/employee/ndc/create/${id}`);
            }}
          />
        </ActionBar>
      )}

      {showModal ? (
        <NDCModal
          t={t}
          action={selectedAction}
          tenantId={tenantId}
          state={state}
          getEmployees={getEmployees}
          id={id}
          applicationDetails={applicationDetails}
          applicationData={applicationDetails?.applicationData}
          closeModal={closeModal}
          submitAction={submitAction}
          actionData={workflowDetails?.data?.timeline}
          workflowDetails={workflowDetails}
          showToast={showToast}
          closeToast={closeToast}
          errors={error}
          showErrorToast={showErrorToast}
          errorOne={errorOne}
          closeToastOne={closeToastOne}
        />
      ) : null}
      {showToast && (
        <div ref={toastRef}>
          <Toast error={error} label={getLable} isDleteBtn={true} onClose={closeToast} />
        </div>
      )}
      {/* {showToast && <Toast error={error} label={getLable} isDleteBtn={true} onClose={closeToast} />} */}
      {(isLoading || isDetailsLoading || checkLoading || getLoader) && <Loader page={true} />}
    </div>
  );
};

export default ApplicationOverview;
