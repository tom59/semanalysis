package com.hcomemea.sem.dictionnary;

import java.util.Arrays;
import java.util.List;

import net.sf.extjwnl.JWNLException;
import net.sf.extjwnl.data.POS;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.hcomemea.sem.dictionnary.impl.DictionaryServiceImpl;

public class DictionnaryServiceTest {

	private DictionaryService dictionaryService;

	@BeforeMethod
	public void initTests() throws JWNLException {
		dictionaryService = new DictionaryServiceImpl();
	}

	@Test(dataProvider = "mostProbablePOS")
	public void testGetMostProbablePOS(String word, POS expectedPos) {
		POS actual = dictionaryService.getMostProbablePOS(word);
		Assert.assertEquals(actual, expectedPos);
	}

	@DataProvider(name = "mostProbablePOS")
	public Object[][] get() {
		return new Object[][] {
			new Object[]{"at", POS.NOUN},
			new Object[]{"vegetarians", POS.NOUN}};
	}

	@Test(dataProvider = "findNounsInSentence")
	public void testFindNounsInSentence(String sentence, List<String> expectedNouns) {
		List<String> words = dictionaryService.findNounsInSentence(sentence);
		Assert.assertEquals(words, expectedNouns);
	}

	@DataProvider(name = "findNounsInSentence")
	public Object[][] findNounsInSentence() {
		return new Object[][] {
			new Object[]{"at the hotel it was nice", Arrays.asList("hotel")},
			new Object[]{"in the room it was not", Arrays.asList("room")}};
	}
}
