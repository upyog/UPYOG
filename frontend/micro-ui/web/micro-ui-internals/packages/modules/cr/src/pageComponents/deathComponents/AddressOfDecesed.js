import React, { useState } from "react";
import { FormStep, CardLabel, TextInput, Dropdown, DatePicker, NewRadioButton } from "@egovernments/digit-ui-react-components";
import { useTranslation } from "react-i18next";
import AdressOutside from "./AdressOutside";
import AdressInside from "./AdressInside" ;


const AddressOfDecesed = ({ config, onSelect, userType, formData }) => {
  const stateId = Digit.ULBService.getStateId();
  const { t } = useTranslation();
  let validation = {};
  const { data: place = {}, isLoad } = Digit.Hooks.tl.useTradeLicenseMDMS(stateId, "TradeLicense", "PlaceOfActivity");
  const [setPlaceofActivity, setSelectedPlaceofActivity] = useState(formData?.TradeDetails?.setPlaceofActivity);
  const isEdit = window.location.href.includes("/edit-application/") || window.location.href.includes("renew-trade");
  const [TradeName, setTradeName] = useState(null);
  const [CommencementDate, setCommencementDate] = useState();
  let naturetypecmbvalue = null;
  let cmbPlace = [];
  place &&
    place["TradeLicense"] &&
    place["TradeLicense"].PlaceOfActivity.map((ob) => {
      cmbPlace.push(ob);
    });

  const onSkip = () => onSelect();

  function selectPlaceofactivity(value) {
    naturetypecmbvalue = value.code.substring(0, 4);
    setSelectedPlaceofActivity(value);
  }

  function setSelectTradeName(e) {
    setTradeName(e.target.value);
  }
  function selectCommencementDate(value) {
    setCommencementDate(value);
  }

  const goNext = () => {
    // sessionStorage.setItem("PlaceOfActivity", setPlaceofActivity.code);
    // onSelect(config.key, { setPlaceofActivity });
  };

  const [inside, setInside] = useState(true);
  const [outside, setOutside] = useState(false);
  const insideHandler = () => {
    setInside(true);
    setOutside(false);
  };
  const outsideHandler = () => {
    setInside(false);
    setOutside(true);
  };
  return (
    <React.Fragment>
      {window.location.href.includes("/citizen") ? <Timeline /> : null}
      <FormStep t={t} config={config} onSelect={goNext} onSkip={onSkip} isDisabled={!CommencementDate}>
        <header className="tittle">AdressOfDeceased</header>
        <div className="maindeath">
          <div className="radios">
            <div className="inside">
            <button onClick={insideHandler}>
              <NewRadioButton />
              </button>
              <p>Inside Local Body</p>
            </div>
            <div className="inside">
              <NewRadioButton />
              <p>Inside Local Body</p>
            </div>
            <div className="inside">
              <NewRadioButton />
              <p>Inside Local Body</p>
            </div>
            <div className="inside">
              <button  onClick={outsideHandler}> 
              <NewRadioButton />
              </button>
              <p>Outside India</p>
            </div>
          </div>
          <div>
            {inside && (
               <AdressInside />
            )}
            {outside && (
                <AdressOutside />
            )}
        </div>
          
          
        </div>
      </FormStep>
    </React.Fragment>
  );
};
export default AddressOfDecesed;
