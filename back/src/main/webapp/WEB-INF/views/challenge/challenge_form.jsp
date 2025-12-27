<%@ page contentType="text/html; charset=UTF-8" %>

<h2>챌린지 추가</h2>

<form action="/challenge/form" method="post">
    <input type="hidden" name="modeType" value="${mode}">

    모드: ${mode} <br>

    카테고리:
    <input type="text" name="category"><br>

    이름:
    <input type="text" name="title"><br>

    설명:<br>
    <textarea name="description" rows="4" cols="40"></textarea><br>

    기간(일):
    <input type="number" name="periodDays"><br>

    난이도:
    <select name="difficulty">
        <option value="EASY">EASY</option>
        <option value="NORMAL">NORMAL</option>
        <option value="HARD">HARD</option>
    </select><br><br>
    
    <label>최대 인원</label>
	<input type="number" name="maxParticipants" min="1" value="10" required>
    

    <button type="submit">생성</button>
</form>

<a href="/challenge/list?mode=${mode}">목록으로</a>