custom-solr-parser
==================

This is a query preprocessor to create query for pre analyzed fields introduced in Solr 4.x
This jar need to be in the lib directory of your solr installation.

Solr config needs to be updated with the following section 
Also check the the guideline in Solr wiki for creating and using solr query parser  plugin.

Following is a sample config for the custom parser in Solrconfig.xml. 

 

  <queryParser name="ndqueryparser" class="org.apache.solr.search.PreAnalyzedQParserPlugin">
        <str name="modelsPath">../solr/latest/resources/</str>
        <str name="synonymsPath">../solr/latest/resources/synonyms.txt </str> 
        <str name="mappingsPath">../solr/latest/resources/mapping-ISOLatin1Accent.txt</str>
        <bool name="showLemmas">false</bool> <!--set to true to store lemma expansion for query terms in log file -->
        <str name="lemmaLogPath">.</str>
  </queryParser>
   <requestHandler name="/ndparser" class="solr.SearchHandler">
     <lst name="defaults">
       <str name="echoParams">explicit</str>
       <int name="rows">10</int>
       <str name="df">textpreanalyzed</str>
       <str name="defType">ndqueryparser</str>
    </lst>
    <arr name="last-components">
      <str>spellcheck</str>
    </arr>
  </requestHandler>

