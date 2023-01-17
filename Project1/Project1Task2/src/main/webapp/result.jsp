<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%= request.getAttribute("doctype") %>
<%-- Name: Leo Lin--%>
<%-- AndrewID: hungfanl--%>
<html>
    <head>
        <title>HTML Elements Referencesas</title>
    </head>
    <body>
        <font size="7">State: <%= request.getAttribute("state")%> </font>
        <h1>Population: <%= request.getAttribute("population")%></h1>
        <h1>Nickname: <%= request.getAttribute("nickname")%></h1>
        <h1>Capital: <%= request.getAttribute("capital")%></h1>
        <h1>Song: <%= request.getAttribute("song")%></h1>
        <h1>Flower: </h1>
        <img src="<%= request.getAttribute("flowerURL")%>">
        <h1>Credit: https://statesymbolsusa.org/categories/flower</h1>
        <h1>Flag: </h1>
        <img src="<%= request.getAttribute("flagURL")%>">
        <h1>Credit: https://states101.com/flags</h1>
         <form action="getAnStateInformation" method="GET">
            <label for="letter">Select another state.</label>
             <p> </p>
             <input type="submit" value="Continue" />
        </form>
    </body>
</html>

