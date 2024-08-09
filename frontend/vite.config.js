import { fileURLToPath, URL } from 'node:url';
import { defineConfig } from 'vite';
import vue from '@vitejs/plugin-vue';
import vueDevTools from 'vite-plugin-vue-devtools';

export default defineConfig({
    // 플러그인 설정
    plugins: [
        vue(), // Vue 플러그인
        vueDevTools(), // Vue DevTools 플러그인
    ],
    // 경로 별칭 설정
    resolve: {
        alias: {
            // '@'를 'src' 디렉토리의 절대 경로로 설정
            '@': fileURLToPath(new URL('./src', import.meta.url)),
        },
    },
    // 개발 서버 설정
    server: {
        https: false, // HTTPS 사용 설정을 false로 변경
        // headers 설정 제거
    },
});
