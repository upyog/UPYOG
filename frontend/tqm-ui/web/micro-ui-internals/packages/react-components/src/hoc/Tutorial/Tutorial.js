import React, { useState } from 'react';
import Joyride, { ACTIONS, EVENTS, LIFECYCLE, STATUS } from 'react-joyride';
import { useHistory } from 'react-router-dom';

const theme = {
  // primaryColor: '#ad7bff',
  // arrowColor: '#000',
  // textColor: '#fff',
  primaryColor: '#F47738',
  arrowColor: '#FFFFFF',
  textColor: '#505A5F',
};

const Tutorial = ({ tutorial, updateTutorial, ...props }) => {
  const history = useHistory()
  const { run, stepIndex, steps } = tutorial;

  
  const handleCallback = (event) => {
    
    const {type,action,status,step} = event
    //when we want to end the tutorial and reset the state
    if(type==="tour:end" || action==="close"){
      updateTutorial({
        run: false,
        steps: [],
        tourActive: false,
      });
    }
  } 
  return (
    <Joyride
      callback={handleCallback}
      continuous
      run={run}
      // stepIndex={stepIndex}
      steps={steps}
      styles={{
        options: {
          arrowColor: theme.arrowColor,
          backgroundColor: theme.arrowColor,
          primaryColor: theme.primaryColor,
          textColor: theme.textColor,
        },
      }}
      showProgress={true}
      hideBackButton={false}
      disableOverlay={false}
      spotlightClicks={true}
    />
  );
};

export default Tutorial;
