<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<h2>저장한 글</h2>

<ul>
<c:forEach var="post" items="${scraps}">
    <li>
        <a href="/community/detail/${post.postId}">${post.title}</a>
        <span>(${post.createdAt})</span>
    </li>
</c:forEach>
</ul>