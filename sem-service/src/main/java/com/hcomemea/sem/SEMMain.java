package com.hcomemea.sem;

import java.util.Map;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.hcomemea.sem.service.SentenceAnalysisService;

public class SEMMain {
	public static void main(String[] args) {
        ClassPathXmlApplicationContext ctxt = new ClassPathXmlApplicationContext("setup-context.xml");
        SentenceAnalysisService sentenceAnalysisService = ctxt.getBean(SentenceAnalysisService.class);

//        String sentence = "great location and price, just a bit cramped in the room, wouldnt want to stay any longer than 1 night";
        String sentence = "great hotel with a great pool. only downside would be the food... not a great choice for vegetarians";

        //        String sentence = "good hotel with excellent beach and pools";

        Map<String, Double> subjects = sentenceAnalysisService.getSummarizedContent(sentence);

        for(String subject : subjects.keySet()) {
            System.out.println("SUBJECT:" + subject + " SCORES:" + subjects.get(subject));
        }
//      DictionaryService dictionaryService = ctxt.getBean(DictionaryService.class);
//      String stem = dictionaryService.findInfinitiveVerb("running");
//      dictionaryService.getMostProbablePOS("was");
	}

}
