function GetUserMines(table) {	
	//TODO: freeze table
	
	$.ajax(window.baseSiteUrl + '/Resources/Stats/UserMines')
	.done(function(data, status, xhr) {
		table.data('mines', data.mines);
		table.data('totals', data.totals);
		
		var savedTableSort = Cookies.get('mineSortColumn');
		if(savedTableSort != null) {
			SortMineTable(table, savedTableSort);
		}
		else {
			BuildMineTable(table);
		}
	})
	.fail(function(xhr, status, errorStr) {
		DisplayServerMessages(["Failed to get user mines. Error(" + xhr.status + ") " + errorStr]);
	})
	.always(function(data, status, xhr) {
		// TODO: unfreeze table
	});
}

function SetUserMine(e) {
	var formData = $(this).serializeArray();
	var table = $(this).closest('table');
	
	//TODO: freeze table
	
	$.ajax({
		url: window.baseSiteUrl + '/Resources/Stats/UserMines',
		type: 'POST',
		data: formData
	})
	.done(function(data, status, xhr) {
		if(data.success) {
			// prevent reverse sort 
			$(table).data('mineSortColumn', null);
			
			GetUserMines(table);
		}
		
		if(data.message && data.message.length > 0)
			DisplayServerMessages([data.message]);
	})
	.fail(function(xhr, status, errorStr) {
		DisplayServerMessages(["Failed to set user mine. Error(" + xhr.status + ") " + errorStr]);
	})
	.always(function(data, status, xhr) {
		// TODO: unfreeze table
	});
	
	e.preventDefault();
	return false;
}

function BuildMineTable(table) {
	table.html(''); //clear
	var mines = $(table).data('mines');
	var totals = $(table).data('totals');
	
	var headers = $('<tr>').append(
			$('<th>').html("<a onclick='SortMineTableHeaders(this, \"name\")'>Name</a>"),
			$('<th>').html("<a onclick='SortMineTableHeaders(this, \"quantity\")'>Quantity</a>"),			
			$('<th>').html("<a onclick='SortMineTableHeaders(this, \"yield\")'>Actual Yield</a>"),
			$('<th>').html("<a onclick='SortMineTableHeaders(this, \"mineProfit\")'>Profit Per Mine/H</a>"),
			$('<th>').html("<a onclick='SortMineTableHeaders(this, \"hourlyProfit\")'>Total Profit/H</a>"),
			$('<th>').html("<a onclick='SortMineTableHeaders(this, \"buildCost\")'>Build Cost</a>"),
			$('<th>').html("<a onclick='SortMineTableHeaders(this, \"buildPayoff\")'>Build Payoff Time</a>"))
		.addClass("sortRow")
		.appendTo(table);
	
	$.each(mines, function(i, mine) {
		var thumbnail = $('<img>').attr('src', window.baseSiteUrl + "/Images/" + mine.resource.machineName + ".png").addClass('thumbnail');
		
		var mineNameTooltip = "Resource: " + mine.resource.displayName + "<br/>"
			+ "100% Quality: " + mine.maxProduction + "<br/>"
			+ "Required Level: " + mine.requiredLevel;
		var mineName = $('<div>').attr('title', mineNameTooltip)
			.append(thumbnail, mine.displayName);
		
		var quantityInputForm = $('<form>').attr('method', 'POST').attr('action', window.baseSiteUrl + "/Resources/Stats/UserMines")
			.addClass('setMineForm')
			.append($('<input>').attr('type', 'hidden').attr('name', 'item').attr('value', mine.resource.machineName),
					$('<input>').attr('type', 'number').attr('name', 'quantity').attr('maxlength', 4).attr("min", "0"));
				
		var quantityButtonForm = $('<form>').attr('method', 'POST').attr('action', window.baseSiteUrl + "/Resources/Stats/UserMines")
			.addClass('setMineForm')
			.append($('<input>').attr('type', 'hidden').attr('name', 'item').attr('value', mine.resource.machineName),
					$('<input>').attr('type', 'hidden')	.attr('name', 'quantity').attr('value', parseInt(mine.userQuantity) + 1),
					$('<button>').attr('type', 'submit').append('+1').addClass('plusButton'));
		
		var profitPerMineTooltip = "100% Quality: " + FormatNumber((mine.maxProduction * mine.resource.marketPrice), true, false) + "/H<br/>"
			+ "5xTech: " + FormatNumber((mine.maxProduction * mine.resource.marketPrice * 5), true, false) + "/H<br/>"
			+ "T+L9HQ: " + FormatNumber((mine.maxProduction * mine.resource.marketPrice * 5 * 9.1), true, false) + "/H";
		var profitPerMine = $('<div>').attr('title', profitPerMineTooltip)
			.append(FormatNumber((mine.maxProduction * mine.resource.marketPrice), true, false));
		
		var yieldDisplay = FormatNumber(mine.userHourlyYield, false, false);
		var yieldInputForm = $('<form>').attr('method', 'POST').attr('action', window.baseSiteUrl + "/Resources/Stats/UserMines")
			.addClass('setMineForm')
			.append($('<input>').attr('type', 'hidden').attr('name', 'item').attr('value', mine.resource.machineName),
					$('<input>').attr('type', 'number').attr('name', 'hourlyYield').attr('maxlength', 10).attr("min", "0").addClass('longInput'));
		var actualYield = $('<div>').attr('title', mine.userHourlyYield)
			.append(yieldDisplay, yieldInputForm);
		
		var hourlyProfitTooltip = yieldDisplay + " " + mine.resource.displayName + "/H<br/>"
				+ "@ $" + mine.resource.marketPrice + " each";
		var hourlyProfit = $('<div>').attr('title', hourlyProfitTooltip)
			.append(FormatNumber(mine.hourlyProfit, true, false));
				
		$('<tr>').append(
				$('<td>').html(mineName),
				$('<td>').append(mine.userQuantity, quantityButtonForm, quantityInputForm),				
				$('<td>').html(actualYield),
				$('<td>').html(profitPerMine),
				$('<td>').html(hourlyProfit),
				$('<td>').html(FormatNumber(mine.buildCost, true, false)),
				$('<td>').html(FormatHours(mine.buildPayoff)))
			.appendTo(table);
	});
	
	var footers = $('<tr>').append(
			$('<th>').html("Totals: "),
			$('<th>').html(totals.totalQuantity),			
			$('<th>').html(FormatNumber(totals.totalHourlyYield, false, false)),
			$('<th>').html("-"),
			$('<th>').html(FormatCurrency(totals.totalHourlyProfit)),
			$('<th>').html(FormatCurrency(totals.totalBuildCost)),
			$('<th>').html(FormatHours(totals.totalBuildPayoff)))
		.appendTo(table);
	
	$('.setMineForm').on('submit', SetUserMine);
}

