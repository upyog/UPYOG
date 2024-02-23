import { getCommonContainer, getCommonHeader } from "egov-ui-framework/ui-config/screens/specs/utils";
import { prepareFinalObject, toggleSnackbar } from "egov-ui-framework/ui-redux/screen-configuration/actions";
import { getQueryArg } from "egov-ui-framework/ui-utils/commons";
import get from "lodash/get";
import { viewReceiptDetailsCard } from "./cancelReceiptResource/cancelReceiptDetails";
import { viewReceiptFooter } from "./cancelReceiptResource/cancelReceiptFooter";
import { httpRequest } from "egov-ui-framework/ui-utils/api";

const header = getCommonContainer({
  header: getCommonHeader({
    labelName: `Cancel Receipt`, //later use getFinancialYearDates
    labelKey: "CR_COMMON_HEADER"
  }),
  applicationNumber: {
    uiFramework: "custom-atoms-local",
    moduleName: "egov-abg",
    componentPath: "ApplicationContainer",
    props: {
      number: getQueryArg(window.location.href, "receiptNumbers"),
      label: {
        labelValue: "Receipt Details Receipt No.",
        labelKey: "CR_RECEIPT_DETAILS_NUMBER"
      }
    },
    visible: true
  }
});
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
const getPaymentSearchResults = async (queryObject, dispatch) => {
  try {
    let businessService = '';
    queryObject && Array.isArray(queryObject) && queryObject.map(query => {
      if (query.key == "businessService") {
        businessService=query.value;
      }
    })

      queryObject = queryObject && Array.isArray(queryObject) && queryObject.filter(query => query.key != "businessService")

    const response=await httpRequest(
      "post",
      getPaymentSearchAPI(businessService),
      "",
      queryObject
    );

    return response;
  } catch (error) {
    // enableFieldAndHideSpinner('search',"components.div.children.UCSearchCard.children.cardContent.children.buttonContainer.children.searchButton",dispatch);
    console.error(error);
    dispatch(
      toggleSnackbar(
        true,
        { labelName: error.message, labelCode: error.message },
        "error"
      )
    );
  }
};

const setSearchResponse = async (
  state,
  dispatch,
  consumerNumber,
  businessService,
  tenantId
) => {
  try {
    const response = await getPaymentSearchResults([
      {
        key: "tenantId",
        value: tenantId
      }, {
        key: "businessService",
        value: businessService
      },
      { key: "receiptNumbers", value: consumerNumber }
    ], dispatch);

    dispatch(prepareFinalObject("PaymentReceipt", get(response, 'Payments[0]', {})));
  }
  catch (error) {
    // enableFieldAndHideSpinner('search',"components.div.children.UCSearchCard.children.cardContent.children.buttonContainer.children.searchButton",dispatch);
    console.error(error);
    dispatch(
      toggleSnackbar(
        true,
        { labelName: error.message, labelCode: error.message },
        "error"
      )
    );
  }

};
const viewReceipt = {
  uiFramework: "material-ui",
  name: "viewReceipt",
  beforeInitScreen: (action, state, dispatch) => {

    const applicationNumber = getQueryArg(
      window.location.href,
      "receiptNumbers"
    );
    const businessService = getQueryArg(
      window.location.href,
      "businessService"
    );
    const tenantId = getQueryArg(
      window.location.href,
      "tenantId"
    );
    setSearchResponse(state, dispatch, applicationNumber, businessService, tenantId);
    if (getQueryArg(
      window.location.href,
      "edit"
    )) {

    } else {
      dispatch(prepareFinalObject('paymentWorkflows', [{}]));
    }

    return action;
  },

  components: {
    div: {
      uiFramework: "custom-atoms",
      componentPath: "Form",
      props: {
        className: "common-div-css",
        id: "viewReceipt"
      },
      children: {
        headerDiv: {
          uiFramework: "custom-atoms",
          componentPath: "Container",

          children: {
            header: {
              gridDefination: {
                xs: 12,
                sm: 6
              },
              ...header
            }
          }
        },
        viewReceiptDetailsCard,
        viewReceiptFooter
      }
    }
  }
};

export default viewReceipt;
