<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<h2>내가 쓴 글</h2>

<ul>
<c:forEach var="post" items="${posts}">
    <li>
        <a href="/community/detail/${post.postId}">${post.title}</a>
        <span>(${post.createdAt})</span>
    </li>
</c:forEach>
</ul>