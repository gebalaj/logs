# Logs
Small application that process entries in a log file.


Every line in the file is a JSON object containing eventLog data:
id - the unique eventLog identifier
state - whether the eventLog was started or finished (can have values "STARTED" or "FINISHED")
timestamp - the timestamp of the eventLog in milliseconds

Additional attributes:
type - type of log
host - hostname

Example:

{"id":"scsmbstgra", "state":"STARTED", "type":"APPLICATION_LOG", "host":"12345", "timestamp":1491377495212}
{"id":"scsmbstgrb", "state":"STARTED", "timestamp":1491377495213}
{"id":"scsmbstgrc", "state":"FINISHED", "timestamp":1491377495218}
{"id":"scsmbstgra", "state":"FINISHED", "type":"APPLICATION_LOG", "host":"12345", "timestamp":1491377495217}
{"id":"scsmbstgrc", "state":"STARTED", "timestamp":1491377495210}
{"id":"scsmbstgrb", "state":"FINISHED", "timestamp":1491377495216}
...


## Useful commands

`gradlew bootRun --args src/main/resources/example.log` - compile and run the application for user specified file

`gradlew bootRun --args sample` - compile and run the application for sample data

`gradlew test` - run tests 
