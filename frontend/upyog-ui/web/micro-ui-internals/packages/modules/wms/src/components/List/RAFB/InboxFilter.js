// import { ActionBar, ApplyFilterBar, CloseSvg, Dropdown, RadioButtons, RemoveableTag, SubmitBar } from "@egovernments/digit-ui-react-components";
// import React, { useEffect, useState } from "react";
// import { useTranslation } from "react-i18next";
// import { getCityThatUserhasAccess } from "./Utils";

// const Filter = ({ searchParams, onFilterChange, onSearch, removeParam, ...props }) => {
//   const [filters, onSelectFilterRoles] = useState(searchParams?.filters?.role || { role: [] });
//   const [_searchParams, setSearchParams] = useState(() => searchParams);
//   const [selectedRoles, onSelectFilterRolessetSelectedRole] = useState(null);
//   const { t } = useTranslation();
//   const tenantIds = Digit.SessionStorage.get("WMS_TENANTS");

//   function onSelectRoles(value, type) {
//     if (!ifExists(filters.role, value)) {
//       onSelectFilterRoles({ ...filters, role: [...filters.role, value] });
//     }
//   }

//   const onRemove = (index, key) => {
//     let afterRemove = filters[key].filter((value, i) => {
//       return i !== index;
//     });
//     onSelectFilterRoles({ ...filters, [key]: afterRemove });
//   };

//   useEffect(() => {
//     if (filters.role.length > 1) {
//       onSelectFilterRolessetSelectedRole({ name: `${filters.role.length} selected` });
//     } else {
//       onSelectFilterRolessetSelectedRole(filters.role[0]);
//     }
//   }, [filters.role]);
//   const [tenantId, settenantId] = useState(() => {
//     return tenantIds?.filter(
//       (ele) =>
//         ele.code == (searchParams?.tenantId != undefined ? { code: searchParams?.tenantId } : { code: Digit.ULBService.getCurrentTenantId() })?.code
//     )[0];
//   });
//   const { isLoading, isError, errors, data: data, ...rest } = Digit.Hooks.hrms.useHrmsMDMS(
//     tenantId ? tenantId.code : searchParams?.tenantId,
//     "egov-hrms",
//     "HRMSRolesandDesignation"
//   );
//   const [departments, setDepartments] = useState(() => {
//     return { departments: null };
//   });

//   const [roles, setRoles] = useState(() => {
//     return { roles: null };
//   });
//   const [isActive, setIsactive] = useState(() => {
//     return { isActive: true };
//   });

//   useEffect(() => {
//     if (tenantId?.code) {
//       setSearchParams({ ..._searchParams, tenantId: tenantId.code });
//     }
//   }, [tenantId]);

//   useEffect(() => {
//     if (filters.role && filters.role.length > 0) {
//       let res = [];
//       filters.role.forEach((ele) => {
//         res.push(ele.code);
//       });

//       setSearchParams({ ..._searchParams, roles: [...res].join(",") });
//       if (filters.role && filters.role.length > 1) {
//         let res = [];
//         filters.role.forEach((ele) => {
//           res.push(ele.code);
//         });
//         setSearchParams({ ..._searchParams, roles: [...res].join(",") });
//       }
//     }
//   }, [filters.role]);

//   useEffect(() => {
//     if (departments) {
//       setSearchParams({ ..._searchParams, departments: departments.code });
//     }
//   }, [departments]);

//   useEffect(() => {
//     if (roles) {
//       setSearchParams({ ..._searchParams, roles: roles.code });
//     }
//   }, [roles]);

//   const ifExists = (list, key) => {
//     return list?.filter((object) => object.code === key.code).length;
//   };

//   useEffect(() => {
//     if (isActive) {
//       setSearchParams({ ..._searchParams, isActive: isActive.code });
//     }
//   }, [isActive]);
//   const clearAll = () => {
//     onFilterChange({ delete: Object.keys(searchParams) });
//     settenantId(tenantIds.filter((ele) => ele.code == Digit.ULBService.getCurrentTenantId())[0]);
//     setDepartments(null);
//     setRoles(null);
//     setIsactive(null);
//     props?.onClose?.();
//     onSelectFilterRoles({ role: [] });
//   };

