
function getBrandUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/brand";
}

//BUTTON ACTIONS
function addBrand(event){
	//Set the values to update

	var $form = $("#brand-add-form");
	var json = toJson($form);
	var check = validateBrand(json);
	if(check){
	var url = getBrandUrl();

    	$.ajax({
    	   url: url,
    	   type: 'POST',
    	   data: json,
    	   headers: {
           	'Content-Type': 'application/json'
           },
    	   success: function(response) {
    	   		getBrandList();
    	   		$('#add-brand-modal').modal('hide');
    	   		toastr.options.closeButton=false;
    	   		toastr.options.timeOut=3000;
    	   		toastr.success("Brand added successfully");
    	   		toastr.options.closeButton=true;
                toastr.options.timeOut=none;
    	   },
    	   error: handleAjaxError
    	});
	}

	return false;
}

function updateBrand(event){
	$('#edit-brand-modal').modal('toggle');
	//Get the ID
	var id = $("#brand-edit-form input[name=id]").val();
	//Set the values to update
	var $form = $("#brand-edit-form");
	var json = toJson($form);
    var check = validateBrand(json);
    	if(check){
    	var url = getBrandUrl() + "/" + id;
	$.ajax({
	   url: url,
	   type: 'PUT',
	   data: json,
	   headers: {
       	'Content-Type': 'application/json'
       },
	   success: function(response) {
	   		getBrandList();
	   		toastr.options.closeButton=false;
            toastr.options.timeOut=3000;
            toastr.success("Brand updated successfully");
            toastr.options.closeButton=true;
            toastr.options.timeOut=none;
	   },
	   error: handleAjaxError
	});
    }
	return false;
}

function getBrandList(){
	var url = getBrandUrl();
	$.ajax({
	   url: url,
	   type: 'GET',
	   success: function(data) {
	   		displayBrandList(data);
	   },
	   error: handleAjaxError
	});
}

function validateBrand(json) {
	json = JSON.parse(json);
	if(isBlank(json.brand)) {
	    toastr.error("Brand field must not be empty")
		return false;
	}
	if(isBlank(json.category)) {
	toastr.error("Category field must not be empty")
		return false;
	}
	return true;
}
// FILE UPLOAD METHODS
var fileData = [];
var errorData = [];
var processCount = 0;


function processData(){
	var file = $('#brandFile')[0].files[0];
	readFileData(file, readFileDataCallback);
	console.log(file);
}

function readFileDataCallback(results){
	fileData = results.data;
	uploadRows();
}

function uploadRows(){
	//Update progress
	updateUploadDialog();
	//If everything processed then return
	if(processCount==fileData.length){
		return;
	}

	//Process next row
	var row = fileData[processCount];
	processCount++;
	var json = JSON.stringify(row);
	var check=validateBrand(json);
	var url = getBrandUrl();

    	//Make ajax call
    	$.ajax({
    	   url: url,
    	   type: 'POST',
    	   data: json,
    	   headers: {
           	'Content-Type': 'application/json'
           },
    	   success: function(response) {
    	   		uploadRows();
    	   },
    	   error: function(response){

    	   		row.error=response.responseText;
    	   		errorData.push(row);
    	   		uploadRows();
    	   }
    	});
}

function downloadErrors(){
	writeFileData(errorData);
}

//UI DISPLAY METHODS

function displayBrandList(data){
	var $tbody = $('#brand-table').find('tbody');
	$tbody.empty();
	for(let i in data){
		let e = data[i];
		var buttonHtml = ' <button class="btn btn-primary" title="Edit brand" onclick="displayEditBrand(' + e.id + ')">Edit</button>';
		var row = '<tr>'
		+ '<td>' + e.brand + '</td>'
		+ '<td>'  + e.category + '</td>'
		+ '<td>' + buttonHtml + '</td>'
		+ '</tr>';
        $tbody.append(row);
	}
}

function displayEditBrand(id){
	var url = getBrandUrl() + "/" + id;
	$.ajax({
	   url: url,
	   type: 'GET',
	   success: function(data) {
	   		displayBrand(data);
	   },
	   error: handleAjaxError
	});
}

function resetUploadDialog(){
	//Reset file name
	var $file = $('#brandFile');
	$file.val('');
	$('#brandFileName').html("Choose File");
	//Reset various counts
	processCount = 0;
	fileData = [];
	errorData = [];
	//Update counts
	updateUploadDialog();
}

function updateUploadDialog(){
	$('#rowCount').html("" + fileData.length);
	$('#processCount').html("" + processCount);
	$('#errorCount').html("" + errorData.length);
}

function updateFileName(){
	var $file = $('#brandFile');
	var fileName = $file.val();
	var actualfilename=$file.get(0).files.item(0).name;
	console.log(actualfilename);
	$('#brandFileName').html(actualfilename);

}

function displayUploadData(){
 	resetUploadDialog();
	$('#upload-brand-modal').modal('toggle');
}

function displayBrand(data){
	$("#brand-edit-form input[name=brand]").val(data.brand);
	$("#brand-edit-form input[name=category]").val(data.category);
	$("#brand-edit-form input[name=id]").val(data.id);
	$('#edit-brand-modal').modal('toggle');
}

function displayAddBrand(){
    $('#add-brand-modal').modal('toggle');
}

//INITIALIZATION CODE
function init(){
	$('#submit-brand').click(addBrand);
	$('#add-brand').click(displayAddBrand);
	$('#update-brand').click(updateBrand);
	$('#refresh-data').click(getBrandList);
	$('#upload-data').click(displayUploadData);
	$('#process-data').click(processData);
	$('#download-errors').click(downloadErrors);
    $('#brandFile').on('change', updateFileName);
}

$(document).ready(init);
$(document).ready(getBrandList);

