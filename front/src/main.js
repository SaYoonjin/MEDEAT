import './assets/main.css'

import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import { autoSubscribePush } from "@/api/push";

const app = createApp(App)

app.use(router)

app.mount('#app')

// =============================
// ⭐ Service Worker 등록 추가
// =============================
if ('serviceWorker' in navigator) {
  navigator.serviceWorker.register('/service-worker.js')
    .then(() => {
      console.log("Service Worker registered");
    })
    .catch(err => {
      console.error("SW registration failed:", err);
    });
}

if ("serviceWorker" in navigator && "PushManager" in window) {
  navigator.serviceWorker.ready.then(() => {
    autoSubscribePush();
  });
}
