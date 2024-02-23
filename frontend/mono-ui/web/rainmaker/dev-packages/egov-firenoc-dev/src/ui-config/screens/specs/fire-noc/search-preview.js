import { getCommonCard, getCommonContainer, getCommonHeader, getLabelWithValue } from "egov-ui-framework/ui-config/screens/specs/utils";
import { handleScreenConfigurationFieldChange as handleField, prepareFinalObject } from "egov-ui-framework/ui-redux/screen-configuration/actions";
import { getFileUrl, getFileUrlFromAPI, getQueryArg, getTransformedLocale, setBusinessServiceDataToLocalStorage } from "egov-ui-framework/ui-utils/commons";
import { generateNOCAcknowledgement } from "egov-ui-kit/utils/pdfUtils/generateNOCAcknowledgement";
import { loadUlbLogo } from "egov-ui-kit/utils/pdfUtils/generatePDF";
import jp from "jsonpath";
import get from "lodash/get";
import set from "lodash/set";
import { getSearchResults,download } from "../../../../ui-utils/commons";
import { checkValueForNA, generateBill } from "../utils/index";
import generatePdf from "../utils/receiptPdf";
import { loadPdfGenerationData } from "../utils/receiptTransformer";
import "./index.css";
import { citizenFooter } from "./searchResource/citizenFooter";
import { applicantSummary, institutionSummary } from "./summaryResource/applicantSummary";
import { documentsSummary } from "./summaryResource/documentsSummary";
import { estimateSummary } from "./summaryResource/estimateSummary";
import { nocSummary } from "./summaryResource/nocSummary";
import { propertySummary } from "./summaryResource/propertySummary";
import { uniqBy } from "lodash";

