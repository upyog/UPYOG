/**
 * Fetches required document definitions from MDMS (FireNoc.Documents) and displays
 * the mandatory documents and acceptable file formats (PDF, JPG, PNG) to the applicant.
 */
import { Card, CardHeader, CardSubHeader, CardText, SubmitBar, Loader } from "@nudmcdgnpm/digit-ui-react-components";
import React from "react";
import { stringReplaceAll } from "../utils";

/**
 * Renders the NOC Service required document checklist and accepted file types card.
 */
const NOCServiceDoc = ({ t, config, onSelect, userType, formData }) => {
  sessionStorage.removeItem("docReqScreenByBack");
  const stateId = Digit.ULBService.getStateId();

  React.useEffect(() => {
    Digit.SessionStorage.del("NOC_CREATE_APPLICATION");
    Digit.SessionStorage.del("NOC_SUCCESSFUL_APPLICATION");
  }, []);

  const { isLoading, data: docs } = Digit.Hooks.useCustomMDMS(
    stateId,
    "FireNoc",
    [{ name: "Documents" }],
    {
      select: (data) => {
        return data?.FireNoc?.Documents || [];
      },
    }
  );

  return (
    <React.Fragment>
      <Card>
        <CardHeader>{t("NOC_REQ_DOCS_HEADER")}</CardHeader>
        <div>
          <CardText className="astericColor">{t("NOC_PDF_AND_JPG_BOTH_FORMAT_ACCEPTED_IN_DOCUMENT_UPLOAD")}</CardText>

          {isLoading && <Loader />}
          {Array.isArray(docs)
            ? docs.map(({ code, dropdownData, description }, index) => (
                <div key={index}>
                  <CardSubHeader>
                    {index + 1}. {t(stringReplaceAll(code, ".", "_"))}
                  </CardSubHeader>
                  <CardText className="primaryColor">
                    {t(description || "NOC_DOC_REQ_SUBTEXT")}
                  </CardText>
                  {dropdownData && dropdownData.map((dropdownItem, dropdownIndex) => (
                    <CardText className="primaryColor" key={dropdownIndex}>
                      {`${dropdownIndex + 1}`}. {t(stringReplaceAll(dropdownItem?.code, ".", "_"))}
                    </CardText>
                  ))}
                </div>
              ))
            : null}
        </div>
        <span>
          <SubmitBar label={t("CS_COMMON_NEXT")} onSubmit={onSelect} />
        </span>
      </Card>
    </React.Fragment>
  );
};

export default NOCServiceDoc;
