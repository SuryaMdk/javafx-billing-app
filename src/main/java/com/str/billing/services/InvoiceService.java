package com.str.billing.services;

import com.str.billing.dao.InvoiceDAO;

public class InvoiceService {

	public boolean isValidInvoiceId(int id) {
		return InvoiceDAO.invoiceIdExist(id);
	}

	public boolean isValidHsn(int hsn) {
		return String.valueOf(hsn).matches("^\\d{4,8}$");
	}
	
	public static String convertToWords(long num) {
        if (num == 0) return "Zero Rupees Only";

        StringBuilder result = new StringBuilder();
        
        //0 TO 99
        if (num > 0 && num < 100) {
            result.append(convertBelowThousand((int) num)).append(" ");
            return result.toString().trim() + " Rupees Only";
        }
        
        // Crores
        if (num >= 10000000) {
            result.append(convertBelowThousand((int) (num / 10000000))).append(" Crore ");
            num %= 10000000;
        }

        // Lakhs
        if (num >= 100000) {
            result.append(convertBelowThousand((int) (num / 100000))).append(" Lakh ");
            num %= 100000;
        }

        // Thousands
        if (num >= 1000) {
            result.append(convertBelowThousand((int) (num / 1000))).append(" Thousand ");
            num %= 1000;
        }

        // Hundreds
        if (num >= 100) {
            result.append(convertBelowThousand((int) (num / 100))).append(" Hundred ");
            num %= 100;
        }

        // Tens and Units
        if (num > 0) {
            result.append("and ").append(convertBelowThousand((int) num)).append(" ");
        }

        return result.toString().trim() + " Rupees Only";
    }

    // Helper method to convert numbers below 1000
    public static String convertBelowThousand(int n) {
        String[] units = {"", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten", "Eleven",
                "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen", "Seventeen", "Eighteen", "Nineteen"};
        String[] tens = {"", "", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety"};

        if (n == 0) return "";
        if (n < 20) return units[n];
        if (n < 100) return tens[n / 10] + (n % 10 != 0 ? " " + units[n % 10] : "");
        return units[n / 100] + " Hundred" + (n % 100 != 0 ? " " + convertBelowThousand(n % 100) : "");
    }
}
