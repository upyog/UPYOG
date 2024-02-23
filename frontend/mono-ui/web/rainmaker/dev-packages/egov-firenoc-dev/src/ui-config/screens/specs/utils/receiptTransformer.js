import get from "lodash/get";
import { prepareFinalObject } from "egov-ui-framework/ui-redux/screen-configuration/actions";
import store from "../../../../ui-redux/store";
import { getMdmsData, getReceiptData, getFinancialYearDates } from "../utils";
import { getTenantId } from "egov-ui-kit/utils/localStorageUtils";
import { httpRequest } from "../../../../ui-utils";
import {
  getLocalization,
  getLocale
} from "egov-ui-kit/utils/localStorageUtils";
import {
  getUlbGradeLabel,
  getTranslatedLabel,
  transformById,
  getTransformedLocale,
  getUserDataFromUuid,
  getQueryArg
} from "egov-ui-framework/ui-utils/commons";

import { fetchLocalizationLabel } from "egov-ui-kit/redux/app/actions";

import { getSearchResults } from "../../../../ui-utils/commons";

const ifNotNull = value => {
  return !["", "NA", "null", null].includes(value);
};

const nullToNa = value => {
  return ["", "NA", "null", null].includes(value) ? "NA" : value;
};
debugger;
const createAddress = (doorNo, street, locality, city) => {
  let address = "";
  address += ifNotNull(doorNo) ? doorNo + ", " : "";
//  address += ifNotNull(buildingName) ? buildingName + ", " : "";
  address += ifNotNull(street) ? street + ", " : "";
  address += locality + ", ";
  address += city;
  return address;
};

const epochToDate = et => {
  if (!et) return null;
  var date = new Date(Math.round(Number(et)));
  var formattedDate =
    date.getDate() + "/" + (date.getMonth() + 1) + "/" + date.getFullYear();
  return formattedDate;
};

export const getMessageFromLocalization = code => {
  let messageObject = JSON.parse(getLocalization("localization_en_IN")).find(
    item => {
      return item.code == code;
    }
  );
  return messageObject ? messageObject.message : code;
};

export const loadUlbLogo = utenantId => {
  var img = new Image();
  img.crossOrigin = "Anonymous";
  img.onload = function() {
    var canvas = document.createElement("CANVAS");
    var ctx = canvas.getContext("2d");
    canvas.height = this.height;
    canvas.width = this.width;
    ctx.drawImage(this, 0, 0);
    store.dispatch(
      prepareFinalObject("base64UlbLogoForPdf", canvas.toDataURL())
    );
    canvas = null;
  };
 img.src = `/pb-egov-assets/${utenantId}/logo.png`; 
 //img.src = '/pb-egov-assets/pb/Punjab_FS_logo.jpg'; 
};

