import _ from "lodash";
import PropTypes from "prop-types";
import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { Loader, UnMaskComponent, PrivacyMaskIcon } from "..";

/**
 * Custom Component to demask the masked values.
 *
 * @author jagankumar-egov
 *
 * Feature :: Privacy
 *
 * @example
 * <WrapUnMaskComponent value={value}  privacy={{ uuid: "", fieldName: "name", model: "User" ,hide: false,loadData = {  serviceName="",requestBody={},requestParam=[],jsonPath="",isArray=false}}} unmaskField={()=>{
 *  function to be called when api response is received after eye icon is clicked
 * }}   />
 */

const formatValue = (showValue) => {
  return (
    showValue && Digit.Utils.locale.stringReplaceAll(showValue, showValue?.substring(showValue?.indexOf("*"), showValue?.lastIndexOf("*") + 1), "")
  );
};

const WrapUnMaskComponent = React.memo(({ privacy = {}, value, unmaskField, ...rem }) => {
  const [privacyState, setPrivacyState] = useState(false);
  const { loadData = {} } = privacy;
  const { t } = useTranslation();
  const isMobile = window.Digit.Utils.browser.isMobile();
  const isEmployee = window.location.href.includes("/employee")

  const requestCriteria = [
    loadData?.serviceName,
    loadData?.requestParam,
    loadData?.requestBody,
    { recordId: privacy?.uuid, plainRequestFields: Array.isArray(privacy?.fieldName) ? privacy?.fieldName : [privacy?.fieldName] },
    {
      enabled: privacyState,
      cacheTime: 100,
      select: (data) => {
        if (loadData?.d) {
          let unmaskeddata = loadData?.d(data, value);
          if (rem?.setunmaskedNumber)
            rem?.setunmaskedNumber(unmaskeddata);
          return unmaskeddata;
        }
        return unmaskField ? unmaskField(_.get(data, loadData?.jsonPath, value)) : _.get(data, loadData?.jsonPath, value);
      },
    },
  ];
  const { isLoading, data, revalidate } = Digit.Hooks.useCustomAPIHook(...requestCriteria);

  useEffect(() => {
    return () => {
      revalidate();
      setPrivacyState(false);
    };
  });
  if (isLoading) {
    return !unmaskField ? (
      <Loader />
    ) : (
      <span className={isMobile && isEmployee ? "unmask-container--mobile" : "unmask-container--desktop"}>
        <div className={`tooltip`}>
          <PrivacyMaskIcon className="cursor-pointer-default" style={rem?.style}></PrivacyMaskIcon>
        </div>
      </span>
    );
  }

  return privacy?.uuid && data ? (
    <React.Fragment>
      {!unmaskField && !loadData?.oldValue && t(data)}
      {!unmaskField && loadData?.oldValue && data}
      {!unmaskField && privacy?.showValue && formatValue(value)}
    </React.Fragment>
  ) : (
    <React.Fragment>
      {!unmaskField && value}
      {privacy && (
        <span className="rc-status-table-spacing">
          <UnMaskComponent
            privacy={privacy}
            unmaskData={() => {
              privacy?.uuid && loadData && setPrivacyState(true);
            }}
            {...rem}
          ></UnMaskComponent>
        </span>
      )}
    </React.Fragment>
  );
});

WrapUnMaskComponent.propTypes = {
  privacy: PropTypes.object,
};
WrapUnMaskComponent.defaultProps = {
  privacy: { uuid: "", fieldName: "", model: "" },
};

export default WrapUnMaskComponent;
