// Config File For Edit Case
export const EditConfig =
[
    {
        "body": [
            {
                "route": "property-details",
                "component": "PropertyNature",
                "withoutLabel": true,
                "key": "propertyNature",
                "type": "component",
                "nextStep": "waste-material-details",
                "isMandatory": true,
                "texts": {
                    "header": "CND_NATURE_PROPERTY",
                }
            },
            {
                "route": "waste-material-details",
                "component": "WasteType",
                "withoutLabel": true,
                "key": "wasteType",
                "type": "component",
                "nextStep": null,
                "isMandatory": true,
                "texts": {
                    "header": "CND_WASTE_TYPE",
                }
            }
        ],
    },
];