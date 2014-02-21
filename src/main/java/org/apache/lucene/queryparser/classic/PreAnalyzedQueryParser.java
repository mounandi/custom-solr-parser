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

package org.apache.lucene.queryparser.classic;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.LinkedHashSet;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.miscellaneous.SingleTokenTokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.queryparser.classic.QueryNode.NTypes;
import org.apache.lucene.search.Query;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.handler.component.SearchHandler;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.schema.FieldType;
import org.apache.solr.schema.IndexSchema;
import org.apache.solr.search.PreAnalyzedQParserPlugin;
import org.apache.solr.search.QParser;
import org.apache.solr.search.SyntaxError;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.apache.solr.parser.QueryParser;

public class PreAnalyzedQueryParser extends QParser {

	protected final IndexSchema schema;
	SolrQueryRequest req;
	/***
	 * Class Constructor
	 * 
	 * @param qstr
	 * @param localParams
	 * @param params
	 * @param req
	 */
	public PreAnalyzedQueryParser(String qstr, SolrParams localParams,
			SolrParams params, SolrQueryRequest req) {
		super(qstr, localParams, params, req);
		this.schema = this.getReq().getSchema();
		this.req = req;		
	}

	/***
	 * Transform a single token into its corresponding escaped JSON representation, 
	 * i.e. test => \{\"v\"\:\"1\"\,\"tokens\"\:\[\{\"t\"\:\"test\"\}\]\}
	 * 
	 * @param term
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private String transformTermToJSON(String term) {
		JSONObject jsonPreAnalizedField = new JSONObject();
		jsonPreAnalizedField.put("v", "1");
		JSONArray tokensArr = new JSONArray();
		JSONObject tokenInfo = new JSONObject();
		tokenInfo.put("t", term.toLowerCase());
		tokensArr.add(tokenInfo);
		jsonPreAnalizedField.put("tokens", tokensArr);
		String qEscJSON = jsonPreAnalizedField.toJSONString().replace("{", "\\{")
				.replace("[", "\\[")													
				.replace("\"", "\\\"")
				.replace("}", "\\}")
				.replace("]", "\\]")
				.replace(":", "\\:")
				.replace(",", "\\,");
		return qEscJSON.toString();
	}

	/***
	 * Transform a phrase term into its corresponding escaped JSON representation,
	 * i.e. "early pionner" => {\"v\"\:\"1\",\"tokens\"\:[{\"t\"\:\"early\"},{\"t\"\:\"pioneer\"}]}
	 * 
	 * @param phrase
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private String transformTermToJSONArray(String phrase) {
		String[] terms = phrase.split(" ");
		JSONObject jsonPreAnalizedField = new JSONObject();
		jsonPreAnalizedField.put("v", "1");
		JSONArray tokensArr = new JSONArray();
		for(String term : terms){
			JSONObject tokenInfo = new JSONObject();
			tokenInfo.put("t", term.toLowerCase());
			tokensArr.add(tokenInfo);

		}
		jsonPreAnalizedField.put("tokens", tokensArr);
		String qEscJSON = jsonPreAnalizedField.toJSONString().replace("\"", "\\\"").replace(":", "\\:");
		return qEscJSON.toString();
	}


	/***
	 * Main entry that parses the "q" parameter value from every query,
	 * overwrites the parse method from the Solr Query Class.
	 * 
	 */	 
	@Override
	public Query parse() throws SyntaxError {

		// Get list of params from request
		NamedList<Object> requestParams = this.params.toNamedList();

		// Getting q param value from request
		String qryJO = requestParams.get("q").toString();

		String finalQry = "";
		
		if (requestParams.get("queryProcessing") == null) {

			// Get debug parameter from request
			Boolean debugQuery = Boolean.valueOf(params.get("debugQuery"));
			if (debugQuery==null){
				debugQuery = false;
			}

			// Get lemma exp global param
			Boolean lemmaExp = Boolean.valueOf(params.get("lemma"));
			if (lemmaExp==null){
				lemmaExp = false;
			}

			// Get synonym exp global param
			Boolean synExp = Boolean.valueOf(params.get("syn"));
			if (synExp==null){
				synExp = false;
			}

			// Get accent removal global param
			Boolean accRem = Boolean.valueOf(params.get("accRem"));
			if (accRem==null){
				accRem = false;
			}

			// Get language for the query terms, default to english
			String lang = params.get("language");
			if(lang==null){
				lang = "en";
			}

			// Special param for lemmatizer testing
			Boolean showLemmas = params.getBool("showLemmas");

			// Store debug messages
			String debugMessage = "<![CDATA[";

			String[] transOut = new String[2];
			if(!qryJO.equals("*:*")){ // validates all docs wildcard query
				transOut = transformParamValue(qryJO, debugQuery, lang, synExp, lemmaExp, accRem);
				finalQry = transOut[0];
			} else { // all docs wildcard query scenario
				finalQry = "*:*";			
			}	
			// Print final transformed query
			if (debugQuery) {
				debugMessage += transOut[1];
				debugMessage += "After PreAnalyzed transformation->" + finalQry + "]]>";
				System.out.println(debugMessage);
			}

			// Get fq params from request
			List<Object> allFQs = requestParams.getAll("fq");
			requestParams.removeAll("fq");			
			// Iterate over all fq params and apply transformations (tokenization, lemmatization, JSON format)
			for (Object fqValue : allFQs) {
				if (fqValue instanceof String[]) {
					for (String val : (String[]) fqValue) {
						String newFQ = transformParamValue(val, false, lang, synExp, lemmaExp, accRem)[0];
						requestParams.add("fq", newFQ);		
					}
				} else {
					String newFQ = transformParamValue(fqValue.toString(), false, lang, synExp, lemmaExp, accRem)[0];
					requestParams.add("fq", newFQ);
				}
			}
			if(debugQuery) {			
				requestParams.add("queryParserDebugProcess", debugMessage);			
			}
			requestParams.add("queryProcessing", "True");
			requestParams.remove("q");
			requestParams.add("q", finalQry);
			this.req.setParams(SolrParams.toSolrParams(requestParams));
		} else {
			finalQry = requestParams.get("q").toString();
		}


		// Redirect the transform query to Solr Query Class
		QueryParser qp = new QueryParser(schema.getDefaultLuceneMatchVersion(), params.get("df"), this);

		Query qry = qp.parse(finalQry);
		return qry;
	}


