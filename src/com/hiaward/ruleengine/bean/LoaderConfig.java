package com.hiaward.ruleengine.bean;

import java.util.Map;
import java.util.Properties;

public class LoaderConfig {

	private String type;
	private String scan;
	private Properties properties;
	private Map<String, TemplateConfig> templates;
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getScan() {
		return scan;
	}
	public void setScan(String scan) {
		this.scan = scan;
	}
	public Properties getProperties() {
		return properties;
	}
	public void setProperties(Properties properties) {
		this.properties = properties;
	}
	public Map<String, TemplateConfig> getTemplates() {
		return templates;
	}
	public void setTemplates(Map<String, TemplateConfig> templates) {
		this.templates = templates;
	}
	
}
