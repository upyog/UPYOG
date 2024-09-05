export const createModifiedData = (data) => {
    const modifiedData = [
        {
            "tenantId": Digit.ULBService.getCurrentTenantId(),
            "plantCode": data?.TestStandard?.plantCode?.plantCode,
            "processCode": data?.TestStandard?.processCode?.process,
            "stageCode": data?.TestStandard?.stageCode?.stage,
            "materialCode": data?.TestStandard?.materialCode?.material,
            "testCriteria": Object.keys(data?.QualityParameter)
                .filter(criteriaCode => criteriaCode.toLowerCase() !== "document")
                .map(criteriaCode => ({
                    criteriaCode: criteriaCode,
                    resultValue: data?.QualityParameter[criteriaCode],
                    isActive: true
                })),
            "testType": "LAB_ADHOC",
            "scheduledDate": null,
            "isActive": null,
            "documents": data?.QualityParameter?.document?.length > 0
                ? [
                    {
                        "fileStoreId": data?.QualityParameter?.document?.[0]?.[1]?.fileStoreId?.fileStoreId
                    }
                ]
                : [],
            "additionalDetails": {},
            "workflow": null
        }
    ]
    return modifiedData;
};