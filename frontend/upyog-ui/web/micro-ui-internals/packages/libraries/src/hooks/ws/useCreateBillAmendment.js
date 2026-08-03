import { mutationTemplate } from "../../common/mutationTemplate"
import Create from "../../services/molecules/WS/Create"

const useCreateBillAmendment = () => {
    return mutationTemplate({ mutationFn: (data) => Create.BillAmendment(data) })
}

export default useCreateBillAmendment