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
import org.apache.commons.logging.Log;

import edu.internet2.middleware.grouper.util.GrouperUtil;
import edu.internet2.middleware.grouper.validator.EffectiveMembershipValidator;
import edu.internet2.middleware.grouper.validator.GrouperValidator;
import edu.internet2.middleware.grouper.validator.NotNullValidator;

/**
 * @author  blair christensen.
 * @version $Id: Test_Unit_API_EffectiveMembershipValidator_validate.java,v 1.8 2009-01-27 12:09:23 mchyzer Exp $
 * @since   1.2.0
 */
public class Test_Unit_API_EffectiveMembershipValidator_validate extends GrouperTest {

  // PRIVATE CLASS CONSTANTS //
  private static final Log LOG = GrouperUtil.getLog(Test_Unit_API_EffectiveMembershipValidator_validate.class);


  // TESTS //  

  public void testValidate_NullMembership() {
    try {
      LOG.info("testValidate_NullMembership");
      GrouperValidator v = EffectiveMembershipValidator.validate(null);
      assertTrue( "v is invalid", v.isInvalid() );
      assertEquals( "v error msg", NotNullValidator.INVALID, v.getErrorMessage() );
    }
    catch (Exception e) {
      unexpectedException(e);
    }
  } // public void testValidate_NullMembership()
   
  public void testValidate_InvalidType() {
    try {
      LOG.info("testValidate_InvalidType");
      Membership _ms = new Membership();
      _ms.setType(null);
      GrouperValidator v = EffectiveMembershipValidator.validate(_ms);
      assertTrue( "v is invalid", v.isInvalid() );
      assertEquals( "v error msg", EffectiveMembershipValidator.INVALID_TYPE, v.getErrorMessage() );
    }
    catch (Exception e) {
      unexpectedException(e);
    }
  } // public void testValidate_InvalidType()

  public void testValidate_InvalidDepth() {
    try {
      LOG.info("testValidate_InvalidDepth");
      Membership _ms = new Membership();
      _ms.setType(Membership.EFFECTIVE);
      _ms.setDepth(0);
      GrouperValidator v = EffectiveMembershipValidator.validate(_ms);
      assertTrue( "v is invalid", v.isInvalid() );
      assertEquals( "v error msg", EffectiveMembershipValidator.INVALID_DEPTH, v.getErrorMessage() );
    }
    catch (Exception e) {
      unexpectedException(e);
    }
  } // public void testValidate_InvalidDepth()

  public void testValidate_InvalidViaUuid() {
    try {
      LOG.info("testValidate_InvalidViaUuid");
      Membership _ms = new Membership();
      _ms.setType(Membership.EFFECTIVE);
      _ms.setDepth(1);
      GrouperValidator v = EffectiveMembershipValidator.validate(_ms);
      assertTrue( "v is invalid", v.isInvalid() );
      assertEquals( "v error msg", EffectiveMembershipValidator.INVALID_VIAUUID, v.getErrorMessage() );
    }
    catch (Exception e) {
      unexpectedException(e);
    }
  } // public void testValidate_InvalidViaUuid()

  public void testValidate_InvalidParentUuid() {
    try {
      LOG.info("testValidate_InvalidParentUuid");
      Membership _ms = new Membership();
      _ms.setType(Membership.EFFECTIVE);
      _ms.setDepth(1);
      _ms.setViaGroupId("viaUuid");
      _ms.setParentUuid(null);
      GrouperValidator v = EffectiveMembershipValidator.validate(_ms);
      assertTrue( "v is invalid", v.isInvalid() );
      assertEquals( "v error msg", EffectiveMembershipValidator.INVALID_PARENTUUID, v.getErrorMessage() );
    }
    catch (Exception e) {
      unexpectedException(e);
    }
  } // public void testValidate_InvalidParentUuid()

  public void testValidate_InvalidMembership() {
    try {
      LOG.info("testValidate_InvalidMembership");
      Membership _ms = new Membership();
      _ms.setType(Membership.EFFECTIVE);
      _ms.setDepth(1);
      _ms.setViaGroupId("viaUuid");
      _ms.setParentUuid("parentUuid");
      GrouperValidator v = EffectiveMembershipValidator.validate(_ms);
      assertTrue( "v is invalid", v.isInvalid() );
    }
    catch (Exception e) {
      unexpectedException(e);
    }
  } // public void testValidate_InvalidMembership()

} // public class Test_Unit_API_EffectiveMembershipValidator_validate extends GrouperTest
