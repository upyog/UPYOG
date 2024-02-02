import React, { useEffect, useRef, useState, Fragment } from "react";
import ButtonSelector from "./ButtonSelector";
import { useTranslation } from "react-i18next";
import RemoveableTag from "./RemoveableTag";


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

const UploadFileDigiLocker = (props) => {
  const { t } = useTranslation();
  const inpRef = useRef();
  const [hasFile, setHasFile] = useState(false);
  const [prevSate, setprevSate] = useState(null);
  const [showModal, setShowModal] = useState(false);
  const user_type = Digit.SessionStorage.get("userType");
  const { isLoading, isSuccess,error,count,data:dataNew,  mutate: assessmentMutate } = Digit.Hooks.createTokenAPI("document");
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

  const dataURItoBlob = (dataURI) => {
    var binary = atob(dataURI.split(',')[1]);
    var array = [];
    for (var i = 0; i < binary.length; i++) {
        array.push(binary.charCodeAt(i));
    }
    return new Blob([new Uint8Array(array)], { type: 'application/pdf' });
  };
/* this fetchDigiLockerDocuments function is used to fetch documents from Digilocker*/

   const fetchDigiLockerDocuments  = async (e) => {
    e.preventDefault()
   
          const digiLockerToken = sessionStorage.getItem('DigiLocker.token1')
          let TokenReq = {
            "authToken":digiLockerToken
          }
          const res1 = await Digit.DigiLockerService.issueDoc({TokenReq })
          console.log("res1res1res1res1res1",res1)
          let uri = res1.IssuedDoc.filter((item)=>{
            return item.doctype == "DRVLC"
          })
          let TokenReqNew = {
            "authToken":digiLockerToken,
            "id":uri?.[0]?.uri,
          }
          
         console.log("url",uri)
         if(uri?.length>0)
         {
          const res2 = await Digit.DigiLockerService.uri({"TokenReq":TokenReqNew})

          let c= new Blob([res2])
          convertToFile(e,c)
          
          // fetch('https://api.digitallocker.gov.in/public/oauth2/1/file/' + uri?.[0]?.uri, {
          //       method: 'GET',
          //       mode: 'cors',
          //       headers: {
          //         "Authorization": code1,
          //         "Access-Control-Allow-Origin": "*",
          //         "Accept":"*/*"

          //       },
          //     }).then(res => res.blob().then(data =>{
          //       console.log("resssssssss",res)
          //        var reader = new FileReader();
          //        reader.readAsDataURL(data);
          //       reader.onloadend = function () {
          //         var base64data = reader.result;
          //         var blobData = dataURItoBlob(base64data);
          //         let newFile= new File([blobData], `drivingL.pdf`, { type: "application/pdf" })
          //         console.log("newFile",newFile)
          //         props.onUpload(e,newFile)
          //       //  const response1 =  Digit.UploadServices.Filestorage("property-upload", newFile, Digit.ULBService.getStateId());
          //       //   console.log("fffffffff",response1)
          //     }
          //     }).catch(err =>{console.log("pdffff",err)})
          //     )
            
         }
         // console.log("data",data)
          // fetch('https://api.digitallocker.gov.in/public/oauth2/2/files/issued', {
          //   method: 'GET',                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                     
          //   mode: 'cors',
          //   headers: {
          //     "Authorization": code1,
          //     "Access-Control-Allow-Origin": "*",
          //   },
          // }).then(response =>
          //   response.json().then(data => ({
          //     data: data,
          //   }))).then(res => {
          //     console.log("step 2",res)
          //     fetch('https://api.digitallocker.gov.in/public/oauth2/1/file/' + res.data.items[0].uri, {
          //       method: 'GET',
          //       mode: 'cors',
          //       headers: {
          //         "Authorization": code1,
          //         "Access-Control-Allow-Origin": "*",
          //         "Accept":"*/*"

          //       },
          //     }).then(res => res.blob().then(data =>{
          //        var reader = new FileReader();
          //        reader.readAsDataURL(data);
          //       reader.onloadend = function () {
          //         var base64data = reader.result;
          //         var blobData = dataURItoBlob(base64data);
          //         let newFile= new File([blobData], `drivingL.pdf`, { type: "application/pdf" })
          //         console.log("newFile",newFile)
          //         props.onUpload(e,newFile)
          //       //  const response1 =  Digit.UploadServices.Filestorage("property-upload", newFile, Digit.ULBService.getStateId());
          //       //   console.log("fffffffff",response1)
          //     }
          //     }).catch(err =>{console.log("pdffff",err)})
          //     )
          //   }).catch(error => console.log('error2', error))
       
  }
  const convertToFile = (e,blob) => {
    var reader = new FileReader();
    reader.readAsDataURL(blob);
    reader.onloadend = function () {
      var base64data = reader.result;
      var blobData = dataURItoBlob(base64data);
      let newFile = new File([blobData], `drivingL.pdf`, { type: "application/pdf" })
      props.onUpload(e, newFile)
      };
    
  };
  const Close = () => (
    <svg width="24" height="24" viewBox="0 0 24 24" fill="#FFFFFF" xmlns="http://www.w3.org/2000/svg">
        <path d="M19 6.41L17.59 5L12 10.59L6.41 5L5 6.41L10.59 12L5 17.59L6.41 19L12 13.41L17.59 19L19 17.59L13.41 12L19 6.41Z" fill="#0B0C0C" />
    </svg>
  );
  const showHint = props?.showHint || false;
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
          <div className="col col-md-4  text-md-center p-0" style={{width:"40%", marginTop:"5px"}}>
             <button className="digilocker-btn"type="submit" onClick={(e)=> fetchDigiLockerDocuments(e)}><img src="https://meripehchaan.gov.in/assets/img/icon/digi.png" class="mr-2" style={{"width":"12%"}}></img>Fetch from DigiLocker</button>
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
    </Fragment>
  );
};

export default UploadFileDigiLocker;
