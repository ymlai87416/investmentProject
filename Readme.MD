# Investment Project

This project is a personnel project for me to analyse HK stock. Beside just using stock price time-series, I also make use of the option data to refine my decision making.

Making use of option data allows me to trade on every market situation. You can bet on a rise, a fall or a stagnant market.
I learnt to use option data from an investment course I attend.

The project is organized in the folllowing structure.

```
investmentProject
├── Readme.MD
├── data
|  ├── zip-option-iv
|  └── zip-option-price
├── data-fetcher
├── database
|  ├── database_schema.sql
|  └── database_schema.v2.sql
└── stock-option-worksheet-js
   ├── Readme.MD
   ├── backup
   ├── client
   └── server
```

### Data
    For other people who is interested to download option data from HKEx from Jan 2017 to Sept 2018

### data-etcher
    a fetcher written in java/spring to fetch data from internet. it is scheduled to run in 3:00am HKT every day.

### database
    database schema for mysql database

### stock-option-worksheet-js
    written in java/spring boot/angular, current in production on AWS.

web-api: [http://stockoption.ymlai87416.com:8080/](http://stockoption.ymlai87416.com:8080/)

frontend: [http://stockoption.ymlai87416.com/](http://stockoption.ymlai87416.com/)

# Future direction

* Option: enhance UI to highlight some abnormal trading data according to rules.