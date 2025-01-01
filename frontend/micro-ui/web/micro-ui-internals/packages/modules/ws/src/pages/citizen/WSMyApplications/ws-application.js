import { Card, KeyNote, SubmitBar, Loader } from "@nudmcdgnpm/digit-ui-react-components";
import React from "react";
import { useTranslation } from "react-i18next";
import { Link } from "react-router-dom";
import { getAddress } from "../../../utils/index";
import _ from "lodash";
import { stringReplaceAll, convertEpochToDate } from "../../../utils";

const WSApplication = ({ application }) => {

  const { t } = useTranslation();
  let encodeApplicationNo = encodeURI(application.applicationNo)
  // let workflowDetails = Digit.Hooks.useWorkflowDetails({
  //    tenantId: application?.tenantId,
  //    id: application?.applicationNo,
  //    moduleCode: "WS",
  //    config: {
  //      enabled: !!application?.applicationNo
  //    }
  //  });
   
   let filter1 = { tenantId: application.tenantId, applicationNumber: application.applicationNo };
  const { isLoading, isError, error, data } = Digit.Hooks.ws.useMyApplicationSearch(
    { filters: filter1, BusinessService: application.applicationNo?.includes("SW") ? "SW" : "WS" },
    { filters: filter1, privacy: Digit.Utils.getPrivacyObject() }
  );
   const { isLoading: isPTLoading, isError: isPTError, error: PTerror, data: PTData } = Digit.Hooks.pt.usePropertySearch(
    { filters: { propertyIds: application?.propertyId } },
    { filters: { propertyIds: application?.propertyId }, privacy: Digit.Utils.getPrivacyObject() }
  );
  const businessService = application?.applicationNo?.includes("SW") ? (application?.applicationNo?.includes("DC") ? "SW" : "SW.ONE_TIME_FEE") : (application?.applicationNo?.includes("DC")? "WS" :"WS.ONE_TIME_FEE")
  const fetchBillParams = { consumerCode: application?.applicationNo?.includes("DC") ? application?.connectionNo : application?.connectionNo };
  if (isLoading) {
    return <Loader />;
  }
  return (
    <Card>
    <KeyNote keyValue={t("WS_MYCONNECTIONS_APPLICATION_NO")} note={application?.applicationNo} />
    <KeyNote keyValue={t("WS_SERVICE_NAME")} note={t(`WS_APPLICATION_TYPE_${application?.applicationType}`)} />
    <KeyNote keyValue={t("WS_CONSUMER_NAME")} note={application?.connectionHolders?.map((owner) => owner.name).join(",") || application?.property?.owners?.sort((a,b)=> a?.additionalDetails?.ownerSequence- b?.additionalDetails?.ownerSequence).map((owner) => owner.name).join(",") || t("CS_NA")} />
    <KeyNote keyValue={t("WS_PROPERTY_ID")} note={application?.propertyId || t("CS_NA")} />
    <KeyNote keyValue={t("WS_STATUS")} note={t(`CS_${application?.applicationStatus}`) || t("CS_NA")} />
    <KeyNote keyValue={t("WS_SLA")} note={Math.round(application?.sla / (24 * 60 * 60 * 1000)) ? `${Math.round(application?.sla / (24 * 60 * 60 * 1000))} Days` : t("CS_NA")} /> 
    <KeyNote 
      keyValue={t("WS_PROPERTY_ADDRESS")} 
      note={getAddress(application?.property?.address, t)} 
      privacy={{
        uuid:application?.property?.owners?.[0]?.uuid, 
        fieldName: ["doorNo" , "street" , "landmark"], 
        model: "Property",showValue: true,
        loadData: {
          serviceName: "/property-services/property/_search",
          requestBody: {},
          requestParam: { tenantId : application?.tenantId, propertyIds : application?.propertyId },
          jsonPath: "Properties[0].address.street",
          isArray: false,
          d: (res) => {
            let resultString = (_.get(res,"Properties[0].address.doorNo") ?  `${_.get(res,"Properties[0].address.doorNo")}, ` : "") + (_.get(res,"Properties[0].address.street")? `${_.get(res,"Properties[0].address.street")}, ` : "") + (_.get(res,"Properties[0].address.landmark") ? `${_.get(res,"Properties[0].address.landmark")}`:"")
            return resultString;
          }
        },
       }}
      /> 
      <Link to={`/digit-ui/citizen/ws/connection/application/${encodeApplicationNo}`}>
        <SubmitBar label={t("WS_VIEW_DETAILS_LABEL")} />
      </Link>
      {application?.applicationStatus === "PENDING_FOR_PAYMENT"  ? (
            <Link
              to={{
                pathname: `/digit-ui/citizen/payment/my-bills/${
                  businessService
                }/${application?.applicationNo?.includes("DC") ? (stringReplaceAll(application?.connectionNo, "/", "+") || stringReplaceAll(application?.connectionNo, "/", "+")) :
                  (stringReplaceAll(application?.applicationNo, "/", "+") ||
                  stringReplaceAll(application?.applicationNo, "/", "+"))
                }?workflow=WNS&tenantId=${application?.tenantId }&ConsumerName=${application?.connectionHolders?.map((owner) => owner.name).join(",") || application?.connectionHolders?.map((owner) => owner.name).join(",") || PTData?.Properties?.[0]?.owners?.map((owner) => owner.name).join(",")}&isDisoconnectFlow=${application?.applicationNo?.includes("DC")?true : false}`,
                state: {},
              }}
            >
              <div style= {{marginTop : "10px"}}>
              <SubmitBar label={t("MAKE_PAYMENT")} />
              </div>
            </Link>
          ) : null}
    </Card>
  );
};

export default WSApplication;
