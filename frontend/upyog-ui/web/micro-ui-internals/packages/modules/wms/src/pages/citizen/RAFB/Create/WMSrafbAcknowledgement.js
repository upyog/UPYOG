import { Banner, Card, CardText, LinkButton, LinkLabel, Loader, Row, StatusTable, SubmitBar } from "@egovernments/digit-ui-react-components";
import React, { useEffect } from "react";
import { useTranslation } from "react-i18next";
import { Link, useRouteMatch } from "react-router-dom";
import getWMSrafbAcknowledgementData from "../../../../utils/getWMSrafbAcknowledgementData";
import { convertToProperty, convertToUpdateProperty } from "../../../../utils";

const GetActionMessage = (props) => {
  const { t } = useTranslation();
  if (props.isSuccess) {
    return !window.location.href.includes("edit-application") ? t("CS_PROPERTY_APPLICATION_SUCCESS") : t("CS_PROPERTY_UPDATE_APPLICATION_SUCCESS");
  } else if (props.isLoading) {
    return !window.location.href.includes("edit-application") ? t("CS_PROPERTY_APPLICATION_PENDING") : t("CS_PROPERTY_UPDATE_APPLICATION_PENDING");
  } else if (!props.isSuccess) {
    return !window.location.href.includes("edit-application") ? t("CS_PROPERTY_APPLICATION_FAILED") : t("CS_PROPERTY_UPDATE_APPLICATION_FAILED");
  }
};

const rowContainerStyle = {
  padding: "4px 0px",
  justifyContent: "space-between",
};

const BannerPicker = (props) => {
  return (
    <Banner
      message={GetActionMessage(props)}
      // applicationNumber={props.data?.Properties[0].acknowldgementNumber}
      applicationNumber={props.data?.id}
      info={props.isSuccess ? props.t("PT_APPLICATION_NO") : ""}
      successful={props.isSuccess}
      style={{width: "100%"}}
    />
  );
};