const titlebar = getCommonContainer({
  header: getCommonHeader({
    labelName: "Task Details",
    labelKey: "NOC_TASK_DETAILS_HEADER"
  }),
  applicationNumber: {
    uiFramework: "custom-atoms-local",
    moduleName: "egov-firenoc",
    componentPath: "ApplicationNoContainer",
    props: {
      number: getQueryArg(window.location.href, "applicationNumber")
    }
  },
});
export const downloadPrintContainer = (
  state,
  dispatch
) => {
  /** MenuButton data based on status */

  let preparedFinalObject = get(
    state,
    "screenConfiguration.preparedFinalObject", {});
  let status = get(
    state,
    "screenConfiguration.preparedFinalObject.FireNOCs[0].fireNOCDetails.status"
  );
  let applicationNumber=get(
  state,
  "screenConfiguration.preparedFinalObject.FireNOCs[0].fireNOCDetails.applicationNumber"
);
let tenantId=get(
  state,
  "screenConfiguration.preparedFinalObject.FireNOCs[0].tenantId"
);

  let downloadMenu = [];
  let printMenu = [];
  let certificateDownloadObject = {
    label: { labelName: "NOC Certificate", labelKey: "NOC_CERTIFICATE" },
    link: () => {
      generatePdf(state, dispatch, "certificate_download");
    },
    leftIcon: "book"
  };
  let certificatePrintObject = {
    label: { labelName: "NOC Certificate", labelKey: "NOC_CERTIFICATE" },
    link: () => {
      generatePdf(state, dispatch, "certificate_print");
    },
    leftIcon: "book"
  };
  let receiptDownloadObject = {
    label: { labelName: "Receipt", labelKey: "NOC_RECEIPT" },
    link: () => {
      const receiptQueryString = [
        { key: "consumerCodes", value: get(state.screenConfiguration.preparedFinalObject.FireNOCs[0].fireNOCDetails, "applicationNumber") },
        { key: "tenantId", value: get(state.screenConfiguration.preparedFinalObject.FireNOCs[0], "tenantId") },
        { key: "businessService", value:'FIRENOC' }        
      ]
      download(receiptQueryString, "download", "firenocreceipt", state);
    },
    leftIcon: "receipt"
  };
  let receiptPrintObject = {
    label: { labelName: "Receipt", labelKey: "NOC_RECEIPT" },
    link: () => {
      const receiptQueryString = [
        { key: "consumerCodes", value: get(state.screenConfiguration.preparedFinalObject.FireNOCs[0].fireNOCDetails, "applicationNumber") },
        { key: "tenantId", value: get(state.screenConfiguration.preparedFinalObject.FireNOCs[0], "tenantId") },
        { key: "businessService", value:'FIRENOC' }      
      ]
      download(receiptQueryString, "print", "consolidatedreceipt", state);
    },
    leftIcon: "receipt"
  };
  let applicationDownloadObject = {
    label: { labelName: "Application", labelKey: "NOC_APPLICATION" },
    link: () => {
      generateNOCAcknowledgement(preparedFinalObject, `noc-acknowledgement-${get(preparedFinalObject, 'FireNOCs[0].fireNOCDetails.applicationNumber', '')}`);
      // generatePdf(state, dispatch, "application_download");
    },
    leftIcon: "assignment"
  };
  let applicationPrintObject = {
    label: { labelName: "Application", labelKey: "NOC_APPLICATION" },
    link: () => {
      generateNOCAcknowledgement(preparedFinalObject, 'print');
      // generatePdf(state, dispatch, "application_print");
    },
    leftIcon: "assignment"
  };
  switch (status) {
    case "APPROVED":
      downloadMenu = [
        certificateDownloadObject,
        receiptDownloadObject,
        applicationDownloadObject
      ];
      printMenu = [
        certificatePrintObject,
        receiptPrintObject,
        applicationPrintObject
      ];
      break;
    case "DOCUMENTVERIFY":
    case "FIELDINSPECTION":
    case "PENDINGAPPROVAL":
    case "REJECTED":
      downloadMenu = [receiptDownloadObject, applicationDownloadObject];
      printMenu = [receiptPrintObject, applicationPrintObject];
      break;
    case "CANCELLED":
    case "PENDINGPAYMENT":
      downloadMenu = [applicationDownloadObject];
      printMenu = [applicationPrintObject];
      break;
    default:
      break;
  }
  /** END */

  return {
    rightdiv: {
      uiFramework: "custom-atoms",
      componentPath: "Div",
      props: {
        style: { textAlign: "right", display: "flex" }
      },
      children: {
        downloadMenu: {
          uiFramework: "custom-molecules",
          componentPath: "DownloadPrintButton",
          props: {
            data: {
              label: { labelName: "DOWNLOAD", labelKey: "TL_DOWNLOAD" },
              leftIcon: "cloud_download",
              rightIcon: "arrow_drop_down",
              props: { variant: "outlined", style: { height: "60px", color: "#FE7A51", marginRight: "5px" }, className: "tl-download-button" },
              menu: downloadMenu
            }
          }
        },
        printMenu: {
          uiFramework: "custom-molecules",
          componentPath: "DownloadPrintButton",
          props: {
            data: {
              label: { labelName: "PRINT", labelKey: "TL_PRINT" },
              leftIcon: "print",
              rightIcon: "arrow_drop_down",
              props: { variant: "outlined", style: { height: "60px", color: "#FE7A51" }, className: "tl-print-button" },
              menu: printMenu
            }
          }
        }

      },
      // gridDefination: {
      //   xs: 12,
      //   sm: 6
      // }
    }
  }
};
export const prepareDocumentsView = async (state, dispatch) => {
  let documentsPreview = [];

  // Get all documents from response
  let firenoc = get(
    state,
    "screenConfiguration.preparedFinalObject.FireNOCs[0]",
    {}
  );
  let buildingDocuments = jp.query(
    firenoc,
    "$.fireNOCDetails.buildings.*.applicationDocuments.*"
  );
  let applicantDocuments = jp.query(
    firenoc,
    "$.fireNOCDetails.applicantDetails.additionalDetail.ownerAuditionalDetail.documents.*"
  );
  let otherDocuments = jp.query(
    firenoc,
    "$.fireNOCDetails.additionalDetail.documents.*"
  );
  let allDocuments = [
    ...buildingDocuments,
    ...applicantDocuments,
    ...otherDocuments
  ];

  allDocuments.forEach((doc, index) => {
    documentsPreview.push({
      title: getTransformedLocale(doc.documentType || doc.title),
      fileStoreId: doc.fileStoreId,
      linkText: "View"
    });
    if(doc && doc.dropdown && doc.dropdown.value) {
      documentsPreview[index].dropdown = {
        value : doc.dropdown.value
      }
    }
  });
  if(documentsPreview && documentsPreview.length <= 0) {
    let reduxDocuments = get(
      state,
      "screenConfiguration.preparedFinalObject.documentsUploadRedux",
      {}
    );
    jp.query(reduxDocuments, "$.*").forEach((doc, index) => {
      if (doc.documents && doc.documents.length > 0) {
        documentsPreview.push({
          title: getTransformedLocale(doc.documentCode),
          name: doc.documents[0].fileName,
          fileStoreId: doc.documents[0].fileStoreId,
          linkText: "View",
        });
        if(doc && doc.dropdown && doc.dropdown.value) {
          documentsPreview[index].dropdown = {
            value : doc.dropdown.value
          }
        }
      }

    });
    set(
      firenoc,
      "fireNOCDetails.applicantDetails.additionalDetail.ownerAuditionalDetail.documents",
      documentsPreview
    );
  }
  let fileStoreIds = jp.query(documentsPreview, "$.*.fileStoreId");
  let fileUrls =
    fileStoreIds.length > 0 ? await getFileUrlFromAPI(fileStoreIds) : {};
  documentsPreview = documentsPreview.map((doc, index) => {
    doc["link"] =
      (fileUrls &&
        fileUrls[doc.fileStoreId] &&
        getFileUrl(fileUrls[doc.fileStoreId])) ||
      "";
    doc["name"] =
      (fileUrls[doc.fileStoreId] &&
        decodeURIComponent(
          getFileUrl(fileUrls[doc.fileStoreId])
            .split("?")[0]
            .split("/")
            .pop()
            .slice(13)
        )) ||
      `Document - ${index + 1}`;
    return doc;
  });
  documentsPreview = uniqBy(documentsPreview, "fileStoreId");
  dispatch(prepareFinalObject("documentsPreview", documentsPreview));
  dispatch(prepareFinalObject("FireNOCs[0].fireNOCDetails.additionalDetail.documents", documentsPreview));

};

