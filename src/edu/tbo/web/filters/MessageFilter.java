package edu.tbo.web.filters;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Component;

@Component
public class MessageFilter implements Filter {
	protected static Map<HttpSession, List<String>> userMessages;
	
	public MessageFilter() {
		userMessages = new HashMap<HttpSession, List<String>>();
	}
	
	private static List<String> getUserMessages(HttpSession session) {
		if(userMessages.containsKey(session)) {
			return userMessages.get(session);
		}
		
		return new ArrayList<String>();
	}
	
	public static void addUserMessage(HttpSession session, String... messages) {
		if(userMessages.containsKey(session)) {
			List<String> existingMessages = getUserMessages(session);
			existingMessages.addAll(Arrays.asList(messages));
			userMessages.replace(session, existingMessages);
		}
		else {
			userMessages.put(session, Arrays.asList(messages));
		}
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpSession session = ((HttpServletRequest) request).getSession();
		
		if(session != null) {
			List<String> messages = getUserMessages(session);
			request.setAttribute("cachedMessages", messages);
			userMessages.remove(session);
		}
		
		chain.doFilter(request, response);
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		// TODO Auto-generated method stub
		
	}
}
