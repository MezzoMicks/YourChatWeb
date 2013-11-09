
	function chatResize() {
		var $chat = $('#chat');
		var $talk = $('#chat-input');
		var chatHeight = $('body').height() - $('.title-area').outerHeight();
		$chat.css('height', chatHeight);
		$('#sidebar').css('height', chatHeight);
		$('#chat-screen').css('height', chatHeight - $talk.outerHeight());
	}

	// Invitation-Dialog /a
	function createKey() {
		var selectedVal = "";
		var selected = $("#inviteForm input[type='radio']:checked");
		if (selected.length > 0) {
			selectedVal = selected.val();
		}
		var key = invite(selectedVal);
		if (key != null && key != 'null') {
			var resultDiv = $('#inviteForm div');
			resultDiv.css('visibility', 'visible');
			$('#keyResult').html(key);
		}
	}
	// Invitation-Dialog /e
	
	var window_focus = true;
	var lastUpdate = 0;
	var oldTimeout = -1;

    function showBackground() {
        console.log("showBackground");
        var $chatScreen = $('#chat');
        $chatScreen.css('background-color', '#' + $chatScreen.data('background'));
        var bgImage = null;
            console.log("before selected");
        if ($('#showBackground').is(':checked')) {
            console.log("selected");
            bgImage = $chatScreen.data('background-image');
        }
        if (bgImage != null) {
            $chatScreen.css('background-image', 'url(' + bgImage + ')');
        } else {
            $chatScreen.css('background-image', 'none');
        }
    }

	function chatListen() {
		listen(listenID, function (data) {
			if (!window_focus && data.length > 0 && data.indexOf('<!--') == -1) {
				$("#favicon").remove();
				$('head').append('<link id="favicon" rel="icon" href="favicon_talky.gif" type="image/x-icon">');
			}
			$('#chat-screen').append(data);
			oldTimeout = window.setTimeout(function() { chatListen(); }, 500);
			$('#signal').attr("src", "img/ampel.gif");
			lastUpdate = new Date().getTime();
		});
		if (typeof scrolling == 'undefined' || scrolling) { 
			$('#chat-screen').scrollTop(5000000); 
		}
	}
	
	

	function restartListener() {
		var now = new Date().getTime();
		if (now - lastUpdate > 10000) {
			alert('restarting');
			window.clearTimeout(oldTimeout);
			chatListen();
		}
	}
	
	function isArray(what) {
	    return Object.prototype.toString.call(what) === '[object Array]';
	}
	
	
	function tooltipify($what) {
		console.log($what);
		var pinky = $what.data('pinky');
		console.log(pinky);
		if (pinky != null) {
			$what.attr('title', "$img:" + pinky + "," + $what.text());
			$what.attr('data-tooltip', '');
		}
//		console.log('calling it');
//		$(document).foundation('tooltips');
	}
	
	function appendUser(userList, user, withAka) {
		var $userLi = $('<li>');
		var $userContainer;
		// if the user is a 'normal' user
		if (user.guest == false) {
			// add a hyperlink
			var $userA = $('<a class="userLink" target="_blank">');
			$userA.attr('href', 'profile.jsp?user=' + user.username);
			$userLi.append($userA);
			// if the user has an avatar, show a tooltip for it
			if (user.avatar != null && user.avatar != 'null') {
				$userA.attr('data-tooltip', '<img src="data/db.image.pinky.' + user.avatar + '" style="width:64px;height:64px"/>');
			}
			$userContainer = $userA;
		} else {
		    $userContainer = $userLi;
		}
		var $userI = $('<i>');
		$userContainer.append($userI);
		// colorize the icons background in the users color
		$userI.css('color', '#' + user.color);
		if (user.away == "true") {;
			$userI.addClass('icon-remove-sign');
		} else {
			$userI.addClass('icon-user');
		}
		// append their name
		$userContainer.append('&nbsp;' + user.username);
		if (withAka && user.alias != null && user.alias != 'null') {
			$userContainer.append('&nbsp;(' + user.alias + ')');
		}
		userList.append($userLi);
	}
	
	function appendMedia(mediaList, media) {
		var mediaLi = $('<li>');
		var mediaA = $('<a>');
		mediaA.attr('target', '_blank');
		mediaA.attr('href', media.link);
		mediaA.addClass("mediaLink");
		if (media.preview != null && media.preview != 'null') {
			mediaA.attr('data-preview', media.preview);
		}
		if (media.pinky != null && media.pinky != 'null') {
			mediaA.data('pinky', media.pinky);
			tooltipify(mediaA);
		}
		var mediaI = $('<i>');
		mediaA.append(mediaI);
		if (media.type == "IMAGE") {
			mediaI.addClass('icon-picture');
		} else if (media.type == "VIDEO") {
			mediaI.addClass('icon-film');
		} else if (media.type == "WEBSITE") {
			mediaI.addClass('icon-globe');
		} else if (media.type == "PROTOCOL") {
			mediaI.addClass('icon-list-alt');
		}
		mediaA.append('&nbsp;' + media.name);
		if (media.type != "PROTOCOL" && media.user != null && media.user != "null") {
			mediaLi.append('<br/><i class="icon-user"></i>&nbsp' + media.user);
		}
		mediaLi.append(mediaA);
		mediaList.append(mediaLi);
	}
	
	function refresh() {
		console.log("refresh called!");
		doRefresh(function(data) {
			console.log(data);
			console.log(data.users);
			// ### Refresh UserList ###
			// get the selector for the userlist and clear it
			var userList = $('#roomMateList');
			userList.empty();
			// iterate over returned users
			if (data.users != null) {
				if (isArray(data.users)) {
					$.each(data.users, function(i, user) {
						appendUser(userList, user, true);
					});	
				} else {
					appendUser(userList, data.users, true);
				}
			}
			var otherList = $('#neighbourList');
			otherList.empty();
			// iterate over returned users
			if (data.others != null) {
				if (isArray(data.others)) {
					$.each(data.others, function(i, user) {
						appendUser(otherList, user, false);
					});
				} else {
					appendUser(otherList, data.others, false);
				}
			}
			// ### Refresh MediaList ###
			// selector for medialist and clear it
			var mediaList = $('#mediaList');
			mediaList.empty();
			if (data.medias != null) {
				if (isArray(data.medias)) {
					$.each(data.medias, function(i, media) {
						appendMedia(mediaList, media);
					});
				} else {
					appendMedia(mediaList, data.medias);
				}
			}
			// ### Refresh RoomList ###
			var roomList = $('#roomSelect');
			roomList.empty();
			// initial dummy option
			roomList.append("<option selected>switch rooms</option>");
			$.each(data.rooms, function(i, r) {
				var roomOption = $('<option>');
				roomList.append(roomOption);
				roomOption.attr('data-room', r.name);
				roomOption.append(r.name);
				// usercount in brackets!
				roomOption.append('&nbsp;(' + r.users + ')');
			});
			$('#channelName').html(data.room);
			var $chatScreen = $('#chat');
			$chatScreen.data('background-color', data.background);
			if (data.backgroundimage) {
			    $chatScreen.data('background-image', data.backgroundimage);
			} else {
			    $chatScreen.data('background-image');
			}
			showBackground();
		});
	}
	
	function upload() {
		$("#talkFile").trigger("click");
	}

	$().ready(function() {
		$(document).foundation();
		
		$('#chat-screen').mouseover(function(event){
		});
		
		$("#talkForm").ajaxForm({
		    success:function() {
                var $inputField = $("#talkText");
                $inputField.focus();
		    },
		    resetForm: true ,
		    uploadProgress: function(e, pos, total, percent) {
                console.log(percent);
		    }
		   });
		$(window).focus(function() {
			$("#favicon").remove();
			$('head').append('<link id="favicon" rel="icon" href="favicon.ico" type="image/x-icon">');
			window_focus = true;
		});
		$(window).blur(function() {
			window_focus = false;
		});

        $('#showBackground').on('change', showBackground);
		chatListen();
		refresh();
		$('#signalLink').click(restartListener);
	});
	$(window).resize(chatResize);