package com.hiaward.ruleengine.function.impl;

import com.hiaward.ruleengine.function.IFunctionable;

public class SQLUtils implements IFunctionable{

	public void save(String sql){
		System.out.println("保存对象");
	}
	
	public Object execute(String sql,Object... args){
		System.out.println("执行sql语句");
		return new Object();
	}
}
