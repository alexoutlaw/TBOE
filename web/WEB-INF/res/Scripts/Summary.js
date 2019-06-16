$(window).load(function() {
	GetLatestPriceDate();
	window.setInterval(GetLatestPriceDate, 20000);
});

function GetLatestPriceDate() {
	$.ajax({
		url : window.globalRoutes.getLatestPriceUrl,
		type : "GET",
		dataType : "html",
		success : function(data) {
			if(data) {
				var latestPriceDate = new Date(data.trim());

				if(window.priceDate) {
					if(latestPriceDate.getTime() > window.priceDate.getTime()) {
						window.location.reload(); // get new prices
					}
				}
				else {
					// Set first price
					window.priceDate = latestPriceDate;
					window.setInterval(SetPriceUpdateText, 500);
				}
			}
		},
		error : function(data) {
			DisplayServerMessages(["ERROR: " + data.responseText]);
		}
	});
}

function SetPriceUpdateText() {
	$(".priceUpdateText").each(function() {
		$(this).html(CreatePriceDateString(window.priceDate));
	});
}

function CreatePriceDateString(date) {
	var now = new Date();
	var secondsDiff = Math.round((now.getTime() - date.getTime()) / 1000);

	return (secondsDiff > 60) 
		? "Prices Updated " + Math.floor(secondsDiff / 60) + "m " + secondsDiff % 60 + "s ago"
		: "Prices Updated " + secondsDiff + "s ago";
}