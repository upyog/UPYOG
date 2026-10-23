import { queryTemplate } from "../../common/queryTemplate";
import { OBPSService } from "../../services/elements/OBPS";

const useEDCRInbox = ({
  tenantId,
  filters,
  config = { retry: false, staleTime: Infinity },
}) => {
  const { filterForm, searchForm, tableForm } = filters;

  const finalFilters = {
    tenantId,
    ...(searchForm?.edcrNumber && { edcrNumber: searchForm.edcrNumber }),
    ...(searchForm?.applicationNumber && { applicationNumber: searchForm.applicationNumber }),
    ...(filterForm?.status && { status: filterForm.status }),
    ...(filterForm?.appliactionType && { appliactionType: filterForm.appliactionType }),
    ...(tableForm?.sortOrder && { orderBy: tableForm.sortOrder }),
    limit: tableForm?.limit,
    offset: tableForm?.offset,
  };

  return queryTemplate({
    queryKey: [
      "OBPS_EDCR_INBOX",
      tenantId,
      JSON.stringify(finalFilters),
    ],
    queryFn: () =>
      OBPSService.scrutinyDetails(tenantId, finalFilters),
    select: (data) => ({
      table:
        data?.edcrDetail?.map((app) => ({
          applicationId: app.applicationNumber,
          edcrNumber: app.edcrNumber,
          date: app.applicationDate,
          status: app.status,
          owner: app.planDetail?.planInformation?.applicantName,
        })) || [],
      totalCount: data?.count || 0,
    }),
    config,
  });
};

export default useEDCRInbox;