var selectedAssignmentFileCount, totalAssignmentUploadedValue, fileCount, filesAssignmentUploaded;

function onUploadAssignmentComplete(e) {
	totalAssignmentUploadedValue += document.getElementById('files').files[filesAssignmentUploaded].size;
	filesAssignmentUploaded++;
	if (filesAssignmentUploaded < fileAssignmentCount) {
		uploadNext();
	} else {
		var bar = document.getElementById('bar');
		bar.style.width = '100%';
		bar.innerHTML = '100% complete';
		swal("Success!", "Finished uploading file(s)", "success");
	}
}

function onAssignmentFileSelect(e) {
	files = e.target.files; // FileList object
	var output = [];
	fileAssignmentCount = files.length;
	selectedAssignmentFileCount = 0;
	for (var i = 0; i < fileAssignmentCount; i++) {
		var file = files[i];
		output.push(file.name, ' (', file.size, ' bytes, ', file.lastModifiedDate
				.toLocaleDateString(), ')');
		output.push('<br/>');
		selectedAssignmentFileCount += file.size;
	}
	document.getElementById('selectedFiles').innerHTML = output.join('');

}

function onUploadAssignmentProgress(e) {
	if (e.lengthComputable) {
		var percentComplete = parseInt((e.loaded + totalAssignmentUploadedValue) * 100 / selectedAssignmentFileCount);
		var bar = document.getElementById('bar');
		bar.style.width = percentComplete + '%';
		bar.innerHTML = percentComplete + ' % Completed';
	} else {
		console.err("Unable to compute length");
	}
}

function onUploadAssignmentFailed(e) {
	swal("Error!", "Error uploading file(s)", "danger");
}

function uploadAssignmentNext() {
	var zxhr = new XMLHttpRequest();
	var zfd = new FormData();
	var zfile = document.getElementById('files').files[filesAssignmentUploaded];
	zfd.append("multipartFile", zfile);
	zxhr.upload.addEventListener("progress", onUploadAssignmentProgress, false);
	zxhr.addEventListener("load", onUploadAssignmentComplete, false);
	zxhr.addEventListener("error", onUploadAssignmentFailed, false);
	zxhr.open("POST", "/upload");
	zxhr.send(zfd);
}

function startAssignmentUpload() {
	if (document.getElementById('files').files.length <= 0) {
		swal("Cannot Upload!", "Please select file(s) to upload", "warning");
	} else {
		totalAssignmentUploadedValue = filesAssignmentUploaded = 0;
		uploadAssignmentNext();
	}
}

function resetAssignmentScreen() {
	document.getElementById('bar').style.width = '0%';
	document.getElementById('bar').innerText = '';
	document.getElementById("selectedFiles").innerHTML = '';
	document.getElementById("imageForm").reset();
}

window.onload = function() {
	document.getElementById('files').addEventListener('change', onAssignmentFileSelect, false);
	document.getElementById('uploadButton').addEventListener('click', startAssignmentUpload, false);
	document.getElementById('resetButton').addEventListener('click', resetAssignmentScreen, false);
}
