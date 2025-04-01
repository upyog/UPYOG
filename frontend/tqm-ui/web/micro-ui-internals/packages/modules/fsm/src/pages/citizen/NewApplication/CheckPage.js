import React from 'react';
import {
  Card,
  CardHeader,
  StatusTable,
  Row,
  LinkButton,
  SubmitBar,
  CitizenInfoLabel,
  EditIcon,
  Header,
  ActionBar,
} from '@egovernments/digit-ui-react-components';
import { useHistory } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import Timeline from '../../../components/TLTimelineInFSM';
import { ViewImages } from '../../../components/ViewImages';

const ActionButton = ({ jumpTo }) => {
  const { t } = useTranslation();
  const history = useHistory();

  function routeTo() {
    history.push(jumpTo);
  }

  return (
    <LinkButton
      label={t('CS_COMMON_CHANGE')}
      className='check-page-link-button'
      onClick={routeTo}
    />
  );
};

const routeLink = `/sanitation-ui/citizen/fsm/new-application`;

const CheckPage = ({ onSubmit, value }) => {
  const { t } = useTranslation();
  const history = useHistory();
  const userInfo = Digit.UserService.getUser().info;
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const {
    address,
    propertyType,
    subtype,
    pitType,
    pitDetail,
    selectGender,
    selectPaymentPreference,
    selectTripNo,
  } = value;
  const pitDetailValues = pitDetail
    ? Object.values(pitDetail).filter((value) => !!value)
    : null;
  const pitMeasurement = pitDetailValues?.reduce(
    (previous, current, index, array) => {
      if (index === array.length - 1) {
        return previous + current + 'm';
      } else {
        return previous + current + 'm x ';
      }
    },
    ''
  );

  function routeTo(jumpTo) {
    location.href = jumpTo;
  }

  return (
    <React.Fragment>
      <Timeline currentStep={4} flow='APPLY' />

      {/* <Header styles={{ fontSize: "32px" }}>{t("WS_COMMON_SUMMARY")}</Header> */}
      <Header styles={{ fontSize: '32px' }}>
        {t('CS_CHECK_CHECK_YOUR_ANSWERS')}
      </Header>

      {/* Property Details */}
      <Card style={{ paddingRight: '16px' }}>
        <div style={{ position: 'relative' }}>
          <CardHeader styles={{ fontSize: '28px' }}>
            {t(`CS_CHECK_PROPERTY_DETAILS`)}
          </CardHeader>
          <LinkButton
            label={
              <EditIcon
                style={{
                  marginTop: '-10px',
                  float: 'right',
                  position: 'relative',
                  bottom: '32px',
                  marginRight: '-10px',
                }}
              />
            }
            style={{ width: '100px', display: 'inline' }}
            onClick={() => routeTo(`${routeLink}/property-type`)}
          />
        </div>
        <StatusTable>
          <Row
            className='border-none'
            label={t('CS_CHECK_PROPERTY_TYPE')}
            text={t(propertyType.i18nKey)}
          />
          <Row
            className='border-none'
            label={t('CS_CHECK_PROPERTY_SUB_TYPE')}
            text={t(subtype.i18nKey)}
          />
          <Row
            className='border-none'
            label={t('CS_CHECK_ADDRESS')}
            text={`${
              address?.doorNo?.trim() ? `${address?.doorNo?.trim()}, ` : ''
            } ${
              address?.street?.trim() ? `${address?.street?.trim()}, ` : ''
            } ${t(address?.locality?.i18nkey)}, ${t(address?.city.code)}`}
          />
          {address?.landmark && (
            <Row
              className='border-none'
              label={t('CS_CHECK_LANDMARK')}
              text={address?.landmark}
            />
          )}
          {address?.pincode && (
            <Row
              className='border-none'
              label={t('CS_APPLICATION_DETAILS_PINCODE')}
              text={address?.pincode}
            />
          )}
          {address?.slumArea?.code === true && (
            <Row
              className='border-none'
              label={t('CS_APPLICATION_DETAILS_SLUM_NAME')}
              text={t(address?.slumData?.i18nKey)}
            />
          )}
          {pitType && (
            <Row
              className='border-none'
              label={t('CS_CHECK_PIT_TYPE')}
              text={t(pitType.i18nKey)}
            />
          )}
          {pitDetail?.images && (
            <Row
              className='border-none check-page-uploaded-images'
              label={t('CS_CHECK_SIZE')}
              text={
                <ViewImages
                  fileStoreIds={pitDetail.images}
                  tenantId={tenantId}
                  onClick={(source, index) => window.open(source, '_blank')}
                />
              }
            />
          )}
        </StatusTable>
      </Card>

      {/* Gender Details */}
      <Card style={{ paddingRight: '16px' }}>
        <div style={{ position: 'relative' }}>
          <CardHeader styles={{ fontSize: '28px' }}>
            {t(`CS_FSM_PERSONAL_DETAILS`)}
          </CardHeader>
          <LinkButton
            label={
              <EditIcon
                style={{
                  marginTop: '-10px',
                  float: 'right',
                  position: 'relative',
                  bottom: '32px',
                  marginRight: '-10px',
                }}
              />
            }
            style={{ width: '100px', display: 'inline' }}
            onClick={() => routeTo(`${routeLink}/select-gender`)}
          />
        </div>
        <StatusTable>
          <Row
            className='border-none'
            label={t('CS_NAME')}
            text={t(userInfo.name)}
          />
          <Row
            className='border-none'
            label={t('CS_APPLICATION_DETAILS_APPLICANT_MOBILE')}
            text={t(userInfo.mobileNumber)}
          />

          {selectGender && (
            <Row
              className='border-none'
              label={t('CS_FSM_GENDER')}
              text={t(selectGender?.i18nKey)}
            />
          )}
        </StatusTable>
      </Card>

      {/* Service Delivery Details */}
      <Card style={{ paddingRight: '16px' }}>
        <div style={{ position: 'relative' }}>
          <CardHeader styles={{ fontSize: '28px' }}>
            {t(`FSM_SERVICE_DELIVERY_DETAILS`)}
          </CardHeader>
          <LinkButton
            label={
              <EditIcon
                style={{
                  marginTop: '-10px',
                  float: 'right',
                  position: 'relative',
                  bottom: '32px',
                  marginRight: '-10px',
                }}
              />
            }
            style={{ width: '100px', display: 'inline' }}
            onClick={() => routeTo(`${routeLink}/select-trip-number`)}
          />
        </div>
        <StatusTable>
          {selectTripNo && selectTripNo?.tripNo && (
            <Row
              className='border-none'
              label={t('ES_FSM_ACTION_NUMBER_OF_TRIPS')}
              text={t(selectTripNo?.tripNo?.i18nKey)}
            />
          )}
          {selectTripNo && selectTripNo?.vehicleCapacity && (
            <Row
              className='border-none'
              label={t('ES_VEHICLE CAPACITY')}
              text={t(selectTripNo?.vehicleCapacity?.capacity)}
            />
          )}
          {selectPaymentPreference && selectPaymentPreference?.totalAmount && (
            <Row
              className='border-none'
              label={t('PAY_TOTAL_AMOUNT')}
              text={'₹ ' + t(selectPaymentPreference?.totalAmount)}
            />
          )}
          {selectPaymentPreference &&
            selectPaymentPreference?.MinAmount >= 0 && (
              <Row
                className='border-none'
                label={t('FSM_ADV_MIN_PAY')}
                text={'₹ ' + t(selectPaymentPreference?.MinAmount)}
              />
            )}
          {selectPaymentPreference &&
            selectPaymentPreference?.advanceAmount !== null && (
              <Row
                className='border-none'
                label={t('ADV_AMOUNT')}
                text={'₹ ' + t(selectPaymentPreference?.advanceAmount)}
              />
            )}
        </StatusTable>
      </Card>
      <ActionBar style={{ zIndex: '19' }}>
        <SubmitBar label={t('CS_COMMON_SUBMIT')} onSubmit={onSubmit} />
      </ActionBar>

      {propertyType && (
        <CitizenInfoLabel
          style={{ marginTop: '8px', padding: '16px' }}
          info={t('CS_FILE_APPLICATION_INFO_LABEL')}
          text={t('CS_FILE_APPLICATION_INFO_TEXT', {
            content: t('CS_DEFAULT_INFO_TEXT'),
            ...propertyType,
          })}
        />
      )}
    </React.Fragment>
  );
};

export default CheckPage;
