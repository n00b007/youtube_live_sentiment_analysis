import os
import findspark
findspark.init()
import pyspark
import redis
from pyspark import SparkContext
from pyspark.streaming import StreamingContext
from pyspark.streaming.kafka import KafkaUtils
from pyspark.sql import SparkSession, Row
from pyspark.sql.types import *
import json
import time
import nltk
import numpy as np
nltk.download('vader_lexicon')
nltk.download('punkt')
from nltk.sentiment.vader import SentimentIntensityAnalyzer
import nltk.data
import matplotlib.pyplot as pyplot
import mleap.pyspark
from mleap.pyspark.spark_support import SimpleSparkSerializer
from pyspark.ml import PipelineModel

import matplotlib.animation as animation

# redis is used to publish the output to localhost. Consumed by flask which displays the comments and the 2 sentiments in real time.
r = redis.StrictRedis(host="localhost")

# get the sentiment of a sentence using NLTK vader.	
def get_nltk_sentiment(arr):
	sid = SentimentIntensityAnalyzer()
	# tokenizer = nltk.data.load('tokenizers/punkt/english.pickle')
	message_txt = arr
	scores = sid.polarity_scores(message_txt)
	score = ""
	if (scores.get('pos') > scores.get('neg')) and (scores.get('pos') > scores.get('neu')):
		#print("Positive Comment with Score:", scores.get('pos'))
		score = "2.0" # positive
	elif (scores.get('neg') > scores.get('pos')) and (scores.get('neg') > scores.get('neu')):
	    #print("Negative Comment with Score:", scores.get('neg'))
	    score = "0.0" # negative
	else:
		#print("Neutral Comment with Score:", scores.get('neu'))
		score = "1.0" # neutral

	return score

	
# create a global instance of the saved model after loading it.
def getModel():
	if ("sparkModel" not in globals()):
		new_model = PipelineModel.load("/home/sriram/ma_future")
		globals()["sparkModel"] = new_model
	return globals()["sparkModel"]

# create a global instance of the saved sparkSession.
def getSparkSessionInstance(sparkConf):
	if ("sparkSessionSingletonInstance" not in globals()):
	    globals()["sparkSessionSingletonInstance"] = SparkSession \
	        .builder \
	        .config(conf=sparkConf) \
	        .getOrCreate()
	return globals()["sparkSessionSingletonInstance"]
		
# append to a dataframe after every computation to perform further analytics on it in pyspark itself.
# eg. select count(*) from sentences group by sentiment; ( sentiment -> positive, neutral, negative )
# plot this using matplotlib or plotly.
# not enough memory to handle this locally.
def getDataFrame(sparkSession):
	field = [StructField("sentence",StringType(),True),StructField("sentiment",StringType(),True),StructField("nltk_sentiment",StringType(),True)]
	schema = StructType(field)
	if ("sparkDa" not in globals()):
		globals()["sparkDa"]=sparkSession.createDataFrame(sc.emptyRDD(), schema)
	return globals()["sparkDa"]

# load the model and predict the sentiment using our trained model.
# TODO: add spark streaming to the model to update the model using the live streaming data.	
def predict(sentence):
	new_model = getModel()
	#new_model = PipelineModel.load("/home/sriram/ma_future")
	sparkSession = SparkSession.builder.getOrCreate()
	df2 = sparkSession.createDataFrame([{"Comment":sentence}])
	prediction = new_model.transform(df2)
	print("sentence is", sentence,"prediction is",prediction.select("prediction").collect()[0].prediction)
	return str(prediction.select("prediction").collect()[0].prediction)
	

# used to process the rdds as they come in from the kafka stream.
def process(time, rdd):
	print("========= %s =========" % str(time))
	sparkSession = getSparkSessionInstance(rdd.context.getConf())
	# if the RDD is not extract the sentence from the RDD.
	if not rdd.isEmpty():
		sent = rdd.map(lambda x: x[1])
		df = getDataFrame(sparkSession)
		sent_pipelineRDD = rdd.map(lambda x: Row(sentence=x[1],sentiment=predict(x[1]),nltk_sentiment=get_nltk_sentiment(x[1])))
		# sent_nltk_piplelineRDD = rdd.map(lambda x: Row(sentence=x[1], sentiment=get_nltk_sentiment(x[1])))
		print("SENTENCE->",sent_pipelineRDD.collect()[0].sentence,"SENTIMENT->",sent_pipelineRDD.collect()[0].sentiment,"NLTK_SENTIMENT->",sent_pipelineRDD.collect()[0].nltk_sentiment)
		new_df = sparkSession.createDataFrame(sent_pipelineRDD)
		new_df.createOrReplaceTempView("sentences")
		output_df = sparkSession.sql("select * from sentences")
		merged_df = df.union(new_df)
		globals()["sparkDa"] = merged_df
		for_redis = sent_pipelineRDD.collect()[0].sentence+"|"+str(sent_pipelineRDD.collect()[0].sentiment)+"|"+str(sent_pipelineRDD.collect()[0].nltk_sentiment)
		print("sending this to redis",for_redis)
		r.publish("youtube",for_redis)
		output_df.show()
   	



sc = SparkContext(appName="PythonSparkStreamingKafka")
ssc = StreamingContext(sc, 2)
# get a spark dstream object from the kafka stream, here the consumer is defined at localhost:9092 and listening to the channel hello-world.
kafkaStream = KafkaUtils.createDirectStream(ssc, ["hello-world"], {"metadata.broker.list":"localhost:9092"})
# for each RDD perform the function.
kafkaStream.foreachRDD(process)
#counts = lines.flatMap(lambda line: line.split(" ")).map(lambda word: (word,1)).reduceByKey(lambda a,b: a+b)
#counts.pprint()
ssc.start()
ssc.awaitTermination()
