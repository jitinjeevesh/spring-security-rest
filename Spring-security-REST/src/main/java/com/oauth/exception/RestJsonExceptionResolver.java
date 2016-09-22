package com.oauth.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.handler.AbstractHandlerExceptionResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

// TODO: Auto-generated Javadoc

/**
 * The Class RestJsonExceptionResolver.
 */
public class RestJsonExceptionResolver extends AbstractHandlerExceptionResolver
  implements View
{
  
  /** The Constant LOG. */
  private static final Logger LOG = LoggerFactory.getLogger(RestJsonExceptionResolver.class);
  
  /** The Constant MAPPER. */
  private static final ObjectMapper MAPPER = new ObjectMapper();
  
  /** The Constant KEY_ERROR_OBJECT. */
  private static final String KEY_ERROR_OBJECT = "error";
  
  /** The Constant httpErrorCodeMap. */
  private static final Map<String, Integer> httpErrorCodeMap = new HashMap<>();
  
  /** The error id generator. */
  private ErrorIdGenerator errorIdGenerator = new DefaultErrorIdGenerator();

  /**
   * Instantiates a new rest json exception resolver.
   */
  public RestJsonExceptionResolver()
  {
    MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
  }

  /* (non-Javadoc)
   * @see org.springframework.web.servlet.handler.AbstractHandlerExceptionResolver#doResolveException(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.Object, java.lang.Exception)
   */
  protected ModelAndView doResolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception e)
  {
    ModelAndView mav = new ModelAndView(this);
    RestError error = new RestError();

    if (e != null)
    {
      error.setMessage(e.getLocalizedMessage());

      /*if ((e instanceof BusinessException))
        error.setErrorCode(((BusinessException)e).getErrorCode());
      else if (((e instanceof AuthenticationException)) && (((AuthenticationException)e).getErrorCode() != 0))
      {
        error.setErrorCode(((AuthenticationException)e).getErrorCode());
      }*/
      error.setErrorCode(getErrorCode(e));
      httpErrorCodeMap.put(ObjectNotFoundException.class.getName(), 401);
  //    httpErrorCodeMap.put(UnModificationException.class.getName(), 304);
      Integer httpCode = (Integer)httpErrorCodeMap.get(e.getClass().getName());
      if (httpCode == null) {
        httpCode = Integer.valueOf(500);

        StackTraceElement[] trace = null;
        StringBuilder sb = new StringBuilder();

        if (e.getCause() == null) {
          trace = e.getStackTrace();
          sb.append("Exception - ").append(e.getClass().getName());
          sb.append("\r\nStack -\r\n");
        } else {
          trace = e.getCause().getStackTrace();
          sb.append("\r\nException Cause -\r\n");
        }

        for (int i = 0; i < trace.length; i++) {
          sb.append("\r\n\tat ").append(trace[i].toString());
        }

        error.setDiagnostic(sb.toString());
        error.setErrorId(this.errorIdGenerator.generateId());
      }
      error.setHttpCode(httpCode.intValue());
    } else {
      error.setMessage("Unknown error");
      error.setHttpCode(500);
      error.setErrorId(this.errorIdGenerator.generateId());
    }

    mav.addObject("error", error);
    LOG.info("Error {}", error);
    return mav;
  }

  /**
   * Gets the error code.
   *
   * @param e the e
   * @return the error code
   */
  private int getErrorCode(Exception e) {
		 
		 if(e instanceof RequiredFieldMissingException){
			return  ((RequiredFieldMissingException) e).getErrorCode();
		 }else if (e instanceof AuthorizationException){
			 return ((AuthorizationException)e).getErrorCode();
		 }else if (e instanceof AuthenticationException){
			 return ((AuthenticationException)e).getErrorCode();
		 }else if (e instanceof ObjectNotFoundException){
			 return ((ObjectNotFoundException)e).getErrorCode();
		 }else if (e instanceof UnModificationException){
			 return ((UnModificationException)e).getErrorCode();
		 }	
	  
	  return 199;
}

/* (non-Javadoc)
 * @see org.springframework.web.servlet.View#render(java.util.Map, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
 */
