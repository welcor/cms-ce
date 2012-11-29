[#ftl]
<!doctype html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <title>Enonic CMS - Authentication</title>
    <style type="text/css">
        div.row {
            overflow: auto;
            margin-bottom: 5px;
        }

        label {
            display: block;
            float: left;
            width: 100px;
        }
    </style>
</head>

<body>

[#if authenticationFailed = true]
    <p class="cms-error">The username or password you entered is not valid or you are not a member of the Developer group.</p>
[/#if]

<form action="_itrace/authenticate" method="post">
    <input type="hidden" name="_itrace_authentication" value="true"/>
    <input type="hidden" name="_itrace_original_url" value="${originalURL!}"/>
    <label for="userstore">Userstore:</label>

    <div class="row">
        <select name="_itrace_userstore" id="userstore">
        [#list userStores?keys?sort as key]
            <option value="${userStores[key].key}" [#if userStores[key].defaultStore = 1] selected="selected" [/#if]>
            ${userStores[key].name}
            </option>
        [/#list]
        </select>
    </div>
    <div class="row">
        <label for="username">Username:</label>
        <input type="text" id="username" name="_itrace_username" value=""/>
    </div>
    <div class="row">
        <label for="password">Password:</label>
        <input type="password" id="password" name="_itrace_password" value=""/>
    </div>
    <div class="row">
        <input type="submit" style="cursor: pointer;" class="button_text" name="login" value="Authenticate"
               id="login"/>
    </div>
</form>

<script>
    document.getElementById('username').focus();
</script>

</body>
</html>
<html>
</html>