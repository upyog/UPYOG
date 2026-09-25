/**
 * Commented so that we can later cross verify as this is the major API call for DigiLocker 
 * and we need to make sure that we are not breaking anything.
 
 

import { DigiLockerService } from "../../services/elements/DigiLocker";

const createTokenAPI = (type) => {

  return useMutation((data) => {
    DigiLockerService.token(data)});
 }

export default createTokenAPI;
*/


// New one as per the new structure of the codebase and the way we are using mutationTemplate for all the mutation calls.
import { DigiLockerService } from "../../services/elements/DigiLocker";
import { mutationTemplate } from "../../common/mutationTemplate";

/**
 * Generate DigiLocker token.
 */
const useCreateToken = () => {
  const mutationFn = (data) =>
    DigiLockerService.token(data);

  return mutationTemplate({ mutationFn });
};

export default useCreateToken;
