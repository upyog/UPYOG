import {
  getBreak,
  getCommonContainer,
  getCommonGrayCard,
  getCommonSubHeader,
  getLabel,
  getLabelWithValue
} from "egov-ui-framework/ui-config/screens/specs/utils";
import { gotoApplyWithStep, checkValueForNA } from "../../utils/index";
import {
  getQueryArg,
  getTransformedLocale
} from "egov-ui-framework/ui-utils/commons";
import get from "lodash/get";
import store from "ui-redux/store";


let state = store.getState();

const test = value => {
  value = value ? value.split(".")[0] : "NA";
  return value;
};

const tenantId = getQueryArg(window.location.href, "tenantId");

const getHeader = label => {
  return {
    uiFramework: "custom-molecules-local",
    moduleName: "egov-firenoc",
    componentPath: "DividerWithLabel",
    props: {
      className: "hr-generic-divider-label",
      labelProps: {},
      dividerProps: {},
      label
    },
    type: "array"
  };
};
export const propertySummaryDetails={
  propertyType: getLabelWithValue(
    {
      labelName: "Property Type",
      labelKey: "NOC_PROPERTY_TYPE_LABEL"
    },
    {
      jsonPath: "FireNOCs[0].fireNOCDetails.noOfBuildings",
      callBack: checkValueForNA
    }
  ),
  buildingName: getLabelWithValue(
    {
      labelName: "Name Of Building",
      labelKey: "NOC_NAME_OF_BUILDING_LABEL"
    },
    {
      jsonPath: "FireNOCs[0].fireNOCDetails.buildings[0].name",
      callBack: checkValueForNA
    }
  ),
  buildingUsageType: getLabelWithValue(
    {
      labelName: "Building Usage Type",
      labelKey: "NOC_PROPERTY_DETAILS_BUILDING_USAGE_TYPE_LABEL"
    },
    {
      jsonPath: "FireNOCs[0].fireNOCDetails.buildings[0].usageType",
      callBack: test,
      localePrefix: {
        moduleName: "firenoc",
        masterName: "BuildingType"
      }
    }
  ),
  buildingUsageSubType: getLabelWithValue(
    {
      labelName: "Building Usage Subtype",
      labelKey: "NOC_PROPERTY_DETAILS_BUILDING_USAGE_SUBTYPE_LABEL"
    },
    {
      jsonPath: "FireNOCs[0].fireNOCDetails.buildings[0].usageSubType",
      callBack: checkValueForNA,
      localePrefix: {
        moduleName: "firenoc",
        masterName: "BuildingType"
      }
    }
  ),
  buildingLandArea: getLabelWithValue(
    {
      labelName: "Land Area(in Sq meters)",
      // labelKey: "NOC_PROPERTY_DETAILS_LAND_AREA_LABEL"
    },
    {
      jsonPath: "FireNOCs[0].fireNOCDetails.buildings[0].landArea",
      // localePrefix: {
      //   moduleName: "firenoc",
      //   masterName: "BuildingLandArea"
      // },
    }
  ),
  buildingCoveredArea: getLabelWithValue(
    {
      labelName: "Total Covered Area(in Sq meters)",
      // labelKey: "NOC_PROPERTY_DETAILS_COVERED_AREA_LABEL"
    },
    {
      jsonPath: "FireNOCs[0].fireNOCDetails.buildings[0].totalCoveredArea",
      // localePrefix: {
      //   moduleName: "firenoc",
      //   masterName: "BuildingCoveredArea"
      // },
    }
  ),
  buildingParkingArea: getLabelWithValue(
    {
      labelName: "Parking Area",
      labelKey: "NOC_PROPERTY_DETAILS_PARKING_AREA_LABEL"
    },
    {
      jsonPath: "FireNOCs[0].fireNOCDetails.buildings[0].parkingArea",
      callBack: checkValueForNA
      // localePrefix: {
      //   moduleName: "firenoc",
      //   masterName: "BuildingParkingArea"
      // },
    }
  ),
  buildingleftSurrounding: getLabelWithValue(
    {
      labelName: "Left surrounding",
      // labelKey: "NOC_PROPERTY_DETAILS_LEFT_SURROUNDING_LABEL"
    },
    {
      jsonPath: "FireNOCs[0].fireNOCDetails.buildings[0].leftSurrounding",
      callBack: checkValueForNA
    }
  ),
  buildingrightSurrounding: getLabelWithValue(
    {
      labelName: "Right surrounding",
      // labelKey: "NOC_PROPERTY_DETAILS_RIGHT_SURROUNDING_LABEL"
    },
    {
      jsonPath: "FireNOCs[0].fireNOCDetails.buildings[0].rightSurrounding",
      callBack: checkValueForNA
    }
  ),
  buildingfrontSurrounding: getLabelWithValue(
    {
      labelName: "Front surrounding",
      // labelKey: "NOC_PROPERTY_DETAILS_FRONT_SURROUNDING_LABEL"
    },
    {
      jsonPath: "FireNOCs[0].fireNOCDetails.buildings[0].frontSurrounding",
      callBack: checkValueForNA
    }
  ),
  buildingbackSurrounding: getLabelWithValue(
    {
      labelName: "Back surrounding",
      // labelKey: "NOC_PROPERTY_DETAILS_BACK_SURROUNDING_LABEL"
    },
    {
      jsonPath: "FireNOCs[0].fireNOCDetails.buildings[0].backSurrounding",
      callBack: checkValueForNA
    }
  ),
}
const propertyDetails = {
  uiFramework: "custom-containers",
  componentPath: "MultiItem",
  props: {
    className: "noc-summary",
    scheama: getCommonGrayCard({
      propertyContainer: getCommonContainer(propertySummaryDetails)
    }),
    items: [],
    hasAddItem: false,
    isReviewPage: true,
    sourceJsonPath: "FireNOCs[0].fireNOCDetails.buildings",
    prefixSourceJsonPath:
      "children.cardContent.children.propertyContainer.children",
    afterPrefixJsonPath: "children.value.children.key"
  },
  type: "array"
};
export const propertyLocationSummaryDetail={
  propertyId: getLabelWithValue(
    {
      labelName: "Property ID",
      labelKey: "NOC_PROPERTY_ID_LABEL"
    },
    { jsonPath: "FireNOCs[0].fireNOCDetails.propertyDetails.propertyId", callBack: checkValueForNA }
  ),
  areaType: getLabelWithValue(
    {
      labelName: "Area Type",
      // labelKey: "NOC_AREA_TYPE_LABEL"
    },
    { 
    jsonPath: "FireNOCs[0].fireNOCDetails.propertyDetails.address.areaType" ,
    }
  ),
  district: getLabelWithValue(
    {
      labelName: "District Name",
      // labelKey: "NOC_PROPERTY_CITY_LABEL"
    },
    {
      jsonPath: "FireNOCs[0].fireNOCDetails.propertyDetails.address.city",
      localePrefix: {
        moduleName: "TENANT",
        masterName: "TENANTS"
      }
    }
  ),
  subDistrict: getLabelWithValue(
    {
      labelName: "Sub District Name",
      // labelKey: "NOC_SUB_DISTRICT_LABEL"
    },
    { jsonPath: "FireNOCs[0].fireNOCDetails.propertyDetails.address.subDistrict",
    callBack: value => {
      return `${value}`;
    }
    }
  ), 
  city: getLabelWithValue(
    {
      labelName: "City",
      labelKey: "NOC_PROPERTY_CITY_LABEL"
    },
    {
      jsonPath: "FireNOCs[0].fireNOCDetails.propertyDetails.address.subDistrict",
      callBack: checkValueForNA,
      localePrefix: {
        moduleName: "TENANT",
        masterName: "TENANTS"
      }
    }
  ),
  doorHouseNo: getLabelWithValue(
    {
      labelName: "Door/House No.",
      labelKey: "NOC_SUMMARY_PROPERTY__LOCATION_DOOR_HOUSE_NO_LABEL"
    },
    { jsonPath: "FireNOCs[0].fireNOCDetails.propertyDetails.address.doorNo", callBack: checkValueForNA }
  ),
  // buildingCompanyName: getLabelWithValue(
  //   {
  //     labelName: "Building/Company Name",
  //     labelKey: "NOC_PROPERTY_DETAILS_BLDG_NAME_LABEL"
  //   },
  //   {
  //     jsonPath:
  //       "FireNOCs[0].fireNOCDetails.propertyDetails.address.buildingName", callBack: checkValueForNA
  //   }
  // ),
  streetName: getLabelWithValue(
    {
      labelName: "Street Name",
      labelKey: "NOC_PROPERTY_DETAILS_SRT_NAME_LABEL"
    },
    { jsonPath: "FireNOCs[0].fireNOCDetails.propertyDetails.address.street", callBack: checkValueForNA }
  ),
  villageName: getLabelWithValue(
    {
      labelName: "Village Name",
      labelKey: "NOC_PROPERTY_DETAILS_VILL_NAME_LABEL"
    },
    { 
      jsonPath: "FireNOCs[0].fireNOCDetails.propertyDetails.address.addressLine2",
      callBack: checkValueForNA
    }
  ),
  landMarkName: getLabelWithValue( 
    {
      labelName: "Landmark Name",
      labelKey: "NOC_PROPERTY_DETAILS_LANDMARK_NAME_LABEL"
    },
    { 
      jsonPath: "FireNOCs[0].fireNOCDetails.propertyDetails.address.landmark",
      callBack: checkValueForNA 
    },   
  ),
  mohalla: getLabelWithValue(
    {
      labelName: "Mohalla",
      labelKey: "NOC_PROPERTY_DETAILS_MOHALLA_LABEL"
    },
    {
      jsonPath:
        "FireNOCs[0].fireNOCDetails.propertyDetails.address.locality.code",
      callBack: value => {
        let mtenanatId = get(
          state.screenConfiguration.preparedFinalObject,
          "FireNOCs[0].fireNOCDetails.propertyDetails.address.subDistrict"
        ); 
        let res = value && value.replace("-", "_");
        return `${getTransformedLocale(mtenanatId)}_REVENUE_${res}`;
      }
    }
  ),
  pincode: getLabelWithValue(
    {
      labelName: "Pincode",
      labelKey: "NOC_PROPERTY_DETAILS_PIN_LABEL"
    },
    { jsonPath: "FireNOCs[0].fireNOCDetails.propertyDetails.address.pincode", callBack: checkValueForNA }
  ),
  locationOnMap: getLabelWithValue(
    {
      labelName: "Location On Map",
      labelKey: "NOC_PROPERTY_DETAILS_GIS_CORD_LABEL"
    },
    {
      jsonPath:
      "FireNOCs[0].fireNOCDetails.propertyDetails.address.latitude"+","+"FireNOCs[0].fireNOCDetails.propertyDetails.address.longitude",
      callBack: checkValueForNA
    }
  ),
  applicableFireStation: getLabelWithValue(
    {
      labelName: "Applicable Fire Station",
      labelKey: "NOC_PROPERTY_DETAILS_FIRESTATION_LABEL"
    },
    {
      jsonPath: "FireNOCs[0].fireNOCDetails.firestationId",
      callBack: checkValueForNA,
      localePrefix: {
        moduleName: "firenoc",
        masterName: "FireStations"
      }
    }
  )
}
const propertyLocationDetails = getCommonGrayCard({
  propertyLocationContainer: getCommonContainer(propertyLocationSummaryDetail)
});