const prepareUoms = (state, dispatch) => {
  let buildings = get(
    state,
    "screenConfiguration.preparedFinalObject.FireNOCs[0].fireNOCDetails.buildings",
    []
  );
  buildings.forEach((building, index) => {
    let uoms = get(building, "uoms", []);
    let uomsMap = {};
    uoms.forEach(uom => {if(uom.active==true){
        uomsMap[uom.code] = uom.value;} });
    dispatch(
      prepareFinalObject(
        `FireNOCs[0].fireNOCDetails.buildings[${index}].uomsMap`,
        uomsMap
      )
    );

    // Display UOMS on search preview page
    uoms.forEach(item => {
      let labelElement = getLabelWithValue(
        {
          labelName: item.code,
          labelKey: `NOC_PROPERTY_DETAILS_${item.code}_LABEL`
        },
        {
          jsonPath: `FireNOCs[0].fireNOCDetails.buildings[0].uomsMap.${
            item.code
            }`,
          callBack: checkValueForNA,
        }
      );

      dispatch(
        handleField(
          "search-preview",
          "components.div.children.body.children.cardContent.children.propertySummary.children.cardContent.children.cardOne.props.scheama.children.cardContent.children.propertyContainer.children",
          item.code,
          labelElement
        )
      );
    });
  });
};

// const prepareDocumentsUploadRedux = (state, dispatch) => {
//   dispatch(prepareFinalObject("documentsUploadRedux", documentsUploadRedux));
// };

