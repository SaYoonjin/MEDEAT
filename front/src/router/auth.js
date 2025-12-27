// src/router/auth.js
import Login from '@/pages/auth/Login.vue';
import Signup from '@/pages/auth/Signup.vue';
import ResetPassword from '@/pages/auth/ResetPassword.vue'
import FindIdView from "@/pages/auth/FindIdView.vue";

export default [
  {
    path: '/login',
    name: 'login',
    component: Login,
  },
  {
    path: '/signup',
    name: 'signup',
    component: Signup,
  },
  {
    path: '/auth/reset-password',
    name: 'reset-password',
    component: ResetPassword,
  },

  {
  path: "/auth/find-id",
  name: "find-id",
  component: FindIdView,
  },

];
