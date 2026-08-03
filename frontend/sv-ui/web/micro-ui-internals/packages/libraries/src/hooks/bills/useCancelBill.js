import { useQuery, useQueryClient } from "@tanstack/react-query";
import BillingService from "../../services/elements/Bill";

const useCancelBill = ({ filters }) => {
    const client = useQueryClient();
    // Updated: TanStack Query v5 requires useQuery to accept a single object instead of positional arguments.
    // Updated: queryKey and queryFn are now explicit keys inside the object — positional args removed.

    const { isLoading, error, data } = useQuery({
        queryKey: ["CANCEL_BILL", filters],
        queryFn: async () => await BillingService.cancel_bill(filters),
        enabled: filters?.businessService ? true : false,
    });
    return { isLoading, error, data, revalidate: () => client.invalidateQueries(["CANCEL_BILL", filters]) };
};

export default useCancelBill;
