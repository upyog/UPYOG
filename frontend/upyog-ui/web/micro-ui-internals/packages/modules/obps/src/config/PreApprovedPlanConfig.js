export const newConfig = [
    {
      head: "ES_NEW_APPLICATION_PROPERTY_ASSESSMENT",
      body: [
        {
          route: "documents-required",
          component: "PreApprovedDocsRequired",
          key: "data",
          nextStep: "planDetails"
        },
        
        {
          type: "component",
          route: "planDetails",
          key: "BuildingPlanScrutiny",
          isMandatory: true,
          component: "BuildingPlanScrutiny",
          texts: {
            headerCaption: "",
            header: "PRE_APPROVED_APPLICATION_DETAILS",
            cardText: "",
            submitBarLabel: "CS_COMMON_PROCEED",
            

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
          nextStep: "plot-details",
          
          hideInEmployee: true,
        },
        {
          route: "plot-details",
          component: "PlotDetails",
          key: "data",
          nextStep: "scrutiny-details",
          texts: {
            headerCaption: "",
            header: "BPA_PLOT_DETAILS_TITLE",
            cardText: "",
            submitBarLabel: "CS_COMMON_NEXT",
            // skipText: "CORE_COMMON_SKIP_CONTINUE",
          },
          
        },
        {
          route: "scrutiny-details",
          component: "ScrutinyDetails",
          nextStep: "location",
          hideInEmployee: true,
          key: "subOccupancy",
          texts: {
            headerCaption: "",
            header: "BPA_STEPPER_PLAN_DETAILS_HEADER",
            cardText: "",
            submitBarLabel: "CS_COMMON_NEXT",
            
          },
        },
        {
          route: "location",
          component: "LocationDetails",
          nextStep: "owner-details",
          hideInEmployee: true,
          key: "address",
          texts: {
            headerCaption: "",
            header: "BPA_NEW_TRADE_DETAILS_HEADER_DETAILS",
            cardText: "",
            submitBarLabel: "CS_COMMON_NEXT",
            skipAndContinueText: "",
          },
        },
        
        
        {
          route: "owner-details",
          component: "OwnerDetails",
          nextStep: "document-details",
          key: "owners",
          texts: {
            headerCaption: "",
            header: "BPA_APPLICANT_DETAILS_HEADER",
            submitBarLabel: "CS_COMMON_NEXT"
          }
        },
        {
          route: "document-details",
          component: "DocumentDetails",
          nextStep: null,
          key: "documents",
          texts: {
            headerCaption: "",
            header: "BPA_DOCUMENT_DETAILS_LABEL",
            submitBarLabel: "CS_COMMON_NEXT",
          }
        },
        
        


      ]
    }
  ] 