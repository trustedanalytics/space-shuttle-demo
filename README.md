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

## Deploying application to TAP
   
### Manual deployment
1. Upload the model to HDFS: 
   * Login to TAP console and select `Data catalog` page in the navigation panel
   * Select `Submit Transfer` tab
   * Select input type: `Link`. Place link: [https://s3.amazonaws.com/trustedanalytics/v0.7.0/models/space-shuttle-model.tar](https://s3.amazonaws.com/trustedanalytics/v0.7.0/models/space-shuttle-model.tar)
   * Enter `Title`
   * Click `Upload` 
   
   Alternatively, you can create TAP Analytics Toolkit model by yourself. Please refer to the [instructions](#creating-tap-analytics-toolkit-model).
   
1. Create required service instances (if they do not exist already). 
   Application will connect to these service instances using Spring Cloud Connectors. Note: If you use the recommended names of the required service instances they will be bound automatically with the application when it is pushed to Cloud Foundry. Otherwise, service instances names will need to be adjusted in `manifest.yml` file or removed from `manifest.yml` and bound manually after application is pushed to Cloud Foundry.
    1. Instance of InfluxDB (recommended name: `space-shuttle-db`)
    1. Instance of Zookeeper (recommended name: `zookeeper`)
    1. Instance of Gateway called (recommended name: `space-shuttle-gateway`)
    1. Instance of Scoring Engine with recommended name: `space-shuttle-scoring-engine`. Instructions below describe how to create the Scoring Engine service instance.

   To create Scoring Engine service instance take the following actions:
     * Select `Marketplace` tab in TAP Console navigation panel
     * Select `TAP Scoring Engine` service offering
     * Type name `space-shuttle-scoring-engine`
     * Click `+ Add an extra parameter` and add TAP Analytics Toolkit model url:
        key: `TAR_ARCHIVE`
        value: `hdfs://path_to_model`
     * Click `Create new instance`

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

### Automated deployment
* Switch to `deploy` directory: `cd deploy`
* Download [the model](https://s3.amazonaws.com/trustedanalytics/v0.7.0/models/space-shuttle-model.tar) and rename it to `model.tar` 
* Install tox: `sudo -E pip install --upgrade tox`
* Run: `tox`
* Activate virtualenv with installed dependencies: `. .tox/py27/bin/activate`
* Run deployment script: `python deploy.py`, the script will use parameters provided on input. Alternatively, provide parameters when running script. (`python deploy.py -h` to check script parameters with their descriptions).

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

##Creating TAP Analytics Toolkit model
To create the model for Scoring Engine take the following actions: 

#### Upload training data set to HDFS
   * Login to TAP console and select `Data catalog` page in the navigation panel
   * Select `Submit Transfer` tab
   * Select input type: `Local path`
   * Select sample training data file which can be found here: [src/main/atkmodelgenerator/train-data.csv](src/main/atkmodelgenerator/train-data.csv))
   * Enter `Title`
   * Click `Upload` 

When upload is completed, click `Data sets` tab and view the details of uploaded data set by clicking its name.
Copy the value of `targetUri` which contains path to the uploaded data set in HDFS - you will need this to create TAP Analytics Toolkit model in Jupyter notebook.

#### Create TAP Analytics Toolkit instance
   * In TAP console select `Data Science` and then `TAP Analytics Toolkit` tab
   * If there is an instance of `TAP Analytics Toolkit` installed you will see it in an instances list - no action needed. If there are no instances you will be asked if you want to create one - select `Yes`, wait until the application is created (it can take about a minute or two). The application will appear in `TAP Analytics Toolkit` instances list

#### Create Jupyter instance
   * In `Data Science` tab select `Jupyter`. Create new `Jupyter` instance.
   * Copy the password for created Jupyter instance.
   * Login to Jupyter by clicking `App Url` link. 

#### Install TAP Analytics Toolkit client
   * Create new notebook
   * Install TAP Analytics Toolkit client in your notebook by executing command: `!pip install <link-to-atk-server>/client`. `<link-to-atk-server>` can be copied from URL column in `TAP Analytics Toolkit` instances list.

#### Connect to TAP Analytics Toolkit server and run model generation script
   * Copy the contents from [src/main/atkmodelgenerator/atk_model_generator.py](src/main/atkmodelgenerator/atk_model_generator.py) into your notebook. 
   * After imports section set the URI to TAP Analytics Toolkit server: `ta.server.uri = <link-to-atk-server>`
   * Set the value of `ds` as the link to the data set previously uploaded to HDFS (`targetUri`).
   * Run the script. The link to the created model in HDFS will be printed in the output.
