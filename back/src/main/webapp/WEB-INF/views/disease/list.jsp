<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<h2 style="margin-bottom: 15px;">나의 질병 관리</h2>

<p style="color:#555; margin-bottom: 20px;">
    현재 등록해둔 질병 목록입니다.
</p>

<!-- 질병 없음 -->
<c:if test="${empty diseases}">
    <div style="padding: 20px; color: #888;">
        등록된 질병이 없습니다.
    </div>
</c:if>

<!-- 질병 목록 출력 -->
<c:if test="${not empty diseases}">
    <div style="display: flex; flex-wrap: wrap; gap: 10px;">
        <c:forEach var="d" items="${diseases}">
            <div style="
                padding: 10px 16px;
                background: #f8f8f8;
                border-radius: 20px;
                border: 1px solid #ddd;
                display: flex;
                align-items: center;
                font-size: 14px;
            ">
                <span>${d.diseaseName}</span>

                <!-- 삭제 버튼 -->
                <form action="/disease/remove" 
                      method="post" 
                      style="margin-left: 12px;">
                    <input type="hidden" name="userDiseaseId" value="${d.userDiseaseId}">
                    <button type="submit"
                            style="
                                background:none;
                                border:none;
                                cursor:pointer;
                                font-size: 14px;
                                color: red;
                                font-weight: bold;
                            ">
                        ✕
                    </button>
                </form>
            </div>
        </c:forEach>
    </div>
</c:if>

<!-- 추가 버튼 -->
<div style="margin-top: 30px;">
    <button onclick="location.href='/mypage/my_medical'"
            style="
                padding:8px 14px;
                background:#4CAF50;
                color:white;
                border:none;
                border-radius:4px;
                cursor:pointer;
            ">
        ➕ 질병 추가하러 가기
    </button>
</div>
