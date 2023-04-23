<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.ArrayList" %><%--
  Created by IntelliJ IDEA.
  User: Leo, Yash
  Date: 2022/11/14
  Time: 7:40 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>DashBoard</title>
</head>
<body>
    <h1>Dashboard Results</h1>
    The Top 3 currencies from which user wants to convert from-<br>
    <% ArrayList<String> fromArray = (ArrayList<String>) request.getAttribute("fromArray");%>
    <% fromArray.size();%>
    <% for (int i = 0; i < fromArray.size(); i++) { %>
    <%= fromArray.get(i) %><br>
    <% } %>
    <br>The Top 3 currencies To which user wants to convert to-<br>
    <% ArrayList<String> toArray = (ArrayList<String>) request.getAttribute("toArray");%>
    <% toArray.size();%>
    <% for (int i = 0; i < toArray.size(); i++) { %>
    <%= toArray.get(i) %><br>
    <% } %>
    <br>The Top 3 dates on which user converts currency -<br>
    <% ArrayList<String> freqDates = (ArrayList<String>) request.getAttribute("freqDates");%>
    <% freqDates.size();%>
    <% for (int i = 0; i < freqDates.size(); i++) { %>
    <%= freqDates.get(i) %><br>
    <% } %>
    <br>MongoDB Info <br>
    <br>Date &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; User&nbsp; &nbsp; &nbsp; &nbsp; Base Amount To &nbsp; &nbsp;  Get<br>
    <% ArrayList<String> mongo = (ArrayList<String>) request.getAttribute("result");%>
    <% mongo.size();%>
    <% for (int i = 0; i < mongo.size(); i++) { %>
    <%= mongo.get(i) %><br>
    <% } %>
    <br>
</body>
</html>