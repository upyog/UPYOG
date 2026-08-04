import {
  Card,
  CardHeader,
  CardSubHeader,
  CardText,
  SubmitBar,
  Loader,
} from "@nudmcdgnpm/digit-ui-react-components";
import React, { useMemo } from "react";
import styles from "../styles/ESTAssignAstRequiredDoc.module.scss";

const FALLBACK_DOCUMENTS = [
  { name: "Citizen Request Letter (Accepted PDF)" },
  { name: "Allotment Letter (Accepted PDF)" },
  { name: "Signed Deed (Accepted PDF)" },
];

/**
 * Informational wizard step — document list from MDMS step config or Estate.RequiredDocuments.
 */
const ESTAssignAstRequiredDoc = ({ t, onSelect, config }) => {
  const { data: mdmsDocuments, isLoading } = Digit.Hooks.useCustomMDMS(
    Digit.ULBService.getStateId(),
    "Estate",
    [{ name: "RequiredDocuments" }],
    {
      select: (data) => data?.Estate?.RequiredDocuments || [],
    }
  );

  const hasStepDocuments =
    Array.isArray(config?.requiredDocuments) && config.requiredDocuments.length > 0;

  const documents = useMemo(() => {
    if (hasStepDocuments) return config.requiredDocuments;

    const active = (mdmsDocuments || []).filter(
      (doc) => doc.active !== false && doc.active !== "false"
    );
    if (active.length > 0) return active;

    return FALLBACK_DOCUMENTS;
  }, [config, mdmsDocuments, hasStepDocuments]);

  const goNext = () => onSelect(config?.key || "Documents", {});

  if (isLoading && !hasStepDocuments) {
    return <Loader />;
  }

  return (
    <div className={styles.estAssignAstRequiredDoc}>
      <Card>
        <CardHeader>{t(config?.sectionHeading || "MODULE_EST")}</CardHeader>

        <div>
          <CardSubHeader>{t(config?.documentsHeading || "EST_REQUIRED_DOCUMENTS")}</CardSubHeader>

          <div className={styles.documentList}>
            {documents.map((doc, index) => (
              <CardText key={doc.code || doc.i18nKey || index} className="primaryColor">
                {doc.order ?? index + 1}. {t(doc.i18nKey || doc.name || doc.label || "")}
                {/* {doc.acceptedFormat ? ` (${doc.acceptedFormat})` : ""} */}
              </CardText>
            ))}
          </div>
        </div>

        <span className={styles.submitBarWrap}>
          <SubmitBar label={t("COMMON_NEXT")} onSubmit={goNext} />
        </span>
      </Card>
    </div>
  );
};

export default ESTAssignAstRequiredDoc;
