import {
  handleScreenConfigurationFieldChange as handleField,
  toggleSnackbar,
  prepareFinalObject
} from "egov-ui-framework/ui-redux/screen-configuration/actions";
import get from "lodash/get";
import {
  getPayload,
  getTenantName
} from "./publicSearchUtils";
import { validateFields } from "../../utils/index";
import { getSearchResults } from "../../../../../ui-utils/commons";
import { ComponentJsonPath, fetchBill, getPropertyWithBillAmount ,fetchPayments} from "./publicSearchUtils";
import {
  convertEpochToDate
} from "egov-ui-framework/ui-config/screens/specs/utils";

export const propertySearch = async (state, dispatch) => {
  searchApiCall(state, dispatch);
};

const removeValidation = (state, dispatch) => {
  Object.keys(ComponentJsonPath).map(key => {
    dispatch(
      handleField("public-search", ComponentJsonPath[key], "props.error", false)
    );

    dispatch(
      handleField("public-search", ComponentJsonPath[key], "isFieldValid", true)
    );

    dispatch(
      handleField("public-search", ComponentJsonPath[key], "props.helperText", "")
    );
    return true;
  });
};

const getAddress = item => {
  if (item && item.address) {
    let doorNo = item.address.doorNo != null ? item.address.doorNo + "," : "";
    let buildingName =
      item.address.buildingName != null ? item.address.buildingName + "," : "";
    let street = item.address.street != null ? item.address.street + "," : "";
    let mohalla = item.address.locality.name
      ? item.address.locality.name + ","
      : "";
    let city = item.address.city != null ? item.address.city : "";
    return doorNo + buildingName + street + mohalla + city;
  }
};

