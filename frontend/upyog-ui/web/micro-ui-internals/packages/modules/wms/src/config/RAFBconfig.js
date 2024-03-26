//TL config
export const newConfig = [
  // {
  //   head: "TL_COMMON_TR_DETAILS",
  //   body: [
  //     {
  //       type: "component",
  //       component: "TLTradeDetailsEmployee",
  //       key: "tradedetils",
  //       withoutLabel: true,
  //       hideInCitizen: true,
  //     }
  //   ]
  // },


  {
    head: "",
    body: [
      
      {
        // route: "TradeName",
        // component: "SelectTradeName",
        route: "ProjectName",
        component: "SelectProjectName", 
        texts: {
          headerCaption: "",
          header: "WMS_RUNNING_ACCOUNT_FINAL_BILL_PROJECT_INFORMATION",
          cardText: "WMS_TARDE_NAME_TEXT",
          submitBarLabel: "CS_COMMON_NEXT",
          skipText: "",
        },
        withoutLabel: true,
        key: "ProjectInfo",
        nextStep: "work-name",
        type: "component",
        hideInEmployee: true,
      },
      {
        route: "work-name",
        component: "SelectWorkName", 
        texts: {
          headerCaption: "",
          header: "WMS_RUNNING_ACCOUNT_FINAL_BILL_PROJECT_INFORMATION",
          cardText: "TL_TARDE_NAME_TEXT",
          submitBarLabel: "CS_COMMON_NEXT",
          skipText: "",
        },
        withoutLabel: true,
        key: "ProjectInfo",
        nextStep: "work-order-no",
        type: "component",
        hideInEmployee: true,
      },
      {
        route: "work-order-no",
        isMandatory: true,
        component: "WorkOrderNo", 
        texts: {
          headerCaption: "",
          header: "WMS_RUNNING_ACCOUNT_FINAL_BILL_PROJECT_INFORMATION",
          cardText: "TL_TARDE_NAME_TEXT",
          submitBarLabel: "CS_COMMON_NEXT",
          skipText: "",
        },
        withoutLabel: true,
        key: "ProjectInfo",
        nextStep: "previous-running-bill",
        type: "component",
        hideInEmployee: true,
      }
    ],
  },

  {
    head: "",
    body: [
      {
        route: "previous-running-bill",
        component: "PreviousRunningBillInformation", 
        texts: {
          headerCaption: "",
          header: "WMS_RUNNING_ACCOUNT_FINAL_BILL_PREVIOUS_RUNNING_BILL_INFORMATION",
          cardText: "",
          submitBarLabel: "CS_COMMON_NEXT",
          skipText: "",
        },
        withoutLabel: true,
        key: "previous_bill",
        nextStep: "select-measurement-book",
        type: "component",
        hideInEmployee: true,
      }
    ],
  },

  {
    head: "",
    body: [
      {
        route: "select-measurement-book",
        component: "SelectMeasurementBook", 
        texts: {
          headerCaption: "",
          header: "WMS_RUNNING_ACCOUNT_FINAL_BILL_MB_NOT_PAID",
          cardText: "",
          submitBarLabel: "CS_COMMON_NEXT",
          skipText: "",
        },
        withoutLabel: true,
        key: "mbNotPaid",
        nextStep: "measurement-book-date",
        type: "component",
        hideInEmployee: true,
      },
      {
        route: "measurement-book-date",
        component: "MeasurementBookDate", 
        texts: {
          headerCaption: "",
          header: "WMS_RUNNING_ACCOUNT_FINAL_BILL_MB_NOT_PAID",
          cardText: "",
          submitBarLabel: "CS_COMMON_NEXT",
          skipText: "",
        },
        withoutLabel: true,
        key: "mbNotPaid",
        nextStep: "measurement-book-no",
        type: "component",
        hideInEmployee: true,
      },
      {
        route: "measurement-book-no",
        component: "MeasurementBookno", 
        texts: {
          headerCaption: "",
          header: "WMS_RUNNING_ACCOUNT_FINAL_BILL_MB_NOT_PAID",
          cardText: "",
          submitBarLabel: "CS_COMMON_NEXT",
          skipText: "",
        },
        withoutLabel: true,
        key: "mbNotPaid",
        nextStep: "measurement-book-amount",
        type: "component",
        hideInEmployee: true,
      },
      {
        route: "measurement-book-amount",
        component: "MeasurementBookAmount", 
        texts: {
          headerCaption: "",
          header: "WMS_RUNNING_ACCOUNT_FINAL_BILL_MB_NOT_PAID",
          cardText: "",
          submitBarLabel: "CS_COMMON_NEXT",
          skipText: "",
        },
        withoutLabel: true,
        key: "mbNotPaid",
        nextStep: "tender-work-details",
        type: "component",
        hideInEmployee: true,
      }
    ],
  },
  {
    head: "",
    body: [
      {
        route: "tender-work-details",
        component: "TenderWorkDetails", 
        texts: {
          headerCaption: "",
          header: "WMS_RUNNING_ACCOUNT_TENDER_WORK_DETAILS",
          cardText: "",
          submitBarLabel: "CS_COMMON_NEXT",
          skipText: "",
        },
        withoutLabel: true,
        key: "TenderWorkDetail",
        nextStep: "withheld-deductions-details",
        type: "component",
        hideInEmployee: true,
      }
    ],
  },
  {
    head: "",
    body: [
      {
        route: "withheld-deductions-details",
        component: "WithheldDeductionsDetails", 
        texts: {
          headerCaption: "",
          header: "WMS_RUNNING_ACCOUNT_WITHHELD_DEDUCTIONS_DETAILS",
          cardText: "",
          submitBarLabel: "CS_COMMON_NEXT",
          skipText: "",
        },
        withoutLabel: true,
        key: "withheldDeductionsDetail",
        nextStep: "ra-bills-tax-details",
        type: "component",
        hideInEmployee: true,
      }
    ],
  },
  {
    head: "",
    body: [
      {
        route: "ra-bills-tax-details",
        component: "RABillsTaxDetails", 
        texts: {
          headerCaption: "",
          header: "WMS_RUNNING_ACCOUNT_RA_BILLS_TAX_DETAILS",
          cardText: "",
          submitBarLabel: "CS_COMMON_NEXT",
          skipText: "",
        },
        withoutLabel: true,
        key: "RABillTaxDetail",
        nextStep: null,
        type: "component",
        hideInEmployee: true,
      }
    ],
  }
  
  
  
];
