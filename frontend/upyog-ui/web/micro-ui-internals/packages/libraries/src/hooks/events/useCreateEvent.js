import { mutationTemplate } from "../../common/mutationTemplate";

const useCreateEvent = () => {
  const mutationFn = (eventData) =>
    Digit.EventsServices.Create(eventData);

  return mutationTemplate({ mutationFn });
};

export default useCreateEvent;