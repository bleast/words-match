# words-match
This project aims to provide the facility to easily match all/any words(may be some sensitive words) from a long text.

### Usage
```java

		// 创建匹配器
		WordsMatcher wordsMatcher = new DefaultWordsMatcher();
		// 添加要匹配的关键字
		wordsMatcher.addWord("阿里", "巴阿", "阿里巴巴", "哈哈");

		// 进行匹配，可以有多种模式选择，详见WordsMatcher
		Result result = wordsMatcher.match("好阿里巴巴阿里巴巴哈哈哈哈人");

		// 以匹配的文字为单位展现结果，方便统计关键字
		List<Result.WordResult> wordResults = result.wordResults();
		for (Result.WordResult wordResult : wordResults) {
			System.out.println(wordResult.getWord().getWord() + ":"
					+ Arrays.toString(wordResult.getIndexes().toArray()));
		}

		// 以匹配的范围为单位展现结果，方便处理文本
		List<Result.RangeResult> rangeResults = result.rangeResults();
		for (Result.RangeResult rangeResult : rangeResults) {
			System.out.println(rangeResult.getBeginIndex() + "~" + rangeResult.getEndIndex() + ":");
			for (Word word : rangeResult.getWords()) {
				System.out.println("	" + word.getWord());
			}
		}
```

更多功能和模式请查看 WordsMatcher.java