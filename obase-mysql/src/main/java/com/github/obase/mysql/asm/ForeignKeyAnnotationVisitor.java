package com.github.obase.mysql.asm;

import java.util.List;

import org.springframework.asm.AnnotationVisitor;
import org.springframework.asm.SpringAsmInfo;

import com.github.obase.mysql.data.ReferenceAnnotation;

class ForeignKeyAnnotationVisitor extends AnnotationVisitor {

	final List<ReferenceAnnotation> data;

	public ForeignKeyAnnotationVisitor(List<ReferenceAnnotation> data) {
		super(SpringAsmInfo.ASM_VERSION);
		this.data = data;
	}

	@Override
	public AnnotationVisitor visitArray(String name) {
		return this; // 只有value
	}

	@Override
	public AnnotationVisitor visitAnnotation(String name, String desc) {
		ReferenceAnnotation reference = new ReferenceAnnotation();
		data.add(reference);
		return new ReferenceAnnotationVisitor(reference);
	}

}
