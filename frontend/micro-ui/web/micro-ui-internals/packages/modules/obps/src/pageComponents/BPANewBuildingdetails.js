  import React, { useEffect, useState, Fragment  } from "react";
  import { FormStep, TextInput, CardLabel, Dropdown } from "@upyog/digit-ui-react-components";
  import { useLocation, useRouteMatch } from "react-router-dom";
  import { Controller, useForm } from "react-hook-form";


  const BPANewBuildingdetails = ({ t, config, onSelect, formData }) => {
      const { pathname: url } = useLocation();
      let index = window.location.href.charAt(window.location.href.length - 1);
      let validation = {};
      const [approvedColony, setapprovedColony] = useState(formData?.owners?.approvedColony || "");
      const [masterPlan, setmasterPlan] = useState(formData?.owners?.masterPlan || "");
      const [district, setDistrict] = useState(formData?.owners?.district || "");
      const [buildingStatus, setbuildingStatus] = useState(formData?.owners?.buildingStatus || "");
      const [schemes, setschemes] = useState(formData?.owners?.schemes || "");
      const [purchasedFAR, setpurchasedFAR] = useState(formData?.owners?.purchasedFAR || "");
      const [greenbuilding, setgreenbuilding] = useState(formData?.owners?.greenbuilding || "");
      const [restrictedArea, setrestrictedArea] = useState(formData?.owners?.restrictedArea || "");
      const [ulbType, setulbType] = useState(formData?.owners?.ulbType || "");
      const [proposedSite, setproposedSite] = useState(formData?.owners?.proposedSite || "");
      const [nameofApprovedcolony, setnameofApprovedcolony] = useState(formData?.owners?.nameofApprovedcolony || "");
      const [NocNumber, setNocNumber] = useState(formData?.owners?.NocNumber || "");


      const approvedcolonyStatus = [
        {
          code: "YES",
          i18nKey: "YES"
        },
        {
          code: "NO",
          i18nKey: "NO"
        },
        {
          code: "LAL_LAKEER",
          i18nKey: "LAL LAKEER"
        }
      ]

      const common = [
        {
          code: "YES",
          i18nKey: "YES"
        },
        {
          code: "NO",
          i18nKey: "NO"
        }
      ]

      
      const Typeofproposedsite = [
        {
          code: "PROPOSED",
          i18nKey: "Proposed"
        }
      ]

      const forschemes = [
        {
          code: "TP_SCHEMES",
          i18nKey: "TP SCHEMES"
        },
        {
          code: "DEVELOPMENT_SCHEMES",
          i18nKey: "DEVELOPMENT SCHEMES"
        },
        {
          code: "AFFORDABLE",
          i18nKey: "AFFORDABLE"
        }
      ]

      const status = [
        {
          code: "AUTHORIZED",
          i18nKey: "Authorized"
        },
        {
          code: "REGULARIZED",
          i18nKey: "Regularized"
        }
      ]

      const tenantId = Digit.ULBService.getCurrentTenantId();
      const stateId = Digit.ULBService.getStateId();

      const { data: Menu } = Digit.Hooks.obps.useDistricts(stateId, "BPA", "Districts");
      const { data: ULB } = Digit.Hooks.obps.useULBList(stateId, "BPA", "Ulb");

      let menu = [];
      let ulb = [];

      Menu &&
        Menu.map((districts) => {
          menu.push({ i18nKey: `BPA_DISTRICTS_${districts.code}`, code: `${districts.code}`, value: `${districts.name}` });
        });

        ULB &&
        ULB.map((ulblist) => {
          if (ulblist.Districts == district?.code) {
            ulb.push({
              i18nKey: `BPA_ULB_${ulblist.code}`,
              code: `${ulblist.code}`,
              value: `${ulblist.name}`
            });
          }

        });

      
      const { control } = useForm();

      function setApprovedColony(e) {
        setapprovedColony(e.target.value);
      }

      function setMasterPlan(e) {
        setmasterPlan(e.target.value);
      }

      function setdistrict(e) {
        setDistrict(e.target.value);
      }

      function setBuildingStatus(e) {
        setbuildingStatus(e.target.value);
      }
      function setSchemes(e) {
        setschemes(e.target.value);
      }
      function setPurchasedFAR(e) {
        setpurchasedFAR(e.target.value);
      }
      function setGreenbuilding(e) {
        setgreenbuilding(e.target.value);
      }
      function setRestrictedArea(e) {
        setrestrictedArea(e.target.value);
      }
      function setUlbType(e) {
        setulbType(e.target.value);
      }
      function setProposedSite(e) {
        setproposedSite(e.target.value);
      }

      function setNameapprovedcolony(e) {
        setnameofApprovedcolony(e.target.value);
      }

      function setnocNumber(e) {
        setNocNumber(e.target.value);
      }



      const goNext = () => {
        let owners = formData.owners && formData.owners[index];
        let ownerStep = { ...owners, approvedColony, district, ulbType, masterPlan, buildingStatus, schemes, purchasedFAR, greenbuilding, restrictedArea, proposedSite, nameofApprovedcolony, NocNumber };
        let updatedFormData = { ...formData };

        // Check if owners array exists in formData if not , then it will add it 
        if (!updatedFormData.owners) {
          updatedFormData.owners = [];
        }
      
        onSelect(config.key, { ...formData[config.key], ...ownerStep }, updatedFormData, false, index);

      
      };

    

      
      const onSkip = () => onSelect();

      const renderFields = () => {
        switch (approvedColony?.code) {
          case "YES":
            return (
              <>
                <CardLabel>{`${t("BPA_APPROVED_COLONY_NAME")}`}</CardLabel>
                  <TextInput
                    t={t}
                    type={"text"}
                    isMandatory={false}
                    optionKey="i18nKey"
                    name="nameofApprovedcolony"
                    value={nameofApprovedcolony}
                    onChange={setNameapprovedcolony}
                    ValidationRequired={false}
                    {...(validation = {
                      isRequired: true,
                      pattern: "^[a-zA-Z ]*$",
                      type: "text",
                      title: t("TL_NAME_ERROR_MESSAGE"),
                  })}
                  />
              </>
            );
          case "NO":
            return (
              <>
                <CardLabel>{`${t("BPA_NOC_NUMBER")}`}</CardLabel>
                  <TextInput
                    t={t}
                    type={"text"}
                    isMandatory={false}
                    optionKey="i18nKey"
                    name="NocNumber"
                    value={NocNumber}
                    onChange={setnocNumber}
                    ValidationRequired={false}
                    {...(validation = {
                      isRequired: true,
                      pattern: "^[a-zA-Z0-9]*$",
                      type: "text",
                      title: t("TL_NAME_ERROR_MESSAGE"),
                  })}
                  />

              </>
            );
          default:
            return null;
        }
      };


      return (
        <React.Fragment>
          <FormStep
            config={config} onSelect={goNext} onSkip={onSkip} t={t}
          
            isDisabled={!approvedColony || !masterPlan || !district || !ulbType || !buildingStatus || !schemes || !purchasedFAR || !greenbuilding || !restrictedArea || !proposedSite/* || (approvedColony === "YES" && !nameofApprovedcolony) || (approvedColony === "NO" && !NocNumber)*/}
          >
            <div>
              <CardLabel>{`${t("BPA_APPROVED_COLONY")}`}</CardLabel>
              <Controller
                control={control}
                name={"approvedColony"}
                defaultValue={approvedColony}
                rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
                render={(props) => (
                  <Dropdown

                    className="form-field"
                    selected={approvedColony}
                    select={setapprovedColony}
                    option={approvedcolonyStatus}
                    optionKey="i18nKey"
                    t={t}
                  />

                )}

              />

              {renderFields()} 
            
              

              <CardLabel>{`${t("BPA_MASTER_PLAN")}`}</CardLabel>
              <Controller
                control={control}
                name={"masterPlan"}
                defaultValue={masterPlan}
                rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
                render={(props) => (
                  <Dropdown

                    className="form-field"
                    selected={masterPlan}
                    select={setmasterPlan}
                    option={common}
                    optionKey="i18nKey"
                    t={t}
                  />

                )}

              />
            <CardLabel>{`${t("BPA_DISTRICT")}`}</CardLabel>
              <Controller
                control={control}
                name={"district"}
                defaultValue={district}
                rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
                render={(props) => (
                  <Dropdown

                    className="form-field"
                    selected={district}
                    select={setDistrict}
                    option={menu}
                    optionKey="i18nKey"
                    t={t}
                  />
                )}
                />

              <CardLabel>{`${t("BPA_ULB_TYPE")}`}</CardLabel>
              <Controller  
                control={control}
                name={"ulbType"}
                defaultValue={ulbType}
                rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
                render={(props) => (
                  <Dropdown

                    className="form-field"
                    selected={ulbType}
                    select={setulbType}
                    option={ulb}
                    optionKey="i18nKey"
                    t={t}
                  />
                )}
              />
              <CardLabel>{`${t("BPA_BUILDING_STATUS")}`}</CardLabel>
              <Controller
                control={control}
                name={"buildingStatus"}
                defaultValue={buildingStatus}
                rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
                render={(props) => (
                  <Dropdown

                    className="form-field"
                    selected={buildingStatus}
                    select={setbuildingStatus}
                    option={status}
                    optionKey="i18nKey"
                    t={t}
                  />
                )}
              />
              <CardLabel>{`${t("BPA_SCHEMES")}`}</CardLabel>
              <Controller
                control={control}
                name={"schemes"}
                defaultValue={schemes}
                rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
                render={(props) => (
                  <Dropdown

                    className="form-field"
                    selected={schemes}
                    select={setschemes}
                    option={forschemes}
                    optionKey="i18nKey"
                    t={t}
                  />
                )}
              />
              <CardLabel>{`${t("BPA_PURCHASED_FAR")}`}</CardLabel>
              <Controller
                control={control}
                name={"purchasedFAR"}
                defaultValue={purchasedFAR}
                rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
                render={(props) => (
                  <Dropdown

                    className="form-field"
                    selected={purchasedFAR}
                    select={setpurchasedFAR}
                    option={common}
                    optionKey="i18nKey"
                    t={t}
                  />
                )}
              />
              <CardLabel>{`${t("BPA_GREEN_BUIDINGS")}`}</CardLabel>
              <Controller
                control={control}
                name={"greenbuilding"}
                defaultValue={greenbuilding}
                rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
                render={(props) => (
                  <Dropdown

                    className="form-field"
                    selected={greenbuilding}
                    select={setgreenbuilding}
                    option={common}
                    optionKey="i18nKey"
                    t={t}
                  />
                )}
              />
              <CardLabel>{`${t("BPA_RESTRICTED_AREA")}`}</CardLabel>
              <Controller
                control={control}
                name={"restrictedArea"}
                defaultValue={restrictedArea}
                rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
                render={(props) => (
                  <Dropdown

                    className="form-field"
                    selected={restrictedArea}
                    select={setrestrictedArea}
                    option={common}
                    optionKey="i18nKey"
                    t={t}
                  />
                )}
              />
              <CardLabel>{`${t("BPA_PROPOSED_SITE_TYPE")}`}</CardLabel>
              <Controller
                control={control}
                name={"proposedSite"}
                defaultValue={proposedSite}
                rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
                render={(props) => (
                  <Dropdown

                    className="form-field"
                    selected={proposedSite}
                    select={setproposedSite}
                    option={Typeofproposedsite}
                    optionKey="i18nKey"
                    t={t}
                  />
                )}
              />

            </div>
          </FormStep>
        </React.Fragment>
      );
  };

  export default BPANewBuildingdetails;