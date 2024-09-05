import React, { Fragment, useEffect, useState } from "react";
import { CardLabel, CheckBox, Loader } from "@egovernments/digit-ui-react-components";

const WorkflowFilter = ({ props, t, populators, formData, responseData }) => {
  //from inbox response get the statusMap and show the relevant statuses
  //here need to filter these options based on logged in user(and test based on single roles in every inbox)(new requirement from vasanth)
  const [statusMap, setStatusMap] = useState(null);

  function mergeObjects(jsonArray) {
    const mergedMap = new Map();

    jsonArray?.forEach((obj) => {
      const key = `${obj.applicationstatus}`;
      if (!mergedMap.has(key)) {
        mergedMap.set(key, { ...obj });
      } else {
        const mergedObj = mergedMap.get(key);
        mergedObj.count += obj.count;
        mergedObj.statusid += `, ${obj.statusid}`;
        mergedObj.businessservice += `, ${obj.businessservice}`;
        mergedMap.set(key, mergedObj);
      }
    });

    return Array.from(mergedMap.values());
  }

  useEffect(() => {
    if (responseData) {
      setStatusMap(mergeObjects(responseData.statusMap));
    }
  }, [responseData]);

  if (!statusMap && !responseData) return <Loader />;

  return (
    <>
      {statusMap && statusMap.length > 0 && populators?.componentLabel && (
        <CardLabel style={{ ...props.labelStyle, marginBottom: "0.4rem" }}>
          {t(populators?.componentLabel)}
          {populators?.isMandatory ? " * " : null}
        </CardLabel>
      )}
      {statusMap?.map((row) => {
        return (
          <CheckBox
            onChange={(e) => {
              const obj = {
                ...props.value,
                [e.target.value]: e.target.checked,
              };
              props.onChange(obj);
            }}
            value={row.statusid}
            // checked={formData?.[populators.name]?.[row.uuid]}
            checked={formData?.[populators.name]?.[row.statusid]?true:false}
            label={t(Digit.Utils.locale.getTransformedLocale(`CS_COMMON_FSM_${row?.applicationstatus}`))}
          />
        );
      })}
    </>
  );
};

export default WorkflowFilter;
