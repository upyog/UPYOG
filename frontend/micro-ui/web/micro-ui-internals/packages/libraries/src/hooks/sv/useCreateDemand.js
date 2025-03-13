import { useMutation } from "react-query";
import { SVService } from "../../services/elements/SV";

/**
 * Custom React Hook: useCreateDemand
 *
 * This hook utilizes the `useMutation` hook from `react-query` to handle the creation
 * of a new demand using the `update` method from the `SVService`.
 *
 * How it works:
 * - `useMutation` is used to perform a mutation (a change in data, such as a POST request).
 * - The mutation function takes `data` as an argument and calls `SVService.update(data)`,
 *   which is expected to send the request to the backend.
 * - The mutation object returned by `useMutation` provides various states such as
 *   `isLoading`, `isError`, `isSuccess`, and `mutate`, which can be used to manage UI behavior.
 *
 * @returns {UseMutationResult} A mutation object containing state and mutation functions.
 */

export const useCreateDemand = () => {
    return useMutation((data) => SVService.update(data));
};

export default useCreateDemand;