import "@/assets/css/main.css";

import VCalendar from "v-calendar";
import "v-calendar/style.css";

import { createApp } from "vue";
import { createPinia } from "pinia";

import App from "@/App.vue";
import router from "@/router";

const app = createApp(App);

app.use(createPinia());
app.use(router);
app.use(VCalendar, {});

app.mount("#app");
