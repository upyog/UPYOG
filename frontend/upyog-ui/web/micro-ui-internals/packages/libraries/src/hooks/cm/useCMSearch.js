import { mutationTemplate } from "../../common/mutationTemplate";
import { CMServices } from "../../services/elements/CM";

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
const useCMSearch = () => {
  const mutationFn = (data) => CMServices.search(data);

  return mutationTemplate({ mutationFn });
};

export default useCMSearch;