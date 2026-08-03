import { useQueryClient } from "@tanstack/react-query";
import { queryTemplate } from "../../common/queryTemplate";
import BillingService from "../../services/elements/Bill";

const useCancelBill = ({ filters }) => {
  const client = useQueryClient();

  const queryKey = [
    "CANCEL_BILL",
    JSON.stringify(filters),
  ];

  const queryFn = () =>
    BillingService.cancel_bill(filters);

  const enabled = !!filters?.businessService;

  const query = queryTemplate({
    queryKey,
    queryFn,
    enabled,
  });

  return {
    ...query,
    revalidate: () =>
      client.invalidateQueries({ queryKey }),
  };
};

export default useCancelBill;