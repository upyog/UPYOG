
export const Config = [
  {
    "head": "EST_COMMON_NEW_REGISTRATION",
      "body": [
        {
          "key": "newRegistration",
          "route": "newRegistration",
          "component": "NewRegistration",
          "nextStep": null,
          "isPreview": false,
          "withoutLabel": true,
          "type": "component",
          "hideInEmployee": false,
          "isMandatory": true,
          "sectionHeading": null,
          "form": [
            {
              "order": 0,
              "key": "EST_BUILDING_NAME",
              "field": {
                "code": "EST_BUILDING_NAME",
                "name": "buildingName",
                "placeholder": "EST_ENTER_BUILDING_NAME",
                "type": "text"
              },
              "validation": {
                "maxLength": 100,
                "pattern": "^[a-zA-Z0-9\\s]+$",
                "regex": {
                  "pattern": "[^a-zA-Z0-9\\s]",
                  "flags": "g"
                },
                "required": true,
                "disabled": false,
                "readOnly": false
              },
              "messages": {
                "error": "EST_INVALID_BUILDING_NAME"
              }
            },
            {
              "order": 1,
              "key": "EST_BUILDING_NUMBER",
              "field": {
                "code": "EST_BUILDING_NUMBER",
                "name": "buildingNo",
                "placeholder": "EST_ENTER_BUILDING_NUMBER",
                "type": "text"
              },
              "validation": {
                "maxLength": 10,
                "pattern": "^[0-9]+$",
                "regex": {
                  "pattern": "\\D",
                  "flags": "g"
                },
                "required": true,
                "disabled": false,
                "readOnly": false
              },
              "messages": {
                "error": "EST_INVALID_BUILDING_NUMBER"
              }
            },
            {
              "order": 2,
              "key": "EST_BUILDING_FLOOR",
              "field": {
                "code": "EST_BUILDING_FLOOR",
                "name": "buildingFloor",
                "placeholder": "EST_ENTER_BUILDING_FLOOR",
                "type": "text",
                "numeric": true
              },
              "apiFieldName": "floor",
              "validation": {
                "maxLength": 3,
                "pattern": "^[0-9]+$",
                "regex": {
                  "pattern": "\\D",
                  "flags": "g"
                },
                "required": false,
                "disabled": false,
                "readOnly": false
              },
              "messages": {
                "error": "EST_INVALID_BUILDING_FLOOR"
              }
            },
            {
              "order": 3,
              "key": "EST_BUILDING_BLOCK",
              "field": {
                "code": "EST_BUILDING_BLOCK",
                "name": "buildingBlock",
                "placeholder": "EST_ENTER_BUILDING_BLOCK",
                "type": "text"
              },
              "validation": {
                "maxLength": 50,
                "pattern": "^[a-zA-Z0-9\\s]+$",
                "regex": {
                  "pattern": "[^a-zA-Z0-9\\s]",
                  "flags": "g"
                },
                "required": false,
                "disabled": false,
                "readOnly": false
              },
              "messages": {
                "error": "EST_INVALID_BUILDING_BLOCK"
              }
            },
{
  "order": 4,
  "key": "EST_CITY",
  "field": {
    "code": "EST_CITY",
    "name": "city",
    "placeholder": "EST_SELECT_CITY",
    "type": "dropdown",
    "dataSource": { "defaultValueSource": "tenantId" }
  },
  "validation": { "required": false, "disabled": true, "readOnly": false }
},
            {
              "order": 5,
              "key": "EST_LOCALITY",
              "field": {
                "code": "EST_LOCALITY",
                "name": "serviceType",
                "placeholder": "EST_SELECT_LOCALITY",
                "type": "dropdown",
                  "dataSource" :{
                  "type":"MDMS",
                  "moduleName":"egov-location",
                  "moduleData":"TenantBoundary",
                  "customiztionRequired":true,
                   "dependsOn": "city" 
                }
              },
              "validation": {
                "required": false,
                "disabled": false,
                "readOnly": false
              }
            },
            {
              "order": 6,
              "key": "EST_TOTAL_PLOT_AREA",
              "field": {
                "code": "EST_TOTAL_PLOT_AREA",
                "name": "totalFloorArea",
                "placeholder": "EST_ENTER_TOTAL_PLOT_AREA",
                "type": "text",
                "numeric": true,
                "unit": "(In sq.ft)"
              },
              "validation": {
                "pattern": "^[0-9]+$",
                "regex": {
                  "pattern": "\\D",
                  "flags": "g"
                },
                "required": true,
                "disabled": false,
                "readOnly": false
              },
              "messages": {
                "error": "EST_INVALID_TOTAL_PLOT_AREA"
              }
            },
            {
              "order": 7,
              "key": "EST_DIMENSION",
              "label": {
                "code": "EST_DIMENSION",
                "unit": "(In sq.ft)"
              },
              "type": "group",
              "children": [
                {
                  "order": 0,
                  "key": "EST_LENGTH",
                  "field": {
                    "code": "EST_LENGTH",
                    "name": "dimensionLength",
                    "placeholder": "EST_LENGTH",
                    "type": "text",
                "numeric": true
                  },
                  "validation": {
                    "pattern": "^[0-9]+$",
                    "regex": {
                      "pattern": "\\D",
                      "flags": "g"
                    },
                    "required": true,
                    "disabled": false,
                    "readOnly": false
                  },
                  "messages": {
                    "error": "EST_INVALID_LENGTH"
                  }
                },
                {
                  "order": 1,
                  "key": "EST_WIDTH",
                  "field": {
                    "code": "EST_WIDTH",
                    "name": "dimensionWidth",
                    "placeholder": "EST_WIDTH",
                    "type": "text",
                "numeric": true
                  },
                  "validation": {
                    "pattern": "^[0-9]+$",
                    "regex": {
                      "pattern": "\\D",
                      "flags": "g"
                    },
                    "required": true,
                    "disabled": false,
                    "readOnly": false
                  },
                  "messages": {
                    "error": "EST_INVALID_WIDTH"
                  }
                }
              ],
              "messages": {
                "error": "dimensionError"
              }
            },
            {
              "order": 8,
              "key": "EST_RATES",
              "field": {
                "code": "EST_RATES",
                "name": "rate",
                "placeholder": "EST_ENTER_RATE",
                "type": "text",
                "numeric": true,
                "unit": "(Per sq ft)"
              },
              "validation": {
                "maxLength": 10,
                "pattern": "^[0-9]+$",
                "regex": {
                  "pattern": "\\D",
                  "flags": "g"
                },
                "required": true,
                "disabled": false,
                "readOnly": false
              },
              "messages": {
                "error": "EST_INVALID_RATE"
              }
            },
            {
              "order": 9,
              "key": "EST_ASSET_REFERENCE_NUMBER",
              "field": {
                "code": "EST_ASSET_REFERENCE_NUMBER",
                "name": "assetRef",
                "placeholder": "EST_ENTER_ASSET_REFERENCE_NUMBER",
                "type": "text"
              },
              "apiFieldName": "refAssetNo",
              "validation": {
                "maxLength": 50,
                "required": false,
                "disabled": false,
                "readOnly": false
              }
            },
            {
              "order": 10,
              "key": "EST_ASSET_TYPE",
              "field": {
                "code": "EST_ASSET_TYPE",
                "name": "assetType",
                "placeholder": "EST_SELECT_ASSET_TYPE",
                "type": "dropdown",
                "dataSource" :{
                  "type":"MDMS",
                  "moduleName":"ASSET",
                  "moduleData":"assetParentCategory",
                  "customiztionRequired":false
                }
                            },
              "validation": {
                "required": false,
                "disabled": false,
                "readOnly": false
              },
            }
          ],
          "actionButton": {
            "text": {
              "create": "SAVE_&_NEXT",
              "edit": "UPDATE"
            },
            "variant": "contained",
            "color": "primary"
          }
        }
      ]
    }
]

