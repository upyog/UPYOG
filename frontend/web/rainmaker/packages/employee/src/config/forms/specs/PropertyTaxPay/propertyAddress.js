import { pincode, mohalla ,street, colony, houseNumber } from "egov-ui-kit/config/forms/specs/PropertyTaxPay/utils/reusableFields";
import { handleFieldChange, setFieldProperty } from "egov-ui-kit/redux/form/actions";
import { CITY } from "egov-ui-kit/utils/endPoints";
import { prepareFormData } from "egov-ui-kit/redux/common/actions";
import set from "lodash/set";
import get from "lodash/get";
import { getLocale, getTenantId } from "egov-ui-kit/utils/localStorageUtils";
import { fetchLocalizationLabel } from "egov-ui-kit/redux/app/actions";
import commonConfig from '../../../common'
import { httpRequest } from "egov-ui-kit/utils/api";


// const Search = <Icon action="action" name="home" color="#30588c" />;
var tenantIdcode = getTenantId();
let floorDropDownData = [];

floorDropDownData.push({ label: "2013-14", value: "2013-14" }, { label: "2014-15", value: "2014-15" }, { label: "2015-16", value: "2015-16" }, { label: "2016-17", value: "2016-17" }, { label: "2017-18", value: "2017-18" }, { label: "2018-19", value: "2018-19" },
  { label: "2019-20", value: "2019-20" }, { label: "2020-21", value: "2020-21" },
  { label: "2021-22", value: "2021-22" }, { label: "2022-23", value: "2022-23" }, { label: "2023-24", value: "2023-24" });


