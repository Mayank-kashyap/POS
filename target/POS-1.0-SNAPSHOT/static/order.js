var orderitemList = [];

function getOrderUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content");
	console.log(baseUrl);
	return baseUrl + "/api/order";
}

function getOrderItemUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content");
	console.log(baseUrl);
	return baseUrl + "/api/order_item";
}

function getSingleOrderUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content");
	console.log(baseUrl);
	return baseUrl + "/api/singleOrder";
}

function getAllOrdersUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content");
	console.log(baseUrl);
	return baseUrl + "/api/all_orders";
}

function getInvoiceUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content");
	console.log(baseUrl);
	return baseUrl + "/api/invoice";
}

function addOrderItemToList(event) {
	var $form = $("#orderItem-form");
	var json = toJson($form);
	console.log(json);
	var check = validateOrderItem(json);
	if(check) {
		var ind = checkIfAlreadyPresent(JSON.parse(json).barcode);

		if(ind==-1){
			if(parseInt(inventoryMap[JSON.parse(json).barcode])>=parseInt(JSON.parse(json).quantity)) {
				orderitemList.push(JSON.parse(json));
			}
			else{
				toastr.error("The product inventory is: " + inventoryMap[JSON.parse(json).barcode] + "order cannot be placed more than that");

			}
		}
		else{
			var qty = parseInt(orderitemList[ind].quantity) + parseInt(JSON.parse(json).quantity);
			if(parseInt(inventoryMap[JSON.parse(json).barcode])>=qty){
				orderitemList[ind].quantity = qty;
			}
			else{
				if(!inventoryMap[JSON.parse(json).barcode]){
					toastr.error("The product's inventory is: " + 0);

				}
				toastr.error("The product inventory is: " + inventoryMap[JSON.parse(json).barcode] + "Cannot order more than that");
				}
		}
	}
	console.log(orderitemList);
	getOrderItemList();
}

function getOrderItemList() {
	displayOrderItemListFrontend(orderitemList);
}

function addOrderItem(event){

	var $form = $("#orderItem-add-form");
	var json = toJson($form);
	var order_id = $("#orderItem-add-form input[name=order_id]").val();
    console.log(order_id);
	var url = getOrderItemUrl() + "/" + order_id;
	var check = validateOrderItem(json);
	console.log(check);
	if(check) {
	console.log(check);
		ajaxQuery(url,'POST',json,function(response) {
			getOrderList(response);

			$("#add-orderItem-modal").modal('toggle');
		},handleAjaxError);
	}
	return false;
}

function addOrder(event) {

	if(orderitemList.length == 0) {
		toastr.error("Add at least one order item");
		return;
	}

	var json = JSON.stringify(orderitemList);
	orderitemList=[];
	var url = getOrderUrl();

	ajaxQuery(url,'POST',json,function (response) {
		getOrderList(response);
		$("#add-order-modal").modal('toggle');
	},handleAjaxError);

	return false;
}

function updateOrder(event){
	$('#edit-orderItem-modal').modal('toggle');
	//Get the ID
	var id = $("#orderItem-edit-form input[name=id]").val();
	var orderId = $("#orderItem-edit-form input[name=order-id]").val();
	console.log(orderId);
	var url = getOrderItemUrl() + "/" + id;


	var $form = $("#orderItem-edit-form");
	var json = toJson($form);

	var check = validateOrderItem(json);
	if(check){
		ajaxQuery(url,'PUT',json,function (response) {
			getOrderList(response);
			var orderitem_row = '.orderitemrows' + orderId;
		  $(orderitem_row).show();
			console.log(json);
		},handleAjaxError);
	}

	return false;

}

function deleteOrderItemFromOrderList(id) {
	var url = getOrderItemUrl() + "/" + id;
	ajaxQuery(url,'DELETE','',getOrderList,handleAjaxError);
}

function deleteOrder(id) {
	var url = getOrderUrl() + "/" + id;
	ajaxQuery(url,'DELETE','',getOrderList,handleAjaxError);
}

function deleteOrderItem(id) {
	orderitemList.splice(id,1);
	getOrderItemList();
}


