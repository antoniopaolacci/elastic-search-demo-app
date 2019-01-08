package it.example.test.app;

import static org.junit.Assert.assertEquals;

import java.math.BigInteger;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;


/**
 * Unit test for simple app.
 */
public class AppTest {
	
	
    // Define a static logger variable so that it references the
    // Logger instance named "AppTest".
	private final static Logger log = LogManager.getLogger(AppTest.class);
	
	@BeforeClass
	public static void initAppTest() {
		
	}

	@Before
	public void beforeEachTest() {
		log.info("This is executed before each Test");
	}

	@After
	public void afterEachTest() {
		log.info("This is executed after each Test");
	}

	@Test
	public void testIdGenerator() {

		log.info("testIdGenerator() method called.");
		
		try {

			String id = String.format("%040d", new BigInteger(UUID.randomUUID().toString().replace("-", ""), 16));

			log.info("ID = ["+id+"]");
			
			assertEquals(String.class, id.getClass());
			
		} catch (Exception e) {

			e.printStackTrace(System.err);
			
			log.error("Error: ",e);

		}
		
	}

	@Ignore
	@Test(expected = Exception.class)
	public void testAnException() throws Exception { }


	@Ignore
	@Test
	public void testEqual() { }
 
} //end class
