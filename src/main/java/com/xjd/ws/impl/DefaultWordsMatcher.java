package com.xjd.ws.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import com.xjd.ws.Result;
import com.xjd.ws.Word;
import com.xjd.ws.WordsMatcher;
import com.xjd.ws.impl.DefaultResult.DefaultWordResult;
import com.xjd.ws.impl.WordsTreeOper.MatchListener;

public class DefaultWordsMatcher implements WordsMatcher {

	protected WordsTreeOper wordsTreeOper = new WordsTreeOper();
	protected volatile int defaultLevel = 0;
	protected volatile int defaultMode = MODE_STEP_LONG;

	@Override
	public void addWord(String... words) {
		if (words != null) {
			for (String word : words) {
				addWord(word, defaultLevel, defaultMode);
			}
		}
	}

	@Override
	public void addWord(String word, int level) {
		addWord(word, level, defaultMode);
	}

	@Override
	public void addWord(String word, int level, Object attachment) {
		wordsTreeOper.addWord(word, defaultLevel, defaultMode);
	}

	@Override
	public void addWord(Word... words) {
		if (words != null) {
			for (Word word : words) {
				if (word == null) {
					continue;
				}
				addWord(word.getWord(), word.getLevel(), word.getAttachment());
			}
		}
	}

	@Override
	public void setDefaultLevel(int level) {
		defaultLevel = level;
	}

	@Override
	public boolean containsWord(String word) {
		return wordsTreeOper.contains(word);
	}

	@Override
	public void setDefaultMode(int mode) {
		defaultMode = mode;
	}

	@Override
	public Result match(String text) {
		return match(text, defaultMode);
	}

	@Override
	public Result match(String text, int mode) {
		return match(text, mode, null);
	}

	@Override
	public Result match(String text, int mode, ExecutorService executorService) {
		if (text == null || text.length() == 0) {
			return new DefaultResult();
		}

		if (mode == MODE_SKIP_SHORT || mode == MODE_SKIP_LONG) {
			return doSearchSkip(text, mode == MODE_SKIP_SHORT);
		} else if (mode == MODE_STEP_SHORT || mode == MODE_STEP_LONG) {
			return doSearchStep(text, mode == MODE_STEP_SHORT, executorService);
		} else {
			throw new RuntimeException("cannot recogonize mode[" + mode + "]!");
		}
	}

	protected Result doSearchSkip(String text, final boolean isShort) {
		final DefaultResult defaultResult = new DefaultResult();
		defaultResult.setResultMap(new HashMap<String, DefaultResult.DefaultWordResult>());
		final int[] index = new int[] { 0, 0 };
		final char[] textArray = text.toCharArray();
		MatchListener matchListener = new MatchListener() {
			@Override
			public boolean matched(String word, int level, Object attachment) {
				DefaultWordResult wordResult = defaultResult.getResultMap().get(word);
				if (wordResult == null) {
					DefaultWord defaultWord = new DefaultWord();
					defaultWord.setWord(word);
					defaultWord.setLevel(level);
					defaultWord.setAttachment(attachment);
					wordResult = new DefaultWordResult();
					wordResult.setWord(defaultWord);
					wordResult.setIndexes(new LinkedList<Integer>());
					defaultResult.getResultMap().put(word, wordResult);
				}
				wordResult.getIndexes().add(index[0]);
				if (index[1] < word.length()) {
					index[1] = word.length();
				}
				return !isShort;
			}
		};
		while (index[0] < textArray.length) {
			index[1] = 1;
			wordsTreeOper.search(index[0], textArray, matchListener);
			index[0] += index[1];
		}
		return defaultResult;
	}

	protected Result doSearchStep(String text, final boolean isShort, final ExecutorService executorService) {
		final DefaultResult defaultResult = new DefaultResult();
		if (executorService == null) {
			defaultResult.setResultMap(new HashMap<String, DefaultResult.DefaultWordResult>());
		} else {
			defaultResult.setResultMap(new ConcurrentHashMap<String, DefaultResult.DefaultWordResult>());
		}
		final int[] index = new int[] { 0 };

		StepMatchListener matchListener = null;
		if (executorService == null) {
			matchListener = new StepMatchListener();
			matchListener.defaultResult = defaultResult;
			matchListener.executorService = executorService;
			matchListener.isShort = isShort;
			matchListener.index = index;
		}

		List<Future> futures = null;
		if (executorService != null) {
			futures = new LinkedList<Future>();
		}

		final char[] textArray = text.toCharArray();
		while (index[0] < textArray.length) {
			if (executorService == null) {
				wordsTreeOper.search(index[0], textArray, matchListener);
			} else {
				Future<?> future = executorService.submit(new Runnable() {
					@Override
					public void run() {
						StepMatchListener matchListener = new StepMatchListener();
						matchListener.defaultResult = defaultResult;
						matchListener.executorService = executorService;
						matchListener.isShort = isShort;
						matchListener.index = new int[] { index[0] };
						wordsTreeOper.search(index[0], textArray, matchListener);
					}
				});
				futures.add(future);
			}
			++index[0];
		}

		if (futures != null) {
			for (Future<?> future : futures) {
				try {
					future.get();
				} catch (InterruptedException e) {
					// continue;
				} catch (ExecutionException e) {
					for (Future<?> future2 : futures) {
						future2.cancel(true);
					}
					throw new RuntimeException(e);
				}
			}
		}

		return defaultResult;
	}

	public class StepMatchListener implements MatchListener {
		DefaultResult defaultResult;
		ExecutorService executorService;
		int[] index;
		boolean isShort;

		@Override
		public boolean matched(String word, int level, Object attachment) {
			Map<String, DefaultWordResult> resultMap = defaultResult.getResultMap();
			DefaultWordResult wordResult = resultMap.get(word);
			if (wordResult == null) {
				DefaultWord defaultWord = new DefaultWord();
				defaultWord.setWord(word);
				defaultWord.setLevel(level);
				defaultWord.setAttachment(attachment);
				wordResult = new DefaultWordResult();
				wordResult.setWord(defaultWord);
				if (executorService == null) {
					wordResult.setIndexes(new LinkedList<Integer>());
				} else {
					wordResult.setIndexes(Collections.synchronizedList(new LinkedList<Integer>()));
				}
				if (resultMap instanceof ConcurrentHashMap<?, ?>) {
					wordResult = (DefaultWordResult) ((ConcurrentHashMap) resultMap).putIfAbsent(word, wordResult);
				} else {
					resultMap.put(word, wordResult);
				}
			}
			wordResult.getIndexes().add(index[0]);
			return !isShort;
		}
	}
}
