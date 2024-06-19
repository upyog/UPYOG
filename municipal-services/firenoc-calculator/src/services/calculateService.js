import { requestInfoToResponseInfo } from "../utils";
import { searchService } from "../controller/search";
import { generateDemand } from "./demandService";
import { getFireNoc } from "./firenocService";
import isEmpty from "lodash/isEmpty";
import envVariables from "../envVariables";
import { mdmsData } from "./mdmsService";

export const calculateService = async (req, pool, next) => {
  let calculalteResponse = {};
  const requestInfo = req.body.RequestInfo;
  const tenantId = req.body.CalulationCriteria[0].tenantId;

  calculalteResponse.ResponseInfo = requestInfoToResponseInfo(
    requestInfo,
    true
  );

  let calculations = await getCalculation(req, pool, next);
  calculalteResponse.Calculation = calculations;

  console.log("calculations",calculations);
  let a = await generateDemand(requestInfo, tenantId, calculations);

  return calculalteResponse;
};

const getCalculation = async (req, pool, next) => {
  let calculations = [];
  const requestInfo = req.body.RequestInfo;
  for (let i = 0; i < req.body.CalulationCriteria.length; i++) {
    let calculateCriteria = req.body.CalulationCriteria[i];
    if (calculateCriteria.fireNOC) {
      calculateCriteria.applicationNumber =
        calculateCriteria.fireNOC.fireNOCDetails.applicationNumber;
    }
    if (!calculateCriteria.fireNOC || isEmpty(calculateCriteria.fireNOC)) {
      const applicationNumber = calculateCriteria.applicationNumber;
      const tenantId = calculateCriteria.tenantId;
      let firefireNocSearchResponseNOC = getFireNoc(
        requestInfo,
        applicationNumber,
        tenantId
      );
      if (
        !firefireNocSearchResponseNOC.FireNOCs ||
        isEmpty(firefireNocSearchResponseNOC.FireNOCs)
      ) {
        throw `FireNOC not found for application number: ${applicationNumber}`;
      } else {
        calculateCriteria.fireNOC = firefireNocSearchResponseNOC.FireNOCs[0];
      }
    }

    let calculation = await calculateForSingleReq(
      calculateCriteria,
      requestInfo,
      pool,
      next
    );

    calculations.push(calculation);
  }

  return calculations;
};

const calculateForSingleReq = async (
  calculateCriteria,
  requestInfo,
  pool,
  next
) => {
  let mdms = await mdmsData(requestInfo, calculateCriteria.tenantId);
  let mdmsConfig = {};
  for (let i = 0; i < mdms.MdmsRes.firenoc.FireNocULBConstats.length; i++) {
    let constEntry = mdms.MdmsRes.firenoc.FireNocULBConstats[i];
    mdmsConfig = { ...mdmsConfig, [constEntry.code]: constEntry.value };
  }
  for (let i = 0; i < mdms.MdmsRes.firenoc.FireNocStateConstats.length; i++) {
    let constEntry = mdms.MdmsRes.firenoc.FireNocStateConstats[i];
    mdmsConfig = { ...mdmsConfig, [constEntry.code]: constEntry.value };
  }
  let searchReqParam = {};
  searchReqParam.tenantId = calculateCriteria.fireNOC.tenantId;
  searchReqParam.fireNOCType =
    calculateCriteria.fireNOC.fireNOCDetails.fireNOCType;
  searchReqParam.calculationType = mdmsConfig.CALCULATON_TYPE;

  let calculation = {
    applicationNumber: calculateCriteria.applicationNumber,
    fireNoc: calculateCriteria.fireNOC,
    tenantId: searchReqParam.tenantId,
    taxHeadEstimates: []
  };
  const feeEstimate = await calculateNOCFee(
    searchReqParam,
    calculateCriteria,
    mdmsConfig,
    pool,
    requestInfo,
    next
  );
  calculation.taxHeadEstimates.push(feeEstimate);
  if (calculateCriteria.fireNOC.fireNOCDetails.additionalDetail.adhocPenalty) {
    const adhocPenaltyEstimate = calculateAdhocPenalty(calculateCriteria);
    calculation.taxHeadEstimates.push(adhocPenaltyEstimate);
  }
  if (calculateCriteria.fireNOC.fireNOCDetails.additionalDetail.adhocRebate) {
    const adhocRebateEstimate = calculateAdhocRebate(calculateCriteria);
    calculation.taxHeadEstimates.push(adhocRebateEstimate);
  }
 // const taxEstimate = calculateTaxes(mdmsConfig, calculation);
//  calculation.taxHeadEstimates.push(taxEstimate);

  //const roundoffEstimate = calculateRoundOff(calculation);
//  calculation.taxHeadEstimates.push(roundoffEstimate);
  return calculation;
};

