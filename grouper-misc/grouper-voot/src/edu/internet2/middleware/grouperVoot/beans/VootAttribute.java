/*******************************************************************************
 * Copyright 2012 Internet2
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package edu.internet2.middleware.grouperVoot.beans;

/**
 * Class representing VOOT additional attributes field in the object representing a person.
 * @author Andrea Biancini <andrea.biancini@gmail.com>
 */
public class VootAttribute {
  /** Name of the attribute */
  private String name;
  
  /** Value of the attribute */
  private String[] values;
  
  /**
   * Method to check if an object is equal to the current object.
   * @param otherVootAttribute the other object to check
   */
  @Override
  public boolean equals(Object otherVootAttribute) {
    if (otherVootAttribute instanceof VootAttribute) {
      VootAttribute other = (VootAttribute) otherVootAttribute;
      if (!other.getName().equals(name)) return false;
      
      if (values == null && other.getValues() != null) return false;
      if (values != null && other.getValues() == null) return false;
      if (values.length != other.getValues().length) return false;
      for (int i = 0; i < values.length; ++i) {
        if (!values[i].equals(other.getValues()[i])) return false;
      }
      
      return true;
    }
    else {
      return false;
    }
  }

  /**
   * Get the name of the attribute.
   * @return name of the attribute.
   */
  public String getName() {
    return this.name;
  }

  /**
   * Set the name of the attribute.
   * @param name1 the name of the attribute.
   */
  public void setName(String name1) {
    this.name = name1;
  }

  /**
   * Get the values of the attribute.
   * @return values of the attribute.
   */
  public String[] getValues() {
    return this.values;
  }

  /**
   * Set the values of the attribute.
   * @param values1 the values of the attribute.
   */
  public void setValues(String[] values1) {
    this.values = values1;
  }
}
