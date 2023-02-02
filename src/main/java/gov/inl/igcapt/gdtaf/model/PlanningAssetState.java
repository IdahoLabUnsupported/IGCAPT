//
// This file was generated by the Eclipse Implementation of JAXB, v3.0.2 
// See https://eclipse-ee4j.github.io/jaxb-ri 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2023.02.02 at 09:17:35 AM MST 
//


package gov.inl.igcapt.gdtaf.model;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PlanningAssetState.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <pre>
 * &lt;simpleType name="PlanningAssetState"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="PHYSICAL"/&gt;
 *     &lt;enumeration value="INFERRED"/&gt;
 *     &lt;enumeration value="PLACEHOLDER"/&gt;
 *     &lt;enumeration value="ADDED"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "PlanningAssetState")
@XmlEnum
public enum PlanningAssetState {

    PHYSICAL,
    INFERRED,
    PLACEHOLDER,
    ADDED;

    public String value() {
        return name();
    }

    public static PlanningAssetState fromValue(String v) {
        return valueOf(v);
    }

}