	public String[] transformParamValue(String qryJO, Boolean debugQuery, String lang, Boolean synExp, Boolean lemmaExp, Boolean accRem) {
		String[] out = new String[2];
		String debugMsg = "";

		// Move q value to list of query nodes
		ArrayList<QueryNode> qryL = toQueryList(qryJO, schema);
		if (debugQuery) {
			debugMsg += "NodeList->" + nodeListToString(qryL) + "<br/>";	
		}
		// Tokenize recognized term query nodes 
		qryL = tokenizeChildNodes(qryL, lang);
		if (debugQuery) {
			debugMsg += "After tokenization->" + nodeListToString(qryL) + "<br/>";				
		}
		// Perfom synonyms expansion of term query nodes
		qryL = synonymExpandChildNodes(qryL, synExp);
		if (debugQuery) {
			debugMsg += "After Synonym expansion->" + nodeListToString(qryL) + "<br/>";				
		}			
		// Perform lemmatization expansion of term query nodes
		qryL = lemmaExpandChildNodes(qryL, lemmaExp);
		if (debugQuery) {
			debugMsg += "After Lemmatization expansion->" + nodeListToString(qryL) + "<br/>";				
		}
		// Perform accent removal to term query nodes
		qryL = accentRemovalChildNodes(qryL, accRem);
		if (debugQuery) {
			debugMsg += "After Accent reduction->" + nodeListToString(qryL) + "<br/>";				
		}
		// Transform term query nodes to its corresponding JSON Pre-Analyzed format
		qryL = toJSONPreAnalyzedFormat(qryL);
		String finalQuery = nodeListToString(qryL);

		out[0] = finalQuery;
		if(debugQuery) {
			out[1] = debugMsg;
		}
		return out;
	}

