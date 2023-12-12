import React, { useEffect, useRef, useState, Fragment } from "react";
import ButtonSelector from "./ButtonSelector";
import { Close } from "./svgindex";
import { useTranslation } from "react-i18next";
import RemoveableTag from "./RemoveableTag";
import { Loader, Modal } from "..";

const getRandomId = () => {
  return Math.floor((Math.random() || 1) * 139);
};

const getCitizenStyles = (value) => {
  let citizenStyles = {};
  if (value == "propertyCreate") {
    citizenStyles = {
      textStyles: {
        whiteSpace: "nowrap",
        width: "100%",
        overflow: "hidden",
        textOverflow: "ellipsis",
        width: "80%"
      },
      tagStyles: {
        width: "90%",
        flexWrap: "nowrap",
      },
      inputStyles: {
        width: "44%",
        minHeight: "2rem",
        maxHeight: "3rem",
        top: "20%"
      },
      buttonStyles: {
        height: "auto",
        minHeight: "2rem",
        width: "40%",
        maxHeight: "3rem"
      },
      tagContainerStyles: {
        width: "60%",
        display: "flex", 
        marginTop: "0px"
      },
      closeIconStyles: {
        width : "20px"
      },
      containerStyles: {
        padding: "10px", 
        marginTop: "0px"
      },

    };
  } else if (value == "IP") {
    citizenStyles = {
      textStyles: {
        whiteSpace: "nowrap",
        maxWidth: "250px",
        overflow: "hidden",
        textOverflow: "ellipsis",
      },
      tagStyles: {
        marginLeft:"-30px"
      },
      inputStyles: {},
      closeIconStyles: {
        position:"absolute",
        marginTop:"-12px"
      },
      buttonStyles: {},
      tagContainerStyles: {},
    };
  } else if (value == "OBPS") {
    citizenStyles = {
      containerStyles: {
        display: "flex", 
        justifyContent: "flex-start", 
        alignItems: "center", 
        flexWrap: "wrap",
        margin: "0px",
        padding: "0px"
      },
      tagContainerStyles: {
       margin: "0px",
       padding: "0px",
       width: "46%"
      },
      tagStyles: {
        height: "auto", 
        padding: "5px", 
        margin: 0,
        width: "100%",
        margin: "5px"
      },
      textStyles: {
        wordBreak: "break-word",
        height: "auto",
        lineHeight: "16px",
        overflow: "hidden",
        // minHeight: "35px",
        maxHeight: "34px"
      },   
      inputStyles: {
        width: "43%",
        minHeight: "42px",
        maxHeight: "42px",
        top: "5px",
        left: "5px"
      },
      buttonStyles: {
        height: "auto",
        minHeight: "40px",
        width: "43%",
        maxHeight: "40px",
        margin: "5px",
        padding: "0px"
      },
      closeIconStyles: {
        width : "20px"
      },
      uploadFile: {
        minHeight: "50px"
      }
    };
  }
  else {
    citizenStyles = {
      textStyles: {},
      tagStyles: {},
      inputStyles: {},
      buttonStyles: {},
      tagContainerStyles: {},
    };
  }
  return citizenStyles;
};

