package ymlai87416.dataservice.utilities;

import ymlai87416.dataservice.fetcher.constant.FileEncoding;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;

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

    public static java.sql.Date getCurrentSQLDateTime(){
        return convertUtilDateToSqlDate(new java.util.Date());
    }

    public static java.sql.Date getCurrentSQLDate(){
        java.util.Date currentTime = new java.util.Date();
        java.util.Date today = new java.util.Date(currentTime.getYear(), currentTime.getMonth(), currentTime.getDate());

        return convertUtilDateToSqlDate(today);
    }

    public static void downloadWebPageToFile(String urlString, String destination, String encoding) throws IOException {
        //OutputStream out = new FileOutputStream(destination, FileEncoding.defaultFileEncoding);
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(destination), FileEncoding.defaultFileEncoding));

        URL url = new URL(urlString);
        URLConnection conn = url.openConnection();
        conn.connect();
        InputStream is = conn.getInputStream();
        BufferedReader in = new BufferedReader(
                new InputStreamReader(is, encoding));

        copy(in, out);
        is.close();
        out.close();
    }

    public static void downloadWebPageToFile(String urlString, String destination) throws IOException {
        downloadWebPageToFile(urlString, destination, FileEncoding.defaultFileEncoding);
    }

    private static void copy(BufferedReader from, BufferedWriter to) throws IOException {
        char[] buffer = new char[4096];
        while (true) {
            int numBytes = from.read(buffer);
            if (numBytes == -1) {
                break;
            }
            to.write(buffer, 0, numBytes);
        }
    }

    public static java.util.Date getNextDate(java.util.Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add( Calendar.DATE, 1 );
        return cal.getTime();
    }
}

/*public static void downloadWebPageToFile2(String url, String destination) throws IOException {
        URL website = new URL(url);
        File destinationFile = new File(destination);
        destinationFile.createNewFile();
        ReadableByteChannel rbc = Channels.newChannel(website.openStream());
        FileOutputStream fos = new FileOutputStream(destination);
        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
    }*/

