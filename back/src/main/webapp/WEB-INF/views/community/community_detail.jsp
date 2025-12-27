<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<h2>${post.title}</h2>

<div style="color:#555; margin-bottom:15px;">
    <b>${post.writerId}</b> &nbsp; | &nbsp; ${post.createdAt}
</div>

<!-- 게시글 수정/삭제 (작성자만) -->
<c:if test="${loginUserId == post.userId}">
    <div style="margin-bottom:15px;">
        <a href="/community/edit/${post.postId}">
            <button>게시글 수정</button>
        </a>

        <form action="/community/delete" method="post" style="display:inline;">
            <input type="hidden" name="postId" value="${post.postId}">
            <input type="hidden" name="mode" value="${post.modeType}">
            <button onclick="return confirm('정말 삭제할까요?')">게시글 삭제</button>
        </form>
    </div>
</c:if>

<!-- 게시글 내용 -->
<div style="margin-bottom:20px; font-size:16px;">
    ${post.content}
</div>

<!-- 이미지 -->
<c:if test="${post.imagePath != null}">
    <div style="margin-top:20px;">
        <img src="/uploads/${post.imagePath}" style="max-width:400px;">
    </div>
</c:if>

<hr>

<!-- 좋아요 / 스크랩 -->
<div style="margin:10px 0;">
    <button onclick="toggleLike(${post.postId})">
        ❤️ 좋아요 (${post.likeCount})
    </button>

    <button onclick="toggleScrap(${post.postId})">
        📌 스크랩 (${post.scrapCount})
    </button>
</div>

<hr>

<!-- 댓글 목록 -->
<h3>댓글</h3>

<ul style="list-style:none; padding-left:0">
<c:forEach var="c" items="${comments}">
    <li style="margin-bottom:20px;">

        <!-- 일반 보기 -->
        <div id="comment-view-${c.commentId}">
            <b>${c.writerId}</b> | ${c.createdAt} <br>
            ${c.content}

            <!-- 본인 댓글일 때 수정/삭제 -->
            <c:if test="${loginUserId == c.userId}">
                <button onclick="showEditForm(${c.commentId})">수정</button>

                <form action="/community/comment/delete" method="post" style="display:inline;">
                    <input type="hidden" name="commentId" value="${c.commentId}">
                    <input type="hidden" name="postId" value="${post.postId}">
                    <button onclick="return confirm('삭제할까요?')">삭제</button>
                </form>
            </c:if>
        </div>

        <!-- 수정 폼 -->
        <div id="comment-edit-${c.commentId}" style="display:none; margin-top:10px;">
            <form action="/community/comment/update" method="post">
                <input type="hidden" name="commentId" value="${c.commentId}">
                <input type="hidden" name="postId" value="${post.postId}">

                <input type="text" name="content" value="${c.content}" style="width:250px;">
                <button type="submit">저장</button>
                <button type="button" onclick="hideEditForm(${c.commentId})">취소</button>
            </form>
        </div>

    </li>
</c:forEach>
</ul>

<!-- 댓글 작성 -->
<form action="/community/comment" method="post" style="margin-top:20px;">
    <input type="hidden" name="postId" value="${post.postId}">
    <input type="text" name="content" placeholder="댓글 입력" style="width:250px;">
    <button type="submit">댓글쓰기</button>
</form>

<script>
function showEditForm(id){
    document.getElementById("comment-view-" + id).style.display = "none";
    document.getElementById("comment-edit-" + id).style.display = "block";
}

function hideEditForm(id){
    document.getElementById("comment-view-" + id).style.display = "block";
    document.getElementById("comment-edit-" + id).style.display = "none";
}

function toggleLike(id){
    fetch("/community/like",{
        method:"POST",
        headers:{"Content-Type":"application/x-www-form-urlencoded"},
        body:"postId="+id
    }).then(()=>location.reload());
}

function toggleScrap(id){
    fetch("/community/scrap",{
        method:"POST",
        headers:{"Content-Type":"application/x-www-form-urlencoded"},
        body:"postId="+id
    }).then(()=>location.reload());
}
</script>