const UploadFile = (props) => {
  const { t } = useTranslation();
  const inpRef = useRef();
  const [hasFile, setHasFile] = useState(false);
  const [prevSate, setprevSate] = useState(null);
  const [showModal, setShowModal] = useState(false);
  const user_type = Digit.SessionStorage.get("userType");
  let extraStyles = {};
  const handleChange = () => {
    if (inpRef.current.files[0])
    { setHasFile(true);
      setprevSate(inpRef.current.files[0])
    }
    else setHasFile(false);
  };
  const closeModal = () => {
    setShowModal(false);
}
const Heading = (props) => {
  return <h1 style={{ marginLeft: "22px" }} className="heading-m BPAheading-m">{props.label}</h1>;
};

const Close = () => (
  <svg width="24" height="24" viewBox="0 0 24 24" fill="#FFFFFF" xmlns="http://www.w3.org/2000/svg">
      <path d="M19 6.41L17.59 5L12 10.59L6.41 5L5 6.41L10.59 12L5 17.59L6.41 19L12 13.41L17.59 19L19 17.59L13.41 12L19 6.41Z" fill="#0B0C0C" />
  </svg>
);

const CloseBtn = (props) => {
  return (
      <div className="icon-bg-secondary" onClick={props.onClick} style={{ backgroundColor: "#FFFFFF" }}>
          <Close />
      </div>
  );
};

  // for common aligmnent issues added common styles
  extraStyles = getCitizenStyles("OBPS");

  // if (window.location.href.includes("/obps") || window.location.href.includes("/noc")) {
  //   extraStyles = getCitizenStyles("OBPS");
  // } else {
  //   switch (props.extraStyleName) {
  //     case "propertyCreate":
  //       extraStyles = getCitizenStyles("propertyCreate");
  //       break;
  //     case "IP":
  //       extraStyles = getCitizenStyles("IP");
  //       break;
  //     case "OBPS":
  //       extraStyles = getCitizenStyles("OBPS");
  //     default:
  //       extraStyles = getCitizenStyles("");
  //   }
  // }

  const handleDelete = () => {
    inpRef.current.value = "";
    props.onDelete();
  };

  const handleEmpty = () => {
    if(inpRef.current.files.length <= 0 && prevSate !== null)
    { inpRef.current.value = "";
      props.onDelete();
    }
  };

  if (props.uploadMessage && inpRef.current.value) {
    handleDelete();
    setHasFile(false);
  }

  useEffect(() => handleEmpty(), [inpRef?.current?.files])

  useEffect(() => handleChange(), [props.message]);
const setModelValue =(e)=>{
  e.preventDefault()
  setShowModal(true)
}
  const showHint = props?.showHint || false;
let url ="https://digilocker.meripehchaan.gov.in/public/oauth2/1/authorize?response_type=code&client_id=IB0DDEFE20&state=oidc_flow&redirect_uri=https%3A%2F%2Fupyog-test.niua.org%2FDigiLocker&code_challenge=lx1QRh0rzTWiVqlOyruU0CNS9WASYNtYQ1atsutrzK4&code_challenge_method=S256&dl_flow=signin&output=embed"
  return (
    <Fragment>
      {showHint && <p className="cell-text">{t(props?.hintText)}</p>}
      <div className={`upload-file ${user_type === "employee" ? "":"upload-file-max-width"} ${props.disabled ? " disabled" : ""}`} style={extraStyles?.uploadFile ? extraStyles?.uploadFile : {}}>
        <div style= {extraStyles ? extraStyles?.containerStyles : null}>
          <ButtonSelector
            theme="border"
            label={t("CS_COMMON_CHOOSE_FILE")}
            style={{ ...(extraStyles ? extraStyles?.buttonStyles : {}), ...(props.disabled ? { display: "none" } : {}) }}
            textStyles={props?.textStyles}
            type={props.buttonType}
          />
          <span style={{fontWeight:"bold"}}>OR</span>
          <div className="col col-md-4  text-md-center p-0" style={{width:"40%"}}>
             <button className="digilocker-btn"type="submit" onClick={(e)=> setModelValue(e)}><img src="https://meripehchaan.gov.in/assets/img/icon/digi.png" class="mr-2" style={{"width":"12%"}}></img>Fetch from DigiLocker</button>
                </div>
            {props?.uploadedFiles?.map((file, index) => {
              const fileDetailsData = file[1]
              return <div className="tag-container" style={extraStyles ? extraStyles?.tagContainerStyles : null}>
                <RemoveableTag extraStyles={extraStyles} key={index} text={file[0]} onClick={(e) => props?.removeTargetedFile(fileDetailsData, e)} />
              </div>
            })}
          {!hasFile || props.error ? (
            <h2 className="file-upload-status">{props.message}</h2>
          ) : (
            <div className="tag-container" style={extraStyles ? extraStyles?.tagContainerStyles : null}>
              <div className="tag" style={extraStyles ? extraStyles?.tagStyles : null}>
                <span className="text" style={extraStyles ? extraStyles?.textStyles : null}>
                   {(typeof inpRef.current.files[0]?.name !== "undefined") && !(props?.file)  ? inpRef.current.files[0]?.name : props.file?.name} 
                </span>
                <span onClick={() => handleDelete()} style={extraStyles ? extraStyles?.closeIconStyles : null}>
                  <Close style={props.Multistyle} className="close" />
                </span>
              </div>
            </div>
          )}
        </div>
        <input
          className={props.disabled ? "disabled" : "" + "input-mirror-selector-button"}
          style={extraStyles ? { ...extraStyles?.inputStyles, ...props?.inputStyles } : { ...props?.inputStyles }}
          ref={inpRef}
          type="file"
          id={props.id || `document-${getRandomId()}`}
          name="file"
          multiple={props.multiple}
          accept={props.accept}
          disabled={props.disabled}
          onChange={(e) => props.onUpload(e)}
          onClick ={ event => {
            const { target = {} } = event || {};
            target.value = "";
          }}
        />
      </div>
      {props.iserror && <p style={{color: "red"}}>{props.iserror}</p>}
      {props?.showHintBelow && <p className="cell-text">{t(props?.hintText)}</p>}
      <div>
            {showModal ? <Modal
                headerBarMain={<Heading label={t(`CCF_DIGILOCKER_HEADER`)} />}
                headerBarEnd={<CloseBtn onClick={closeModal} />}
                actionCancelOnSubmit={closeModal}
                formId="modal-action"
                popupStyles={{ width: "750px", overflow: "auto" }}
                style={{ minHeight: "45px", height: "auto", width: "160px" }}
                hideSubmit={true}
                headerBarMainStyle={{ margin: "0px", height: "35px" }}

            >
                {url ?
                    <div style={{ width: "auto", height: "91vh", overflow: "hidden" }}>
                        <iframe
                            // allowfullscreen="true"
                            scrollbar={"none"}
                            border="none"
                            width={"100%"}
                            height={"100%"}
                            overflow={"auto"}
                            src={`${url}`}
                            referrerpolicy="no-referrer|no-referrer-when-downgrade|origin|origin-when-cross-origin|same-origin|strict-origin-when-cross-origin|unsafe-url"
                        ></iframe>
                    </div> : 
                    <div style={{width: "100%", height: "100px", display: "flex", justifyContent: "center", alignItems: "center"}}>
                        {t("COMMON_URL_NOT_FOUND")}
                    </div>}

            </Modal> : null}
        </div>
    </Fragment>
  );
};

export default UploadFile;
