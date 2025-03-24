import { useInitStore } from "./store";
import useWorkflowDetails from "./workflow";
import useSessionStorage from "./useSessionStorage";
import useQueryParams from "./useQueryParams";
import useDocumentSearch from "./useDocumentSearch";
import useClickOutside from "./useClickOutside";
import useAudit from "./core/useAudit";
import {
  useFetchPayment,
  usePaymentUpdate,
  useFetchCitizenBillsForBuissnessService,
  useFetchBillsForBuissnessService,
  useGetPaymentRulesForBusinessServices,
  useDemandSearch,
  useRecieptSearch,
  useRecieptSearchNew,
  useAssetQrCode,
  // usePaymentSearch,
  useBulkPdfDetails,
} from "./payment";
import { useUserSearch } from "./userSearch";
import { useApplicationsForBusinessServiceSearch } from "./useApplicationForBillSearch";
import useBoundaryLocalities from "./useLocalities";
import useCommonMDMS from "./useMDMS";
import useCommonMDMSV2 from "./useMDMSV2";
import useCustomMDMS from "./useCustomMDMS";
import useCustomMDMSV2 from "./useCustomMDMSV2";
import useEnabledMDMS from "./useEnabledMDMS";
import useCustomAPIHook from "./useCustomAPIHook";
// import useInboxGeneral from "./useInboxGeneral/useInboxGeneral";
import useApplicationStatusGeneral from "./useStatusGeneral";
import useModuleTenants from "./useModuleTenants";
import useStore from "./useStore";
import { useTenants } from "./useTenants";
import { useEvents, useClearNotifications, useNotificationCount } from "./events";
import useNewInboxGeneral from "./useInboxGeneral/useNewInbox";
import useDynamicData from "./useDynamicData";
import useAccessControl from "./useAccessControl";
import useBillSearch from "./bills/useBillSearch";
import useCancelBill from "./bills/useCancelBill";
import useTenantsBills from "./bills/useTenants";
import useGetHowItWorksJSON from "./useHowItWorksJSON";
import useGetFAQsJSON from "./useGetFAQsJSON";
// import useGetDSSFAQsJSON from "./useGetDSSFAQsJSON";
// import useGetDSSAboutJSON from "./useGetDSSAboutJSON";
import useStaticData from "./useStaticData";
import useBillAmendmentInbox from "./billAmendment/useInbox";
import { usePrivacyContext } from "./usePrivacyContext";
// import useFeedBackSearch from "./useFeedBackSearch";
import createTokenAPI from "./digiLockerApi/createTokenAPI";

import { useCustomBackNavigation } from "./UseCustomBackNavigationProps";
import useSelectedMDMS from "./useSelectedMDMS";
import useEmployeeSearch from "./useEmployeeSearch";
import useRouteSubscription from "./useRouteSubscription";
import useCndCreateApi from "./cnd/useCndCreateApi";
import useCndSearchApplication from "./cnd/useCndSearchApplication";
import useCndApplicationDetails from "./cnd/useCndApplicationDetails";
import useCndApplicationAction from "./cnd/useCndApplicationAction";

const cnd ={
  useCndCreateApi,
  useCndSearchApplication,
  useCndApplicationDetails,
  useCndApplicationAction
}

const Hooks = {
  useSessionStorage,
  useQueryParams,
  useFetchPayment,
  useAssetQrCode,
  usePaymentUpdate,
  useFetchCitizenBillsForBuissnessService,
  useFetchBillsForBuissnessService,
  useGetPaymentRulesForBusinessServices,
  useWorkflowDetails,
  useInitStore,
  useClickOutside,
  useUserSearch,
  useApplicationsForBusinessServiceSearch,
  useDemandSearch,
  // useInboxGeneral,
  useEmployeeSearch,
  useBoundaryLocalities,
  useCommonMDMS,
  useCommonMDMSV2,
  useApplicationStatusGeneral,
  useModuleTenants,
  useRecieptSearch,
  useRecieptSearchNew,
  // usePaymentSearch,
  useNewInboxGeneral,
  useEvents,
  useClearNotifications,
  useNotificationCount,
  useStore,
  useDocumentSearch,
  useTenants,
  // useFeedBackSearch,
  useAccessControl,
  useBillSearch,
  useCancelBill,
  useTenantsBills,
  usePrivacyContext,
  useCustomMDMS,
  useCustomMDMSV2,
  useEnabledMDMS,
  useSelectedMDMS,
  useCustomAPIHook,
  useGetHowItWorksJSON,
  useGetFAQsJSON,
  // useGetDSSFAQsJSON,
  // useGetDSSAboutJSON,
  useStaticData,
  useDynamicData,
  useBulkPdfDetails,
  useBillAmendmentInbox,
  useAudit,
  createTokenAPI,
  useCustomBackNavigation,
  useRouteSubscription,
  cnd
};

export default Hooks;