export const loadApplicationData = async (applicationNumber, tenant) => {
  let data = {};
  let queryObject = [
    { key: "tenantId", value: tenant },
    { key: "applicationNumber", value: applicationNumber }
  ];
  let response = await getSearchResults(queryObject);
  if (response && response.FireNOCs && response.FireNOCs.length > 0) {
    data.applicationNumber = nullToNa(
      get(response, "FireNOCs[0].fireNOCDetails.applicationNumber", "NA")
    );
    data.applicationStatus = getMessageFromLocalization(nullToNa('NOC_'+get(response, "FireNOCs[0].fireNOCDetails.status")));
    data.applicationDate = nullToNa(
      epochToDate(
        get(response, "FireNOCs[0].fireNOCDetails.applicationDate", "NA")
      )
    );
    data.applicationMode = getMessageFromLocalization(
      nullToNa(get(response, "FireNOCs[0].fireNOCDetails.channel", "NA"))
    );
    data.nocType = nullToNa(
      get(response, "FireNOCs[0].fireNOCDetails.fireNOCType", "NA")
    );
    data.provisionalNocNumber = nullToNa(
      get(response, "FireNOCs[0].provisionFireNOCNumber", "NA")
    );
    data.fireStationId = nullToNa(
      getMessageFromLocalization(
        `FIRENOC_FIRESTATIONS_${getTransformedLocale(
          get(response, "FireNOCs[0].fireNOCDetails.firestationId", "NA")
        )}`
      )
    );

    // Certificate Data
    data.fireNOCNumber = nullToNa(
      get(response, "FireNOCs[0].fireNOCNumber", "NA")
    );
    data.issuedDate = nullToNa(
      epochToDate(get(response, "FireNOCs[0].fireNOCDetails.issuedDate", "NA"))
    );
    data.validTo = nullToNa(
      epochToDate(get(response, "FireNOCs[0].fireNOCDetails.validTo", "NA"))
    );

    // Buildings Data
    data.propertyType = nullToNa(
      get(response, "FireNOCs[0].fireNOCDetails.noOfBuildings", "NA")
    );
    let buildings = get(response, "FireNOCs[0].fireNOCDetails.buildings", []);

    data.landArea = nullToNa(
      get(response, "FireNOCs[0].fireNOCDetails.buildings[0].landArea", "NA")
    );

    data.landArea = nullToNa(
      get(response, "FireNOCs[0].fireNOCDetails.buildings[0].landArea", "NA")
    );

    data.totalCoveredArea = nullToNa(
      get(response, "FireNOCs[0].fireNOCDetails.buildings[0].totalCoveredArea", "NA")
    );

    data.parkingArea = nullToNa(
      get(response, "FireNOCs[0].fireNOCDetails.buildings[0].parkingArea", "NA")
    );

    data.leftSurrounding = nullToNa(
      get(response, "FireNOCs[0].fireNOCDetails.buildings[0].leftSurrounding", "NA")
    );

    data.rightSurrounding = nullToNa(
      get(response, "FireNOCs[0].fireNOCDetails.buildings[0].rightSurrounding", "NA")
    );

    data.backSurrounding = nullToNa(
      get(response, "FireNOCs[0].fireNOCDetails.buildings[0].backSurrounding", "NA")
    );

    data.frontSurrounding = nullToNa(
      get(response, "FireNOCs[0].fireNOCDetails.buildings[0].frontSurrounding", "NA")
    );


    data.totalCoveredArea = nullToNa(
      get(response, "FireNOCs[0].fireNOCDetails.buildings[0].totalCoveredArea", "NA")
    );

    data.parkingArea = nullToNa(
      get(response, "FireNOCs[0].fireNOCDetails.buildings[0].parkingArea", "NA")
    );

    data.oldFireNOCNumber=nullToNa(
      get(response, "FireNOCs[0].oldFireNOCNumber", "NA")
    );
    
    data.leftSurrounding = nullToNa(
      get(response, "FireNOCs[0].fireNOCDetails.buildings[0].leftSurrounding", "NA")
    );

    data.rightSurrounding = nullToNa(
      get(response, "FireNOCs[0].fireNOCDetails.buildings[0].rightSurrounding", "NA")
    );

    data.backSurrounding = nullToNa(
      get(response, "FireNOCs[0].fireNOCDetails.buildings[0].backSurrounding", "NA")
    );

    data.frontSurrounding = nullToNa(
      get(response, "FireNOCs[0].fireNOCDetails.buildings[0].frontSurrounding", "NA")
    );

    console.log("buildings", buildings);

    data.buildings = buildings.map(building => {
      let uoms = get(building, "uoms", []);
      let uomsObject = {};
      uoms.forEach(uom => { if(uom.active==true){
        uomsObject[uom.code] = uom.value;}
      });
      return {
        name: get(building, "name", "NA"),
        usageType: getMessageFromLocalization(
          `FIRENOC_BUILDINGTYPE_${getTransformedLocale(
            get(building, "usageType", "NA").split(".")[0]
          )}`
        ),
        usageSubType: getMessageFromLocalization(
          //`${getTransformedLocale(
          `FIRENOC_BUILDINGSUBTYPE_${getTransformedLocale(
            get(building, "usageSubType", "NA")
          )}`
        ),
        uoms: uomsObject
      };
    });

    let mdmsBody = {
      MdmsCriteria: {
        tenantId: tenant,
        moduleDetails: [
          {
            moduleName: "firenoc",
            masterDetails: [{ name: "BuildingType" }]
          },
       
        ]
      }
    };

    var NBCGroup, NBCSubGroup;

    try {
      let payload = null;
      payload = await httpRequest(
        "post",
        "/egov-mdms-service/v1/_search",
        "_search",
        [],
        mdmsBody
      );
      console.log(payload,"loadNBCData");

      let BuildingType =  get(
                            payload,
                            "MdmsRes.firenoc.BuildingType",
                            "NA" 
                          );

      console.log(BuildingType,"BuildingType");

  
      

   //   let bsubtype = []
/* 
      for ( let i =0; i<=BuildingType.length; i++)
        {
            if(buildings[0].usageType === BuildingType[i].code )
            {

              console.log("test",BuildingType[i].BuildingSubType);

            }
       }  */

       let bsubtype = BuildingType.filter(type => 
        { if (buildings[0].usageType === type.code ) 
          return type.BuildingSubType} 
        );

        console.log('bsubtype', bsubtype);


        for(let i=0; i<=bsubtype[0].BuildingSubType.length>0;i++)
        {

          if(buildings[0].usageSubType===bsubtype[0].BuildingSubType[i].code)

            {
                NBCGroup =  bsubtype[0].BuildingSubType[i].NBCGroup ? bsubtype[0].BuildingSubType[i].NBCGroup: '' ;
                NBCSubGroup = bsubtype[0].BuildingSubType[i].NBCSubGroup ? bsubtype[0].BuildingSubType[i].NBCSubGroup: '';
                console.log('NBCGroup', NBCGroup);
                console.log('NBCSubGroup', NBCSubGroup);
            }

            }       
  
      }           
  

  
     catch (e) {
      console.log(e);
    }

    data.NBCGroup = NBCGroup;
    data.NBCSubGroup = NBCSubGroup;
   

    // Property Location
    data.propertyId = nullToNa(
      get(
        response,
        "FireNOCs[0].fireNOCDetails.propertyDetails.propertyId",
        "NA"
      )
    );


    data.basements = nullToNa(
      get(
        response,
        "FireNOCs[0].fireNOCDetails.propertyDetails.propertyId",
        "NA"
      )
    );

 
    let city_value = nullToNa(  
      get(  
         response,  
         "FireNOCs[0].fireNOCDetails.propertyDetails.address.subDistrict",  
         "NA" 
       )         
   );

   let city=city_value.includes("pb")?city_value.split(".")[1].toUpperCase():city_value.toUpperCase();
   data.city = nullToNa(          
       getMessageFromLocalization(`TENANT_TENANTS_PB_${city}`));  

    data.door = nullToNa(
      get(
        response,
        "FireNOCs[0].fireNOCDetails.propertyDetails.address.doorNo",
        "NA"
      )
    );

    data.areaType = nullToNa(
      get(
        response,
        "FireNOCs[0].fireNOCDetails.propertyDetails.address.areaType",
        "NA"
      )
    );

    let lat = nullToNa(
      get(response, "FireNOCs[0].fireNOCDetails.propertyDetails.address.latitude", "NA")
    );

    let long = nullToNa(
      get(response, "FireNOCs[0].fireNOCDetails.propertyDetails.address.longitude", "NA")
    );

    data.mapLoc=lat+","+long;
    let district_value = nullToNa(  
      get(  
         response,  
         "FireNOCs[0].fireNOCDetails.propertyDetails.address.city", 
         "NA" 
       )         
   );
    let distt=district_value.split(".")[1].toUpperCase();
    data.district = nullToNa(          
        getMessageFromLocalization(`TENANT_TENANTS_PB_${distt}`));    
      

    data.subDistrict = nullToNa(
      get(
        response,
        "FireNOCs[0].fireNOCDetails.propertyDetails.address.subDistrict",
        "NA"
      )
    );

    data.village = nullToNa(
      get(
        response,
        "FireNOCs[0].fireNOCDetails.propertyDetails.address.addressLine2",
        "NA"
      )
    );

    data.landmark = nullToNa(
      get(
        response,
        "FireNOCs[0].fireNOCDetails.propertyDetails.address.landmark",
        "NA"
      )
    );

    data.buildingName = nullToNa(
      get(
        response,
        "FireNOCs[0].fireNOCDetails.propertyDetails.address.buildingName",
        "NA"
      )
    );
    data.street = nullToNa(
      get(
        response,
        "FireNOCs[0].fireNOCDetails.propertyDetails.address.street",
        "NA"
      )
    );


    const  tenantId = getQueryArg(window.location.href, "tenantId"); 

    
    let value = get(
      response,
      "FireNOCs[0].fireNOCDetails.propertyDetails.address.locality.code",
      "NA");

      let utenantId = get(
        response,
        "FireNOCs[0].fireNOCDetails.propertyDetails.address.subDistrict",
        "NA");

    //const  tenantId = getQueryArg(window.location.href, "tenantId");    

   if(data.areaType === 'Urban')
    {


     data.mohalla = nullToNa(         
     /*  getTransformedLocale(
            `${getTransformedLocale(utenantId)}_REVENUE_${value.replace("-", "_") }`           
        )  */
        getMessageFromLocalization(`${getTransformedLocale(utenantId)}_REVENUE_${value.replace("-", "_")}`)       

      ); 
    
    }

    else
    {
      data.mohalla = "N/A";
    } 
   

/*     if(data.areaType === 'Urban')
    {
    data.mohalla = nullToNa(  
        getMessageFromLocalization(
            `${getTransformedLocale(tenantId)}_REVENUE_${value.replace("-", "_") }`           
        )
      ); 


  
      let city_value2 = nullToNa(  
        get(  
           response,  
           "FireNOCs[0].fireNOCDetails.propertyDetails.locality.code",  
           "NA" 
         )         
     );
  
      data.mymohalla = nullToNa(          
          getMessageFromLocalization(`TL_${city_value2}` ) ); 
      

    console.log(data.mymohalla,"data.mymohalla");
    }

    else
    {
      data.mohalla = "N/A";
    } */


    /*   data.mohalla = nullToNa(
        getMessageFromLocalization(
        `${getTransformedLocale(
            get(
            response,
            "FireNOCs[0].fireNOCDetails.propertyDetails.address.city",
            "NA"
            )
            )}_REVENUE_${getTransformedLocale(
            get(
              response,
              "FireNOCs[0].fireNOCDetails.propertyDetails.address.locality.code",
              "NA"
              )
            )}`
          ));  */
      

    


    data.pincode = nullToNa(
      get(
        response,
        "FireNOCs[0].fireNOCDetails.propertyDetails.address.pincode",
        "NA"
      )
    );
    data.gis = nullToNa(
      get(
        response,
        "FireNOCs[0].fireNOCDetails.propertyDetails.address.latitude",
        "NA"
      )
    );

    const urbancreateAddress = (doorNo, street, mohalla, landmark, city, district, pincode) => {
      let address = "";
      address += ifNotNull(doorNo) ? doorNo + ", " : "";   
      address += ifNotNull(street) ? street + ", " : "";
      address += ifNotNull(mohalla) ? mohalla + ", " : "";
      address += ifNotNull(landmark) ? landmark + ", " : "";      
      address += ifNotNull(city) ? city + ", " : "";
      address += ifNotNull(district) ? district + ", " : "";
      address += ifNotNull(pincode) ? pincode + ", " : "";   
      return address;
    };

    const ruralcreateAddress = ( doorNo, street, landmark, village, subDistrict, district, pincode ) => {
      let address = "";
      address += ifNotNull(doorNo) ? doorNo + ", " : "";   
      address += ifNotNull(street) ? street + ", " : "";
      address += ifNotNull(landmark) ? landmark + ", " : ""; 
      address += ifNotNull(village) ? village + ", " : "";
      address += ifNotNull(subDistrict) ? subDistrict + ", " : "";
      address += ifNotNull(district) ? district + ", " : "";
      address += ifNotNull(pincode) ? pincode + ", " : "";      
      return address;
    };

    data.urbanaddress = urbancreateAddress(
      data.door,
      data.street,
      data.mohalla,
      data.landmark,
      data.city,     
      data.district,     
      data.pincode     

    );

    data.ruraladdress = ruralcreateAddress(
      data.door,
      data.street,
      data.landmark,
      data.village,     
      data.subDistrict,
      data.district,
      data.pincode

        );

        data.address = data.areaType==='Urban'? data.urbanaddress :data.ruraladdress ;

    // Applicant Details
    let owners = get(
      response,
      "FireNOCs[0].fireNOCDetails.applicantDetails.owners",
      []
    );
    data.owners = owners.map(owner => {
      return {
        mobile: get(owner, "mobileNumber", "NA"),
        name: get(owner, "name", "NA"),
        gender: get(owner, "gender", "NA"),
        fatherHusbandName: get(owner, "fatherOrHusbandName", "NA"),
        relationship: get(owner, "relationship", "NA"),
        dob: epochToDate(get(owner, "dob", "NA")),
        email: get(owner, "emailId", "NA"),
        pan: get(owner, "pan", "NA"),
        address: get(owner, "correspondenceAddress", "NA")
      };
    });

   

    // Institution Details
    data.ownershipType = nullToNa(
      get(
        response,
        "FireNOCs[0].fireNOCDetails.applicantDetails.ownerShipType",
        "NA"
      )
    );
    data.institutionName = nullToNa(
      get(
        response,
        "FireNOCs[0].fireNOCDetails.applicantDetails.additionalDetail.institutionName",
        "NA"
      )
    );
    data.telephoneNumber = nullToNa(
      get(
        response,
        "FireNOCs[0].fireNOCDetails.applicantDetails.additionalDetail.telephoneNumber",
        "NA"
      )
    );
    data.institutionDesignation = nullToNa(
      get(
        response,
        "FireNOCs[0].fireNOCDetails.applicantDetails.additionalDetail.institutionDesignation",
        "NA"
      )
    );

    // Documents

    // User Data
    loadUserNameData(get(response, "FireNOCs[0].auditDetails.lastModifiedBy"));
  }

  console.log(data,"********DATA***********");
  store.dispatch(prepareFinalObject("applicationDataForPdf", data));
  
};

