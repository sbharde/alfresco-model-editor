/**
 * Copyright (C) 2005-2009 Alfresco Software Limited.
 *
 * This file is part of the Spring Surf Extension project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.alfresco.repo.dictionary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.alfresco.repo.dictionary.constraint.AbstractConstraint;
import org.alfresco.repo.dictionary.constraint.AuthorityNameConstraint;
import org.alfresco.repo.dictionary.constraint.ListOfValuesConstraint;
import org.alfresco.repo.dictionary.constraint.NumericRangeConstraint;
import org.alfresco.repo.dictionary.constraint.RegexConstraint;
import org.alfresco.repo.dictionary.constraint.StringLengthConstraint;
import org.alfresco.repo.dictionary.constraint.UserNameConstraint;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;

/**
 * @author drq
 *
 */
public class ModelUtils
{
    /**
     * @param model
     * @return
     */
    public static String getDefaultNameSpace (M2Model model)
    {
        String prefix = "";
        if (model.getNamespaces().size() > 0 )
        {
            prefix = model.getNamespaces().get(0).getPrefix()+":";
        }
        return prefix;
    }

    /**
     * @param prefix
     * @param uri
     * @return
     */
    public static M2Namespace newNameSpace (String prefix, String uri)
    {
        M2Namespace m2ns = new M2Namespace();
        m2ns.setPrefix(prefix);
        m2ns.setUri(uri);
        return m2ns;
    }

    /**
     * @param name
     * @return
     */
    public static M2Type newType (String name)
    {
        M2Type m2t = new M2Type();
        m2t.setName(name);
        return m2t;
    }

    /**
     * @param name
     * @return
     */
    public static M2Aspect newAspect (String name)
    {
        M2Aspect m2t = new M2Aspect();
        m2t.setName(name);
        return m2t;
    }

    /**
     * @param name
     * @return
     */
    public static M2Property newProperty (String name)
    {
        M2Property m2p = new M2Property();
        m2p.setName(name);
        return m2p;
    }

    /**
     * @param name
     * @return
     */
    public static M2PropertyOverride newPropertyOverride (String name)
    {
        M2PropertyOverride m2p = new M2PropertyOverride();
        m2p.setName(name);
        return m2p;
    }

    /**
     * @param name
     * @return
     */
    public static M2Association newAssociation (String name)
    {
        M2Association m2a = new M2Association();
        m2a.setName(name);
        return m2a;
    }

    /**
     * @param name
     * @return
     */
    public static M2ChildAssociation newChildAssociation (String name)
    {
        M2ChildAssociation m2a = new M2ChildAssociation();
        m2a.setName(name);
        return m2a;
    }

    /**
     * @param name
     * @return
     */
    public static M2Constraint newConstraint (String name)
    {
        M2Constraint m2c = new M2Constraint();
        m2c.setName(name);
        return m2c;
    }

    /**
     * @param name
     * @return
     */
    public static M2Model newModel (String name)
    {
        M2Model m2m = new M2Model();
        m2m.setName(name);
        return m2m;
    }

