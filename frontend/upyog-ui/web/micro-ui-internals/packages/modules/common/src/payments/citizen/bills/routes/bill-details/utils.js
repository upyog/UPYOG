export const stringReplaceAll = (str = "", searcher = "", replaceWith = "") => {
    if (searcher == "") return str;
    while (str.includes(searcher)) {
      str = str.replace(searcher, replaceWith);
    }
    return str;
  };

  export const timerEnabledForBusinessService = (businessService) =>{
  const timerEnabledBusinessServices =  ["adv-services", "chb-services"];
      if(timerEnabledBusinessServices.includes(businessService)){
        return true;
      }
      return false;
  };