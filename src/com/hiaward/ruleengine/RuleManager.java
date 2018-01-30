package com.hiaward.ruleengine;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.VelocityContext;

import com.hiaward.ruleengine.bean.FunctionConfig;
import com.hiaward.ruleengine.bean.RuleConfig;
import com.hiaward.ruleengine.bean.TemplateConfig;
import com.hiaward.ruleengine.cache.FunctionCache;
import com.hiaward.ruleengine.cache.GlobalCache;
import com.hiaward.ruleengine.cache.TemplateContextCache;
import com.hiaward.ruleengine.function.IFunctionable;
import com.hiaward.ruleengine.parser.RuleConfigParser;
import com.hiaward.ruleengine.utils.ClassUtils;

public class RuleManager {
	private static final Log log = LogFactory.getLog(RuleManager.class);
	private static RuleManager instance;

	public static RuleManager getInstance() {
		if (null == instance) {
			synchronized (RuleManager.class) {
				if (null == instance) {
					instance = new RuleManager();
					long start = System.currentTimeMillis();
					log.info("内部函数库初始化");
					InputStream inStream = ClassUtils.getReourceStream(
							"rule-config-1.0.xml", RuleManager.class);
					try {
						RuleConfigParser.getInstance().parse(inStream);
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						if (inStream != null) {
							try {
								inStream.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
					long end = System.currentTimeMillis();
					log.info("内部函数库初始化，共耗时：" + (end - start)+"毫秒");
				}
			}
		}
		return instance;
	}

	private RuleManager() {

	}

	/**
	 * 默认搜索跟目录下的rule-config.xml
	 */
	public void init() {
		long start = System.currentTimeMillis();
		log.info("用户函数库初始化");
		InputStream inStream = ClassUtils.getReourceStream("rule-config.xml",
				RuleManager.class);
		try {
			RuleConfigParser.getInstance().parse(inStream);
			FunctionCache.cache();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (inStream != null) {
				try {
					inStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		long end = System.currentTimeMillis();
		log.info("用户函数库初始化，共耗时：" + (end - start)+"毫秒");
	}

	public void init(String classpath) {
		long start = System.currentTimeMillis();
		log.info("用户函数库初始化");
		InputStream inStream = ClassUtils.getReourceStream(classpath,
				RuleManager.class);
		try {
			RuleConfigParser.getInstance().parse(inStream);
			FunctionCache.cache();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (inStream != null) {
				try {
					inStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		long end = System.currentTimeMillis();
		log.info("用户函数库初始化，共耗时：" + (end - start)+"毫秒");
	}

	/**运行配置文件中的模板
	 * @param params
	 * @param template
	 * @return java.lang.String
	 * @roseuid 550790150324
	 */
	public StringWriter execute(String template, Map<String, Object> params) {
		long start = System.currentTimeMillis();
		log.info("用户调用");
		createFunction(params);
		VelocityContext context = new VelocityContext(params,
				FunctionCache.getVelocityContext());
		StringWriter out = new StringWriter();

		GlobalCache.publicCache(template,context,out);
		if (out != null) {
			try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		long end = System.currentTimeMillis();
		log.info("用户调用，共耗时：" + (end - start)+"毫秒");
		return out;
	}

	private void createFunction(Map<String, Object> params) {
		Map<String, FunctionConfig> map = RuleConfig.getFunctions();
		Set<String> keys = map.keySet();
		for (Iterator<String> it = keys.iterator(); it.hasNext();) {
			String key = it.next();
			FunctionConfig function = map.get(key);
			IFunctionable fa = ClassUtils.newInstance(function.getClazz(),
					IFunctionable.class);
			if (null == fa) {
				return;
			}
			params.put(function.getName(), fa);
		}
	}
	/**
	 * 运行自己编写的模板
	 * @param params
	 * @param template
	 * @return
	 */
	public StringWriter execute(TemplateConfig template, Map<String, Object> params) {
		long start = System.currentTimeMillis();
		log.info("用户调用");
		createFunction(params);
		VelocityContext context = new VelocityContext(params,
				FunctionCache.getVelocityContext());
		StringWriter out = new StringWriter();
		TemplateContextCache.contextCache(template, context, out);
		if (out != null) {
			try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		long end = System.currentTimeMillis();
		log.info("用户调用，共耗时：" + (end - start)+"毫秒");
		return out;
	}
}
