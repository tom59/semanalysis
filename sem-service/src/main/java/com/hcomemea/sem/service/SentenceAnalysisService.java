package com.hcomemea.sem.service;

import java.util.Map;

public interface SentenceAnalysisService {

	/**
	 * Get the subjects of the sentence with their score.
	 * @param text the sentence
	 * @return the words scored.
	 */
	Map<String, Double> getSummarizedContent(String text);
}
