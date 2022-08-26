import { coreComponents } from "./Core";
import { ptComponents } from "./pt";
import { tlComponents } from "./tl";

var Digit = window.Digit || {};

const customisedComponent = {
    ...ptComponents,
    ...tlComponents,
    ...coreComponents,
}



export const initCustomisationComponents = () => {
    Object.entries(customisedComponent).forEach(([key, value]) => {
        Digit.ComponentRegistryService.setComponent(key, value);
    });
};


