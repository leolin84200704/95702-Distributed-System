<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%= request.getAttribute("doctype") %>
<%-- Name: Leo Lin--%>
<%-- AndrewID: hungfanl--%>
<html>
    <body>
        <h1>Distributed Systems Class Clicker</h1>
        <% System.out.println(request.getAttribute("sum")); %>
        <% if(request.getAttribute("sum").equals(0)) { %>
           <h1> There are currently no result </h1> <br>
        <% } else {%>
        <h1>A: <%= request.getAttribute("numA")%></h1>
        <h1>B: <%= request.getAttribute("numB")%></h1>
        <h1>C: <%= request.getAttribute("numC")%></h1>
        <h1>D: <%= request.getAttribute("numD")%></h1>
            <p> </p>
        <% } %>
    </body>
</html>
