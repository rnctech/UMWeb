package com.rnctech.um.web;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AbstractTestExecutionListener;

import com.rnctech.um.web.service.UMLDAPService;

/**
 * @contributor zilin
 * 
 */

public class LdapTest extends UMWebTest{
	
	@Autowired
	UMLDAPService ldapservice;
	
	public static LdapTemplate tmpl;
	
	  @Before
	  public void init() {
		  if(null == tmpl)
			  tmpl = getTemplate();
	  }
	  
	  @After
	  public void tearDown(){
		  tmpl = null;
	  }
	  
	
    public static LdapContextSource getLocalLdapContext(){
        LdapContextSource ctxSrc = new LdapContextSource();
        ctxSrc.setUrl("ldap://127.0.0.1:33389");
        ctxSrc.setBase("dc=rnctech,dc=com");
        ctxSrc.setUserDn("");
        ctxSrc.setPassword("");        
        ctxSrc.afterPropertiesSet(); 
        return ctxSrc;
    }
    
    public static LdapContextSource getNumerifyLdapContext(){
        LdapContextSource ctxSrc = new LdapContextSource();
        ctxSrc.setUrl("ldap://10.0.1.83:10389");
        ctxSrc.setBase("dc=ldb,dc=com");
        ctxSrc.setUserDn("uid=admin,ou=system");
        ctxSrc.setPassword("T3ndulkar");        
        ctxSrc.afterPropertiesSet(); 
        return ctxSrc;
    }  
    
    public static LdapTemplate getTemplate(){
    	LdapContextSource ctxSrc = getLocalLdapContext(); 
        LdapTemplate tmpl = new LdapTemplate(ctxSrc);
        return tmpl;
    }
    
    @Test
    public void testUser() throws Exception {
    	ldapservice.setLdapTemplate(getTemplate());
        
    	ldapservice.createLDAPUser("Homedepot_test");
        List ps = ldapservice.getAllPersonNames();
        for(Object o : ps){
        	System.out.println(o.toString()+"\t");
        }        
    }
    
    @Test
    public void testGroup() throws Exception {
    	ldapservice.setLdapTemplate(getTemplate());
    	ldapservice.createLDAPOrg("Homedepot");
        
        List orgs = ldapservice.getAllOrgNames();
        for(Object o : orgs){
        	System.out.println(o.toString()+"\t");
        }
    }

}
