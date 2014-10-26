package com.hcomemea.sem.dictionnary.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.extjwnl.JWNL;
import net.sf.extjwnl.JWNLException;
import net.sf.extjwnl.data.IndexWord;
import net.sf.extjwnl.data.IndexWordSet;
import net.sf.extjwnl.data.POS;
import net.sf.extjwnl.data.Pointer;
import net.sf.extjwnl.data.PointerType;
import net.sf.extjwnl.data.Synset;
import net.sf.extjwnl.data.Word;
import net.sf.extjwnl.dictionary.Dictionary;
import net.sf.extjwnl.dictionary.MorphologicalProcessor;

import org.springframework.stereotype.Service;

import com.hcomemea.sem.domain.WordRelation;
import com.hcomemea.sem.dictionnary.DictionaryService;

@Service
public class DictionaryServiceImpl implements DictionaryService {

	private static Dictionary dictionary;

	public DictionaryServiceImpl() throws JWNLException {
		configureJWordNet();
		dictionary = Dictionary.getInstance();
	}

	public static void main(String[] args) throws JWNLException {
		DictionaryServiceImpl service = new DictionaryServiceImpl();
		service.getRelationalWords(POS.ADJECTIVE, "acceptable");
//		service.printWord(POS.NOUN, "wing");
//		printWord(POS.ADJECTIVE, "amazing");
//		printWord(POS.ADJECTIVE, "decent");
//		printWord(POS.ADJECTIVE, "respectable");
	}

	@Override
	public void printWord(POS pos, String word) {
		try {
			IndexWord indexedWord = dictionary.lookupIndexWord(pos, word);
			if (indexedWord != null) {
				System.out.println("WORD: " + word);
				printAllTarget(indexedWord);
			} else {
				System.out.println("WORD " + word + " not found in dictionary.");
			}
		} catch(JWNLException e) {
			System.out.println("An error occurred while getting word from dictionnary");
		}
	}

	@Override
	public List<String> getAllEnglishAdjectives() {
		List<String> adjectives = new ArrayList<>();
		try {
			Iterator<IndexWord> adjectiveWords =  dictionary.getIndexWordIterator(POS.ADJECTIVE);
			while(adjectiveWords.hasNext()) {
				IndexWord adjective = adjectiveWords.next();
				adjectives.add(adjective.getLemma());
			}
		} catch(JWNLException e) {
			System.out.println("An error occurred while getting word from dictionnary");
		}
		return adjectives;
	}


	@Override
	public List<String> getAllEnglishVerbs() {
		List<String> verbs = new ArrayList<>();
		try {
			Iterator<IndexWord> verbsIt =  dictionary.getIndexWordIterator(POS.VERB);
			while(verbsIt.hasNext()) {
				IndexWord verb = verbsIt.next();
				verbs.add(verb.getLemma());
			}
		} catch(JWNLException e) {
			System.out.println("An error occurred while getting word from dictionnary");
		}
		return verbs;
	}

	@Override
	public Map<WordRelation, List<String>> getRelationalWords(POS pos, String word) {
		Map<WordRelation, List<String>> relationalMap = new HashMap<WordRelation, List<String>>();
		try {
			IndexWord indexedWord = dictionary.lookupIndexWord(pos, word);
			if (indexedWord != null) {
				indexedWord.sortSenses();
				Synset firstSense = indexedWord.getSenses().get(0);
				List<String> synonyms = getRelatedWords(firstSense, PointerType.SIMILAR_TO, PointerType.DERIVATION);
				relationalMap.put(WordRelation.SYNONYM, synonyms);
				List<String> antonyms = getRelatedWords(firstSense, PointerType.ANTONYM);
				relationalMap.put(WordRelation.ANTONYM, antonyms);
//				System.out.println("Process " + word + "= " + synonyms + " != " + antonyms);
			} else {
				System.out.println("WORD " + word + " not found in dictionary.");
			}
		} catch(JWNLException e) {
			System.out.println("An error occurred while getting word from dictionnary");
		}
		return relationalMap;
	}

