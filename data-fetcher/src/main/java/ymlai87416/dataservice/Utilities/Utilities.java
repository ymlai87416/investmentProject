package ymlai87416.dataservice.Utilities;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

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

    public static void downloadWebPageToFile(String url, String destination) throws IOException {
        URL website = new URL(url);
        File destinationFile = new File(destination);
        destinationFile.createNewFile();
        ReadableByteChannel rbc = Channels.newChannel(website.openStream());
        FileOutputStream fos = new FileOutputStream(destination);
        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
    }
}
