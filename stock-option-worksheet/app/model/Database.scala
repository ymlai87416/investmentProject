package model.Database

import java.text.SimpleDateFormat
import java.util.{Date, Locale}

import anorm.{ResultSetParser, RowParser, ~}
import org.joda.time.{DateTime, LocalDateTime}
import org.joda.time.format.{DateTimeFormat, DateTimeFormatter}

import scala.collection.immutable.HashMap
import anorm.SqlParser.{get, _}
import play.api.Play.current
import play.api.db.DB
import anorm.SQL
import anorm.SqlQuery

/**
  * Created by Tom on 2/3/2017.
  */

class Asset(val id: Long, val ticker: String, val name: String){

}

class Stock(id: Long, ticker: String, name: String) extends Asset(id, ticker, name){
  val sehkCode: Option[Long] = {
    try{
      Option(ticker.replace(".HK", "").toLong)
    }
    catch{
      case _: Exception => None
    }
  }
}

class StockOption(id: Long, ticker: String, name: String) extends Asset(id, ticker, name){

  val optionType:Character = {
    ticker.charAt(3)
  }

  val strikePrice:Float = {
    ticker.substring(4, ticker.length()-5) toFloat
  }

  val expiryDate:DateTime = {
    val input = ticker.substring(ticker.length()-5)
    DateTime.parse(input, StockOption.dt)
  }
}

class StockOptionHistory(val id:Long, val stockOptionId: Long, val priceDate: DateTime,
                         val openPrice: Float, val dailyHigh: Float, val dailyLow: Float,
                         val settlePrice: Float, val openInterest: Long, val iv: Float)
{
}

class StockHistory(val id:Long, val stockOptionId: Long, val priceDate: DateTime,
                   val openPrice: Float, val dailyHigh: Float, val dailyLow: Float,
                   val closePrice: Float, val adjClosePrice: Float, val volume: Long)
{
}

class IVSeries(val id:Long, val seriesName: String){
  val asset = seriesName.replace("  IV (%)", "").replace("  HV10 (%)", "").replace("  HV30 (%)", "").replace("  HV60 (%)", "").replace("  HV90 (%)", "")
  val seriesType = {
    if(seriesName.indexOf("  IV (%)")!= -1)
      "Implied volatility"
    else if(seriesName.indexOf("  HV10 (%)")!= -1)
      "History volatility 10 days"
    else if(seriesName.indexOf("  HV30 (%)")!= -1)
      "History volatility 30 days"
    else if(seriesName.indexOf("  HV60 (%)")!= -1)
      "History volatility 60 days"
    else if(seriesName.indexOf("  HV90 (%)")!= -1)
      "History volatility 90 days"
  }

  override def equals(o: Any) = o match{
    case that: IVSeries => that.id == this.id
    case _ => false
  }

  override def hashCode = this.id.hashCode();
}

class IVSeriesTimePoint(val id: Long, val date: DateTime, val value: Float)

object StockOption {
  val dt:DateTimeFormatter = DateTimeFormat.forPattern("MMMyy").withLocale(Locale.US)

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

  def findBySEHKCode(sehkCode: Int): Option[List[StockOption]] = {
    DB.withConnection{
      implicit connection =>
        val abbrevOpt = stockAbbrList.get(sehkCode)

        abbrevOpt match{
          case Some(abbrev) =>
            val abbrevLike= abbrev+"%"
            val sql = SQL("""select id, ticker, name from securities_master.symbol where instrument = 'HK Stock Option' and ticker like {abbrev}""").on("abbrev" -> abbrevLike)
            Option(sql.as(stockOptionParser *))
          case None => None
        }
    }
  }

  def findBySEHKCodeWithHistory(sehkCode: Int, date: DateTime): Option[List[(StockOption, StockOptionHistory)]] = {
    DB.withConnection{
      implicit connection =>
        val abbrevOpt = stockAbbrList.get(sehkCode)

        abbrevOpt match{
          case Some(abbrev) =>
            val abbrevLike = abbrev+"%"
            val sql = SQL("""select a.id, a.ticker, a.name, b.id, b.symbol_id, b.price_date, b.open_price, b.high_price, b.low_price, b.close_price, b.open_interest, b.iv
            from securities_master.symbol a inner join securities_master.daily_price b on (a.id = b.symbol_id)
            where instrument = 'HK Stock Option' and ticker like {abbrev} and b.price_date={date}""")
              .on("abbrev"-> abbrevLike, "date"-> sqlDateTimeFormat.format(date.toDate()))

            val results: List[(StockOption, StockOptionHistory)] = sql.as(stockOptionHistoryParser *)

            Option(results)
          case None => None
        }
    }
  }

