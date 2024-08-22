const path = require("path");
const HtmlWebpackPlugin = require("html-webpack-plugin");
const { CleanWebpackPlugin } = require("clean-webpack-plugin");
const webpack = require("webpack");

module.exports = {
  entry: "./src/index.js",
  devtool: "none",
  module: {
    rules: [
      {
        test: /\.(js)$/,
        use: ["babel-loader"],
      },
      {
        test: /\.css$/i,
        use: ["style-loader", "css-loader"],
      }
    ],
  },
  output: {
    filename: "[name].bundle.js",
    path: path.resolve(__dirname, "build"),
    publicPath: "/digit-ui/",
  },
  optimization: {
    splitChunks: {
      chunks: 'all',
      minSize: 20000,
      maxSize: 50000,
      enforceSizeThreshold: 50000,
      minChunks: 1,
      maxAsyncRequests: 30,
      maxInitialRequests: 30
    },
  },
  plugins: [
    new CleanWebpackPlugin(),
    new HtmlWebpackPlugin({ inject: true, template: "public/index.html" }),
    new webpack.IgnorePlugin({
      resourceRegExp: /^fs$/,
    }),
  ],
};
