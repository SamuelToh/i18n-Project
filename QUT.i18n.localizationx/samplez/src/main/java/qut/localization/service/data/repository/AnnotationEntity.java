package qut.localization.service.data.repository;

import com.google.appengine.api.datastore.Key;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * A Google datastore entity.
 * This data entity encapsulates details of an annotation object.
 * 
 * @author <a href="mailto:yu.toh@connect.qut.edu.au">Samuel Toh</a>
 * @version $Revision: 1 $
 */
@PersistenceCapable(detachable="true")
@XmlRootElement
public class AnnotationEntity {

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key key;
    
    @Persistent
    private PatternEntity patternRef;

    @Persistent
    private String annotationKey;

    @Persistent
    private String annotationValue;

    public AnnotationEntity(){
    	this.setAnnotationKey("");
    	this.setAnnotationValue("");
    }
    
    /**
     * Constructor
     * @param key - The key of our annotation e.g. Author or Reference
     * @param value - Value of the key. 
     */
    public AnnotationEntity(PatternEntity patternRef, String key, String value){
    	this.setAnnotationKey(key);
    	this.setAnnotationValue(value);
    	this.setPatternRef(patternRef);
    }

    /**
     * Sets the primary key of our entity object
     * @param key - The primary key
     */
	public void setKey(Key key) {
		this.key = key;
	}

	/**
	 * Gets our Primary key
	 * @return Data entity's primary key.
	 */
	@XmlElement
	public Key getKey() {
		return key;
	}

	/**
	 * Sets the key of annotation
	 * 
	 * @param annotationKey - Key in String form
	 */
	public void setAnnotationKey(String annotationKey) {
		this.annotationKey = annotationKey;
	}

	/**
	 * Returns the annotation key value
	 * @return Returns the annotation key value
	 */
	@XmlElement
	public String getAnnotationKey() {
		return annotationKey;
	}

	/**
	 * Sets the annotation's value
	 * 
	 * @param annotationValue
	 */
	public void setAnnotationValue(String annotationValue) {
		this.annotationValue = annotationValue;
	}

	/**
	 * 
	 * @return returns the annotation's value
	 */
	public String getAnnotationValue() {
		return annotationValue;
	}

	public void setPatternRef(PatternEntity patternRef) {
		this.patternRef = patternRef;
	}

	public PatternEntity getPatternRef() {
		return patternRef;
	}
}
