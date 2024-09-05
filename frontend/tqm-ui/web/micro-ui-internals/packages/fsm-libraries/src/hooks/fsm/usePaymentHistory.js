import { PaymentService } from '@egovernments/digit-ui-libraries/src/services/elements/Payment';
import { useQuery } from 'react-query';

const usePaymentHistory = (tenantId, id, config = {}) => {
  return useQuery(["PAYMENT_HISTORY", id], () => PaymentService.getReciept(tenantId, "FSM.TRIP_CHARGES", { consumerCodes: id }), { ...config });
};

export default usePaymentHistory;
