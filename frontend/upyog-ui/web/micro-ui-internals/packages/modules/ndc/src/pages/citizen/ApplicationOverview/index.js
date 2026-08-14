import {
  Row,
  StatusTable,
  Card,
  CardSubHeader,
  ActionBar,
  SubmitBar,
  MultiLink
} from "@nudmcdgnpm/digit-ui-react-components";
import React, { useEffect, useState, useRef } from "react";
import { useTranslation } from "react-i18next";
import { useParams } from "react-router-dom";
import NDCDocument from "../../../pageComponents/NDCDocument";
import getAcknowledgementData from "../../../getAcknowlegment";
import NewApplicationTimeline from "../../../../../templates/ApplicationDetails/components/NewApplicationTimeline";
import { EmployeeData } from "../../../utils";
import { Loader } from "../../../components/Loader";

// This component is the overview page for the NDC application. 
// It displays the details of the application along with the workflow status and timeline. 
// It also provides options to download the application details and receipt if available. 
// The user can also edit the application if it is in the initiated or citizen action required state.
// The component also handles the download of the receipt and certificate.
const CitizenApplicationOverview = () => {
  const { id } = useParams();
  const { t } = useTranslation();
  const navigate = Digit.Hooks.useCustomNavigate();
  const tenantId = Digit.ULBService.getCitizenCurrentTenant(true) || Digit.ULBService.getCurrentTenantId();
  const state = tenantId?.split(".")[0];
  const [appDetails, setAppDetails] = useState({});
  const [approver, setApprover] = useState(null);
  const[approverStatement, setApproverStatement]= useState(null)
  const [showOptions, setShowOptions] = useState(false);
  
  const [displayData, setDisplayData] = useState({});
  const [getLoader, setLoader] = useState(false);

  const [isDetailsLoading, setIsDetailsLoading] = useState(false);


  const { isLoading, data: applicationDetails } = Digit.Hooks.ndc.useSearchEmployeeApplication({ applicationNo: id }, tenantId);

  const workflowDetails = Digit.Hooks.useWorkflowDetails({
    tenantId: tenantId,
    id: id,
    moduleCode: "ndc-services",
  });

  useEffect(() => {
    if (workflowDetails) {
      const approveInstance = workflowDetails?.data?.processInstances?.find((pi) => pi?.action === "APPROVE" || pi?.action === "REJECT");
      const name = approveInstance?.assigner?.name || "NA";          
      const status = applicationDetails?.Applications?.[0]?.applicationStatus;
      setApproverStatement(status ? `${t(status)} By` : "");
      setApprover(name);
    }
  }, [workflowDetails]);

  const empData = EmployeeData(tenantId, approver);

  const { data: storeData } = Digit.Hooks.useStore.getInitData();
  const { tenants } = storeData || {};
  let user = Digit.UserService.getUser();
  const menuRef = useRef();

  if (window.location.href.includes("/obps") || window.location.href.includes("/noc")) {
    const userInfos = sessionStorage.getItem("Digit.citizen.userRequestObject");
    const userInfo = userInfos ? JSON.parse(userInfos) : {};
    user = userInfo?.value;
  }
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
      }else {
        response = await Digit.PaymentService.generatePdf(
          tenantId,
          { Payments: [
              {
                ...(payments || {}),
                ...application,
              },
            ], },
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

  if(applicationDetails?.Applications?.[0]?.applicationStatus === "APPROVED" || applicationDetails?.Applications?.[0]?.applicationStatus === "REJECTED"){
    dowloadOptions.push({
    label: t("DOWNLOAD_CERTIFICATE"),
    onClick: () => handleDownloadPdf(),
  });
  }
  if (reciept_data && reciept_data?.Payments.length > 0 && !recieptDataLoading) {
    dowloadOptions.push({
      label: t("PTR_FEE_RECIEPT"),
      onClick: () => getRecieptSearch({ tenantId: reciept_data?.Payments[0]?.tenantId, payments: reciept_data?.Payments[0] }),
    });
  }
  
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
      const primaryOwner = ndcObject?.owners?.find((owner) => owner?.isPrimaryOwner) || ndcObject?.owners?.[0]; // fallback if none marked

      const applicantData = {
        name: primaryOwner?.name,
        mobile: primaryOwner?.mobileNumber,
        email: primaryOwner?.emailId,
        address: primaryOwner?.permanentAddress,
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
      }));

      setDisplayData({ applicantData, Documents, NdcDetails });
    }
  }, [applicationDetails?.Applications]);

  useEffect(() => {
    if (applicationDetails) {
      setIsDetailsLoading(true);
      const { Applicant: details } = applicationDetails?.Applications?.[0];
      setAppDetails({ ...applicationDetails, applicationDetails: [{ title: "NOC_DETAILS_SUMMARY_LABEL" }] });
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
      }, 0);
    } catch (error) {
      console.error("Error generating acknowledgement:", error);
    } finally {
      setLoader(false);
    }
  };

  const [getPropertyId, setPropertyId] = useState(null);

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

  const ownerForName = propertyDetailsFetch?.Properties?.[0]?.owners || [];
  const ownerNames = ownerForName
    ?.map((owner) => owner?.name)
    ?.filter(Boolean)
    ?.join(", ");

  return (
    <div className={"employee-main-application-details"}>
      <div className="ndc-application-overview">
        {/* <Header styles={{ fontSize: "32px" }}>{t("NDC_APP_OVER_VIEW_HEADER")}</Header> */}

        <div className="ndc-flex-end">
          <div className="cardHeaderWithOptions ral-app-details-header">
            { getLoader && <Loader />}
            {dowloadOptions && dowloadOptions.length > 0 && (
              <MultiLink
                className="multilinkWrapper"
                onHeadClick={() => setShowOptions(!showOptions)}
                displayOptions={showOptions}
                options={dowloadOptions}
              />
            )}
          </div>
        </div>

        {(applicationDetails?.Applications?.[0]?.applicationStatus == "INITIATED" ||
          applicationDetails?.Applications?.[0]?.applicationStatus == "CITIZENACTIONREQUIRED") && (
          <ActionBar>
            <SubmitBar
              label={t("COMMON_EDIT")}
              onSubmit={() => {
                const id = applicationDetails?.Applications?.[0]?.applicationNo;
                navigate(`/upyog-ui/citizen/ndc/new-application/${id}`);
              }}
            />
          </ActionBar>
        )}
      </div>

      <Card className="ndc_card_main">
        <CardSubHeader className="ndc_label">{t("NDC_APPLICATION_DETAILS_OVERVIEW")}</CardSubHeader>
        <StatusTable>
          <Row label={t(`Name`)} text={ownerNames} />
          {displayData?.applicantData &&
            Object.entries(displayData?.applicantData)
              ?.filter(([key]) => key !== "name")
              ?.map(([key, value]) => (
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

      <Card className="ndc_card_main">
        <CardSubHeader className="ndc_label">{t("NDC_APPLICATION_NDC_DETAILS_OVERVIEW")}</CardSubHeader>
        {displayData?.NdcDetails?.map((detail, index) => {
          const isRed = detail.dueAmount > 0;
          return (
            <div className="ndc-application-overview-table" key={index}>
              <StatusTable>
                <Row label={t("NDC_BUSINESS_SERVICE")} text={t(`${detail.businessService}`) || detail.businessService} />
                {/* <Row label={t("Name")} text={t(`${detail.businessService}`) || detail.businessService} /> */}
                <Row label={t("NDC_CONSUMER_CODE")} text={detail.consumerCode || "N/A"} />
                {/* <Row label={t("NDC_STATUS")} text={t(detail.status) || detail.status} /> */}
                <div className={isRed ? 'ndc-due-red' : 'ndc-due-default'}>
                  <Row label={t("NDC_DUE_AMOUNT")} text={detail.dueAmount?.toString() || "0"} />
                </div>
                <Row label={t("NDC_PROPERTY_TYPE")} text={t(detail.propertyType) || detail.propertyType} />
                {detail?.businessService == "NDC_PROPERTY_TAX" && propertyDetailsFetch?.Properties && (
                  <>
                    <Row
                      label={t("CHB_DISCOUNT_REASON")}
                      text={t(
                        `${
                          applicationDetails?.Applications?.[0]?.reason === "OTHERS"
                            ? applicationDetails?.Applications?.[0]?.NdcDetails?.find((item) => item?.businessService === "PT")?.additionalDetails
                                ?.reason
                            : applicationDetails?.Applications?.[0]?.reason
                        }`
                      )}
                    />
                    <Row label={t("City")} text={propertyDetailsFetch?.Properties?.[0]?.address?.city || "N/A"} />
                    <Row label={t("House No")} text={propertyDetailsFetch?.Properties?.[0]?.address?.doorNo || "N/A"} />
                    <Row label={t("Colony Name")} text={propertyDetailsFetch?.Properties?.[0]?.address?.buildingName || "N/A"} />
                    <Row label={t("Street Name")} text={propertyDetailsFetch?.Properties?.[0]?.address?.street || "N/A"} />
                    {/* <Row label={t("Mohalla")} text={propertyDetailsFetch?.Properties?.[0]?.address?.city} /> */}
                    <Row label={t("Pincode")} text={propertyDetailsFetch?.Properties?.[0]?.address?.pincode || "N/A"} />
                    {/* <Row label={t("Existing Pid")} text={propertyDetailsFetch?.Properties?.[0]?.address?.city} /> */}
                    <Row label={t("Survey Id/UID")} text={propertyDetailsFetch?.Properties?.[0]?.surveyId || "N/A"} />
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
            </div>
          );
        })}
      </Card>


      <Card className="ndc_card_main">
        <CardSubHeader className="ndc_label">{t("NDC_APPLICATION_DOCUMENTS_OVERVIEW")}</CardSubHeader>
        <div>
          {Array.isArray(displayData?.Documents) && displayData?.Documents?.length > 0 ? (
            <NDCDocument value={{ workflowDocs: displayData?.Documents }} />
          ) : (
            <div>{t("TL_NO_DOCUMENTS_MSG")}</div>
          )}
        </div>
      </Card>
      <NewApplicationTimeline workflowDetails={workflowDetails} t={t} />
      {(isLoading || isDetailsLoading || checkLoading || getLoader) && <Loader page={true} />}
    </div>
  );
};

export default CitizenApplicationOverview;
