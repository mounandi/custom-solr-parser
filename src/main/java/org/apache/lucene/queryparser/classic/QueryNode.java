package org.apache.lucene.queryparser.classic;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.lucene.queryparser.classic.QueryNode.NTypes;

public class QueryNode {

	private String data;
	private NTypes type;
	private String localParams;
	private Boolean isPhrase = false;
	private Boolean isPreAnalyzed = false;
	/*private Boolean isDate = false;
	private Boolean isNumber = false;*/
	
	public Boolean getIsPhrase() {
		return isPhrase;
	}

	public void setIsPhrase(Boolean isPhrase) {
		this.isPhrase = isPhrase;
	}

	public enum NTypes {
	    OPERATOR, TERM, SYMBOL, FIELD
	}
	

	public QueryNode() {
		super();		
	}

	public QueryNode(String data) {
		setData(data);
	}
	
	public QueryNode(String data, NTypes type) {
		setData(data);
		setType(type);
	}
	
	public QueryNode(String data, NTypes type, String localParams) {
		setData(data);
		setType(type);
		setLocalParams(localParams);
	}

	public QueryNode(String data, NTypes term, Boolean isPreAnalyzedTerm) {
		setData(data);
		setType(term);
		setIsPreAnalyzed(isPreAnalyzedTerm);
		/*setIsDate(isDate);
		setIsNumber(isNumber);*/
	}

	public String getData() {
		return this.data;
	}

	public void setData(String data2) {
		this.data = data2;
	}
	
	public NTypes getType() {
		return type;
	}

	public void setType(NTypes type) {
		this.type = type;
	}
	
	public String getLocalParams() {
		return localParams;
	}

	public void setLocalParams(String localParams) {
		this.localParams = localParams;
	}

	public String toString() {
		return getData().toString();
	}

	public boolean equals(QueryNode node) {
		return node.getData().equals(getData());
	}

	public int hashCode() {
		return getData().hashCode();
	}

	public void setIsPreAnalyzed(Boolean isPreAnalyzed) {
		this.isPreAnalyzed = isPreAnalyzed;
	}

	public Boolean getIsPreAnalyzed() {
		return isPreAnalyzed;
	}

	/*public void setIsDate(Boolean isDate) {
		this.isDate = isDate;
	}

	public Boolean getIsDate() {
		return isDate;
	}

	public void setIsNumber(Boolean isNumber) {
		this.isNumber = isNumber;
	}

	public Boolean getIsNumber() {
		return isNumber;
	}*/
}