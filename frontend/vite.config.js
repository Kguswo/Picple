import { fileURLToPath, URL } from 'node:url';

import { defineConfig } from 'vite';
import vue from '@vitejs/plugin-vue';
import vueDevTools from 'vite-plugin-vue-devtools';

export default defineConfig({
	plugins: [vue(), vueDevTools()],
	resolve: {
		alias: {
			'@': fileURLToPath(new URL('./src', import.meta.url)),
		},
	},
	build: {
		sourcemap: false,
		// minify: 'terser',
		// terserOptions: {
		// 	compress: {
		// 		drop_console: true,
		// 		drop_debugger: true,
		// 	},
		// },
	},
	devServer: {
		https: true,
	},
});
