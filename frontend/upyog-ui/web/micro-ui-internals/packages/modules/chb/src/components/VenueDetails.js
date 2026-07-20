import React from 'react';
import { Modal, CardLabel, CardLabelDesc, CardSubHeader } from "@nudmcdgnpm/digit-ui-react-components";
import "../css/chb-inline.css"
import styles from '../utils/styles';

const CloseIcon = () => (
  <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="#FFFFFF">
    <path d="M0 0h24v24H0V0z" fill="none" />
    <path d="M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12 19 6.41z" />
  </svg>
);

const CloseBtn = ({ onClick }) => (
  <div className="icon-bg-secondary" onClick={onClick}>
    <CloseIcon />
  </div>
);


const MODAL_HIDDEN_ACTIONS = {
  actionCancelLabel: null,
  actionCancelOnSubmit: null,
  actionSaveLabel: null,
  actionSaveOnSubmit: null,
  actionSingleLabel: null,
  actionSingleSubmit: null,
};

function parseTermsToList(text = '') {
  return text
    .split('\n')
    .filter((line) => line.trim() !== '')
    .map((line, index) => (
      <li key={index} className="venue-tc-item">
        {line.trim()}
      </li>
    ));
}



const VenueDetails = ({ venueData, setShowDetails, t }) => {
  if (!venueData) return null;

  const VENUE_FIELDS = [
    { label: t("CHB_VENUE_NAME"),         key: 'value'},
    { label: t("CHB_GEO_LOCATION"),       key: 'geoLocation'},
    { label: t("CHB_ADDRESS"),            key: 'address'},
    { label: t("CHB_CONTACT"),            key: 'contactDetails'},
    { label: t("CHB_DESCRIPTION"),        key: 'venueDescription'},
  ];


  return (
    /* can't use className here, Modal prop expects object */
    <Modal
      headerBarMain={
        <CardSubHeader style={styles.headerTitle}>{t("CHB_VENUE_DETAILS")}</CardSubHeader>
      }
      headerBarEnd={<CloseBtn onClick={() => setShowDetails(false)} />}
      popupStyles={styles.popup}
      headerBarMainStyle={styles.headerBar}
      popupModuleActionBarStyles={{ display: 'none' }}
      popupModuleMianStyles={{ padding: '10px' }}
      isOpen
      onClose={() => setShowDetails(false)}
      hideSubmit
      {...MODAL_HIDDEN_ACTIONS}
      error={null}
      setError={() => {}}
      formId="venueDetailsModal"
      isDisabled={false}
      isOBPSFlow={false}
      style={{}}
    >
      <div className="venue-modal-body">
        <div className="venue-fields-grid">
          {VENUE_FIELDS.map(({ label, key }) => (
            <div key={key} className="venue-field-item">
              <CardLabel className="venue-field-label">{label}</CardLabel>
              <CardLabelDesc>{venueData[key]}</CardLabelDesc>
            </div>
          ))}
        </div>
        <CardLabel className="venue-tc-label">{t("CHB_TERMS_AND_CONDITIONS")}</CardLabel>
        <CardLabelDesc>
          <ul>
            {parseTermsToList(venueData.termsAndCondition)}
          </ul>
        </CardLabelDesc>
      </div>
    </Modal>
  );
};

export default VenueDetails;