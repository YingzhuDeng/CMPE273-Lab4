package edu.sjsu.cmpe.cache.client;

import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;

import com.mashape.unirest.http.async.Callback;
import com.mashape.unirest.http.exceptions.UnirestException;

public class DistributedCacheService implements CacheServiceInterface {
    private final String cacheServerUrl;
    private String value;

    public static AtomicInteger countOfSuccess = new AtomicInteger();

    public DistributedCacheService(String inputServerUrl) {
        this.cacheServerUrl = inputServerUrl;
    }
    
     
    @Override
    public Future<HttpResponse<JsonNode>> get(long key) {
    	Future<HttpResponse<JsonNode>> serverF = Unirest
    			.get(this.cacheServerUrl + "/cache/{key}")
                .header("accept", "application/json")
                .header("Accept-Content-Encoding", "gzip")
                .routeParam("key", Long.toString(key))
                .asJsonAsync(new Callback<JsonNode>() {	
    	            
		public void failed(UnirestException e) 
		{
    	               System.out.println("Failed to fetch server " + getServerName());
    	        }
    	
    	        public void completed(HttpResponse<JsonNode> response)
		 {
    	               value = response.getBody().getObject().getString("value");
    	               System.out.println("Fetch completed:  " + getServerName());
    	         }
    	
    	        public void cancelled() 
		{
    	               System.out.println("Fetch cancelled:  " + getServerName());
    	        }	
    	        });

        return serverF;
    }
    
    public String getValue() {
    	return this.value;
    }

    
     
    @Override
    public Future<HttpResponse<JsonNode>> put(long key, String value) {
    	Future<HttpResponse<JsonNode>> serverStatus = Unirest
	        .put(this.cacheServerUrl + "/cache/{key}/{value}")
	        .header("accept", "application/json")
	        .routeParam("key", Long.toString(key))
	        .routeParam("value", value)
	        .asJsonAsync(new Callback<JsonNode>() {	
	            public void failed(UnirestException e) 
			{	            	
	                System.out.println("Save failed:  " + getServerName());
	            	}
	
	            public void completed(HttpResponse<JsonNode> response) 
			{
	            	countOfSuccess.incrementAndGet();
	            	System.out.println("Save completed:  " + getServerName());
	            	}
	
	            public void cancelled() 
			{
	                System.out.println("Save cancelled: " + getServerName());
	            	}
	        });
    	return serverStatus;
    }
    
    
    
    @Override
    public Future<HttpResponse<JsonNode>> delete(long key) {
    	Future<HttpResponse<JsonNode>> serverStatus = Unirest
    		.delete(this.cacheServerUrl + "/cache/{key}")
	        .header("accept", "application/json")
	        .routeParam("key", Long.toString(key))
	        .asJsonAsync(new Callback<JsonNode>() {	        	
	            public void failed(UnirestException e) 
			{
	                System.out.println("Delete failed for " + getServerName());
	            	}
	
	            public void completed(HttpResponse<JsonNode> response) 
			{
	            	System.out.println("Delete completed for " + getServerName());
	            	}
	
	            public void cancelled() 
			{
	                System.out.println("Delete cancelled for " + getServerName());
	            	}	
	        });
        return serverStatus;
    }
    
    public String getServerName() {
    	if (this.cacheServerUrl.contains("3000")) {
    		return "Server_A";
    	} else if (this.cacheServerUrl.contains("3001")) {
    		return "Server_B";
    	} else if (this.cacheServerUrl.contains("3002")) {
    		return "Server_C";
    	}	
    	return null;
    }
}
