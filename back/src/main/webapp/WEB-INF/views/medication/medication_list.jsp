<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<h2>복용 중인 약</h2>

<a href="/medication/form">약 추가</a>

<table border="1">
    <tr>
        <th>이름</th>
        <th>성분</th>
        <th>용량</th>
        <th>시간</th>
        <th>간격</th>
        <th>메모</th>
        <th>관리</th>
    </tr>

    <c:forEach var="m" items="${list}">
        <tr>
            <td>${m.drugName}</td>
            <td>${m.ingredient}</td>
            <td>${m.dose}</td>
            <td>${m.intakeTime}</td>
            <td>${m.intervalHour}</td>
            <td>${m.memo}</td>
            <td>
                <a href="/medication/form?id=${m.medicationId}">수정</a>
                <a href="/medication/delete/${m.medicationId}">삭제</a>
            </td>
        </tr>
    </c:forEach>
</table>