import { useInitStore } from "./store";
import useAccessControl from "./useAccessControl";
import useClickOutside from "./useClickOutside";
import useCustomMDMS from "./useCustomMDMS";
import useDynamicData from "./useDynamicData";
import useLocation from "./useLocation";

import useBoundaryLocalities from "./useLocalities";
import useCommonMDMS from "./useMDMS";
import useWorkflowDetailsV2 from "./useWorkflowDetailsV2";
import useModuleTenants from "./useModuleTenants";
import useQueryParams from "./useQueryParams";
import useRouteSubscription from "./useRouteSubscription";
import useSessionStorage from "./useSessionStorage";
import useStore from "./useStore";
import { useTenants } from "./useTenants";
import useWorkflowDetails from "./workflow";
import useCustomAPIHook from "./useCustomAPIHook";
import useCustomAPIMutationHook from "./useCustomAPIMutationHook";
import useUpdateCustom from "./useUpdateCustom";
import useEmployeeSearch from "./useEmployeeSearch";
import useGetFAQsJSON from "./useGetFAQsJSON";
import useGetHowItWorksJSON from "./useHowItWorksJSON";
import { usePrivacyContext } from "./usePrivacyContext";
import useStaticData from "./useStaticData";
import useCustomNavigate from "./useCustomNavigate";
import useGenderMDMS from "./useGenderMDMS";


const Hooks = {
  useSessionStorage,
  useQueryParams,
  useWorkflowDetails,
  useInitStore,
  useClickOutside,
  useEmployeeSearch,
  useBoundaryLocalities,
  useCommonMDMS,
  useModuleTenants,
  useStore,
  useTenants,
  useAccessControl,
  usePrivacyContext,
  useGenderMDMS,
  useRouteSubscription,
  useCustomAPIHook,
  useCustomAPIMutationHook,
  useWorkflowDetailsV2,
  useUpdateCustom,
  useCustomMDMS,
  useGetHowItWorksJSON,
  useGetFAQsJSON,
  useStaticData,
  useDynamicData,
  useLocation,
  useCustomNavigate
};

export default Hooks;
