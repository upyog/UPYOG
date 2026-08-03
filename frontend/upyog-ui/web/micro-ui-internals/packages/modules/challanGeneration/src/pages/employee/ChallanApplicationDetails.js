import React, { useEffect, useState, useRef } from "react";
import {
  Card,
  CardSubHeader,
  CardSectionHeader,
  Header,
  Row,
  StatusTable,
  MultiLink,
  CheckPoint,
  ConnectingCheckPoints,
  ActionBar,
  Menu,
  SubmitBar,
  Toast,
} from "@nudmcdgnpm/digit-ui-react-components";
import { useTranslation } from "react-i18next";
import { useParams } from "react-router-dom";
import { Loader } from "../../components/Loader";
import { ChallanData, getLocationName } from "../../utils/index";
import NDCModal from "../../pageComponents/NDCModal";
import CHBDocument from "../../components/ChallanDocument";
import NDCDocumentTimline from "../../components/NDCDocument";
import NewApplicationTimeline from "../../../../templates/ApplicationDetails/components/NewApplicationTimeline";

/**
 * ChallanApplicationDetails component:
 * - Displays challan details, documents, and workflow actions
 * - Handles payment, settlement, and timeline view
 */

const getTimelineCaptions = (checkpoint, index, arr, t) => {
  const { wfComment: comment, thumbnailsToShow, wfDocuments } = checkpoint;
  const caption = {
    date: checkpoint?.auditDetails?.lastModified,
    name: checkpoint?.assigner?.name,
    source: checkpoint?.assigner?.source,
  };

  return (
    <div>
      {comment?.length > 0 && (
        <div className="TLComments">
          <h3>{t("WF_COMMON_COMMENTS")}</h3>
          <p className="cg-overflow-x-scroll">{comment}</p>
        </div>
      )}

      {wfDocuments?.length > 0 && (
        <div>
          {wfDocuments?.map((doc, index) => (
            <div key={index}>
              <NDCDocumentTimline value={wfDocuments} Code={doc?.documentType} index={index} />
            </div>
          ))}
        </div>
      )}

      <div className="cg-margin-top-8">
        {caption.date && <p>{caption.date}</p>}
        {caption.name && <p>{caption.name}</p>}
        {caption.source && <p>{t("ES_COMMON_FILED_VIA_" + caption?.source?.toUpperCase())}</p>}
      </div>
    </div>
  );
};

