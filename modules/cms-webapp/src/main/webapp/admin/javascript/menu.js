
var useCookies = true;
var useCookieExpireDate = false;


function findTagInTopChildren(node, tagName) {
    while (node && node.tagName != tagName)
        node = node.children[0];
    return node;
}

// toggle shortcuts - search for all
function toggleShortcut( currentMenuItem, display, isLast ) {
    // get menuItem of shortcut IMG
    while ( currentMenuItem && currentMenuItem.className != 'menuItem' ) {
        currentMenuItem = currentMenuItem.parentNode;
    }

    var subMenuLink = findTagInTopChildren( currentMenuItem, 'A' );

    if ( !subMenuLink ) { // must not have subMenu
        // toggle only upper level ( must have TR parent with id="menutop" )
        var menuTop = currentMenuItem;
        while ( menuTop && menuTop.tagName != 'TR' ) {
            menuTop = menuTop.parentNode;
        }

        if ( menuTop.id == 'id-menutop' ) {
            currentMenuItem.style.display = display;

            // fix up the node lines
            var previousMenuItem = currentMenuItem.previousSibling;
            if ( previousMenuItem && previousMenuItem.tagName != 'TABLE' ) {
                previousMenuItem = previousMenuItem.previousSibling;
            }

            if ( previousMenuItem && previousMenuItem.tagName == 'TABLE' ) {
                var verticalLine = previousMenuItem.children[0].children[1];
                if ( verticalLine ) {
                    verticalLine = verticalLine.children[0];
                    verticalLine.attributes.background.value = display == '' ? 'javascript/images/I.png' : '';
                }

                var sign = '';
                var previousNode = findTagInTopChildren( previousMenuItem, 'IMG' );
                if ( /plus|minus/.test( previousNode.src ) ) {
                    sign = /plus/.test( previousNode.src ) ? 'plus' : 'minus';
                }
                var icon = display == '' || !isLast && (display == 'none') ? 'T' : 'L';
                previousNode.src = 'javascript/images/' + icon + sign + '.png';
            }

            return isLast;
        }
    }

    return false;
}

function searchShortcuts( element, display, isLast ) {
    var length = element.children.length - 1;

    for ( var i = length; i >= 0; i-- ) {
        var child = element.children[i];

        // find IMG with shortcut icon
        if ( child.tagName == 'IMG' && /icon_menuitem_/.test( child.src ) ) {
            var isShortcutToHide = /_shortcut_lock.gif|_shortcut.gif/.test( child.src );
            isLast = isShortcutToHide ? toggleShortcut( child, display, isLast ) : false;
        } else {
            isLast = searchShortcuts( child, display, isLast );
        }
    }
    return isLast;
}

function toggleShortcuts( toggle ) {
    var show = WebFXTabPane.getCookie( 'shortcuts' ) != 'false';
    show = toggle ? !show : show;
    WebFXTabPane.setCookie( 'shortcuts', show, 30 );

    var button = document.getElementById( 'shortcut-image' );

    button.style.filter = show ? '' : 'alpha(opacity=30)';
    button.style.opacity = show ? '' : '.3';

    var menu = document.getElementById( 'id-menus' );
    searchShortcuts( menu, show ? '' : 'none', true );
}

function openTree() {
    toggleShortcuts( false );

    for( var key in branchOpen ) {

        if ( branchOpen[key] ){
            openBranch(key);
        }
    }
}

function closeTree() {
    document.cookie = cookiename + "= ";
    window.location = window.location;
}

function isBranchClosed(key) {
    return document.getElementById('id'+key) && document.getElementById('id'+key).style.display == 'none';
}

function decodeUtf8( str )
{
    var s;
    try
    {
        s = decodeURIComponent( escape( str ) );
    }
    catch(e) { /**/ }

    return s;
}

