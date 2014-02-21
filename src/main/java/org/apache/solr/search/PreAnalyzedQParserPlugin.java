/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.solr.search;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import opennlp.tools.tokenize.SimpleTokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.tokenize.WhitespaceTokenizer;
import opennlp.tools.util.InvalidFormatException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.charfilter.MappingCharFilterFactory;
import org.apache.lucene.analysis.miscellaneous.SingleTokenTokenStream;
import org.apache.lucene.analysis.synonym.SynonymFilterFactory;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.util.ClasspathResourceLoader;
import org.apache.lucene.analysis.util.FilesystemResourceLoader;
import org.apache.lucene.queryparser.classic.PreAnalyzedQueryParser;
import org.apache.lucene.util.Version;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.request.SolrQueryRequest;

import com.searchtechnologies.aspire.lemma.Lemmatizer;
import com.searchtechnologies.aspire.lemma.LemmatizerException;
import com.searchtechnologies.aspire.lemma.LemmatizerFactory;

import org.apache.solr.schema.IndexSchema;

/**
 * Parse Solr's variant on the Lucene {@link org.org.apache.lucene.queryparser.classic.ComplexPhraseQueryParser} syntax.
 * <p/>
 * Modified from {@link org.apache.solr.search.LuceneQParserPlugin}
 */
public class PreAnalyzedQParserPlugin extends QParserPlugin {

	public static Lemmatizer lemmatizer;
	public static String modelsPath;
	public static String synonymsPath;
	public static String mappingPath;
	public static String lemmaLogPath;
	private String lang = "en";
	
	// OpenNLP Model and tokenizer
	private InputStream tokenModelIn = null;	
	private TokenizerModel tokenModel = null;	
	public static TokenizerME tokenizer = null;
	// OpenNLP tokenizer
	public static WhitespaceTokenizer tokenizerWS;
	public static SimpleTokenizer tokenizerSimple; 
	// Solr Synonym factory
	public static SynonymFilterFactory synFactory = null;
	// Solr Mapping MappingCharFilter factory
	public static MappingCharFilterFactory mapCharFactory = null;
	
	public void init(NamedList args) {
		// TODO Auto-generated method stub
		try {
			// Initialize lemmatizer
			lemmatizer = LemmatizerFactory.createLemmatizer();
			
			// Initialize OpenNLP model and tokenizer
			modelsPath = args.get("modelsPath").toString();			
			tokenModelIn = new FileInputStream(PreAnalyzedQParserPlugin.modelsPath + lang + File.separator + lang + "-token.bin");	
			tokenModel = new TokenizerModel(tokenModelIn);			
			tokenizer = new TokenizerME(tokenModel);
			tokenizerSimple = SimpleTokenizer.INSTANCE;
			tokenizerWS = WhitespaceTokenizer.INSTANCE;
			
			// Get Synonyms file path
			synonymsPath = args.get("synonymsPath").toString();
			
			// Initialize Synonyms Filter factory
			Map<String,String> argsSyn = new HashMap<String,String>();
			argsSyn.put("synonyms", synonymsPath);
			argsSyn.put("luceneMatchVersion", Version.LUCENE_46.toString());
			synFactory = new SynonymFilterFactory(argsSyn);
		    synFactory.inform(new FilesystemResourceLoader());
		    
		    // Get Mapping Char file path
		    mappingPath = args.get("mappingsPath").toString();
		    
		    lemmaLogPath = args.get("lemmaLogPath").toString();
		    
		    // Initialize Mapping Char Filter factory
		    Map<String,String> argsCharFactory = new HashMap<String,String>();
		    argsCharFactory.put("mapping", mappingPath);
		    argsCharFactory.put("luceneMatchVersion", Version.LUCENE_46.toString());
		    mapCharFactory = new MappingCharFilterFactory(argsCharFactory);
		    mapCharFactory.inform(new FilesystemResourceLoader());
		    
		} catch (LemmatizerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}

	public QParser createParser(String qstr, SolrParams localParams,
			SolrParams params, SolrQueryRequest req) {
		// TODO Auto-generated method stub
		/*NamedList<Object> fqParam = params.toNamedList();
		
		NamedList<Object> tempParams = new NamedList<Object>();
		
		tempParams.add("fq", "id:10");
		
		SolrParams paramsTest = SolrParams.toSolrParams(tempParams);*/

		return new PreAnalyzedQueryParser(qstr, localParams, params, req);
	}

	
}

