// src/router/mypage.js
import MyInfo from '@/pages/mypage/MyInfo.vue';
import MyEdit from '@/pages/mypage/MyEdit.vue';
import MyPosts from '@/pages/mypage/MyPosts.vue';

export default [
  {
    path: '/mypage',
    name: 'mypage-info',
    component: MyInfo,
  },
  {
    path: '/mypage/edit',
    name: 'mypage-edit',
    component: MyEdit,
  },
  {
    path: '/mypage/posts',
    name: 'mypage-posts',
    component: MyPosts,
  },
];
