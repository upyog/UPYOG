import {
  BackButton,
  HelpOutlineIcon,
  Label,
  Tutorial,
  useTourState,
  Help,
} from '@egovernments/digit-ui-react-components';
import React, { useEffect, useContext, Fragment } from 'react';
import { useTranslation } from 'react-i18next';
import { useLocation,useHistory } from 'react-router-dom';

import { TourSteps } from '../utils/TourSteps';

const excludeBackBtn = [
  'landing'
]
const excludeHelpBtn = [
  'how-it-works'
]

const TqmHeader = ({location,defaultPath}) => {
  const history = useHistory()
  const { tourState, setTourState } = useTourState();
  const pathVar = location.pathname.replace(defaultPath + "/", "").split("?")?.[0];

  // const { tutorial, updateTutorial } = useContext(TutorialContext);
  const { t } = useTranslation();
  //using location.pathname we can update the stepIndex accordingly when help is clicked from any other screen(other than home screen)
  const { pathname } = useLocation();
  const searchParams = new URLSearchParams(location.search);
  const isPast = searchParams.get("type");

  const startTour = () => {
    history.push(`/${window.contextPath}/employee/tqm/how-it-works`);
  };

  return (
    <>
      <Tutorial tutorial={tourState} updateTutorial={setTourState} />

      <div className="tqm-header">
        {!pathVar?.includes(excludeBackBtn) ? (
          <BackButton isCustomBack={pathname?.includes("summary") && isPast !== "past" ? true : false} getBackPageNumber={-3}>
            {t("CS_COMMON_BACK")}
          </BackButton>
        ) : (
          <div></div>
        )}
        {!pathVar?.includes(excludeHelpBtn) ? <Help startTour={startTour} /> :<div></div>}
      </div>
    </>
  );
};

export default TqmHeader;
