import { DigiLockerService } from "../../services/elements/DigiLocker";
import { mutationTemplate } from "../../common/mutationTemplate";


const useCreateToken = () => {
  const mutationFn = (data) =>
    DigiLockerService.token(data);

  return mutationTemplate({ mutationFn });
};

export default useCreateToken;
