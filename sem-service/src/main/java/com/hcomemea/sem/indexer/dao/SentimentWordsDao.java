package com.hcomemea.sem.indexer.dao;

import java.io.IOException;
import java.util.Map;

public interface SentimentWordsDao {
	
	/**
	 * Read sentiments words from positive-words.txt and negative-words.txt.
	 * @return the map of words with their score
	 * @throws IOException
	 */
	public Map<String, Integer> getSentimentWordsMap() throws IOException;
}
