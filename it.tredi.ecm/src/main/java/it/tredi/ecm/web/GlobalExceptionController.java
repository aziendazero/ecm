package it.tredi.ecm.web;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class GlobalExceptionController {
	private static final Logger LOGGER = Logger.getLogger(GlobalExceptionController.class);
	
	@ExceptionHandler(Exception.class)
	public ModelAndView handleAllException(HttpServletRequest request, Exception ex){
		LOGGER.error(ex);
		
		System.out.println(ex);
		
		ModelAndView model = new ModelAndView();
		
		String httpErrorStatus = (String) request.getAttribute("javax.servlet.error.status_code");
		Throwable throwable = (Throwable) request.getAttribute("javax.servlet.error.exception");
		String httpErrorMessage = "";
	    if (throwable != null) {
	    	httpErrorMessage = throwable.getMessage();
	    }
		
	    model.addObject("errorStatus", httpErrorStatus.isEmpty() ? "500" : httpErrorStatus);
		//model.addObject("errorMessage", ex.getMessage());
	    model.addObject("errorMessage", httpErrorMessage.isEmpty() ? ex.getMessage() : httpErrorMessage);
	    
	    model.setViewName("error");
	    return model;
	}
}
