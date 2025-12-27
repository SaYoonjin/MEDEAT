<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<style>
    /* 제목 */
    .community-title {
        font-size: 26px;
        font-weight: 700;
        margin-bottom: 20px;
    }

    /* 모드 탭 */
    .mode-tab button {
        padding: 8px 16px;
        border: 1px solid #bbb;
        border-radius: 6px;
        background: #f7f7f8;
        cursor: pointer;
        font-size: 15px;
        margin-right: 6px;
        transition: 0.2s;
    }
    .mode-tab button:hover {
        background: #e5e7eb;
    }
    .mode-tab .active {
        background: #4CAF50;
        color: white;
        border-color: #4CAF50;
    }

    /* 글쓰기 버튼 */
    .write-btn {
        padding: 8px 14px;
        background: #4CAF50;
        border: none;
        color: white;
        border-radius: 6px;
        cursor: pointer;
        font-size: 15px;
        transition: 0.2s;
    }
    .write-btn:hover {
        background: #43a047;
    }

    /* 게시글 아이템 */
    .post-item {
        padding: 14px 18px;
        border-radius: 10px;
        border: 1px solid #e5e7eb;
        margin-bottom: 12px;
        transition: 0.25s;
        background: white;
    }
    .post-item:hover {
        background: #f9fafb;
        box-shadow: 0px 2px 5px rgba(0,0,0,0.07);
    }

    .post-title {
        font-size: 18px;
        font-weight: bold;
        color: #333;
        text-decoration: none;
    }
    .post-title:hover { text-decoration: underline; }

    .post-meta {
        font-size: 13px;
        color: #777;
        margin-top: 5px;
    }
    .post-stats {
        font-size: 14px;
        color: #444;
        margin-top: 8px;
    }
</style>

<div class="community-title">${mode} 커뮤니티</div>

<!-- ⭐ 모드 탭 -->
<div class="mode-tab" style="margin-bottom:15px; display:flex; gap:8px;">
    <a href="/community/list?mode=EAT">
        <button class="${mode == 'EAT' ? 'active' : ''}">EAT</button>
    </a>
    <a href="/community/list?mode=MEDI">
        <button class="${mode == 'MEDI' ? 'active' : ''}">MEDI-EAT</button>
    </a>
</div>


<!-- ⭐ 글쓰기 버튼 -->
<div style="margin-bottom:20px;">
    <a href="/community/write_form?mode=${mode}">
        <button class="write-btn">글쓰기</button>
    </a>
</div>

<!-- ⭐ 게시글 목록 -->
<div>
<c:forEach var="p" items="${posts}">
    <div class="post-item">

        <!-- 제목 -->
        <a href="/community/detail/${p.postId}" class="post-title">
            ${p.title}
        </a>

        <!-- 작성자 / 날짜 -->
        <div class="post-meta">
            작성자: ${p.writerId} &nbsp;&nbsp; | &nbsp;&nbsp;
            ${p.createdAt}
        </div>

        <!-- 통계 -->
        <div class="post-stats">
            ❤️ ${p.likeCount} &nbsp;&nbsp;
            💬 ${p.commentCount} &nbsp;&nbsp;
            📌 ${p.scrapCount}
        </div>

    </div>
</c:forEach>

<!-- 게시물이 없을 때 -->
<c:if test="${empty posts}">
    <div style="color:#777; padding: 20px 5px;">등록된 게시글이 없습니다.</div>
</c:if>

</div>
