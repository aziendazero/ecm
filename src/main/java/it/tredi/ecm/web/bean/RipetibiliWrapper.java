package it.tredi.ecm.web.bean;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RipetibiliWrapper extends Wrapper{
	private String field;
	private List<String> elements = new ArrayList<String>();
}
