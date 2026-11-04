import { Banner, Card, CardText, ActionBar, SubmitBar, Toast } from "@nudmcdgnpm/digit-ui-react-components";
import React, { useState, Fragment, useEffect } from "react";
import { useTranslation } from "react-i18next";
import { ChallanData, getLocationName } from "../utils";
import { Loader } from "./Loader";
import { useLocation } from "react-router-dom";

/**
 * ChallanResponseCitizen component:
 * - Displays challan acknowledgement screen
 * - Fetches challan details
 * - Supports print, pay later, and payment actions
 */

const ChallanResponseCitizen = (props) => {
  const location = useLocation();
  const state = location?.state;
  const { t } = useTranslation();
  const navigate = Digit.Hooks.useCustomNavigate();
  const nocData = state?.data?.Noc?.[0];
  const isCitizen = window.location.href.includes("citizen");
  const [chbPermissionLoading, setChbPermissionLoading] = useState(false);
  const [loader, setLoader] = useState(false);
  const [getChallanData, setChallanData] = useState();
  const [getLable, setLable] = useState(false);
  const [error, setError] = useState(null);
  const [showToast, setShowToast] = useState(null);

  const tenantId = window.location.href.includes("citizen")
    ? window.localStorage.getItem("CITIZEN.CITY")
    : window.localStorage.getItem("Employee.tenant-id");

  const { pathname } = useLocation();
  const ndcCode = pathname.split("/").pop();

  let challanEmpData = ChallanData(tenantId, ndcCode);

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

  const closeToast = () => {
    setShowToast(null);
  };

  useEffect(() => {
    if (ndcCode) {
      const filters = {};
      filters.challanNo = ndcCode;
      fetchChallans(filters);
    }
  }, []);

  const onSubmit = () => {
    if (isCitizen) navigate(`/upyog-ui/citizen`);
    else navigate(`/upyog-ui/employee`);
  };

  const payLater = async () => {
    // ✅ wait until data is ready
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

      // ✅ Show success first
      setLable("Challan set to pay later.");
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
  };

  const printChallanNotice = async () => {
    if (chbPermissionLoading) return;
    setChbPermissionLoading(true);
    try {
      const applicationDetails = await Digit.ChallanGenerationService.search({ tenantId, filters: { challanNo: ndcCode } });
      const location = await getLocationName(applicationDetails?.challans?.[0]?.additionalDetail?.latitude, applicationDetails?.challans?.[0]?.additionalDetail?.longitude)
      const challan = {
        ...applicationDetails,
        ...challanEmpData,
      };
      let application = challan;
      let fileStoreId = applicationDetails?.Applications?.[0]?.paymentReceiptFilestoreId;
      if (!fileStoreId) {
        let response = await Digit.PaymentService.generatePdf(tenantId, { challan: { ...application, location } }, "challan-notice");
        fileStoreId = response?.filestoreIds[0];
      }
      const fileStore = await Digit.PaymentService.printReciept(tenantId, { fileStoreIds: fileStoreId });
      window.open(fileStore[fileStoreId], "_blank");
    } finally {
      setChbPermissionLoading(false);
    }
  };

  const handlePayment = () => {
    navigate(`/upyog-ui/employee/payment/collect/Challan_Generation/${ndcCode}?tenantId=${tenantId}`);
  };

  return (
    <div>
      <Card>
        <Banner
          message={t("CHALLAN_APPLICATION_CREATED")}
          applicationNumber={ndcCode}
          info={nocData?.applicationStatus == "REJECTED" ? "" : t(`CHALLAN_NUMBER`)}
          successful={nocData?.applicationStatus == "REJECTED" ? false : true}
          className="cg-banner-padding"
          headerClassName="cg-banner-header"
        />

        <div className="primary-label-btn d-grid" onClick={chbPermissionLoading ? undefined : printChallanNotice}>
          {chbPermissionLoading ? (
            <Loader />
          ) : (
            <>
              <svg xmlns="http://www.w3.org/2000/svg" height="24" viewBox="0 0 24 24" width="24">
                <path d="M0 0h24v24H0z" fill="none" />
                <path d="M19 8H5c-1.66 0-3 1.34-3 3v6h4v4h12v-4h4v-6c0-1.66-1.34-3-3-3zm-3 11H8v-5h8v5zm3-7c-.55 0-1-.45-1-1s.45-1 1-1 1 .45 1 1-.45 1-1 1zm-1-9H6v4h12V3z" />
              </svg>
              {t("Challan_Notice")}
            </>
          )}
        </div>
        <ActionBar className="challan-response-action-bar" >
          <SubmitBar label={t("CORE_COMMON_GO_TO_HOME")} onSubmit={onSubmit} />
          <SubmitBar label={t("CHALLAN_PAY_LATER")} onSubmit={payLater} />
          <SubmitBar label={t("CS_APPLICATION_DETAILS_MAKE_PAYMENT")} onSubmit={handlePayment} />
        </ActionBar>
      </Card>
      {showToast && <Toast error={error} label={getLable} isDleteBtn={true} onClose={closeToast} />}
    </div>
  );
};
export default ChallanResponseCitizen;
