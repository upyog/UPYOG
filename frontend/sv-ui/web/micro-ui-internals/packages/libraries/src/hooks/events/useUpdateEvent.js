import { useMutation } from "@tanstack/react-query";

const useUpdateEvent = () => {
  // Updated: TanStack Query v5 requires useMutation to accept an object with mutationFn key instead of a direct function.
  
  return useMutation({
    mutationFn: (eventData) => Digit.EventsServices.Update(eventData)
  })
}

export default useUpdateEvent;