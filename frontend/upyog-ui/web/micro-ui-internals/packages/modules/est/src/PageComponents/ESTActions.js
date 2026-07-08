import React from "react";
import { useTranslation } from "react-i18next";
import {
    Card,
    Header,
} from "@nudmcdgnpm/digit-ui-react-components";
import { Link } from "react-router-dom";

// EST Actions Component
// This component provides navigation links for managing EST-related actions such as rebates, interest, and penalties.

const ESTActions = () => {
    const { t } = useTranslation();
    const { path: modulePath } = Digit.Hooks.useModuleBasePath();

    return (
        <React.Fragment>
            <div>
                <Header>{t("EST_ACTIONS")}</Header>
                {/* REBATE */}
                <Card style={{ padding: "16px 24px" }}>
                    <div
                        style={{
                            display: "flex",
                            gap: "40px", // space between links
                            alignItems: "center",
                        }}
                    >
                        {/* Manage Rebate Link */}
                        <span className="link">
                            <Link
                                to={`${modulePath}/manage-rebate`}
                                style={{
                                    textDecoration: "none",
                                    color: "#8B0000",
                                    fontWeight: "500",
                                    fontSize: "16px",
                                }}
                            >
                                {t("EST_MANAGE_REBATE")}
                            </Link>
                        </span>

                        {/* INTEREST */}
                        <span classname="link">
                            <Link
                                to={`${modulePath}/manage-interest`}
                                style={{
                                    textDecoration: "none",
                                    color: "#8B0000",
                                    fontWeight: "500",
                                    fontSize: "16px",
                                }}
                            >
                                {t("EST_MANAGE_INTEREST")}
                            </Link>
                        </span>

                        {/* PENALTY */}
                        <span className="link">
                            <Link
                                to={`${modulePath}/manage-penalty`}
                                style={{
                                    textDecoration: "none",
                                    color: "#8B0000",
                                    fontWeight: "500",
                                    fontSize: "16px",
                                }}
                            >
                                {t("EST_MANAGE_PENALTY")}
                            </Link>
                        </span>
                    </div>
                </Card>
            </div>
        </React.Fragment>
    );
};

export default ESTActions;
