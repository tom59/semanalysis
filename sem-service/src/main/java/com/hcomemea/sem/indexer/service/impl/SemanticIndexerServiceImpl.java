package com.hcomemea.sem.indexer.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import net.sf.extjwnl.data.POS;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;

import com.hcomemea.sem.indexer.dao.SentimentWordsDao;
import com.hcomemea.sem.dictionnary.WordDao;
import com.hcomemea.sem.domain.WordRelation;
import com.hcomemea.sem.domain.WordType;
import com.hcomemea.sem.indexer.service.SemanticIndexerService;
import com.hcomemea.sem.utils.MathUtils;
import com.hcomemea.sem.dictionnary.DictionaryService;

@Service
public class SemanticIndexerServiceImpl implements SemanticIndexerService {

	@Autowired
	private DictionaryService dictionaryService;

	@Autowired
	private WordDao wordDao;

	private Map<String, Integer> sentimentWords;

	@Autowired
	public SemanticIndexerServiceImpl(SentimentWordsDao sentimentWordsDao) {
		try {
			sentimentWords = sentimentWordsDao.getSentimentWordsMap();
			System.out.println("Found " + sentimentWords.size() + " sentiments words");
		} catch(IOException e) {
			System.err.println("An error occurred while reading sentiment words:" + e.getMessage());
		}
	}

	public static void main(String[] args) {
        ClassPathXmlApplicationContext ctxt = new ClassPathXmlApplicationContext("setup-context.xml");
        SemanticIndexerService semanticIndexerService = ctxt.getBean(SemanticIndexerService.class);
        semanticIndexerService.generateSemanticVerbs();
	}

	@Override
	public void generateSemanticAdjectives() {
		List<String> englishAdjectives = dictionaryService.getAllEnglishAdjectives();
		Map<String, String> words = new TreeMap<>();
		for (String englishAdjective : englishAdjectives) {
			if (POS.ADJECTIVE.equals(dictionaryService.getMostProbablePOS(englishAdjective))) {
				double score = calculateWordScore(POS.ADJECTIVE, englishAdjective);
				if (score != 0) {
					words.put(englishAdjective, String.valueOf(score));
				}
			}
		}
		wordDao.writeWords(WordType.ADJECTIVE, words);
	}

	@Override
	public void generateSemanticVerbs() {
		List<String> englishVerbs = dictionaryService.getAllEnglishVerbs();
		Map<String, String> words = new TreeMap<>();
		for (String englishVerb : englishVerbs) {
			if (POS.VERB.equals(dictionaryService.getMostProbablePOS(englishVerb))) {
				double score = calculateWordScore(POS.VERB, englishVerb);
				if (score != 0) {
					words.put(englishVerb, String.valueOf(score));
				}
			}
		}
		wordDao.writeWords(WordType.VERB, words);
	}

	private double calculateWordScore(POS pos, String adjective) {
		double wordScore = 0;
		//If adjective is not a referenced sentiment word, try to find its score from the synonyms
		if (sentimentWords.get(adjective) == null) {
			Map<WordRelation, List<String>> relationalWords
				= dictionaryService.getRelationalWords(pos, adjective);
			wordScore = calculateAverageScore(sentimentWords, relationalWords);
			if (wordScore != 0) {
				System.out.println(adjective + "=" + wordScore);
			}
		} else {
			wordScore = sentimentWords.get(adjective);
			System.out.println(adjective + "=" + wordScore);
		}
		return wordScore;
	}

	private double calculateAverageScore(Map<String, Integer> sentimentWords, Map<WordRelation, List<String>> words) {
		List<Double> scores = new ArrayList<>();
		List<String> synonyms = words.get(WordRelation.SYNONYM);
		if (synonyms != null) {
			for(String synonym : synonyms) {
				if (sentimentWords.get(synonym) != null) {
					scores.add(Double.valueOf(sentimentWords.get(synonym)));
				}
			}
		}

		//If nothing found on synonym, try with antonym.
		if (scores.size() == 0) {
			//Antonym doesnt work well with weight: eg: acceptable/unacceptable do not have same weight so use only polarity +/-
			List<String> antonyms = words.get(WordRelation.ANTONYM);
			if (antonyms != null) {
				for(String antonym : antonyms) {
					if (sentimentWords.get(antonym) != null) {
						Integer antonymPolarity;
						if(sentimentWords.get(antonym) > 0) {
							antonymPolarity = 1;
						} else {
							antonymPolarity = -1;
						}
						scores.add(Double.valueOf(antonymPolarity * -1));
					}
				}
			}
		}

		return MathUtils.average(scores);
	}
}
