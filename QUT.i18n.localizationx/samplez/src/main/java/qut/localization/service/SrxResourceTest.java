package qut.localization.service;

//import com.google.gwt.junit.client.GWTTestCase;
import static org.junit.Assert.*;

import javax.ws.rs.core.Response;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.junit.Assert;
import org.junit.Test;
import org.jboss.resteasy.util.HttpHeaderNames;

import qut.localization.service.data.repository.PatternEntity;
import qut.localization.service.helper.XMLHelper;


import java.awt.PageAttributes.MediaType;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author <a href="mailto:yu.toh@connect.qut.edu.au">Samuel Toh</a>
 * @version $Revision: 1 $
 */
public class SrxResourceTest { //extends GWTTestCase {
	
	/* The pattern xml used in our web service */
	private String patternML =
			"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
				"<collection>" +
					"<pattern name=\"Paragraphing2\"/>" +
				"</collection>";
	
	private String detailPatternML = 
			"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
				"<pattern standard=\"SRX\" ruleType=\"break\" name=\"Paragraphing\" localeCode=\"en\" lastUpdate=\"2010-04-27T08:24:47.515Z\" beforeRegExpression=\"\\n\">" +
					"<Annotations>" +
						"<annotation>" +
							"<annotationKey>Author</annotationKey>" +
							"<annotationValue>QUT.SamuelToh</annotationValue>" +
						"</annotation>" +
						"<annotation>" +
							"<annotationKey>Purpose</annotationKey>" +
							"<annotationValue>Testing</annotationValue>" +
						"</annotation>" +
					"</Annotations>" +
				"</pattern>";
	
	private String UpdatePatternML = 
		"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
			"<pattern standard=\"SRX\" ruleType=\"exception\" name=\"Paragraphing\" localeCode=\"en\" lastUpdate=\"2010-04-27T08:24:47.515Z\" afterRegExpression=\"\\s\" beforeRegExpression=\"\\bAdd\\.\">" +
				"<Annotations>" +
					"<annotation>" +
						"<annotationKey>Author</annotationKey>" +
						"<annotationValue>QUT.SamToh</annotationValue>" +
					"</annotation>" +
					"<annotation>" +
						"<annotationKey>Purpose</annotationKey>" +
						"<annotationValue>Testing</annotationValue>" +
					"</annotation>" +
					"<annotation>" +
						"<annotationKey>Contact</annotationKey>" +
						"<annotationValue>yu.toh@connect.qut.edu.au</annotationValue>" +
				"</annotation>" +
				"</Annotations>" +
			"</pattern>";
	
	/* our localhost port */
	private static String PORT = "8888";
	
	/* Test reading in our SRX pattern operation*/
	@Test
	public void testReadPattern() throws Exception{
		SrxResource reader = new SrxResource();
		InputStream is = new ByteArrayInputStream(detailPatternML.getBytes("UTF-8"));
		
		PatternEntity pattern = reader.readPattern(is);
		
		//Test basic attributes of our pattern entity
		assertEquals("Paragraphing", pattern.getName());
		assertEquals("SRX", pattern.getStandard());
		
		assertEquals("\\n", pattern.getBeforeRegExpression());
		assertEquals("en", pattern.getLocaleCode());
		assertEquals("break", pattern.getRuleType());
		
		
		//Test its annotations
		assertEquals(2, pattern.getAnnotations().size());
		assertEquals("Author", pattern.getAnnotations().get(0).getAnnotationKey());
		assertEquals("QUT.SamuelToh", pattern.getAnnotations().get(0).getAnnotationValue());
		assertEquals("Purpose", pattern.getAnnotations().get(1).getAnnotationKey());
		assertEquals("Testing", pattern.getAnnotations().get(1).getAnnotationValue());
	}
	
	/* Test calling our web service through posting it with some XML pattern data*/
	@Test
	public void testPostNewPattern() throws Exception
	{
		//Setup a client to emulate our connection
		HttpClient client = new HttpClient();
		{
			PostMethod method = new PostMethod
										("http://localhost:" + PORT +"/rest/SRX/Patterns");
			method.setRequestEntity
					(new StringRequestEntity(detailPatternML,
												javax.ws.rs.core.MediaType.TEXT_XML, null));
			
			//Test authorization method
			method.setRequestHeader("Hello2", "World");
			int status = client.executeMethod(method);
			Assert.assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), status);

			//Resubmit the request with valid username password
			method.setRequestHeader("QUT", "localization");
			status = client.executeMethod(method);
			Assert.assertEquals(Response.Status.CREATED.getStatusCode(), status);
			
			//Close our connection
			method.releaseConnection();
	      }
	}
	
	/**
	 * Test using our update service.
	 * @throws HttpException
	 * @throws IOException
	 */
	@Test
	public void testPutPattern() throws HttpException, IOException{
		HttpClient client = new HttpClient();
		{
			PutMethod put = new PutMethod("http://localhost:" + PORT +"/rest/SRX/Patterns/Paragraphing");
			
			put.setRequestEntity
						(new StringRequestEntity(UpdatePatternML,
										javax.ws.rs.core.MediaType.TEXT_XML, null));
			put.setRequestHeader("QUT", "localization");
			
			int status = client.executeMethod(put);
			Assert.assertEquals(Response.Status.OK.getStatusCode(), status);

		}
	}
	
	@Test
	public void testGetPattern() throws HttpException, IOException {
	
		//Setup a client to emulate our connection
		HttpClient client = new HttpClient();
		{
			GetMethod get = new GetMethod("http://localhost:" + PORT +"/rest/SRX/Patterns/Paragraphing");
			
			int status = client.executeMethod(get);
			Assert.assertEquals(Response.Status.OK.getStatusCode(), status);
			
			//There must be a response from our Get method
			//Assert.assertEquals(get.getResponseBodyAsString(), patternML);
			
			PutMethod method = new PutMethod
			("http://localhost:" + PORT +"/rest/SRX/Patterns/Paragraphing");
			method.setRequestEntity
			(new StringRequestEntity(UpdatePatternML,
										javax.ws.rs.core.MediaType.TEXT_XML, null));
			method.setRequestHeader("QUT", "localization");
			status = client.executeMethod(method);
			//Assert.assertEquals(Response.Status.CREATED.getStatusCode(), status);
	
		}
	}
	
}