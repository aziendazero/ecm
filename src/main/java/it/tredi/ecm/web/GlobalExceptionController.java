package it.tredi.ecm.web;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import it.tredi.ecm.utils.Utils;

@ControllerAdvice
public class GlobalExceptionController {
	private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionController.class);

	@ExceptionHandler(Exception.class)
	public ModelAndView handleAllException(HttpServletRequest request, Exception ex){
		Utils.logError(LOGGER, "AccessDeniedException", ex);
		ModelAndView model = new ModelAndView();
		String httpErrorStatus = (String) request.getAttribute("javax.servlet.error.status_code");
		Throwable throwable = (Throwable) request.getAttribute("javax.servlet.error.exception");
		String httpErrorMessage = "";

		if(ex instanceof AccessDeniedException){
			httpErrorStatus = "403";
			httpErrorMessage = "ACCESSO NEGATO";
		}else if(ex instanceof Exception){
			httpErrorStatus = (String) request.getAttribute("javax.servlet.error.status_code");
			throwable = (Throwable) request.getAttribute("javax.servlet.error.exception");
			httpErrorMessage = "";
			if (throwable != null) {
				httpErrorMessage = throwable.getMessage();
			}
		}

		model.addObject("errorStatus", httpErrorStatus.isEmpty() ? "500" : httpErrorStatus);
		model.addObject("errorMessage", httpErrorMessage.isEmpty() ? ex.getMessage() : httpErrorMessage);
		model.setViewName("error");
		return model;
	}
}