  val dateTimeParser =
    date("price_date") map {
      case priceDate => new DateTime(priceDate.getTime())
    }

  def findAvailableDateBySEHKCode(sehkCode: Int): List[DateTime] ={
    DB.withConnection{
      implicit connection =>
        val abbrev = stockAbbrList(sehkCode)
        val abbrevLike = abbrev+"%"

        val sql = SQL("""select distinct b.price_date
            from securities_master.symbol a inner join securities_master.daily_price b on (a.id = b.symbol_id)
            where instrument = 'HK Stock Option' and ticker like {abbrev}""")
          .on("abbrev" -> abbrevLike)

        val results: List[DateTime] = sql.as(dateTimeParser *)

        results
    }
  }
}

object StockOptionHistory{
  val stockOptionHistoryParser: RowParser[StockOptionHistory] = {

    long("id") ~ long("symbol_id") ~ date("price_date") ~
      get[java.math.BigDecimal]("open_price") ~ get[java.math.BigDecimal]("high_price") ~
      get[java.math.BigDecimal]("low_price") ~ get[java.math.BigDecimal]("close_price") ~ long("open_interest") ~ get[java.math.BigDecimal]("iv") map {
      case id ~ symbolId ~ priceDate ~ openPrice ~ dailyHigh ~ dailyLow ~ settlePrice ~ openInterest ~ iv =>{
        val priceDateJoda = new DateTime(priceDate.getTime())
        new StockOptionHistory(id, symbolId, priceDateJoda, openPrice.floatValue(), dailyHigh.floatValue(), dailyLow.floatValue(), settlePrice.floatValue(), openInterest, iv.floatValue())
      }
    }
  }
}

object Stock{
  val sqlDateTimeFormat = new SimpleDateFormat("yyyy-MM-dd")

  val stockParser: RowParser[Stock] = {
    long("id") ~ str("ticker") ~ str("name") map{
      case id ~ ticker ~ name =>
        new Stock(id, ticker, name)
    }
  }

  val stockHistoryParser: RowParser[(Stock, StockHistory)] = {
    stockParser ~ StockHistory.stockHistoryParser map (flatten)
  }

  def findBySEHKCode(sehkCode: Int): Option[Stock] = {
    DB.withConnection{
      implicit connection =>
        val ticker = convertToTicker(sehkCode)
        val sql = SQL("""select id, ticker, name from securities_master.symbol where instrument = 'Stock' and ticker = {ticker}""").on("ticker" -> ticker)

        sql.as(stockParser *).headOption
    }
  }

  def findBySEHKCodeWithHistory(sehkCode: Int, date: DateTime): Option[(Stock, StockHistory)] = {
    DB.withConnection{
      implicit connection =>
        val ticker = convertToTicker(sehkCode)
        val sql = SQL("""select a.id, a.ticker, a.name, b.id, b.symbol_id, b.price_date, b.open_price, b.high_price, b.low_price, b.close_price, b.adj_close_price, b.volume
          from securities_master.symbol a inner join securities_master.daily_price b on (a.id = b.symbol_id)
          where instrument = 'Stock' and ticker = {ticker} and b.price_date={date}""")
          .on("ticker"-> ticker, "date"-> sqlDateTimeFormat.format(date.toDate()))

        sql.as(stockHistoryParser *).headOption
    }
  }

  val statisticParser =
    get[java.math.BigDecimal]("min_price") ~ get[java.math.BigDecimal]("max_price") ~ get[java.math.BigDecimal]("mean_price") ~ get[java.math.BigDecimal]("mean_std") map {
      case minPrice ~ maxPrice ~meanPrice ~ stdPrice => (minPrice.floatValue(), maxPrice.floatValue(), meanPrice.floatValue(), stdPrice.floatValue())
    }