function getOrderList() {
	var url = getOrderUrl();
	$.ajax({
    		 url: url,
    		 type: 'GET',
    		 headers: {
    				'Content-Type': 'application/json'
    			 },
    		 success: function(response) {
    				displayOrdersList(response);
    		 },
    		 error: function(response){
    				handleAjaxError(response);
    		 }
    	});
}

function getOrderItemsHtml(id) {
	var url = getOrderUrl() + "/" + id;
	$.ajax({
		 url: url,
		 type: 'GET',
		 headers: {
				'Content-Type': 'application/json'
			 },
		 success: function(response) {
				createOrderItemsHtml(response,id);
		 },
		 error: function(response){
				handleAjaxError(response);
		 }
	});
}


//UI DISPLAY METHODS

function displayOrderItemListFrontend(data){
	console.log('Printing Order items');
	var $tbody = $('#orderItem-table').find('tbody');
	$tbody.empty();
	for(var i in data){
		var e = data[i];
		var buttonHtml = '<button class="btn btn-primary" onclick="deleteOrderItem(' + i + ')">delete</button>'
		var row = '<tr>'
		+ '<td>' + (i) + '</td>'
		+ '<td>' + e.barcode + '</td>'
		+ '<td>'  + e.quantity + '</td>'
		+ '<td>'  + e.sp + '</td>'
		+ '<td>' + buttonHtml + '</td>'
		+ '</tr>';
        $tbody.append(row);
	}
}

function displayOrdersList(data) {
	console.log('Printing Orders');
	var $tbody = $('#order-table2').find('tbody');
	$tbody.empty();
	data.reverse();
	for(var i in data){
		var e = data[i];
		var buttonHtml = '<button class="btn btn-primary" onclick="initializeDropdown(' + e.id + ')">View</button>  <button class="btn btn-primary" onclick="downloadPDF('+ e.id +')">Download Invoice</button>  <button class="btn btn-primary btnSend" onclick="displayAddOrderItemModal(' + e.id + ')">Add Order Item</button>';
		var row = '<tr class="order-header">'
		+ '<td>' + e.id + '</td>'
		+ '<td>'  + e.datetime + '</td>'
		+ '<td>' + buttonHtml + '</td>'
		+ '</tr>';
		orderitemsHtml = '<tr><td colspan="3"><table class="table table-striped orderitemrows' + e.id +'"><tbody></tbody></table><td></tr>';
        $tbody.append(row);
		$tbody.append(orderitemsHtml);
		getOrderItemsHtml(e.id);
	}
}

function displayEditOrderItem(id){
	var url = getOrderItemUrl() + "/" + id;
	ajaxQuery(url,'GET','',displayOrderItem,handleAjaxError);
}

function displayAddOrderModal() {
	$("#add-order-modal").modal('toggle');
}

function downloadPDF(id) {
	var url = getInvoiceUrl() + "/" + id;
	$.ajax({
	   url: url,
	   type: 'GET',
	    xhrFields: {
        responseType: 'blob'
     },
	   success: function(blob) {
	   console.log(blob);
		console.log(blob.size);
      	var link=document.createElement('a');
      	link.href=window.URL.createObjectURL(blob);
      	link.download="Invoice_" + new Date() + ".pdf";
      	link.click();
	   },
	   error: function(response){
	   		handleAjaxError(response);
	   }
	});
}
/*
function disableButtons(int id){
    var url=getSingleOrderUrl() + "/" + id;
    $.ajax({
    		 url: url,
    		 type: 'GET',
    		 headers: {
    				'Content-Type': 'application/json'
    			 },
    		 success: function(response) {
    				checkInvoiceGenerated(response);
    		 },
    		 error: function(response){
    				handleAjaxError(response);
    		 }
    	});
}

function checkInvoiceGenerated(json){
json=JSON.parse(json);
console.log(json.isInvoiceGenerated);
}
*/

function displayOrderItem(data){
	console.log(data);
	$("#orderItem-edit-form input[name=barcode]").val(data.barcode);
	$("#orderItem-edit-form input[name=quantity]").val(data.quantity);
	$("#orderItem-edit-form input[name=id]").val(data.id);
	$("#orderItem-edit-form input[name=order-id]").val(data.orderId);
	$("#orderItem-edit-form input[name=sp]").val(data.sp);
	$('#edit-orderItem-modal').modal('toggle');
}

