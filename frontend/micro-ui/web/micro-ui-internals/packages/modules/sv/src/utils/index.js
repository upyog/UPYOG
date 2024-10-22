export const stringReplaceAll = (str = "", searcher = "", replaceWith = "") => {
    if (searcher == "") return str;
    while (str.includes(searcher)) {
      str = str.replace(searcher, replaceWith);
    }
    return str;
};
export const checkForNotNull = (value = "") => {
  return value && value != null && value != undefined && value != "" ? true : false;
};

export const checkForNA = (value = "") => {
  return checkForNotNull(value) ? value : "NA";
};

export const getOrderDocuments = (appUploadedDocumnets, isNoc = false) => {
  let finalDocs = [];
  if (appUploadedDocumnets?.length > 0) {
    let uniqueDocmnts = appUploadedDocumnets.filter((elem, index) => appUploadedDocumnets.findIndex((obj) => obj?.documentType?.split(".")?.slice(0, 2)?.join("_") === elem?.documentType?.split(".")?.slice(0, 2)?.join("_")) === index);
    uniqueDocmnts?.map(uniDoc => {
      const resultsDocs = appUploadedDocumnets?.filter(appDoc => uniDoc?.documentType?.split(".")?.slice(0, 2)?.join("_") == appDoc?.documentType?.split(".")?.slice(0, 2)?.join("_"));
      resultsDocs?.forEach(resDoc => resDoc.title = resDoc.documentType);
      finalDocs.push({
        title: !isNoc ? resultsDocs?.[0]?.documentType?.split(".")?.slice(0, 2)?.join("_") : "",
        values: resultsDocs
      })
    });
  }
  return finalDocs;
}



 /**
  * @author Shivank 
  *  Utility function to calculate that accurately calculates age from a birth date
   * Calculates age from a given birth date
   * 
   * @param {string} birthDate - Date of birth in YYYY-MM-DD format
   * @returns {number} - Age in years
   */
export const calculateAge = (birthDate) => {
  const today = new Date();
  const birth = new Date(birthDate);
  let age = today.getFullYear() - birth.getFullYear();
  const monthDiff = today.getMonth() - birth.getMonth();
  // Adjust age if birthday hasn't occurred this year
  if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birth.getDate())) {
    age--;
  }
  return age;
};