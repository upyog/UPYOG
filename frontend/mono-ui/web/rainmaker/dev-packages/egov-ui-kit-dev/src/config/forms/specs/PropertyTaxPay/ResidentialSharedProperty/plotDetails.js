import { MDMS } from "egov-ui-kit/utils/endPoints";
import { measuringUnit, occupancy, subUsageType, beforeInitFormForPlot, superArea, annualRent, floorName ,noOfMonths,usageForDueMonths} from "../utils/reusableFields";
import { prepareFormData } from "egov-ui-kit/redux/common/actions";

const formConfig = {
  name: "plotDetails",
  fields: {
    usageType: {
      id: "assessment-usageType",
      jsonPath: "Properties[0].propertyDetails[0].units[0].usageCategoryMajor",
      type: "textfield",
      floatingLabelText: "PT_FORM2_USAGE_TYPE",
      hintText: "PT_COMMONS_SELECT_PLACEHOLDER",
      value: "Residential",
      required: true,
      disabled: true,
      numcols: 4,
      formName: "plotDetails",
    },
    ...subUsageType,
    ...occupancy,
    ...superArea,
    ...measuringUnit,
    ...floorName,
    ...annualRent,
    ...noOfMonths,
    ...usageForDueMonths
  },
  isFormValid: false,
  ...beforeInitFormForPlot,
};

export default formConfig;
