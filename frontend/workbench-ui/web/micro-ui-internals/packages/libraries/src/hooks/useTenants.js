import { queryTemplate } from "../common/queryTemplate";

const alphabeticalSortFunctionForTenantsBasedOnName = (firstEl, secondEl) =>{
    if (firstEl.name.toUpperCase() < secondEl.name.toUpperCase() ) {
        return -1
    }
    if (firstEl.name.toUpperCase() > secondEl.name.toUpperCase() ) {
        return 1
    }
        return 0
}

export const useTenants = () => queryTemplate({
    queryKey: ["ALL_TENANTS"],
    queryFn: () => Digit.SessionStorage.get("initData").tenants.sort(alphabeticalSortFunctionForTenantsBasedOnName)
});
