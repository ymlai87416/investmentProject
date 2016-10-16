package ymlai87416.dataservice.reader;

import com.opencsv.CSVReader;
import org.springframework.data.util.Pair;
import ymlai87416.dataservice.domain.DailyPrice;
import ymlai87416.dataservice.domain.DataVendor;
import ymlai87416.dataservice.domain.Exchange;
import ymlai87416.dataservice.domain.Symbol;
import ymlai87416.dataservice.fetcher.constant.Currencies;
import ymlai87416.dataservice.fetcher.constant.DataVendors;
import ymlai87416.dataservice.fetcher.constant.Exchanges;
import ymlai87416.dataservice.fetcher.constant.HKStockOption;
import ymlai87416.dataservice.service.DataVendorService;
import ymlai87416.dataservice.service.ExchangeService;
import ymlai87416.dataservice.utilities.Utilities;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by Tom on 16/10/2016.
 */
public class HKExStockOptionReportCSVReader {

    private static String[] newMonths = { "JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT",
            "NOV", "DEC" };

    private static final SimpleDateFormat expiryDateFormat;
    private Exchange exchange;
    private DataVendor dataVendor;

    static{
        DateFormatSymbols symbols = new DateFormatSymbols();
        symbols.setMonths(newMonths);
        expiryDateFormat = new SimpleDateFormat("MMMyy", symbols);
    }

    public HKExStockOptionReportCSVReader(Exchange exchange, DataVendor dataVendor){
        this.exchange = exchange;
        this.dataVendor = dataVendor;
    }

    public List<Pair<Symbol, DailyPrice>> readStockOptionReportCSV(String csvFilePath, java.util.Date priceDate) throws IOException{
        File csvFile = new File(csvFilePath);

        List<Pair<Symbol, DailyPrice>> result = new ArrayList<>();

        CSVReader reader = new CSVReader(new FileReader(csvFile));
        String[] line;
        while ((line = reader.readNext()) != null) {
            if (line[0] != null && line[0].startsWith("CLASS")) {
                break;
            }
        }

        while(line != null){

            String asset, fullName;
            try {
                Scanner input = new Scanner(line[0]);
                input.next();
                asset = input.next();
                input.next();
                fullName = input.nextLine();
            }
            catch(Exception ex){
                line = reader.readNext();
                continue;
            }

            while(true) {
                line = reader.readNext();
                if (line == null) break;

                if (line[0] != null && line[0].startsWith("CLASS")) {
                    break;
                }

                try {
                    java.util.Date expiryDate = expiryDateFormat.parse(line[0]);
                    Double strikePrice = Double.parseDouble(line[1]);
                    String optionType = line[2];

                    String ticker = HKStockOption.createStockOptionSymbolTicker(asset, optionType, strikePrice, expiryDate);
                    String name = HKStockOption.createStockOptionSymbolName(fullName, optionType, strikePrice, expiryDate);

                    Double opening = Double.parseDouble(line[3]);
                    Double dailyHigh = Double.parseDouble(line[4]);
                    Double dailyLow = Double.parseDouble(line[5]);
                    Double settlement = Double.parseDouble(line[6]);
                    Double changeInSettlement = Double.parseDouble(line[7]);
                    Double iv = Double.parseDouble(line[8]);
                    Long volume = Long.parseLong(line[9]);
                    Long openInterest = Long.parseLong(line[10]);
                    Long changeInOI = Long.parseLong(line[11]);

                    Symbol symbol = new Symbol();
                    symbol.setExchange(exchange);
                    symbol.setTicker(ticker);
                    symbol.setInstrument("HK Stock Option");
                    symbol.setName(name);
                    symbol.setSector("HK Stock Option");
                    symbol.setLot(HKStockOption.lotSize.get(asset.toUpperCase().trim()));
                    symbol.setCurrency(Currencies.HKD);
                    symbol.setCreatedDate(Utilities.getCurrentSQLDate());
                    symbol.setLastUpdatedDate(Utilities.getCurrentSQLDate());

                    DailyPrice dailyPrice = new DailyPrice();
                    dailyPrice.setDataVendor(dataVendor);
                    dailyPrice.setSymbol(symbol);
                    dailyPrice.setPriceDate(Utilities.convertUtilDateToSqlDate(priceDate));
                    dailyPrice.setCreatedDate(Utilities.getCurrentSQLDateTime());
                    dailyPrice.setLastUpdatedDate(Utilities.getCurrentSQLDateTime());
                    dailyPrice.setOpenPrice(opening);
                    dailyPrice.setHighPrice(dailyHigh);
                    dailyPrice.setLowPrice(dailyLow);
                    dailyPrice.setClosePrice(settlement);
                    dailyPrice.setAdjClosePrice(settlement);
                    dailyPrice.setVolume(volume);
                    dailyPrice.setIv(iv);
                    dailyPrice.setOpenInterest(openInterest);

                    result.add(Pair.of(symbol, dailyPrice));
                } catch (Exception ex) {
                    //ex.printStackTrace();
                }
            }
        }

        return result;
    }
}
