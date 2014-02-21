custom-solr-parser
==================

This is a query preprocessor to create query for pre analyzed fields introduced in Solr 4.x
This jar need to be in the lib directory of your solr installation.

Solr config needs to be updated with the following section 
Also check the the guideline in Solr wiki for creating and using solr query parser  plugin.

Following is a sample config for the custom parser in Solrconfig.xml

 
 
  <queryParser name="ndqueryparser" class="org.apache.solr.search.PreAnalyzedQParserPlugin">
        <str name="modelsPath">/home/solradmin/solr/latest/resources/</str>
        <str name="synonymsPath">/home/solradmin/solr/latest/resources/synonyms.txt </str> <!--path to synonyms.txt file-->
        <str name="mappingsPath">/home/solradmin/solr/latest/resources/mapping-ISOLatin1Accent.txt</str><!--path to mapping-ISOLatin1Accent.txt file-->
        <bool name="showLemmas">false</bool> <!--set to true to store lemma expansion for query terms in log file -->
        <str name="lemmaLogPath">.</str> <!-- log file for lemma expansion -->
  </queryParser>

   <requestHandler name="/ndparser" class="solr.SearchHandler">

     <lst name="defaults">
       <str name="echoParams">explicit</str>
       <int name="rows">10</int>
       <str name="df">text</str>
       <str name="defType">ndqueryparser</str>
       <str name="spellcheck.dictionary">default</str>
       <str name="spellcheck">on</str>
       <str name="spellcheck.extendedResults">true</str>
       <str name="spellcheck.count">10</str>
       <str name="spellcheck.alternativeTermCount">5</str>
       <str name="spellcheck.maxResultsForSuggest">5</str>
       <str name="spellcheck.collate">true</str>
       <str name="spellcheck.collateExtendedResults">true</str>
       <str name="spellcheck.maxCollationTries">10</str>
       <str name="spellcheck.maxCollations">5</str> -->
    </lst>
    <shardHandlerFactory class="HttpShardHandlerFactory">
        <!-- <int name="socketTimeOut">1000</int>
        <int name="connTimeOut">5000</int> -->
        <int name="maxConnectionsPerHost">2048</int>
    </shardHandlerFactory>
    <arr name="last-components">
      <str>spellcheck</str>
    </arr>

    </requestHandler>
