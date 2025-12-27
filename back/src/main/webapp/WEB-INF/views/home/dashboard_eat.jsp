<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="content-area">

    <style>
        .dashboard-title { font-size: 26px; font-weight: bold; margin-bottom: 25px; }
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
        .grid-box:hover {
            background: #d7e4ff;
        }
    </style>

    <div class="dashboard-title">EAT Mode Dashboard</div>

    <div class="grid">
        <div class="grid-box" onclick="location.href='/diet/list?userId=1'">식단관리</div>
        <div class="grid-box" onclick="location.href='/challenge/list'">Challenge</div>
        <div class="grid-box" onclick="location.href='/community/list'">Community</div>
        <div class="grid-box" onclick="location.href='/mediscan/list'">메디 스캔</div>
    </div>

</div>