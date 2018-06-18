package com.demo.springboot.sleuth.exception;

import com.demo.springboot.sleuth.Utils;
import com.google.common.collect.Maps;

import java.util.Map;

import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ServerWebExchange;

/**
 * An implementation of {@link ErrorAttributes}. Provides the following attributes
 * when possible:
 * <ul>
 * <li>code - The application specific error code</li>
 * <li>error - The HTTP error reason phrase</li>
 * <li>debugMessage - A message which elaborates in detail why the request failed.</li>
 * </ul>
 * 
 * @author Niranjan Nanda
 */
public class DemoErrorAttributes implements ErrorAttributes {
	
	public static final String CLASS_NAME = DemoErrorAttributes.class.getCanonicalName();
	
	public static final String ERROR_ATTRIBUTE_KEY = "DemoErrorAttributes.ERROR";
	public static final String DEFAULT_ERROR_CODE = "_500000";
    public static final String DEFAULT_ERROR_MESSAGE = "Something went wrong while processing your request. Please contact support team.";
	
	private final boolean includeException;
	
	/**
	 * 
	 */
	public DemoErrorAttributes() {
		this.includeException = false;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<String, Object> getErrorAttributes(final ServerRequest request, final boolean includeStackTrace) {
		final AppException appException = (AppException) getError(request);
		
		final Map<String, Object> errorAttributesMap = Maps.newLinkedHashMap();
		errorAttributesMap.put("code", appException.getErrorCode());
		
		// Get HttpStatus from appException error code.
		final int httpStatusCode = Utils.FN_GET_HTTP_STATUS_CODE_FROM_ERROR_CODE.apply(appException.getErrorCode());
		final HttpStatus httpStatus = HttpStatus.resolve(httpStatusCode);
		
		// Store the HttpStatus in the map
		errorAttributesMap.put(HttpStatus.class.getCanonicalName(), httpStatus);
		
		// Populate "error"
		errorAttributesMap.put("error", httpStatus.getReasonPhrase());
		
		// Populate debug message from appException
		errorAttributesMap.put("debugMessage", appException.getErrorMessage());
		
		return errorAttributesMap;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Throwable getError(final ServerRequest request) {
		return (Throwable) request.attribute(ERROR_ATTRIBUTE_KEY)
				.orElse(new AppException(DEFAULT_ERROR_CODE, DEFAULT_ERROR_MESSAGE));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void storeErrorInformation(final Throwable error, final ServerWebExchange exchange) {
		AppException exceptionToStore = null;
		
		if (error == null) {
			exceptionToStore = new AppException(DEFAULT_ERROR_CODE, DEFAULT_ERROR_MESSAGE);
		} else if (error instanceof AppException) {
			exceptionToStore = (AppException) error;
		} else if (error.getCause() != null && error.getCause() instanceof AppException) {
			exceptionToStore = (AppException) error.getCause();
		} else {
			exceptionToStore = new AppException(DEFAULT_ERROR_CODE, DEFAULT_ERROR_MESSAGE);
		}

		exchange.getAttributes().putIfAbsent(ERROR_ATTRIBUTE_KEY, exceptionToStore);
	}

	/**
	 * Returns the value of includeException.
	 *
	 * @return the includeException
	 */
	public boolean isIncludeException() {
		return includeException;
	}
}
