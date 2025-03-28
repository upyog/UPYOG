export const newConfig = [
    {
      head: "ES_NEW_APPLICATION_PROPERTY_ASSESSMENT",
      body: [
        
        {
          type: "component",
          route: "planDetails",
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
          nextStep: "plot-details",
          
          hideInEmployee: true,
        },
        {
          route: "plot-details",
          component: "PlotDetails",
          key: "data",
          nextStep: "scrutiny-details",
          texts: {
            headerCaption: "BPA_SCRUTINY_DETAILS",
            header: "BPA_PLOT_DETAILS_TITLE",
            cardText: "",
            submitBarLabel: "CS_COMMON_NEXT",
            skipText: "CORE_COMMON_SKIP_CONTINUE",
          },
          inputs: [
            {
              label: "BPA_HOLDING_NUMBER_LABEL",
              type: "text",
              validation: {
                // required: true,
              },
              name: "holdingNumber"
            },
            {
              label: "BPA_BOUNDARY_LAND_REG_DETAIL_LABEL",
              type: "textarea",
              validation: {
                // required: true
              },
              name: "registrationDetails"
            }
          ]
        },
        {
          route: "scrutiny-details",
          component: "ScrutinyDetails",
          nextStep: "location",
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
          route: "location",
          component: "LocationDetails",
          nextStep: "owner-details",
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
          nextStep: null,
          key: "documents",
          texts: {
            headerCaption: "BPA_OWNER_AND_DOCUMENT_DETAILS_LABEL",
            header: "BPA_DOCUMENT_DETAILS_LABEL",
            submitBarLabel: "CS_COMMON_NEXT",
          }
        },
        
        


      ]
    }
  ] 