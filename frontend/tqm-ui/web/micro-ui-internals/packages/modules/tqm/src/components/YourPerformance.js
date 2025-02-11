import React, { Fragment,useState,useRef } from 'react'
import { Card, Header, CardSubHeader, CardHeader, MultiLink, ShareIcon, ArrowDownward, ArrowUpward,EmailIcon,WhatsappIcon } from '@egovernments/digit-ui-react-components'
import { useTranslation } from "react-i18next"


const VerticalLine = () => {
  return (
    <div className="vertical-line"></div>
  );
}

const Compliance = ({ performance }) => {
  const { t } = useTranslation();
  
  return (
    <div className='performance-item'>
      <div className='performance-item-percentage'>{performance?.[0]?.responseData?.data?.[0]?.headerValue}%</div>
      <div className='performance-item-label'>{t("PQM_TEST_COMPLIANCE")}</div>
      {/* <div className='performance-item-label-caption performance-item-label-caption__fail'> <ArrowDownward />10% than State average</div> */}
    </div>
  );
}

const Output = ({ performance }) => {
  const { t } = useTranslation();
  
  return (
    <div className='performance-item'>
      {/* <span className='performance-item-sla performance-item-sla__pass'>Pass</span> */}
      <div className='performance-item-percentage'>{performance?.[1]?.responseData?.data?.[0]?.headerValue}%</div>
      <div className='performance-item-label'>{t("PQM_QUALITY_TEST_PASSED")}</div>
      {/* <div className='performance-item-label-caption'>{formatDate(startDate)}</div> */}
    </div>
  )
}

const YourPerformance = ({ performance, dateRange }) => {
  const { t } = useTranslation();
  const [showOptions, setShowOptions] = useState(false);
  const fullPageRef = useRef()
  const shareOptions = [
    {
      icon: <EmailIcon />,
      label: t("SHARE_IMG_EMAIL"),
      onClick: (e) => {
        setShowOptions(!showOptions);
        setTimeout(() => {
          return Digit.ShareFiles.Image(Digit.ULBService.getCurrentTenantId(), fullPageRef, t("ES_PERFORMANCE"), "mail");
        }, 500);
      },
    },
    {
      icon: <WhatsappIcon />,
      label: t("SHARE_IMG_WHATSAPP"),
      onClick: (e) => {
        setShowOptions(!showOptions);
        setTimeout(() => {
          return Digit.ShareFiles.Image(Digit.ULBService.getCurrentTenantId(), fullPageRef, t("ES_PERFORMANCE"), "whatsapp");
        }, 500);
      },
    },
  ]

  return (
    <div className='get-this-class' ref={fullPageRef}>
      <div className="performance-header">
        <Header
          styles={{ fontSize: '26px', marginBottom: '0px', marginLeft: '0px' }}
        >
          {t('PQM_YOUR_PERFORMANCE')}
        </Header>
        {/* <MultiLink
          className="multilink-block-wrapper multilink-label "
          label={t(`TQM_SHARE`)}
          icon={<ShareIcon className="mrsm" fill="#f18f5e" />}
          onHeadClick={() => { }}
        /> */}
        <MultiLink
          className="multilink-block-wrapper divToBeHidden"
          label={t(`TQM_SHARE`)}
          icon={<ShareIcon className="mrsm" fill="#f18f5e" />}
          setShowOptions={setShowOptions}
          onHeadClick={(e) => {
            setShowOptions(!showOptions);
          }}
          displayOptions={showOptions}
          options={shareOptions}
        />
      </div>
      <span
        style={{ padding: '8px' }}
      >{`${dateRange?.startDate} - ${dateRange?.endDate}`}</span>
      <Card className="performance-container">
        <Compliance performance={performance} />
        <VerticalLine />
        <Output performance={performance} />
      </Card>
    </div>
  );
}

export default YourPerformance