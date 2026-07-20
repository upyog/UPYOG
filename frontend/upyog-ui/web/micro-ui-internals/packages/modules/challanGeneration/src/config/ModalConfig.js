import React from "react";
import { UploadFile } from "@nudmcdgnpm/digit-ui-react-components";

/**
 * ModalConfig:
 * - Generates config for fee waiver modal
 * - Includes amount input and file upload
 */

export const ModalConfig = ({ t, action, setAmount, getChallanData, selectFile, setUploadedFile, uploadedFile }) => {
  const finalAmount = Math.max(getChallanData?.amount?.[0]?.amount || 0, getChallanData?.challanAmount || 0);
  return {
    label: {
      heading: ``,
      submit: `${action?.action}`,
      cancel: "WF_EMPLOYEE_NEWTL_CANCEL",
    },
    form: [
      {
        body: [
          {
            label: `${t("FEE_WAIVER_AMOUNT")} *`,
            populators: (
              <div className="cg-margin-bottom-20">
                <input
                  className="employee-card-input focus-visible cg-input-fullwidth"
                  type="number"
                  onChange={(e) => setAmount(e.target.value)}
                  onWheel={(e) => e.target.blur()}
                  onKeyDown={(e) => {
                    if (e.key === "ArrowUp" || e.key === "ArrowDown") {
                      e.preventDefault();
                    }
                  }}
                />
                <span className="cg-note">
                  <span className="cg-note-red">Note:</span>Please enter amount less than{" "}
                  <span className="cg-note-strong"> {finalAmount}</span>{" "}
                </span>
              </div>
            ),
          },
          {
            label: t("TL_APPROVAL_CHECKLIST_BUTTON_UP_FILE"),
            populators: (
              <div>
                <UploadFile
                  id={"workflow-doc"}
                  onUpload={selectFile}
                  onDelete={() => {
                    setUploadedFile(null);
                  }}
                  message={uploadedFile ? `1 ${t(`ES_PT_ACTION_FILEUPLOADED`)}` : t(`CS_ACTION_NO_FILEUPLOADED`)}
                />
              </div>
            ),
          },
        ],
      },
    ],
  };
};
