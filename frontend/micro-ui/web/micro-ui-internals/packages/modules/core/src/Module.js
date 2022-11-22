import React from "react";
import { QueryClient, QueryClientProvider } from "react-query";
import { Provider } from "react-redux";
import { BrowserRouter as Router } from "react-router-dom";
import { getI18n } from "react-i18next";
import { Body, Loader } from "@egovernments/digit-ui-react-components";
import { DigitApp } from "./App";
import SelectOtp from './pages/citizen/Login/SelectOtp';

import getStore from "./redux/store";
import ErrorBoundary from "./components/ErrorBoundaries";

const DigitUIWrapper = ({ stateCode, enabledModules, moduleReducers }) => {
  const { isLoading, data: initData } = Digit.Hooks.useInitStore(stateCode, enabledModules);
console.log('init api',initData);
const tmpInitData ={
  "languages": [
      {
          "label": "ENGLISH",
          "value": "en_IN"
      },
      {
          "label": "हिंदी",
          "value": "hi_IN"
      },
      {
          "label": "മലയാളം",
          "value": "ml_IN"
      }
  ],
  "stateInfo": {
      "code": "kl",
      "name": "Kerala",
      "logoUrl": "https://in-egov-assets.s3.ap-south-1.amazonaws.com/nugp.png",
      "statelogo": "https://in-egov-assets.s3.ap-south-1.amazonaws.com/nugp.png",
      "logoUrlWhite": "hhttps://in-egov-assets.s3.ap-south-1.amazonaws.com/nugp.png",
      "bannerUrl": "https://s3.ap-south-1.amazonaws.com/ikm-egov-assets/Kerala.png"
  },
  "localizationModules": [
      {
          "label": "rainmaker-abg",
          "value": "rainmaker-abg"
      },
      {
          "label": "rainmaker-common",
          "value": "rainmaker-common"
      },
      {
          "label": "rainmaker-noc",
          "value": "rainmaker-noc"
      },
      {
          "label": "rainmaker-pt",
          "value": "rainmaker-pt"
      },
      {
          "label": "rainmaker-uc",
          "value": "rainmaker-uc"
      },
      {
          "label": "rainmaker-pgr",
          "value": "rainmaker-pgr"
      },
      {
          "label": "rainmaker-fsm",
          "value": "rainmaker-fsm"
      },
      {
          "label": "rainmaker-tl",
          "value": "rainmaker-tl"
      },
      {
          "label": "rainmaker-hr",
          "value": "rainmaker-hr"
      },
      {
          "label": "rainmaker-test",
          "value": "rainmaker-test"
      },
      {
          "label": "finance-erp",
          "value": "finance-erp"
      },
      {
          "label": "rainmaker-receipt",
          "value": "rainmaker-receipt"
      },
      {
          "label": "rainmaker-dss",
          "value": "rainmaker-dss"
      }
  ],
  "modules": [
      {
          "module": "QuickPayLinks",
          "code": "QuickPayLinks",
          "active": true,
          "order": 1,
          "tenants": [
              {
                  "code": "kl.cochin"
              },
              {
                  "code": "kl.kollam"
              },
              {
                  "code": "kl.kannur"
              }
          ]
      },
      {
          "module": "Payment",
          "code": "Payment",
          "active": true,
          "order": 1,
          "tenants": [
              {
                  "code": "kl.cochin"
              },
              {
                  "code": "kl.kollam"
              },
              {
                  "code": "kl.kannur"
              },
              {
                  "code": "kl.thrissur"
              },
              {
                  "code": "kl.thiruvananthapuram"
              }
          ]
      },
      {
          "module": "PGR",
          "code": "PGR",
          "bannerImage": "https://egov-uat-assets.s3.amazonaws.com/PGR.png",
          "active": true,
          "order": 2,
          "tenants": [
              {
                  "code": "kl.cochin"
              },
              {
                  "code": "kl.kollam"
              },
              {
                  "code": "kl.kannur"
              },
              {
                  "code": "kl.kozhikode"
              },
              {
                  "code": "kl.thiruvananthapuram"
              }
          ]
      },
      {
          "module": "TL",
          "code": "TL",
          "bannerImage": "https://egov-uat-assets.s3.amazonaws.com/TL.png",
          "active": true,
          "order": 2,
          "tenants": [
              {
                  "code": "kl.cochin"
              },
              {
                  "code": "kl.kollam"
              },
              {
                  "code": "kl.kannur"
              },
              {
                  "code": "kl.kozhikode"
              },
              {
                  "code": "kl.thiruvananthapuram"
              }
          ]
      },
      {
        "module": "DFM",
        "code": "DFM",
        "bannerImage": "https://egov-uat-assets.s3.amazonaws.com/TL.png",
        "active": true,
        "order": 2,
        "tenants": [
            {
                "code": "kl.cochin"
            },
            {
                "code": "kl.kollam"
            },
            {
                "code": "kl.kannur"
            },
            {
                "code": "kl.kozhikode"
            },
            {
                "code": "kl.thiruvananthapuram"
            }
        ]
    },
      {
          "module": "HRMS",
          "code": "HRMS",
          "active": true,
          "order": 2,
          "tenants": [
              {
                  "code": "kl.cochin"
              },
              {
                  "code": "kl.kollam"
              },
              {
                  "code": "kl.kannur"
              },
              {
                  "code": "kl.kozhikode"
              },
              {
                  "code": "kl.thiruvananthapuram"
              }
          ]
      },
      {
          "module": "Receipts",
          "code": "Receipts",
          "active": true,
          "order": 3,
          "tenants": [
              {
                  "code": "kl.cochin"
              },
              {
                  "code": "kl.kollam"
              },
              {
                  "code": "kl.kannur"
              },
              {
                  "code": "kl.thrissur"
              },
              {
                  "code": "kl.thiruvananthapuram"
              }
          ]
      },
      {
          "module": "Engagement",
          "code": "Engagement",
          "active": true,
          "order": 3,
          "tenants": [
              {
                  "code": "kl.cochin"
              },
              {
                  "code": "kl.kollam"
              },
              {
                  "code": "kl.kannur"
              },
              {
                  "code": "kl.thrissur"
              },
              {
                  "code": "kl.thiruvananthapuram"
              }
          ]
      },
      {
          "module": "NDSS",
          "code": "NDSS",
          "active": true,
          "order": 5,
          "tenants": [
              {
                  "code": "kl.cochin"
              },
              {
                  "code": "kl.kollam"
              },
              {
                  "code": "kl.kannur"
              },
              {
                  "code": "kl.thrissur"
              },
              {
                  "code": "kl.thiruvananthapuram"
              }
          ]
      },
      {
          "module": "DSS",
          "code": "DSS",
          "active": true,
          "order": 6,
          "tenants": [
              {
                  "code": "kl.cochin"
              },
              {
                  "code": "kl.kollam"
              },
              {
                  "code": "kl.kannur"
              },
              {
                  "code": "kl.thrissur"
              },
              {
                  "code": "kl.thiruvananthapuram"
              }
          ]
      }
  ],
  "districts": [
      {
          "name": "Thiruvananthapuram",
          "localname": "തിരുവനന്തപുരം",
          "code": "DIST_TVM",
          "stateid": 32,
          "districtid": 1,
          "active": "true"
      },
      {
          "name": "Kollam",
          "localname": "കൊല്ലം",
          "code": "DIST_KLM",
          "stateid": 32,
          "districtid": 2,
          "active": "true"
      },
      {
          "name": "Pathanamthitta",
          "localname": "പത്തനംതിട്ട",
          "code": "DIST_PTA",
          "stateid": 32,
          "districtid": 3,
          "active": "true"
      },
      {
          "name": "Alappuzha",
          "localname": "ആലപ്പുഴ",
          "code": "DIST_APL",
          "stateid": 32,
          "districtid": 4,
          "active": "true"
      },
      {
          "name": "Kottayam",
          "localname": "കോട്ടയം",
          "code": "DIST_KTM",
          "stateid": 32,
          "districtid": 5,
          "active": "true"
      },
      {
          "name": "Idukki",
          "localname": "ഇടുക്കി",
          "code": "DIST_IDK",
          "stateid": 32,
          "districtid": 6,
          "active": "true"
      },
      {
          "name": "Ernakulam",
          "localname": "എറണാകുളം",
          "code": "DIST_EKM",
          "stateid": 32,
          "districtid": 7,
          "active": "true"
      },
      {
          "name": "Thrissur",
          "localname": "തൃശ്ശൂര്‍",
          "code": "DIST_TSR",
          "stateid": 32,
          "districtid": 8,
          "active": "true"
      },
      {
          "name": "Palakkad",
          "localname": "പാലക്കാട്",
          "code": "DIST_PKD",
          "stateid": 32,
          "districtid": 9,
          "active": "true"
      },
      {
          "name": "Malappuram",
          "localname": "മലപ്പുറം",
          "code": "DIST_MLP",
          "stateid": 32,
          "districtid": 10,
          "active": "true"
      },
      {
          "name": "Kozhikode",
          "localname": "കോഴിക്കോട്",
          "code": "DIST_KZD",
          "stateid": 32,
          "districtid": 11,
          "active": "true"
      },
      {
          "name": "Wayanad",
          "localname": "വയനാട്",
          "code": "DIST_WND",
          "stateid": 32,
          "districtid": 12,
          "active": "true"
      },
      {
          "name": "Kannur",
          "localname": "കണ്ണൂര്‍",
          "code": "DIST_KNR",
          "stateid": 32,
          "districtid": 13,
          "active": "true"
      },
      {
          "name": "Kasaragod",
          "localname": "കാസര്‍കോഡ്",
          "code": "DIST_KSD",
          "stateid": 32,
          "districtid": 14,
          "active": "true"
      },
      {
          "name": "Kasaragod",
          "localname": "കാസര്‍കോഡ്",
          "code": "DIST_KSD",
          "stateid": 32,
          "districtid": 14,
          "active": "true"
      }
  ],
  "selectedLanguage": "en_IN",
  "tenants": [
      {
          "i18nKey": "TENANT_TENANTS_KL_COCHIN",
          "code": "kl.cochin",
          "name": "Cochin Corporation",
          "description": "Cochin",
          "logoId": "https://ulb-logos.s3.ap-south-1.amazonaws.com/cochin/cochin.jpg",
          "imageId": null,
          "domainUrl": "cochinmunicipalcorporation.kerala.gov.in",
          "twitterUrl": null,
          "facebookUrl": null,
          "emailId": "",
          "OfficeTimings": {
              "Mon - Fri": "9.00 AM - 6.00 PM"
          },
          "city": {
              "name": "Cochin Corporation",
              "localName": "കൊച്ചി കോര്‍പ്പറേഷന്‍",
              "districtCode": "555",
              "districtName": "Ernakulam",
              "regionName": "Kerala",
              "ulbGrade": "Corporation",
              "longitude": null,
              "latitude": null,
              "captcha": null,
              "shapeFileLocation": null,
              "code": "C070100",
              "ddrName": "Cochin"
          },
          "address": "Kochi Municipal Corporation\nPB No-1016, Cochin\nErnakulam Dt-Kerala State\nPin - 682011",
          "pincode": [],
          "contactNumber": "4842369007",
          "pdfHeader": "",
          "pdfContactDetails": ""
      },
      {
          "i18nKey": "TENANT_TENANTS_KL_KANNUR",
          "code": "kl.kannur",
          "name": "Kannur Corporation",
          "description": "Kannur",
          "logoId": "https://ulb-logos.s3.ap-south-1.amazonaws.com/kannur/kannur.jpeg",
          "imageId": null,
          "domainUrl": "kannurcorporation.lsgkerala.gov.in",
          "twitterUrl": null,
          "facebookUrl": null,
          "emailId": "",
          "OfficeTimings": {
              "Mon - Fri": "9.00 AM - 6.00 PM"
          },
          "city": {
              "name": "Kannur Corporation",
              "localName": "കണ്ണൂര്‍ കോര്‍പ്പറേഷന്‍",
              "districtCode": "557",
              "districtName": "Kannur",
              "regionName": "Kerala",
              "ulbGrade": "Corporation",
              "longitude": null,
              "latitude": null,
              "captcha": null,
              "shapeFileLocation": null,
              "code": "C130100",
              "ddrName": "Kannur"
          },
          "address": "Kannur Municipal Corporation \nP B No: 39\nPin - 670001\nKannur District",
          "pincode": [],
          "contactNumber": "4972700141",
          "pdfHeader": "",
          "pdfContactDetails": ""
      },
      {
          "i18nKey": "TENANT_TENANTS_KL_KOLLAM",
          "code": "kl.kollam",
          "name": "Kollam Corporation",
          "description": "Kollam",
          "logoId": "https://ulb-logos.s3.ap-south-1.amazonaws.com/kollam/kollam.jpg",
          "imageId": null,
          "domainUrl": "www.kollamcorporation.gov.in",
          "twitterUrl": null,
          "facebookUrl": null,
          "emailId": "",
          "OfficeTimings": {
              "Mon - Fri": "9.00 AM - 6.00 PM"
          },
          "city": {
              "name": "Kollam Corporation",
              "localName": "കൊല്ലം കോര്‍പ്പറേഷന്‍",
              "districtCode": "559",
              "districtName": "Kollam",
              "regionName": "Kerala",
              "ulbGrade": "Corporation",
              "longitude": null,
              "latitude": null,
              "captcha": null,
              "shapeFileLocation": null,
              "code": "C020100",
              "ddrName": "Kollam"
          },
          "address": "Kollamcorporation\nKollam P O\nPin 691001",
          "pincode": [],
          "contactNumber": "4742742192",
          "pdfHeader": "",
          "pdfContactDetails": ""
      },
      {
          "i18nKey": "TENANT_TENANTS_KL_KOZHIKODE",
          "code": "kl.kozhikode",
          "name": "Kozhikode Corporation",
          "description": "Kozhikode",
          "logoId": "",
          "imageId": null,
          "domainUrl": "kozhikodecorporation.lsgkerala.gov.in",
          "twitterUrl": null,
          "facebookUrl": null,
          "emailId": "",
          "OfficeTimings": {
              "Mon - Fri": "9.00 AM - 6.00 PM"
          },
          "city": {
              "name": "Kozhikode Corporation",
              "localName": "കോഴിക്കോട് കോര്‍പ്പറേഷന്‍",
              "districtCode": "561",
              "districtName": "Kozhikode",
              "regionName": "Kerala",
              "ulbGrade": "Corporation",
              "longitude": null,
              "latitude": null,
              "captcha": null,
              "shapeFileLocation": null,
              "code": "C110100",
              "ddrName": "Kozhikode"
          },
          "address": "Kozhikode Municipal Corporation\nCalicut Beach\nNear Akashvani\nKozhikode Dt-Kerala State",
          "pincode": [],
          "contactNumber": "4952365040",
          "pdfHeader": "",
          "pdfContactDetails": ""
      },
      {
          "i18nKey": "TENANT_TENANTS_KL_THIRUVANANTHAPURAM",
          "code": "kl.thiruvananthapuram",
          "name": "Thiruvananthapuram Corporation",
          "description": "Thiruvananthapuram",
          "logoId": "https://ulb-logos.s3.ap-south-1.amazonaws.com/thiruvananthapuram/thiruvanathapuram.jpg",
          "imageId": null,
          "domainUrl": "tmc.lsgkerala.gov.in",
          "twitterUrl": null,
          "facebookUrl": null,
          "emailId": "",
          "OfficeTimings": {
              "Mon - Fri": "9.00 AM - 6.00 PM"
          },
          "city": {
              "name": "Thiruvananthapuram Corporation",
              "localName": "തിരുവനന്തപുരം കോര്‍പ്പറേഷന്‍",
              "districtCode": "565",
              "districtName": "Thiruvananthapuram",
              "regionName": "Kerala",
              "ulbGrade": "Corporation",
              "longitude": null,
              "latitude": null,
              "captcha": null,
              "shapeFileLocation": null,
              "code": "C010100",
              "ddrName": "Thiruvananthapuram"
          },
          "address": "Municipal Corporation of Thiruvananthapuram,\nVikas Bhavan P.O.\nThiruvananthapuram\nKerala, India, Pin:695033",
          "pincode": [],
          "contactNumber": "4712320821",
          "pdfHeader": "",
          "pdfContactDetails": ""
      },
      {
          "i18nKey": "TENANT_TENANTS_KL_THRISSUR",
          "code": "kl.thrissur",
          "name": "Thrissur Corporation",
          "description": "Thrissur",
          "logoId": "",
          "imageId": null,
          "domainUrl": "thrissurcorporation.lsgkerala.gov.in",
          "twitterUrl": null,
          "facebookUrl": null,
          "emailId": "",
          "OfficeTimings": {
              "Mon - Fri": "9.00 AM - 6.00 PM"
          },
          "city": {
              "name": "Thrissur Corporation",
              "localName": "തൃശ്ശൂര്‍ കോര്‍പ്പറേഷന്‍",
              "districtCode": "566",
              "districtName": "Thrissur",
              "regionName": "Kerala",
              "ulbGrade": "Corporation",
              "longitude": null,
              "latitude": null,
              "captcha": null,
              "shapeFileLocation": null,
              "code": "C080100",
              "ddrName": "Thrissur"
          },
          "address": "Thrissur Corporation Office, Thekkinkadu Maidan, Thrissur, 680001",
          "pincode": [],
          "contactNumber": "4872422020",
          "pdfHeader": "",
          "pdfContactDetails": ""
      },
      {
          "i18nKey": "TENANT_TENANTS_KL_ALAPPUZHA",
          "code": "kl.alappuzha",
          "name": "Alappuzha Municipality",
          "description": "Alappuzha",
          "logoId": "",
          "imageId": null,
          "domainUrl": "alappuzhamunicipality.lsgkerala.gov.in",
          "twitterUrl": null,
          "facebookUrl": null,
          "emailId": "",
          "OfficeTimings": {
              "Mon - Fri": "9.00 AM - 6.00 PM"
          },
          "city": {
              "name": "Alappuzha Municipality",
              "localName": "ആലപ്പുഴ മുനിസിപ്പാലിറ്റി",
              "districtCode": "554",
              "districtName": "Alappuzha",
              "regionName": "Kerala",
              "ulbGrade": "Municipality Grade 1",
              "longitude": null,
              "latitude": null,
              "captcha": null,
              "shapeFileLocation": null,
              "code": "M040500",
              "ddrName": "Alappuzha"
          },
          "address": "Municiplaity Kerala",
          "pincode": [],
          "contactNumber": "111111",
          "pdfHeader": "",
          "pdfContactDetails": ""
      },
      {
          "i18nKey": "TENANT_TENANTS_KL_ALUVA",
          "code": "kl.aluva",
          "name": "Aluva Municipality",
          "description": "Aluva",
          "logoId": "",
          "imageId": null,
          "domainUrl": "aluvamunicipality.lsgkerala.gov.in",
          "twitterUrl": null,
          "facebookUrl": null,
          "emailId": "",
          "OfficeTimings": {
              "Mon - Fri": "9.00 AM - 6.00 PM"
          },
          "city": {
              "name": "Aluva Municipality",
              "localName": "ആലുവ മുനിസിപ്പാലിറ്റി",
              "districtCode": "555",
              "districtName": "Eranakulam",
              "regionName": "Kerala",
              "ulbGrade": "Municipality Grade 1",
              "longitude": null,
              "latitude": null,
              "captcha": null,
              "shapeFileLocation": null,
              "code": "M070800",
              "ddrName": "Aluva"
          },
          "address": "Municiplaity Kerala",
          "pincode": [],
          "contactNumber": "111111",
          "pdfHeader": "",
          "pdfContactDetails": ""
      },
      {
          "i18nKey": "TENANT_TENANTS_KL_CHANGANASSERY",
          "code": "kl.changanassery",
          "name": "Changanassery Municipality",
          "description": "Changanassery",
          "logoId": "",
          "imageId": null,
          "domainUrl": "changanasserymunicipality.lsgkerala.gov.in",
          "twitterUrl": null,
          "facebookUrl": null,
          "emailId": "",
          "OfficeTimings": {
              "Mon - Fri": "9.00 AM - 6.00 PM"
          },
          "city": {
              "name": "Changanassery Municipality",
              "localName": "ചങ്ങനാശ്ശേരി മുനിസിപ്പാലിറ്റി",
              "districtCode": "560",
              "districtName": "Kottayam",
              "regionName": "Kerala",
              "ulbGrade": "Municipality Grade 1",
              "longitude": null,
              "latitude": null,
              "captcha": null,
              "shapeFileLocation": null,
              "code": "M050300",
              "ddrName": "Changanassery"
          },
          "address": "Municiplaity Kerala",
          "pincode": [],
          "contactNumber": "111111",
          "pdfHeader": "",
          "pdfContactDetails": ""
      },
      {
          "i18nKey": "TENANT_TENANTS_KL_GURUVAYUR",
          "code": "kl.guruvayur",
          "name": "Guruvayur Municipality",
          "description": "Guruvayur",
          "logoId": "",
          "imageId": null,
          "domainUrl": "guruvayoormunicipality.lsgkerala.gov.in",
          "twitterUrl": null,
          "facebookUrl": null,
          "emailId": "",
          "OfficeTimings": {
              "Mon - Fri": "9.00 AM - 6.00 PM"
          },
          "city": {
              "name": "Guruvayur Municipality",
              "localName": "ഗുരുവായൂര്‍ മുനിസിപ്പാലിറ്റി",
              "districtCode": "566",
              "districtName": "Thrissur",
              "regionName": "Kerala",
              "ulbGrade": "Municipality Grade 1",
              "longitude": null,
              "latitude": null,
              "captcha": null,
              "shapeFileLocation": null,
              "code": "M080100",
              "ddrName": "Guruvayur"
          },
          "address": "Municiplaity Kerala",
          "pincode": [],
          "contactNumber": "111111",
          "pdfHeader": "",
          "pdfContactDetails": ""
      },
      {
          "i18nKey": "TENANT_TENANTS_KL_IRINJALAKUDA",
          "code": "kl.irinjalakuda",
          "name": "Irinjalakuda Municipality",
          "description": "Irinjalakuda",
          "logoId": "",
          "imageId": null,
          "domainUrl": "irinjalakudamunicipality.lsgkerala.gov.in",
          "twitterUrl": null,
          "facebookUrl": null,
          "emailId": "",
          "OfficeTimings": {
              "Mon - Fri": "9.00 AM - 6.00 PM"
          },
          "city": {
              "name": "Irinjalakuda Municipality",
              "localName": "ഇരിങ്ങാലക്കുട മുനിസിപ്പാലിറ്റി",
              "districtCode": "566",
              "districtName": "Thrissur",
              "regionName": "Kerala",
              "ulbGrade": "Municipality Grade 1",
              "longitude": null,
              "latitude": null,
              "captcha": null,
              "shapeFileLocation": null,
              "code": "M080500",
              "ddrName": "Irinjalakuda"
          },
          "address": "Municiplaity Kerala",
          "pincode": [],
          "contactNumber": "111111",
          "pdfHeader": "",
          "pdfContactDetails": ""
      },
      {
          "i18nKey": "TENANT_TENANTS_KL_KALAMASSERY",
          "code": "kl.kalamassery",
          "name": "Kalamassery Municipality",
          "description": "Kalamassery",
          "logoId": "",
          "imageId": null,
          "domainUrl": "kalamasserymunicipality.lsgkerala.gov.in",
          "twitterUrl": null,
          "facebookUrl": null,
          "emailId": "",
          "OfficeTimings": {
              "Mon - Fri": "9.00 AM - 6.00 PM"
          },
          "city": {
              "name": "Kalamassery Municipality",
              "localName": "കളമശ്ശേരി മുനിസിപ്പാലിറ്റി",
              "districtCode": "555",
              "districtName": "Eranakulam",
              "regionName": "Kerala",
              "ulbGrade": "Municipality Grade 1",
              "longitude": null,
              "latitude": null,
              "captcha": null,
              "shapeFileLocation": null,
              "code": "M070100",
              "ddrName": "Kalamassery"
          },
          "address": "Municiplaity Kerala",
          "pincode": [],
          "contactNumber": "111111",
          "pdfHeader": "",
          "pdfContactDetails": ""
      },
      {
          "i18nKey": "TENANT_TENANTS_KL_KANHANGAD",
          "code": "kl.kanhangad",
          "name": "Kanhangad Municipality",
          "description": "Kanhangad",
          "logoId": "",
          "imageId": null,
          "domainUrl": "kanhangadmunicipality.lsgkerala.gov.in",
          "twitterUrl": null,
          "facebookUrl": null,
          "emailId": "",
          "OfficeTimings": {
              "Mon - Fri": "9.00 AM - 6.00 PM"
          },
          "city": {
              "name": "Kanhangad Municipality",
              "localName": "കാഞ്ഞങ്ങാട് മുനിസിപ്പാലിറ്റി",
              "districtCode": "558",
              "districtName": "Kasargode",
              "regionName": "Kerala",
              "ulbGrade": "Municipality Grade 1",
              "longitude": null,
              "latitude": null,
              "captcha": null,
              "shapeFileLocation": null,
              "code": "M140100",
              "ddrName": "Kanhangad"
          },
          "address": "Municiplaity Kerala",
          "pincode": [],
          "contactNumber": "111111",
          "pdfHeader": "",
          "pdfContactDetails": ""
      },
      {
          "i18nKey": "TENANT_TENANTS_KL_KASARAGOD",
          "code": "kl.kasaragod",
          "name": "Kasaragod Municipality",
          "description": "Kasaragod",
          "logoId": "",
          "imageId": null,
          "domainUrl": "kasaragodmunicipality.lsgkerala.gov.in",
          "twitterUrl": null,
          "facebookUrl": null,
          "emailId": "",
          "OfficeTimings": {
              "Mon - Fri": "9.00 AM - 6.00 PM"
          },
          "city": {
              "name": "Kasaragod Municipality",
              "localName": "കാസര്‍കോഡ് മുനിസിപ്പാലിറ്റി",
              "districtCode": "558",
              "districtName": "Kasargode",
              "regionName": "Kerala",
              "ulbGrade": "Municipality Grade 1",
              "longitude": null,
              "latitude": null,
              "captcha": null,
              "shapeFileLocation": null,
              "code": "M140200",
              "ddrName": "Kasaragod"
          },
          "address": "Municiplaity Kerala",
          "pincode": [],
          "contactNumber": "111111",
          "pdfHeader": "",
          "pdfContactDetails": ""
      },
      {
          "i18nKey": "TENANT_TENANTS_KL_KOTTAYAM",
          "code": "kl.kottayam",
          "name": "Kottayam Municipality",
          "description": "Kottayam",
          "logoId": "",
          "imageId": null,
          "domainUrl": "kottayammunicipality.lsgkerala.gov.in",
          "twitterUrl": null,
          "facebookUrl": null,
          "emailId": "",
          "OfficeTimings": {
              "Mon - Fri": "9.00 AM - 6.00 PM"
          },
          "city": {
              "name": "Kottayam Municipality",
              "localName": "കോട്ടയം മുനിസിപ്പാലിറ്റി",
              "districtCode": "560",
              "districtName": "Kottayam",
              "regionName": "Kerala",
              "ulbGrade": "Municipality Grade 1",
              "longitude": null,
              "latitude": null,
              "captcha": null,
              "shapeFileLocation": null,
              "code": "M050400",
              "ddrName": "Kottayam"
          },
          "address": "Municiplaity Kerala",
          "pincode": [],
          "contactNumber": "111111",
          "pdfHeader": "",
          "pdfContactDetails": ""
      },
      {
          "i18nKey": "TENANT_TENANTS_KL_KUNNAMKULAM",
          "code": "kl.kunnamkulam",
          "name": "Kunnamkulam Municipality",
          "description": "Kunnamkulam",
          "logoId": "",
          "imageId": null,
          "domainUrl": "kunnamkulammunicipality.lsgkerala.gov.in",
          "twitterUrl": null,
          "facebookUrl": null,
          "emailId": "",
          "OfficeTimings": {
              "Mon - Fri": "9.00 AM - 6.00 PM"
          },
          "city": {
              "name": "Kunnamkulam Municipality",
              "localName": "കുന്നംകുളം മുനിസിപ്പാലിറ്റി",
              "districtCode": "566",
              "districtName": "Thrissur",
              "regionName": "Kerala",
              "ulbGrade": "Municipality Grade 1",
              "longitude": null,
              "latitude": null,
              "captcha": null,
              "shapeFileLocation": null,
              "code": "M080600",
              "ddrName": "Kunnamkulam"
          },
          "address": "Municiplaity Kerala",
          "pincode": [],
          "contactNumber": "111111",
          "pdfHeader": "",
          "pdfContactDetails": ""
      },
      {
          "i18nKey": "TENANT_TENANTS_KL_MALAPPURAM",
          "code": "kl.malappuram",
          "name": "Malappuram Municipality",
          "description": "Malappuram",
          "logoId": "",
          "imageId": null,
          "domainUrl": "malappurammunicipality.lsgkerala.gov.in",
          "twitterUrl": null,
          "facebookUrl": null,
          "emailId": "",
          "OfficeTimings": {
              "Mon - Fri": "9.00 AM - 6.00 PM"
          },
          "city": {
              "name": "Malappuram Municipality",
              "localName": "മലപ്പുറം മുനിസിപ്പാലിറ്റി",
              "districtCode": "562",
              "districtName": "Malappuram",
              "regionName": "Kerala",
              "ulbGrade": "Municipality Grade 1",
              "longitude": null,
              "latitude": null,
              "captcha": null,
              "shapeFileLocation": null,
              "code": "M100500",
              "ddrName": "Malappuram"
          },
          "address": "Municiplaity Kerala",
          "pincode": [],
          "contactNumber": "111111",
          "pdfHeader": "",
          "pdfContactDetails": ""
      },
      {
          "i18nKey": "TENANT_TENANTS_KL_MANJERI",
          "code": "kl.manjeri",
          "name": "Manjeri Municipality",
          "description": "Manjeri",
          "logoId": "",
          "imageId": null,
          "domainUrl": "manjerimunicipality.lsgkerala.gov.in",
          "twitterUrl": null,
          "facebookUrl": null,
          "emailId": "",
          "OfficeTimings": {
              "Mon - Fri": "9.00 AM - 6.00 PM"
          },
          "city": {
              "name": "Manjeri Municipality",
              "localName": "മഞ്ചേരി മുനിസിപ്പാലിറ്റി",
              "districtCode": "562",
              "districtName": "Malappuram",
              "regionName": "Kerala",
              "ulbGrade": "Municipality Grade 1",
              "longitude": null,
              "latitude": null,
              "captcha": null,
              "shapeFileLocation": null,
              "code": "M100300",
              "ddrName": "Manjeri"
          },
          "address": "Municiplaity Kerala",
          "pincode": [],
          "contactNumber": "111111",
          "pdfHeader": "",
          "pdfContactDetails": ""
      },
      {
          "i18nKey": "TENANT_TENANTS_KL_NEDUMANGAD",
          "code": "kl.nedumangad",
          "name": "Nedumangad Municipality",
          "description": "Nedumangad",
          "logoId": "",
          "imageId": null,
          "domainUrl": "nedumangadmunicipality.lsgkerala.gov.in",
          "twitterUrl": null,
          "facebookUrl": null,
          "emailId": "",
          "OfficeTimings": {
              "Mon - Fri": "9.00 AM - 6.00 PM"
          },
          "city": {
              "name": "Nedumangad Municipality",
              "localName": "നെടുമങ്ങാട് മുനിസിപ്പാലിറ്റി",
              "districtCode": "565",
              "districtName": "Thiruvananthapuram",
              "regionName": "Kerala",
              "ulbGrade": "Municipality Grade 1",
              "longitude": null,
              "latitude": null,
              "captcha": null,
              "shapeFileLocation": null,
              "code": "M010300",
              "ddrName": "Nedumangad"
          },
          "address": "Municiplaity Kerala",
          "pincode": [],
          "contactNumber": "111111",
          "pdfHeader": "",
          "pdfContactDetails": ""
      },
      {
          "i18nKey": "TENANT_TENANTS_KL_NEYYATTINKARA",
          "code": "kl.neyyattinkara",
          "name": "Neyyattinkara Municipality",
          "description": "Neyyattinkara",
          "logoId": "",
          "imageId": null,
          "domainUrl": "neyyattinkaramunicipality.lsgkerala.gov.in",
          "twitterUrl": null,
          "facebookUrl": null,
          "emailId": "",
          "OfficeTimings": {
              "Mon - Fri": "9.00 AM - 6.00 PM"
          },
          "city": {
              "name": "Neyyattinkara Municipality",
              "localName": "നെയ്യാറ്റിന്‍കര മുനിസിപ്പാലിറ്റി",
              "districtCode": "565",
              "districtName": "Thiruvananthapuram",
              "regionName": "Kerala",
              "ulbGrade": "Municipality Grade 1",
              "longitude": null,
              "latitude": null,
              "captcha": null,
              "shapeFileLocation": null,
              "code": "M010400",
              "ddrName": "Neyyattinkara"
          },
          "address": "Municiplaity Kerala",
          "pincode": [],
          "contactNumber": "111111",
          "pdfHeader": "",
          "pdfContactDetails": ""
      },
      {
          "i18nKey": "TENANT_TENANTS_KL_PALA",
          "code": "kl.pala",
          "name": "Pala Municipality",
          "description": "Pala",
          "logoId": "",
          "imageId": null,
          "domainUrl": "palamunicipality.lsgkerala.gov.in",
          "twitterUrl": null,
          "facebookUrl": null,
          "emailId": "",
          "OfficeTimings": {
              "Mon - Fri": "9.00 AM - 6.00 PM"
          },
          "city": {
              "name": "Pala Municipality",
              "localName": "പാലാ മുനിസിപ്പാലിറ്റി",
              "districtCode": "560",
              "districtName": "Kottayam",
              "regionName": "Kerala",
              "ulbGrade": "Municipality Grade 1",
              "longitude": null,
              "latitude": null,
              "captcha": null,
              "shapeFileLocation": null,
              "code": "M050100",
              "ddrName": "Pala"
          },
          "address": "Municiplaity Kerala",
          "pincode": [],
          "contactNumber": "111111",
          "pdfHeader": "",
          "pdfContactDetails": ""
      },
      {
          "i18nKey": "TENANT_TENANTS_KL_PALAKKAD",
          "code": "kl.palakkad",
          "name": "Palakkad Municipality",
          "description": "Palakkad",
          "logoId": "",
          "imageId": null,
          "domainUrl": "palakkadmunicipality.lsgkerala.gov.in",
          "twitterUrl": null,
          "facebookUrl": null,
          "emailId": "",
          "OfficeTimings": {
              "Mon - Fri": "9.00 AM - 6.00 PM"
          },
          "city": {
              "name": "Palakkad Municipality",
              "localName": "പാലക്കാട് മുനിസിപ്പാലിറ്റി",
              "districtCode": "563",
              "districtName": "Palakkad",
              "regionName": "Kerala",
              "ulbGrade": "Municipality Grade 1",
              "longitude": null,
              "latitude": null,
              "captcha": null,
              "shapeFileLocation": null,
              "code": "M090400",
              "ddrName": "Palakkad"
          },
          "address": "Municiplaity Kerala",
          "pincode": [],
          "contactNumber": "111111",
          "pdfHeader": "",
          "pdfContactDetails": ""
      },
      {
          "i18nKey": "TENANT_TENANTS_KL_PAYYANNUR",
          "code": "kl.payyannur",
          "name": "Payyannur Municipality",
          "description": "Payyannur",
          "logoId": "",
          "imageId": null,
          "domainUrl": "payyanurmunicipality.lsgkerala.gov.in",
          "twitterUrl": null,
          "facebookUrl": null,
          "emailId": "",
          "OfficeTimings": {
              "Mon - Fri": "9.00 AM - 6.00 PM"
          },
          "city": {
              "name": "Payyannur Municipality",
              "localName": "പയ്യന്നൂര്‍ മുനിസിപ്പാലിറ്റി",
              "districtCode": "557",
              "districtName": "Kannur",
              "regionName": "Kerala",
              "ulbGrade": "Municipality Grade 1",
              "longitude": null,
              "latitude": null,
              "captcha": null,
              "shapeFileLocation": null,
              "code": "M130400",
              "ddrName": "Payyannur"
          },
          "address": "Municiplaity Kerala",
          "pincode": [],
          "contactNumber": "111111",
          "pdfHeader": "",
          "pdfContactDetails": ""
      },
      {
          "i18nKey": "TENANT_TENANTS_KL_PERUMBAVOOR",
          "code": "kl.perumbavoor",
          "name": "Perumbavoor Municipality",
          "description": "Perumbavoor",
          "logoId": "",
          "imageId": null,
          "domainUrl": "perumbavoormunicipality.lsgkerala.gov.in",
          "twitterUrl": null,
          "facebookUrl": null,
          "emailId": "",
          "OfficeTimings": {
              "Mon - Fri": "9.00 AM - 6.00 PM"
          },
          "city": {
              "name": "Perumbavoor Municipality",
              "localName": "പെരുമ്പാവൂര്‍ മുനിസിപ്പാലിറ്റി",
              "districtCode": "555",
              "districtName": "Eranakulam",
              "regionName": "Kerala",
              "ulbGrade": "Municipality Grade 1",
              "longitude": null,
              "latitude": null,
              "captcha": null,
              "shapeFileLocation": null,
              "code": "M070700",
              "ddrName": "Perumbavoor"
          },
          "address": "Municiplaity Kerala",
          "pincode": [],
          "contactNumber": "111111",
          "pdfHeader": "",
          "pdfContactDetails": ""
      },
      {
          "i18nKey": "TENANT_TENANTS_KL_PONNANI",
          "code": "kl.ponnani",
          "name": "Ponnani Municipality",
          "description": "Ponnani",
          "logoId": "",
          "imageId": null,
          "domainUrl": "ponnanimunicipality.lsgkerala.gov.in",
          "twitterUrl": null,
          "facebookUrl": null,
          "emailId": "",
          "OfficeTimings": {
              "Mon - Fri": "9.00 AM - 6.00 PM"
          },
          "city": {
              "name": "Ponnani Municipality",
              "localName": "പൊന്നാനി മുനിസിപ്പാലിറ്റി",
              "districtCode": "562",
              "districtName": "Malappuram",
              "regionName": "Kerala",
              "ulbGrade": "Municipality Grade 1",
              "longitude": null,
              "latitude": null,
              "captcha": null,
              "shapeFileLocation": null,
              "code": "M100200",
              "ddrName": "Ponnani"
          },
          "address": "Municiplaity Kerala",
          "pincode": [],
          "contactNumber": "111111",
          "pdfHeader": "",
          "pdfContactDetails": ""
      },
      {
          "i18nKey": "TENANT_TENANTS_KL_THALASSERY",
          "code": "kl.thalassery",
          "name": "Thalassery Municipality",
          "description": "Thalassery",
          "logoId": "",
          "imageId": null,
          "domainUrl": "thalasserymunicipality.lsgkerala.gov.in",
          "twitterUrl": null,
          "facebookUrl": null,
          "emailId": "",
          "OfficeTimings": {
              "Mon - Fri": "9.00 AM - 6.00 PM"
          },
          "city": {
              "name": "Thalassery Municipality",
              "localName": "തലശ്ശേരി മുനിസിപ്പാലിറ്റി",
              "districtCode": "557",
              "districtName": "Kannur",
              "regionName": "Kerala",
              "ulbGrade": "Municipality Grade 1",
              "longitude": null,
              "latitude": null,
              "captcha": null,
              "shapeFileLocation": null,
              "code": "M130500",
              "ddrName": "Thalassery"
          },
          "address": "Municiplaity Kerala",
          "pincode": [],
          "contactNumber": "111111",
          "pdfHeader": "",
          "pdfContactDetails": ""
      },
      {
          "i18nKey": "TENANT_TENANTS_KL_THALIPPARAMBA",
          "code": "kl.thalipparamba",
          "name": "Thalipparamba Municipality",
          "description": "Thalipparamba",
          "logoId": "",
          "imageId": null,
          "domainUrl": "taliparambamunicipality.lsgkerala.gov.in",
          "twitterUrl": null,
          "facebookUrl": null,
          "emailId": "",
          "OfficeTimings": {
              "Mon - Fri": "9.00 AM - 6.00 PM"
          },
          "city": {
              "name": "Thalipparamba Municipality",
              "localName": "തളിപ്പറമ്പ് മുനിസിപ്പാലിറ്റി",
              "districtCode": "557",
              "districtName": "Kannur",
              "regionName": "Kerala",
              "ulbGrade": "Municipality Grade 1",
              "longitude": null,
              "latitude": null,
              "captcha": null,
              "shapeFileLocation": null,
              "code": "M130200",
              "ddrName": "Thalipparamba"
          },
          "address": "Municiplaity Kerala",
          "pincode": [],
          "contactNumber": "111111",
          "pdfHeader": "",
          "pdfContactDetails": ""
      },
      {
          "i18nKey": "TENANT_TENANTS_KL_THIRUVALLA",
          "code": "kl.thiruvalla",
          "name": "Thiruvalla Municipality",
          "description": "Thiruvalla",
          "logoId": "",
          "imageId": null,
          "domainUrl": "thiruvallamunicipality.lsgkerala.gov.in",
          "twitterUrl": null,
          "facebookUrl": null,
          "emailId": "",
          "OfficeTimings": {
              "Mon - Fri": "9.00 AM - 6.00 PM"
          },
          "city": {
              "name": "Thiruvalla Municipality",
              "localName": "തിരുവല്ല മുനിസിപ്പാലിറ്റി",
              "districtCode": "564",
              "districtName": "Pathanamthitta",
              "regionName": "Kerala",
              "ulbGrade": "Municipality Grade 1",
              "longitude": null,
              "latitude": null,
              "captcha": null,
              "shapeFileLocation": null,
              "code": "M030200",
              "ddrName": "Thiruvalla"
          },
          "address": "Municiplaity Kerala",
          "pincode": [],
          "contactNumber": "111111",
          "pdfHeader": "",
          "pdfContactDetails": ""
      },
      {
          "i18nKey": "TENANT_TENANTS_KL_THODUPUZHA",
          "code": "kl.thodupuzha",
          "name": "Thodupuzha Municipality",
          "description": "Thodupuzha",
          "logoId": "",
          "imageId": null,
          "domainUrl": "thodupuzhamunicipality.lsgkerala.gov.in",
          "twitterUrl": null,
          "facebookUrl": null,
          "emailId": "",
          "OfficeTimings": {
              "Mon - Fri": "9.00 AM - 6.00 PM"
          },
          "city": {
              "name": "Thodupuzha Municipality",
              "localName": "തൊടുപുഴ മുനിസിപ്പാലിറ്റി",
              "districtCode": "556",
              "districtName": "Idukki",
              "regionName": "Kerala",
              "ulbGrade": "Municipality Grade 1",
              "longitude": null,
              "latitude": null,
              "captcha": null,
              "shapeFileLocation": null,
              "code": "M060100",
              "ddrName": "Thodupuzha"
          },
          "address": "Municiplaity Kerala",
          "pincode": [],
          "contactNumber": "111111",
          "pdfHeader": "",
          "pdfContactDetails": ""
      },
      {
          "i18nKey": "TENANT_TENANTS_KL_THRIKKAKARA",
          "code": "kl.thrikkakara",
          "name": "Thrikkakara Municipality",
          "description": "Thrikkakara",
          "logoId": "",
          "imageId": null,
          "domainUrl": "www.thrikkakara.lsgkerala.gov.in",
          "twitterUrl": null,
          "facebookUrl": null,
          "emailId": "",
          "OfficeTimings": {
              "Mon - Fri": "9.00 AM - 6.00 PM"
          },
          "city": {
              "name": "Thrikkakara Municipality",
              "localName": "തൃക്കാക്കര മുനിസിപ്പാലിറ്റി",
              "districtCode": "555",
              "districtName": "Eranakulam",
              "regionName": "Kerala",
              "ulbGrade": "Municipality Grade 1",
              "longitude": null,
              "latitude": null,
              "captcha": null,
              "shapeFileLocation": null,
              "code": "M070900",
              "ddrName": "Thrikkakara"
          },
          "address": "Municiplaity Kerala",
          "pincode": [],
          "contactNumber": "111111",
          "pdfHeader": "",
          "pdfContactDetails": ""
      },
      {
          "i18nKey": "TENANT_TENANTS_KL_TIRUR",
          "code": "kl.tirur",
          "name": "Tirur Municipality",
          "description": "Tirur",
          "logoId": "",
          "imageId": null,
          "domainUrl": "tirurmunicipality.lsgkerala.gov.in",
          "twitterUrl": null,
          "facebookUrl": null,
          "emailId": "",
          "OfficeTimings": {
              "Mon - Fri": "9.00 AM - 6.00 PM"
          },
          "city": {
              "name": "Tirur Municipality",
              "localName": "തിരൂര്‍ മുനിസിപ്പാലിറ്റി",
              "districtCode": "562",
              "districtName": "Malappuram",
              "regionName": "Kerala",
              "ulbGrade": "Municipality Grade 1",
              "longitude": null,
              "latitude": null,
              "captcha": null,
              "shapeFileLocation": null,
              "code": "M100400",
              "ddrName": "Tirur"
          },
          "address": "Municiplaity Kerala",
          "pincode": [],
          "contactNumber": "111111",
          "pdfHeader": "",
          "pdfContactDetails": ""
      },
      {
          "i18nKey": "TENANT_TENANTS_KL_TRIPUNITHURA",
          "code": "kl.tripunithura",
          "name": "Tripunithura Municipality",
          "description": "Tripunithura",
          "logoId": "",
          "imageId": null,
          "domainUrl": "thrippunithuramunicipality.lsgkerala.gov.in",
          "twitterUrl": null,
          "facebookUrl": null,
          "emailId": "",
          "OfficeTimings": {
              "Mon - Fri": "9.00 AM - 6.00 PM"
          },
          "city": {
              "name": "Tripunithura Municipality",
              "localName": "തൃപ്പൂണിത്തുറ മുനിസിപ്പാലിറ്റി",
              "districtCode": "555",
              "districtName": "Eranakulam",
              "regionName": "Kerala",
              "ulbGrade": "Municipality Grade 1",
              "longitude": null,
              "latitude": null,
              "captcha": null,
              "shapeFileLocation": null,
              "code": "M070400",
              "ddrName": "Tripunithura"
          },
          "address": "Municiplaity Kerala",
          "pincode": [],
          "contactNumber": "111111",
          "pdfHeader": "",
          "pdfContactDetails": ""
      },
      {
          "i18nKey": "TENANT_TENANTS_KL_VADAKARA",
          "code": "kl.vadakara",
          "name": "Vadakara Municipality",
          "description": "Vadakara",
          "logoId": "",
          "imageId": null,
          "domainUrl": "vadakaramunicipality.lsgkerala.gov.in",
          "twitterUrl": null,
          "facebookUrl": null,
          "emailId": "",
          "OfficeTimings": {
              "Mon - Fri": "9.00 AM - 6.00 PM"
          },
          "city": {
              "name": "Vadakara Municipality",
              "localName": "വടകര മുനിസിപ്പാലിറ്റി",
              "districtCode": "561",
              "districtName": "Kozhikkode",
              "regionName": "Kerala",
              "ulbGrade": "Municipality Grade 1",
              "longitude": null,
              "latitude": null,
              "captcha": null,
              "shapeFileLocation": null,
              "code": "M110100",
              "ddrName": "Vadakara"
          },
          "address": "Municiplaity Kerala",
          "pincode": [],
          "contactNumber": "111111",
          "pdfHeader": "",
          "pdfContactDetails": ""
      },
      {
          "i18nKey": "TENANT_TENANTS_KL_ADOOR",
          "code": "kl.adoor",
          "name": "Adoor Municipality",
          "description": "Adoor",
          "logoId": "",
          "imageId": null,
          "domainUrl": "adoormunicipality.lsgkerala.gov.in",
          "twitterUrl": null,
          "facebookUrl": null,
          "emailId": "",
          "OfficeTimings": {
              "Mon - Fri": "9.00 AM - 6.00 PM"
          },
          "city": {
              "name": "Adoor Municipality",
              "localName": "അടൂര്‍ മുനിസിപ്പാലിറ്റി",
              "districtCode": "564",
              "districtName": "Pathanamthitta",
              "regionName": "Kerala",
              "ulbGrade": "Municipality Grade 2",
              "longitude": null,
              "latitude": null,
              "captcha": null,
              "shapeFileLocation": null,
              "code": "M030100",
              "ddrName": "Adoor"
          },
          "address": "Municiplaity Kerala",
          "pincode": [],
          "contactNumber": "111111",
          "pdfHeader": "",
          "pdfContactDetails": ""
      },
      {
          "i18nKey": "TENANT_TENANTS_KL_ANGAMALY",
          "code": "kl.angamaly",
          "name": "Angamaly Municipality",
          "description": "Angamaly",
          "logoId": "",
          "imageId": null,
          "domainUrl": "angamalymunicipality.lsgkerala.gov.in",
          "twitterUrl": null,
          "facebookUrl": null,
          "emailId": "",
          "OfficeTimings": {
              "Mon - Fri": "9.00 AM - 6.00 PM"
          },
          "city": {
              "name": "Angamaly Municipality",
              "localName": "അങ്കമാലി മുനിസിപ്പാലിറ്റി",
              "districtCode": "555",
              "districtName": "Eranakulam",
              "regionName": "Kerala",
              "ulbGrade": "Municipality Grade 2",
              "longitude": null,
              "latitude": null,
              "captcha": null,
              "shapeFileLocation": null,
              "code": "M070300",
              "ddrName": "Angamaly"
          },
          "address": "Municiplaity Kerala",
          "pincode": [],
          "contactNumber": "111111",
          "pdfHeader": "",
          "pdfContactDetails": ""
      },
      {
          "i18nKey": "TENANT_TENANTS_KL_ATTINGAL",
          "code": "kl.attingal",
          "name": "Attingal Municipality",
          "description": "Attingal",
          "logoId": "",
          "imageId": null,
          "domainUrl": "attingalmunicipality.lsgkerala.gov.in",
          "twitterUrl": null,
          "facebookUrl": null,
          "emailId": "",
          "OfficeTimings": {
              "Mon - Fri": "9.00 AM - 6.00 PM"
          },
          "city": {
              "name": "Attingal Municipality",
              "localName": "ആറ്റിങ്ങല്‍ മുനിസിപ്പാലിറ്റി",
              "districtCode": "565",
              "districtName": "Thiruvananthapuram",
              "regionName": "Kerala",
              "ulbGrade": "Municipality Grade 2",
              "longitude": null,
              "latitude": null,
              "captcha": null,
              "shapeFileLocation": null,
              "code": "M010200",
              "ddrName": "Attingal"
          },
          "address": "Municiplaity Kerala",
          "pincode": [],
          "contactNumber": "111111",
          "pdfHeader": "",
          "pdfContactDetails": ""
      },
      {
          "i18nKey": "TENANT_TENANTS_KL_CHALAKUDY",
          "code": "kl.chalakudy",
          "name": "Chalakudy Municipality",
          "description": "Chalakudy",
          "logoId": "",
          "imageId": null,
          "domainUrl": "chalakudymunicipality.lsgkerala.gov.in",
          "twitterUrl": null,
          "facebookUrl": null,
          "emailId": "",
          "OfficeTimings": {
              "Mon - Fri": "9.00 AM - 6.00 PM"
          },
          "city": {
              "name": "Chalakudy Municipality",
              "localName": "ചാലക്കുടി മുനിസിപ്പാലിറ്റി",
              "districtCode": "566",
              "districtName": "Thrissur",
              "regionName": "Kerala",
              "ulbGrade": "Municipality Grade 2",
              "longitude": null,
              "latitude": null,
              "captcha": null,
              "shapeFileLocation": null,
              "code": "M080400",
              "ddrName": "Chalakudy"
          },
          "address": "Municiplaity Kerala",
          "pincode": [],
          "contactNumber": "111111",
          "pdfHeader": "",
          "pdfContactDetails": ""
      },
      {
          "i18nKey": "TENANT_TENANTS_KL_CHAVAKKAD",
          "code": "kl.chavakkad",
          "name": "Chavakkad Municipality",
          "description": "Chavakkad",
          "logoId": "",
          "imageId": null,
          "domainUrl": "chavakkadmunicipality.lsgkerala.gov.in",
          "twitterUrl": null,
          "facebookUrl": null,
          "emailId": "",
          "OfficeTimings": {
              "Mon - Fri": "9.00 AM - 6.00 PM"
          },
          "city": {
              "name": "Chavakkad Municipality",
              "localName": "ചാവക്കാട് മുനിസിപ്പാലിറ്റി",
              "districtCode": "566",
              "districtName": "Thrissur",
              "regionName": "Kerala",
              "ulbGrade": "Municipality Grade 2",
              "longitude": null,
              "latitude": null,
              "captcha": null,
              "shapeFileLocation": null,
              "code": "M080200",
              "ddrName": "Chavakkad"
          },
          "address": "Municiplaity Kerala",
          "pincode": [],
          "contactNumber": "111111",
          "pdfHeader": "",
          "pdfContactDetails": ""
      },
      {
          "i18nKey": "TENANT_TENANTS_KL_CHERTHALA",
          "code": "kl.cherthala",
          "name": "Cherthala Municipality",
          "description": "Cherthala",
          "logoId": "",
          "imageId": null,
          "domainUrl": "cherthalamunicipality.lsgkerala.gov.in",
          "twitterUrl": null,
          "facebookUrl": null,
          "emailId": "",
          "OfficeTimings": {
              "Mon - Fri": "9.00 AM - 6.00 PM"
          },
          "city": {
              "name": "Cherthala Municipality",
              "localName": "ചേര്‍ത്തല മുനിസിപ്പാലിറ്റി",
              "districtCode": "554",
              "districtName": "Alappuzha",
              "regionName": "Kerala",
              "ulbGrade": "Municipality Grade 2",
              "longitude": null,
              "latitude": null,
              "captcha": null,
              "shapeFileLocation": null,
              "code": "M040300",
              "ddrName": "Cherthala"
          },
          "address": "Municiplaity Kerala",
          "pincode": [],
          "contactNumber": "111111",
          "pdfHeader": "",
          "pdfContactDetails": ""
      },
      {
          "i18nKey": "TENANT_TENANTS_KL_CHITTUR",
          "code": "kl.chittur",
          "name": "Chittur Tattamangalam Municipality",
          "description": "Chittur",
          "logoId": "",
          "imageId": null,
          "domainUrl": "chitturthathamangalam.lsgkerala.gov.in",
          "twitterUrl": null,
          "facebookUrl": null,
          "emailId": "",
          "OfficeTimings": {
              "Mon - Fri": "9.00 AM - 6.00 PM"
          },
          "city": {
              "name": "Chittur Tattamangalam Municipality",
              "localName": "ചിറ്റൂര്‍ തത്തമംഗലം മുനിസിപ്പാലിറ്റി",
              "districtCode": "563",
              "districtName": "Palakkad",
              "regionName": "Kerala",
              "ulbGrade": "Municipality Grade 2",
              "longitude": null,
              "latitude": null,
              "captcha": null,
              "shapeFileLocation": null,
              "code": "M090300",
              "ddrName": "Chittur"
          },
          "address": "Municiplaity Kerala",
          "pincode": [],
          "contactNumber": "111111",
          "pdfHeader": "",
          "pdfContactDetails": ""
      },
      {
          "i18nKey": "TENANT_TENANTS_KL_ELOOR",
          "code": "kl.eloor",
          "name": "Eloor Municipality",
          "description": "Eloor",
          "logoId": "",
          "imageId": null,
          "domainUrl": "www.eloor.lsgkerala.gov.in",
          "twitterUrl": null,
          "facebookUrl": null,
          "emailId": "",
          "OfficeTimings": {
              "Mon - Fri": "9.00 AM - 6.00 PM"
          },
          "city": {
              "name": "Eloor Municipality",
              "localName": "ഏലൂര്‍ മുനിസിപ്പാലിറ്റി",
              "districtCode": "555",
              "districtName": "Eranakulam",
              "regionName": "Kerala",
              "ulbGrade": "Municipality Grade 2",
              "longitude": null,
              "latitude": null,
              "captcha": null,
              "shapeFileLocation": null,
              "code": "M071000",
              "ddrName": "Eloor"
          },
          "address": "Municiplaity Kerala",
          "pincode": [],
          "contactNumber": "111111",
          "pdfHeader": "",
          "pdfContactDetails": ""
      },
      {
          "i18nKey": "TENANT_TENANTS_KL_KALPETTA",
          "code": "kl.kalpetta",
          "name": "Kalpetta Municipality",
          "description": "Kalpetta",
          "logoId": "",
          "imageId": null,
          "domainUrl": "kalpettamunicipality.lsgkerala.gov.in",
          "twitterUrl": null,
          "facebookUrl": null,
          "emailId": "",
          "OfficeTimings": {
              "Mon - Fri": "9.00 AM - 6.00 PM"
          },
          "city": {
              "name": "Kalpetta Municipality",
              "localName": "കല്‍പ്പറ്റ മുനിസിപ്പാലിറ്റി",
              "districtCode": "567",
              "districtName": "Wayanad",
              "regionName": "Kerala",
              "ulbGrade": "Municipality Grade 2",
              "longitude": null,
              "latitude": null,
              "captcha": null,
              "shapeFileLocation": null,
              "code": "M120100",
              "ddrName": "Kalpetta"
          },
          "address": "Municiplaity Kerala",
          "pincode": [],
          "contactNumber": "111111",
          "pdfHeader": "",
          "pdfContactDetails": ""
      },
      {
          "i18nKey": "TENANT_TENANTS_KL_KARUNAGAPPALLY",
          "code": "kl.karunagappally",
          "name": "Karunagappally Municipality",
          "description": "Karunagappally",
          "logoId": "",
          "imageId": null,
          "domainUrl": "www.karunagappally.lsgkerala.gov.in",
          "twitterUrl": null,
          "facebookUrl": null,
          "emailId": "",
          "OfficeTimings": {
              "Mon - Fri": "9.00 AM - 6.00 PM"
          },
          "city": {
              "name": "Karunagappally Municipality",
              "localName": "കരുനാഗപ്പള്ളി മുനിസിപ്പാലിറ്റി",
              "districtCode": "559",
              "districtName": "Kollam",
              "regionName": "Kerala",
              "ulbGrade": "Municipality Grade 2",
              "longitude": null,
              "latitude": null,
              "captcha": null,
              "shapeFileLocation": null,
              "code": "M020100",
              "ddrName": "Karunagappally"
          },
          "address": "Municiplaity Kerala",
          "pincode": [],
          "contactNumber": "111111",
          "pdfHeader": "",
          "pdfContactDetails": ""
      },
      {
          "i18nKey": "TENANT_TENANTS_KL_KAYAMKULAM",
          "code": "kl.kayamkulam",
          "name": "Kayamkulam Municipality",
          "description": "Kayamkulam",
          "logoId": "",
          "imageId": null,
          "domainUrl": "kayamkulammunicipality.lsgkerala.gov.in",
          "twitterUrl": null,
          "facebookUrl": null,
          "emailId": "",
          "OfficeTimings": {
              "Mon - Fri": "9.00 AM - 6.00 PM"
          },
          "city": {
              "name": "Kayamkulam Municipality",
              "localName": "കായംകുളം മുനിസിപ്പാലിറ്റി",
              "districtCode": "554",
              "districtName": "Alappuzha",
              "regionName": "Kerala",
              "ulbGrade": "Municipality Grade 2",
              "longitude": null,
              "latitude": null,
              "captcha": null,
              "shapeFileLocation": null,
              "code": "M040400",
              "ddrName": "Kayamkulam"
          },
          "address": "Municiplaity Kerala",
          "pincode": [],
          "contactNumber": "111111",
          "pdfHeader": "",
          "pdfContactDetails": ""
      },
      {
          "i18nKey": "TENANT_TENANTS_KL_KODUNGALLUR",
          "code": "kl.kodungallur",
          "name": "Kodungallur Municipality",
          "description": "Kodungallur",
          "logoId": "",
          "imageId": null,
          "domainUrl": "kodungalloormunicipality.lsgkerala.gov.in",
          "twitterUrl": null,
          "facebookUrl": null,
          "emailId": "",
          "OfficeTimings": {
              "Mon - Fri": "9.00 AM - 6.00 PM"
          },
          "city": {
              "name": "Kodungallur Municipality",
              "localName": "കൊടുങ്ങല്ലൂര്‍ മുനിസിപ്പാലിറ്റി",
              "districtCode": "566",
              "districtName": "Thrissur",
              "regionName": "Kerala",
              "ulbGrade": "Municipality Grade 2",
              "longitude": null,
              "latitude": null,
              "captcha": null,
              "shapeFileLocation": null,
              "code": "M080300",
              "ddrName": "Kodungallur"
          },
          "address": "Municiplaity Kerala",
          "pincode": [],
          "contactNumber": "111111",
          "pdfHeader": "",
          "pdfContactDetails": ""
      },
      {
          "i18nKey": "TENANT_TENANTS_KL_KOTHAMANGALAM",
          "code": "kl.kothamangalam",
          "name": "Kothamangalam Municipality",
          "description": "Kothamangalam",
          "logoId": "",
          "imageId": null,
          "domainUrl": "kothamangalammunicipality.lsgkerala.gov.in",
          "twitterUrl": null,
          "facebookUrl": null,
          "emailId": "",
          "OfficeTimings": {
              "Mon - Fri": "9.00 AM - 6.00 PM"
          },
          "city": {
              "name": "Kothamangalam Municipality",
              "localName": "കോതമംഗലം മുനിസിപ്പാലിറ്റി",
              "districtCode": "555",
              "districtName": "Eranakulam",
              "regionName": "Kerala",
              "ulbGrade": "Municipality Grade 2",
              "longitude": null,
              "latitude": null,
              "captcha": null,
              "shapeFileLocation": null,
              "code": "M070200",
              "ddrName": "Kothamangalam"
          },
          "address": "Municiplaity Kerala",
          "pincode": [],
          "contactNumber": "111111",
          "pdfHeader": "",
          "pdfContactDetails": ""
      },
      {
          "i18nKey": "TENANT_TENANTS_KL_KOTTAKKAL",
          "code": "kl.kottakkal",
          "name": "Kottakkal Municipality",
          "description": "Kottakkal",
          "logoId": "",
          "imageId": null,
          "domainUrl": "www.kottakkal.lsgkerala.gov.in",
          "twitterUrl": null,
          "facebookUrl": null,
          "emailId": "",
          "OfficeTimings": {
              "Mon - Fri": "9.00 AM - 6.00 PM"
          },
          "city": {
              "name": "Kottakkal Municipality",
              "localName": "കോട്ടക്കല്‍ മുനിസിപ്പാലിറ്റി",
              "districtCode": "562",
              "districtName": "Malappuram",
              "regionName": "Kerala",
              "ulbGrade": "Municipality Grade 2",
              "longitude": null,
              "latitude": null,
              "captcha": null,
              "shapeFileLocation": null,
              "code": "M100700",
              "ddrName": "Kottakkal"
          },
          "address": "Municiplaity Kerala",
          "pincode": [],
          "contactNumber": "111111",
          "pdfHeader": "",
          "pdfContactDetails": ""
      },
      {
          "i18nKey": "TENANT_TENANTS_KL_KOYILANDY",
          "code": "kl.koyilandy",
          "name": "Koyilandy Municipality",
          "description": "Koyilandy",
          "logoId": "",
          "imageId": null,
          "domainUrl": "quilandymunicipality.lsgkerala.gov.in",
          "twitterUrl": null,
          "facebookUrl": null,
          "emailId": "",
          "OfficeTimings": {
              "Mon - Fri": "9.00 AM - 6.00 PM"
          },
          "city": {
              "name": "Koyilandy Municipality",
              "localName": "കൊയിലാണ്ടി മുനിസിപ്പാലിറ്റി",
              "districtCode": "561",
              "districtName": "Kozhikkode",
              "regionName": "Kerala",
              "ulbGrade": "Municipality Grade 2",
              "longitude": null,
              "latitude": null,
              "captcha": null,
              "shapeFileLocation": null,
              "code": "M110200",
              "ddrName": "Koyilandy"
          },
          "address": "Municiplaity Kerala",
          "pincode": [],
          "contactNumber": "111111",
          "pdfHeader": "",
          "pdfContactDetails": ""
      },
      {
          "i18nKey": "TENANT_TENANTS_KL_KUTHUPARAMBA",
          "code": "kl.kuthuparamba",
          "name": "Kuthuparamba Municipality",
          "description": "Kuthuparamba",
          "logoId": "",
          "imageId": null,
          "domainUrl": "koothuparambamunicipality.lsgkerala.gov.in",
          "twitterUrl": null,
          "facebookUrl": null,
          "emailId": "",
          "OfficeTimings": {
              "Mon - Fri": "9.00 AM - 6.00 PM"
          },
          "city": {
              "name": "Kuthuparamba Municipality",
              "localName": "കൂത്തുപറമ്പ് മുനിസിപ്പാലിറ്റി",
              "districtCode": "557",
              "districtName": "Kannur",
              "regionName": "Kerala",
              "ulbGrade": "Municipality Grade 2",
              "longitude": null,
              "latitude": null,
              "captcha": null,
              "shapeFileLocation": null,
              "code": "M130300",
              "ddrName": "Kuthuparamba"
          },
          "address": "Municiplaity Kerala",
          "pincode": [],
          "contactNumber": "111111",
          "pdfHeader": "",
          "pdfContactDetails": ""
      },
      {
          "i18nKey": "TENANT_TENANTS_KL_MARADU",
          "code": "kl.maradu",
          "name": "Maradu Municipality",
          "description": "Maradu",
          "logoId": "",
          "imageId": null,
          "domainUrl": "www.maradu.lsgkerala.gov.in",
          "twitterUrl": null,
          "facebookUrl": null,
          "emailId": "",
          "OfficeTimings": {
              "Mon - Fri": "9.00 AM - 6.00 PM"
          },
          "city": {
              "name": "Maradu Municipality",
              "localName": "മരട് മുനിസിപ്പാലിറ്റി",
              "districtCode": "555",
              "districtName": "Eranakulam",
              "regionName": "Kerala",
              "ulbGrade": "Municipality Grade 2",
              "longitude": null,
              "latitude": null,
              "captcha": null,
              "shapeFileLocation": null,
              "code": "M071100",
              "ddrName": "Maradu"
          },
          "address": "Municiplaity Kerala",
          "pincode": [],
          "contactNumber": "111111",
          "pdfHeader": "",
          "pdfContactDetails": ""
      },
      {
          "i18nKey": "TENANT_TENANTS_KL_MATTANUR",
          "code": "kl.mattanur",
          "name": "Mattanur Municipality",
          "description": "Mattanur",
          "logoId": "",
          "imageId": null,
          "domainUrl": "mattannurmunicipality.lsgkerala.gov.in",
          "twitterUrl": null,
          "facebookUrl": null,
          "emailId": "",
          "OfficeTimings": {
              "Mon - Fri": "9.00 AM - 6.00 PM"
          },
          "city": {
              "name": "Mattanur Municipality",
              "localName": "മട്ടന്നൂര്‍ മുനിസിപ്പാലിറ്റി",
              "districtCode": "557",
              "districtName": "Kannur",
              "regionName": "Kerala",
              "ulbGrade": "Municipality Grade 2",
              "longitude": null,
              "latitude": null,
              "captcha": null,
              "shapeFileLocation": null,
              "code": "M130100",
              "ddrName": "Mattanur"
          },
          "address": "Municiplaity Kerala",
          "pincode": [],
          "contactNumber": "111111",
          "pdfHeader": "",
          "pdfContactDetails": ""
      },
      {
          "i18nKey": "TENANT_TENANTS_KL_MAVELIKARA",
          "code": "kl.mavelikara",
          "name": "Mavelikara Municipality",
          "description": "Mavelikara",
          "logoId": "",
          "imageId": null,
          "domainUrl": "mavelikaramunicipality.lsgkerala.gov.in",
          "twitterUrl": null,
          "facebookUrl": null,
          "emailId": "",
          "OfficeTimings": {
              "Mon - Fri": "9.00 AM - 6.00 PM"
          },
          "city": {
              "name": "Mavelikara Municipality",
              "localName": "മാവേലിക്കര മുനിസിപ്പാലിറ്റി",
              "districtCode": "554",
              "districtName": "Alappuzha",
              "regionName": "Kerala",
              "ulbGrade": "Municipality Grade 2",
              "longitude": null,
              "latitude": null,
              "captcha": null,
              "shapeFileLocation": null,
              "code": "M040200",
              "ddrName": "Mavelikara"
          },
          "address": "Municiplaity Kerala",
          "pincode": [],
          "contactNumber": "111111",
          "pdfHeader": "",
          "pdfContactDetails": ""
      },
      {
          "i18nKey": "TENANT_TENANTS_KL_MUVATTUPUZHA",
          "code": "kl.muvattupuzha",
          "name": "Muvattupuzha Municipality",
          "description": "Muvattupuzha",
          "logoId": "",
          "imageId": null,
          "domainUrl": "muvattupuzhamunicipality.lsgkerala.gov.in",
          "twitterUrl": null,
          "facebookUrl": null,
          "emailId": "",
          "OfficeTimings": {
              "Mon - Fri": "9.00 AM - 6.00 PM"
          },
          "city": {
              "name": "Muvattupuzha Municipality",
              "localName": "മൂവാറ്റുപുഴ മുനിസിപ്പാലിറ്റി",
              "districtCode": "555",
              "districtName": "Eranakulam",
              "regionName": "Kerala",
              "ulbGrade": "Municipality Grade 2",
              "longitude": null,
              "latitude": null,
              "captcha": null,
              "shapeFileLocation": null,
              "code": "M070500",
              "ddrName": "Muvattupuzha"
          },
          "address": "Municiplaity Kerala",
          "pincode": [],
          "contactNumber": "111111",
          "pdfHeader": "",
          "pdfContactDetails": ""
      },
      {
          "i18nKey": "TENANT_TENANTS_KL_NILAMBUR",
          "code": "kl.nilambur",
          "name": "Nilambur Municipality",
          "description": "Nilambur",
          "logoId": "",
          "imageId": null,
          "domainUrl": "www.nilambur.lsgkerala.gov.in",
          "twitterUrl": null,
          "facebookUrl": null,
          "emailId": "",
          "OfficeTimings": {
              "Mon - Fri": "9.00 AM - 6.00 PM"
          },
          "city": {
              "name": "Nilambur Municipality",
              "localName": "നിലമ്പൂര്‍ മുനിസിപ്പാലിറ്റി",
              "districtCode": "562",
              "districtName": "Malappuram",
              "regionName": "Kerala",
              "ulbGrade": "Municipality Grade 2",
              "longitude": null,
              "latitude": null,
              "captcha": null,
              "shapeFileLocation": null,
              "code": "M100600",
              "ddrName": "Nilambur"
          },
          "address": "Municiplaity Kerala",
          "pincode": [],
          "contactNumber": "111111",
          "pdfHeader": "",
          "pdfContactDetails": ""
      },
      {
          "i18nKey": "TENANT_TENANTS_KL_NILESHWAR",
          "code": "kl.nileshwar",
          "name": "Nileshwar Municipality",
          "description": "Nileshwar",
          "logoId": "",
          "imageId": null,
          "domainUrl": "www.nileshwar.lsgkerala.gov.in",
          "twitterUrl": null,
          "facebookUrl": null,
          "emailId": "",
          "OfficeTimings": {
              "Mon - Fri": "9.00 AM - 6.00 PM"
          },
          "city": {
              "name": "Nileshwar Municipality",
              "localName": "നീലേശ്വരം മുനിസിപ്പാലിറ്റി",
              "districtCode": "558",
              "districtName": "Kasargode",
              "regionName": "Kerala",
              "ulbGrade": "Municipality Grade 2",
              "longitude": null,
              "latitude": null,
              "captcha": null,
              "shapeFileLocation": null,
              "code": "M140300",
              "ddrName": "Nileshwar"
          },
          "address": "Municiplaity Kerala",
          "pincode": [],
          "contactNumber": "111111",
          "pdfHeader": "",
          "pdfContactDetails": ""
      },
      {
          "i18nKey": "TENANT_TENANTS_KL_NORTH",
          "code": "kl.north",
          "name": "North Paravur Municipality",
          "description": "North",
          "logoId": "",
          "imageId": null,
          "domainUrl": "northparavurmunicipality.lsgkerala.gov.in",
          "twitterUrl": null,
          "facebookUrl": null,
          "emailId": "",
          "OfficeTimings": {
              "Mon - Fri": "9.00 AM - 6.00 PM"
          },
          "city": {
              "name": "North Paravur Municipality",
              "localName": "വടക്കന്‍ പറവൂര്‍ മുനിസിപ്പാലിറ്റി",
              "districtCode": "555",
              "districtName": "Eranakulam",
              "regionName": "Kerala",
              "ulbGrade": "Municipality Grade 2",
              "longitude": null,
              "latitude": null,
              "captcha": null,
              "shapeFileLocation": null,
              "code": "M070600",
              "ddrName": "North"
          },
          "address": "Municiplaity Kerala",
          "pincode": [],
          "contactNumber": "111111",
          "pdfHeader": "",
          "pdfContactDetails": ""
      },
      {
          "i18nKey": "TENANT_TENANTS_KL_OTTAPALAM",
          "code": "kl.ottapalam",
          "name": "Ottapalam Municipality",
          "description": "Ottapalam",
          "logoId": "",
          "imageId": null,
          "domainUrl": "ottapalammunicipality.lsgkerala.gov.in",
          "twitterUrl": null,
          "facebookUrl": null,
          "emailId": "",
          "OfficeTimings": {
              "Mon - Fri": "9.00 AM - 6.00 PM"
          },
          "city": {
              "name": "Ottapalam Municipality",
              "localName": "ഒറ്റപ്പാലം മുനിസിപ്പാലിറ്റി",
              "districtCode": "563",
              "districtName": "Palakkad",
              "regionName": "Kerala",
              "ulbGrade": "Municipality Grade 2",
              "longitude": null,
              "latitude": null,
              "captcha": null,
              "shapeFileLocation": null,
              "code": "M090100",
              "ddrName": "Ottapalam"
          },
          "address": "Municiplaity Kerala",
          "pincode": [],
          "contactNumber": "111111",
          "pdfHeader": "",
          "pdfContactDetails": ""
      },
      {
          "i18nKey": "TENANT_TENANTS_KL_PARAVUR",
          "code": "kl.paravur",
          "name": "Paravur Municipality",
          "description": "Paravur",
          "logoId": "",
          "imageId": null,
          "domainUrl": "paravurmunicipality.lsgkerala.gov.in",
          "twitterUrl": null,
          "facebookUrl": null,
          "emailId": "",
          "OfficeTimings": {
              "Mon - Fri": "9.00 AM - 6.00 PM"
          },
          "city": {
              "name": "Paravur Municipality",
              "localName": "പരവൂര്‍ മുനിസിപ്പാലിറ്റി",
              "districtCode": "559",
              "districtName": "Kollam",
              "regionName": "Kerala",
              "ulbGrade": "Municipality Grade 2",
              "longitude": null,
              "latitude": null,
              "captcha": null,
              "shapeFileLocation": null,
              "code": "M020200",
              "ddrName": "Paravur"
          },
          "address": "Municiplaity Kerala",
          "pincode": [],
          "contactNumber": "111111",
          "pdfHeader": "",
          "pdfContactDetails": ""
      },
      {
          "i18nKey": "TENANT_TENANTS_KL_PATHANAMTHITTA",
          "code": "kl.pathanamthitta",
          "name": "Pathanamthitta Municipality",
          "description": "Pathanamthitta",
          "logoId": "",
          "imageId": null,
          "domainUrl": "pathanamthittamunicipality.lsgkerala.gov.in",
          "twitterUrl": null,
          "facebookUrl": null,
          "emailId": "",
          "OfficeTimings": {
              "Mon - Fri": "9.00 AM - 6.00 PM"
          },
          "city": {
              "name": "Pathanamthitta Municipality",
              "localName": "പത്തനംതിട്ട മുനിസിപ്പാലിറ്റി",
              "districtCode": "564",
              "districtName": "Pathanamthitta",
              "regionName": "Kerala",
              "ulbGrade": "Municipality Grade 2",
              "longitude": null,
              "latitude": null,
              "captcha": null,
              "shapeFileLocation": null,
              "code": "M030300",
              "ddrName": "Pathanamthitta"
          },
          "address": "Municiplaity Kerala",
          "pincode": [],
          "contactNumber": "111111",
          "pdfHeader": "",
          "pdfContactDetails": ""
      },
      {
          "i18nKey": "TENANT_TENANTS_KL_PERINTHALMANNA",
          "code": "kl.perinthalmanna",
          "name": "Perinthalmanna Municipality",
          "description": "Perinthalmanna",
          "logoId": "",
          "imageId": null,
          "domainUrl": "perinthalmannamunicipality.lsgkerala.gov.in",
          "twitterUrl": null,
          "facebookUrl": null,
          "emailId": "",
          "OfficeTimings": {
              "Mon - Fri": "9.00 AM - 6.00 PM"
          },
          "city": {
              "name": "Perinthalmanna Municipality",
              "localName": "പെരിന്തല്‍മണ്ണ മുനിസിപ്പാലിറ്റി",
              "districtCode": "562",
              "districtName": "Malappuram",
              "regionName": "Kerala",
              "ulbGrade": "Municipality Grade 2",
              "longitude": null,
              "latitude": null,
              "captcha": null,
              "shapeFileLocation": null,
              "code": "M100100",
              "ddrName": "Perinthalmanna"
          },
          "address": "Municiplaity Kerala",
          "pincode": [],
          "contactNumber": "111111",
          "pdfHeader": "",
          "pdfContactDetails": ""
      },
      {
          "i18nKey": "TENANT_TENANTS_KL_PUNALUR",
          "code": "kl.punalur",
          "name": "Punalur Municipality",
          "description": "Punalur",
          "logoId": "",
          "imageId": null,
          "domainUrl": "punalurmunicipality.lsgkerala.gov.in",
          "twitterUrl": null,
          "facebookUrl": null,
          "emailId": "",
          "OfficeTimings": {
              "Mon - Fri": "9.00 AM - 6.00 PM"
          },
          "city": {
              "name": "Punalur Municipality",
              "localName": "പുനലൂര്‍ മുനിസിപ്പാലിറ്റി",
              "districtCode": "559",
              "districtName": "Kollam",
              "regionName": "Kerala",
              "ulbGrade": "Municipality Grade 2",
              "longitude": null,
              "latitude": null,
              "captcha": null,
              "shapeFileLocation": null,
              "code": "M020300",
              "ddrName": "Punalur"
          },
          "address": "Municiplaity Kerala",
          "pincode": [],
          "contactNumber": "111111",
          "pdfHeader": "",
          "pdfContactDetails": ""
      },
      {
          "i18nKey": "TENANT_TENANTS_KL_SHORANUR",
          "code": "kl.shoranur",
          "name": "Shoranur Municipality",
          "description": "Shoranur",
          "logoId": "",
          "imageId": null,
          "domainUrl": "shornurmunicipality.lsgkerala.gov.in",
          "twitterUrl": null,
          "facebookUrl": null,
          "emailId": "",
          "OfficeTimings": {
              "Mon - Fri": "9.00 AM - 6.00 PM"
          },
          "city": {
              "name": "Shoranur Municipality",
              "localName": "ഷൊര്‍ണ്ണൂര്‍ മുനിസിപ്പാലിറ്റി",
              "districtCode": "563",
              "districtName": "Palakkad",
              "regionName": "Kerala",
              "ulbGrade": "Municipality Grade 2",
              "longitude": null,
              "latitude": null,
              "captcha": null,
              "shapeFileLocation": null,
              "code": "M090200",
              "ddrName": "Shoranur"
          },
          "address": "Municiplaity Kerala",
          "pincode": [],
          "contactNumber": "111111",
          "pdfHeader": "",
          "pdfContactDetails": ""
      },
      {
          "i18nKey": "TENANT_TENANTS_KL_VARKALA",
          "code": "kl.varkala",
          "name": "Varkala Municipality",
          "description": "Varkala",
          "logoId": "",
          "imageId": null,
          "domainUrl": "varkalamunicipality.lsgkerala.gov.in",
          "twitterUrl": null,
          "facebookUrl": null,
          "emailId": "",
          "OfficeTimings": {
              "Mon - Fri": "9.00 AM - 6.00 PM"
          },
          "city": {
              "name": "Varkala Municipality",
              "localName": "വര്‍ക്കല മുനിസിപ്പാലിറ്റി",
              "districtCode": "565",
              "districtName": "Thiruvananthapuram",
              "regionName": "Kerala",
              "ulbGrade": "Municipality Grade 2",
              "longitude": null,
              "latitude": null,
              "captcha": null,
              "shapeFileLocation": null,
              "code": "M010100",
              "ddrName": "Varkala"
          },
          "address": "Municiplaity Kerala",
          "pincode": [],
          "contactNumber": "111111",
          "pdfHeader": "",
          "pdfContactDetails": ""
      },
      {
          "i18nKey": "TENANT_TENANTS_KL_ANTHOOR",
          "code": "kl.anthoor",
          "name": "Anthoor Municipality",
          "description": "Anthoor",
          "logoId": "",
          "imageId": null,
          "domainUrl": "anthoormunicipality.lsgkerala.gov.in",
          "twitterUrl": null,
          "facebookUrl": null,
          "emailId": "",
          "OfficeTimings": {
              "Mon - Fri": "9.00 AM - 6.00 PM"
          },
          "city": {
              "name": "Anthoor Municipality",
              "localName": "ആന്തൂര്‍ മുനിസിപ്പാലിറ്റി",
              "districtCode": "557",
              "districtName": "Kannur",
              "regionName": "Kerala",
              "ulbGrade": "Municipality Grade 3",
              "longitude": null,
              "latitude": null,
              "captcha": null,
              "shapeFileLocation": null,
              "code": "M130900",
              "ddrName": "Anthoor"
          },
          "address": "Municiplaity Kerala",
          "pincode": [],
          "contactNumber": "111111",
          "pdfHeader": "",
          "pdfContactDetails": ""
      },
      {
          "i18nKey": "TENANT_TENANTS_KL_CHENGANNUR",
          "code": "kl.chengannur",
          "name": "Chengannur Municipality",
          "description": "Chengannur",
          "logoId": "",
          "imageId": null,
          "domainUrl": "chengannurmunicipality.lsgkerala.gov.in",
          "twitterUrl": null,
          "facebookUrl": null,
          "emailId": "",
          "OfficeTimings": {
              "Mon - Fri": "9.00 AM - 6.00 PM"
          },
          "city": {
              "name": "Chengannur Municipality",
              "localName": "ചെങ്ങന്നൂര്‍ മുനിസിപ്പാലിറ്റി",
              "districtCode": "554",
              "districtName": "Alappuzha",
              "regionName": "Kerala",
              "ulbGrade": "Municipality Grade 3",
              "longitude": null,
              "latitude": null,
              "captcha": null,
              "shapeFileLocation": null,
              "code": "M040100",
              "ddrName": "Chengannur"
          },
          "address": "Municiplaity Kerala",
          "pincode": [],
          "contactNumber": "111111",
          "pdfHeader": "",
          "pdfContactDetails": ""
      },
      {
          "i18nKey": "TENANT_TENANTS_KL_CHERPULASSERY",
          "code": "kl.cherpulassery",
          "name": "Cherpulassery Municipality",
          "description": "Cherpulassery",
          "logoId": "",
          "imageId": null,
          "domainUrl": "cherpulasserymunicipality.lsgkerala.gov.in",
          "twitterUrl": null,
          "facebookUrl": null,
          "emailId": "",
          "OfficeTimings": {
              "Mon - Fri": "9.00 AM - 6.00 PM"
          },
          "city": {
              "name": "Cherpulassery Municipality",
              "localName": "ചെര്‍പുളശ്ശേരി മുനിസിപ്പാലിറ്റി",
              "districtCode": "563",
              "districtName": "Palakkad",
              "regionName": "Kerala",
              "ulbGrade": "Municipality Grade 3",
              "longitude": null,
              "latitude": null,
              "captcha": null,
              "shapeFileLocation": null,
              "code": "M090600",
              "ddrName": "Cherpulassery"
          },
          "address": "Municiplaity Kerala",
          "pincode": [],
          "contactNumber": "111111",
          "pdfHeader": "",
          "pdfContactDetails": ""
      },
      {
          "i18nKey": "TENANT_TENANTS_KL_ERATTUPETTA",
          "code": "kl.erattupetta",
          "name": "Erattupetta Municipality",
          "description": "Erattupetta",
          "logoId": "",
          "imageId": null,
          "domainUrl": "erattupettamunicipality.lsgkerala.gov.in",
          "twitterUrl": null,
          "facebookUrl": null,
          "emailId": "",
          "OfficeTimings": {
              "Mon - Fri": "9.00 AM - 6.00 PM"
          },
          "city": {
              "name": "Erattupetta Municipality",
              "localName": "ഈരാറ്റുപേട്ട മുനിസിപ്പാലിറ്റി",
              "districtCode": "560",
              "districtName": "Kottayam",
              "regionName": "Kerala",
              "ulbGrade": "Municipality Grade 3",
              "longitude": null,
              "latitude": null,
              "captcha": null,
              "shapeFileLocation": null,
              "code": "M050600",
              "ddrName": "Erattupetta"
          },
          "address": "Municiplaity Kerala",
          "pincode": [],
          "contactNumber": "111111",
          "pdfHeader": "",
          "pdfContactDetails": ""
      },
      {
          "i18nKey": "TENANT_TENANTS_KL_ETTUMANOOR",
          "code": "kl.ettumanoor",
          "name": "Ettumanoor Municipality",
          "description": "Ettumanoor",
          "logoId": "",
          "imageId": null,
          "domainUrl": "ettumanoormunicipality.lsgkerala.gov.in",
          "twitterUrl": null,
          "facebookUrl": null,
          "emailId": "",
          "OfficeTimings": {
              "Mon - Fri": "9.00 AM - 6.00 PM"
          },
          "city": {
              "name": "Ettumanoor Municipality",
              "localName": "ഏറ്റുമാനൂര്‍ മുനിസിപ്പാലിറ്റി",
              "districtCode": "560",
              "districtName": "Kottayam",
              "regionName": "Kerala",
              "ulbGrade": "Municipality Grade 3",
              "longitude": null,
              "latitude": null,
              "captcha": null,
              "shapeFileLocation": null,
              "code": "M050500",
              "ddrName": "Ettumanoor"
          },
          "address": "Municiplaity Kerala",
          "pincode": [],
          "contactNumber": "111111",
          "pdfHeader": "",
          "pdfContactDetails": ""
      },
      {
          "i18nKey": "TENANT_TENANTS_KL_FEROKE",
          "code": "kl.feroke",
          "name": "Feroke Municipality",
          "description": "Feroke",
          "logoId": "",
          "imageId": null,
          "domainUrl": "ferokemunicipality.lsgkerala.gov.in",
          "twitterUrl": null,
          "facebookUrl": null,
          "emailId": "",
          "OfficeTimings": {
              "Mon - Fri": "9.00 AM - 6.00 PM"
          },
          "city": {
              "name": "Feroke Municipality",
              "localName": "ഫറോക്ക് മുനിസിപ്പാലിറ്റി",
              "districtCode": "561",
              "districtName": "Kozhikkode",
              "regionName": "Kerala",
              "ulbGrade": "Municipality Grade 3",
              "longitude": null,
              "latitude": null,
              "captcha": null,
              "shapeFileLocation": null,
              "code": "M110700",
              "ddrName": "Feroke"
          },
          "address": "Municiplaity Kerala",
          "pincode": [],
          "contactNumber": "111111",
          "pdfHeader": "",
          "pdfContactDetails": ""
      },
      {
          "i18nKey": "TENANT_TENANTS_KL_HARIPAD",
          "code": "kl.haripad",
          "name": "Haripad Municipality",
          "description": "Haripad",
          "logoId": "",
          "imageId": null,
          "domainUrl": "haripadmunicipality.lsgkerala.gov.in",
          "twitterUrl": null,
          "facebookUrl": null,
          "emailId": "",
          "OfficeTimings": {
              "Mon - Fri": "9.00 AM - 6.00 PM"
          },
          "city": {
              "name": "Haripad Municipality",
              "localName": "ഹരിപ്പാട് മുനിസിപ്പാലിറ്റി",
              "districtCode": "554",
              "districtName": "Alappuzha",
              "regionName": "Kerala",
              "ulbGrade": "Municipality Grade 3",
              "longitude": null,
              "latitude": null,
              "captcha": null,
              "shapeFileLocation": null,
              "code": "M040600",
              "ddrName": "Haripad"
          },
          "address": "Municiplaity Kerala",
          "pincode": [],
          "contactNumber": "111111",
          "pdfHeader": "",
          "pdfContactDetails": ""
      },
      {
          "i18nKey": "TENANT_TENANTS_KL_IRITTY",
          "code": "kl.iritty",
          "name": "Iritty Municipality",
          "description": "Iritty",
          "logoId": "",
          "imageId": null,
          "domainUrl": "irittymunicipality.lsgkerala.gov.in",
          "twitterUrl": null,
          "facebookUrl": null,
          "emailId": "",
          "OfficeTimings": {
              "Mon - Fri": "9.00 AM - 6.00 PM"
          },
          "city": {
              "name": "Iritty Municipality",
              "localName": "ഇരിട്ടി മുനിസിപ്പാലിറ്റി",
              "districtCode": "557",
              "districtName": "Kannur",
              "regionName": "Kerala",
              "ulbGrade": "Municipality Grade 3",
              "longitude": null,
              "latitude": null,
              "captcha": null,
              "shapeFileLocation": null,
              "code": "M130800",
              "ddrName": "Iritty"
          },
          "address": "Municiplaity Kerala",
          "pincode": [],
          "contactNumber": "111111",
          "pdfHeader": "",
          "pdfContactDetails": ""
      },
      {
          "i18nKey": "TENANT_TENANTS_KL_KATTAPPANA",
          "code": "kl.kattappana",
          "name": "Kattappana Municipality",
          "description": "Kattappana",
          "logoId": "",
          "imageId": null,
          "domainUrl": "kattapanamunicipality.lsgkerala.gov.in",
          "twitterUrl": null,
          "facebookUrl": null,
          "emailId": "",
          "OfficeTimings": {
              "Mon - Fri": "9.00 AM - 6.00 PM"
          },
          "city": {
              "name": "Kattappana Municipality",
              "localName": "കട്ടപ്പന മുനിസിപ്പാലിറ്റി",
              "districtCode": "556",
              "districtName": "Idukki",
              "regionName": "Kerala",
              "ulbGrade": "Municipality Grade 3",
              "longitude": null,
              "latitude": null,
              "captcha": null,
              "shapeFileLocation": null,
              "code": "M060200",
              "ddrName": "Kattappana"
          },
          "address": "Municiplaity Kerala",
          "pincode": [],
          "contactNumber": "111111",
          "pdfHeader": "",
          "pdfContactDetails": ""
      },
      {
          "i18nKey": "TENANT_TENANTS_KL_KODUVALLY",
          "code": "kl.koduvally",
          "name": "Koduvally Municipality",
          "description": "Koduvally",
          "logoId": "",
          "imageId": null,
          "domainUrl": "koduvallymunicipality.lsgkerala.gov.in",
          "twitterUrl": null,
          "facebookUrl": null,
          "emailId": "",
          "OfficeTimings": {
              "Mon - Fri": "9.00 AM - 6.00 PM"
          },
          "city": {
              "name": "Koduvally Municipality",
              "localName": "കൊടുവള്ളി മുനിസിപ്പാലിറ്റി",
              "districtCode": "561",
              "districtName": "Kozhikkode",
              "regionName": "Kerala",
              "ulbGrade": "Municipality Grade 3",
              "longitude": null,
              "latitude": null,
              "captcha": null,
              "shapeFileLocation": null,
              "code": "M110400",
              "ddrName": "Koduvally"
          },
          "address": "Municiplaity Kerala",
          "pincode": [],
          "contactNumber": "111111",
          "pdfHeader": "",
          "pdfContactDetails": ""
      },
      {
          "i18nKey": "TENANT_TENANTS_KL_KONDOTTY",
          "code": "kl.kondotty",
          "name": "Kondotty Municipality",
          "description": "Kondotty",
          "logoId": "",
          "imageId": null,
          "domainUrl": "kondottymunicipality.lsgkerala.gov.in",
          "twitterUrl": null,
          "facebookUrl": null,
          "emailId": "",
          "OfficeTimings": {
              "Mon - Fri": "9.00 AM - 6.00 PM"
          },
          "city": {
              "name": "Kondotty Municipality",
              "localName": "കൊണ്ടോട്ടി മുനിസിപ്പാലിറ്റി",
              "districtCode": "562",
              "districtName": "Malappuram",
              "regionName": "Kerala",
              "ulbGrade": "Municipality Grade 3",
              "longitude": null,
              "latitude": null,
              "captcha": null,
              "shapeFileLocation": null,
              "code": "M100800",
              "ddrName": "Kondotty"
          },
          "address": "Municiplaity Kerala",
          "pincode": [],
          "contactNumber": "111111",
          "pdfHeader": "",
          "pdfContactDetails": ""
      },
      {
          "i18nKey": "TENANT_TENANTS_KL_KOOTHATTUKULAM",
          "code": "kl.koothattukulam",
          "name": "Koothattukulam Municipality",
          "description": "Koothattukulam",
          "logoId": "",
          "imageId": null,
          "domainUrl": "koothatukulammunicipality.lsgkerala.gov.in",
          "twitterUrl": null,
          "facebookUrl": null,
          "emailId": "",
          "OfficeTimings": {
              "Mon - Fri": "9.00 AM - 6.00 PM"
          },
          "city": {
              "name": "Koothattukulam Municipality",
              "localName": "കൂത്താട്ടുകുളം മുനിസിപ്പാലിറ്റി",
              "districtCode": "555",
              "districtName": "Eranakulam",
              "regionName": "Kerala",
              "ulbGrade": "Municipality Grade 3",
              "longitude": null,
              "latitude": null,
              "captcha": null,
              "shapeFileLocation": null,
              "code": "M071200",
              "ddrName": "Koothattukulam"
          },
          "address": "Municiplaity Kerala",
          "pincode": [],
          "contactNumber": "111111",
          "pdfHeader": "",
          "pdfContactDetails": ""
      },
      {
          "i18nKey": "TENANT_TENANTS_KL_KOTTARAKARA",
          "code": "kl.kottarakara",
          "name": "Kottarakara Municipality",
          "description": "Kottarakara",
          "logoId": "",
          "imageId": null,
          "domainUrl": "kottarakaramunicipality.lsgkerala.gov.in",
          "twitterUrl": null,
          "facebookUrl": null,
          "emailId": "",
          "OfficeTimings": {
              "Mon - Fri": "9.00 AM - 6.00 PM"
          },
          "city": {
              "name": "Kottarakara Municipality",
              "localName": "കൊട്ടാരക്കര മുനിസിപ്പാലിറ്റി",
              "districtCode": "559",
              "districtName": "Kollam",
              "regionName": "Kerala",
              "ulbGrade": "Municipality Grade 3",
              "longitude": null,
              "latitude": null,
              "captcha": null,
              "shapeFileLocation": null,
              "code": "M020400",
              "ddrName": "Kottarakara"
          },
          "address": "Municiplaity Kerala",
          "pincode": [],
          "contactNumber": "111111",
          "pdfHeader": "",
          "pdfContactDetails": ""
      },
      {
          "i18nKey": "TENANT_TENANTS_KL_MANANTHAVADY",
          "code": "kl.mananthavady",
          "name": "Mananthavady Municipality",
          "description": "Mananthavady",
          "logoId": "",
          "imageId": null,
          "domainUrl": "mananthavadymunicipality.lsgkerala.gov.in",
          "twitterUrl": null,
          "facebookUrl": null,
          "emailId": "",
          "OfficeTimings": {
              "Mon - Fri": "9.00 AM - 6.00 PM"
          },
          "city": {
              "name": "Mananthavady Municipality",
              "localName": "മാനന്തവാടി മുനിസിപ്പാലിറ്റി",
              "districtCode": "567",
              "districtName": "Wayanad",
              "regionName": "Kerala",
              "ulbGrade": "Municipality Grade 3",
              "longitude": null,
              "latitude": null,
              "captcha": null,
              "shapeFileLocation": null,
              "code": "M120200",
              "ddrName": "Mananthavady"
          },
          "address": "Municiplaity Kerala",
          "pincode": [],
          "contactNumber": "111111",
          "pdfHeader": "",
          "pdfContactDetails": ""
      },
      {
          "i18nKey": "TENANT_TENANTS_KL_MANNARKAD",
          "code": "kl.mannarkad",
          "name": "Mannarkad Municipality",
          "description": "Mannarkad",
          "logoId": "",
          "imageId": null,
          "domainUrl": "mannarkkadmunicipality.lsgkerala.gov.in",
          "twitterUrl": null,
          "facebookUrl": null,
          "emailId": "",
          "OfficeTimings": {
              "Mon - Fri": "9.00 AM - 6.00 PM"
          },
          "city": {
              "name": "Mannarkad Municipality",
              "localName": "മണ്ണാര്‍ക്കാട് മുനിസിപ്പാലിറ്റി",
              "districtCode": "563",
              "districtName": "Palakkad",
              "regionName": "Kerala",
              "ulbGrade": "Municipality Grade 3",
              "longitude": null,
              "latitude": null,
              "captcha": null,
              "shapeFileLocation": null,
              "code": "M090700",
              "ddrName": "Mannarkad"
          },
          "address": "Municiplaity Kerala",
          "pincode": [],
          "contactNumber": "111111",
          "pdfHeader": "",
          "pdfContactDetails": ""
      },
      {
          "i18nKey": "TENANT_TENANTS_KL_MUKKAM",
          "code": "kl.mukkam",
          "name": "Mukkam Municipality",
          "description": "Mukkam",
          "logoId": "",
          "imageId": null,
          "domainUrl": "mukkammunicipality.lsgkerala.gov.in",
          "twitterUrl": null,
          "facebookUrl": null,
          "emailId": "",
          "OfficeTimings": {
              "Mon - Fri": "9.00 AM - 6.00 PM"
          },
          "city": {
              "name": "Mukkam Municipality",
              "localName": "മുക്കം മുനിസിപ്പാലിറ്റി",
              "districtCode": "561",
              "districtName": "Kozhikkode",
              "regionName": "Kerala",
              "ulbGrade": "Municipality Grade 3",
              "longitude": null,
              "latitude": null,
              "captcha": null,
              "shapeFileLocation": null,
              "code": "M110500",
              "ddrName": "Mukkam"
          },
          "address": "Municiplaity Kerala",
          "pincode": [],
          "contactNumber": "111111",
          "pdfHeader": "",
          "pdfContactDetails": ""
      },
      {
          "i18nKey": "TENANT_TENANTS_KL_PANDALAM",
          "code": "kl.pandalam",
          "name": "Pandalam Municipality",
          "description": "Pandalam",
          "logoId": "",
          "imageId": null,
          "domainUrl": "pandalammunicipality.lsgkerala.gov.in",
          "twitterUrl": null,
          "facebookUrl": null,
          "emailId": "",
          "OfficeTimings": {
              "Mon - Fri": "9.00 AM - 6.00 PM"
          },
          "city": {
              "name": "Pandalam Municipality",
              "localName": "പന്തളം മുനിസിപ്പാലിറ്റി",
              "districtCode": "564",
              "districtName": "Pathanamthitta",
              "regionName": "Kerala",
              "ulbGrade": "Municipality Grade 3",
              "longitude": null,
              "latitude": null,
              "captcha": null,
              "shapeFileLocation": null,
              "code": "M030400",
              "ddrName": "Pandalam"
          },
          "address": "Municiplaity Kerala",
          "pincode": [],
          "contactNumber": "111111",
          "pdfHeader": "",
          "pdfContactDetails": ""
      },
      {
          "i18nKey": "TENANT_TENANTS_KL_PANOOR",
          "code": "kl.panoor",
          "name": "Panoor Municipality",
          "description": "Panoor",
          "logoId": "",
          "imageId": null,
          "domainUrl": "panoormunicipality.lsgkerala.gov.in",
          "twitterUrl": null,
          "facebookUrl": null,
          "emailId": "",
          "OfficeTimings": {
              "Mon - Fri": "9.00 AM - 6.00 PM"
          },
          "city": {
              "name": "Panoor Municipality",
              "localName": "പാനൂര്‍ മുനിസിപ്പാലിറ്റി",
              "districtCode": "557",
              "districtName": "Kannur",
              "regionName": "Kerala",
              "ulbGrade": "Municipality Grade 3",
              "longitude": null,
              "latitude": null,
              "captcha": null,
              "shapeFileLocation": null,
              "code": "M130700",
              "ddrName": "Panoor"
          },
          "address": "Municiplaity Kerala",
          "pincode": [],
          "contactNumber": "111111",
          "pdfHeader": "",
          "pdfContactDetails": ""
      },
      {
          "i18nKey": "TENANT_TENANTS_KL_PARAPPANANGADI",
          "code": "kl.parappanangadi",
          "name": "Parappanangadi Municipality",
          "description": "Parappanangadi",
          "logoId": "",
          "imageId": null,
          "domainUrl": "parappanangadimunicipality.lsgkerala.gov.in",
          "twitterUrl": null,
          "facebookUrl": null,
          "emailId": "",
          "OfficeTimings": {
              "Mon - Fri": "9.00 AM - 6.00 PM"
          },
          "city": {
              "name": "Parappanangadi Municipality",
              "localName": "പരപ്പനങ്ങാടി മുനിസിപ്പാലിറ്റി",
              "districtCode": "562",
              "districtName": "Malappuram",
              "regionName": "Kerala",
              "ulbGrade": "Municipality Grade 3",
              "longitude": null,
              "latitude": null,
              "captcha": null,
              "shapeFileLocation": null,
              "code": "M101100",
              "ddrName": "Parappanangadi"
          },
          "address": "Municiplaity Kerala",
          "pincode": [],
          "contactNumber": "111111",
          "pdfHeader": "",
          "pdfContactDetails": ""
      },
      {
          "i18nKey": "TENANT_TENANTS_KL_PATTAMBI",
          "code": "kl.pattambi",
          "name": "Pattambi Municipality",
          "description": "Pattambi",
          "logoId": "",
          "imageId": null,
          "domainUrl": "pattambimunicipality.lsgkerala.gov.in",
          "twitterUrl": null,
          "facebookUrl": null,
          "emailId": "",
          "OfficeTimings": {
              "Mon - Fri": "9.00 AM - 6.00 PM"
          },
          "city": {
              "name": "Pattambi Municipality",
              "localName": "പട്ടാമ്പി മുനിസിപ്പാലിറ്റി",
              "districtCode": "563",
              "districtName": "Palakkad",
              "regionName": "Kerala",
              "ulbGrade": "Municipality Grade 3",
              "longitude": null,
              "latitude": null,
              "captcha": null,
              "shapeFileLocation": null,
              "code": "M090500",
              "ddrName": "Pattambi"
          },
          "address": "Municiplaity Kerala",
          "pincode": [],
          "contactNumber": "111111",
          "pdfHeader": "",
          "pdfContactDetails": ""
      },
      {
          "i18nKey": "TENANT_TENANTS_KL_PAYYOLI",
          "code": "kl.payyoli",
          "name": "Payyoli Municipality",
          "description": "Payyoli",
          "logoId": "",
          "imageId": null,
          "domainUrl": "payyolimunicipality.lsgkerala.gov.in",
          "twitterUrl": null,
          "facebookUrl": null,
          "emailId": "",
          "OfficeTimings": {
              "Mon - Fri": "9.00 AM - 6.00 PM"
          },
          "city": {
              "name": "Payyoli Municipality",
              "localName": "പയ്യോളി മുനിസിപ്പാലിറ്റി",
              "districtCode": "561",
              "districtName": "Kozhikkode",
              "regionName": "Kerala",
              "ulbGrade": "Municipality Grade 3",
              "longitude": null,
              "latitude": null,
              "captcha": null,
              "shapeFileLocation": null,
              "code": "M110300",
              "ddrName": "Payyoli"
          },
          "address": "Municiplaity Kerala",
          "pincode": [],
          "contactNumber": "111111",
          "pdfHeader": "",
          "pdfContactDetails": ""
      },
      {
          "i18nKey": "TENANT_TENANTS_KL_PIRAVOM",
          "code": "kl.piravom",
          "name": "Piravom Municipality",
          "description": "Piravom",
          "logoId": "",
          "imageId": null,
          "domainUrl": "piravommunicipality.lsgkerala.gov.in",
          "twitterUrl": null,
          "facebookUrl": null,
          "emailId": "",
          "OfficeTimings": {
              "Mon - Fri": "9.00 AM - 6.00 PM"
          },
          "city": {
              "name": "Piravom Municipality",
              "localName": "പിറവം മുനിസിപ്പാലിറ്റി",
              "districtCode": "555",
              "districtName": "Eranakulam",
              "regionName": "Kerala",
              "ulbGrade": "Municipality Grade 3",
              "longitude": null,
              "latitude": null,
              "captcha": null,
              "shapeFileLocation": null,
              "code": "M071300",
              "ddrName": "Piravom"
          },
          "address": "Municiplaity Kerala",
          "pincode": [],
          "contactNumber": "111111",
          "pdfHeader": "",
          "pdfContactDetails": ""
      },
      {
          "i18nKey": "TENANT_TENANTS_KL_RAMANATTUKARA",
          "code": "kl.ramanattukara",
          "name": "Ramanattukara Municipality",
          "description": "Ramanattukara",
          "logoId": "",
          "imageId": null,
          "domainUrl": "ramanattukaramunicipality.lsgkerala.gov.in",
          "twitterUrl": null,
          "facebookUrl": null,
          "emailId": "",
          "OfficeTimings": {
              "Mon - Fri": "9.00 AM - 6.00 PM"
          },
          "city": {
              "name": "Ramanattukara Municipality",
              "localName": "രാമനാട്ടുകര മുനിസിപ്പാലിറ്റി",
              "districtCode": "561",
              "districtName": "Kozhikkode",
              "regionName": "Kerala",
              "ulbGrade": "Municipality Grade 3",
              "longitude": null,
              "latitude": null,
              "captcha": null,
              "shapeFileLocation": null,
              "code": "M110600",
              "ddrName": "Ramanattukara"
          },
          "address": "Municiplaity Kerala",
          "pincode": [],
          "contactNumber": "111111",
          "pdfHeader": "",
          "pdfContactDetails": ""
      },
      {
          "i18nKey": "TENANT_TENANTS_KL_SREEKANDAPURAM",
          "code": "kl.sreekandapuram",
          "name": "Sreekandapuram Municipality",
          "description": "Sreekandapuram",
          "logoId": "",
          "imageId": null,
          "domainUrl": "sreekandapurammunicipality.lsgkerala.gov.in",
          "twitterUrl": null,
          "facebookUrl": null,
          "emailId": "",
          "OfficeTimings": {
              "Mon - Fri": "9.00 AM - 6.00 PM"
          },
          "city": {
              "name": "Sreekandapuram Municipality",
              "localName": "ശ്രീകണ്ഠാപുരം മുനിസിപ്പാലിറ്റി",
              "districtCode": "557",
              "districtName": "Kannur",
              "regionName": "Kerala",
              "ulbGrade": "Municipality Grade 3",
              "longitude": null,
              "latitude": null,
              "captcha": null,
              "shapeFileLocation": null,
              "code": "M130600",
              "ddrName": "Sreekandapuram"
          },
          "address": "Municiplaity Kerala",
          "pincode": [],
          "contactNumber": "111111",
          "pdfHeader": "",
          "pdfContactDetails": ""
      },
      {
          "i18nKey": "TENANT_TENANTS_KL_SULTHAN",
          "code": "kl.sulthan",
          "name": "Sulthan Bathery Municipality",
          "description": "Sulthan",
          "logoId": "",
          "imageId": null,
          "domainUrl": "sulthanbatherymunicipality.lsgkerala.gov.in",
          "twitterUrl": null,
          "facebookUrl": null,
          "emailId": "",
          "OfficeTimings": {
              "Mon - Fri": "9.00 AM - 6.00 PM"
          },
          "city": {
              "name": "Sulthan Bathery Municipality",
              "localName": "സുല്‍ത്താന്‍ ബത്തേരി മുനിസിപ്പാലിറ്റി",
              "districtCode": "567",
              "districtName": "Wayanad",
              "regionName": "Kerala",
              "ulbGrade": "Municipality Grade 3",
              "longitude": null,
              "latitude": null,
              "captcha": null,
              "shapeFileLocation": null,
              "code": "M120300",
              "ddrName": "Sulthan"
          },
          "address": "Municiplaity Kerala",
          "pincode": [],
          "contactNumber": "111111",
          "pdfHeader": "",
          "pdfContactDetails": ""
      },
      {
          "i18nKey": "TENANT_TENANTS_KL_TANUR",
          "code": "kl.tanur",
          "name": "Tanur Municipality",
          "description": "Tanur",
          "logoId": "",
          "imageId": null,
          "domainUrl": "tanurmunicipality.lsgkerala.gov.in",
          "twitterUrl": null,
          "facebookUrl": null,
          "emailId": "",
          "OfficeTimings": {
              "Mon - Fri": "9.00 AM - 6.00 PM"
          },
          "city": {
              "name": "Tanur Municipality",
              "localName": "താനൂര്‍ മുനിസിപ്പാലിറ്റി",
              "districtCode": "562",
              "districtName": "Malappuram",
              "regionName": "Kerala",
              "ulbGrade": "Municipality Grade 3",
              "longitude": null,
              "latitude": null,
              "captcha": null,
              "shapeFileLocation": null,
              "code": "M101200",
              "ddrName": "Tanur"
          },
          "address": "Municiplaity Kerala",
          "pincode": [],
          "contactNumber": "111111",
          "pdfHeader": "",
          "pdfContactDetails": ""
      },
      {
          "i18nKey": "TENANT_TENANTS_KL_TIRURANGADI",
          "code": "kl.tirurangadi",
          "name": "Tirurangadi Municipality",
          "description": "Tirurangadi",
          "logoId": "",
          "imageId": null,
          "domainUrl": "tirurangadimunicipality.lsgkerala.gov.in",
          "twitterUrl": null,
          "facebookUrl": null,
          "emailId": "",
          "OfficeTimings": {
              "Mon - Fri": "9.00 AM - 6.00 PM"
          },
          "city": {
              "name": "Tirurangadi Municipality",
              "localName": "തിരൂരങ്ങാടി മുനിസിപ്പാലിറ്റി",
              "districtCode": "562",
              "districtName": "Malappuram",
              "regionName": "Kerala",
              "ulbGrade": "Municipality Grade 3",
              "longitude": null,
              "latitude": null,
              "captcha": null,
              "shapeFileLocation": null,
              "code": "M101000",
              "ddrName": "Tirurangadi"
          },
          "address": "Municiplaity Kerala",
          "pincode": [],
          "contactNumber": "111111",
          "pdfHeader": "",
          "pdfContactDetails": ""
      },
      {
          "i18nKey": "TENANT_TENANTS_KL_VAIKOM",
          "code": "kl.vaikom",
          "name": "Vaikom Municipality",
          "description": "Vaikom",
          "logoId": "",
          "imageId": null,
          "domainUrl": "vaikommunicipality.lsgkerala.gov.in",
          "twitterUrl": null,
          "facebookUrl": null,
          "emailId": "",
          "OfficeTimings": {
              "Mon - Fri": "9.00 AM - 6.00 PM"
          },
          "city": {
              "name": "Vaikom Municipality",
              "localName": "വൈക്കം മുനിസിപ്പാലിറ്റി",
              "districtCode": "560",
              "districtName": "Kottayam",
              "regionName": "Kerala",
              "ulbGrade": "Municipality Grade 3",
              "longitude": null,
              "latitude": null,
              "captcha": null,
              "shapeFileLocation": null,
              "code": "M050200",
              "ddrName": "Vaikom"
          },
          "address": "Municiplaity Kerala",
          "pincode": [],
          "contactNumber": "111111",
          "pdfHeader": "",
          "pdfContactDetails": ""
      },
      {
          "i18nKey": "TENANT_TENANTS_KL_VALANCHERY",
          "code": "kl.valanchery",
          "name": "Valanchery Municipality",
          "description": "Valanchery",
          "logoId": "",
          "imageId": null,
          "domainUrl": "valancherymunicipality.lsgkerala.gov.in",
          "twitterUrl": null,
          "facebookUrl": null,
          "emailId": "",
          "OfficeTimings": {
              "Mon - Fri": "9.00 AM - 6.00 PM"
          },
          "city": {
              "name": "Valanchery Municipality",
              "localName": "വളാഞ്ചേരി മുനിസിപ്പാലിറ്റി",
              "districtCode": "562",
              "districtName": "Malappuram",
              "regionName": "Kerala",
              "ulbGrade": "Municipality Grade 3",
              "longitude": null,
              "latitude": null,
              "captcha": null,
              "shapeFileLocation": null,
              "code": "M100900",
              "ddrName": "Valanchery"
          },
          "address": "Municiplaity Kerala",
          "pincode": [],
          "contactNumber": "111111",
          "pdfHeader": "",
          "pdfContactDetails": ""
      },
      {
          "i18nKey": "TENANT_TENANTS_KL_WADAKANCHERY",
          "code": "kl.wadakanchery",
          "name": "Wadakanchery Municipality",
          "description": "Wadakanchery",
          "logoId": "",
          "imageId": null,
          "domainUrl": "wadakancherymunicipality.lsgkerala.gov.in",
          "twitterUrl": null,
          "facebookUrl": null,
          "emailId": "",
          "OfficeTimings": {
              "Mon - Fri": "9.00 AM - 6.00 PM"
          },
          "city": {
              "name": "Wadakanchery Municipality",
              "localName": "വടക്കാഞ്ചേരി മുനിസിപ്പാലിറ്റി",
              "districtCode": "566",
              "districtName": "Thrissur",
              "regionName": "Kerala",
              "ulbGrade": "Municipality Grade 3",
              "longitude": null,
              "latitude": null,
              "captcha": null,
              "shapeFileLocation": null,
              "code": "M080700",
              "ddrName": "Wadakanchery"
          },
          "address": "Municiplaity Kerala",
          "pincode": [],
          "contactNumber": "111111",
          "pdfHeader": "",
          "pdfContactDetails": ""
      },
      {
          "i18nKey": "TENANT_TENANTS_KL",
          "code": "kl",
          "name": "Kerala",
          "description": "Kerala State",
          "logoId": "",
          "imageId": null,
          "domainUrl": "",
          "twitterUrl": null,
          "facebookUrl": null,
          "emailId": "",
          "OfficeTimings": {
              "Mon - Fri": "9.00 AM - 6.00 PM"
          },
          "city": {
              "name": "Kerala",
              "localName": "വടക്കാഞ്ചേരി മുനിസിപ്പാലിറ്റി",
              "districtCode": "",
              "districtName": "Kerala",
              "regionName": "Kerala",
              "ulbGrade": "",
              "longitude": null,
              "latitude": null,
              "captcha": null,
              "shapeFileLocation": null,
              "code": "kl",
              "ddrName": "kl"
          },
          "address": "Kerala",
          "pincode": [],
          "contactNumber": "111111",
          "pdfHeader": "",
          "pdfContactDetails": ""
      }
  ],
  "revenue_localities": {},
  "localities": {}
}
  if (isLoading) {
    return <Loader page={true} />;
  }

  const i18n = getI18n();
  return (
    <Provider store={getStore(tmpInitData, moduleReducers(tmpInitData))}>
      <Router>
        <Body>
          <DigitApp
            initData={tmpInitData}
            stateCode={stateCode}
            modules={tmpInitData?.modules}
            appTenants={tmpInitData.tenants}
            logoUrl={tmpInitData?.stateInfo?.logoUrl}
          />
        </Body>
      </Router>
    </Provider>
  );
};

