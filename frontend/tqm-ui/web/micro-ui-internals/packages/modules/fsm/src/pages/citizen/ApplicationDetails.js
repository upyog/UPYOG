import React, { useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import {
  Header,
  Card,
  KeyNote,
  LinkButton,
  Loader,
  MultiLink,
  Row,
  StatusTable,
  CardHeader,
  CardSubHeader,
  ActionBar,
  SubmitBar,
} from '@egovernments/digit-ui-react-components';
import { Link, useHistory, useLocation, useParams } from 'react-router-dom';
import getPDFData from '../../getPDFData';
import { getVehicleType } from '../../utils';
import { ApplicationTimeline } from '../../components/ApplicationTimeline';

const ApplicationDetails = () => {
  const { t } = useTranslation();
  const { id } = useParams();
  const history = useHistory();
  const { state: locState } = useLocation();
  const tenantId = locState?.tenantId || Digit.ULBService.getCurrentTenantId();
  const state = Digit.ULBService.getStateId();

  const {
    isLoading,
    isError,
    error,
    data: application,
    error: errorApplication,
  } = Digit.Hooks.fsm.useApplicationDetail(t, tenantId, id, {}, 'CITIZEN');

  const { data: paymentsHistory } = Digit.Hooks.fsm.usePaymentHistory(
    tenantId,
    id
  );

  const {
    isLoading: isWorkflowLoading,
    data: workflowDetails,
  } = Digit.Hooks.useWorkflowDetails({
    tenantId: tenantId,
    id: id,
    moduleCode: 'FSM',
  });

  const { data: storeData } = Digit.Hooks.useStore.getInitData();
  const { tenants } = storeData || {};
  const [showOptions, setShowOptions] = useState(false);

  if (isLoading || !application) {
    return <Loader />;
  }

  if (application?.applicationDetails?.length === 0) {
    history.goBack();
  }

  const handleDownloadPdf = async () => {
    const tenantInfo = tenants.find(
      (tenant) => tenant.code === application?.tenantId
    );
    const data = getPDFData({ ...application?.pdfData }, tenantInfo, t);
    Digit.Utils.pdf.generate(data);
    setShowOptions(false);
  };

  const downloadPaymentReceipt = async () => {
    const receiptFile = {
      filestoreIds: [paymentsHistory.Payments[0]?.fileStoreId],
    };

    if (!receiptFile?.fileStoreIds?.[0]) {
      const newResponse = await Digit.PaymentService.generatePdf(
        state,
        { Payments: [paymentsHistory.Payments[0]] },
        'fsm-receipt'
      );
      const fileStore = await Digit.PaymentService.printReciept(state, {
        fileStoreIds: newResponse.filestoreIds[0],
      });
      window.open(fileStore[newResponse.filestoreIds[0]], '_blank');
      setShowOptions(false);
    } else {
      const fileStore = await Digit.PaymentService.printReciept(state, {
        fileStoreIds: receiptFile.filestoreIds[0],
      });
      window.open(fileStore[receiptFile.filestoreIds[0]], '_blank');
      setShowOptions(false);
    }
  };

  const dowloadOptions = [
    {
      label: t('CS_COMMON_APPLICATION_ACKNOWLEDGEMENT'),
      onClick: handleDownloadPdf,
    },
  ];

  const showNextActions = (nextAction) => {
    switch (nextAction) {
      case 'PAY':
        return (
          <Link
            to={{
              pathname: `/${window?.contextPath}/citizen/payment/my-bills/FSM.TRIP_CHARGES/${id}/?tenantId=${application?.pdfData?.tenantId}`,
              state: { tenantId: application?.pdfData?.tenantId },
            }}
          >
            <SubmitBar label={t('CS_APPLICATION_DETAILS_MAKE_PAYMENT')} />
          </Link>
        );
      case 'RATE':
        return (
          <Link to={`/${window?.contextPath}/citizen/fsm/rate/${id}`}>
            <SubmitBar
              label={t('CS_FSM_RATE')}
              style={{
                border: '1px solid #F47738',
                backgroundColor: 'white',
                boxShadow: 'unset',
              }}
              headerStyle={{ color: '#F47738' }}
            />
          </Link>
        );
    }
  };

  return (
    <React.Fragment>
      <MultiLink
        className='multilinkWrapper'
        onHeadClick={handleDownloadPdf}
        label={t('CS_COMMON_APPLICATION_ACKNOWLEDGEMENT')}
        style={{ marginTop: '10px' }}
        // displayOptions={showOptions}
        // options={dowloadOptions}
      />
      <div className='cardHeaderWithOptions'>
        <Header>
          {t('CS_FSM_APPLICATION_DETAIL_TITLE_APPLICATION_DETAILS')}
        </Header>
      </div>
      {application?.applicationDetails?.map(({ title, values }, index) => {
        return (
          <Card
            style={{
              position: 'relative',
              marginBottom: '16px',
              padding: '16px',
            }}
          >
            {index !== 0 && <CardSubHeader>{t(title)}</CardSubHeader>}
            <StatusTable>
              {values?.map(({ title, value }, index) => {
                return (
                  <Row
                    key={t(value)}
                    label={t(title)}
                    text={t(value) || 'N/A'}
                    // last={index === detail?.values?.length - 1}
                    // caption={value}
                    className='border-none'
                    rowContainerStyle={{
                      marginBottom: 0,
                      display: 'flex',
                      justifyContent: 'space-between',
                    }}
                  />
                );
              })}
            </StatusTable>
          </Card>
        );
      })}
      {!isWorkflowLoading && (
        <Card style={{ position: 'relative', marginBottom: '80px' }}>
          <ApplicationTimeline
            application={application?.pdfData}
            id={id}
            isLoading={isWorkflowLoading}
            data={workflowDetails}
          />
        </Card>
      )}
      {workflowDetails?.nextActions?.length > 0 ||
      paymentsHistory?.Payments?.length > 0 ? (
        <ActionBar style={{ zIndex: '19' }}>
          {paymentsHistory?.Payments?.length > 0 && (
            <SubmitBar
              label={t('CS_DOWNLOAD_RECEIPT')}
              onSubmit={downloadPaymentReceipt}
              style={{ marginBottom: '12px' }}
            />
          )}
          {workflowDetails?.nextActions?.length > 0 &&
            showNextActions(workflowDetails?.nextActions?.[0].action)}
        </ActionBar>
      ) : null}
    </React.Fragment>
  );
};

export default ApplicationDetails;
