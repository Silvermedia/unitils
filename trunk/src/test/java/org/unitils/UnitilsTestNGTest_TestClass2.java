package org.unitils;

import org.testng.annotations.*;
import org.unitils.core.TestListener;

import java.util.List;

/**
 * TestNG test class containing 2 test methods
 * <p/>
 * Test class used in the {@link UnitilsTestNGTest} tests.
 * This is a public class because there is a bug in TestNG that does not allow tests on inner classes.
 */
public class UnitilsTestNGTest_TestClass2 extends UnitilsTestNG {


    private static List<String> callList;

    public static void setCallList(List<String> list) {
        callList = list;
    }


    @BeforeClass
    public void beforeClass() {
        callList.add("[Test]    beforeTestClass   - TestClass2");
    }

    @AfterClass
    public void afterClass() {
        callList.add("[Test]    afterTestClass    - TestClass2");
    }

    @BeforeMethod
    public void setUp() {
        callList.add("[Test]    testSetUp         - TestClass2");
    }

    @AfterMethod
    public void tearDown() {
        callList.add("[Test]    testTearDown      - TestClass2");
    }

    @Test
    public void test1() {
        callList.add("[Test]    testMethod        - TestClass2 - test1");
    }

    @Test
    public void test2() {
        callList.add("[Test]    testMethod        - TestClass2 - test2");
    }


    @Override
    protected TestListener createTestListener() {
        return new TracingTestListener(callList);
    }
}
