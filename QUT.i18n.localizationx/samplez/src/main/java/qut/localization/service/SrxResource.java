package qut.localization.service;

import java.io.ByteArrayInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Consumes;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import qut.localization.service.data.repository.AnnotationEntity;
import qut.localization.service.data.repository.DataCommitter;
import qut.localization.service.data.repository.DataViewer;
import qut.localization.service.data.repository.PatternEntity;
import qut.localization.service.datastructure.Pattern;
import qut.localization.service.helper.XMLHelper;

/**
 * This class acts as an End Point in our REST web service.
 * It contains all implementations of SRX
 * @author <a href="mailto:yu.toh@connect.qut.edu.au">Samuel Toh</a>
 * @version $Revision: 1 $
 */
@Path("/")
public class SrxResource {

	private static final Logger logger = LoggerFactory.getLogger(SrxResource.class);
	
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/{lang}")
	/**
	 * An echo method to test our GET method in JUnit.
	 * This method is purely for testing purpose only.
	 */
	public String hello(@PathParam("lang") String lang){
		return "hello world " + lang;
	}

	/**
	 * Returns a list of pattern names in a xml format.
	 */
	@GET
	@Produces(MediaType.TEXT_XML)
	@Path("/SRX/Patterns")
	public List<Pattern> getPatternNames() throws Exception{
		logger.debug ("Returning list of known pattern names...");
		return DataViewer.viewNames("SRX");
	}
	
	/**
	 * Method used for requesting a pattern's detailed information.
	 * 
	 * On success the method should return http code ok else
	 * it will return http code not found
	 * 
	 * @param name - The pattern name.
	 * @return - returns a media type of text xml  representing the pattern object
	 * @throws Exception - thrown when service is not available
	 */
	@GET
	@Produces(MediaType.TEXT_XML)
	@Path("/SRX/Patterns/{patternName}")
	public Response getPatternDetail(@PathParam("patternName") String name) throws Exception{
		Pattern pattern = DataViewer.viewPattern(name);
		
		if(pattern == null){
			logger.debug("No such Pattern : {}", name);
			return Response.status(javax.ws.rs.core.Response.Status.NOT_FOUND).build();
		}
		return Response.ok(pattern).build();	
	}
	
	/**
	 * Creates a new segmentation pattern in our SRX repository.
	 * 
	 * @param content - passed in as a text_xml document containing all the pattern
	 * details.
	 */
	@POST
	@Consumes(MediaType.TEXT_XML)
	@Path("/SRX/Patterns")
	public Response newPattern
		(@HeaderParam(Authenticator.SERVICE_USERNAME) String password, String content) throws Exception{

		if(!Authenticator.authenticate(password))
			//Fails authentication
			return Response.status(Response.Status.UNAUTHORIZED).build();

		InputStream is = new ByteArrayInputStream(content.getBytes("UTF-8"));
		
		logger.debug ("Committing new pattern into repository...");
		int result = DataCommitter.Create(readPattern(is));
		
		switch (result) {
	    	case DataCommitter.PATTERN_CREATED:
	    		return Response.status(Response.Status.CREATED).build();
	    		
	    case DataCommitter.PATTERN_CREATE_FAIL:
	      return Response.status(Response.Status.NOT_MODIFIED).build();
	   
	    default:
	      return Response.serverError().build();
	    }
	}
	
	@PUT
	@Consumes(MediaType.TEXT_XML)
	@Path("SRX/Patterns/{name}")
	/**
	 * Updates a segment pattern in our SRX repository.
	 * 
	 * @param content - passed in as a text_xml document containing all the pattern
	 * details.
	 */
	public Response updatePattern
		(@HeaderParam(Authenticator.SERVICE_USERNAME) String password,
				@PathParam("name") String patternName, InputStream updateML){
		
		if(!Authenticator.authenticate(password))
			//Fails authentication
			return Response.status(Response.Status.UNAUTHORIZED).build();
		
		PatternEntity newPattern = null;
		
		try {
			newPattern = this.readPattern(updateML);
		} catch (Exception e) {
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
		
		int result = DataCommitter.Update(newPattern);

		switch (result) {
    		case DataCommitter.PATTERN_UPDATE_FAIL:
    			return Response.status(Response.Status.NOT_MODIFIED).build();
    		
    		case DataCommitter.PATTERN_UPDATE_FAIL_PAT_NOT_FOUND:
    			return Response.status(Response.Status.NOT_FOUND).build();
   
    		default:
    			//Everything is alright
    			return Response.status(Response.Status.OK).build();
		}
	}
	
	/** 
	 * This method parses the given content into our PatternEntity object 
	 * The reason for it being a protected is because we want JUnit to access
	 * this method and test its reliability
	 * 
	 * @param content The input stream object containing our XML pattern details
	 * @return Returns the PatternEntity object containing all parsed data
	 * 
	 * */
	protected PatternEntity readPattern(InputStream content) throws Exception 
	{	
		PatternEntity pattern = new PatternEntity();
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document doc = builder.parse(content);
		Element root = doc.getDocumentElement();

		if(!root.getNodeName().equals("pattern")){
			throw new Exception("Root element is not pattern!");
		}
		
		populatePattern(pattern,root);
		
         return pattern; 
	}
	
	private void populatePattern(PatternEntity pattern, Node node){
		pattern.setName(XMLHelper.getAttrValString(node, "name"));
		pattern.setStandard(XMLHelper.getAttrValString(node, "standard"));				
		Date date = new Date();
		
		pattern.setLastUpdate(date);		
		pattern.setBeforeRegExpression(((XMLHelper.getAttrValString(node, "beforeRegExpression"))));
		pattern.setAfterRegExpression(((XMLHelper.getAttrValString(node, "afterRegExpression"))));
		pattern.setRuleType(((XMLHelper.getAttrValString(node, "ruleType"))));
		pattern.setLocaleCode(((XMLHelper.getAttrValString(node, "localeCode"))));
		
		NodeList nodes = node.getChildNodes();

		for (int i = 0; i < nodes.getLength(); i++) {
			//Skip all the rubbish until we found a node
			if(nodes.item(i).getNodeType() == org.w3c.dom.Node.ELEMENT_NODE)
			{	
				//If element is Annotations scan deeper in for a list of annotations
				//and attach it onto our 
				if(nodes.item(i).getNodeName().equals("Annotations"))
					pattern.setAnnotations(readAnnotations(pattern, nodes.item(i)));	
			} 
         }
	}
	
	private ArrayList<AnnotationEntity> readAnnotations(PatternEntity refPattern, Node node){
		
		ArrayList<AnnotationEntity> annotations = new ArrayList<AnnotationEntity>();
		NodeList annotationList = node.getChildNodes();
		
		for(int i = 0; i < annotationList.getLength(); i ++)
		{
			if(annotationList.item(i).getNodeType() == org.w3c.dom.Node.ELEMENT_NODE)
			{	
				if(annotationList.item(i).getNodeName().equals("annotation")){
					annotations.add
						(new AnnotationEntity
								(refPattern,
								 XMLHelper.getTextContent(annotationList.item(i), "annotationKey"),
								 XMLHelper.getTextContent(annotationList.item(i), "annotationValue")));
				}
			} 
		}
		
		return annotations;
			
	}
	
}
