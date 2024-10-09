export const shouldHideBackButton = (config = []) => {
    return config.filter((key) => window.location.href.includes(key.screenPath)).length > 0 ? true : false;
  };
  