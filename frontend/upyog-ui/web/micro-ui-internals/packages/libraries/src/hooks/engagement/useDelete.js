import { mutationTemplate } from "../../common/mutationTemplate";
import { Engagement } from "../../services/elements/Engagement";

/**
 * Delete engagement document.
 */
const useDeleteDocument = () => {
  const mutationFn = (filters) =>
    Engagement.delete(filters);

  return mutationTemplate({ mutationFn });
};

export default useDeleteDocument;