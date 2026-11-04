import { queryTemplate } from "../../common/queryTemplate";
import { PaymentService } from "../../services/elements/Payment";

const usePaymentHistory = (tenantId, id, config = {}) => {
  const queryKey = ["FSM_PAYMENT_HISTORY", tenantId, id];

  const queryFn = () =>
    PaymentService.getReciept(tenantId, "FSM.TRIP_CHARGES", {
      consumerCodes: id,
    });

  return queryTemplate({
    queryKey,
    queryFn,
    config,
  });
};

export default usePaymentHistory;