/*
  Copyright (C) 2004-2007 University Corporation for Advanced Internet Development, Inc.
  Copyright (C) 2004-2007 The University Of Chicago

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
*/


package edu.internet2.middleware.grouper;
import  edu.internet2.middleware.grouper.internal.util.ParameterHelper;


/**
 * Test {@link ParameterHelper}.
 * <p/>
 * @author  blair christensen.
 * @version $Id: Test_util_ParameterHelper.java,v 1.2 2007-08-24 19:42:50 blair Exp $
 * @since   @HEAD@
 */
public class Test_util_ParameterHelper extends GrouperTest {


  private ParameterHelper param;


  public void setUp() {
    super.setUp();
    this.param = new ParameterHelper();
  }

  public void tearDown() {
    super.tearDown();
  }



  /**
   * @since   @HEAD@
   */
  public void test_notNull_accessAdapterNull() {
    try {
      this.param.notNullAccessAdapter(null);
      fail("failed to throw IllegalArgumentException");
    }
    catch (IllegalArgumentException eExpected) {
      assertTrue("threw expected exception", true);
    }
  }
  /**
   * @since   @HEAD@
   */
  public void test_notNull_accessAdapterNotNull() {
    assertEquals( this.param, this.param.notNullAccessAdapter( new GrouperAccessAdapter() ) ); 
  }



  /**
   * @since   @HEAD@
   */
  public void test_notNull_accessResolverNull() {
    try {
      this.param.notNullAccessResolver(null);
      fail("failed to throw IllegalArgumentException");
    }
    catch (IllegalArgumentException eExpected) {
      assertTrue("threw expected exception", true);
    }
  }
  /**
   * @since   @HEAD@
   */
  public void test_notNull_accessResolverNotNull() {
    assertEquals( this.param, this.param.notNullAccessResolver( new MockAccessResolver() ) ); 
  }




  /**
   * @since   @HEAD@
   */
  public void test_notNull_groupNull() {
    try {
      this.param.notNullGroup(null);
      fail("failed to throw IllegalArgumentException");
    }
    catch (IllegalArgumentException eExpected) {
      assertTrue("threw expected exception", true);
    }
  }
  /**
   * @since   @HEAD@
   */
  public void test_notNull_groupNotNull() {
    assertEquals( this.param, this.param.notNullGroup( new Group() ) );
  }



  /**
   * @since   @HEAD@
   */
  public void test_notNull_grouperSessionNull() {
    try {
      this.param.notNullGrouperSession(null);
      fail("failed to throw IllegalArgumentException");
    }
    catch (IllegalArgumentException eExpected) {
      assertTrue("threw expected exception", true);
    }
  }
  /**
   * @since   @HEAD@
   */
  public void test_notNull_grouperSessionNotNull() 
    throws  SessionException
  {
    // TODO 20070816 i need a MockGrouperSession
    assertEquals( this.param, this.param.notNullGrouperSession( GrouperSession.start( SubjectFinder.findAllSubject() ) ) );
  }



  /**
   * @since   @HEAD@
   */
  public void test_notNull_namingAdapterNull() {
    try {
      this.param.notNullNamingAdapter(null);
      fail("failed to throw IllegalArgumentException");
    }
    catch (IllegalArgumentException eExpected) {
      assertTrue("threw expected exception", true);
    }
  }
  /**
   * @since   @HEAD@
   */
  public void test_notNull_namingAdapterNotNull() {
    assertEquals( this.param, this.param.notNullNamingAdapter( new GrouperNamingAdapter() ) ); 
  }



  /**
   * @since   @HEAD@
   */
  public void test_notNull_namingResolverNull() {
    try {
      this.param.notNullNamingResolver(null);
      fail("failed to throw IllegalArgumentException");
    }
    catch (IllegalArgumentException eExpected) {
      assertTrue("threw expected exception", true);
    }
  }
  /**
   * @since   @HEAD@
   */
  public void test_notNull_namingResolverNotNull() {
    assertEquals( this.param, this.param.notNullNamingResolver( new MockNamingResolver() ) );
  }



  public void test_notNull_privilegeNull() {
    try {
      this.param.notNullPrivilege(null);
      fail("failed to throw IllegalArgumentException");
    }
    catch (IllegalArgumentException eExpected) {
      assertTrue("threw expected exception", true);
    }
  }

  public void test_notNull_privilegeNotNull() {
    assertEquals( this.param, this.param.notNullPrivilege( AccessPrivilege.ADMIN ) );
  }



  /**
   * @since   @HEAD@
   */
  public void test_notNull_stemNull() {
    try {
      this.param.notNullStem(null);
      fail("failed to throw IllegalArgumentException");
    }
    catch (IllegalArgumentException eExpected) {
      assertTrue("threw expected exception", true);
    }
  }
  /**
   * @since   @HEAD@
   */
  public void test_notNull_stemNotNull() {
    assertEquals( this.param, this.param.notNullStem( new Stem() ) ); 
  }



  public void test_notNull_subjectNull() {
    try {
      this.param.notNullSubject(null);
      fail("failed to throw IllegalArgumentException");
    }
    catch (IllegalArgumentException eExpected) {
      assertTrue("threw expected exception", true);
    }
  }

  public void test_notNull_subjectNotNull() {
    assertEquals( this.param, this.param.notNullSubject( SubjectFinder.findAllSubject() ) );
  }

}

