package com.qkwl.common.dto.common;

public class PageHelper {
	
	/**
	 * 分页HTML
	 * @param total总页数
	 * @param currentPage当前页数
	 * @param path跳转路径
	 * @return StringHTML
	 */
	public static String generatePagin(int total, int currentPage, String path) {
		if (total <= 0) {
			return "";
		}

		StringBuffer sb = new StringBuffer();
				
		if (currentPage == 1) {
			sb.append("<li class='active'><a href='javascript:void(0)'>1</a></li>");
		} else {
			sb.append("<li><a href='" + path + "currentPage=1'>&lt</a></li>");
			sb.append("<li><a href='" + path + "currentPage=1'>1</a></li>");
		}

		if (currentPage == 2) {
			sb.append("<li class='active'><a href='javascript:void(0)'>2</a></li>");
		} else if (total >= 2) {
			sb.append("<li><a href='" + path + "currentPage=2'>2</a></li>");
		}

		if (currentPage >= 7) {
			sb.append("<li><a href='javascript:void(0)'>...</a></li>");
		}

		// 前三页
		int begin = currentPage - 3;
		begin = begin <= 2 ? 3 : begin;
		for (int i = begin; i < currentPage; i++) {
			sb.append("<li><a href='" + path + "currentPage=" + i + "'>" + i + "</a></li>");
		}

		if (currentPage != 1 && currentPage != 2) {
			sb.append("<li class='active'><a  href='javascript:void(0)'>" + currentPage + "</a></li>");
		}

		// 后三页
		begin = currentPage + 1;
		begin = begin <= 2 ? 3 : begin;
		int end = currentPage + 4;
		if (currentPage < 6) {
			int tInt = 6 - currentPage;
			end = end + ((tInt > 3 ? 3 : tInt));
		}
		for (int i = begin; i < end && i <= total; i++) {
			sb.append("<li><a href='" + path + "currentPage=" + i + "'>" + i + "</a></li>");
		}

		if (total - currentPage > 6) {
			sb.append("<li><a href='javascript:void(0)'>...</a></li>");
		}

		if (total >= 11 && total - currentPage > 4) {
			sb.append("<li><a href='" + path + "currentPage=" + total + "'>" + total + "</a></li>");
		}

		if (currentPage < total) {
			sb.append("<li><a href='" + path + "currentPage=" + total + "'>&gt</a></li>");
		}

		return sb.toString();
	}
	
	/**
	 * 获取总页数
	 * @param totalCount总条数
	 * @param maxResults每页最大条数
	 * @return 总页数
	 */
	public static int totalPage(int totalCount, int maxResults) {
		return totalCount / maxResults + (totalCount % maxResults == 0 ? 0 : 1);
	}
}
