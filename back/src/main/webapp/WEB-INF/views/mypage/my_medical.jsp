<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<h2>질병 및 복용 약 정보</h2>

<h3>📌 질병 리스트</h3>

<!-- 질병 검색 -->
<input type="text" id="diseaseKeyword" placeholder="질병 검색">
<button type="button" onclick="searchDisease()">검색</button>

<ul id="searchResult"></ul>

<h4>내 질병</h4>
<ul id="myDiseaseList">
    <c:forEach var="d" items="${diseases}">
        <li>
            ${d.diseaseName}
            <button type="button" onclick="removeDisease(${d.diseaseId})">삭제</button>
        </li>
    </c:forEach>
</ul>

<hr/>

<h3>💊 복용 중인 약</h3>
<table border="1">
    <tr>
        <th>약 이름</th>
        <th>성분</th>
        <th>용량</th>
        <th>복용 시간</th>
        <th>간격</th>
        <th>메모</th>
    </tr>
    <c:forEach var="m" items="${medications}">
        <tr>
            <td>${m.drugName}</td>
            <td>${m.ingredient}</td>
            <td>${m.dose}</td>
            <td>${m.intakeTime}</td>
            <td>${m.intervalHour}</td>
            <td>${m.memo}</td>
        </tr>
    </c:forEach>
</table>

<script>
    // 질병 검색
    function searchDisease() {
        const keyword = document.getElementById('diseaseKeyword').value;
        if (!keyword) return;

        fetch('/disease/search?keyword=' + encodeURIComponent(keyword))
            .then(res => res.json())
            .then(list => {
                const ul = document.getElementById('searchResult');
                ul.innerHTML = '';
                list.forEach(d => {
                    const li = document.createElement('li');
                    li.textContent = d.diseaseName + ' ';
                    const btn = document.createElement('button');
                    btn.textContent = '추가';
                    btn.onclick = function () { addDisease(d.diseaseId); };
                    li.appendChild(btn);
                    ul.appendChild(li);
                });
            });
    }

    // 질병 추가
    function addDisease(diseaseId) {
        fetch('/disease/add', {
            method: 'POST',
            headers: {'Content-Type': 'application/x-www-form-urlencoded'},
            body: 'diseaseId=' + diseaseId
        }).then(() => location.reload());
    }

    // 질병 삭제
    function removeDisease(diseaseId) {
        fetch('/disease/remove', {
            method: 'POST',
            headers: {'Content-Type': 'application/x-www-form-urlencoded'},
            body: 'diseaseId=' + diseaseId
        }).then(() => location.reload());
    }
</script>
