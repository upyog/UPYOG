import React, { Fragment, useEffect, useState } from "react";
import CheckBox from "../atoms/CheckBox";
import { Loader } from "../atoms/Loader";
import CardLabel from "../atoms/CardLabel";

const WorkflowStatusFilter = ({ props, t, populators, formData, inboxResponse }) => {
  //from inbox response get the statusMap and show the relevant statuses
  //here need to filter these options based on logged in user(and test based on single roles in every inbox)(new requirement from vasanth)

  const [statusMap, setStatusMap] = useState(null);
  useEffect(() => {
    if (inboxResponse) {
      setStatusMap(
        inboxResponse.statusMap?.map((row) => {
          return {
            uuid: row.statusid,
            state: row.state || row.applicationstatus,
            businessService: row?.businessservice,
            count: row?.count,
          };
        })
      );
    }
  }, [inboxResponse]);

  if (!statusMap && !inboxResponse) return <Loader />;

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
              // TODO:: set formData in such a way that it's an array of objects instead of array of ids so that we are able to show removable crumbs for this
            }}
            value={row.uuid}
            checked={formData?.[populators.name]?.[row.uuid] ? true : false}
            label={`${t(Digit.Utils.locale.getTransformedLocale(`${populators.labelPrefix}${row?.businessService}_STATE_${row?.state}`))} (${row?.count || 0})`}
          />
        );
      })}
    </>
  );
};

export default WorkflowStatusFilter;
