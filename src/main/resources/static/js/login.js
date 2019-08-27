function submitForm(){
	$.ajax({
		url: '/autoupload',
		method: 'post',
		data: {
			username: $('#usernameId').val(),
			password: $('#passwordId').val(),
		}
	}).done(function(req){
		if (req !== null && req !== 'undefined' && req.status !== 'error') {
			window.location.href = '/autoupload'
		} else {
			$('#errorMsgId').html(req.msg);
		}
	})
}
function clearForm(){
	$('#ff').form('clear');
}

$(document).keyup(function(event){
	  if(event.keyCode ==13){
	    $("#loginBtn").trigger("click");
	  }
});