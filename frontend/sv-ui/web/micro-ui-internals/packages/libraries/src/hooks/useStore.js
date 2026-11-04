import { useQuery } from "@tanstack/react-query";
import StoreData from "../services/molecules/StoreData";

const useStore = {
  getInitData: () =>
    useQuery({
      queryKey: ["STORE_DATA"],
      queryFn: () => StoreData.getInitData(),
      staleTime: Infinity,
    }),
};

export default useStore;
