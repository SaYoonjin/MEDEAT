<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>회원가입</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            max-width: 600px;
            margin: 40px auto;
        }
        h2 {
            text-align: center;
        }
        form {
            display: flex;
            flex-direction: column;
            gap: 12px;
        }
        input, select {
            padding: 8px;
            font-size: 15px;
        }
        button {
            padding: 10px;
            cursor: pointer;
            background: #4CAF50;
            color: #fff;
            border: none;
            font-size: 16px;
        }
        button:hover {
            background: #45a049;
        }
    </style>
</head>
<body>

<h2>회원가입</h2>

<form action="/auth/signup" method="post">
    <label>아이디</label>
    <input type="text" name="loginId" required>

    <label>비밀번호</label>
    <input type="password" name="password" required>

    <label>이름</label>
    <input type="text" name="name" required>

    <label>닉네임</label>
    <input type="text" name="nickname">

    <label>이메일</label>
    <input type="email" name="email">

    <label>전화번호</label>
    <input type="text" name="phone">

    <label>성별</label>
    <select name="gender">
        <option value="">선택</option>
        <option value="M">남성</option>
        <option value="F">여성</option>
    </select>

    <label>나이</label>
    <input type="number" name="age" min="1">

    <label>키(cm)</label>
    <input type="number" step="0.1" name="height">

    <label>몸무게(kg)</label>
    <input type="number" step="0.1" name="weight">

    <label>목표 유형</label>
    <select name="goalType">
        <option value="">선택</option>
        <option value="diet">다이어트</option>
        <option value="keep">유지</option>
        <option value="health">건강</option>
    </select>


    <button type="submit">회원가입</button>
</form>

</body>
</html>