package com.qkwl.common.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
 
import org.modelmapper.ModelMapper;

/** Java对象自动映射
 * @author ZKF
 *  
 */
public class ModelMapperUtils {
	/**
	 * 列表数据映射
	 * @Title: mapper 
	 * @Description: 列表数据映射，类中的字段必须相同
	 * @param srcList 源列表
	 * @param clazz 目标列表类型
	 * @return
	 * @return List<T>    
	 * @throws
	 */
    public static<S,T> List<T> mapper(List<S> srcList,Class<T> clazz){
    	if(srcList==null){
    		return null;
    	}
    	ModelMapper modelMapper = new ModelMapper(); 
    	List<T> tarList=new ArrayList<T>(); 
    	//DozerBeanMapper mapper = new DozerBeanMapper();    
    	//List<String> mappers = new ArrayList<String>();  
    	//mapper.setMappingFiles(mappers);
    	for(S src:srcList){
    		T t=modelMapper.map(src, clazz);
    		//T t=mapper.map(src, clazz);
    		tarList.add(t);
    	}
    	return tarList;
    }
    
    public static<S,T> Collection<T> mapper(Collection<S> srcList,Class<T> clazz){
    	if(srcList==null){
    		return null;
    	}
    	ModelMapper modelMapper = new ModelMapper(); 
    	Collection<T> tarList=new ArrayList<T>(); 
    	for(S src:srcList){
    		T t=modelMapper.map(src, clazz);
    		tarList.add(t);
    	}
    	return tarList;
    }
    
    /**
     * 实体映射转换
     * @Title: mapper 
     * @Description: 实体映射转换，类中的字段必须相同
     * @param src 源实体
     * @param clazz 目标实体
     * @return
     * @return T    
     * @throws
     */
    public static<S,T> T mapper(S src,Class<T> clazz){
    	if(src == null)
    		return null;
    	ModelMapper modelMapper = new ModelMapper();
    	T t=modelMapper.map(src, clazz);
    	return t;
    }
    
    
}
