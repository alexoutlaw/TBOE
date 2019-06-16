function GetUserFactories(table) {	
	//TODO: freeze table
	
	$.ajax(window.baseSiteUrl + '/Resources/Stats/UserFactories')
	.done(function(data, status, xhr) {
		table.data('factories', data.factories);
		table.data('totals', data.totals);
		
		var savedTableSort = Cookies.get('factorySortColumn');
		if(savedTableSort != null) {
			SortFactoryTable(table, savedTableSort);
		}
		else {
			BuildFactoryTable(table);
		}
	})
	.fail(function(xhr, status, errorStr) {
		DisplayServerMessages(["Failed to get user factories. Error(" + xhr.status + ") " + errorStr]);
	})
	.always(function(data, status, xhr) {
		// TODO: unfreeze table
	});
}

function SetUserFactory(e) {
	var formData = $(this).serializeArray();
	var table = $(this).closest('table');
	
	//TODO: freeze table
	
	$.ajax({
		url: window.baseSiteUrl + '/Resources/Stats/UserFactories',
		type: 'POST',
		data: formData
	})
	.done(function(data, status, xhr) {
		if(data.success) {
			UpdateFactoryTable(table, data.factory);
		}
		
		if(data.message && data.message.length > 0)
			DisplayServerMessages([data.message]);
	})
	.fail(function(xhr, status, errorStr) {
		DisplayServerMessages(["Failed to set user factory. Error(" + xhr.status + ") " + errorStr]);
	})
	.always(function(data, status, xhr) {
		// TODO: unfreeze table
	});
	
	e.preventDefault();
	return false;
}

function BuildFactoryTable(table) {
	table.html(''); //clear
	var factories = $(table).data('factories');
	var totals = $(table).data('totals');
	
	var headers = $('<tr>').append(
			$('<th>').html("<a onclick='SortFactoryTableHeaders(this, \"name\")'>Name</a>"),
			$('<th>').html("<a onclick='SortFactoryTableHeaders(this, \"level\")'>Level</a>"),
			$('<th>').html("<a onclick='SortFactoryTableHeaders(this, \"hourlyProfit\")'>Hourly Profit</a>"),
			$('<th>').html("<a onclick='SortFactoryTableHeaders(this, \"hourlyProfit\")'>5-Day Boost</a>"),
			$('<th>').html("<a onclick='SortFactoryTableHeaders(this, \"upgradeCost\")'>Upgrade Cost</a>"),
			$('<th>').html("<a onclick='SortFactoryTableHeaders(this, \"upgradePayoff\")'>Upgrade Payoff Time</a>"))
		.addClass("sortRow")
		.appendTo(table);
	
	$.each(factories, function(i, factory) {
		var thumbnail = $('<img>').attr('src', window.baseSiteUrl + "/Images/" + factory.displayName + ".png").addClass('thumbnail');
		
		var inputForm = $('<form>').attr('method', 'POST').attr('action', window.baseSiteUrl + "/Resources/Stats/UserFactories")
			.addClass('setFactoryLevelForm')
			.append($('<input>').attr('type', 'hidden').attr('name', 'item').attr('value', factory.product.machineName),
					$('<input>').attr('type', 'number').attr('name', 'level').attr('maxlength', 4).attr("min", "0"));
				
		var buttonForm = $('<form>').attr('method', 'POST').attr('action', window.baseSiteUrl + "/Resources/Stats/UserFactories")
			.addClass('setFactoryLevelForm')
			.append($('<input>').attr('type', 'hidden').attr('name', 'item').attr('value', factory.product.machineName),
					$('<input>').attr('type', 'hidden')	.attr('name', 'level').attr('value', factory.userLevel + 1),
					$('<button>').attr('type', 'submit').append('+1').addClass('plusButton'));
		
		var totalProduction = factory.baseProduction * factory.userLevel;
		var hourlyProfitTooltip = totalProduction + " " + factory.product.displayName + "/H<br/>"
			+ "@ " + FormatNumber(factory.product.marketPrice, true, false) + " each";
		var hourlyProfit = $('<div>').attr('title', hourlyProfitTooltip)
				.append(FormatCurrency(factory.hourlyProfit));
		
		var fiveDayProfit = FormatCurrency(factory.hourlyProfit * 24 * 5);
		
		var upgradeProfit = (factory.product.marketPrice - factory.product.factoryCostPrice) * factory.baseProduction;
		var upgradePayoffTooltip = factory.baseProduction + " " + factory.product.displayName + "/H<br/>"
			+ "Profit: " + FormatNumber(upgradeProfit, true, false) + "/H";
		var upgradePayoff = $('<div>').attr('title', upgradePayoffTooltip)
				.append(FormatHours(factory.upgradePayoff));
		
		$('<tr>').append(
				$('<td>').append(thumbnail, factory.displayName),
				$('<td>').append(factory.userLevel, buttonForm, inputForm),
				$('<td>').html(hourlyProfit),
				$('<td>').html(fiveDayProfit),
				$('<td>').html(FormatNumber(factory.upgradeCost, true, false)),
				$('<td>').html(upgradePayoff))
			.appendTo(table);
	});
	
	var fiveDayProfitTotal = totals.hourlyProfitTotal * 24 * 5;
	
	var footers = $('<tr>').append(
			$('<th>').html("Totals: "),
			$('<th>').html(totals.levelTotal),
			$('<th>').html(FormatCurrency(totals.hourlyProfitTotal)),
			$('<th>').html(FormatCurrency(fiveDayProfitTotal)),
			$('<th>').html(FormatCurrency(totals.upgradeCostTotal)),
			$('<th>').html(FormatHours(totals.upgradePayoffTotal)))
		.addClass('footerRow')
		.appendTo(table);
	
	$('.setFactoryLevelForm').on('submit', SetUserFactory);
}

