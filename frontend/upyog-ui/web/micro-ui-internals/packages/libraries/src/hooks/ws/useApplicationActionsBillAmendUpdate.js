import { mutationTemplate } from "../../common/mutationTemplate";
import ApplicationUpdateActionsBillAmendUpdate from "../../services/molecules/WS/ApplicationUpdateActionsBillAmendUpdate";

const useApplicationActionsBillAmendUpdate = () => {
    return mutationTemplate({ mutationFn: (applicationData) => ApplicationUpdateActionsBillAmendUpdate(applicationData) });
};

export default useApplicationActionsBillAmendUpdate;
