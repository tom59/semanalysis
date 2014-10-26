<%@ page contentType="text/html; charset=utf-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags/form" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>

<head>

    <title>Semantic Analysis</title>
</head>
<body>
    <h1>Semantic Analysis</h1>

   <spring:form modelAttribute="searchRequest"
        action="search" method="get" acceptCharset="UTF-8"  >

        <table>
            <tbody>
            <tr>
            <td>Text</td>
            <td>
                <spring:textarea class="inputfield" path="text" rows="5" cols="50" />
            </td>
            </tr>
            </tbody>
         </table>
	     <input type="submit" />
     </spring:form>

    <c:if test="${not empty searchResult}">

        <table>
            <thead>
                <tr>
                    <th>Word</th>
                    <th>Score</th>
                </tr>
            </thead>
		<c:forEach var="word" items="${searchResult.summarizedContent}">
			<tr>
	            <td><c:out value="${word.key}" /></td>
	            <td><c:out value="${word.value}" /></td>
			</tr>
		</c:forEach>
		</table>
    </c:if>
</body>
</html>
