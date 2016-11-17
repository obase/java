package com.github.obase.mysql.asm;

import static com.github.obase.mysql.asm.AsmKit.*;

import org.springframework.asm.AnnotationVisitor;
import org.springframework.asm.FieldVisitor;
import org.springframework.asm.SpringAsmInfo;

import com.github.obase.mysql.data.ColumnAnnotation;
import com.github.obase.mysql.data.FieldMetaInfo;

public class SimpleFieldVisitor extends FieldVisitor {

	final FieldMetaInfo data;

	public SimpleFieldVisitor(FieldMetaInfo data) {
		super(SpringAsmInfo.ASM_VERSION);
		this.data = data;
	}

	@Override
	public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
		if (COLUMN_ANNOTATION_DESC.equals(desc)) {
			data.columnAnnotation = new ColumnAnnotation();
			return new ColumnAnnotationVisitor(data.columnAnnotation);
		}
		return null;
	}

}
