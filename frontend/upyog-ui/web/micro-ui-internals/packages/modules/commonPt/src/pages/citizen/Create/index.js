import { Loader } from "@nudmcdgnpm/digit-ui-react-components";
import React from "react";
import { useTranslation } from "react-i18next";
import { useQueryClient } from "@tanstack/react-query";
import { Routes, Route, useLocation } from "react-router-dom";
import CreatePropertyForm from "../../pageComponents/createForm";
import PTAcknowledgement from "../../pageComponents/PTAcknowledgement";

const CreateProperty = ({ parentRoute, onSelect }) => {
  const queryClient = useQueryClient();
  const { t } = useTranslation();
  const location = useLocation();
  const navigate = Digit.Hooks.useCustomNavigate();
  const stateId = Digit.ULBService.getStateId();

  const [params, setParams, clearParams] = Digit.Hooks.useSessionStorage(
    "PT_CREATE_PROPERTY",
    {}
  );
  let { data: commonFields, isLoading } = Digit.Hooks.pt.useMDMS(
    stateId,
    "PropertyTax",
    "CommonFieldsConfig"
  );

  const search = location.search;
  const redirectUrl = new URLSearchParams(search).get("redirectUrl");

  const createProperty = async () => {
    // navigate to save-property so mutation runs
    navigate("save-property", { state: { data: params } });
  };

  const onSuccess = () => {
    clearParams();
    queryClient.invalidateQueries({ queryKey: ["PT_CREATE_PROPERTY"] });
  };

  if (isLoading) {
    return <Loader />;
  }

  return (
    <Routes>
      <Route
        path=""
        element={
          <CreatePropertyForm
            onSubmit={createProperty}
            value={params}
            userType="citizen"
          />
        }
      />
      <Route
        path="save-property"
        element={
          <PTAcknowledgement
            data={params}
            onSuccess={onSuccess}
            redirectUrl={redirectUrl}
            userType="citizen"
          />
        }
      />
    </Routes>
  );
};

export default CreateProperty;
