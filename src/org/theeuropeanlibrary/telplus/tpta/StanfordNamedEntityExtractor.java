package org.theeuropeanlibrary.telplus.tpta;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations.AnswerAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.CurrentAnnotation;


public class StanfordNamedEntityExtractor implements INamedEntityExtractor {

	// Constants ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	private static String PERSON = "PERSON";
	private static String ORGANISATION = "ORGANIZATION";
	private static String LOCATION = "LOCATION";
	private static String OTHER = "MISC";
	private static String UNKNOWN = "O";

	// Members ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	private CRFClassifier classifier;

	// Static ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	// Constructors ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	public StanfordNamedEntityExtractor(String serializedClassifierPath) 
															throws 
															NamedEntityExtractorException {
		try {
			this.classifier = CRFClassifier.getClassifier(serializedClassifierPath);
		} catch (Exception e) {
			throw new NamedEntityExtractorException(e);
		}
	}
	
	public StanfordNamedEntityExtractor(InputStream serializedClassifierStream) 
															throws 
															NamedEntityExtractorException {
		try {
			this.classifier = CRFClassifier.getClassifier(serializedClassifierStream);
		} catch (Exception e) {
			throw new NamedEntityExtractorException(e);
		}
		
	}

	// X Implementation ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	public List<NamedEntity> getNamedEntities(String text) 
												throws NamedEntityExtractorException{
		
		ArrayList<NamedEntity> entities = new ArrayList<NamedEntity>();
		/*
		 * please refer to the stanford-ner package documentation to find out
		 * what this is all about
		 */
		List<List<CoreLabel>> labeledSentences = classifier.testSentences(text);
		for (List<CoreLabel> sentence : labeledSentences) {
			String value = "";
			String label = "";
			String previousLabel = "";
			for (CoreLabel word : sentence) {
				label = word.getString(AnswerAnnotation.class);
				if (!(label.equals(previousLabel) || previousLabel.equals(""))) {
					if (!previousLabel.equals(UNKNOWN)) {
						entities.add(createEntity(previousLabel, value.trim()));
					}
					value = word.getString(CurrentAnnotation.class) + " ";
				} else {
					value += word.getString(CurrentAnnotation.class) + " ";
				}
				previousLabel = label;
			}
			
			// process the last word
			if (!label.equals(UNKNOWN)) {
				entities.add(createEntity(label, value.trim()));
			}
			
		}
		return entities;
	}

	// Y Overrides ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	// Public ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	// Protected ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	// Private ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	private NamedEntity createEntity(String label, String value) 
												throws NamedEntityExtractorException{
		if (label.equals(PERSON)) {
			return new PersonEntity(value);
		} else if (label.equals(ORGANISATION)) {
			return new OrganisationEntity(value);
		} else if (label.equals(LOCATION)) {
			return new LocationEntity(value);
		} else if (label.equals(OTHER)) {
			return new OtherEntity(value);
		} else {
			throw new NamedEntityExtractorException("Unknown entity type, label: " 
											+ label + ", value: " + value);
		}
	}
	// Inner classes ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

}