function SortMineTableHeaders(header, column) {
	SortMineTable($(header).closest('table'), column);
}

function SortMineTable(table, column) {
	var currentSortColumn = $(table).data('mineSortColumn');
	var currentSortDirection = $(table).data('mineSortDirection');
	
	var sortDirection = currentSortDirection;
	if(currentSortDirection == null) {	// first load
		sortDirection = Cookies.get('mineSortDirection');
	}
	if(column == currentSortColumn) {
		// Flip sort direction if sorting on the same column
		sortDirection = (sortDirection == "DESC")
			? "ASC"
			: "DESC";	// default descending
	}
	
	var mines = $(table).data('mines');
	switch(column) {
		case "quantity":
			mines.sort(function(a, b) {
				return a.userQuantity - b.userQuantity;
			});
			break;
		case "mineProfit":
			mines.sort(function(a, b) {
				return (a.maxProduction * a.resource.marketPrice) - (b.maxProduction * b.resource.marketPrice);
			});
			break;
		case "yield":
			mines.sort(function(a, b) {
				return a.userHourlyYield - b.userHourlyYield;
			});
			break;
		case "hourlyProfit":
			mines.sort(function(a, b) {
				return a.hourlyProfit - b.hourlyProfit;
			});
			break;
		case "buildCost":
			mines.sort(function(a, b) {
				return a.buildCost - b.buildCost;
			});
			break;
		case "buildPayoff":
			mines.sort(function(a, b) {
				return a.buildPayoff - b.buildPayoff;
			});
			break;
		case "name":
		default:
			mines.sort(function(a, b) {
				if(a.displayName < b.displayName) return -1;
			    if(a.displayName > b.displayName) return 1;
			    return 0;
			});
		break;
	}
	
	if(sortDirection == "DESC") {
		mines.reverse();
	}
	
	//console.log("FACTORY SORT: " + column + " / " + sortDirection);
	
	Cookies.set('mineSortColumn', column);
	Cookies.set('mineSortDirection', sortDirection);
	
	$(table).data('mineSortColumn', column);
	$(table).data('mineSortDirection', sortDirection);
	$(table).data('mines', mines);
	BuildMineTable(table);
}