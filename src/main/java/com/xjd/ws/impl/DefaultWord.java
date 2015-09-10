package com.xjd.ws.impl;

import com.xjd.ws.Word;

public class DefaultWord implements Word {
	protected String word;
	protected int level;
	protected Object attachment;

	/** @return the word */
	public String getWord() {
		return word;
	}

	/** @param word the word to set */
	public void setWord(String word) {
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

	@Override
	public int hashCode() {
		return word.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o instanceof Word) {
			return word.equals(((Word) o).getWord());
		}
		return false;
	}
}
