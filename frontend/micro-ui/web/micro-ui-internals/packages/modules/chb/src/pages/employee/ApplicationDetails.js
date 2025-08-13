    import { Header, MultiLink } from "@upyog/digit-ui-react-components";
    import _ from "lodash";
    import React, { useEffect, useState } from "react";
    import { useTranslation } from "react-i18next";
    import { useParams } from "react-router-dom";
    import ApplicationDetailsTemplate from "../../../../templates/ApplicationDetails";
    import getChbAcknowledgementData from "../../getChbAcknowledgementData";

/*
    The ApplicationDetails component fetches and displays details of a community hall booking 
    based on a booking number from the URL parameter. It includes functionality for displaying 
    download options like receipt and permission letter, managing workflow details, and handling 
    PDF generation for receipts/letters. The component integrates with hooks for data fetching 
    and mutation, and provides a UI for interacting with the application details.
  */

    const ApplicationDetails = () => {
      const { t } = useTranslation();
      const { data: storeData } = Digit.Hooks.useStore.getInitData();
      const tenantId = Digit.ULBService.getCurrentTenantId();
      const { tenants } = storeData || {};
      const { id: bookingNo } = useParams();
      const [showToast, setShowToast] = useState(null);
      const [appDetailsToShow, setAppDetailsToShow] = useState({});
      const [showOptions, setShowOptions] = useState(false);
      const [enableAudit, setEnableAudit] = useState(false);
      const [businessService, setBusinessService] = useState("booking-refund");
    
      sessionStorage.setItem("chb", bookingNo);
      const { isLoading, isError, data: applicationDetails, error } = Digit.Hooks.chb.useChbApplicationDetail(t, tenantId, bookingNo);
      
      const {
        isLoading: updatingApplication,
        isError: updateApplicationError,
        data: updateResponse,
        error: updateError,
        mutate,
      } = Digit.Hooks.chb.useChbApplicationAction(tenantId);
      let workflowDetails = Digit.Hooks.useWorkflowDetails({
        tenantId: applicationDetails?.applicationData?.tenantId || tenantId,
        id: applicationDetails?.applicationData?.applicationData?.bookingNo,
        moduleCode: businessService,
        role: ["CHB_CEMP"],
      });

      const mutation = Digit.Hooks.chb.useChbCreateAPI(tenantId, false);
      const { isLoading: auditDataLoading, isError: isAuditError, data,refetch} = Digit.Hooks.chb.useChbSearch(
        {
          tenantId,
          filters: { bookingNo: bookingNo, audit: true },
        },
        // { enabled: enableAudit, select: (data) => data.PetRegistrationApplications?.filter((e) => e.status === "ACTIVE") }
      );

      const closeToast = () => {
        setShowToast(null);
      };

      useEffect(() => {
        if (applicationDetails) {
          setAppDetailsToShow(_.cloneDeep(applicationDetails));
        
        }
      }, [applicationDetails]);



      useEffect(() => {

        if (workflowDetails?.data?.applicationBusinessService && !(workflowDetails?.data?.applicationBusinessService === "booking-refund" && businessService === "booking-refund")) {
          setBusinessService(workflowDetails?.data?.applicationBusinessService);
        }
      }, [workflowDetails.data]);
      

      const { data: reciept_data, isLoading: recieptDataLoading } = Digit.Hooks.useRecieptSearch(
        {
          tenantId: tenantId,
          businessService: "chb-services",
          consumerCodes: appDetailsToShow?.applicationData?.applicationData?.bookingNo,
          isEmployee: false,
        },
        { enabled: appDetailsToShow?.applicationData?.applicationData?.bookingNo ? true : false }
      );
      async function getRecieptSearch({ tenantId, payments, ...params }) {
        let application = data?.hallsBookingApplication?.[0];
        let fileStoreId = application?.paymentReceiptFilestoreId
        if (!fileStoreId) {
        let response = { filestoreIds: [payments?.fileStoreId] };
        response = await Digit.PaymentService.generatePdf(tenantId, { Payments: [{ ...payments }] }, "chbservice-receipt");
        const updatedApplication = {
          ...application,
          paymentReceiptFilestoreId: response?.filestoreIds[0]
        };
        await mutation.mutateAsync({
          hallsBookingApplication: updatedApplication
        });
        fileStoreId = response?.filestoreIds[0];
        refetch();
        }
        const fileStore = await Digit.PaymentService.printReciept(tenantId, { fileStoreIds: fileStoreId });
        window.open(fileStore[fileStoreId], "_blank");
      };

      async function getPermissionLetter({ tenantId, payments, ...params }) {
        let application = data?.hallsBookingApplication?.[0];
        let fileStoreId = application?.permissionLetterFilestoreId;
        if (!fileStoreId) {
          const response = await Digit.PaymentService.generatePdf(
            tenantId,
            { hallsBookingApplication: [application] }, 
            "chbpermissionletter"
          );
          const updatedApplication = {
            ...application,
            permissionLetterFilestoreId: response?.filestoreIds[0]
          };
          await mutation.mutateAsync({
            hallsBookingApplication: updatedApplication
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
        label: t("CHB_FEE_RECIEPT"),
        onClick: () => getRecieptSearch({ tenantId: reciept_data?.Payments[0]?.tenantId, payments: reciept_data?.Payments[0] }),
      });

      if (reciept_data && reciept_data?.Payments.length > 0 && recieptDataLoading == false)
        dowloadOptions.push({
          label: t("CHB_PERMISSION_LETTER"),
          onClick: () => getPermissionLetter({ tenantId: reciept_data?.Payments[0]?.tenantId, payments: reciept_data?.Payments[0] }),
        });
    

      return (
        <div>
        <div className={"employee-application-details"} style={{ marginBottom: "15px" }}>
          <Header styles={{ marginLeft: "0px", paddingTop: "10px", fontSize: "32px" }}>{t("CHB_BOOKING_DETAILS")}</Header>
          <div style={{zIndex: "10",display:"flex",flexDirection:"row-reverse",alignItems:"center",marginTop:"-25px"}}>
            {dowloadOptions && dowloadOptions.length > 0 && (
              <MultiLink
                className="multilinkWrapper employee-mulitlink-main-div"
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
          <ApplicationDetailsTemplate
            applicationDetails={appDetailsToShow?.applicationData}
            isLoading={isLoading}
            isDataLoading={isLoading}
            applicationData={appDetailsToShow?.applicationData?.applicationData}
            mutate={mutate}
            workflowDetails={workflowDetails}
            businessService={businessService}
            moduleCode="chb-services"
            showToast={showToast}
            setShowToast={setShowToast}
            closeToast={closeToast}
            timelineStatusPrefix={""}
            forcedActionPrefix={"CHB"}
            statusAttribute={"state"}
            MenuStyle={{ color: "#FFFFFF", fontSize: "18px" }}
          />
          

        </div>
      );
    };

    export default React.memo(ApplicationDetails);