	@Override
	public boolean isPos(String word, POS pos) {
		IndexWord indexWord = null;
		try {
			indexWord = dictionary.lookupIndexWord(pos, word);
		} catch (JWNLException e) {
			System.out.print("Error " + e.getMessage());
		}
		return indexWord != null;
	}

	/**
	 * Get the most probable part of speech of a word. (Adjective/Noun/Verb)
	 */
	@Override
	public POS getMostProbablePOS(String word) {
		POS pos = null;
		try {
			IndexWordSet indexWordSet = dictionary.lookupAllIndexWords(word);
			Set<POS> allPos = indexWordSet.getValidPOSSet();
			int posUseCount = -1;
			for (POS aPos : allPos) {
				int aPosUseCount = 0;
				for(Synset synset : indexWordSet.getIndexWord(aPos).getSenses()) {
					for (Word aWord : synset.getWords()) {
						aPosUseCount = aPosUseCount + aWord.getUseCount();
					}
				}
				if (aPosUseCount > posUseCount) {
					posUseCount = aPosUseCount;
					pos = aPos;
				}
			}
		} catch (JWNLException e) {
			System.out.print("Error " + e.getMessage());
		}
		return pos;
	}

	@Override
	public String findInfinitiveVerb(String word)
	{
		//Surprisingly "Can" and "Will" are not part of the dictionary.
		if (word.equals("could")) {
			return "can";
		} else if(word.equals("would")) {
			return "will";
		} else {
			return stemWord(word, POS.VERB);
		}
	}

	@Override
	public String stemWord(String word, POS pos) {
		MorphologicalProcessor morph = dictionary.getMorphologicalProcessor();
		IndexWord w;
		try
		{
			w = morph.lookupBaseForm(pos, word );
			if ( w != null )
				return w.getLemma().toString ();
		} 
		catch ( JWNLException e )
		{
		}
		return null;
	}

	@Override
	public List<String> findNounsInSentence(String sentence) {
		List<String> resultList = new ArrayList<>();
		String[] words = sentence.split("\\s");
		for (String word : words) {
			if (POS.NOUN.equals(getMostProbablePOS(word))) {
				resultList.add(word.toLowerCase());
			}
		}
		resultList.removeAll(Arrays.asList("it", "at", "a", "nothing", "in"));
		return resultList;
	}

	private void printAllTarget(IndexWord word) throws JWNLException {
		List<Synset> senses = word.getSenses();
		int i = 1;
		for (Synset sense : senses) {
		  System.out.println("\n" + i + ". " + sense.getGloss());
		  for (Pointer pointer : sense.getPointers()) {
			  if (pointer.getTarget() instanceof Word) {
				  Word aWord = (Word) pointer.getTarget();
				  System.out.print(pointer.getType().getLabel() + ":" + aWord.getLemma() + "\n");
			  } else {
				  Synset synset = (Synset) (pointer.getTarget());
				  List<Word> words = synset.getWords();
				  String wordsLemma = "";
				  for(Word aWord : words) {
					  if(wordsLemma.length() > 0) {
						  wordsLemma = wordsLemma + ", ";
					  }
					  wordsLemma = wordsLemma + aWord.getLemma();
				  }
				  System.out.print(pointer.getType().getLabel() + ":" + wordsLemma+ "\n");
			  }
		  }
		  i++;
		}
	}

	private List<String> getRelatedWords(Synset synset, PointerType... pointerTypes) {
		List<String> relatedWords = new ArrayList<>();
		for (PointerType pointerType : pointerTypes) {
			List<Pointer> pointers = synset.getPointers(pointerType);
			for (Pointer pointer : pointers) {
				if (pointer.getTarget() instanceof Word) {
					relatedWords.add(((Word) pointer.getTarget()).getLemma().toLowerCase());
				} else {
					Synset aSynset = (Synset) (pointer.getTarget());
					for (Word aWord : aSynset.getWords()) {
						relatedWords.add(aWord.getLemma().toLowerCase());
					}
				}
			}
		}
		return relatedWords;
	}

	private void configureJWordNet() {
		try {
			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			JWNL.initialize(classLoader.getResourceAsStream("file_properties.xml"));
		} catch (Exception ex) {
			ex.printStackTrace();
			System.exit(-1);
		}
	}

}
