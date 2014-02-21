package org.apache.solr.search;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.solr.SolrTestCaseJ4;
import org.junit.Ignore;
import org.junit.Test;


public class LemmatizerTest  extends SolrTestCaseJ4  {
	
	@Ignore
	public void lemmatizerExpansionTest() throws Exception {
		
		super.setUp();
		initCore("solrconfig.xml", "schema.xml");
		
		String logFile = "./src/test/resources/logs/localhost_access_log.2013-02-13cp.txt";
		BufferedReader in = new BufferedReader(new FileReader(logFile));
		String fieldStr = "&([^&])*_tmulti_idx:";
		String fieldQueryStr = fieldStr + ".*?&";		
		Pattern fieldQueryPattern = Pattern.compile(fieldQueryStr);
		
		while (in.ready()) {
		  String line = in.readLine();
		  String[] logMsgParts = line.split(" ");
		  
		  Matcher fieldQueryMatcher = fieldQueryPattern.matcher(logMsgParts[logMsgParts.length - 2]);
		  
		  while (fieldQueryMatcher.find()) {
			  String term = fieldQueryMatcher.group().replaceAll(fieldStr, "").replace("&", "");
			  String qValue = "{!lemma=true} pre_analyzed_j_1:" + term;
			  assertQ(req("q", qValue, "showLemmas", "true")
							, "//result[@numFound='0']");			  
		  }
		  
		  
		}
		in.close();
	}

}
