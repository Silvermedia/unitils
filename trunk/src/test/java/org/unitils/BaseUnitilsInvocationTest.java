package org.unitils;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.fail;

import java.util.Iterator;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.unitils.TracingTestListener.Call;
import org.unitils.TracingTestListener.Invocation;
import org.unitils.TracingTestListener.TestFramework;
import org.unitils.UnitilsInvocationTest.UnitilsJUnit3Test_EmptyTestClass;
import org.unitils.core.TestListener;
import org.unitils.core.Unitils;
import org.unitils.inject.util.InjectionUtils;
import org.unitils.spring.SpringUnitilsJUnit38Test;
import org.unitils.spring.SpringUnitilsJUnit4Test;
import org.unitils.spring.SpringUnitilsTestNGTest;

public class BaseUnitilsInvocationTest {

	/* TestListener normally used by Unitils, to be restored after test */
	private static TestListener originalTestListener;
	
    /* Listener that records all method invocations during the tests */
    protected TracingTestListener tracingTestListener;
    
    private Iterator<Call> callListIterator;

    protected TestFramework testFramework;
    
    protected TestExecutor testExecutor;

	protected BaseUnitilsInvocationTest(TestFramework testFramework,
			TestExecutor testExecutor) {
		this.testFramework = testFramework;
		this.testExecutor = testExecutor;
	}
    
	@BeforeClass
	public static void storeOriginalTestListener() {
		originalTestListener = Unitils.getInstance().getTestListener();
	}
	
	@AfterClass
	public static void restoreOrginalTestListener() {
		InjectionUtils.injectInto(originalTestListener, Unitils.getInstance(), "testListener");
	}


    /**
     * Sets up the test installing the tracing test listener that will record all method invocations during the test.
     * The current test listeners are stored so that they can be restored during the class tear down. 
     * Also re-initializes the base-classes so that, for example, beforeAll() will be called another time.
     */
    @Before
    public void init() throws Exception {
    	// Create a test listener that traces the test execution, and make sure it is used by the tests to
    	// record their calls
        tracingTestListener = new TracingTestListener();

        UnitilsJUnit3Test.setTracingTestListener(tracingTestListener);
        UnitilsJUnit3Test_EmptyTestClass.setTracingTestListener(tracingTestListener);
        SpringUnitilsJUnit38Test.setTracingTestListener(tracingTestListener);

        UnitilsJUnit4Test.setTracingTestListener(tracingTestListener);
        SpringUnitilsJUnit4Test.setTracingTestListener(tracingTestListener);

        UnitilsTestNGTest.setTracingTestListener(tracingTestListener);
        UnitilsTestNGTest_EmptyTestClass.setTracingTestListener(tracingTestListener);
        UnitilsTestNGTest_GroupsTest.setTracingTestListener(tracingTestListener);
        
        SpringUnitilsTestNGTest.setTracingTestListener(tracingTestListener);
        
        InjectionUtils.injectInto(tracingTestListener, Unitils.getInstance(), "testListener");
    }
    
    @After
    public void cleanUp() throws Exception {
    	UnitilsJUnit3Test.setTracingTestListener(null);
        UnitilsJUnit3Test_EmptyTestClass.setTracingTestListener(null);
        SpringUnitilsJUnit38Test.setTracingTestListener(null);

        UnitilsJUnit4Test.setTracingTestListener(null);
        SpringUnitilsJUnit4Test.setTracingTestListener(null);

        UnitilsTestNGTest.setTracingTestListener(null);
        UnitilsTestNGTest_EmptyTestClass.setTracingTestListener(null);
        UnitilsTestNGTest_GroupsTest.setTracingTestListener(null);
        
        SpringUnitilsTestNGTest.setTracingTestListener(null);
    }
    
    protected void assertInvocation(Invocation invocation, Class<?> testClass, TestFramework... testFrameworks) {
    	if (isApplicableFor(testFrameworks)) {
			if (!getCallListIterator().hasNext()) {
				fail("No more invocations in calllist. Calllist:\n"
						+ toString(tracingTestListener.getCallList()));
			}
    		assertEquals("Calllist:\n"
					+ toString(tracingTestListener.getCallList()), new Call(
					invocation, testClass),
					getCallListIterator().next());
		}
	}

	protected void assertNoMoreInvocations() {
		assertFalse("No more invocations expected, calllist:\n" + toString(tracingTestListener.getCallList()), 
				getCallListIterator().hasNext());
	}

	
	private Iterator<Call> getCallListIterator() {
		if (callListIterator == null) {
			callListIterator = tracingTestListener.getCallList().iterator();
		}
		return callListIterator;
	}
	
	protected boolean isApplicableFor(TestFramework[] testFrameworks) {
		if (testFrameworks.length == 0) {
			return true;
		}
		for (TestFramework framework : testFrameworks) {
			if (framework.equals(this.testFramework)) {
				return true;
			}
		}
		return false;
	}

	private String toString(List<Call> callList) {
		StringBuffer result = new StringBuffer();
		for (Call call : callList) {
			result.append(call.toString() + "\n");
		}
		return result.toString();
	}
    
}
