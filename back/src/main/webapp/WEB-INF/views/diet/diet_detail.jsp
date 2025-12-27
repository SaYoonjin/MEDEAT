<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<h2>식단 상세보기</h2>

<style>
    .item-box {
        border: 1px solid #ccc;
        padding: 15px;
        margin: 10px 0;
        border-radius: 10px;
        background: #fdfdff;
    }
    .btn {
        padding: 6px 12px;
        font-size: 14px;
        cursor: pointer;
        margin-right: 5px;
    }
    .btn-red { background: #ff5c5c; color: white; border: none; }
    .btn-blue { background: #4f7bff; color: white; border: none; }
</style>

<div>
    <b>${diet.mealTime}</b> &nbsp; (${diet.logDate})
</div>

<div style="margin-top: 10px;">
    <b>메모:</b> ${diet.memo}
</div>

<!-- 사진 -->
<c:if test="${not empty diet.photoPath}">
    <div style="margin-top: 15px;">
        <img src="${diet.photoPath}" width="250px" style="border-radius:10px;">
    </div>
</c:if>

<hr>

<h3>음식 목록</h3>

<c:forEach var="item" items="${items}">
    <div class="item-box">
        <b>${item.foodName}</b> (${item.gram}g)<br>
        ${item.calorie} kcal / 탄 ${item.carb}g / 단 ${item.protein}g / 지 ${item.fat}g

        <!-- 삭제 -->
        <form action="/diet/item/delete" method="post" style="margin-top:5px;">
            <input type="hidden" name="itemId" value="${item.itemId}">
            <input type="hidden" name="dietId" value="${diet.dietId}">
            <button class="btn btn-red">삭제</button>
        </form>
    </div>
</c:forEach>

<!-- 음식 없을 때 -->
<c:if test="${empty items}">
    <div>등록된 음식이 없습니다.</div>
</c:if>

<hr>

<!-- 버튼 -->
<button class="btn btn-blue" onclick="location.href='/diet/form?dietId=${diet.dietId}'">수정</button>

<form action="/diet/delete" method="post" style="display:inline;">
    <input type="hidden" name="dietId" value="${diet.dietId}">
    <input type="hidden" name="date" value="${diet.logDate}">
    <button class="btn btn-red">식단 삭제</button>
</form>
