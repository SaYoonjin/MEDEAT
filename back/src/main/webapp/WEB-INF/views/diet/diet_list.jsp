<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<h2>식단 기록</h2>

<style>
    .date-select {
        margin-bottom: 20px;
        font-size: 16px;
        padding: 8px;
    }

    .diet-box {
        border: 1px solid #ddd;
        padding: 15px;
        border-radius: 10px;
        margin-bottom: 15px;
        background: #f8faff;
        cursor: pointer;
        transition: 0.2s;
    }
    .diet-box:hover {
        background: #e6efff;
    }

    .meal-title {
        font-weight: bold;
        font-size: 18px;
        color: #2956d3;
    }

    .nutri {
        margin-top: 5px;
        color: #666;
        font-size: 14px;
    }

    .btn-small {
        display: inline-block;
        padding: 5px 10px;
        background: #4F8BFF;
        color: white;
        font-size: 13px;
        border-radius: 6px;
        text-decoration: none;
        margin-right: 5px;
    }
    .btn-delete {
        background: #ff6565;
    }
</style>

<a href="/diet/form?date=${selectedDate}"
   style="padding:10px 16px; background:#4F8BFF; color:white; border-radius:8px; text-decoration:none;">
    + 식단 기록 추가
</a>

<!-- 날짜 선택 -->
<form method="get" action="/diet/list">
    <input type="date" name="date" value="${selectedDate}" class="date-select">
    <button type="submit">조회</button>
</form>

<hr>

<!-- 식단 리스트 -->
<c:forEach var="d" items="${dietList}">
    <div class="diet-box" onclick="location.href='/diet/edit/${d.dietId}'">
        <div class="meal-title">${d.mealTime}</div>
        <div>${d.logDate}</div>

        <div class="nutri">
            총 칼로리: <b>${d.totalCalorie} kcal</b><br>
            탄수화물: ${d.totalCarb}g · 단백질: ${d.totalProtein}g · 지방: ${d.totalFat}g
        </div>

        <!-- ⭐ 수정/삭제 버튼 -->
        <div style="margin-top:8px;">
            <a href="/diet/edit/${d.dietId}"
               style="color:#1a73e8; margin-right:10px;">수정</a>

            <a href="/diet/delete/${d.dietId}"
               onclick="return confirm('삭제할까요?')"
               style="color:red;">삭제</a>
        </div>
    </div>
</c:forEach>

<!-- 식단 없을 때 -->
<c:if test="${empty dietList}">
    <div>해당 날짜의 식단 기록이 없습니다.</div>
</c:if>
