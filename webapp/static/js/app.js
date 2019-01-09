var eventSource = new EventSource("/stream");
var dataStream = document.getElementById("data-stream");
var eventList = document.getElementById("visual-stream");

eventSource.onmessage = function(e) {

    var newData = e.data.split("|");
    console.log("new_data is",newData);
    comment = newData[0]
    sentiment_1 = newData[1]
    nltk_sentiment = newData[2]

    var table_element = document.getElementById("data-stream")

    // Create an empty <tr> element and add it to the 1st position of the table:
    var row = table_element.insertRow(0);

    // Insert new cells (<td> elements) at the 1st and 2nd position of the "new" <tr> element:
    var cell1 = row.insertCell(0);
    var cell2 = row.insertCell(1);
    var cell3 = row.insertCell(2);

    // Add some text to the new cells:
    cell1.innerHTML = comment;
    cell2.innerHTML = sentiment_1;
    cell3.innerHTML = nltk_sentiment;

    if (parseFloat(sentiment_1) == 1.0) {
        cell2.className = "negative"
    }
    if (parseFloat(nltk_sentiment) == 0.0) {
        cell3.className = "negative"
    }
    else if (parseFloat(nltk_sentiment) == 1.0) {
        cell3.className = "neutral"
    }
    else if (parseFloat(nltk_sentiment) == 2.0) {
        cell3.className = "positive"
    }

    

}