const WMSrafbAcknowledgement = ({ data, onSuccess }) => {
  console.log("WMS Acknowledgement data ",data)
  console.log("WMS Acknowledgement !data  ",Boolean(data))
  console.log("WMS Acknowledgement data data?.address?.city ",data?.address?.city)
  const { t } = useTranslation();
  const isPropertyMutation = window.location.href.includes("property-mutation");
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const mutation = Digit.Hooks.wms.rafb.useWmsRAFBCreate(
    tenantId,
    // Boolean(data) ? data : tenantId,
    // data?.address?.city ? data.address?.city?.code : tenantId,
    !window.location.href.includes("edit-application") && !isPropertyMutation
  );
  console.log(" sdasdsa mutation",mutation)
  console.log(" sdasdsa tenantId",tenantId)
  const { data: storeData } = Digit.Hooks.useStore.getInitData();
  const match = useRouteMatch();
  const { tenants } = storeData || {};

  useEffect(() => {
    data.id = function (message) {
      var index;
      for (var i = 0; i < data.length; i++) {
          if(data[i].id == message.id) {
              index = i;
              break;
          }
      }
      if (index) {
            data.splice(index,1);
      } 

}
    // data.id="200"
    console.log("data with id ",data)
    try{
      mutation.mutate(data, {
             onSuccess,
           })
    } catch (err){

    }
    // try {
    //   let tenantId = isPropertyMutation ? data.Property?.address.tenantId : data?.address?.city ? data.address?.city?.code : tenantId;
    //   data.tenantId = tenantId;
    //   let formdata = !window.location.href.includes("edit-application")
    //     ? isPropertyMutation
    //       ? data
    //       :data
    //       :data
    //     //   : convertToProperty(data)
    //     // : convertToUpdateProperty(data,t);
    //   formdata.Property.tenantId = formdata?.Property?.tenantId || tenantId;
    //   mutation.mutate(formdata, {
    //     onSuccess,
    //   });
    //   console.log("formdata onSuccess ",formdata,onSuccess)

    // } catch (err) {
    //   console.log("err err ",err)
    // }
  }, []);

  const handleDownloadPdf = async () => {
    console.log("mutation.data ",mutation.data)
    const { TenderWorkDetail = [] } = {
      "RequestInfo": {
          "apiId": "Rainmaker",
          "authToken": "6a743fce-f662-43d0-b3ef-6eb20d26455c",
          "userInfo": {
              "id": 1681,
              "uuid": "2bef105b-eff3-479a-8aad-64c2b5354070",
              "userName": "8080808080",
              "name": "Archt",
              "mobileNumber": "8080808080",
              "emailId": "Arch@test.com",
              "locale": null,
              "type": "CITIZEN",
              "roles": [
                  {
                      "name": "Citizen",
                      "code": "CITIZEN",
                      "tenantId": "pg"
                  }
              ],
              "active": true,
              "tenantId": "pg",
              "permanentCity": "pg.citya"
          },
          "msgId": "1711445537931|en_IN",
          "plainAccessRequest": {}
      },
      "ProjectInfo": {
          "ProjectName": {
              "code": "OWNER.ADDRESSPROOF.ELECTRICITYBILL",
              "active": true,
              "i18nKey": "OWNER_ADDRESSPROOF_ELECTRICITYBILL"
          },
          "WorkName": "abcd",
          "WorkOrderNo": "67"
      },
      "previous_bill": {
          "0": [
              {
                  "uid": 1,
                  "billDate": "2023-12-22",
                  "bilNo": "1234567890",
                  "billAmount": "3455678",
                  "deductionAmount": "34256",
                  "remark": "Test"
              },
              {
                  "uid": 2,
                  "billDate": "2023-08-04",
                  "bilNo": "94873938",
                  "billAmount": "345678",
                  "deductionAmount": "334456",
                  "remark": "Test"
              }
          ]
      },
      "mbNotPaid": {
          "i18nKey": "Measurement Book A",
          "mbDate": "2024-02-02",
          "mbNumber": "MB0001",
          "amount": "234567"
      },
      "undefined": {},
      "TenderWorkDetail": {
          "workName": "ABC",
          "estimatedWorkCost": "1234543",
          "tenderType": "Tender Type A",
          "percentageType": "10",
          "amount": "34567"
      },
      "withheldDeductionsDetail": {
          "withheldDeductionsDetail": [
              {
                  "taxcategory": {
                      "i18nKey": "10"
                  },
                  "remark": "Test 1",
                  "amount": "154"
              }
          ]
      },
      "RABillTaxDetail": {
          "RABillTaxDetail": [
              {
                  "taxcategory": {
                      "i18nKey_0": "10"
                  },
                  "addition_deduction": {
                      "i18nKey_1": "40"
                  },
                  "amount_percentage": {
                      "i18nKey_2": "70"
                  },
                  "percentageValue": "10",
                  "amount": "100",
                  "total": "110"
              }
          ]
      },
      "id": 275
  };

  console.log("mutation.data TenderWorkDetail ",TenderWorkDetail)
   let Properties = [TenderWorkDetail];
    let Property = (Properties && Properties[0]) || {};
    const tenantInfo = tenants.find((tenant) => tenant.code === Property.tenantId);
  // console.log(" sdasdsa tenantId innn",tenantId)
  //   let tenantId = Property.tenantId || tenantId;
    const propertyDetails = await Digit.PTService.search({ tenantId, filters: { propertyIds: Property?.propertyId, status: "INACTIVE" } });
    Property.transferorDetails = propertyDetails?.Properties?.[0] || [];
    Property.isTransferor = true;
    Property.transferorOwnershipCategory = propertyDetails?.Properties?.[0]?.ownershipCategory
    // const data = await getWMSrafbAcknowledgementData({ ...Property }, tenantInfo, t);
    const data = {
      t: t,
      tenantId: 123,
      name: `Siyaram Kumar`,
      email: `siyaram.gupta@in.ey.com`,
      phoneNumber: `12345678909`,
      heading: "WMS RAFB ACKNOWLEDGEMENT",
      details: [
        {
          title: "RAFB TITLE APPLICATION DETAILS",
          values: [
            { title: "WMS APPLICATION NO", value: "123456789" },
            { title: t("WMS PROPERRTY ID"), value: "PRP0987" },
            {
              title: "WMS RAFB APPLICATION DETAILS APPLICATION DATE",
              value: "26/03/2024",
            },
          ],
        },
        // getOwner(application, t),
        // getAssessmentInfo(application, t),
        // {
        //   title: t("PT_PROPERTY_ADDRESS_SUB_HEADER"),
        //   values: [
        //     { title: t("PT_PROPERTY_ADDRESS_PINCODE"), value: application?.address?.pincode || t("CS_NA") },
        //     { title: t("PT_PROPERTY_ADDRESS_CITY"), value: t(getCityLocale(application?.tenantId)) || t("CS_NA") },
        //     {
        //       title: t("PT_PROPERTY_ADDRESS_MOHALLA"),
        //       value: t(`${getMohallaLocale(application?.address?.locality?.code, application?.tenantId)}`) || t("CS_NA"),
        //     },
        //     { title: t("PT_PROPERTY_ADDRESS_STREET_NAME"), value: application?.address?.street || t("CS_NA") },
        //     { title: t("PT_PROPERTY_ADDRESS_HOUSE_NO"), value: application?.address?.doorNo || t("CS_NA") },
        //     application?.channel === "CITIZEN" ? { title: t("PT_PROPERTY_ADDRESS_LANDMARK"), value: application?.address?.landmark || t("CS_NA") }: {},
        //   ],
        // },
        // {
        //   title: t("PT_COMMON_DOCS"),
        //   values:
        //   application.documents && application.documents.length > 0
        //       ? application.documents.map((document, index) => {
        //           let documentLink = pdfDownloadLink(res?.data, document?.fileStoreId);
        //           return {
        //             title: t(document?.documentType || t("CS_NA")),
        //             value: pdfDocumentName(documentLink, index) || t("CS_NA"),
        //           };
        //         })
        //       : {
        //         title: t("PT_NO_DOCUMENTS"),
        //         value: " ",
        //       },
        // },
      ],
    };
    Digit.Utils.pdf.generate(data);
  };

  return mutation.isLoading || mutation.isIdle ? (
    <Loader />
  ) : (
    <Card>
      <BannerPicker t={t} data={mutation.data} isSuccess={mutation.isSuccess} isLoading={mutation.isIdle || mutation.isLoading} />
      {mutation.isSuccess && <CardText>{t("CS_FILE_PROPERTY_RESPONSE")}</CardText>}
      {!mutation.isSuccess && <CardText>{t("CS_FILE_PROPERTY_FAILED_RESPONSE")}</CardText>}
      {/* {mutation.isSuccess && (
        <LinkButton
          label={
            <div className="response-download-button">
              <span>
                <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="#a82227">
                  <path d="M19 9h-4V3H9v6H5l7 7 7-7zM5 18v2h14v-2H5z" />
                </svg>
              </span>
              <span className="download-button">{t("CS_COMMON_DOWNLOAD")}</span> 
            </div>
          }
          onClick={handleDownloadPdf}
          className="w-full"
        />)}*/}
      <StatusTable>
        {mutation.isSuccess && (
          <Row
            rowContainerStyle={rowContainerStyle}
            last
            label={t("PT_COMMON_TABLE_COL_PT_ID")}
            // text={mutation?.data?.Properties[0]?.propertyId}
            text={mutation?.data?.id}
            textStyle={{ whiteSpace: "pre", width: "60%" }}
          />
        )}
      </StatusTable>
      {/* {mutation.isSuccess && <Link to={`/upyog-ui/citizen/feedback?redirectedFrom=${match.path}&propertyId=${mutation.isSuccess ? mutation?.data?.Properties[0]?.propertyId : ""}&acknowldgementNumber=${mutation.isSuccess ? mutation?.data?.Properties[0]?.acknowldgementNumber : ""}&creationReason=${mutation.isSuccess ? mutation?.data?.Properties[0]?.creationReason : ""}&tenantId=${mutation.isSuccess ? mutation?.data?.Properties[0]?.tenantId : ""}&locality=${mutation.isSuccess ? mutation?.data?.Properties[0]?.address?.locality?.code : ""}`}>
          <SubmitBar label={t("CS_REVIEW_AND_FEEDBACK")}/>
      </Link>} */}
      {mutation.isSuccess && <SubmitBar label={t("PT_DOWNLOAD_ACK_FORM")} onSubmit={handleDownloadPdf} />}
      <Link to={`/upyog-ui/citizen`}>
        <LinkButton label={t("CORE_COMMON_GO_TO_HOME")} />
      </Link>
    </Card>
  );
};

export default WMSrafbAcknowledgement;
