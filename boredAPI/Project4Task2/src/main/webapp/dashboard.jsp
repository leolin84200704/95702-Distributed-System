<% // Name: Hsiu-Yuan Yang %>
<% // AndrewID: hsiuyuay %>
<% // Email: hsiuyuay@andrew.cmu.edu %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%= request.getAttribute("doctype") %>

<html>

<head>
    <title>Activity Recommendation Dashboard</title>
</head>
<body style="font-family:'Roboto Light'; margin-left: 10px; margin-right:10px;">
<!--title on the page-->
<h1 style = "text-align: center;padding: 20px; background-color:cornflowerblue; font-family: sans-serif;">Activity Recommendation Statistics</h1>

<!--the operation analytics of the web servlet-->
<h3>Some Fun Facts About Us:</h3>
<!--info for top user-->
<p>1. The top user goes to: <b><i><%= request.getAttribute("topUser")%></i></b>, who visited our app for <b><i><%= request.getAttribute("topUserCount")%></i></b> times.
    Amazing, how boring your life must have been! Don't worry, we will always try our best to help you <3 </p>

<!--info for top request date-->
<p>2. The top request date is: <b><i><%= request.getAttribute("topRequestDate")%></i></b>, which has a total of <b><i><%= request.getAttribute("topDateCount")%></i></b> requests.
    Oh my god! What exactly happened on that day? But it's always good to have more visits so that we can get advertisement revenues :D </p>

<!--info for response successful rate-->
<p>3. We are pleased to announce that we have successfully helped <b><i><%= request.getAttribute("successfulRate")%></i></b>% of the requests. Compliment us! </p>

<!--info for activity types-->
<p>4. The most recommended activity type is/are: <b><i><%= request.getAttribute("topActivityType")%></i></b>, which has/have been recommended <b><i><%= request.getAttribute("topTypeCount")%></i></b>
    times. People's taste is always out of expectation :P </p>

<!--info for average process time-->
<p>5. The average processing time of our app is: <b><i><%= request.getAttribute("avgProcessTime")%></i></b> (ms). WE ARE FAST! WE ARE THE BEST! </p>
<br><br>

<!--the logs of the web servlet-->
<h3>Log Table Is Below In Case You Would Like To Invest In Us :) </h3>
<table  style="border: 1px solid; margin:5px; width:100%;">

    <thead style="border: 1px solid; padding: 15px; text-align: center; background-color:lightgrey; font-weight:bold">

        <th>Username</th>
        <th>Timestamp</th>
        <th>Response Generated?</th>
        <th>Activity</th>
        <th>Type</th>
        <th>Number of Participants</th>
        <th>Estimated Price</th>
        <th>Request Processing Time (ms)</th>
    </thead>
    <tbody style="border: 1px solid; padding: 15px; text-align: center">
        <%= request.getAttribute("logValues")%>
    </tbody>
</table>

</body>
</html>

