
import React from "react";
import { useTranslation } from "react-i18next";
import { CardSubHeader, StatusTable, Row, CardSectionHeader } from "@nudmcdgnpm/digit-ui-react-components";

function TLTradeAccessories({ units }) {
  const { t } = useTranslation();
  return (
    <React.Fragment>
      {units.map((unit, index) => (
        // TODO, Later will move to classes
        <div key={t(unit?.title)} className="tmpl-tltrade-accessories-top-spacing">
          <CardSubHeader className="tmpl-bpadocuments-header">{`${t(unit?.title)} ${index + 1}`}</CardSubHeader>
          <React.Fragment key={index}>
            <StatusTable className="tmpl-tltrade-accessories-table-cell">
              <div
                className="tmpl-tltrade-accessories-wrapper"
              ></div>
              {unit?.values?.map((value, index) => {
                if (value.map === true && value.value !== "N/A") {
                  return <Row key={t(value.title)} label={t(value.title)} text={<img src={t(value.value)} alt="" />} />;
                }
                return (
                  <Row
                    key={t(value.title)}
                    label={`${t(value.title)}:`}
                    text={t(value.value) || "N/A"}
                    last={index === value?.values?.length - 1}
                    caption={value.caption}
                    className="border-none"
                    // TODO, Later will move to classes
                    rowContainerStyle={{justifyContent: "space-between", fontSize: "16px", lineHeight: "19px", color: "#0B0C0C"}}
                  />
                );
              })}
            </StatusTable>
          </React.Fragment>
        </div>
      ))}
    </React.Fragment>
  );
}

export default TLTradeAccessories;
