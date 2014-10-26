package com.hcomemea.sem.service;

import java.util.List;

public interface SentenceService {

	/**
	 * Split sentence by subject
	 * @param text the text
	 * @return the list of subjects
	 */
	public List<String> splitSentenceBySubject(String text);

	/**
	 * Format sentence to be analysis friendly
	 * @param sentence the sentence
	 * @return the formatted sentence.
	 */
	public String formatSentence(String sentence);

}
