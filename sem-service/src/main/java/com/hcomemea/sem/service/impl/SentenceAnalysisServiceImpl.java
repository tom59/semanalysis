package com.hcomemea.sem.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.extjwnl.data.POS;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Function;
import com.google.common.collect.Maps;
import com.hcomemea.sem.dictionnary.DictionaryService;
import com.hcomemea.sem.domain.WordType;
import com.hcomemea.sem.dictionnary.WordDao;
import com.hcomemea.sem.service.SentenceAnalysisService;
import com.hcomemea.sem.service.SentenceService;
import com.hcomemea.sem.utils.MathUtils;

@Service
public class SentenceAnalysisServiceImpl implements SentenceAnalysisService {

	private SentenceService sentenceService;

	private DictionaryService dictionaryService;

	private Map<String, String> adjectives;
	private Map<String, String> verbs;

	@Autowired
	public SentenceAnalysisServiceImpl(SentenceService sentenceService, WordDao wordDao,
			DictionaryService dictionaryService) {
		this.sentenceService = sentenceService;
		this.dictionaryService = dictionaryService;
		adjectives = wordDao.getWords(WordType.ADJECTIVE);
		verbs = wordDao.getWords(WordType.VERB);
	}

	public Map<String, Double> getSummarizedContent(String text) {
		Map<String, List<Double>> sumContent = getSummarizedContentAcc(text);
		return Maps.transformValues(sumContent, new Function<List<Double>, Double>() {
			public Double apply(List<Double> numList) {
				return MathUtils.average(numList);
			}
		});
	}

	private Map<String, List<Double>> getSummarizedContentAcc(String text) {
		Map<String, List<Double>> summarizedContent = new HashMap<String, List<Double>>();

		List<String> subjects = sentenceService.splitSentenceBySubject(text);
		for (String subject : subjects) {
			double score = 0;
			subject = sentenceService.formatSentence(subject);
			List<String> nouns = dictionaryService.findNounsInSentence(subject);
			String[] subjectWords = subject.split("\\s");
			float coef = 1;
			for (String subjectWord : subjectWords) {
				if(subjectWord.equals("not")) {
					coef = -1;
				}
				if(subjectWord.equals("very") || subjectWord.equals("really")) {
					coef = coef * 2;
				}
				if (adjectives.get(subjectWord) != null) {
					score = score + Double.valueOf(adjectives.get(subjectWord)) * coef;
				} else {
					String infinitiveVerb = dictionaryService.findInfinitiveVerb(subjectWord);
					if (verbs.get(infinitiveVerb) != null) {
						score = score + Double.valueOf(verbs.get(infinitiveVerb)) * coef;
					}
				}
			}
			for (String noun : nouns) {
				String stemmedWord = dictionaryService.stemWord(noun, POS.NOUN);
				if(summarizedContent.get(stemmedWord) == null) {
					summarizedContent.put(stemmedWord, new ArrayList<Double>());
				}
				summarizedContent.get(stemmedWord).add(score);
			}
		}
		return summarizedContent;
	}

	public void setSentenceService(SentenceService sentenceService) {
		this.sentenceService = sentenceService;
	}

	public void setDictionaryService(DictionaryService dictionaryService) {
		this.dictionaryService = dictionaryService;
	}
}
