package ymlai87416.dataservice.fetcher.constant;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Tom on 14/10/2016.
 */
public class HKStockOption {

    public static Map<String, Integer> lotSize;

    static final SimpleDateFormat sdf;

    private static String[] newMonths = { "JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT",
            "NOV", "DEC" };

    static{
        lotSize = new TreeMap<>();
        lotSize.put("CKP",1000);
        lotSize.put("XAB",10000);
        lotSize.put("AIA",1000);
        lotSize.put("NCL",1000);
        lotSize.put("PIN",5000);
        lotSize.put("CDA",5000);
        lotSize.put("CGN",10000);
        lotSize.put("MSB",2500);
        lotSize.put("CPI",1000);
        lotSize.put("CSA",5000);
        lotSize.put("A50",5000);
        lotSize.put("CS3",1000);
        lotSize.put("HCF",1000);
        lotSize.put("AMC",2000);
        lotSize.put("PLE",5000);
        lotSize.put("CTS",1000);
        lotSize.put("HAI",2000);
        lotSize.put("CKH",500);
        lotSize.put("CLP",500);
        lotSize.put("HKG",1000);
        lotSize.put("WHL",1000);
        lotSize.put("HKB",400);
        lotSize.put("HEH",500);
        lotSize.put("HSB",100);
        lotSize.put("HLD",1000);
        lotSize.put("SHK",1000);
        lotSize.put("NWD",1000);
        lotSize.put("SWA",500);
        lotSize.put("BEA",200);
        lotSize.put("GLX",1000);
        lotSize.put("MTR",500);
        lotSize.put("KLE",2000);
        lotSize.put("WWC",1000);
        lotSize.put("CIT",1000);
        lotSize.put("CPA",1000);
        lotSize.put("ESP",100);
        lotSize.put("JXC",1000);
        lotSize.put("CPC",2000);
        lotSize.put("HEX",100);
        lotSize.put("CRG",1000);
        lotSize.put("DFM",2000);
        lotSize.put("LIF",2000);
        lotSize.put("COL",2000);
        lotSize.put("TCH",100);
        lotSize.put("CTC",2000);
        lotSize.put("CHU",2000);
        lotSize.put("PEC",2000);
        lotSize.put("CNC",1000);
        lotSize.put("HNP",2000);
        lotSize.put("ACC",500);
        lotSize.put("XCC",1000);
        lotSize.put("CHT",500);
        lotSize.put("LEN",2000);
        lotSize.put("CTB",1000);
        lotSize.put("HGN",500);
        lotSize.put("CSE",500);
        lotSize.put("CRL",2000);
        lotSize.put("YZC",2000);
        lotSize.put("CRC",500);
        lotSize.put("BYD",500);
        lotSize.put("XIC",1000);
        lotSize.put("CCC",1000);
        lotSize.put("BIH",1000);
        lotSize.put("CCE",1000);
        lotSize.put("CCS",500);
        lotSize.put("SAN",400);
        lotSize.put("FIH",1000);
        lotSize.put("MGM",400);
        lotSize.put("PAI",500);
        lotSize.put("MEN",1000);
        lotSize.put("PIC",2000);
        lotSize.put("GWM",500);
        lotSize.put("BOC",500);
        lotSize.put("ALC",2000);
        lotSize.put("CLI",1000);
        lotSize.put("RFP",400);
        lotSize.put("TRF",500);
        lotSize.put("STC",50);
        lotSize.put("ZJM",2000);
        lotSize.put("NBM",2000);
        lotSize.put("BCM",1000);
        lotSize.put("KSO",1000);
        lotSize.put("CMB",500);
        lotSize.put("XBC",1000);

        DateFormatSymbols symbols = new DateFormatSymbols();
        symbols.setMonths(newMonths);
        sdf = new SimpleDateFormat("MMMyy", symbols);
    }

    public static String createStockOptionSymbolTicker(String asset, String optionType, Double strikePrice, java.util.Date expiryDate){
        String dateStr = newMonths[expiryDate.getMonth()] + String.valueOf(expiryDate.getYear()%100);
        String result = String.format("%s%s%04.2f%s", asset , optionType, strikePrice, dateStr); //example X50C0010.00AUG16
        return result;
    }

    public static String createStockOptionSymbolName(String fullName, String optionType, Double strikePrice, java.util.Date expiryDate){
        String dateStr = newMonths[expiryDate.getMonth()] + String.valueOf(expiryDate.getYear()%100);
        String fullOptionType;
        if(optionType.toUpperCase().compareTo("C") == 0)
            fullOptionType = "Call";
        else if(optionType.toUpperCase().compareTo("P") == 0)
            fullOptionType = "Put";
        else
            return null;

        String result = String.format("%s %s %.2f %s", fullName , fullOptionType, strikePrice, dateStr); //example X50 Call 10.00 AUG16
        return result;
    }
}
