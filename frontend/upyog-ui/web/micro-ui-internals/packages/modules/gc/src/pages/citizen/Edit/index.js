import { Loader, Timeline } from "@nudmcdgnpm/digit-ui-react-components";
import React, { useEffect, useMemo } from "react";
import { useTranslation } from "react-i18next";
import { useQueryClient } from "@tanstack/react-query";
import { Navigate, Route, Routes, useLocation, useParams } from "react-router-dom";
import { commonConfig } from "../../../config/Create/citizenconfig";
import { GCAPIToFormData } from "../../../utils";
import CheckPage from "../Create/CheckPage";
import GCAcknowledgement from "../Create/GCAcknowledgement";

/**
 * GC Edit Wizard
 * Flow:
 *  - Read applicationNo from URL
 *  - Search existing application
 *  - Pre-fill wizard (using session storage)
 *  - Submit updated payload via GC update service
 */
const GCEdIt = () => {
  const queryClient = useQueryClient();
  const { t } = useTranslation();
  const { pathname } = useLocation();
  const navigate = Digit.Hooks.useCustomNavigate();
  const stateId = Digit.ULBService.getStateId();

  const { applicationNo: applicationNoParam } = useParams();
  const applicationNo = useMemo(
    () => decodeURIComponent(applicationNoParam || ""),
    [applicationNoParam]
  );

  const tenantId =
    Digit.ULBService.getCitizenCurrentTenant(true) || Digit.ULBService.getCurrentTenantId();

  const { data: commonFieldsData, isLoading: isCommonLoading } = Digit.Hooks.pt.useMDMS(
    stateId,
    "PropertyTax",
    "CommonFieldsConfig"
  );

  const { data: gcData, isLoading: isSearchLoading } = Digit.Hooks.gc.useGCSearch({
    tenantId,
    data: {
      searchCriteriaGarbageAccount: {
        applicationNumber: [applicationNo],
      },
    },
    filters: { applicationNumber: [applicationNo] },
  }, { enabled: !!applicationNo, cacheTime: 0, staleTime: 0 });

  const applicationList = gcData?.garbageAccounts || gcData?.GarbageApplications || gcData?.data || [];
  const application = applicationList.length > 0 ? applicationList[0] : null;


  // IMPORTANT: keep wizard logic identical to Create, but with a different session storage key
  const [params, setParams, clearParams] = Digit.Hooks.useSessionStorage("GC_Edit", {});

  const config = useMemo(() => {
    let c = [];
    commonConfig.forEach((obj) => {
      c = c.concat(obj.body.filter((a) => !a.hideInCitizen));
    });
    c.indexRoute = "applicant-details";
    return c;
  }, []);

  const goNext = (skipStep, index, isAddMultiple, key) => {
    let currentPath = pathname.split("/").pop(),
      lastchar = currentPath.charAt(currentPath.length - 1),
      isMultiple = false,
      nextPage;

    if (Number.parseInt(currentPath)) {
      isMultiple = false;
    }
    if (Number(parseInt(currentPath)) || currentPath == "0" || currentPath == "-1") {
      if (currentPath == "-1" || currentPath == "-2") {
        currentPath = pathname.slice(0, -3);
        currentPath = currentPath.split("/").pop();
        isMultiple = true;
      } else {
        currentPath = pathname.slice(0, -2);
        currentPath = currentPath.split("/").pop();
        isMultiple = true;
      }
    } else {
      isMultiple = false;
    }
    if (!Number.isNaN(lastchar)) {
      isMultiple = true;
    }

    let { nextStep = {} } = config.find((routeObj) => routeObj.route === currentPath);

    let redirectWithHistory = (to, state) => navigate(to, state != null ? { state } : undefined);
    if (skipStep) {
      redirectWithHistory = (to, state) =>
        navigate(to, state != null ? { replace: true, state } : { replace: true });
    }

    if (isAddMultiple) {
      nextStep = key;
    }

    if (nextStep === null) {
      return redirectWithHistory(`check`);
    }

    if (!Number.isNaN(nextStep.split("/").pop())) {
      nextPage = `${nextStep}`;
    } else {
      nextPage = isMultiple && nextStep !== "map" ? `${nextStep}/${index}` : `${nextStep}`;
    }

    redirectWithHistory(nextPage);
  };

  const initialized = React.useRef(false);

  // useEffect(() => {
  //   if (!application || initialized.current) return;

  //   initialized.current = true;

  //   const formData = GCAPIToFormData(application, params);

  //   Digit.SessionStorage.set("GC_Edit", formData);
  //   setParams(formData);
  // }, [application, setParams]);

  const handleSubmit = async () => {
    try {
      const formdata = GCAPIToFormData(application, params);
      const applicationStatus =
        application?.applicationStatus || application?.status || application?.grbgApplication?.status;

      // Persist the edited form first. The garbage-service deliberately treats
      // workflow-only requests separately and reloads the account from storage.
      const payload = { garbageAccounts: [formdata] };

      await Digit.GCServices.update(payload, tenantId);

      if (applicationStatus === "EDIT_APPLICATION") {
        const workflowPayload = {
          garbageAccounts: [
            {
              ...formdata,
              workflowAction: "EDIT",
              workflowComment: "Application resubmitted by citizen",
              isOnlyWorkflowCall: true,
            },
          ],
        };

        await Digit.GCServices.update(workflowPayload, tenantId);
      }

      clearParams();
      queryClient.invalidateQueries("GC_Edit");

      navigate("acknowledgement", {
        state: {
          data: payload,
          isSuccess: true,
        },
      });
    } catch (error) {
      console.error("GC edit update error:", error);
      navigate("acknowledgement", {
        state: {
          data: null,
          isSuccess: false,
          error,
        },
      });
    }
  };

  function handleSelect(key, data, skipStep, index, isAddMultiple = false) {
    let nextParams;
    if (key === "owner") {
      nextParams = {
        ...params,
        owner: { ...params.owner, ...data },
      };
    } else if (key === "owners") {
      const owners = [...(params.owners || [])];
      owners[index] = data;
      nextParams = { ...params, owners };
    } else if (key === "units") {
      nextParams = { ...params, units: data };
    } else {
      nextParams = {
        ...params,
        [key]: { ...params[key], ...data },
      };
    }

    // Persist synchronously. Navigation immediately after this handler must not
    // depend on React having processed the queued state update.
    Digit.SessionStorage.set("GC_Edit", nextParams);
    setParams(nextParams);
    goNext(skipStep, index, isAddMultiple, key);
  }

  const isLoading = isCommonLoading || isSearchLoading;
  if (isLoading) return <Loader />;

  if (!application) {
    return <div>{t("GC_APPLICATION_NOT_FOUND")}</div>;
  }

  return (
    <>
      <Timeline config={config} />
      <Routes>
        {config.map((routeObj, index) => {
          const { component, texts, inputs, key, additionaFields } = routeObj;
          const Component =
            typeof component === "string"
              ? Digit.ComponentRegistryService.getComponent(component)
              : component;

          return (
            <Route
              path={`${routeObj.route}`}
              key={index}
              element={<Component config={{ texts, inputs, key, additionaFields }} onSelect={handleSelect} t={t} formData={params} renewApplication={application} />}
            />
          );
        })}

        <Route path="check/*" element={<CheckPage onSubmit={handleSubmit} value={params} renewApplication={application} />} />
        <Route path="acknowledgement/*" element={<GCAcknowledgement />} />
        <Route path="/*" element={<Navigate to={`${config.indexRoute}`} replace />} />
      </Routes>
    </>
  );
};

export default GCEdIt;