export const propertySummary = getCommonGrayCard({
  header: {
    uiFramework: "custom-atoms",
    componentPath: "Container",
    props: {
      style: { marginBottom: "10px" }
    },
    children: {
      header: {
        gridDefination: {
          xs: 8
        },
        ...getCommonSubHeader({
          labelName: "Property Details",
          labelKey: "NOC_COMMON_PROPERTY_DETAILS"
        })
      },
      editSection: {
        componentPath: "Button",
        props: {
          color: "primary",
          style: {
            marginTop: "-10px",
            marginRight: "-18px"
          }
        },
        gridDefination: {
          xs: 4,
          align: "right"
        },
        children: {
          editIcon: {
            uiFramework: "custom-atoms",
            componentPath: "Icon",
            props: {
              iconName: "edit"
            }
          },
          buttonLabel: getLabel({
            labelKey: "NOC_SUMMARY_EDIT"
          })
        },
        onClickDefination: {
          action: "condition",
          callBack: (state, dispatch) => {
            gotoApplyWithStep(state, dispatch, 1);
          }
        }
      }
    }
  },
  propertyDetailsHeader: getHeader("Property Details"),
  break: getBreak(),
  cardOne: propertyDetails,
  propertyLocationDetailsHeader: getHeader("Property Location Details"),
  cardTwo: propertyLocationDetails
});
