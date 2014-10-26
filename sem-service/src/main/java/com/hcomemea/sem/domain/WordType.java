package com.hcomemea.sem.domain;

public enum WordType {

	ADJECTIVE ("adjective.properties"), VERB ("verb.properties");

	private String fileName;

	private WordType(String fileName) {
		this.fileName = fileName;
	}

	public String getFileName() {
		return fileName;
	}
}
