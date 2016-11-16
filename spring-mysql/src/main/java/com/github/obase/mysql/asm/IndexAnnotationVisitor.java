package com.github.obase.mysql.asm;

import java.util.LinkedList;

import org.springframework.asm.AnnotationVisitor;
import org.springframework.asm.SpringAsmInfo;

import com.github.obase.mysql.annotation.IndexType;
import com.github.obase.mysql.annotation.Using;
import com.github.obase.mysql.data.IndexAnnotation;

class IndexAnnotationVisitor extends AnnotationVisitor {

	final IndexAnnotation data;

	public IndexAnnotationVisitor(IndexAnnotation data) {
		super(SpringAsmInfo.ASM_VERSION);
		this.data = data;
	}

	@Override
	public void visit(String name, Object value) {
		if ("name".equals(name)) {
			data.name = (String) value;
		}
	}

	@Override
	public AnnotationVisitor visitArray(String name) {
		if ("columns".equals(name)) {
			data.columns = new LinkedList<String>();
			return new SimpleArrayAnnotationVisitor<String>(data.columns);
		}
		return null;
	}

	@Override
	public void visitEnum(String name, String desc, String value) {
		if ("type".equals(name)) {
			data.type = IndexType.valueOf(value);
		} else if ("using".equals(name)) {
			data.using = Using.valueOf(value);
		}
	}

}
