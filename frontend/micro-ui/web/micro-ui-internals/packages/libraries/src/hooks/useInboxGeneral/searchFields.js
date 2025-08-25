const inboxSearchFields = {
  PT: [
    {
      label: "PT_PROPERTY_APPLICATION_NO",
      name: "acknowledgementIds",
      roles: [],
    },
    {
      label: "ES_SEARCH_UNIQUE_PROPERTY_ID",
      name: "propertyIds",
      // minLength: "10",
      roles: [],
    },
    {
      label: "ES_SEARCH_APPLICATION_MOBILE_NO",
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
    },
  ],
  PTR: [
    {
      label: "PTR_APPLICATION_NUMBER",
      name: "applicationNumber",
      roles: [],
    },
    {
      label: "PTR_PET_TYPE",
      name: "petType",
      // minLength: "10",
      roles: [],
    },
    {
      label: "PTR_MOBILE_NUMBER",
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
    },
  ],
  ASSET: [
    {
      label: "ES_ASSET_RESPONSE_CREATE_LABEL",
      name: "applicationNo",
      roles: [],
    },
    {
      label: "AST_ASSET_CATEGORY_LABEL",
      name: "assetClassification",
      type: "Dropdown",
      roles: [],
    },
    {
      label: "AST_PARENT_CATEGORY_LABEL",
      name: "assetParentCategory",
      // minLength: "10",
      roles: [],
    },
  ],
  EW: [
    {
      label: "EW_REQUEST_ID",
      name: "requestId",
      roles: [],
    },
    {
      label: "EW_MOBILE_NUMBER",
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
    },
  ],
    CHB: [
      {
        label: "CHB_BOOKING_NO",
        name: "bookingNo",
        roles: [],
      },
      {
        label: "CHB_COMMUNITY_HALL_NAME",
        name: "communityHallCode",
        type: "Dropdown",
        roles: [],
      },
      {
        label: "CHB_MOBILE_NUMBER",
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
      },
    ],
    WT: [
      {
        label: "WT_BOOKING_NO",
        name: "bookingNo",
        roles: [],
      },
      {
        label: "WT_MOBILE_NUMBER",
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
      },
    ],
    MT: [
      {
        label: "MT_BOOKING_NO",
        name: "bookingNo",
        roles: [],
      },
      {
        label: "MT_MOBILE_NUMBER",
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
      },
    ],
    TP: [
      {
        label: "TP_BOOKING_NO",
        name: "bookingNo",
        roles: [],
      },
      {
        label: "TP_MOBILE_NUMBER",
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
      },
    ],
    SV: [
      {
        label: "SV_APPLICATION_NUMBER",
        name: "applicationNumber",
        roles: [],
      },
      {
        label: "SV_REGISTERED_MOB_NUMBER",
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
    ],
};

const searchFieldsForSearch = {
  PT: [
    {
      label: "ES_INBOX_LOCALITY",
      name: "locality",
      type: "Locality",
      isMendatory: true,
    },
    {
      label: "ES_INBOX_UNIQUE_PROPERTY_ID",
      name: "propertyIds",
      roles: [],
    },
    {
      label: "ES_SEARCH_EXISTING_PROPERTY_ID",
      name: "oldpropertyids",
      title: "ES_SEARCH_APPLICATION_MOBILE_INVALID",
      roles: [],
    },
    {
      label: "ES_SEARCH_APPLICATION_MOBILE_NO",
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
    },
  ],
};

export const getSearchFields = (isInbox) => (isInbox ? inboxSearchFields : searchFieldsForSearch);
