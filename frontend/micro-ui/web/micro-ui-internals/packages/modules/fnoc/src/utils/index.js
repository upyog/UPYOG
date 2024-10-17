export const convertDotValues = (value = "") => {
    return (
      (checkForNotNull(value) && ((value.replaceAll && value.replaceAll(".", "_")) || (value.replace && stringReplaceAll(value, ".", "_")))) || "NA"
    );
  };

  export const stringReplaceAll = (str = "", searcher = "", replaceWith = "") => {
    if (searcher == "") return str;
    while (str.includes(searcher)) {
      str = str.replace(searcher, replaceWith);
    }
    return str;
  };

  export const sortDropdownNames = (options, optionkey, locilizationkey) => {
    return options.sort((a, b) => locilizationkey(a[optionkey]).localeCompare(locilizationkey(b[optionkey])));
  };


  // Custom Function which will calculate Financial Year Accordingly 
  export const calculateCurrentFinancialYear = () => {
    const currentDate = new Date();
    const year = currentDate.getFullYear();
    const month = currentDate.getMonth() + 1; // getMonth() is zero-based
    if (month >= 4) {
      return `${year}-${(year + 1).toString().slice(-2)}`;  
      //it will return the value in format of - YYYY-YY, you can remove ".toString().slice(-2)" to get the value in this format - "YYYY-YYYY" 
    } else {
      return `${year - 1}-${year.toString().slice(-2)}`;
    }
  };