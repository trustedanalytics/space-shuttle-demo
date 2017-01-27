[![Build Status](https://travis-ci.org/trustedanalytics/space-shuttle-demo.svg)](https://travis-ci.org/trustedanalytics/space-shuttle-demo)
[![Dependency Status](https://www.versioneye.com/user/projects/5723704eba37ce00464e061c/badge.svg?style=flat)](https://www.versioneye.com/user/projects/5723704eba37ce00464e061c)

# space-shuttle-demo
This page provides instructions for using the Space-Shuttle demo application. The default version of this application uses Gateway and Kafka as streaming sources. If you want to use a mqtt instead go [here](mqtt/README.md)

## Overview
![](wikiimages/space_shuttle_demo.png)

### Implementation summary
#### Scoring flow:
* The space-shuttle-demo application listens to the Kafka topic and waits for feature vectors.
* When a Kafka message appears, the application asks the Scoring Engine to classify the received feature vector.
* The application stores the scoring result in an InfluxDB database.

#### Generating graph flow:
* The web application asks the backend application (space-shuttle-demo) for an anomalies chart.
* The space-shuttle-demo application fetches anomalies (classes different than 1) several times per minute from InfluxDB and then displays them.

## Deploying application to TAP

### Manual deployment
1. Upload the model to HDFS:
   * Download the pre-packaged model from: [https://s3.amazonaws.com/trustedanalytics/v0.7.4/models/spaceshuttleSVMmodel.zip](https://s3.amazonaws.com/trustedanalytics/v0.7.4/models/spaceshuttleSVMmodel.zip)
   * Login to the TAP console and navigate to  **Data catalog > Submit Transfer**.
   * Select input type: **Local path** and choose the previously downloaded model.
   * Enter a title in the **Title** field.
   * Click the **Upload** button.

   Alternatively, you can create the Spark-tk model yourself. Refer to the [instructions](#creating-spark-tk-model) later on this page.

1. Create the required service instances (if they do *not* exist already). The application will connect to these service instances using Spring Cloud Connectors. **Note:** If you use the recommended names for the required service instances, they will be bound automatically with the application when it is pushed to Cloud Foundry. Otherwise, service instance names will need to be either (1) adjusted in the 'manifest.yml' file or (2) removed from 'manifest.yml' and bound manually after the application is pushed to Cloud Foundry.
    * Instance of InfluxDB (recommended name: ‘space-shuttle-db’).
    * Instance of Zookeeper (recommended name: ‘space-shuttle-zookeeper’).
    * Instance of Gateway (recommended name: ‘space-shuttle-gateway’).
    * Instance of Scoring Engine with recommended name: ‘space-shuttle-scoring-engine’. The next step provides instructions to create the Scoring Engine service instance.

    To create the Scoring Engine service instance:
    * From the TAP console, navigate to **Services > Marketplace**. Select the “Scoring Engine for Spark-tk” service.
    * Type the name ‘space-shuttle-scoring-engine’
    * Click **+ Add an extra parameter** and add the uploaded model url: ‘key: uri value: hdfs://path_to_model'.
    * Click the **Create new instance** button.

1. Create a Java package:
  ```
  mvn package
  ```
1. (Optional) If you created service instances with different names than were recommended, edit the auto-generated 'manifest.yml' file to adjust the names of service instances in the services section to match those that you've created. You can also remove the services section and bind them manually later. You may also want to change the application host/name.
1. Push the application to the platform using the Cloud Foundry (CF)  command:  ‘cf push’.
1. (Optional) If you removed the services section from ‘manifest.yml’, the application will *not* be started yet. First, bind the required service instances (‘cf bind-service’) to the application and then restage (‘cf restage’) it.
1. The application is now up and running. You should see the space shuttle image appear followed by anomaly data being displayed.

### Automated deployment
1. Switch to the ‘deploy’ directory using: ‘cd deploy’
1. Download [the model](https://s3.amazonaws.com/trustedanalytics/v0.7.4/models/spaceshuttleSVMmodel.zip) and rename it to ‘model.zip.
1. Install tox: ‘sudo -E pip install --upgrade tox’
1. Run: ‘tox’
1. Activate virtualenv with installed dependencies: ‘. .tox/py27/bin/activate’
1. Run the deployment script: ‘python deploy.py’; the script will use parameters provided on input. Alternatively, provide parameters when running the script. (python deploy.py -h to check script parameters and their descriptions).

## Sending data to Kafka

To send data to Kafka through a gateway you can either (1) push space_shuttle_client from the client directory to the space with the existing gateway instance or (2) use the Python file ‘space_shuttle_client.py’, locally passing the Gateway url as a parameter.

### Running on Cloud Foundry:

1. Login to the space containing space-shuttle-gateway
1. Go to: ‘client/’
1. Push the app to Cloud Foundry using: ‘cf push’
>Note: In case of name conflict during the push, add the name parameter ‘cf push <custom_name>’

### Local configuration:

#### Prerequisites:

1. Python 2.7
1. tox ([installation details](http://tox.readthedocs.io/en/latest/install.html))

### Gateway URL
To determine the URL of the gateway you are going to send data to:

1. From the TAP console, navigate to **Applications**.
1. Search for space-shuttle-gateway
1. Copy the application URL.

### Running Python client locally:
1. Go to: ‘client/’
1. Run tox: ‘tox’
1. Activate created virtualenv: ‘. .tox/py27/bin/activate’
1. Run: ‘python space_shuttle_client.py --gateway-url <gateway_url>’

##Creating Spark-tk model
To create the model for the Scoring Engine, follow these steps:

#### Upload training data set to HDFS
1. Login to the TAP console and navigate to  **Data catalog > Submit Transfer**.
1. Select the input type: **Local path**.
1. Select the sample training data file, which can be found here: src/main/sparktkmodelgenerator/train-data.csv)
1. Enter a title in the **Title** field.
1. Click the **Upload** button.
When the upload is completed, click the **Data sets** tab and view the details of the uploaded data set by clicking its name. Copy the value of targetUri, which contains the path to the uploaded data set in HDFS; you will need this to create the Spark-tk model in Jupyter notebook.

#### Create Jupyter instance
1. In the TAP console, navigate to the **Data Science > Jupyter**. Click the **Create a new Jupyter instance** button.
1. Copy the password for the newly created Jupyter instance (for use in the next step).
1. Login to Jupyter by clicking the App Url link, using the password you just copied.
1. In Jupyter, click the (white) **Upload** button, then navigate to the Space Shuttle notebook file, which can be found here: `space-shuttle-demo-master/sparktkmodelgenerator/sparktk_shuttle_model_generator.ipynb`
1. Select the file and click the second (blue) **Upload** button.
1. In Jupyter, click on the `sparktk_shuttle_model_generator.ipynb` file to open the file.

#### Connecting to TAP server and generating the model
In this step, you modify the generic notebook script, then run it to create the model.

1. In the TAP Console, navigate to the **Data catalog > Data sets** tab and then click on the name of the data set to show detailed information. Copy the URI for the data set for use in the next step.
1. In Jupyter, paste this URI over the default name for data set `ds=` in the third cell.  
```
    ds=”your_dataset_uri_here”
```  
1. In Jupyter, select the second cell, then on the Jupyter menu, select **Cell > Run Cell, Select Below** (same as the ![](https://github.com/trustedanalytics/platform-wiki/blob/master/wikiImages/Jup_Run_Sel_Next_Icon.png) icon) to run the script in the cell.
1. When execution of the cell is finished, the asterisk in the `In [*]` text on the right side of the cell is replaced by the cell number. Work through Cells 3 through 6,  one at a time, using **Cell > Run, Highlight Next**. Make sure you wait for the current cell to complete before moving to the next one.
1. When you start Cell 7, you will be prompted for:  
  - The URL of your TAP server. Paste the domain name of the TAP server into the field and press **Enter**. (Example: `ontap07.demo-gotapaas.com`.)  
  - Your TAP user name. Type your user name and press **Enter**.  
  - Your TAP password. Type your password and press **Enter**.  
1. In the next cell, you are prompted for the organization number, for example **[1-2]**. Enter the organization number and press **Enter**.
>If there is only one organization, you will *not* be prompted.

1. Work through the remaining cells, one at a time, using **Cell > Run, Highlight Next**. Make sure you wait for the current cell to complete before moving to the next one.
1. When finished, the trained model will appear in the **Data catalog > Data sets** tab. Filename = `spaceshuttleSVMmodel.mar`.
>The Model Archive format (.mar files) is a new model format for use with spark-tk.

1. Click on the model name to see detailed information. Copy the targetURI for the model for use in creation of space-shuttle-scoring-engine.

## Local development
The application can be run in three different configurations depending on chosen data provider (streaming source).

* There is one special Spring @Profile (local), which was created to enable local development.
* cloud, Kafka, and mqtt profiles should be inactive when doing local development.
* random profile should be active instead while local development. It uses a simple random number generator instead of streaming source like Kafka or mqtt.

>Note: Streaming data during local development is random numbers, so this generates a lot of anomalies.

### Local Configuration
#### Prerequisites
##### InfluxDB
1. Instructions to install and run InfluxDB are provided here: http://influxdb.com/docs/v0.8/introduction/installation.html
1. The easiest way is to run InfluxDB is in inside a docker container: docker run -d -p 8083:8083 -p 8086:8086 tutum/influxdb:0.8.8.

>Note: influxdb:0.9 is *not* backwards compatible with 0.8.x.


##### Scoring Engine  
For instructions on pushing the scoring engine to the platform, go [here[(https://github.com/trustedanalytics/space-shuttle-demo#manual-deployment).  


#### Run
1. Make sure that both local and random profiles are active.
1. ‘export SE_URL <scoring engine URL>’
     **Note:** The link should not contain  the ‘http:// protocol’
1. ‘mvn spring-boot:run’
1. In a web browser, enter ‘localhost:8080’
