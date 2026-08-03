
const useChallanGenerationTaxHeads = (selectedCategoryType,categoriesandTypes) => {
   let TaxHeadMasterFields = [];
   TaxHeadMasterFields = categoriesandTypes && categoriesandTypes?.MdmsRes?.BillingService?.TaxHeadMaster?.filter((ob) => ob.service === selectedCategoryType?.code);
   return TaxHeadMasterFields;
};
    
/**
 * Filters and returns tax heads based on selected category type.
 *
 * - Extracts TaxHeadMaster data from MDMS response.
 * - Filters tax heads where service matches selectedCategoryType.code.
 *
 * @param {Object} selectedCategoryType - Selected category type
 * @param {Object} categoriesandTypes - MDMS response data
 *
 * @returns {Array} TaxHeadMasterFields - Filtered tax heads
 */

export default useChallanGenerationTaxHeads;