package it.tredi.ecm;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

import it.tredi.ecm.utils.Utils;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@ActiveProfiles("demo")
@WithUserDetails("test1")

@Ignore
public class CodiceFiscaleValidatorTest {
	private static Logger LOGGER = LoggerFactory.getLogger(CodiceFiscaleValidatorTest.class);

	class CF{
		private String cf;
		CF(String cf){
			this.cf = cf;
		}
		public String getCf() {
			return cf;
		}
		public void setCf(String cf) {
			this.cf = cf;
		}


	}

	@Test
	public void goodCF() throws Exception {
		CF cf = new CF("FOXDRA26C24H872Y");
		Errors errors = new BeanPropertyBindingResult(cf, "cf");

		Assert.assertTrue(! Utils.rejectIfCodFiscIncorrect("FOXDRA26C24H872Y", errors, "cf"));
		Assert.assertTrue(! errors.hasErrors());
	}

	@Test
	public void wrongCF() throws Exception {
		CF cf = new CF("FOXDRA26C24H8721");
		Errors errors = new BeanPropertyBindingResult(cf, "cf");

		Assert.assertTrue(Utils.rejectIfCodFiscIncorrect("FOXDRA26C24H8721", errors, "cf"));
		Assert.assertTrue(errors.hasErrors());
	}

	@Test
	public void omocodia() throws Exception {
		CF cf = new CF("BNZVCNPNSMLERTPX");
		Errors errors = new BeanPropertyBindingResult(cf, "cf");

		Assert.assertTrue(! Utils.rejectIfCodFiscIncorrect("BNZVCNPNSMLERTPX", errors, "cf"));
		Assert.assertTrue(!errors.hasErrors());
	}

	@Test
	public void goodCFm() throws Exception {
		Map<String, String> errMap = new HashMap<>();
		Assert.assertTrue(! Utils.rejectIfCodFiscIncorrect("FOXDRA26C24H872Y", errMap, "cf"));
		Assert.assertTrue(errMap.size() == 0);
	}

	@Test
	public void wrongCFm() throws Exception {
		Map<String, String> errMap = new HashMap<>();

		Assert.assertTrue(Utils.rejectIfCodFiscIncorrect("FOXDRA26C24H8721", errMap, "cf"));
		Assert.assertTrue(errMap.size() != 0);
		Assert.assertTrue(errMap.get("cf").equals("error.invalid"));
	}

	@Test
	public void emptyCFm() throws Exception {
		Map<String, String> errMap = new HashMap<>();

		Assert.assertTrue(Utils.rejectIfCodFiscIncorrect(null, errMap, "cf"));
		Assert.assertTrue(errMap.size() != 0);
		Assert.assertTrue(errMap.get("cf").equals("error.empty"));
	}

	@Test
	public void omocodiam() throws Exception {
		Map<String, String> errMap = new HashMap<>();

		Assert.assertTrue(! Utils.rejectIfCodFiscIncorrect("BNZVCNPNSMLERTPX", errMap, "cf"));
		Assert.assertTrue(errMap.size() == 0);
	}

	@Test
	public void goodCFv() throws Exception {
		Assert.assertTrue(! Utils.rejectIfCodFiscIncorrect("FOXDRA26C24H872Y"));
	}

	@Test
	public void wrongCFv() throws Exception {
		Assert.assertTrue(Utils.rejectIfCodFiscIncorrect("FOXDRA26C24H8721"));
	}

	@Test
	public void emptyCFv() throws Exception {
		Assert.assertTrue(Utils.rejectIfCodFiscIncorrect(null));
	}

	@Test
	public void omocodiav() throws Exception {
		Assert.assertTrue(! Utils.rejectIfCodFiscIncorrect("BNZVCNPNSMLERTPX"));
	}


}