    /**
     * @param m2nA
     * @param m2n
     * @return
     */
    public static boolean compareNameSpace(M2Namespace m2nA,M2Namespace m2n)
    {
        if (m2n.getPrefix().equals(m2nA.getPrefix()) && m2n.getUri().equals(m2nA.getUri()))
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    /**
     * @param sa
     * @param sb
     * @return
     */
    public static boolean compareString(String sa, String sb)
    {
        if (sa == null)
        {
            if (sb == null)
            {
                return false;
            }
            else
            {
                return true;
            }
        }
        else if (sa.equals(sb))
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    /**
     * @param sa
     * @param sb
     * @return
     */
    public static boolean compareBoolean(Boolean sa, Boolean sb)
    {
        if (sa == null)
        {
            if (sb == null)
            {
                return false;
            }
            else
            {
                return true;
            }
        }
        else if (sa.equals(sb))
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    /**
     * @param m2ca
     * @param m2cb
     * @return
     */
    public static boolean compareConstraint(M2Constraint m2ca,M2Constraint m2cb)
    {
        // Compare fields
        if ( compareString(m2ca.getName(),m2cb.getName()) 
                || compareString(m2ca.getTitle(),m2cb.getTitle()) 
                || compareString(m2ca.getDescription(),m2cb.getDescription()) 
                || compareString(m2ca.getType(),m2cb.getType()) 
                || compareString(m2ca.getRef(),m2cb.getRef()) 
        )
        {
            return true;
        }
        //compare parameters
        if (m2ca.getParameters().size() != m2cb.getParameters().size())
        {
            return true;
        }
        for (M2NamedValue m2nva : m2ca.getParameters())
        {
            boolean foundName = false;
            String nameA = m2nva.getName();
            for (M2NamedValue m2nvb : m2cb.getParameters())
            {
                if (m2nvb.getName().equals(nameA))
                {
                    foundName = true;
                    if (compareString(m2nva.toString(),m2nvb.toString()))
                    {
                        return true;
                    }
                }
            }
            if (!foundName)
            {
                return true;
            }
        }       
        return false;
    }

    /**
     * @param m2ca
     * @param m2cb
     */
    public static void copyConstraint(M2Constraint m2ca, M2Constraint m2cb)
    {
        m2ca.setTitle(m2cb.getTitle());
        m2ca.setName(m2cb.getName());
        m2ca.setDescription(m2cb.getDescription());
        m2ca.setType(m2cb.getType());
        m2ca.setRef(m2cb.getRef());

        List<M2NamedValue> params = new ArrayList<M2NamedValue>(m2ca.getParameters());
        for (M2NamedValue param : params)
        {
            m2ca.removeParameter(param.getName());            
        }
        for (M2NamedValue param : m2cb.getParameters())
        {
            if (param.hasSimpleValue())
            {
                m2ca.createParameter(param.getName(), param.getSimpleValue());
            }
            if (param.hasListValue())
            {
                m2ca.createParameter(param.getName(), param.getListValue());
            }
        }
    }

    /**
     * @param m2pa
     * @param m2pb
     * @return
     */
    public static boolean compareProperty(M2Property m2pa, M2Property m2pb)
    {
        // Compare fields
        if ( compareString(m2pa.getName(),m2pb.getName()) 
                || compareString(m2pa.getTitle(),m2pb.getTitle()) 
                || compareString(m2pa.getDescription(),m2pb.getDescription()) 
                || m2pa.isOverride() != m2pb.isOverride()
                || m2pa.isMandatory() != m2pb.isMandatory()
                || m2pa.isMandatoryEnforced() != m2pb.isMandatoryEnforced()
                || m2pa.isMultiValued() != m2pb.isMultiValued()
                || m2pa.isProtected() != m2pb.isProtected()
                || m2pa.isIndexed() != m2pb.isIndexed()
                || m2pa.isIndexedAtomically() != m2pb.isIndexedAtomically()
                || m2pa.isStoredInIndex() != m2pb.isStoredInIndex()
                || m2pa.getIndexTokenisationMode() != m2pb.getIndexTokenisationMode()
                || compareString(m2pa.getDefaultValue(),m2pb.getDefaultValue()) 
        )
        {
            return true;
        }
        // Compare Constrains
        for (M2Constraint m2ca : m2pa.getConstraints())
        {
            boolean founded = false;

            for (M2Constraint m2cb : m2pb.getConstraints())
            {
                if (m2ca.getRef().equals(m2cb.getRef()))
                {
                    founded = true;
                }
            }
            if (!founded)
            {
                return true;
            }
        }
        return false;
    }

    /**
     * @param m2pa
     * @param m2pb
     * @return
     */
    public static boolean comparePropertyOverride(M2PropertyOverride m2pa, M2PropertyOverride m2pb)
    {
        // Compare fields
        if ( compareString(m2pa.getName(),m2pb.getName())
                || compareBoolean(m2pa.isMandatory(),m2pb.isMandatory())
                || compareString(m2pa.getDefaultValue(),m2pb.getDefaultValue()) 
        )
        {
            return true;
        }

        return false;
    }

    /**
     * @param m2pa
     * @param m2pb
     */
    public static void copyProperty(M2Property m2pa, M2Property m2pb)
    {
        m2pa.setTitle(m2pb.getTitle());
        m2pa.setName(m2pb.getName());
        m2pa.setDescription(m2pb.getDescription());
        m2pa.setType(m2pb.getType());

        m2pa.setOverride(m2pb.isOverride());
        m2pa.setMandatory(m2pb.isMandatory());
        m2pa.setMandatoryEnforced(m2pb.isMandatoryEnforced());
        m2pa.setMultiValued(m2pb.isMultiValued());
        m2pa.setDefaultValue(m2pb.getDefaultValue());
        m2pa.setProtected(m2pb.isProtected());

        m2pa.setIndexed(m2pb.isIndexed());
        if (m2pb.isIndexedAtomically() != null)
        {
            m2pa.setIndexedAtomically(m2pb.isIndexedAtomically());
        }
        if (m2pb.getIndexTokenisationMode() != null)
        {
            m2pa.setIndexTokenisationMode(m2pb.getIndexTokenisationMode());
        }
        if (m2pb.isStoredInIndex() != null)
        {
            m2pa.setStoredInIndex(m2pb.isStoredInIndex());
        }

        for (M2Constraint m2c : m2pa.getConstraints())
        {
            m2pa.removeConstraintRef(m2c.getRef());
        }

        for (M2Constraint m2c : m2pb.getConstraints())
        {
            m2pa.addConstraintRef(m2c.getRef());
        }

    }

    /**
     * @param m2pa
     * @param m2pb
     */
    public static void copyPropertyOverride(M2PropertyOverride m2pa, M2PropertyOverride m2pb)
    {
        m2pa.setName(m2pb.getName());
        if (m2pb.isMandatory() != null)
            m2pa.setMandatory(m2pb.isMandatory());
        m2pa.setDefaultValue(m2pb.getDefaultValue());
    }
    /**
     * @param m2ta
     * @param m2tb
     * @return
     */
    public static boolean compareClass(M2Class m2ta, M2Class m2tb)
    {
        if (compareString(m2ta.getName(),m2tb.getName()) 
                || compareString(m2ta.getTitle(),m2tb.getTitle()) 
                || compareString(m2ta.getDescription(),m2tb.getDescription())
                || compareString(m2ta.getParentName(),m2tb.getParentName())
                || compareBoolean(m2ta.getArchive(),m2tb.getArchive())
                || compareBoolean(m2ta.getIncludedInSuperTypeQuery(),m2tb.getIncludedInSuperTypeQuery()))
        {
            return true;
        }
        //compare properties
        if (m2ta.getProperties().size() != m2tb.getProperties().size())
        {
            return true;
        }
        for (M2Property m2pa : m2ta.getProperties())
        {
            M2Property m2pb = m2tb.getProperty(m2pa.getName());

            if (m2pb == null)
            {
                return true;
            }
            if (compareProperty(m2pa,m2pb))
            {
                return true;
            }
        }
        //compare property overrides
        if (m2ta.getPropertyOverrides().size() != m2tb.getPropertyOverrides().size())
        {
            return true;
        }
        for (M2PropertyOverride m2pa : m2ta.getPropertyOverrides())
        {
            M2PropertyOverride m2pb = m2tb.getPropertyOverride(m2pa.getName());

            if (m2pb == null)
            {
                return true;
            }
            if (comparePropertyOverride(m2pa,m2pb))
            {
                return true;
            }
        }
        //compare associations
        if (m2ta.getAssociations().size() != m2tb.getAssociations().size())
        {
            return true;
        }
        for (M2ClassAssociation m2pa : m2ta.getAssociations())
        {
            M2ClassAssociation m2pb = m2tb.getAssociation(m2pa.getName());

            if (m2pb == null)
            {
                return true;
            }

            if (m2pa.isChild() && m2pb.isChild())
            {
                if (compareChildAssociation((M2ChildAssociation)m2pa,(M2ChildAssociation)m2pb))
                {
                    return true;
                }
                else
                {
                    return false;
                }
            }
            else
            {
                if (!m2pa.isChild() && !m2pb.isChild())
                {
                    if (compareAssociation((M2Association)m2pa,(M2Association)m2pb))
                    {
                        return true;
                    }
                    else
                    {
                        return false;
                    }
                } 
            }
        }
        //compare mandatory aspects
        if (m2ta.getMandatoryAspects().size() != m2tb.getMandatoryAspects().size())
        {
            return true;
        }
        for (String ma : m2ta.getMandatoryAspects())
        {
            boolean foundMatch = false;
            for (String mb : m2tb.getMandatoryAspects())
            {
                if (ma.equals(mb))
                {
                    foundMatch = true;
                }
            }
            if (!foundMatch)
            {
                return true;
            }
        }
        return false;
    }

    /**
     * @param m2ta
     * @param m2tb
     * @return
     */
    public static boolean compareType(M2Type m2ta, M2Type m2tb)
    {
        return compareClass(m2ta, m2tb);     
    }

    /**
     * @param m2ta
     * @param m2tb
     * @return
     */
    public static boolean compareAspect(M2Aspect m2ta, M2Aspect m2tb)
    {
        return compareClass(m2ta, m2tb);     
    }

    /**
     * @param m2ta
     * @param m2tb
     */
    public static void copyClass(M2Class m2ta, M2Class m2tb)
    {
        m2ta.setTitle(m2tb.getTitle());
        m2ta.setName(m2tb.getName());
        m2ta.setDescription(m2tb.getDescription());
        m2ta.setParentName(m2tb.getParentName());
        if (m2tb.getArchive() != null)
        {
            m2ta.setArchive(m2tb.getArchive());
        }
        if (m2tb.getIncludedInSuperTypeQuery() != null)
        {
            m2ta.setIncludedInSuperTypeQuery(m2tb.getIncludedInSuperTypeQuery());
        }
        //copy properties
        ArrayList<String> propNames = new ArrayList<String>();
        for (M2Property m2pa : m2ta.getProperties())
        {
            propNames.add(m2pa.getName());
        }
        for (String propName : propNames)
        {
            m2ta.removeProperty(propName);
        }
        for (M2Property m2pb : m2tb.getProperties())
        {
            M2Property m2tan = m2ta.createProperty(m2pb.getName());
            copyProperty(m2tan,m2pb);
        }
        //copy associations
        ArrayList<String> associationNames = new ArrayList<String>();
        for (M2ClassAssociation m2pa : m2ta.getAssociations())
        {
            associationNames.add(m2pa.getName());
        }
        for (String associationName : associationNames)
        {
            m2ta.removeAssociation(associationName);
        }
        for (M2ClassAssociation m2pb : m2tb.getAssociations())
        {
            if (m2pb.isChild())
            {
                M2ChildAssociation m2tan = m2ta.createChildAssociation(m2pb.getName());
                copyChildAssociation(m2tan,(M2ChildAssociation)m2pb);
            }
            else
            {
                M2Association m2tan = m2ta.createAssociation(m2pb.getName());
                copyAssociation(m2tan,(M2Association)m2pb);
            }
        }
        //copy property overrides
        ArrayList<String> overrideNames = new ArrayList<String>();
        for (M2PropertyOverride m2pa : m2ta.getPropertyOverrides())
        {
            overrideNames.add(m2pa.getName());
        }
        for (String overrideName : overrideNames)
        {
            m2ta.removePropertyOverride(overrideName);
        }
        for (M2PropertyOverride m2pb : m2tb.getPropertyOverrides())
        {
            M2PropertyOverride m2tan = m2ta.createPropertyOverride(m2pb.getName());
            copyPropertyOverride(m2tan,m2pb);
        }       
        //copy mandatory aspects
        ArrayList<String> mandatoryAspectNames = new ArrayList<String>();
        for (String mandatoryAspectName:  m2ta.getMandatoryAspects())
        {
            mandatoryAspectNames.add(mandatoryAspectName);
        }
        for (String mandatoryAspectName:  mandatoryAspectNames)
        {
            m2ta.removeMandatoryAspect(mandatoryAspectName);
        }        
        for (String mandatoryAspectName: m2tb.getMandatoryAspects())
        {
            m2ta.addMandatoryAspect(mandatoryAspectName);
        }        
    }

    /**
     * @param m2ta
     * @param m2tb
     */
    public static void copyType(M2Type m2ta, M2Type m2tb)
    {
        copyClass(m2ta, m2tb);
    }

    /**
     * @param m2ta
     * @param m2tb
     */
    public static void copyAspect(M2Aspect m2ta, M2Aspect m2tb)
    {
        copyClass(m2ta, m2tb);
    }

    /**
     * @param m2aa
     * @param m2ab
     */
    public static void copyClassAssociation(M2ClassAssociation m2aa, M2ClassAssociation m2ab)
    {
        m2aa.setTitle(m2ab.getTitle());
        m2aa.setName(m2ab.getName());
        m2aa.setDescription(m2ab.getDescription());

        m2aa.setProtected(m2ab.isProtected());
        m2aa.setSourceMandatory(m2ab.isSourceMandatory());
        m2aa.setSourceMany(m2ab.isSourceMany());
        if(m2ab.getSourceRoleName() != null)
        {
            m2aa.setSourceRoleName(m2ab.getSourceRoleName());
        }
        m2aa.setTargetMandatory(m2ab.isTargetMandatory());
        m2aa.setTargetMandatoryEnforced(m2ab.isTargetMandatoryEnforced());
        m2aa.setSourceMany(m2ab.isSourceMany());
        if(m2ab.getTargetRoleName() != null)
        {
            m2aa.setTargetRoleName(m2ab.getTargetRoleName());
        }
        if(m2ab.getTargetClassName() != null)
        {
            m2aa.setTargetClassName(m2ab.getTargetClassName());
        }
    }

    /**
     * @param m2aa
     * @param m2ab
     */
    public static void copyAssociation(M2Association m2aa, M2Association m2ab)
    {
        copyClassAssociation(m2aa, m2ab);
    }

    /**
     * @param m2aa
     * @param m2ab
     */
    public static void copyChildAssociation(M2ChildAssociation m2aa, M2ChildAssociation m2ab)
    {
        copyClassAssociation(m2aa, m2ab);
        m2aa.setPropagateTimestamps(m2ab.isPropagateTimestamps());
        m2aa.setRequiredChildName(m2ab.getRequiredChildName());
        m2aa.setAllowDuplicateChildName(m2ab.allowDuplicateChildName());
    }

    /**
     * @param m2aa
     * @param m2ab
     * @return
     */
    public static boolean compareClassAssociation(M2ClassAssociation m2aa, M2ClassAssociation m2ab)
    {
        // Compare fields
        if ( compareString(m2aa.getName(),m2ab.getName()) 
                || compareString(m2aa.getTitle(),m2ab.getTitle()) 
                || compareString(m2aa.getDescription(),m2ab.getDescription()) 
                || m2aa.isSourceMandatory() != m2ab.isSourceMandatory()
                || m2aa.isSourceMany() != m2ab.isSourceMany()
                || m2aa.isTargetMandatory() != m2ab.isTargetMandatory()
                || m2aa.isTargetMandatoryEnforced() != m2ab.isTargetMandatoryEnforced()
                || m2aa.isTargetMany() != m2ab.isTargetMany()
                || m2aa.isProtected() != m2ab.isProtected()
                || compareString(m2aa.getSourceRoleName(),m2ab.getSourceRoleName())
                || compareString(m2aa.getTargetRoleName(),m2ab.getTargetRoleName())
                || compareString(m2aa.getTargetClassName(),m2ab.getTargetClassName())
        ) 
        {
            return true;
        }
        return false;
    }

    /**
     * @param m2aa
     * @param m2ab
     * @return
     */
    public static boolean compareAssociation(M2Association m2aa, M2Association m2ab)
    {
        return compareClassAssociation(m2aa, m2ab);
    }

    /**
     * @param m2aa
     * @param m2ab
     * @return
     */
    public static boolean compareChildAssociation(M2ChildAssociation m2aa, M2ChildAssociation m2ab)
    {
        if ( compareClassAssociation(m2aa, m2ab) )
        {
            return true;
        }
        else
        {
            if ( compareString(m2aa.getRequiredChildName(),m2ab.getRequiredChildName()) 
                    || m2aa.allowDuplicateChildName() != m2ab.allowDuplicateChildName()
                    || m2aa.isPropagateTimestamps() != m2ab.isPropagateTimestamps() )
            {
                return true;
            }
            else
            {
                return false;
            }
        }
    }
    
    /**
     * This method doesn't seem to work due to possible spring bug in 3.0.0?
     * https://jira.springsource.org/browse/SPR-6592?page=com.atlassian.jirafisheyeplugin%3Afisheye-issuepanel
     * 
     * @return
     */
    @SuppressWarnings("unchecked")
    public static HashMap<String,AbstractConstraint> getAllConstrainTypesAuto()
    {
        HashMap<String,AbstractConstraint> classList = new HashMap<String,AbstractConstraint>();
        
        ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(true);
        provider.addIncludeFilter(new AssignableTypeFilter(AbstractConstraint.class));

        // scan in org.example.package
        Set<BeanDefinition> components = provider.findCandidateComponents("org/alfresco/repo/dictionary/constraint");
        for (BeanDefinition component : components)
        {
            try
            {
                Class clazz = Class.forName(component.getBeanClassName());
                AbstractConstraint object = (AbstractConstraint)clazz.newInstance();
                String name = object.getType();
                classList.put(name, object);
            } catch (ClassNotFoundException e)
            {
                
            } catch (InstantiationException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalAccessException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return classList;
    }
    
    public static HashMap<String,M2Constraint> getAllConstrainTypes()
    {
        HashMap<String,M2Constraint> classList = new HashMap<String,M2Constraint>();
        
        AuthorityNameConstraint c1 = new AuthorityNameConstraint();        
        M2Constraint m2c1 = newConstraint("new");
        m2c1.setType(c1.getType());        
        classList.put(c1.getType(), m2c1);
        
        ListOfValuesConstraint c2 = new ListOfValuesConstraint();        
        M2Constraint m2c2 = newConstraint("new");
        m2c2.setType(c2.getType());
        m2c2.createParameter("caseSensitive", ""+c2.isCaseSensitive());
        ArrayList<String> allowedValues = new ArrayList<String>();
        allowedValues.add("value");
        m2c2.createParameter("allowedValues", allowedValues);
        classList.put(c2.getType(), m2c2);
        
        NumericRangeConstraint c3 = new NumericRangeConstraint();
        M2Constraint m2c3 = newConstraint("new");
        m2c3.setType(c3.getType());
        m2c3.createParameter("minValue", ""+c3.getMinValue());
        m2c3.createParameter("maxValue", ""+c3.getMaxValue());
        classList.put(c3.getType(), m2c3);
        
        RegexConstraint c4 = new RegexConstraint();
        M2Constraint m2c4 = newConstraint("new");
        m2c4.setType(c4.getType());
        m2c4.createParameter("expression", "[A-Z]*");
        m2c4.createParameter("requiresMatch", ""+c4.getRequiresMatch());
        classList.put(c4.getType(), m2c4);
        
        StringLengthConstraint c5 = new StringLengthConstraint();
        M2Constraint m2c5 = newConstraint("new");
        m2c5.setType(c5.getType());
        m2c5.createParameter("minLength", ""+c5.getMinLength());
        m2c5.createParameter("maxLength", ""+c5.getMaxLength());
        classList.put(c5.getType(), m2c5);
        
        UserNameConstraint c6 = new UserNameConstraint();
        M2Constraint m2c6 = newConstraint("new");
        m2c6.setType(c6.getType());
        classList.put(c6.getType(), m2c6);

        return classList;
    }
}
