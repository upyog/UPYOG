import { DigiLockerService } from "../../services/elements/DigiLocker";
import { useMutation } from "@tanstack/react-query";

const createTokenAPI = (type) => {
// Updated: TanStack Query v5 requires useMutation to accept an object with mutationFn key instead of a direct function.
  return useMutation({
    mutationFn: (data) => {
      return DigiLockerService.token(data)
    }
  });
 }

export default createTokenAPI;
