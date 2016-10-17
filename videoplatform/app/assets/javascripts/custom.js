$(document).ready(function () {
	//Only numeric values in login_code field
	$('#user_login_code').bind('keyup', function(e) {
	  this.value = this.value.replace(/[^0-9]/g,'');
	});
	$('#session_login_code').bind('keyup', function(e) {
	  this.value = this.value.replace(/[^0-9]/g,'');
	});
});
