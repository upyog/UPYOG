import { Card, CardHeader, CardSubHeader, CardText, SubmitBar, Loader } from "@nudmcdgnpm/digit-ui-react-components";
import React from "react";
import { stringReplaceAll } from "../utils";

const NOCServiceDoc = ({ t, config, onSelect, userType, formData }) => {
  sessionStorage.removeItem("docReqScreenByBack");
  const stateId = Digit.ULBService.getStateId();

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
        <CardHeader>{t("NOC_REQ_DOCS_HEADER", "Required Documents-Fire NOC")}</CardHeader>
        <div>
          <CardText style={{ color: "red" }}>{t("NOC_PDF_AND_JPG_BOTH_FORMAT_ACCEPTED_IN_DOCUMENT_UPLOAD", "PDF, JPG, PNG formats are accepted")}</CardText>

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
