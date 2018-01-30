package com.hiaward.ruleengine.bean;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.hiaward.ruleengine.utils.ClassUtils;

public class RuleConfig {
	public static final String SCOPE_SINGLETON = "singleton";
	public static final String SCOPE_PROTOTYPE = "prototype";
	private static String scope;
	private static Map<String, FunctionConfig> functions = new HashMap<String, FunctionConfig>();
	private static Map<String, LoaderConfig> loaders = new HashMap<String, LoaderConfig>();
	public static  String macros;
	public static String getMacros() {
		return macros;
	}

	public static void setMacros(String macros) {
		RuleConfig.macros = macros;
	}

	public static String getScope() {
		return scope;
	}

	public static void setScope(String scope) {
		RuleConfig.scope = scope;
	}

	public static void addFunction(FunctionConfig function) {
		if (function == null) {
			return;
		}
		if (StringUtils.isBlank(function.getClazz())) {
			return;
		}
		String name = function.getName();
		if (StringUtils.isBlank(name)) {
			name = ClassUtils.getClassName(function.getClazz());
		}
		functions.put(name, function);
	}

	public static void removeFunction(String key) {
		functions.remove(key);
	}

	public static Map<String, FunctionConfig> getFunctions() {
		return functions;
	}


	public static Map<String, LoaderConfig> getLoaders() {
		return loaders;
	}

	public static void setLoaders(Map<String, LoaderConfig> loaders) {
		RuleConfig.loaders = loaders;
	}

	public static void setFunctions(Map<String, FunctionConfig> functions) {
		RuleConfig.functions = functions;
	}

}
