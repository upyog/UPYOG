import React from "react";
import { Card, Row, CardHeader, StatusTable } from "@egovernments/digit-ui-react-components";
import { useTranslation } from "react-i18next";
const DigiLockerConcent = ({ path }) => {
    const { t } = useTranslation();
localStorage.setItem("digilocker",window.location.href.split("=")[1].split("&")[0])
    return (
        <React.Fragment>
            <div style={{ width: "100%" }}>
                {window.location.href.includes("error")?
                <Card>
                    <CardHeader>DigiLocker Concent Error</CardHeader>
                    <span>Concent has been denied</span>
                </Card>:
                <Card>
                <CardHeader>DigiLocker Concent</CardHeader>
                <span>Concent recieved. Now documents can be shared from Digilocker by using "Fetch from Digilocker" button on document upload screen</span>
            </Card>}
            </div>
        </React.Fragment>
    );
};
export default DigiLockerConcent;