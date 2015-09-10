package com.xjd.ws;

import java.util.concurrent.ExecutorService;

/**
 * 搜索主类
 * 
 * @author elvis.xu
 * @since 2015-4-17 上午11:24:45
 */
public interface WordsMatcher {

	/**
	 * <pre>
	 * 跳跃最短匹配优先模式,匹配数少 ,性能消耗小,只能使用单线程
	 * 示例1
	 * 	TEXT: "哈哈哈哈"
	 * 	WORD: "哈哈"
	 * 	匹配: "哈哈" -- 2次;索引0,2;跳跃2
	 * 示例2
	 * 	TEXT: "阿里巴巴阿里巴巴"
	 * 	WORD: "阿里", "巴阿", "阿里巴巴"
	 * 	匹配: "阿里" -- 1次;索引0;跳跃2
	 * 	      "巴阿" -- 1次;索引3;跳跃2
	 * </pre>
	 */
	int MODE_SKIP_SHORT = 1;

	/**
	 * <pre>
	 * 跳跃最长匹配优先模式(多匹配),匹配数少 ,性能消耗小,只能使用单线程
	 * 示例1
	 * 	TEXT: "哈哈哈哈"
	 * 	WORD: "哈哈"
	 * 	匹配: "哈哈" -- 2次;索引0,2;跳跃2
	 * 示例2
	 * 	TEXT: "阿里巴巴阿里巴巴"
	 * 	WORD: "阿里", "巴阿", "阿里巴巴"
	 * 	匹配: "阿里巴巴" -- 2次;索引0,4;跳跃4
	 * 	      "阿里" -- 2次;索引0,4;不用于跳跃
	 * </pre>
	 */
	int MODE_SKIP_LONG = 2;

	/**
	 * <pre>
	 * 逐步最短匹配优先模式,匹配数较多 ,性能消耗较大,可以使用多线程
	 * 示例1
	 * 	TEXT: "哈哈哈哈"
	 * 	WORD: "哈哈"
	 * 	匹配: "哈哈"3次, 索引为0,1,2
	 * 示例2
	 * 	TEXT: "阿里巴巴阿里巴巴"
	 * 	WORD: "阿里", "巴阿", "阿里巴巴"
	 * 	匹配: "阿里" -- 2次;索引0,4;
	 * 	      "巴阿" -- 1次;索引3;
	 * </pre>
	 */
	int MODE_STEP_SHORT = 3;

	/**
	 * <pre>
	 * 逐步最长匹配优先模式,匹配数较多 ,性能消耗较大,可以使用多线程
	 * 示例1
	 * 	TEXT: "哈哈哈哈"
	 * 	WORD: "哈哈"
	 * 	匹配: "哈哈"3次, 索引为0,1,2
	 * 示例2
	 * 	TEXT: "阿里巴巴阿里巴巴"
	 * 	WORD: "阿里", "巴阿", "阿里巴巴"
	 * 	匹配: "阿里" -- 2次;索引0,4;
	 * 	      "巴阿" -- 1次;索引3;
	 * 	      "阿里巴巴" -- 2次;索引0,4;
	 * </pre>
	 */
	int MODE_STEP_LONG = 4;

	/**
	 * 添加搜索关键字，如果存在则更新, level为默认level, attachment为null
	 * 
	 * @param words
	 */
	void addWord(String... words);

	void addWord(String word, int level);

	void addWord(String word, int level, Object attachment);

	void addWord(Word... words);

	void setDefaultLevel(int level);

	boolean containsWord(String word);

	/**
	 * 在给定的文本中搜索(匹配)关键字, 返回搜索结果, 使用默认模式
	 * 
	 * @param text
	 * @return
	 */
	Result match(String text);

	Result match(String text, int mode);

	Result match(String text, int mode, ExecutorService executorService);

	void setDefaultMode(int mode);
}
