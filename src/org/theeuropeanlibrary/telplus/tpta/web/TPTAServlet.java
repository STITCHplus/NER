package org.theeuropeanlibrary.telplus.tpta.web;

import org.theeuropeanlibrary.telplus.tpta.web.helpers;

import java.net.URL;
import java.net.URLConnection;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Properties;


import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.hadoop.conf.Configuration;
import org.apache.nutch.analysis.lang.LanguageIdentifier;
import org.theeuropeanlibrary.telplus.tpta.StanfordNamedEntityExtractor;
import org.theeuropeanlibrary.telplus.tpta.NamedEntityExtractorException;
import org.theeuropeanlibrary.telplus.tpta.TPTAResponse;

/**
 * 
 * Servlet entry point for the project.
 * 
 * @author Michel Koppelaar
 *
 * Created on: 21 jul 2008
 * 
 * $Id: TPTAServlet.java 3199 2009-03-17 16:54:47Z michel $
 */
public class TPTAServlet extends HttpServlet {

	// Constants ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	private static final String ENGLISH = "en";
	private static final String GERMAN = "de";
	private static final String DUTCH = "nl";

	private static final String SERIALIZED_ENGLISH_CLASSIFIER_PATH = 
								"/conf/tpta/ner-eng-ie.crf-4-conll.ser.gz";
	private static final String SERIALIZED_GERMAN_CLASSIFIER_PATH = 
								"/conf/tpta/ner-ger-ie.crf-4-kb.ser.gz";
	private static final String SERIALIZED_DUTCH_CLASSIFIER_PATH = 
								"/conf/tpta/dutch.gz";


	// Members ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	private StanfordNamedEntityExtractor enExtractor;
	private StanfordNamedEntityExtractor deExtractor;
	private StanfordNamedEntityExtractor nlExtractor;
	private String version;
	
	// useful Nutch class for language identification
	private LanguageIdentifier languageIdentifier;

	// Static ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	// Constructors ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	// X Implementation ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	// Y Overrides ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	@Override
	public void init() throws ServletException {
		try {
			String enClassifierPath = getClass().getResource(SERIALIZED_ENGLISH_CLASSIFIER_PATH).getPath();
			String deClassifierPath = getClass().getResource(SERIALIZED_GERMAN_CLASSIFIER_PATH).getPath();
			String nlClassifierPath = getClass().getResource(SERIALIZED_DUTCH_CLASSIFIER_PATH).getPath();

			enExtractor = new StanfordNamedEntityExtractor(enClassifierPath);
			deExtractor = new StanfordNamedEntityExtractor(deClassifierPath);
			nlExtractor = new StanfordNamedEntityExtractor(nlClassifierPath);
			languageIdentifier = new LanguageIdentifier(new Configuration());
		} catch (Exception e) {
			throw new ServletException(e);
		}
		this.version = getVersion();
	}
	
	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) 
													throws ServletException, IOException {
		
		resp.setContentType("text/xml; charset=UTF-8");
		PrintWriter pw = resp.getWriter();

		
		String text = req.getParameter("text");
		String url = req.getParameter("url");
    

		String language = req.getParameter("lang");
        String echo = req.getParameter("echo");

		if ((text == null) && (url == null)) {
			resp.sendError(400, "Parameter \"text\" or \"url\" is mandatory");
			return;
		}

        if (text == null) {
            URL url_handle = null;
            try{ 
                url_handle = new URL(url);
            } catch (Exception e) {
			    resp.sendError(400, "Parameter \"url\" is not valid");
    			return;
            }
            try {
                URLConnection con = url_handle.openConnection();
                con.connect();
                InputStream inputStream = con.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF8"));
                String line = bufferedReader.readLine();
                StringBuffer result = new StringBuffer ("");
                while (line != null) {
                    result.append(helpers.xmlEncode(line));
                    line = bufferedReader.readLine();
                }
                text=result.toString();

            } catch (Exception e) {
			    resp.sendError(400, "Could not read \"url\"");
    			return;

            }
        }

		
		// try to autodetect the language
		if (language == null)
			language = languageIdentifier.identify(text);
		
		StanfordNamedEntityExtractor extractor;
		if (language.equals(ENGLISH)) {
			extractor = enExtractor;
		} else if (language.equals(DUTCH)) {
			extractor = nlExtractor;
		} else if (language.equals(GERMAN)) {
			extractor = deExtractor;
		} else {
			resp.sendError(400, "Detected language " + language + " unknown or unsupported");
			return;
		}
		
		try {
			TPTAResponse response = new TPTAResponse(version, text, language, extractor.getNamedEntities(text));
			pw.println(response.toString(echo));
		} catch (NamedEntityExtractorException e) {
			resp.sendError(500, "An exception occurred: " + e.getMessage());
			return;
		}
		
	}
	
	// Public ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	// Protected ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	// Private ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	/* 
	 * extract version info from manifest
	 */
	private String getVersion() {
		String version = "Unknown";
		Properties prop = new Properties();
		try {
			prop.load(getServletContext().getResourceAsStream("/META-INF/MANIFEST.MF"));
			if (prop.containsKey("Specification-Version")) {
				version = (String)prop.get("Specification-Version");
			}
		} catch (IOException e) {
			// just ignore it
		}
		return version;
	}
	
	
	// Inner classes ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

}
