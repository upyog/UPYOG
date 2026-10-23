const Urls = {
  MDMS: `/egov-mdms-service/v1/_search`,
  MDMSV2: `/mdms-v2/v1/_search`,
  WorkFlow: `/egov-workflow-v2/egov-wf/businessservice/_search`,
  WorkFlowProcessSearch: `/egov-workflow-v2/egov-wf/process/_search`,
  localization: `/localization/messages/v1/_search`,
  location: {
    localities: `/egov-location/location/v11/boundarys/_search?hierarchyTypeCode=ADMIN&boundaryType=Locality`,
    wards: `/egov-location/location/v11/boundarys/_search?hierarchyTypeCode=ADMIN&boundaryType=Ward`,
    revenue_localities: `/egov-location/location/v11/boundarys/_search?hierarchyTypeCode=REVENUE&boundaryType=Locality`,
  },

  
  
  filter_data: `https://run.mocky.io/v3/597a50a0-90e5-4a45-b82e-8a2186b760bd`,
  FileStore: "/filestore/v1/files",

  FileFetch: "/filestore/v1/files/url",
 
 

  OTP_Send: "/user-otp/v1/_send",
  Authenticate: "/user/oauth/token",
  RegisterUser: "/user/citizen/_create",
  ChangePassword: "/user/password/nologin/_update",
  ChangePassword1: "/user/password/_update",
  UserProfileUpdate: "/user/profile/_update",
 


  UserSearch: "/user/_search",
  UserLogout: "/user/_logout",

  Shortener: "/egov-url-shortening/shortener",

  works: {
    create:"/loi-service/v1/_create",
    estimateSearch:"/estimate-service/estimate/v1/_search",
    loiSearch:"/loi-service/v1/_search",
    createEstimate:"/estimate-service/estimate/v1/_create",
    approvedEstimateSearch:"/estimate-service/estimate/v1/_search",
    searchEstimate:"/estimate-service/estimate/v1/_search",
    updateLOI:"/loi-service/v1/_update",
    updateEstimate:"/estimate-service/estimate/v1/_update",
    download_pdf:"/egov-pdf/download/WORKSESTIMATE/estimatepdf"
  },
  access_control: "/access/v1/actions/mdms/_get",
  
};

export default Urls;
