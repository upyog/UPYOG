import { useQueryClient } from "../common/queryClientTemplate";

export const useRevalidateQuery = async (key) => {
  const client = useQueryClient();
  return client.refetchQueries(key);
};
