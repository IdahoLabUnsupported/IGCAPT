//
// This file was generated by the Eclipse Implementation of JAXB, v3.0.2 
// See https://eclipse-ee4j.github.io/jaxb-ri 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2023.02.02 at 09:17:37 AM MST 
//


package gov.inl.igcapt.gdtaf.gucs;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Payload complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Payload"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://pnnl.gov/gdtaf-core}BaseBlock"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Attributes" type="{http://pnnl.gov/gdtaf-gucs}PayloadAttribute" maxOccurs="unbounded"/&gt;
 *         &lt;element name="data" type="{http://pnnl.gov/gdtaf-core}guid"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="Description" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Payload", propOrder = {
    "attributes",
    "data"
})
public class Payload
    extends BaseBlock
{

    @XmlElement(name = "Attributes", required = true)
    protected List<PayloadAttribute> attributes;
    @XmlElement(required = true)
    protected String data;
    @XmlAttribute(name = "Description")
    protected String description;

    /**
     * Gets the value of the attributes property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the attributes property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAttributes().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PayloadAttribute }
     * 
     * 
     */
    public List<PayloadAttribute> getAttributes() {
        if (attributes == null) {
            attributes = new ArrayList<PayloadAttribute>();
        }
        return this.attributes;
    }

    /**
     * Gets the value of the data property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getData() {
        return data;
    }

    /**
     * Sets the value of the data property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setData(String value) {
        this.data = value;
    }

    /**
     * Gets the value of the description property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescription(String value) {
        this.description = value;
    }

}
