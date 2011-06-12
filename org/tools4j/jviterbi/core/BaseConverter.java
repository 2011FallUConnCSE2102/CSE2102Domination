package org.tools4j.jviterbi.core;

public class BaseConverter {

	public static String decimalToBinary(int iNumber, int iDigits) {
		String sResult = decimalToBinary(iNumber);
		
		while(sResult.length() < iDigits) {
			sResult = "0" + sResult;
		}
		
		return sResult;
	}
	
	public static String decimalToBinary(int iNumber) {
		String sResult = new String();
		
		while(iNumber != 0) {
			int iDigit = iNumber % 2;
			iNumber /= 2;
			
			sResult = iDigit + sResult;
		}
		
		return sResult;
	}
}
