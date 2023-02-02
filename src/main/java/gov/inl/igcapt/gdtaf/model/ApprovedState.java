//
// This file was generated by the Eclipse Implementation of JAXB, v3.0.2 
// See https://eclipse-ee4j.github.io/jaxb-ri 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2023.02.02 at 09:17:35 AM MST 
//


package gov.inl.igcapt.gdtaf.model;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ApprovedState.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <pre>
 * &lt;simpleType name="ApprovedState"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="Approved"/&gt;
 *     &lt;enumeration value="Not_Approved"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "ApprovedState")
@XmlEnum
public enum ApprovedState {

    @XmlEnumValue("Approved")
    APPROVED("Approved"),
    @XmlEnumValue("Not_Approved")
    NOT_APPROVED("Not_Approved");
    private final String value;

    ApprovedState(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ApprovedState fromValue(String v) {
        for (ApprovedState c: ApprovedState.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
