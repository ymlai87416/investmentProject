package model.Database

import java.text.SimpleDateFormat

import anorm.{ResultSetParser, RowParser, ~}
import org.joda.time.DateTime
import org.joda.time.format.{DateTimeFormat, DateTimeFormatter}

import scala.collection.immutable.HashMap
import anorm.SqlParser._
import play.api.Play.current
import play.api.db.DB
import anorm.SQL
import anorm.SqlQuery

/**
  * Created by Tom on 2/3/2017.
  */

class StockOption(id: Long, ticker: String, name: String){

  val optionType:Character = {
    ticker.charAt(3)
  }

  val strikePrice:Float = {
    ticker.substring(4, ticker.length()-5) toFloat
  }

  val expiryDate:DateTime = {
    DateTime.parse(ticker.substring(ticker.length()-5), StockOption.dt)
  }
}

class StockOptionHistory(id:Long, stockOptionId: Long, priceDate: DateTime,
                         openPrice: Float, dailyHigh: Float, dailyLow: Float,
                         settlePrice: Float, openInterest: Float, iv: Float)
{

}

object StockOption {
  val dt:DateTimeFormatter = DateTimeFormat.forPattern("MMMyy")

  val stockAbbrList = HashMap[Int, String](
    1113 -> "CKP",
    1288 -> "XAB",
    1299 -> "AIA",
    1336 -> "NCL",
    1339 -> "PIN",
    1359 -> "CDA",
    1816 -> "CGN",
    1988 -> "MSB",
    2601 -> "CPI",
    2822 -> "CSA",
    2823 -> "A50",
    2827 -> "CS3",
    2828 -> "HCF",
    3188 -> "AMC",
    3800 -> "PLE",
    6030 -> "CTS",
    6837 -> "HAI",
    1 -> "CKH",
    2 -> "CLP",
    3 -> "HKG",
    4 -> "WHL",
    5 -> "HKB",
    6 -> "HEH",
    11 -> "HSB",
    12 -> "HLD",
    16 -> "SHK",
    17 -> "NWD",
    19 -> "SWA",
    23 -> "BEA",
    27 -> "GLX",
    66 -> "MTR",
    135 -> "KLE",
    151 -> "WWC",
    267 -> "CIT",
    293 -> "CPA",
    330 -> "ESP",
    358 -> "JXC",
    386 -> "CPC",
    388 -> "HEX",
    390 -> "CRG",
    489 -> "DFM",
    494 -> "LIF",
    688 -> "COL",
    700 -> "TCH",
    728 -> "CTC",
    762 -> "CHU",
    857 -> "PEC",
    883 -> "CNC",
    902 -> "HNP",
    914 -> "ACC",
    939 -> "XCC",
    941 -> "CHT",
    992 -> "LEN",
    998 -> "CTB",
    1044 -> "HGN",
    1088 -> "CSE",
    1109 -> "CRL",
    1171 -> "YZC",
    1186 -> "CRC",
    1211 -> "BYD",
    1398 -> "XIC",
    1800 -> "CCC",
    1880 -> "BIH",
    1898 -> "CCE",
    1919 -> "CCS",
    1928 -> "SAN",
    2038 -> "FIH",
    2282 -> "MGM",
    2318 -> "PAI",
    2319 -> "MEN",
    2328 -> "PIC",
    2333 -> "GWM",
    2388 -> "BOC",
    2600 -> "ALC",
    2628 -> "CLI",
    2777 -> "RFP",
    2800 -> "TRF",
    2888 -> "STC",
    2899 -> "ZJM",
    3323 -> "NBM",
    3328 -> "BCM",
    3888 -> "KSO",
    3968 -> "CMB",
    3988 -> "XBC"
  )

