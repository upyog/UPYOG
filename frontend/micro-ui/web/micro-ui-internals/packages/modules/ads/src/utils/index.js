// Determines if the back button should be hidden based on the current URL matching screen paths in the config array.
// Returns true to hide the back button if a match is found; otherwise, returns false.
export const shouldHideBackButton = (config = []) => {
    return config.filter((key) => window.location.href.includes(key.screenPath)).length > 0 ? true : false;
  };
  