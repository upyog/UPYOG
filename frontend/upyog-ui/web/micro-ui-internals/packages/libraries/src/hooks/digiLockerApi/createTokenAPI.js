import { DigiLockerService } from "../../services/elements/DigiLocker";
import { PTService } from "../../services/elements/PT";
import { useMutation } from "react-query";

const createTokenAPI = (type) => {

  return useMutation((data) => {
    DigiLockerService.token(data)});
 }

export default createTokenAPI;