export const DigitUI = ({ stateCode, registry, enabledModules, moduleReducers }) => {
  const userType = Digit.UserService.getType();
  const queryClient = new QueryClient({
    defaultOptions: {
      queries: {
        staleTime: 15 * 60 * 1000,
        cacheTime: 50 * 60 * 1000,
        retryDelay: attemptIndex => Infinity
        /*
          enable this to have auto retry incase of failure
          retryDelay: attemptIndex => Math.min(1000 * 3 ** attemptIndex, 60000)
         */
      },
    },
  });

  const ComponentProvider = Digit.Contexts.ComponentProvider;
  const DSO = Digit.UserService.hasAccess(["FSM_DSO"]);

  return (
    <div>
      <ErrorBoundary>
        <QueryClientProvider client={queryClient}>
          <ComponentProvider.Provider value={registry}>
            <DigitUIWrapper stateCode={stateCode} enabledModules={enabledModules} moduleReducers={moduleReducers} />
          </ComponentProvider.Provider>
        </QueryClientProvider>
      </ErrorBoundary>
    </div>
  );
};

const componentsToRegister = {
  SelectOtp
}

export const initCoreComponents = () => {
  Object.entries(componentsToRegister).forEach(([key, value]) => {
    Digit.ComponentRegistryService.setComponent(key, value);
  });
}
