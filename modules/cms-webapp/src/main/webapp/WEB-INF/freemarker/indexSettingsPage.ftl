[#ftl]
<html>
<head>
    <title>Index Settings</title>
    <link type="text/css" rel="stylesheet" href="../css/admin.css"/>
    <style type="text/css">

        .infoBox {
            padding: 8px;
            margin: 10px;
            border: 1px dotted #000000;
            background-color: #EEEEEE;
        }

        .monospace {
            font-family: 'Courier New';
        }

        .messages {
            overflow: auto;
            height: 600px;
            width: 100%;
            margin-top: 10px;
        }

        .logentry {
            margin-left: 10px;
        }

        .level-info {
            color: #808080;
        }

        .level-error {
            color: #FF0000;
        }

        .level-warning {
            color: #808000;
        }

        .stacktrace {
            font-size: 10pt;
            border-left: medium solid #808080;
            margin-left: 20px;
            color: #808080;
        }

        .traceelem {
            margin-left: 4px;
        }
    </style>

</head>
<body>

<h1>Admin / <a href="${baseUrl}/adminpage?page=1050&op=browse">Content handler</a> / Index Settings</h1>

<div class="infoBox">
    <h2>Index settings</h2>
    <ul>
    [#list indexSettings?keys?sort as key]
        <li><span style="color: #000080">${key}</span> <b>=</b>
            <span style="color: #008000">${indexSettings[key]}</span></li>
    [/#list]
    </ul>
</div>

<div class="infoBox">
    <form action="indexSettings" method="post">
        <table>
            <tr><td>Setting:</td><td><input type="text" name="updateSetting"></td></tr>
            <tr><td>Value: </td><td><input type="text" name="value"></td></tr>
        </table>
        <input type="submit" value="Submit">
    </form>


</div>

</body>
</html>
