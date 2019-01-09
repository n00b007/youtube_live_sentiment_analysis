import findspark
findspark.init()
from pyspark import SparkContext
from pyspark.streaming import StreamingContext
from pyspark.sql.functions import udf
from pyspark.sql.types import StringType
from pyspark.sql import SparkSession, Row
from pyspark.sql.types import *
from pyspark.ml.linalg import *
from pyspark.ml import PipelineModel
from mleap.pyspark.spark_support import SimpleSparkSerializer

from nltk.stem.wordnet import WordNetLemmatizer
from nltk.corpus import stopwords
from nltk import pos_tag
import string
import re
import mleap.pyspark
from pyspark.ml.classification import LogisticRegression
from pyspark.ml.evaluation import MulticlassClassificationEvaluator
from pyspark.ml.tuning import CrossValidator, ParamGridBuilder
from pyspark.ml.classification import DecisionTreeClassifier

from pyspark.ml.feature import HashingTF, IDF, Tokenizer
from pyspark.ml.feature import StringIndexer
from pyspark.ml import Pipeline

# create a spark context
sc = SparkContext(appName="myModel")
spark = SparkSession.builder.getOrCreate()
file_type = "csv"
delimiter = ","
file_location = "/home/sriram/test_data_twitter.csv"
infer_schema = "false"
first_row_is_header = "true"
# The applied options are for CSV files. For other file types, these will be ignored.
df = spark.read.format(file_type) \
  .option("inferSchema", infer_schema) \
  .option("header", first_row_is_header) \
  .option("sep", delimiter) \
  .load(file_location)

# check the distinct values available in the target column.
print(df.select("Label").distinct())
print(df)

# remove non ASCII characters
def strip_non_ascii(data_str):
    stripped = (c for c in data_str if 0 < ord(c) < 127)
    return ''.join(stripped)

# create and call a udf which will convert a normal function to a user defined function.    
strip_non_ascii_udf = udf(strip_non_ascii, StringType())

# df = df.withColumn('Comment',strip_non_ascii_udf(df['Comment']))
df.show(5,True)


# fixed abbreviation
def fix_abbreviation(data_str):
    data_str = data_str.lower()
    data_str = re.sub(r'\bthats\b', 'that is', data_str)
    data_str = re.sub(r'\bive\b', 'i have', data_str)
    data_str = re.sub(r'\bim\b', 'i am', data_str)
    data_str = re.sub(r'\bya\b', 'yeah', data_str)
    data_str = re.sub(r'\bcant\b', 'can not', data_str)
    data_str = re.sub(r'\bdont\b', 'do not', data_str)
    data_str = re.sub(r'\bwont\b', 'will not', data_str)
    data_str = re.sub(r'\bid\b', 'i would', data_str)
    data_str = re.sub(r'wtf', 'what the fuck', data_str)
    data_str = re.sub(r'\bwth\b', 'what the hell', data_str)
    data_str = re.sub(r'\br\b', 'are', data_str)
    data_str = re.sub(r'\bu\b', 'you', data_str)
    data_str = re.sub(r'\bk\b', 'OK', data_str)
    data_str = re.sub(r'\bsux\b', 'sucks', data_str)
    data_str = re.sub(r'\bno+\b', 'no', data_str)
    data_str = re.sub(r'\bcoo+\b', 'cool', data_str)
    data_str = re.sub(r'rt\b', '', data_str)
    data_str = data_str.strip()
    return data_str

fix_abbreviation_udf = udf(fix_abbreviation, StringType())

df = df.withColumn('Comment',fix_abbreviation_udf(df['Comment']))
df.show(5,True)

def remove_extras(data_str):
    punc_re = re.compile('[%s]' % re.escape(string.punctuation))
    mention_re = re.compile('@(\w+)')
    # convert to lowercase
    data_str = data_str.lower()
    # remove @mentions
    data_str = mention_re.sub(' ', data_str)     
    return data_str
remove_extras_udf = udf(remove_extras, StringType())


df = df.withColumn('Comment',remove_extras_udf(df['Comment']))
df.show(5)

# drop the userId column because its useless for calculating a sentiment.
df = df.drop("UserID")
df = df.withColumnRenamed('Label','Target')
df.show(5)

df=df.dropna()
df.count()
print(df)

# split the dataset.
(train_set, val_set, test_set) = df.randomSplit([0.80, 0.18, 0.02], seed = 2000)

tokenizer = Tokenizer(inputCol="Comment", outputCol="words")
hashtf = HashingTF(inputCol="words", outputCol='features')
idf = IDF(inputCol='tf', outputCol="features", minDocFreq=2) #minDocFreq: remove sparse terms

label_stringIdx = StringIndexer(inputCol = "Target", outputCol = "label")

val_set.printSchema()
train_set.printSchema()

dt = DecisionTreeClassifier()
'''
lr = LogisticRegression(maxIter=100).setLabelCol('label').\
    setFeaturesCol('idf').\
    setRegParam(0.3).\
    setMaxIter(100).\
    setElasticNetParam(0)
'''
# create a pipeline, defining the stages.
lr_pipeline = Pipeline(stages=[label_stringIdx,tokenizer,hashtf,dt])

# create a parameter grid for getting the best possible m*n combinations of parameters.
paramGrid = ParamGridBuilder().addGrid(hashtf.numFeatures, [1000, 2000]).build()
cv = CrossValidator(estimator=lr_pipeline, evaluator=MulticlassClassificationEvaluator(), estimatorParamMaps=paramGrid)

#val_set = val_set.drop('Target')
#train_set = train_set.drop('Target')
cv_model = cv.fit(train_set)

# select the best model.
model = cv_model.bestModel
transformed = model.transform(val_set)

#predictions = model.transform(val_set)
# predictions = lrModel.transform(val_df)
# from pyspark.ml.evaluation import BinaryClassificationEvaluator
# evaluator = BinaryClassificationEvaluator(rawPredictionCol="rawPrediction")
# evaluator.evaluate(predictions)


# check the accuracy of the model.
evaluator = MulticlassClassificationEvaluator()
print(evaluator.evaluate(transformed))

print(transformed)




# MAGIC %sh
# MAGIC rm -rf /tmp/mleap_python_model_export
# MAGIC mkdir /tmp/mleap_python_model_export
# MAGIC rm -rf /FileStore/future.zip


# save the model for future use.
model.save("/home/sriram/ma_future")

# this saves the pipeline using mleap. not using this but just wanted to demonstrate it.
model.serializeToBundle("jar:file:/home/sriram/future4.zip", transformed)
#dbutils.fs.cp("file:/tmp/mleap_python_model_export/future.zip", "file:/home/sriram/future.zip")
#display(dbutils.fs.ls("dbfs:/FileStore"))


# deserialize the model from the saved bundle. Again this is using mleap.
deserializedPipeline = PipelineModel.deserializeFromBundle("jar:file:/home/sriram/future4.zip")
new_model = PipelineModel.load("/home/sriram/ma_future")


# time to test the model.
# add your own comment and check.
d = [{"Comment":"this sucks"}]
df2 = spark.createDataFrame(d)
df2.show()
new_predictions = new_model.transform(df2)
predictions = deserializedPipeline.transform(df2)
abc = new_predictions.select("prediction")

# get the prediction.
print(abc.collect()[0].prediction)