export const loadReceiptData = async (consumerCode, tenant) => {

  let data = {};
  let queryObject = [
    {
      key: "tenantId",
      value: tenant
    },
    {
      key: "consumerCodes",
      value: consumerCode
    }
  ];
  
  let response = await getReceiptData(queryObject);
   data.collectedAmnt = nullToNa(
    get(
      response,
      "Payments[0].totalAmountPaid",
      "NA"
    )
  );

  if (response && response.Payments && response.Payments.length > 0) {
    data.receiptNumber = nullToNa(
      get(response, "Payments[0].paymentDetails[0].receiptNumber", "NA")
    );
    data.amountPaid = get(
      response,
      "Payments[0].paymentDetails[0].bill.billDetails[0].amountPaid",
      0
    );
    data.totalAmount = get(
      response,
      "Payments[0].paymentDetails[0].bill.billDetails[0].amount",
      0
    );
    data.amountDue = data.totalAmount - data.amountPaid;
    data.paymentMode = nullToNa(
      get(response, "Payments[0].paymentMode", "NA")
    );
    data.transactionNumber = nullToNa(
      get(response, "Payments[0].transactionNumber", "NA")
    );
   // data.bankName = get(response, "Receipt[0].instrument.bank.name", "NA");
    //data.branchName = get(response, "Receipt[0].instrument.branchName", null);
    //data.bankAndBranch = nullToNa(
    //   data.bankName && data.branchName
    //     ? data.bankName + ", " + data.branchName
    //     : get(data, "bankName", "NA")
    // );
    data.bankName = get(response, "Receipt[0].instrument.bank.name", "NA");
    data.branchName = get(response, "Receipt[0].instrument.branchName", "NA");
    data.bankAndBranch = nullToNa(
    data.bankName && data.branchName
        ? data.bankName + ", " + data.branchName
        : get(data, "bankName", "NA")
     );
    data.paymentDate = nullToNa(
      epochToDate(
        get(response, "Payments[0].transactionDate", 0)
      )
    );
    data.g8ReceiptNo = nullToNa(
      get(
        response,
        "Payments[0].paymentDetails[0].manualReceiptNumber",
        "NA"
      )
    );
    data.g8ReceiptDate = nullToNa(
      epochToDate(
        get(response, "Payments[0].paymentDetails[0].manualReceiptDate", 0)
      )
    );
    /** START NOC Fee, Adhoc Penalty/Rebate Calculation */
    let nocAdhocPenalty = 0,
      nocAdhocRebate = 0;
    response.Payments[0].paymentDetails[0].bill.billDetails[0].billAccountDetails.map(item => {
      let desc = item.taxHeadCode ? item.taxHeadCode : "";
      if (desc === "FIRENOC_FEES") {
        data.nocFee = item.amount;
      } else if (desc === "FIRENOC_ADHOC_PENALTY") {
        nocAdhocPenalty = item.amount;
      } else if (desc === "FIRENOC_ADHOC_REBATE") {
        nocAdhocRebate = item.amount;
      } else if (desc === "FIRENOC_TAXES") {
        data.nocTaxes = item.amount;
      }
    });
    data.nocPenaltyRebate = "NA";
    data.nocAdhocPenaltyRebate = nocAdhocPenalty + nocAdhocRebate;
    /** END */
  }
  store.dispatch(prepareFinalObject("receiptDataForPdf", data));
};