const formConfig = {
  name: "propertyAddress",
  fields: {
    city: {
      id: "city",
      jsonPath: "PropertiesTemp[0].address.city",
      required: true,
      type: "singleValueList",
      floatingLabelText: "CORE_COMMON_CITY",
      className: "pt-emp-property-address-city",
      disabled: true,
      errorStyle: { position: "absolute", bottom: -8, zIndex: 5 },
      fullWidth: true,
      hintText: "PT_COMMONS_SELECT_PLACEHOLDER",
      numcols: 6,
      dataFetchConfig: {
        url: CITY.GET.URL,
        action: CITY.GET.ACTION,
        queryParams: [],
        requestBody: {
          MdmsCriteria: {
            tenantId: commonConfig.tenantId,
            moduleDetails: [
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
        },
        dataPath: ["MdmsRes.tenant.tenants"],
        dependants: [
          {
            fieldKey: "mohalla",
          },
        ],
      },

      beforeFieldChange: ({ action, dispatch, state }) => {
        if (get(state, "common.prepareFormData.PropertiesTemp[0].address.city") !== action.value) {
          const moduleValue = action.value;
          dispatch(fetchLocalizationLabel(getLocale(), moduleValue, moduleValue));
        }
        return action;
      },
    },
    ...houseNumber,
    ...colony,
    ...street,
   // ...mohalla,
    mohalla: {
      id: "mohalla",
      jsonPath: "Properties[0].address.locality.code",
      type: "AutocompleteDropdown",
      floatingLabelText: "PT_PROPERTY_DETAILS_MOHALLA",
      hintText: "PT_COMMONS_SELECT_PLACEHOLDER",
      fullWidth: true,
      toolTip: true,
      localePrefix: true,
      toolTipMessage: "PT_MOHALLA_TOOLTIP_MESSAGE",
      labelsFromLocalisation: true,
      //toolTipMessage: "Name of the area in which your property is located",
      boundary: true,
      numcols: 6,
      gridDefination: {
        xs: 12,
        sm: 6
      },
      errorMessage: "PT_PROPERTY_DETAILS_MOHALLA_ERRORMSG",
      dataFetchConfig: {
        url: "egov-location/location/v11/boundarys/_search?hierarchyTypeCode=REVENUE&boundaryType=Locality",
        action: "",
        queryParams: [],
        requestBody: {},
        isDependent: true,
        hierarchyType: "REVENUE",
      },
      errorStyle: { position: "absolute", bottom: -8, zIndex: 5 },
      required: true,
      formName: "propertyAddress",
      updateDependentFields: async ({ formKey, field, dispatch, state }) => {
        if (field.value && field.value.length > 0) {
          const mohalla = field.dropDownData.find((option) => {
            return option.value === field.value;
          });
          dispatch(prepareFormData("Properties[0].address.locality.area", mohalla.area));
        }
        setTimeout(async () => {
          let localityCode = await state.screenConfiguration.preparedFinalObject.Properties[0].address.locality.code;
              if (tenantIdcode == "pb.jalandhar" || tenantIdcode == "pb.testing") {
                
                let request = { searchCriteria: { tenantId: tenantIdcode } };
                try {
                  const response = await httpRequest(
                    "/egov-searcher/rainmaker-pt-gissearch/GetTenantConfig/_get",
                    "_get",
                    [],
                    request);
                  if (response) {
                    const data = response.data.find(obj => {
                      return obj.locality == localityCode;
                    });
                    dispatch(setFieldProperty(formKey, "UID", "required", data ? true : false));
                  }
                } catch (error) {
                  console.log("functions-js getUserDataFromUuid error", error);
                }
              }
            }, "100");
              
        
      },
    },
    ...pincode,
    oldPID: {
      id: "oldpid",
      type: "textfield",
      className: "pt-old-pid-text-field",
      //text: "PT_SEARCH_BUTTON",
      //iconRedirectionURL: "https://pmidc.punjab.gov.in/propertymis/search.php",
      jsonPath: "Properties[0].oldPropertyId",
      floatingLabelText: "PT_PROPERTY_ADDRESS_EXISTING_PID",
      hintText: "PT_PROPERTY_ADDRESS_EXISTING_PID_PLACEHOLDER",
      numcols: 6,
      errorMessage: "PT_PROPERTY_DETAILS_PINCODE_ERRORMSG",
      errorStyle: { position: "absolute", bottom: -8, zIndex: 5 },
      //toolTip: true,
      pattern: /^[^\$\"'<>?\\\\~`!@$%^+={}*,.:;“”‘’]{1,64}$/i,
      //toolTipMessage: "PT_OLDPID_TOOLTIP_MESSAGE",
      maxLength: 64,
    },
    UID: {
      id: "UID",
      type: "textFieldIcon",
      className: "pt-old-pid-text-field",
      text: "Search",
      iconRedirectionURL: getTenantId() == 'pb.amritsar' ? "https://arcserver.punjab.gov.in/portal/apps/webappviewer/index.html?id=8b678d4d5020448499054bf346843ea9" : getTenantId() == 'pb.hoshiarpur' ? "https://arcserver.punjab.gov.in/portal/apps/webappviewer/index.html?id=9bc1b255320a49c590dd17d4d258e054" : "https://gis.punjab.gov.in",
      jsonPath: "Properties[0].surveyId",
      floatingLabelText: "Survey Id/UID",
      hintText: "Enter Survey Id/UID",
      numcols: 6,
      errorMessage: "PT_PROPERTY_DETAILS_PINCODE_ERRORMSG",
      errorStyle: { position: "absolute", bottom: -8, zIndex: 5 },
      required: false,
      // toolTip: true,
      //pattern: /^[^\$\"'<>?\\\\~`!@$%^()+={}\[\]*:;“”‘’]{1,64}$/i,
      // toolTipMessage: "PT_OLDPID_TOOLTIP_MESSAGE",
      maxLength: 64,
    },

    YearcreationProperty: {
      id: "YearcreationProperty",
      type: "AutocompleteDropdown",
      className: "pt-old-pid-text-field",
      // iconRedirectionURL: getTenantId()=='pb.amritsar'? "https://arcserver.punjab.gov.in/portal/apps/webappviewer/index.html?id=8b678d4d5020448499054bf346843ea9": getTenantId()=='pb.hoshiarpur'?"https://arcserver.punjab.gov.in/portal/apps/webappviewer/index.html?id=9bc1b255320a49c590dd17d4d258e054": "https://gis.punjab.gov.in",
      jsonPath: "Properties[0].additionalDetails.yearConstruction",
      floatingLabelText: "Year of creation of Property",
      hintText: "Select",
      numcols: 6,
      gridDefination: {
        xs: 12,
        sm: 6
      },
      errorMessage: "PT_PROPERTY_DETAILS_PINCODE_ERRORMSG",
      errorStyle: { position: "absolute", bottom: -8, zIndex: 5 },

      formName: "propertyAddress",
      dropDownData: floorDropDownData,
      updateDependentFields: ({ formKey, field, dispatch }) => {
        if (field.value && field.value.length > 0) {
          const mohalla = field.dropDownData.find((option) => {
            return option.value === field.value;
          });
          dispatch(prepareFormData("Properties[0].additionalDetails.yearConstruction", mohalla.code));
        }
      },
      // toolTip: true,
      //pattern: /^[^\$\"'<>?\\\\~`!@$%^()+={}\[\]*:;“”‘’]{1,64}$/i,
      // toolTipMessage: "PT_OLDPID_TOOLTIP_MESSAGE",
      maxLength: 64,
    },

  },
  afterInitForm: (action, store, dispatch) => {
    let tenantId = getTenantId();
    let state = store.getState();
    const { citiesByModule } = state.common;
    const { PT } = citiesByModule || {};
    if (PT) {
      const tenants = PT.tenants;
      let found = tenants.find((city) => {
        return city.code === tenantId;
      });

      if (found) {
        const { cities } = state.common;
        let tenantInfo = cities.find((t) => {
          if (t.code == found.code)
            return t;
        })
        let cityName = tenantId;
        if (tenantInfo && tenantInfo.city && tenantInfo.city.name)
          cityName = tenantInfo.city.name;

        let surveyId = get(state.screenConfiguration.preparedFinalObject, "Properties[0].surveyId");
        let year = get(state.screenConfiguration.preparedFinalObject, "Properties[0].additionalDetails.yearConstruction");
        dispatch(handleFieldChange("propertyAddress", "YearcreationProperty", year));
        if (surveyId)
          dispatch(handleFieldChange("propertyAddress", "UID", surveyId));

        dispatch(handleFieldChange("propertyAddress", "city", tenantId));
        dispatch(prepareFormData("Properties[0].address.city", cityName));
      }
    }
    set(action, "form.fields.city.required", true);
    set(action, "form.fields.pincode.disabled", false);
    return action;
  },
  action: "",
  redirectionRoute: "",
  saveUrl: "",
  isFormValid: false,
};

export default formConfig;
