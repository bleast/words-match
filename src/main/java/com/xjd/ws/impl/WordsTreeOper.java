package com.xjd.ws.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class WordsTreeOper {

	protected WordsTree rootTree = new WordsTree();
	protected ReadWriteLock lock = new ReentrantReadWriteLock();

	public void addWord(String word, int level, Object attachment) {
		if (word == null || word.length() == 0) {
			return;
		}
		lock.writeLock().lock();
		try {
			doAddWord(rootTree, 0, word.toCharArray(), level, attachment);
		} finally {
			lock.writeLock().unlock();
		}
	}

	protected void doAddWord(WordsTree tree, int index, char[] word, int level, Object attachment) {
		char w = word[index];
		WordsTree wTree = null;

		Map<Character, WordsTree> body = tree.getBody();
		boolean newBody = false;
		if (body == null) {
			body = new HashMap<Character, WordsTree>();
			tree.setBody(body);
			newBody = true;
		}

		if (!newBody) {
			wTree = body.get(w);
		}
		if (wTree == null) {
			wTree = new WordsTree();
			body.put(w, wTree);
		}

		++index;
		if (index >= word.length) {
			wTree.setWord(true);
			wTree.setLevel(level);
			wTree.setAttachment(attachment);
		} else {
			doAddWord(wTree, index, word, level, attachment);
		}
	}

	public void search(int index, char[] text, MatchListener matchListener) {
		if (text == null || text.length == 0 || matchListener == null || index >= text.length || index < 0) {
			return;
		}
		lock.readLock().lock();
		try {
			doSearch(rootTree, "", index, text, matchListener);
		} finally {
			lock.readLock().unlock();
		}
	}

	protected void doSearch(WordsTree tree, String prefix, int index, char[] text, MatchListener matchListener) {
		Map<Character, WordsTree> body = tree.getBody();
		if (body == null) {
			return;
		}

		char w = text[index];
		WordsTree wTree = body.get(w);
		if (wTree == null) {
			return;
		}

		prefix += w;

		if (wTree.isWord()) {
			boolean rt = matchListener.matched(prefix, wTree.getLevel(), wTree.getAttachment());
			if (!rt) {
				return;
			}
		}

		++index;
		if (index >= text.length) {
			return;
		}

		doSearch(wTree, prefix, index, text, matchListener);
	}

	public static interface MatchListener {
		boolean matched(String word, int level, Object attachment);
	}

	public boolean contains(String word) {
		if (word == null ||  word.length() == 0) {
			return false;
		}

		final int wordLen = word.length();
		final boolean[] found = new boolean[] { false };
		search(0, word.toCharArray(), new MatchListener() {
			@Override
			public boolean matched(String mword, int level, Object attachment) {
				if (mword.length() == wordLen) {
					found[0] = true;
					return false;
				}
				return true;
			}
		});
		return found[0];
	}

}
