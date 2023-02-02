//
// This file was generated by the Eclipse Implementation of JAXB, v3.0.2 
// See https://eclipse-ee4j.github.io/jaxb-ri 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2023.02.02 at 09:17:33 AM MST 
//


package gov.inl.igcapt.gdtaf.core;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ConstrainingAttributeType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <pre>
 * &lt;simpleType name="ConstrainingAttributeType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="MAX_CHILDREN"/&gt;
 *     &lt;enumeration value="MAX_PEERS"/&gt;
 *     &lt;enumeration value="MAX_PARENTS"/&gt;
 *     &lt;enumeration value="PATH_LOSS_CALCULATION"/&gt;
 *     &lt;enumeration value="MIN_PATH_THRESHOLD"/&gt;
 *     &lt;enumeration value="COST_FACTOR"/&gt;
 *     &lt;enumeration value="IMPLEMENTATION_FACTOR"/&gt;
 *     &lt;enumeration value="SAFETY_FACTOR"/&gt;
 *     &lt;enumeration value="RELIABILITY_FACTOR"/&gt;
 *     &lt;enumeration value="MAX_APPLICATION_PACKET_BYTES"/&gt;
 *     &lt;enumeration value="MAX_BYTES_PER_SECOND"/&gt;
 *     &lt;enumeration value="NOMINAL_BYTES_PER_SECOND"/&gt;
 *     &lt;enumeration value="MAX_QUEUED_PACKETS"/&gt;
 *     &lt;enumeration value="RESILIENCE_FACTOR"/&gt;
 *     &lt;enumeration value="LIFECYCLE_MANAGEMENT_FACTOR"/&gt;
 *     &lt;enumeration value="DISTRIBUTED_COMPUTATION_FACTOR"/&gt;
 *     &lt;enumeration value="SCALABILITY_FACTOR"/&gt;
 *     &lt;enumeration value="CAPACITY_FACTOR"/&gt;
 *     &lt;enumeration value="MAX_MESH_DEPTH"/&gt;
 *     &lt;enumeration value="MIN_VIABLE_RSSI"/&gt;
 *     &lt;enumeration value="P2P_LATENCY"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "ConstrainingAttributeType")
@XmlEnum
public enum ConstrainingAttributeType {

    MAX_CHILDREN("MAX_CHILDREN"),
    MAX_PEERS("MAX_PEERS"),
    MAX_PARENTS("MAX_PARENTS"),
    PATH_LOSS_CALCULATION("PATH_LOSS_CALCULATION"),
    MIN_PATH_THRESHOLD("MIN_PATH_THRESHOLD"),
    COST_FACTOR("COST_FACTOR"),
    IMPLEMENTATION_FACTOR("IMPLEMENTATION_FACTOR"),
    SAFETY_FACTOR("SAFETY_FACTOR"),
    RELIABILITY_FACTOR("RELIABILITY_FACTOR"),
    MAX_APPLICATION_PACKET_BYTES("MAX_APPLICATION_PACKET_BYTES"),
    MAX_BYTES_PER_SECOND("MAX_BYTES_PER_SECOND"),
    NOMINAL_BYTES_PER_SECOND("NOMINAL_BYTES_PER_SECOND"),
    MAX_QUEUED_PACKETS("MAX_QUEUED_PACKETS"),
    RESILIENCE_FACTOR("RESILIENCE_FACTOR"),
    LIFECYCLE_MANAGEMENT_FACTOR("LIFECYCLE_MANAGEMENT_FACTOR"),
    DISTRIBUTED_COMPUTATION_FACTOR("DISTRIBUTED_COMPUTATION_FACTOR"),
    SCALABILITY_FACTOR("SCALABILITY_FACTOR"),
    CAPACITY_FACTOR("CAPACITY_FACTOR"),
    MAX_MESH_DEPTH("MAX_MESH_DEPTH"),
    MIN_VIABLE_RSSI("MIN_VIABLE_RSSI"),
    @XmlEnumValue("P2P_LATENCY")
    P_2_P_LATENCY("P2P_LATENCY");
    private final String value;

    ConstrainingAttributeType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ConstrainingAttributeType fromValue(String v) {
        for (ConstrainingAttributeType c: ConstrainingAttributeType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