  def getStockStatistic(sehkCode: Int, startDate: DateTime, endDate: DateTime): Option[(Float, Float, Float, Float)]= {
    DB.withConnection{
      implicit connection =>
        val ticker = convertToTicker(sehkCode)
        val sql = SQL(
          """select min(b.adj_close_price) as min_price, max(b.adj_close_price) as max_price,
          | mean(b.adj_close_price) as mean_price, std(b.adj_close_price) as mean_std
          | from securities_master.symbol a inner join securities_master.daily_price b on (a.id = b.symbol_id)
          | where instrument = 'Stock' and ticker = {ticker} and b.price_date between {startDate} and {endDate}
        """.stripMargin)
          .on("ticker"-> ticker, "startDate"-> sqlDateTimeFormat.format(startDate.toDate), "endDate" -> sqlDateTimeFormat.format(endDate.toDate))

        sql.as(statisticParser *).headOption
    }
  }

  def convertToTicker(sehkCode: Int) = "%04d".format(sehkCode) +".HK"
}

object StockHistory{
  val stockHistoryParser: RowParser[StockHistory] = {

    long("id") ~ long("symbol_id") ~ date("price_date") ~
      get[java.math.BigDecimal]("open_price") ~ get[java.math.BigDecimal]("high_price") ~
      get[java.math.BigDecimal]("low_price") ~ get[java.math.BigDecimal]("close_price") ~ get[java.math.BigDecimal]("adj_close_price") ~ long("volume") map {
      case id ~ symbolId ~ priceDate ~ openPrice ~ dailyHigh ~ dailyLow ~ closePrice ~ adjClosePrice ~ volume =>{
        val priceDateJoda = new DateTime(priceDate.getTime())
        new StockHistory(id, symbolId, priceDateJoda, openPrice.floatValue(), dailyHigh.floatValue(),
          dailyLow.floatValue(), closePrice.floatValue(), adjClosePrice.floatValue(), volume)
      }
    }
  }
}

object IVSeries{
  val sqlDateTimeFormat = new SimpleDateFormat("yyyy-MM-dd")

  val ivSeriesParser: RowParser[IVSeries] = {
    long("id") ~ str("series_name") map{
      case id ~ name =>
        new IVSeries(id, name)
    }
  }

  val ivSeriesTimePointParser: RowParser[(IVSeries, IVSeriesTimePoint)] = {
    ivSeriesParser ~ IVSeriesTimePoint.ivSeriesTimePointParser map (flatten)
  }

  def findBySEHKCode(sehkCode: Int, date: DateTime) : Option[IVSeries]={
    val assetName = StockOption.stockFullList.get(sehkCode)
    assetName match{
      case Some(name) =>
        DB.withConnection{
          implicit connection =>
            val nameLike = name+"%"
            val sql = SQL(
              """select id, series_name from securities_master.time_series where series_name like {name}""".stripMargin)
              .on("name"-> nameLike)

            sql.as(ivSeriesParser *).headOption
        }
      case None => None
    }
  }

  def findBySEHKCodeWithTimePoint(sehkCode: Int, date: DateTime): Option[Map[IVSeries, List[IVSeriesTimePoint]]] ={
    val assetName = StockOption.stockFullList.get(sehkCode)
    assetName match{
      case Some(name) =>
        DB.withConnection{
          implicit connection =>
            val nameLike = name+"%"
            val sql = SQL(
              """select a.id, a.series_name, b.id as time_point_id, b.time_point_date, b.value
                | from securities_master.time_series a inner join securities_master.time_point b on (a.id = b.series_id)
                | where a.series_name like {name} and b.time_point_date <={date}""".stripMargin)
              .on("name"-> nameLike, "date" -> sqlDateTimeFormat.format(date.toDate))

            val results: List[(IVSeries, IVSeriesTimePoint)] = sql.as(ivSeriesTimePointParser *)

            Option(results.groupBy(_._1).mapValues(_.map {_._2}))
        }
      case None => None
    }
  }
}

object IVSeriesTimePoint{
  val ivSeriesTimePointParser: RowParser[IVSeriesTimePoint] = {
    long("time_point_id") ~ date("time_point_date") ~ get[java.math.BigDecimal]("value") map {
      case id ~ date ~ value =>{
        val dateJoda = new DateTime(date.getTime())
        new IVSeriesTimePoint(id, dateJoda, value.floatValue())
      }
    }
  }
}