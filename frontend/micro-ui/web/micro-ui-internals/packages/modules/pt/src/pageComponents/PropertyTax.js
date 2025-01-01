import { Card, CardHeader, CardSubHeader, CardText, Loader, SubmitBar } from "@nudmcdgnpm/digit-ui-react-components";
import React, { useEffect } from "react";
import { cardBodyStyle, stringReplaceAll } from "../utils";
//import { map } from "lodash-es";

const PropertyTax = ({ t, config, onSelect, userType, formData }) => {
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const stateId = Digit.ULBService.getStateId();
  sessionStorage.removeItem("docReqScreenByBack");

  const docType = config?.isMutation ? ["MutationDocuments"] : "Documents";

  const { isLoading, data: Documentsob = {} } = Digit.Hooks.pt.usePropertyMDMS(stateId, "PropertyTax", docType);

  let docs = Documentsob?.PropertyTax?.[config?.isMutation ? docType[0] : docType];
  if (!config?.isMutation) docs = docs?.filter((doc) => doc["digit-citizen"]);
  function onSave() {}

  function goNext() {
    onSelect();
  }
  function randomIntFromInterval(min, max) {
    return Math.floor(Math.random() * (max - min + 1) + min);
  }
  function generateCodeVerifier(length) {
    const characters =
      "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-._~";
    let codeVerifier = "";
    for (let i = 0; i < length; i++) {
      const randomIndex = Math.floor(Math.random() * characters.length);
      codeVerifier += characters.charAt(randomIndex);
    }
    return codeVerifier;
  }
  function sha256(plain) {
    const encoder = new TextEncoder();
    const data = encoder.encode(plain);
    return window.crypto.subtle.digest("SHA-256", data);
  }
  function base64UrlEncode(buffer) {
    const padding = "=".repeat((4 - (buffer.length % 4)) % 4);
    const base64 = btoa(String.fromCharCode.apply(null, new Uint8Array(buffer)));
    return (
      base64.replace(/\+/g, "-").replace(/\//g, "_").replace(/=+$/, "") + padding
    );
  }
  async function generateCodeChallenge(codeVerifier) {
    const hashedBuffer = await sha256(codeVerifier);
    const codeChallenge = base64UrlEncode(hashedBuffer);
    return codeChallenge;
  }
  useEffect(()=>{
window.process={...window.process}
// console.log("enviorement Variable",process.env.NODE_ENV,process.env.REACT_APP_PROXY_API,
// process.env)

  },[])

  const {  isSuccess,error,count, mutate: assessmentMutate } = Digit.Hooks.createTokenAPI();
const onConcent=async (e)=>{
  const data = await Digit.DigiLockerService.authorization({module:"PT"});
  e.preventDefault()
  console.log("data",data)
  sessionStorage.setItem("code_verfier",data?.codeverifier)
  //let redirectURL=data?.redirectURL.replace("https://upyog-test.niua.org","http://localhost:3000")
  window.location.href=data?.redirectURL
    /* Number of Random Bytes to Use to Generate Code Verifier (min 32, max 96 bytes) */
    // const randomByte = randomIntFromInterval(44, 96);
    // const codeVerifier = generateCodeVerifier(randomByte);
    // setItemWithExpiry('DigiLocker.codeVerifier', codeVerifier, 60);
    // /* Generate Code Challenge */
    // generateCodeChallenge(codeVerifier)
    //   .then((codeChallenge) => {
     
    //     console.log("Code Verifier:", codeVerifier);
    //     console.log("Code Challenge:", codeChallenge);
    //       window.location.href =`https://digilocker.meripehchaan.gov.in/public/oauth2/1/authorize?response_type=code&client_id=AT3053EB6D&state=oidc_flow&redirect_uri=http%3A%2F%2Flocalhost:3000%2Fdigit-ui%2Fcitizen%2Fpt%2Fproperty%2Fnew-application%2Finfo&code_challenge=${codeChallenge}&code_challenge_method=S256&dl_flow=signin`;
        
    //   })
    //   .catch((error) => {
    //     console.error("An error occurred:", error);
    //   });
      
 
}
// const useTLSearch = (params, config) => {
//   return async () => {
//     const data = await Digit.TLService.search(params, config);
//     const tenant = data?.Licenses?.[0]?.tenantId;
//     const businessIds = data?.Licenses.map((application) => application.applicationNumber);
//     const workflowRes = await Digit.WorkflowService.getAllApplication(tenant, { businessIds: businessIds.join() });
//     return combineResponse(data?.Licenses, workflowRes?.ProcessInstances, data?.Count);
//   };
// };
useEffect(async ()=>{
  //sessionStorage.setItem("DigiLocker.token1","cf87055822e4aa49b0ba74778518dc400a0277e5")
if(window.location.href.includes("code"))
{
  let code =window.location.href.split("=")[1].split("&")[0]
  let TokenReq = {
    code_verifier: sessionStorage.getItem("code_verfier"),
    code: code, module: "PT"
  }
  console.log("token",code,TokenReq,sessionStorage.getItem("code_verfier"))
  const data = await Digit.DigiLockerService.token({TokenReq })
  sessionStorage.setItem("DigiLocker.token1",data?.TokenRes?.access_token)
  //sessionStorage.setItem("DigiLocker.token1",data?.)
  //const data = await Digit.DigiLockerService.token(TokenReq);
  // assessmentMutate(
  //   { TokenReq
  //   },
  //   {
  //     onError: (error, variables) => {
  //       console.log("error:123 ",error)
  //       //setShowToast({ key: "error", action: error?.response?.data?.Errors[0]?.message || error.message, error : {  message:error?.response?.data?.Errors[0]?.code || error.message } });
  //       setTimeout(closeToast, 5000);
  //     },
  //     onSuccess: (data, variables) => {
  //       //sessionStorage.setItem("IsPTAccessDone", data?.Assessments?.[0]?.auditDetails?.lastModifiedTime);
  //     console.log("success",data,isSuccess,variables)
  //     sessionStorage.setItem("DigiLocker.token1","94e648239a5096773d18774fb97b37f00a413587")
        
  //     },
  //   }
  // );
  //console.log("tokenData",data)
  // fetch('https://api.digitallocker.gov.in/public/oauth2/1/token', {
  //   method: 'POST',
  //   mode: 'cors',
  //   headers: {
  //     'Content-Type': 'application/x-www-form-urlencoded',
  //     "Access-Control-Allow-Origin": "*",
  //     "Access-Control-Allow-Methods": "PUT, DELETE,POST"
  //   },
  //   body: new URLSearchParams({
  //     'code': code,
  //     'grant_type': "authorization_code",
  //     'client_id': "AT3053EB6D",
  //     "client_secret": "75fa589aa7c35b89e127",
  //     "redirect_uri": "http://localhost:3000/digit-ui/citizen/pt/property/new-application/info",
  //     "code_verifier": getItemWithExpiry('DigiLocker.codeVerifier')
  //   })
  // }) .then(response =>
  //   response.json().then(data => ({
  //     data: data,

  //   })).then(res => {
  //     console.log("step 1",res)
  //     //code1 = "Bearer " + res.data.access_token
  //     sessionStorage.setItem('DigiLocker.token1', res.data.access_token)
  //     setItemWithExpiry('DigiLocker.token', res.data.access_token, 60);
  //   }))
  
}
},[])
// Function to set data with an expiration time in sessionStorage
function setItemWithExpiry(key, value, expiryMinutes) {
  const now = new Date();
  const expiryTime = now.getTime() + (expiryMinutes * 60 * 1000); // Convert minutes to milliseconds

  const item = {
    value: value,
    expiry: expiryTime
  };

  sessionStorage.setItem(key, JSON.stringify(item));
}

// Function to get data from sessionStorage, checking for expiration
function getItemWithExpiry(key) {
  const itemString = sessionStorage.getItem(key);

  if (!itemString) {
    return null;
  }

  const item = JSON.parse(itemString);
  const now = new Date();

  if (now.getTime() > item.expiry) {
    // Item has expired, remove it
    sessionStorage.removeItem(key);
    return null;
  }

  return item.value;
}

  return (
    <React.Fragment>
      <Card>
        <CardHeader>{!config.isMutation ? t("PT_DOC_REQ_SCREEN_HEADER") : t("PT_REQIURED_DOC_TRANSFER_OWNERSHIP")}</CardHeader>
        <div>
          <CardText className={"primaryColor"}>{t("PT_DOC_REQ_SCREEN_SUB_HEADER")}</CardText>
          <CardText className={"primaryColor"}>{t("PT_DOC_REQ_SCREEN_TEXT")}</CardText>
          <CardText className={"primaryColor"}>{t("PT_DOC_REQ_SCREEN_SUB_TEXT")}</CardText>
          <CardSubHeader>{t("PT_DOC_REQ_SCREEN_LABEL")}</CardSubHeader>
          <CardText className={"primaryColor"}>{t("PT_DOC_REQ_SCREEN_LABEL_TEXT")}</CardText>
          <div>
            {isLoading && <Loader />}
            {Array.isArray(docs)
              ? config?.isMutation ?
                  docs.map(({ code, dropdownData }, index) => (
                    <div key={index}>
                      <CardSubHeader>
                        {index + 1}. {t(code)}
                      </CardSubHeader>
                      <CardText className={"primaryColor"}>
                        {dropdownData.map((dropdownData) => (
                          t(dropdownData?.code)
                        )).join(', ')}
                      </CardText>
                      {/* <CardText>{t(`${code.split('.')[0]}.${code.split('.')[1]}.${code.split('.')[1]}_DESCRIPTION`)}</CardText> */}
                    </div>
                  )) :
                  docs.map(({ code, dropdownData }, index) => (
                    <div key={index}>
                      <CardSubHeader>
                        {index + 1}. {t("PROPERTYTAX_" + stringReplaceAll(code, ".", "_") + "_HEADING")}
                      </CardSubHeader>
                      {dropdownData.map((dropdownData) => (
                        <CardText className={"primaryColor"}>{t("PROPERTYTAX_" + stringReplaceAll(dropdownData?.code, ".", "_") + "_LABEL")}</CardText>
                      ))}
                    </div>
                  ))
              : null}
          </div>
        </div>
        <span>
          <SubmitBar label={t("PT_COMMON_NEXT")} onSubmit={onSelect} />
        </span>
        <span style={{marginTop:"10px"}}>
          <SubmitBar label={t("PT_DIGILOCKER_CONSENT")} onSubmit={(e) => {onConcent(e)}} />
        </span>
      </Card>
    </React.Fragment>
  );
};

export default PropertyTax;
