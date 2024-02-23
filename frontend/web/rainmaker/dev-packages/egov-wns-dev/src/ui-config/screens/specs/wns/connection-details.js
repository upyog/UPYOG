import {
  convertEpochToDate,
  getCommonCard,
  getCommonContainer,
  getCommonHeader,
  getLabel
} from "egov-ui-framework/ui-config/screens/specs/utils";
import {
  handleScreenConfigurationFieldChange as handleField,
  prepareFinalObject,toggleSnackbar
} from "egov-ui-framework/ui-redux/screen-configuration/actions";
import { getQueryArg } from "egov-ui-framework/ui-utils/commons";
import set from "lodash/set";
import {
  getDescriptionFromMDMS,
  getSearchResults,
  getSearchResultsForSewerage,
  serviceConst,
} from "../../../../ui-utils/commons";
import { ifUserRoleExists, getDemand} from "../utils";
import { connectionDetailsDownload } from "./connectionDetailsResource/connectionDetailsDownload";
import { connectionDetailsFooter } from "./connectionDetailsResource/connectionDetailsFooter";
import {
  connHolderDetailsSameAsOwnerSummary,
  connHolderDetailsSummary,
  getOwnerDetails,
} from "./connectionDetailsResource/owner-deatils";
import { getPropertyDetails } from "./connectionDetailsResource/property-details";
import { getServiceDetails } from "./connectionDetailsResource/service-details";
import { getRequiredDocData } from "egov-billamend/ui-config/screens/specs/utils";
import { getPaymentDetails } from "./connectionDetailsResource/paymentDetails";
import { getDCBDetails } from "./connectionDetailsResource/DCBDetails";
import { getTenantId, getTenantIdCommon } from "egov-ui-kit/utils/localStorageUtils";
import { httpRequest } from "../../../../ui-utils/api";
import { getBill } from "egov-common/ui-config/screens/specs/utils";
import get from "lodash/get";
import { getBillAmdSearchResult } from "egov-billamend/ui-utils/commons";

const tenantId = getQueryArg(window.location.href, "tenantId");
let connectionNumber = getQueryArg(window.location.href, "connectionNumber");
const service = getQueryArg(window.location.href, "service");

const getApplicationNumber = (dispatch, connectionsObj) => {
  let appNos = "";
  if (connectionsObj.length > 1) {
    for (var i = 0; i < connectionsObj.length; i++) {
      appNos += connectionsObj[i].applicationNo + ",";
    }
    appNos = appNos.slice(0, -1);
  } else {
    appNos = connectionsObj[0].applicationNo;
  }
  console.log(appNos, "application numbers");
  dispatch(prepareFinalObject("applicationNos", appNos));
};
const showHideConnectionHolder = (dispatch, connectionHolders) => {
  if (connectionHolders != "NA" && connectionHolders.length > 0) {
    dispatch(
      handleField(
        "connection-details",
        "components.div.children.connectionDetails.children.cardContent.children.connectionHolders",
        "visible",
        true
      )
    );
    dispatch(
      handleField(
        "connection-details",
        "components.div.children.connectionDetails.children.cardContent.children.connectionHoldersSameAsOwner",
        "visible",
        false
      )
    );
  } else {
    dispatch(
      handleField(
        "connection-details",
        "components.div.children.connectionDetails.children.cardContent.children.connectionHolders",
        "visible",
        false
      )
    );
    dispatch(
      handleField(
        "connection-details",
        "components.div.children.connectionDetails.children.cardContent.children.connectionHoldersSameAsOwner",
        "visible",
        true
      )
    );
  }
};
export const sortpayloadDataObj = (connectionObj) => {
  return connectionObj.sort((a, b) =>
    a.additionalDetails.appCreatedDate < b.additionalDetails.appCreatedDate
      ? 1
      : -1
  );
};

const getActiveConnectionObj = (connectionsObj) => {
  let getActiveConnectionObj = "";
  for (var i = 0; i < connectionsObj.length; i++) {
    if (
      (connectionsObj[i] &&
        connectionsObj[i].applicationStatus === "CONNECTION_ACTIVATED") ||
      connectionsObj[i].applicationStatus === "APPROVED"
    ) {
      getActiveConnectionObj = connectionsObj[i];
      break;
    }
  }
  return getActiveConnectionObj;
};

