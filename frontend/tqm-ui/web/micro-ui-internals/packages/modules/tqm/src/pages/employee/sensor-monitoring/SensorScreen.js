import React, { useMemo } from "react";
import { useTranslation } from "react-i18next";
import { Header, InboxSearchComposer, Loader } from "@egovernments/digit-ui-react-components";
import { SensorScreenConfigUlbAdmin } from "./configUlbAdmin";
import { SensorScreenConfigPlantOperator } from "./configPlantOperator";
const SensorScreen = () => {
    const { t } = useTranslation();
    const configModuleName = Digit.Utils.getConfigModuleName()
    const tenant = Digit.ULBService.getStateId();
    const { isLoading, data } = Digit.Hooks.useCustomMDMS(
        tenant,
        configModuleName,
        [
            {
                "name": "SearchEstimateWMSConfig"
            }
        ],
        {
            select: (data) => {

                if (Digit.Utils.tqm.isPlantOperatorLoggedIn()) {
                    return SensorScreenConfigPlantOperator ?.SensorConfig?.[0]
                }
                if (Digit.Utils.tqm.isUlbAdminLoggedIn()) {
                    return SensorScreenConfigUlbAdmin?.SensorConfig?.[0]
                }
                // return tqmSearchConfigPlantOperator?.tqmSearchConfig?.[0]
                return {

                }
            },
        }
    );
    // const configs = Digit.Utils.configUpdater(searchConfigMuktaFuzzy())

    // const configs = data?.[configModuleName].SearchEstimateWMSConfig?.[0]
    let configs = useMemo(
        () => Digit.Utils.preProcessMDMSConfigInboxSearch(t, data, "sections.search.uiConfig.fields", {
            updateDependent: [
                {
                    key: "fromProposalDate",
                    value: [new Date().toISOString().split("T")[0]]
                },
                {
                    key: "toProposalDate",
                    value: [new Date().toISOString().split("T")[0]]
                }
            ]
        }
        ), [data]);


    if (isLoading) return <Loader />
    return (
        <React.Fragment>
            <Header className="works-header-search">{t(configs?.label)}</Header>
            <div className="inbox-search-wrapper">
                <InboxSearchComposer configs={configs}></InboxSearchComposer>
            </div>
        </React.Fragment>
    )
}

export default SensorScreen;