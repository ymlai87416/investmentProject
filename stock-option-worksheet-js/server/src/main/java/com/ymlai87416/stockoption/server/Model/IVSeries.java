package com.ymlai87416.stockoption.server.model;

public class IVSeries {

    private Long id;
    private String seriesName;

    private String getAsset(){
        return seriesName.replace("  IV (%)", "")
                .replace("  HV10 (%)", "")
                .replace("  HV30 (%)", "")
                .replace("  HV60 (%)", "")
                .replace("  HV90 (%)", "");
    }

    private String getSeriesType(){
        String result = null;

        if(seriesName.indexOf("  IV (%)")!= -1)
            result = "Implied volatility";
        else if(seriesName.indexOf("  HV10 (%)")!= -1)
            result = "History volatility 10 days";
        else if(seriesName.indexOf("  HV30 (%)")!= -1)
            result = "History volatility 30 days";
        else if(seriesName.indexOf("  HV60 (%)")!= -1)
            result = "History volatility 60 days";
        else if(seriesName.indexOf("  HV90 (%)")!= -1)
            result = "History volatility 90 days";

        return result;
    }

    @Override
    public boolean equals(Object other){
        if(other instanceof IVSeries){
            return ((IVSeries)other).id == this.id;
        }
        else
            return false;
    }

    @Override
    public int hashCode() {
        return this.id.hashCode();
    }

}
