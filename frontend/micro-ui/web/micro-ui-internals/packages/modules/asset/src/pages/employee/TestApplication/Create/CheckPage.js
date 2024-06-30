import {
  Card,
  CardHeader,
  CardSubHeader,
  CardText,
  CheckBox,
  LinkButton,
  Row,
  StatusTable,
  SubmitBar,
} from "@upyog/digit-ui-react-components";
import React, { useState } from "react";
import { useTranslation } from "react-i18next";
import { useHistory } from "react-router-dom";
import { checkForNA } from "../../../../utils";
import Timeline from "../../../../components/ASTTimeline";

const ActionButton = ({ jumpTo }) => {
  const { t } = useTranslation();
  const history = useHistory();
  function routeTo() {
    history.push(jumpTo);
  }

  return <LinkButton label={t("CS_COMMON_CHANGE")} className="check-page-link-button" onClick={routeTo} />;
};

const CheckPage = ({ onSubmit, value = {} }) => {
  const { t } = useTranslation();
  const history = useHistory();
  

  

  const {
    address,
    assetDetails,
    index,    
    isEdit,
    isUpdate,
    asset,
   
  } = value;



  console.log("assetsss",asset,assetDetails,address);



  const typeOfApplication = !isEdit && !isUpdate ? `new-application` : `edit-application`;


  const [agree, setAgree] = useState(false);
  const setdeclarationhandler = () => {
    setAgree(!agree);
  };
  return (
    <React.Fragment>
     {window.location.href.includes("/employee") ? <Timeline currentStep={5}/> : null}
    <Card>
      <CardHeader>{t("AST_CHECK_DETAILS")}</CardHeader>
      <div>
        <br></br>
      
        

        <CardSubHeader>{t("ASSET_GENERAL_DETAILS")}</CardSubHeader>
        <StatusTable>

        <Row
            label={t("AST_FINANCIAL_YEAR")}
            text={`${t(checkForNA(asset?.financialYear?.code))}`}
            actionButton={<ActionButton jumpTo={`/digit-ui/employee/asset/assetservice/new-assets/asset-deatils`} />}

        />
         <Row
            label={t("AST_SOURCE_FINANCE")}
            text={`${t(checkForNA(asset?.sourceOfFinance?.value))}`}
            actionButton={<ActionButton jumpTo={`/digit-ui/employee/asset/assetservice/new-assets/asset-deatils`} />}

        />
        <Row
            label={t("AST_ASSET_CATEGORY_LABEL")}
            text={`${t(checkForNA(asset?.assetclassification?.value))}`}
            actionButton={<ActionButton jumpTo={`/digit-ui/employee/asset/assetservice/new-assets/asset-deatils`} />}
        />

        <Row
            label={t("AST_PARENT_CATEGORY_LABEL")}
            text={`${t(checkForNA(asset?.assettype?.value))}`}
            actionButton={<ActionButton jumpTo={`/digit-ui/employee/asset/assetservice/new-assets/asset-deatils`} />}

        />

        <Row
            label={t("AST_SUB_CATEGORY")}
            text={`${t(checkForNA(asset?.assetsubtype?.value))}`}
            actionButton={<ActionButton jumpTo={`/digit-ui/employee/asset/assetservice/new-assets/asset-deatils`} />}

        />

        <Row
            label={t("AST_BOOK_REF_SERIAL_NUM")}
            text={`${t(checkForNA(asset?.BookPagereference))}`}
            actionButton={<ActionButton jumpTo={`/digit-ui/employee/asset/assetservice/new-assets/asset-deatils`} />}
        />
        <Row
            label={t("AST_NAME")}
            text={`${t(checkForNA(asset?.AssetName))}`}
            actionButton={<ActionButton jumpTo={`/digit-ui/employee/asset/assetservice/new-assets/asset-deatils`} />}
        />

          <Row
            label={t("AST_DEPARTMENT")}
            text={`${t(checkForNA(asset?.Department?.value))}`}
            actionButton={<ActionButton jumpTo={`/digit-ui/employee/asset/assetservice/new-assets/asset-deatils`} />}
        />


        </StatusTable>
        <br></br>

        <CardSubHeader>{t("AST_DETAILS")}</CardSubHeader>
       
        <StatusTable>
          {asset?.assettype?.value==="Land" && (
            <React.Fragment>
            <Row
            label={t("AST_LAND_TYPE")}
            text={`${t(checkForNA(assetDetails?.landType))}`}
            actionButton={<ActionButton jumpTo={`/digit-ui/employee/asset/assetservice/new-assets/assets`} />}

        />

          <Row
              label={t("AST_LAND_AREA")}
              text={`${t(checkForNA(assetDetails?.area))}`}
              actionButton={<ActionButton jumpTo={`/digit-ui/employee/asset/assetservice/new-assets/assets`} />}

          />
          <Row
            label={t("AST_ACQUISTION_COST_BOOK_VALUE")}
            text={`${t(checkForNA(assetDetails?.bookValue))}`}
            actionButton={<ActionButton jumpTo={`/digit-ui/employee/asset/assetservice/new-assets/assets`} />}

        />

          <Row
              label={t("AST_DATE_DEED_EXECUTION")}
              text={`${t(checkForNA(assetDetails?.dateOfDeedExecution))}`}
              actionButton={<ActionButton jumpTo={`/digit-ui/employee/asset/assetservice/new-assets/assets`} />}

          />
          <Row
            label={t("AST_DATE_POSSESION")}
            text={`${t(checkForNA(assetDetails?.dateOfPossession))}`}
            actionButton={<ActionButton jumpTo={`/digit-ui/employee/asset/assetservice/new-assets/assets`} />}

        />

          <Row
              label={t("AST_FROM_DEED_TAKEN")}
              text={`${t(checkForNA(assetDetails?.fromWhomDeedTaken))}`}
              actionButton={<ActionButton jumpTo={`/digit-ui/employee/asset/assetservice/new-assets/assets`} />}

          />
          <Row
              label={t("AST_GOVT_ORDER_NUM")}
              text={`${t(checkForNA(assetDetails?.governmentOrderNumber))}`}
              actionButton={<ActionButton jumpTo={`/digit-ui/employee/asset/assetservice/new-assets/assets`} />}

          />
          <Row
            label={t("AST_COLLECT_ORDER_NUM")}
            text={`${t(checkForNA(assetDetails?.collectorOrderNumber))}`}
            actionButton={<ActionButton jumpTo={`/digit-ui/employee/asset/assetservice/new-assets/assets`} />}

        />

          <Row
              label={t("AST_COUNCIL_RES_NUM")}
              text={`${t(checkForNA(assetDetails?.councilResolutionNumber))}`}
              actionButton={<ActionButton jumpTo={`/digit-ui/employee/asset/assetservice/new-assets/assets`} />}

          />
          <Row
              label={t("AST_TOTAL_COST")}
              text={`${t(checkForNA(assetDetails?.totalCost))}`}
              actionButton={<ActionButton jumpTo={`/digit-ui/employee/asset/assetservice/new-assets/assets`} />}
              />
            <Row
              label={t("AST_DEPRICIATION_RATE")}
              text={`${t(checkForNA(assetDetails?.depreciationRate))}`}
              actionButton={<ActionButton jumpTo={`/digit-ui/employee/asset/assetservice/new-assets/assets`} />}
              />
            <Row
              label={t("AST_COST_AFTER_DEPRICIAT")}
              text={`${t(checkForNA(assetDetails?.costAfterDepreciation))}`}
              actionButton={<ActionButton jumpTo={`/digit-ui/employee/asset/assetservice/new-assets/assets`} />}
              />
            </React.Fragment>

          )}

          {asset?.assettype?.value === "Service" && (
            <React.Fragment>
            <Row
            label={t("AST_ROAD_TYPE")}
            text={`${t(checkForNA(assetDetails?.roadType))}`}
            actionButton={<ActionButton jumpTo={`/digit-ui/employee/asset/assetservice/new-assets/assets`} />}

        />

          <Row
              label={t("AST_TOTAL_LENGTH")}
              text={`${t(checkForNA(assetDetails?.totalLength))}`}
              actionButton={<ActionButton jumpTo={`/digit-ui/employee/asset/assetservice/new-assets/assets`} />}
              />

            <Row
              label={t("AST_ROAD_WIDTH")}
              text={`${t(checkForNA(assetDetails?.roadWidth))}`}
              actionButton={<ActionButton jumpTo={`/digit-ui/employee/asset/assetservice/new-assets/assets`} />}
              />

            <Row
              label={t("AST_SURFACE_TYPE")}
              text={`${t(checkForNA(assetDetails?.surfaceType))}`}
              actionButton={<ActionButton jumpTo={`/digit-ui/employee/asset/assetservice/new-assets/assets`} />}
              />
            <Row
              label={t("AST_PROTECTION_LENGTH")}
              text={`${t(checkForNA(assetDetails?.protectionLength))}`}
              actionButton={<ActionButton jumpTo={`/digit-ui/employee/asset/assetservice/new-assets/assets`} />}
              />
            <Row
              label={t("AST_DRAINAGE_CHANNEL_LENGTH")}
              text={`${t(checkForNA(assetDetails?.drainageLength))}`}
              actionButton={<ActionButton jumpTo={`/digit-ui/employee/asset/assetservice/new-assets/assets`} />}
              />
              <Row
              label={t("AST_FOOTPATH_NUMBER")}
              text={`${t(checkForNA(assetDetails?.numOfFootpath))}`}
              actionButton={<ActionButton jumpTo={`/digit-ui/employee/asset/assetservice/new-assets/assets`} />}
              />
            <Row
              label={t("AST_LAST_MAINTAINENCE")}
              text={`${t(checkForNA(assetDetails?.lastMaintainence))}`}
              actionButton={<ActionButton jumpTo={`/digit-ui/employee/asset/assetservice/new-assets/assets`} />}
              />
            <Row
              label={t("AST_NEXT_MAINTAINENCE")}
              text={`${t(checkForNA(assetDetails?.nextMaintainence))}`}
              actionButton={<ActionButton jumpTo={`/digit-ui/employee/asset/assetservice/new-assets/assets`} />}
              />
            <Row
              label={t("AST_TOTAL_COST")}
              text={`${t(checkForNA(assetDetails?.totalCost))}`}
              actionButton={<ActionButton jumpTo={`/digit-ui/employee/asset/assetservice/new-assets/assets`} />}
              />
            <Row
              label={t("AST_DEPRICIATION_RATE")}
              text={`${t(checkForNA(assetDetails?.depreciationRate))}`}
              actionButton={<ActionButton jumpTo={`/digit-ui/employee/asset/assetservice/new-assets/assets`} />}
              />
            <Row
              label={t("AST_COST_AFTER_DEPRICIAT")}
              text={`${t(checkForNA(assetDetails?.costAfterDepreciation))}`}
              actionButton={<ActionButton jumpTo={`/digit-ui/employee/asset/assetservice/new-assets/assets`} />}
              />


          </React.Fragment>
          )}

          {asset?.assettype?.value==="Building" && (
            <React.Fragment>
               <Row
              label={t("AST_BUILDING_NO")}
              text={`${t(checkForNA(assetDetails?.buildingSno))}`}
              actionButton={<ActionButton jumpTo={`/digit-ui/employee/asset/assetservice/new-assets/assets`} />}

          />
          <Row
              label={t("AST_PLOT_AREA")}
              text={`${t(checkForNA(assetDetails?.plotArea))}`}
              actionButton={<ActionButton jumpTo={`/digit-ui/employee/asset/assetservice/new-assets/assets`} />}

          />
          <Row
              label={t("AST_PLINTH_AREA")}
              text={`${t(checkForNA(assetDetails?.plinthArea))}`}
             actionButton={<ActionButton jumpTo={`/digit-ui/employee/asset/assetservice/new-assets/assets`} />}


          />
          <Row
              label={t("AST_FLOORS_NO")}
              text={`${t(checkForNA(assetDetails?.floorNo))}`}
             actionButton={<ActionButton jumpTo={`/digit-ui/employee/asset/assetservice/new-assets/assets`} />}


          />
          <Row
              label={t("AST_TOTAL_COST")}
              text={`${t(checkForNA(assetDetails?.totalCost))}`}
             actionButton={<ActionButton jumpTo={`/digit-ui/employee/asset/assetservice/new-assets/assets`} />}

          />
            <Row
              label={t("AST_DEPRICIATION_RATE")}
              text={`${t(checkForNA(assetDetails?.depreciationRate))}`}
             actionButton={<ActionButton jumpTo={`/digit-ui/employee/asset/assetservice/new-assets/assets`} />}

          />
            <Row
              label={t("AST_COST_AFTER_DEPRICIAT")}
              text={`${t(checkForNA(assetDetails?.costAfterDepreciation))}`}
             actionButton={<ActionButton jumpTo={`/digit-ui/employee/asset/assetservice/new-assets/assets`} />}

          />

            </React.Fragment>
          )}

          {asset?.assettype?.value==="Vehicle" && (
            <React.Fragment>
              <Row
              label={t("AST_REGISTRATION_NUMBER")}
              text={`${t(checkForNA(assetDetails?.registrationNumber))}`}
             actionButton={<ActionButton jumpTo={`/digit-ui/employee/asset/assetservice/new-assets/assets`} />}


              />
              <Row
              label={t("AST_ENGINE_NUMBER")}
              text={`${t(checkForNA(assetDetails?.engineNumber))}`}
             actionButton={<ActionButton jumpTo={`/digit-ui/employee/asset/assetservice/new-assets/assets`} />}


          />
          <Row
              label={t("AST_CHASIS_NUMBER")}
              text={`${t(checkForNA(assetDetails?.chasisNumber))}`}
             actionButton={<ActionButton jumpTo={`/digit-ui/employee/asset/assetservice/new-assets/assets`} />}


          />
          <Row
              label={t("AST_ACQUISTION_DATE")}
              text={`${t(checkForNA(assetDetails?.acquisitionDate))}`}
             actionButton={<ActionButton jumpTo={`/digit-ui/employee/asset/assetservice/new-assets/assets`} />}


          />
          <Row
              label={t("AST_ACQUIRED_SOURCE")}
              text={`${t(checkForNA(assetDetails?.acquiredFrom))}`}
             actionButton={<ActionButton jumpTo={`/digit-ui/employee/asset/assetservice/new-assets/assets`} />}


          />
          <Row
              label={t("AST_IMPROVEMENT_COST")}
              text={`${t(checkForNA(assetDetails?.improvementCost))}`}
             actionButton={<ActionButton jumpTo={`/digit-ui/employee/asset/assetservice/new-assets/assets`} />}


          />
          <Row
              label={t("AST_CURRENT_VALUE")}
              text={`${t(checkForNA(assetDetails?.currentAssetValue))}`}
             actionButton={<ActionButton jumpTo={`/digit-ui/employee/asset/assetservice/new-assets/assets`} />}


          />

            </React.Fragment>
          )}

          {asset?.assettype?.code === "IT" && (
            <React.Fragment>
              <Row
              label={t("AST_BRAND")}
              text={`${t(checkForNA(assetDetails?.brand?.value))}`}
             actionButton={<ActionButton jumpTo={`/digit-ui/employee/asset/assetservice/new-assets/assets`} />}


              />
            <Row
                label={t("AST_ASSET_AGE")}
                text={`${t(checkForNA(assetDetails?.assetAge))}`}
              actionButton={<ActionButton jumpTo={`/digit-ui/employee/asset/assetservice/new-assets/assets`} />}


            />
            <Row
                label={t("AST_CURRENT_LOCATION")}
                text={`${t(checkForNA(assetDetails?.currentLocation))}`}
              actionButton={<ActionButton jumpTo={`/digit-ui/employee/asset/assetservice/new-assets/assets`} />}


            />
            <Row
                label={t("AST_INVOICE_DATE")}
                text={`${t(checkForNA(assetDetails?.invoiceDate))}`}
              actionButton={<ActionButton jumpTo={`/digit-ui/employee/asset/assetservice/new-assets/assets`} />}


            />
            <Row
                label={t("AST_MANUFACTURER")}
                text={`${t(checkForNA(assetDetails?.manufacturer))}`}
              actionButton={<ActionButton jumpTo={`/digit-ui/employee/asset/assetservice/new-assets/assets`} />}
            />
            <Row
                label={t("AST_ASSIGNED_USER")}
                text={`${t(checkForNA(assetDetails?.assignedUser))}`}
              actionButton={<ActionButton jumpTo={`/digit-ui/employee/asset/assetservice/new-assets/assets`} />}
            />

              <Row
                label={t("AST_PURCHASE_COST")}
                text={`${t(checkForNA(assetDetails?.purchaseCost))}`}
              actionButton={<ActionButton jumpTo={`/digit-ui/employee/asset/assetservice/new-assets/assets`} />}


            />
          <Row
              label={t("AST_PURCHASE_DATE")}
              text={`${t(checkForNA(assetDetails?.purchaseDate))}`}
             actionButton={<ActionButton jumpTo={`/digit-ui/employee/asset/assetservice/new-assets/assets`} />}


          />
          <Row
              label={t("AST_PURCHASE_ORDER")}
              text={`${t(checkForNA(assetDetails?.purchaseOrderNumber))}`}
             actionButton={<ActionButton jumpTo={`/digit-ui/employee/asset/assetservice/new-assets/assets`} />}


          />
          <Row
              label={t("AST_WARRANTY")}
              text={`${t(checkForNA(assetDetails?.warranty?.value))}`}
             actionButton={<ActionButton jumpTo={`/digit-ui/employee/asset/assetservice/new-assets/assets`} />}


          />
            </React.Fragment>

          )}

        </StatusTable>

        <br></br>
        <CardSubHeader>{t("AST_ADDRESS_DETAILS")}</CardSubHeader>
        
        <StatusTable>
         
         <Row
            label={t("MYCITY_CODE_LABEL")}
            text={`${t(checkForNA(address?.city?.name))}`}
            actionButton={<ActionButton jumpTo={`/digit-ui/employee/asset/assetservice/new-assets/address`} />}
        />
         <Row
            label={t("AST_LOCALITY")}
            text={`${t(checkForNA(address?.locality?.name))}`}
            actionButton={<ActionButton jumpTo={`/digit-ui/employee/asset/assetservice/new-assets/address`} />}
        />

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