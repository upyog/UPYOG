export const calculateRiskType = (riskTypes, plotArea, blocks) => {
  const buildingHeight = blocks?.reduce((acc, block) => {
    return Math.max(acc, block.building.buildingHeightExcludingMP)
  }, Number.NEGATIVE_INFINITY);

   // Removing plot area condition for the calculation of riskType because punjab only 
   //needs building height to calculate riskType

  const risk = riskTypes?.find(riskType => {
    if (riskType.riskType === "HIGH" &&  buildingHeight >= riskType?.fromBuildingHeight) {
      return true;
    }

    if (riskType.riskType === "MEDIUM" && 
    (buildingHeight >= riskType?.fromBuildingHeight && buildingHeight <= riskType?.toBuildingHeight)) {
      return true;
    }

    if (riskType?.riskType === "LOW" && buildingHeight < riskType.toBuildingHeight) {
      return true;
    }

    return false;
  })
  return risk?.riskType;
}