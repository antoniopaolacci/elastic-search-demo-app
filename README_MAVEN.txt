How to MAVEN for build projext:

## Setting maven 3.1 ##
"C:\Program Files\Java\setMVN.cmd" 3

## Setting java 8 ##
"C:\Program Files\Java\setJDK.cmd" 8

## In the workspace directory, create archetype maven ##

mvn archetype:generate -DgroupId=com.dw -DartifactId=elastic-search-demo -DarchetypeArtifactId=maven-archetype-quickstart -DinteractiveMode=false

## Compile ##

mvn eclipse:eclipse install

## Launch Elastic Search server and Kibana Console and LogStash##

..\elasticsearch-6.5.2\elasticsearch-6.5.2\bin\elasticsearch.bat

..\kibana-6.5.2-windows-x86_64\kibana-6.5.2-windows-x86_64\bin\kibana.bat

..\logstash-6.5.3\logstash-6.5.3\bin\logstash.bat -f E:\logstash-6.5.3\logstash-6.5.3\config\elastic-search-demo-app.conf

## Access console ##

http://localhost:5601  Kibana Console

# Access demo app ##

http://localhost:4567  Demo App

