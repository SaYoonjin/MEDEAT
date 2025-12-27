<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<h2>내 뱃지</h2>

<ul>
    <c:forEach var="b" items="${badges}">
        <li style="margin-bottom:10px;">
            <img src="${b.iconPath}" style="width:40px; height:40px;">
            <strong>${b.name}</strong> - ${b.description}
        </li>
    </c:forEach>
</ul>

<a href="/challenge/list">챌린지로 돌아가기</a>
