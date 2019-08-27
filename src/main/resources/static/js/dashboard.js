$(function(){
	$('#lobSiteTable').datagrid({
		url: '/data/getDomanLobSiteList',
		fitColumns: true,
		striped: true,
		method: 'get',
		singleSelect: true,
		rownumbers: true,
		columns: [[
			{field:'site',title:'CENTER-Org Node Description',width:'500px', align: 'center'},
			{field:'lobSite',title:'Lob Site', width: '500px', align: 'center'},
			{field:'domain',title:'Domain Name',width: '268px', align: 'center'},
			{field:'lob',title:'Lob Display Name',width: '500px', align: 'center'}
		]]
	});
	$("#master").click(function(){
		$("#master").css("background-color","#FF9900");
		$("#site").css("background-color","");
		$("#dashboard").css("background-color","");
		showTable(0);
	});
	$("#site").click(function(){
		$("#master").css("background-color","");
		$("#site").css("background-color","#FF9900");
		$("#dashboard").css("background-color","");
		showTable(1);
	});
	$("#dashboard").click(function(){
		$("#master").css("background-color","");
		$("#site").css("background-color","");
		$("#dashboard").css("background-color","#FF9900");
		showTable(2);
	});
	$("#logout").click(function(){
		$.ajax({
			url:'/logout',
			method: 'POST'
		}).done(function(res,status){
			if(status==='success') {
				window.location.href = '/login';
			}
		});
	});
	

});
var loadDetail = function(parseLogId){
	$('#dlg').dialog({
		width:1300,
	    height:600,
	    modal:true,
	    cache: false,
	    resizable: true
	});
	$('#autoUploadDetailTable').datagrid({
		url: '/data/getAutoUploadDetail',
		fitColumns: true,
		striped: true,
		method: 'get',
		singleSelect: true,
		rownumbers: true,
		queryParams: {
			parseLogId: parseLogId
		},
		columns: [[
			{field:'fileName',title:'File Name',align: 'center'},
			{field:'fileDate',title:'File Date',align: 'center',formatter: formatter1},
			{field:'domainName',title:'Domain Name',align: 'center'},
			{field:'lobName',title:'Lob',align: 'center'},
			{field:'siteName',title:'Site',align: 'center'},
			{field:'status',title:'Upload Status', formatter: formatterUploadStatus},
			{field:'uploader',title:'Uploader',align: 'center'},
			{field:'attemptNumber',title:'Attempt Times',align: 'center'},
			{field:'comment',title:'Comment',align: 'center'},
		]]
	});
}
var formatterUploadStatus = function(value, row, index) {
	if (value === 1) {
		return '<span style="color: green;">Completed</span>';
	} 
	if (value === 0){
		return '<span style="color: blue;">On Going</span>';
	}
	if (value === 2){
		return '<span style="color: red;">Failed</span>';
	}
};

//YYYY-MM-DD
var formatter1 = function(value, row, index) {
	 var date = new Date(value);
	    Y = date.getFullYear() + '-';
	    M = (date.getMonth()+1 < 10 ? '0'+(date.getMonth()+1) : date.getMonth()+1) + '-';
	    D = date.getDate()<10?'0'+date.getDate():date.getDate();
	    return Y+M+D;
};

//YYYY-MM-DD hh:mm:ss
var formatter2 = function(value, row, index) {
	var date = new Date(value);
	Y = date.getFullYear() + '-';
	M = (date.getMonth()+1 < 10 ? '0'+(date.getMonth()+1) : date.getMonth()+1) + '-';
	D = date.getDate()<10?'0'+date.getDate():date.getDate()+' ';
	h = date.getHours()<10?'0'+ date.getHours():date.getHours()+ ':';
	m = date.getMinutes()<10?'0'+date.getMinutes():date.getMinutes() + ':';
	s = date.getSeconds()<10?'0'+date.getSeconds():date.getSeconds();
	return Y+M+D+' '+h+m+s;
};
//format 'Action' column
var formatterZip = function(value, row, index) {
	var fileName = row.fileName;
	var parseLogId = row.id;
	var fileZipName = fileName.substring(0, fileName.indexOf('.'))+".zip";
	var downloadLink =  "<a href='/data/download?fileName="+fileZipName+"'>Download</a>";
	var detailLink =  "<a class='detailLink' href='#' onclick='loadDetail("+parseLogId+")'>Detail</a>";
	var deleteLink =  "<a class='deleteLink' href='#' onclick='deleteHistory("+parseLogId+",\""+fileName+"\")'>Delete</a>";
	
	return downloadLink +" "+ detailLink+" "+deleteLink;
};

var deleteHistory = function(parseLogId, fileName) {
	$.messager.confirm('Delete Confirm', "Confirm to delete the upload record for '"+fileName+"'?", function(r){
		if (r){
			$.ajax({
				url: '/data/deleteUploadDetail',
				type: 'PUT',
				data: {parseLogId: parseLogId}
			}).done(function(res){
				$('#historyTable').datagrid('reload');
			});
		}
	});
};
var showTable = function(type){
	if (type === 0) {
		$('#uploadBigFileDiv').show();
		$('#uploadSiteFileDiv').hide();
	}
	if (type === 1) {
		$('#uploadBigFileDiv').hide();
		$('#uploadSiteFileDiv').show();
	}
	if (type === 2) {
		$('#lobSiteDiv').show();
		$('#historyDiv').hide();
		$('#lobSiteTable').datagrid('reload');
		return;
	}
	var IsCheckFlag = false;
	$('#historyDiv').show();
	$('#lobSiteDiv').hide();
	$('#historyTable').datagrid({
		url: '/data/getMasterParserHistory',
		fitColumns: true,
		striped: true,
		method: 'get',
		rownumbers: true,
		checkOnSelect: false,
		selectOnCheck: true,
		queryParams: {
			fileType: type
		},
		columns: [[
			{field:'fileDate',title:'File Date', width: '300px', align: 'center',formatter: formatter1},
			{field:'fileName',title:'File Name',width: '600px',align: 'center'},
			{field:'startTime',title:'Upload Start Time', width: '300px', align: 'center',formatter: formatter2},
			{field:'endTime',title:'Upload End Time', width: '300px',align: 'center', formatter: formatter2},
			{field:'action',title: 'Action', width: '202px',align: 'center', formatter: formatterZip},
		]],
		toolbar: type==0?'#uploadBigFileDiv':'#uploadSiteFileDiv',
		onClickCell: function (rowIndex, field, value) {
		    IsCheckFlag = false;
		},
	     onSelect: function (rowIndex, rowData) {
	         if (!IsCheckFlag) {
	             IsCheckFlag = true;
	             $("#dg").datagrid("unselectRow", rowIndex);
	         }
	     },                    
		 onUnselect: function (rowIndex, rowData) {
		     if (!IsCheckFlag) {
		         IsCheckFlag = true;
		         $("#dg").datagrid("selectRow", rowIndex);
		     }
		 }
	});
}

