[#ftl]
<!doctype html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <title>Enonic CMS - Authentication</title>
    <link rel="stylesheet" href="_itrace/resources/authentication-page.css" type="text/css"/>
    <link rel="stylesheet" href="_itrace/resources/authentication-page-ie.css" type="text/css"/>
</head>

<body>

<table cellpadding="0" cellspacing="0" border="0" id="wrapper">
    <tr>
        <td>
        [#if authenticationFailed = true]
            <p class="cms-error">The username or password you entered is not valid or you are not a member of the Developer group.</p>
        [/#if]

            <div id="inner">
                <h1>Authenticate</h1>

                <div id="form-container">
                    <form action="_itrace/authenticate" method="post">
                        <input type="hidden" name="_itrace_authentication" value="true"/>
                        <input type="hidden" name="_itrace_original_url" value="${originalURL!}"/>
                        <table cellspacing="0" cellpadding="0" border="0">
                            <tr>
                                <td class="label-container">
                                    <label for="userstore">Userstore</label>
                                </td>
                                <td class="input-container">
                                    <select name="_itrace_userstore" id="userstore">
                                    [#list userStores?keys?sort as key]
                                        <option value="${key}">${userStores[key]}</option>
                                    [/#list]
                                    </select>
                                </td>
                            </tr>
                            <tr>
                                <td class="label-container">
                                    <label for="username">Username</label>
                                </td>
                                <td class="input-container">
                                    <input type="text" id="username" name="_itrace_username" value=""/>
                                </td>
                            </tr>
                            <tr>
                                <td class="label-container">
                                    <label for="password">Password</label>
                                </td>
                                <td class="input-container">
                                    <input type="password" id="password" name="_itrace_password" value=""/>
                                </td>
                            </tr>
                            <tr>
                                <td class="label-container">
                                    <br/>
                                </td>
                                <td class="input-container">
                                    <input type="submit" style="cursor: pointer;" class="button_text" name="login" value="Authenticate"
                                           id="login"/>
                                </td>
                            </tr>
                        </table>
                    </form>
                </div>
            </div>
        </td>
    </tr>
</table>

<script>
    document.getElementById( 'username' ).focus();
</script>

</body>
</html>
<html>
</html>