package com.github.obase.mysql.asm;

import java.util.LinkedList;

import org.springframework.asm.AnnotationVisitor;
import org.springframework.asm.SpringAsmInfo;

import com.github.obase.mysql.annotation.Match;
import com.github.obase.mysql.annotation.Option;
import com.github.obase.mysql.data.ReferenceAnnotation;

class ReferenceAnnotationVisitor extends AnnotationVisitor {

	final ReferenceAnnotation data;

	public ReferenceAnnotationVisitor(ReferenceAnnotation data) {
		super(SpringAsmInfo.ASM_VERSION);
		this.data = data;
	}

	@Override
	public void visit(String name, Object value) {
		if ("name".equals(name)) {
			data.name = (String) value;
		} else if ("targetTable".equals(name)) {
			data.targetTable = (String) value;
		}
	}

	@Override
	public AnnotationVisitor visitArray(String name) {
		if ("columns".equals(name)) {
			data.columns = new LinkedList<String>();
			return new SimpleArrayAnnotationVisitor<String>(data.columns);
		} else if ("targetColumns".equals(name)) {
			data.targetColumns = new LinkedList<String>();
			return new SimpleArrayAnnotationVisitor<String>(data.targetColumns);
		}
		return null;
	}

	@Override
	public void visitEnum(String name, String desc, String value) {
		if ("match".equals(name)) {
			data.match = Match.valueOf(value);
		} else if ("onDelete".equals(name)) {
			data.onDelete = Option.valueOf(value);
		} else if ("onUpdate".equals(name)) {
			data.onUpdate = Option.valueOf(value);
		}
	}

}
