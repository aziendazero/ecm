package it.tredi.ecm.web.bean;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Wrapper {
	private int idOffset;
	private List<Integer> idEditabili;
}
