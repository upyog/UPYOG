import {
  getCommonCard,
  getCommonTitle,
  getSelectField,
  getCommonContainer,
  getLabelWithValue,
  getLabel
} from "egov-ui-framework/ui-config/screens/specs/utils";

import { handleNA } from '../../utils';

import "./index.css";

export const resData =  () =>  {
  return( 
     getCommonContainer({
       transactionType: getLabelWithValue({ label: "Transaction Type" }, { jsonPath: "createBillResponse[0].transactionType", callBack: ""  })  ,
       locality: getLabelWithValue({ label: "Locality" }, { jsonPath: "createBillResponse[0].locality", callBack: ""  })  ,
       billingcycleStartdate: getLabelWithValue({ label: "Billing Startdate" }, { jsonPath: "createBillResponse[0].billingcycleStartdate", callBack: ""  }),
       billingcycleEnddate: getLabelWithValue({ label: "Billing Enddate" }, { jsonPath: "createBillResponse[0].billingcycleEnddate", callBack: ""  })  ,
       status: getLabelWithValue({ label: "Status" }, { jsonPath: "createBillResponse[0].status", callBack: ""  })  ,
       tenantId: getLabelWithValue({ label: "TenantId" }, { jsonPath: "createBillResponse[0].tenantId", callBack: ""  })  ,
     })
   )
  }


export const viewCreateBill = getCommonCard({

subHeader: getCommonTitle({
    label: "Generate Bill Response"
  },
  {
    style: {
      marginBottom: 8
    }
  }
  ),

billResponse:resData(),



});



