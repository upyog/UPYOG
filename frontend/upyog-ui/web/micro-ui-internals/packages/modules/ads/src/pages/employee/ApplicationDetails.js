import { Header, MultiLink } from "@upyog/digit-ui-react-components";
import _ from "lodash";
import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useParams } from "react-router-dom";
import ApplicationDetailsTemplate from "../../../../templates/ApplicationDetails";

/**
 * ApplicationDetails component fetches and displays detailed information about a specific advertisement booking.
 * It retrieves data based on the `bookingNo` from the URL parameters and fetches relevant details
 * such as application information, payment receipts, and permission letters.
 */

const ApplicationDetails = () => {
  const { t } = useTranslation();
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const { id: bookingNo } = useParams();
  const [showToast, setShowToast] = useState(null);
  const [appDetailsToShow, setAppDetailsToShow] = useState({});
  const [showOptions, setShowOptions] = useState(false);
  
  const { isLoading, isError, data: applicationDetails, error } = Digit.Hooks.ads.useADSApplicationDetail(t, tenantId, bookingNo);
  

  const mutation = Digit.Hooks.ads.useADSCreateAPI(tenantId, false);
  const { isLoading: auditDataLoading, isError: isAuditError, data,refetch} = Digit.Hooks.ads.useADSSearch(
    {
      tenantId,
      filters: { bookingNo: bookingNo, audit: true },
    },
  );

  const closeToast = () => {
    setShowToast(null);
  };

  useEffect(() => {
    if (applicationDetails) {
      setAppDetailsToShow(_.cloneDeep(applicationDetails));
    
    }
  }, [applicationDetails]);


  const { data: reciept_data, isLoading: recieptDataLoading } = Digit.Hooks.useRecieptSearch(
    {
      tenantId: tenantId,
      businessService: "adv-services",
      consumerCodes: appDetailsToShow?.applicationData?.applicationData?.bookingNo,
      isEmployee: false,
    },
    { enabled: appDetailsToShow?.applicationData?.applicationData?.bookingNo ? true : false }
  );
  async function getRecieptSearch({ tenantId, payments, ...params }) {
    let application = data?.bookingApplication?.[0];
    let fileStoreId = application?.paymentReceiptFilestoreId
    if (!fileStoreId) {
    let response = { filestoreIds: [payments?.fileStoreId] };
    response = await Digit.PaymentService.generatePdf(tenantId, { Payments: [{ ...payments }] }, "advservice-receipt");
    const updatedApplication = {
      ...application,
      paymentReceiptFilestoreId: response?.filestoreIds[0]
    };
    await mutation.mutateAsync({
      bookingApplication: updatedApplication
    });
    fileStoreId = response?.filestoreIds[0];
    refetch();
    }
    const fileStore = await Digit.PaymentService.printReciept(tenantId, { fileStoreIds: fileStoreId });
    window.open(fileStore[fileStoreId], "_blank");
  };

  async function getPermissionLetter({ tenantId, payments, ...params }) {
    let application = data?.bookingApplication?.[0];
    let fileStoreId = application?.permissionLetterFilestoreId;
    if (!fileStoreId) {
      const response = await Digit.PaymentService.generatePdf(
        tenantId,
        { bookingApplication: [application] }, 
        "advpermissionletter"
      );
      const updatedApplication = {
        ...application,
        permissionLetterFilestoreId: response?.filestoreIds[0]
      };
      await mutation.mutateAsync({
        bookingApplication: updatedApplication
      });
      fileStoreId = response?.filestoreIds[0];
      refetch();
    }
    const fileStore = await Digit.PaymentService.printReciept(tenantId, { fileStoreIds: fileStoreId });
    window.open(fileStore[fileStoreId], "_blank");
  }

  let dowloadOptions = [];
  if (reciept_data && reciept_data?.Payments.length > 0 && recieptDataLoading == false)
  dowloadOptions.push({
    label: t("ADS_FEE_RECEIPT"),
    onClick: () => getRecieptSearch({ tenantId: reciept_data?.Payments[0]?.tenantId, payments: reciept_data?.Payments[0] }),
  });

  if (reciept_data && reciept_data?.Payments.length > 0 && recieptDataLoading == false)
    dowloadOptions.push({
      label: t("ADS_PERMISSION_LETTER"),
      onClick: () => getPermissionLetter({ tenantId: reciept_data?.Payments[0]?.tenantId, payments: reciept_data?.Payments[0] }),
    });


  return (
    <div>
        <div className={"employee-application-details"} style={{ marginBottom: "15px" }}>
          <Header styles={{ marginLeft: "0px", paddingTop: "10px", fontSize: "32px" }}>{t("ADS_BOOKING_DETAILS")}</Header>
          <div style={{zIndex: "10",display:"flex",flexDirection:"row-reverse",alignItems:"center",marginTop:"-25px"}}>
          <div style={{zIndex: "10",  position: "relative"}}>
        {dowloadOptions && dowloadOptions.length > 0 && (
          <MultiLink
            className="multilinkWrapper"
            onHeadClick={() => setShowOptions(!showOptions)}
            displayOptions={showOptions}
            options={dowloadOptions}
            downloadBtnClassName={"employee-download-btn-className"}
            optionsClassName={"employee-options-btn-className"}
          // ref={menuRef}
          />
        )}
      </div>
      </div>
      </div>
      <ApplicationDetailsTemplate
        applicationDetails={appDetailsToShow?.applicationData}
        isLoading={isLoading}
        isDataLoading={isLoading}
        applicationData={appDetailsToShow?.applicationData?.applicationData}
        showToast={showToast}
        setShowToast={setShowToast}
        closeToast={closeToast}
        MenuStyle={{ color: "#FFFFFF", fontSize: "18px" }}
      />
      

    </div>
  );
};

export default React.memo(ApplicationDetails);
