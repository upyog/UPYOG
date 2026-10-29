import { mutationTemplate } from "../../common/mutationTemplate";
import { Engagement } from "../../services/elements/Engagement";

/**
 * Create engagement document.
 */
const useCreateDocument = () => {
  const mutationFn = (filters) =>
    Engagement.create(filters);

  return mutationTemplate({ mutationFn });
};

export default useCreateDocument;