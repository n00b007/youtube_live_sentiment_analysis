import redis
import time

r = redis.StrictRedis(host="localhost")

p = r.pubsub()
p.subscribe("channel")
while True:
	print("Waiting For the publisher...")
	message = p.get_message()
	if message:
		print("message received:",message)
	
	time.sleep(1)
	

