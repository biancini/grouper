/*
 * Copyright (C) 2004-2007 University Corporation for Advanced Internet Development, Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under
 * the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package edu.internet2.middleware.ldappc.spml.provider;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.openspml.v2.msg.Marshallable;
import org.openspml.v2.msg.XMLMarshaller;
import org.openspml.v2.msg.XMLUnmarshaller;
import org.openspml.v2.msg.spml.ErrorCode;
import org.openspml.v2.msg.spml.ExecutionMode;
import org.openspml.v2.msg.spml.Request;
import org.openspml.v2.msg.spml.Response;
import org.openspml.v2.msg.spml.StatusCode;
import org.openspml.v2.util.Spml2Exception;
import org.openspml.v2.util.xml.ReflectiveDOMXMLUnmarshaller;
import org.openspml.v2.util.xml.ReflectiveXMLMarshaller;
import org.slf4j.Logger;

import edu.internet2.middleware.grouper.util.GrouperUtil;
import edu.internet2.middleware.ldappc.util.PSPUtil;
import edu.internet2.middleware.shibboleth.common.config.BaseReloadableService;

/**
 * Base class for SPMLv2 Provisioning Service Providers. Handling of requests is provided
 * by subclasses. Extends Shibboleth's {@link BaseReloadableService}.
 */
public abstract class BaseSpmlProvider extends BaseReloadableService implements SpmlProvider {

  /** Logger. */
  private static final Logger LOG = GrouperUtil.getLogger(BaseSpmlProvider.class);

  /** The Spring identifier. */
  private String id;

  /** Method name to look for in subclasses. */
  public static final String methodName = "execute";

  /** SPML toolkit XML marshaller. */
  private XMLMarshaller xmlMarshaller;

  /** SPML toolkit XML unmarshaller. */
  private XMLUnmarshaller xmlUnmarshaller;

  /**
   * {@inheritDoc}
   * 
   * Only the synchronous execution mode is supported.
   */
  public Response execute(Request request) {

    // a generic Response returned under error conditions
    Response response = new Response();
    response.setRequestID(this.getOrGenerateRequestID(request));

    try {
      // FUTURE handle asynchronous requests
      if (request.getExecutionMode() == ExecutionMode.ASYNCHRONOUS) {
        fail(response, ErrorCode.UNSUPPORTED_EXECUTION_MODE);
        LOG.error(PSPUtil.toString(response));
        LOG.trace("response:\n{}", this.toXML(response));
      } else {
        // determine the appropriate method
        Method method = this.getClass().getMethod(methodName, new Class[] { request.getClass() });
        // execute the request
        response = (Response) method.invoke(this, new Object[] { request });
        if (response.getRequestID() == null) {
          response.setRequestID(this.getOrGenerateRequestID(request));
        }
      }

    } catch (NoSuchMethodException e) {
      fail(response, ErrorCode.UNSUPPORTED_OPERATION, e);
      LOG.error(PSPUtil.toString(response));
      LOG.trace("response:\n{}", this.toXML(response));
    } catch (IllegalAccessException e) {
      fail(response, ErrorCode.UNSUPPORTED_OPERATION, e);
      LOG.error(PSPUtil.toString(response));
      LOG.trace("response:\n{}", this.toXML(response));
    } catch (InvocationTargetException e) {
      fail(response, ErrorCode.UNSUPPORTED_OPERATION, e);
      LOG.error(PSPUtil.toString(response));
      LOG.trace("response:\n{}", this.toXML(response));
    }

    return response;
  }

  /**
   * {@inheritDoc}
   */
  public String getId() {
    return id;
  }

  /**
   * Sets the id.
   * 
   * @param id
   */
  public void setId(String id) {
    this.id = id;
  }

  /**
   * Return the XML marshaller, which will be instantiated once and reused per instance of
   * this class.
   * 
   * @return the {@link XMLMarshaller}
   */
  public XMLMarshaller getXMLMarshaller() {
    if (this.xmlMarshaller == null) {
      this.xmlMarshaller = new ReflectiveXMLMarshaller();
    }
    return this.xmlMarshaller;
  }

  /**
   * Set the XML marshaller
   * 
   * @param xmlMarshaller
   *          the {@link XMLMarshaller}
   */
  public void setXMLMarshaller(XMLMarshaller xmlMarshaller) {
    this.xmlMarshaller = xmlMarshaller;
  }

  /**
   * Return the XML unmarshaller, which will be instantiated once and reused per instance
   * of this class.
   * 
   * @return the {@link XMLUnmarshaller}
   */
  public XMLUnmarshaller getXmlUnmarshaller() {
    if (this.xmlUnmarshaller == null) {
      this.xmlUnmarshaller = new ReflectiveDOMXMLUnmarshaller();
    }
    return xmlUnmarshaller;
  }

  /**
   * Set the XML unmarshaller
   * 
   * @param xmlUnmarshaller
   *          the {@link XMLUnmarshaller}
   */
  public void setXmlUnmarshaller(XMLUnmarshaller xmlUnmarshaller) {
    this.xmlUnmarshaller = xmlUnmarshaller;
  }

  /**
   * See {@link #fail(Response, ErrorCode, String...)}
   * 
   * The messages from the given exception are added to the response.
   * 
   * @param response
   *          the {@link Response}
   * @param errorCode
   *          the {@link ErrorCode}
   * @param e
   *          the exception
   * @return the updated {@link Response}
   */
  public Response fail(Response response, ErrorCode errorCode, Exception e) {
    return fail(response, errorCode, e.getMessage());
  }

  /**
   * Set the status code of the given response to failure and set the error code and
   * messages.
   * 
   * Kludge : the 0x0 unicode character is replaced with an underscore to avoid exceptions
   * when handling Active Directory error messages.
   * 
   * @param response
   *          the {@link Response}
   * @param errorCode
   *          the {@link ErrorCode}
   * @param messages
   *          error text
   * @return the updated {@link Response}
   */
  public Response fail(Response response, ErrorCode errorCode, String... messages) {
    response.setStatus(StatusCode.FAILURE);
    response.setError(errorCode);
    if (messages != null) {
      for (String message : messages) {
        if (message != null) {
          // TODO for Active Directory, find a better way
          message = message.replace((char) 0x0, '_');
          response.addErrorMessage(message);
        }
      }
    }
    return response;
  }

  /**
   * Generate a reasonably unique id.
   * 
   * see {@link PSPUtil#uniqueRequestId()}
   * 
   * @return the id
   */
  public String generateRequestID() {
    return PSPUtil.uniqueRequestId();
  }

  /**
   * Return the request ID of the given request or generate a new request ID.
   * 
   * @param request
   * @return the request ID
   */
  public String getOrGenerateRequestID(Request request) {
    if (request.getRequestID() != null) {
      return request.getRequestID();
    }
    return this.generateRequestID();
  }

  /**
   * Return the XML representation of the given object. Return null if an exception
   * occurs.
   * 
   * @param marshallable
   *          the {@link Marshallable} object
   * @return the XML representation
   */
  public String toXML(Marshallable marshallable) {
    try {
      return marshallable.toXML(this.getXMLMarshaller());
    } catch (Spml2Exception e) {
      LOG.error("Unable to marshal xml", e);
      return null;
    }
  }
}
