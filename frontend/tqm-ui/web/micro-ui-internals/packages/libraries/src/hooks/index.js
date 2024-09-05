import { useInitStore } from './store';
import useWorkflowDetails from './workflow';
import useSessionStorage from './useSessionStorage';
import useQueryParams from './useQueryParams';
import useDocumentSearch from './useDocumentSearch';
import useClickOutside from './useClickOutside';
import useLocation from "./useLocation";
import {
  useFetchPayment,
  usePaymentUpdate,
  useFetchCitizenBillsForBuissnessService,
  useFetchBillsForBuissnessService,
  useGetPaymentRulesForBusinessServices,
  useDemandSearch,
  useRecieptSearch,
  usePaymentSearch,
  useBulkPdfDetails,
} from './payment';
import useWorkflowDetailsFSM from './workflowWorks.js';
import { useUserSearch } from './userSearch';
import { useApplicationsForBusinessServiceSearch } from './useApplicationForBillSearch';
import useBoundaryLocalities from './useLocalities';
import useCommonMDMS from './useMDMS';
import useCustomMDMS from './useCustomMDMS';
import useInboxGeneral from './useInboxGeneral/useInboxGeneral';
import useApplicationStatusGeneral from './useStatusGeneral';
import useModuleTenants from './useModuleTenants';
import useStore from './useStore';
import { useTenants } from './useTenants';
import {
  useEvents,
  useClearNotifications,
  useNotificationCount,
} from './events';
import useCreateEvent from './events/useCreateEvent';
import useUpdateEvent from './events/useUpdateEvent';
import useNewInboxGeneral from './useInboxGeneral/useNewInbox';

import useEmployeeSearch from './useEmployeeSearch';

import useDssMdms from './dss/useMDMS';
import useDashboardConfig from './dss/useDashboardConfig';
import useDSSDashboard from './dss/useDSSDashboard';
import useGetChart from './dss/useGetChart';

import useHRMSSearch from './hrms/useHRMSsearch';
import useHrmsMDMS from './hrms/useHRMSMDMS';
import useHRMSCreate from './hrms/useHRMScreate';
import useHRMSUpdate from './hrms/useHRMSUpdate';
import useHRMSCount from './hrms/useHRMSCount';
import useHRMSGenderMDMS from './hrms/useHRMSGender';

import useEventInbox from './events/useEventInbox';
import useEventDetails from './events/useEventDetails';
import { useEngagementMDMS } from './engagement/useMdms';
import useDocSearch from './engagement/useSearch';
import useDocCreate from './engagement/useCreate';
import useDocUpdate from './engagement/useUpdate';
import useDocDelete from './engagement/useDelete';

import useSurveyCreate from './surveys/useCreate';
import useSurveyDelete from './surveys/useDelete';
import useSurveyUpdate from './surveys/useUpdate';
import useSurveySearch from './surveys/useSearch';
import useSurveyShowResults from './surveys/useShowResults';
import useSurveySubmitResponse from './surveys/useSubmitResponse';
import useSurveyInbox from './surveys/useSurveyInbox';

import useAccessControl from './useAccessControl';
import useBillSearch from './bills/useBillSearch';
import useCancelBill from './bills/useCancelBill';

import useTenantsBills from './bills/useTenants';
import useReportMeta from './reports/useReport';

import useGetHowItWorksJSON from './useHowItWorksJSON';
import useGetFAQsJSON from './useGetFAQsJSON';
import useGetDSSFAQsJSON from './useGetDSSFAQsJSON';
import useGetDSSAboutJSON from './useGetDSSAboutJSON';
import useStaticData from './useStaticData';
import { usePrivacyContext } from './usePrivacyContext';
import useCustomAPIHook from './useCustomAPIHook';
import useCustomAPIMutationHook from './useCustomAPIMutationHook.js';
import useDynamicData from './useDynamicData';
import useRouteSubscription from './useRouteSubscription';
import useGenderMDMS from './useGenderMDMS';
import useScrollPersistence from './useScrollPersistence.js';

const dss = {
  useMDMS: useDssMdms,
  useDashboardConfig,
  useDSSDashboard,
  useGetChart,
};

const hrms = {
  useHRMSSearch,
  useHrmsMDMS,
  useHRMSCreate,
  useHRMSUpdate,
  useHRMSCount,
  useHRMSGenderMDMS,
};

const events = {
  useInbox: useEventInbox,
  useCreateEvent,
  useEventDetails,
  useUpdateEvent,
};

const engagement = {
  useMDMS: useEngagementMDMS,
  useDocCreate,
  useDocSearch,
  useDocDelete,
  useDocUpdate,
};

const survey = {
  useCreate: useSurveyCreate,
  useUpdate: useSurveyUpdate,
  useDelete: useSurveyDelete,
  useSearch: useSurveySearch,
  useSubmitResponse: useSurveySubmitResponse,
  useShowResults: useSurveyShowResults,
  useSurveyInbox,
};

const reports = {
  useReportMeta,
};

const Hooks = {
  useSessionStorage,
  useQueryParams,
  useFetchPayment,
  usePaymentUpdate,
  useFetchCitizenBillsForBuissnessService,
  useFetchBillsForBuissnessService,
  useGetPaymentRulesForBusinessServices,
  useWorkflowDetails,
  useWorkflowDetailsFSM,
  useInitStore,
  useClickOutside,
  useUserSearch,
  useApplicationsForBusinessServiceSearch,
  useDemandSearch,
  useInboxGeneral,
  useEmployeeSearch,
  useBoundaryLocalities,
  useCommonMDMS,
  useApplicationStatusGeneral,
  useModuleTenants,
  useRecieptSearch,
  usePaymentSearch,
  useNewInboxGeneral,
  useEvents,
  useClearNotifications,
  useNotificationCount,
  useStore,
  useDocumentSearch,
  useTenants,
  useAccessControl,
  useBillSearch,
  useCancelBill,
  useTenantsBills,
  usePrivacyContext,
  dss,
  hrms,
  events,
  engagement,
  survey,
  useCustomMDMS,
  reports,
  useGetHowItWorksJSON,
  useGetFAQsJSON,
  useGetDSSFAQsJSON,
  useGetDSSAboutJSON,
  useStaticData,
  useBulkPdfDetails,
  useCustomAPIHook,
  useCustomAPIMutationHook,
  useRouteSubscription,
  useDynamicData,
  useGenderMDMS,
  useLocation,
  useScrollPersistence
};

export default Hooks;
