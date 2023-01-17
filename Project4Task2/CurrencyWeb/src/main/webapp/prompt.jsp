<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%= request.getAttribute("doctype") %>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <p>Currency Exchange.</p>
        <form action="CurrencyExchange" method="GET">
            <label for="letter">Type the currency you have.</label>
            <input type="text" name="currencyFrom" value="" /><br>
            <label for="letter">Type the currency you want.</label>
            <input type="text" name="currencyTo" value="" /><br>
            <label for="letter">Type amount you want to exchange.</label>
            <input type="text" name="amountString" value="" /><br>
            <input type="submit" value="Click Here" />
        </form>
    </body>
</html>

