import { useCallback, useMemo } from "react";
import { useQueryClient } from "@tanstack/react-query";
import { mergeSessionStepWithRouteConfig } from "@nudmcdgnpm/digit-ui-react-components";
import { useLocation } from "react-router-dom";
import {
  buildWizardSteps,
  createWizardGoNext,
  getWizardBasePath,
} from "./estWizardUtils";

/**
 * Shared MDMS wizard router state for EST flows.
 */
const useEstWizard = ({
  mdmsData,
  isLoading,
  indexRoute,
  sessionKey,
  terminalSegments,
  multiStepNavigation = true,
  invalidateQueryKey,
  checkFlow,
  buildSuccessAckState,
}) => {
  const location = useLocation();
  const { pathname } = location;
  const navigate = Digit.Hooks.useCustomNavigate();
  const match = Digit.Hooks.useModuleBasePath();
  const queryClient = useQueryClient();

  const [params, setParams, clearParams] = Digit.Hooks.useSessionStorage(sessionKey, {});

  const config = useMemo(
    () => buildWizardSteps(mdmsData, indexRoute),
    [mdmsData, indexRoute]
  );

  const getBasePath = useCallback(
    () => getWizardBasePath(pathname, match?.pathnameBase, terminalSegments),
    [pathname, match, terminalSegments]
  );

  const goNext = useCallback(
    createWizardGoNext({ pathname, config, navigate, multiStep: multiStepNavigation }),
    [pathname, config, navigate, multiStepNavigation]
  );

  const handleSelect = useCallback(
    (key, data, skipStep, index, isAddMultiple = false) => {
      setParams((prev) => mergeSessionStepWithRouteConfig(prev, key, data));
      goNext(skipStep, index, isAddMultiple, key);
    },
    [setParams, goNext]
  );

  const onAckSuccess = useCallback(() => {
    clearParams();
    if (invalidateQueryKey) queryClient.invalidateQueries(invalidateQueryKey);
  }, [clearParams, queryClient, invalidateQueryKey]);

  const onCheckSuccess = useCallback(
    (response) => {
      // Build ack payload from session BEFORE clearing — PDF needs routeConfigs + form values.
      // Never let ack-builder failures flip a successful API (e.g. 201) into a failure banner.
      let ackState = { data: response, isSuccess: true };
      try {
        if (buildSuccessAckState) {
          ackState = buildSuccessAckState(response, params);
          if (!ackState || typeof ackState !== "object") {
            ackState = { data: response, isSuccess: true };
          }
          ackState = { ...ackState, isSuccess: true };
        }
      } catch (err) {
        console.error("EST ack state build failed after successful submit:", err);
        ackState = { data: response, isSuccess: true, ackBuildError: String(err?.message || err) };
      }

      clearParams();
      if (invalidateQueryKey) queryClient.invalidateQueries(invalidateQueryKey);
      navigate(`${getBasePath()}/acknowledgement`, { state: ackState });
    },
    [clearParams, queryClient, invalidateQueryKey, buildSuccessAckState, params, navigate, getBasePath]
  );

  const onCheckError = useCallback(
    (error) => {
      // HTTP 2xx that still rejected (rare) — treat as success if body has allotment/asset data.
      const status = error?.response?.status;
      const body = error?.response?.data;
      if (status >= 200 && status < 300) {
        console.warn("EST submit treated error with 2xx status as success", status, error);
        const ackState = buildSuccessAckState
          ? (() => {
              try {
                return { ...buildSuccessAckState(body, params), isSuccess: true };
              } catch {
                return { data: body, isSuccess: true };
              }
            })()
          : { data: body, isSuccess: true };
        clearParams();
        navigate(`${getBasePath()}/acknowledgement`, { state: ackState });
        return;
      }

      navigate(`${getBasePath()}/acknowledgement`, {
        state: {
          data: null,
          isSuccess: false,
          error: {
            message: error?.message || "EST_APPLICATION_FAILED",
            status,
          },
        },
      });
    },
    [navigate, getBasePath, buildSuccessAckState, params, clearParams]
  );

  const isReady = !isLoading && mdmsData && config.length > 0;

  return {
    config,
    params,
    setParams,
    clearParams,
    location,
    match,
    getBasePath,
    goNext,
    handleSelect,
    onAckSuccess,
    onCheckSuccess,
    onCheckError,
    checkFlow,
    isReady,
  };
};

export default useEstWizard;
