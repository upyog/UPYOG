const path = require("path");
const HtmlWebpackPlugin = require("html-webpack-plugin");
const { CleanWebpackPlugin } = require("clean-webpack-plugin");
const TerserPlugin = require('terser-webpack-plugin');

module.exports = {
   //mode: 'development',
  mode: 'production',
  entry: "./src/index.js",
  devtool: "source-map",
  module: {
    rules: [
      {
        test: /\.(js)$/,
        exclude: /node_modules/,
        use: ["babel-loader"],
      },
      {
        test: /pdf\.worker\.(min\.)?js$/,
        use: "file-loader", // Ensures the worker script is loaded separately
      }
    ],
  },
  output: {
    filename: "[name].bundle.js",
    path: path.resolve(__dirname, "build"),
    publicPath: "/upyog-ui/",
  },
  optimization: {
    splitChunks: {
      chunks: 'all',
    },
    minimizer: [new TerserPlugin({ /* additional options here */ })],
  },
  plugins: [
    new CleanWebpackPlugin(),
    new HtmlWebpackPlugin({ inject: true, template: "public/index.html" }),
  ],
};
