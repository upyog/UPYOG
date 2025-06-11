const Urls = {
  MDMS: `/egov-mdms-service/v1/_search`,
  MDMSV2: `/mdms-v2/v1/_search`,
  WorkFlow: `/egov-workflow-v2/egov-wf/businessservice/_search`,
  WorkFlowProcessSearch: `/egov-workflow-v2/egov-wf/process/_search`,
  localization: `/localization/messages/v1/_search`,

  location: {
    localities: `/egov-location/location/v11/boundarys/_search?hierarchyTypeCode=ADMIN&boundaryType=Locality`,
    revenue_localities: `/egov-location/location/v11/boundarys/_search?hierarchyTypeCode=REVENUE&boundaryType=Locality`,
    gramPanchayats: `/egov-location/location/v11/boundarys/_search?hierarchyTypeCode=REVENUE&boundaryType=GP`,
    vendingZones: `/egov-location/location/v11/boundarys/_search?hierarchyTypeCode=ADMIN&boundaryType=VendingZone`,
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
  EmployeeSearch: "/egov-hrms/employees/_search",

  InboxSearch: "/inbox/v1/_search",
  vendorSearch: "/vendor/v1/_search",
  vehicleSearch: "/vehicle/v1/_search",
  UserSearch: "/user/_search",
  UserSearchNew: "/user/users/v2/_search",
  UserLogout: "/user/_logout",
  Shortener: "/egov-url-shortening/shortener",
  employeeDashboardSearch: "/employee-dashboard/_search",
  payment: {
    fetch_bill: "/billing-service/bill/v2/_fetchbill",
    demandSearch: "/billing-service/demand/_search",
    create_reciept: "/collection-services/payments/_create",
    print_reciept: "/collection-services/payments",
    generate_pdf: "/pdf-service/v1/_create",
    create_citizen_reciept: "/pg-service/transaction/v1/_create",
    update_citizen_reciept: "/pg-service/transaction/v1/_update",
    search_bill: "/billing-service/bill/v2/_search",
    reciept_search: "/collection-services/payments/:buisnessService/_search",
    obps_Reciept_Search: "/collection-services/payments/_search",
    billAmendmentSearch: "/billing-service/amendment/_search",
    getBulkPdfRecordsDetails: "/pdf-service/v1/_getBulkPdfRecordsDetails",
  },
  events: {
    search: "/egov-user-event/v1/events/_search",
    update: "/egov-user-event/v1/events/lat/_update",
    updateEvent: "/egov-user-event/v1/events/_update",
    updateEventCDG: "/egov-user-event/v1/events/lat/_update",
    count: "/egov-user-event/v1/events/notifications/_count",
    create: "/egov-user-event/v1/events/_create",
  },

  digiLocker:{
    authorization:"/requester-services-dx/user/authorization/url",
    register :"/requester-services-dx/user/authorization/url/citizen",
    token:"/requester-services-dx/user/token/citizen",
    issueDoc:"/requester-services-dx/user/issuedfiles",
    uri:"/requester-services-dx/user/file"
  },
  
  bills: {
    cancelBill: "/billing-service/bill/v2/_cancelbill",
  },

  sv:{
    create: "/sv-services/street-vending/_create",
    search: "/sv-services/street-vending/_search",
    update: "/sv-services/street-vending/_update",
    deleteDraft:"/sv-services/street-vending/_deletedraft",
    demandCreate: "/sv-services/street-vending/_createdemand"
  },

  access_control: "/access/v1/actions/mdms/_get",
  billgenie: "/egov-searcher",
  audit: "/inbox/v1/elastic/_search",
};

export default Urls;

export const getOpenStreetMapUrl = (lat, lng) => {
  return `https://nominatim.openstreetmap.org/reverse?format=json&lat=${lat}&lon=${lng}&zoom=18&addressdetails=1`;
}