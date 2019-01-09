*****************************************************************************
SETUP
*****************************************************************************

*****************************
APACHE SPARK, KAFKA, REDIS
*****************************
1. Please set up the apache spark environment in your local machine. Spark version used was 2.3.2 using Hadoop.
Resource used -> http://www.insightsbot.com/blog/1bHSyT/apache-spark-installation-guide-on-ubuntu-1604-lts
2. Please set up apache zookeeper on your local machine.
Resource used -> https://zookeeper.apache.org/doc/r3.3.3/zookeeperStarted.html
3. Please install apache kafka which will act as the producer/consumer.
Resource used -> https://kafka.apache.org/documentation/ and the same from tutorialspoint.
4. Apache spark will implement dstreams here which is connected to the kafka streaming consumer which gets the data from the java kafka producer.

*****************************
JAVA ENVIRONMENT
*****************************
1. Please set up JVM and JDK -> openJDK1.8 or any other version which suits you.
2. All the files are already present with the requisite libraries. Use the gradle.bat file to load the libraries to the version you want it to be.
Personally I prefer downloading the libraries and avoiding any unnecessary glunk.

*****************************
PYTHON
*****************************
1. Version used 3.5
2. Please set up flask and have a look at the libraries used and make sure you download them using easy install or pip.

********************************
OBTAIN YOUTUBE API CLIENT SECRET
********************************
1. https://github.com/youtube/api-samples
2. https://www.slickremix.com/docs/get-api-key-for-youtube/

Start UP
*****************************
1. Start zookeeper by first going to the zookeeper installation folder, for me it was located at /usr/share/zookeeper. Here run -> bin/zkServer.sh start (sudo maybe required based on your permissions)
2. Once zookeeper is up and running start the kafka-producer, by going to the apache kafka installation folder and running -> bin/kafka-server-start.sh config/server.properties. Here the server.properties is something I created for my server and it contains the following ->

SNAPSHOT NOT THE COMPLETE FILE
############################# Zookeeper #############################

# Zookeeper connection string (see zookeeper docs for details).
# This is a comma separated host:port pairs, each corresponding to a zk
# server. e.g. "127.0.0.1:3000,127.0.0.1:3001,127.0.0.1:3002".
# You can also append an optional chroot string to the urls to specify the
# root directory for all kafka znodes.
zookeeper.connect=localhost:2181

# Timeout in ms for connecting to zookeeper
zookeeper.connection.timeout.ms=6000


############################# Group Coordinator Settings #############################


3. Once the kafka producer is up, go to the youtubeLiveAPI/com/liveyoutube/YoutubeLiveStream and run the Main.main() function.
4. If you get any error in the previous step, make sure all the libraries are added, and you have to include your client_secret file in the path and modify it in a class called Auth and try again.
5. It should work now and a browser window will pop up asking you to grant permissions to the youtube API.
6. Once this is done, you should see the comments popping up on the terminal window where you've executed the java program.
7. start the sentiment_analyzer by running the command on the terminal -> spark-submit --packages ml.combust.mleap:mleap-spark-base_2.11:0.10.1,ml.combust.mleap:mleap-spark_2.11:0.10.1,org.apache.spark:spark-streaming-kafka-0-8_2.11:2.0.2 my_model.py
8. You will get an error because you have to change the path, to store the model inside the my_model.py file.
9. Run the same command and you should have the model created in the path you wish.
10. Run the spark-submit --packages ml.combust.mleap:mleap-spark-base_2.11:0.10.1,ml.combust.mleap:mleap-spark_2.11:0.10.1,org.apache.spark:spark-streaming-kafka-0-8_2.11:2.0.2 predict_sentiment.py
11. In another terminal please run python3.5 app.py which is found inside webapps/. This will start the flask webservice and can be connected to using the browser and switching to localhost:5000
12. Here you should see the comment and the associated nltk_sentiment where 0.0 is negative, 1.0 is neutral and 2.0 is positive.
13. You can also view the your model's sentiment.

***************************************
TODO:
***************************************
1. Sparkstreaming.. updating the model using the live data stream.
2. Better visualization.
3. More analytics probably -> dataframe has been created, can probably use that and register API endpoints on flask to get the requisite Business Analytics on the live stream.

