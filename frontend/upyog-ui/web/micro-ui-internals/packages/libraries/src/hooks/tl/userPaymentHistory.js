import { queryTemplate } from "../../common/queryTemplate";
import { PaymentService } from "../../services/elements/Payment";

const useTLPaymentHistory = (tenantId, id, config = {}) => {
  return queryTemplate({ queryKey: ["PAYMENT_HISTORY", id], queryFn: () => PaymentService.getReciept(tenantId, "TL", { consumerCodes: id }), config });
};

export default useTLPaymentHistory;
