import { DigiLockerService } from "../../services/elements/DigiLocker";
import { PTService } from "../../services/elements/PT";
import { useMutation } from "react-query";

const createTokenAPI = () => {
 
    return useMutation((data) => {
      console.log("createTokenAPI",data)
      DigiLockerService.token(data)});
};

export default createTokenAPI;
