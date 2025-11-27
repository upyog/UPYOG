import React, { useEffect, useState } from "react";
import { Card, Banner, CardText, SubmitBar, Loader, LinkButton, Toast, ActionBar } from "@upyog/digit-ui-react-components";
import { Link, useHistory } from "react-router-dom";
import { useTranslation } from "react-i18next";
import { useQueryClient } from "react-query";



const GetVendorMessage = (type, action, isSuccess, isEmployee, t) => {
  return t(`${isEmployee ? "IN" : "C"}V_VENDOR_RESPONSE_${action ? action : "EDIT_SUCCESS"}_${type}${isSuccess ? "" : "_ERROR"}`);
};

const GetVendorActionMessage = (action, isSuccess, isEmployee, t) => {
  return GetVendorMessage("ACTION", action, isSuccess, isEmployee, t);
};

const GetVendorLabel = (action, isSuccess, isEmployee, t) => {
  return GetVendorMessage("LABEL", action, isSuccess, isEmployee, t);
};

const BannerPicker = (props) => {
  return (
    <Banner
      message={GetVendorActionMessage(props.action, props.isSuccess, props.isEmployee, props.t)}
      applicationNumber={props?.data?.Vendors?.[0]?.vendorId}
       info={props.isSuccess ? props.t("INV_VENDOR_LABEL") : ""}
      successful={props.isSuccess}
    />
  );
};

const InventoryResponse = (props) => {
  const { t } = useTranslation();
  const queryClient = useQueryClient();
  const history = useHistory();
 ;
  const [enableAudit, setEnableAudit] = useState(false);
  const [mutationHappened, setMutationHappened, clear] = Digit.Hooks.useSessionStorage("EMPLOYEE_MUTATION_HAPPENED", false);
  const [successData, setsuccessData, clearSuccessData] = Digit.Hooks.useSessionStorage("EMPLOYEE_MUTATION_SUCCESS_DATA", false);

  const tenantId = Digit.ULBService.getCurrentTenantId();
  const { state } = props.location;

  const mutation = Digit.Hooks.asset.useInventoryApplication(tenantId,false);

  useEffect(() => {
    const onSuccess = async (successRes) => {
      setMutationHappened(true);
      queryClient.clear();
      if (successRes?.ResponseInfo?.status === "successful") {
        setEnableAudit(true);
      }
    };
    const onError = (error, variables) => {
      setShowToast({ key: "error" });
      setError(error?.response?.data?.Errors[0]?.message || null);
    };

      mutation.mutate(
        {
          Vendor: state?.Vendor,
        },
        {
          onError,
          onSuccess,
        }
      );
  }, []);


  if (mutation.isLoading || (mutation.isIdle && !mutationHappened)) {
    return <Loader />;
  }

  return (
    <div>
      <Card>
        <BannerPicker
          t={t}
          data={mutation?.data || successData}
          action={state?.action}
          isSuccess={!Object.keys(successData || {}).length ? mutation?.isSuccess : true}
          isLoading={(mutation.isIdle && !mutationHappened) || mutation?.isLoading}
          isEmployee={props.parentRoute.includes("employee")}
        />
       
      </Card>
      
    </div>
  );
};

export default InventoryResponse;