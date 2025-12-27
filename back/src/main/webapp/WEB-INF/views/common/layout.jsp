<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>Medeat</title>
    <style>
        body { margin:0; font-family: Arial, sans-serif; }
        .layout-wrapper {
            display: flex;
            height: 100vh;
            overflow: hidden;
        }
        .sidebar-area {
            width: 220px;
            background: #f5f6fa;
            border-right: 1px solid #ddd;
        }
        .content-area {
            flex-grow: 1;
            padding: 30px;
            overflow-y: auto;
        }
    </style>
</head>

<body>

    <!-- 상단 헤더 -->
    <jsp:include page="header.jsp" />

    <div class="layout-wrapper">

        <!-- 사이드바 -->
        <div class="sidebar-area">
            <jsp:include page="${sidebarPath}" />
        </div>

        <!-- 실제 페이지 내용이 들어갈 자리 -->
        <div class="content-area">
            <jsp:include page="${contentPage}" />
        </div>


    </div>

</body>
</html>