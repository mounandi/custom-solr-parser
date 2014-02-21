package org.apache.solr.search;
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

import org.apache.solr.SolrTestCaseJ4;
import org.junit.Ignore;
import org.junit.Test;

public class PreAnalyzedQParserPluginTest extends SolrTestCaseJ4 {

	@Override
	public void setUp() throws Exception {
		super.setUp();
		initCore("solrconfig.xml", "schema.xml");
		assertU(adoc("id", "1",
				"subject1", "Paul Nelson is an early pioneer in the field of text retrival and has worked on search engines for over 20 years", 
				"subject2", "Solr is written in Java and runs as a standalone full-text search server within a servlet container such as Tomcat",
				"subject3", "Solr es escalable, permitiendo realizar bÃºsquedas distribuidas y replicaciÃ³n de Ã­ndices",
				"last_modified_tdt", "1995-12-31T23:59:59Z",
				"content_type", "nv-scale-shareddocs b -10",
				"count_i", "123",
				"pre_analyzed_j_4",
				"{\"v\":\"1\",\"str\":\"email - document alert - enhancements - new requests\",\"tokens\":[{\"t\":\"email\",\"e\":5,\"s\":0},{\"t\":\"-\",\"e\":7,\"s\":6},{\"t\":\"document\",\"e\":16,\"s\":8},{\"t\":\"alert\",\"e\":22,\"s\":17},{\"t\":\"-\",\"e\":24,\"s\":23},{\"t\":\"enhancements\",\"e\":37,\"s\":25},{\"t\":\"-\",\"e\":39,\"s\":38},{\"t\":\"new\",\"e\":43,\"s\":40},{\"t\":\"requests\",\"e\":52,\"s\":44}]}",
				
				"pre_analyzed_j_1", 				
				"{\"v\":\"1\",\"tokens\":[{\"s\":0,\"e\":4,\"t\":\"paul\"}," +
				"{\"s\":5,\"e\":11,\"t\":\"nelson\"}," +
				"{\"s\":12,\"e\":14,\"t\":\"is\"}," +
				"{\"s\":15,\"e\":17,\"t\":\"an\"}," +
				"{\"s\":18,\"e\":23,\"t\":\"early\"}," +
				"{\"s\":24,\"e\":31,\"t\":\"pioneer\"}," +
				"{\"s\":32,\"e\":34,\"t\":\"in\"}," +
				"{\"s\":35,\"e\":38,\"t\":\"the\"}," +
				"{\"s\":39,\"e\":44,\"t\":\"field\"}," +
				"{\"s\":45,\"e\":47,\"t\":\"of\"}," +
				"{\"s\":48,\"e\":52,\"t\":\"text\"}," +
				"{\"s\":53,\"e\":62,\"t\":\"retrieval\"}," +
				"{\"s\":63,\"e\":66,\"t\":\"and\"}," +
				"{\"s\":67,\"e\":70,\"t\":\"has\"}," +
				"{\"s\":71,\"e\":77,\"t\":\"worked\"}," +
				"{\"s\":78,\"e\":80,\"t\":\"on\"}," +
				"{\"s\":81,\"e\":87,\"t\":\"search\"}," +
				"{\"s\":88,\"e\":95,\"t\":\"engines\"}," +
				"{\"s\":96,\"e\":99,\"t\":\"for\"}," +
				"{\"s\":100,\"e\":104,\"t\":\"over\"}," +
				"{\"s\":105,\"e\":107,\"t\":\"20\"}," +
				"{\"s\":108,\"e\":113,\"t\":\"years\"}]}",

				"pre_analyzed_j_2", 
				"{\"v\":\"1\",\"tokens\":[{\"s\":0,\"e\":5,\"t\":\"solr\"}," +
				"{\"s\":6,\"e\":8,\"t\":\"is\"}," +
				"{\"s\":9,\"e\":16,\"t\":\"written\"}," +
				"{\"s\":17,\"e\":19,\"t\":\"in\"}," +
				"{\"s\":20,\"e\":24,\"t\":\"java\"}," +
				"{\"s\":25,\"e\":28,\"t\":\"and\"}," +
				"{\"s\":29,\"e\":33,\"t\":\"runs\"}," +
				"{\"s\":34,\"e\":36,\"t\":\"as\"}," +
				"{\"s\":37,\"e\":38,\"t\":\"a\"}," +
				"{\"s\":39,\"e\":49,\"t\":\"standalone\"}," +
				"{\"s\":50,\"e\":59,\"t\":\"full-text\"}," +
				"{\"s\":60,\"e\":66,\"t\":\"search\"}," +
				"{\"s\":67,\"e\":73,\"t\":\"server\"}," +
				"{\"s\":74,\"e\":80,\"t\":\"within\"}," +
				"{\"s\":81,\"e\":82,\"t\":\"a\"}," +
				"{\"s\":83,\"e\":90,\"t\":\"servlet\"}," +
				"{\"s\":91,\"e\":100,\"t\":\"container\"}," +
				"{\"s\":101,\"e\":105,\"t\":\"such\"}," +
				"{\"s\":106,\"e\":108,\"t\":\"as\"}," +
				"{\"s\":109,\"e\":115,\"t\":\"tomcat\"}]}",

				"pre_analyzed_j_3", 
				"{\"v\":\"1\",\"tokens\":[{\"s\":0,\"e\":4,\"t\":\"solr\"}," +
				"{\"s\":5,\"e\":7,\"t\":\"es\"}," +
				"{\"s\":8,\"e\":17,\"t\":\"escalable\"}," +
				"{\"s\":18,\"e\":19,\"t\":\",\"}," +
				"{\"s\":20,\"e\":31,\"t\":\"permitiendo\"}," +
				"{\"s\":32,\"e\":40,\"t\":\"realizar\"}," +
				"{\"s\":41,\"e\":50,\"t\":\"busquedas\"}," +
				"{\"s\":51,\"e\":63,\"t\":\"distribuidas\"}," +
				"{\"s\":64,\"e\":65,\"t\":\"y\"}," +
				"{\"s\":66,\"e\":77,\"t\":\"replicacion\"}," +
				"{\"s\":78,\"e\":80,\"t\":\"de\"}," +
				"{\"s\":81,\"e\":88,\"t\":\"indices\"}]}"

		));


		assertU(adoc("id", "2",
				"subject1", "SolrTM is the popular, blazing fast open source enterprise search platform from the Apache LuceneTM project",
				"subject2", "The Apache Software Foundation provides support for the Apache community of open-source software projects",
				"subject3", "Este es un pequeÃ±o corrector ortogrÃ¡fico ideal para escribir correctamente en espaÃ±ol",
				"count_i", "456",
				"content_type", "/ducot/n/0/j/y/~0105222056526072.nev|1",
				"pre_analyzed_j_1", 
				"{\"v\":\"1\",\"tokens\":[{\"s\":0,\"e\":6,\"t\":\"solrtm\"}," +
				"{\"s\":7,\"e\":9,\"t\":\"is\"}," +
				"{\"s\":10,\"e\":13,\"t\":\"the\"}," +
				"{\"s\":14,\"e\":21,\"t\":\"popular\"}," +
				"{\"s\":22,\"e\":23,\"t\":\",\"}," +
				"{\"s\":24,\"e\":31,\"t\":\"blazing\"}," +
				"{\"s\":32,\"e\":36,\"t\":\"fast\"}," +
				"{\"s\":37,\"e\":41,\"t\":\"open\"}," +
				"{\"s\":42,\"e\":48,\"t\":\"source\"}," +
				"{\"s\":49,\"e\":59,\"t\":\"enterprise\"}," +
				"{\"s\":60,\"e\":66,\"t\":\"search\"}," +
				"{\"s\":67,\"e\":75,\"t\":\"platform\"}," +
				"{\"s\":76,\"e\":80,\"t\":\"from\"}," +
				"{\"s\":81,\"e\":84,\"t\":\"the\"}," +
				"{\"s\":85,\"e\":91,\"t\":\"apache\"}," +
				"{\"s\":92,\"e\":100,\"t\":\"lucenetm\"}," +
				"{\"s\":101,\"e\":108,\"t\":\"project\"}]}",

				"pre_analyzed_j_2", 
				"{\"v\":\"1\",\"tokens\":[{\"s\":0,\"e\":4,\"t\":\"the\"}," +
				"{\"s\":5,\"e\":11,\"t\":\"apache\"}," +
				"{\"s\":12,\"e\":20,\"t\":\"software\"}," +
				"{\"s\":21,\"e\":31,\"t\":\"foundation\"}," +
				"{\"s\":32,\"e\":40,\"t\":\"provides\"}," +
				"{\"s\":41,\"e\":48,\"t\":\"support\"}," +
				"{\"s\":49,\"e\":51,\"t\":\"for\"}," +
				"{\"s\":52,\"e\":55,\"t\":\"the\"}," +
				"{\"s\":56,\"e\":62,\"t\":\"apache\"}," +
				"{\"s\":63,\"e\":72,\"t\":\"community\"}," +
				"{\"s\":73,\"e\":75,\"t\":\"of\"}," +
				"{\"s\":76,\"e\":87,\"t\":\"open-source\"}," +
				"{\"s\":88,\"e\":96,\"t\":\"software\"}," +
				"{\"s\":97,\"e\":105,\"t\":\"projects\"}]}",

				"pre_analyzed_j_3", 
				"{\"v\":\"1\",\"tokens\":[{\"s\":0,\"e\":5,\"t\":\"este\"}," +
				"{\"s\":6,\"e\":8,\"t\":\"es\"}," +
				"{\"s\":9,\"e\":11,\"t\":\"un\"}," +
				"{\"s\":12,\"e\":19,\"t\":\"pequeno\"}," +
				"{\"s\":20,\"e\":29,\"t\":\"corrector\"}," +
				"{\"s\":30,\"e\":42,\"t\":\"ortografico\"}," +
				"{\"s\":43,\"e\":48,\"t\":\"ideal\"}," +
				"{\"s\":49,\"e\":53,\"t\":\"para\"}," +
				"{\"s\":54,\"e\":62,\"t\":\"escribir\"}," +
				"{\"s\":63,\"e\":76,\"t\":\"correctamente\"}," +
				"{\"s\":77,\"e\":79,\"t\":\"en\"}," +
				"{\"s\":80,\"e\":87,\"t\":\"espanol\"}]}"
		));


		assertU(commit());
		assertU(optimize());
	}

