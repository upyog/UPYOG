  import React, { useEffect, useState, Fragment  } from "react";
  import { FormStep, TextInput, CardLabel, Dropdown,UploadFile,SearchIcon } from "@upyog/digit-ui-react-components";
  import Timeline from "../components/Timeline";
  import { useLocation, useRouteMatch } from "react-router-dom";
  import { Controller, useForm } from "react-hook-form";


  const BPANewBuildingdetails = ({ t, config, onSelect, formData }) => {
      const { pathname: url } = useLocation();
      let index = window.location.href.charAt(window.location.href.length - 1);
      let validation = {};
      const [approvedColony, setapprovedColony] = useState(formData?.owners?.approvedColony || "");
      const [masterPlan, setmasterPlan] = useState(formData?.owners?.masterPlan || "");
      const [UlbName, setUlbName] = useState(formData?.owners?.UlbName || "");
      const [buildingStatus, setbuildingStatus] = useState(formData?.owners?.buildingStatus || "");
      const [schemes, setschemes] = useState(formData?.owners?.schemes || "");
      const [purchasedFAR, setpurchasedFAR] = useState(formData?.owners?.purchasedFAR || "");
      const [greenbuilding, setgreenbuilding] = useState(formData?.owners?.greenbuilding || "");
      const [restrictedArea, setrestrictedArea] = useState(formData?.owners?.restrictedArea || "");
      const [District, setDistrict] = useState(formData?.owners?.District || "");
      const [proposedSite, setproposedSite] = useState(formData?.owners?.proposedSite || "");
      const [nameofApprovedcolony, setnameofApprovedcolony] = useState(formData?.owners?.nameofApprovedcolony || "");
      const [NocNumber, setNocNumber] = useState(formData?.owners?.NocNumber || "");
      const [coreArea, setcoreArea] = useState(formData?.owners?.coreArea || "");
      const [schemesselection, setschemesselection] = useState(formData?.owners?.schemesselection || "");
      const [schemeName, setschemeName] = useState(formData?.owners?.schemeName || "");
      const [transferredscheme, settransferredscheme] = useState("Pre-Approved Standard Designs" || "");
      const [Ulblisttype, setUlblisttype] = useState(formData?.owners?.Ulblisttype || "");
      const [uploadedFile, setUploadedFile] = useState(formData?.owners?.uploadedFile);
      const [file, setFile] = useState();
      const [error, setError] = useState(null);
      const [uploadMessage, setUploadMessage] = useState("");
      let Webview = !Digit.Utils.browser.isMobile();
      let acceptFormat = ".pdf"
      useEffect(() => {
        (async () => {
          setError(null);
          if (file&& file?.type) {
            if(!(acceptFormat?.split(",")?.includes(`.${file?.type?.split("/")?.pop()}`)))
            {
              setError(t("PT_UPLOAD_FORMAT_NOT_SUPPORTED"));
            }
            else if (file.size >= 2000000) {
              setError(t("PT_MAXIMUM_UPLOAD_SIZE_EXCEEDED"));
            } else {
              try {
                const response = await Digit.UploadServices.Filestorage("property-upload", file, Digit.ULBService.getStateId());
                if (response?.data?.files?.length > 0) {
                  setUploadedFile(response?.data?.files[0]?.fileStoreId);
                } else {
                  setError(t("PT_FILE_UPLOAD_ERROR"));
                }
              } catch (err) {
              }
            }
          }
        })();        
      }, [file]);




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

      const schemesselectiontype = [
        {
          code: "SCHEME",
          i18nKey: "SCHEME"
        },
        {
          code: "NON_SCHEME",
          i18nKey: "NON SCHEME"
        },
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

      const { data: ULBLIST } = Digit.Hooks.obps.useUlbType(stateId, "BPA", "UlbType");

      const { data: Menu } = Digit.Hooks.obps.useDistricts(stateId, "BPA", "Districts");
      const { data: ULB } = Digit.Hooks.obps.useULBList(stateId, "BPA", "Ulb");

      let ulblists = [];

      let menu = [];
      let ulb = [];

      ULBLIST &&
      ULBLIST.map((ulbtypelist) => {
        ulblists.push({ i18nKey: `${ulbtypelist.code}`, code: `${ulbtypelist.code}`, value: `${ulbtypelist.name}` });
        });

      Menu &&
        Menu.map((districts) => {
          if(districts.UlbType == Ulblisttype?.code)
          menu.push({ i18nKey: `${districts.code}`, code: `${districts.code}`, value: `${districts.name}` });
        });

        ULB &&
        ULB.map((ulblist) => {
          if (ulblist.Districts == UlbName?.code) {
            ulb.push({
              i18nKey: `${ulblist.code}`,
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

      function setcorearea(e) {
        setcoreArea(e.target.value);
      }


      function setulbname(e) {
        setUlbName(e.target.value);
      }

      function setulblisttype(e) {
        setUlblisttype(e.target.value);
      }

      function setBuildingStatus(e) {
        setbuildingStatus(e.target.value);
      }
      function setSchemes(e) {
        setschemes(e.target.value);
      }

      function setSchemeselection(e) {
        setschemesselection(e.target.value);
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
      function setdistrict(e) {
        setDistrict(e.target.value);
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

      function setSchemename(e) {
        setschemeName(e.target.value);
      }

      function TransferredScheme(e){
        settransferredscheme(e.target.value);
      }
      function selectfile(e) {
        setUploadedFile(e.target.files[0]);
        setFile(e.target.files[0]);
     }
      function onClick(e){
        console.log("inside_NOC_search")
      }


      const goNext = () => {
        let owners = formData.owners && formData.owners[index];
        let ownerStep = { ...owners, approvedColony, UlbName, Ulblisttype, District, masterPlan, coreArea, buildingStatus, schemes, schemesselection,  purchasedFAR, greenbuilding, restrictedArea, proposedSite, nameofApprovedcolony, schemeName, transferredscheme, NocNumber, uploadedFile };
        let updatedFormData = { ...formData };

        // Check if owners array exists in formData if not , then it will add it 
        if (!updatedFormData.owners) {
          updatedFormData.owners = [];
        }
        if((approvedColony?.code=="NO") ) 
        { if(NocNumber || uploadedFile || formData?.owners?.uploadedFile || formData?.owners?.NocNumber){
          onSelect(config.key, { ...formData[config.key], ...ownerStep }, updatedFormData, false, index);          
        }
        else{
          alert("Please fill NOC number or Upload NOC Document")
        }  }
        else{
          onSelect(config.key, { ...formData[config.key], ...ownerStep }, updatedFormData, false, index);
        }        
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
                    style={{ width: "86%" }}
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
              <div className="field-container">
                  <TextInput
                    t={t}
                    type={"text"}
                    isMandatory={false}
                    optionKey="i18nKey"
                    defaultValue={formData?.owners?.NocNumber}
                    name="NocNumber"
                    value={NocNumber}
                    onChange={setnocNumber}
                    style={{ width: "86%" }}
                    ValidationRequired={false}
                    {...(validation = {
                      //isRequired: true,
                      pattern: "^[a-zA-Z0-9]*$",
                      type: "text",
                      title: t("TL_NAME_ERROR_MESSAGE"),
                  })}
                  />
                <div style={{ position: "relative", zIndex: "100", right: "95px", marginTop: "-24px", marginRight:Webview?"-20px":"-20px" }} onClick={(e) => onClick( e)}> <SearchIcon /> </div>
                </div>
                <div style={{ position: "relative", fontWeight:"bold", left:"20px"}}>OR</div>
                <UploadFile
                id={"noc-doc"}
                style={{ width: "86%" }}
                onUpload={selectfile}
                onDelete={() => {
                    setUploadedFile(null);
                    setFile("");
                }}
                message={uploadedFile ? `1 ${t(`FILEUPLOADED`)}` : t(`ES_NO_FILE_SELECTED_LABEL`)}
                error={error}
                uploadMessage={uploadMessage}
            />
              </>
            );
          default:
            return null;
        }
      }
      
      const renderschemedropdown = () => {
        switch (schemes?.code) {
          case "SCHEME":
            return (
              <>
              <CardLabel>{`${t("BPA_SCHEMES_TYPE")}`}</CardLabel>
              <Controller
                control={control}
                name={"schemesselection"}
                defaultValue={schemesselection}
                rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG")}}
                render={(props) => (
                  <Dropdown
                    className="form-field"
                    selected={schemesselection}
                    select={setschemesselection}
                    option={forschemes}
                    optionKey="i18nKey"
                    t={t}
                  />
                )}
              />

              <CardLabel>{`${t("BPA_SCHEME_NAME")}`}</CardLabel>
              <TextInput
                t={t}
                type={"text"}
                isMandatory={false}
                optionKey="i18nKey"
                name="schemeName"
                value={schemeName}
                onChange={setSchemename}
                style={{ width: "86%" }}
                ValidationRequired={false}
                {...(validation = {
                  isRequired: true,
                  pattern: "^[a-zA-Z-.`' ]*$",
                  type: "text",
                  title: t("PT_NAME_ERROR_MESSAGE"),
                })}
              />

              <CardLabel>{`${t("BPA_TRANSFERRED_SCHEME")}`}</CardLabel>
              <TextInput
                t={t}
                type={"text"}
                isMandatory={false}
                optionKey="i18nKey"
                name="transferredscheme"
                value={transferredscheme}
                onChange={TransferredScheme}
                style={{ width: "86%" }}
                ValidationRequired={false}
                {...(validation = {
                  isRequired: true,
                  pattern: "^[a-zA-Z-.`' ]*$",
                  type: "text",
                  title: t("PT_NAME_ERROR_MESSAGE"),
                })}
              />

              </>
            );
          case "NON_SCHEME":
            return null;
            
          default:
            return null;
        }

      };


      return (
        <React.Fragment>
          <Timeline currentStep={2} />
          <FormStep
            config={config} onSelect={goNext} onSkip={onSkip} t={t}
          
            isDisabled={!approvedColony || !masterPlan || !coreArea || !Ulblisttype|| !UlbName || !buildingStatus || !schemes || !purchasedFAR || !greenbuilding || !restrictedArea || !proposedSite/* || (approvedColony === "YES" && !nameofApprovedcolony) || (approvedColony === "NO" && !NocNumber)*/}
          >
            <div>
              <CardLabel>{`${t("BPA_APPROVED_COLONY")}`}</CardLabel>
              <Controller
                control={control}
                name={"approvedColony"}
                defaultValue={approvedColony}
                rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG")}}
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
            
              

              <CardLabel style={{marginTop:"15px"}}>{`${t("BPA_MASTER_PLAN")}`}</CardLabel>
              <Controller
                control={control}
                name={"masterPlan"}
                defaultValue={masterPlan}
                rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG")}}
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
               <CardLabel>{`${t("BPA_CORE_AREA")}`}</CardLabel>
              <Controller
                control={control}
                name={"coreArea"}
                defaultValue={coreArea}
                rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG")}}
                render={(props) => (
                  <Dropdown

                    className="form-field"
                    selected={coreArea}
                    select={setcoreArea}
                    option={common}
                    optionKey="i18nKey"
                    t={t}
                  />

                )}

              />

              <CardLabel>{`${t("BPA_ULB_TYPE")}`}</CardLabel>
              <Controller
                control={control}
                name={"Ulblisttype"}
                defaultValue={Ulblisttype}
                rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG")}}
                render={(props) => (
                  <Dropdown

                    className="form-field"
                    selected={Ulblisttype}
                    select={setUlblisttype}
                    option={ulblists}
                    optionKey="i18nKey"
                    t={t}
                  />
                )}
                />

            <CardLabel>{`${t("BPA_ULB_NAME")}`}</CardLabel>
              <Controller
                control={control}
                name={"UlbName"}
                defaultValue={UlbName}
                rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG")}}
                render={(props) => (
                  <Dropdown

                    className="form-field"
                    selected={UlbName}
                    select={setUlbName}
                    option={menu}
                    optionKey="i18nKey"
                    t={t}
                  />
                )}
                />

              <CardLabel>{`${t("BPA_DISTRICT")}`}</CardLabel>
              <Controller  
                control={control}
                name={"District"}
                defaultValue={District}
                rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG")}}
                render={(props) => (
                  <Dropdown

                    className="form-field"
                    selected={District}
                    select={setDistrict}
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
                rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG")}}
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
                rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG")}}
                render={(props) => (
                  <Dropdown

                    className="form-field"
                    selected={schemes}
                    select={setschemes}
                    option={schemesselectiontype}
                    optionKey="i18nKey"
                    t={t}
                  />
                )}
              />
              {renderschemedropdown()}

              <CardLabel>{`${t("BPA_PURCHASED_FAR")}`}</CardLabel>
              <Controller
                control={control}
                name={"purchasedFAR"}
                defaultValue={purchasedFAR}
                rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG")}}
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
                rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG")}}
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
                rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG")}}
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
                rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG")}}
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