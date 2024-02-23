import {
  getCommonCard,
  getCommonContainer,
  getCommonTitle,
  getPattern,
  getSelectField,
  getTextField
} from "egov-ui-framework/ui-config/screens/specs/utils";
import {
  handleScreenConfigurationFieldChange as handleField,
  prepareFinalObject,
  toggleSnackbar
} from "egov-ui-framework/ui-redux/screen-configuration/actions";
import { getTenantId } from "egov-ui-kit/utils/localStorageUtils";
import get from "lodash/get";
import set from "lodash/set";
import { httpRequest } from "../../../../../ui-utils/api";
import { getLocale } from "egov-ui-kit/utils/localStorageUtils";
import { fetchLocalizationLabel } from "egov-ui-kit/redux/app/actions";
import "./index.css";

const showHideMapPopup = (state, dispatch) => {
  let toggle = get(
    state.screenConfiguration.screenConfig["apply"],
    "components.div.children.formwizardSecondStep.children.propertyLocationDetails.children.cardContent.children.mapsDialog.props.open",
    false
  );
  dispatch(
    handleField(
      "apply",
      "components.div.children.formwizardSecondStep.children.propertyLocationDetails.children.cardContent.children.mapsDialog",
      "props.open",
      !toggle
    )
  );
};

const getMapLocator = textSchema => {
  return {
    uiFramework: "custom-molecules-local",
    moduleName: "egov-firenoc",
    componentPath: "MapLocator",
    props: {}
  };
};

const getDetailsFromProperty = async (state, dispatch) => {
  try {
    const propertyId = get(
      state.screenConfiguration.preparedFinalObject,
      "FireNOCs[0].fireNOCDetails.propertyDetails.propertyId",
      ""
    );

    const tenantId = getTenantId();
    if (!tenantId) {
      dispatch(
        toggleSnackbar(
          true,
          {
            labelName: "Please select city to search by property id !!",
            labelKey: "ERR_SELECT_CITY_TO_SEARCH_PROPERTY_ID"
          },
          "warning"
        )
      );
      return;
    }
    if (propertyId) {
      let payload = await httpRequest(
        "post",
        `/property-services/property/_search?tenantId=${tenantId}&propertyIds=${propertyId}`,
        "_search",
        [],
        {}
      );
      if (
        payload &&
        payload.Properties &&
        payload.Properties.hasOwnProperty("length")
      ) {
        if (payload.Properties.length === 0) {
          dispatch(
            toggleSnackbar(
              true,
              {
                labelName: "Property is not found with this Property Id",
                labelKey: "ERR_PROPERTY_NOT_FOUND_WITH_PROPERTY_ID"
              },
              "info"
            )
          );
          dispatch(
            handleField(
              "apply",
              "components.div.children.formwizardSecondStep.children.tradeLocationDetails.children.cardContent.children.tradeDetailsConatiner.children.tradeLocPropertyID",
              "props.value",
              ""
            )
          );
        } else {
          dispatch(
            handleField(
              "apply",
              "components.div.children.formwizardSecondStep.children.propertyLocationDetails.children.cardContent.children.propertyDetailsConatiner.children.propertyMohalla",
              "props.value",
              {
                value: payload.Properties[0].address.locality.code,
                label: payload.Properties[0].address.locality.name
              }
            )
          );
          dispatch(
            prepareFinalObject(
              "FireNOCs[0].fireNOCDetails.propertyDetails.address",
              payload.Properties[0].address
            )
          );
          // dispatch(
          //   handleField(
          //     "apply",
          //     "components.div.children.formwizardSecondStep.children.tradeLocationDetails.children.cardContent.children.tradeDetailsConatiner.children.tradeLocCity.children.cityDropdown",
          //     "props.value",
          //     payload.Properties[0].address.tenantId
          //   )
          // );
        }
      }
    }
  } catch (e) {
    console.log(e);
  }
};

