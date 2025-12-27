<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<h2>챌린지 상세</h2>

<h3>${challenge.title}</h3>
<p>모드: ${challenge.modeType}</p>
<p>카테고리: ${challenge.category}</p>
<p>기간: ${challenge.periodDays}일</p>
<p>난이도: ${challenge.difficulty}</p>
<p>설명: ${challenge.description}</p>
<p>참여 인원: ${challenge.currentParticipants} / ${challenge.maxParticipants}</p>
<p>남은 자리: ${challenge.maxParticipants - challenge.currentParticipants}명</p>

<hr/>

<h3>진행 상태</h3>
<p>상태: ${uc.status}</p>
<p>시작일: ${uc.startDate}</p>
<p>종료일: ${uc.endDate}</p>

<hr/>

<h3>일별 로그</h3>

<ul>
    <c:forEach var="log" items="${logs}">
        <li>
            ${log.logDate} - ${log.status} (${log.memo})
        </li>
    </c:forEach>
</ul>

<form action="/challenge/log" method="post">
    <input type="hidden" name="userChallengeId" value="${uc.userChallengeId}">
    상태:
    <select name="status">
        <option value="SUCCESS">성공</option>
        <option value="FAIL">실패</option>
    </select>
    메모: <input type="text" name="memo">
    <button type="submit">로그 추가</button>
</form>

<a href="/challenge/list?mode=${challenge.modeType}">목록으로</a>