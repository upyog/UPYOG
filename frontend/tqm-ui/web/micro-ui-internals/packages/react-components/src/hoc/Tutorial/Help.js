import React from 'react';
import { HelpOutlineIcon } from "../../atoms/svgindex"
import Label from '../../atoms/Label';
import { useTranslation } from 'react-i18next';

const Help = ({startTour}) => {
  const { t } = useTranslation()
  return (
    <div className="header-icon-container" onClick={startTour}>
      <Label>{t('Help')}</Label>
      <HelpOutlineIcon />
    </div>
  );
};

export default Help;
