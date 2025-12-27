// src/router/index.js
import { createRouter, createWebHistory } from "vue-router";

import aiRoutes from "./ai";
import authRoutes from "./auth";
import dashboardRoutes from "./dashboard";
import challengeRoutes from "./challenge";
import communityRoutes from "./community";
import dietRoutes from "./diet";
import diseaseRoutes from "./disease";
import medicationRoutes from "./medication";
import mypageRoutes from "./mypage";
import notificationRoutes from "./notification";

const routes = [
  {
    path: "/",
    name: "intro",
    component: () => import("@/views/IntroView.vue"),
  },
  ...aiRoutes,
  ...authRoutes,
  ...dashboardRoutes,
  ...challengeRoutes,
  ...communityRoutes,
  ...dietRoutes,
  ...diseaseRoutes,
  ...medicationRoutes,
  ...mypageRoutes,
  ...notificationRoutes,

  // ✅ 없는 경로는 인트로로
  { path: "/:pathMatch(.*)*", redirect: "/" },
];

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
});

export default router;
