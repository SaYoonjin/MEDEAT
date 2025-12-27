<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>로그인 | MEDEAT</title>
    <style>
        body {
            margin: 0;
            padding: 0;
            background: #f7faff;
            font-family: Arial, sans-serif;
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
        }

        .login-container {
            width: 380px;
            background: #ffffff;
            padding: 35px 40px;
            border-radius: 12px;
            box-shadow: 0 4px 14px rgba(0, 0, 0, 0.08);
        }

        .login-title {
            font-size: 28px;
            font-weight: bold;
            text-align: center;
            margin-bottom: 30px;
            color: #2c3e50;
        }

        .input-label {
            font-size: 14px;
            margin-bottom: 6px;
            display: block;
            color: #555;
        }

        .input-field {
            width: 100%;
            padding: 12px;
            margin-bottom: 18px;
            border-radius: 8px;
            border: 1px solid #ccd4e0;
            font-size: 15px;
        }

        .btn-login {
            width: 100%;
            padding: 14px;
            background: #4f84ff;
            color: white;
            border: none;
            border-radius: 8px;
            font-size: 16px;
            cursor: pointer;
            margin-top: 10px;
        }

        .btn-login:hover {
            background: #3c66d6;
        }

        .signup-link {
            margin-top: 15px;
            text-align: center;
            font-size: 14px;
        }

        .signup-link a {
            color: #4f84ff;
            text-decoration: none;
        }

        .error-msg {
            text-align: center;
            color: red;
            margin-bottom: 15px;
            font-size: 14px;
        }
    </style>
</head>

<body>

<div class="login-container">
    <div class="login-title">로그인</div>

    <!-- 에러 메시지 표시 -->
    <c:if test="${not empty msg}">
        <div class="error-msg">${msg}</div>
    </c:if>

    <form action="/auth/login" method="post">

        <label class="input-label">아이디</label>
        <input type="text" name="loginId" class="input-field" placeholder="아이디 입력">

        <label class="input-label">비밀번호</label>
        <input type="password" name="password" class="input-field" placeholder="비밀번호 입력">

        <button type="submit" class="btn-login">로그인</button>
    </form>

    <div class="signup-link">
        아직 회원이 아니신가요?  
        <a href="/auth/signup">회원가입하기</a>
    </div>
</div>

</body>
</html>