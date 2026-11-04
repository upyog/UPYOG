import { queryTemplate } from "../common/queryTemplate"; // queryTemplate use karo
import StoreData from "../services/molecules/StoreData";

const useStore = {
  getInitData: () =>
    queryTemplate({
      queryKey: ["STORE_DATA"],
      queryFn: () => StoreData.getInitData(),
      config: {
        staleTime: Infinity,
      },
    }),
};

export default useStore;