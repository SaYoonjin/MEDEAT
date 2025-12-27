<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>질병 및 복용약 정보 입력</title>
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
        #pregnancy-week-wrapper {
            display: none;
        }
        .week-input {
		    width: 100%;
		    padding: 12px;
		    font-size: 16px;
		    border: 1px solid #aaa;
		    border-radius: 6px;
		    box-sizing: border-box;
		    margin-top: 5px;
		}
        
    </style>
</head>

<body>

<h2>질병 및 복용중인 약 입력</h2>

<form action="/auth/signup_med_info" method="post">

    <label>질병</label>
    <input type="text" name="disease" required>

    <label>복용 중인 약</label>
    <input type="text" name="medicine" required>
    
    <label>임신 여부</label>
	<select name="pregnant" id="pregnant-select" required>
	    <option value="">선택하세요</option>
	    <option value="yes">예</option>
	    <option value="no">아니오</option>
	</select>

    <div id="pregnancy-week-wrapper">
        <label>임신 주차</label>
        <input type="number" name="pregnancyWeek" min="1" max="40" placeholder="몇 주차인지 입력" class="week-input">
    </div>

    <button type="submit">설정 완료</button>
</form>

<script>
document.addEventListener("DOMContentLoaded", () => {
    const select = document.getElementById("pregnant-select");
    const weekWrapper = document.getElementById("pregnancy-week-wrapper");
    const weekInput = document.querySelector("input[name='pregnancyWeek']");

    select.addEventListener("change", function () {
        if (this.value === "yes") {
            weekWrapper.style.display = "block";
            weekInput.required = true;
        } else {
            weekWrapper.style.display = "none";
            weekInput.required = false;
            weekInput.value = "";
        }
    });
});
</script>

</body>
</html>
