package com.xjd.ws.impl;

import java.util.Map;

public class WordsTree {

	protected boolean word = false;

	protected int level;

	protected Object attachment;

	protected Map<Character, WordsTree> body;

	/** @return the word */
	public boolean isWord() {
		return word;
	}

	/** @param word the word to set */
	public void setWord(boolean word) {
		this.word = word;
	}

	/** @return the level */
	public int getLevel() {
		return level;
	}

	/** @param level the level to set */
	public void setLevel(int level) {
		this.level = level;
	}

	/** @return the attachment */
	public Object getAttachment() {
		return attachment;
	}

	/** @param attachment the attachment to set */
	public void setAttachment(Object attachment) {
		this.attachment = attachment;
	}

	/** @return the body */
	public Map<Character, WordsTree> getBody() {
		return body;
	}

	/** @param body the body to set */
	public void setBody(Map<Character, WordsTree> body) {
		this.body = body;
	}

}
