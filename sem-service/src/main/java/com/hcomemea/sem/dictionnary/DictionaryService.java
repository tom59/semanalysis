package com.hcomemea.sem.dictionnary;

import java.util.List;
import java.util.Map;

import com.hcomemea.sem.domain.WordRelation;

import net.sf.extjwnl.data.POS;

public interface DictionaryService {

	/**
	 * Print word definition
	 * @param pos the pos
	 * @param word the word
	 */
	void printWord(POS pos, String word);

	/**
	 * Retrieve all english adjectives
	 * @return the adjectives
	 */
	List<String> getAllEnglishAdjectives();

	/**
	 * Retrieve all english adjectives
	 * @return the verbs
	 */
	List<String> getAllEnglishVerbs();

	/**
	 * Retrieve synonym and antonym
	 * @param pos the pos
	 * @param word the word
	 * @return the synonym and antonym
	 */
	Map<WordRelation, List<String>> getRelationalWords(POS pos, String word);

	/**
	 * true if the word is a pos
	 * @param word the word
	 * @param pos the pos
	 * @return true if word is the pos
	 */
	boolean isPos(String word, POS pos);

	/**
	 * Retrieve most probable pos
	 * @param word the word
	 * @return the pos
	 */
	POS getMostProbablePOS(String word);

	/**
	 * Retrieve infinitive of verb.
	 * @param word the word
	 * @return infinitive or null
	 */
	String findInfinitiveVerb(String word);

	/**
	 * Find the nouns in a sentence
	 * @param sentence the sentence
	 * @return the nouns
	 */
	List<String> findNounsInSentence(String sentence);

	/**
	 * Stem a word. "running" will be run, "rooms" will be "room"
	 * @param word the word
	 * @param pos the pos type
	 * @return the stemmed word
	 */
	public String stemWord(String word, POS pos);
}
