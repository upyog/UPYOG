import { useQuery } from "react-query";

// Sort function
const alphabeticalSortFunctionForTenantsBasedOnName = (firstEl, secondEl) => {
    console.log("firstEl, secondEl", firstEl, secondEl);
    
    // Extract city names for comparison
    const firstCityName = firstEl.city.name.toUpperCase();
    const secondCityName = secondEl.city.name.toUpperCase();

    // Check if either of the cities is "PG"
    const isFirstCityPG = firstCityName === "PG";
    const isSecondCityPG = secondCityName === "PG";

    // If the first city is "PG", it should come after the second city
    if (isFirstCityPG && !isSecondCityPG) {
        return 1; // firstEl (PG) comes after secondEl
    }
    if (!isFirstCityPG && isSecondCityPG) {
        return -1; // secondEl (PG) comes after firstEl
    }

    // If both cities are not "PG", sort alphabetically by city name
    if (firstCityName < secondCityName) {
        return -1; // firstEl comes first
    }
    if (firstCityName > secondCityName) {
        return 1; // secondEl comes first
    }

    // If both have the same city name, sort alphabetically by tenant name
    if (firstEl.name.toUpperCase() < secondEl.name.toUpperCase()) {
        return -1;
    }
    if (firstEl.name.toUpperCase() > secondEl.name.toUpperCase()) {
        return 1;
    }

    return 0; // They are equal
};


export const useTenants = () => useQuery(["ALL_TENANTS"], () => Digit.SessionStorage.get("initData").tenants.sort(alphabeticalSortFunctionForTenantsBasedOnName))
