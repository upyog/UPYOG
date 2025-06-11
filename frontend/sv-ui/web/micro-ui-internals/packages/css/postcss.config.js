// PostCSS is a Node.js tool that transforms your styles using JavaScript plugins.

const postcssPresetEnv = require("postcss-preset-env");

module.exports = {
  parser: require("postcss-scss"),
  plugins: [
    require("postcss-import"),
    require("postcss-nested").default,
    require("tailwindcss"),
    require("postcss-preset-env"),
    require("autoprefixer"),
    // require("cssnano"),
  ],
};