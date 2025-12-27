<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<h2>챌린지 목록</h2>

<!-- 모드 선택 버튼 -->
<a href="/challenge/list?mode=EAT">EAT Mode</a> |
<a href="/challenge/list?mode=MEDI_EAT">MEDI-EAT Mode</a>

<h3>🔥 인기 챌린지</h3>
<ul>
    <c:forEach var="c" items="${popularList}">
        <li>
            ${c.title} (${c.difficulty}) - 기간: ${c.periodDays}일
        </li>
    </c:forEach>
</ul>

<h3>⏳ 진행 중인 챌린지</h3>
<ul>
    <c:forEach var="uc" items="${ongoingList}">
        <li>
            챌린지명: ${uc.title}
            (상세 보기: <a href="/challenge/detail/${uc.userChallengeId}">이동</a>)
            <form action="/challenge/giveup" method="post" style="display:inline;">
                <input type="hidden" name="userChallengeId" value="${uc.userChallengeId}">
                <input type="hidden" name="mode" value="${mode}">
                <button type="submit">포기</button>
            </form>
        </li>
    </c:forEach>
</ul>

<h3>✅ 참여 가능한 챌린지</h3>
<ul>
    <c:forEach var="c" items="${availableList}">
        <li>
            ${c.title} - ${c.description}
            (난이도: ${c.difficulty}, 기간: ${c.periodDays}일)
            <form action="/challenge/join" method="post" style="display:inline;">
                <input type="hidden" name="challengeId" value="${c.challengeId}">
                <input type="hidden" name="mode" value="${mode}">
                <button type="submit">참여</button>
            </form>
        </li>
    </c:forEach>
</ul>

<a href="/challenge/form?mode=${mode}">챌린지 추가하기</a> |
<a href="/challenge/badges">내 뱃지 보기</a>