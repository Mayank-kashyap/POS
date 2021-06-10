function getReportUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content");
	return baseUrl + "/api/report";
}


function getBrandReport(){
	var url = getReportUrl() + "/brand";
	console.log(url);
	$.ajax({
	   url: url,
	   type: 'GET',
		 xhrFields: {
        responseType: 'blob'
     },
	   success: function(blob) {

      	var link=document.createElement('a');
      	link.href=window.URL.createObjectURL(blob);
      	link.download="BrandReport_" + new Date() + ".pdf";
      	link.click();
	   },
	   error: function(response){
	   		handleAjaxError(response);
	   }
	});
}

function getInventoryReport(){
	var url = getReportUrl() + "/inventory";
	console.log(url);
	$.ajax({
	   url: url,
	   type: 'GET',
		 xhrFields: {
        responseType: 'blob'
     },
	   success: function(blob) {
				console.log(blob.size);
      	var link=document.createElement('a');
      	link.href=window.URL.createObjectURL(blob);
      	link.download="InventoryReport_" + new Date() + ".pdf";
      	link.click();
	   },
	   error: function(response){
	   		handleAjaxError(response);
	   }
	});
}

function init(){
	$('#brand-report').click(getBrandReport);
	$('#inventory-report').click(getInventoryReport);

}

$(document).ready(init);
