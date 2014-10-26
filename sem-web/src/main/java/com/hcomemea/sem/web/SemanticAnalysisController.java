package com.hcomemea.sem.web;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.hcomemea.sem.service.SentenceAnalysisService;
import com.hcomemea.sem.web.domain.SemanticAnalysisRequest;
import com.hcomemea.sem.web.domain.SemanticAnalysisResponse;


@Controller
public class SemanticAnalysisController {

	private SentenceAnalysisService sentenceAnalysisService;

	@Autowired
	public SemanticAnalysisController(SentenceAnalysisService sentenceAnalysisService) {
		this.sentenceAnalysisService = sentenceAnalysisService;
	}

	@RequestMapping("/search")
    public Model search(Model model, @ModelAttribute("searchRequest") SemanticAnalysisRequest searchRequest, BindingResult bindingResult) {
        if (bindingResult != null && bindingResult.hasErrors()) {
            model.addAttribute("errors", "Errors during binding");
        } else {
            if (searchRequest != null && searchRequest.getText() != null) {
            	Map<String, Double> summarizedContent = sentenceAnalysisService
            			.getSummarizedContent(searchRequest.getText());
                model.addAttribute("searchResult", new SemanticAnalysisResponse
                		.Builder().withSummarizedContent(summarizedContent).build());
            }
        }
        return model;
    }
}
