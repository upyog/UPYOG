import { defineConfig } from 'vite';

/**
 * Vite build config for the "css" package.
 *
 * - Builds the SCSS entry file (src/index.scss) as a library and outputs it as index.css.
 * - Renames the default style.css output to index.css for consistent naming.
 * - Processes SCSS using the preprocessor with silenced deprecation warnings for @import.
 * - Runs PostCSS plugins: postcss-import (resolve @import), tailwindcss, and autoprefixer.
 */
export default defineConfig({
  build: {
    lib: {
      entry: 'src/index.scss',
      formats: ['es'],
      fileName: 'index'
    },
    rollupOptions: {
      output: {
        assetFileNames: (assetInfo) => {
          if (assetInfo.name === 'style.css') return 'index.css';
          return assetInfo.name;
        }
      }
    }
  },
  css: {
    preprocessorOptions: {
      scss: {
        silenceDeprecations: ["import"],
      },
    },
    postcss: {
      plugins: [
        require('postcss-import'),
        require('tailwindcss'),
        require('autoprefixer'),
      ]
    }
  }
});