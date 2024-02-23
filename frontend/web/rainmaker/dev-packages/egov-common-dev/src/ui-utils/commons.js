	import commonConfig from "config/common.js";
	import { convertDateToEpoch } from "egov-ui-framework/ui-config/screens/specs/utils";
	import { handleScreenConfigurationFieldChange as handleField, prepareFinalObject, toggleSnackbar, toggleSpinner } from "egov-ui-framework/ui-redux/screen-configuration/actions";
	import { httpRequest } from "egov-ui-framework/ui-utils/api";
	import { getFileUrlFromAPI, getTransformedLocale } from "egov-ui-framework/ui-utils/commons";
	import { downloadPdf, openPdf, printPdf } from "egov-ui-kit/utils/commons";
	import { getTenantId } from "egov-ui-kit/utils/localStorageUtils";
	import jp from "jsonpath";
	import get from "lodash/get";
	import set from "lodash/set";
	import store from "ui-redux/store";
	import { convertEpochToDate, getTranslatedLabel } from "../ui-config/screens/specs/utils";
	import axios from 'axios';
	import { getQueryArg } from "egov-ui-framework/ui-utils/commons";
	const PAYMENTSEARCH = {
		GET: {
		  URL: "/collection-services/payments/",
		  ACTION: "_search",
		},
	  };
	  const getPaymentSearchAPI = (businessService='')=>{
		if(businessService=='-1'){
		  return `${PAYMENTSEARCH.GET.URL}${PAYMENTSEARCH.GET.ACTION}`
		}else if (process.env.REACT_APP_NAME === "Citizen") {
		  return `${PAYMENTSEARCH.GET.URL}${PAYMENTSEARCH.GET.ACTION}`;
		}
		return `${PAYMENTSEARCH.GET.URL}${businessService}/${PAYMENTSEARCH.GET.ACTION}`;
	  }
	const handleDeletedCards = (jsonObject, jsonPath, key) => {
	  let originalArray = get(jsonObject, jsonPath, []);
	  let modifiedArray = originalArray.filter(element => {
		return element.hasOwnProperty(key) || !element.hasOwnProperty("isDeleted");
	  });
	  modifiedArray = modifiedArray.map(element => {
		if (element.hasOwnProperty("isDeleted")) {
		  element["isActive"] = false;
		}
		return element;
	  });
	  set(jsonObject, jsonPath, modifiedArray);
	};

	export const getLocaleLabelsforTL = (label, labelKey, localizationLabels) => {
	  if (labelKey) {
		let translatedLabel = getTranslatedLabel(labelKey, localizationLabels);
		if (!translatedLabel || labelKey === translatedLabel) {
		  return label;
		} else {
		  return translatedLabel;
		}
	  } else {
		return label;
	  }
	};

	export const findItemInArrayOfObject = (arr, conditionCheckerFn) => {
	  for (let i = 0; i < arr.length; i++) {
		if (conditionCheckerFn(arr[i])) {
		  return arr[i];
		}
	  }
	};

	export const getSearchResults = async (queryObject, dispatch) => {
	  try {
		store.dispatch(toggleSpinner());
		const response = await httpRequest(
		  "post",
		  "/firenoc-services/v1/_search",
		  "",
		  queryObject
		);
		store.dispatch(toggleSpinner());
		return response;
	  } catch (error) {
		store.dispatch(
		  toggleSnackbar(
			true,
			{ labelName: error.message, labelKey: error.message },
			"error"
		  )
		);
		throw error;
	  }
	};

	export const createUpdateNocApplication = async (state, dispatch, status) => {
	  let nocId = get(
		state,
		"screenConfiguration.preparedFinalObject.FireNOCs[0].id"
	  );
	  let method = nocId ? "UPDATE" : "CREATE";
	  try {
		let payload = get(
		  state.screenConfiguration.preparedFinalObject,
		  "FireNOCs",
		  []
		);
		let tenantId = get(
		  state.screenConfiguration.preparedFinalObject,
		  "FireNOCs[0].fireNOCDetails.propertyDetails.address.city",
		  getTenantId()
		);
		set(payload[0], "tenantId", tenantId);
		set(payload[0], "fireNOCDetails.action", status);

		// Get uploaded documents from redux
		let reduxDocuments = get(
		  state,
		  "screenConfiguration.preparedFinalObject.documentsUploadRedux",
		  {}
		);

		handleDeletedCards(payload[0], "fireNOCDetails.buildings", "id");
		handleDeletedCards(
		  payload[0],
		  "fireNOCDetails.applicantDetails.owners",
		  "id"
		);

		let buildings = get(payload, "[0].fireNOCDetails.buildings", []);
		buildings.forEach((building, index) => {
		  // GET UOMS FOR THE SELECTED BUILDING TYPE
		  let requiredUoms = get(
			state,
			"screenConfiguration.preparedFinalObject.applyScreenMdmsData.firenoc.BuildingType",
			[]
		  ).filter(buildingType => {
			return buildingType.code === building.usageType;
		  });
		  requiredUoms = get(requiredUoms, "[0].uom", []);
		  // GET UNIQUE UOMS LIST INCLUDING THE DEFAULT
		  let allUoms = [
			...new Set([
			  ...requiredUoms,
			  ...[
				"NO_OF_FLOORS",
				"NO_OF_BASEMENTS",
				"PLOT_SIZE",
				"BUILTUP_AREA",
				"HEIGHT_OF_BUILDING"
			  ]
			])
		  ];
		  let finalUoms = [];
		  allUoms.forEach(uom => {
			let value = get(building.uomsMap, uom);
			value &&
			  finalUoms.push({
				code: uom,
				value: parseInt(value),
				isActiveUom: requiredUoms.includes(uom) ? true : false,
				active: true
			  });
		  });

		  // Quick fix to repair old uoms
		  let oldUoms = get(
			payload[0],
			`fireNOCDetails.buildings[${index}].uoms`,
			[]
		  );
		  oldUoms.forEach((oldUom, oldUomIndex) => {
			set(
			  payload[0],
			  `fireNOCDetails.buildings[${index}].uoms[${oldUomIndex}].isActiveUom`,
			  false
			);
			set(
			  payload[0],
			  `fireNOCDetails.buildings[${index}].uoms[${oldUomIndex}].active`,
			  false
			);
		  });
		  // End Quick Fix

		  set(payload[0], `fireNOCDetails.buildings[${index}].uoms`, [
			...finalUoms,
			...oldUoms
		  ]);

		  // Set building documents
		  let uploadedDocs = [];
		  jp.query(reduxDocuments, "$.*").forEach(doc => {
			if (doc.documents && doc.documents.length > 0) {
			  if (
				doc.documentSubCode &&
				doc.documentSubCode.startsWith("BUILDING.BUILDING_PLAN")
			  ) {
				if (doc.documentCode === building.name) {
				  uploadedDocs = [
					...uploadedDocs,
					{
					  tenantId: tenantId,
					  documentType: doc.documentSubCode,
					  fileStoreId: doc.documents[0].fileStoreId
					}
				  ];
				}
			  }
			}
		  });
		  set(
			payload[0],
			`fireNOCDetails.buildings[${index}].applicationDocuments`,
			uploadedDocs
		  );
		});

		// Set owners & other documents
		let ownerDocuments = [];
		let otherDocuments = [];
		jp.query(reduxDocuments, "$.*").forEach(doc => {
		  if (doc.documents && doc.documents.length > 0) {
			if (doc.documentType === "OWNER") {
			  ownerDocuments = [
				...ownerDocuments,
				{
				  tenantId: tenantId,
				  documentType: doc.documentSubCode
					? doc.documentSubCode
					: doc.documentCode,
				  fileStoreId: doc.documents[0].fileStoreId
				}
			  ];
			} else if (!doc.documentSubCode) {
			  // SKIP BUILDING PLAN DOCS
			  otherDocuments = [
				...otherDocuments,
				{
				  tenantId: tenantId,
				  documentType: doc.documentCode,
				  fileStoreId: doc.documents[0].fileStoreId
				}
			  ];
			}
		  }
		});

		set(
		  payload[0],
		  "fireNOCDetails.applicantDetails.additionalDetail.documents",
		  ownerDocuments
		);
		set(
		  payload[0],
		  "fireNOCDetails.additionalDetail.documents",
		  otherDocuments
		);

		// Set Channel and Financial Year
		process.env.REACT_APP_NAME === "Citizen"
		  ? set(payload[0], "fireNOCDetails.channel", "CITIZEN")
		  : set(payload[0], "fireNOCDetails.channel", "COUNTER");
		set(payload[0], "fireNOCDetails.financialYear", "2019-20");

		// Set Dates to Epoch
		let owners = get(payload[0], "fireNOCDetails.applicantDetails.owners", []);
		owners.forEach((owner, index) => {
		  set(
			payload[0],
			`fireNOCDetails.applicantDetails.owners[${index}].dob`,
			convertDateToEpoch(get(owner, "dob"))
		  );
		});

		let response;
		if (method === "CREATE") {
		  response = await httpRequest(
			"post",
			"/firenoc-services/v1/_create",
			"",
			[],
			{ FireNOCs: payload }
		  );
		  response = furnishNocResponse(response);
		  dispatch(prepareFinalObject("FireNOCs", response.FireNOCs));
		  setApplicationNumberBox(state, dispatch);
		} else if (method === "UPDATE") {
		  response = await httpRequest(
			"post",
			"/firenoc-services/v1/_update",
			"",
			[],
			{ FireNOCs: payload }
		  );
		  response = furnishNocResponse(response);
		  dispatch(prepareFinalObject("FireNOCs", response.FireNOCs));
		}

		return { status: "success", message: response };
	  } catch (error) {
		dispatch(toggleSnackbar(true, { labelName: error.message }, "error"));

		// Revert the changed pfo in case of request failure
		let fireNocData = get(
		  state,
		  "screenConfiguration.preparedFinalObject.FireNOCs",
		  []
		);
		fireNocData = furnishNocResponse({ FireNOCs: fireNocData });
		dispatch(prepareFinalObject("FireNOCs", fireNocData.FireNOCs));

		return { status: "failure", message: error };
	  }
	};

	export const prepareDocumentsUploadData = (state, dispatch) => {
	  let documents = get(
		state,
		"screenConfiguration.preparedFinalObject.applyScreenMdmsData.FireNoc.Documents",
		[]
	  );
	  documents = documents.filter(item => {
		return item.active;
	  });
	  let documentsContract = [];
	  let tempDoc = {};
	  documents.forEach(doc => {
		let card = {};
		card["code"] = doc.documentType;
		card["title"] = doc.documentType;
		card["cards"] = [];
		tempDoc[doc.documentType] = card;
	  });

	  documents.forEach(doc => {
		// Handle the case for multiple muildings
		if (
		  doc.code === "BUILDING.BUILDING_PLAN" &&
		  doc.hasMultipleRows &&
		  doc.options
		) {
		  let buildingsData = get(
			state,
			"screenConfiguration.preparedFinalObject.FireNOCs[0].fireNOCDetails.buildings",
			[]
		  );

		  buildingsData.forEach(building => {
			let card = {};
			card["name"] = building.name;
			card["code"] = doc.code;
			card["hasSubCards"] = true;
			card["subCards"] = [];
			doc.options.forEach(subDoc => {
			  let subCard = {};
			  subCard["name"] = subDoc.code;
			  subCard["required"] = subDoc.required ? true : false;
			  card.subCards.push(subCard);
			});
			tempDoc[doc.documentType].cards.push(card);
		  });
		} else {
		  let card = {};
		  card["name"] = doc.code;
		  card["code"] = doc.code;
		  card["required"] = doc.required ? true : false;
		  if (doc.hasDropdown && doc.dropdownData) {
			let dropdown = {};
			dropdown.label = "NOC_SELECT_DOC_DD_LABEL";
			dropdown.required = true;
			dropdown.menu = doc.dropdownData.filter(item => {
			  return item.active;
			});
			dropdown.menu = dropdown.menu.map(item => {
			  return { code: item.code, label: getTransformedLocale(item.code) };
			});
			card["dropdown"] = dropdown;
		  }
		  tempDoc[doc.documentType].cards.push(card);
		}
	  });

	  Object.keys(tempDoc).forEach(key => {
		documentsContract.push(tempDoc[key]);
	  });

	  dispatch(prepareFinalObject("documentsContract", documentsContract));
	};

	export const prepareDocumentsUploadRedux = (state, dispatch) => {
	  const {
		documentsList,
		documentsUploadRedux = {},
		prepareFinalObject
	  } = this.props;
	  let index = 0;
	  documentsList.forEach(docType => {
		docType.cards &&
		  docType.cards.forEach(card => {
			if (card.subCards) {
			  card.subCards.forEach(subCard => {
				let oldDocType = get(
				  documentsUploadRedux,
				  `[${index}].documentType`
				);
				let oldDocCode = get(
				  documentsUploadRedux,
				  `[${index}].documentCode`
				);
				let oldDocSubCode = get(
				  documentsUploadRedux,
				  `[${index}].documentSubCode`
				);
				if (
				  oldDocType != docType.code ||
				  oldDocCode != card.name ||
				  oldDocSubCode != subCard.name
				) {
				  documentsUploadRedux[index] = {
					documentType: docType.code,
					documentCode: card.name,
					documentSubCode: subCard.name
				  };
				}
				index++;
			  });
			} else {
			  let oldDocType = get(documentsUploadRedux, `[${index}].documentType`);
			  let oldDocCode = get(documentsUploadRedux, `[${index}].documentCode`);
			  if (oldDocType != docType.code || oldDocCode != card.name) {
				documentsUploadRedux[index] = {
				  documentType: docType.code,
				  documentCode: card.name,
				  isDocumentRequired: card.required,
				  isDocumentTypeRequired: card.dropdown
					? card.dropdown.required
					: false
				};
			  }
			}
			index++;
		  });
	  });
	  prepareFinalObject("documentsUploadRedux", documentsUploadRedux);
	};

	export const furnishNocResponse = response => {
	  // Handle applicant ownership dependent dropdowns
	  let ownershipType = get(
		response,
		"FireNOCs[0].fireNOCDetails.applicantDetails.ownerShipType"
	  );
	  set(
		response,
		"FireNOCs[0].fireNOCDetails.applicantDetails.ownerShipMajorType",
		ownershipType == undefined ? "SINGLE" : ownershipType.split(".")[0]
	  );

	  // Prepare UOMS and Usage Type Dropdowns in required format
	  let buildings = get(response, "FireNOCs[0].fireNOCDetails.buildings", []);
	  buildings.forEach((building, index) => {
		let uoms = get(building, "uoms", []);
		let uomMap = {};
		uoms.forEach(uom => {
		  uomMap[uom.code] = `${uom.value}`;
		});
		set(
		  response,
		  `FireNOCs[0].fireNOCDetails.buildings[${index}].uomsMap`,
		  uomMap
		);

		let usageType = get(building, "usageType");
		set(
		  response,
		  `FireNOCs[0].fireNOCDetails.buildings[${index}].usageTypeMajor`,
		  usageType == undefined ? "" : usageType.split(".")[0]
		);
	  });

	  return response;
	};

	export const setApplicationNumberBox = (state, dispatch, applicationNo) => {
	  if (!applicationNo) {
		applicationNo = get(
		  state,
		  "screenConfiguration.preparedFinalObject.FireNOCs[0].fireNOCDetails.applicationNumber",
		  null
		);
	  }

	  if (applicationNo) {
		dispatch(
		  handleField(
			"apply",
			"components.div.children.headerDiv.children.header.children.applicationNumber",
			"visible",
			true
		  )
		);
		dispatch(
		  handleField(
			"apply",
			"components.div.children.headerDiv.children.header.children.applicationNumber",
			"props.number",
			applicationNo
		  )
		);
	  }
	};

	export const downloadReceiptFromFilestoreID = (fileStoreId, mode, tenantId,showConfirmation=false) => {
	  getFileUrlFromAPI(fileStoreId, tenantId).then(async (fileRes) => {
		if (fileRes && !fileRes[fileStoreId]) {
		  console.error('ERROR IN DOWNLOADING RECEIPT');
		  return;
		}
		if (mode === 'download') {
		  downloadPdf(fileRes[fileStoreId]);
		  if(showConfirmation){
			if(localStorage.getItem('receipt-channel')=='whatsapp'&&localStorage.getItem('receipt-redirectNumber')!=''){
				setTimeout(() => {
				  const weblink = "https://api.whatsapp.com/send?phone=" + localStorage.getItem('receipt-redirectNumber') + "&text=" + ``;
				  window.location.href = weblink
				}, 1500)
			}
			store.dispatch(toggleSnackbar(true, { labelName: "Success in Receipt Generation", labelKey: "SUCCESS_IN_GENERATION_RECEIPT" }
		  , "success"));
		  }
		} else if (mode === 'open') {
		  openPdf(fileRes[fileStoreId], '_self')
		  if(showConfirmation){
			if(localStorage.getItem('receipt-channel')=='whatsapp'&&localStorage.getItem('receipt-redirectNumber')!=''){
				setTimeout(() => {
				  const weblink = "https://api.whatsapp.com/send?phone=" + localStorage.getItem('receipt-redirectNumber') + "&text=" + ``;
				  window.location.href = weblink
				}, 1500)
			}
			store.dispatch(toggleSnackbar(true, { labelName: "Success in Receipt Generation", labelKey: "SUCCESS_IN_GENERATION_RECEIPT" }
		  , "success"));
		  }
		}
		else {
		  printPdf(fileRes[fileStoreId]);
		}
	  });
	}
	const getUserDataFromUuid = async bodyObject => {
  try {
    const response = await httpRequest(
      "post",
      "/user/_search",
      "",
      [],
      bodyObject
    );
    return response;
  } catch (error) {
    console.log(error);
    return {};
  }
};
export const download = async(receiptQueryString, mode = "download", configKey, state,showConfirmation=false) => {
	  if (state && process.env.REACT_APP_NAME === "Citizen" && configKey === "consolidatedreceipt") {
		const uiCommonPayConfig = get(state.screenConfiguration.preparedFinalObject, "commonPayInfo");
		configKey = get(uiCommonPayConfig, "receiptKey", "consolidatedreceipt")
	  }

 const FETCHRECEIPT = {
    GET: {
      URL: "/collection-services/payments/_search",
      ACTION: "_get",
    },
  };
	  const DOWNLOADRECEIPT = {
		GET: {
		  URL: "/pdf-service/v1/_create",
		  ACTION: "_get",
		},
	  };
	  let consumerCode = getQueryArg(window.location.href, "consumerCode")?getQueryArg(window.location.href, "consumerCode"):receiptQueryString[0].value;
  let tenantId = getQueryArg(window.location.href, "tenantId")?getQueryArg(window.location.href, "tenantId"):receiptQueryString[1].value;
  let applicationNumber = getQueryArg(window.location.href, "applicationNumber");
  let queryObject = [
    { key: "tenantId", value:tenantId },
    { key: "applicationNumber", value: consumerCode?consumerCode:applicationNumber}
  ];

  if(consumerCode !=null && consumerCode.includes("PG-CH"))
  {
	configKey="consolidatedreceipt";
  }
  let queryObjectForPT = [
    { key: "tenantId", value:tenantId },
    { key: "propertyIds", value: consumerCode?consumerCode:applicationNumber}
  ];
  const FETCHFIREDETAILS = {
    GET: {
      URL: "/firenoc-services/v1/_search",
      ACTION: "_get",
    },
  };
  const FETCHPROPERTYDETAILS = {
    GET: {
      URL: "/property-services/property/_search",
      ACTION: "_get",
    },
  };
  const FETCHTRADEDETAILS = {
    GET: {
      URL: "/tl-services/v1/_search",
      ACTION: "_get",
    },
  };
  const responseForTrade = await httpRequest("post", FETCHTRADEDETAILS.GET.URL, FETCHTRADEDETAILS.GET.ACTION,queryObject);
  const response =  await httpRequest("post", FETCHFIREDETAILS.GET.URL, FETCHFIREDETAILS.GET.ACTION,queryObject);
  const responseForPT =  await httpRequest("post", FETCHPROPERTYDETAILS.GET.URL, FETCHPROPERTYDETAILS.GET.ACTION,queryObjectForPT);
 
  let uuid=responseForPT && responseForPT.Properties[0]?responseForPT.Properties[0].auditDetails.lastModifiedBy:null;
  let data = {};
  let bodyObject = {
    uuid: [uuid]
  };
  let responseForUser = await getUserDataFromUuid(bodyObject);
  let lastmodifier=responseForUser && responseForUser.user[0]?responseForUser.user[0].name:null;

	  let businessService = '';
	  receiptQueryString && Array.isArray(receiptQueryString) && receiptQueryString.map(query => {
		if (query.key == "businessService") {
		  businessService = query.value;
		}
	  })
	  receiptQueryString = receiptQueryString && Array.isArray(receiptQueryString) && receiptQueryString.filter(query => query.key != "businessService")
	  try {
		await httpRequest("post",getPaymentSearchAPI(businessService), FETCHRECEIPT.GET.ACTION, receiptQueryString).then((payloadReceiptDetails) => {
 if(payloadReceiptDetails.Payments[0].payerName!=null){
      payloadReceiptDetails.Payments[0].payerName=payloadReceiptDetails.Payments[0].payerName.trim();}
      else if(payloadReceiptDetails.Payments[0].payerName == null && payloadReceiptDetails.Payments[0].paymentDetails[0].businessService=="FIRENOC" && payloadReceiptDetails.Payments[0].paidBy !=null)
       { payloadReceiptDetails.Payments[0].payerName=payloadReceiptDetails.Payments[0].paidBy.trim();
      }
      if(payloadReceiptDetails.Payments[0].paidBy!=null)
      {
        payloadReceiptDetails.Payments[0].paidBy=payloadReceiptDetails.Payments[0].paidBy.trim();
      }
      if(payloadReceiptDetails.Payments[0].paymentDetails[0].receiptNumber.includes("MP")){
        let tax,field,cgst,sgst;
		if(payloadReceiptDetails.Payments[0].paymentDetails[0].receiptDate <= 1638921600000)
		configKey="consolidatedreceiptold";
let billaccountarray=payloadReceiptDetails.Payments[0].paymentDetails[0].bill.billDetails[0].billAccountDetails;
billaccountarray.map(element => {
if(element.taxHeadCode.includes("CGST")){  cgst=element.amount;}
else if(element.taxHeadCode.includes("SGST")){  sgst=element.amount;}
else if(element.taxHeadCode.includes("FIELD_FEE")){  field=element.amount;}
else  { tax=element.amount;}
});
let taxheads = {
  "tax": tax,
  "fieldfee":field,
  "cgst":cgst,
  "sgst":sgst
  }
payloadReceiptDetails.Payments[0].paymentDetails[0].additionalDetails=taxheads; 
      }
      let assessmentYear="",assessmentYearForReceipt="";
      let count=0;
	  if(payloadReceiptDetails.Payments[0].paymentDetails[0].businessService=="PT"){
        let reasonss = null;
        let adhocPenaltyReason=null,adhocRebateReason=null;
        if(state && get(state.screenConfiguration,"preparedFinalObject") && (get(state.screenConfiguration.preparedFinalObject,"adhocExemptionPenalty.adhocExemptionReason") || get(state.screenConfiguration.preparedFinalObject,"adhocExemptionPenalty.adhocPenaltyReason")))
          {
            adhocPenaltyReason = get(
            state.screenConfiguration.preparedFinalObject,"adhocExemptionPenalty.adhocPenaltyReason");
           adhocRebateReason = get(
            state.screenConfiguration.preparedFinalObject,"adhocExemptionPenalty.adhocExemptionReason");
            if(adhocPenaltyReason == "Others")
            { adhocPenaltyReason=get(
              state.screenConfiguration.preparedFinalObject,"adhocExemptionPenalty.adhocOtherPenaltyReason");}
            if(adhocRebateReason == "Others")
              { adhocRebateReason=get(
                state.screenConfiguration.preparedFinalObject,"adhocExemptionPenalty.adhocOtherExemptionReason");}
          }
		  reasonss = {
            "adhocPenaltyReason": adhocPenaltyReason,
            "adhocRebateReason":adhocRebateReason,
            "lastModifier":lastmodifier
            }
       payloadReceiptDetails.Payments[0].paymentDetails[0].bill.additionalDetails=reasonss; 
          let arrearRow={};  let arrearArray=[];
          let taxRow={};  let taxArray=[];
         

          let roundoff=0,tax=0,firecess=0,cancercess=0,penalty=0,rebate=0,interest=0,usage_exemption=0,special_category_exemption=0,adhoc_penalty=0,adhoc_rebate=0,total=0;
          let roundoffT=0,taxT=0,firecessT=0,cancercessT=0,penaltyT=0,rebateT=0,interestT=0,usage_exemptionT=0,special_category_exemptionT=0,adhoc_penaltyT=0,adhoc_rebateT=0,totalT=0;

          payloadReceiptDetails.Payments[0].paymentDetails[0].bill.billDetails.map(element => {

          if(element.amount >0 || element.amountPaid>0)
          { count=count+1;
            totalT=0;
            let toDate=convertEpochToDate(element.toPeriod).split("/")[2];
            let fromDate=convertEpochToDate(element.fromPeriod).split("/")[2];
            assessmentYear=assessmentYear==""?fromDate+"-"+toDate+"(Rs."+element.amountPaid+")":assessmentYear+","+fromDate+"-"+toDate+"(Rs."+element.amountPaid+")";
         assessmentYearForReceipt=fromDate+"-"+toDate;
       
    element.billAccountDetails.map(ele => {
    if(ele.taxHeadCode == "PT_TAX")
    {tax=ele.adjustedAmount;
      taxT=ele.amount}
    else if(ele.taxHeadCode == "PT_TIME_REBATE")
    {rebate=ele.adjustedAmount;
      rebateT=ele.amount;}
    else if(ele.taxHeadCode == "PT_CANCER_CESS")
    {cancercess=ele.adjustedAmount;
    cancercessT=ele.amount;}
    else if(ele.taxHeadCode == "PT_FIRE_CESS")
    {firecess=ele.adjustedAmount;
      firecessT=ele.amount;}
    else if(ele.taxHeadCode == "PT_TIME_INTEREST")
    {interest=ele.adjustedAmount;
      interestT=ele.amount;}
    else if(ele.taxHeadCode == "PT_TIME_PENALTY")
    {penalty=ele.adjustedAmount;
      penaltyT=ele.amount;}
    else if(ele.taxHeadCode == "PT_OWNER_EXEMPTION")
    {special_category_exemption=ele.adjustedAmount;
      special_category_exemptionT=ele.amount;}	
    else if(ele.taxHeadCode == "PT_ROUNDOFF")
    {roundoff=ele.adjustedAmount;
      roundoffT=ele.amount;}	
    else if(ele.taxHeadCode == "PT_UNIT_USAGE_EXEMPTION")
    {usage_exemption=ele.adjustedAmount;
      usage_exemptionT=ele.amount;}	
    else if(ele.taxHeadCode == "PT_ADHOC_PENALTY")
    {adhoc_penalty=ele.adjustedAmount;
      adhoc_penaltyT=ele.amount;}
    else if(ele.taxHeadCode == "PT_ADHOC_REBATE")
    {adhoc_rebate=ele.adjustedAmount;
      adhoc_rebateT=ele.amount;}
  
    totalT=totalT+ele.amount;
    });
  arrearRow={
  "year":assessmentYearForReceipt,
  "tax":tax,
  "firecess":firecess,
  "cancercess":cancercess,
  "penalty":penalty,
  "rebate": rebate,
  "interest":interest,
  "usage_exemption":usage_exemption,
  "special_category_exemption": special_category_exemption,
  "adhoc_penalty":adhoc_penalty,
  "adhoc_rebate":adhoc_rebate,
  "roundoff":roundoff,
  "total":element.amountPaid
  };
  taxRow={
    "year":assessmentYearForReceipt,
    "tax":taxT,
    "firecess":firecessT,
    "cancercess":cancercessT,
    "penalty":penaltyT,
    "rebate": rebateT,
    "interest":interestT,
    "usage_exemption":usage_exemptionT,
    "special_category_exemption": special_category_exemptionT,
    "adhoc_penalty":adhoc_penaltyT,
    "adhoc_rebate":adhoc_rebateT,
    "roundoff":roundoffT,
    "total":element.amount
    };
  arrearArray.push(arrearRow);
  taxArray.push(taxRow);
            } 
          });
  
		  if(count==0){  total=0; totalT=0;
			let index=payloadReceiptDetails.Payments[0].paymentDetails[0].bill.billDetails.length;
			let toDate=convertEpochToDate( payloadReceiptDetails.Payments[0].paymentDetails[0].bill.billDetails[0].toPeriod).split("/")[2];
			let fromDate=convertEpochToDate( payloadReceiptDetails.Payments[0].paymentDetails[0].bill.billDetails[0].fromPeriod).split("/")[2];
			assessmentYear=assessmentYear==""?fromDate+"-"+toDate:assessmentYear+","+fromDate+"-"+toDate; 
			assessmentYearForReceipt=fromDate+"-"+toDate;
			payloadReceiptDetails.Payments[0].paymentDetails[0].bill.billDetails[0].billAccountDetails.map(ele => {
			   
			  if(ele.taxHeadCode == "PT_TAX")
			  {tax=ele.adjustedAmount;
				taxT=ele.amount}
			  else if(ele.taxHeadCode == "PT_TIME_REBATE")
			  {rebate=ele.adjustedAmount;
				rebateT=ele.amount;}
			  else if(ele.taxHeadCode == "PT_CANCER_CESS")
			  {cancercess=ele.adjustedAmount;
			  cancercessT=ele.amount;}
			  else if(ele.taxHeadCode == "PT_FIRE_CESS")
			  {firecess=ele.adjustedAmount;
				firecessT=ele.amount;}
			  else if(ele.taxHeadCode == "PT_TIME_INTEREST")
			  {interest=ele.adjustedAmount;
				interestT=ele.amount;}
			  else if(ele.taxHeadCode == "PT_TIME_PENALTY")
			  {penalty=ele.adjustedAmount;
				penaltyT=ele.amount;}
			  else if(ele.taxHeadCode == "PT_OWNER_EXEMPTION")
			  {special_category_exemption=ele.adjustedAmount;
				special_category_exemptionT=ele.amount;}	
			  else if(ele.taxHeadCode == "PT_ROUNDOFF")
			  {roundoff=ele.adjustedAmount;
				roundoffT=ele.amount;}	
			  else if(ele.taxHeadCode == "PT_UNIT_USAGE_EXEMPTION")
			  {usage_exemption=ele.adjustedAmount;
				usage_exemptionT=ele.amount;}	
			  else if(ele.taxHeadCode == "PT_ADHOC_PENALTY")
			  {adhoc_penalty=ele.adjustedAmount;
				adhoc_penaltyT=ele.amount;}
			  else if(ele.taxHeadCode == "PT_ADHOC_REBATE")
			  {adhoc_rebate=ele.adjustedAmount;
				adhoc_rebateT=ele.amount;}
			
			  total=total+ele.adjustedAmount;
			  totalT=totalT+ele.amount;
  
			  });
			arrearRow={
			"year":assessmentYearForReceipt,
			"tax":tax,
			"firecess":firecess,
			"cancercess":cancercess,
			"penalty":penalty,
			"interest":interest,
			"usage_exemption":usage_exemption,
			"special_category_exemption": special_category_exemption,
			"adhoc_penalty":adhoc_penalty,
			"adhoc_rebate":adhoc_rebate,
			"roundoff":roundoff,
			"total": payloadReceiptDetails.Payments[0].paymentDetails[0].bill.billDetails[0].amountPaid

			};
			taxRow={
			  "year":assessmentYearForReceipt,
			  "tax":taxT,
			  "firecess":firecessT,
			  "cancercess":cancercessT,
			  "penalty":penaltyT,
			  "rebate": rebateT,
			  "interest":interestT,
			  "usage_exemption":usage_exemptionT,
			  "special_category_exemption": special_category_exemptionT,
			  "adhoc_penalty":adhoc_penaltyT,
			  "adhoc_rebate":adhoc_rebateT,
			  "roundoff":roundoffT,
			  "total": payloadReceiptDetails.Payments[0].paymentDetails[0].bill.billDetails[0].amount
			};
			arrearArray.push(arrearRow);
			taxArray.push(taxRow);
		  
	}  
          
          const details = {
        "assessmentYears": assessmentYear,
        "arrearArray":arrearArray,
        "taxArray": taxArray
            }
            payloadReceiptDetails.Payments[0].paymentDetails[0].additionalDetails=details; 
        }
     
	  if(payloadReceiptDetails.Payments[0].paymentDetails[0].businessService=="WS.ONE_TIME_FEE" || payloadReceiptDetails.Payments[0].paymentDetails[0].businessService=="SW.ONE_TIME_FEE" || payloadReceiptDetails.Payments[0].paymentDetails[0].businessService=="WS" || payloadReceiptDetails.Payments[0].paymentDetails[0].businessService=="SW"){
		configKey="ws-onetime-receipt";
		let dcbRow=null,dcbArray=[];
        let installment,totalamount=0;
        payloadReceiptDetails.Payments[0].paymentDetails[0].bill.billDetails.map((element,index) => {
      if(element.amountPaid >0 || element.amountPaid < 0)
      {
	  installment=convertEpochToDate(element.fromPeriod) +"-"+convertEpochToDate(element.toPeriod);
      element.billAccountDetails.map((dd)=>{
      if((dd.adjustedAmount > 0 || dd.adjustedAmount < 0) || (dd.amount < 0))
      {
        let code=null,amount=null;;
        if(dd.taxHeadCode == "WS_CHARGE")
        {
        code="Water Charges";         amount=dd.adjustedAmount;

        }
        else if( dd.taxHeadCode == "SW_CHARGE")
        {
        code="Sewerage Charges";         amount=dd.adjustedAmount;

        }
		else if(dd.taxHeadCode == "WS_Round_Off" || dd.taxHeadCode == "SW_Round_Off")
        {
        code="Round Off";         amount=dd.adjustedAmount;

        }
        else if(dd.taxHeadCode == "WS_TIME_INTEREST" || dd.taxHeadCode == "SW_TIME_INTEREST")
        {
        code="Interest";         amount=dd.adjustedAmount;

        }
        else if(dd.taxHeadCode == "WS_TIME_PENALTY" || dd.taxHeadCode == "SW_TIME_PENALTY")
        {
        code="Penalty";         amount=dd.adjustedAmount;

        }  else if(dd.taxHeadCode == "WS_SCRUTINY_FEE" || dd.taxHeadCode == "SW_SCRUTINY_FEE")
        {
        code="Scrutiny Fee";         amount=dd.adjustedAmount;

        }  else if(dd.taxHeadCode == "WS_ROAD_CUTTING_CHARGE" || dd.taxHeadCode == "SW_ROAD_CUTTING_CHARGE")
        {
        code="Road Cutting Charges";         amount=dd.adjustedAmount;

        }  else if(dd.taxHeadCode == "WS_METER_TESTING_FEE" || dd.taxHeadCode == "SW_METER_TESTING_FEE")
        {
        code="Meter Testing Fee";          amount=dd.adjustedAmount;

        }  else if(dd.taxHeadCode == "WS_SECURITY_DEPOSIT" || dd.taxHeadCode == "SW_SECURITY_DEPOSIT")
        {
        code="Security Deposit";          amount=dd.adjustedAmount;

        }  else if(dd.taxHeadCode == "WS_OTHER_FEE" || dd.taxHeadCode == "SW_OTHER_FEE")
        {
        code="Other Fee";         amount=dd.adjustedAmount;

        }else if(dd.taxHeadCode == "WS_USER_CHARGE" || dd.taxHeadCode == "SW_USER_CHARGE")
        {
        code="User Charges";         amount=dd.adjustedAmount;

        }else if(dd.taxHeadCode == "WS_CONNECTION_FEE" || dd.taxHeadCode == "SW_CONNECTION_FEE")
        {
        code="Connection Fee";         amount=dd.adjustedAmount;

        }else if(dd.taxHeadCode == "WS_COMPOSITION_FEE" || dd.taxHeadCode == "SW_COMPOSITION_FEE")
        {
        code="Composition Fee";         amount=dd.adjustedAmount;

        }else if(dd.taxHeadCode == "SW_ADVANCE_CARRYFORWARD" || dd.taxHeadCode == "WS_ADVANCE_CARRYFORWARD" )
        {
        code="Advance";         amount=-dd.amount;

        }
		if(payloadReceiptDetails.Payments[0].paymentDetails[0].businessService=="WS.ONE_TIME_FEE" || payloadReceiptDetails.Payments[0].paymentDetails[0].businessService=="SW.ONE_TIME_FEE")
        {
          dcbRow={
            "taxhead":code ,
            "amount":amount
          };
        }
        else{
        dcbRow={
          "taxhead":code + "("+installment+")",
          "amount":amount
        };}
totalamount=totalamount+amount;
dcbArray.push(dcbRow);
      }

     
      });    
      };
        });
		dcbRow={
			"taxhead":"Total Amount Paid",
			"amount":totalamount
		  };
		dcbArray.push(dcbRow);
        payloadReceiptDetails.Payments[0].paymentDetails[0].additionalDetails=dcbArray;
        }
      if(payloadReceiptDetails.Payments[0].paymentDetails[0].businessService=="TL"){
        configKey="tradelicense-receipt";
    
        const details = {
          "address": responseForTrade.Licenses[0].tradeLicenseDetail.address.locality.code
          }
    payloadReceiptDetails.Payments[0].paymentDetails[0].bill.billDetails[0].additionalDetails=details; 
      }
    
      if(payloadReceiptDetails.Payments[0].paymentDetails[0].businessService=="FIRENOC"){
      let owners=""; let contacts="";
      response.FireNOCs[0].fireNOCDetails.applicantDetails.owners.map(ele=>{
        if(owners=="")
        {owners=ele.name; 
          contacts=ele.mobileNumber;}
        else{
          owners=owners+","+ele.name; 
          contacts=contacts+","+ele.mobileNumber;
        }
      });
      payloadReceiptDetails.Payments[0].payerName=owners;
      payloadReceiptDetails.Payments[0].mobileNumber=contacts;
      let receiptDate=convertEpochToDate(payloadReceiptDetails.Payments[0].paymentDetails[0].receiptDate);
      let year=receiptDate.split("/")[2];
      year++;
      var nextyear=year;
      year--;
      var lastyear=year-1;
      let month=receiptDate.split("/")[1];
      let from=null,to=null;
      if(month<=3){ from=convertDateToEpoch("04/01/"+lastyear);
      to=convertDateToEpoch("03/31/"+year);}
      else{from=convertDateToEpoch("04/01/"+year);
      to=convertDateToEpoch("03/31/"+nextyear);}

	  let building='';
	  let length=response.FireNOCs[0].fireNOCDetails.buildings.length;
	  response.FireNOCs[0].fireNOCDetails.buildings.map( (item,index) => {
if(index == 0)
	  building=building + item.name;
else 
building = building + "," + item.name;
	  });


	  const details = {
	"address": "Building :"+building +","+ response.FireNOCs[0].fireNOCDetails.applicantDetails.owners[0].correspondenceAddress
  }
       payloadReceiptDetails.Payments[0].paymentDetails[0].bill.billDetails[0].additionalDetails=details; 
       payloadReceiptDetails.Payments[0].paymentDetails[0].bill.billDetails[0].fromPeriod=from;
       payloadReceiptDetails.Payments[0].paymentDetails[0].bill.billDetails[0].toPeriod=to; 
 
    } 
		 


		 const queryStr = [
			{ key: "key", value: configKey },
			{ key: "tenantId", value: receiptQueryString[1].value.split('.')[0] }
		  ]
		  if (payloadReceiptDetails && payloadReceiptDetails.Payments && payloadReceiptDetails.Payments.length == 0) {
			console.log("Could not find any receipts");
			store.dispatch(toggleSnackbar(true, { labelName: "Receipt not Found", labelKey: "ERR_RECEIPT_NOT_FOUND" }
			  , "error"));
			return;
		  }
		  // Setting the Payer and mobile from Bill to reflect it in PDF
		  state = state ? state : {};
		  if(payloadReceiptDetails.Payments[0].paymentMode=="CHEQUE" || payloadReceiptDetails.Payments[0].paymentMode=="DD" || payloadReceiptDetails.Payments[0].paymentMode=="OFFLINE_NEFT" || payloadReceiptDetails.Payments[0].paymentMode=="OFFLINE_RTGS" || payloadReceiptDetails.Payments[0].paymentMode=="ONLINE"){
			let ifsc = get(state, "screenConfiguration.preparedFinalObject.ReceiptTemp[0].instrument.ifscCode", null);
			let branchName = get(state, "screenConfiguration.preparedFinalObject.ReceiptTemp[0].instrument.branchName", null);
			let bank = get(state, "screenConfiguration.preparedFinalObject.ReceiptTemp[0].instrument.bank.name", null);
			payloadReceiptDetails.Payments[0].ifscCode=ifsc; 
			const details = [{
			   "branchName": branchName ,
			  "bankName":bank }
			]       
		  payloadReceiptDetails.Payments[0].additionalDetails=details; 
		}
		  let billDetails = get(state, "screenConfiguration.preparedFinalObject.ReceiptTemp[0].Bill[0]", null);
		  if ((billDetails && !billDetails.payerName) || !billDetails) {
			billDetails = {
			  payerName: get(state, "screenConfiguration.preparedFinalObject.applicationDataForReceipt.owners[0].name", null) || get(state, "screenConfiguration.preparedFinalObject.applicationDataForPdf.owners[0].name", null),
			  mobileNumber: get(state, "screenConfiguration.preparedFinalObject.applicationDataForReceipt.owners[0].mobile", null) || get(state, "screenConfiguration.preparedFinalObject.applicationDataForPdf.owners[0].mobile", null),
			};
		  }
		  
		    if(payloadReceiptDetails.Payments[0].paymentMode=="CASH")
      {
        payloadReceiptDetails.Payments[0].instrumentDate=null;
        payloadReceiptDetails.Payments[0].instrumentNumber=null;
      }
		  if (!payloadReceiptDetails.Payments[0].payerName && process.env.REACT_APP_NAME === "Citizen" && billDetails) {
			payloadReceiptDetails.Payments[0].payerName = billDetails.payerName;
			// payloadReceiptDetails.Payments[0].paidBy = billDetails.payer;
			payloadReceiptDetails.Payments[0].mobileNumber = billDetails.mobileNumber;
		  }
   if((payloadReceiptDetails.Payments[0].payerName==null || payloadReceiptDetails.Payments[0].mobileNumber==null)  && payloadReceiptDetails.Payments[0].paymentDetails[0].businessService=="FIRENOC" && process.env.REACT_APP_NAME === "Citizen")
      {
        payloadReceiptDetails.Payments[0].payerName=response.FireNOCs[0].fireNOCDetails.applicantDetails.owners[0].name;
        payloadReceiptDetails.Payments[0].mobileNumber= response.FireNOCs[0].fireNOCDetails.applicantDetails.owners[0].mobileNumber;
      }
		  const oldFileStoreId = get(payloadReceiptDetails.Payments[0], "fileStoreId")
		  if (oldFileStoreId) {
			downloadReceiptFromFilestoreID(oldFileStoreId, mode,undefined,showConfirmation)
		  }
		  else {
			httpRequest("post", DOWNLOADRECEIPT.GET.URL, DOWNLOADRECEIPT.GET.ACTION, queryStr, { Payments: payloadReceiptDetails.Payments }, { 'Accept': 'application/json' }, { responseType: 'arraybuffer' })
			  .then(res => {
				res.filestoreIds[0]
				if (res && res.filestoreIds && res.filestoreIds.length > 0) {
				  res.filestoreIds.map(fileStoreId => {
					downloadReceiptFromFilestoreID(fileStoreId, mode,undefined,showConfirmation)
				  })
				} else {
				  console.log('Some Error Occured while downloading Receipt!');
				  store.dispatch(toggleSnackbar(true, { labelName: "Error in Receipt Generation", labelKey: "ERR_IN_GENERATION_RECEIPT" }
					, "error"));
				}
			  });
		  }
		})
	  } catch (exception) {
		console.log('Some Error Occured while downloading Receipt!');
		store.dispatch(toggleSnackbar(true, { labelName: "Error in Receipt Generation", labelKey: "ERR_IN_GENERATION_RECEIPT" }
		  , "error"));
	  }
	}

	const getMdmsData = async (businesService) => {
		let mdmsBody =null;

		if(businesService=="SW"){
			mdmsBody={MdmsCriteria: {
			tenantId: "pb",
			moduleDetails: [
			  { moduleName: "sw-services-calculation", masterDetails: [{ name: "Penalty" }] }]
		  }};
		}
	else{mdmsBody={MdmsCriteria: {
		tenantId: "pb",
		moduleDetails: [
		  { moduleName: "ws-services-calculation", masterDetails: [{ name: "Penalty" }] }]
	  }};}
		try {
		  let payload = null;
		  payload = await httpRequest("post", "/egov-mdms-service/v1/_search", "_search", [], mdmsBody);
		  if (payload.MdmsRes['ws-services-calculation'] && payload.MdmsRes['ws-services-calculation'].Penalty !== undefined && payload.MdmsRes['ws-services-calculation'].Penalty.length > 0) {
		return payload.MdmsRes['ws-services-calculation'].Penalty[0].rate;
		  }
		else if (payload.MdmsRes['sw-services-calculation'] && payload.MdmsRes['sw-services-calculation'].Penalty !== undefined && payload.MdmsRes['sw-services-calculation'].Penalty.length > 0) {
			return payload.MdmsRes['sw-services-calculation'].Penalty[0].rate;
		}
		
		} catch (e) { console.log(e); }
	  
	  };
	export const downloadBill = async (consumerCode, tenantId, configKey = "consolidatedbill", url = "egov-searcher/bill-genie/billswithaddranduser/_get",businesService) => {
	  const searchCriteria = {
		consumerCode,
		tenantId,
		businesService
	  }
	  const FETCHBILL = {
		GET: {
		  URL: url,
		  ACTION: "_get",
		}
	  }

	  const DOWNLOADRECEIPT = {
		GET: {
		  URL: "/pdf-service/v1/_create",
		  ACTION: "_get",
		},
	  };

	  const queryObject = [
        { key: "connectionNumber", value: consumerCode},
        { key: "tenantId", value: tenantId }
    ]
    const responseSewerage = await httpRequest(
        "post",
        "/sw-services/swc/_search",
        "_search",
        queryObject
      );

      const responseWater = await httpRequest(
        "post",
        "/ws-services/wc/_search",
        "_search",
        queryObject
      );
      let oldConnection=null,ledgerId=null,propertyId=null;
	  let rate=await getMdmsData(businesService);
    if(businesService=="SW")
    { 
		configKey="sw-bill";
        oldConnection=responseSewerage.SewerageConnections[0].oldConnectionNo;
        ledgerId=responseSewerage.SewerageConnections[0].additionalDetails.ledgerId;
		propertyId=responseSewerage.SewerageConnections[0].propertyId

    }
    else if(businesService=="WS")
    {
		configKey="ws-bill";
        oldConnection=responseWater.WaterConnection[0].oldConnectionNo;
        ledgerId=responseWater.WaterConnection[0].additionalDetails.ledgerId;
		propertyId=responseWater.WaterConnection[0].propertyId;
    }
	  try {
		 // configKey="ws-bill";

		const billResponse = await httpRequest("post", FETCHBILL.GET.URL, FETCHBILL.GET.ACTION, [], { searchCriteria });
		const oldFileStoreId = get(billResponse.Bills[0], "fileStoreId")
		if (oldFileStoreId) {
		  downloadReceiptFromFilestoreID(oldFileStoreId, 'download')
		}
		else {
			var addDetail=null;
              
			addDetail = {
				"penaltyRate":rate
				}
				billResponse.Bills[0].additionalDetails=addDetail;
		  const queryStr = [
			{ key: "key", value: configKey },
			{ key: "tenantId", value: commonConfig.tenantId }
		  ]
		  const pfResponse = await httpRequest("post", DOWNLOADRECEIPT.GET.URL, DOWNLOADRECEIPT.GET.ACTION, queryStr, { Bill: billResponse.Bills }, { 'Accept': 'application/pdf' }, { responseType: 'arraybuffer' })
		  downloadReceiptFromFilestoreID(pfResponse.filestoreIds[0], 'download');
		}
	  } catch (error) {
		console.log(error);
	  }

	}

	export const downloadMultipleBill = async (bills = [], configKey,businesService) => {
		let rate=await getMdmsData(businesService);
	  
	  try {
		const DOWNLOADRECEIPT = {
		  GET: {
			URL: "/pdf-service/v1/_create",
			ACTION: "_get",
		  },
		};
		const queryStr = [
		  { key: "key", value: configKey },
		  { key: "tenantId", value: commonConfig.tenantId }
		]
		var addDetail=null;
              
		addDetail = {		
			"penaltyRate":rate
			}
			bills=bills.filter(item=> item.totalAmount>0);
bills.map(item =>{

	item.additionalDetails=addDetail;
})
		const pfResponse = await httpRequest("post", DOWNLOADRECEIPT.GET.URL, DOWNLOADRECEIPT.GET.ACTION, queryStr, { Bill: bills }, { 'Accept': 'application/pdf' }, { responseType: 'arraybuffer' })
		downloadReceiptFromFilestoreID(pfResponse.filestoreIds[0], 'download');
	  } catch (error) {
		console.log(error);

	  }

	}


	export const downloadMultipleFileFromFilestoreIds = (fileStoreIds = [], mode, tenantId) => {
	  getFileUrlFromAPI(fileStoreIds.join(','), tenantId).then(async (fileRes) => {
		fileStoreIds.map(fileStoreId => {
		  if (mode === 'download') {
			downloadPdf(fileRes[fileStoreId]);
		  } else if (mode === 'open') {
			openPdf(fileRes[fileStoreId], '_self')
		  }
		  else {
			printPdf(fileRes[fileStoreId]);
		  }
		})
	  });
	}
	export const downloadChallan = async (queryStr, mode = 'download') => {

		const DOWNLOADRECEIPT = {
		  GET: {
			URL: "/egov-pdf/download/UC/mcollect-challan",
			ACTION: "_get",
		  },
		};
		try {
		  httpRequest("post", DOWNLOADRECEIPT.GET.URL, DOWNLOADRECEIPT.GET.ACTION, queryStr, { 'Accept': 'application/json' }, { responseType: 'arraybuffer' })
			.then(res => {
			  res.filestoreIds[0]
			  if (res && res.filestoreIds && res.filestoreIds.length > 0) {
				res.filestoreIds.map(fileStoreId => {
				  downloadReceiptFromFilestoreID(fileStoreId, mode)
				})
			  } else {
				console.log("Error In Acknowledgement form Download");
			  }
			});
		} catch (exception) {
		  alert('Some Error Occured while downloading Acknowledgement form!');
		}
	  
	  }
