<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>□□□ 나의 JSP page □□□</title>
<!-- Latest compiled and minified CSS -->
<link rel="stylesheet"
	href="https://maxcdn.bootstrapcdn.com/bootstrap/4.4.1/css/bootstrap.min.css">
<!-- jQuery library -->
<script src="https://code.jquery.com/jquery-latest.min.js"></script>
<!-- Popper JS -->
<script
	src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.16.0/umd/popper.min.js"></script>
<!-- Latest compiled JavaScript -->
<script
	src="https://maxcdn.bootstrapcdn.com/bootstrap/4.4.1/js/bootstrap.min.js"></script>
<style>
body {
	border: 1px solid #aaa;
}
</style>
<script>
	$(function() {
		$("#btn-writer").click(function() {
			document.location.href = '${rootPath}/bbs/write'
		})
	})
</script>
</head>
<header class="jumbotron text-center">
	<h3>Build Board System</h3>
</header>
<ul class="nav">
	<li class="nav-item"><a class="nav-link" href="/">Home</a></li>
	<li class="nav-item justify-content-end"><a class="nav-link"
		href="/member/login">로그인</a></li>
	<li class="nav-item"><a class="nav-link" href="/member/join">회원가입</a></li>
</ul>
<body class="container">

	<div class="input-group">
		<div class="input-group-btn">
			<button class="btn btn-default" id="btn-writer" type="button">게시판작성</button>
		</div>
	</div>
</body>
</html>