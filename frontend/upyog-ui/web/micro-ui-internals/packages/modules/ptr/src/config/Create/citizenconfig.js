/**
 * @file citizenConfig.js
 * @description Configuration for the citizen flow in the pet registration (PTR) service.
 * 
 * @structure
 * - `head`: Section title (localized).
 * - `body`: Steps with:
 *   - `route`: URL path.
 *   - `component`: Rendered component.
 *   - `nextStep`: Navigation target.
 *   - `key`: Unique identifier.
 *   - `isMandatory`: Marks required steps.
 *   - `texts`: Localization keys for labels and buttons.
 * 
 * @sections
 * 1. **Owner Details**: Document and citizen info (`PTRServiceDoc`, `PTRCitizenDetails`).
 * 2. **Pet Details**: Pet info (`PTRCitizenPet`).
 * 3. **Location Details**: Property search and address selection.
 * 4. **Document Details**: Proof of identity (`PTRSelectProofIdentity`).
 * 
 * @usage
 * Used to define steps and navigation in the citizen's PTR application.
 */


export const citizenConfig =
    [
        {
            "head": "ES_TITILE_OWNER_DETAILS",
            "body": [
                {
                    "route": "info",
                    "component": "PTRServiceDoc",
                    "nextStep": "owners",
                    "key": "Documents"
                },


                {
                    "route": "owners",
                    "component": "PTRCitizenDetails",
                    "withoutLabel": true,
                    "key": "ownerss",
                    "type": "component",
                    "nextStep": "pet-details",
                    "hideInEmployee": true,
                    "isMandatory": true,
                    "texts": {
                        "submitBarLabel": "PTR_COMMON_NEXT",
                    }
                },
            ],
        },

        {
            "head": "ES_TITILE_PET_DETAILS",
            "body": [
                {
                    "route": "pet-details",
                    "component": "PTRCitizenPet",
                    "withoutLabel": true,
                    "key": "pets",
                    "type": "component",
                    "isMandatory": true,
                    "hideInEmployee": true,
                    "nextStep": "address",
                    "texts": {
                        "submitBarLabel": "PTR_COMMON_NEXT",
                    }
                },
            ],
        },

        {
            "head": "PTR_LOCATION_DETAILS",
            "body":
                [
                    {
                        "route": "address",
                        "component": "PTRSelectAddress",
                        "withoutLabel": true,
                        "texts": {
                            "submitBarLabel": "PTR_COMMON_NEXT",
                        },
                        "key": "address",
                        "nextStep": "documents",
                        "isMandatory": true,
                        "type": "component",
                    },
                ],
        },


        {
            "head": "ES_TITILE_DOCUMENT_DETAILS",
            "body": [
                {
                    "route": "documents",
                    "component": "PTRSelectProofIdentity",
                    "withoutLabel": true,
                    "key": "documents",
                    "type": "component",
                    "nextStep": null,
                    "texts": {
                        "submitBarLabel": "PTR_COMMON_NEXT",
                    },

                }
            ],
        },
    ];