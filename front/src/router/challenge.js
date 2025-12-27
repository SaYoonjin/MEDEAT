import api from "@/api/axios";

export default [
  {
    path: '/challenge',
    name: 'challenge-list',
    component: () => import('@/pages/challenge/ChallengeList.vue'),
  },
  {
    path: '/challenge/form',
    name: 'challenge-form',
    component: () => import('@/pages/challenge/ChallengeForm.vue'),
  },
  {
    path: '/challenge/new',
    name: 'challenge-new',
    component: () => import('@/pages/challenge/ChallengeForm.vue'),
  },
  {
    path: '/challenge/:id',
    name: 'challenge-detail',
    component: () => import('@/pages/challenge/ChallengeDetail.vue'),
  },
  {
    path: '/my/challenges',
    name: 'my-challenge-list',
    component: () => import('@/pages/challenge/my.vue'),
  },
  {
    path: '/challenge/edit/:id',
    name: 'challenge-edit',
    component: () => import('@/pages/challenge/ChallengeForm.vue'),
    props: true,
  },

  // ✅ 여기만 수정
  // router/index.js (또는 해당 라우터 파일)
  {
    path: "/challenge/:id/chat",
    name: "challenge-chat",
    component: () => import("@/pages/challenge/ChallengeChat.vue"),
    beforeEnter: async (to, from, next) => {
      const challengeId = to.params.id;
      
      // id가 없는 경우 예외 처리
      if (!challengeId) {
        return next("/challenge");
      }

      try {
        // ✅ axios 인스턴스(api)를 사용하여 세션 쿠키가 포함되도록 함
        const res = await api.get(`/challenge/${challengeId}/me`);

        if (res.data && res.data.status === "PROGRESS") {
          next(); // 성공 시에만 진입
        } else {
          alert("참여 중인 챌린지만 채팅이 가능합니다.");
          next(from.path || "/challenge"); 
        }
      } catch (e) {
        console.error("채팅 권한 확인 실패:", e);
        // 401 에러가 발생하면 로그인 페이지로 보내기 전에 세션을 다시 확인하거나 경고창 표시
        if (e.response && e.response.status === 401) {
          alert("세션이 만료되었거나 권한이 없습니다. 다시 로그인해 주세요.");
          next("/login");
        } else {
          next("/challenge");
        }
      }
    },
  },
];
