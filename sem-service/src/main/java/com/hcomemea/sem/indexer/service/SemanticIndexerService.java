package com.hcomemea.sem.indexer.service;

public interface SemanticIndexerService {

	/**
	 * Retrieve all english adjectives from WordNet.
	 * Get their score in the semantic repository for it or its synonym.
	 * Write them in adjectives.properties
	 */
	public void generateSemanticAdjectives();

	/**
	 * Retrieve all english verbs from WordNet.
	 * Get their score in the semantic repository for it or its synonym.
	 * Write them in verbs.properties
	 */
	public void generateSemanticVerbs();
}