const searchResults = async (action, state, dispatch, connectionNumber) => {
  /**
   * This methods holds the api calls and the responses of fetch bill and search connection for both water and sewerage service
   */
  let queryObject = [
    { key: "tenantId", value: tenantId },
    { key: "connectionNumber", value: connectionNumber },
  ];
  let serviceUrl = getQueryArg(window.location.href, "service");
  if (serviceUrl === serviceConst.SEWERAGE) {
    let payloadData = await getSearchResultsForSewerage(
      queryObject,
      dispatch,
      true
    );
    if (
      payloadData !== null &&
      payloadData !== undefined &&
      payloadData.SewerageConnections.length > 0
    ) {
      payloadData.SewerageConnections = sortpayloadDataObj(
        payloadData.SewerageConnections
      );

      let sewerageConnection = getActiveConnectionObj(
        payloadData.SewerageConnections
      );
      let propTenantId = sewerageConnection.property.tenantId.split(".")[0];
      sewerageConnection.service = serviceUrl;

      if (sewerageConnection.property.propertyType !== undefined) {
        const propertyTpe =
          "[?(@.code  == " +
          JSON.stringify(sewerageConnection.property.propertyType) +
          ")]";
        let propertyTypeParams = {
          MdmsCriteria: {
            tenantId: propTenantId,
            moduleDetails: [
              {
                moduleName: "PropertyTax",
                masterDetails: [
                  { name: "PropertyType", filter: `${propertyTpe}` },
                ],
              },
            ],
          },
        };
        const mdmsPropertyType = await getDescriptionFromMDMS(
          propertyTypeParams,
          dispatch
        );
        if (
          mdmsPropertyType !== undefined &&
          mdmsPropertyType !== null &&
          mdmsPropertyType.MdmsRes.PropertyTax.PropertyType[0].name !==
            undefined &&
          mdmsPropertyType.MdmsRes.PropertyTax.PropertyType[0].name !== null
        ) {
          sewerageConnection.property.propertyTypeData =
            mdmsPropertyType.MdmsRes.PropertyTax.PropertyType[0].name; //propertyType from Mdms
        } else {
          sewerageConnection.property.propertyTypeData = "NA";
        }
      }

      if (sewerageConnection.noOfToilets === undefined) {
        sewerageConnection.noOfToilets = "NA";
      }
      if (sewerageConnection.noOfToilets === 0) {
        sewerageConnection.noOfToilets = "0";
      }
      sewerageConnection.connectionExecutionDate = convertEpochToDate(
        sewerageConnection.connectionExecutionDate
      );
      const lat = sewerageConnection.property.address.locality.latitude
        ? sewerageConnection.property.address.locality.latitude
        : "NA";
      const long = sewerageConnection.property.address.locality.longitude
        ? sewerageConnection.property.address.locality.longitude
        : "NA";
      sewerageConnection.property.address.locality.locationOnMap = `${lat} ${long}`;

      /*if (sewerageConnection.property.usageCategory !== undefined) {
        const propertyUsageType = "[?(@.code  == " + JSON.stringify(sewerageConnection.property.usageCategory) + ")]"
        let propertyUsageTypeParams = { MdmsCriteria: { tenantId: "pb", moduleDetails: [{ moduleName: "PropertyTax", masterDetails: [{ name: "UsageCategoryMajor", filter: `${propertyUsageType}` }] }] } }
        const mdmsPropertyUsageType = await getDescriptionFromMDMS(propertyUsageTypeParams, dispatch)
        if (mdmsPropertyUsageType !== undefined && mdmsPropertyUsageType !== null && mdmsPropertyUsageType.MdmsRes.PropertyTax.PropertyType !== undefined && mdmsPropertyUsageType.MdmsRes.PropertyTax.PropertyType[0].name !== null) {
          sewerageConnection.property.propertyUsageType = mdmsPropertyUsageType.MdmsRes.PropertyTax.UsageCategoryMajor[0].name;//propertyUsageType from Mdms
        } else {
          sewerageConnection.property.propertyTypeData = "NA"
        }
      }*/
      showHideConnectionHolder(dispatch, sewerageConnection.connectionHolders);
      const queryObjForBill = [
        {
          key: "tenantId",
          value: tenantId ? tenantId :getTenantId(),
        },
        {
          key: "consumerCode",
          value:connectionNumber,
        },
        {
          key: "businessService",
          value: "SW"
          ,
        },
      ];
      const bill = await getDemand(queryObjForBill,dispatch);
      dispatch(prepareFinalObject("BILL_FOR_WNS", bill));
      let billAMDSearch = process.env.REACT_APP_NAME !== "Citizen" ? await getBillAmdSearchResult(queryObjForBill, dispatch): [];
      let amendments=get(billAMDSearch, "Amendments", []);
      amendments=amendments&&Array.isArray(amendments)&&amendments.filter(amendment=>amendment.status==='INWORKFLOW');
      dispatch(prepareFinalObject("BILL_FOR_WNS", bill));
      dispatch(prepareFinalObject("isAmendmentInWorkflow", amendments&&Array.isArray(amendments)&&amendments.length==0?true:false));
      dispatch(prepareFinalObject("WaterConnection[0]", sewerageConnection));
      getApplicationNumber(dispatch, payloadData.SewerageConnections);
      dispatch(
        handleField(
          "connection-details",
          "components.div.children.connectionDetails.children.cardContent.children.serviceDetails.children.cardContent.children.sewerDetails",
          "visible",
          true
        )
      );
      dispatch(
        handleField(
          "connection-details",
          "components.div.children.connectionDetails.children.cardContent.children.serviceDetails.children.cardContent.children.waterDetails",
          "visible",
          false
        )
      );
      if(sewerageConnection && sewerageConnection.length > 0 && sewerageConnection[0].uom) {
        dispatch(
          handleField(
            "connection-details",
            "components.div.children.connectionDetails.children.cardContent.children.serviceDetails.children.cardContent.children.sewerDetails.children.unitOfMeasurement",
            "visible",
            true
          )
        );
      } else {
        dispatch(
          handleField(
            "connection-details",
            "components.div.children.connectionDetails.children.cardContent.children.serviceDetails.children.cardContent.children.sewerDetails.children.unitOfMeasurement",
            "visible",
            false
          )
        );
      }
    }
  } else if (serviceUrl === serviceConst.WATER) {
    let payloadData = await getSearchResults(queryObject, true);
    if (
      payloadData !== null &&
      payloadData !== undefined &&
      payloadData.WaterConnection.length > 0
    ) {
      payloadData.WaterConnection = sortpayloadDataObj(
        payloadData.WaterConnection
      );
      let waterConnection = getActiveConnectionObj(payloadData.WaterConnection);
      waterConnection.service = serviceUrl;
      let propTenantId = waterConnection.property.tenantId.split(".")[0];
      if (waterConnection.connectionExecutionDate !== undefined) {
        waterConnection.connectionExecutionDate = convertEpochToDate(
          waterConnection.connectionExecutionDate
        );
      } else {
        waterConnection.connectionExecutionDate = "NA";
      }
      if (waterConnection.noOfTaps === undefined) {
        waterConnection.noOfTaps = "NA";
      }
      if (waterConnection.noOfTaps === 0) {
        waterConnection.noOfTaps = "0";
      }
      if (waterConnection.pipeSize === 0) {
        waterConnection.pipeSize = "0";
      }
      if (waterConnection.property.propertyType !== undefined) {
        const propertyTpe =
          "[?(@.code  == " +
          JSON.stringify(waterConnection.property.propertyType) +
          ")]";
        let propertyTypeParams = {
          MdmsCriteria: {
            tenantId: propTenantId,
            moduleDetails: [
              {
                moduleName: "PropertyTax",
                masterDetails: [
                  { name: "PropertyType", filter: `${propertyTpe}` },
                ],
              },
            ],
          },
        };
        const mdmsPropertyType = await getDescriptionFromMDMS(
          propertyTypeParams,
          dispatch
        );
        waterConnection.property.propertyTypeData =
          mdmsPropertyType.MdmsRes.PropertyTax.PropertyType[0].name !==
          undefined
            ? mdmsPropertyType.MdmsRes.PropertyTax.PropertyType[0].name
            : "NA"; //propertyType from Mdms
      }
      const lat = waterConnection.property.address.locality.latitude;
      const long = waterConnection.property.address.locality.longitude;
      waterConnection.property.address.locality.locationOnMap = `${lat} ${long}`;

      /*if (waterConnection.property.usageCategory !== undefined) {
        const propertyUsageType = "[?(@.code  == " + JSON.stringify(waterConnection.property.usageCategory) + ")]"
        let propertyUsageTypeParams = { MdmsCriteria: { tenantId: "pb", moduleDetails: [{ moduleName: "PropertyTax", masterDetails: [{ name: "UsageCategoryMajor", filter: `${propertyUsageType}` }] }] } }
        const mdmsPropertyUsageType = await getDescriptionFromMDMS(propertyUsageTypeParams, dispatch)
        if (mdmsPropertyUsageType !== undefined && mdmsPropertyUsageType !== null && mdmsPropertyUsageType.MdmsRes.PropertyTax.PropertyType !== undefined && mdmsPropertyUsageType.MdmsRes.PropertyTax.PropertyType[0].name !== null) {
          waterConnection.property.propertyUsageType = mdmsPropertyUsageType.MdmsRes.PropertyTax.UsageCategoryMajor[0].name;//propertyUsageType from Mdms
        } else {
          waterConnection.property.propertyTypeData = "NA"
        }
      }*/
      const queryObjForBill = [
        {
          key: "tenantId",
          value: tenantId ? tenantId :getTenantId(),
        },
        {
          key: "consumerCode",
          value:connectionNumber,
        },
        {
          key: "businessService",
          value: "WS",
        },
      ];
      const bill = await getDemand(queryObjForBill, dispatch);
      let billAMDSearch = process.env.REACT_APP_NAME !== "Citizen" ? await getBillAmdSearchResult(queryObjForBill, dispatch): [];
      let amendments=get(billAMDSearch, "Amendments", []);
      amendments=amendments&&Array.isArray(amendments)&&amendments.filter(amendment=>amendment.status==='INWORKFLOW');
      dispatch(prepareFinalObject("BILL_FOR_WNS", bill));
      dispatch(prepareFinalObject("isAmendmentInWorkflow", amendments&&Array.isArray(amendments)&&amendments.length==0?true:false));
      showHideConnectionHolder(dispatch, waterConnection.connectionHolders);
      dispatch(prepareFinalObject("WaterConnection[0]", waterConnection));
      getApplicationNumber(dispatch, payloadData.WaterConnection);

      dispatch(
        handleField(
          "connection-details",
          "components.div.children.connectionDetails.children.cardContent.children.serviceDetails.children.cardContent.children.sewerDetails",
          "visible",
          false
        )
      );
      dispatch(
        handleField(
          "connection-details",
          "components.div.children.connectionDetails.children.cardContent.children.serviceDetails.children.cardContent.children.waterDetails",
          "visible",
          true
        )
      );
      const tenantId = getTenantIdCommon();
      const service = getQueryArg(window.location.href, "service");
      const connectionType = getQueryArg(window.location.href, "connectionType");
      const getRedirectionURL = async (state, dispatch) => {
        let tenant = getQueryArg(window.location.href, "tenantId");
        const connectionNumber = getQueryArg(window.location.href, "connectionNumber");
        const environment = process.env.NODE_ENV === "production" ? process.env.REACT_APP_NAME === "Citizen" ? "citizen" : "employee" : "";
        const origin =  process.env.NODE_ENV === "production" ? window.location.origin + "/" : window.location.origin;
        window.location.assign(`${origin}${environment}/wns/meter-reading?connectionNos=${connectionNumber}&tenantId=${tenantId}`);
      };
      const editSection= {
        componentPath: "Button",
        props: { color: "primary", style: { margin: "-16px" } },
        visible: true,
        gridDefination: { xs: 12, sm: 12, align: "left" },
        children: { buttonLabel: getLabel({ labelKey: "WS_CONNECTION_DETAILS_VIEW_CONSUMPTION_LABEL" }) },
        onClickDefination: {
          action: "condition",
          callBack: getRedirectionURL
        }
      }
      if( service === "WATER" && connectionType === "Metered") {
      dispatch(
        handleField(
          "connection-details",
          "components.div.children.connectionDetails.children.cardContent.children.serviceDetails.children.cardContent.children.waterDetails.children",
          "editSection",
          editSection
        )
      );
      }
    }
  }
};

