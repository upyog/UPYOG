import { FormComposer, Loader } from "@egovernments/digit-ui-react-components";
import React, {  useState } from "react";
import { useTranslation } from "react-i18next";
import { useHistory } from "react-router-dom";
import { newConfig } from "../../../components/config/config";

const Create = () => {
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const { t } = useTranslation();
  const history = useHistory();
  

  const onSubmit = (data) => {

    let ScheduleOfRateApplications = [
      {
/**
 *  "chapter": "string",
      "description_of_item": "string",
      "end_date": "string",
      "item_no": "string",
      "rate": 0,
      "sor_id": 0,
      "sor_name": "string",
      "start_date": "string",
      "unit": 0
 */
        ScheduleOfRateApplication: {
          chapter: data?.WmsChapter?.sorChapter,
          description_of_item: data?.WmsDescriptionOfItem?.sorDescriptionOfItem,          
          item_no: data?.WmsItemNo?sorItemNo,
          unit: data?.WmsUnit?.sorUnit,
          rate: data?.WmsRate?.sorRate,
          sor_id: data?.WmsId?.sorId,
          sor_name: data?.WmsName?.sorName,
          start_date: data?.WmsStartDate?.sorStartDate,
          end_date: data?.WmsEndDate?.sorEndDate,
          tenantId: tenantId,
        },
        
      },
    ];
      /* use customiseCreateFormData hook to make some chnages to the Employee object */
     Digit.WMSService.create(ScheduleOfRateApplications, tenantId).then((result,err)=>{
       let getdata = {...data , get: result }
       onSelect("", getdata, "", true);
       console.log("daaaa",getdata);
     })
     .catch((e) => {
     console.log("err");
    });

    history.push("/digit-ui/citizen/wms/response");

    console.log("getting data",ScheduleOfRateApplications)
    
  };
 
  
  /* use newConfig instead of commonFields for local development in case needed */

  const configs = newConfig?newConfig:newConfig;

  return (
    <FormComposer
    heading={t("Create Schedule Of Rate")}
    label={t("ES_COMMON_APPLICATION_SUBMIT")}
    config={configs.map((config) => {
      return {
        ...config,
        body: config.body.filter((a) => !a.hideInEmployee),
      };
    })}
    onSubmit={onSubmit}
    fieldStyle={{ marginRight: 0 }}
  />
  );
};

export default Create;
