# elastic-search-demo-app
An ELK-stack search demo app.

I want to send logs from a java app to ElasticSearch stack, and the conventional approach seems to be to set up logstash on the server running the app, and have logstash parse the log files and load them into ElasticSearch.
Instead I want to uselog4j-json-layout everywhere, so I can have log4j's regular file appenders produce JSON logs that don't require any further parsing by logstash.
It seems crazy to have deal with grok filters to deal with multiline stack traces problem for example (and burn CPU cycles on log parsing).

![image](https://github.com/antoniopaolacci/elastic-search-demo-app/blob/master/elk-stack.png)

# Install ELK Stack #

Prerequisites:
- Install java 8 

# Install Elastic Search Server #

 Download and install the public signing key
 ```
 rpm --import https://artifacts.elastic.co/GPG-KEY-elasticsearch
 echo '[elasticsearch-6.x]
		name=Elasticsearch repository for 6.x packages
		baseurl=https://artifacts.elastic.co/packages/6.x/yum
		gpgcheck=1
		gpgkey=https://artifacts.elastic.co/GPG-KEY-elasticsearch
		enabled=1
		autorefresh=1
		type=rpm-md ' | sudo tee /etc/yum.repos.d/elesticsearch.repos
 ```
 ```
 yum install elasticsearch
 ```
 
 Configure elasticsearch yml file
 ```
 vi /etc/elasticsearch/elasticsearch.yml
 ```
 
 Find network.host: 192.168.0.1 and decomment/change it with localhost
 
 Execute the start of elasticsearch
 ```
 systemctl start elasticsearch
 ```
 Enable the service
 ```
 systemctl enable elasticsearch
 ```
# Install Kibana #
  ```
  vi /ect/yum.repos.d/kibana.repos
  
 [kibana-6.x]
 name=Kibana repository for 6.x packages
 baseurl=https://artifacts.elastic.co/packages/6.x/yum
 gpgcheck=1
 gpgkey=https://artifacts.elastic.co/GPG-KEY-elasticsearch
 enabled=1
 autorefresh=1
 type=rpm-md
 ```
 ```
 yum install kibana
 ```
 
 Find <i>server.host: "0.0.0.0"</i> and decomment/change it with <i>localhost</i>
 
 Execute the start of kibana
 ```
 systemctl start kibana
 ```
 
 Enable the service
 
 ```
 systemctl enable kibana
 ```

# Install NGINX as reverse proxy, to serve Kibana console content to the internet #

  Install the epel release repository, nginx and httpd-tools
  ```
  yum -y install epel-release
  yum -y install nginx
  yum -y install httpd-tools
  ```
  
  Configure basic authentication with password to access elastic and kibana server
  
  ```
  htpasswd -c /etc/nginx/.htpasswd.users kibanaadmin
  ```
  
  Configure nginx as reverse-proxy
  ```
  vi /etc/nginx/nginx.conf 
  ```
  
  Find server directive and delete it 
  
  Configure conf nginx
  
  ```
  vi /etc/nginx/conf.d/kibana.conf
  
	server {
	  listen 80;
	  listen [::]:80;

	  server_name example.com;

	  auth_basic "Restricted Access";
	  auth_basic_user_file /etc/nginx/htpasswd.users;
	  
	  location / {
		  proxy_pass http://localhost:5601/;
		  proxy_http_version 1.1;
		  proxy_set_header Upgrade $http_upgrade;
		  proxy_set_header Connection 'upgrade';
		  proxy_set_header Host $host;
		  proxy_cache_bypass $http_upgrade;
	  }
	}
  ```
  
  Execute the start of nginx
  ```
  systemctl start nginx
  ```
  
  Enable the service
  ```
  systemctl enable nginx
  ```
  
# Install logstash #

  ```
  Create the repository file of logstash
  vi /etc/yum.repos.d/logstash.repos
  
  [logstash-6.x]
  name=Elastic repository for 6.x packages
  baseurl=https://artifacts.elastic.co/packages/6.x/yum
  gpgcheck=1
  gpgkey=https://artifacts.elastic.co/GPG-KEY-elasticsearch
  enabled=1
  autorefresh=1
  type=rpm-md
  ```
  
  ```
  yum install logstash
  ```
  
 Send out application logs to the Elastic Stack (ELK). 
 You need to encode log from log4j2 into a JSON format, which is easier to use with Logstash.
 To make use of this encoder, we need to add the following dependency, compatible with log4j2, to our pom.xml:
 
 ```
		 <dependency>
			<groupId>com.vlkan.log4j2</groupId>
			<artifactId>log4j2-logstash-layout</artifactId>
			<version>0.15</version>
		</dependency>

	...
	<Appenders>
	...
		<File name="MyJson" fileName="log/json.log" immediateFlush="true">
			<LogstashLayout dateTimeFormatPattern="yyyy-MM-dd'T'HH:mm:ss.SSSZZZ"
							templateUri="classpath:LogstashJsonEventLayoutV1.json"
							prettyPrintEnabled="false" 
							locationInfoEnabled="true" />
		</File>
	</Appenders>
	<Loggers>
		<Logger name="it.example" level="info">
	      <AppenderRef ref="MyJson"/>
		  ...
	    </Logger>
	</Loggers>
	...
 ```
 
 We need to configure Logstash to read data from log files created by our app and send it to ElasticSearch and visualize on Kibana.
 ```
	input {
	  file {
		path => "E:/git-repo/elastic-search-demo-app/log/json.log"
	  }
	}

	output {
	   
	  stdout {
		codec => rubydebug
	  }
	 
	  # Sending properly parsed log events to elasticsearch
	  elasticsearch {
		hosts => ["localhost:9200"]
	  }
	}
  ```
 
 Input file is used as Logstash will read logs this time from logging files.
 Path is set to our logging directory and all files with .log extension will be processed.
 
 Restart the service logstash
 ```
 systemctl restart logstash
 ```
 
 Enable the service
 ```
 systemctl enable logstash
 ```
 
 To run Logstash on Windows/Unix with -f option and the new configuration file .conf, for example we’ll use:
 ```
 E:\logstash-6.5.3\logstash-6.5.3\bin\logstash.bat -f E:\logstash-6.5.3\logstash-6.5.3\config\elastic-search-demo-app.conf
 
 E:\logstash-6.5.3\logstash-6.5.3\bin\logstash.bat -f E:\logstash-6.5.3\logstash-6.5.3\config\elk-example-spring-boot.conf
 ```
 
 Verify default logstash-* index 
 
 ![image](https://github.com/antoniopaolacci/elastic-search-demo-app/blob/master/logstash.jpg)