import React, { Fragment } from 'react';
import {
  BackButton,
  Card,
  AddNewIcon,
  InboxIcon,
  ViewReportIcon,
  CardText,
  CardHeader,
  ULBHomeCard,
  ShippingTruck,
  DashboardIcon,
  IssueIcon,
  TreatmentQualityIcon,
  VehicleLogIcon,
} from '@egovernments/digit-ui-react-components';
import { useTranslation } from 'react-i18next';
import { useHistory } from 'react-router-dom';
import TQMPendingTask from './TQMPendingTask';

const TQMLanding = () => {
  const { t } = useTranslation();
  const state = Digit.ULBService.getStateId();
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const stateId = Digit.ULBService.getStateId();
  const history = useHistory();
  const module = [
    {
      name: 'ES_TQM_TREATMENT_QUALITY',
      link: `/${window?.contextPath}/employee/tqm/home`,
      icon: <TreatmentQualityIcon />,
    },
  ];

  return (
    <React.Fragment>
      <ULBHomeCard module={module}> </ULBHomeCard>
      <TQMPendingTask />
    </React.Fragment>
  );
};

export default TQMLanding;
