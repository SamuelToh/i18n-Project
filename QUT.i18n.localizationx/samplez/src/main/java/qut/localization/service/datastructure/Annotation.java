package qut.localization.service.datastructure;

import javax.jdo.annotations.Persistent;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

public class Annotation {

	  private String annotationKey;

	  private String annotationValue;
	  
	  public Annotation(){}
	  
	  public Annotation(String key, String value){
		  annotationKey = key;
		  annotationValue = value;
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
		@XmlElement
		public String getAnnotationValue() {
			return annotationValue;
		} 
}
