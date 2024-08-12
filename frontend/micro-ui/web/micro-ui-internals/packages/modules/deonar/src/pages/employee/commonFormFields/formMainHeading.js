import React, { useState } from "react";
import { useTranslation } from "react-i18next";

const MainFormHeader = ({title}) => {
  const { t } = useTranslation();

  return (
    <div className="bmc-row-card-header" style={{ padding: "1rem" }}>
        <div className="bmc-title" style={{ textAlign: "center", paddingBottom: "0", padding: "1rem" }}>
            {t(title.toString().toUpperCase())}
        </div>
    </div>
  );
};

export default MainFormHeader;
