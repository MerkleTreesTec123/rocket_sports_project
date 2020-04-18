package com.qkwl.common.redis;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.codec.binary.Base64;



public class RedisDataUtils {
	
	
	public static <T extends Serializable> String serializationObject(T obj) {
		byte[] b = null;
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
			GZIPOutputStream gzipos = new GZIPOutputStream(baos);
			ObjectOutputStream oos = new ObjectOutputStream(gzipos);
			oos.writeObject(obj);
			gzipos.finish();
			
	        b = baos.toByteArray();
       
	        oos.flush();
            oos.close();
            gzipos.flush();
            gzipos.close();
            baos.flush();
            baos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
 
        return new String(new Base64().encode(b));
    }
	
	
	@SuppressWarnings("unchecked")
    public static <T extends Serializable> T deserializationObject(String obj,
            Class<T> clazz) {
		 
        ByteArrayInputStream bais = new ByteArrayInputStream(new Base64().decode(obj));
        T t = null;
        try {
     	    GZIPInputStream gzipis = new GZIPInputStream(bais);
     	    ObjectInputStream ois = new ObjectInputStream(gzipis);
     	    t = (T) ois.readObject();
     	    ois.close();
     	    gzipis.close(); // required !
		} catch (Exception e) {
			e.printStackTrace();
		}
       
	    return t;
    }

}
