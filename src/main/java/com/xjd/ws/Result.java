package com.xjd.ws;

import java.util.List;
import java.util.Set;

/**
 * 搜索结果
 * 
 * @author elvis.xu
 * @since 2015-4-17 上午11:47:26
 */
public interface Result {

	/**
	 * 以WordResult的方式展现搜索结果, 空List表未搜索到结果
	 * 
	 * @return
	 */
	List<WordResult> wordResults();

	/**
	 * 以RangeResult的方式展现搜索结果, 空List表未搜索到结果
	 * 
	 * @return
	 */
	List<RangeResult> rangeResults();

	/**
	 * 结果以搜索到的Word为键，展示该Word在文中出现的所有索引
	 * 
	 * @author elvis.xu
	 * @since 2015-4-17 下午1:59:48
	 */
	public static interface WordResult {
		Word getWord();

		List<Integer> getIndexes();
	}

	/**
	 * 结果以搜索到的区域范围为键，展示在该区域范围内所有匹配到的Word
	 * 
	 * @author elvis.xu
	 * @since 2015-4-17 下午2:03:23
	 */
	public static interface RangeResult {
		int getBeginIndex();

		int getEndIndex();

		Set<Word> getWords();
	}
}
