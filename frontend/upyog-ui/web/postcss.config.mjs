import postcssImport from "postcss-import";
import postcssNested from "postcss-nested";
import tailwindcss from "tailwindcss";
import postcssPresetEnv from "postcss-preset-env";
import autoprefixer from "autoprefixer";
import cssnano from "cssnano";

const isProd = process.env.NODE_ENV === "production";

export default {
  parser: "postcss-scss",

  plugins: [
    postcssImport(),
    postcssNested(),
    tailwindcss(),
    postcssPresetEnv({
      stage: 2,
      autoprefixer: {
        cascade: false,
      },
      features: {
        "custom-properties": true,
      },
    }),
    autoprefixer(),
    ...(isProd ? [cssnano()] : []),
  ],
};