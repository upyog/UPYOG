import { useMutation } from "@tanstack/react-query";

/**
 * Standard Mutation Template (v5)
 */
export const mutationTemplate = ({
  mutationFn,
  config = {},
}) => {
  return useMutation({
    mutationFn,
    ...config,
  });
};