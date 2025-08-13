import { useQuery, useQueryClient, useMutation } from "react-query";

/**
 * Custom React Hook for performing a search mutation using the React Query library.
 * 
 * This hook utilizes the `useMutation` hook from React Query to perform search operations.
 * It abstracts the logic for managing loading states, error handling, and data submission.
 * 
 * @returns {Object} A mutation object containing:
 *   - {function} mutate - Function to trigger the search with data parameter
 *   - {boolean} isLoading - Indicates if the mutation is currently loading
 *   - {Error} error - Contains any error that occurred during the mutation
 *   - {any} data - The data returned from the search mutation
 *   - {boolean} isSuccess - Indicates if the mutation was successful
 * 
 * Usage:
 * const mutation = useCMSearch();
 * mutation.mutate(searchData, {
 *   onSuccess: (data) => {  handle success  }.
  onError: (error) => {  handle error }
 * });
 */

import { CMServices } from "../../services/elements/CM";

const useCMSearch = () => {
  return useMutation((data) => CMServices.search(data));
};

export default useCMSearch;