	/***
	 * Prints the content of the list of query nodes.
	 * @param nodeList
	 * @return
	 */
	private String nodeListToString(ArrayList<QueryNode> nodeList){
		String nodeListStr = "";
		String nodeFieldName = "";
		for(QueryNode qryNode : nodeList) {   
			if(qryNode.getType().equals(NTypes.TERM)){
				if(qryNode.getIsPreAnalyzed()){
					nodeListStr += nodeFieldName + ":";
				}
			}
			if(qryNode.getType().equals(NTypes.FIELD)) {
				nodeFieldName = qryNode.getData();
				if(!qryNode.getIsPreAnalyzed()){
					nodeListStr += nodeFieldName + ":";
				}
			}
			if(qryNode.getIsPhrase()) {
				nodeListStr += "\"" + qryNode.getData() + "\"" + " ";
			} else if(qryNode.getType().equals(NTypes.OPERATOR) && ( qryNode.getData().equals("+")) 
					|| qryNode.getData().equals("-")) {
				nodeListStr += qryNode.getData();
			} else if((!qryNode.getType().equals(NTypes.FIELD))&& !qryNode.getData().equals(":")){
				nodeListStr += qryNode.getData() +  " ";
			}
		}		
		return nodeListStr.trim();
	}

	/***
	 * Expands the term query nodes, if local parameter "syn" is set to true for the specified 
	 * field query, i.e. {!syn=true} pre_analyzed_j_2:pixima
	 * And adds the expansions as new nodes to the query node list. 
	 * @param term
	 * @return
	 */
	private ArrayList<String> expandSynonyms(String term){
		ArrayList<String> synonyms = new ArrayList<String>();
		TokenStream ts = PreAnalyzedQParserPlugin.synFactory.create(new SingleTokenTokenStream(new Token(term, 0, term.length())));

		CharTermAttribute termAtt = null;		
		try {
			while(ts.incrementToken()) {
				termAtt = ts.getAttribute(CharTermAttribute.class);
				if(!termAtt.toString().equals(term)){
					synonyms.add(termAtt.toString());
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return synonyms;

	}

	/***
	 * Remove accent characters from a token, usign the MappingCharFilterFactory
	 * from Solr.
	 * @param term
	 * @return
	 */
	private String removeAccents(String term){
		List<String> synonyms = new ArrayList<String>();
		Reader ts = PreAnalyzedQParserPlugin.mapCharFactory.create(new StringReader(term));
		StringBuilder actualBuilder = new StringBuilder();
		try{
			// Now consume the actual mapFilter, somewhat randomly:
			while (true) {
				int ch;
				ch = ts.read();
				if (ch == -1) {
					break;
				}
				actualBuilder.append((char) ch);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return actualBuilder.toString();		
	}


	/***
	 * Perform tokenization and term analysis using the Solr analyzers defined in the Schema. 
	 * @param langCode 
	 * @param rawContent 
	 * @return
	 * */
	private ArrayList<String> getTokensArrayUsingFieldTypes(String langCode, String rawContent)
	{
		ArrayList<String> tokens = new ArrayList<String>();
		Boolean isPhrase = false;
		if((rawContent.trim().startsWith("\"")) && (rawContent.trim().endsWith("\""))){
			isPhrase = true;
		}
		try {
			// Build Solr field type based on language and get the respective analyzer
			String fieldTypeName = "text_" + langCode; 
			FieldType fType = schema.getFieldTypeByName(fieldTypeName);
			//Analyzer an = fType.getAnalyzer();
			Analyzer an = fType.getQueryAnalyzer();


			// Apply the analyzer to the content
			TokenStream ts = null;
			ts = an.tokenStream("", new StringReader(rawContent));
			ts.reset();
			CharTermAttribute term = (CharTermAttribute) ts.getAttribute(CharTermAttribute.class);
			int termsCount = 0;
			while(ts.incrementToken()){ // For each detected token
				if((termsCount==0) && isPhrase){
					tokens.add("\""+ term.toString()); // preserve initial quote
				} else {
					String tok = term.toString();
					if(rawContent.contains(tok+"*")) {// preserve trailing wildcard operator
						tok+="*";
					} 
					if(rawContent.contains("*"+tok)) {// preserve leading wildcard operator
						tok="*"+tok;
					}
					tokens.add(tok);
				} 
				termsCount++;
			}
			if(isPhrase){ // Adding final quote to last term from phrase
				tokens.set(tokens.size()-1, tokens.get(tokens.size()-1)+"\"");
			}
			// Close token stream
			ts.end();
			ts.close();
		} catch (Exception e) {
			// TODO: handle exception
		}

		return tokens;
	}

	/***
	 * Moves the q parameter value into a list of query nodes
	 * @param str
	 * @return
	 */
	public ArrayList<QueryNode> toQueryList(String str, IndexSchema schema) {

		// Regular expression for query elements recognition
		/***
		 * http://lucene.apache.org/solr/4_6_1/solr-core/org/apache/solr/schema/DateField.html
		 * Matches:
		 *  	1995-12-31T23:59:59Z
		 *		1995-12-31T23:59:59.9Z
		 *		1995-12-31T23:59:59.99Z
		 *		1995-12-31T23:59:59.999Z
		 */
		String dateTimeRegEx = "\"?(\\d{4})-(\\d{2})-(\\d{2})T(\\d{2}):(\\d{2}):(\\d{2})(\\.\\d{1}(\\d{1})?(\\d{1})?)?Z\"?";
		Pattern pattern = Pattern.compile(dateTimeRegEx + "|[(]|[)]|[\\[]|[\\]]|[\\p{L}|\\p{Pc}|\\p{N}]+|\\p{S}+|[{]|[}]|[\"]|[\"]|[:]|[*]|[?]|[=]|[+]|[-]|.");
		Matcher m = pattern.matcher(str);
		Boolean isOpenLocalParamText = false;
		Boolean isPhrase = false;			
		String localParamText = "";
		String tempTerm = "";
		String tempPhrase = "";
		Boolean isWildcardTerm = false;
		Boolean isPreAnalyzed = false;
		ArrayList<QueryNode> nodeList = new ArrayList<QueryNode>();
		String previousToken = "";
		// Iterate over the set of recognized elements from the regular expression defined above
		while (m.find()) {
			String node1 = m.group();				
			// if debug mode ON print each recognized element
			/*if (PreAnalyzedQParserPlugin.debugOn) {

				}*/
			if(node1.equals("{")) { // Open bracket for local param section
				isOpenLocalParamText = true;
			} else if(node1.equals("}")) {// Close bracket for local param section
				isOpenLocalParamText = false;					
			} else if (node1.equals("(") || node1.equals("[")) {// Open parenthesis, add it to the list of nodes 
				nodeList.add(new QueryNode(node1, NTypes.SYMBOL));
			} else if (node1.equals("OR") || node1.equals("AND")
					|| node1.equals("NOT") || node1.equals("+") || node1.equals("-") || (node1.equalsIgnoreCase("to") && !isPreAnalyzed)) {// Boolean operators
				if((previousToken.trim().isEmpty())&& !isPhrase) {
					if(!tempTerm.isEmpty()) {// If temporal term buffer not empty, create a node with this value and add it to the list of nodes
						QueryNode fieldNode = new QueryNode(tempTerm.trim(), NTypes.TERM, isPreAnalyzed);
						nodeList.add(fieldNode);
						tempTerm = ""; //clean temporal term buffer
					} else if (!tempPhrase.isEmpty()) {// If temporal phrase buffer not empty, create a node with this value and add it to the list of nodes
						QueryNode fieldNode = new QueryNode(tempPhrase.trim(), NTypes.TERM, isPreAnalyzed);
						fieldNode.setIsPhrase(true); //set phrase flag to true
						nodeList.add(fieldNode);
						tempPhrase = ""; //clean temporal phrase buffer
					}
					nodeList.add(new QueryNode(node1, NTypes.OPERATOR)); // Add the operator to the list of query nodes
				} else {
					if(!tempTerm.isEmpty()) {
						if (previousToken.trim().isEmpty()) {
							tempTerm = tempTerm + node1;
						} else {
							tempTerm = tempTerm.trim() + node1;
						}						
					} else if (!tempPhrase.isEmpty()) {
						if (previousToken.trim().isEmpty()) {
							tempPhrase = tempPhrase + node1;
						} else {
							tempPhrase = tempPhrase.trim() + node1;
						}
					}
				}
			} else if (node1.equals(")")||node1.equals("]")) { // Close parenthesis, add it to the list of nodes
				if(!tempTerm.isEmpty()) {// If temporal term buffer not empty, create a node with this value and add it to the list of nodes
					QueryNode fieldNode = new QueryNode(tempTerm.trim(), NTypes.TERM, isPreAnalyzed);						
					nodeList.add(fieldNode);
					tempTerm = ""; //clean temporal term buffer
				} else if (!tempPhrase.isEmpty()) {
					QueryNode fieldNode = new QueryNode(tempPhrase.trim(), NTypes.TERM, isPreAnalyzed);
					fieldNode.setIsPhrase(true); //set phrase flag to true
					nodeList.add(fieldNode);
					tempPhrase = ""; //clean temporal phrase buffer
				}
				nodeList.add(new QueryNode(node1, NTypes.SYMBOL)); // Add parenthesis to list of query nodes
			} else if (node1.equals(":") && !isPhrase){ // field name delimiter					
				if(!tempTerm.isEmpty()) { // If temporal term buffer not empty, create a node with this value and add it to the list of nodes as a FIELD type
					String fieldType = schema.getField(tempTerm.trim()).getType().getClassArg();
					isPreAnalyzed = false;
					if(fieldType.contains("solr.PreAnalyzedField")) {
						isPreAnalyzed = true;
					} 
					QueryNode fieldNode = new QueryNode(tempTerm.trim(), NTypes.FIELD, isPreAnalyzed);
					if(!localParamText.isEmpty()) {// Add the local params if any
						fieldNode.setLocalParams(localParamText); 
					}
					nodeList.add(fieldNode);
					tempTerm = "";
				}
				nodeList.add(new QueryNode(node1, NTypes.SYMBOL)); // Add colon to list of query nodes	 			
			} else if (node1.equals("*") || node1.equals("?") ||  // Other element like wildcard operators, accented characters and equals
					node1.equals("=") || node1.matches("\\p{S}")){					
				if(isOpenLocalParamText) { 
					localParamText = localParamText.trim() + node1; // Keep adding to the local params buffer
				} else if (isPhrase) {
					tempPhrase += node1;
				}	else  {
					tempTerm += node1; // Keep adding to the term buffer
				}
				if ((node1.equals("*") || node1.equals("?")) && !isPhrase){
					isWildcardTerm = true;
				}
			} else if (node1.trim().isEmpty()) { // Matches white spaces, help delimiting the scope of a wildcard operator to see if 
				// is the end of a term or a part of it, in order to keep adding more element to the term buffer
				if(isWildcardTerm) {
					isWildcardTerm = false;
				} else if(isPhrase) {
					tempPhrase += node1;				
				} else if(!tempTerm.isEmpty()){
					tempTerm += node1;
				}
			} else { // Any other recognized element, like words
				if(isOpenLocalParamText) {
					localParamText += node1 + " "; // Keep adding to the local params buffer
				} else if (node1.equals("\"")) {
					if(isPhrase) {
						isPhrase = false;
						tempPhrase = tempPhrase.trim(); // Keep adding to the phrase buffer
					} else {
						isPhrase = true;							
					}
				} else {
					if(isPhrase) {
						tempPhrase += node1; // Keep adding to the phrase buffer
					} else {
						if((tempTerm.trim().endsWith("*"))||  // if term buffer ends with wildcard operators or accented characters
								(tempTerm.trim().endsWith("?")) ||
								(tempTerm.trim().length()>1)){
							if (isWildcardTerm) {
								tempTerm = tempTerm.trim() + node1 + " "; // Keep adding to the term buffer, character should be part of term, not the end, i.e se?rch
							} else {
								tempTerm += node1; // Keep adding to the term buffer, character is the end of the term, i.e. sear*
							}
						} else {
							tempTerm += node1; // // Keep adding to the term buffer
						}
					}						
				}					
			}
			previousToken = node1;
		}

		if(!tempTerm.isEmpty()) { // If there's any term left on buffer, create a node and add it to the list
			QueryNode fieldNode = new QueryNode(tempTerm.trim(), NTypes.TERM, isPreAnalyzed);
			nodeList.add(fieldNode);
			tempTerm = "";
		} else if (!tempPhrase.isEmpty()) { // If there's any phrase left on buffer, create a node and add it to the list
			QueryNode fieldNode = new QueryNode(tempPhrase.trim(), NTypes.TERM, isPreAnalyzed);
			fieldNode.setIsPhrase(true);
			nodeList.add(fieldNode);
			tempPhrase = "";
		}
		return nodeList;		
	}

	/***
	 * Split the term query nodes into new nodes, depending on the output from the tokenization process 
	 *  
	 * @param langCode 
	 * @param rawContent 
	 * @return
	 * */
	public ArrayList<QueryNode> tokenizeChildNodes(ArrayList<QueryNode> qryNodeList, String langCode) {

		ArrayList<QueryNode> newQryNodeList =  new ArrayList<QueryNode>();
		for (QueryNode qryNode : qryNodeList) {

			if(qryNode.getType().equals(NTypes.TERM) && !qryNode.getIsPhrase() && qryNode.getIsPreAnalyzed()){
				ArrayList<String> tokens = getTokensArrayUsingFieldTypes(langCode, qryNode.getData());

				//Removing duplicates
				LinkedHashSet hs = new LinkedHashSet();
				hs.addAll(tokens);
				tokens.clear();
				tokens.addAll(hs);

				for (String token : tokens){
					newQryNodeList.add(new QueryNode(token, NTypes.TERM, qryNode.getIsPreAnalyzed()));
				}				

			} else {
				newQryNodeList.add(qryNode);
			}

		}

		return newQryNodeList;	

	}

	public ArrayList<QueryNode> synonymExpandChildNodes(ArrayList<QueryNode> qryNodeList, Boolean synExp) {

		ArrayList<QueryNode> newQryNodeList =  new ArrayList<QueryNode>();
		Boolean fieldClauseHasSynExp = false;
		for (QueryNode qryNode : qryNodeList) {
			if(qryNode.getType().equals(NTypes.TERM) && !qryNode.getIsPhrase() && qryNode.getIsPreAnalyzed()){
				if(fieldClauseHasSynExp) {
					ArrayList<String> synonyms = expandSynonyms(qryNode.getData());
					//Removing duplicates
					LinkedHashSet hs = new LinkedHashSet();
					hs.addAll(synonyms);
					synonyms.clear();
					synonyms.addAll(hs);

					if(synonyms.size()>0){ 
						newQryNodeList.add(new QueryNode("(", NTypes.SYMBOL));
					}
					newQryNodeList.add(new QueryNode(qryNode.getData(), NTypes.TERM, qryNode.getIsPreAnalyzed()));

					for (String syn : synonyms){
						newQryNodeList.add(new QueryNode(syn, NTypes.TERM, qryNode.getIsPreAnalyzed()));
					}		
					if(synonyms.size()>0){
						newQryNodeList.add(new QueryNode(")", NTypes.SYMBOL));
					}
				} else {
					newQryNodeList.add(new QueryNode(qryNode.getData(), NTypes.TERM, qryNode.getIsPreAnalyzed()));
				}
			} else {
				if(qryNode.getType().equals(NTypes.FIELD)) {
					if(((qryNode.getLocalParams()!=null) && (qryNode.getLocalParams().contains("syn=true")))|| synExp){
						fieldClauseHasSynExp = true;
					} else {
						fieldClauseHasSynExp = false;
					}
					newQryNodeList.add(qryNode);					
				} else {				
					newQryNodeList.add(qryNode);
				}
			}
		}		
		return newQryNodeList;		

	}

	public ArrayList<QueryNode> lemmaExpandChildNodes(ArrayList<QueryNode> qryNodeList, Boolean lemmaExp) {

		ArrayList<QueryNode> newQryNodeList =  new ArrayList<QueryNode>();
		Boolean fieldClauseHasLemmaExp = false;
		for (QueryNode qryNode : qryNodeList) {
			if(qryNode.getType().equals(NTypes.TERM) && !qryNode.getIsPhrase() && qryNode.getIsPreAnalyzed()){
				if(fieldClauseHasLemmaExp) {
					List<String> lemmas = PreAnalyzedQParserPlugin.lemmatizer.expand(qryNode.getData());
					//Removing duplicates
					LinkedHashSet hs = new LinkedHashSet();
					hs.addAll(lemmas);
					lemmas.clear();
					lemmas.addAll(hs);

					if(lemmas.size()>0){
						newQryNodeList.add(new QueryNode("(", NTypes.SYMBOL));
					}
					newQryNodeList.add(new QueryNode(qryNode.getData(), NTypes.TERM, qryNode.getIsPreAnalyzed()));

					for (String lemma : lemmas){
						newQryNodeList.add(new QueryNode(lemma, NTypes.TERM, qryNode.getIsPreAnalyzed()));
					}	

					if(lemmas.size()>0){
						newQryNodeList.add(new QueryNode(")", NTypes.SYMBOL));
					}
				}  else {
					newQryNodeList.add(new QueryNode(qryNode.getData(), NTypes.TERM, qryNode.getIsPreAnalyzed()));
				}
			} else {
				if(qryNode.getType().equals(NTypes.FIELD)) {
					if(((qryNode.getLocalParams()!=null) && (qryNode.getLocalParams().contains("lemma=true"))) || lemmaExp) {
						fieldClauseHasLemmaExp = true;
					} else {
						fieldClauseHasLemmaExp = false;
					}
					newQryNodeList.add(qryNode);					
				} else {				
					newQryNodeList.add(qryNode);
				}
			}
		}		
		return newQryNodeList;		
	}

	public ArrayList<QueryNode> accentRemovalChildNodes(ArrayList<QueryNode> qryNodeList, Boolean accRem) {

		ArrayList<QueryNode> newQryNodeList =  new ArrayList<QueryNode>();
		Boolean fieldClauseHasAccentRem = false;
		for (QueryNode qryNode : qryNodeList) {
			if(qryNode.getType().equals(NTypes.TERM) && !qryNode.getIsPhrase() && qryNode.getIsPreAnalyzed()){
				if(fieldClauseHasAccentRem) {
					newQryNodeList.add(new QueryNode(removeAccents(qryNode.getData()), NTypes.TERM, qryNode.getIsPreAnalyzed()));
				} else {
					newQryNodeList.add(new QueryNode(qryNode.getData(), NTypes.TERM, qryNode.getIsPreAnalyzed()));
				}
			} else {
				if(qryNode.getType().equals(NTypes.FIELD)) {
					if(((qryNode.getLocalParams()!=null) && (qryNode.getLocalParams().contains("accRed=true"))) || accRem){
						fieldClauseHasAccentRem = true;
					} else {
						fieldClauseHasAccentRem = false;
					}
					newQryNodeList.add(qryNode);					
				} else {				
					newQryNodeList.add(qryNode);
				}
			}
		}		
		return newQryNodeList;
	}

	public ArrayList<QueryNode> toJSONPreAnalyzedFormat(ArrayList<QueryNode> qryNodeList) {

		for (QueryNode qryNode : qryNodeList) {
			if(qryNode.getType().equals(NTypes.TERM)){
				if(qryNode.getIsPreAnalyzed()){
					if(qryNode.getIsPhrase()){
						qryNode.setData(transformTermToJSONArray(qryNode.getData()));
					} else if (!(qryNode.getData().contains("*"))&& !(qryNode.getData().contains("?"))){
						qryNode.setData(transformTermToJSON(qryNode.getData()));
					} 
				} 
			} 
		}

		return qryNodeList;
	}
}