<%@ page contentType="text/html; charset=UTF-8" %>

<h2>내 정보 수정</h2>

<form action="/mypage/update" method="post">

    <input type="hidden" name="userId" value="${user.userId}">

    이름: <input type="text" name="name" value="${user.name}"><br>
    닉네임: <input type="text" name="nickname" value="${user.nickname}"><br>
    이메일: <input type="text" name="email" value="${user.email}"><br>
    휴대폰: <input type="text" name="phone" value="${user.phone}"><br>
    성별: <input type="text" name="gender" value="${user.gender}"><br>
    나이: <input type="number" name="age" value="${user.age}"><br>
    키: <input type="number" name="height" value="${user.height}"><br>
    몸무게: <input type="number" name="weight" value="${user.weight}"><br>
    목표: <input type="text" name="goalType" value="${user.goalType}"><br>

    <!-- ⭐ 임신 여부 -->
    임신 여부:
    <select name="pregnantStatus" id="pregnant-select">
        <option value="">선택하세요</option>
        <option value="yes" ${user.pregnantStatus == 'yes' ? 'selected' : ''}>예</option>
        <option value="no"  ${user.pregnantStatus == 'no'  ? 'selected' : ''}>아니오</option>
    </select>
    <br><br>

    <!-- ⭐ 임신 주차 -->
    <div id="pregnancy-week-wrapper" style="display:none;">
        임신 주차:
        <input type="number" name="pregnancyWeek"
               min="1" max="40"
               value="${user.pregnancyWeek != null ? user.pregnancyWeek : ''}">
        <br><br>
    </div>

    <button type="submit">저장</button>
</form>

<hr>

<button onclick="location.href='/mypage/info'">뒤로가기</button>


<!-- ⭐ JavaScript – 임신 여부에 따라 주차 입력칸 자동 표시 -->
<script>
    const pregnantSelect = document.getElementById("pregnant-select");
    const weekWrapper = document.getElementById("pregnancy-week-wrapper");

    function togglePregnancyWeek() {
        if (pregnantSelect.value === 'yes') {
            weekWrapper.style.display = 'block';
        } else {
            weekWrapper.style.display = 'none';
        }
    }

    // 드롭다운 변경 시 실행
    pregnantSelect.addEventListener("change", togglePregnancyWeek);

    // 페이지 로딩 시 자동 실행 (기존 선택값에 따라 표시)
    window.onload = togglePregnancyWeek;
</script>
