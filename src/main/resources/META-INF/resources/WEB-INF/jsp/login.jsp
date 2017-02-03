<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="content-type" content="text/html; charset=utf-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=0"/>
    <title>Login</title>
    <link rel="stylesheet" href="http://www.w3schools.com/lib/w3.css" />
    <link rel="stylesheet" href="http://www.w3schools.com/lib/w3-theme-blue.css" />
    <link rel="stylesheet" href="http://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css" />
    <link rel="stylesheet" type="text/css" href="https://cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/3.3.7/css/bootstrap.min.css"/>
    <link rel="stylesheet" type="text/css" href="/css/jquery.growl.css"/>
    <script src="https://code.jquery.com/jquery-3.1.1.min.js"></script>
    <script src="https://cdn.auth0.com/js/lock/10.9.1/lock.min.js"></script>
    <script src="/js/jquery.growl.js" type="text/javascript"></script>
</head>
<body style="background-image: url(/images/main_bg.jpg);
        background-position: center top;
        background-color: #46c6fc !important;
        background-repeat: no-repeat;
        margin-left: 0px;
        margin-top: 0px;
        margin-right: 0px;
        margin-bottom: 0px;">

<nav class="w3-theme">
    <a href="/index.jsf" class="w3-padding"><img src="/images/graphic/logo.png" style="max-width: 141px; width:100%" alt="logo"/></a>
    <span class="w3-right w3-padding w3-xlarge"><a href="javascript:history.back()"><i class="fa fa-chevron-left w3-text-white" aria-hidden="true"></i></a></span>
</nav>

<div class="container">
    <div id="root" style="margin-top: 40px; margin-bottom: 40px; box-sizing: border-box;">
    </div>
    <script type="text/javascript">
        var options = {
            language: '${sessionScope.userSession.auth0Locale}',
            container: 'root',
            allowLogin: ${allowLogin},
            closable: false,
            auth: {
                redirectUrl: '${fn:replace(pageContext.request.requestURL, pageContext.request.requestURI, '')}/callback',
                responseType: 'code',
                params: {
                    state: '${state}',
                    scope: 'openid user_id name nickname email picture'
                }
            },
            theme: {
                logo: 'http://www.funfunspell.com/images/graphic/logo.png',
                primaryColor: 'green'
            }
        };

        $(function () {
            var error = ${error};
            if (error) {
                $.growl.error({message: "Please log in"});
            } else {
                $.growl({title: "Loading...", message: ""});
            }
        });
        $(function () {
            var lock = new Auth0Lock('W8nTboR1CzD4nyytnxnnIYn2JhiVU1PL', 'thcathy.auth0.com', options);
            lock.show();
        });
    </script>
</div>
</body>
</html>