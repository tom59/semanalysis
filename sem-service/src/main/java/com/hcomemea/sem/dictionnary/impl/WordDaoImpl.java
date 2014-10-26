package com.hcomemea.sem.dictionnary.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.TreeSet;

import org.springframework.stereotype.Component;

import com.hcomemea.sem.dictionnary.WordDao;
import com.hcomemea.sem.domain.WordType;

@Component
public class WordDaoImpl implements WordDao {

	private static final String WORDS_PATH = "C:/SEM/temp/";

	@Override
	public void writeWords(WordType wordType, Map<String, String> wordsMap) {
		File file = new File(wordType.getFileName());
		FileWriter writer = null;
		try {
			writer = new FileWriter(WORDS_PATH + file);
			Properties properties = createSortedProperties();
			properties.putAll(wordsMap);
			properties.store(writer, "All Polarized English Adjectives");
		} catch (IOException e) {
			System.out.println("An error occured while writing adjective file: " + e.getMessage());
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
				}
			}
		}
	}

	@Override
	public Map<String, String> getWords(WordType wordType) {
		Map<String, String> wordScore = new HashMap<String, String>();
		File file = new File(WORDS_PATH  + wordType.getFileName());
		try {
			Properties properties = createSortedProperties();
			FileInputStream inputStream = new FileInputStream(file);
			properties.load(inputStream);
			for(Object word : properties.keySet()) {
				wordScore.put((String)word, (String)properties.get(word));
			}
		} catch (IOException e) {
			System.out.println("An error occured while reading file: " + e.getMessage());
		}
		return wordScore;
	}

	private Properties createSortedProperties() {
		return new Properties() {
			private static final long serialVersionUID = 1L;

			@Override
		    public synchronized Enumeration<Object> keys() {
		        return Collections.enumeration(new TreeSet<Object>(super.keySet()));
		    }
		};

	}
}
