import {
  Card,
  CardHeader,
  CardSubHeader,
  CardText,
  CardLabel,
  CitizenInfoLabel,
  LinkButton,
  Row,
  StatusTable,
  SubmitBar,
} from "@egovernments/digit-ui-react-components";
import React from "react";
import { useTranslation } from "react-i18next";
import { useHistory, useRouteMatch } from "react-router-dom";
import TLDocument from "../../../pageComponents/TLDocumets";
import Timeline from "../../../components/TLTimeline";

const ActionButton = ({ jumpTo }) => {
  const { t } = useTranslation();
  const history = useHistory();
  function routeTo() {
    sessionStorage.setItem("isDirectRenewal", false);
    history.push(jumpTo);
  }
  return (
    <LinkButton
      label={t("CS_COMMON_CHANGE")}
      className="check-page-link-button"
      style={jumpTo.includes("proof-of-identity") ? { textAlign: "right", marginTop: "-32px" } : {}}
      onClick={routeTo}
    />
  );
};

const getPath = (path, params) => {
  params &&
    Object.keys(params).map((key) => {
      path = path.replace(`:${key}`, params[key]);
    });
  return path;
};
const CheckPage = ({ onSubmit, value }) => {
  let isEdit = window.location.href.includes("renew-trade");
  const { t } = useTranslation();
  const history = useHistory();
  const match = useRouteMatch();
  const { TradeDetails, address, owners, propertyType, subtype, pitType, pitDetail, isEditProperty, cpt } = value;
  function getdate(date) {
    let newdate = Date.parse(date);
    return `${new Date(newdate).getDate().toString() + "/" + (new Date(newdate).getMonth() + 1).toString() + "/" + new Date(newdate).getFullYear().toString()
      }`;
  }
  const typeOfApplication = !isEditProperty ? `new-application` : `renew-trade`;
  let routeLink = `/digit-ui/citizen/tl/tradelicence/${typeOfApplication}`;
  if (window.location.href.includes("edit-application") || window.location.href.includes("renew-trade")) {
    routeLink = `${getPath(match.path, match.params)}`;
    routeLink = routeLink.replace("/check", "");
  }
  console.log(value);

  return (
    <React.Fragment>
      {window.location.href.includes("/citizen") ? <Timeline currentStep={4} /> : null}
      <Card>
        {/* <CardHeader>{t("CS_CHECK_CHECK_YOUR_ANSWERS")}</CardHeader> */}
        {/* <CardText>{t("CS_CHECK_CHECK_YOUR_ANSWERS_TEXT")}</CardText> */}
        {isEdit && <CitizenInfoLabel info={t("CS_FILE_APPLICATION_INFO_LABEL")} text={t("TL_RENEWAL_INFO_TEXT")} />}
        {/* <CardSubHeader>{t("TL_TRADE_UNITS_HEADER")}</CardSubHeader> */}
        <label style={{ fontSize: "17px", fontWeight: "bold" }} >{t("TL_TRADE_UNITS_HEADER")}</label>
        <div className="row">
          <div className="col-md-12" ><h1 className="headingh1" ><span style={{ background: "#fff", padding: "0 10px" }}>{`${t("TL_TRADE_UNITS_HEADER")}`}</span></h1>
          </div>
        </div>
        <StatusTable >
          <div className="row">
            <div className="col-md-4" ><CardLabel style={{ lineHeight: "auto" }}>{`${t("TL_NEW_TRADE_DETAILS_TRADE_CAT_LABEL")}`}</CardLabel>
              <CardText style={{ fontSize: "15px", Colour: "black" }}>{t(TradeDetails[0].tradecategory?.i18nKey)}</CardText>
            </div>
            <div className="col-md-4" ><CardLabel style={{ lineHeight: "auto" }}>{`${t("TL_NEW_TRADE_DETAILS_TRADE_TYPE_LABEL")}`}</CardLabel>
              <CardText style={{ fontSize: "15px", Colour: "black" }}>{t(TradeDetails[0].tradesubtype?.i18nKey)}</CardText>
            </div>
            <div className="col-md-4" ><CardLabel style={{ lineHeight: "auto" }}>{`${t("TL_NEW_TRADE_DETAILS_TRADE_SUBTYPE_LABEL")}`}</CardLabel>
              <CardText style={{ fontSize: "15px", Colour: "black" }}>{t(TradeDetails[0].tradetype?.i18nKey)}</CardText>
            </div>
          </div>
          <div className="row">
            <div className="col-md-6" ><CardLabel style={{ lineHeight: "auto" }}>{`${t("TL_CUSTOM_DETAILED_TYPE_LABEL")}`}</CardLabel>
              <CardText style={{ fontSize: "15px", Colour: "black" }}>{t(TradeDetails[0].unit)}</CardText>
            </div>
            <div className="col-md-6" ><CardLabel style={{ lineHeight: "auto" }}>{`${t("TL_BUSINESS_ACTIVITY_LABEL")}`}</CardLabel>
              <CardText style={{ fontSize: "15px", Colour: "black" }}>{t(TradeDetails[0].uom)}&nbsp;&nbsp;{<ActionButton jumpTo={`${routeLink}/units-details`} />}</CardText>
            </div>
          </div>
        </StatusTable>
        <label style={{ fontSize: "17px", fontWeight: "bold" }} >{t("TL_LICENSEE_TYPE")}</label>
        {TradeDetails?.LicenseeType.code === "INDIVIDUAL" && (
          <div>
            <div className="row">
              <div className="col-md-12" ><h1 className="headingh1" ><span style={{ background: "#fff", padding: "0 10px" }}>{`${t("TL_LICENSEE_INDIVIDUAL_HEADER")}`}</span></h1>
              </div>
            </div>
            <StatusTable >
              <div className="row">
                <div className="col-md-4" ><CardLabel>{`${t("Licensing Unit")}`}</CardLabel>
                  <CardText style={{ fontSize: "15px", Colour: "black" }}>{t(TradeDetails?.LicensingUnitType?.name)}</CardText>
                </div>
                <div className="col-md-4" ><CardLabel>{`${t("Unit ID")}`}</CardLabel>
                  <CardText style={{ fontSize: "15px", Colour: "black" }}>{t(TradeDetails?.LicenseUnitID)}</CardText>
                </div>
                <div className="col-md-4" ><CardLabel>{`${t("Unit Name")}`}</CardLabel>
                  <CardText style={{ fontSize: "15px", Colour: "black" }}>{t(TradeDetails?.LicenseUnitName)}</CardText>
                </div>
              </div>
              <div className="row">
                <div className="col-md-4" ><CardLabel>{`${t("TL_LOCALIZATION_ZONAL_OFFICE")}`}</CardLabel>
                  <CardText style={{ fontSize: "15px", Colour: "black" }}>{t(TradeDetails.Zonal.name)}</CardText>
                </div>
                <div className="col-md-4" ><CardLabel>{`${t("TL_LOCALIZATION_WARD_NO")}`}</CardLabel>
                  <CardText style={{ fontSize: "15px", Colour: "black" }}>{t(TradeDetails.WardNo.name)}</CardText>
                </div>
                <div className="col-md-4" ><CardLabel>{`${t("TL_STREET_NAME")}`}</CardLabel>
                  <CardText style={{ fontSize: "15px", Colour: "black" }}>{t(TradeDetails.StreetName)}</CardText>
                </div>
              </div>
              <div className="row">
                <div className="col-md-4" ><CardLabel>{`${t("TL_LOCALIZATION_LAND_MARK")}`}</CardLabel>
                  <CardText style={{ fontSize: "15px", Colour: "black" }}>{t(TradeDetails.LandMark)}</CardText>
                </div>
                <div className="col-md-4" ><CardLabel>{`${t("TL_LOCALIZATION_MOBILE_NO")}`}</CardLabel>
                  <CardText style={{ fontSize: "15px", Colour: "black" }}>{t(TradeDetails.MobileNo)}</CardText>
                </div>
                <div className="col-md-4" ><CardLabel>{`${t("TL_LOCALIZATION_EMAIL_ID")}`}</CardLabel>
                  <CardText style={{ fontSize: "15px", Colour: "black" }}>{t(TradeDetails.EmailID)}&nbsp;&nbsp;{<ActionButton jumpTo={`${routeLink}/units-details`} />}</CardText>
                </div>
              </div>
            </StatusTable>
          </div>)}
        {TradeDetails?.LicenseeType.code === "INSTITUTION" && (
          <div>
            <div className="row">
              <div className="col-md-12" ><h1 className="headingh1" ><span style={{ background: "#fff", padding: "0 10px" }}>{`${t("TL_LICENSING_INSTITUTION_HEADER")}`}</span></h1>
              </div>
            </div>
            <StatusTable>
              <div className="row">
                <div className="col-md-4" ><CardLabel>{`${t("TL_LICENSING_INSTITUTION_TYPE")}`}</CardLabel>
                  <CardText style={{ fontSize: "15px", Colour: "black" }}>{t(TradeDetails?.LicensingInstitutionType?.name)}</CardText>
                </div>
                <div className="col-md-4" ><CardLabel>{`${t("TL_LICENSING_INSTITUTION_ID")}`}</CardLabel>
                  <CardText style={{ fontSize: "15px", Colour: "black" }}>{t(TradeDetails?.LicensingInstitutionID)}</CardText>
                </div>
                <div className="col-md-4" ><CardLabel>{`${t("TL_LICENSING_INSTITUTION_NAME")}`}</CardLabel>
                  <CardText style={{ fontSize: "15px", Colour: "black" }}>{t(TradeDetails?.LicensingInstitutionName)}</CardText>
                </div>
              </div>
              <div className="row">
                <div className="col-md-4" ><CardLabel>{`${t("TL_LICENSING_INSTITUTION_ADDRESS")}`}</CardLabel>
                  <CardText style={{ fontSize: "15px", Colour: "black" }}>{t(TradeDetails.LicensingInstitutionAddress)}</CardText>
                </div>
                <div className="col-md-4" ><CardLabel>{`${t("TL_LOCALIZATION_MOBILE_NO")}`}</CardLabel>
                  <CardText style={{ fontSize: "15px", Colour: "black" }}>{t(TradeDetails.InstitutionMobileNo)}</CardText>
                </div>
                <div className="col-md-4" ><CardLabel>{`${t("TL_LOCALIZATION_EMAIL_ID")}`}</CardLabel>
                  <CardText style={{ fontSize: "15px", Colour: "black" }}>{t(TradeDetails.InstitutionEmailID)}</CardText>
                </div>
              </div>
              <div className="row">
                <div className="col-md-12" ><h1 className="headingh1" ><span style={{ background: "#fff", padding: "0 10px" }}>{`${t("TL_LICENSING_UNIT_HEADER")}`}</span></h1>
                </div>
              </div>
              <div className="row">
                <div className="col-md-4" ><CardLabel>{`${t("Licensing Unit")}`}</CardLabel>
                  <CardText style={{ fontSize: "15px", Colour: "black" }}>{t(TradeDetails?.LicensingUnitType?.name)}</CardText>
                </div>
                <div className="col-md-4" ><CardLabel>{`${t("Unit ID")}`}</CardLabel>
                  <CardText style={{ fontSize: "15px", Colour: "black" }}>{t(TradeDetails?.LicenseUnitID)}</CardText>
                </div>
                <div className="col-md-4" ><CardLabel>{`${t("Unit Name")}`}</CardLabel>
                  <CardText style={{ fontSize: "15px", Colour: "black" }}>{t(TradeDetails?.LicenseUnitName)}</CardText>
                </div>
              </div>
              <div className="row">
                <div className="col-md-4" ><CardLabel>{`${t("TL_LOCALIZATION_ZONAL_OFFICE")}`}</CardLabel>
                  <CardText style={{ fontSize: "15px", Colour: "black" }}>{t(TradeDetails.Zonal.name)}</CardText>
                </div>
                <div className="col-md-4" ><CardLabel>{`${t("TL_LOCALIZATION_WARD_NO")}`}</CardLabel>
                  <CardText style={{ fontSize: "15px", Colour: "black" }}>{t(TradeDetails.WardNo.name)}</CardText>
                </div>
                <div className="col-md-4" ><CardLabel>{`${t("TL_STREET_NAME")}`}</CardLabel>
                  <CardText style={{ fontSize: "15px", Colour: "black" }}>{t(TradeDetails.StreetName)}</CardText>
                </div>
              </div>
              <div className="row">
                <div className="col-md-4" ><CardLabel>{`${t("TL_LOCALIZATION_LAND_MARK")}`}</CardLabel>
                  <CardText style={{ fontSize: "15px", Colour: "black" }}>{t(TradeDetails.LandMark)}</CardText>
                </div>
                <div className="col-md-4" ><CardLabel>{`${t("TL_LOCALIZATION_MOBILE_NO")}`}</CardLabel>
                  <CardText style={{ fontSize: "15px", Colour: "black" }}>{t(TradeDetails.MobileNo)}</CardText>
                </div>
                <div className="col-md-4" ><CardLabel>{`${t("TL_LOCALIZATION_EMAIL_ID")}`}</CardLabel>
                  <CardText style={{ fontSize: "15px", Colour: "black" }}>{t(TradeDetails.EmailID)}&nbsp;&nbsp;{<ActionButton jumpTo={`${routeLink}/units-details`} />}</CardText>
                </div>
              </div>
            </StatusTable>
          </div>)}

        {TradeDetails?.LicenseeType.code === "INDIVIDUAL" && (
          <div>
            <div className="row">
              <div className="col-md-12" ><h1 className="headingh1" ><span style={{ background: "#fff", padding: "0 10px" }}>{`${t("TL_LICENSEE_INDIVIDUAL_HEADER_MSG")}`}</span></h1>
              </div>
            </div>
            <StatusTable >
              <div className="row">
                <div className="col-md-4" ><CardLabel>{`${t("TL_LICENSEE_AADHAR_NO")}`}</CardLabel>
                  <CardText style={{ fontSize: "15px", Colour: "black" }}>{t(address.IndividualAadharNo)}</CardText>
                </div>
                <div className="col-md-4" ><CardLabel>{`${t("TL_LICENSEE_NAME")}`}</CardLabel>
                  <CardText style={{ fontSize: "15px", Colour: "black" }}>{t(address.IndividualName)}</CardText>
                </div>
                <div className="col-md-4" ><CardLabel>{`${t("TL_LICENSEE_ADDRESS")}`}</CardLabel>
                  <CardText style={{ fontSize: "15px", Colour: "black" }}>{t(address.IndividualAddress)}</CardText>
                </div>
              </div>
              <div className="row">
                <div className="col-md-4" ><CardLabel>{`${t("TL_LOCALIZATION_MOBILE_NO")}`}</CardLabel>
                  <CardText style={{ fontSize: "15px", Colour: "black" }}>{t(address.IndividualMobNo)}</CardText>
                </div>
                <div className="col-md-4" ><CardLabel>{`${t("TL_LOCALIZATION_EMAIL_ID")}`}</CardLabel>
                  <CardText style={{ fontSize: "15px", Colour: "black" }}>{t(address.IndividualEmailID)}</CardText>
                </div>
                <div className="col-md-4" ><CardLabel></CardLabel>
                  <CardText style={{ fontSize: "15px", Colour: "black" }}>{<ActionButton jumpTo={`${routeLink}/units-details`} />}</CardText>
                </div>
              </div>
            </StatusTable>
          </div>)}
        {TradeDetails?.LicenseeType.code === "INSTITUTION" && (
          <div>
            <div className="row">
              <div className="col-md-12" ><h1 className="headingh1" ><span style={{ background: "#fff", padding: "0 10px" }}>{`${t("TL_LICENSEE_INDIVIDUAL_HEADER_MSG")}`}</span></h1>
              </div>
            </div>
            <StatusTable>
              <div className="row">
                <div className="col-md-4" ><CardLabel>{`${t("TL_LICENSEE_DESIGNATION")}`}</CardLabel>
                  <CardText style={{ fontSize: "15px", Colour: "black" }}>{t(address.IndividualDesignation)}</CardText>
                </div>
                <div className="col-md-4" ><CardLabel>{`${t("TL_LICENSEE_AADHAR_NO")}`}</CardLabel>
                  <CardText style={{ fontSize: "15px", Colour: "black" }}>{t(address.IndividualAadharNo)}</CardText>
                </div>
                <div className="col-md-4" ><CardLabel>{`${t("TL_LICENSEE_NAME")}`}</CardLabel>
                  <CardText style={{ fontSize: "15px", Colour: "black" }}>{t(address.IndividualName)}</CardText>
                </div>
              </div>
              <div className="row">
                <div className="col-md-4" ><CardLabel>{`${t("TL_LOCALIZATION_MOBILE_NO")}`}</CardLabel>
                  <CardText style={{ fontSize: "15px", Colour: "black" }}>{t(address.IndividualMobNo)}</CardText>
                </div>
                <div className="col-md-4" ><CardLabel>{`${t("TL_LOCALIZATION_EMAIL_ID")}`}</CardLabel>
                  <CardText style={{ fontSize: "15px", Colour: "black" }}>{t(address.IndividualEmailID)}</CardText>
                </div>
                <div className="col-md-4" ><CardLabel>{`${t("TL_LICENSEE_ADDRESS")}`}</CardLabel>
                  <CardText style={{ fontSize: "15px", Colour: "black" }}>{t(address.IndividualAddress)}&nbsp;&nbsp;{<ActionButton jumpTo={`${routeLink}/units-details`} />}</CardText>
                </div>
              </div>
            </StatusTable>
          </div>)}
        <label style={{ fontSize: "17px", fontWeight: "bold" }} >{t("TL_LICENSEE_PLACE_STRUCTURE")}</label>
        {address?.OwnProperty.code === "YES" && (
          <div>
            <StatusTable >
              <div className="row">
                <div className="col-md-12" ><CardLabel>{`${t("TL_PLACE_MSG")}`} : {t(address.OwnProperty.code)}</CardLabel>
                  {/* <CardText style={{ fontSize: "15px", Colour: "black" }}>{t(address.OwnProperty.code)}</CardText> */}
                </div>
              </div>
            </StatusTable>
          </div>)}
        {address?.OwnProperty.code === "NO" && (
          <div>
            <div className="row">
              <div className="col-md-12" ><h1 className="headingh1" ><span style={{ background: "#fff", padding: "0 10px" }}>{`${t("TL_OWNER_DETAILS_HEADER")}`}</span></h1>
              </div>
            </div>
            <StatusTable>
              <div className="row">
                <div className="col-md-12" ><CardLabel>{`${t("TL_PLACE_MSG")}`} : {t(address.OwnProperty.code)}</CardLabel>
                  {/* <CardText style={{ fontSize: "15px", Colour: "black" }}>{t(address.OwnProperty.code)}</CardText> */}
                </div>
              </div>
              <div className="row">
                <div className="col-md-4" ><CardLabel>{`${t("TL_OWNER_AADHAR_NO")}`}</CardLabel>
                  <CardText style={{ fontSize: "15px", Colour: "black" }}>{t(address.OwnerAadharNo)}</CardText>
                </div>
                <div className="col-md-4" ><CardLabel>{`${t("TL_OWNER_NAME")}`}</CardLabel>
                  <CardText style={{ fontSize: "15px", Colour: "black" }}>{t(address.OwnerName)}</CardText>
                </div>
                <div className="col-md-4" ><CardLabel>{`${t("TL_LOCALIZATION_MOBILE_NO")}`}</CardLabel>
                  <CardText style={{ fontSize: "15px", Colour: "black" }}>{t(address.OwnerMobileNo)}</CardText>
                </div>
              </div>
              <div className="row">
                <div className="col-md-4" ><CardLabel>{`${t("OwnerConsentPlace")}`}</CardLabel>
                  <CardText style={{ fontSize: "15px", Colour: "black" }}>{t(address.OwnerConsentPlace)}</CardText>
                </div>
                <div className="col-md-4" ><CardLabel>{`${t("OwnerConsentDateStart")}`}</CardLabel>
                  <CardText style={{ fontSize: "15px", Colour: "black" }}>{t(address.OwnerConsentDateStart)}</CardText>
                </div>
                <div className="col-md-4" ><CardLabel>{`${t("OwnerConsentDateEnd")}`}</CardLabel>
                  <CardText style={{ fontSize: "15px", Colour: "black" }}>{t(address.OwnerConsentDateEnd)}</CardText>
                </div>
              </div>
              <div className="row">
                <div className="col-md-12" ><CardLabel>{`${t("TL_OWNER_ADDRESS")}`}</CardLabel>
                  <CardText style={{ fontSize: "15px", Colour: "black" }}>{t(address.OwnerAddress)}&nbsp;&nbsp;{<ActionButton jumpTo={`${routeLink}/units-details`} />}</CardText>
                </div>
              </div>
            </StatusTable>
          </div>)}

        <label style={{ fontSize: "17px", fontWeight: "bold" }} >{t("TL_STRUCTURE_TYPE_HEADER")}</label>
        <div className="row">
          <div className="col-md-12" ><h1 className="headingh1" ><span style={{ background: "#fff", padding: "0 10px" }}>{`${t("TL_PLACE_HEADER_MSG")}`}</span></h1>
          </div>
        </div>
        <StatusTable >
          <div className="row">
            <div className="col-md-6" ><CardLabel>{`${t("TL_LOCALIZATION_PLACE_ACTVITY")}`}</CardLabel>
              <CardText style={{ fontSize: "15px", Colour: "black" }}>{t(TradeDetails.setPlaceofActivity.name)}</CardText>
            </div>
            <div className="col-md-6" ><CardLabel>{`${t("TL_LOCALIZATION_NATURE_STRUCTURE")}`}</CardLabel>
              <CardText style={{ fontSize: "15px", Colour: "black" }}>{t(TradeDetails.StructureType.name)}</CardText>
            </div>
          </div>
        </StatusTable>
        {TradeDetails?.setPlaceofActivity.code === "LAND" && (
          <div >
            <StatusTable >
              <div className="row">
                <div className="col-md-12" ><CardLabel>{`${t("TL_RESURVEY_LAND")}`} : {t(TradeDetails.ResurveyedLand.code)}</CardLabel>
                </div>
              </div>
            </StatusTable>
            {TradeDetails?.ResurveyedLand.code === "YES" && (
              <div>
                <div className="row">
                  <div className="col-md-12" ><h1 className="headingh1" ><span style={{ background: "#fff", padding: "0 10px" }}>{`${t("TL_RESURVEY_LAN_DETAILS")}`}</span></h1>
                  </div>
                </div>
                <StatusTable>
                  <div className="row">
                    <div className="col-md-6" ><CardLabel>{`${t("TL_LOCALIZATION_BLOCK_NO")}`} : {t(TradeDetails.BlockNo)}</CardLabel>
                      {/* <CardText style={{ fontSize: "15px", Colour: "black" }}></CardText> */}
                    </div>
                    <div className="col-md-6" ><CardLabel>{`${t("TL_LOCALIZATION_SURVEY_NO")}`} : {t(TradeDetails.SurveyNo)}</CardLabel>
                      {/* <CardText style={{ fontSize: "15px", Colour: "black" }}></CardText> */}
                    </div>
                  </div>
                  <div className="row">
                    <div className="col-md-6" ><CardLabel>{`${t("TL_LOCALIZATION_SUBDIVISION_NO")}`} : {t(TradeDetails.SubDivNo)}</CardLabel>
                      {/* <CardText style={{ fontSize: "15px", Colour: "black" }}></CardText> */}
                    </div>
                    <div className="col-md-6" ><CardLabel>{`${t("TL_LOCALIZATION_PARTITION_NO")}`} : {t(TradeDetails.SubDivNo)}</CardLabel>
                      <CardText>{<ActionButton jumpTo={`${routeLink}/units-details`} />}</CardText>
                    </div>
                  </div>
                </StatusTable>
              </div>
            )}
            {TradeDetails?.ResurveyedLand.code === "NO" && (
              <div>
                <div className="row">
                  <div className="col-md-12" ><h1 className="headingh1" ><span style={{ background: "#fff", padding: "0 10px" }}>{`${t("TL_RESURVEY_LAN_DETAILS")}`}</span></h1>
                  </div>
                </div>
                <StatusTable>
                  <div className="row">
                    <div className="col-md-6" ><CardLabel>{`${t("TL_LOCALIZATION_BLOCK_NO")}`} : {t(TradeDetails.BlockNo)}</CardLabel>
                      {/* <CardText style={{ fontSize: "15px", Colour: "black" }}></CardText> */}
                    </div>
                    <div className="col-md-6" ><CardLabel>{`${t("TL_LOCALIZATION_SURVEY_NO")}`} : {t(TradeDetails.SurveyNo)}</CardLabel>
                      {/* <CardText style={{ fontSize: "15px", Colour: "black" }}></CardText> */}
                    </div>
                  </div>
                  <div className="row">
                    <div className="col-md-6" ><CardLabel>{`${t("TL_LOCALIZATION_SUBDIVISION_NO")}`} : {t(TradeDetails.SubDivNo)}</CardLabel>
                      {/* <CardText style={{ fontSize: "15px", Colour: "black" }}></CardText> */}
                    </div>
                    <div className="col-md-6" ><CardLabel></CardLabel>
                    <CardText>{<ActionButton jumpTo={`${routeLink}/units-details`} />}</CardText>
                    </div>
                  </div>
                </StatusTable>
              </div>
            )}

          </div>)}
        {TradeDetails?.setPlaceofActivity.code === "BUILDING" && (
          <div>
            <div className="row">
              <div className="col-md-12" ><h1 className="headingh1" ><span style={{ background: "#fff", padding: "0 10px" }}>{`${t("TL_BUILDING_HEADER")}`}</span></h1>
              </div>
            </div>
            <StatusTable>              
              {/* <div className="row">
                <div className="col-md-6" ><CardLabel>{`${t("TL_LOCALIZATION_ZONAL_OFFICE")}`}</CardLabel>
                  <CardText style={{ fontSize: "15px", Colour: "black" }}>{t(TradeDetails.ZonalBuilding)}</CardText>
                </div>
                <div className="col-md-6" ><CardLabel>{`${t("TL_LOCALIZATION_WARD_NO")}`}</CardLabel>
                  <CardText style={{ fontSize: "15px", Colour: "black" }}>{t(TradeDetails.WardNoBuilding)}</CardText>
                </div>
              </div> */}
              <div className="row">                
                <div className="col-md-6" ><CardLabel>{`${t("TL_LOCALIZATION_DOOR_NO")}`}</CardLabel>
                  <CardText style={{ fontSize: "15px", Colour: "black" }}>{t(TradeDetails.DoorNoBuild)}</CardText>
                </div>
                <div className="col-md-6" ><CardLabel>{`${t("TL_LOCALIZATION_DOOR_NO_SUB")}`}</CardLabel>
                  <CardText style={{ fontSize: "15px", Colour: "black" }}>{t(TradeDetails.DoorSubBuild)}&nbsp;&nbsp;{<ActionButton jumpTo={`${routeLink}/units-details`} />}</CardText>
                </div>
              </div>
            </StatusTable>
          </div>)}
          {TradeDetails?.setPlaceofActivity.code === "VECHICLE" && (
          <div>
            <div className="row">
              <div className="col-md-12" ><h1 className="headingh1" ><span style={{ background: "#fff", padding: "0 10px" }}>{`${t("TL_VECHICLE_HEADER")}`}</span></h1>
              </div>
            </div>
            <StatusTable>              
              <div className="row">
                <div className="col-md-6" ><CardLabel>{`${t("TL_VECHICLE_NO")}`}</CardLabel>
                  <CardText style={{ fontSize: "15px", Colour: "black" }}>{t(TradeDetails.VechicleNo)}</CardText>
                </div>
                <div className="col-md-6" ><CardLabel></CardLabel>
                  <CardText style={{ fontSize: "15px", Colour: "black" }}>{<ActionButton jumpTo={`${routeLink}/units-details`} />}</CardText>
                </div>
              </div>
            </StatusTable>
          </div>)}
          {TradeDetails?.setPlaceofActivity.code === "WATER" && (
          <div>
            <div className="row">
              <div className="col-md-12" ><h1 className="headingh1" ><span style={{ background: "#fff", padding: "0 10px" }}>{`${t("TL_VESSEL_HEADER")}`}</span></h1>
              </div>
            </div>
            <StatusTable>              
              <div className="row">
                <div className="col-md-6" ><CardLabel>{`${t("TL_VESSEL_NO")}`}</CardLabel>
                  <CardText style={{ fontSize: "15px", Colour: "black" }}>{t(TradeDetails.VesselNo)}</CardText>
                </div>
                <div className="col-md-6" ><CardLabel></CardLabel>
                  <CardText style={{ fontSize: "15px", Colour: "black" }}>{<ActionButton jumpTo={`${routeLink}/units-details`} />}</CardText>
                </div>
              </div>
            </StatusTable>
          </div>)}
        <label style={{ fontSize: "17px", fontWeight: "bold" }} >{t("TL_TRADE_BUISINESS_CATEGORY")}</label>
        <div className="row">
          <div className="col-md-12" ><h1 className="headingh1" ><span style={{ background: "#fff", padding: "0 10px" }}>{`${t("TL_BUISINESS_HEADER_MSG")}`}</span></h1>
          </div>
        </div>
        <StatusTable >
          <div className="row">
            <div className="col-md-6" ><CardLabel style={{ lineHeight: "auto" }}>{`${t("TL_LOCALIZATION_SECTOR")}`}</CardLabel>
              <CardText style={{ fontSize: "15px", Colour: "black" }}>{t(TradeDetails.setSector.name)}</CardText>
            </div>
            <div className="col-md-6" ><CardLabel style={{ lineHeight: "auto" }}>{`${t("TL_LOCALIZATION_CAPITAL_AMOUNT")}`}</CardLabel>
              <CardText style={{ fontSize: "15px", Colour: "black" }}>{t(TradeDetails.CapitalAmount)}&nbsp;&nbsp;{<ActionButton jumpTo={`${routeLink}/units-details`} />}</CardText>
            </div>
          </div>
        </StatusTable>
        <label style={{ fontSize: "17px", fontWeight: "bold" }} >{t("Date of Commencement and Final Declaration")}</label>
        <div className="row">
          <div className="col-md-12" ><h1 className="headingh1" ><span style={{ background: "#fff", padding: "0 10px" }}>{`${t("TL_LICENSE_DECLARATION")}`}</span></h1>
          </div>
        </div>
        <StatusTable >
          <div className="row">
            <div className="col-md-6" ><CardLabel style={{ lineHeight: "auto" }}>{`${t("TL_NEW_TRADE_DETAILS_TRADE_COMM_DATE_LABEL")}`}</CardLabel>
              <CardText style={{ fontSize: "15px", Colour: "black" }}>{t(owners.CommencementDate)}</CardText>
            </div>
            <div className="col-md-6" ><CardLabel style={{ lineHeight: "auto" }}>{`${t("TL_LICENSE_PERIOD")}`}</CardLabel>
              <CardText style={{ fontSize: "15px", Colour: "black" }}>{t(owners.LicensePeriod)}&nbsp;&nbsp;{<ActionButton jumpTo={`${routeLink}/units-details`} />}</CardText>
            </div>
          </div>
        </StatusTable>

        <StatusTable>
          {/* <Row
            label={t("TL_LOCALIZATION_TRADE_NAME")}
            text={t(TradeDetails?.TradeName)}
            actionButton={<ActionButton jumpTo={`${routeLink}/TradeName`} />}
          />
          <Row
            label="Place Of Activity"
            // {t("TL_STRUCTURE_TYPE")}
            text={t(`TL_${TradeDetails?.setPlaceofActivity.code}`)}
            actionButton={<ActionButton jumpTo={`${routeLink}/structure-type`} />}
          />
          <Row
            label="Nature Of Structure"
            // {t("TL_STRUCTURE_TYPE")}
            text={t(`TL_${TradeDetails?.StructureType.code}`)}
            actionButton={<ActionButton jumpTo={`${routeLink}/structure-type`} />}
          />          
          <Row
            label="Block No/Sub Division No/SurveyNo"
            // {t("TL_STRUCTURE_SUB_TYPE")}
            text={t(TradeDetails?.BlockNo, + "/" , + TradeDetails?.SubDivNo, + "/", + TradeDetails?.SurveyNo)}
            actionButton={<ActionButton jumpTo={`${routeLink}/land-type`} />}
          /> */}

          {/* <Row
            label={t("TL_STRUCTURE_SUB_TYPE")}
            text={t(TradeDetails?.StructureType.code !== "IMMOVABLE" ? TradeDetails?.VehicleType?.i18nKey : TradeDetails?.BuildingType?.i18nKey)}
            actionButton={<ActionButton jumpTo={TradeDetails?.VehicleType ? `${routeLink}/vehicle-type` : `${routeLink}/Building-type`} />}
          /> */}
          {/* <Row
            label={t("TL_NEW_TRADE_DETAILS_TRADE_COMM_DATE_LABEL")}
            text={t(getdate(TradeDetails?.CommencementDate))}
            actionButton={<ActionButton jumpTo={`${routeLink}/commencement-date`} />}
          />
          {TradeDetails?.units.map((unit, index) => (
            <div key={index}>
              <CardSubHeader>
                {t("TL_UNIT_HEADER")}-{index + 1}
              </CardSubHeader>
              <Row
                label={t("TL_NEW_TRADE_DETAILS_TRADE_CAT_LABEL")}
                text={t(unit?.tradecategory?.i18nKey)}
                actionButton={<ActionButton jumpTo={`${routeLink}/units-details`} />}
              />
              <Row
                label={t("TL_NEW_TRADE_DETAILS_TRADE_TYPE_LABEL")}
                text={t(unit?.tradetype?.i18nKey)}
                actionButton={<ActionButton jumpTo={`${routeLink}/units-details`} />}
              />
              <Row
                label={t("TL_NEW_TRADE_DETAILS_TRADE_SUBTYPE_LABEL")}
                text={t(unit?.tradesubtype?.i18nKey)}
                actionButton={<ActionButton jumpTo={`${routeLink}/units-details`} />}
              />
              <Row
                label={t("TL_UNIT_OF_MEASURE_LABEL")}
                labelStyle={{marginRight:"2px"}}
                text={`${unit?.unit ? t(unit?.unit) : t("CS_NA")}`}
                actionButton={<ActionButton jumpTo={`${routeLink}/units-details`} />}
              /> */}
          {/* <Row
                label={t("TL_NEW_TRADE_DETAILS_UOM_VALUE_LABEL")}
                labelStyle={{marginRight:"2px"}}
                text={`${unit?.uom ? t(unit?.uom) : t("CS_NA")}`}
                actionButton={<ActionButton jumpTo={`${routeLink}/units-details`} />}
              /> */}
          {/* </div>
          ))} */}
          {/* {TradeDetails?.accessories &&
            TradeDetails?.accessories.map((acc, index) => (
              <div key={index}>
                <CardSubHeader>
                  {t("TL_ACCESSORY_LABEL")}-{index + 1}
                </CardSubHeader>
                <Row
                  label={t("TL_TRADE_ACC_HEADER")}
                  text={t(acc?.accessory?.i18nKey)}
                  actionButton={<ActionButton jumpTo={`${routeLink}/accessories-details`} />}
                />
                <Row
                  label={t("TL_NEW_TRADE_ACCESSORY_COUNT")}
                  text={t(acc?.accessorycount)}
                  actionButton={<ActionButton jumpTo={`${routeLink}/accessories-details`} />}
                />
                <Row
                  label={t("TL_ACC_UOM_LABEL")}
                  labelStyle={{marginRight:"2px"}}
                  text={`${acc?.unit ? t(acc?.unit) : t("CS_NA")}`}
                  actionButton={<ActionButton jumpTo={`${routeLink}/accessories-details`} />}
                />
                <Row
                  label={t("TL_ACC_UOM_VALUE_LABEL")}
                  labelStyle={{marginRight:"2px"}}
                  text={`${acc?.unit ? t(acc?.uom) : t("CS_NA")}`}
                  actionButton={<ActionButton jumpTo={`${routeLink}/accessories-details`} />}
                />
              </div>
            ))} */}
          {/* <CardSubHeader>{t("TL_NEW_TRADE_DETAILS_HEADER_TRADE_LOC_DETAILS")}</CardSubHeader> */}
          {/* {TradeDetails && TradeDetails.details && TradeDetails.details?.propertyId ? (
            <React.Fragment>
              <Row
                label={t("TL_PROPERTY_ID")}
                text={`${TradeDetails.details.propertyId?.trim()}`}
                // actionButton={<ActionButton jumpTo={`${routeLink}/know-your-property`} />}
              />
              <Row
                label={t("TL_CHECK_ADDRESS")}
                text={`${address?.doorNo?.trim() ? `${details?.address?.doorNo?.trim()}, ` : ""} ${
                  address?.street?.trim() ? `${address?.street?.trim()}, ` : ""
                } ${address?.buildingName?.trim() ? `${address?.buildingName?.trim()}, ` : ""}
              ${t(address?.Zonal?.name)}, ${t(address?.WardNo.children.code)} ${
                address?.pincode?.trim() ? `,${address?.pincode?.trim()}` : ""
                }`}
                actionButton={
                  // cpt && cpt.details && cpt.details.propertyId ? (
                  //   <ActionButton jumpTo={`${routeLink}/property-details`} />
                  // ) : (
                    <ActionButton jumpTo={`${routeLink}/map`} />
                  // )
                }
              />
            </React.Fragment>
          ) : ( */}
          {/* <Row
              label={t("TL_CHECK_ADDRESS")}
              text={`${address?.doorNo?.trim() ? `${address?.doorNo?.trim()}, ` : ""} ${
                address?.street?.trim() ? `${address?.street?.trim()}, ` : ""
              }${t(address?.Zonal?.name)},${t(address?.WardNo.name)}  ${address?.pincode?.trim() ? `,${address?.pincode?.trim()}` : ""}`}
              actionButton={<ActionButton jumpTo={`${routeLink}/tladdress`} />}
            /> */}
          {/* )} */}
          {/* ${t(address?.city.code)} */}
          {/* <CardSubHeader>{t("TL_NEW_OWNER_DETAILS_HEADER")}</CardSubHeader>
          {owners.owners &&
            owners.owners.map((owner, index) => (
              <div key={index}>
                <CardSubHeader>
                  {t("TL_PAYMENT_PAID_BY_PLACEHOLDER")}-{index + 1}
                </CardSubHeader>
                <Row
                  label={t("TL_COMMON_TABLE_COL_OWN_NAME")}
                  text={t(owner?.name)}
                  actionButton={<ActionButton jumpTo={`${routeLink}/owner-details`} />}
                />
                <Row
                  label={t("TL_NEW_OWNER_DETAILS_GENDER_LABEL")}
                  text={t(owner?.gender?.i18nKey) || t("CS_NA")}
                  actionButton={<ActionButton jumpTo={`${routeLink}/owner-details`} />}
                />
                <Row
                  label={t("TL_MOBILE_NUMBER_LABEL")}
                  text={t(owner?.mobilenumber)}
                  actionButton={<ActionButton jumpTo={`${routeLink}/owner-details`} />}
                />{" "}
                <Row
                  label={t("TL_GUARDIAN_S_NAME_LABEL")}
                  text={t(owner?.fatherOrHusbandName)}
                  actionButton={<ActionButton jumpTo={`${routeLink}/owner-details`} />}
                />{" "}
                <Row
                  label={t("TL_RELATIONSHIP_WITH_GUARDIAN_LABEL")}
                  text={t(owner?.relationship?.i18nKey)}
                  actionButton={<ActionButton jumpTo={`${routeLink}/owner-details`} />}
                />
              </div>
            ))} */}
          {/* <Row
                  label={t("TL_CORRESPONDENCE_ADDRESS")}
                  labelStyle={{marginRight:"2px"}}
                  text={t(owners?.permanentAddress)}
                  actionButton={<ActionButton jumpTo={`${routeLink}/owner-address`} />}
            /> */}
          {/* <CardSubHeader>{t("TL_COMMON_DOCS")}</CardSubHeader>
          <ActionButton jumpTo={`${routeLink}/proof-of-identity`} />
          <div>
            {owners?.documents["OwnerPhotoProof"] || owners?.documents["ProofOfIdentity"] || owners?.documents["ProofOfOwnership"] ? (
              <TLDocument value={value}></TLDocument>
            ) : (
              <StatusTable>
                <Row text={t("TL_NO_DOCUMENTS_MSG")} />
              </StatusTable>
            )}
          </div> */}
        </StatusTable>
        <SubmitBar label={t("CS_COMMON_SUBMIT")} onSubmit={onSubmit} />
      </Card>
    </React.Fragment>
  );
};

export default CheckPage;