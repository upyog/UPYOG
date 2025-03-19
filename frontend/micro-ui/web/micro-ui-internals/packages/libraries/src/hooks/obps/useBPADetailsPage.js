import { useQuery } from "react-query";
import { OBPSService } from "../../services/elements/OBPS";
const useBPADetailsPage = (tenantId, filters, config) => {
  return useQuery(['BPA_DETAILS_PAGE', filters, tenantId], () => OBPSService.BPADetailsPage(tenantId, filters), config);
}

export default useBPADetailsPage;