const calculateNOCFee = async (
  searchReqParam,
  calculateCriteria,
  mdmsConfig,
  pool,
  requestInfo,
  next
) => {
  let nocfee = 0;
  let buidingnocfees = [];
  for (
    let i = 0;
    i < calculateCriteria.fireNOC.fireNOCDetails.buildings.length;
    i++
  ) {
    let buidingnocfee = 0; let usage=calculateCriteria.fireNOC.fireNOCDetails.buildings[i].usageType+"."+calculateCriteria.fireNOC.fireNOCDetails.buildings[i].usageSubType;
    searchReqParam.buildingUsageType =usage;
    
    let uoms = calculateCriteria.fireNOC.fireNOCDetails.buildings[
      i
    ].uoms.filter(uom => {
      return uom.isActiveUom;
    });
    //for (let uomindex = 0; uomindex < uoms.length; uomindex++) {
    
    for(let j=0;j<uoms.length;j++)
    { 
      if(uoms[j].code=='HEIGHT_OF_BUILDING')
      {   
        searchReqParam.height = uoms[j].value;
      }
    }
      //searchReqParam.uomValue = uoms[uomindex].value;
     //searchReqParam.uomValue ="HEIGHT_OF_BUILDING";
    let landArea=calculateCriteria.fireNOC.fireNOCDetails.buildings[i].landArea;
    let AREA_SQYD=landArea* 1.19599;   let AREA_ACRE = landArea * 0.000247105;
    searchReqParam.areaSQYD=AREA_SQYD; searchReqParam.areaACRE=AREA_ACRE;

    const billingslabs = await searchService(searchReqParam, {}, pool);
      console.log("billingslabs",billingslabs);
      let errors = [];
      if (billingslabs.length > 1) {
        errors = [...errors, { message: "More than 1 billingslabs!" }];
      }
      if (billingslabs.length < 1) {
        errors = [...errors, { message: "No Billing slabs found!" }];
      }
      if (errors.length > 0) {
        next({
          errorType: "custom",
          errorReponse: {
            ResponseInfo: requestInfoToResponseInfo(requestInfo, false),
            Errors: errors
          }
        });
        return;
      }
      if( billingslabs[0].calculationType==="FLAT"){
      let fullrate = Number(billingslabs[0].rate);
          if (calculateCriteria.fireNOC.fireNOCDetails.fireNOCType === "PROVISIONAL")
            buidingnocfee= Math.round((fullrate * billingslabs[0].provisional_percentage) / 100.0);
          else if (calculateCriteria.fireNOC.fireNOCDetails.fireNOCType === "RENEWAL" || calculateCriteria.fireNOC.fireNOCDetails.fireNOCType === "RENEW")
            buidingnocfee= Math.round((fullrate * billingslabs[0].renew_percentage) / 100.0);
          else
            buidingnocfee= Math.round((fullrate * billingslabs[0].new_percentage) / 100.0);
      }
      else  
      {
          let area;
                if ( billingslabs[0].areauom === "SQYD")
                    area = AREA_SQYD;
                else
                    area = AREA_ACRE;
                    
                    let fullrate = Number(billingslabs[0].rate);
                    let fee_per_unit = Number(billingslabs[0].feeperunitrate);
    
                    
    
                    let fee = fullrate + ((Math.round(area*2)/2) * fee_per_unit); // if unit is above 0.5 then treat full unit otherwise half e.g. 3.4 ACRE will be treated as 3.5 unit and 3.6 ACRE will be treated as 4 unit multiplier
                    let max_fee = Number(billingslabs[0].maxfee);
                    let min_fee =Number(billingslabs[0].minfee);
    
    
                    if (fee > max_fee)
                        fee = max_fee;
                    else if (fee < min_fee)
                        fee = min_fee;
    
                   if (calculateCriteria.fireNOC.fireNOCDetails.fireNOCType === "PROVISIONAL")
                   buidingnocfee= Math.round((fee * billingslabs[0].provisional_percentage) / 100.0);
                    else if (calculateCriteria.fireNOC.fireNOCDetails.fireNOCType === "RENEWAL" || calculateCriteria.fireNOC.fireNOCDetails.fireNOCType === "RENEW")
                    buidingnocfee= Math.round((fee * billingslabs[0].renew_percentage) / 100.0);
                    else
                    buidingnocfee= Math.round((fee * billingslabs[0].new_percentage) / 100.0);

      }
  //     if (mdmsConfig.CALCULATON_TYPE === "FLAT") {
  //       buidingnocfee += Number(billingslabs[0].rate);
  //     } else {
  //       buidingnocfee += Number(
  //         billingslabs[0].rate * Number(searchReqParam.uomValue)
  //       );
  //     }
  //  // }
  //   if (mdmsConfig.CALCULATON_TYPE !== "FLAT") {
  //     const minimumFee =
  //       searchReqParam.fireNOCType === "NEW"
  //         ? mdmsConfig.MINIMUM_NEW
  //         : mdmsConfig.MINIMUM_PROVISIONAL;
  //     buidingnocfee = Math.max(buidingnocfee, minimumFee);
  //   }

    buidingnocfees.push(buidingnocfee);
  }
  switch (mdmsConfig.MULTI_BUILDING_CALC_METHOD) {
    case "SUM":
      nocfee = buidingnocfees.reduce((a, b) => a + b, 0);
      break;
    case "AVERAGE":
      nocfee =
        buidingnocfees.reduce((a, b) => a + b, 0) / buidingnocfees.length;
      break;
    case "MAX":
      nocfee = Math.max(...buidingnocfees);
      break;
    case "MIN":
      nocfee = Math.min(...buidingnocfees);
      break;
  }
  const feeEstimate = {
    category: "FEE",
    taxHeadCode: "FIRENOC_FEES",
    estimateAmount: nocfee
  };
  return feeEstimate;
};

