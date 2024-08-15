import { fileURLToPath, URL } from "node:url";

import { defineConfig } from "vite";
import vue from "@vitejs/plugin-vue";
import vueDevTools from "vite-plugin-vue-devtools";
import path from 'path'

export default defineConfig({
  plugins: [vue(), vueDevTools()],
  resolve: {
    alias: {
      "@": fileURLToPath(new URL("./src", import.meta.url)),
    },
  },
  build: {
    // sourcemap: false,
    // minify: 'terser',
    // terserOptions: {
    // 	compress: {
    // 		drop_console: true,
    // 		drop_debugger: true,
    // 	},
    // },
    commonjsOptions: {
      transformMixedEsModules: true,
    },
    // assetsInlineLimit: 0,
    // rollupOptions: {
    //   input: {
    //     main: path.resolve(__dirname, "index.html"),
    //     worker: path.resolve(__dirname, "src/assets/js/showView/BackgroundRemovalWorker.js"),
    //   },
    // },
  },
  devServer: {
    https: true,
  },
});
