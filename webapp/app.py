from flask import Flask, render_template,Response,request,stream_with_context
import redis
import logging

app = Flask(__name__)

r = redis.StrictRedis(host="localhost")


def event_stream():
    pubsub = r.pubsub()
    pubsub.subscribe('youtube')
    for message in pubsub.listen():
        yield 'data: %s\n\n' % message['data']

@app.route('/')
def render_homepage():
    app.logger.info("Home Page")
    return render_template('index.html')


#  used to get the data for js.
# js makes a call to this stream and when there is a message on the stream it is sent back to js in text/event-stream format.
@app.route('/stream')
def stream():
    if request.headers.get('accept') == 'text/event-stream':
        app.logger.info("Stream")
        return Response(event_stream(),mimetype="text/event-stream")

if __name__== '__main__':
    app.logger.info("web app started")
    app.debug = True
    app.run(host='0.0.0.0',port=5000, threaded=True)



