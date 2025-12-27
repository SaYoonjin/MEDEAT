<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>회원가입 완료</title>

    <style>
        body {
            margin: 0;
            padding: 0;
            font-family: "Noto Sans KR", sans-serif;
            background: #f5f7fa;
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
        }

        .container {
            text-align: center;
            background: #ffffff;
            padding: 40px 50px;
            border-radius: 16px;
            box-shadow: 0 4px 12px rgba(0,0,0,0.08);
            width: 400px;
        }

        h2 {
            margin-bottom: 20px;
            color: #333;
            font-size: 1.7rem;
        }

        p {
            font-size: 1rem;
            color: #555;
            margin-bottom: 30px;
        }

        .btn {
            display: block;
            width: 100%;
            padding: 14px 0;
            margin: 10px 0;
            font-size: 1.05rem;
            border-radius: 10px;
            border: none;
            cursor: pointer;
            color: white;
            text-decoration: none;
            transition: 0.2s ease;
        }

        .btn.eat {
            background-color: #4CAF50;
        }
        .btn.eat:hover {
            background-color: #3b8a3f;
        }

        .btn.medi {
            background-color: #3c7dff;
        }
        .btn.medi:hover {
            background-color: #2e64d6;
        }
    </style>
</head>

<body>

<div class="container">
    <h2>회원가입이 완료되었습니다 🎉</h2>
    <p>원하시는 모드를 선택하여 MEDEAT을 시작해보세요.</p>
	
    <a href="/auth/dashboard_eat" class="btn eat">EAT 모드로 시작하기</a>
    <a href="/auth/signup_med_info" class="btn medi">MEDI-EAT 모드로 시작하기</a>
</div>

</body>
</html>