	@Override
	public void tearDown() throws Exception {
		super.tearDown();
	}

	/** Tests to be executed **
	 * 
	 * Term Queries single fields +- local params
	 * Term Queries multiple fields  +- local params
	 * Term Queries synonyms expansion
	 * Term Queries lemmatizer
	 * Term Queries char mapping
	 * Phrase Queries +- local params
	 * Phrase Queries + Term Queries
	 * Wildcard Queries +- local params
	 * Wilcard Queries + Term Queries
	 * Wildcard Queries + Term Queries + Phrase Queries
	 * 
	 */
	
	@Ignore
	//@Test
	public void testQueries() throws Exception{

		/**
		 * Term queries
		 * 
		 * */
		assertQ(req("q", "pre_analyzed_j_1:(aÃ±o search)^10000" )
				, "//result[@numFound='2']"
		);

		assertQ(req("q", "pre_analyzed_j_1:(early search)^10000" )
				, "//result[@numFound='2']"
		);

		assertQ(req("q", "pre_analyzed_j_1:(early search)" )
				, "//result[@numFound='2']"
		);
		
		/***
		 * Proximity queries
		 * 
		 * */
		assertQ(req("q", "pre_analyzed_j_1:\"hypertext classification\"~5" )
				, "//result[@numFound='0']"
		);
		assertQ(req("q", "pre_analyzed_j_1:(\"hypertext classification\"~5 OR \"classification hypertext\"~5)" )
				, "//result[@numFound='0']"
		);
		
		/***
		 * Term Queries With Lemma And/Or Syn
		 * 
		 * */		
		/*assertQ(req("q", "{!lemma=true syn=true} pre_analyzed_j_1^10000:(earlier search)" )
				, "//result[@numFound='2']"
		);

		assertQ(req("q", "{!lemma=true syn=true} pre_analyzed_j_1^10000:(early search)" )
				, "//result[@numFound='2']"
		);

		assertQ(req("q", "{!lemma=true syn=true} pre_analyzed_j_1:(early search)" )
				, "//result[@numFound='2']"
		);*/
		
		/***
		 * Phrase queries
		 * 
		 * */
		

		assertQ(req("q", "pre_analyzed_j_1:((\"early pioneer\") AND text OR logic)^10000" )
				, "//result[@numFound='1']"
		);
		
		/***
		 * Wildcard queries
		 * 
		 * */
		
		assertQ(req("q", "*:*", "lemma", "true")
				, "//result[@numFound='2']"
		);
		
		assertQ(req("q", "{!lemma=true syn=false} pre_analyzed_j_1:*", "lemma", "true")
				, "//result[@numFound='2']"
		);
		
		assertQ(req("q", "pre_analyzed_j_3:*", "lemma", "true")
				, "//result[@numFound='2']"
		);
		
		assertQ(req("q", "{!lemma=true syn=false} pre_analyzed_j_2:foundation AND pre_analyzed_j_1:*", "lemma", "true")
				, "//result[@numFound='1']"
		);
		
		assertQ(req("q", "{!lemma=true syn=false} pre_analyzed_j_1:work english eng*", "lemma", "true")
				, "//result[@numFound='1']"
		);
	}
	
