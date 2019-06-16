var marketHidden = false;
var vendorHidden = false;
var resourceHidden = false;
var recycleHidden = false;

$(function() {
	$.ajax({
		url : "GetItems",
		type : "POST",
		dataType : "json",
		success : function(data) {
			$("#combobox").html('');
			$.each(data, function(i, item) {
				var $tr = $('<option>').append(item).appendTo('#combobox');
			});
			$("#combobox").change();
		},
		error : function(data) {
			DisplayServerMessages(["ERROR: " + data.responseText]);
		}
	});

	$('#combobox').change(function() {
		getPrice();
	});	
	$('#historicalCheck').change(function() {
		getPrice();
	});	
	$('#resourceCheck').change(function() {
		getPrice();
	});	
	$('#recycleCheck').change(function() {
		getPrice();
	});
	
	window.setInterval(RefreshPrice, 60000);
});

$(function() {
	$('#priceChart')
			.highcharts(
					{
						chart : {
							zoomType : 'x',
							height: 600,
						},
						title : {
							text : 'Price History',
							x : -20
						//center
						},
						xAxis : {
							type : 'datetime',
							title : {
								text : 'Local Time'
							},
							labels : {
								formatter : function() {
									var date = new Date(this.value);
									return date.toLocaleTimeString()
											+ "<br/>"
											+ date.toLocaleDateString();
								},
							}
						},
						yAxis : {
							title : {
								text : '$$ Value'
							}
						},
						tooltip : {
							formatter : function() {
								if (this.y) {
									var price = this.y;
									var priceLength = price.toString().length;
									
									switch (priceLength) {
										case 5:
											price = (price / 1000).toFixed(1) + "K";
											break;
										case 6:
											price = (price / 1000).toFixed(0) + "K";
											break;
										case 7:
											price = (price /100000).toFixed(2) + "M";
											break;
										case 8:
											price = (price /1000000).toFixed(1) + "M";
											break;
										case 9:
											price = (price /1000000).toFixed(0) + "M";
											break;
										case 10:
											price = (price /1000000).toFixed(2) + "B";
											break;
										case 11:
											price = (price /1000000000).toFixed(1) + "B";
											break;
										case 12:
											price = (price /1000000000).toFixed(0) + "B";
											break;
									}

									return '<span style="color:' + this.series.color + '">'
											+ this.series.name
											+ '</span>'
											+ ': <b>$'
											+ price
											+ '</b><br/>'
											+ new Date(this.x)
													.toLocaleTimeString();
								}
							}
						},
						legend : {
							layout : 'vertical',
							align : 'right',
							verticalAlign : 'middle',
							borderWidth : 0
						},
						plotOptions: {
							series: {
								events: {
									hide: function() {
										switch(this.name) {
										case "Market":
											marketHidden = true;
											break;
										case "Vendor":
											vendorHidden = true;
											break;
										case "Resource Cost":
											resourceHidden = true;
											break;
										case "Recycle Yield":
											recycleHidden = true;
											break;
										default:
											break;											
										}
									},
									show: function() {
										switch(this.name) {
										case "Market":
											marketHidden = false;
											break;
										case "Vendor":
											vendorHidden = false;
											break;
										case "Resource Cost":
											resourceHidden = false;
											break;
										case "Recycle Yield":
											recycleHidden = false;
										default:
											break;											
										}
									},
								}
							}
						}							
					});
});

function getPrice() {
	$("#priceChart").highcharts().showLoading();

	$.ajax({
		url : "Chart",
		type : "POST",
		data : {
			itemname : $("#combobox option:selected").text(),
			getHistorical : $("#historicalCheck").is(":checked"),
			getResources: $("#resourceCheck").is(":checked"),
			getRecycle: $("#recycleCheck").is(":checked"),
		},
		dataType : "json",
		success : function(data) {
			$("#priceChart").highcharts().hideLoading();
			LoadJsonToChart(data);
		},
		error : function(data) {
			$("#priceChart").highcharts().hideLoading();
			DisplayServerMessages(["ERROR: " + data.responseText]);
		}
	});
}

function RefreshPrice() {
	if($("#refreshCheck").is(":checked")) {
		getPrice();
		$('#priceChart .highcharts-button').click();	// reset zoom			
	}
}

function LoadJsonToChart(json) {
	var chart = $('#priceChart').highcharts();
	
	// Clear 
	while(chart.series.length > 0)
		chart.series[0].remove(true);
	
	var resourceCalc = json.shift();
	$("#resourceCalc").text($("#resourceCheck").is(":checked") ? resourceCalc : "");
	var recycleCalc = json.shift();
	$("#recycleCalc").text($("#recycleCheck").is(":checked") ? recycleCalc : "");
	
	// Parse
	$.each(json, function(i, source) {
		var seriesPrices = [];
		$.each(source.prices, function(j, priceTime) {
			seriesPrices.push([Date.parse(priceTime.createDate), priceTime.price]);
		});
		
		var isVisible = true;
		var color = "white";
		switch(source.name) {
		case "Market":
			color = "green";
			if(marketHidden) {
				isVisible = false;
			}
			break;
		case "Vendor":
			color = "DarkKhaki";
			if(vendorHidden) {
				isVisible = false;
			}
			break;
		case "Factory Cost":
			color = "crimson";
			if(resourceHidden) {
				isVisible = false;
			}
			break;
		case "Recycle Yield":
			color = "lightgray";
			if(recycleHidden) {
				isVisible = false;
			}
			break;
		}
		
		chart.addSeries({
			name: source.name,
			color: color,
			visible: isVisible,
			data: seriesPrices
		}, false);
	});	
	
	chart.redraw();
}


function buildPricesAsTable(data) {
	// 		<table id="results">
	// 			<tr>
	// 				<th>Vendor Price |</th>
	// 				<th>Market Price |</th>
	// 				<th>Time</th>
	// 			</tr>
	// 		</table>
	$.each(data, function(i, item) {
		var $tr = $('<tr>').append($('<td>').text(item.vendorDollars),
				$('<td>').text(item.marketDollars),
				$('<td>').text(item.createDate)).appendTo('#results');
	});
}