export const propertyLocationDetails = getCommonCard(
  {
    header: getCommonTitle(
      {
        labelName: "Property Location Details",
        labelKey: "NOC_PROPERTY_LOCATION_DETAILS_HEADER"
      },
      {
        style: {
          marginBottom: 18
        }
      }
    ),

    propertyDetailsConatiner: getCommonContainer({
      area: {
        ...getSelectField({
          label: {
            labelName: "Area Type",
            labelKey: "NOC_AREA_TYPE_LABEL"
          },
          placeholder: {
            labelName: "Select Area Type",
            labelKey: "NOC_AREA_TYPE_PLACEHOLDER"
          },
          data: [
            {
              code: "Urban",
              label: "NOC_AREA_TYPE_URBAN"
            },
            {
              code: "Rural",
              label: "NOC_AREA_TYPE_RURAL"
            }
          ],
          jsonPath: "FireNOCs[0].fireNOCDetails.propertyDetails.address.areaType",
          required: true
        }),

        beforeFieldChange: async (action, state, dispatch) => {
          dispatch(
            prepareFinalObject(
              "FireNOCs[0].fireNOCDetails.propertyDetails.address.areaType",
              action.value
            )
          );
          if (action.value === 'Rural') {


            dispatch(
              handleField(
                "apply",
                "components.div.children.formwizardSecondStep.children.propertyLocationDetails.children.cardContent.children.propertyDetailsConatiner.children.district",
                "visible",
                true
              )
            );
            dispatch(
              handleField(
                "apply",
                "components.div.children.formwizardSecondStep.children.propertyLocationDetails.children.cardContent.children.propertyDetailsConatiner.children.subDistrict",
                "visible",
                true
              )
            );
            dispatch(
              handleField(
                "apply",
                "components.div.children.formwizardSecondStep.children.propertyLocationDetails.children.cardContent.children.propertyDetailsConatiner.children.propertyLandmark",
                "visible",
                true
              )
            );
            dispatch(
              handleField(
                "apply",
                "components.div.children.formwizardSecondStep.children.propertyLocationDetails.children.cardContent.children.propertyDetailsConatiner.children.propertyVillageName",
                "visible",
                true
              )
            );
            dispatch(
              handleField(
                "apply",
                "components.div.children.formwizardSecondStep.children.propertyLocationDetails.children.cardContent.children.propertyDetailsConatiner.children.propertyFirestation",
                "visible",
                true
              )
            );
            dispatch(
              handleField(
                "apply",
                "components.div.children.formwizardSecondStep.children.propertyLocationDetails.children.cardContent.children.propertyDetailsConatiner.children.propertyCity",
                "visible",
                false
              )
            );

            dispatch(
              handleField(
                "apply",
                "components.div.children.formwizardSecondStep.children.propertyLocationDetails.children.cardContent.children.propertyDetailsConatiner.children.propertyMohalla",
                "visible",
                false
              )
            );


            const districtData = get(
              state.screenConfiguration,
              "preparedFinalObject.applyScreenMdmsData.tenant.tenants",
              []);
             
            // console.log("districtTenantMap", districtTenantMap);

            const fireStationsList = get(
              state,
              "screenConfiguration.preparedFinalObject.applyScreenMdmsData.firenoc.FireStations",
              []
            );

            const firestation=[];
            for (let i = 0; i < fireStationsList.length; i++) {
              firestation.push(fireStationsList[i].baseTenantId);
            }
            //console.log("fireStationsList",fireStationsList);

            let districtList = districtData.filter((districtlists) => firestation.includes(districtlists.code));
           
                     // console.log("districtList", districtList);

          const districtTenantMap = districtList.map((item) => {
            return {
              name: item.city.districtName,
              code: item.city.districtTenantCode

            }

          });

            const districtlistRural = [];

            for (let i = 0; i < districtTenantMap.length; i++) {
              districtlistRural.push({

                code: districtTenantMap[i].code,

              })

            }



            // console.log("districtlist", districtlistRural);

            const unqDistrictList = districtlistRural.filter(
              (ele, ind) => ind === districtlistRural.findIndex(elem => elem.code === ele.code)
            );

            // console.log("unique districtlist", unqDistrictList);


            dispatch(
              handleField(
                "apply",
                "components.div.children.formwizardSecondStep.children.propertyLocationDetails.children.cardContent.children.propertyDetailsConatiner.children.district",
                "props.data",
                unqDistrictList
              )
            );


          }
          else {


            const districtData = get(
              state.screenConfiguration,
              "preparedFinalObject.applyScreenMdmsData.tenant.tenants",
              []
            );
           

            //console.log("districtTenantMap",districtTenantMap);

            const fireStationsList = get(
              state,
              "screenConfiguration.preparedFinalObject.applyScreenMdmsData.firenoc.FireStations",
              []
            );

            const firestation=[];
            for (let i = 0; i < fireStationsList.length; i++) {
              firestation.push(fireStationsList[i].baseTenantId);
            }
            //console.log("fireStationsList",fireStationsList);

            let districtList = districtData.filter((districtlists) => firestation.includes(districtlists.code));
            const districtTenantMap = districtList.map((item) => {
              return {
                name: item.city.districtName,
                //code:item.code
                code: item.city.districtTenantCode
              }

            });
            const districtlist = [];



            for (let i = 0; i < districtTenantMap.length; i++) {
              districtlist.push({

                code: districtTenantMap[i].code,

              })

            }

            //console.log("districtlist",districtlist);

            const unqDistrictList = districtlist.filter(
              (ele, ind) => ind === districtlist.findIndex(elem => elem.code === ele.code)
            );

            // console.log("urban list", unqDistrictList);
            dispatch(
              handleField(
                "apply",
                "components.div.children.formwizardSecondStep.children.propertyLocationDetails.children.cardContent.children.propertyDetailsConatiner.children.district",
                "props.data",
                unqDistrictList
              )
            );


            dispatch(
              handleField(
                "apply",
                "components.div.children.formwizardSecondStep.children.propertyLocationDetails.children.cardContent.children.propertyDetailsConatiner.children.propertyVillageName",
                "visible",
                false
              )
            );
            dispatch(
              handleField(
                "apply",
                "components.div.children.formwizardSecondStep.children.propertyLocationDetails.children.cardContent.children.propertyDetailsConatiner.children.propertyCity",
                "visible",
                true
              )
            );
            dispatch(
              handleField(
                "apply",
                "components.div.children.formwizardSecondStep.children.propertyLocationDetails.children.cardContent.children.propertyDetailsConatiner.children.propertyMohalla",
                "visible",
                true
              )
            );
            dispatch(
              handleField(
                "apply",
                "components.div.children.formwizardSecondStep.children.propertyLocationDetails.children.cardContent.children.propertyDetailsConatiner.children.propertyFirestation",
                "visible",
                true
              )
            );
            dispatch(
              handleField(
                "apply",
                "components.div.children.formwizardSecondStep.children.propertyLocationDetails.children.cardContent.children.propertyDetailsConatiner.children.propertyStreetName",
                "visible",
                true
              )
            );
            dispatch(
              handleField(
                "apply",
                "components.div.children.formwizardSecondStep.children.propertyLocationDetails.children.cardContent.children.propertyDetailsConatiner.children.district",
                "visible",
                true
              )
            );
            dispatch(
              handleField(
                "apply",
                "components.div.children.formwizardSecondStep.children.propertyLocationDetails.children.cardContent.children.propertyDetailsConatiner.children.subDistrict",
                "visible",
                false
              )
            );
          }
        }

      },
      district: {
        ...getSelectField({
          localePrefix: {
            moduleName: "TENANT",
            masterName: "TENANTS"
          },
        props: {
          style: {
            // width: "100%",
            cursor: "pointer"
          },
        },
          label: {
            labelName: "District Name",
            // labelKey: "NOC_DISTRICT_LABEL"
          },
          placeholder: {
            labelName: "Select District",
            // labelKey: "NOC_DISTRICT_PLACEHOLDER"
          },
          // sourceJsonPath: "applyScreenMdmsData.tenant.tenants",

          jsonPath:
            "FireNOCs[0].fireNOCDetails.propertyDetails.address.city",
  
          labelsFromLocalisation: true,
          errorMessage: "ERR_DEFAULT_INPUT_FIELD_MSG",
          suggestions: [],
          fullwidth: true,
          required: true,
          inputLabelProps: {
            shrink: true
          }
        }),
        beforeFieldChange: async (action, state, dispatch) => {
          dispatch(
            prepareFinalObject(
              "FireNOCs[0].fireNOCDetails.propertyDetails.address.city",
              action.value
            )
          );
          if (action.value) {


            let fireStationsList = get(
              state,
              "screenConfiguration.preparedFinalObject.applyScreenMdmsData.firenoc.FireStations",
              []
            );

            // console.log("fireStationsList", fireStationsList);

            const districtData = get(
              state.screenConfiguration,
              "preparedFinalObject.applyScreenMdmsData.tenant.tenants",
              []
            );
            let districtlist = districtData.filter((districtlists) => {

              return districtlists.city.districtTenantCode === action.value

            });
            // console.log("districtData", districtData);

            // console.log("tenanats list", districtlist);

            let tenantids = districtlist.map((districtlists) => {
              return districtlists.code
            });

            let urbanids = districtlist.map((districtlists) => {
              return districtlists.code
            });
            // console.log("tenant ids", urbanids);



            let urbanlist = []

            for (let i = 0; i < urbanids.length; i++) {
              urbanlist.push({

                code: urbanids[i],

              })
            }

            // console.log("urbanlist", urbanlist);



            const subDistrictLists = [];

            const firestationtenantidlist = [];

            const fireStations = [];

            for (var i = 0; i < tenantids.length; i++) {
              const fireStations = fireStationsList.filter(firestation => {

                return tenantids.includes(firestation.baseTenantId);

              });

              if (fireStations[i]) {


                for (var j = 0; j < fireStations[i].subDistrict.length; j++) {
                  subDistrictLists.push(fireStations[i].subDistrict[j]);
                }
              }

            }

            //console.log('filtered fireStations', fireStations);

            let value = get(
              state.screenConfiguration.preparedFinalObject,
              "FireNOCs[0].fireNOCDetails.propertyDetails.address.areaType", []);

            if (value === 'Urban') {

              const ulblist = [];

              const firestationtenantidlist = [];

              const fireStations = [];

              for (var i = 0; i < tenantids.length; i++) {
                const fireStations = fireStationsList.filter(firestation => {

                  return tenantids.includes(firestation.baseTenantId);

                });

                if (fireStations[i]) {


                  for (var j = 0; j < fireStations[i].ulb.length; j++) {

                    ulblist.push(fireStations[i].ulb[j]);
                  }
                }

              }


              dispatch(
                handleField(
                  "apply",
                  "components.div.children.formwizardSecondStep.children.propertyLocationDetails.children.cardContent.children.propertyDetailsConatiner.children.propertyCity",
                  "props.data",
                  ulblist
                )
              );

            } else {


              // console.log("subdistrict list", subDistrictLists);

              dispatch(
                handleField(
                  "apply",
                  "components.div.children.formwizardSecondStep.children.propertyLocationDetails.children.cardContent.children.propertyDetailsConatiner.children.subDistrict",
                  "props.data",
                  subDistrictLists
                )
              );
            }


          }

        }

      },
      subDistrict: {
        ...getSelectField({
          label: {
            labelName: "Tehsil",
            // labelKey: "NOC_SUB_DISTRICT_LABEL"
          },
          placeholder: {
            labelName: "Select Sub District",
            // labelKey: "NOC_SUB_DISTRICT_PLACEHOLDER"
          },
          // sourceJsonPath: "applyScreenMdmsData.tenant.tenants",
          jsonPath: "FireNOCs[0].fireNOCDetails.propertyDetails.address.subDistrict",
          required: true,
          visible: false,
          props: {
            menuPortalTarget: document.querySelector('body'),
            setDataInField: true,
          }
        }),
        beforeFieldChange: async (action, state, dispatch) => {


          let fireStationsList = get(
            state,
            "screenConfiguration.preparedFinalObject.applyScreenMdmsData.firenoc.FireStations",
            []
          );
          // console.log("fireStationsList", fireStationsList);
          let fireStations = fireStationsList.filter(firestation => {
            return firestation.baseTenantId === action.value;
          });

          let fireStationsulb = fireStationsList.filter(firestation => {
            return firestation.ulb
          });

          let props_value;

          let fire_stationid;

          for (var i = 0; i < fireStationsulb.length; i++) {
            for (var j = 0; j < fireStationsulb[i].ulb.length; j++) {
              if (fireStationsulb[i].ulb[j].code === action.value) {
                props_value = fireStationsulb[i].baseTenantId;
                fire_stationid = fireStationsulb[i].code
              }
            }
          }

          dispatch(
            prepareFinalObject(
              "FireNOCs[0].tenantId", props_value)
          );

          dispatch(
            prepareFinalObject(
              "FireNOCs[0].fireNOCDetails.firestationId", fire_stationid)
          );


        },
        afterFieldChange: (action, state, dispatch) => {

          let fireStationsList = get(
            state,
            "screenConfiguration.preparedFinalObject.applyScreenMdmsData.firenoc.FireStations",
            []
          );

          let fireStations = fireStationsList.filter(firestation => {
            return firestation.subDistrict
          });

          let props_value;
          let fire_stationid;
          for (var i = 0; i < fireStations.length; i++) {
            for (var j = 0; j < fireStations[i].subDistrict.length; j++) {
              if (fireStations[i].subDistrict[j].code == action.value) {
                props_value = fireStations[i].baseTenantId;
                fire_stationid = fireStations[i].code

              }
            }
          }

          // console.log("props value", props_value);
          dispatch(
            prepareFinalObject(
              "FireNOCs[0].fireNOCDetails.firestationId", fire_stationid)
          );

          set(
            state,
            "screenConfiguration.preparedFinalObject.FireNOCs[0].tenantId",
            props_value
          );
        }

      },
      propertyId: getTextField({
        label: {
          labelName: "Property ID",
          labelKey: "NOC_PROPERTY_ID_LABEL"
        },
        placeholder: {
          labelName: "Enter Property ID",
          labelKey: "NOC_PROPERTY_ID_PLACEHOLDER"
        },
        // iconObj: {
        //   iconName: "search",
        //   position: "end",
        //   color: "#FE7A51",
        //   onClickDefination: {
        //     action: "condition",
        //     callBack: (state, dispatch) => {
        //       getDetailsFromProperty(state, dispatch);
        //     }
        //   }
        // },
        // title: {
        //   value:
        //     "If you have already assessed your property, then please search your property by your PAID",
        //   key: "NOC_PROPERTY_ID_TOOLTIP_MESSAGE"
        // },
        // infoIcon: "info_circle",
        jsonPath: "FireNOCs[0].fireNOCDetails.propertyDetails.propertyId",
        visible: true
      }),

      propertyCity: {
        ...getSelectField({
          label: { labelName: "City", labelKey: "NOC_PROPERTY_CITY_LABEL" },
          localePrefix: {
            moduleName: "TENANT",
            masterName: "TENANTS"
          },
          optionLabel: "name",
          placeholder: {
            labelName: "Select City",
            labelKey: "NOC_PROPERTY_CITY_PLACEHOLDER"
          },
          sourceJsonPath: "applyScreenMdmsData.tenant.tenants",
          jsonPath: "FireNOCs[0].fireNOCDetails.propertyDetails.address.subDistrict",
          required: true,
          visible: false,
          props: {
            className: "applicant-details-error",
            required: true
            // disabled: true
          }
        }),
        beforeFieldChange: async (action, state, dispatch) => {
          //Below only runs for citizen - not required here in employee
          dispatch(
            prepareFinalObject(
              "FireNOCs[0].fireNOCDetails.propertyDetails.address.subDistrict",
              action.value
            )
          );

          // Set Firestation based on ULBl
          let fireStationsList = get(
            state,
            "screenConfiguration.preparedFinalObject.applyScreenMdmsData.firenoc.FireStations",
            []
          );
          console.log("fireStationsList", fireStationsList);
          let fireStations = fireStationsList.filter(firestation => {
            return firestation.baseTenantId === action.value;
          });
          if(fireStations.length ==0){           
            fireStations=fireStationsList.filter(firestation => {
            let subdistrict=firestation.subDistrict;
            subdistrict=subdistrict.filter(item=> {
              return item.code.toUpperCase() === action.value.split('.')[1].toUpperCase();
            });
            if(subdistrict.length > 0)
            return firestation;
          });
        }
          // dispatch(
          //   prepareFinalObject(
          //     "FireNOCs[0].fireNOCDetails.firestationId", fireStations[0].code)
          // );

          let fireStationsulb = fireStationsList.filter(firestation => {
            return firestation.ulb
          });

          let props_value;

          let fire_stationid=fireStations[0].code;

          dispatch(
            prepareFinalObject(
              "FireNOCs[0].tenantId", fireStations[0].baseTenantId)
          );

          dispatch(
            prepareFinalObject(
              "FireNOCs[0].fireNOCDetails.firestationId", fire_stationid)
          );




          try {
            let payload = await httpRequest(
              "post",
              "/egov-location/location/v11/boundarys/_search?hierarchyTypeCode=REVENUE&boundaryType=Locality",
              "_search",
              [{ key: "tenantId", value: action.value }],
              {}
            );
            console.log("payload", payload)
            const mohallaData =
              payload &&
              payload.TenantBoundary[0] &&
              payload.TenantBoundary[0].boundary &&
              payload.TenantBoundary[0].boundary.reduce((result, item) => {
                result.push({
                  ...item,
                  name: `${action.value
                    .toUpperCase()
                    .replace(
                      /[.]/g,
                      "_"
                    )}_REVENUE_${item.code
                      .toUpperCase()
                      .replace(/[._:-\s\/]/g, "_")}`
                });
                return result;
              }, []);

            console.log(mohallaData, "mohallaData")



            dispatch(
              prepareFinalObject(
                "applyScreenMdmsData.tenant.localities",
                mohallaData
              )
            );
            dispatch(
              handleField(
                "apply",
                "components.div.children.formwizardSecondStep.children.propertyLocationDetails.children.cardContent.children.propertyDetailsConatiner.children.propertyMohalla",
                "props.suggestions",
                mohallaData
              )
            );
            const mohallaLocalePrefix = {
              moduleName: action.value,
              masterName: "REVENUE"
            };
            dispatch(
              handleField(
                "apply",
                "components.div.children.formwizardSecondStep.children.propertyLocationDetails.children.cardContent.children.propertyDetailsConatiner.children.propertyMohalla",
                "props.localePrefix",
                mohallaLocalePrefix
              )
            );

            dispatch(
              fetchLocalizationLabel(getLocale(), action.value, action.value)
            );

          } catch (e) {
            console.log(e);
          }

        },
      },
      propertyPlotSurveyNo: getTextField({
        label: {
          labelName: "Plot/Survey No.",
          labelKey: "NOC_PROPERTY_PLOT_NO_LABEL"
        },
        props: {
          className: "applicant-details-error"
        },
        placeholder: {
          labelName: "Enter Plot/Survey No.",
          labelKey: "NOC_PROPERTY_PLOT_NO_PLACEHOLDER"
        },
        pattern: getPattern("DoorHouseNo"),
        errorMessage: "ERR_DEFAULT_INPUT_FIELD_MSG",
        jsonPath: "FireNOCs[0].fireNOCDetails.propertyDetails.address.doorNo"
      }),
      // propertyBuilidingName: getTextField({
      //   label: {
      //     labelName: "Building/Colony Name",
      //     labelKey: "NOC_PROPERTY_DETAILS_BLDG_NAME_LABEL"
      //   },
      //   props:{
      //     className:"applicant-details-error"
      //   },
      //   placeholder: {
      //     labelName: "Enter Building/Colony Name",
      //     labelKey: "NOC_PROPERTY_DETAILS_BLDG_NAME_PLACEHOLDER"
      //   },
      //   pattern: getPattern("BuildingStreet"),
      //   errorMessage: "ERR_DEFAULT_INPUT_FIELD_MSG",

      //   jsonPath:
      //     "FireNOCs[0].fireNOCDetails.propertyDetails.address.buildingName"
      // }),
      propertyStreetName: getTextField({
        label: {
          labelName: "Street Name",
          labelKey: "NOC_PROPERTY_DETAILS_SRT_NAME_LABEL"
        },
        props: {
          className: "applicant-details-error"
        },
        placeholder: {
          labelName: "Enter Street Name",
          labelKey: "NOC_PROPERTY_DETAILS_SRT_NAME_PLACEHOLDER"
        },
        pattern: getPattern("BuildingStreet"),
        errorMessage: "ERR_DEFAULT_INPUT_FIELD_MSG",
        jsonPath: "FireNOCs[0].fireNOCDetails.propertyDetails.address.street"
      }),
      propertyVillageName: getTextField({
        label: {
          labelName: "Village Name",
          labelKey: "NOC_PROPERTY_DETAILS_VILL_NAME_LABEL"
        },
        props: {
          className: "applicant-details-error"
        },
        placeholder: {
          labelName: "Enter Village Name",
          labelKey: "NOC_PROPERTY_DETAILS_VILL_NAME_PLACEHOLDER"
        },
        visible: false,
        required: true,
        jsonPath: "FireNOCs[0].fireNOCDetails.propertyDetails.address.addressLine2"
      }),
      propertyLandmark: getTextField({
        label: {
          labelName: "Landmark Name",
          labelKey: "NOC_PROPERTY_DETAILS_LANDMARK_NAME_LABEL"
        },
        placeholder: {
          labelName: "Enter Landmark",
          labelKey: "NOC_PROPERTY_DETAILS_LANDMARK_NAME_PLACEHOLDER"
        },
        visible: true,
        jsonPath: "FireNOCs[0].fireNOCDetails.propertyDetails.address.landmark"
      }),
      propertyMohalla: {
        uiFramework: "custom-containers",
        componentPath: "AutosuggestContainer",
        jsonPath:
          "FireNOCs[0].fireNOCDetails.propertyDetails.address.locality.code",
        required: true,
        props: {
          style: {
            width: "100%",
            cursor: "pointer"
          },
          label: {
            labelName: "Mohalla",
            labelKey: "NOC_PROPERTY_DETAILS_MOHALLA_LABEL"
          },
          placeholder: {
            labelName: "Select Mohalla",
            labelKey: "NOC_PROPERTY_DETAILS_MOHALLA_PLACEHOLDER"
          },
          jsonPath:
            "FireNOCs[0].fireNOCDetails.propertyDetails.address.locality.code",
          sourceJsonPath: "applyScreenMdmsData.tenant.localities",
          labelsFromLocalisation: true,
          errorMessage: "ERR_DEFAULT_INPUT_FIELD_MSG",
          suggestions: [],
          fullwidth: true,
          required: true,
          inputLabelProps: {
            shrink: true
          }
          // className: "tradelicense-mohalla-apply"
        },
        beforeFieldChange: async (action, state, dispatch) => {
          // dispatch(
          //   prepareFinalObject(
          //     "Licenses[0].tradeLicenseDetail.address.locality.name",
          //     action.value && action.value.label
          //   )
          // );
        },
        gridDefination: {
          xs: 12,
          sm: 6
        }
      },
      propertyPincode: getTextField({
        label: {
          labelName: "Pincode",
          labelKey: "NOC_PROPERTY_DETAILS_PIN_LABEL"
        },
        props:{
          className:"applicant-details-error"
        },
        placeholder: {
          labelName: "Enter Pincode",
          labelKey: "NOC_PROPERTY_DETAILS_PIN_PLACEHOLDER"
        },
        pattern: getPattern("Pincode"),
        errorMessage: "ERR_DEFAULT_INPUT_FIELD_MSG",
        jsonPath: "FireNOCs[0].fireNOCDetails.propertyDetails.address.pincode"
        // required: true
      }),
      propertyGisCoordinates: {
        uiFramework: "custom-atoms",
        componentPath: "Div",
        props: {
          className: "gis-div-css",
          style: {
            width: "100%",
            cursor: "pointer"
          },
          jsonPath:
            "FireNOCs[0].fireNOCDetails.propertyDetails.address.latitude"
        },
        jsonPath: "FireNOCs[0].fireNOCDetails.propertyDetails.address.latitude",
        onClickDefination: {
          action: "condition",
          callBack: showHideMapPopup
        },
        gridDefination: {
          xs: 12,
          sm: 6
        },
        children: {
          gisTextField: {
            ...getTextField({
              label: {
                labelName: "Locate on Map",
                labelKey: "NOC_PROPERTY_DETAILS_GIS_CORD_LABEL"
              },
              placeholder: {
                labelName: "Select your property location on map",
                labelKey: "NOC_PROPERTY_DETAILS_GIS_CORD_PLACEHOLDER"
              },
              jsonPath:
                "FireNOCs[0].fireNOCDetails.propertyDetails.address.latitude",
              iconObj: {
                iconName: "gps_fixed",
                position: "end"
              },
              gridDefination: {
                xs: 12,
                sm: 12
              },
              props: {
                disabled: true,
                cursor: "pointer",
                jsonPath:
                  "FireNOCs[0].fireNOCDetails.propertyDetails.address.latitude"
              }
            })
          }
        }
      },
      // propertyFirestation: {
      //   uiFramework: "custom-containers-local",
      //   moduleName: "egov-firenoc",
      //   componentPath: "AutosuggestContainer",
      //   props:{
      //     className:"applicant-details-error autocomplete-dropdown",
      //     label: {
      //       labelName: "Applicable Fire Station",
      //       labelKey: "NOC_PROPERTY_DETAILS_FIRESTATION_LABEL"
      //     },
      //     placeholder: {
      //       labelName: "Select Applicable Fire Station",
      //       labelKey: "NOC_PROPERTY_DETAILS_FIRESTATION_PLACEHOLDER"
      //     },
      //     required: true,
      //     isClearable: true,
      //     labelsFromLocalisation: true,
      //     localePrefix: {
      //       moduleName: "firenoc",
      //       masterName: "FireStations"
      //     },
      //     jsonPath: "FireNOCs[0].fireNOCDetails.firestationId",
      //   },
      //   required: true,
      //   jsonPath: "FireNOCs[0].fireNOCDetails.firestationId",
      //   gridDefination: {
      //     xs: 12,
      //     sm: 12,
      //     md: 6
      //   },
      // },
    }),
    mapsDialog: {
      componentPath: "Dialog",
      props: {
        open: false
      },
      children: {
        dialogContent: {
          componentPath: "DialogContent",
          children: {
            popup: getMapLocator()
          }
        }
      }
    }
  },
  {
    style: { overflow: "visible" }
  }
);
