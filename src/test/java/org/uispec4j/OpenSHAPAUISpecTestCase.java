package org.uispec4j;

import org.openshapa.OpenSHAPA;
import org.uispec4j.interception.MainClassAdapter;

/**
 * Extends UISpecTestCase with methods specific to OpenSHAPA.
 */
public abstract class OpenSHAPAUISpecTestCase extends UISpecTestCase {

  protected OpenSHAPAUISpecTestCase() {
      super();
  }

  protected OpenSHAPAUISpecTestCase(String testName) {
    super(testName);
  }

  /**
   * Initializes the resources needed by the test case.<br>
   * NB: If you provide your own implementation, do not forget to
   * call this one first.
   */
  @Override
  protected void setUp() throws Exception {
    super.setUp();
    setAdapter(new MainClassAdapter(OpenSHAPA.class, new String[0]));
  }

  /**
   * Checks whether an unexpected exception had occurred, and
   * releases the test resources.
   */
  @Override
  protected void tearDown() throws Exception {
    OpenSHAPA.getApplication().cleanUpForTests();
    super.tearDown();
  }
}
