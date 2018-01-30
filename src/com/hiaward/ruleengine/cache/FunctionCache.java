package com.hiaward.ruleengine.cache;

import java.io.BufferedWriter;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.runtime.resource.loader.StringResourceLoader;
import org.apache.velocity.runtime.resource.util.StringResourceRepository;

import com.hiaward.ruleengine.bean.FunctionConfig;
import com.hiaward.ruleengine.bean.RuleConfig;
import com.hiaward.ruleengine.exception.IExceptionCode;
import com.hiaward.ruleengine.function.IFunctionable;
import com.hiaward.ruleengine.utils.ClassUtils;

public class FunctionCache implements IExceptionCode{
	private static final Log log = LogFactory.getLog(ClassUtils.class);
	private static VelocityContext velocityContext = new VelocityContext();

	public static VelocityContext getVelocityContext() {
		return velocityContext;
	}

	public static void cache() {
		String macros=RuleConfig.getMacros();
		chcheMacro(macros);
		if (RuleConfig.SCOPE_SINGLETON.equals(RuleConfig.getScope())) {
			Map<String, FunctionConfig> map = RuleConfig.getFunctions();
			Set<String> keys = map.keySet();
			for (Iterator<String> it = keys.iterator(); it.hasNext();) {
				String key = it.next();
				FunctionConfig fun = map.get(key);
				chcheFunction(fun);
			}
		} else {
			Map<String, FunctionConfig> map = RuleConfig.getFunctions();
			Set<String> keys = map.keySet();
			for (Iterator<String> it = keys.iterator(); it.hasNext();) {
				String key = it.next();
				FunctionConfig fun = map.get(key);
				if (fun.getCache()) {
					RuleConfig.removeFunction(key);
					chcheFunction(fun);
				}
			}
		}
	}

	private static void chcheFunction(FunctionConfig function) {
		IFunctionable fa = ClassUtils.newInstance(function.getClazz(),
				IFunctionable.class);
		if (null == fa) {
			if (log.isInfoEnabled()) {
				log.info(ERROR_CODE_NULLCLASS+function.getClazz()+"不存在");
			}
			return;
		}
		velocityContext.put(function.getName(), fa);
	}
	
	private static void chcheMacro(String macros){
		try{
			Properties prop = new Properties();
			prop.setProperty("resource.loader", "string");
			prop.setProperty("string.resource.loader.description", "Velocity StringResource loader");
			prop.setProperty("string.resource.loader.class", "org.apache.velocity.runtime.resource.loader.StringResourceLoader");
			prop.setProperty("string.resource.loader.repository.name", "repo");
			prop.setProperty("string.resource.loader.repository.static", "true");
	
			Velocity.init(prop);
	
			StringResourceRepository repo = StringResourceLoader.getRepository("repo");
	
			repo.putStringResource("macro", macros);
	
			Template template = Velocity.getTemplate("macro");
	
			BufferedWriter writer = writer = new BufferedWriter(new StringWriter());
	
			if (template != null)
				template.merge(velocityContext, writer);
			writer.flush();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