function SortFactoryTableHeaders(header, column) {
	SortFactoryTable($(header).closest('table'), column);
}

function SortFactoryTable(table, column) {
	var currentSortColumn = $(table).data('factorySortColumn');
	var currentSortDirection = $(table).data('factorySortDirection');
	
	var sortDirection = currentSortDirection;
	if(currentSortDirection == null) {	// first load
		sortDirection = Cookies.get('factorySortDirection');
	}
	if(column == currentSortColumn) {
		// Flip sort direction if sorting on the same column
		sortDirection = (sortDirection == "DESC")
			? "ASC"
			: "DESC";	// default descending
	}
	
	var factories = $(table).data('factories');
	switch(column) {
		case "level":
			factories.sort(function(a, b) {
				return a.userLevel - b.userLevel;
			});
			break;
		case "hourlyProfit":
			factories.sort(function(a, b) {
				return a.hourlyProfit - b.hourlyProfit;
			});
			break;
		case "upgradeCost":
			factories.sort(function(a, b) {
				return a.upgradeCost - b.upgradeCost;
			});
			break;
		case "upgradePayoff":
			factories.sort(function(a, b) {
				return a.upgradePayoff - b.upgradePayoff;
			});
			break;
		case "name":
		default:
			factories.sort(function(a, b) {
				if(a.displayName < b.displayName) return -1;
			    if(a.displayName > b.displayName) return 1;
			    return 0;
			});
		break;
	}
	
	if(sortDirection == "DESC") {
		factories.reverse();
	}
	
	//console.log("FACTORY SORT: " + column + " / " + sortDirection);
	
	Cookies.set('factorySortColumn', column);
	Cookies.set('factorySortDirection', sortDirection);
	
	$(table).data('factorySortColumn', column);
	$(table).data('factorySortDirection', sortDirection);
	$(table).data('factories', factories);
	BuildFactoryTable(table);
}

function UpdateFactoryTable(table, updatedFactory) {
	var factories = $(table).data('factories');
	for(var i = 0; i < factories.length; i++) {
		if(factories[i].product.machineName == updatedFactory.product.machineName) {
			factories[i] = updatedFactory;
			break;
		}
	}
	
	UpdateFactoryTableTotals(table);
	BuildFactoryTable(table);
}

function UpdateFactoryTableTotals(table) {
	var factories = $(table).data('factories');	
	var totals = {
		levelTotal: 0,
		hourlyProfitTotal: 0,
		upgradeCostTotal: 0,
		upgradePayoffTotal: 0
	}
	
	for(var i = 0; i < factories.length; i++) {
		totals.levelTotal += parseInt(factories[i].userLevel);
		totals.hourlyProfitTotal += parseInt(factories[i].hourlyProfit);
		totals.upgradeCostTotal += parseInt(factories[i].upgradeCost);
		totals.upgradePayoffTotal += parseInt(factories[i].upgradePayoff);
	}
	
	$(table).data('totals', totals);
}
	