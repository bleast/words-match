package com.xjd.ws.impl;

import java.util.*;

import com.xjd.ws.Result;
import com.xjd.ws.Word;

public class DefaultResult implements Result {

	protected Map<String, DefaultWordResult> resultMap;

	@Override
	public List<WordResult> wordResults() {
		if (resultMap == null) {
			return Collections.emptyList();
		} else {
			return new ArrayList<WordResult>(resultMap.values());
		}
	}

	@Override
	public List<RangeResult> rangeResults() {
		if (resultMap == null) {
			return Collections.emptyList();
		} else {
			List<RangeResult> list = new ArrayList<RangeResult>(resultMap.size());
			for (DefaultWordResult wordResult : resultMap.values()) {
				int wordLength = wordResult.getWord().getWord().length();
				for (int index : wordResult.getIndexes()) {
					boolean found = false;
					for (RangeResult rangeResult : list) {
						if ((rangeResult.getBeginIndex() <= index && index <= rangeResult.getEndIndex())
								|| (rangeResult.getBeginIndex() <= index + wordLength && index + wordLength <= rangeResult
										.getEndIndex())
								|| (index < rangeResult.getBeginIndex() && index + wordLength > rangeResult
										.getEndIndex())) {
							if (index < rangeResult.getBeginIndex()) {
								((DefaultRangeResult) rangeResult).setBeginIndex(index);
							}
							if (index + wordLength > rangeResult.getEndIndex()) {
								((DefaultRangeResult) rangeResult).setEndIndex(index + wordLength);
							}
							rangeResult.getWords().add(wordResult.getWord());
							found = true;
							break;
						}
					}
					if (!found) {
						DefaultRangeResult rangeResult = new DefaultRangeResult();
						rangeResult.setBeginIndex(index);
						rangeResult.setEndIndex(index + wordLength);
						rangeResult.getWords().add(wordResult.getWord());
						list.add(rangeResult);
					}
				}
			}
			return list;
		}
	}

	/** @return the resultMap */
	public Map<String, DefaultWordResult> getResultMap() {
		return resultMap;
	}

	/** @param resultMap the resultMap to set */
	public void setResultMap(Map<String, DefaultWordResult> resultMap) {
		this.resultMap = resultMap;
	}

	public static class DefaultWordResult implements WordResult {

		protected Word word;
		protected List<Integer> indexes;

		/** @param word the word to set */
		public void setWord(Word word) {
			this.word = word;
		}

		/** @param indexes the indexes to set */
		public void setIndexes(List<Integer> indexes) {
			this.indexes = indexes;
		}

		@Override
		public Word getWord() {
			return word;
		}

		@Override
		public List<Integer> getIndexes() {
			return indexes;
		}

	}

	public static class DefaultRangeResult implements RangeResult {

		protected int beginIndex;
		protected int endIndex;
		protected Set<Word> words = new HashSet<Word>();

		@Override
		public int getBeginIndex() {
			return beginIndex;
		}

		public void setBeginIndex(int beginIndex) {
			this.beginIndex = beginIndex;
		}

		@Override
		public int getEndIndex() {
			return endIndex;
		}

		public void setEndIndex(int endIndex) {
			this.endIndex = endIndex;
		}

		@Override
		public Set<Word> getWords() {
			return words;
		}

		public void setWords(Set<Word> words) {
			this.words = words;
		}
	}
}
