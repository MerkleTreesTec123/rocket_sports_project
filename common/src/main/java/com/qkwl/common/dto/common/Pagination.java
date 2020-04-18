package com.qkwl.common.dto.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * 分页工具类
 * @author ZKF
 */
@JsonInclude(Include.NON_NULL)
public class Pagination<T> implements Serializable {

	private static final long serialVersionUID = 1L;

	/** 总行数 */
	private int totalRows;

	/** 每页显示的行数 */
	private int pageSize;

	/** 当前页号 */
	private int currentPage;

	/** 总页数 */
	private int totalPages;

	/** 偏移数 */
	@JsonIgnore
	private int offset;

	private Collection<T> data;

	private Object rows;
	/** 表单总计 */
	private String totalPrice;
	/* 跳转页面 */
	private String redirectUrl;

	private String pagin;

	private String begindate;

	private String enddate;

	private String orderField;

	private String orderDirection;

	private String keyword;

	private Map<String, Object> param;

	public Pagination() {
	}

	public Pagination(int currentPage, int pageSize) {
		this.pageSize = pageSize;
		if(currentPage < 1){
			currentPage = 1;
		}
		this.currentPage = currentPage;
		// 偏移量
		this.offset = (this.currentPage - 1) * pageSize;
	}

	public Pagination(int currentPage, int pageSize, String redirectUrl) {
		this.pageSize = pageSize;
		if(currentPage < 1){
			currentPage = 1;
		}
		this.currentPage = currentPage;
		this.redirectUrl = redirectUrl;
		// 偏移量
		this.offset = (this.currentPage - 1) * pageSize;
	}

	public Pagination(int currentPage, int pageSize, String begindate, String enddate, String redirectUrl) {
		this.pageSize = pageSize;
		if(currentPage < 1){
			currentPage = 1;
		}
		this.currentPage = currentPage;
		this.redirectUrl = redirectUrl;
		this.begindate = begindate;
		this.enddate = enddate;
		// 偏移量
		this.offset = (this.currentPage - 1) * pageSize;
	}

	public int getTotalRows() {
		return totalRows;
	}

	public void setTotalRows(int totalRows) {
		if(pageSize<=0){
			return;
		}
		this.totalPages = ((totalRows - 1) / pageSize) + 1;
		this.totalRows = totalRows;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(int currentPage) {
		if(currentPage < 1){
			currentPage = 1;
		}
		this.currentPage = currentPage;
		// 偏移量
		this.offset = (this.currentPage - 1) * pageSize;
	}

	public int getTotalPages() {
		return totalPages;
	}

	public void setTotalPages(int totalPages) {
		this.totalPages = totalPages;
	}

	public Collection<T> getData() {
		if(data == null){
			return new ArrayList<T>();
		}
		return data;
	}

	public void setData(Collection<T> data) {
		this.data = data;
	}

	public int getOffset() {
		return offset;
	}

	public Object getRows() {
		return rows;
	}

	public void setRows(Object rows) {
		this.rows = rows;
	}

	public String getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(String totalPrice) {
		this.totalPrice = totalPrice;
	}

	public String getRedirectUrl() {
		return redirectUrl;
	}

	public void setRedirectUrl(String redirectUrl) {
		this.redirectUrl = redirectUrl;
	}

	/**
	 * 生成页面分页数据
	 */
	public void generate() {
		if (getRedirectUrl() == null) {
			pagin = "";
			return;
		}
		if(getTotalRows()>getPageSize()){
			pagin = PageHelper.generatePagin(getTotalRows() / getPageSize() + (getTotalRows() % getPageSize() == 0 ? 0 : 1), getCurrentPage(), getRedirectUrl());
		}
	}

	public String getPagin() {
		return pagin;
	}

	public void setPagin(String pagin) {
		this.pagin = pagin;
	}

	public String getBegindate() {
		return begindate;
	}

	public void setBegindate(String begindate) {
		this.begindate = begindate;
	}

	public String getEnddate() {
		return enddate;
	}

	public void setEnddate(String enddate) {
		this.enddate = enddate;
	}

	public String getOrderField() {
		return orderField;
	}

	public void setOrderField(String orderField) {
		this.orderField = orderField;
	}

	public String getOrderDirection() {
		return orderDirection;
	}

	public void setOrderDirection(String orderDirection) {
		this.orderDirection = orderDirection;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public Map<String, Object> getParam() {
		return param;
	}

	public void setParam(Map<String, Object> param) {
		this.param = param;
	}

}