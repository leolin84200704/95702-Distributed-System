<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%-- Name: Leo Lin--%>
<%-- AndrewID: hungfanl--%>
<html>
    <head>
        <title>JSP - Hash</title>
    </head>
    <body>
        <h1><%= "Please insert the string you want to hash!" %></h1>
        <form action="computeHash" method="GET">
            <label for="letter">String you want to hash:</label><br>
            <input type="text" name="hash_value_name"><br>
            <p>Choose the way you want to hash:</p>
            <input type="radio" name="hash_way" value="MD5" checked>
            <label for="MD5">MD5</label><br>
            <input type="radio" name="hash_way" value="SHA-256">
            <label for="SHA-256">SHA-256</label><br>
            <input type="submit" value="HASH"></a>
        </form>
        <br/>
    </body>
</html>