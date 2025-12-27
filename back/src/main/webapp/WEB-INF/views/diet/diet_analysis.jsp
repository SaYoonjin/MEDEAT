<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<h2>식단 분석</h2>

<style>
    .period-btn {
        padding: 8px 16px;
        margin-right: 10px;
        cursor: pointer;
        background: #e7ecff;
        border: none;
        border-radius: 6px;
        font-size: 14px;
    }
    .period-btn.active { background: #4f7bff; color:white; }
    .box {
        background: #f4f7ff;
        border-radius: 10px;
        padding: 20px;
        margin-top: 15px;
    }
</style>

<!-- 기간 선택 -->
<div>
    <a href="/diet/analysis?period=day">
        <button class="period-btn ${period=='day'?'active':''}">1일</button>
    </a>
    <a href="/diet/analysis?period=week">
        <button class="period-btn ${period=='week'?'active':''}">1주일</button>
    </a>
    <a href="/diet/analysis?period=month">
        <button class="period-btn ${period=='month'?'active':''}">1개월</button>
    </a>
</div>

<div class="box">
    <h3>총 영양 분석</h3>
    <p>총 칼로리: <b>${summary.totalCalorie} kcal</b></p>
    <p>탄수화물: ${summary.totalCarb} g</p>
    <p>단백질: ${summary.totalProtein} g</p>
    <p>지방: ${summary.totalFat} g</p>
</div>

<div class="box">
    <h3>식단 수</h3>
    ${summary.count} 개
</div>
