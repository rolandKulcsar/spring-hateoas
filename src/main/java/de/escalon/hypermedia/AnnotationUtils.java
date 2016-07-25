package de.escalon.hypermedia;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * Created by Dietrich on 05.04.2015.
 */
public class AnnotationUtils {

	private AnnotationUtils() {}

	public static Method getAnnotatedMethod(Class<?> clazz, Class<? extends Annotation> annotation) {
		Method[] methods = clazz.getMethods();
		Method ret = null;
		for (Method method : methods) {
			if (method.getAnnotation(annotation) != null) {
				ret = method;
				break;
			}
		}
		return ret;
	}
}
