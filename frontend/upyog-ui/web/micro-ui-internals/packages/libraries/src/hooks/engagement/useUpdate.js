import { mutationTemplate } from "../../common/mutationTemplate";
import { Engagement } from "../../services/elements/Engagement";

/**
 * Update engagement document.
 */
const useUpdateDocument = () => {
  const mutationFn = (filters) =>
    Engagement.update(filters);

  return mutationTemplate({ mutationFn });
};

export default useUpdateDocument;