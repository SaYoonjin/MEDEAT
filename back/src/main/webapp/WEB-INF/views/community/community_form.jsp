<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<h2>${post != null ? "게시글 수정" : "게시글 작성"}</h2>

<form action="${post != null ? '/community/update' : '/community/write'}"
      method="post"
      enctype="multipart/form-data"
      style="display:flex; flex-direction:column; gap:12px; max-width:600px;">

    <input type="hidden" name="postId" value="${post.postId}">
    <input type="hidden" name="modeType" value="${mode != null ? mode : post.modeType}">

    <label>제목</label>
    <input type="text" name="title" value="${post.title}" required>

    <label>내용</label>
    <textarea name="content" rows="8" required>${post.content}</textarea>

    <label>이미지 업로드</label>
    <input type="file" name="uploadFile">

    <!-- 기존 이미지가 있을 때 미리보기 -->
    <c:if test="${post.imagePath != null}">
        <div style="margin-top:10px;">
            <img src="/uploads/${post.imagePath}" style="max-width:250px;">
        </div>
    </c:if>

    <button type="submit">
        ${post != null ? "수정 완료" : "작성 완료"}
    </button>

</form>