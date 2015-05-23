package edu.sjsu.cmpe.cache.client;
import java.util.HashMap;
import java.util.Map;

public class CRDTClient {
    public static void readOnRepair(CacheServiceInterface arg1, CacheServiceInterface arg2, CacheServiceInterface arg3) throws Exception {
    	CacheServiceInterface cache_1  = arg1;
    	CacheServiceInterface cache_2  = arg2;
    	CacheServiceInterface cache_3  = arg3;
    	
        long key = 1;
        String value = "a";
        
        cache_1.put(key, value);
        cache_2.put(key, value);
        cache_3.put(key, value);
        
        System.out.println("Setting value: a");
        Thread.sleep(30000);
        
        cache_1.get(1);
        cache_2.get(1);
	cache_3.get(1);
	        
	System.out.println("Getting value: a");
	Thread.sleep(1000);
	    
	System.out.println("Server A getValue: " + cache_1.getValue());
	System.out.println("Server B getValue: " + cache_2.getValue());
	System.out.println("Server C getValue: " + cache_3.getValue());
        
        value = "b";
        cache_1.put(key, value);
        cache_2.put(key, value);
        cache_3.put(key, value);

        System.out.println("Set value: b");
        Thread.sleep(30000);
	        
	    cache_1.get(1);
	    cache_2.get(1);
	    cache_3.get(1);
	        
	    System.out.println("Getting value: b");
	    Thread.sleep(1000);
	    
	    System.out.println("Server A getValue: " + cache_1.getValue());
	    System.out.println("Server B getValue: " + cache_2.getValue());
	    System.out.println("Server C getValue: " + cache_3.getValue());
	    String[] values = {cache_1.getValue(), cache_2.getValue(), cache_3.getValue()};
	    
	    Map<String, Integer> map = new HashMap<String, Integer>();
	    String majorityValue = null;
	    for (String eachValue : values) {
	        Integer countValue = map.get(eachValue);
	        map.put(eachValue, countValue != null ? countValue+1 : 1);
	        if (map.get(eachValue) > values.length / 2) {
	        	majorityValue = eachValue;
	        	break;
	        }	
	    }
	    
	cache_1.put(key, majorityValue);
        cache_2.put(key, majorityValue);
        cache_3.put(key, majorityValue);
        
        System.out.println("Repair value: b");
	Thread.sleep(1000);
	    
	cache_1.get(key);
        cache_2.get(key);
        cache_3.get(key);
        
        System.out.println("Get value b after repair: ");
	Thread.sleep(1000);
	    
	    System.out.println("Server A getValue: " + cache_1.getValue());
	    System.out.println("Server B getValue: " + cache_2.getValue());
	    System.out.println("Server C getValue: " + cache_3.getValue());
    }
}
