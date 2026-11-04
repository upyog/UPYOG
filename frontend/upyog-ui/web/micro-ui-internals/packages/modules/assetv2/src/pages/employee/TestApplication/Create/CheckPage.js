import { Card, CardHeader, CardSubHeader, CardText, CheckBox, LinkButton, Row, StatusTable, SubmitBar } from "@nudmcdgnpm/digit-ui-react-components";
import React, { useState, useEffect } from "react";
import { useTranslation } from "react-i18next";


import { checkForNA } from "../../../../utils";

const CheckPage = ({ onSubmit, value = {} }) => {
  const { t } = useTranslation();

  const [agree, setAgree] = useState(false);
  const [categoriesWiseData, setCategoriesWiseData] = useState();
  
   const stateTenantId = Digit.ULBService.getStateId();
 
 
   // This call with stateTenantId (Get state-level data)
   const stateResponseObject = Digit.Hooks.useEnabledMDMS(stateTenantId, "ASSETV2", [{ name: "AssetParentCategoryFields" }], {
     select: (data) => {
       const formattedData = data?.["ASSETV2"]?.["AssetParentCategoryFields"];
       return formattedData;
     },
   });


  const { address, assetDetails, asset } = value;


  
  const setdeclarationhandler = () => {
    setAgree(!agree);
  };
  useEffect(() => {
    let combinedData;
    // if city level master is not available then fetch  from state-level
    if (stateResponseObject?.data) {
      combinedData = stateResponseObject.data;
    } else {
      combinedData = [];
    }
    setCategoriesWiseData(combinedData);
  }, [stateResponseObject]);
  
  let formJson = [];
  if (Array.isArray(categoriesWiseData)) {

    formJson = categoriesWiseData
      .filter((category) => {
        const isMatch = category.assetParentCategory === asset?.assettype?.code || category.assetParentCategory === "COMMON";
        return isMatch;
      })
      .map((category) => category.fields) // Extract the fields array
      .flat() // Flatten the fields array
      .filter((field) => field.active === true); // Filter by active status
  }

  function extractValue(key){
      var vl = assetDetails[key]
      if(typeof vl === 'object'){
          return vl.value
      }
      return vl
  };


  return (
    <React.Fragment>
      <Card>
        <CardHeader>{t("AST_CHECK_DETAILS")}</CardHeader>
        <div>
          <br></br>

          <CardSubHeader>{t("ASSET_GENERAL_DETAILS")}</CardSubHeader>
          <StatusTable>

             <Row
              label={t("AST_FINANCIAL_YEAR")}
              text={`${t(checkForNA(asset?.financialYear?.code))}`}
              //actionButton={<ActionButton jumpTo={`/upyog-ui/employee/asset/assetservice/new-assets/asset-deatils`} />}
            />
            <Row
              label={t("AST_DEPARTMENT")}
              text={`${t(checkForNA(asset?.Department?.value))}`}
              //actionButton={<ActionButton jumpTo={`/upyog-ui/employee/asset/assetservice/new-assets/asset-deatils`} />}
            />
            
             <Row
              label={t("AST_PARENT_CATEGORY_LABEL")}
              text={`${t(checkForNA(asset?.assettype?.code))}`}
              //actionButton={<ActionButton jumpTo={`/upyog-ui/employee/asset/assetservice/new-assets/asset-deatils`} />}
            />
            {/* <Row
              label={t("AST_ASSET_CATEGORY_LABEL")}
              text={`${t(checkForNA(asset?.assetclassification?.code))}`}
              //actionButton={<ActionButton jumpTo={`/upyog-ui/employee/asset/assetservice/new-assets/asset-deatils`} />}
            /> */}

            <Row
              label={t("AST_CATEGORY_SUB_CATEGORY")}
              text={`${t(checkForNA(asset?.assetsubtype?.code))}`}
              //actionButton={<ActionButton jumpTo={`/upyog-ui/employee/asset/assetservice/new-assets/asset-deatils`} />}
            />

            <Row
              label={t("AST_NAME")}
              text={`${t(checkForNA(asset?.AssetName))}`}
              //actionButton={<ActionButton jumpTo={`/upyog-ui/employee/asset/assetservice/new-assets/asset-deatils`} />}
            />

            {formJson.filter(row => row.group === "generalDetails")
              .map((row, index) => (
                <Row key= {index}
                  label={t(row.code)}
                  text={`${checkForNA(extractValue(row.name))}`}
                  //actionButton={<ActionButton jumpTo={`/upyog-ui/employee/asset/assetservice/new-assets/assets`} />}
                />
                
              ))}


            <Row
              label={t("AST_LOCATION_DETAILS")}
              text={`${t(checkForNA(assetDetails?.location))}`}
              //actionButton={<ActionButton jumpTo={`/upyog-ui/employee/asset/assetservice/new-assets/assets`} />}
            />

            <Row
              label={t("AST_PLOT_NO")}
              text={`${t(checkForNA(address?.plotNumber))}`}
              //actionButton={<ActionButton jumpTo={`/upyog-ui/employee/asset/assetservice/new-assets/address`} />}
            />
            <Row
              label={t("AST_ADDRESS_LINE_ONE")}
              text={`${t(checkForNA(address?.addressLineOne))}`}
              //actionButton={<ActionButton jumpTo={`/upyog-ui/employee/asset/assetservice/new-assets/address`} />}
            />
            <Row
              label={t("AST_ADDRESS_LINE_TWO")}
              text={`${t(checkForNA(address?.addressLineTwo))}`}
              //actionButton={<ActionButton jumpTo={`/upyog-ui/employee/asset/assetservice/new-assets/address`} />}
            />

            <Row
              label={t("AST_PINCODE")}
              text={`${t(checkForNA(address?.pincode))}`}
              //actionButton={<ActionButton jumpTo={`/upyog-ui/employee/asset/assetservice/new-assets/address`} />}
            />
            <Row
              label={t("MYCITY_CODE_LABEL")}
              text={`${t(checkForNA(address?.city?.name))}`}
              //actionButton={<ActionButton jumpTo={`/upyog-ui/employee/asset/assetservice/new-assets/address`} />}
            />
            <Row
              label={t("AST_LOCALITY")}
              text={`${t(checkForNA(address?.locality?.i18nKey))}`}
              //actionButton={<ActionButton jumpTo={`/upyog-ui/employee/asset/assetservice/new-assets/address`} />}
            />

          </StatusTable>
          <br></br>

          <CardSubHeader>{t("AST_ACQUSTION_DETAILS")}</CardSubHeader>
          <StatusTable>
              <React.Fragment>
             {(asset?.assettype?.code=== "LAND" || asset?.assettype?.code=== "BUILDING") ?null:(
              <React.Fragment>
            <Row
              label={t("AST_INVOICE_DATE")}
              text={`${t(checkForNA(assetDetails?.invoiceDate))}`}
              //actionButton={<ActionButton jumpTo={`/upyog-ui/employee/asset/assetservice/new-assets/assets`} />}
            />
            <Row
              label={t("AST_INVOICE_NUMBER")}
              text={`${t(checkForNA(assetDetails?.invoiceNumber))}`}
              //actionButton={<ActionButton jumpTo={`/upyog-ui/employee/asset/assetservice/new-assets/assets`} />}
            />
            </React.Fragment>
             )}   

              {assetDetails?.modeOfPossessionOrAcquisition?.code==="PURCHASE" && (
                <React.Fragment>
                  <Row
                            label={t("AST_PURCHASE_DATE")}
                            text={`${checkForNA(assetDetails?.purchaseDate)}`}
                            //actionButton={<ActionButton jumpTo={`/upyog-ui/employee/asset/assetservice/new-assets/assets`} />}
                          />
                          <Row
                            label={t("AST_PURCHASE_ORDER")}
                            text={`${checkForNA(assetDetails?.purchaseOrderNumber)}`}
                            //actionButton={<ActionButton jumpTo={`/upyog-ui/employee/asset/assetservice/new-assets/assets`} />}
                          /> 
              </React.Fragment>
              )}
              {assetDetails?.modeOfPossessionOrAcquisition?.code==="CONSTRUCTED" && (
                <React.Fragment>
                  <Row
                    label={t("AST_ESTIMATED_DATE_CONSTRUCTION")}
                    text={`${checkForNA(assetDetails?.estimateConstructionDate)}`}
                    //actionButton={<ActionButton jumpTo={`/upyog-ui/employee/asset/assetservice/new-assets/assets`} />}
                  />
                  <Row
                    label={t("AST_ESTIMATED_DATE_CONSTRUCTION")}
                    text={`${checkForNA(assetDetails?.dateOfConstruction)}`}
                    //actionButton={<ActionButton jumpTo={`/upyog-ui/employee/asset/assetservice/new-assets/assets`} />}
                  />

              </React.Fragment>
              )}
            <Row
                label={t("AST_SOURCE_FINANCE")}
                text={`${checkForNA(asset?.sourceOfFinance?.value)}`}
                //actionButton={<ActionButton jumpTo={`/upyog-ui/employee/asset/assetservice/new-assets/asset-deatils`} />}
              /> 
               <Row
              label={t("AST_PURCHASE_COST")}
              text={`${checkForNA(assetDetails?.purchaseCost)}`}
              //actionButton={<ActionButton jumpTo={`/upyog-ui/employee/asset/assetservice/new-assets/assets`} />}
            />
            <Row
              label={t("AST_ACQUISITION_COST")}
              text={`${checkForNA(assetDetails?.acquisitionCost)}`}
              //actionButton={<ActionButton jumpTo={`/upyog-ui/employee/asset/assetservice/new-assets/assets`} />}
            />

            <Row
              label={t("AST_BOOK_VALUE")}
              text={`${checkForNA(assetDetails?.bookValue)}`}
              //actionButton={<ActionButton jumpTo={`/upyog-ui/employee/asset/assetservice/new-assets/assets`} />}
            />
           <Row
              label={t("AST_MARKET_RATE_EVALUATION")}
              text={`${checkForNA(assetDetails?.marketRateEvaluation)}`}
              //actionButton={<ActionButton jumpTo={`/upyog-ui/employee/asset/assetservice/new-assets/assets`} />}
            />
            <Row
              label={t("AST_MARKET_RATE_CIRCLE")}
              text={`${checkForNA(assetDetails?.marketRateCircle)}`}
              //actionButton={<ActionButton jumpTo={`/upyog-ui/employee/asset/assetservice/new-assets/assets`} />}
            />

              {formJson.filter(row => row.group === "acquistionDetails")
              .map((row, index) => (
                <Row key= {index}
                  label={t(row.code)}
                  text={`${checkForNA(extractValue(row.name))}`}
                  //actionButton={<ActionButton jumpTo={`/upyog-ui/employee/asset/assetservice/new-assets/assets`} />}
                />
                
              ))}
              </React.Fragment>
          </StatusTable>

          <br></br>
          <CardSubHeader>{t("AST_PHYSICAL_CHARACTERISTICS_DETAILS")}</CardSubHeader>
          <StatusTable>
            <React.Fragment>
              {formJson.filter(row => row.group === "physicialCharacteristics")
              .map((row, index) => (
                <Row key= {index}
                  label={t(row.code)}
                  text={`${checkForNA(extractValue(row.name))}`}
                />       
            ))}
            </React.Fragment>
          </StatusTable>

          <br></br>
          <CheckBox
            label={t("AST_FINAL_DECLARATION_MESSAGE")}
            onChange={setdeclarationhandler}
            styles={{ height: "auto" }}
            //disabled={!agree}
          />
        </div>
        <br></br>
        <SubmitBar label={t("COMMON_BUTTON_SUBMIT")} onSubmit={onSubmit} disabled={!agree} />
      </Card>
    </React.Fragment>
  );
};

export default CheckPage;
