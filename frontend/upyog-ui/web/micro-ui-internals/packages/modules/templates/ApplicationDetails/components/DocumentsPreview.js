import React from "react";
import { useTranslation } from "react-i18next";
import { CardSubHeader, PDFSvg } from "@nudmcdgnpm/digit-ui-react-components";

function DocumentsPreview({ documents, svgStyles = {}, isSendBackFlow = false, isHrLine = false, titleStyles }) {
    const { t } = useTranslation();
    const isStakeholderApplication = window.location.href.includes("stakeholder");

    return (
        <div className="tmpl-documents-preview-top-spacing">
            {!isStakeholderApplication && documents?.map((document, index) => (
                <React.Fragment key={index}>
                    {document?.title ? <CardSubHeader style={titleStyles ? titleStyles : { marginTop: "32px", marginBottom: "8px", color: "#505A5F", fontSize: "24px" }}>{t(document?.title)}</CardSubHeader> : null}
                    <div className="tmpl-documents-preview-flex-row">
                        {document?.values && document?.values.length > 0 ? document?.values?.map((value, index) => (
                            <a target="_" href={value?.url} className="tmpl-documents-preview-spacing" key={index}>
                                <div className="tmpl-documents-preview-flex-row-2">
                                    <PDFSvg />
                                </div>
                                <p className="tmpl-documents-preview-centered">{t(value?.title)}</p>
                                {isSendBackFlow ? value?.documentType?.includes("NOC") ? <p className="tmpl-documents-preview-centered-2">{t(value?.documentType.split(".")[1])}</p> : <p className="tmpl-documents-preview-centered-2">{t(value?.documentType)}</p> : ""}
                            </a>
                        )) : !(window.location.href.includes("citizen")) && <div><p>{t("BPA_NO_DOCUMENTS_UPLOADED_LABEL")}</p></div>}
                    </div>
                    {isHrLine && documents?.length != index + 1 ? <hr className="tmpl-documents-preview-top-spacing-2" /> : null}
                </React.Fragment>
            ))}
            {isStakeholderApplication && documents?.map((document, index) => (
                <React.Fragment key={index}>
                    {document?.title ? <CardSubHeader className="tmpl-documents-preview-header">{t(document?.title)}</CardSubHeader> : null}
                    <div>
                        {document?.values && document?.values.length > 0 ? document?.values?.map((value, index) => (
                            <a target="_" href={value?.url} className="min-width-160" key={index}>
                                <div className="tmpl-documents-preview-container-padding">
                                    <p className="tmpl-documents-preview-top-spacing-3">{t(value?.title)}</p>
                                    {value?.docInfo ? <div className="tmpl-documents-preview-bottom-spacing">{`${t(value?.docInfo)}`}</div> : null}
                                    <PDFSvg />
                                    <p className="tmpl-documents-preview-centered-3">{`${t(value?.title)}`}</p>
                                </div>
                            </a>
                        )) : !(window.location.href.includes("citizen")) && <div><p>{t("BPA_NO_DOCUMENTS_UPLOADED_LABEL")}</p></div>}
                    </div>
                </React.Fragment>
            ))}
        </div>
    );
}

export default DocumentsPreview;
