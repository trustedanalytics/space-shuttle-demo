[![Build Status](https://travis-ci.org/trustedanalytics/space-shuttle-demo.svg)](https://travis-ci.org/trustedanalytics/space-shuttle-demo)
[![Dependency Status](https://www.versioneye.com/user/projects/5723704eba37ce00464e061c/badge.svg?style=flat)](https://www.versioneye.com/user/projects/5723704eba37ce00464e061c)

# space-shuttle-demo
Sample application for ATK space shuttle demo

## Overview
![](wikiimages/space_shuttle_demo.png)

#### Scoring flow:
1. Application space-shuttle-demo listens to kafka topic and waits for feature vectors.
2. When a kafka message appears, application asks Scoring Engine to classify received feature vector.
3. Application stores scoring result in InfluxDB.

#### Generating graph flow:
1. Web application asks backend application (space-shuttle-demo) for a anomalies chart.
2. Space-shuttle-demo gets anomalies (classes different than 1) count per minute from InfluxDB.

##Preparing the Scoring Engine model

#### Uploading file to hdfs
1. Go to TAP - `Data catalog` page
1. Select `Submit Transfer` tab
1. Select input type: `Local path`
1. Select file you want to upload (a sample training data can be found here: [src/main/atkmodelgenerator/train-data.csv](src/main/atkmodelgenerator/train-data.csv))
1. Enter `Title`
1. Click `Upload` 

When upload will be completed, you could go to page `Data sets`.
Then select your data set.
Here field `targetUri` contain path to submitted file on hdfs.

#### Creating TAP Analytics Toolkit model
Atk model is necessary to create instance of [Scoring Engine](#scoring-engine)
Go to `src/main/atkmodelgenerator` and run `python atk_model_generator.py hdfs://path_to_training_data`
You can use example training data set from `src/main/client/shuttle_scale_cut_val.csv`
You need to put this training data set on hdfs. Look at [putting file on hdfs](#uploading-file-to-hdfs)
To run script correctly need to use `python 2.7` and install python package: `pip install trustedanalytics`
You will need to enter Atk server url and credentials
Result of this operation is url to Atk model on hdfs

#### Scoring Engine
To create Scoring Engine instance you will need [Atk model](#creating-atk-model).
When you have the model prepared, you can create a new instance of Scoring Engine from Marketplace:
1. Go to page `Marketplace`
1. Select `TAP Scoring Engine` service offering
1. Type name `space-shuttle-scoring-engine`
1. Click `+ Add an extra parameter` and add Atk model url:
  key: `TAR_ARCHIVE`
  value: `hdfs://path_to_model`
1. Click `Create new instance`

Note: The TAR_ARCHIVE value (`hdfs://path_to_model`) is the result of [Creating Atk model](#creating-atk-model)

## Deploying application to TAP

1. Create required service instances (if they do not exist already). Application will connect to these service instances using Spring Cloud Connectors. Note: If you use the recommended names of the required service instances they will be bound automatically with the application when it is pushed to Cloud Foundry. Otherwise, service instances names will need to be adjusted in `manifest.yml` file or removed from `manifest.yml` and bound manually after application is pushed to Cloud Foundry.
    1. Instance of InfluxDB (recommended name: `space-shuttle-db`)
    1. Instance of Zookeeper (recommended name: `zookeeper`)
    1. Instance of Gateway called (recommended name: `space-shuttle-gateway`)
    1. Instance of Scoring Engine with recommended name: `space-shuttle-scoring-engine` (created in [Scoring Engine](#scoring-engine) paragraph)

1. Create Java package:
  ```
  mvn package
  ```
1. (Optional) Edit the auto-generated `manifest.yml`. If you created service instances with different names than recommended, adjust names of service instances in `services` section to match those that you've created. You can also remove `services` section and bind them manually later. You may also want to change the application host/name.
1. Push application to the platform using command:
  ```
  cf push
  ```
1. (Optional) If you removed `services` section from `manifest.yml` application will not be started yet. Bind required service instances (`cf bind-service`) to the application and restage (`cf restage`) the application.
1. The application is up and running

## Sending data to Kafka

To send data to kafka through a gateway you can use python script `client.py` located in the repository

#### Prerequisites:

1. Python 2.7
2. Websocket-client library
  ```pip install websocket-client```

#### Gateway URL

To determine URL of the gateway you are going to send data to:

1. Go to `Applications` page
2. Search for `space-shuttle-gateway`
3. Copy the application URL
   
#### Running python client:

1. Go to: `src/main/client` 
2. Run client.py: 
  ```python client.py wss://<gateway_url>/ws shuttle_scale_cut_val.csv```

## Local development
#### InfluxDB
  To launch space-shuttle demo application it's best to install and run the InfluxDB locally. Instructions how to do it can be found here: http://influxdb.com/docs/v0.8/introduction/installation.html
  
  For Debian/Ubuntu 64bit based systems:
  ```
  wget http://s3.amazonaws.com/influxdb/influxdb_latest_amd64.deb
  sudo dpkg -i influxdb_latest_amd64.deb
  sudo /etc/init.d/influxdb start
  ```         
  Configuration file is located at /opt/influxdb/shared/config.toml or /usr/local/etc/influxdb.conf
  There you can check or change ports used by InfluxFB. By default there will be 8083, 8086, 8090, and 8099.
  
  Space-shuttle app will by default try to connect to influx on localhost:8086. If you have changed that port, you can tell the application to connect to a different port by setting an environment variable:
  ```
  export SERVICES_STORE_APIPORT=<port>
  ```
  
  To access web-based admin panel, open your browser and navigate to: ```localhost:8083```.


#### Running:

To run the application locally type:
```mvn spring-boot:run```






