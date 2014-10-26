package com.hcomemea.sem.dictionnary;

import java.util.Map;

import com.hcomemea.sem.domain.WordType;

public interface WordDao {

	/**
	 * Write semantic words in C:/SEM/temp/{wordType}.properties alphabetically ordered
	 * @param wordType the type of word
	 * @param words the list of words with their score
	 */
	void writeWords(WordType wordType, Map<String, String> words);

	/**
	 * Read the semantics words
	 * @param wordType the type of word
	 * @return semantic words map with their score
	 */
	Map<String, String> getWords(WordType wordType);
}
