/*
 * Copyright (c) 2012-2019 Smart Grid Energy
 * All Right Reserved
 * http://www.smartgridenergy.fr
 */

package mcs.pattern;

import mcs.utils.FileUtils;
import mcs.utils.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

public class GuitarPatternStore {

	public static final File DEFAULT_PATTERN_DIR = new File("./pattern/guitar/");
	public static final String GUITAR_PATTERN_FILE_REGEX = "([^\\.]+)\\.gpt";

	private File m_dir;

	Map<String, GuitarPattern> m_patterns;

	public GuitarPatternStore() {
		this(DEFAULT_PATTERN_DIR);
	}

	public GuitarPatternStore(File patternDir) {
		m_dir = patternDir;
		m_patterns = new TreeMap<>();
		load();
	}

	public GuitarPattern get(String name) {
		return m_patterns.get(name);
	}

	public Map<String, GuitarPattern> getAll() {
		return new TreeMap<>(m_patterns);
	}

	//
	// Persistence
	//

	private void load() {
		for(File file : m_dir.listFiles()) {
			String fileName = file.getName();
			if(!fileName.matches(GUITAR_PATTERN_FILE_REGEX)) {
				continue;
			}
			String name = StringUtils.getGroup(fileName, GUITAR_PATTERN_FILE_REGEX, 1);

			GuitarPattern pattern = null;
			try {
				pattern = new GuitarPattern(file);
			} catch(IOException e) {
				FileUtils.log("Cannot load pattern '%s': %s", fileName, e.getMessage());
			}

			m_patterns.put(name, pattern);
		}
	}

	public void save(String name, GuitarPattern pattern) {
		File output = new File(m_dir, name.replaceAll(" ", "") + ".gpt");
		try {
			pattern.save(output);
		} catch(IOException e) {
			e.printStackTrace();
		}
		m_patterns.put(name, pattern);
	}

}