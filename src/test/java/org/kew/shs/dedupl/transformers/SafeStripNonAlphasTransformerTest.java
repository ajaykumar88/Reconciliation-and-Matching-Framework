package org.kew.shs.dedupl.transformers;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class SafeStripNonAlphasTransformerTest {

	@Test
	public void blank2blank () {
		Transformer transformer = new SafeStripNonAlphasTransformer();
		assertEquals("", transformer.transform(""));
	}

	@Test
	public void aStringWithDiacritsAndNumbersAndPunctuation () {
		Transformer transformer = new SafeStripNonAlphasTransformer();
		assertEquals("Tete a tete en ", transformer.transform("Tête-à-tête en 2"));
	}

}