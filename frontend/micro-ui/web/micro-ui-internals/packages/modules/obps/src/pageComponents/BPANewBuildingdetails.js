import React, { useEffect, useState, Fragment  } from "react";
import { FormStep, TextInput, CardLabel, Dropdown,UploadFile,SearchIcon } from "@upyog/digit-ui-react-components";
import Timeline from "../components/Timeline";
import { useLocation, useRouteMatch } from "react-router-dom";
import { Controller, useForm } from "react-hook-form";


const BPANewBuildingdetails = ({ t, config, onSelect, formData }) => {
    const { pathname: url } = useLocation();
    let index = window.location.href.charAt(window.location.href.length - 1);
    let validation = {};
    const convertToObject = (String) => String ? { i18nKey: String, code: String, value: String } : null;

    const [approvedColony, setapprovedColony] = useState(formData?.owners?.approvedColony || convertToObject(formData?.additionalDetails?.approvedColony) || "");
    const [masterPlan, setmasterPlan] = useState(formData?.owners?.masterPlan || convertToObject(formData?.additionalDetails?.masterPlan) || "");
    const [UlbName, setUlbName] = useState(formData?.owners?.UlbName || convertToObject(formData?.additionalDetails?.UlbName) ||"");
    const [buildingStatus, setbuildingStatus] = useState(formData?.owners?.buildingStatus || convertToObject(formData?.additionalDetails?.buildingStatus) ||"");
    const [schemes, setschemes] = useState(formData?.owners?.schemes || convertToObject(formData?.additionalDetails?.schemes) || "");
    const [purchasedFAR, setpurchasedFAR] = useState(formData?.owners?.purchasedFAR || convertToObject(formData?.additionalDetails?.purchasedFAR) || "");
    const [greenbuilding, setgreenbuilding] = useState(formData?.owners?.greenbuilding || convertToObject(formData?.additionalDetails?.greenbuilding) || "");
    const [restrictedArea, setrestrictedArea] = useState(formData?.owners?.restrictedArea || convertToObject(formData?.additionalDetails?.restrictedArea) || "");
    const [District, setDistrict] = useState(formData?.owners?.District || convertToObject(formData?.additionalDetails?.District) || "");
    const [proposedSite, setproposedSite] = useState(formData?.owners?.proposedSite || convertToObject(formData?.additionalDetails?.proposedSite) || "");
    const [nameofApprovedcolony, setnameofApprovedcolony] = useState(formData?.owners?.nameofApprovedcolony || formData?.additionalDetails?.nameofApprovedcolony || "");
    const [NocNumber, setNocNumber] = useState(formData?.owners?.NocNumber || formData?.additionalDetails?.NocNumber || "");
    const [schemesselection, setschemesselection] = useState(formData?.owners?.schemesselection || convertToObject(formData?.additionalDetails?.schemesselection) || "");
    const [schemeName, setschemeName] = useState(formData?.owners?.schemeName || formData?.additionalDetails?.schemeName || "");
    const [transferredscheme, settransferredscheme] = useState("Pre-Approved Standard Designs" || "");
    const [rating, setrating] = useState(formData?.owners?.rating || convertToObject(formData?.additionalDetails?.rating) || "");
    const [use, setUse] = useState(formData?.owners?.use || convertToObject(formData?.additionalDetails?.use) || "");
    const [Ulblisttype, setUlblisttype] = useState(formData?.owners?.Ulblisttype || convertToObject(formData?.additionalDetails?.Ulblisttype) || "");
    const [uploadedFile, setUploadedFile] = useState(formData?.owners?.uploadedFile);
    const [greenuploadedFile, setGreenUploadedFile] = useState(formData?.owners?.greenuploadedFile);


    const [files, setFiles] = useState();

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

    useEffect(() => {
      (async () => {
        setError(null);
        if (files&& files?.type) {
          if(!(acceptFormat?.split(",")?.includes(`.${files?.type?.split("/")?.pop()}`)))
          {
            setError(t("PT_UPLOAD_FORMAT_NOT_SUPPORTED"));
          }
          else if (files.size >= 2000000) {
            setError(t("PT_MAXIMUM_UPLOAD_SIZE_EXCEEDED"));
          } else {
            try {
              const response = await Digit.UploadServices.Filestorage("property-upload", files, Digit.ULBService.getStateId());
              if (response?.data?.files?.length > 0) {
                setGreenUploadedFile(response?.data?.files[0]?.fileStoreId);
              } else {
                setError(t("PT_FILE_UPLOAD_ERROR"));
              }
            } catch (err) {
            }
          }
        }
      })();        
    }, [files]);


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

    
    const stateId = Digit.ULBService.getStateId();

    const { data: ulbList } = Digit.Hooks.obps.useUlbType(stateId, "BPA", "UlbType");

    const { data: districtMenu } = Digit.Hooks.obps.useDistricts(stateId, "BPA", "Districts");
    const { data: ULB } = Digit.Hooks.obps.useULBList(stateId, "BPA", "Ulb");

    let ulblists = [];

    let menu = [];
    let ulb = [];

    ulbList &&
    ulbList.map((ulbtypelist) => {
      if(ulbtypelist?.Districts === UlbName?.code)
      ulblists.push({ i18nKey: `${ulbtypelist.name}`, code: `${ulbtypelist.code}`, value: `${ulbtypelist.name}` });
      });

    districtMenu &&
      districtMenu.map((districts) => {
        // if(districts.UlbType == Ulblisttype?.code)
        menu.push({ i18nKey: `${districts.name}`, code: `${districts.code}`, value: `${districts.name}` });
      });

      ULB &&
      ULB.map((ulblist) => {
        if (ulblist.Districts == UlbName?.code) {
          ulb.push({
            i18nKey: `${ulblist.name}`,
            code: `${ulblist.code}`,
            value: `${ulblist.name}`
          });
        }

      });


      // Custom hooks to get the Data directly from MDMS, No need to make file inside Libraries --> Hooks Folder  

      const { data: commonBuilding } = Digit.Hooks.useCustomMDMS(Digit.ULBService.getStateId(), "BPA", [{ name: "BuildingStatus" }],
      {
        select: (data) => {
            const formattedData = data?.["BPA"]?.["BuildingStatus"]
            return formattedData;
        },
    }); 
    let building_status = [];

    commonBuilding && commonBuilding.map((selectBuilding) => {
      building_status.push({i18nKey: `BPA_${selectBuilding.code}`, code: `${selectBuilding.code}`, value: `${selectBuilding.name}`})
    }) 

    const { data: commonrating } = Digit.Hooks.useCustomMDMS(Digit.ULBService.getStateId(), "BPA", [{ name: "RatingValue" }],
      {
        select: (data) => {
            const formattedData = data?.["BPA"]?.["RatingValue"]
            return formattedData;
        },
    }); 
    let selectRating = [];

    commonrating && commonrating.map((selectRatings) => {
      selectRating.push({i18nKey: `BPA_${selectRatings.code}`, code: `${selectRatings.code}`, value: `${selectRatings.name}`})
    })
    
    const { data: commonmasterFields } = Digit.Hooks.useCustomMDMS(Digit.ULBService.getStateId(), "BPA", [{ name: "MasterFields" }],
      {
        select: (data) => {
            const formattedData = data?.["BPA"]?.["MasterFields"]
            return formattedData;
        },
    }); 
    let selectmasterDrop = [];

    commonmasterFields && commonmasterFields.map((selectMaster) => {
      selectmasterDrop.push({i18nKey: `BPA_${selectMaster.code}`, code: `${selectMaster.code}`, value: `${selectMaster.name}`})
    })

    const { data: commonScheme } = Digit.Hooks.useCustomMDMS(Digit.ULBService.getStateId(), "BPA", [{ name: "Scheme" }],
      {
        select: (data) => {
            const formattedData = data?.["BPA"]?.["Scheme"]
            return formattedData;
        },
    }); 
    let selectscheme = [];

    commonScheme && commonScheme.map((selectScheme) => {
      selectscheme.push({i18nKey: `BPA_${selectScheme.code}`, code: `${selectScheme.code}`, value: `${selectScheme.name}`})
    })

    const { data: commonSchemeType } = Digit.Hooks.useCustomMDMS(Digit.ULBService.getStateId(), "BPA", [{ name: "SchemeType" }],
      {
        select: (data) => {
            const formattedData = data?.["BPA"]?.["SchemeType"]
            return formattedData;
        },
    }); 
    let selectschemetypes = [];

    commonSchemeType && commonSchemeType.map((selectscheme) => {
      selectschemetypes.push({i18nKey: `BPA_${selectscheme.code}`, code: `${selectscheme.code}`, value: `${selectscheme.name}`})
    })


    
    const { control } = useForm();

    function setApprovedColony(e) {
      setapprovedColony(e.target.value);
    }

    function setMasterPlan(e) {
      setmasterPlan(e.target.value);
    }

    function setRatings(e) {
      setrating(e.target.value)
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
    function selectfiles(e) {
      setGreenUploadedFile(e.target.files[0]);
      setFiles(e.target.files[0]);
   }


   function setuse(e) {
    setUse(e.target.value);
  }



    const goNext = () => {
      let owners = formData.owners && formData.owners[index];
      let ownerStep = { ...owners, approvedColony, use, UlbName, Ulblisttype, District, rating, masterPlan, buildingStatus, schemes, schemesselection,  purchasedFAR, greenbuilding, restrictedArea, proposedSite, nameofApprovedcolony, schemeName, transferredscheme, NocNumber, uploadedFile,greenuploadedFile };
      let updatedFormData = { ...formData };

      // Check if owners array exists in formData if not , then it will add it 
      if (!updatedFormData.owners) {
        updatedFormData.owners = [];
      }
      if((approvedColony?.code=="NO") ) 
      { if(NocNumber || uploadedFile || formData?.owners?.uploadedFile ){
        if((greenbuilding?.code==="YES"))
        { if(greenuploadedFile || formData?.owners?.greenuploadedFile){
          onSelect(config.key, { ...formData[config.key], ...ownerStep }, updatedFormData, false, index);          
        }
        else{
          alert("Please Upload Document")
        }  }
        else{
        onSelect(config.key, { ...formData[config.key], ...ownerStep }, updatedFormData, false, index);        
      }  
      }
      else{
        alert("Please fill NOC number or Upload NOC Document")
      }  }
      
      else{
        if((greenbuilding?.code==="YES"))
        { if(greenuploadedFile || formData?.owners?.greenuploadedFile){
          onSelect(config.key, { ...formData[config.key], ...ownerStep }, updatedFormData, false, index);          
        }
        else{
          alert("Please Upload Document")
        }  }
        else{
        onSelect(config.key, { ...formData[config.key], ...ownerStep }, updatedFormData, false, index);
        }
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
                    pattern: "^[a-zA-Z0-9/]*$",
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

    const renderGreenbuildingfields = () => {
      switch (greenbuilding?.code) {
        case "YES":
          return(
          <>
            <UploadFile
                id={"green-building-doc"}
                onUpload={selectfiles}
                onDelete={() => {
                  setGreenUploadedFile(null);
                  setFiles("");
                }}
                message={greenuploadedFile ? `1 ${t(`FILEUPLOADED`)}` : t(`ES_NO_FILE_SELECTED_LABEL`)}
                error={error}
                uploadMessage={uploadMessage}
            />
            <br></br>

            <CardLabel>{`${t("BPA_SELECT_RATINGS")}`}</CardLabel>
            <Controller
              control={control}
              name={"rating"}
              defaultValue={rating}
              rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG")}}
              render={(props) => (
                <Dropdown
                  className="form-field"
                  selected={rating}
                  select={setrating}
                  option={selectRating}
                  optionKey="i18nKey"
                  t={t}
                />
              )}
            />

          </>
          );
          case "NO":
          return null;
          
        default:
          return null;
      }
    }

    const Master_plan_render_fields = () => {
      switch(masterPlan?.code) {
        case "YES":
          return(
            <>
             <CardLabel>{`${t("BPA_USE")}`}</CardLabel>
            <Controller
              control={control}
              name={"use"}
              defaultValue={use}
              rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG")}}
              render={(props) => (
                <Dropdown
                  className="form-field"
                  selected={use}
                  select={setUse}
                  option={selectmasterDrop}
                  optionKey="i18nKey"
                  t={t}
                />
              )}
            />

            </>
          );
        case "NO":
          return null;

        default:
          return null;

      }
    };
    
    const renderschemedropdown = () => {
      switch (schemes?.code) {
        case "SCHEME":
          return (
            <>
            <CardLabel>{`${t("BPA_SCHEME_TYPE_LABEL")}`}</CardLabel>
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
                  option={selectschemetypes}
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

            <CardLabel>{`${t("BPA_TRANFERRED_SCHEME_LABEL")}`}</CardLabel>
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
          isDisabled={!approvedColony || !masterPlan || !Ulblisttype|| !UlbName || !buildingStatus || !schemes || !purchasedFAR || !greenbuilding || !restrictedArea || !proposedSite/* || (approvedColony === "YES" && !nameofApprovedcolony) || (approvedColony === "NO" && !NocNumber)*/}
        >
          <div>
            <CardLabel>{`${t("BPA_APPROVED_COLONY")}`}</CardLabel>
            <Controller
              control={control}
              name={"approvedColony"}
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
            {Master_plan_render_fields()}

          <CardLabel>{`${t("BPA_DISTRICT")}`}</CardLabel>
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

            <CardLabel>{`${t("BPA_ULB_NAME")}`}</CardLabel>
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
                  option={building_status}
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
                  option={selectscheme}
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
            {renderGreenbuildingfields()}

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