// src/router/community.js
import CommunityList from '@/pages/community/CommunityList.vue';
import CommunityDetail from '@/pages/community/CommunityDetail.vue';
import CommunityForm from '@/pages/community/CommunityForm.vue';
import CommunityScrap from '@/pages/community/CommunityScrap.vue';
import CommunityLike from '@/pages/community/CommunityLike.vue';

export default [
  {
    path: '/community',
    name: 'community-list',
    component: CommunityList,
  },
  {
    path: '/community/new',
    name: 'community-new',
    component: CommunityForm,
  },
    {
    path: '/community/scrap',
    name: 'community-scrap',
    component: CommunityScrap,
  },
  {
    path: '/community/like',
    name: 'community-like',
    component: CommunityLike,
  },
  {
    path: '/community/:id',
    name: 'community-detail',
    component: CommunityDetail,
    props: true,
  },
  {
    path: '/community/edit/:id',
    name: 'community-edit',
    component: CommunityForm,
    props: true,
  },
];
