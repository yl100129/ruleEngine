package com.hiaward.ruleengine.cache;

import java.io.StringWriter;
import java.util.Properties;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import com.hiaward.ruleengine.bean.TemplateConfig;

public class TemplateContextCache {
	public static void contextCache(TemplateConfig template,VelocityContext context, StringWriter out){
		Properties prop = new Properties();
		prop.put("resource.loader", "string");
		prop.put("string.resource.loader.class ", "org.apache.velocity.runtime.resource.loader.StringResourceLoader");
		Velocity.init(prop);
		Velocity.evaluate(context,out,"",template.getContext());
	}
}