const setSearchResponse = async (
  action,
  state,
  dispatch,
  applicationNumber,
  tenantId
) => {
  const fireDetails = get(state.screenConfiguration.preparedFinalObject, 'FireNOCs', []);
  const response = await getSearchResults([
    {
      key: "tenantId",
      value: tenantId
    },
    { key: "applicationNumber", value: applicationNumber }
  ]);
  const equals = (a, b) =>
  a.length === b.length &&
  a.every((v, i) => v === b[i]);
  if( getQueryArg(window.location.href, "edited") ) {
    if(fireDetails && fireDetails.length > 0 && !(equals(fireDetails, response.FireNOCs)) && (fireDetails[0].fireNOCDetails.applicationNumber === response.FireNOCs[0].fireNOCDetails.applicationNumber)) {
      // const response = sampleSingleSearch();
      dispatch(prepareFinalObject("FireNOCs", fireDetails, []));
    }
    else {
      dispatch(prepareFinalObject("FireNOCs", get(response, "FireNOCs", [])));
    }
  }
  else {
    dispatch(prepareFinalObject("FireNOCs", get(response, "FireNOCs", [])));
  }
  // const response = sampleSingleSearch();
  // set(response,'FireNOCs[0].fireNOCDetails.additionalDetail.assignee[0]','');
  // set(response,'FireNOCs[0].fireNOCDetails.additionalDetail.comment','');
  
  // set(response,'FireNOCs[0].fireNOCDetails.additionalDetail.wfDocuments',[]);
  // dispatch(prepareFinalObject("FireNOCs", get(response, "FireNOCs", [])));

  // Set Institution/Applicant info card visibility
  if (
    get(
      response,
      "FireNOCs[0].fireNOCDetails.applicantDetails.ownerShipType",
      ""
    ).startsWith("INSTITUTION")
  ) {
    dispatch(
      handleField(
        "search-preview",
        "components.div.children.body.children.cardContent.children.applicantSummary",
        "visible",
        false
      )
    );
  } else {
    dispatch(
      handleField(
        "search-preview",
        "components.div.children.body.children.cardContent.children.institutionSummary",
        "visible",
        false
      )
    );
  }
  let value = get(
    state.screenConfiguration.preparedFinalObject,
    "FireNOCs[0].fireNOCDetails.propertyDetails.address.areaType","");
  if( value === 'Urban' ) {           
    set(
      action,
      "screenConfig.components.div.children.body.children.cardContent.children.propertySummary.children.cardContent.children.cardTwo.children.cardContent.children.propertyLocationContainer.children.subDistrict.visible",
      false
    );   
    set(
      action,
      "screenConfig.components.div.children.body.children.cardContent.children.propertySummary.children.cardContent.children.cardTwo.children.cardContent.children.propertyLocationContainer.children.villageName.visible",
      false
    );  
  } else {      
    set(
      action,
      "screenConfig.components.div.children.body.children.cardContent.children.propertySummary.children.cardContent.children.cardTwo.children.cardContent.children.propertyLocationContainer.children.city.visible",
      false
    );
    set(
      action,
      "screenConfig.components.div.children.body.children.cardContent.children.propertySummary.children.cardContent.children.cardTwo.children.cardContent.children.propertyLocationContainer.children.mohalla.visible",
      false
    );
  }

  let NOCTypeDta= get(response,
    "FireNOCs[0].fireNOCDetails.fireNOCType",
      ""
    )
    debugger;
    let diffDays ;
    const getdate=get(response, "FireNOCs[0].fireNOCDetails.applicationNumber");
    // if(getdate){
    // const cd= getdate.split("PB-FN-");
	  // const currentDate = new Date();
    // const appActualDate=cd[1].slice(0,10);
    // let datef=(appActualDate.split("-"));
    // var applicationdate = new Date(parseInt(datef[0]),parseInt(datef[1])-1, datef[2]);
	  // const diffTime = Math.abs(currentDate - applicationdate);
	  // diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
    // let nintydayscondition = parseInt(diffDays)+90;
    // console.log(diffTime + " milliseconds");
    // console.log(nintydayscondition + " days");
    // }
      	  
    if(NOCTypeDta === "RENEWAL"){
     // if (diffDays <= 455){
      dispatch(
        handleField(
          "search-preview",
          "components.div.children.body.children.cardContent.children.nocSummary.children.cardContent.children.body.children.fireNocNumber",
          "visible",
          false
        )
      );
    // }
    // else{
      // alert("NOC expired on 01-01-2023, max allowed time to apply for renewal is 90 days after expiry");
   // }
    }
    else{
      dispatch(
        handleField(
          "search-preview",
          "components.div.children.body.children.cardContent.children.nocSummary.children.cardContent.children.body.children.oldFireNocNumber",
          "visible",
          false
        )
      );
    }
  prepareDocumentsView(state, dispatch);
  prepareUoms(state, dispatch);
  await loadPdfGenerationData(applicationNumber, tenantId);
  let status = get(
    state,
    "screenConfiguration.preparedFinalObject.FireNOCs[0].fireNOCDetails.status"
  );
  const printCont = downloadPrintContainer(
    state,
    dispatch
  );
  if (status !== "INITIATED") {

    set(
      action,
      "screenConfig.components.div.children.headerDiv.children.helpSection.children",
      printCont
    )
  }
generateBill(state, dispatch, applicationNumber, tenantId, status);
};