function displayAddOrderItemModal(order_id) {
	$("#orderItem-add-form input[name=order_id]").val(order_id);
	$('#add-orderItem-modal').modal('toggle');
}


function createOrderItemsHtml(data,id) {

	var table = $('.orderitemrows' + id).find('tbody');
	var thHtml = '<tr>';
	thHtml += '<th scope="col">Id</th>';
	thHtml += '<th scope="col">Barcode</th>';
	thHtml += '<th scope="col">Quantity</th>';
	thHtml += '<th scope="col">SP</th>';
	thHtml += '<th scope="col">Actions</th>';
	thHtml += '</tr>';
	table.append(thHtml);
	for(var i in data){
		var e = data[i];
		var buttonHtml = '<button class="btn btn-primary btnSend" onclick="displayEditOrderItem(' + e.id + ')">Edit</button>';
		var row = '<tr>'
		+ '<td>' + e.id + '</td>'
		+ '<td>' + e.barcode + '</td>'
		+ '<td>'  + e.quantity + '</td>'
		+ '<td>' + e.sp + '</td>'
		+ '<td>' + buttonHtml + '</td>'
		+ '</tr>';
		table.append(row);
	}
}

function initializeDropdown(id) {
	console.log("Orderitems toggle");
	var orderitem_row = '.orderitemrows' + id;
  $(orderitem_row).toggle();
}

function validateOrderItem(json) {
	json = JSON.parse(json);
	if(isBlank(json.barcode)) {
		toastr.error("Barcode must not be empty");

		return false;
	}
	if(isBlank(json.quantity) || isNaN(parseInt(json.quantity)) || !isInt(json.quantity)) {
		toastr.error("Quantity must not be empty and must be an integer value");

		return false;
	}

	if(parseInt(json.quantity)<=0) {
		toastr.error("Quantity must be positive");

		return false;
	}

	if(isBlank(json.sp) || isNaN(parseFloat(json.sp))) {
    		toastr.error("Selling price must not be empty and must be a double value");
    		return false;
    	}

    if(parseFloat(json.sp)<=0) {
    		toastr.error("Selling price must be positive");
    		return false;
    	}
	return true;
}

function checkIfAlreadyPresent(barcode) {
	for(var i in orderitemList) {
		var e = orderitemList[i];
		if(e.barcode.localeCompare(barcode) == 0){
			return i;
		}
	}
	return -1;
}


//helper
function ajaxQuery(url, type, data, successFunction,errorFunction) {
	$.ajax({
	   url: url,
	   type: type,
	   data: data,
	   headers: {
       	'Content-Type': 'application/json'
       },
	   success: function(response) {
	   		successFunction(response);
	   },
	   error: function(response){
	   		errorFunction(response);
	   }
	});
}

function ajaxQueryRecur(url, type, data, successFunction,recurFunction) {
	$.ajax({
	   url: url,
	   type: type,
	   data: data,
	   headers: {
       	'Content-Type': 'application/json'
       },
	   success: function(response) {
	   		successFunction(response);
	   },
	   error: function(response){
			  var error_obj = JSON.parse(response.responseText);
			  var error = "For " + data;
				console.log(error_obj.message);
				error_obj.message = error + " " + error_obj.message;
				toastr.error(error_obj.message);
				errorData.push(error_obj);
				recurFunction();
	   }
	});
}

function isBlank(str) {
    return (!str || /^\s*$/.test(str));
}

function isInt(n) {
   return n % 1 === 0;
}

//INITIALIZATION CODE
function init(){
	$("#open-add-order").click(displayAddOrderModal);
	$('#add-orderItem').click(addOrderItemToList);
	$('#refresh-data').click(getOrderItemList);
	$('#add-order').click(addOrder);
	$("#add-orderItem-previousOrders").click(addOrderItem);
	$('#update-orderItem').click(updateOrder);
}

$(document).ready(init);
$(document).ready(getOrderItemList);
$(document).ready(getOrderList);
