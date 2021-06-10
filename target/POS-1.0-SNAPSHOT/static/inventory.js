
function getInventoryUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/inventory";
}

function getInventoryListUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/inventory/list";
}
//BUTTON ACTIONS
function addInventory(event){
	//Set the values to update
	var $form = $("#inventory-add-form");
	var json = toJson($form);
	var url = getInventoryUrl();
    var check = validateInventory(json);
    	if(check){
	$.ajax({
	   url: url,
	   type: 'POST',
	   data: json,
	   headers: {
       	'Content-Type': 'application/json'
       },
	   success: function(response) {
	   		getInventoryList();
	   		$('#add-inventory-modal').modal('hide');
	   		toastr.options.closeButton=false;
            toastr.options.timeOut=3000;
            toastr.success("Product Inventory added successfully");
            toastr.options.closeButton=true;
            toastr.options.timeOut=0;
	   },
	   error: handleAjaxError
	});
    }
	return false;
}

function displayAddInventory(){
$('#add-inventory-modal').modal('toggle');
}
function updateInventory(event){
	$('#edit-inventory-modal').modal('toggle');
	//Get the ID
	var id = $("#inventory-edit-form input[name=id]").val();
	var url = getInventoryUrl() + "/" + id;

	//Set the values to update
	var $form = $("#inventory-edit-form");
	var json = toJson($form);
    var check = validateInventory(json);
    	if(check){
	$.ajax({
	   url: url,
	   type: 'PUT',
	   data: json,
	   headers: {
       	'Content-Type': 'application/json'
       },
	   success: function(response) {
	   		getInventoryList();
	   		toastr.options.closeButton=false;
            toastr.options.timeOut=3000;
	   		toastr.success("Product inventory updated successfully");
	   		toastr.options.closeButton=true;
            toastr.options.timeOut=0;
	   },
	   error: handleAjaxError
	});
    }
	return false;
}

function getInventoryList(){
	var url = getInventoryUrl();
	$.ajax({
	   url: url,
	   type: 'GET',
	   success: function(data) {
	   		displayInventoryList(data);
	   },
	   error: handleAjaxError
	});
}

function validateInventoryUpload(arr) {
	for(var i in arr){
	var json=arr[i];
	    if(isBlank(json.barcode)) {
        		toastr.error("Barcode field must not be empty");
        		return false;
        	}
        	if(isBlank(json.quantity)) {
        		toastr.error("Quantity field must not be empty");
        		return false;
        	}
        	else if(isNaN(parseInt(json.quantity)) || !isInt(json.quantity)){
        	    toastr.error("Quantity field must be an integer value: "+ json.quantity);
                        		return false;
        	}
        	if(parseInt(json.quantity)<=0){
            	toastr.error("Quantity must be positive");
                return false;
            	}
	}

	return true;
}

function validateInventory(json) {
	json = JSON.parse(json);
	if(isBlank(json.barcode)) {
		toastr.error("Barcode field must not be empty");
		return false;
	}
	if(isBlank(json.quantity)) {
            		toastr.error("Quantity field must not be empty");
            		return false;
            	}
            	else if(isNaN(parseInt(json.quantity)) || !isInt(json.quantity)){
            	    toastr.error("Quantity field must be an integer value: "+ json.quantity);
                            		return false;
            	}

	if(parseInt(json.quantity)<=0){
    	toastr.error("Quantity must be positive");
        return false;
    	}
	return true;
}

// FILE UPLOAD METHODS
var fileData = [];
var errorData = [];
var processCount = 0;

function processData(){
	var file = $('#inventoryFile')[0].files[0];
	checkHeader(file,["barcode","quantity"],readFileDataCallback);
}

function readFileDataCallback(results){
	fileData = results.data;
	uploadRows();
}

function uploadRows(){
	//Update progress
	updateUploadDialog();
	var row = fileData;
	var check=validateInventoryUpload(row);
            	var json = JSON.stringify(row);
            	var url = getInventoryListUrl();
    if(check)
    {
        //Make ajax call
        	$.ajax({
        	   url: url,
        	   type: 'POST',
        	   data: json,
        	   headers: {
               	'Content-Type': 'application/json'
               },
        	   success: function(response) {
        	   		console.log(response);
                    	   		toastr.options.closeButton=false;
                                    	   		toastr.options.timeOut=3000;
                                    	   		toastr.success("File uploaded successfully");
                                    	   		toastr.options.closeButton=true;
                                                toastr.options.timeOut=0;
        	   },
        	   error: function(response){
        	   		console.log(response);
                    toastr.error("File cannot be uploaded: "+JSON.parse(response.responseText).message);
        	   }
        	});
    }
	return false;
}

function downloadErrors(){
	writeFileData(errorData);
}

//UI DISPLAY METHODS
function displayInventoryList(data){
	var $tbody = $('#inventory-table').find('tbody');
	$tbody.empty();
	for(var i in data){
		var e = data[i];
		var buttonHtml = ' <button class="btn btn-primary" onclick="displayEditInventory(' + e.id + ')">Edit</button>';
		var row = '<tr>'
		+ '<td>' + e.barcode + '</td>'
		+ '<td>'  + e.quantity + '</td>'
		+ '<td>' + buttonHtml + '</td>'
		+ '</tr>';
        $tbody.append(row);
	}
}

function displayEditInventory(id){
	var url = getInventoryUrl() + "/" + id;
	$.ajax({
	   url: url,
	   type: 'GET',
	   success: function(data) {
	   		displayInventory(data);
	   },
	   error: handleAjaxError
	});
}

function resetUploadDialog(){
	//Reset file name
	var $file = $('#inventoryFile');
	$file.val('');
	$('#inventoryFileName').html("Choose File");
	//Reset various counts
	processCount = 0;
	fileData = [];
	errorData = [];
	//Update counts
	updateUploadDialog();
}

function updateUploadDialog(){
	$('#rowCount').html("" + fileData.length);
}

function updateFileName(){
	var $file = $('#inventoryFile');
	var fileName = $file.val();
	var actualfilename=$file.get(0).files.item(0).name;
    console.log(actualfilename);
	$('#inventoryFileName').html(actualfilename);
}

function displayUploadData(){
 	resetUploadDialog();
	$('#upload-inventory-modal').modal('toggle');
}

function displayInventory(data){
	$("#inventory-edit-form input[name=barcode]").val(data.barcode);
	$("#inventory-edit-form input[name=quantity]").val(data.quantity);
	$("#inventory-edit-form input[name=id]").val(data.id);
	$('#edit-inventory-modal').modal('toggle');
}

//INITIALIZATION CODE
function init(){
	$('#add-inventory').click(displayAddInventory);
	$('#submit-inventory').click(addInventory);
	$('#update-inventory').click(updateInventory);
	$('#refresh-data').click(getInventoryList);
	$('#close-upload').click(getInventoryList);
	$('#upload-data').click(displayUploadData);
	$('#process-data').click(processData);

    $('#inventoryFile').on('change', updateFileName)
}

$(document).ready(init);
$(document).ready(getInventoryList);

