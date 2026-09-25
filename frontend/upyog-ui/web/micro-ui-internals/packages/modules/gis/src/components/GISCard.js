import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { EmployeeModuleCard, CHBIcon } from "@nudmcdgnpm/digit-ui-react-components";

/**
 * `GISCard` component is a module card that displays information related to the GIS service.
 * It provides links for navigating to various GIS-related pages such as map view and polygon services.
 * It conditionally renders the links based on the user's role (GIS_CEMP). If the user doesn't have access to the GIS service, the component returns null.
 * 
 * @returns {JSX.Element} A module card displaying GIS-related links.
 */
const GISCard = () => {
        const { t } = useTranslation();

        const [total, setTotal] = useState("-");



        if (!Digit.Utils.gisAccess()) {
                return null;
        }
        const links = [

                {
                        label: t("MAP_VIEW"),
                        link: `/upyog-ui/employee/gis/servicetype`
                },
                {
                        label: t("MAP_LAYER_VIEW"),
                        link: `/upyog-ui/employee/gis/layerview`
                },
                {
                        label: t("MAP_POLYGON_ASSET"),
                        link: `/upyog-ui/employee/gis/viewpolygon`
                },

        ]
        const GIS_CEMP = Digit.UserService.hasAccess(["GIS_CEMP"]) || false;


        const propsForModuleCard = {
                Icon: <CHBIcon />,
                moduleName: t("GIS"),
                kpis: [
                        {
                        },
                ],
                links: links.filter(link => !link?.role || GIS_CEMP),
        };

        return <EmployeeModuleCard {...propsForModuleCard} />;
};

export default GISCard;
