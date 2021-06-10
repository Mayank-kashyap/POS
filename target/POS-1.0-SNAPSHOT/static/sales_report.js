function getReportUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content");
	return baseUrl + "/api/report";
}
function getSalesReport(){
	var $form = $("#sales-report-form");
	var json = toJson($form);
	console.log(json);
	if(true) {
		var url = getReportUrl() + "/sales";

		$.ajax({
		   url: url,
		   type: 'POST',
			 data: json,
			 headers: {
	       	'Content-Type': 'application/json'
	       },
			 xhrFields: {
	        responseType: 'blob'
	     },
		   success: function(blob) {
					console.log(blob.size);
	      	var link=document.createElement('a');
	      	link.href=window.URL.createObjectURL(blob);
	      	link.download="SalesReport_" + new Date() + ".pdf";
	      	link.click();
		   },
		   error: function(response){

		   		toastr.error("No sales data was found within given date range and brand category pair");
		   }
		});
	}

}


function endDateValidation() {
	var startDate = document.getElementById("inputStartDate").value;
	var endDate = document.getElementById("inputEndDate").value;
	var start=new Date();
        var end=new Date();
        start.setMonth(end.getMonth()-6);
	if ((Date.parse(startDate) > Date.parse(endDate))) {
			toastr.error("End date should be greater than Start date");
			document.getElementById("inputEndDate").value = end.toLocaleDateString("en-CA");
	}
	if((Date.parse(endDate)>end))
	document.getElementById("inputEndDate").value = end.toLocaleDateString("en-CA");
}

function startDateValidation() {
	var startDate = document.getElementById("inputStartDate").value;
	var endDate = document.getElementById("inputEndDate").value;
	var start=new Date();
        var end=new Date();
        start.setMonth(end.getMonth()-6);
	if ((Date.parse(startDate) > Date.parse(endDate))) {
			toastr.error("Start date should be lesser than End date");
			document.getElementById("inputStartDate").value = start.toLocaleDateString("en-CA");
	}
}

function validateSalesForm(json) {
	json = JSON.parse(json);
	if(!brandList.includes(json.brand) && !isBlank(json.brand)) {
		toastr.error("Please enter valid brand");
		return false;
	}
	if(!categoryList.includes(json.category) && !isBlank(json.category)) {
		toastr.error("Please enter valid category");
		return false;
	}
	return true;
}
function setDefaultDateValues() {
    var start=new Date();
    var end=new Date();
    start.setMonth(end.getMonth()-6);
	document.getElementById("inputStartDate").value =  start.toLocaleDateString("en-CA");
	document.getElementById("inputEndDate").value =  end.toLocaleDateString("en-CA");
}

function init(){
	$('#sales-report').click(getSalesReport);
	$("#inputEndDate").change(endDateValidation);
	$("#inputStartDate").change(startDateValidation);

}

$(document).ready(init);
$(document).ready(setDefaultDateValues);
