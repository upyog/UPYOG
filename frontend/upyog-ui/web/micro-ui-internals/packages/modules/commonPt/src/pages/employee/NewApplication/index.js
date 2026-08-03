import React from "react";

import { Loader } from "@nudmcdgnpm/digit-ui-react-components";

import { useTranslation } from "react-i18next";

import { useQueryClient } from "@tanstack/react-query";

import {
  Routes,
  Route,
  useNavigate,
  useLocation,
  useMatch,
} from "react-router-dom";

import CreatePropertyForm from "../../pageComponents/createForm";
import PTAcknowledgement from "../../pageComponents/PTAcknowledgement";

const NewApplication = () => {
  const queryClient = useQueryClient();

  const navigate = useNavigate();

  const location = useLocation();

  const stateId = Digit.ULBService.getStateId();

  const tenantId = Digit.ULBService.getCurrentTenantId();

  /*
   * Store partially completed property creation form data in session storage.
   * This allows users to retain entered values while navigating between
   * property creation steps (for example, create form -> acknowledgement).
   */

  const [params, setParams, clearParams] = Digit.Hooks.useSessionStorage(
    "PT_CREATE_PROPERTY",
    {},
  );

  /*
   * Fetch common property form configurations from MDMS.
   * These configurations decide the dynamic fields displayed during
   * property creation.
   */

  const { data: commonFields, isLoading } = Digit.Hooks.pt.useMDMS(
    stateId,
    "PropertyTax",
    "CommonFieldsConfig",
  );

  /*
   * Fetch redirect URL if the property creation flow was initiated
   * from another module/page.
   */

  const redirectUrl = new URLSearchParams(location.search).get("redirectToUrl");

  /*
   * Navigate to acknowledgement page after successful property creation.
   * The acknowledgement component will handle final submission status.
   */

  const createProperty = () => {
    navigate("acknowledgement");
  };
  /*
   * Clear temporary session data after successful acknowledgement
   * and invalidate related queries so stale property creation data
   * is not reused.
   */

  const onSuccess = () => {
    clearParams();

    queryClient.invalidateQueries({
      queryKey: ["PT_CREATE_PROPERTY"],
    });
  };

  if (isLoading) {
    return <Loader />;
  }

  return (
    <Routes>
      {/*
        Default route renders the property creation form.
        Saved form values are passed from session storage so users
        can continue their unfinished application.
      */}

      <Route
        path="*"
        element={
          <CreatePropertyForm
            onSubmit={createProperty}
            value={params}
            redirectUrl={redirectUrl}
            userType="employee"
          />
        }
      />

      {/*
        After property creation, show acknowledgement page.
        onSuccess removes temporary stored data after completion.
      */}

      <Route
        path="save-property"
        element={
          <PTAcknowledgement
            data={params}
            onSuccess={onSuccess}
            redirectUrl={redirectUrl}
            userType="employee"
          />
        }
      />
    </Routes>
  );
};

export default NewApplication;
