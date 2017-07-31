package com.rnctech.umweb.service;

import javax.naming.ldap.LdapName;

import java.io.Serializable;
import java.util.List;

import javax.naming.InvalidNameException;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.filter.AndFilter;
import org.springframework.ldap.filter.EqualsFilter;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

@Service
public class UMLDAPService {
    
	protected LdapTemplate ldapTemplate;
    
	protected String BASE_DN = "dc=rnctech,dc=com";
	
	public static Logger logger = Logger.getLogger(UMLDAPService.class);

    public void setLdapTemplate(LdapTemplate ldapTemplate) {
        this.ldapTemplate = ldapTemplate;
    }
    
    protected LdapName buildDn(String name) throws Exception {     	   
  	   LdapName dn = new LdapName(name);     	   
       return dn; 
    }
    
    public UMLDAPService() {
    }
    
    public UMLDAPService(LdapTemplate ldapTemplate) {
        this.ldapTemplate = ldapTemplate;
    }
    
    protected LdapName buildOrgDn(String orgName) throws Exception {     	   
 	   LdapName dn = new LdapName("ou="+orgName+",ou=groups");     	   
     	return dn; 
    }
            
    public void createLDAPOrg(String orgName) throws Exception {
    	LdapName dn = buildDn(orgName); 
    	ldapTemplate.bind(dn, null, buildOrgAttributes(orgName)); 
    	}

    
    public Attributes buildOrgAttributes(String orgName) {
        Attributes attributes=new BasicAttributes();
        Attribute basicAttribute=new BasicAttribute("objectclass");
        basicAttribute.add("top");
        basicAttribute.add("organizationalUnit");
        attributes.put(basicAttribute);
        attributes.put("ou",orgName+"_group"); 
        return attributes;
    }
    
    public List getAllOrgNames() throws Exception {
        logger.info("getAllOrgNames called : ");
        EqualsFilter filter = new EqualsFilter("objectclass", "organizationalUnit");
        return ldapTemplate.search("",
                filter.encode(),
                new AttributesMapper() {
                    public Object mapFromAttributes(Attributes attrs) throws NamingException {
                        return attrs.get("ou").get()+" "+(null != attrs.get("contact")?attrs.get("contact").get():"");
                    }
                });
    }
    
    protected LdapName buildUserDn(String cn) throws Exception {    	   
 	   LdapName dn = new LdapName("uid="+cn.replaceAll("_", "")+",ou=people");     	   
     	return dn; 
     	}
     
     public void createLDAPUser(String name) throws Exception {
     	LdapName dn = buildDn(name); 
     	ldapTemplate.bind(dn, null, buildAttributes(name)); 
     	}

     
     public Attributes buildAttributes(String cn) {
         Attributes attributes=new BasicAttributes();
         Attribute basicAttribute=new BasicAttribute("objectclass");
         basicAttribute.add("top");
         basicAttribute.add("person");
         basicAttribute.add("organizationalPerson");
         basicAttribute.add("inetOrgPerson");
         attributes.put(basicAttribute);                        
         attributes.put("sn",cn.substring(0, cn.indexOf("_")));
         attributes.put("cn",cn);       
         attributes.put("uid",cn.replaceAll("_", ""));
         attributes.put("userPassword","Admin123"); 
         return attributes;
     }

     public List getAllPersonNames() throws Exception {
         logger.info("getAllPersonNames called : ");
         EqualsFilter filter = new EqualsFilter("objectclass", "person");
         return ldapTemplate.search("",
                 filter.encode(),
                 new AttributesMapper() {
                     public Object mapFromAttributes(Attributes attrs) throws NamingException {
                         return attrs.get("cn").get()+" "+(null != attrs.get("phone")?attrs.get("phone").get():"");
                     }
                 });
     }
     
 	public List getAllContactNames() {
 		return ldapTemplate.search("", "(objectClass=person)",
 				new AttributesMapper() {
 					public Object mapFromAttributes(Attributes attrs)
 							throws NamingException {
 						return attrs.get("email").get();
 					}
 				});
 	}
  
 	public List getContactDetails(String objectclass){
 		AndFilter andFilter = new AndFilter();
 		andFilter.and(new EqualsFilter("objectClass",objectclass));
 		System.out.println("LDAP Query " + andFilter.encode());
 		return ldapTemplate.search("", andFilter.encode(),new AttributesMapper(){
				public Object mapFromAttributes(Attributes attrs)
						throws NamingException {
					return attrs.get("email").get()+" "+attrs.get("phone").get()+" "+attrs.get("compony");
				}
 			
 		});
  
 	}

	public OrgGroup create(OrgGroup group) {
		try {
			createLDAPOrg(group.getGroupName().trim());
			createLDAPOrg(group.getGroupName().trim()+"_admin");
		} catch (Exception e) {
			logger.info(e.getMessage());
		}
		return null;
	}

	public boolean remove(OrgGroup group) {
		try {
			LdapName dn = new LdapName("ou="+group.getGroupName().trim()+"_group,ou=groups");
			ldapTemplate.unbind(dn, true);
			return true;
		} catch (InvalidNameException e) {
			logger.info(e.getMessage());
		}
		return false;
	}
	
	public class BaseModel implements Serializable{
		
		Long id;
		String displayName;
		String description;
		
		public Long getId() {
			return id;
		}
		public void setId(Long id) {
			this.id = id;
		}

		public String getDisplayName() {
			return displayName;
		}
		public void setDisplayName(String displayName) {
			this.displayName = displayName;
		}
		public String getDescription() {
			return description;
		}
		public void setDescription(String description) {
			this.description = description;
		}

	}
	
	public class OrgGroup extends BaseModel {
		private String groupName;
		private List<String> members;
		public String getGroupName() {
			return groupName;
		}
		public void setGroupName(String groupName) {
			this.groupName = groupName;
		}
		public List<String> getMembers() {
			return members;
		}
		public void setMembers(List<String> members) {
			this.members = members;
		}
	}
    
}
