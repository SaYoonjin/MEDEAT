<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>


<style>
    .profile-container {
        width: 150px;
        height: 150px;
        margin: 20px auto;           /* 가운데 정렬 */
        border-radius: 50%;          /* 동그랗게 */
        overflow: hidden;            /* 내부 이미지 영역 제한 */
        border: 3px solid #ddd;      /* 테두리 */
        display: flex;
        justify-content: center;
        align-items: center;
    }

    .profile-img {
        width: 100%;
        height: 100%;
        object-fit: cover;           /* 꽉 차게 */
    }
</style>


<!-- 프로필 사진 -->
<div class="profile-container">
    <img src="/resources/img/IMG_1666-edited-scaled.jpg" alt="profile" class="profile-img">
</div>

<h2>내 정보</h2>

<p>이름: ${user.name}</p>
<p>닉네임: ${user.nickname}</p>
<p>이메일: ${user.email}</p>
<p>휴대폰: ${user.phone}</p>
<p>성별: ${user.gender}</p>
<p>나이: ${user.age}</p>
<p>키: ${user.height}</p>
<p>몸무게: ${user.weight}</p>
<p>목표: ${user.goalType}</p>

<!-- ⭐ 임신 정보 -->
<p>임신 여부: 
    <c:choose>
        <c:when test="${user.pregnantStatus == 'yes'}">예</c:when>
        <c:when test="${user.pregnantStatus == 'no'}">아니오</c:when>
        <c:otherwise>미입력</c:otherwise>
    </c:choose>
</p>

<c:if test="${user.pregnantStatus == 'yes'}">
    <p>임신 주차: ${user.pregnancyWeek}주</p>
</c:if>

<hr>

<button onclick="location.href='/mypage/edit'">수정하기</button>
<br><br>

<a href="/mypage/delete" onclick="return confirm('정말 탈퇴하시겠습니까?')">회원 탈퇴</a>
<br><br>

<!-- ⭐ 홈화면으로 돌아가기 -->
<button onclick="location.href='/auth/dashboard_medeat'">홈으로 돌아가기</button>
