<%@ include file="/includes/taglibs.jsp"%>
<%@ page language="java"%>

<html>

<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<title>Refund Approval</title>
</head>

<body onload="refreshInbox()">

	<div class="formmainbox">

		<div class="subheadnew">Refund Approval</div>

		<div align="center">

			<span class="mandatory1"> <s:actionerror /> <s:actionmessage />

			</span>

			<s:property value="%{message}" />

			<br />
			<br /> <input type="button" value="Close" onclick="window.close();"
				class="button" />

		</div>

	</div>

</body>
</html>