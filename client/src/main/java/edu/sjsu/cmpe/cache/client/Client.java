package edu.sjsu.cmpe.cache.client;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;


public class Client {
	private static CacheServiceInterface cache_1 = null;
	private static CacheServiceInterface cache_2 = null;
	private static CacheServiceInterface cache_3 = null;
	
        public static void main(String[] args) {
    	try {
    		System.out.println("Starting Cache Client...");
    		
            cache_1 = new DistributedCacheService("http://localhost:3000");
            cache_2 = new DistributedCacheService("http://localhost:3001");
            cache_3 = new DistributedCacheService("http://localhost:3002");
            
	    	if (args.length > 0) 
		{
	    		if (args[0].equals("write")) 
			{
	    			write();
	    		} else if (args[0].equals("read")) 
			{
	    			CRDTClient.readOnRepair(cache_1, cache_2, cache_3);
	    		}
	    	}
	    	
	    	System.out.println("Existing Cache Client...");
    	} catch (Exception e) 
	{
    		e.printStackTrace();
    	}        
    }
   

 
    public static void write() throws Exception {    
   
        long key = 1;
        String value = "a";
        
        Future<HttpResponse<JsonNode>> server_3000 = cache_1.put(key, value);
        Future<HttpResponse<JsonNode>> server_3001 = cache_2.put(key, value);
        Future<HttpResponse<JsonNode>> server_3002 = cache_3.put(key, value);
        
        final CountDownLatch countDown = new CountDownLatch(3);
        
        try {
        	server_3000.get();
        } catch (Exception e) {
        } finally {
        	countDown.countDown();
        }
        
        try {
        	server_3001.get();
        } catch (Exception e) {
        } finally {
        	countDown.countDown();
        }
        
        try {
        	server_3002.get();
        } catch (Exception e) {
        } finally {
        	countDown.countDown();
        }

        countDown.await();
        
        if (DistributedCacheService.countOfSuccess.intValue() < 2) 
	{	        	
        	cache_1.delete(key);
        	cache_2.delete(key);
        	cache_3.delete(key);
        } else
	{
        	cache_1.get(key);
        	cache_2.get(key);
        	cache_3.get(key);
        	Thread.sleep(1000);

            System.out.println("Server A: " + cache_1.getValue());
    	    System.out.println("Server B: " + cache_2.getValue());
    	    System.out.println("Server C: " + cache_3.getValue());
        }

        DistributedCacheService.countOfSuccess = new AtomicInteger();
    }
}
