<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<h2>약 등록 / 수정</h2>

<form action="/medication/save" method="post">

    <input type="hidden" name="medicationId" value="${med.medicationId}">

    약 이름: <input type="text" name="drugName" value="${med.drugName}">
    <br>

    성분: <input type="text" name="ingredient" value="${med.ingredient}">
    <br>

    용량: <input type="text" name="dose" value="${med.dose}">
    <br>

    복용 시간: <input type="text" name="intakeTime" value="${med.intakeTime}">
    <br>

    간격(시간): <input type="number" name="intervalHour" value="${med.intervalHour}">
    <br>

    메모: <input type="text" name="memo" value="${med.memo}">
    <br><br>

    <button type="submit">저장</button>
</form>