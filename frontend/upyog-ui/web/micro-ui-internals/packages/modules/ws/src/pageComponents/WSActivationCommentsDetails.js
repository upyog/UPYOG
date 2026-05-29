import { CardLabel, LabelFieldPair, TextInput } from "@upyog/digit-ui-react-components";
import React, { useState, useEffect } from "react";
import "../css/ws-inline-auto.css";
const WSActivationCommentsDetails = ({
  t,
  config,
  userType,
  formData,
  onSelect
}) => {
  const [comments, setComments] = useState({
    comments: formData?.comments?.comments || ""
  });
  useEffect(() => {
    if (formData && formData?.comments) {}
  });
  useEffect(() => {
    if (userType === "employee") {
      onSelect(config.key, {
        ...formData[config.key],
        ...comments
      });
    }
  }, [comments]);
  return <React.Fragment>
            <LabelFieldPair>
                <CardLabel className="card-label-smaller ws-auto-31">{t("WF_COMMON_COMMENTS")}:</CardLabel>
                <div className="field">
                    <TextInput key={config.key} value={comments.comments} onChange={ev => {
          setComments({
            ...comments,
            comments: ev.target.value
          });
        }}></TextInput>
                </div>
            </LabelFieldPair>
        </React.Fragment>;
};
export default WSActivationCommentsDetails;
