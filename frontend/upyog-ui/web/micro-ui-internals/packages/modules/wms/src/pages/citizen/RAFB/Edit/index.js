import React, {useEffect, useState} from "react";
import { useTranslation } from "react-i18next";
import { useQueryClient } from "react-query";
import { Redirect, Route, Switch, useHistory, useLocation, useRouteMatch } from "react-router-dom";
// import { newConfig as newConfigWMS } from "../../../../config/RAFBconfig";
import { newConfig } from "../../../../config/RAFBconfig";
import { Loader } from "@egovernments/digit-ui-react-components";

const WmsRAFBEdit=()=>{
    const queryClient = useQueryClient();
  const match = useRouteMatch();
  const { t } = useTranslation();
  const { pathname } = useLocation();
  const history = useHistory();
  console.log("match ",match)

  //################# TL code start
  const [params, setParams, clearParams] = Digit.Hooks.useSessionStorage("WMS_RUNNING_BILL_EDIT", {});
  const {data} = Digit?.Hooks?.wms?.rafb?.useWmsRAFBGet(200, "getPriviousSingleList") || {};
  console.log("singleList ",data)
  useEffect(()=>{
    const Property ={
      "ProjectInfo": {
          "ProjectName": {
              "code": "OWNER.ADDRESSPROOF.ELECTRICITYBILL",
              "active": true,
              "i18nKey": "OWNER_ADDRESSPROOF_ELECTRICITYBILL"
          },
          "WorkName": "abc edit",
          "WorkOrderNo": "1234 345"
      },
      "previous_bill": {
          "0": [
              {
                  "uid": 2,
                  "billDate": "2023-08-04",
                  "bilNo": "94873938",
                  "billAmount": "345678",
                  "deductionAmount": "334456",
                  "remark": "Test"
              },
              {
                  "uid": 3,
                  "billDate": "2023-05-18",
                  "bilNo": "94873938",
                  "billAmount": "345678",
                  "deductionAmount": "334456",
                  "remark": "Test"
              }
          ]
      },
      "mbNotPaid": {
          "i18nKey": "Measurement Book B",
          "mbDate": "2024-01-09",
          "mbNumber": "MB0002",
          "amount": "234545"
      },
      "undefined": {},
      "TenderWorkDetail": {
          "workName": "ABC",
          "estimatedWorkCost": "1234543",
          "tenderType": "Tender Type A",
          "percentageType": "10",
          "amount": "34567"
      },
      "withheldDeductionsDetail": {
          "withheldDeductionsDetail": [
              {
                  "taxcategory": {
                      "i18nKey": "10"
                  },
                  "remark": "Test 1",
                  "amount": "4534"
              },
              {
                  "taxcategory": {
                      "i18nKey": "20"
                  },
                  "remark": "Test 2",
                  "amount": "43534"
              }
          ]
      },
      "RABillTaxDetail": {
          "RABillTaxDetail": [
              {
                  "taxcategory": {
                      "i18nKey_0": "10"
                  },
                  "addition_deduction": {
                      "i18nKey_1": "40"
                  },
                  "amount_percentage": {
                      "i18nKey_2": "70"
                  },
                  "percentageValue": "4",
                  "amount": "100",
                  "total": "104"
              },
              {
                  "taxcategory": {
                      "i18nKey_0": "20"
                  },
                  "addition_deduction": {
                      "i18nKey_1": "50"
                  },
                  "amount_percentage": {
                      "i18nKey_2": "80"
                  },
                  "percentageValue": "10",
                  "amount": "200",
                  "total": "220"
              }
          ]
      },
      "RequestInfo": {
          "apiId": "Rainmaker",
          "authToken": "e8e7eaf7-2240-4d02-af25-b4c422d4bac6",
          "userInfo": {
              "id": 1681,
              "uuid": "2bef105b-eff3-479a-8aad-64c2b5354070",
              "userName": "8080808080",
              "name": "Archt",
              "mobileNumber": "8080808080",
              "emailId": "Arch@test.com",
              "locale": null,
              "type": "CITIZEN",
              "roles": [
                  {
                      "name": "Citizen",
                      "code": "CITIZEN",
                      "tenantId": "pg"
                  }
              ],
              "active": true,
              "tenantId": "pg",
              "permanentCity": "pg.citya"
          },
          "msgId": "1712130869559|en_IN",
          "plainAccessRequest": {}
      },
      "id": 354
  }
    // setParams(Property)
    if(data!=undefined){setParams(data)}
  },[data!=undefined])
  // setParams(singleList?.data)

  console.log("params ",params)
  let config = [];
  let isReneworEditTrade = window.location.href.includes("/renew-trade/") || window.location.href.includes("/edit-application/")
  console.log("isReneworEditTrade ",isReneworEditTrade)
  
  const goNext = (skipStep, index, isAddMultiple, key, isPTCreateSkip) => {
    console.log("skipStep, index, isAddMultiple, key, isPTCreateSkip ",skipStep, index, isAddMultiple, key, isPTCreateSkip)
    let currentPath = pathname.split("/").pop(),
    
      nextPage;
    //   debugger
    console.log("currentPath ",currentPath);
    let { nextStep = {} } = config.find((routeObj) => routeObj.route === currentPath);
    console.log("currentPath nextStep",nextStep);
    let { isCreateEnabled : enableCreate = true } = config.find((routeObj) => routeObj.route === currentPath);
    console.log("enableCreate ",enableCreate)
    if (typeof nextStep == "object" && nextStep != null) {
      if((params?.cptId?.id || params?.cpt?.details?.propertyId || (isReneworEditTrade && params?.cpt?.details?.propertyId ))  && (nextStep[sessionStorage.getItem("isAccessories")] && nextStep[sessionStorage.getItem("isAccessories")] === "know-your-property")  )
      {
        nextStep = "property-details";
      }
      if (
        nextStep[sessionStorage.getItem("isAccessories")] &&
        (
          nextStep[sessionStorage.getItem("isAccessories")] === "accessories-details" ||
          nextStep[sessionStorage.getItem("isAccessories")] === "map" ||
          nextStep[sessionStorage.getItem("isAccessories")] === "owner-ship-details" || 
          nextStep[sessionStorage.getItem("isAccessories")] === "other-trade-details")
      ) {
        nextStep = `${nextStep[sessionStorage.getItem("isAccessories")]}`;
        console.log("currentPath nextStep in if con ",nextStep)
      } else if (
        nextStep[sessionStorage.getItem("StructureType")] &&
        (nextStep[sessionStorage.getItem("StructureType")] === "Building-type" ||
          nextStep[sessionStorage.getItem("StructureType")] === "vehicle-type")
      ) {
        nextStep = `${nextStep[sessionStorage.getItem("StructureType")]}`;
      } 
      else if (
        nextStep[sessionStorage.getItem("KnowProperty")] &&
        (nextStep[sessionStorage.getItem("KnowProperty")] === "search-property" ||
          nextStep[sessionStorage.getItem("KnowProperty")] === "create-property")
      ) {
          if(nextStep[sessionStorage.getItem("KnowProperty")] === "create-property" && !enableCreate)
          {
            nextStep = `map`;
          }
          else{
         nextStep = `${nextStep[sessionStorage.getItem("KnowProperty")]}`;
          }
      }
      // else if (
      //   nextStep[sessionStorage.getItem("KnowProperty")] &&
      //   (nextStep[sessionStorage.getItem("KnowProperty")] === "search-property" ||
      //     nextStep[sessionStorage.getItem("KnowProperty")] === "create-property")
      // ) {
      //     if(nextStep[sessionStorage.getItem("KnowProperty")] === "create-property" && !enableCreate)
      //     {
      //       nextStep = `map`;
      //     }
      //     else{
      //    nextStep = `${nextStep[sessionStorage.getItem("KnowProperty")]}`;
      //     }
      // }
    }
    console.log("hghghghg")
    if(nextStep === "know-your-property" && params?.TradeDetails?.StructureType?.code === "MOVABLE")
    {
      nextStep = "map";
    }
    if(nextStep === "landmark" && params?.TradeDetails?.StructureType?.code === "MOVABLE")
    {
      nextStep = "owner-ship-details";
    }
    if(nextStep === "owner-details" && (sessionStorage.getItem("isSameAsPropertyOwner") === "true"))
    {
      nextStep = "proof-of-identity"
    }
    if( (params?.cptId?.id || params?.cpt?.details?.propertyId || (isReneworEditTrade && params?.cpt?.details?.propertyId ))  && nextStep === "know-your-property" )
    { 
      nextStep = "property-details";
    }
    let redirectWithHistory = history.push;
    if (skipStep) {
      redirectWithHistory = history.replace;
    }
    if (isAddMultiple) {
      nextStep = key;
    }
    if (nextStep === null) {
      return redirectWithHistory(`${match.path}/check`);
    }
    if(isPTCreateSkip && nextStep === "acknowledge-create-property")
    {
      nextStep = "map";
    }
    nextPage = `${match.path}/${nextStep}`;
    redirectWithHistory(nextPage);
  };
  const createProperty = async () => {
    history.push(`${match.path}/acknowledgement`);
  };
  function handleSelect(key, data, skipStep, index, isAddMultiple = false) {
    console.log("onSelect TLInfo key, data, skipStep, index, isAddMultiple = false",{key, data, skipStep, index, isAddMultiple})
      alert("handleSelect")
      if(key === "formData")
      setParams({...data})
      else{
      setParams({ ...params, ...{ [key]: { ...params[key], ...data } } });
      if(key === "isSkip" && data === true)
      {
        goNext(skipStep, index, isAddMultiple, key, true);
      }
      else
      {
        goNext(skipStep, index, isAddMultiple, key);
      }
    }
    }
    
    const handleSkip = () => {};

    const onSuccess = () => {
      clearParams();
      queryClient.invalidateQueries("PT_CREATE_PROPERTY");
    };

//RAFBConfig.js file setup
// let { data: newConfig, isLoading } = Digit.Hooks.tl.useMDMS.getFormConfig(stateId, {});
// newConfig = newConfig ? newConfig : newConfigWMS;
const newConfig_temp = newConfig && newConfig;
newConfig_temp?.forEach((obj) => {
  config = config.concat(obj.body.filter((a) => !a.hideInCitizen));
});
let skipenanbledOb = newConfig_temp?.filter(obj => obj?.body?.some(com => com.component === "CPTCreateProperty"))?.[0];
let skipenabled = skipenanbledOb?.body?.filter((ob) => ob?.component === "CPTCreateProperty")?.[0]?.isSkipEnabled;
sessionStorage.setItem("skipenabled",skipenabled);
config.indexRoute = "ProjectName";

const CheckPage = Digit?.ComponentRegistryService?.getComponent("WMSCheckPage");
const WMSrafbEditAcknowledgement = Digit?.ComponentRegistryService?.getComponent("WMSrafbEditAcknowledgement");

//################# TL code end
return(<div>
        <Switch>
      {config.map((routeObj, index) => {
        const { component, texts, inputs, key, isSkipEnabled } = routeObj;
        const Component = typeof component === "string" ? Digit.ComponentRegistryService.getComponent(component) : component;
        // console.log("Component abc ",Component)
       
        return (
          <Route path={`${match.path}/${routeObj.route}`} key={index}>
            <Component
              config={{ texts, inputs, key, isSkipEnabled }}
              onSelect={handleSelect}
              onSkip={handleSkip}
              t={t}
              formData={params}
            //   onAdd={handleMultiple}
              userType="citizen"
            />
          </Route>
        );
      })}
      <Route path={`${match.path}/check`}>
        <CheckPage 
        onSubmit={createProperty}
         value={params}
          />
      </Route>
       <Route path={`${match.path}/acknowledgement`}>
        <WMSrafbEditAcknowledgement data={params} onSuccess={onSuccess} 
        // onUpdateSuccess={onUpdateSuccess}
         />
      </Route> 

      <Route>
        <Redirect to={`${match.path}/${config.indexRoute}`} />
      </Route>
    </Switch>
    
</div>)
}
export default WmsRAFBEdit