function openBranch(key) {

    var _key;
    if ( document.all )
        _key = decodeUtf8(key);
    else
        _key = key;

    if ( document.getElementById('id' + _key) && document.getElementById('img' + _key) ) {

        if (isBranchClosed(_key)) {
            document.getElementById('id'+_key).style.display = '';

            if ( document.getElementById('img'+_key).src.search(/Tplus\.png/) != -1 ){
                document.getElementById('img'+_key).src = 'javascript/images/Tminus.png';
            }
            else{
                document.getElementById('img'+_key).src = 'javascript/images/Lminus.png';
            }

            // TODO: Is this IF test needed? There is no key that begins with -site
            if (_key.search(/\-site/) == -1) {
                branchOpen[_key] = true;
            }
        }
        else {
            document.getElementById('id'+_key).style.display = 'none';

            if ( document.getElementById('img'+_key).src.search(/Tminus\.png/) != -1 ){
                document.getElementById('img'+_key).src = 'javascript/images/Tplus.png';
            }
            else{
                document.getElementById('img'+_key).src = 'javascript/images/Lplus.png';
            }

            // TODO: Is this IF test needed? There is no key that begins with -site
            if (_key.search(/\-site/) == -1) {
                branchOpen[_key] = false;
            }
        }

        if(useCookies) {
            setCookie();
        }
    }

}

function changeSite( domainkey)
{
    document.location = "adminpage?page=5&redirect=adminpage%3Fpage%3D2%26op%3Dbrowse%26loadsitepage%3Dtrue%26selecteddomainkey%3D"+domainkey;
}

function loadUnit(unitKey, domainKey) {

    var key = "-unit" + unitKey;
    branchOpen[key] = true;
    if (useCookies) {
        setCookie();
    }

    document.splash['redirect'].value = "adminpage?page=2&op=browse&selecteddomainkey="+ domainKey +"&selectedunitkey="+ unitKey;
    document.splash.submit();
}

function loadTopCategory(topCategoryKey) {
	var url = document.splash['redirect'].value;
    url = setParameter(url, "topcategorykey", topCategoryKey);
    document.splash['redirect'].value = url;
    document.splash.submit();
}


function loadMenu(menuKey, domainKey) {

    var key = "-rootmenu" + menuKey;
    branchOpen[key] = true;
    if (useCookies) {
        setCookie();
    }

    document.splash['redirect'].value = "adminpage?page=2&op=browse&selecteddomainkey="+ domainKey +"selectedmenukey="+ menuKey;
    document.splash.submit();
}

function _removeCurrentMenuElementFromBranchOpen()
{
    for ( var key in branchOpen )
    {
        if ( /-menu\d/.test(key) )
        {
            delete branchOpen[key];
        }
    }
}

function loadBranch(type, key) {
    var url;
    if (type == 'category') {
    	url = document.splash['redirect'].value;
    	url = setParameter(url, "selectedunitkey", key);
    	document.splash['redirect'].value = url;
    }
    else if (type == 'menu') {
    	url = document.splash['redirect'].value;
    	url = setParameter(url, "selectedmenukey", key);
        if (useCookies) {
            _removeCurrentMenuElementFromBranchOpen();
            branchOpen['-menu' + key] = true;
            setCookie();
        }

    	document.splash['redirect'].value = url;
    }
    document.splash.submit();
}

function setCookie()
{
    var cookieValue = "";

    var i = 0;

    for ( key in branchOpen ){
        if ( branchOpen[key] ){
            if ( i > 0 )
                cookieValue += ",";
            i++;
            cookieValue += key;
        }
    }

    if ( useCookieExpireDate )
    {
        var date = new Date();
        var daysToExpire = 18250;
                                        
        date.setTime(date.getTime() + (daysToExpire * 24 * 60 * 60 * 1000));

        cookieValue += '; expires=' + date.toGMTString();
    }

    try
    {
        document.cookie = cookiename + "=" + cookieValue;
    }
    catch (err)
    {
        /**/
    }
}