// utils/getMDMSRequestsFromConfig.js
export const getMDMSRequestsFromConfig = (formConfig = []) => {
  const requests = [];

  formConfig.forEach((item) => {
    // handle top-level dropdown fields
    if (item?.field?.type === "dropdown" && item?.field?.dataSource?.type === "MDMS") {
      requests.push({
        key: item.key,
        moduleName: item.field.dataSource.moduleName,
        masterName: item.field.dataSource.moduleData,
      });
    }

    // handle nested "group" type fields (like EST_DIMENSION -> children)
    if (item?.type === "group" && Array.isArray(item.children)) {
      item.children.forEach((child) => {
        if (child?.field?.type === "dropdown" && child?.field?.dataSource?.type === "MDMS") {
          requests.push({
            key: child.key,
            moduleName: child.field.dataSource.moduleName,
            masterName: child.field.dataSource.moduleData,
          });
        }
      });
    }
  });

  return requests;
};