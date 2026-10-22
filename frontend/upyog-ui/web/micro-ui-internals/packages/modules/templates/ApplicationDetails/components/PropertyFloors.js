import React from "react";
import { useTranslation } from "react-i18next";
import { CardSubHeader, StatusTable, Row, CardSectionHeader } from "@nudmcdgnpm/digit-ui-react-components";

function PropertyFloors({ floors }) {
  const { t } = useTranslation();

  return (
    <React.Fragment>
      {floors.map((floor) => (
        <div key={t(floor?.title)} className="tmpl-documents-preview-top-spacing">
          <CardSubHeader className="tmpl-property-floors-header">{t(floor?.title)}</CardSubHeader>
          {floor?.values?.map((value, index) => {
            return (
              <React.Fragment key={index}>
                <CardSectionHeader className={index !== 0 ? "card-section-header-spacing-top" : "card-section-header-spacing"}>
                  {t(value.title)}
                </CardSectionHeader>
                <StatusTable className="tmpl-property-floors-table-cell">
                  <div
                    className="tmpl-property-floors-top-spacing"
                  >
                  {value?.values?.map((value, index) => {
                    if (value.map === true && value.value !== "N/A") {
                      return <Row key={t(value.title)} label={t(value.title)} text={<img src={t(value.value)} alt="" />} />;
                    }
                    return (
                      <Row
                        key={t(value.title)}
                        label={t(value.title)}
                        text={t(value.value) || "N/A"}
                        last={index === value?.values?.length - 1}
                        caption={value.caption}
                        className="border-none"
                      />
                    );
                  })}
                  </div>
                </StatusTable>
              </React.Fragment>
            );
          })}
        </div>
      ))}
    </React.Fragment>
  );
}

export default PropertyFloors;
