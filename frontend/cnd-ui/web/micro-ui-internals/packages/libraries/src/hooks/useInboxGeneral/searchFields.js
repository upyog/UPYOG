const inboxSearchFields = {
//Search Fields for the Inbox where employee can search particular application using Mobile Number and Application Number
    CND: [
        {
          label: "CND_APPLICATION_NUMBER",
          name: "applicationNumber",
          roles: [],
        },
        {
          label: "CND_REGISTERED_MOB_NUMBER",
          name: "mobileNumber",
          type: "mobileNumber",
          maxLength: 10,
          minLength: 0,
          roles: [],
          pattern: "^$|[6-9][0-9]{9}",
          errorMessages: {
            pattern: "",
            minLength: "",
            maxLength: "",
          },
        }
      ]
};

export const getSearchFields = (isInbox) => (isInbox ? inboxSearchFields : null);
