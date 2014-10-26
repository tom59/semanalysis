package com.hcomemea.sem.service.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import net.sf.extjwnl.JWNLException;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.hcomemea.sem.dictionnary.DictionaryService;
import com.hcomemea.sem.dictionnary.impl.DictionaryServiceImpl;
import com.hcomemea.sem.dictionnary.impl.WordDaoImpl;

import static org.testng.Assert.assertEquals;

public class SentenceAnalysisServiceTest {

	SentenceAnalysisServiceImpl sentenceAnalysisService;

	@BeforeMethod
	private void initTests() throws JWNLException {
		DictionaryService dictionaryService = new DictionaryServiceImpl();
		sentenceAnalysisService = new SentenceAnalysisServiceImpl(new SentenceServiceImpl(dictionaryService),
				new WordDaoImpl(), dictionaryService);
	}

	@Test(dataProvider = "getSummarizedContentTest")
	public void getSummarizedContent(String content, List<StaticWord> expectedResult) {
		Map<String, Double> actual = sentenceAnalysisService.getSummarizedContent(content);
		assertEquals(actual.size(), expectedResult.size());
		for (StaticWord word : expectedResult) {
			assertEquals(actual.get(word.name), word.score,
					"Expected " + word.name + "=" + word.score);
		}
	}

	@DataProvider
	public Object[][] getSummarizedContentTest() {
		return new Object[][] {
				{"The room was awesome but the service was unacceptable" , 
					Arrays.asList(new StaticWord("room", 2), new StaticWord("service", -2))},
				{"The hotel isnt too bad" , 
					Arrays.asList(new StaticWord("hotel", 1))},
				{"The hotel is really bad" , 
					Arrays.asList(new StaticWord("hotel", -2))},
				{"Really good stay and for price a good location, would definitely return, great staff and modern hotel" , 
					Arrays.asList(new StaticWord("stay", 2), new StaticWord("location", 1)
					, new StaticWord("price", 1), new StaticWord("staff", 1), new StaticWord("hotel", 1))},
					{"Nothing standout about the hotel. I always find places with transparent doors on the bathroom a little odd. All in all it was a fine hotel and at a decent price." , 
					Arrays.asList(new StaticWord("hotel", 0), new StaticWord("place", -1), new StaticWord("door", -1),
							new StaticWord("bathroom", -1), new StaticWord("price", 1))},
				{"Great hotel for both business and pleasure. Excellent location and all the staff went the extra mile from arrival to departure. Will be staying here again for sure!",
					Arrays.asList(new StaticWord("hotel", 1), new StaticWord("business", 1), new StaticWord("pleasure", 1),
							new StaticWord("arrival", 1), new StaticWord("departure", 1),
							new StaticWord("mile", 1), new StaticWord("location", 2), new StaticWord("staff", 1))},
				{"breakfast exceeds expectations", Arrays.asList(new StaticWord("breakfast", 2), new StaticWord("expectation", 2))}
		};
	}

	private class StaticWord {
		String name;
		double score;
		public StaticWord(String name, double score) {
			this.name = name;
			this.score = score;
		}
	}
}
