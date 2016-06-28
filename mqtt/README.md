# Using space-shuttle-demo with MQTT based ingestion

If you want to use MQTT based ingestion follow that [README](../README.md) instruction with the following changes:
  * you should create Mosquitto service instance (recommended name: `space-shuttle-mqtt`) instead of gateway and zookeeper instances
  * while pushing an app to cloud foundry you should use `manifest-mqtt.yml` file by calling `cf push -f manifest-mqtt.yml`
  * you should use `ingest_mqtt.sh` script located in mqtt directory to send data to mqtt. More information below
  
#### Get mosquitto credentials
After your application is pushed, you can type ```cf env space-shuttle``` in your terminal. You should see JSON object. The most interesting part is  mosquitto credentials:
```
"mosquitto": [
  {
    "credentials": {
      "port": "32826",
      "ports": {
        "1883/tcp": "32826"
      },
      "username": "sample_user",
      "password": "sample_pass"
    },
    "name": "space-shuttle-mqtt",
     ..........
  }
]
```
You will need **port**, **username** and **password** to connect to mosquitto instance and ingest data to your application.  
  
#### Install mosquitto clients on your local machine
To install Mosquitto on you local machine, follow the instructions here: http://www.eclipse.org/mosquitto/download/

#### Run ingestion script
In a terminal:
```
cd mqtt
vim ingest_mqtt.sh
  {edit first two lines of this file}
    {change HOST from 'localhost' to 'mqtt.<platform-domain>'}
    {change PORT from '1883' to 'port' acquired in "Get mosquitto credentials" section}
./ingest_mqtt.sh
{enter username - the same that you get in ""Get mosquitto credentials" section}
{enter password - the same that you get in "Get mosquitto credentials" section}  