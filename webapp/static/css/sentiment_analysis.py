from pyspark.sql import SparkSession
from pyspark import SparkContext, SparkConf
import findspark
findspark.init()

conf = SparkConf().setAppName("Hella").setMaster("local")
sc = SparkContext(conf=conf)
spark = SparkSession.builder.config(conf=conf).getOrCreate()

file_location = "/home/sriram/Downloads/test_data_twitter.csv"
file_type = "csv"

# CSV options
infer_schema = "false"
first_row_is_header = "true"
delimiter = ","

# The applied options are for CSV files. For other file types, these will be ignored.
df = spark.read.format(file_type) \
    .option("inferSchema", infer_schema) \
    .option("header", first_row_is_header) \
    .option("sep", delimiter) \
    .load(file_location)
df.head(10)
