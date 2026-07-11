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
      clearParams();
      if (invalidateQueryKey) queryClient.invalidateQueries(invalidateQueryKey);
      const ackState = buildSuccessAckState
        ? buildSuccessAckState(response, params)
        : { data: response, isSuccess: true };
      navigate(`${getBasePath()}/acknowledgement`, { state: ackState });
    },
    [clearParams, queryClient, invalidateQueryKey, buildSuccessAckState, params, navigate, getBasePath]
  );

  const onCheckError = useCallback(
    (error) => {
      navigate(`${getBasePath()}/acknowledgement`, {
        state: { data: null, isSuccess: false, error },
      });
    },
    [navigate, getBasePath]
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
