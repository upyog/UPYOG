import {
  getCommonGrayCard,
  getCommonSubHeader,
  getCommonContainer,
  getLabelWithValue,
  getLabel
} from "egov-ui-framework/ui-config/screens/specs/utils";
import { getQueryArg } from "egov-ui-framework/ui-utils/commons";
import { serviceConst } from "../../../../../ui-utils/commons";
import { getUserInfo, getTenantIdCommon } from "egov-ui-kit/utils/localStorageUtils";

export const checkValueForNA = value => {
  return value ? value : "NA";
};
const tenantId = getTenantIdCommon()
const service = getQueryArg(window.location.href, "service")
const connectionType = getQueryArg(window.location.href, "connectionType")
export const waterDetails = () => {
  if (connectionType === "Metered") {
    return getCommonContainer({
      serviceType: getLabelWithValue({ labelKey: "WS_SERV_DETAIL_SERV_LABEL" }, { jsonPath: "WaterConnection[0].service" }),
      connectionCategory: getLabelWithValue({ labelKey: "WS_SERV_DETAIL_CONN_CATEGORY" }, { jsonPath: "WaterConnection[0].additionalDetails.connectionCategory" }),
      connectionType: getLabelWithValue({ labelKey: "WS_SERV_DETAIL_CONN_TYPE" }, { jsonPath: "WaterConnection[0].connectionType" }),
      meterID: getLabelWithValue({ labelKey: "WS_SERV_DETAIL_METER_ID" }, { jsonPath: "WaterConnection[0].meterId" }),
      pipeSize: getLabelWithValue({ labelKey: "WS_SERV_DETAIL_PIPE_SIZE" }, { jsonPath: "WaterConnection[0].pipeSize" }),
      connectionExecutionDate: getLabelWithValue({ labelKey: "WS_SERV_DETAIL_CONN_EXECUTION_DATE" }, { jsonPath: "WaterConnection[0].connectionExecutionDate" }),
      waterSource: getLabelWithValue({ labelKey: "WS_SERV_DETAIL_WATER_SOURCE" }, { jsonPath: "WaterConnection[0].waterSource" }),
      oldConsumerNo: getLabelWithValue({ labelKey: "WS_OLD_CONSUMER_NO" }, { jsonPath: "WaterConnection[0].oldConnectionNo",callBack: checkValueForNA }),
    })
  } else {
    return getCommonContainer({
      serviceType: getLabelWithValue({ labelKey: "WS_SERV_DETAIL_SERV_LABEL" }, { jsonPath: "WaterConnection[0].service" }),
      connectionCategory: getLabelWithValue({ labelKey: "WS_SERV_DETAIL_CONN_CATEGORY" }, { jsonPath: "WaterConnection[0].additionalDetails.connectionCategory" }),
      connectionType: getLabelWithValue({ labelKey: "WS_SERV_DETAIL_CONN_TYPE" }, { jsonPath: "WaterConnection[0].connectionType" }),
      pipeSize: getLabelWithValue({ labelKey: "WS_SERV_DETAIL_PIPE_SIZE" }, { jsonPath: "WaterConnection[0].pipeSize" }),
      connectionExecutionDate: getLabelWithValue({ labelKey: "WS_SERV_DETAIL_CONN_EXECUTION_DATE" }, { jsonPath: "WaterConnection[0].connectionExecutionDate" }),
      waterSource: getLabelWithValue({ labelKey: "WS_SERV_DETAIL_WATER_SOURCE" }, { jsonPath: "WaterConnection[0].waterSource" }),
      // waterSubSource: getLabelWithValue({ labelKey: "WS_SERV_DETAIL_WATER_SUB_SOURCE" }, { jsonPath: "WaterConnection[0].waterSubSource" }),
      numberOfTaps: getLabelWithValue({ labelKey: "WS_SERV_DETAIL_NO_OF_TAPS" }, { jsonPath: "WaterConnection[0].noOfTaps" }),
      oldConsumerNo: getLabelWithValue({ labelKey: "WS_OLD_CONSUMER_NO" }, { jsonPath: "WaterConnection[0].oldConnectionNo",callBack: checkValueForNA })
    })
  }
}
export const sewerDetails = () => {
  return getCommonContainer({
    serviceType: getLabelWithValue({ labelKey: "WS_SERV_DETAIL_SERV_LABEL" }, { jsonPath: "WaterConnection[0].service",callBack: checkValueForNA }),
    connectionExecutionDate: getLabelWithValue({ labelKey: "WS_SERV_DETAIL_CONN_EXECUTION_DATE" }, { jsonPath: "WaterConnection[0].connectionExecutionDate",callBack: checkValueForNA }),
    unitOfMeasurement: getLabelWithValue({ labelKey: "WS_SERV_DETAIL_UNIT_OF_MEASUREMENT" }, { jsonPath: "WaterConnection[0].uom",callBack: checkValueForNA }),
    numberOfToilets: getLabelWithValue({ labelKey: "WS_SERV_DETAIL_NO_OF_TOILETS" }, { jsonPath: "WaterConnection[0].noOfToilets",callBack: checkValueForNA }),
    oldConsumerNo: getLabelWithValue({ labelKey: "WS_OLD_CONSUMER_NO" }, { jsonPath: "WaterConnection[0].oldConnectionNo",callBack: checkValueForNA })

  })
}

export const getServiceDetails = () => {
  return getCommonGrayCard({
    headerDiv: {
      uiFramework: "custom-atoms",
      componentPath: "Container",
      props: {
        style: { marginBottom: "10px" }
      },
      children: {
        header: {
          gridDefination: {
            xs: 12,
            sm: 10
          },
          ...getCommonSubHeader({
            labelKey: "WS_COMMON_SERV_DETAIL"
          })
        }
      }
    },
    waterDetails:  waterDetails(),
    sewerDetails: sewerDetails(),
  });
};


