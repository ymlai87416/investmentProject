package ymlai87416.dataservice.Utilities;

/**
 * Created by Tom on 6/10/2016.
 */
public class Utilities {

    public static java.util.Date convertSQLDateToUtilDate(java.sql.Date sqlDate){
        java.util.Date utilDate = new java.util.Date(sqlDate.getTime());
        return utilDate;
    }

    public static java.sql.Date convertUtilDateToSqlDate(java.util.Date utilDate){
        java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
        return sqlDate;
    }

    public static java.sql.Date getCurrentSQLDate(){
        return convertUtilDateToSqlDate(new java.util.Date());
    }
}
