import { mutationTemplate } from "../../common/mutationTemplate";

const useUpdateEvent = () => {
  const mutationFn = (eventData) =>
    Digit.EventsServices.Update(eventData);

  return mutationTemplate({ mutationFn });
};

export default useUpdateEvent;