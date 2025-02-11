import React, { Fragment } from 'react'
import { Card, Header } from '@egovernments/digit-ui-react-components'
import { useTranslation } from "react-i18next"
const alerts = ({ ale }) => {
  const { t } = useTranslation();

  return [
    {
      label: t("PQM_RESULTS_TEST_NOT_SUBMITTED"),
      count: ale?.responseData?.data?.[0]?.plots?.[2]?.value || 0
    },
    {
      label: t("PQM_RESULTS_NOT_UPTO_BENCHMARK"),
      count: ale?.responseData?.data?.[0]?.plots?.[3]?.value || 0
    },
  ]
}

const Alerts = ({ ale }) => {
  const { t } = useTranslation();
  const data = alerts({ ale });
  let isMobile = window.Digit.Utils.browser.isMobile();

  if (isMobile) {
    return (
      <Card className={'alerts-container'} style={{ paddingLeft: "0px", paddingRight: "0px" }}>
        <div className='alerts-container-header alerts-container-item'>
          <div>
            <p>
              {t("PQM_TEST_ALERTS")}
            </p>
            <p className='alerts-container-subheader'>{t("PQM_TEST_ALERTS_LAST_30_DAYS")}</p>
          </div>
          <div className='alerts-container-count'>{ale?.responseData?.data?.[0]?.plots?.[4]?.value}</div>
        </div>

        <div className={'alerts-container'}>
          {data?.map((alert, alertIdx) => {
            return (
              <div className='alerts-container-item'>
                <p className='alerts-container-item-label'> {alert.label} </p>
                <p className='alerts-container-item-count'>{alert.count} </p>
              </div>
            )
          })}
        </div>

      </Card>
    )
  }

  return (
    <div>
      <div className='alert-word'>
        <Header styles={{fontSize:"26px"}}>{t("PQM_TEST_ALERTS")}</Header>
      </div>
      <Card className={'alerts-container1'} style={{ padding: "0px", display: "grid", gridTemplateColumns: "1fr 1fr", backgroundColor: "#FAFAFA" }}>
        <div className='alerts-container-header alerts-container-item'>
          <div className='kk'>
            <div className='alerts-container-count1'>{ale?.responseData?.data?.[0]?.plots?.[4]?.value}</div>
            <div>
              <p className='alerts-container-subheader'>{t("PQM_TEST_ALERTS_LAST_30_DAYS")}</p>
            </div>
          </div>
        </div>
        <div className={'alerts-container1'}>
          {data?.map((alert, alertIdx) => {
            return (
              <div className='alerts-container-item'>
                <p className='alerts-container-item-label'> {alert.label} </p>
                <p className={`alerts-container-item-count-${alertIdx}`}>{alert.count} </p>
              </div>
            )
          })}
        </div>

      </Card>
    </div>
  )
}

export default Alerts;