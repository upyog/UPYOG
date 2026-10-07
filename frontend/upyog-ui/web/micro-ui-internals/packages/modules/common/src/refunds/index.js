import React from "react";
import { Link, Route, Routes } from "react-router-dom";
import { useTranslation } from "react-i18next";

import { CreateRefund } from "./employee/create";

export const RefundModule = ({ userType }) => {
  const { t } = useTranslation();

  if (userType !== "employee") {
    return null;
  }

  return (
    <React.Fragment>
      <p className="breadcrumb" style={{ marginLeft: "15px" }}>
        <Link to="/upyog-ui/employee">
          {t("ES_COMMON_HOME")}
        </Link>
        {" / "}
        {t("REFUND_OFFLINE_TITLE", {
          defaultValue: "Offline Refund",
        })}
      </p>

      <Routes>
        <Route
          path=":refundId"
          element={<CreateRefund />}
        />
      </Routes>
    </React.Fragment>
  );
};