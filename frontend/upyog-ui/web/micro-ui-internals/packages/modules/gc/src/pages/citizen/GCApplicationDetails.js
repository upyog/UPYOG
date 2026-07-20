import { Card, CardSubHeader, Header, Loader, Row, StatusTable, SubmitBar, ActionBar, Modal, Toast, TextArea, CardText, CloseSvg } from "@nudmcdgnpm/digit-ui-react-components";
import React, { useEffect, useState, useRef } from "react";
import { useTranslation } from "react-i18next";
import { useParams } from "react-router-dom";
import { useMutation, useQueryClient } from "@tanstack/react-query";

// GC Application Details Component
// This component displays detailed information about a specific GC application,
// including applicant details, property location, garbage specifications, and payment status.

const GCApplicationDetails = () => {
  const { t } = useTranslation();
  const navigate = Digit.Hooks.useCustomNavigate();
  const params = useParams();
  
  // Reconstruct application number from URL params
  let reconstructedAppNo = params.applicationNo;
  if (params["*"]) {
    reconstructedAppNo = `${params.applicationNo}/${params["*"]}`;
  }
  const applicationNo = decodeURIComponent(reconstructedAppNo);
  const tenantId = Digit.ULBService.getCitizenCurrentTenant(true) || Digit.ULBService.getCurrentTenantId();

  const [userType, setUserType] = useState("citizen");
  const [billData, setBillData] = useState(null);
  const [paymentStatus, setPaymentStatus] = useState("CHECKING");
  const isMountedRef = useRef(true);

  const [selectedAction, setSelectedAction] = useState(null);
  const [comments, setComments] = useState("");
  const [showToast, setShowToast] = useState(null);

  useEffect(() => {
    if (showToast) {
      const timer = setTimeout(() => setShowToast(null), 10000);
      return () => clearTimeout(timer);
    }
  }, [showToast]);

  useEffect(() => {
    const currentPath = navigate.location?.pathname || window.location.pathname;
    if (currentPath.includes("/employee/")) {
      setUserType("employee");
    } else {
      setUserType("citizen");
    }
    return () => {
      isMountedRef.current = false;
    };
  }, [navigate.location?.pathname]);

  const searchCriteria = {
    searchCriteriaGarbageAccount: {
      applicationNumber: [applicationNo],
    },
  };

  const { isLoading, data: gcData } = Digit.Hooks.gc.useGCSearch(
    { tenantId, data: searchCriteria, filters: { applicationNumber: [applicationNo] } },
    { enabled: !!applicationNo, cacheTime: 0, staleTime: 0 }
  );

  // The API should now return a single application in the array
  const applicationList = gcData?.garbageAccounts || gcData?.GarbageApplications || gcData?.data || [];
  const application = applicationList.length > 0 ? applicationList[0] : null;

  const businessService = application?.businessService || "garbage-service";

  const { isLoading: isWorkflowLoading, data: workflowDetails } = Digit.Hooks.useWorkflowDetails({
    tenantId: tenantId,
    id: applicationNo, // Use the application number from the URL directly
    moduleCode: businessService,
    config: { staleTime: 0 }
  });

  const queryClient = useQueryClient();

  const { mutateAsync } = useMutation({ mutationFn: (data) => Digit.GCServices.update(data, tenantId) });

  const CloseBtn = (props) => {
    return (
      <div onClick={props.onClick} style={{ cursor: "pointer", padding: "5px" }}>
        <CloseSvg />
      </div>
    );
  };

  const submitWorkflowAction = async () => {
    try {
      const payload = {
        garbageAccounts: [
          {
            ...application,
            workflow: {
              action: selectedAction.action,
              comments: comments,
              assignes: []
            }
          }
        ]
      };

      await mutateAsync(payload);

      queryClient.invalidateQueries({ queryKey: ["GC_SEARCH_APPLICATIONS"] });
      queryClient.invalidateQueries({ queryKey: ["workFlowDetails"] });
      setShowToast({ key: "success", label: t("GC_ACTION_SUCCESS") });
      setSelectedAction(null);
      setComments("");
      setTimeout(() => window.location.reload(), 1500);
    } catch (error) {
      setShowToast({ key: "error", label: t("GC_ACTION_FAILED") });
    }
  };

  // Step 2: flatten any wrapper (must come before applicationDetails)
  let appData = application?.garbageAccount || application || {};
  if (typeof appData === "string") {
    try {
      appData = JSON.parse(appData);
    } catch (e) {
      appData = {};
    }
  }

  // Step 3: derive nested application details (grbgApplication lives inside the account object)
  const applicationDetails = appData?.grbgApplication || appData?.GarbageApplication || {};

  // Application number — prefer nested applicationNo, fall back to top-level grbgApplicationNumber
  const appNo =
    applicationDetails?.applicationNo ||
    appData?.grbgApplicationNumber ||
    appData?.applicationNo ||
    t("CS_NA");

  // Status — prefer nested status, fall back to top-level status
  const appStatus =
    applicationDetails?.status ||
    appData?.status ||
    appData?.applicationStatus ||
    t("CS_NA");

  useEffect(() => {
    const fetchBillData = async () => {
      if (!isMountedRef.current) return;
      try {
        const result = await Digit.PaymentService.fetchBill(tenantId, {
          businessService: "gc-services",
          consumerCode: applicationNo,
        });
        if (isMountedRef.current) {
          setBillData(result);
          if (result?.Bill?.[0]?.totalAmount > 0) {
            setPaymentStatus("PENDING");
          } else {
            setPaymentStatus("PAID");
          }
        }
      } catch (error) {
        if (isMountedRef.current) {
          setPaymentStatus("UNKNOWN");
        }
      }
    };

    if (application && appStatus === "PENDINGPAYMENT") {
      fetchBillData();
    } else if (application) {
      setPaymentStatus(appStatus === "APPROVED" ? "PAID" : "NA");
    }
  }, [application, appStatus, tenantId]);

  const handleMakePayment = () => {
    navigate({
      pathname: `/upyog-ui/citizen/payment/collect/gc-services/${encodeURIComponent(appNo)}/${tenantId}?tenantId=${tenantId}`,
    });
  };

  if (isLoading || isWorkflowLoading) {
    return <Loader />;
  }

  if (!application) {
    return <div>{t("GC_APPLICATION_NOT_FOUND")}</div>;
  }

  // ---------- Additional Details ----------
  /* This piece of code is not yet in but we can use it in future, If we have to add any additional details 
  let additionalDetails = appData?.additionalDetails || appData?.additionalDetail || {};
  if (typeof additionalDetails === "string") {
    try {
      additionalDetails = JSON.parse(additionalDetails);
    } catch (e) {
      additionalDetails = {};
    }
  }*/

  // ---------- Applicant / Owner Details ----------
  const rawOwners =
    appData?.additionalDetail?.applicantDetails ||
    appData?.additionalDetails?.applicantDetails ||
    appData?.applicantDetails ||
    [];
  const owners = Array.isArray(rawOwners) ? rawOwners : rawOwners ? [rawOwners] : [];

  const ownerNames =
    owners
      ?.map((o) => o?.name || o?.applicantName || o?.ownerName)
      ?.filter(Boolean)
      ?.join(", ") ||
    appData?.name ||
    t("CS_NA");


  const mobileNumbers =
    owners?.map((o) => o?.mobileNumber)?.filter(Boolean)?.join(", ") ||
    appData?.mobileNumber ||
    t("CS_NA");

  const emails =
    owners
      ?.map((o) => o?.emailId || o?.email || o?.emailAddress)
      ?.filter(Boolean)
      ?.join(", ") ||
    appData?.emailId ||
    appData?.email ||
    additionalDetails?.emailId ||
    appData?.user?.emailId;

  const altMobileNumbers =
    owners
      ?.map((o) => o?.alternateNumber || o?.altMobileNumber || o?.altMobileNo || o?.alternateMobileNumber)
      ?.filter(Boolean)
      ?.join(", ") ||
    appData?.alternateNumber ||
    appData?.altMobileNumber ||
    additionalDetails?.alternateNumber;

  // ---------- Address ----------
  let address = appData?.addresses?.[0] || appData?.address || appData?.propertyLocation || {};
  if (Array.isArray(address)) address = address[0] || {};

  const addressAdditional = address?.additionalDetail || address?.additionalDetails || {};

  const propertyId = address?.propertyId || appData?.propertyId || additionalDetails?.propertyId;
  const pincode = address?.pincode || address?.pinCode || address?.zipCode || appData?.pincode;
  const city =
    address?.city ||
    address?.cityCode ||
    address?.cityName ||
    appData?.city ||
    appData?.tenantId?.split(".")?.[1] ||
    appData?.tenantId;

  const localityObj =
    address?.locality ||
    addressAdditional?.locality ||
    address?.mohalla ||
    address?.mohallaObj ||
    appData?.locality;
  const localityText = localityObj?.name
    ? t(localityObj.name)
    : localityObj?.code
      ? t(localityObj.code)
      : typeof localityObj === "string"
        ? t(localityObj)
        : null;

  const street =
    address?.street ||
    addressAdditional?.streetName ||
    address?.streetName ||
    address?.roadName ||
    appData?.streetName ||
    appData?.street;

  const houseNo =
    address?.doorNo ||
    addressAdditional?.houseNo ||
    address?.houseNo ||
    address?.buildingNo ||
    address?.plotNo ||
    appData?.doorNo ||
    appData?.houseNo;

  const buildingName =
    address?.buildingName ||
    addressAdditional?.houseName ||
    address?.premise ||
    address?.premiseName ||
    appData?.buildingName;

  const addressLine1 = address?.addressLine1 || address?.address1 || appData?.addressLine1;
  const addressLine2 = address?.addressLine2 || address?.address2 || appData?.addressLine2;
  const landmark = address?.landmark || addressAdditional?.landmark || address?.landMark || appData?.landmark;

  // ---------- Garbage Specs ----------
  let specs = appData?.grbgCollectionUnits?.[0] || appData?.garbageSpecification || {};
  if (Array.isArray(specs)) specs = specs[0] || {};

  const typeOfCollection =
    specs?.typeOfCollection ||
    specs?.unitType ||
    specs?.collectionType ||
    specs?.collectionMethod ||
    specs?.type;

  const category = specs?.category || specs?.propertyCategory || specs?.garbageCategory;
  const subCategory = specs?.subCategory || specs?.propertySubCategory || specs?.garbageSubCategory;

  const ownerOrTenant =
    appData?.isOwner !== undefined
      ? appData.isOwner
        ? "Owner"
        : "Tenant"
      : specs?.propertyOwnerType ||
      specs?.ownerOrTenant ||
      specs?.occupancyType ||
      specs?.ownershipType ||
      specs?.tenantOrOwner;

  const estimatedQuantity =
    specs?.estimatedQuantity ??
    specs?.no_of_units ??
    specs?.wasteQuantity ??
    specs?.quantity ??
    specs?.expectedQuantity ??
    specs?.wasteSize ??
    specs?.totalWeight;

  const collectionFrequency =
    specs?.collectionFrequency || specs?.frequency || specs?.pickupFrequency || specs?.duration;

  const wasteType =
    specs?.wasteType || specs?.typeOfWaste || specs?.garbageType || specs?.wasteCategory;

  const specialRequest =
    specs?.specialRequest ||
    specs?.remarks ||
    specs?.description ||
    specs?.comments ||
    specs?.specialInstructions;
  // ---------- Render ----------
  return (
    <React.Fragment>
      <div>
        <div className="cardHeaderWithOptions" style={{ marginRight: "auto", maxWidth: "960px" }}>
          <Header styles={{ fontSize: "32px" }}>{t("GC_APPLICATION_DETAILS")}</Header>
        </div>

        <Card>
          {/* Application Summary */}
          <CardSubHeader style={{ fontSize: "24px" }}>{t("GC_APPLICATION_SUMMARY")}</CardSubHeader>
          <StatusTable>
            <Row className="border-none" label={t("GC_APPLICATION_NUMBER_LABEL")} text={appNo || t("CS_NA")} />
            <Row
              className="border-none"
              label={t("GC_APPLICATION_STATUS_LABEL")}
              text={appStatus ? t(`GC_STATUS_${appStatus}`) : t("CS_NA")}
            />
          </StatusTable>

          {/* Applicant Details */}
          <CardSubHeader style={{ fontSize: "24px" }}>{t("ES_APPLICANT_DETAILS")}</CardSubHeader>
          <StatusTable>
            <Row className="border-none" label={t("GC_APPLICANT_NAME")} text={ownerNames || t("CS_NA")} />
            <Row className="border-none" label={t("GC_MOBILE_NUMBER")} text={mobileNumbers || t("CS_NA")} />
            <Row className="border-none" label={t("GC_ALT_MOBILE_NUMBER")} text={altMobileNumbers || t("CS_NA")} />
            <Row className="border-none" label={t("GC_EMAIL_ID")} text={emails || t("CS_NA")} />
          </StatusTable>

          {/* Property Location */}
          <CardSubHeader style={{ fontSize: "24px" }}>{t("GC_PROPERTY_LOCATION_DETAILS")}</CardSubHeader>
          <StatusTable>
            <Row className="border-none" label={t("GC_PROPERTY_ID")} text={propertyId || t("CS_NA")} />
            <Row className="border-none" label={t("GC_PINCODE")} text={pincode || t("CS_NA")} />
            <Row className="border-none" label={t("GC_CITY")} text={city ? t(city) : t("CS_NA")} />
            <Row className="border-none" label={t("GC_LOCALITY")} text={localityText || t("CS_NA")} />
            <Row className="border-none" label={t("GC_STREET_NAME")} text={street || t("CS_NA")} />
            <Row className="border-none" label={t("GC_HOUSE_NO")} text={houseNo || t("CS_NA")} />
            <Row className="border-none" label={t("GC_BUILDING_NAME")} text={buildingName || t("CS_NA")} />
            <Row className="border-none" label={t("GC_ADDRESS_LINE1")} text={addressLine1 || t("CS_NA")} />
            <Row className="border-none" label={t("GC_ADDRESS_LINE2")} text={addressLine2 || t("CS_NA")} />
            <Row className="border-none" label={t("GC_LANDMARK")} text={landmark || t("CS_NA")} />
          </StatusTable>

          {/* Garbage Specifications */}
          <CardSubHeader style={{ fontSize: "24px" }}>{t("GC_GARBAGE_SPECIFICATIONS")}</CardSubHeader>
          <StatusTable>
            <Row
              className="border-none"
              label={t("GC_TYPE_OF_COLLECTION")}
              text={typeOfCollection ? t(typeOfCollection) : t("CS_NA")}
            />
            <Row className="border-none" label={t("GC_CATEGORY")} text={category ? t(category) : t("CS_NA")} />
            <Row
              className="border-none"
              label={t("GC_SUB_CATEGORY")}
              text={subCategory ? t(subCategory) : t("CS_NA")}
            />
            <Row
              className="border-none"
              label={t("GC_OWNER_OR_TENANT")}
              text={ownerOrTenant ? t(ownerOrTenant) : t("CS_NA")}
            />
            <Row
              className="border-none"
              label={t("GC_ESTIMATED_QUANTITY")}
              text={
                estimatedQuantity !== null && estimatedQuantity !== undefined
                  ? estimatedQuantity
                  : t("CS_NA")
              }
            />
            <Row
              className="border-none"
              label={t("GC_COLLECTION_FREQUENCY")}
              text={collectionFrequency ? t(collectionFrequency) : t("CS_NA")}
            />
            <Row
              className="border-none"
              label={t("GC_WASTE_TYPE")}
              text={wasteType ? t(wasteType) : t("CS_NA")}
            />
            <Row className="border-none" label={t("GC_SPECIAL_REQUEST")} text={specialRequest || t("CS_NA")} />
          </StatusTable>

          {/* Payment Details — only shown when pending */}
          {appStatus === "PENDINGPAYMENT" && (
            <>
              <CardSubHeader style={{ fontSize: "24px" }}>{t("GC_PAYMENT_DETAILS")}</CardSubHeader>
              <StatusTable>
                <Row
                  className="border-none"
                  label={t("GC_TOTAL_AMOUNT")}
                  text={
                    paymentStatus === "PENDING" ? (
                      <span>
                        ₹ {billData?.Bill?.[0]?.totalAmount || t("CS_NA")}{" "}
                        <strong style={{ color: "#a82227" }}>({t("PENDING_PAYMENT")})</strong>
                      </span>
                    ) : (
                      t("CS_NA")
                    )
                  }
                />
              </StatusTable>
            </>
          )}
        </Card>

        {/* Edit Application action bar — citizen only, when status is EDIT_APPLICATION */}
        {appStatus === "EDIT_APPLICATION" && userType === "citizen" && (
          <ActionBar>
            <SubmitBar
              label={t("GC_EDIT_APPLICATION")}
              onSubmit={() =>
                navigate(`/upyog-ui/citizen/gc/edit/${encodeURIComponent(appNo)}`)
              }
            />
          </ActionBar>
        )}

        {/* Make Payment action bar — citizen only */}
        {appStatus === "PENDINGPAYMENT" && userType === "citizen" && (
          <ActionBar>
            <SubmitBar label={t("CS_APPLICATION_DETAILS_MAKE_PAYMENT")} onSubmit={handleMakePayment} />
          </ActionBar>
        )}

        {/* Workflow Action Bar — employee only */}
        {userType === "employee" && workflowDetails?.actionState?.nextActions?.length > 0 && (
          <ActionBar>
            {workflowDetails.actionState.nextActions.map((action, index) => (
              <SubmitBar key={index} label={t(`WF_EMPLOYEE_GC_${action.action}`)} onSubmit={() => setSelectedAction(action)} />
            ))}
          </ActionBar>
        )}

        {selectedAction && (
          <Modal
            headerBarMain={<h1 className="heading-m">{t(`WF_EMPLOYEE_GC_${selectedAction.action}`)}</h1>}
            headerBarEnd={<CloseBtn onClick={() => setSelectedAction(null)} />}
            actionCancelLabel={t("CS_COMMON_CANCEL")}
            actionCancelOnSubmit={() => setSelectedAction(null)}
            actionSaveLabel={t("CS_COMMON_SUBMIT")}
            actionSaveOnSubmit={submitWorkflowAction}
          >
            <Card style={{ padding: "0px", margin: "0px", boxShadow: "none" }}>
              <CardText>{t("WF_COMMON_COMMENTS")}</CardText>
              <TextArea value={comments} onChange={(e) => setComments(e.target.value)} />
            </Card>
          </Modal>
        )}

        {showToast && (
          <Toast
            error={showToast.key === "error"}
            label={showToast.label}
            onClose={() => setShowToast(null)}
          />
        )}
      </div>
    </React.Fragment>
  );
};

export default GCApplicationDetails;