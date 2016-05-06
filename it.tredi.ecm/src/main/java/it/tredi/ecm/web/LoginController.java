package it.tredi.ecm.web;

import java.util.Locale;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class LoginController {

	@RequestMapping("/")
	public String root(Locale locale) {
		return "redirect:/home";
	}

	/** Home page. */
	@RequestMapping("/home")
	public String home() {
		return "home";
	}

	/** Login form. */
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String login() {
		return "login";
	}

	/** Reset form. */
	//TODO implementare reset form della password dimenticata
	@RequestMapping("/reset")
	public String reset() {
		return "login";
	}

	/** Main form. */
	@RequestMapping(value = "/main", method = RequestMethod.GET)
	public String main() {
		return "main";
	}
}
