package it.example.app;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.transport.client.PreBuiltTransportClient;


import spark.ModelAndView;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;

/**
 * Hello world demo app!
 *
 */
public class App {
	
    // Define a static logger variable so that it references the
    // Logger instance named "App".
    private static final Logger logger = LogManager.getLogger(App.class);

	public static void main(String[] args) throws UnknownHostException {

		TransportClient client = new PreBuiltTransportClient(Settings.EMPTY).addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));

		Spark.get("/", (request, response) -> {
			
			logger.info("URL requested: / ");
			
			SearchResponse searchResponse = client.prepareSearch("music").setTypes("lyrics").execute().actionGet();

			SearchHit[] hits = searchResponse.getHits().getHits();

			Map<String, Object> attributes = new HashMap<>();

			attributes.put("songs", hits);

			logger.info("Display homepage");
			
			return new ModelAndView(attributes, "index.mustache");

		}, new MustacheTemplateEngine());

		Spark.get("/search",(request, response) -> {
			
			logger.info("URL requested: /search ");

			SearchRequestBuilder srb = client.prepareSearch("music").setTypes("lyrics");

			String lyricParam = request.queryParams("query");

			QueryBuilder lyricQuery = null;

			if (lyricParam != null && lyricParam.trim().length() > 0) {

				lyricQuery = QueryBuilders.matchQuery("lyrics",lyricParam);

			}

			String artistParam = request.queryParams("artist");

			QueryBuilder artistQuery = null;

			if (artistParam != null && artistParam.trim().length() > 0) {

				artistQuery = QueryBuilders.matchQuery("artist", artistParam);
			}

			if (lyricQuery != null && artistQuery == null) {
				
				srb.setQuery(lyricQuery);
				
			} else if (lyricQuery == null && artistQuery != null) {
				
				srb.setQuery(artistQuery);
				
			} else if (lyricQuery != null && artistQuery != null) {
				
				srb.setQuery(artistQuery);
				
			}

			SearchResponse searchResponse = srb.execute().actionGet();

			SearchHit[] hits = searchResponse.getHits().getHits();

			Map<String, Object> attributes = new HashMap<>();

			attributes.put("songs", hits);
			
			logger.info("Display homepage");
			
			return new ModelAndView(attributes, "index.mustache");
			
		}, new MustacheTemplateEngine());

		
		Spark.get("/add", (request, response) -> {
			
			logger.info("URL requested: /add ");
			
			return new ModelAndView(new HashMap(), "add.mustache");

		}, new MustacheTemplateEngine());
		

		Spark.post("/save", (request, response) -> {
			
			logger.info("URL requested: /save ");

			StringBuilder json = new StringBuilder("{");
			json.append("\"name\":\""+request.raw().getParameter("name")+"\",");
			json.append("\"artist\":\""+request.raw().getParameter("artist")+"\",");
			json.append("\"year\":"+request.raw().getParameter("year")+",");
			json.append("\"album\":\""+request.raw().getParameter("album")+"\",");
			json.append("\"lyrics\":\""+request.raw().getParameter("lyrics")+"\"}");

			IndexRequest indexRequest = new IndexRequest("music", "lyrics", new BigInteger(UUID.randomUUID().toString().replace("-", ""), 16).toString());
			indexRequest.source(json.toString());
			IndexResponse esResponse = client.index(indexRequest).actionGet();

			Map<String, Object> attributes = new HashMap<>();
			
			logger.info("Attributes save: "+attributes.toString());

			return new ModelAndView(attributes, "index.mustache");

		}, new MustacheTemplateEngine());

	}//end main

}//end class