export const loadMdmsData = async tenantid => {
  let localStorageLabels = JSON.parse(
    window.localStorage.getItem(`localization_${getLocale()}`)
  );
  let localizationLabels = transformById(localStorageLabels, "code");
  let data = {};
  let queryObject = [
    {
      key: "tenantId",
      value: `${tenantid}`
    },
    {
      key: "moduleName",
      value: "tenant"
    },
     {
      key: "masterName",
      value: "tenants"
    },
  
    
  ];
  let response = await getMdmsData(queryObject);

  if (
    response &&
    response.MdmsRes &&
    response.MdmsRes.tenant.tenants.length > 0
  ) {
    let ulbData = response.MdmsRes.tenant.tenants.find(item => {
      return item.code == tenantid;
    });

    console.log('prasad ulbData',ulbData);


    /** START Corporation name generation logic */
    const ulbGrade = get(ulbData, "city.ulbGrade", "NA")
      ? getUlbGradeLabel(get(ulbData, "city.ulbGrade", "NA"))
      : "MUNICIPAL CORPORATION";

    const cityKey = `TENANT_TENANTS_${get(ulbData, "code", "NA")
      .toUpperCase()
      .replace(/[.]/g, "_")}`;

    data.corporationName = `${getTranslatedLabel(
      cityKey,
      localizationLabels
    ).toUpperCase()} ${getTranslatedLabel(ulbGrade, localizationLabels)}`;

    /** END */

    data.ulbname = get(ulbData, "name", "NA");
    data.corporationAddress = get(ulbData, "address", "NA");
    data.corporationContact = get(ulbData, "contactNumber", "NA");
    data.corporationWebsite = get(ulbData, "domainUrl", "NA");
    data.corporationEmail = get(ulbData, "emailId", "NA");
  }
  store.dispatch(prepareFinalObject("mdmsDataForPdf", data));
};


