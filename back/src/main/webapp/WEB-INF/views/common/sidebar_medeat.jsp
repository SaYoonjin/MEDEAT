<%@ page contentType="text/html; charset=UTF-8" %>

<style>
    .sidebar-menu {
        display: flex;
        flex-direction: column;
        height: 100%;
    }

    .menu-top {
        flex-grow: 1;
        margin-top: 20px;
    }

    .menu-bottom {
        margin-top: auto;
    }

    .menu-item {
        padding: 12px 20px;
        cursor: pointer;
        font-size: 17px;
        border-radius: 8px;
        margin-bottom: 8px;
        transition: 0.3s;
    }

    .menu-item:hover {
        background: #e5e8f0;
    }
</style>

<div class="sidebar-menu">

    <!-- ⭐ 로고 + 프로필 + 닉네임 -->
    <div class="sidebar-header" style="text-align:center; padding:25px 0 20px 0; border-bottom:1px solid #ddd;">

        <!-- 로고 -->
        <div onclick="location.href='/auth/dashboard_medeat'" style="cursor:pointer;">
            <img src="${pageContext.request.contextPath}/resources/img/medeat_logo.png"
                 style="width:150px; height:auto; margin-bottom:20px;">
        </div>

        <!-- 프로필 + 닉네임 (가로 정렬) -->
        <div style="display:flex; align-items:center; justify-content:center; gap:12px;">
            <img src="${pageContext.request.contextPath}/resources/img/IMG_1666-edited-scaled.jpg"
                 style="
                    width:55px;
                    height:55px;
                    border-radius:50%;
                    object-fit:cover;
                    border:2px solid #4CAF50;
                 ">
            <div style="font-size:18px; font-weight:bold; color:#333;">
                ${loginUser.nickname}
            </div>
        </div>

    </div>

    <!-- ⭐ Medi-EAT 전용 메뉴 -->
    <div class="menu-top">
        <div class="menu-item" onclick="location.href='/auth/dashboard_medeat'">대시보드</div>
        <div class="menu-item" onclick="location.href='/diet/list?mode=MEDI'">식단관리</div>
        <div class="menu-item" onclick="location.href='/disease/list'">질병 & 약 관리</div>
        <div class="menu-item" onclick="location.href='/challenge/list?mode=MEDI'">Challenge</div>
        <div class="menu-item" onclick="location.href='/community/list?mode=MEDI'">Community</div>
        <div class="menu-item" onclick="location.href='/mypage/myinfo?mode=MEDI'">MyPage</div>
    </div>

    <!-- ⭐ 하단 고정 메뉴 -->
    <div class="menu-bottom">
        <div class="menu-item" onclick="location.href='/mediscan'">메디스캔</div>
        <div class="menu-item" onclick="location.href='${pageContext.request.contextPath}/auth/logout'">Logout</div>
    </div>

</div>
