/*
 * citizenConfig outlines the routing,
 * detailing steps for applicant, address, and document details, along with their configurations.
 */

export const citizenConfig = [
  {
    "body": [
      {
        "route": "fileGrievance",
        "component": "NewGrievance",
        "nextStep":null,
        "key": "newGrievance",
        "type": "component",
        "texts": {
          "header":"NEW_GRIEVANCE",
          "submitBarLabel": "COMMON_BUTTON_SUBMIT",
        },
      },
    ],
  },
];
