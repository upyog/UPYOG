import React from "react";
import { Card, Row, CardHeader, StatusTable } from "@egovernments/digit-ui-react-components";
import { useTranslation } from "react-i18next";
import { useParams, useHistory, useRouteMatch } from "react-router-dom";
const ChallanQRCode = ({ path }) => {
    const { t } = useTranslation();
    const convertEpochToDate = (dateEpoch) => {
        // Returning NA in else case because new Date(null) returns Current date from calender
        if (dateEpoch) {
            const dateFromApi = new Date(dateEpoch);
            let month = dateFromApi.getMonth() + 1;
            let day = dateFromApi.getDate();
            let year = dateFromApi.getFullYear();
            month = (month > 9 ? "" : "0") + month;
            day = (day > 9 ? "" : "0") + day;
            return `${day}/${month}/${year}`;
        } else {
            return "NA";
        }
    };
    const convertToLocale = (value = "", key = "") => {
        let convertedValue = convertDotValues(value);
        if (convertedValue == "NA") {
            return "PT_NA";
        }
        return `${key}_${convertedValue}`;
    };

    const convertDotValues = (value = "") => {
        return (
            (checkForNotNull(value) && ((value.replaceAll && value.replaceAll(".", "_")) || (value.replace && stringReplaceAll(value, ".", "_")))) || "NA"
        );
    };
    const checkForNotNull = (value = "") => {
        return value && value != null && value != undefined && value != "" ? true : false;
    };
    const getFinancialYears = (from, to) => {
        const fromDate = new Date(from);
        const toDate = new Date(to);
        if (toDate.getYear() - fromDate.getYear() != 0) {
            return `FY${fromDate.getYear() + 1900}-${toDate.getYear() - 100}`;
        }
        return `${fromDate.toLocaleDateString()}-${toDate.toLocaleDateString()}`;
    };
    const city = window.location.href.split("/challan/details?")?.[1].split("&")?.[0].split("=")[1]
    const challan = window.location.href.split("challan/details?")?.[1].split("&")?.[1].split("=")[1]
    const {isLoading , data, ...rest }= Digit.Hooks.mcollect.useMCollectSearch({
        tenantId:city,
        filters: { challanNo: challan },
      });
    return (
        <React.Fragment>
            <div style={{ width: "100%" }}>
                <Card>
                    <CardHeader>Challan summary</CardHeader>
                    {!isLoading?
                    <StatusTable>
                        <Row label={t("CHALLAN_NUMBER")} text={data?.challans?.[0]?.challanNo|| "NA"} textStyle={{ whiteSpace: "pre" }} />
                        <Row label={t("CHALLAN_SERVICE_TYPE")} text={ t(convertToLocale(data?.challans?.[0]?.businessService))|| "NA"} textStyle={{ whiteSpace: "pre" }} />
                        <Row
                                label={t("CHALLAN_BILL_PERIOD")}
                                text={
                                    getFinancialYears(
                                        data?.challans?.[0]?.taxPeriodFrom,
                                        data?.challans?.[0]?.taxPeriodTo
                                    ) || "NA"
                                }
                        />
                        <Row label ={t("CHALLAN_OWNER_NAME")} text={data?.challans?.[0]?.citizen?.name}/>
                        <Row label ={t("CHALLAN_OWNER_MOBILE_NUMBER")} text={data?.challans?.[0]?.citizen?.mobileNumber}/>
                        <Row label ={t("CHALLAN_OWNER_LOCALITY")} text={data?.challans?.[0]?.address?.locality?.code}/>
                    </StatusTable>:null}
                    
                   
                    
                    
                </Card>
            </div>
        </React.Fragment>
    );
};
export default ChallanQRCode;