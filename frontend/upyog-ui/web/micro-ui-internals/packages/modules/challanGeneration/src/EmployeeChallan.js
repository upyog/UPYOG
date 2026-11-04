import {
  Card,
  CardSubHeader,
  Header,
  Row,
  StatusTable,
  SubmitBar,
  ActionBar,
  Menu,
  Toast,
  MultiLink,
  DownloadBtnCommon,
} from "@nudmcdgnpm/digit-ui-react-components";
import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useParams, useMatch } from "react-router-dom";
import { stringReplaceAll, convertEpochToDate } from "./utils";
import ActionModal from "./components/Modal";
import { downloadAndPrintChallan, downloadAndPrintReciept } from "./utils";

/**
 * EmployeeChallan component:
 * - Displays challan details, bill breakdown, and actions
 * - Supports download, payment, update, and cancel operations
 */

const EmployeeChallan = (props) => {
  const { t } = useTranslation();
  const { challanno } = useParams();
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const [challanBillDetails, setChallanBillDetails] = useState([]);
  const [totalDueAmount, setTotalDueAmount] = useState(0);

  const [displayMenu, setDisplayMenu] = useState(false);
  const [showModal, setShowModal] = useState(false);
  const [selectedAction, setSelectedAction] = useState(null);
  const navigate = Digit.Hooks.useCustomNavigate();
  const { url } = useMatch();
  const [isDisplayDownloadMenu, setIsDisplayDownloadMenu] = useState(false);
  const [showToast, setShowToast] = useState(null);

  useEffect(() => {
    switch (selectedAction) {
      case "CANCEL_CHALLAN":
        return setShowModal(true);
      case "UPDATE_CHALLAN":
        return navigate(`/upyog-ui/employee/mcollect/modify-challan/${challanno}`);
      case "BUTTON_PAY":
        return navigate(
          `/upyog-ui/employee/payment/collect/${challanDetails?.businessService}/${challanno}/tenantId=${tenantId}?workflow=mcollect`
        );
      default:
        break;
    }
  }, [selectedAction]);

  function onActionSelect(action) {
    setSelectedAction(action);
    setDisplayMenu(false);
  }

  const closeModal = () => {
    setSelectedAction(null);
    setShowModal(false);
  };

  const submitAction = (data) => {
    Digit.ChallanGenerationService.update({ Challan: data?.Challan }, tenantId)
      .then((result) => {
        if (result.challans && result.challans.length > 0) {
          const challan = result.challans[0];
          let LastModifiedTime = Digit.SessionStorage.set("isMcollectAppChanged", challan.challanNo);
          navigate(
            `/upyog-ui/employee/challan-generation/acknowledgement?purpose=challan&status=success&tenantId=${challan?.tenantId}&serviceCategory=${challan.businessService}&challanNumber=${challan.challanNo}&applicationStatus=${challan.applicationStatus}`,
            { from: url }
          );
        }
      })
      .catch((e) => setShowToast({ key: true, label: e?.response?.data?.Errors[0].message }));
    closeModal();
  };

  let isMcollectAppChanged = Digit.SessionStorage.get("isMcollectAppChanged");

  const { isLoading, isError, error, data, ...rest } = Digit.Hooks.mcollect.useMCollectSearch({
    tenantId,
    filters: { challanNo: challanno },
    isMcollectAppChanged,
  });

  var challanDetails = data?.challans?.filter(function (item) {
    return item.challanNo === challanno;
  })[0];

  let billDetails = [];

  useEffect(() => {
    async function fetchMyAPI() {
      billDetails = [];
      let res = await Digit.PaymentService.searchBill(tenantId, {
        consumerCode: data?.challans[0]?.challanNo,
        service: data?.challans[0]?.businessService,
      });
      res?.Bill[0]?.billDetails[0]?.billAccountDetails?.map((bill) => {
        billDetails.push(bill);
      });
      setTotalDueAmount(res?.Bill[0]?.totalAmount);
      billDetails &&
        billDetails.map((ob) => {
          if (ob.taxHeadCode.includes("CGST")) ob.order = 3;
          else if (ob.taxHeadCode.includes("SGST")) ob.order = 4;
        });
      billDetails.sort((a, b) => a.order - b.order);
      setChallanBillDetails(billDetails);
    }
    if (data?.challans && data?.challans?.length > 0) {
      fetchMyAPI();
    }
  }, [data]);

  const challanDownload = {
    order: 1,
    label: t("UC_CHALLAN"),
    onClick: () => downloadAndPrintChallan(challanno, "download"),
  };

  const receiptDownload = {
    order: 2,
    label: t("Receipt"),
    onClick: () => downloadAndPrintReciept(challanDetails?.businessService, challanno, "download"),
  };

  const workflowActions = ["CANCEL_CHALLAN", "UPDATE_CHALLAN", "BUTTON_PAY"];


  return (
    <React.Fragment>
      <div className={"employee-application-details cg-employee-application-details"}>
        <Header>{`${t("CHALLAN_DETAILS")}`} </Header>
        <MultiLink
          className="multilinkWrapper employee-mulitlink-main-div"
          onHeadClick={() => setIsDisplayDownloadMenu(!isDisplayDownloadMenu)}
          displayOptions={isDisplayDownloadMenu}
          options={challanDetails?.applicationStatus === "PAID" ? [challanDownload, receiptDownload] : [challanDownload]}
          downloadBtnClassName={"employee-download-btn-className"}
          optionsClassName={"employee-options-btn-className"}
        />
      </div>

      <div>
        <Card>
          <StatusTable className="cg-status-padding">
            <Row label={`${t("UC_CHALLAN_NO")}`} text={challanno} />
              <hr className="cg-hr-small" />
            {challanBillDetails?.map((data) => {
              return (
                  <Row label={t(stringReplaceAll(data?.taxHeadCode, ".", "_"))} text={<span className="cg-white-space-pre">{`₹${data?.amount}` || 0}</span>} />
              );
            })}
              <hr className="cg-hr-small" />
              <Row label={<b className="cg-total-label">{t("PAYMENT_CS_TOTAL_AMOUNT_DUE")}</b>} text={<span className="cg-total-text">{`₹${totalDueAmount}`}</span>} />
          </StatusTable>
            <div className="cg-section-title">{t("SERVICEDETAILS")}</div>
          <StatusTable>
            <Row
              label={`${t("UC_SERVICE_CATEGORY_LABEL")}`}
              text={<span className="cg-white-space-pre">{t(
                `BILLINGSERVICE_BUSINESSSERVICE_${stringReplaceAll(challanDetails?.businessService?.toUpperCase(), ".", "_")}` || t("CS_NA")
              )}</span>}
            />
            <Row label={`${t("UC_FROM_DATE_LABEL")}`} text={convertEpochToDate(challanDetails?.taxPeriodFrom) || t("CS_NA")} />
            <Row label={`${t("UC_TO_DATE_LABEL")}`} text={convertEpochToDate(challanDetails?.taxPeriodTo) || t("CS_NA")} />
            <Row label={`${t("UC_COMMENT_LABEL")}`} text={`${challanDetails?.description || t("CS_NA")}`} />
            <Row label={`${t("CS_INBOX_STATUS_FILTER")}`} text={t(`UC_${challanDetails?.applicationStatus || t("CS_NA")}`)} />
          </StatusTable>
          <div className="cg-section-title">{t("CONSUMERDETAILS")}</div>
          <StatusTable>
            <Row label={`${t("UC_CONS_NAME_LABEL")}`} text={challanDetails?.citizen.name || t("CS_NA")} />
            <Row label={`${t("UC_MOBILE_NUMBER")}`} text={challanDetails?.citizen.mobileNumber || t("CS_NA")} />
            <Row label={`${t("UC_EMAIL_ID")}`} text={challanDetails?.citizen.emailId || t("CS_NA")} />
            <Row label={`${t("UC_DOOR_NO_LABEL")}`} text={challanDetails?.address.doorNo || t("CS_NA")} />
            <Row label={`${t("UC_BUILDING_NAME_LABEL")}`} text={challanDetails?.address.buildingName || t("CS_NA")} />
            <Row label={`${t("UC_STREET_NAME_LABEL")}`} text={challanDetails?.address.street || t("CS_NA")} />
            <Row
              label={`${t("UC_MOHALLA_LABEL")}`}
              text={`${t(
                `${stringReplaceAll(challanDetails?.address?.tenantId?.toUpperCase(), ".", "_")}_REVENUE_${
                  challanDetails?.address?.locality?.code
                }` || t("CS_NA")
              )}`}
            />
          </StatusTable>
        </Card>
      </div>
      {showModal ? (
        <ActionModal
          t={t}
          action={selectedAction}
          applicationData={challanDetails}
          billData={challanBillDetails}
          closeModal={closeModal}
          submitAction={submitAction}
        />
      ) : null}
      {showToast && <Toast error={showToast.key} label={t(showToast.label)} onClose={() => setShowToast(null)} />}
      {challanDetails?.applicationStatus == "ACTIVE" && (
        <ActionBar>
          {displayMenu && workflowActions ? <Menu localeKeyPrefix="UC" options={workflowActions} t={t} onSelect={onActionSelect} /> : null}
          <SubmitBar label={t("ES_COMMON_TAKE_ACTION")} onSubmit={() => setDisplayMenu(!displayMenu)} />
        </ActionBar>
      )}
    </React.Fragment>
  );
};

export default EmployeeChallan;
