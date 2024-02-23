import React from "react";
import { Button } from "components";
import { ImageUpload } from "modules/common";
import { TextArea } from "modules/common";
import { httpRequest } from "egov-ui-kit/utils/api";

const ComplaintResolvedForm = ({ formKey, form, handleFieldChange, onSubmit }) => {
      var complaintId = (window.location.pathname.substring(window.location.pathname.lastIndexOf('/') + 1)).replace(/%2F/gi, "/");
      const updateStatus = async () => { await httpRequest("rainmaker-pgr/v1/requests/_apicall?complaintId="+complaintId);
        };
      
  const fields = form.fields || {};
  const submit = form.submit;
  return (
    <div>
      <div className="custom-padding-for-screens">
        <ImageUpload module="rainmaker-pgr" formKey={formKey} fieldKey="media" />
        <div style={{ padding: "24px 16px 0px 1px" }}>
          <TextArea onChange={(e, value) => handleFieldChange("textarea", value)} {...fields.textarea} />
        </div>
      </div>
      <div className="responsive-action-button-cont">
        <Button
          onClick={onSubmit, updateStatus}
          className="responsive-action-button"
          id={"complaint-resolved-mark-resolved"}
          {...submit}
          primary={true}
          fullWidth={true}
        />
      </div>
    </div>
  );
};

export default ComplaintResolvedForm;
