package org.theeuropeanlibrary.telplus.tpta;

import java.util.List;

public interface INamedEntityExtractor {
	public List<NamedEntity> getNamedEntities(String text) throws NamedEntityExtractorException;
}
