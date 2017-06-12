tool_obj =
{
	testMode: false,

	title: "CHANGE PASSWORD",

	passwordsValid: 0,

	start: function(obj, data)
	{
		setTitle(obj.title);

		setupAlert("changepw_alertNoSave_1");
		setupAlert("changepw_alertNoSave_2");
		setupAlert("changepw_alertNoSave_3");
		setupAlert("changepw_alertSuccess", function(){parent.location.replace("/portal");});
		
		$("#changepw_password_alert").addClass("e3_offstage");
		$("#changepw_password_alert2").addClass("e3_offstage");
		$("#changepw_password_alert3").addClass("e3_offstage");
		$('#changepw_pw_1').val(null);
		$('#changepw_pw_2').val(null);
		$('#changepw_pw_1').unbind('change').change(function(){obj.validatePasswords(obj);return true;});
		$('#changepw_pw_2').unbind('change').change(function(){obj.validatePasswords(obj);return true;});
		$("#changepw_go").on('click', function(event){obj.savePassword(obj);});
		
		obj.loadAccount(obj);
	},

	stop: function(obj, save)
	{
	},

	loadAccount: function(obj)
	{
		requestCdp("account_account", null, function(data)
		{
			$("#changepw_email").val(data.account.email);
		});
	},

	validatePasswords: function(obj)
	{
		var p1 = $.trim($('#changepw_pw_1').val());
		var p2 = $.trim($('#changepw_pw_2').val());
		// TODO: strength
		$("#changepw_password_alert").addClass("e3_offstage");
		$("#changepw_password_alert2").addClass("e3_offstage");
		$("#changepw_password_alert3").addClass("e3_offstage");
		if (p1 != p2)
		{
			$("#changepw_password_alert").removeClass("e3_offstage");
			obj.passwordsValid = 2;
		}
		else if (p1.length == 0)
		{
			$("#changepw_password_alert2").removeClass("e3_offstage");
			obj.passwordsValid = 1;
		}
		else if (!obj.strongPassword(obj, p1))
		{
			$("#changepw_password_alert3").removeClass("e3_offstage");
			obj.passwordsValid = 3;
		}
		else
		{
			obj.passwordsValid = 0;		
		}
	},

	savePassword: function(obj)
	{
		// if not valid...
		obj.validatePasswords(obj);
		if (obj.passwordsValid != 0)
		{
			$("#changepw_alertNoSave_" + obj.passwordsValid).dialog("open");
			return false;
		}

		var data = new Object();
		data.pw = $.trim($('#changepw_pw_1').val());
		data.email = $.trim($("#changepw_email").val());
		requestCdp("changepw_setPassword", data, function(data)
		{
			$("#changepw_alertSuccess").dialog("open");
		});

		return true;
	},
	
	strongPassword: function(obj, pw)
	{
		if (pw.length < 8) return false;
		if (-1 == pw.search("[A-Z]")) return false;
		if (-1 == pw.search("[a-z]")) return false;
		if (-1 == pw.search("[0-9]")) return false;

		return true;
	}
};

completeToolLoad();
