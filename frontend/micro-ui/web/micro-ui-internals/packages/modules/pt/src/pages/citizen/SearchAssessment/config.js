export const config = [
    {
      texts: {
        header: "Search Assessment",
        submitButtonLabel: "PT_HOME_SEARCH_RESULTS_BUTTON_SEARCH",
        text: "",
      },
      inputs: [
        
        {
          label: "PT_PROPERTY_UNIQUE_ID",
          description: "CS_PROPERTY_ID_FORMAT_MUST_BE",
          type: "text",
          name: "propertyIds",
          error: "ERR_INVALID_PROPERTY_ID",
          validation: {
            pattern: {
              value: "",
              // "[A-Za-z]{2}\-[A-Za-z]{2}\-[0-9]{4}\-[0-9]{2}\-[0-9]{2}\-[0-9]{6}",
              message: "ERR_INVALID_PROPERTY_ID",
            },
          },
        },
      ],
    },
  ];
  