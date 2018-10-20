# Stock option worksheet presented by Super rich fund

Started by three mans in HK to spectaculate HK stock.

Production URL: [http://stockoption.ymlai87416.com/](http://stockoption.ymlai87416.com/)

## Project structure

### Server 

Written in spring-boot and current deploy in AWS as a standalone JAR with embedded Tomcat. 

#### Run

    gradle bootRun

#### Build

    gradle build

#### Deploy

    java -jar server-0.0.1-SNAPSHOT.jar

### Client

Written in angular 5 and semantic UI to provide a decent front-end to users.

#### Run

    npm run start

#### Build

    ng build --prod

#### Deploy

    Copy the content under ./client/dist/client to Apache server directory e.g. (/var/www/html)

