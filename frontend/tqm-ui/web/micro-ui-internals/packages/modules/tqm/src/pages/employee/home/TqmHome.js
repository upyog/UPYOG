import React, { Fragment, useEffect, useState } from 'react'
import TqmCard from '../../../components/TqmCard'
import Alerts from '../../../components/Alerts'
import YourPerformance from '../../../components/YourPerformance'
import { useTranslation } from "react-i18next";
import getDateRange from '../../../utils/formatDate';

const TqmHome = (props) => {
  const { t } = useTranslation();
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const endDate = Date.now();
  const startDate = new Date(endDate);
  startDate.setMonth(startDate.getMonth() - 1);
  const [dateRange, setDateRange] = useState(getDateRange({ startDate: startDate.getTime(), endDate: endDate }));
  
  useEffect(() =>{
    // removing scroll persistent
    localStorage.removeItem("/tqm-ui/employee/tqm/inbox");
    localStorage.removeItem("/tqm-ui/employee/tqm/test-details");
  }, [])

  const activePlantCode = Digit.SessionStorage.get('active_plant')?.plantCode
    ? [Digit.SessionStorage.get('active_plant')?.plantCode]
    : Digit.SessionStorage.get('user_plants')
        ?.filter((row) => row.plantCode)
        ?.map((row) => row.plantCode);
  

  const requestCriteria1 = {
    url: "/dashboard-analytics/dashboard/getChartV2",
    params: {},
    body: {
      "aggregationRequestDto": {
        "visualizationType": "METRIC",
        "visualizationCode": "pqmTestCompliance",
        "queryType": "",
        "filters": {
          "plantCode":activePlantCode?.length > 0 ? activePlantCode : [],
          "tenantId": tenantId
        },
        "moduleLevel": "PQM",
        "aggregationFactors": null,
        "requestDate": {
          "startDate": startDate.getTime(),
          "endDate": endDate,
        },
        "source": "es"
      },
      "headers": {
        "tenantId": tenantId
      }
    },
    changeQueryName: "testCompliance",
    staletime: 0
  };
  const { isLoading, data: data1 } = Digit.Hooks.useCustomAPIHook(requestCriteria1);
  
  const requestCriteria2 = {
    url: "/dashboard-analytics/dashboard/getChartV2",
    params: {},
    body: {
      "aggregationRequestDto": {
        "visualizationType": "METRIC",
        "visualizationCode": "pqmPercentageOfTestResultsMeetingBenchmarks",
        "queryType": "",
        "filters": {
          "plantCode":activePlantCode?.length > 0 ? activePlantCode : [],
          "tenantId": tenantId
        },
        "moduleLevel": "PQM",
        "aggregationFactors": null,
        "requestDate": {
          "startDate": startDate.getTime(),
          "endDate": endDate,
        },
        "source": "es"
      },
      "headers": {
        "tenantId": tenantId
      }
    },
    changeQueryName: "percentage",
    staletime: 0
  };
  const { data: data2 } = Digit.Hooks.useCustomAPIHook(requestCriteria2);

  const requestCriteria3 = {
    url: "/dashboard-analytics/dashboard/getChartV2",
    params: {},
    body: {
      "aggregationRequestDto": {
        "visualizationType": "xtable",
        "visualizationCode": "pqmAlerts",
        "queryType": "",
        "filters": {
          "tenantId": [tenantId],
          "plantCode":activePlantCode?.length > 0 ? activePlantCode : [],
        },
        "moduleLevel": "PQM",
        "aggregationFactors": null,
        "requestDate": {
          "startDate": startDate.getTime(),
          "endDate": endDate
        },
        "source": "es"
      },
      "headers": {
        "tenantId": tenantId
      }
    },
    changeQueryName: "alerts",
    staletime: 0
  };
  const { data: alerts } = Digit.Hooks.useCustomAPIHook(requestCriteria3);
  const userNotLinkedToPlant = Digit.Utils.tqm.isUserNotLinkedToPlant()
  return (
    <div className='tqm-home-container'>
      <TqmCard t={t} reRoute={false} />
      <div className='dashboard-container' style={userNotLinkedToPlant? {display:"none"}:{}}>
      <YourPerformance performance={[data1, data2]} dateRange={dateRange} />
      <Alerts ale={alerts} />
      </div>
    </div>
  )
}

export default TqmHome