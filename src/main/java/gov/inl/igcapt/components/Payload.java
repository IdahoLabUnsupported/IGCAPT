/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.inl.igcapt.components;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author FRAZJD
 */
public class Payload implements java.io.Serializable {
    /** List of uses cases that are independent. */
    public List<UseCaseEntry> payloadUseCaseList = new ArrayList<>();
    
    /** List of uses cases that are dependent. These are groups of dependent use cases. */
    public List<DependentUseCaseEntry> payloadDependentUseCaseList = new ArrayList<>();
}
