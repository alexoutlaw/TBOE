package edu.tbo.web;

public class HtmlHelper {
	public static String FormatCurrency(String input) {
		if(input == null || input.length() == 0 || input.equals("0")) {
			return "-";
		}
		
		boolean isNegative = false;
		if(input.charAt(0) == '-') {
			input = input.substring(1);
			isNegative = true;
		}
		
		input = input.trim();
		
		String currencyStr = input;
		switch(input.length()) {
		case 0:
		case 1:
		case 2:
		case 3:
		case 4:
			currencyStr = input;
			break;
		case 5:
			currencyStr = input.substring(0, 2) + "." + input.charAt(2) + "K";
			break;
		case 6:
			currencyStr = input.substring(0, 3) + "K";
			break;
		case 7:
			currencyStr = input.charAt(0) + "." + input.substring(1, 3) + "M";
			break;
		case 8:
			currencyStr = input.substring(0, 2) + "." + input.charAt(2) + "M";
			break;
		case 9:
			currencyStr = input.substring(0, 3) + "M";
			break;
		case 10:
			currencyStr = input.charAt(0) + "." + input.substring(1, 3) + "B";
		case 11:
			currencyStr = input.substring(0, 2) + "." + input.charAt(2) + "B";
			break;
		case 12:
			currencyStr = input.substring(0, 3) + "B";
			break;
		case 13:
			currencyStr = input.charAt(0) + "." + input.substring(1, 3) + "T";
		case 14:
			currencyStr = input.substring(0, 2) + "." + input.charAt(2) + "T";
			break;
		case 15:
			currencyStr = input.substring(0, 3) + "T";
			break;
		default:
			currencyStr = input.substring(0, 1) + "E" + input.length();
		}
				
		return isNegative
				? "<a class='negative'>-$" + currencyStr + "</a>"
				: "$" + currencyStr;
	}
	
	public static String FormatPercent(String top, String bottom) {
		if(top == null || top.length() == 0 || top.equals("0")) {
			return "-";
		}
		if(bottom == null || bottom.length() == 0 || bottom.equals("0")) {
			return "-";
		}
		
		Float percent = Float.parseFloat(top) / Float.parseFloat(bottom) * 100;
		return percent > 0.0f
				? "<a class='positive'>+" + String.format("%.0f", percent) + "%</a>"
				: "<a class='negative'>" + String.format("%.0f", percent) + "%</a>";
	}
	
	public static String FormatPayoffTime(Integer hours) {
		if(hours <= 0) {
			return "<a class='negative'>Never</a>";
		}
		else if(hours > 100) {
			return "<a class='days'>" + hours / 24 + "</a>D <a class='hours'>" + hours % 24 + "</a>H";
		}
		else {
			return "<a class='hours'>" + hours + "</a>H";
		}
	}
}