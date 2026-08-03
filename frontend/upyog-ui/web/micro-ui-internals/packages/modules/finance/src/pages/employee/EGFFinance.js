import React, { useEffect, useRef, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import { Loader } from "@nudmcdgnpm/digit-ui-react-components";

/**
 * EGFFinance – embeds the upyog Finance (ERP/EGF) application inside
 * an iframe. Auth credentials are passed via a hidden HTML POST form so the
 * finance app can authenticate without an additional login step.
 *
 * Ported from:
 *   frontend/mono-ui/web/rainmaker/packages/employee/src/modules/employee/Erp/EGF/index.js
 *
 * Changes from the original class component:
 *  - Converted to a functional component using React hooks
 *  - Auth token: `Digit.UserService.getUser()?.access_token` (replaces egov-ui-kit util)
 *  - Tenant ID: `Digit.ULBService.getCurrentTenantId()`   (replaces egov-ui-kit util)
 *  - Navigation: `useNavigate()` from react-router-dom v6   (replaces `this.props.history`)
 *  - Pathname:   `useLocation().pathname`                    (replaces `this.props.location`)
 */
const EGFFinance = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const iframeRef = useRef(null);
  const formRef = useRef(null);
  const [isLoading, setIsLoading] = useState(true);

  const user = Digit.UserService.getUser();
  const auth_token = user?.access_token || "";
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const locale = localStorage.getItem("locale");
  // Extract the service path from the route dynamically stripping any upyog-ui or employee prefixes
  const isUpyog = location.pathname.includes("/upyog-ui");
  const prefix = isUpyog ? "/upyog-ui/employee" : "/employee";
  let menuUrl = location.pathname.startsWith(prefix)
    ? location.pathname.substring(prefix.length)
    : location.pathname;
  if (menuUrl.startsWith("/finance")) {
    menuUrl = menuUrl.substring("/finance".length);
  }

  const loc = window.location;
  const hostname = loc.hostname;
  
  let erp_url;
  if (hostname === "localhost" || hostname === "127.0.0.1") {
    erp_url = loc.protocol + "//" + loc.host + menuUrl;
  } else {
    const domainurl = hostname.substring(hostname.indexOf(".") + 1);
    const globalConfigExists =
      typeof window.globalConfigs !== "undefined" &&
      typeof window.globalConfigs.getConfig === "function";
    const finEnv = globalConfigExists
      ? window.globalConfigs.getConfig("FIN_ENV")
      : "qa";
    const subdomainurl = finEnv ? finEnv + "." + domainurl : domainurl;
    erp_url = loc.protocol + "//" + subdomainurl + menuUrl;
  }

  const winheight = window.innerHeight - 100;

  const getSubdomain = () => {
    const h = window.location.hostname;
    return h.substring(h.indexOf(".") + 1);
  };

  const setLocaleCookie = () => {
    const isSecure = window.location.protocol === "https:";
    let localeCookie =
      "locale=" + locale + ";path=/;domain=." + getSubdomain();
    if (isSecure) {
      localeCookie += ";secure";
    }
    window.document.cookie = localeCookie;
  };

  const submitForm = () => {
    if (formRef.current) {
      formRef.current.submit();
    }
  };

  const showIframe = () => {
    setIsLoading(false);
    if (iframeRef.current) {
      iframeRef.current.style.display = "block";
    }
  };

  // postMessage handler: finance app sends "close" to return to inbox
  const onMessage = (event) => {
    if (event.data !== "close") return;
    navigate("/upyog-ui/employee/inbox");
  };

  // Reset iframe on locale change
  const resetIframe = () => {
    setLocaleCookie();
    submitForm();
  };

  useEffect(() => {
    const iframe = iframeRef.current;

    // Listen for postMessage events from the finance iframe
    window.addEventListener("message", onMessage, false);
    // Listen for locale change events from upyog-ui
    window.addEventListener("loacaleChangeEvent", resetIframe, false);
    // Show the iframe once it has loaded content
    if (iframe) {
      iframe.addEventListener("load", showIframe);
    }

    return () => {
      window.removeEventListener("message", onMessage, false);
      window.removeEventListener("loacaleChangeEvent", resetIframe, false);
      if (iframe) {
        iframe.removeEventListener("load", showIframe);
      }
    };
  }, []);

  // Submit the POST form every time the component updates (location change, locale change)
  useEffect(() => {
    setIsLoading(true);
    if (iframeRef.current) {
      iframeRef.current.style.display = "none";
    }
    setLocaleCookie();
    submitForm();
  }, [menuUrl, locale]);

  return (
    <div>
      {isLoading && <Loader />}
      {/* The iframe is hidden until the finance app has finished loading */}
      <iframe
        ref={iframeRef}
        name="erp_iframe"
        id="erp_iframe"
        height={winheight}
        width="100%"
        style={{ display: "none", border: "none" }}
        title="EGF Finance"
      />

      {/*
        Hidden POST form — submits credentials into the iframe so the finance
        application can establish an authenticated session without a separate login.
      */}
      <form
        ref={formRef}
        action={erp_url}
        id="erp_form"
        method="post"
        target="erp_iframe"
      >
        <input readOnly hidden name="auth_token" value={auth_token} />
        <input readOnly hidden name="tenantId" value={tenantId} />
        <input readOnly hidden name="locale" value={locale} />
        <input readOnly hidden name="formPage" value="true" />
      </form>
    </div>
  );
};

export default EGFFinance;
