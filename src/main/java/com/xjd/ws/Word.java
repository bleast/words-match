package com.xjd.ws;

/**
 * 一个Word用于搜索(匹配)
 * 
 * @author elvis.xu
 * @since 2015-4-17 下午2:04:29
 */
public interface Word {

	/**
	 * Word字符串
	 * 
	 * @return
	 */
	String getWord();

	/**
	 * 该字的权重(等级)
	 * 
	 * @return
	 */
	int getLevel();

	/**
	 * 与该字有关的其它信息
	 * 
	 * @return
	 */
	Object getAttachment();
}
