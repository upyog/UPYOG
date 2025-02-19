import { sortDropdown } from "egov-ui-kit/utils/PTCommon";
import { setDependentFields } from "../utils/enableDependentFields";
import { prepareFormData } from "egov-ui-kit/redux/common/actions";
import {
  subUsageType,
  occupancy,
  builtArea,
  annualRent,
  beforeInitForm,
  mergeMaster,
  prepareDropDownData,
  getPresentMasterObj,
  getAbsentMasterObj,
  floorName,
} from "../utils/reusableFields";
import filter from "lodash/filter";
import get from "lodash/get";
import set from "lodash/set";
import isEmpty from "lodash/isEmpty";

const formConfig = {
  name: "floorDetails",
  fields: {
    usageType: {
      id: "assessment-usageType",
      jsonPath: "Properties[0].propertyDetails[0].units[0].usageCategoryMinor",
      type: "singleValueList",
      localePrefix: "PROPERTYTAX_BILLING_SLAB",
      floatingLabelText: "PT_FORM2_USAGE_TYPE",
      hintText: "PT_COMMONS_SELECT_PLACEHOLDER",
      required: true,
      numcols: 4,
      dropDownData: [],
      formName: "plotDetails",
      updateDependentFields: ({ formKey, field, dispatch, state }) => {
        let minorObject = get(state, `common.generalMDMSDataById.UsageCategoryMinor[${field.value}]`);
        if (!isEmpty(minorObject)) {
          dispatch(prepareFormData(`${field.jsonPath.split("usageCategoryMinor")[0]}usageCategoryMajor`, minorObject.usageCategoryMajor));
          var filteredSubUsageMinor = filter(
            prepareDropDownData(get(state, "common.generalMDMSDataById.UsageCategorySubMinor"), true),
            (subUsageMinor) => {
              return subUsageMinor.usageCategoryMinor === field.value;
            }
          );
          if (filteredSubUsageMinor.length > 0) {
            var filteredUsageCategoryDetails = getPresentMasterObj(
              prepareDropDownData(get(state, "common.generalMDMSDataById.UsageCategoryDetail"), true),
              filteredSubUsageMinor,
              "usageCategorySubMinor"
            );
            setDependentFields(["subUsageType"], dispatch, formKey, false);
            const mergedMaster = mergeMaster(filteredSubUsageMinor, filteredUsageCategoryDetails, "usageCategorySubMinor");
            const subUsageData = sortDropdown(mergedMaster, "label", true);
            setDependentFields(["subUsageType"], dispatch, formKey, subUsageData, "dropDownData");
          }
        } else {
          setDependentFields(["subUsageType"], dispatch, formKey, true);
          dispatch(prepareFormData(`${field.jsonPath.split("usageCategoryMinor")[0]}usageCategoryMajor`, field.value));
          dispatch(prepareFormData(`${field.jsonPath.split("usageCategoryMinor")[0]}usageCategoryMinor`, null));
        }
      },
    },
    ...subUsageType,
    ...occupancy,
    ...builtArea,
    ...floorName,
    ...annualRent,
  },
  isFormValid: false,
  ...beforeInitForm,
};

export default formConfig;
