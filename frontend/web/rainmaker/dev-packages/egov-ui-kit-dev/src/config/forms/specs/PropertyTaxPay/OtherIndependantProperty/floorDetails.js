import { MDMS } from "egov-ui-kit/utils/endPoints";
import { subUsageType, occupancy, builtArea, annualRent, floorName, beforeInitForm , usageForDueMonths } from "../utils/reusableFields";

const formConfig = {
  name: "floorDetails",
  fields: {
    usageType: {
      id: "assessment-usageType",
      jsonPath: "Properties[0].propertyDetails[0].units[0].usageCategoryMinor",
      type: "textfield",
      floatingLabelText: "PT_FORM2_USAGE_TYPE",
      // value: "Other",
      hintText: "PT_COMMONS_SELECT_PLACEHOLDER",
      value: "PROPERTYTAX_BILLING_SLAB_OTHER",
      required: true,
      disabled: true,
      numcols: 4,
      formName: "plotDetails",
    },
    ...subUsageType,
    ...occupancy,
    ...builtArea,
    ...floorName,
    ...annualRent,
    ...noOfMonths,
    ...usageForDueMonths
  },
  isFormValid: false,
  ...beforeInitForm,
};

export default formConfig;
