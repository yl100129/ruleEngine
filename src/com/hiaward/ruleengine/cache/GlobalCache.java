package com.hiaward.ruleengine.cache;

import java.io.StringWriter;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.runtime.resource.loader.StringResourceLoader;
import org.apache.velocity.runtime.resource.util.StringResourceRepository;

import com.hiaward.ruleengine.bean.LoaderConfig;
import com.hiaward.ruleengine.bean.RuleConfig;
import com.hiaward.ruleengine.bean.TemplateConfig;

public class GlobalCache{
	static Map<String, LoaderConfig> loaderMap = RuleConfig.getLoaders();

	public static void publicCache(String templateName,VelocityContext context, StringWriter out) {
		LoaderConfig loaderConfig=null;
		TemplateConfig templateConfig=null;
		
		for (Entry<String, LoaderConfig> entry : loaderMap.entrySet()) {
			if(entry.getKey().equals(templateName)){
				loaderConfig=entry.getValue();
				 templateConfig=loaderConfig.getTemplates().get(templateName);
			} 
		}		
		 Properties prop = loaderConfig.getProperties();
		 Velocity.init(prop);
		 Template template = executeVelocity(templateName, loaderConfig, templateConfig);
		 template.merge(context, out);
	}
	
	private static Template executeVelocity(String templateName,LoaderConfig loaderConfig,TemplateConfig templateConfig){
		String type = loaderConfig.getType();
		Template template = null;
		if("string" == type){
       	 	StringResourceRepository repo = StringResourceLoader
   				 .getRepository("repo");
       	 	String templateContext=templateConfig.getContext();
       	 	repo.putStringResource(templateName, templateContext);
       	 	template = Velocity.getTemplate(templateName,"utf-8");
        }else if("url" == type){
       	 
        }else if("datasource" == type){
       	 
        }else if("file" == type){
       	 
        }else if("jar" == type){
       	 
        }else if("class" == type){
       	 
        }else{
       	 System.out.println("loaderConfig中type值非法！");
        }
		return template;
	}
}
