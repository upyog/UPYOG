import { Card, Loader } from "@nudmcdgnpm/digit-ui-react-components";
import React, { useContext, useState } from "react";
import { useTranslation } from "react-i18next";
import { ArrowDownwardElement } from "./ArrowDownward";
import { ArrowUpwardElement } from "./ArrowUpward";
import FilterContext from "./FilterContext";

const MetricData = ({ t, data }) => {
  const { value } = useContext(FilterContext);
  const insight = data?.insight?.value?.replace(/[+-]/g, "")?.split("%");
  return (
    <div>
      <p className="heading-m dss-summary-spacing">
        {`${Digit.Utils.dss.formatter(data?.headerValue, data?.headerSymbol, value?.denomination, true, t)}`}
      </p>
      {data?.insight && (
        <div
          className="dss-summary-fullwidth"
        >
          {data?.insight?.indicator === "upper_green" ? ArrowUpwardElement("10px") : ArrowDownwardElement("10px")}
          <p className={`${data?.insight.colorCode} dss-generic-chart-wrapper-2`}>
            {insight?.[0] &&
              `${Digit.Utils.dss.formatter(insight[0], "number", value?.denomination, true, t)}% ${t(
                Digit.Utils.locale.getTransformedLocale("DSS" + insight?.[1] || "")
              )}`}
          </p>
        </div>
      )}
    </div>
  );
};

const Chart = ({ data }) => {
  const { id, chartType } = data;
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const { t } = useTranslation();
  const { value } = useContext(FilterContext);
  const [showDate, setShowDate] = useState({});
  const isMobile = window.Digit.Utils.browser.isMobile();
  const { isLoading, data: response } = Digit.Hooks.dss.useGetChart({
    key: id,
    type: chartType,
    tenantId,
    requestDate: { ...value?.requestDate, startDate: value?.range?.startDate?.getTime(), endDate: value?.range?.endDate?.getTime() },
    filters: value?.filters,
  });
  if (isLoading) {
    return <Loader />;
  }
  let name = t(data?.name) || "";

  const getWidth = (data) => {
    if (isMobile) return "auto";
    else return t(`TIP_${data.name}`).length < 50 ? "fit-content" : 400;
  };

  const getHeight = (data) => {
    if (isMobile) return "auto";
    else return 50;
  };
  return (
    <div className="blocks cursorPointer dss-generic-chart-wrapper">
      <div className={`tooltip`}>
        {typeof name == "string" && name}
        {Array.isArray(name) && name?.filter((ele) => ele)?.map((ele) => <div className="dss-generic-chart-wrapper-2">{ele}</div>)}
        <span className="dss-white-pre dss-metric-chart-wrapper">
          {" "}
          {showDate?.[id]?.todaysDate}
        </span>
        <span className={`tooltiptext dss-tooltip-metric-box ${t(`TIP_${data.name}`).length < 30 ? "width-fit-content" : "width-400"}`}>
          <span className="dss-metric-chart-text-style">{t(`TIP_${data.name}`)}</span>
          <span className="dss-metric-chart-wrapper-2"> {showDate?.[id]?.lastUpdatedTime}</span>
        </span>
      </div>
      <MetricData t={t} data={response?.responseData?.data?.[0]}></MetricData>
    </div>
  );
};
const Summary = ({ data }) => {
  const { t } = useTranslation();
  const { value } = useContext(FilterContext);
  return (
    <Card className="summary-card-margin dss-summary-card">
      <div className="summary-wrapper">
        <div className="wrapper-child fullWidth">
          <div className="blocks">
            <p>
              {t(data?.name)}{" "}
              {<span className="dss-generic-chart-wrapper-2"> ({t(`DSS_${Digit.Utils.locale.getTransformedLocale(value?.denomination)}`)})</span>}
            </p>
          </div>
          <div className="dss-summary-flex-row">
            {data.charts.map((chart, key) => (
              <Chart data={chart} key={key} url={data?.ref?.url} />
            ))}
          </div>
        </div>
      </div>
    </Card>
  );
};

export default Summary;