	@Test
	public void testSingleQuery() throws Exception{
		
		
		assertQ(req("q", "pre_analyzed_j_4:\"email - document alert - enhancements - new requests\"", "debugQuery", "true")
				, "//result[@numFound='1']"
        );
		
		assertQ(req("q", "pre_analyzed_j_4:\"document alert\"", "debugQuery", "true")
				, "//result[@numFound='1']"
        );
		
		assertQ(req("q", "content_type:\"nv-scale-shareddocs b -10\"", "debugQuery", "true")
				, "//result[@numFound='1']"
        );
		
		assertQ(req("fq", "content_type:\"nv-scale-shareddocs b -10\"", "debugQuery", "true", "q", "*:*")
				, "//result[@numFound='1']"
        );
		
		
		assertQ(req("q", "content_type:\"/ducot/n/0/j/y/~0105222056526072.nev|1\" OR content_type:4833-9890-9952", "debugQuery", "true")
				, "//result[@numFound='1']"
        );
		
		assertQ(req("q", "content_type:\"/ducot/n/0/j/y/~0105222056526072.nev|1\" OR content_type:4833-9890-9952", "debugQuery", "true")
				, "//result[@numFound='1']"
        );
		
		assertQ(req("fq", "content_type:\"/ducot/n/0/j/y/~0105222056526072.nev|1\" OR content_type:4833-9890-9952", "debugQuery", "true", "q", "*:*")
				, "//result[@numFound='1']"
        );
		
		assertQ(req("fq", "(content_type:(en AND fr) OR pre_analyzed_j_1:(import AND export OR (here AND there AND \"here and there\" OR \"I am :looking for:\"))) AND pre_analyzed_j_1:(find it all)", "debugQuery", "true", "q", "*:*")
				, "//result[@numFound='0']"
        );
		
		assertQ(req("q", "(content_type:(en AND fr) OR pre_analyzed_j_1:(import AND export OR (here AND there AND \"here and there\" OR \"I am :looking for:\"))) AND pre_analyzed_j_1:(find it all)", "debugQuery", "true")
				, "//result[@numFound='0']"
        );
				
		assertQ(req("q", "count_i:[1 TO 200]", "debugQuery", "true")
                , "//result[@numFound='1']"
        );
		
		assertQ(req("q", "count_i:[1 TO 1000]", "debugQuery", "true")
                , "//result[@numFound='2']"
        );
		
		assertQ(req("q", "count_i:123", "debugQuery", "true")
                , "//result[@numFound='1']"
        );
		
		assertQ(req("q", "last_modified_tdt:[1995-12-31T23:59:59.999Z TO NOW]", "debugQuery", "true")
                , "//result[@numFound='0']"
        );
		
		assertQ(req("q", "last_modified_tdt:[1995-12-31T23:59:59Z TO *]", "debugQuery", "true")
                , "//result[@numFound='1']"
        );
		
		assertQ(req("q", "last_modified_tdt:[* TO 1995-12-31T23:59:59Z]", "debugQuery", "true")
                , "//result[@numFound='1']"
        );
		
		assertQ(req("q", "last_modified_tdt:[1995-12-31T23:59:59Z TO 1995-12-31T23:59:59Z]", "debugQuery", "true")
                , "//result[@numFound='1']"
        );
		
		assertQ(req("q", "last_modified_tdt:\"1995-12-31T23:59:59Z\"")
                , "//result[@numFound='1']"
        );
		
		assertQ(req("q", "{!lemma=true syn=true} -pre_analyzed_j_1:(test OR technologies) AND -id:\"/Ducot7/j/d/3/r/~140129163304922.nev|1\"", "fq", "pre_analyzed_j_2:solr", "debugQuery", "true")
                , "//result[@numFound='1']"
        );

		
		assertQ(req("q", "{!lemma=true syn=true} -pre_analyzed_j_1:(popular OR technologies)", "fq", "pre_analyzed_j_2:solr", "debugQuery", "true")
				, "//result[@numFound='1']"
		);
		
		assertQ(req("q", "id:1", "lemma", "false", "debugQuery", "true" )
				, "//result[@numFound='1']"
		);
		
		assertQ(req("q", "id:1", "lemma", "false", "fq", "-pre_analyzed_j_2:search" )
				, "//result[@numFound='0']"
		);
		
		assertQ(req("q", "{!lemma=true syn=true} pre_analyzed_j_1:(search OR technologies)")
				, "//result[@numFound='2']"
		);
		
		assertQ(req("q", "pre_analyzed_j_1:search technologies", "lemma", "true")
				, "//result[@numFound='2']"
		);
		
		
		assertQ(req("q", "{!lemma=true syn=true} pre_analyzed_j_1:\"search technologies\"", "lemma", "true", "debugQuery", "true")
				, "//result[@numFound='0']"
		);
		
		assertQ(req("q", "pre_analyzed_j_1:se* technologies", "lemma", "true")
				, "//result[@numFound='2']"
		);
		
		assertQ(req("q", "pre_analyzed_j_1:cÃ©line", "debugQuery", "true")
				, "//result[@numFound='0']"
		);
		
		assertQ(req("q", "pre_analyzed_j_1:(\"early pioneer\")" )
				, "//result[@numFound='1']"
		);
		
		assertQ(req("q", "pre_analyzed_j_1:ea*ly" )
				, "//result[@numFound='1']"
		);
		
		assertQ(req("q", "pre_analyzed_j_1:nathan AND pre_analyzed_j_2:EKSTROM" )
				, "//result[@numFound='0']"
		);
		
		assertQ(req("q", "pre_analyzed_j_1:search AND pre_analyzed_j_1:technologies AND pre_analyzed_j_1:search AND pre_analyzed_j_1:engine" )
				, "//result[@numFound='0']"
		);
		
		assertQ(req("q", "(pre_analyzed_j_2:sha?ed)" )
				, "//result[@numFound='0']"
		);	
		
		assertQ(req("q", "{!lemma=true syn=true} pre_analyzed_j_2:pixima" )
				, "//result[@numFound='0']"
		);
		
		assertQ(req("q", "((pre_analyzed_j_1:Breitenbucher AND pre_analyzed_j_2:James) AND (pre_analyzed_j_3:Pat AND pre_analyzed_j_1:Haley))") 
				, "//result[@numFound='0']"
		);
		
		assertQ(req("q", "(pre_analyzed_j_1:niño)") 
				, "//result[@numFound='0']"
		);
		
		assertQ(req("q", "pre_analyzed_j_1:â AND pre_analyzed_j_1:ê AND pre_analyzed_j_1:î AND pre_analyzed_j_1: ô AND pre_analyzed_j_1: û AND pre_analyzed_j_1:â AND pre_analyzed_j_1:ê AND pre_analyzed_j_1:î AND pre_analyzed_j_1: ô AND pre_analyzed_j_1:û") 
				, "//result[@numFound='0']"
		);
		
		assertQ(req("q", "pre_analyzed_j_1:search pixima technologies", "lemma", "true", "syn", "true", "debugQuery", "true")
				, "//result[@numFound='2']"
		);
	}
}