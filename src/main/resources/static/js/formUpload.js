$(function(){
	$('#submitMasterBtn').click( function() {
		var uploadFile = $('#masterFileValue').textbox('getValue');
		var validateResult = validateFile(uploadFile);
		if(!validateResult) {
			return;
		}
	     $("#uploadStatus").show();
	     $.ajax({
	         url: '/bigfile/autoUploadFile',
	         type: 'POST',
	         timeout : 600000,
	         cache: false,
	         data: new FormData($('#uploadBigFileForm')[0]),
	         processData: false,
	         contentType: false
	     }).done(function (res) {
	         if (res.status == "SUCCEED") {
	             $("#uploadStatus").hide();
	             showTable(0);
	         } else {
	             $("#uploadStatus").hide();
	             if(res.errorCode!=undefined&&"java.heap.space"==res.errorCode){
	            	 alertMsg("Server busy! Please try again later");
	             }else
	              if (res.errorMessage != null) {
	            	  alertMsg(res.errorMessage);
	             } else {
	            	 alertMsg("upload error!");
	             }
	         }

	     }).fail(function (res,status) {
	         $("#uploadStatus").hide();
	         if(status=='timeout'){//超时,status还有success,error等值的情况
	        	 alertMsg("connection overtime! Please try again later");
	             return;
	         }
	         if(res.responseJSON!=undefined&&res.responseJSON.errors!=undefined){
	             var errors = res.responseJSON.errors;
	             var num = errors.length;
	             for(var i=0;i<num;i++){
	                   if("validation.file.format.error"==errors[i].code){
	                	   alertMsg("file name format error");
	                   }else if("validation.file.empty"==errors[i].code){
	                	   alertMsg("upload file is empty")
	                   }
	             }
	         }else{
	        	 alertMsg("upload error!");
	         }

	     });
	     return false;
	 });
	$('#submitSiteBtn').click( function() {
		var uploadFile = $('#siteFileValue').textbox('getValue');
		var validateResult = validateFile(uploadFile);
		if(!validateResult) {
			return;
		}
		$("#uploadSiteStatus").show();
		$.ajax({
				url: '/sitefile/autoUploadFile',
				type: 'POST',
				cache: false,
				data: new FormData($('#uploadSiteFileForm')[0]),
				processData: false,
				contentType: false
			}).done(function (res) {
				if(res.status=="SUCCEED"){
					$("#uploadSiteStatus").hide();
					showTable(1);
				}else{
					$("#uploadSiteStatus").hide();
					if(res.errorMessage!=null){
						alertMsg(res.errorMessage);
					}else{
						alertMsg("upload error!");
					}
				}
			}).fail(function (res) {
				$("#uploadSiteStatus").hide();
				if(res.responseJSON!=undefined&&res.responseJSON.errors!=undefined){
					var errors = res.responseJSON.errors;
					var num = errors.length;
					for(var i=0;i<num;i++){
						if("validation.file.format.error"==errors[i].code){
							alertMsg("file name format error");
						}else if("validation.file.empty"==errors[i].code){
							alertMsg("upload file is empty")
						}
					}
				}else{
					alertMsg("upload error!");
				}
			});
			return false;
		}
	) 
});
var validateFile = function(uploadFile) {
	if (uploadFile === null || uploadFile === 'undefined' || uploadFile == '') {
		alertMsg('File must not be empty!');
		return false;
	}
	return true;
	
}
var alertMsg = function (msg) {
	$.messager.alert('Attention', msg, 'error');
}