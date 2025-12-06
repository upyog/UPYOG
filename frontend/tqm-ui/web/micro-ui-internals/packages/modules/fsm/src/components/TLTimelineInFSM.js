import React from 'react';
import { useTranslation } from 'react-i18next';
import { TickMark } from '@egovernments/digit-ui-react-components';
import { useHistory } from 'react-router-dom';

let actions = [];

const getAction = (flow) => {
  switch (flow) {
    case 'STAKEHOLDER':
      actions = [];
      break;
    case 'APPLY':
      actions = [
        'FSM_TIMELINE_PROPERTY_DETAILS',
        'CS_FSM_PERSONAL_DETAILS',
        'FSM_SERVICE_DELIVERY_DETAILS',
        'FSM_TIMELINE_SUMMARY',
      ];
      break;
    case 'PT_APPLY':
      actions = [
        'ES_NEW_APPLICATION_PROPERTY_DETAILS',
        'PT_OWNERSHIP_INFO_SUB_HEADER',
        'CE_DOCUMENT_DETAILS',
        'PT_COMMON_SUMMARY',
      ];
      break;
    default:
      actions = [
        'TL_COMMON_TR_DETAILS',
        'TL_LOCATION_AND_OWNER_DETAILS',
        'TL_DOCUMENT_DETAIL',
        'TL_COMMON_SUMMARY',
      ];
  }
};
const Timeline = ({ currentStep = 1, flow = '' }) => {
  const { t } = useTranslation();
  const isMobile = window.Digit.Utils.browser.isMobile();
  const history = useHistory();

  const handleClick = (index) => {
    const getSessionApplyData = window.sessionStorage.getItem(
      'Digit.FSM_CITIZEN_FILE_PROPERTY'
    );

    if (getSessionApplyData) {
      const value = JSON.parse(getSessionApplyData).value;
      const propertyToUrlMap = {
        propertyType: `/${window?.contextPath}/citizen/fsm/new-application/property-type`,
        selectGender: `/${window?.contextPath}/citizen/fsm/new-application/select-gender`,
        selectPaymentPreference: `/${window?.contextPath}/citizen/fsm/new-application/select-trip-number`,
      };
      const properties = Object.keys(propertyToUrlMap);
      const propertyName = properties[index];

      if (propertyName && propertyName in value) {
        // Redirect to the corresponding URL if the propertyName exists in the value object
        history.push(propertyToUrlMap[propertyName]);
      }
    }
  };

  getAction(flow);
  return (
    <div
      className='timeline-container'
      style={isMobile ? {} : { maxWidth: '960px', marginRight: 'auto' }}
    >
      {actions.map((action, index, arr) => (
        <div
          className='timeline-checkpoint'
          key={index}
          onClick={() => handleClick(index)}
        >
          <div className='timeline-content'>
            <span className={`circle ${index <= currentStep - 1 && 'active'}`}>
              {index < currentStep - 1 ? <TickMark /> : index + 1}
            </span>
            <span className='secondary-color'>{t(action)}</span>
          </div>
          {index < arr.length - 1 && (
            <span
              className={`line ${index < currentStep - 1 && 'active'}`}
            ></span>
          )}
        </div>
      ))}
    </div>
  );
};

export default Timeline;
