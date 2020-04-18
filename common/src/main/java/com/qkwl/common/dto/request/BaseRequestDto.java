package com.qkwl.common.dto.request;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 通用请求实体基类
 * @author ZKF
 */
public class BaseRequestDto extends BaseDto {

	private static final long serialVersionUID = 1L;
	
	/**
	 * 偏移量
	 */
	// @JsonIgnore
	private int offSet;
	/**
	 * 页面大小
	 */
	// @JsonIgnore
	private int limit = 10;
	/**
	 * 搜索关键字
	 */
	private String key;
	
	/**
	 * 开始时间 (结束时间的前6天，也就是默认一周)
	 */
	private Date startDateTime = new Date(System.currentTimeMillis() - 0x5265c00L * 6);
	/**
	 * 结束时间
	 */
	private Date endDateTime = new Date();
	/**
	 * 排序字段名称
	 */
	private String sort;
	/**
	 * 排序方法
	 */
	private String order;

	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public int getOffSet() {
		return offSet;
	}

	public void setOffSet(int offSet) {
		this.offSet = offSet;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	// @JsonIgnore
	public int getCurrentPage() {
		return (offSet / limit) + 1;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Date getStartDateTime() {
		if (startDateTime == null)
			return null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String dt = sdf.format(startDateTime);
		dt = dt + " 00:00:00";
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			return sdf2.parse(dt);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}

	public void setStartDateTime(Date startDateTime) {
		this.startDateTime = startDateTime;
	}

	public Date getEndDateTime() {
		if (endDateTime == null)
			return null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String dt = sdf.format(endDateTime);
		dt = dt + " 23:59:59";
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			return sdf2.parse(dt);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}

	public void setEndDateTime(Date endDateTime) {
		this.endDateTime = endDateTime;
	}

	public static void main(String[] args) {
		BaseRequestDto dto = new BaseRequestDto();
		System.out.println(dto.getStartDateTime());
		System.out.println(dto.getEndDateTime());
	}

}