  val stockFullList = HashMap[Int, String](
    1 -> "CK Hutchison Holdings Ltd.",
    2 -> "CLP Holdings Limited",
    3 -> "The Hong Kong and China Gas Company Limited",
    4 -> "The Wharf (Holdings) Limited",
    5 -> "HSBC Holdings Plc.",
    6 -> "Power Assets Holdings Limited",
    11 -> "Hang Seng Bank Limited",
    12 -> "Henderson Land Development Company Limited",
    16 -> "Sun Hung Kai Properties Limited",
    17 -> "New World Development Company Limited",
    19 -> "Swire Pacific Limited",
    23 -> "The Bank of East Asia, Limited",
    27 -> "Galaxy Entertainment Group Limited",
    66 -> "MTR Corporation Limited",
    135 -> "Kunlun Energy Co. Ltd.",
    151 -> "Want Want China Holdings Ltd.",
    267 -> "CITIC Limited",
    293 -> "Cathay Pacific Airways Limited",
    330 -> "Esprit Holdings Limited",
    358 -> "Jiangxi Copper Company Limited",
    386 -> "China Petroleum & Chemical Corporation",
    388 -> "Hong Kong Exchanges and Clearing Limited",
    390 -> "China Railway Group Limited",
    489 -> "Dongfeng Motor Group Co. Ltd.",
    494 -> "Li & Fung Limited",
    688 -> "China Overseas Land & Investment Limited",
    700 -> "Tencent Holdings Limited",
    728 -> "China Telecom Corporation Limited",
    762 -> "China Unicom (Hong Kong) Limited",
    857 -> "PetroChina Company Limited",
    883 -> "CNOOC Limited",
    902 -> "Huaneng Power International, Inc.",
    914 -> "Anhui Conch Cement Company Limited",
    939 -> "China Construction Bank Corporation",
    941 -> "China Mobile Limited",
    992 -> "Lenovo Group Limited",
    998 -> "China CITIC Bank Corporation Limited",
    1044 -> "Hengan International Group Co. Ltd.",
    1088 -> "China Shenhua Energy Company Limited",
    1109 -> "China Resources Land Ltd.",
    1171 -> "Yanzhou Coal Mining Company Limited",
    1186 -> "China Railway Construction Corporation Limited",
    1211 -> "BYD Company Limited",
    1398 -> "Industrial and Commercial Bank of China Limited",
    1800 -> "China Communications Construction Company Limited",
    1880 -> "Belle International Holdings Limited",
    1898 -> "China Coal Energy Company Limited",
    1919 -> "China COSCO Holdings Company Limited",
    1928 -> "Sands China Ltd.",
    2038 -> "FIH Mobile Limited",
    2282 -> "MGM China Holdings Limited",
    2318 -> "Ping An Insurance (Group) Company of China, Ltd.",
    2319 -> "China Mengniu Dairy Co. Ltd.",
    2328 -> "PICC Property and Casualty Company Limited",
    2333 -> "Great Wall Motor Co. Limited",
    2388 -> "BOC Hong Kong (Holdings) Limited",
    2600 -> "Aluminum Corporation of China Limited",
    2628 -> "China Life Insurance Company Limited",
    2777 -> "Guangzhou R&F Properties Co., Ltd.",
    2800 -> "Tracker Fund of Hong Kong",
    2888 -> "Standard Chartered PLC",
    2899 -> "Zijin Mining Group Company Limited",
    3323 -> "China National Building Material Company Limited",
    3328 -> "Bank of Communications Co., Ltd.",
    3888 -> "Kingsoft Corporation Ltd.",
    3968 -> "China Merchants Bank Co., Ltd.",
    3988 -> "Bank of China Limited",
    1113 -> "Cheung Kong Property Holdings Ltd.",
    1288 -> "Agricultural Bank of China Limited ",
    1299 -> "AIA Group Limited",
    1336 -> "New China Life Insurance Co. Ltd.",
    1339 -> "The People's Insurance Company (Group) of China Limited",
    1359 -> "China Cinda Asset Management Co., Ltd",
    1816 -> "CGN Power Co., Ltd",
    1988 -> "China Minsheng Banking Corporation Limited",
    2601 -> "China Pacific Insurance (Group) Co., Ltd.",
    2822 -> "CSOP FTSE China A50 ETF ",
    2823 -> "iShares FTSE A50 China Index ETF#",
    2827 -> "W.I.S.E. - CSI 300 China Tracker #",
    2828 -> "Hang Seng H-Share Index ETF",
    3188 -> "ChinaAMC CSI 300 Index ETF ",
    3800 -> "GCL-Poly Energy Holdings Ltd.",
    6030 -> "CITIC Securities Co. Ltd.",
    6837 -> "Haitong Securities Co., Ltd."
  )

  val sqlDateTimeFormat = new SimpleDateFormat("yyyy-MM-dd")

  val stockOptionParser: RowParser[StockOption] = {
    long("id") ~ str("ticker") ~ str("name") map{
      case id ~ ticker ~ name =>
        new StockOption(id, ticker, name)
    }
  }

  val stockOptionHistoryParser: RowParser[(StockOption, StockOptionHistory)] = {
    stockOptionParser ~ StockOptionHistory.stockOptionHistoryParser map (flatten)
  }

  def getAllStockOption: List[StockOption] = DB.withConnection{
    implicit connection =>
      val sql: SqlQuery = SQL("select id, ticker, name from securities_master.symbol where instrument = 'HK Stock Option'")
      sql.as(stockOptionParser *)
  }

  def findBySEHKCode(sehkCode: Long): List[StockOption] = {
    DB.withConnection{
      implicit connection =>
        val abbrev = stockAbbrList(sehkCode)
        val sql = SQL("select id, ticker, name from securities_master.symbol where instrument = 'HK Stock Option' and ticker like '" + abbrev + "%'")

        sql.as(stockOptionParser *)
    }
  }

  def findBySEHKCodeWithHistory(sehkCode: Long, date: DateTime): Map[StockOption, List[StockOptionHistory]] = {
    DB.withConnection{
      implicit connection =>
        val abbrev = stockAbbrList(sehkCode)
        val sql = SQL(
          "select id, ticker, name from securities_master.symbol a inner join securities_master.daily_price b on (a.id = b.symbol_id) where instrument = 'HK Stock Option' and ticker like '"
        + abbrev + "%'" + "b.price_date=" + sqlDateTimeFormat.format(date.toDate()))

        val results: List[(StockOption, StockOptionHistory)] = sql.as(stockOptionHistoryParser *)

        results.groupBy(_._1).mapValues {_.map {_._2} }
    }
  }

  //TODO: get the last 3 year mean, and std, high, low

}

object StockOptionHistory{
  val stockOptionHistoryParser: RowParser[StockOptionHistory] = {

    long("id") ~ long("symbol_id") ~ date("price_date") ~ float("open_price") ~ float("high_price") ~ float("low_price") ~ float("close_price") ~ long("open_interest") ~ float("iv") map {
      case id ~ symbolId ~ priceDate ~ openPrice ~ dailyHigh ~ dailyLow ~ settlePrice ~ openInterest ~ iv =>{
        val priceDateJoda = new DateTime(priceDate.getTime())
        new StockOptionHistory(id, symbolId, priceDateJoda, openPrice, dailyHigh, dailyLow, settlePrice, openInterest, iv)
      }
    }
  }

  val stockOptionHistoriesParser: ResultSetParser[List[StockOptionHistory]] = {
    stockOptionHistoryParser *
  }

}
