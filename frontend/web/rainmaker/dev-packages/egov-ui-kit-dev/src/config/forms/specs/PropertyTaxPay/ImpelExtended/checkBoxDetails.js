import set from "lodash/set";
import get from "lodash/get";
const formConfig = {
    name: "checkBoxDetails",
    fields: {
        inflammableMaterial: {
            id: "inflammableMaterial",
            jsonPath: "Properties[0].additionalDetails.inflammable",
            type: "checkbox",
            floatingLabelText: "PT_COMMON_INFLAMMABLE_MATERIAL_PROPERTY",
            hintText: "Do you have any inflammable material stored in your property?",
            required: true,
            fullWidth: true,
        },
        
      
        heightOfProperty: {
            id: "heightOfProperty",
            jsonPath: "Properties[0].additionalDetails.heightAbove36Feet",
            type: "checkbox",
            floatingLabelText: "PT_COMMON_HEIGHT_OF_PROPERTY",
            hintText: "Height of property more than 36 feet?",
            required: true,
            fullWidth: true,
        }
    },
    // beforeInitForm: (action, store) => {
    //     try {
    //       let state = store.getState();
    //       set(action, "form.fields.heightAbove36Feet.value", get(state.common.prepareFormData, "Properties[0].additionalDetails.heightAbove36Feet", ""));
    //       set(action, "form.fields.inflammable.value", get(state.common.prepareFormData, "Properties[0].additionalDetails.inflammable", ""));
    //       return action;
    //     } catch (e) {
    //       console.log(e);
    //     }
    //   },
}
export default formConfig;