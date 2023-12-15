import React from "react";
import { Card, Row, CardHeader, StatusTable } from "@egovernments/digit-ui-react-components";
import { useTranslation } from "react-i18next";
const DigiLockerConcent = ({ path }) => {
    const { t } = useTranslation();
localStorage.setItem("digilocker",window.location.href.split("=")[1].split("&")[0])
    return (
        <React.Fragment>
            <div style={{ width: "100%" }}>
                <Card>
                    <CardHeader>Receipt Summary</CardHeader>
                    <span>successful</span>
                </Card>
            </div>
        </React.Fragment>
    );
};
export default DigiLockerConcent;