//   const GetSelectOptions = (lable, options, selected, select, optionKey, onRemove, key) => {
//     selected = selected || { [optionKey]: " ", code: "" };
//     return (
//       <div>
//         <div className="filter-label">{lable}</div>
//         {<Dropdown option={options} selected={selected} select={(value) => select(value, key)} optionKey={optionKey} />}
//         <div className="tag-container">
//           {filters?.role?.length > 0 &&
//             filters?.role?.map((value, index) => {
//               return <RemoveableTag key={index} text={`${value[optionKey].slice(0, 22)} ...`} onClick={() => onRemove(index, key)} />;
//             })}
//         </div>
//       </div>
//     );
//   };
//   const ULBData= [{
//     "ResponseInfo": null,
//     "MdmsRes": {
//         "common-masters": {
//             "Designation": [
//                 {
//                     "code": "DESIG_01",
//                     "name": "Superintending Engineer ( B&R)",
//                     "description": "Superintending Engineer ( B&R)",
//                     "active": true
//                 },
//                 {
//                     "code": "DESIG_02",
//                     "name": "Corporation Engineer (B&R)",
//                     "description": "Corporation Engineer (B&R)",
//                     "active": true
//                 },
//                 {
//                     "code": "DESIG_03",
//                     "name": "Asst. Engineer ( B&R)",
//                     "description": "Asst. Engineer ( B&R)",
//                     "active": true
//                 },
//                 {
//                     "code": "DESIG_04",
//                     "name": "Junior Engineer ( B&R)",
//                     "description": "Junior Engineer ( B&R)",
//                     "active": true
//                 },
//                 {
//                     "code": "DESIG_05",
//                     "name": "Land Scape Officer",
//                     "description": "Land Scape Officer",
//                     "active": true
//                 },
//                 {
//                     "code": "DESIG_06",
//                     "name": "Superintending Engineer ( O&M)",
//                     "description": "Superintending Engineer ( O&M)",
//                     "active": true
//                 },
//                 {
//                     "code": "DESIG_07",
//                     "name": "Corporation Engineer (O&M)",
//                     "description": "Corporation Engineer (O&M)",
//                     "active": true
//                 },
//                 {
//                     "code": "DESIG_08",
//                     "name": "Asst. Engineer ( O&M)",
//                     "description": "Asst. Engineer ( O&M)",
//                     "active": true
//                 },
//                 {
//                     "code": "DESIG_09",
//                     "name": "Junior Engineer ( O&M)",
//                     "description": "Junior Engineer ( O&M)",
//                     "active": true
//                 },
//                 {
//                     "code": "DESIG_10",
//                     "name": "Superintending Engineer ( Light)",
//                     "description": "Superintending Engineer ( Light)",
//                     "active": true
//                 },
//                 {
//                     "code": "DESIG_11",
//                     "name": "Corporation Engineer (Light)",
//                     "description": "Corporation Engineer (Light)",
//                     "active": true
//                 },
//                 {
//                     "code": "DESIG_12",
//                     "name": "Junior Engineer ( Light)",
//                     "description": "Junior Engineer ( Light)",
//                     "active": true
//                 },
//                 {
//                     "code": "DESIG_13",
//                     "name": "Health Officer",
//                     "description": "Health Officer",
//                     "active": true
//                 },
//                 {
//                     "code": "DESIG_14",
//                     "name": "Medical Officer",
//                     "description": "Medical Officer",
//                     "active": true
//                 },
//                 {
//                     "code": "DESIG_15",
//                     "name": "Chief Sanitary Inspector",
//                     "description": "Mechanical Oversear",
//                     "active": true
//                 },
//                 {
//                     "code": "DESIG_16",
//                     "name": "Sainitary Inspector",
//                     "description": "Clerk",
//                     "active": true
//                 },
//                 {
//                     "code": "DESIG_17",
//                     "name": "Sainitary Supervisor",
//                     "description": "Accountant",
//                     "active": true
//                 },
//                 {
//                     "code": "DESIG_18",
//                     "name": "Senior Town Planner",
//                     "description": "Senior Town Planner",
//                     "active": true
//                 },
//                 {
//                     "code": "DESIG_19",
//                     "name": "Municipal Town Planner",
//                     "description": "Municipal Town Planner",
//                     "active": true
//                 },
//                 {
//                     "code": "DESIG_20",
//                     "name": "Asst. Town Planner",
//                     "description": "Asst. Town Planner",
//                     "active": true
//                 },
//                 {
//                     "code": "DESIG_21",
//                     "name": "Building Inspector",
//                     "description": "Building Inspector",
//                     "active": true
//                 },
//                 {
//                     "code": "DESIG_22",
//                     "name": "Junior Enginer ( Horticulutre)",
//                     "description": "Junior Enginer ( Horticulutre)",
//                     "active": true
//                 },
//                 {
//                     "code": "DESIG_23",
//                     "name": "Citizen service representative",
//                     "description": "Citizen service representative",
//                     "active": true
//                 },
//                 {
//                     "name": "Deputy Controller Finance and Accounts",
//                     "description": "Deputy Controller Finance and Accounts",
//                     "code": "DESIG_1001",
//                     "active": true
//                 },
//                 {
//                     "name": "Accountant",
//                     "description": "Accountant",
//                     "code": "DESIG_58",
//                     "active": true
//                 },
//                 {
//                     "code": "DESIG_24",
//                     "name": "Assistant Commissioner",
//                     "description": "Assistant Commissioner",
//                     "active": true
//                 },
//                 {
//                     "name": "Superintendent",
//                     "description": "Superintendent",
//                     "code": "DESIG_47",
//                     "active": true
//                 },
//                 {
//                     "name": "Accounts Officer",
//                     "description": "Accounts Officer",
//                     "code": "AO",
//                     "active": true
//                 },
//                 {
//                     "name": "Commissioner",
//                     "description": "Commissioner",
//                     "code": "COMM",
//                     "active": true
//                 },
//                 {
//                     "name": "FSM Executive",
//                     "description": "FSM Executive",
//                     "code": "DESIG_200",
//                     "active": true
//                 }
//             ],
//             "Department": [
//                 {
//                     "name": "Street Lights",
//                     "code": "DEPT_1",
//                     "active": true
//                 },
//                 {
//                     "name": "Building & Roads",
//                     "code": "DEPT_2",
//                     "active": true
//                 },
//                 {
//                     "name": "Health & Sanitation",
//                     "code": "DEPT_3",
//                     "active": true
//                 },
//                 {
//                     "name": "Operation & Maintenance",
//                     "code": "DEPT_4",
//                     "active": true
//                 },
//                 {
//                     "name": "Horticulture",
//                     "code": "DEPT_5",
//                     "active": true
//                 },
//                 {
//                     "name": "Building Branch",
//                     "code": "DEPT_6",
//                     "active": true
//                 },
//                 {
//                     "name": "Citizen service desk",
//                     "code": "DEPT_7",
//                     "active": true
//                 },
//                 {
//                     "name": "Complaint Cell",
//                     "code": "DEPT_8",
//                     "active": true
//                 },
//                 {
//                     "name": "Executive Branch",
//                     "code": "DEPT_9",
//                     "active": true
//                 },
//                 {
//                     "name": "Others",
//                     "code": "DEPT_10",
//                     "active": true
//                 },
//                 {
//                     "name": "Tax Branch",
//                     "code": "DEPT_13",
//                     "active": true
//                 },
//                 {
//                     "name": "Accounts Branch",
//                     "code": "DEPT_25",
//                     "active": true
//                 },
//                 {
//                     "name": "Works Branch",
//                     "code": "DEPT_35",
//                     "active": true
//                 }
//             ],
//             "StateInfo": [
//                 {
//                     "name": "Demo",
//                     "code": "pg",
//                     "qrCodeURL": "https://lh3.googleusercontent.com/-311gz2-xcHw/X6KRNSQTkWI/AAAAAAAAAKU/JmHSj-6rKPMVFbo6oL5x4JhYTTg8-UHmwCK8BGAsYHg/s0/2020-11-04.png",
//                     "bannerUrl": "https://upyog-assets.s3.ap-south-1.amazonaws.com/bannerImage.png",
//                     "logoUrl": "https://in-egov-assets.s3.ap-south-1.amazonaws.com/nugp.png",
//                     "logoUrlWhite": "https://in-egov-assets.s3.ap-south-1.amazonaws.com/nugp.png",
//                     "statelogo": "https://s3.ap-south-1.amazonaws.com/pg-egov-assets/pg.citya/logo.png",
//                     "hasLocalisation": true,
//                     "defaultUrl": {
//                         "citizen": "/user/register",
//                         "employee": "/user/login"
//                     },
//                     "languages": [
//                         {
//                             "label": "ENGLISH",
//                             "value": "en_IN"
//                         },
//                         {
//                             "label": "हिंदी",
//                             "value": "hi_IN"
//                         }
//                     ],
//                     "localizationModules": [
//                         {
//                             "label": "rainmaker-abg",
//                             "value": "rainmaker-abg"
//                         },
//                         {
//                             "label": "rainmaker-common",
//                             "value": "rainmaker-common"
//                         },
//                         {
//                             "label": "rainmaker-noc",
//                             "value": "rainmaker-noc"
//                         },
//                         {
//                             "label": "rainmaker-pt",
//                             "value": "rainmaker-pt"
//                         },
//                         {
//                             "label": "rainmaker-uc",
//                             "value": "rainmaker-uc"
//                         },
//                         {
//                             "label": "rainmaker-pgr",
//                             "value": "rainmaker-pgr"
//                         },
//                         {
//                             "label": "rainmaker-tl",
//                             "value": "rainmaker-tl"
//                         },
//                         {
//                             "label": "rainmaker-hr",
//                             "value": "rainmaker-hr"
//                         },
//                         {
//                             "label": "rainmaker-test",
//                             "value": "rainmaker-test"
//                         },
//                         {
//                             "label": "finance-erp",
//                             "value": "finance-erp"
//                         },
//                         {
//                             "label": "rainmaker-receipt",
//                             "value": "rainmaker-receipt"
//                         },
//                         {
//                             "label": "rainmaker-dss",
//                             "value": "rainmaker-dss"
//                         },
//                         {
//                             "label": "rainmaker-fsm",
//                             "value": "rainmaker-fsm"
//                         },
//                         {
//                             "label": "rainmaker-workbench",
//                             "value": "rainmaker-workbench"
//                         },
//                         {
//                             "label": "rainmaker-schema",
//                             "value": "rainmaker-schema"
//                         },
//                         {
//                             "label": "rainmaker-mdms",
//                             "value": "rainmaker-mdms"
//                         }
//                     ]
//                 }
//             ],
//             "wfSlaConfig": [
//                 {
//                     "slotPercentage": 33,
//                     "positiveSlabColor": "#4CAF50",
//                     "negativeSlabColor": "#F44336",
//                     "middleSlabColor": "#EEA73A"
//                 }
//             ],
//             "uiHomePage": [
//                 {
//                     "appBannerDesktop": {
//                         "code": "APP_BANNER_DESKTOP",
//                         "name": "App Banner Desktop View",
//                         "bannerUrl": "https://uat.digit.org/egov-uat-assets/app-banner-mobile.jpg",
//                         "enabled": true
//                     },
//                     "appBannerMobile": {
//                         "code": "APP_BANNER_MOBILE",
//                         "name": "App Banner Mobile View",
//                         "bannerUrl": "https://uat.digit.org/egov-uat-assets/app-banner-mobile.jpg",
//                         "enabled": true
//                     },
//                     "citizenServicesCard": {
//                         "code": "HOME_CITIZEN_SERVICES_CARD",
//                         "name": "Home Citizen services Card",
//                         "enabled": true,
//                         "headerLabel": "DASHBOARD_CITIZEN_SERVICES_LABEL",
//                         "sideOption": {
//                             "name": "DASHBOARD_VIEW_ALL_LABEL",
//                             "enabled": true,
//                             "navigationUrl": "/digit-ui/citizen/all-services"
//                         },
//                         "props": [
//                             {
//                                 "code": "CITIZEN_SERVICE_PGR",
//                                 "name": "Complaints",
//                                 "label": "ES_PGR_HEADER_COMPLAINT",
//                                 "enabled": true,
//                                 "navigationUrl": "/digit-ui/citizen/pgr-home"
//                             },
//                             {
//                                 "code": "CITIZEN_SERVICE_PT",
//                                 "name": "Property Tax",
//                                 "label": "MODULE_PT",
//                                 "enabled": true,
//                                 "navigationUrl": "/digit-ui/citizen/pt-home"
//                             },
//                             {
//                                 "code": "CITIZEN_SERVICE_TL",
//                                 "name": "Trade Licence",
//                                 "label": "MODULE_TL",
//                                 "enabled": true,
//                                 "navigationUrl": "/digit-ui/citizen/tl-home"
//                             },
//                             {
//                                 "code": "CITIZEN_SERVICE_WS",
//                                 "name": "Water & Sewerage",
//                                 "label": "ACTION_TEST_WATER_AND_SEWERAGE",
//                                 "enabled": true,
//                                 "navigationUrl": "/digit-ui/citizen/ws-home"
//                             }
//                         ]
//                     },
//                     "informationAndUpdatesCard": {
//                         "code": "HOME_CITIZEN_INFO_UPDATE_CARD",
//                         "name": "Home Citizen Information and Updates card",
//                         "enabled": true,
//                         "headerLabel": "CS_COMMON_DASHBOARD_INFO_UPDATES",
//                         "sideOption": {
//                             "name": "DASHBOARD_VIEW_ALL_LABEL",
//                             "enabled": true,
//                             "navigationUrl": ""
//                         },
//                         "props": [
//                             {
//                                 "code": "CITIZEN_MY_CITY",
//                                 "name": "My City",
//                                 "label": "CS_HEADER_MYCITY",
//                                 "enabled": true,
//                                 "navigationUrl": ""
//                             },
//                             {
//                                 "code": "CITIZEN_EVENTS",
//                                 "name": "Events",
//                                 "label": "EVENTS_EVENTS_HEADER",
//                                 "enabled": true,
//                                 "navigationUrl": "/digit-ui/citizen/engagement/events"
//                             },
//                             {
//                                 "code": "CITIZEN_DOCUMENTS",
//                                 "name": "Documents",
//                                 "label": "CS_COMMON_DOCUMENTS",
//                                 "enabled": true,
//                                 "navigationUrl": "/digit-ui/citizen/engagement/docs"
//                             },
//                             {
//                                 "code": "CITIZEN_SURVEYS",
//                                 "name": "Surveys",
//                                 "label": "CS_COMMON_SURVEYS",
//                                 "enabled": true,
//                                 "navigationUrl": "/digit-ui/citizen/engagement/surveys/list"
//                             }
//                         ]
//                     },
//                     "whatsNewSection": {
//                         "code": "WHATSNEW",
//                         "name": "What's New",
//                         "enabled": true,
//                         "headerLabel": "DASHBOARD_WHATS_NEW_LABEL",
//                         "sideOption": {
//                             "name": "DASHBOARD_VIEW_ALL_LABEL",
//                             "enabled": true,
//                             "navigationUrl": "/digit-ui/citizen/engagement/whats-new"
//                         }
//                     },
//                     "whatsAppBannerDesktop": {
//                         "code": "WHATSAPP_BANNER_DESKTOP",
//                         "name": "WhatsApp Banner Desktop View",
//                         "bannerUrl": "https://uat.digit.org/egov-uat-assets/whatsapp-web.jpg",
//                         "enabled": true,
//                         "navigationUrl": "https://api.whatsapp.com/send?phone=918744060444&text=mSeva"
//                     },
//                     "whatsAppBannerMobile": {
//                         "code": "WHATSAPP_BANNER_MOBILE",
//                         "name": "WhatsApp Banner Mobile View",
//                         "bannerUrl": "https://uat.digit.org/egov-uat-assets/whatsapp-mobile.jpg",
//                         "enabled": true,
//                         "navigationUrl": "https://api.whatsapp.com/send?phone=918744060444&text=mSeva"
//                     }
//                 }
//             ]
//         },
//         "tenant": {
//             "tenants": [
//                 {
//                     "code": "pg.citya",
//                     "name": "City A",
//                     "description": "City A",
//                     "pincode": [
//                         143001,
//                         143002,
//                         143003,
//                         143004,
//                         143005
//                     ],
//                     "logoId": "https://in-egov-assets.s3.ap-south-1.amazonaws.com/in.citya/logo.png",
//                     "imageId": null,
//                     "domainUrl": "https://www.upyog-test.niua.org",
//                     "type": "CITY",
//                     "twitterUrl": null,
//                     "facebookUrl": null,
//                     "emailId": "citya@gmail.com",
//                     "OfficeTimings": {
//                         "Mon - Fri": "9.00 AM - 6.00 PM"
//                     },
//                     "city": {
//                         "name": "City A",
//                         "localName": null,
//                         "districtCode": "CITYA",
//                         "districtName": null,
//                         "districtTenantCode": "pg.citya",
//                         "regionName": null,
//                         "ulbGrade": "Municipal Corporation",
//                         "longitude": 75.5761829,
//                         "latitude": 31.3260152,
//                         "shapeFileLocation": null,
//                         "captcha": null,
//                         "code": "1013",
//                         "ddrName": "DDR A"
//                     },
//                     "address": "City A Municipal Corporation",
//                     "contactNumber": "001-2345876"
//                 },
//                 {
//                     "code": "pg.cityb",
//                     "name": "City B",
//                     "description": null,
//                     "pincode": [
//                         143006,
//                         143007,
//                         143008,
//                         143009,
//                         143010
//                     ],
//                     "logoId": "https://in-egov-assets.s3.ap-south-1.amazonaws.com/in.citya/logo.png",
//                     "imageId": null,
//                     "domainUrl": "https://www.upyog-test.niua.org",
//                     "type": "CITY",
//                     "twitterUrl": null,
//                     "facebookUrl": null,
//                     "emailId": "cityb@gmail.com",
//                     "OfficeTimings": {
//                         "Mon - Fri": "9.00 AM - 6.00 PM",
//                         "Sat": "9.00 AM - 12.00 PM"
//                     },
//                     "city": {
//                         "name": "City B",
//                         "localName": null,
//                         "districtCode": "CITYB",
//                         "districtName": null,
//                         "districtTenantCode": "pg.cityb",
//                         "regionName": null,
//                         "ulbGrade": "Municipal Corporation",
//                         "longitude": 74.8722642,
//                         "latitude": 31.6339793,
//                         "shapeFileLocation": null,
//                         "captcha": null,
//                         "code": "107",
//                         "ddrName": "DDR B"
//                     },
//                     "address": "City B Municipal Corporation Address",
//                     "contactNumber": "0978-7645345",
//                     "helpLineNumber": "0654-8734567"
//                 },
//                 {
//                     "code": "pg.cityc",
//                     "name": "City C",
//                     "description": null,
//                     "logoId": "https://in-egov-assets.s3.ap-south-1.amazonaws.com/in.citya/logo.png",
//                     "imageId": null,
//                     "domainUrl": "https://www.upyog-test.niua.org",
//                     "type": "CITY",
//                     "twitterUrl": null,
//                     "facebookUrl": null,
//                     "emailId": "cityc@gmail.com",
//                     "OfficeTimings": {
//                         "Mon - Fri": "9.00 AM - 6.00 PM",
//                         "Sat": "9.00 AM - 12.00 PM"
//                     },
//                     "city": {
//                         "name": "City C",
//                         "localName": null,
//                         "districtCode": "CITYC",
//                         "districtName": null,
//                         "districtTenantCode": "pg.cityc",
//                         "regionName": null,
//                         "ulbGrade": "Municipal Corporation",
//                         "longitude": 73.8722642,
//                         "latitude": 31.6339793,
//                         "shapeFileLocation": null,
//                         "captcha": null,
//                         "code": "108",
//                         "ddrName": "DDR C"
//                     },
//                     "address": "City C Municipal Corporation Address",
//                     "contactNumber": "0978-7645345",
//                     "helpLineNumber": "0654-8734567"
//                 },
//                 {
//                     "code": "pg.cityd",
//                     "name": "City D",
//                     "description": null,
//                     "logoId": "https://in-egov-assets.s3.ap-south-1.amazonaws.com/in.citya/logo.png",
//                     "imageId": null,
//                     "domainUrl": "https://www.upyog-test.niua.org",
//                     "type": "CITY",
//                     "twitterUrl": null,
//                     "facebookUrl": null,
//                     "emailId": "cityd@gmail.com",
//                     "OfficeTimings": {
//                         "Mon - Fri": "9.00 AM - 6.00 PM",
//                         "Sat": "9.00 AM - 12.00 PM"
//                     },
//                     "city": {
//                         "name": "City D",
//                         "localName": null,
//                         "districtCode": "CITYD",
//                         "districtName": null,
//                         "districtTenantCode": "pg.cityd",
//                         "regionName": null,
//                         "ulbGrade": "Municipal Corporation",
//                         "longitude": 75.8722642,
//                         "latitude": 35.6339793,
//                         "shapeFileLocation": null,
//                         "captcha": null,
//                         "code": "109",
//                         "ddrName": "DDR D"
//                     },
//                     "address": "City D Municipal Corporation Address",
//                     "contactNumber": "0978-7645345",
//                     "helpLineNumber": "0654-8734567"
//                 },
//                 {
//                     "code": "pg.citye",
//                     "name": "City E",
//                     "description": null,
//                     "logoId": "https://in-egov-assets.s3.ap-south-1.amazonaws.com/in.citya/logo.png",
//                     "imageId": null,
//                     "domainUrl": "https://www.upyog-test.niua.org",
//                     "type": "CITY",
//                     "twitterUrl": null,
//                     "facebookUrl": null,
//                     "emailId": "citye@gmail.com",
//                     "OfficeTimings": {
//                         "Mon - Fri": "9.00 AM - 6.00 PM",
//                         "Sat": "9.00 AM - 12.00 PM"
//                     },
//                     "city": {
//                         "name": "City E",
//                         "localName": null,
//                         "districtCode": "CITYE",
//                         "districtName": null,
//                         "districtTenantCode": "pg.citye",
//                         "regionName": null,
//                         "ulbGrade": "Municipal Corporation",
//                         "longitude": 76.8722642,
//                         "latitude": 36.6339793,
//                         "shapeFileLocation": null,
//                         "captcha": null,
//                         "code": "110",
//                         "ddrName": "DDR E"
//                     },
//                     "address": "City E Municipal Corporation Address",
//                     "contactNumber": "0978-7645345",
//                     "helpLineNumber": "0654-8734567"
//                 },
//                 {
//                     "code": "pg",
//                     "name": "State",
//                     "description": "State",
//                     "logoId": "https://s3.ap-south-1.amazonaws.com/pg-egov-assets/pg.citya/logo.png",
//                     "imageId": null,
//                     "domainUrl": "www.upyog-test.niua.org",
//                     "type": "CITY",
//                     "twitterUrl": null,
//                     "facebookUrl": null,
//                     "emailId": "pg.state@gmail.com",
//                     "OfficeTimings": {
//                         "Mon - Fri": "9.00 AM - 5.00 PM"
//                     },
//                     "city": {
//                         "name": "State",
//                         "localName": "Demo State",
//                         "districtCode": "0",
//                         "districtName": "State",
//                         "districtTenantCode": "pg",
//                         "regionName": "State",
//                         "ulbGrade": "ST",
//                         "longitude": 75.3412,
//                         "latitude": 31.1471,
//                         "shapeFileLocation": null,
//                         "captcha": null,
//                         "code": "0",
//                         "ddrName": null
//                     },
//                     "address": "State Municipal Corporation",
//                     "contactNumber": "0978-7645345"
//                 }
//             ],
//             "citymodule": [
//                 {
//                     "module": "PGR",
//                     "code": "PGR",
//                     "bannerImage": "https://egov-uat-assets.s3.amazonaws.com/PGR.png",
//                     "active": true,
//                     "order": 2,
//                     "tenants": [
//                         {
//                             "code": "pg.citya"
//                         },
//                         {
//                             "code": "pg.cityb"
//                         },
//                         {
//                             "code": "pg.cityc"
//                         },
//                         {
//                             "code": "pg.cityd"
//                         },
//                         {
//                             "code": "pg.citye"
//                         }
//                     ]
//                 },
//                 {
//                     "module": "PT",
//                     "code": "PT",
//                     "bannerImage": "https://egov-uat-assets.s3.amazonaws.com/PT.png",
//                     "active": true,
//                     "order": 1,
//                     "tenants": [
//                         {
//                             "code": "pg.citya"
//                         },
//                         {
//                             "code": "pg.cityb"
//                         },
//                         {
//                             "code": "pg.cityc"
//                         },
//                         {
//                             "code": "pg.cityd"
//                         },
//                         {
//                             "code": "pg.citye"
//                         }
//                     ]
//                 },
//                 {
//                     "module": "QuickPayLinks",
//                     "code": "QuickPayLinks",
//                     "active": true,
//                     "order": 1,
//                     "tenants": [
//                         {
//                             "code": "pg.citya"
//                         },
//                         {
//                             "code": "pg.cityb"
//                         },
//                         {
//                             "code": "pg.cityc"
//                         }
//                     ]
//                 },
//                 {
//                     "module": "Finance",
//                     "code": "Finance",
//                     "active": false,
//                     "order": 4,
//                     "tenants": [
//                         {
//                             "code": "pg.citya"
//                         },
//                         {
//                             "code": "pg.cityb"
//                         },
//                         {
//                             "code": "pg.cityc"
//                         },
//                         {
//                             "code": "pg.cityd"
//                         },
//                         {
//                             "code": "pg.citye"
//                         }
//                     ]
//                 },
//                 {
//                     "module": "TL",
//                     "code": "TL",
//                     "bannerImage": "https://egov-uat-assets.s3.amazonaws.com/TL.png",
//                     "active": true,
//                     "order": 2,
//                     "tenants": [
//                         {
//                             "code": "pg.citya"
//                         },
//                         {
//                             "code": "pg.cityb"
//                         },
//                         {
//                             "code": "pg.cityc"
//                         },
//                         {
//                             "code": "pg.cityd"
//                         },
//                         {
//                             "code": "pg.citye"
//                         }
//                     ]
//                 },
//                 {
//                     "module": "FireNoc",
//                     "code": "FireNoc",
//                     "active": true,
//                     "order": 2,
//                     "tenants": [
//                         {
//                             "code": "pg.citya"
//                         },
//                         {
//                             "code": "pg.cityb"
//                         },
//                         {
//                             "code": "pg.cityc"
//                         },
//                         {
//                             "code": "pg.cityd"
//                         },
//                         {
//                             "code": "pg.citye"
//                         }
//                     ]
//                 },
//                 {
//                     "module": "UC",
//                     "code": "UC",
//                     "active": false,
//                     "order": 2,
//                     "tenants": [
//                         {
//                             "code": "pg.citya"
//                         },
//                         {
//                             "code": "pg.cityb"
//                         },
//                         {
//                             "code": "pg.cityc"
//                         },
//                         {
//                             "code": "pg.cityd"
//                         },
//                         {
//                             "code": "pg.citye"
//                         }
//                     ]
//                 },
//                 {
//                     "module": "BPAREG",
//                     "code": "BPAREG",
//                     "bannerImage": "https://egov-uat-assets.s3.amazonaws.com/OBPS.png",
//                     "active": true,
//                     "order": 2,
//                     "tenants": [
//                         {
//                             "code": "pg.citya"
//                         },
//                         {
//                             "code": "pg.cityb"
//                         },
//                         {
//                             "code": "pg.cityc"
//                         },
//                         {
//                             "code": "pg.cityd"
//                         },
//                         {
//                             "code": "pg.citye"
//                         }
//                     ]
//                 },
//                 {
//                     "module": "BPAAPPLY",
//                     "code": "BPAAPPLY",
//                     "bannerImage": "https://egov-uat-assets.s3.amazonaws.com/OBPS.png",
//                     "active": true,
//                     "order": 2,
//                     "tenants": [
//                         {
//                             "code": "pg.citya"
//                         },
//                         {
//                             "code": "pg.cityb"
//                         },
//                         {
//                             "code": "pg.cityc"
//                         },
//                         {
//                             "code": "pg.cityd"
//                         },
//                         {
//                             "code": "pg.citye"
//                         }
//                     ]
//                 },
//                 {
//                     "module": "PGR.WHATSAPP",
//                     "code": "PGR.WHATSAPP",
//                     "active": false,
//                     "order": 4,
//                     "tenants": [
//                         {
//                             "code": "pg.citya"
//                         }
//                     ]
//                 },
//                 {
//                     "module": "OBPS",
//                     "code": "OBPS",
//                     "bannerImage": "https://egov-uat-assets.s3.amazonaws.com/OBPS.png",
//                     "active": true,
//                     "order": 2,
//                     "tenants": [
//                         {
//                             "code": "pg.citya"
//                         },
//                         {
//                             "code": "pg.cityb"
//                         },
//                         {
//                             "code": "pg.cityc"
//                         },
//                         {
//                             "code": "pg.cityd"
//                         },
//                         {
//                             "code": "pg.citye"
//                         }
//                     ]
//                 },
//                 {
//                     "module": "FSM",
//                     "code": "FSM",
//                     "bannerImage": "https://egov-uat-assets.s3.amazonaws.com/FSM.png",
//                     "active": true,
//                     "order": 2,
//                     "tenants": [
//                         {
//                             "code": "pg.citya"
//                         },
//                         {
//                             "code": "pg.cityb"
//                         },
//                         {
//                             "code": "pg.cityc"
//                         },
//                         {
//                             "code": "pg.cityd"
//                         },
//                         {
//                             "code": "pg.citye"
//                         }
//                     ]
//                 },
//                 {
//                     "module": "Payment",
//                     "code": "Payment",
//                     "active": true,
//                     "order": 1,
//                     "tenants": [
//                         {
//                             "code": "pg.citya"
//                         },
//                         {
//                             "code": "pg.cityb"
//                         },
//                         {
//                             "code": "pg.cityc"
//                         },
//                         {
//                             "code": "pg.cityd"
//                         },
//                         {
//                             "code": "pg.citye"
//                         }
//                     ]
//                 },
//                 {
//                     "module": "Receipts",
//                     "code": "Receipts",
//                     "active": true,
//                     "order": 3,
//                     "tenants": [
//                         {
//                             "code": "pg.citya"
//                         },
//                         {
//                             "code": "pg.cityb"
//                         },
//                         {
//                             "code": "pg.cityc"
//                         },
//                         {
//                             "code": "pg.cityd"
//                         },
//                         {
//                             "code": "pg.citye"
//                         }
//                     ]
//                 },
//                 {
//                     "module": "NOC",
//                     "code": "NOC",
//                     "active": true,
//                     "order": 2,
//                     "tenants": [
//                         {
//                             "code": "pg.citya"
//                         },
//                         {
//                             "code": "pg.cityb"
//                         },
//                         {
//                             "code": "pg.cityc"
//                         },
//                         {
//                             "code": "pg.cityd"
//                         },
//                         {
//                             "code": "pg.citye"
//                         }
//                     ]
//                 },
//                 {
//                     "module": "DSS",
//                     "code": "DSS",
//                     "active": true,
//                     "order": 6,
//                     "tenants": [
//                         {
//                             "code": "pg.citya"
//                         },
//                         {
//                             "code": "pg.cityb"
//                         },
//                         {
//                             "code": "pg.cityc"
//                         },
//                         {
//                             "code": "pg.cityd"
//                         },
//                         {
//                             "code": "pg.citye"
//                         }
//                     ]
//                 },
//                 {
//                     "module": "Engagement",
//                     "code": "Engagement",
//                     "active": true,
//                     "order": 3,
//                     "tenants": [
//                         {
//                             "code": "pg.citya"
//                         },
//                         {
//                             "code": "pg.cityb"
//                         },
//                         {
//                             "code": "pg.cityc"
//                         },
//                         {
//                             "code": "pg.cityd"
//                         },
//                         {
//                             "code": "pg.citye"
//                         }
//                     ]
//                 },
//                 {
//                     "module": "MCollect",
//                     "code": "MCollect",
//                     "active": true,
//                     "order": 1,
//                     "tenants": [
//                         {
//                             "code": "pg.citya"
//                         },
//                         {
//                             "code": "pg.cityb"
//                         },
//                         {
//                             "code": "pg.cityc"
//                         },
//                         {
//                             "code": "pg.cityd"
//                         },
//                         {
//                             "code": "pg.citye"
//                         }
//                     ]
//                 },
//                 {
//                     "module": "HRMS",
//                     "code": "HRMS",
//                     "active": true,
//                     "order": 2,
//                     "tenants": [
//                         {
//                             "code": "pg.citya"
//                         },
//                         {
//                             "code": "pg.cityb"
//                         },
//                         {
//                             "code": "pg.cityc"
//                         },
//                         {
//                             "code": "pg.cityd"
//                         },
//                         {
//                             "code": "pg.citye"
//                         }
//                     ]
//                 },
//                 {
//                     "module": "CommonPT",
//                     "code": "CommonPT",
//                     "active": true,
//                     "order": 3,
//                     "tenants": [
//                         {
//                             "code": "pg.citya"
//                         },
//                         {
//                             "code": "pg.cityb"
//                         },
//                         {
//                             "code": "pg.cityc"
//                         },
//                         {
//                             "code": "pg.cityd"
//                         },
//                         {
//                             "code": "pg.citye"
//                         }
//                     ]
//                 },
//                 {
//                     "module": "NDSS",
//                     "code": "NDSS",
//                     "active": true,
//                     "order": 5,
//                     "tenants": [
//                         {
//                             "code": "pg.citya"
//                         },
//                         {
//                             "code": "pg.cityb"
//                         },
//                         {
//                             "code": "pg.cityc"
//                         },
//                         {
//                             "code": "pg.cityd"
//                         },
//                         {
//                             "code": "pg.citye"
//                         }
//                     ]
//                 },
//                 {
//                     "module": "WS",
//                     "code": "WS",
//                     "bannerImage": "https://egov-uat-assets.s3.amazonaws.com/WS.png",
//                     "active": true,
//                     "order": 1,
//                     "tenants": [
//                         {
//                             "code": "pg.citya"
//                         },
//                         {
//                             "code": "pg.cityb"
//                         },
//                         {
//                             "code": "pg.cityc"
//                         }
//                     ]
//                 },
//                 {
//                     "module": "SW",
//                     "code": "SW",
//                     "active": true,
//                     "order": 1,
//                     "tenants": [
//                         {
//                             "code": "pg.citya"
//                         },
//                         {
//                             "code": "pg.cityb"
//                         },
//                         {
//                             "code": "pg.cityc"
//                         }
//                     ]
//                 },
//                 {
//                     "module": "BillAmendment",
//                     "code": "BillAmendment",
//                     "active": true,
//                     "order": 1,
//                     "tenants": [
//                         {
//                             "code": "pg.citya"
//                         },
//                         {
//                             "code": "pg.cityb"
//                         },
//                         {
//                             "code": "pg.cityc"
//                         }
//                     ]
//                 },
//                 {
//                     "module": "Bills",
//                     "code": "Bills",
//                     "bannerImage": "https://egov-uat-assets.s3.amazonaws.com/Bill.png",
//                     "active": true,
//                     "order": 3,
//                     "tenants": [
//                         {
//                             "code": "pg.citya"
//                         },
//                         {
//                             "code": "pg.cityb"
//                         },
//                         {
//                             "code": "pg.cityc"
//                         }
//                     ]
//                 },
//                 {
//                     "module": "Birth",
//                     "code": "Birth",
//                     "bannerImage": "https://egov-uat-assets.s3.amazonaws.com/PT.png",
//                     "active": true,
//                     "order": 1,
//                     "tenants": [
//                         {
//                             "code": "pg.citya"
//                         },
//                         {
//                             "code": "pg.cityb"
//                         },
//                         {
//                             "code": "pg.cityc"
//                         },
//                         {
//                             "code": "pg.cityd"
//                         },
//                         {
//                             "code": "pg.citye"
//                         }
//                     ]
//                 },
//                 {
//                     "module": "Death",
//                     "code": "Death",
//                     "bannerImage": "https://egov-uat-assets.s3.amazonaws.com/PT.png",
//                     "active": true,
//                     "order": 1,
//                     "tenants": [
//                         {
//                             "code": "pg.citya"
//                         },
//                         {
//                             "code": "pg.cityb"
//                         },
//                         {
//                             "code": "pg.cityc"
//                         },
//                         {
//                             "code": "pg.cityd"
//                         },
//                         {
//                             "code": "pg.citye"
//                         }
//                     ]
//                 },
//                 {
//                     "module": "Workbench",
//                     "code": "Workbench",
//                     "active": true,
//                     "order": 13,
//                     "tenants": [
//                         {
//                             "code": "pg.citya"
//                         },
//                         {
//                             "code": "pg.cityb"
//                         },
//                         {
//                             "code": "pg.cityc"
//                         },
//                         {
//                             "code": "pg.cityd"
//                         },
//                         {
//                             "code": "pg.citye"
//                         }
//                     ]
//                 }
//             ]
//         },
//         "DIGIT-UI": {}
//     }
// }]
//   return (
//     <React.Fragment>
//       <div className="filter">
//         <div className="filter-card">
//           <div className="heading">
//             <div className="filter-label" style={{ display: "flex", alignItems: "center" }}>
//               <span>
//                 <svg width="17" height="17" viewBox="0 0 22 22" fill="none" xmlns="http://www.w3.org/2000/svg">
//                   <path
//                     d="M0.66666 2.48016C3.35999 5.9335 8.33333 12.3335 8.33333 12.3335V20.3335C8.33333 21.0668 8.93333 21.6668 9.66666 21.6668H12.3333C13.0667 21.6668 13.6667 21.0668 13.6667 20.3335V12.3335C13.6667 12.3335 18.6267 5.9335 21.32 2.48016C22 1.60016 21.3733 0.333496 20.2667 0.333496H1.71999C0.613327 0.333496 -0.01334 1.60016 0.66666 2.48016Z"
//                     fill="#505A5F"
//                   />
//                 </svg>
//               </span>
//               <span>{t("WMS_COMMON_FILTER")}:</span>{" "}
//             </div>
//             <div className="clearAll" onClick={clearAll}>
//               {t("WMS_COMMON_CLEAR_ALL")}
//             </div>
//             {props.type === "desktop" && (
//               <span className="clear-search" onClick={clearAll} style={{ border: "1px solid #e0e0e0", padding: "6px" }}>
//                 <svg width="17" height="17" viewBox="0 0 16 22" fill="none" xmlns="http://www.w3.org/2000/svg">
//                   <path
//                     d="M8 5V8L12 4L8 0V3C3.58 3 0 6.58 0 11C0 12.57 0.46 14.03 1.24 15.26L2.7 13.8C2.25 12.97 2 12.01 2 11C2 7.69 4.69 5 8 5ZM14.76 6.74L13.3 8.2C13.74 9.04 14 9.99 14 11C14 14.31 11.31 17 8 17V14L4 18L8 22V19C12.42 19 16 15.42 16 11C16 9.43 15.54 7.97 14.76 6.74Z"
//                     fill="#505A5F"
//                   />
//                 </svg>
//               </span>
//             )}
//             {props.type === "mobile" && (
//               <span onClick={props.onClose}>
//                 <CloseSvg />
//               </span>
//             )}
//           </div>
//           <div>
//             <div>
//               <div className="filter-label">{t("WMS_ULB_LABEL")}</div>
//               <Dropdown
//                 // option={[...getCityThatUserhasAccess(tenantIds)?.sort((x, y) => x?.name?.localeCompare(y?.name)).map(city => { return { ...city, i18text: Digit.Utils.locale.getCityLocale(city.code) } })]}
//                 option={[...getCityThatUserhasAccess(ULBData)?.sort((x, y) => x?.name?.localeCompare(y?.name)).map(city => { return { ...city, i18text: Digit.Utils.locale.getCityLocale(city.code) } })]}
//                 // option={}ULBData
//                 selected={tenantId}
//                 select={settenantId}
//                 optionKey={"i18text"}
//                 t={t}
//               />
//             </div>
//             <div>
//               <div className="filter-label">{t("WMS_COMMON_TABLE_COL_DEPT")}</div>
//               <Dropdown
//                 option={Digit.Utils.locale.convertToLocaleData(data?.MdmsRes?.["common-masters"]?.Department, 'COMMON_MASTERS_DEPARTMENT')}
//                 selected={departments}
//                 select={setDepartments}
//                 optionKey={"i18text"}
//                 t={t}
//               />
//             </div>
//             <div>
//               <div>
//                 {GetSelectOptions(
//                   t("WMS_COMMON_TABLE_COL_ROLE"),
//                   Digit.Utils.locale.convertToLocaleData(data?.MdmsRes["ACCESSCONTROL-ROLES"]?.roles, 'ACCESSCONTROL_ROLES_ROLES', t),
//                   selectedRoles,
//                   onSelectRoles,
//                   "i18text",
//                   onRemove,
//                   "role"
//                 )}
//               </div>
//             </div>
//             <div>
//               <div className="filter-label">{t("WMS_EMP_STATUS_LABEL")}</div>
//               <RadioButtons
//                 onSelect={setIsactive}
//                 selected={isActive}
//                 selectedOption={isActive}
//                 optionsKey="name"
//                 options={[
//                   { code: true, name: t("WMS_ACTIVATE_HEAD") },
//                   { code: false, name: t("WMS_DEACTIVATE_HEAD") },
//                 ]}
//               />
//               {props.type !== "mobile" && <div>
//                 <SubmitBar onSubmit={() => onFilterChange(_searchParams)} label={t("WMS_COMMON_APPLY")} />
//               </div>}
//             </div>
//           </div>
//         </div>
//       </div>
//       {props.type === "mobile" && (
//         <ActionBar>
//           <ApplyFilterBar
//             submit={false}
//             labelLink={t("WMS_COMMON_CLEAR_ALL")}
//             buttonLink={t("WMS_COMMON_FILTER")}
//             onClear={clearAll}
//             onSubmit={() => {
//               onFilterChange(_searchParams)
//               props?.onClose?.()
//             }}
//             style={{ flex: 1 }}
//           />
//         </ActionBar>
//       )}
//     </React.Fragment>
//   );
// };

