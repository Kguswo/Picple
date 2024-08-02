import '@/assets/css/main.css';

import VCalendar from 'v-calendar';
import 'v-calendar/style.css';

import { createApp } from 'vue';
import { createPinia } from 'pinia';
import piniaPluginPersistedstate from 'pinia-plugin-persistedstate';

import App from '@/App.vue';
import router from '@/router';

import VueSweetalert2 from 'vue-sweetalert2';
import 'sweetalert2/dist/sweetalert2.min.css';

const app = createApp(App);

app.use(VueSweetalert2);
app.use(createPinia().use(piniaPluginPersistedstate));
app.use(router);
app.use(VCalendar, {});

app.mount('#app');
