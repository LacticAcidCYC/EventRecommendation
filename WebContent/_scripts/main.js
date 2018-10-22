'use strict';

$(function () {

    /**
     * Variables
     */
    // default value
    var user_id = '1111';
    var user_fullname = 'Eric Chen';
    var lng = -122.08;
    var lat = 37.38;

    /**
     * Initialize
     */
    function init() {
        // Register event listeners
        $('#login-btn').click(login);
        $('#logout-link').click(logout);
        $('#nearby-btn').click(loadNearbyItems);
        $('#fav-btn').click(loadFavoriteItems);
        $('#recommend-btn').click(loadRecommendedItems);
        //initGeoLocation();
        onSessionInvalid();
        validateSession();
    }

    /**
     * Session
     */
    function validateSession() {
        // The request parameters
        //var url = 'http://34.211.21.63/EventAuth/login';
    	var url = './login';
        var req = JSON.stringify({});

        //onSessionInvalid();
        // display loading message
        showLoadingMessage('Validating session...');

        // make AJAX call
        ajax('GET', url, req,
            // session is still valid
            function(res) {
                var result = JSON.parse(res);
                //console.log(`name: ${result.name}`);

                if (result.status === 'OK') {
                    onSessionValid(result);
                }
            }, onSessionInvalid, true);
    }

    function onSessionValid(result) {
        user_id = result.user_id;
        user_fullname = result.name;
        //console.log(`login_name: ${user_fullname}`);

        var loginForm = $('#login-form');
        var itemNav = $('#item-nav');
        var itemList = $('#item-list');
        var avatar = $('#avatar');
        var welcomeMsg = $('#welcome-msg');
        var logoutBtn = $('#logout-link');

        welcomeMsg.html('Welcome, ' + user_fullname);

        showElement(itemNav);
        showElement(itemList);
        showElement(avatar);
        showElement(welcomeMsg);
        showElement(logoutBtn, 'inline-block');
        hideElement(loginForm);

        fixFooterPosition();

        initGeoLocation();
    }

    function onSessionInvalid() {
        var loginForm = $('#login-form');
        var itemNav = $('#item-nav');
        var itemList = $('#item-list');
        var avatar = $('#avatar');
        var welcomeMsg = $('#welcome-msg');
        var logoutBtn = $('#logout-link');

        hideElement(itemNav);
        hideElement(itemList);
        hideElement(avatar);
        hideElement(welcomeMsg);
        hideElement(logoutBtn);

        $('#username').val('');
        $('#password').val('');
        showElement(loginForm);

        fixFooterPosition();
    }

    function initGeoLocation() {
        if (navigator.geolocation) {
            console.log('get location.');
            navigator.geolocation.getCurrentPosition(onPositionUpdated, onLoadPositionFailed, {
                    maximumAge : 60000
                });
            showLoadingMessage('Retrieving your location...');
        } else {
            onLoadPositionFailed();
        }
    }

    function onPositionUpdated(position) {
        lat = position.coords.latitude;
        lng = position.coords.longitude;
        console.log(`latitude: ${lat}`);
        console.log(`longitude: ${lng}`);
        loadNearbyItems();
    }

    function onLoadPositionFailed() {
        //console.log('location error');
        console.warn('navigator.geolocation is not available');
        getLocationFromIP();
    }

    // -----------------------------------
    // Login
    // -----------------------------------
    function login() {
        var username = $('#username').val();
        var password = $('#password').val();
        password = md5(username + md5(password));
        //console.log(username + " " + password);

        // The request parameters
        //var url = 'http://34.211.21.63/EventAuth/login';
        var url = './login';
        var req = JSON.stringify({
            user_id : username,
            password : password,
        });

        ajax('POST', url, req,
            // successful callback
            function(res) {
                var result = JSON.parse(res);
                console.log(`login_name:${result.name}`);

                // successfully logged in
                if (result.status === 'OK') {
                    onSessionValid(result);
                }
            },

            // error
            function() {
                showLoginError();
            },
            true);
    }

    function showLoginError() {
        $('#login-error').html('Invalid username or password');
    }

    function clearLoginError() {
        $('#login-error').html('');
    }

    function logout() {
        var url = './logout';
        var req = JSON.stringify({});
        ajax('POST', url, req,
            function () {
                onSessionInvalid();
            },

            // error
            function () {

            },
            true);

        //onSessionInvalid();
    }

    // -----------------------------------
    // Helper Functions
    // -----------------------------------

    /**
     * A helper function that makes a navigation button active
     *
     * @param btnId -
     *            The id of the navigation button
     */
    function activeBtn(btnId) {
        var btns = $('.main-nav-btn');

        // deactivate all navigation buttons
        for (var i = 0; i < btns.length; i++) {
            $(btns[i]).removeClass('active');
        }

        // active the one that has id = btnId
        var btn = $(`#${btnId}`);
        btn.addClass('active');
    }

    /**
     * A helper function that creates a DOM element <tag options...>
     *
     * @param tag
     * @param options
     * @returns
     */
    function createDOMElement(tag, options) {
        var element = document.createElement(tag);
        for (var option in options) {
            if (options.hasOwnProperty(option)) {
                element[option] = options[option];
            }
        }

        return element;
    }

    function hideElement(element) {
        element.css('display', 'none');
    }

    function showElement(element, style) {
        element.css('display', style ? style : 'block');
    }

    function showLoadingMessage(msg) {
        var itemList = $('#item-list');
        itemList.html('<p class="notice"><i class="fas fa-spinner fa-spin"></i> ' +
            msg + '</p>');
    }

    function showWarningMessage(msg) {
        var itemList = $('#item-list');
        itemList.html('<p class="notice"><i class="fas fa-exclamation-triangle"></i> ' +
            msg + '</p>');
    }

    function showErrorMessage(msg) {
        var itemList = $('#item-list');
        itemList.html('<p class="notice"><i class="fas fa-exclamation-circle"></i> ' +
            msg + '</p>');
    }

    /**
     * AJAX helper
     *
     * @param method -
     *            GET|POST|PUT|DELETE
     * @param url -
     *            API end point
     * @param callback -
     *            This the successful callback
     * @param errorHandler -
     *            This is the failed callback
     */
    function ajax(method, url, data, callback, errorHandler, credentials) {
        var xhr = new XMLHttpRequest();

        xhr.open(method, url, true);

        // must set before sending data!!
        xhr.withCredentials = credentials;

        if (data === null) {
            xhr.send();
        } else {
            xhr.setRequestHeader("Content-Type", "application/json;charset=utf-8");
            xhr.send(data);
        }
        
        xhr.onload = function () {
            if (xhr.status === 200) {
                callback(xhr.responseText);
            } else if (xhr.status == 403) {
            	onSessionInvalid();
            } else {
                errorHandler();
            }
        };

        xhr.onerror = function () {
            console.error("The request couldn't be completed.");
            errorHandler();
        };
    }

    // -------------------------------------
    // AJAX call server-side APIs
    // -------------------------------------

    /**
     * API #1 Load the nearby items API end point: [GET]
     * /Dashi/search?user_id=1111&lat=37.38&lon=-122.08
     */
    function loadNearbyItems() {

        // active button
        activeBtn('nearby-btn');

        // The request parameters
        var url = './search';
        //var url = 'http://34.211.21.63/EventAuth/search';
        var params = 'user_id=' + user_id + '&lat=' + lat + '&lon=' + lng;
        var req = JSON.stringify({});

        //console.log(req);

        // display loading message
        console.log('Loading nearby items...');
        ajax('GET', url + '?' + params, req,
            // successful callback
            function (res) {
                var items = JSON.parse(res);
                if (!items || items.length === 0) {
                    showWarningMessage('No nearby item.');
                } else {
                    listItems(items);
                    var f = $('footer');
            	    f.css({position:'static'});
                }
            },
            // failed callback
            function () {
                showErrorMessage('Cannot load nearby items.');
            }, true);

        // var xhr = new XMLHttpRequest();
        // xhr.open('GET', url + '?' + params, true);
        // xhr.setRequestHeader("Content-Type", "application/json;charset=utf-8");
        // xhr.send(req);
        //
        // xhr.onload = function() {
        //     console.log('load success');
        //     if(xhr.status === 200) {
        //         var items = JSON.parse(xhr.responseText);
        //         if (!items || items.length === 0) {
        //             //showWarningMessage('No nearby item.');
        //             console.log('No nearby item.');
        //         } else {
        //             listItems(items);
		// 			//console.log(items);
        //         }
        //     } else if(xhr.status === 403) {
        //         console.log('invalid session');
        //     } else {
        //         console.log('error');
        //     }
        // }
        //
        // xhr.onerror = function() {
        //     console.error("The request couldn't be completed.");
        //     //showErrorMessage("The request couldn't be completed.");
        // };

    }

    /**
     * API #2 Load favorite (or visited) items API end point: [GET]
     * /Dashi/history?user_id=1111
     */
    function loadFavoriteItems() {
        activeBtn('fav-btn');

        // The request parameters
        //var url = 'http://34.211.21.63/EventAuth/history';
        var url = './history';
        var params = 'user_id=' + user_id;
        var req = JSON.stringify({});

        // display loading message
        console.log(url);
        showLoadingMessage('Loading favorite items...');
        console.log('Loading favorite items...');

        // make AJAX call
        ajax('GET', url + '?' + params, req, function(res) {
            var items = JSON.parse(res);
            if (!items || items.length === 0) {
                showWarningMessage('No favorite item.');
            } else {
                listItems(items);
                var f = $('footer');
        	    f.css({position:'static'});
            }
        }, function() {
            showErrorMessage('Cannot load favorite items.');
        }, true);
    }

    /**
     * API #3 Load recommended items API end point: [GET]
     * /Dashi/recommendation?user_id=1111
     */
    function loadRecommendedItems() {
        activeBtn('recommend-btn');

        // The request parameters
        //var url = 'http://34.211.21.63/EventAuth/recommendation';
        var url = './recommendation';
        var params = 'user_id=' + user_id + '&lat=' + lat + '&lon=' + lng;

        var req = JSON.stringify({});

        // display loading message
        showLoadingMessage('Loading recommended items...');
        console.log('Loading recommended items...');

        // make AJAX call
        ajax(
            'GET',
            url + '?' + params,
            req,
            // successful callback
            function(res) {
                var items = JSON.parse(res);
                if (!items || items.length === 0) {
                    showWarningMessage('No recommended item. Make sure you have favorites.');
                } else {
                    listItems(items);
                    var f = $('footer');
            	    f.css({position:'static'});
                }
            },
            // failed callback
            function() {
                showErrorMessage('Cannot load recommended items.');
            },
            true);
    }

    /**
     * API #4 Toggle favorite (or visited) items
     *
     * @param item_id -
     *            The item business id
     *
     * API end point: [POST]/[DELETE] /Dashi/history request json data: {
     * user_id: 1111, visited: [a_list_of_business_ids] }
     */
    function changeFavoriteItem(item_id) {
        // Check whether this item has been visited or not
        var li = $(`#item-${item_id}`);
        var favIcon = $(`#fav-icon-${item_id}`);
        //console.log(li.data('favorite'));
        //console.log(typeof li.data('favorite'));
        // this 'favorite' is a boolean
        var favorite = li.data('favorite') !== true;

        // this 'favorite' is a string ......
        // var li2 = document.getElementById(`item-${item_id}`);
        // console.log(li2.dataset.favorite);
        // console.log(typeof li2.dataset.favorite);

        // The request parameters
        //var url = 'http://34.211.21.63/EventAuth/history';
        var url = './history';
        var req = JSON.stringify({
            user_id : user_id,
            favorite : [ item_id ]
        });
        var method = favorite ? 'POST' : 'DELETE';

        ajax(method, url, req,
            // successful callback
            function(res) {
                var result = JSON.parse(res);
                if (result.status === 'OK') {
                    li.data('favorite', favorite);
                    //console.log(favorite);
                    favIcon.removeClass('fas fa-heart far');
                    favIcon.addClass(favorite ? 'fas fa-heart' : 'far fa-heart');
                }
            },
            function() {
                showErrorMessage('Cannot update favorite items.');
            },
            true);
    }

    // -------------------------------------
    // Create item list
    // -------------------------------------

    /**
     * List items
     *
     * @param items -
     *            An array of item JSON objects
     */
    function listItems (items){
        var itemList = $('#item-list');
        itemList.html('');

        for (var i = 0; i < items.length; i++) {
            addItem(itemList, items[i]);
        }
    }

    /**
     * Add item to the list
     *
     * @param itemList -
     *            The
     *            <ul id="item-list">
     *            tag
     * @param item -
     *            The item data (JSON object)
     */
    function addItem(itemList, item) {
        var item_id = item.item_id;

        // create the <li> tag and specify the id and class attributes
        var li = createDOMElement('li', {
            id: 'item-' + item_id,
            className: 'item'
        });

        // set the data attribute
        li.dataset.item_id = item_id;
        li.dataset.favorite = item.favorite;

        // item image
        if (item.image_url) {
            li.appendChild(createDOMElement('img', {
                src: item.image_url
            }));
        } else {
            li.appendChild(createDOMElement('img', {
                src: 'https://assets-cdn.github.com/images/modules/logos_page/GitHub-Mark.png'
            }));
        }

        // section
        var section = createDOMElement('div', {});

        // title
        var title = createDOMElement('a', {
            href: item.url,
            target: '_blank',
            className: 'item-name'
        });
        title.innerHTML = item.name;
        section.appendChild(title);

        // category
        var category = createDOMElement('p', {
            className: 'item-category'
        });
        category.innerHTML = 'Category: ' + item.categories.join(', ');
        section.appendChild(category);

        li.appendChild(section);

        // address
        var address = createDOMElement('p', {
            className: 'item-address'
        });
        address.innerHTML = item.address.replace(/,/g, '<br>').replace(/\"/g,
            '');
        li.appendChild(address);

        // favorite link
        var favLink = createDOMElement('p', {
            className: 'fav-link'
        });

        var extra = createDOMElement('i', {
            id: 'fav-icon-' + item_id,
            className: item.favorite ? 'fas fa-heart' : 'far fa-heart'
        });

        favLink.appendChild(extra);

        favLink.onclick = function() {
            changeFavoriteItem(item_id);
        };

        li.appendChild(favLink);

        itemList.append(li);
    }

    function getLocationFromIP() {
        // Get location from http://ipinfo.io/json
        var url = 'http://ipinfo.io/json'
        var req = null;

        ajax('GET', url, req, function(res) {
                var result = JSON.parse(res);
                if ('loc' in result) {
                    var loc = result.loc.split(',');
                    lat = loc[0];
                    lng = loc[1];
                } else {
                    console.warn('Getting location by IP failed.');
                }
                loadNearbyItems();
            },
            function() {
                console.warn('Getting location by IP failed.');
            },
            false);
    }

    // fix the footer at the bottom
    function fixFooterPosition() {
//        $('footer').removeClass('fixed-bottom');
//
//        var contentHeight = $(document.body).height(),
//            windowHeight = window.innerHeight;
//        console.log(`contentHeight: ${contentHeight}`);
//        console.log(`windowHeight: ${windowHeight}`);
//        if (contentHeight < windowHeight) {
//            $('footer').addClass('fixed-bottom');
//            console.log('111111');
//        }
    	$(window).on('load resize scroll', function() {
    	    var f = $('footer');
    	    f.css({position:'static'});
    	    //console.log(f.offset().top + f.height());
    	    if (f.offset().top + f.height() < $(window).height()) {
    	        f.css({position:'fixed', bottom:'0', width:'100%'});
    	        console.log('11111');
    	    }
    	});
    }

    init();

});