package com.hcomemea.sem.web.domain;

import java.util.Map;

public class SemanticAnalysisResponse {

	private Map<String, Double> summarizedContent;

	private SemanticAnalysisResponse(Builder builder) {
		this.summarizedContent = builder.summarizedContent;
	}

	public Map<String, Double> getSummarizedContent() {
		return summarizedContent;
	}

	public static class Builder {
		private Map<String, Double> summarizedContent;

		public Builder withSummarizedContent(Map<String, Double> summarizedContent) {
			this.summarizedContent = summarizedContent;
			return this;
		}

		public SemanticAnalysisResponse build() {
			return new SemanticAnalysisResponse(this);
		}
	}
}
