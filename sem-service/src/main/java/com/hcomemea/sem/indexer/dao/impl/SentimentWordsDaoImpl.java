package com.hcomemea.sem.indexer.dao.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.hcomemea.sem.indexer.dao.SentimentWordsDao;

/**
 * Retrieve sentiments words for positive-words.txt and negative-words.txt
 */
@Service
public class SentimentWordsDaoImpl implements SentimentWordsDao {

	@Override
	public Map<String, Integer> getSentimentWordsMap() throws IOException {
		Map<String, Integer> sentimentWords = new HashMap<>();

		Map<String, Integer> positiveWords = readWordsFromFile("positive-words.txt", 1);
		sentimentWords.putAll(positiveWords);
		Map<String, Integer> negativeWords = readWordsFromFile("negative-words.txt", -1);
		sentimentWords.putAll(negativeWords);
		return sentimentWords;
	}

	private Map<String, Integer> readWordsFromFile(String fileName, int defaultScore) throws IOException {
		Map<String, Integer> words = new HashMap<>();
		InputStream inputStream = null;
		try {
			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			inputStream = classLoader.getResourceAsStream(fileName);
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));

			String line;
			while ((line = reader.readLine()) != null) {
				if (!line.startsWith(";")) {
					if (line.contains("=")) {
						String[] wordWithScore = line.split("=");
						words.put(wordWithScore[0], Integer.valueOf(wordWithScore[1]));
					} else {
						words.put(line, defaultScore);

					}
				}
			}
		} finally {
			if (inputStream != null) {
				inputStream.close();
			}
		}
		return words;
	}
}
