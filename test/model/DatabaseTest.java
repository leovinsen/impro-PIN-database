/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author asus
 */
public class DatabaseTest {
    static Database d;
    public DatabaseTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
        d = new Database();
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void testGetResultSetData() throws Exception {
        System.out.println("getResultSetData");
        d.checkMember("IP00000544");
        String[] expResult = {"IP00000544", "9280091301", "Lenardo Vinsen", "1234567890", "IP00000001", "IP00000009"}; 
        String[] result = d.getMemberInfo();
        assertArrayEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }
    
    @Test
    public void testEditChild() throws Exception {
        System.out.println("testEditChild");
        Stringd.editChild("IP00000544", "IP00000544", "IP00000001", "IP00000000");
        
    }
    
}
