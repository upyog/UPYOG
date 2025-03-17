export const newConfig = [
    {
      head: "ES_NEW_APPLICATION_PROPERTY_ASSESSMENT",
      body: [
        
        {
          type: "component",
          route: "preApprovedPlanDetails",
          key: "BuildingPlanScrutiny",
          isMandatory: true,
          component: "BuildingPlanScrutiny",
          texts: {
            headerCaption: "",
            header: "NEW_BUILDING_PLAN_SCRUTINY",
            cardText: "",
            submitBarLabel: "CS_COMMON_SUBMIT",
            skipText: "CLEAR_FORM"

          },
          nextStep: "BasicDetails",
          
          hideInEmployee: true,
        },
        {
          type: "component",
          route: "BasicDetails",
          key: "data",
          isMandatory: true,
          component: "BasicDetails",
          nextStep: "location",
          
          hideInEmployee: true,
        },
        {
          route: "location",
          component: "LocationDetails",
          nextStep: "scrutiny-details",
          hideInEmployee: true,
          key: "address",
          texts: {
            headerCaption: "BPA_STEPPER_SCRUTINY_DETAILS_HEADER",
            header: "BPA_NEW_TRADE_DETAILS_HEADER_DETAILS",
            cardText: "",
            submitBarLabel: "CS_COMMON_NEXT",
            skipAndContinueText: "",
          },
        },
        {
          route: "scrutiny-details",
          component: "ScrutinyDetails",
          nextStep: "owner-details",
          hideInEmployee: true,
          key: "subOccupancy",
          texts: {
            headerCaption: "BPA_STEPPER_SCRUTINY_DETAILS_HEADER",
            header: "BPA_STEPPER_SCRUTINY_DETAILS_HEADER",
            cardText: "",
            submitBarLabel: "CS_COMMON_NEXT",
            skipText:"CORE_COMMON_SKIP_CONTINUE",
          },
        },
        
        {
          route: "owner-details",
          component: "OwnerDetails",
          nextStep: "document-details",
          key: "owners",
          texts: {
            headerCaption: "BPA_OWNER_AND_DOCUMENT_DETAILS_LABEL",
            header: "BPA_APPLICANT_DETAILS_HEADER",
            submitBarLabel: "CS_COMMON_NEXT"
          }
        },
        {
          route: "document-details",
          component: "DocumentDetails",
          nextStep: "noc-details",
          key: "documents",
          texts: {
            headerCaption: "BPA_OWNER_AND_DOCUMENT_DETAILS_LABEL",
            header: "BPA_DOCUMENT_DETAILS_LABEL",
            submitBarLabel: "CS_COMMON_NEXT",
          }
        },
        {
          route: "noc-details",
          component: "NOCDetails",
          nextStep: null,
          key: "nocDocuments",
          texts: {
            headerCaption: "BPA_NOC_DETAILS_SUMMARY",
            header: "",
            submitBarLabel: "CS_COMMON_NEXT",
            skipText: "CORE_COMMON_SKIP_CONTINUE",
          }
        }
        
        


      ]
    }
  ] 