public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response)
    throws Exception
  {
    RestError e = (RestError)model.get("error");

    response.setContentType(getContentType());
    response.setStatus(e.getHttpCode());

    PrintWriter writer = response.getWriter();
    MAPPER.writeValue(writer, e);
    writer.close();
  }

  /* (non-Javadoc)
   * @see org.springframework.web.servlet.View#getContentType()
   */
  public String getContentType()
  {
    return "application/json";
  }

  /**
   * Sets the error id generator.
   *
   * @param errorIdGenerator the new error id generator
   */
  public void setErrorIdGenerator(ErrorIdGenerator errorIdGenerator)
  {
    this.errorIdGenerator = errorIdGenerator;
  }

  /**
   * Register exception with http code.
   *
   * @param e the e
   * @param httpResponseCode the http response code
   */
  public static void registerExceptionWithHTTPCode(Exception e, int httpResponseCode)
  {
    httpErrorCodeMap.put(e.getClass().getName(), Integer.valueOf(httpResponseCode));
  }

  /**
   * Register exception with http code.
   *
   * @param clazz the clazz
   * @param httpResponseCode the http response code
   */
  public static void registerExceptionWithHTTPCode(Class<? extends Exception> clazz, int httpResponseCode) {
    httpErrorCodeMap.put(clazz.getName(), Integer.valueOf(httpResponseCode));
  }

  static
  {
    httpErrorCodeMap.put(InvalidArgumentsException.class.getName(), Integer.valueOf(400));
    httpErrorCodeMap.put(AuthenticationException.class.getName(), Integer.valueOf(401));
    httpErrorCodeMap.put(AuthorizationException.class.getName(), Integer.valueOf(403));
    httpErrorCodeMap.put(ObjectNotFoundException.class.getName(), Integer.valueOf(404));
    httpErrorCodeMap.put(ObjectAlreadyExistsException.class.getName(), Integer.valueOf(409));
    httpErrorCodeMap.put(BusinessException.class.getName(), Integer.valueOf(500));
    httpErrorCodeMap.put(UnModificationException.class.getName(), Integer.valueOf(304));
    
  }

  /**
   * The Class RestError.
   */
  private final class RestError
  {
    
    /** The http code. */
    private int httpCode;
    
    /** The error code. */
    private Integer errorCode;
    
    /** The message. */
    private String message;
    
    /** The error id. */
    private String errorId;
    
    /** The diagnostic. */
    private String diagnostic;

    /**
     * Gets the http code.
     *
     * @return the http code
     */
    public int getHttpCode()
    {
      return this.httpCode;
    }

    /**
     * Sets the http code.
     *
     * @param httpCode the new http code
     */
    public void setHttpCode(int httpCode) {
      this.httpCode = httpCode;
    }

    /**
     * Gets the error code.
     *
     * @return the error code
     */
    public Integer getErrorCode() {
      return this.errorCode;
    }

    /**
     * Sets the error code.
     *
     * @param errorCode the new error code
     */
    public void setErrorCode(int errorCode) {
      this.errorCode = Integer.valueOf(errorCode);
    }

    /**
     * Gets the message.
     *
     * @return the message
     */
    public String getMessage() {
      return this.message;
    }

    /**
     * Sets the message.
     *
     * @param message the new message
     */
    public void setMessage(String message) {
      this.message = message;
    }

    /**
     * Gets the error id.
     *
     * @return the error id
     */
    public String getErrorId() {
      return this.errorId;
    }

    /**
     * Sets the error id.
     *
     * @param errorId the new error id
     */
    public void setErrorId(String errorId) {
      this.errorId = errorId;
    }

    /**
     * Gets the diagnostic.
     *
     * @return the diagnostic
     */
    public String getDiagnostic() {
      return this.diagnostic;
    }

    /**
     * Sets the diagnostic.
     *
     * @param diagnostic the new diagnostic
     */
    public void setDiagnostic(String diagnostic) {
      this.diagnostic = diagnostic;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
      return String.format("HTTP %d {errorCode:%d, message:%s, errorId:%s, diagnostic:%s}", new Object[] { Integer.valueOf(this.httpCode), this.errorCode, this.message, this.errorId, this.diagnostic });
    }
  }
}