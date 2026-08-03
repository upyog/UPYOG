import { useMutation } from "@tanstack/react-query";

const useCreateEvent = () => {
  // Updated: TanStack Query v5 requires useMutation to accept an object with mutationFn key instead of a direct function.
  return useMutation({
    mutationFn: (eventData) => Digit.EventsServices.Create(eventData)
  })
}

export default useCreateEvent;