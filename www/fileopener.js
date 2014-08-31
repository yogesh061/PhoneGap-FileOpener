window.openFile = function(file_name, file_type, callback){
	cordova.exec(callback, function(err){
		alert("error:: "+err);
	}, "FileOpener", "open", [file_name, file_type])
}