const beforeInitFn = async (action, state, dispatch, connectionNumber) => {
  //Search details for given application Number
  if (connectionNumber) {
    await searchResults(action, state, dispatch, connectionNumber);
  }

  let serviceCode=null;
  if(getQueryArg(window.location.href, "service") == "SEWERAGE") 
  serviceCode="SW";
  else
  serviceCode="WS";
  const queryObjForPayment = [
    {
      key: "tenantId",
      value: tenantId,
    },
    {
      key: "consumerCodes",
      value:connectionNumber,
    },
    {
      key: "businessService",
      value:serviceCode,
    },
    {
      key: "consumerCode",
      value:connectionNumber,
    },
    
  ];
  getPaymentHistory(queryObjForPayment, dispatch , serviceCode);
  getDCBDetail(queryObjForPayment, dispatch);
};

export const getPaymentHistory = async (queryObject , dispatch , serviceCode) => {
  try {

    let response = await httpRequest(
      "post",
      "collection-services/payments/"+serviceCode+"/_search",
       "",
      queryObject
    );
  
    let paymentArray = [];
    let paymentRow=null;
    let receiptNumber,receiptDate,totalAmountPaid,totalDue,paymentMode,service,tenant;

   // response=response.filter(i=> i.paymentStatus !='CANCELLED');
    response.Payments.map((element,index) => {
      if(element.paymentStatus != 'CANCELLED'){
       paymentMode = element.paymentMode;
       tenant = element.tenantId;

       element.paymentDetails.map((dd)=>{
       receiptDate=convertEpochToDate(dd.receiptDate);
       receiptNumber=dd.receiptNumber;
       totalAmountPaid = dd.totalAmountPaid;
       totalDue = dd.totalDue;
       service=dd.bill.businessService;
      });
      paymentRow={
        "paymentMode":paymentMode,
        "receiptDate":receiptDate,
        "receiptNumber":receiptNumber,
        "totalAmountPaid":totalAmountPaid,
        "totalDue":totalDue,
        "service":service,
        "tenant":tenant
      };
      paymentArray.push(paymentRow);
    }
    });



   dispatch(prepareFinalObject("paymentHistory", paymentArray));

  } catch (error) {
      dispatch(
        toggleSnackbar(
          true,
          { labelName: error.message, labelKey: error.message },
        "warning"
        )
      );
  }
}