const calculateAdhocPenalty = calculateCriteria => {
  const adhocPenaltyEstimate = {
    category: "PENALTY",
    taxHeadCode: "FIRENOC_ADHOC_PENALTY",
    estimateAmount:
      calculateCriteria.fireNOC.fireNOCDetails.additionalDetail.adhocPenalty
  };
  return adhocPenaltyEstimate;
};

const calculateAdhocRebate = calculateCriteria => {
  const adhocRebateEstimate = {
    category: "REBATE",
    taxHeadCode: "FIRENOC_ADHOC_REBATE",
    estimateAmount:
      calculateCriteria.fireNOC.fireNOCDetails.additionalDetail.adhocRebate
  };
  return adhocRebateEstimate;
};

const calculateTaxes = (mdmsConfig, calculation) => {
  let taxableAmount = 0;
  calculation.taxHeadEstimates.map(taxHeadEstimate => {
    if (envVariables.TAXABLE_TAXHEADS.includes(taxHeadEstimate.taxHeadCode)) {
      taxableAmount += taxHeadEstimate.estimateAmount;
    }
  });
  let taxAmount = (taxableAmount * mdmsConfig.TAX_PERCENTAGE) / 100;
  taxAmount = taxAmount.toFixed(2);
  const taxEstimate = {
    category: "TAX",
    taxHeadCode: "FIRENOC_TAXES",
    estimateAmount: taxAmount
  };
  return taxEstimate;
};

const calculateRoundOff = calculation => {
  let roundoffAmount = 0;
  let totalAmount = 0;
  calculation.taxHeadEstimates.map(taxHeadEstimate => {
    if (envVariables.DEBIT_TAXHEADS.includes(taxHeadEstimate.taxHeadCode)) {
      totalAmount -= taxHeadEstimate.estimateAmount;
    } else {
      totalAmount += taxHeadEstimate.estimateAmount;
    }
  });
  roundoffAmount = Math.round(totalAmount) - totalAmount;
  roundoffAmount = roundoffAmount.toFixed(2);
  const roundoffEstimate = {
    category: "TAX",
    taxHeadCode: "FIRENOC_ROUNDOFF",
    estimateAmount: roundoffAmount
  };
  return roundoffEstimate;
};