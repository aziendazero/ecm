package it.tredi.ecm.web.bean;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountChangePassword {
	private String oldPassword;
	private String newPassword;
	private String confirmNewPassword;
}
