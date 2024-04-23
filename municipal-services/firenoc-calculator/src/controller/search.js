import { requestInfoToResponseInfo, upadteForAuditDetails } from "../utils";
import { validateBillingSlabSearch } from "../utils/modelValidation";

const search = async (req, res, pool, next) => {
  console.log("search");
  const queryObj = JSON.parse(JSON.stringify(req.query));

  let errors = validateBillingSlabSearch(queryObj);
  if (errors.length > 0) {
    next({
      errorType: "custom",
      errorReponse: {
        ResponseInfo: requestInfoToResponseInfo(req.body.RequestInfo, false),
        Errors: errors
      }
    });
    return;
  }

  let searchResponse = {};
  searchResponse.ResponseInfo = requestInfoToResponseInfo(
    req.body.RequestInfo,
    true
  );

  searchResponse.BillingSlabs = await searchService(
    req.query,
    searchResponse,
    pool
  );
  res.send(searchResponse);
};

export default search;

export const searchService = async (reqestCriteria, searchResponse, pool) => {
  const  querystring = generateQuery(reqestCriteria);
  let billingSlabs = [];
  billingSlabs = await pool
    .query(querystring)
    .then(result => {
      return popolateSearchResponse(result);
    })
    .catch(err => console.log(err));

  return billingSlabs;
};

const popolateSearchResponse = result => {
  let BillingSlabs = [];
  result.rows.map(rowData => {
    const billingSlab = {};
   
    billingSlab.id = rowData.id;
    billingSlab.isActive = rowData.isactive;
    billingSlab.fireNOCType = rowData.firenoctype;
    billingSlab.buildingUsageType = rowData.buildingusagetype;
    billingSlab.calculationType = rowData.calculationtype;
    billingSlab.rate = Number(rowData.rate);
    billingSlab.heightuom = rowData.heightuom;
    billingSlab.heightfromUom = rowData.heightfromuom;
    billingSlab.heighttoUom = rowData.heighttouom;
    billingSlab.areauom = rowData.areauom;
    billingSlab.areafromUom = rowData.areafromuom;
    billingSlab.areatoUom = rowData.areatouom;
    billingSlab.fromDate = rowData.fromdate;
    billingSlab.toDate = rowData.todate;
    billingSlab.maxfee = rowData.maxfee;
    billingSlab.minfee = rowData.minfee;
    billingSlab.feeperunitrate = rowData.feeperunitrate;
    billingSlab.new_percentage = rowData.new_percentage;
    billingSlab.renew_percentage = rowData.renew_percentage;
    billingSlab.provisional_percentage = rowData.provisional_percentage;
    billingSlab.slab_description = rowData.slab_description;

    // billingSlab.id = '0ce2dd8789d3aa634c4e81599824822b';
    // billingSlab.isActive = 1;
    // billingSlab.fireNOCType = null;
    // billingSlab.buildingUsageType = 'BANK.BANK';
    // billingSlab.calculationType = 'FLAT';
    // billingSlab.rate = 20000;
    // billingSlab.heightuom = 'MTR';
    // billingSlab.heightfromUom = 0;
    // billingSlab.heighttoUom = 100;
    // billingSlab.areauom = 'ACRE';
    // billingSlab.areafromUom = 0.00;
    // billingSlab.areatoUom = 0.99;
    // billingSlab.fromDate = null;
    // billingSlab.toDate = null;
    // billingSlab.maxfee = null;
    // billingSlab.minfee = null;
    // billingSlab.feeperunitrate = 0;
    // billingSlab.new_percentage = 100;
    // billingSlab.renew_percentage = 50;
    // billingSlab.provisional_percentage = 50;
    // billingSlab.slab_description = 'Rs. 20000 lumpsum';
   
    BillingSlabs.push(billingSlab);
  });
  return BillingSlabs;
};

const generateQuery = params => {
let height=params.height;
let usagetype=params.buildingUsageType; let AREA_SQYD=params.areaSQYD; let AREA_ACRE=params.areaACRE;
  let queryString =
  "SELECT * FROM eg_firenoc_state_billingslab WHERE (" + height + " BETWEEN heightfromuom AND heighttouom) AND buildingusagetype='" + usagetype + "' AND ((CASE WHEN areauom='SQYD' THEN " + AREA_SQYD + " BETWEEN areafromuom AND areatouom END) OR (CASE WHEN areauom='ACRE' THEN " + AREA_ACRE + " BETWEEN areafromuom AND areatouom END))";
  return queryString;
};
