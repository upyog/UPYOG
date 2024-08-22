import { EmployeeModuleCard, PersonIcon } from "@egovernments/digit-ui-react-components";
import React from "react";
import { useTranslation } from "react-i18next";

const DEONARCard = () => {
  const { t } = useTranslation();

  const formatLabel = (label) => {
    const [beforeDash, afterDash] = label.split(" - ");
    return (
      <React.Fragment>
        <strong>{beforeDash}</strong>
        <br />
        {afterDash}
      </React.Fragment>
    );
  };

  const propsForModuleCard = {
    Icon: <PersonIcon />,
    moduleName: t("DEONAR"),
    kpis: [],
    links: [
      {
        label: formatLabel(t("SECURITY CHECK - ARRIVAL")),
        link: `/${window?.contextPath}/employee/deonar/securitycheck`,
      },
      {
        label: t("PARKING FEE"),
        link: `/${window?.contextPath}/employee/deonar/parking`,
      },
      {
        label: t("REMOVAL"),
        link: `/${window?.contextPath}/employee/deonar/removal`,
      },
      {
        label: formatLabel(t("COLLECTION POINT - ASSIGN SHOPKEEPER")),
        link: `/${window?.contextPath}/employee/deonar/assignshopkeeper`,
      },
      {
        label: t("ENTRY FEE"),
        link: `/${window?.contextPath}/employee/deonar/entryfee`,
      },
      {
        label: t("REMOVAL FEE"),
        link: `/${window?.contextPath}/employee/deonar/removalfee`,
      },
      {
        label: t("STABLING FEE"),
        link: `/${window?.contextPath}/employee/deonar/stablingfee`,
      },
      {
        label: formatLabel(t("INSPECTION POINT - ANTE MORTEM INSPECTION")),
        link: `/${window?.contextPath}/employee/deonar/antemorteminspection`,
      },
      {
        label: t("RE-ANTE MORTEM INSPECTION"),
        link: `/${window?.contextPath}/employee/deonar/reantemorteminspection`,
      },
      {
        label: t("BEFORE SLAUGHTER ANTE MORTEM INSPECTION"),
        link: `/${window?.contextPath}/employee/deonar/antemortembeforeslaughterinspection`,
      },
      {
        label: t("POST MORTEM INSPECTION"),
        link: `/${window?.contextPath}/employee/deonar/postmorteminspection`,
      },
      {
        label: formatLabel(t("SLAUGHTER RECOVERY POINT - SLAUGHTER FEE RECOVERY")),
        link: `/${window?.contextPath}/employee/deonar/slaughterfeerecovery`,
      },
      {
        label: formatLabel(t("DELIVERY POINT - VEHICLE WASHING CHARGE COLLECTION")),
        link: `/${window?.contextPath}/employee/deonar/vehiclewashing`,
      },
      {
        label: t("WEIGHING CHARGE"),
        link: `/${window?.contextPath}/employee/deonar/weighingcharge`,
      },
      {
        label: t("PENALTY CHARGE"),
        link: `/${window?.contextPath}/employee/deonar/penaltyCharge`,
      },
      {
        label: t("GATE PASS"),
        link: `/${window?.contextPath}/employee/deonar/gatePass`,
      },
    ],
    longModuleName: false,
  };
  return <EmployeeModuleCard {...propsForModuleCard} />;
};

export default DEONARCard;
