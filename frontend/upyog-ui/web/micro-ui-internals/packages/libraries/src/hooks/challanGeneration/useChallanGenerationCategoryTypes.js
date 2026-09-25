const useChallanGenerationCategoryTypes = (selectedCategory,categoriesandTypes) => {
let categorieTypes = [];
categoriesandTypes && selectedCategory && categoriesandTypes?.MdmsRes?.BillingService?.BusinessService?.filter((ob) => ob.code.split(".")[0] === selectedCategory.code.split(".")[0]).map((type) =>{
    categorieTypes.push({...type, i18nkey: `BILLINGSERVICE_BUSINESSSERVICE_${type.code.toUpperCase().replaceAll(".","_")}`})
});
return categorieTypes;
};

/**
 * Filters and returns category types based on selected category.
 *
 * - Matches BusinessService items by code prefix with selectedCategory.
 * - Generates an i18n key for each type for localization.
 *
 * @param {Object} selectedCategory - Selected category object
 * @param {Object} categoriesandTypes - MDMS response data
 *
 * @returns {Array} categorieTypes - Filtered and formatted category types
 */

export default useChallanGenerationCategoryTypes;