export const getDCBDetail = async (queryObject , dispatch) => {
  try {

    const response = await httpRequest(
      "post",
      "billing-service/demand/_search",
       "",
      queryObject
    );
    let dcbArray = [];
    let dcbtotalArray = [];
    let dcbRow=null;
    let dcbtotalRow=null;
    let installment,advance,taxAmount,taxCollected,taxBalance,interestAmount,interestCollected,interestBalance,penaltyBalance,penaltyCollected,penaltyAmount;
    response.Demands.map((element,index) => {
      taxAmount=0;taxCollected=0;taxBalance=0;interestAmount=0;
      interestCollected=0;interestBalance=0;penaltyBalance=0;penaltyCollected=0;penaltyAmount=0;
advance=0;
  if(element.status == "ACTIVE")
  {
  installment=convertEpochToDate(element.taxPeriodFrom) +"-"+convertEpochToDate(element.taxPeriodTo);
  element.demandDetails.map((dd)=>{
    if(dd.taxHeadMasterCode=='WS_CHARGE' || dd.taxHeadMasterCode=='SW_CHARGE' ){
      taxAmount=taxAmount+dd.taxAmount;
      taxCollected=taxCollected+dd.collectionAmount;
      taxBalance=taxAmount-taxCollected;
    }
    if(dd.taxHeadMasterCode=='WS_TIME_INTEREST'  || dd.taxHeadMasterCode=='SW_TIME_INTEREST' ){

      interestAmount=interestAmount+dd.taxAmount;
      interestCollected=interestCollected+dd.collectionAmount;
      interestBalance=interestAmount-interestCollected;
    }
    if(dd.taxHeadMasterCode=='WS_TIME_PENALTY' || dd.taxHeadMasterCode=='SW_TIME_PENALTY'){
      penaltyAmount=penaltyAmount+dd.taxAmount;
      penaltyCollected=penaltyCollected+dd.collectionAmount;
      penaltyBalance=penaltyAmount-penaltyCollected;
     
    } if(dd.taxHeadMasterCode == "SW_ADVANCE_CARRYFORWARD" || dd.taxHeadMasterCode == "WS_ADVANCE_CARRYFORWARD" )
    {
    advance=advance+dd.taxAmount;
    }
    });
    
  dcbRow={
    "installment":installment,
    "taxAmount":taxAmount?taxAmount:0,
    "interestAmount":interestAmount?interestAmount:0,
    "penaltyAmount":penaltyAmount?penaltyAmount:0,
    "taxCollected":taxCollected?taxCollected:0,
    "interestCollected":interestCollected?interestCollected:0,
    "penaltyCollected":penaltyCollected?penaltyCollected:0,
    "taxBalance":taxBalance?taxBalance:0,
    "interestBalance":interestBalance?interestBalance:0,
    "penaltyBalance":penaltyBalance?penaltyBalance:0,
    "advance":advance?advance:0
  };
  dcbArray.push(dcbRow);
  };
  
    });
let length=response.Demands.length;
let netAdvance=0;
response.Demands[length-1].demandDetails.map((dd)=>{
  if(dd.taxHeadMasterCode == "SW_ADVANCE_CARRYFORWARD" || dd.taxHeadMasterCode == "WS_ADVANCE_CARRYFORWARD" )
  {
    netAdvance=dd.taxAmount;
  }
});
  const totalTaxDemand = dcbArray.reduce((tax, item) => tax + parseInt(item.taxAmount, 10), 0);
  const totalInterestDemand = dcbArray.reduce((interest, item) => interest + parseInt(item.interestAmount, 10), 0);
  const totalPenaltyDemand = dcbArray.reduce((penalty, item) => penalty + parseInt(item.penaltyAmount, 10), 0);
 
  const totalTaxCollected = dcbArray.reduce((tax, item) => tax + parseInt(item.taxCollected, 10), 0);
  const totalInterestCollected = dcbArray.reduce((interest, item) => interest + parseInt(item.interestCollected, 10), 0);
  const totalPenaltyCollected = dcbArray.reduce((penalty, item) => penalty + parseInt(item.penaltyCollected, 10), 0);

  const totalTaxBalance = dcbArray.reduce((tax, item) => tax + parseInt(item.taxBalance, 10), 0);
  const totalInterestBalance = dcbArray.reduce((interest, item) => interest + parseInt(item.interestBalance, 10), 0);
  const totalPenaltyBalance = dcbArray.reduce((penalty, item) => penalty + parseInt(item.penaltyBalance, 10), 0);
 const totalAdvance=netAdvance;
  const totalBalance = parseInt(totalTaxBalance) + parseInt(totalInterestBalance) + parseInt(totalPenaltyBalance)+parseInt(totalAdvance);  




  dcbtotalRow={
             
              "totalTaxDemand":totalTaxDemand,
              "totalInterestDemand":totalInterestDemand,
              "totalPenaltyDemand":totalPenaltyDemand,
              "totalTaxCollected":totalTaxCollected,
              "totalInterestCollected":totalInterestCollected,
              "totalPenaltyCollected":totalPenaltyCollected,
              "totalTaxBalance":totalTaxBalance,
              "totalInterestBalance":totalInterestBalance,
              "totalPenaltyBalance":totalPenaltyBalance,
              "totalBalance":totalBalance,
              "totalAdvance":totalAdvance
   };
   dcbtotalArray.push(dcbtotalRow);
   dispatch(prepareFinalObject("dcbDetails", dcbArray));
   dispatch(prepareFinalObject("dcbtotalDetails", dcbtotalArray));
  
  } catch (error) {
      dispatch(
        toggleSnackbar(
          true,
          { labelName: error.message, labelKey: error.message },
        "warning"
        )
      );
  }
}

