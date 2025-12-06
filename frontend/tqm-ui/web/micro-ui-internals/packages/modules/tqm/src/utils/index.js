export default {
  isPlantOperatorLoggedIn: () => {
    return Digit.Utils.didEmployeeHasAtleastOneRole(Digit.Customizations.commonUiConfig.tqmRoleMapping?.plant)
  },
  isUlbAdminLoggedIn: () => {
    return Digit.Utils.didEmployeeHasAtleastOneRole(Digit.Customizations.commonUiConfig.tqmRoleMapping?.ulb)
  },
  convertDateRangeToEpochObj: (dateRange) => {
    //null check
    if(!dateRange || Object.keys(dateRange).length===0){
      return null
    }
    //convert to epoch and send {fromDate,toDate}
    const {range:{startDate,endDate}} = dateRange
    const fromDate = new Date(startDate).getTime()
    const toDate = new Date(endDate).getTime()

    return {
      fromDate,
      toDate
    }
  },
  tqmAccess : () => {
    const userInfo = Digit.UserService.getUser();
    const userRoles = userInfo?.info?.roles?.map((roleData) => roleData?.code);
    const tqmRoles = [
      "PQM_TP_OPERATOR",
      "PQM_ADMIN",
    ];
  
    const TQM_ACCESS = userRoles?.filter((role) => tqmRoles?.includes(role));
  
    return TQM_ACCESS?.length > 0;
  },
  isUserNotLinkedToPlant: () => {
    return Digit.SessionStorage.get("user_plants")?.filter(row => row.plantCode)?.length===0 || !Digit.SessionStorage.get("user_plants")
  },
  
  getMappedPlants:()=>{
    return Digit.SessionStorage.get("user_plants")?.length>=0 ? Digit.SessionStorage.get("user_plants")?.filter(row => row.plantCode) : []
  },
  //fn to decide whether to destroy a session or not
  destroySessionHelper:(currentPath,pathList,sessionName) => {
    if(!pathList.includes(currentPath)){
      sessionStorage.removeItem(`Digit.${sessionName}`)
    }
  }
}

export const checkForEmployee = (roles) => {
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const userInfo = Digit.UserService.getUser();
  let rolesArray = [];

  const rolearray = userInfo?.info?.roles.filter((item) => {
    for (let i = 0; i < roles.length; i++) {
      if (item.code == roles[i] && item.tenantId === tenantId) rolesArray.push(true);
    }
  });

  return rolesArray?.length;
};