const screenConfig = {
  uiFramework: "material-ui",
  name: "search-preview",
  beforeInitScreen: (action, state, dispatch) => {
    let applicationNumber =
      getQueryArg(window.location.href, "applicationNumber") ||
      get(
        state.screenConfiguration.preparedFinalObject,
        "FireNOCs[0].fireNOCDetails.applicationNumber"
      );
    const tenantId = getQueryArg(window.location.href, "tenantId");
    loadUlbLogo(tenantId);
    generateBill(dispatch, applicationNumber, tenantId);
    const queryObject = [
      { key: "tenantId", value: tenantId },
      { key: "businessServices", value: "FIRENOC" }
    ];
    setBusinessServiceDataToLocalStorage(queryObject, dispatch);

    setSearchResponse(action, state, dispatch, applicationNumber, tenantId);
    // Hide edit buttons
    set(
      action,
      "screenConfig.components.div.children.body.children.cardContent.children.nocSummary.children.cardContent.children.header.children.editSection.visible",
      false
    );
    set(
      action,
      "screenConfig.components.div.children.body.children.cardContent.children.propertySummary.children.cardContent.children.header.children.editSection.visible",
      false
    );
    set(
      action,
      "screenConfig.components.div.children.body.children.cardContent.children.applicantSummary.children.cardContent.children.header.children.editSection.visible",
      false
    );
    set(
      action,
      "screenConfig.components.div.children.body.children.cardContent.children.institutionSummary.children.cardContent.children.header.children.editSection.visible",
      false
    );
    set(
      action,
      "screenConfig.components.div.children.body.children.cardContent.children.documentsSummary.children.cardContent.children.header.children.editSection.visible",
      false
    );
    return action;
  },
  components: {
    div: {
      uiFramework: "custom-atoms",
      componentPath: "Div",
      props: {
        className: "common-div-css"
      },
      children: {
        headerDiv: {
          uiFramework: "custom-atoms",
          componentPath: "Container",
          children: {
            header: {
              gridDefination: {
                xs: 12,
                sm: 8
              },
              ...titlebar
            },
            helpSection: {
              uiFramework: "custom-atoms",
              componentPath: "Container",
              props: {
                color: "primary",
                style: { justifyContent: "flex-end" }
              },
              gridDefination: {
                xs: 12,
                sm: 4,
                align: "right"
              }
            }

          }
        },
        taskStatus: {
          uiFramework: "custom-containers-local",
          componentPath: "WorkFlowContainer",
          moduleName: "egov-workflow",
          // visible: process.env.REACT_APP_NAME === "Citizen" ? false : true,
          props: {
            dataPath: "FireNOCs",
            moduleName: "FIRENOC",
            updateUrl: "/firenoc-services/v1/_update"
          }
        },
        body: getCommonCard({
          estimateSummary: estimateSummary,
          nocSummary: nocSummary,
          propertySummary: propertySummary,
          applicantSummary: applicantSummary,
          institutionSummary: institutionSummary,
          documentsSummary: documentsSummary
        }),
        citizenFooter:
          process.env.REACT_APP_NAME === "Citizen" ? citizenFooter : {}
      }
    }
  }
};

export default screenConfig;