const headerrow = getCommonContainer({
  header: getCommonHeader({ labelKey: "WS_SEARCH_CONNECTIONS_DETAILS_HEADER" }),
  connectionNumber: {
    uiFramework: "custom-atoms-local",
    moduleName: "egov-wns",
    componentPath: "ConsumerNoContainer",
    props: {
      number: getQueryArg(window.location.href, "connectionNumber"),
    },
  },
});

const serviceDetails = getServiceDetails();

const propertyDetails = getPropertyDetails(false);

const ownerDetails = getOwnerDetails(false);

const connectionHolders = connHolderDetailsSummary();

const connectionHoldersSameAsOwner = connHolderDetailsSameAsOwnerSummary();

const getConnectionDetailsFooterAction =  (ifUserRoleExists('WS_CEMP')) ? connectionDetailsFooter : {};
 
const paymentDetails = getPaymentDetails(true);

const DCBDetails = getDCBDetails(true);

export const connectionDetails = getCommonCard({
  serviceDetails,
  propertyDetails,
  ownerDetails,
  connectionHolders,
  connectionHoldersSameAsOwner,
  paymentDetails,
  DCBDetails
});
const getMDMSData = async (action, state, dispatch) => {
  const tenantId = getTenantId();
  let mdmsBody = {
    MdmsCriteria: {
      tenantId: tenantId,
      moduleDetails: [
        {
          moduleName: "BillingService",
          masterDetails: [
            {
              name: "BusinessService",
            },
          ],
        },
        {
          moduleName: "BillAmendment",
          masterDetails: [{ name: "documentObj" }],
        },
        {
          moduleName: "common-masters",
          masterDetails: [
            {
              name: "uiCommonPay",
            },
          ],
        },
        {
          moduleName: "tenant",
          masterDetails: [
            {
              name: "tenants",
            },
          ],
        },
      ],
    },
  };
  try {
    getRequiredDocData(action, dispatch, [
      {
        moduleName: "BillAmendment",
        masterDetails: [{ name: "documentObj" }],
      },
    ]);
    const payload = await httpRequest(
      "post",
      "/egov-mdms-service/v1/_search",
      "_search",
      [],
      mdmsBody
    );
    payload.MdmsRes.BillingService.BusinessService = payload.MdmsRes.BillingService.BusinessService.filter(
      (service) => service.billGineiURL
    );
    // console.log(payload.MdmsRes,"nishant")
    dispatch(prepareFinalObject("connectDetailsData", payload.MdmsRes));
  } catch (e) {
    console.log(e);
  }
};
const getDataForBillAmendment = async (action, state, dispatch) => {
  await getMDMSData(action, state, dispatch);
};
const screenConfig = {
  uiFramework: "material-ui",
  name: "connection-details",
  beforeInitScreen: (action, state, dispatch) => {
    let connectionNo = getQueryArg(window.location.href, "connectionNumber");
    getDataForBillAmendment(action, state, dispatch);

    beforeInitFn(action, state, dispatch, connectionNo);
    getRequiredDocData(action, dispatch, [
      {
        moduleName: "BillAmendment",
        masterDetails: [{ name: "documentObj" }],
      },
    ]);
    set(
      action,
      "screenConfig.components.div.children.headerDiv.children.header1.children.connectionNumber.props.number",
      connectionNo
    );
    set(
      action,
      "components.div.children.getConnectionDetailsFooterAction.children.takeAction.props.connectionNumber",
      connectionNo
    );
    return action;
  },

  components: {
    div: {
      uiFramework: "custom-atoms",
      componentPath: "Div",
      props: {
        className: "common-div-css search-preview",
        id: "connection-details",
      },
      children: {
        headerDiv: {
          uiFramework: "custom-atoms",
          componentPath: "Container",
          children: {
            header1: {
              gridDefination: {
                xs: 12,
                sm: 7,
              },
              ...headerrow,
            },
            helpSection: {
              uiFramework: "custom-atoms",
              componentPath: "Container",
              props: {
                color: "primary",
                style: { justifyContent: "flex-end" },
                //display: "block"
              },
              gridDefination: {
                xs: 12,
                sm: 5,
              //  align: "right",
              },
              children: {
                connectionDetailsDownload,
              },
            },
          },
        },
        connectionDetails,
        getConnectionDetailsFooterAction,
      },
    },
    adhocDialog: {
      uiFramework: "custom-containers",
      componentPath: "DialogContainer",
      props: {
        open: false,
        maxWidth: false,
        screenKey: "connection-details",
      },
      children: {
        popup: {},
      },
    },
  },
};

export default screenConfig;
