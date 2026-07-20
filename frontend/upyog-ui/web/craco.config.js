module.exports = {
  webpack: {
    configure: (config) => {
      // Externalize the runtime chunk
      config.optimization.runtimeChunk = 'single';
      return config;
    }
  }
};
