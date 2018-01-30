package com.hiaward.ruleengine.parser;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.hiaward.ruleengine.bean.FunctionConfig;
import com.hiaward.ruleengine.bean.LoaderConfig;
import com.hiaward.ruleengine.bean.RuleConfig;
import com.hiaward.ruleengine.bean.TemplateConfig;
import com.hiaward.ruleengine.utils.ClassUtils;

public class RuleConfigParser {

	private static RuleConfigParser instance;

	public static RuleConfigParser getInstance() {
		if (null == instance) {
			synchronized (RuleConfigParser.class) {
				if (null == instance) {
					instance = new RuleConfigParser();
				}
			}
		}
		return instance;
	}
	
	@SuppressWarnings("unchecked")
	public void parseFunction(Element functions){
		String scan = functions.attributeValue("scan");
		if (StringUtils.isNotBlank(scan)) {
			String cache = functions.attributeValue("cache");
			Boolean bCache = "true".equals(cache) ? true : false;
			File file = ClassUtils.getResourceFile(scan,
					RuleConfigParser.class);
			eachClasspath(scan, file, bCache);
		}

		List<Element> functionList = functions.selectNodes("function");
		for (int j = 0; j < functionList.size(); j++) {
			Element function = functionList.get(j);
			String clazz = function.attributeValue("class");
			if (StringUtils.isNotBlank(clazz)) {
				String name = function.attributeValue("name");
				String cache = function.attributeValue("cache");
				Boolean bCache = "true".equals(cache) ? true : false;
				if (StringUtils.isBlank(name)) {
					name = clazz.substring(clazz.lastIndexOf(".") + 1);
				}
				FunctionConfig f = new FunctionConfig();
				f.setName(name);
				f.setClazz(clazz);
				f.setCache(bCache);
				RuleConfig.addFunction(f);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public LoaderConfig parseLoader(Element loaders){
		LoaderConfig loaderConfig=new LoaderConfig();
		String type = loaders.attributeValue("type");
		if (StringUtils.isNotBlank(type)) {
			if(type.equalsIgnoreCase("string")){
				loaderConfig.setType("string");
			}else if(type.equalsIgnoreCase("url")){
				loaderConfig.setType("url");
			}else if(type.equalsIgnoreCase("datasource")){
				loaderConfig.setType("datasource");
			}else if(type.equalsIgnoreCase("file")){
				loaderConfig.setType("file");
			}else if(type.equalsIgnoreCase("jar")){
				loaderConfig.setType("jar");
			}else if(type.equalsIgnoreCase("class")){
				loaderConfig.setType("class");
			}else{
				System.out.println("loader标签的type属性的值不符合规范！");
			}
			Properties properties = new Properties();
			List<Element> propsList = loaders.selectNodes("props");
			if(0 != propsList.size()){
				for(int i = 0; i < propsList.size(); i++){
					Element props = propsList.get(i);
					List<Element> propertysList = props.selectNodes("property");
					for(int j = 0; j < propertysList.size(); j++){
						Element propertys = propertysList.get(j);
						String name = propertys.attributeValue("name");
						String value = propertys.getStringValue();
						properties.put(name, value);
					}
				}
			}
			loaderConfig.setProperties(properties);
			
			List<Element> templateList = loaders.selectNodes("template");
			Map<String,TemplateConfig> templateConfigMap=new HashMap<String, TemplateConfig>();
			for(int j = 0; j < templateList.size(); j++){
				Element template = templateList.get(j);
				templateConfigMap.put(parseTemplate(template).getName(), parseTemplate(template));
			}
			loaderConfig.setTemplates(templateConfigMap);
		}
		return loaderConfig;
	}
	
	public TemplateConfig parseTemplate(Element templates){
		TemplateConfig templateConfig=new TemplateConfig();
		//解析using属性
		String using = templates.attributeValue("using");
		if(using.equalsIgnoreCase("on") || StringUtils.isBlank(using)){
			templateConfig.setUsing("on");
			//解析name属性
			String name = templates.attributeValue("name");
			if(StringUtils.isNotBlank(name)){
				templateConfig.setName(name);
			}
			//解析context属性
			String context = templates.getStringValue();
			templateConfig.setContext(context.trim());
		}else if(using.equalsIgnoreCase("off")){
			templateConfig.setUsing("off");
		}else{
			System.out.println("template标签的using属性的值不符合规范！");
		}
		return templateConfig;
	}
	
	@SuppressWarnings("unchecked")
	public void parse(InputStream inStream) throws Exception {
		SAXReader reader = new SAXReader();
		Document document = reader.read(inStream);
		Element root = document.getRootElement();
		Element scopeElement = (Element) root.selectSingleNode("scope");
		if (null != scopeElement) {
			String strScope = scopeElement.getStringValue();
			if (RuleConfig.SCOPE_PROTOTYPE.equals(strScope)
					|| RuleConfig.SCOPE_SINGLETON.equals(strScope)) {
				RuleConfig.setScope(strScope);
			} else {
				RuleConfig.setScope(RuleConfig.SCOPE_PROTOTYPE);
			}
		} else {
			RuleConfig.setScope(RuleConfig.SCOPE_PROTOTYPE);
		}

		List<Element> removeFunctionList = root
				.selectNodes("removefunctions/function");
		for (int i = 0; i < removeFunctionList.size(); i++) {
			String key = removeFunctionList.get(i).attributeValue("name");
			if (StringUtils.isNotBlank(key)) {
				RuleConfig.removeFunction(key);
			}
		}

		List<Element> functionsList = root.selectNodes("functions");
		for (int i = 0; i < functionsList.size(); i++) {
			Element functions = functionsList.get(i);
			 parseFunction(functions);
		}
		
		List<Element> loadersList = root.selectNodes("loader");
		Map<String,LoaderConfig> loaderConfigMap=RuleConfig.getLoaders();
		for (int i = 0; i < loadersList.size(); i++) {
			Element loaders = loadersList.get(i);
			LoaderConfig lc=parseLoader(loaders);
			Map<String,TemplateConfig> map = lc.getTemplates();
			Set<String> keys = map.keySet();
			for(Iterator<String> it = keys.iterator();it.hasNext();){
				String key = it.next();
				TemplateConfig value = map.get(key);
				loaderConfigMap.put(value.getName(), lc);
			}
		}
		RuleConfig.setLoaders(loaderConfigMap);
		
		List<Element> macrosList = root.selectNodes("macro");
		for (int i = 0; i < macrosList.size(); i++) {
			Element macros = macrosList.get(i);
			//解析context属性
			String context = macros.getStringValue();
			RuleConfig.setMacros(context.trim());
		}
	}

	private void eachClasspath(String pkg, File file, Boolean cache) {
		if (file == null) {
			return;
		}
		if (file.isDirectory()) {
			File[] f = file.listFiles();
			for (int i = 0; i < f.length; i++) {
				if (f[i].isDirectory()) {
					String pg = null;
					if (pkg.endsWith("/")) {
						pg = pkg + f[i].getName();
					} else {
						pg = pkg + "/" + f[i].getName();
					}
					eachClasspath(pg, f[i], cache);
				} else {
					eachClasspath(pkg, f[i], cache);
				}
			}
		} else {
			String fileName = file.getName();
			if (fileName.endsWith(".class")) {
				FunctionConfig function = new FunctionConfig();
				fileName = fileName.substring(0, fileName.indexOf("."));
				String clazz = null;
				if (pkg.endsWith("/")) {
					clazz = ClassUtils.replaceClasspath(pkg + fileName);
				} else {
					clazz = ClassUtils.replaceClasspath(pkg + "/" + fileName);
				}
				function.setName(fileName);
				function.setClazz(clazz);
				function.setCache(cache);
				RuleConfig.addFunction(function);
			}
		}
	}
}
