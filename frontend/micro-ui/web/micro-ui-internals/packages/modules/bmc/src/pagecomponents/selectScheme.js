import React, { useEffect, useState } from 'react';
import { useTranslation } from "react-i18next";
import { Link, useHistory, useLocation } from "react-router-dom";
import Timeline from "../components/bmcTimeline";
import RadioButton from "../components/radiobutton";
import Title from "../components/title";

const SelectSchemePage = () => {
  const { t } = useTranslation();
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const headerLocale = Digit.Utils.locale.getTransformedLocale(tenantId);
  const [radioValueCheck, setRadioValueCheck] = useState();
  const [selectedRadio, setSelectedRadio] = useState("");
  const [selectedScheme, setSelectedScheme] = useState(null);
  const history = useHistory();
  const location = useLocation();
  const [schemeHeads, setSchemeHeads] = useState([]);
  const [schemeDetails, setSchemeDetails] = useState([]);

  const getSchems = { SchemeSearchCriteria: { 'Status': 1 } };
  // const processSchemeData = (data, headerLocale) => {
  //   const schemeHeadsData = [];
  //   const schemeDetailsData = [];

  //   data?.SchemeDetails?.forEach((event) => {
  //     event.schemeshead.forEach((schemeHead) => {
  //       schemeHeadsData.push({
  //         eventName: event.eventName,
  //         startDate: event.startDate,
  //         endDate: event.endDate,
  //         schemeHead: schemeHead.schemeHead,
  //         schemeHeadDesc: schemeHead.schemeheadDesc,
  //         i18nKey: `${headerLocale}_ADMIN_${schemeHead.schemeHead}`
  //       });

  //       schemeHead.schemeDetails.forEach((scheme) => {
  //         schemeDetailsData.push({
  //           schemeID: scheme.schemeID,
  //           schemeName: scheme.schemeName,
  //           schemeDesc: scheme.schemeDesc,
  //           schemeHead: schemeHead.schemeHead,
  //           eventName: event.eventName,
  //           criteria: scheme.criteria.map((criterion) => ({
  //             criteriaType: criterion.criteriaType,
  //             criteriaCondition: criterion.criteriaCondition,
  //             criteriaValue: criterion.criteriaValue
  //           })),
  //           courses: scheme.courses,
  //           machines: scheme.machines,
  //           i18nKey: `${headerLocale}_ADMIN_${scheme.schemeName}`
  //         });
  //       });
  //     });
  //   });

  //   setSchemeHeads(schemeHeadsData);
  //   setSchemeDetails(schemeDetailsData);
  //   return {
  //     schemeHeadsData, schemeDetailsData
  //   };
  // };

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
          i18nKey: `${headerLocale}_ADMIN_${schemeHead.schemeHead}`
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
              i18nKey: `${headerLocale}_ADMIN_${criterion.criteriaType}_${criterion.criteriaCondition}_${criterion.criteriaValue}`
            })),
            courses: scheme.courses.map((course) => ({
              ...course,
              i18nKey: `${headerLocale}_ADMIN_${course.courseName}`
            })),
            machines: scheme.machines.map((machine) => ({
              ...machine,
              i18nKey: `${headerLocale}_ADMIN_${machine.machName}`
            })),
            i18nKey: `${headerLocale}_ADMIN_${scheme.schemeName}`
          });
        });
      });
    });

    setSchemeHeads(schemeHeadsData);
    setSchemeDetails(schemeDetailsData);
    return {
      schemeHeadsData, schemeDetailsData
    };
  };
  
  const qualificationFunction = (data) => {
    const SchemeData = processSchemeData(data, headerLocale);
    return { SchemeData };
  };

  Digit.Hooks.bmc.useSchemesGet(getSchems, { select: qualificationFunction });



  useEffect(() => {
    const fetchSchemeData = async () => {
      const response = await fetch('/path/to/your/scheme/api');
      const data = await response.json();
      processSchemeData(data, 'headerLocale');
    };

    fetchSchemeData();
  }, []);

  const handleSchemeSelect = (scheme) => {
    setSelectedScheme(scheme);
    setSelectedRadio(scheme.schemeID);
    setRadioValueCheck(scheme.schemeID);
  };

  const renderSchemeSections = () => {
    const groupedSchemes = schemeHeads.reduce((acc, schemeHead) => {
      acc[schemeHead.schemeHead] = schemeDetails.filter(detail => detail.schemeHead === schemeHead.schemeHead);
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
              options={[{ label: scheme.i18nKey, value: scheme.schemeID }]}
              onSelect={() => handleSchemeSelect(scheme)}
              selectedOption={radioValueCheck}
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
          <p style={{ paddingLeft: "4rem" }}>
            {selectedScheme.schemeDesc}
          </p>
          <p style={{ paddingLeft: "4rem" }}>
          {t("Following are the critierias to avail this scheme")}  
          <ul style={{ paddingLeft: "4rem" }}>
            {selectedScheme.criteria.map((criterion, index) => (
              <li key={index}>{`${criterion.criteriaType} ${criterion.criteriaCondition} ${criterion.criteriaValue}`}</li>
            ))}
          </ul>
          </p>
        </div>

        {selectedScheme.courses.length > 0 && (
          <div className="bmc-row-card-header">
            {selectedScheme.courses.map((course, index) => (
              <div key={index} style={{ backgroundColor: "#F7F5F5", padding: "1rem", borderRadius: "10px" , margin: "5px"}}>
                <RadioButton
                  t={t}
                  optionsKey="value"
                  options={[{ label: course.i18nKey, value: course.i18nKey }]}
                  selectedOption={radioValueCheck}
                  onSelect={setRadioValueCheck}
                  style={{ marginTop: "0", marginBottom: "0" }}
                  value={selectedRadio}
                  isMandatory={true}
                />
                <p style={{ paddingLeft: "4rem" }}>
                  <label htmlFor={`course-${course.courseID}`}>
                    <strong>{course.i18nKey}</strong>: {course.courseDesc} (Duration: {course.courseDuration}, Amount: INR.{course.courseAmount})
                  </label>
                </p>
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
                  >
                    View More
                  </button>
                </div>
              </div>
            ))}
          </div>
        )}

        {selectedScheme.machines.length > 0 && (
          <div className="bmc-row-card-header">
            {selectedScheme.machines.map((machine, index) =>  (
              <div key={index} style={{ backgroundColor: "#F7F5F5", padding: "1rem", borderRadius: "10px", margin: "5px" }}>
                <RadioButton
                  t={t}
                  optionsKey="value"
                  options={[{ label: machine.machName, value: machine.i18nKey }]}
                  selectedOption={radioValueCheck}
                  onSelect={setRadioValueCheck}
                  style={{ marginTop: "0", marginBottom: "0" }}
                  value={selectedRadio}
                  isMandatory={true}
                />
                <p style={{ paddingLeft: "4rem" }}>
                <label htmlFor={`machine-${machine.machID}`}>
                    <strong>{machine.i18nKey}</strong>: {machine.machDesc} (Amount: INR.{machine.machAmount})
                  </label>
                </p>
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
                  >
                    View More
                  </button>
                </div>
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
        {window.location.href.includes("/citizen") && <Timeline currentStep={2} />}
        <Title text={"Select Scheme"} />
        <div className="bmc-card-grid">
          {renderSchemeSections()}
        </div>
          {renderSelectedSchemeDetails()}
      </div>
      <div style={{ textAlign: "end", padding: "1rem" }}>
        <Link
          to={{
            pathname: "/digit-ui/citizen/bmc/ApplicationDetails",
            state: { selectedScheme: selectedScheme, selectedRadio: selectedRadio },
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
