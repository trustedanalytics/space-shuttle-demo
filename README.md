# space-shuttle-demo
Sample application for ATK space shuttle demo

## Overview
![](wikiimages/SpaceShuttle.png)

#### Scoring flow:
1. Application space-shuttle-demo listens to kafka topic and waits for feature vectors.
2. When a kafka message appears, application asks Scoring Engine to classify received feature vector.
3. Application stores scoring result in InfluxDB.

#### Generating graph flow:
1. Web application asks backend application (space-shuttle-demo) for a anomalies chart.
2. Space-shuttle-demo gets anomalies (classes different than 1) count per minute from InfluxDB.

## Local development
#### Prerequisites
* InfluxDB
  * You need to install and run it locally. You can find instruction here: http://influxdb.com/docs/v0.8/introduction/installation.html
  ```
  wget http://s3.amazonaws.com/influxdb/influxdb_latest_amd64.deb
  sudo dpkg -i influxdb_latest_amd64.deb
  ```         
  Configuration file is located at /opt/influxdb/shared/config.toml or /usr/local/etc/influxdb.conf
  There you can check or change ports used by InfluxFB. By default there will be 8083, 8086, 8090, and 8099.

  To start InfluxDB type: ```sudo /etc/init.d/influxdb start```
  
  You can then access admin panel, by default accessible at: ```localhost:8083```
  
  After going there for first time, remember to create username and password. ```root:root``` seems to be a good choice.

* Scoring Engine
  There should be information added in environment variables about scoring engine URL. Required variable is baseUrl:
  '''
  {
      "credentials": {
       "baseUrl": "atk-scoringengine.example.com"
      },
      "label": "user-provided",
      "name": "atkscoreengine",
      "syslog_drain_url": "",
      "tags": []
     }

  '''

#### Running:

To run the application type:
```mvn spring-boot:run```






