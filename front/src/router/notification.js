// src/router/notification.js

export default [
  {
    path: "/notifications",
    name: "notifications",
    component: () => import("@/pages/notification/NotificationPage.vue"),
    meta: {
      requiresAuth: true, // 🔒 로그인 필요하면 유지 (없어도 되면 제거 가능)
      title: "알림",
    },
  },
];
