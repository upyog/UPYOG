import React, { useEffect, useRef, useState, Fragment } from "react";
import ButtonSelector from "../../../../react-components/src/atoms/ButtonSelector";
import { Close } from "../../../../react-components/src/atoms/svgindex";
import { useTranslation } from "react-i18next";
import RemoveableTag from "../../../../react-components/src/atoms/RemoveableTag";


// using the custom upload file component in order to do the style related changes.
// Rest code is same as the original code

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
  const user_type = Digit.SessionStorage.get("userType");
  let extraStyles = {};
  const handleChange = () => {
    if (inpRef.current.files[0])
    { setHasFile(true);
      setprevSate(inpRef.current.files[0])
    }
    else setHasFile(false);
  };

  // for common aligmnent issues added common styles
  extraStyles = getCitizenStyles("OBPS");

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

  const showHint = props?.showHint || false;

  return (
    <Fragment>
      {showHint && <p className="cell-text">{t(props?.hintText)}</p>}
      <div className={`upload-file ${user_type === "employee" ? "":"upload-file-max-width"} ${props.disabled ? " disabled" : ""}`} 
        style={{
          borderRadius: "8px",
          border: "1px solid #ccc",
          boxShadow: "0 1px 3px rgba(0, 0, 0, 0.1)",
          padding: "8px",
          backgroundColor: "#fff",
          display: "flex",
          flexDirection: "column",
          width: user_type === "employee" ? "45%" : "86%",
          ...(extraStyles?.uploadFile ? extraStyles?.uploadFile : {})
        }}>
        <div style={{
          display: "flex",
          alignItems: "center",
          justifyContent: "space-between",
          width: "100%",
          ...(extraStyles ? extraStyles?.containerStyles : null)
        }}>
          <div 
            className="upload-button-container"
            style={{ 
              display: "flex", 
              alignItems: "center", 
              cursor: "pointer",
              padding: "8px 16px",
              borderRadius: "4px",
              color: "#902434",
              backgroundColor: "white",
              ...(extraStyles ? extraStyles?.buttonStyles : {}), 
              ...(props.disabled ? { display: "none" } : {})
            }}
            onClick={() => inpRef.current.click()}
          >
            <svg width="25" height="25" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg" style={{ marginRight: "8px" }}>
              <path d="M19.35 10.04C18.67 6.59 15.64 4 12 4C9.11 4 6.6 5.64 5.35 8.04C2.34 8.36 0 10.91 0 14C0 17.31 2.69 20 6 20H19C21.76 20 24 17.76 24 15C24 12.36 21.95 10.22 19.35 10.04ZM19 18H6C3.79 18 2 16.21 2 14C2 11.95 3.53 10.24 5.56 10.03L6.63 9.92L7.13 8.97C8.08 7.14 9.94 6 12 6C14.62 6 16.88 7.86 17.39 10.43L17.69 11.93L19.22 12.04C20.78 12.14 22 13.45 22 15C22 16.65 20.65 18 19 18ZM8 13H10.55V16H13.45V13H16L12 9L8 13Z" fill="#902434"/>
            </svg>
            <span style={{
              fontSize: "15px",
              fontWeight: "500",
              letterSpacing: "0.5px",
              textTransform: "uppercase",
              ...(props?.textStyles || {})
            }}>{t("CS_COMMON_CHOOSE_FILE")}</span>
          </div>
            {props?.uploadedFiles?.map((file, index) => {
              const fileDetailsData = file[1]
              return <div className="tag-container" style={extraStyles ? extraStyles?.tagContainerStyles : null}>
                <RemoveableTag extraStyles={extraStyles} key={index} text={file[0]} onClick={(e) => props?.removeTargetedFile(fileDetailsData, e)} />
              </div>
            })}
          {!hasFile || props.error ? (
            <div className="file-upload-status" style={{
              display: "flex",
              alignItems: "center",
              color: "#666",
              marginLeft: "10px",
              fontSize: "14px"
            }}>
              {props.error ? (
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg" style={{ marginRight: "8px" }}>
                  <path d="M12 2C6.48 2 2 6.48 2 12C2 17.52 6.48 22 12 22C17.52 22 22 17.52 22 12C22 6.48 17.52 2 12 2ZM12 20C7.59 20 4 16.41 4 12C4 7.59 7.59 4 12 4C16.41 4 20 7.59 20 12C20 16.41 16.41 20 12 20ZM11 15H13V17H11V15ZM11 7H13V13H11V7Z" fill="#FF3333"/>
                </svg>
              ) : (
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg" style={{ marginRight: "8px" }}>
                  <path d="M12 2C6.48 2 2 6.48 2 12C2 17.52 6.48 22 12 22C17.52 22 22 17.52 22 12C22 6.48 17.52 2 12 2ZM12 20C7.59 20 4 16.41 4 12C4 7.59 7.59 4 12 4C16.41 4 20 7.59 20 12C20 16.41 16.41 20 12 20ZM11 15H13V17H11V15ZM11 7H13V13H11V7Z" fill="#666"/>
                </svg>
              )}
              {props.message}
            </div>
          ) : (
            <div className="tag-container" style={{
              display: "flex",
              alignItems: "center",
              backgroundColor: "white",
              borderRadius: "4px",
              padding: "6px 10px",
              margin: "0 10px",
              ...(extraStyles ? extraStyles?.tagContainerStyles : null)
            }}>
              <div className="tag" style={{
                display: "flex",
                alignItems: "center",
                justifyContent: "space-between",
                width: "100%",
                ...(extraStyles ? extraStyles?.tagStyles : null)
              }}>
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg" style={{ marginRight: "8px" }}>
                  <path d="M14 2H6C4.9 2 4.01 2.9 4.01 4L4 20C4 21.1 4.89 22 5.99 22H18C19.1 22 20 21.1 20 20V8L14 2ZM18 20H6V4H13V9H18V20ZM8 15.01L9.41 16.42L11 14.84V19H13V14.84L14.59 16.43L16 15.01L12.01 11L8 15.01Z" fill="#902434"/>
                </svg>
                <span className="text" style={{
                  flex: 1,
                  overflow: "hidden",
                  textOverflow: "ellipsis",
                  whiteSpace: "nowrap",
                  color: "#902434",
                  ...(extraStyles ? extraStyles?.textStyles : null)
                }}>
                  {(typeof inpRef.current.files[0]?.name !== "undefined") && !(props?.file) ? inpRef.current.files[0]?.name : props.file?.name} 
                </span>
                <span onClick={() => handleDelete()} style={{
                  cursor: "pointer",
                  marginLeft: "8px",
                  ...(extraStyles ? extraStyles?.closeIconStyles : null)
                }}>
                  <Close style={{...props.Multistyle, fill: "#902434"}} className="close" />
                </span>
              </div>
            </div>
          )}
        </div>
        <input
          className={props.disabled ? "disabled" : "" + "input-mirror-selector-button"}
          style={{
            position: "absolute",
            width: "1px",
            height: "1px",
            padding: "0",
            margin: "-1px",
            overflow: "hidden",
            clip: "rect(0, 0, 0, 0)",
            border: "0",
            ...(extraStyles ? { ...extraStyles?.inputStyles, ...props?.inputStyles } : { ...props?.inputStyles })
          }}
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
    </Fragment>
  );
};

export default UploadFile;