// export default Filter;




// #########################
import React, { useState, useEffect } from "react";
import { Dropdown, CloseSvg, SubmitBar, Loader } from "@egovernments/digit-ui-react-components";
import { useTranslation } from "react-i18next";
import _ from "lodash";

const SearchCitizenFilter = ({ searchParams, onFilterChange, defaultSearchParams, statuses, ...props }) => {
  const { t } = useTranslation();

  const [_searchParams, setSearchParams] = useState(() => searchParams);
  const [service, setService] = useState([]);
  const [ulbLists, setulbLists] = useState([]);
  const allCities = Digit.Hooks.useTenantsWMS()?.sort((a, b) => a?.i18nKey?.localeCompare?.(b?.i18nKey));
  console.log("allCities ",{allCities,service,ulbLists})
  let cityList = [];
  let tenantId = Digit.SessionStorage.get("User")?.info?.tenantId;

  const clearAll = () => {
    setService([]);
    setulbLists([]);
    onFilterChange([]);
  };

  useEffect(() => {
    if (service && ulbLists) {
      // setSearchParams({ ...service, tenantId: ulbLists.tenantId });
      setSearchParams({ ...service, vendor_type: "Vendor Type 2",vendor_name: "", });
      
    }
  }, [service, ulbLists]);

  const { isLoading, data: generateServiceType } = Digit.Hooks.useCommonMDMS(tenantId, "BillingService", "BillsGenieKey");

  const filterServiceType = generateServiceType?.BillingService?.BusinessService?.filter((element) => element.billGineiURL);

  let serviceTypeList = [];
  if (filterServiceType) {
    serviceTypeList = filterServiceType.map((element) => {
      return {
        name: Digit.Utils.locale.getTransformedLocale(`BILLINGSERVICE_BUSINESSSERVICE_${element.code}`),
        url: element.billGineiURL,
        businesService: element.code,
      };
    });
  }
  if (allCities) {
    cityList = allCities.map((element) => {
      return {
        tenantId: element.code,
      };
    });
  }

  if (isLoading) {
    return <Loader />;
  }

  return (
    <React.Fragment>
      <div style={{marginTop:"16px!important"}}>
      <div className="filter citizen-filter">
        <div className="filter-card">
          <div className="heading" style={{ alignItems: "center" }}>
            <div className="filter-label" style={{ display: "flex", alignItems: "center" }}>
              <span>
                <svg width="17" height="17" viewBox="0 0 22 22" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path
                    d="M0.66666 2.48016C3.35999 5.9335 8.33333 12.3335 8.33333 12.3335V20.3335C8.33333 21.0668 8.93333 21.6668 9.66666 21.6668H12.3333C13.0667 21.6668 13.6667 21.0668 13.6667 20.3335V12.3335C13.6667 12.3335 18.6267 5.9335 21.32 2.48016C22 1.60016 21.3733 0.333496 20.2667 0.333496H1.71999C0.613327 0.333496 -0.01334 1.60016 0.66666 2.48016Z"
                    fill="#505A5F"
                  />
                </svg>
              </span>
              <span style={{ marginLeft: "8px", fontWeight: "normal" }}>{t("ES_COMMON_FILTER_BY")}:</span>
            </div>
            <div className="clearAll" onClick={clearAll}>
              {t("ES_COMMON_CLEAR_ALL")}
            </div>
            {props.type === "desktop" && (
              <span className="clear-search" onClick={clearAll} style={{ border: "1px solid #e0e0e0", padding: "6px" }}>
                <svg width="17" height="17" viewBox="0 0 16 22" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path
                    d="M8 5V8L12 4L8 0V3C3.58 3 0 6.58 0 11C0 12.57 0.46 14.03 1.24 15.26L2.7 13.8C2.25 12.97 2 12.01 2 11C2 7.69 4.69 5 8 5ZM14.76 6.74L13.3 8.2C13.74 9.04 14 9.99 14 11C14 14.31 11.31 17 8 17V14L4 18L8 22V19C12.42 19 16 15.42 16 11C16 9.43 15.54 7.97 14.76 6.74Z"
                    fill="#505A5F"
                  />
                </svg>
              </span>
            )}
            {props.type === "mobile" && (
              <span onClick={props.onClose}>
                <CloseSvg />
              </span>
            )}
          </div>
          <div>
            <div>
              <div className="filter-label">{t("LABEL_FOR_ULB")}</div>
              <Dropdown 
              option={cityList} 
              optionKey="tenantId" value={ulbLists} select={setulbLists} t={t} />
            </div>
            <div>
              <div className="filter-label">{t("ABG_SERVICE_CATEGORY_LABEL")}</div>
              <Dropdown t={t} 
              option={cityList} 
              // option={serviceTypeList}
               value={service} selected={service} select={setService} 
               optionKey={"tenantId"}
              //  optionKey={"name"}
                />
            </div>

            <div>
              <SubmitBar
                disabled={_.isEqual(_searchParams, searchParams)}
                onSubmit={() => onFilterChange(_searchParams)}
                label={t("ES_COMMON_APPLY")}
              />
            </div>
          </div>
        </div>
      </div>
      </div>
    </React.Fragment>
  );
};

export default SearchCitizenFilter;


