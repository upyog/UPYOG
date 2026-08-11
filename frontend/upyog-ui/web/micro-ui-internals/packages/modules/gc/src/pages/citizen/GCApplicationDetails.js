import { Card, CardSubHeader, CardSectionHeader, Header, Loader, Row, StatusTable, SubmitBar, ActionBar, Modal, Toast, TextArea, CardText, CloseSvg, MultiLink } from "@nudmcdgnpm/digit-ui-react-components";
import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useParams } from "react-router-dom";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import GCWFApplicationTimeline from "../../pageComponents/GCWFApplicationTimeline";
import { downloadGCReceipt, downloadGCAcknowledgement, multiUnits } from "../../utils";

/**
 * GCApplicationDetails Component (Citizen)
 * 
 * Displays detailed information about a specific GC application for citizens,
 * including applicant details, property location, garbage specifications, and payment status.
 * 
 * Features:
 * - Fetches application and workflow details by application number from URL params
 * - Handles payment: fetches bill data and provides "Make Payment" button when pending
 * - Supports edit flow: shows "Edit Application" button when status is EDIT_APPLICATION
 * - Displays all details in organized sections with StatusTable rows
 * - Handles multiple data formats from the API (nested garbageAccount, flat structure, etc.)
 */
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

  const [showOptions, setShowOptions] = useState(false);
  const [selectedAction, setSelectedAction] = useState(null);
  const [comments, setComments] = useState("");
  const [showToast, setShowToast] = useState(null);

  useEffect(() => {
    if (showToast) {
      const timer = setTimeout(() => setShowToast(null), 10000);
      return () => clearTimeout(timer);
    }
  }, [showToast]);

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

  const { data: storeData } = Digit.Hooks.useStore.getInitData();
  const { tenants } = storeData || {};

  const { data: reciept_data, isLoading: recieptDataLoading } = Digit.Hooks.useRecieptSearch(
    { tenantId, businessService: "garbage-service", consumerCodes: applicationNo, isEmployee: false },
    { enabled: !!applicationNo }
  );

  const GCDocuments = Digit?.ComponentRegistryService?.getComponent("GCDocuments");

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

  const dueDate = appData?.dueDate || null;

  const downloadOptions = [];
  downloadOptions.push({
    label: t("GC_DOWNLOAD_ACKNOWLEDGEMENT"),
    onClick: () => downloadGCAcknowledgement(appData, tenants, t),
  });
  if (reciept_data?.Payments?.length > 0 && !recieptDataLoading) {
    downloadOptions.push({
      label: t("GC_FEE_RECEIPT"),
      onClick: () => downloadGCReceipt(reciept_data.Payments[0].tenantId, reciept_data.Payments[0]),
    });
  }

  const docs = appData?.documents || [];

  const handleMakePayment = () => {
    navigate(`/upyog-ui/citizen/payment/my-bills/garbage-service/${appNo}`);
  };

  if (isLoading || isWorkflowLoading) {
    return <Loader />;
  }

  if (!application) {
    return <div>{t("GC_APPLICATION_NOT_FOUND")}</div>;
  }

  // ---------- Additional Details ----------
  let additionalDetails = appData?.additionalDetails || appData?.additionalDetail || {};
  if (typeof additionalDetails === "string") {
    try {
      additionalDetails = JSON.parse(additionalDetails);
    } catch (e) {
      additionalDetails = {};
    }
  }

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
  const address = appData?.addresses?.[0] || {};
  const addressAdditional = address?.additionalDetail || {};
  const propertyLocation = appData?.propertyLocation || {};

  const propertyId = appData?.propertyId || propertyLocation?.propertyId;
  const pincode = address?.pincode || propertyLocation?.pincode;
  const city = address?.city || propertyLocation?.city || appData?.tenantId;
  const localityRaw = addressAdditional?.locality || propertyLocation?.locality;
  const localityText = typeof localityRaw === "string" ? t(localityRaw) : localityRaw?.name ? t(localityRaw.name) : null;
  const street = addressAdditional?.streetName || propertyLocation?.streetName;
  const houseNo = addressAdditional?.houseNo || propertyLocation?.houseNo;
  const buildingName = addressAdditional?.houseName || propertyLocation?.houseName;
  const addressLine1 = address?.address1 || propertyLocation?.addressline1;
  const addressLine2 = address?.address2 || propertyLocation?.addressline2;
  const landmark = addressAdditional?.landmark || propertyLocation?.landmark;

  // ---------- Garbage Specs ----------
  const specs = appData?.grbgCollectionUnits?.[0] || {};
  const garbageSpec = appData?.garbageSpecification || {};
  const oldGarbageId = appData?.grbgOldDetails?.oldGarbageId || garbageSpec?.oldGarbageId;
  const typeOfCollection = specs?.unitType || garbageSpec?.typeOfCollection;
  const ownerOrTenant = specs?.ownerType || garbageSpec?.propertyOwnerType;
  const category = specs?.category || garbageSpec?.category;
  const subCategory = specs?.subCategory || garbageSpec?.subCategory;
  const subCategoryType = specs?.subCategoryType || garbageSpec?.subCategoryType;
  const no_of_units = specs?.no_of_units || garbageSpec?.no_of_units;
  const specialCategory = specs?.specialCategory || garbageSpec?.specialCategory;
  const isInheritance = specs?.isInheritance || garbageSpec?.isInheritance;
  const specName = garbageSpec?.name || appData?.name;
  const specPhone = garbageSpec?.phoneNumber || appData?.mobileNumber;
  const specGender = garbageSpec?.gender || appData?.gender;
  const specEmail = garbageSpec?.email || appData?.emailId;
  // ---------- Render ----------
  return (
    <React.Fragment>
      <div>
        <div className="cardHeaderWithOptions" style={{ marginRight: "auto", maxWidth: "960px" }}>
          <Header styles={{ fontSize: "32px" }}>{t("GC_APPLICATION_DETAILS")}</Header>
          {downloadOptions.length > 0 && (
            <MultiLink
              className="multilinkWrapper"
              onHeadClick={() => setShowOptions(!showOptions)}
              displayOptions={showOptions}
              options={downloadOptions}
            />
          )}
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
            {dueDate && <Row className="border-none" label={t("GC_DUE_DATE")} text={dueDate} />}
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
            <Row className="border-none" label={t("GC_OLD_GARBAGE_ID")} text={oldGarbageId || t("CS_NA")} />
            <Row className="border-none" label={t("GC_TYPE_OF_COLLECTION")} text={typeOfCollection ? t(typeOfCollection) : t("CS_NA")} />
            <Row className="border-none" label={t("GC_OWNER_OR_TENANT")} text={ownerOrTenant ? t(ownerOrTenant) : t("CS_NA")} />
            <Row className="border-none" label={t("GC_NAME")} text={specName || t("CS_NA")} />
            <Row className="border-none" label={t("GC_PHONE_NUMBER")} text={specPhone || t("CS_NA")} />
            <Row className="border-none" label={t("GC_GENDER")} text={specGender ? t(specGender) : t("CS_NA")} />
            <Row className="border-none" label={t("GC_EMAIL")} text={specEmail || t("CS_NA")} />
            <Row className="border-none" label={t("GC_CATEGORY")} text={category ? t(category) : t("CS_NA")} />
            <Row className="border-none" label={t("GC_SUB_CATEGORY")} text={subCategory ? t(subCategory) : t("CS_NA")} />
            <Row className="border-none" label={t("GC_SUB_CATEGORY_TYPE")} text={subCategoryType ? t(subCategoryType) : t("CS_NA")} />
            {multiUnits.includes(typeOfCollection) && (
              <Row className="border-none" label={t("GC_NO_OF_UNITS")} text={no_of_units || t("CS_NA")} />
            )}
            <Row className="border-none" label={t("GC_SPECIAL_CATEGORY")} text={specialCategory ? t(specialCategory) : t("CS_NA")} />
            <Row className="border-none" label={t("GC_IS_INHERITANCE")} text={isInheritance ? t("YES") : t("NO")} />
          </StatusTable>

          {/* Documents */}
          {docs.length > 0 && (
            <>
              <CardSubHeader style={{ fontSize: "24px" }}>{t("GC_GARBAGE_DOCUMENTS")}</CardSubHeader>
              <StatusTable>
                <Card className="chb-doc-card">
                  {docs.map((doc, index) => (
                    <div key={`doc-${index}`} className="chb-doc-item">
                      <div>
                        <CardSectionHeader>{t("GC_" + (doc?.documentType?.split(".").slice(0, 2).join("_")))}</CardSectionHeader>
                        <GCDocuments value={docs} Code={doc?.documentType} index={index} />
                      </div>
                    </div>
                  ))}
                </Card>
              </StatusTable>
            </>
          )}

          <GCWFApplicationTimeline application={appData} />


        </Card>

        {appStatus === "EDIT_APPLICATION" && (
          <ActionBar>
            <SubmitBar
              label={t("GC_EDIT_APPLICATION")}
              onSubmit={() =>
                navigate(`/upyog-ui/citizen/gc/edit/${encodeURIComponent(appNo)}`)
              }
            />
          </ActionBar>
        )}

        {appStatus === "PENDING_FOR_PAYMENT" && (
          <ActionBar>
            <SubmitBar label={t("CS_APPLICATION_DETAILS_MAKE_PAYMENT")} onSubmit={handleMakePayment} />
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