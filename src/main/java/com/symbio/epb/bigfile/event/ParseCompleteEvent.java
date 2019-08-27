package com.symbio.epb.bigfile.event;

import java.util.Map;

import org.springframework.context.ApplicationEvent;
/**
 * 
 * @author Yao Pan
 *
 */

public class ParseCompleteEvent extends ApplicationEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ParseCompleteEvent(Map<String, Object> source) {
		super(source);
	}

}
