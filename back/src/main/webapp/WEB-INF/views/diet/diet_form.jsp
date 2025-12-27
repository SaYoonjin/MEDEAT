<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<h3>식단 기록 입력</h3>

<form action="/diet/save" method="post" enctype="multipart/form-data">

    <c:if test="${not empty log.dietId}">
    	<input type="hidden" name="dietId" value="${log.dietId}">

	</c:if>

    <!-- 날짜 -->
    <label>날짜</label>
    <input type="date" name="logDate" value="${log.logDate}" required/><br/>

    <!-- 끼니 -->
    <label>끼니</label>
    <select name="mealTime" required>
        <option value="">선택</option>

        <option value="아침"
            <c:if test="${log.mealTime eq '아침'}">selected="selected"</c:if>>아침</option>

        <option value="점심"
            <c:if test="${log.mealTime eq '점심'}">selected="selected"</c:if>>점심</option>

        <option value="저녁"
            <c:if test="${log.mealTime eq '저녁'}">selected="selected"</c:if>>저녁</option>

        <option value="간식"
            <c:if test="${log.mealTime eq '간식'}">selected="selected"</c:if>>간식</option>

        <option value="야식"
            <c:if test="${log.mealTime eq '야식'}">selected="selected"</c:if>>야식</option>
    </select>
    <br/>

    <!-- 메모 -->
    <label>메모</label><br/>
    <textarea name="memo" rows="2" cols="60">${log.memo}</textarea><br/>

    <!-- 사진 -->
    <label>사진 첨부</label>
    <input type="file" name="photo"/><br/>

    <input type="hidden" name="photoPath" value="${log.photoPath}">

    <c:if test="${not empty log.photoPath}">
        <img src="${log.photoPath}" style="max-width:150px; max-height:150px;"/><br/>
    </c:if>

    <hr/>

    <!-- 음식 검색 -->
    <div>
        <label>음식 검색</label>
        <input type="text" id="food-search" placeholder="예: 김치, 밥"/>
        <input type="button" value="검색" onclick="searchFood()">

        <div id="food-search-result"
             style="max-height:200px; overflow-y:auto; border:1px solid #ccc; margin-top:5px;"></div>
    </div>

    <hr/>

    <!-- 음식 리스트 -->
    <h4>선택한 음식</h4>
    <table id="selected-food-table" border="1" cellpadding="4" cellspacing="0" style="width:100%;">
        <tr>
            <th>음식명</th>
            <th>양(g)</th>
            <th>칼로리</th>
            <th>탄</th>
            <th>단</th>
            <th>지</th>
            <th>삭제</th>
        </tr>

        <c:forEach var="it" items="${log.items}">
            <tr>
                <td>
                    ${it.foodName}
                    <input type="hidden" name="itemFoodId" value="${it.foodId}"/>
                    <input type="hidden" name="itemFoodName" value="${it.foodName}"/>
                </td>
                <td><input type="number" name="itemGram" value="${it.gram}" style="width:60px;"/></td>
                <td><input type="number" name="itemCalorie" value="${it.calorie}" style="width:70px;"/></td>
                <td><input type="number" step="0.1" name="itemCarb" value="${it.carb}" style="width:60px;"/></td>
                <td><input type="number" step="0.1" name="itemProtein" value="${it.protein}" style="width:60px;"/></td>
                <td><input type="number" step="0.1" name="itemFat" value="${it.fat}" style="width:60px;"/></td>
                <td><button type="button" onclick="removeRow(this)">X</button></td>
            </tr>
        </c:forEach>
    </table>

    <br/>
    <button type="submit">저장</button>
</form>

<script>
function searchFood(){
    const keyword = document.getElementById("food-search").value.trim();
    if (!keyword) {
        alert("검색어를 입력하세요");
        return;
    }
    fetch("/food/search?keyword=" + encodeURIComponent(keyword))
        .then(res => res.json())
        .then(list => {
            const area = document.getElementById("food-search-result");
            area.innerHTML = "";
            if (list.length === 0) {
                area.innerHTML = "<div style='padding:4px;'>검색 결과가 없습니다.</div>";
                return;
            }
            list.forEach(f => {
                const div = document.createElement("div");
                div.style.padding = "4px";
                div.style.borderBottom = "1px solid #eee";
                div.style.cursor = "pointer";
                div.onclick = () => addFood(f);
                div.innerHTML =
                    "<b>" + f.name + "</b> (" +
                    (f.calorie || 0) + "kcal / " +
                    "탄:" + (f.carb || 0) + " / " +
                    "단:" + (f.protein || 0) + " / " +
                    "지:" + (f.fat || 0) + ")";
                area.appendChild(div);
            });
        });
}

function addFood(f){
    const table = document.getElementById("selected-food-table");
    const tr = document.createElement("tr");

    const gramValue = f.servingGram ? f.servingGram : 100;

    tr.innerHTML =
        '<td>'
        + f.name +
        '<input type="hidden" name="itemFoodId" value="' + (f.foodId || '') + '"/>'
        + '<input type="hidden" name="itemFoodName" value="' + f.name + '"/>'
        + '</td>'
        + '<td><input type="number" name="itemGram" value="' + gramValue + '" style="width:60px;"/></td>'
        + '<td><input type="number" name="itemCalorie" value="' + (f.calorie || 0) + '" style="width:70px;"/></td>'
        + '<td><input type="number" step="0.1" name="itemCarb" value="' + (f.carb || 0) + '" style="width:60px;"/></td>'
        + '<td><input type="number" step="0.1" name="itemProtein" value="' + (f.protein || 0) + '" style="width:60px;"/></td>'
        + '<td><input type="number" step="0.1" name="itemFat" value="' + (f.fat || 0) + '" style="width:60px;"/></td>'
        + '<td><button type="button" onclick="removeRow(this)">X</button></td>';

    table.appendChild(tr);
}

function removeRow(btn){
    btn.closest("tr").remove();
}
</script>
