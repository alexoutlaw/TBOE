$(window).load(function() {
	// Allow tooltips
	$(document).tooltip({
		content: function () {
            return $(this).prop('title');
        },
        track: true
	});
});

function toggleMobileMenu() {
	var target = $("#siteMap");
	var shown = target.css('display') != "none";
	if(shown) {
		target.removeClass('mobileMenu');
	}
	else {
		target.addClass('mobileMenu');
	}
}
function toggleSideMenu() {
	var target = $("#sidemenu");
	var shown = target.css('display') != "none";
	if(shown) {
		target.hide();
	}
	else {
		target.show();
	}
}

function DisplayServerMessages(messages) {
	var dialog = $("#serverDialog");
	dialog.html(messages.join($("<br>")));
	dialog.dialog('open');
}

function FormatCurrency(number) {
	return FormatNumber(number, true, true);
}
function FormatNumber(number, currency, colorCoding) {
	var numberStr = number.toString();
	var len = numberStr.length;
	
	switch (len) {
		case 5:
			numberStr = (numberStr / 1000).toFixed(1) + "<span class='abbreviation'>K</span>";
			break;
		case 6:
			numberStr = (numberStr / 1000).toFixed(0) + "<span class='abbreviation'>K</span>";
			break;
		case 7:
			numberStr = (numberStr /1000000).toFixed(2) + "<span class='abbreviation'>M</span>";
			break;
		case 8:
			numberStr = (numberStr /1000000).toFixed(1) + "<span class='abbreviation'>M</span>";
			break;
		case 9:
			numberStr = (numberStr /1000000).toFixed(0) + "<span class='abbreviation'>M</span>";
			break;
		case 10:
			numberStr = (numberStr / 1000000000).toFixed(2) + "<span class='abbreviation'>B</span>";
			break;
		case 11:
			numberStr = (numberStr /1000000000).toFixed(1) + "<span class='abbreviation'>B</span>";
			break;
		case 12:
			numberStr = (numberStr /1000000000).toFixed(0) + "<span class='abbreviation'>B</span>";
			break;
		case 13:
			numberStr = (numberStr / 1000000000000).toFixed(2) + "<span class='abbreviation'>T</span>";
			break;
		case 14:
			numberStr = (numberStr /1000000000000).toFixed(1) + "<span class='abbreviation'>T</span>";
			break;
		case 15:
			numberStr = (numberStr /1000000000000).toFixed(0) + "<span class='abbreviation'>T</span>";
			break;
	}
	
	if(currency) {
		numberStr = "$" + numberStr;
	}
	
	if(colorCoding) {
		numberStr = (number > 0)
			? "<span class='positive'>" + numberStr + "</span>"
			: "<span class='negative'>" + numberStr + "</span>";
	}

	return numberStr;
}
function FormatHours(hours) {
	if(hours <= 0) {
		return "<span class='negative'>Never</span>";
	}
	else if (hours > 100) {
		return "<span class='days'>" + Math.round(hours / 24) + "</span><span class='abbreviation'>D</span> <span class='hours'>" + pad(Math.round(hours % 24)) + "</span><span class='abbreviation'>H</span>";
	}
	else {
		return "<span class='hours'>" + Math.round(hours) + "</span><span class='abbreviation'>H<span>";
	}
}

function pad(n) {
    return (n < 10) ? ("0" + n) : n;
}