import React, { useState } from "react";
import { useTranslation } from "react-i18next";
import { Link, useHistory } from "react-router-dom";
import Timeline from "../components/bmcTimeline";
import RadioButton from "../components/radiobutton";
import Title from "../components/title";
import { Modal } from "@egovernments/digit-ui-react-components";

const SelectSchemePage = () => {
  const { t } = useTranslation();
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const headerLocale = Digit.Utils.locale.getTransformedLocale(tenantId);
  const [radioValueCheck, setRadioValueCheck] = useState({});
  const [selectedRadio, setSelectedRadio] = useState("");
  const [selectedScheme, setSelectedScheme] = useState(null);
  const history = useHistory();
  const [schemeHeads, setSchemeHeads] = useState([]);
  const [schemeDetails, setSchemeDetails] = useState([]);
  const [isModalOpen, setIsModalOpen] = useState(false);

  const getSchemes = { SchemeSearchCriteria: { Status: 1 } };

  const processSchemeData = (data, headerLocale) => {
    const schemeHeadsData = [];
    const schemeDetailsData = [];

    data?.SchemeDetails?.forEach((event) => {
      event.schemeshead.forEach((schemeHead) => {
        schemeHeadsData.push({
          eventName: event.eventName,
          startDate: event.startDate,
          endDate: event.endDate,
          schemeHead: schemeHead.schemeHead,
          schemeHeadDesc: schemeHead.schemeheadDesc,
          i18nKey: `${headerLocale}_ADMIN_${schemeHead.schemeHead}`,
        });

        schemeHead.schemeDetails.forEach((scheme) => {
          schemeDetailsData.push({
            schemeID: scheme.schemeID,
            schemeName: scheme.schemeName,
            schemeDesc: scheme.schemeDesc,
            schemeHead: schemeHead.schemeHead,
            eventName: event.eventName,
            criteria: scheme.criteria.map((criterion) => ({
              criteriaType: criterion.criteriaType,
              criteriaCondition: criterion.criteriaCondition,
              criteriaValue: criterion.criteriaValue,
              i18nKey: `${headerLocale}_ADMIN_${criterion.criteriaType}_${criterion.criteriaCondition}_${criterion.criteriaValue}`,
            })),
            courses: scheme.courses.map((course) => ({
              ...course,
              i18nKey: `${headerLocale}_ADMIN_${course.courseName}`,
            })),
            machines: scheme.machines.map((machine) => ({
              ...machine,
              i18nKey: `${headerLocale}_ADMIN_${machine.machName}`,
            })),
            i18nKey: `${headerLocale}_ADMIN_${scheme.schemeName}`,
          });
        });
      });
    });

    setSchemeHeads(schemeHeadsData);
    setSchemeDetails(schemeDetailsData);
    return {
      schemeHeadsData,
      schemeDetailsData,
    };
  };

  const openModal = () => {
    setIsModalOpen(true);
  };

  const closeModal = () => {
    setIsModalOpen(false);
  };

  const Close = () => (
    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="#FFFFFF">
      <path d="M0 0h24v24H0V0z" fill="none" />
      <path d="M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12 19 6.41z" />
    </svg>
  );

  const CloseBtn = (props) => {
    return (
      <div className="icon-bg-secondary" onClick={props.onClick}>
        <Close />
      </div>
    );
  };

  const qualificationFunction = (data) => {
    const SchemeData = processSchemeData(data, headerLocale);
    return { SchemeData };
  };

  Digit.Hooks.bmc.useSchemesGet(getSchemes, { select: qualificationFunction });

  const handleSchemeSelect = (scheme) => {
    setSelectedScheme(scheme);
    setSelectedRadio(scheme.schemeID);

    const type = scheme.courses.length ? "course" : scheme.machines.length ? "machine" : "pension";
    if (type === "pension") {
      setRadioValueCheck({});
    } else {
      setRadioValueCheck({
        id: scheme.schemeID,
        label: scheme.i18nKey,
      });
    }
  };

  const renderSchemeSections = () => {
    const groupedSchemes = schemeHeads.reduce((acc, schemeHead) => {
      acc[schemeHead.schemeHead] = schemeDetails.filter((detail) => detail.schemeHead === schemeHead.schemeHead);
      return acc;
    }, {});

    return Object.keys(groupedSchemes).map((schemeHead) => (
      <div key={schemeHead} className="bmc-row-card-header">
        <div className="bmc-title" style={{ color: "#F47738" }}>
          {schemeHead}
        </div>
        <div className="bmc-card-row">
          {groupedSchemes[schemeHead].map((scheme) => (
            <RadioButton
              key={scheme.schemeID}
              t={t}
              optionsKey="label"
              options={[{ label: scheme.i18nKey, id: scheme.schemeID }]}
              onSelect={setSelectedRadio}
              onClick={() => handleSchemeSelect(scheme)}
              selectedOption={selectedRadio}
              style={{ paddingLeft: "1rem", margin: "0" }}
              isMandatory={true}
            />
          ))}
        </div>
      </div>
    ));
  };

  const renderSelectedSchemeDetails = () => {
    if (!selectedScheme) return null;
    return (
      <React.Fragment>
        <div className="bmc-row-card-header">
          <div className="bmc-title">{selectedScheme.schemeDesc}</div>
          <div className="bmc-card-row">
            {t("Following are the critierias to avail this scheme")}
            <div className="bmc-criteria">
              {selectedScheme.criteria.map((criterion, index) => (
                <span key={index}>{`${criterion.criteriaType} ${criterion.criteriaCondition} ${criterion.criteriaValue}`}</span>
              ))}
            </div>
          </div>
        </div>

        {selectedScheme.courses.length > 0 && (
          <div className="bmc-row-card-header">
            {selectedScheme.courses.map((course, index) => (
              <div key={index} style={{ backgroundColor: "#F7F5F5", padding: "1rem", borderRadius: "10px", margin: "5px" }}>
                <div className="bmc-card-row">
                  <div className="bmc-col-large-header">
                    <RadioButton
                      t={t}
                      optionsKey="value"
                      options={[
                        { label: course.i18nKey, value: course.i18nKey, id: course.courseID, type: "course", GrantAmount: course.courseAmount },
                      ]}
                      selectedOption={radioValueCheck}
                      onSelect={setRadioValueCheck}
                      style={{ marginTop: "0", marginBottom: "0" }}
                      value={selectedRadio}
                      isMandatory={true}
                    />
                  </div>
                  <div className="bmc-col-small-header">
                    <div className="bmc-course-amount" style={{ textAlign: "end" }}>
                      Amount: ₹ {course.courseAmount}
                    </div>
                  </div>
                </div>

                <div className="bmc-select-scheme">
                  <label htmlFor={`course-${course.courseID}`}>
                    <strong>{course.i18nKey}</strong>: {course.courseDesc} (Duration: {course.courseDuration})
                  </label>
                </div>
                <div style={{ display: "flex", justifyContent: "flex-end" }}>
                  <button
                    style={{
                      backgroundColor: "#F47738",
                      width: "91px",
                      height: "34px",
                      color: "white",
                      marginTop: "1.5rem",
                      borderBottom: "3px solid black",
                      outline: "none",
                    }}
                    onClick={openModal}
                  >
                    View More
                  </button>
                </div>
                {isModalOpen && radioValueCheck.value === course.i18nKey && (
                  <div className="bmc-modal">
                    <Modal
                      onClose={closeModal}
                      fullScreen
                      hideSubmit={true}
                      headerBarEnd={<CloseBtn onClick={closeModal} />}
                      headerBarMain={
                        <h5 className="bmc-title" style={{ textAlign: "center", padding: "1rem" }}>
                          Selected Course: {course.i18nKey}
                        </h5>
                      }
                    >
                      <p style={{ fontSize: "15px" }}>
                        <strong>Course Description:</strong> {course.courseDesc}
                      </p>
                    </Modal>
                  </div>
                )}
              </div>
            ))}
          </div>
        )}

        {selectedScheme.machines.length > 0 && (
          <div className="bmc-row-card-header">
            {selectedScheme.machines.map((machine, index) => (
              <div key={index} style={{ backgroundColor: "#F7F5F5", padding: "1rem", borderRadius: "10px", margin: "5px" }}>
                <div className="bmc-card-row">
                  <div className="bmc-col-large-header">
                    <RadioButton
                      t={t}
                      optionsKey="value"
                      options={[
                        { label: machine.machName, value: machine.i18nKey, id: machine.machID, type: "machine", GrantAmount: machine.machAmount },
                      ]}
                      selectedOption={radioValueCheck}
                      onSelect={setRadioValueCheck}
                      style={{ marginTop: "0", marginBottom: "0" }}
                      value={selectedRadio}
                      isMandatory={true}
                    />
                  </div>
                  <div className="bmc-col-small-header">
                    <div className="bmc-course-amount" style={{ textAlign: "end" }}>
                      Amount: ₹ {machine.machAmount}
                    </div>
                  </div>
                </div>
                <div className="bmc-select-scheme">
                  <label htmlFor={`machine-${machine.machID}`}>
                    <strong>{machine.i18nKey}</strong>: {machine.machDesc}
                  </label>
                </div>
                <div style={{ display: "flex", justifyContent: "flex-end" }}>
                  <button
                    style={{
                      backgroundColor: "#F47738",
                      width: "91px",
                      height: "34px",
                      color: "white",
                      marginTop: "1.5rem",
                      borderBottom: "3px solid black",
                      outline: "none",
                    }}
                    onClick={openModal}
                  >
                    View More
                  </button>
                </div>
                {isModalOpen && radioValueCheck.value === machine.i18nKey && (
                  <div className="bmc-modal">
                    <Modal
                      onClose={closeModal}
                      fullScreen
                      hideSubmit={true}
                      headerBarEnd={<CloseBtn onClick={closeModal} />}
                      headerBarMain={
                        <h5 className="bmc-title" style={{ textAlign: "center", padding: "1rem" }}>
                          Selected Machine : {machine.i18nKey}
                        </h5>
                      }
                    >
                      <p style={{ fontSize: "15px" }}>
                        <strong>Machine Description:</strong> {machine.machDesc}
                      </p>
                      <p style={{ fontSize: "15px" }}>
                        <strong>Machine Amount:</strong> {machine.machAmount}
                      </p>
                    </Modal>
                  </div>
                )}
              </div>
            ))}
          </div>
        )}
      </React.Fragment>
    );
  };
  return (
    <React.Fragment>
      <div className="bmc-card-full">
        {window.location.href.includes("/citizen") && <Timeline currentStep={3} />}
        <Title text={"Select Scheme"} />
        <div className="bmc-card-grid">{renderSchemeSections()}</div>
        {renderSelectedSchemeDetails()}
      </div>
      <div style={{ textAlign: "end", padding: "1rem" }}>
        <Link
          to={{
            pathname: "/digit-ui/citizen/bmc/ApplicationDetails",
            state: {
              scheme: selectedRadio,
              schemeType: radioValueCheck,
              selectedScheme: selectedScheme,
            },
          }}
          style={{ textDecoration: "none" }}
          disabled={!selectedRadio}
        >
          <button
            className="bmc-card-button"
            style={{ backgroundColor: radioValueCheck ? "#F47738" : "grey", borderBottom: "3px solid black", marginRight: "1rem" }}
          >
            {t("BMC_Next")}
          </button>
        </Link>
        <button
          className="bmc-card-button-cancel"
          style={{ borderBottom: "3px solid black", outline: "none", marginRight: "5rem" }}
          onClick={() => history.goBack()}
        >
          {t("BMC_Cancel")}
        </button>
      </div>
    </React.Fragment>
  );
};

export default SelectSchemePage;