const searchApiCall = async (state, dispatch) => {
  showHideTable(false, dispatch);

  let searchScreenObject = get(
    state.screenConfiguration.preparedFinalObject,
    "searchScreen",
    {}
  );
  
  // if (
  //   searchScreenObject.tenantId === "" ||
  //   searchScreenObject.locality === ""
  // ) {
  //   dispatch(
  //     toggleSnackbar(
  //       true,
  //       {
  //         labelName: "Please fill valid fields to search",
  //         labelKey: "ERR_FIRENOC_FILL_VALID_FIELDS"
  //       },
  //       "error"
  //     )
  //   );
  //   return;
  // }

  const isSearchBoxFirstRowValid = validateFields(
    "components.div.children.searchPropertyDetails.children.cardContent.children.searchPropertyContainer.children",
    state,
    dispatch,
    "public-search"
  );

  if (!isSearchBoxFirstRowValid) {
    dispatch(
      toggleSnackbar(
        true,
        {
          labelName: "Please fill valid fields to search",
          labelKey: "ERR_PT_FILL_VALID_FIELDS"
        },
        "error"
      )
    );
    return;
  }
  
  if (searchScreenObject.tenantId && searchScreenObject.locality && !(searchScreenObject.ids || searchScreenObject.mobileNumber || searchScreenObject.ownerName)) {
    dispatch(
      toggleSnackbar(
        true,
        {
          labelName: "Please fill at least one field along with city and locality",
          labelKey:
            "PT_SEARCH_SELECT_AT_LEAST_ONE_FIELD_WITH_CITY_AND_LOCALITY"
        },
        "error"
      )
    );
    return;
  } else {
    removeValidation(state, dispatch);
    const isAdvancePaymentAllowed = get(state, "screenConfiguration.preparedFinalObject.businessServiceInfo.isAdvanceAllowed");
    const querryObject = getPayload(searchScreenObject);
    try {
      let response = await getSearchResults(querryObject);
      response.Properties=response.Properties.filter(item=> item.status=="ACTIVE");
      const billResponse = await fetchBill(dispatch, response, searchScreenObject.tenantId, "PT");
      const finalResponse = getPropertyWithBillAmount(response, billResponse);
      const FETCHRECEIPT = {
        GET: {
          URL: "/collection-services/payments/_search",
          ACTION: "_get",
        },
      };
      let propertyData = [];
      let assessmentYear = '';
      finalResponse.Properties.map( async (item, index) => {
        let receiptQueryString = [
          { key: "consumerCodes", value: item.propertyId },
          { key: "tenantId", value: item.tenantId },
        ];
        const paymentResponse = await fetchPayments(dispatch,receiptQueryString, FETCHRECEIPT);  
        if(paymentResponse && paymentResponse.Payments && paymentResponse.Payments.length > 0) {
          paymentResponse.Payments.map(pay => {
            pay.paymentDetails[0].bill.billDetails.map(b => {
              let toDate = convertEpochToDate(b.toPeriod).split("/")[2];
              let fromDate = convertEpochToDate(b.fromPeriod).split("/")[2];
              assessmentYear = assessmentYear == "" ? fromDate + "-" + toDate + "(Rs." + b.amountPaid + ")" : assessmentYear + "," + fromDate + "-" + toDate + "(Rs." + b.amountPaid + ")";
            });
          });
          item.assessmentYear = assessmentYear;
        }
        propertyData.push({
          ["PT_MUTATION_PID"]: item.propertyId || "-",
          ["PT_COMMON_TABLE_COL_OWNER_NAME"]: item.owners[0].name || "-",
          ["PT_COMMON_COL_ADDRESS"]: getAddress(item) || "-",
          ["PT_COMMON_TABLE_PROPERTY_STATUS"]: item.status || "-",
          ["PT_COMMON_PAID_YEARS"]: item.assessmentYear || "NA",
          ["PT_AMOUNT_DUE"]: (item.totalAmount || item.totalAmount===0) ? item.totalAmount : "-",
          ["PT_COMMON_TABLE_COL_ACTION_LABEL"]: { status: item.status, totalAmount: item.totalAmount, isAdvancePaymentAllowed },
          ["TENANT_ID"]: item.tenantId || "-",
          ["ADVANCE_PAYMENT"]: isAdvancePaymentAllowed
        })
        
        dispatch(
          handleField(
            "public-search",
            "components.div.children.searchPropertyTable",
            "props.data",
            propertyData
          )
        );
      })
      dispatch(
        handleField(
          "public-search",
          "components.div.children.searchPropertyTable",
          "props.rows",
          response.Properties.length
        )
      );

      //showHideProgress(false, dispatch);
      showHideTable(true, dispatch);
    } catch (error) {
      //showHideProgress(false, dispatch);
      dispatch(
        toggleSnackbar(
          true,
          { labelName: error.message, labelKey: error.message },
          "error"
        )
      );
      console.log(error);
    }
  }
};
const showHideTable = (booleanHideOrShow, dispatch) => {
  dispatch(
    handleField(
      "public-search",
      "components.div.children.searchPropertyTable",
      "visible",
      booleanHideOrShow
    )
  );
};

export const resetFields = (state, dispatch) => {
  Object.keys(ComponentJsonPath).map(key => {
    dispatch(
      handleField("public-search", ComponentJsonPath[key], "props.value", "")
    );
    return true;
  });
  dispatch(
    handleField("public-search", ComponentJsonPath["locality"], "props.data", [])
  );
  dispatch(
    prepareFinalObject("applyScreenMdmsData.tenant.localities", [])
  );
  dispatch(prepareFinalObject("ptSearchScreen.tenantId", ""));
  dispatch(prepareFinalObject("ptSearchScreen.locality.code", ""));
  dispatch(prepareFinalObject("ptSearchScreen.ids", ""));
  dispatch(prepareFinalObject("ptSearchScreen.mobileNumber", ""));
  dispatch(prepareFinalObject("ptSearchScreen.ownerName", ""));
  dispatch(prepareFinalObject("searchScreen.tenantId", ""));
  dispatch(prepareFinalObject("searchScreen.locality.code", ""));
  dispatch(prepareFinalObject("searchScreen.ids", ""));
  dispatch(prepareFinalObject("searchScreen.mobileNumber", ""));
  dispatch(prepareFinalObject("searchScreen.ownerName", ""));
  removeValidation(state, dispatch);
};
