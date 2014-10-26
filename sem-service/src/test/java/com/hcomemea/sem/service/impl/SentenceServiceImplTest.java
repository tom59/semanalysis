package com.hcomemea.sem.service.impl;

import java.util.List;

import net.sf.extjwnl.JWNLException;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.hcomemea.sem.dictionnary.impl.DictionaryServiceImpl;

import static org.testng.Assert.assertEquals;


public class SentenceServiceImplTest {

	SentenceServiceImpl sentenceService;

	@BeforeMethod
	private void initTests() throws JWNLException {
		sentenceService = new SentenceServiceImpl(new DictionaryServiceImpl());
	}

	@Test(dataProvider="splitSentenceBySubjectData")
	public void testSplitSentenceBySubject(String sentence, String[] expectedResults) {
		List<String> results = sentenceService.splitSentenceBySubject(sentence);
		assertEquals(results.size(), expectedResults.length);
		for(int i = 0; i< expectedResults.length; i++) {
			assertEquals(results.get(i), expectedResults[i]);
		}
	}

	@DataProvider
	public Object[][] splitSentenceBySubjectData() {
		return new Object[][]{
			{"The hotel was dirty", new String[]{"The hotel was dirty"}},
			{"The hotel was dirty, small and just awful", new String[]{"The hotel was dirty, small, just awful"}},
			{"The hotel was dirty, the room was awful", new String[]{"The hotel was dirty", "the room was awful"}},
			{"dirty hotel, dont go there", new String[]{"dirty hotel, dont go there"}},
			{"great location and price, just a bit cramped in the room", 
				new String[]{"great location, price", "just a bit cramped in the room"}},
			{"The hotel was dirty and the room was awful", new String[]{"The hotel was dirty", "the room was awful"}},
		};
	}
}
