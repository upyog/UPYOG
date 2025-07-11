export const config = [
  {
    head: "",
    body: [
      {
        type: "form",
        withoutLabel: true,
        component: "EventForm",
        nextStep: "",
        route: "",
        key: "eventData",
      },
      {
        type: "textarea",
        label: "EVENTS_DESCRIPTION_LABEL",
        isMandatory: true,
        description: "EVENTS_DESCRIPTION_TEXT",
        populators: {
          name: "description",
          className: "fullWidth",
          validation: {
            required: true,
            maxLength: 500,
          },
          error: 'EVENTS_DESCRIPTION_ERROR_REQUIRED',
        },
      }
      // {
      //   type: "number",
      //   label: "EVENTS_ENTRY_FEE_INR_LABEL",
      //   populators: {
      //     name: "fees",
      //     className: "fullWidth",
      //     error: "EVENTS_ENTRY_ERROR_REQUIRED",
      //   }
      // },
    ]
  }
] 