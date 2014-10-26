package com.hcomemea.sem.service.impl;

import static net.sf.extjwnl.data.POS.NOUN;
import static net.sf.extjwnl.data.POS.VERB;
import static net.sf.extjwnl.data.POS.ADJECTIVE;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.extjwnl.data.POS;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hcomemea.sem.dictionnary.DictionaryService;
import com.hcomemea.sem.service.SentenceService;

@Service
public class SentenceServiceImpl implements SentenceService {

	private DictionaryService dictionaryService;

	List<String> stopWords;

	@Autowired
	public SentenceServiceImpl(DictionaryService dictionaryService) {
		this.dictionaryService = dictionaryService;
	}

	@Override
	public String formatSentence(String sentence) {
		sentence = sentence.toLowerCase();
		//Replace all ',' by 'and' for clarity
		sentence = sentence.replace(",", " and ");
		//1 space max
		sentence = sentence.replace("  ", " ");
		//Replace all 'n't ' by not
		sentence = sentence.replace("n't ", " not ");
		//Replace all ' nt ' by not
		sentence = sentence.replace(" nt ", " not ");
		//Replace all verb ending 'nt' by not
		String[] words = sentence.split("\\s");
		for (String word : words) {
			if (word.endsWith("nt")) {
				String theWord = word.substring(0, word.length() - 2);
				if (dictionaryService.findInfinitiveVerb(theWord) != null) {
					sentence = sentence.replace(" " + theWord + "nt ", " " + theWord + " not ");
				}
			}
		}
		sentence = sentence.replace("n't ", " not ");

		return sentence;
	}

	@Override
	public List<String> splitSentenceBySubject(String text) {
		List<String> subjects = new ArrayList<>();
		String[] sentences = text.split("\\.|\\!|\\?");
		for (String sentence : sentences) {
			subjects.addAll(getSubjects(sentence));
		}
		return subjects;
	}

	/** Split sentence by subject
	 * "The hotel was dirty, small and just awful" should be one sentence
	 * "clean hotel, room small, bedroom confortable" should be 3 sentences
	 * Split by ',' and ';'. If there is at least "name + adjective" or "name + verb"
	 * @return list of sentence.
	 */
	private List<String> getSubjects(String text) {
		List<String> subjects = new ArrayList<>();
		String[] hardTokens = text.split(",|;|( or )|( but )");

		String subject = "";
		for (String hardToken : hardTokens) {
			hardToken = hardToken.trim();

			boolean isSoftToken = false;
			String[] softTokens = hardToken.split("( and )");
			for (String token : softTokens) {
				if (token.length() > 0) {
					if(subject.length() > 0) {
						subject = subject + ", " + token;
					} else {
						subject = token;
					}
					Map<POS, Integer> countPos = countPos(subject);
					if (getPOSCount(countPos, NOUN)>0 
							&& (getPOSCount(countPos, ADJECTIVE) > 0 || getPOSCount(countPos, VERB) > 0)) {
						subjects.add(subject.trim());
						subject = "";
					} else if(isSoftToken && (getPOSCount(countPos, NOUN) ==0 || getPOSCount(countPos, ADJECTIVE) ==0)) {
						subjects.set(subjects.size() - 1, subjects.get(subjects.size() - 1).concat(", " + subject));
						subject = "";
					}
				}
				isSoftToken = true;
			}
		}
		//Add last piece to the previous piece if it doesnt contain name + adjective.
		if(subject.length() > 0) {
			if(subjects.size() > 0) {
				subjects.set(subjects.size() - 1, subjects.get(subjects.size() - 1).concat(", " + subject));
			} else {
				subjects.add(subject);
			}
		}
		return subjects;
	}

	
	private Map<POS, Integer> countPos(String text) {
		Map<POS, Integer> count = new EnumMap<>(POS.class);
		String[] words = text.split("\\s");
		for (String word : words) {
			POS pos = dictionaryService.getMostProbablePOS(word);
			if (pos != null) {
				if(pos.equals(NOUN)) {
					addPOSCount(count, NOUN);
				} else if(pos.equals(ADJECTIVE)) {
					addPOSCount(count, ADJECTIVE);
				} else if(dictionaryService.findInfinitiveVerb(word) != null) {
					addPOSCount(count, VERB);
				}
			}
		}
		return count;
	}

	private void addPOSCount(Map<POS, Integer> count, POS pos) {
		if( count.get(pos) == null) {
			count.put(pos, 0);
		}
		count.put(pos, count.get(pos) + 1);
	}

	private Integer getPOSCount(Map<POS, Integer> count, POS pos) {
		if(count.get(pos) == null) {
			return 0;
		}
		return count.get(pos);
	}

}
