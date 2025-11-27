import React, { useState, useCallback, useMemo , useEffect} from "react";
import { useTranslation } from "react-i18next";
import { Header, Card, Table, Loader } from "@upyog/digit-ui-react-components";
import SearchProcurementList from "../../components/SearchProcurementList";

const ProcurementList = ({ parentRoute }) => {
  const { t } = useTranslation();
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const [payload, setPayload] = useState({});
  const [showToast, setShowToast] = useState(null);
 
  const config = {
    enabled: !!(payload && Object.keys(payload).length > 0),
  };

  const {  isSuccess, isError, data: { ProcurementRequests: searchReult, Count: count } = {} } = Digit.Hooks.asset.useProcurementList(
    { tenantId, filters: {} },
    config
  );

  function onSubmit (_data) {
    setPayload({randomId: Math.random()});
  }

  // Call onSubmit only once when component mounts
  useEffect(() => {
    onSubmit();
  }, []);

  //toaster msg with
  useEffect(() => {
    if (showToast) {
      const timer = setTimeout(() => {
        setShowToast(null);
      }, 1500); // Close toast after 1.5 seconds

      return () => clearTimeout(timer); // Clear timer on cleanup
    }
  }, [showToast]);

  

  if (false) return <Loader />;

  return <React.Fragment>
        <SearchProcurementList t={t} isLoading={false} parentRoute={parentRoute} 
        tenantId={tenantId} setShowToast={setShowToast} onSubmit={onSubmit} 
        data={  isSuccess ? (searchReult.length>0? searchReult : { display: "ES_COMMON_NO_DATA" } ):""} count={count} /> 
        {showToast && (
        <Toast
          error={showToast.error}
          warning={showToast.warning}
          label={t(showToast.label)}
          isDleteBtn={true}
          onClose={() => {
            setShowToast(null);
          }}
        />
      )}
    </React.Fragment>
};

export default ProcurementList;
