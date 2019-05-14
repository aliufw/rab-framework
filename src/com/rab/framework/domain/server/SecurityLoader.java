package com.rab.framework.domain.server;

import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandlerFactory;

public class SecurityLoader extends URLClassLoader{

	public SecurityLoader(URL[] urls, ClassLoader parent,
			URLStreamHandlerFactory factory) {
		super(urls, parent, factory);
	}

	public SecurityLoader(URL[] urls, ClassLoader parent) {
		super(urls, parent);
	}

	public SecurityLoader(URL[] urls) {
		super(urls);
	}

	 public final Class<?> defineClass2(String name, byte[] b, int off, int len){
		 return super.defineClass(name, b, off, len);
	 }
	 
}
