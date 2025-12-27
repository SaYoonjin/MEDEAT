<%@ page contentType="text/html; charset=UTF-8" %>

<!-- 여기부터가 개별 페이지 content -->
<div class="page-container">

    <style>
        .dashboard-title { font-size: 26px; font-weight: bold; margin-bottom: 25px; }
        .mediscan-banner {
            width: 100%;
            background: #4f84ff;
            color: white;
            font-size: 20px;
            padding: 15px;
            border-radius: 10px;
            text-align: center;
            margin-bottom: 30px;
            cursor: pointer;
        }
        .mediscan-banner:hover { background: #3a64d9; }

        .grid {
            display: grid;
            grid-template-columns: repeat(2, 1fr);
            grid-gap: 20px;
        }
        .grid-box {
            background: #eef3ff;
            border-radius: 12px;
            height: 200px;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 22px;
            font-weight: bold;
            cursor: pointer;
            transition: 0.2s;
        }
        .grid-box:hover { background: #d7e4ff; }
    </style>

    <!-- 사이드바는 layout.jsp에서 자동 include됨 -->

    <div class="dashboard-title">MEDI-EAT Mode Dashboard</div>

    <div class="mediscan-banner" onclick="location.href='/mediscan'">
        메디 스캔 바로가기
    </div>

    <div class="grid">
        <div class="grid-box" onclick="location.href='/diet/list?mode=MEDI'">식단관리</div>
        <div class="grid-box" onclick="location.href='/medication/list?mode=MEDI'">질병 & 약 관리</div>
        <div class="grid-box" onclick="location.href='/challenge/list?mode=MEDI'">Challenge</div>
        <div class="grid-box" onclick="location.href='/community/list?mode=MEDI'">Community</div>
    </div>

</div>