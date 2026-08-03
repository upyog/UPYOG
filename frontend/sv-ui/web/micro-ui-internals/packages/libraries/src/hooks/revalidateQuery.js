import { useQueryClient } from "@tanstack/react-query";

export const useRevalidateQuery = async (key) => {
  const client = useQueryClient();
  return client.refetchQueries(key);
};