const ChallanApplicationDetails = () => {
  const { t } = useTranslation();
  const navigate = Digit.Hooks.useCustomNavigate();
  const { acknowledgementIds, tenantId } = useParams();
  const [showOptions, setShowOptions] = useState(false);
  const { data: storeData } = Digit.Hooks.useStore.getInitData();
  const { tenants } = storeData || {};
  const menuRef = useRef();
  const [loader, setLoader] = useState(false);
  const [displayMenu, setDisplayMenu] = useState(false);
  const [selectedAction, setSelectedAction] = useState(null);
  const [showModal, setShowModal] = useState(false);
  const [showToast, setShowToast] = useState(false);
  const [error, setError] = useState("");
  const [getChallanData, setChallanData] = useState();
  const [chbPermissionLoading, setChbPermissionLoading] = useState(false);
  const [printing, setPrinting] = useState(false);
  const [errorOne, setErrorOne] = useState(null);
  const [showErrorToast, setShowErrorToastt] = useState(null);
  const [getLable, setLable] = useState(false);

  const closeMenu = () => {
    setDisplayMenu(false);
  };

  const closeToast = () => {
    setShowToast(null);
  };

  const closeModal = () => {
    setSelectedAction(null);
    setShowModal(false);
  };

  const closeToastOne = () => {
    setShowErrorToastt(null);
  };

  Digit.Hooks.useClickOutside(menuRef, closeMenu, displayMenu);

  const fetchChallans = async (filters) => {
    setLoader(true);
    try {
      const responseData = await Digit.ChallanGenerationService.search({ tenantId, filters });
      setChallanData(responseData?.challans?.[0]);
      setLoader(false);
    } catch (error) {
      setLoader(false);
    }
  };
  let challanEmpData = ChallanData(tenantId, acknowledgementIds);

  useEffect(() => {
    if (acknowledgementIds) {
      const filters = {};
      filters.challanNo = acknowledgementIds;
      fetchChallans(filters);
    }
  }, []);

  const workflowDetails = Digit.Hooks.useWorkflowDetails({
    tenantId: tenantId,
    id: acknowledgementIds,
    moduleCode: "challan-generation",
    role: "EMPLOYEE",
  });

  if (workflowDetails?.data?.actionState?.nextActions && !workflowDetails.isLoading)
    workflowDetails.data.actionState.nextActions = [...workflowDetails?.data?.nextActions];

  if (workflowDetails && workflowDetails.data && !workflowDetails.isLoading) {
    workflowDetails.data.initialActionState = workflowDetails?.data?.initialActionState || { ...workflowDetails?.data?.actionState } || {};
    workflowDetails.data.actionState = { ...workflowDetails.data };
  }

  let user = Digit.UserService.getUser();

  const userRoles = user?.info?.roles?.map((e) => e.code);

  let actions =
    workflowDetails?.data?.actionState?.nextActions?.filter((e) => {
      return userRoles?.some((role) => e.roles?.includes(role)) || !e.roles;
    }) ||
    workflowDetails?.data?.nextActions?.filter((e) => {
      return userRoles?.some((role) => e.roles?.includes(role)) || !e.roles;
    });

  const { data: reciept_data, isLoading: recieptDataLoading } = Digit.Hooks.useRecieptSearch(
    {
      tenantId: tenantId,
      businessService: "Challan_Generation",
      consumerCodes: acknowledgementIds,
      isEmployee: false,
    },
    { enabled: acknowledgementIds ? true : false }
  );
  const dowloadOptions = [];

  async function printChallanNotice({ tenantId, payments, ...params }) {
    if (chbPermissionLoading) return;
    setChbPermissionLoading(true);
    try {
      const applicationDetails = await Digit.ChallanGenerationService.search({ tenantId, filters: { challanNo: acknowledgementIds } });
      const location = await getLocationName(
        applicationDetails?.challans?.[0]?.additionalDetail?.latitude,
        applicationDetails?.challans?.[0]?.additionalDetail?.longitude
      );
      const challan = {
        ...applicationDetails,
        ...challanEmpData,
      };
      let application = challan;
      let fileStoreId = applicationDetails?.Applications?.[0]?.paymentReceiptFilestoreId;
      if (!fileStoreId) {
        let response = await Digit.PaymentService.generatePdf(tenantId, { challan: { ...application, ...payments, location } }, "challan-notice");
        fileStoreId = response?.filestoreIds[0];
      }
      const fileStore = await Digit.PaymentService.printReciept(tenantId, { fileStoreIds: fileStoreId });
      window.open(fileStore[fileStoreId], "_blank");
    } finally {
      setChbPermissionLoading(false);
    }
  }

  async function printChallanReceipt({ tenantId, payments, ...params }) {
    if (printing) return;
    setPrinting(true);
    try {
      const applicationDetails = await Digit.ChallanGenerationService.search({ tenantId, filters: { challanNo: acknowledgementIds } });
      const challan = {
        ...applicationDetails,
        ...challanEmpData,
      };
      let application = challan;
      let fileStoreId = applicationDetails?.Applications?.[0]?.paymentReceiptFilestoreId;
      if (!fileStoreId) {
        let response = await Digit.PaymentService.generatePdf(
          tenantId,
          { Payments: [{ ...payments, challan: application }] },
          "challangeneration-receipt"
        );
        fileStoreId = response?.filestoreIds[0];
      }
      const fileStore = await Digit.PaymentService.printReciept(tenantId, { fileStoreIds: fileStoreId });
      window.open(fileStore[fileStoreId], "_blank");
    } finally {
      setPrinting(false);
    }
  }
  dowloadOptions.push({
    label: t("Challan_Notice"),
    onClick: () => printChallanNotice({ tenantId, payments: reciept_data?.Payments[0] }),
  });

  if (reciept_data && reciept_data?.Payments.length > 0 && !recieptDataLoading) {
    dowloadOptions.push({
      label: t("PTR_FEE_RECIEPT"),
      onClick: () => printChallanReceipt({ tenantId: reciept_data?.Payments[0]?.tenantId, payments: reciept_data?.Payments[0] }),
    });
  }

  function onActionSelect(action) {
    const payload = {
      Licenses: [action],
    };
    if (action.action == "PAY") {
      const code = getChallanData?.challanNo;
      navigate(`/upyog-ui/employee/payment/collect/Challan_Generation/${code}/${tenantId}?tenantId=${tenantId}`);
    } else if (action.action == "PAY_LATER") {
      payLater();
    } else if (action.action == "SETTLED") {
      setShowModal(true);
      setSelectedAction(action);
    }
  }

  const payLater = async () => {
    // Prevent API call if data not ready
    if (!getChallanData || !getChallanData.challanNo) {
      console.warn("Challan data not loaded yet");
      return;
    }

    setLoader(true);

    const payload = {
      Challan: {
        ...getChallanData,
        workflow: {
          action: "PAY_LATER",
        },
      },
    };

    try {
      const response = await Digit.ChallanGenerationService.update(payload);

      setLoader(false);

      setLable("Challan set to pay later.");
      setError(false);
      setShowToast(true);

      setTimeout(() => {
        navigate("/upyog-ui/employee/challangeneration/inbox");
        window.location.reload();
      }, 2000);

    } catch (error) {
      console.error("PAY_LATER failed:", error);
      setLoader(false);
    }
  };

  const submitAction = async (modalData) => {
    if (!modalData?.amount) {
      setErrorOne(`Please Enter Amount`);
      setShowErrorToastt(true);
    } else {
      const finalAmount = Math.max(getChallanData?.amount?.[0]?.amount || 0, getChallanData?.challanAmount || 0);
      if (modalData?.amount > finalAmount) {
        setErrorOne(`Amount must be less than or equal to ${finalAmount}`);
        setShowErrorToastt(true);
        setError(`Amount must be less than or equal to ${finalAmount}`);
      } else {

        setLoader(true);

        const payload = {
          Challan: {
            ...getChallanData,
            workflow: {
              action: "SETTLED",
              documents: modalData?.wfDocuments,
            },
            feeWaiver: modalData?.amount,
          },
        };

        try {
          const response = await Digit.ChallanGenerationService.update(payload);
          setLoader(false);
          setShowModal(false);
          // ✅ Show success first
          setLable("Challan is Settled");
          setError(false);
          setShowToast(true);

          // ✅ Delay navigation so toast shows
          setTimeout(() => {
            navigate("/upyog-ui/employee/challangeneration/inbox");
            window.location.reload();
          }, 2000);
        } catch (error) {
          setLoader(false);
        }
      }
    }
  };

  return (
    <React.Fragment>
      <div className="challan-application-details">
        <div className="cardHeaderWithOptions">
          <Header className="challan-custom-header-font">{t("CHALLAN_DETAILS")}</Header>
          {dowloadOptions && dowloadOptions.length > 0 && (
            <MultiLink
              className="multilinkWrapper"
              onHeadClick={() => setShowOptions(!showOptions)}
              displayOptions={showOptions}
              options={dowloadOptions}
            />
          )}
        </div>
        <Card>
          <CardSubHeader className="challan-custom-subheader-font">{t("CHALLAN_OFFENDER_DETAILS")}</CardSubHeader>
          <StatusTable>
            <Row className="border-none" label={t("CORE_COMMON_NAME")} text={getChallanData?.citizen?.name || t("CS_NA")} />
            <Row className="border-none" label={t("CHALLAN_OWNER_MOBILE_NUMBER")} text={getChallanData?.citizen?.mobileNumber || t("CS_NA")} />
            <Row className="border-none" label={t("NDC_ADDRESS")} text={getChallanData?.address?.addressLine1 || t("CS_NA")} />
          </StatusTable>

          <CardSubHeader className="challan-custom-subheader-font">{t("CHALLAN_DETAILS")}</CardSubHeader>
          <StatusTable>
            <Row className="border-none" label={t("CHALLAN_NUMBER")} text={t(getChallanData?.challanNo) || t("CS_NA")} />
            <Row className="border-none" label={t("reports.mcollect.status")} text={t(getChallanData?.challanStatus) || t("CS_NA")} />
            <Row className="border-none" label={t("CHALLAN_OFFENCE_NAME")} text={t(getChallanData?.offenceTypeName) || t("CS_NA")} />
            <Row className="border-none" label={t("CHALLAN_OFFENCE_TYPE")} text={getChallanData?.offenceCategoryName || t("CS_NA")} />
            <Row
              className="border-none"
              label={t("CHALLAN_AMOUNT")}
              text={Math.max(getChallanData?.amount?.[0]?.amount || 0, getChallanData?.challanAmount || 0)}
            />
            {getChallanData?.feeWaiver && <Row className="border-none" label={t("FEE_WAIVER_AMOUNT")} text={getChallanData?.feeWaiver} />}
          </StatusTable>

          <CardSubHeader className="challan-custom-header">{t("CS_COMMON_DOCUMENTS")}</CardSubHeader>
          <StatusTable>
            <Card className="challan-custom-card">
              {getChallanData?.documents?.length > 0 ? (
                getChallanData?.documents?.map((doc, index) => (
                  <React.Fragment key={index}>
                    <div>
                      <CHBDocument value={getChallanData?.documents} Code={doc?.documentType} index={index} />
                      <CardSectionHeader>{t(doc?.documentType)}</CardSectionHeader>
                    </div>
                  </React.Fragment>
                ))
              ) : (
                <h5>{t("CS_NO_DOCUMENTS_UPLOADED")}</h5>
              )}
            </Card>
          </StatusTable>
        </Card>
        <NewApplicationTimeline workflowDetails={workflowDetails} t={t} />
        {actions && actions.length > 0 && !actions.some((a) => a.action === "SUBMIT") && (
          <ActionBar>
            {displayMenu && (workflowDetails?.data?.actionState?.nextActions || workflowDetails?.data?.nextActions) ? (
              <Menu localeKeyPrefix={`WF_CHALLAN`} options={actions} optionKey={"action"} t={t} onSelect={onActionSelect} />
            ) : null}
            <SubmitBar ref={menuRef} label={t("WF_TAKE_ACTION")} onSubmit={() => setDisplayMenu(!displayMenu)} />
          </ActionBar>
        )}
      </div>

      {showModal ? (
        <NDCModal
          t={t}
          action={selectedAction}
          tenantId={tenantId}
          id={acknowledgementIds}
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
          getLable={getLable}
          getChallanData={getChallanData}
        />
      ) : null}
      {showToast && <Toast isDleteBtn={true} error={error} label={getLable} onClose={closeToast} />}
      {(loader || workflowDetails?.isLoading) && <Loader page={true} />}
    </React.Fragment>
  );
};

export default ChallanApplicationDetails;