export const loadUserNameData = async uuid => {
  let data = {};
  let bodyObject = {
    uuid: [uuid]
  };
  let response = await getUserDataFromUuid(bodyObject);

  if (response && response.user && response.user.length > 0) {
    data.auditorName = get(response, "user[0].name", "NA");
  }
  store.dispatch(prepareFinalObject("userDataForPdf", data));
};

 /** Data used for creation of receipt is generated and stored in local storage here */
/*export const loadPdfGenerationData = (applicationNumber, tenant) => {
  /** Logo loaded and stored in local storage in base64 
  loadUlbLogo(tenant);
  loadApplicationData(applicationNumber, tenant); //PB-FN-2019-06-14-002241
  loadReceiptData(applicationNumber, tenant); //PB-FN-2019-06-14-002241
  loadMdmsData(tenant);
}; */

/** Data used for creation of receipt is generated and stored in local storage here */
export const loadPdfGenerationData = (applicationNumber, tenant) => {
  /** Logo loaded and stored in local storage in base64 */

  loadUlbLogo(tenant);

  loadApplicationData(applicationNumber, tenant); //PB-FN-2019-06-14-002241
  loadReceiptData(applicationNumber, tenant); //PB-FN-2019-06-14-002241

  loadMdmsData(tenant);


};
