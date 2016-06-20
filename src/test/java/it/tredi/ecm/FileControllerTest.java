package it.tredi.ecm;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.fileUpload;
import java.io.FileInputStream;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import it.tredi.ecm.dao.enumlist.Costanti;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@ActiveProfiles("dev")
@WithUserDetails("admin")
public class FileControllerTest {
	
	@Autowired
	private WebApplicationContext webApplicationContext;

	private MockMvc mockMvc;

	@Before
	public void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
	}

	@Test
	public void uploadFile() throws Exception {
		FileInputStream inputFile = new FileInputStream("C:\\Users\\dpranteda\\Pictures\\Balocco.jpg");  
		MockMultipartFile multiPartFile = new MockMultipartFile("multiPartFile", "pippo150.jpg", "multipart/form-data", inputFile); 

		ResultActions actions = this.mockMvc.perform(fileUpload("/file/upload")
				.file(multiPartFile)
				.contentType(MediaType.MULTIPART_FORM_DATA)
				.param("fileId", "156")
				.param("tipo", Costanti.FILE_ATTO_NOMINA)
				);
		
		String response = actions.andReturn().getResponse().getContentAsString();
		
		System.out.println(response);
	}
	
}
