package com.github.obase.mysql.asm;

import org.springframework.asm.FieldVisitor;
import org.springframework.asm.MethodVisitor;

import com.github.obase.mysql.data.ClassMetaInfo;

final class AnnotationMetaInfoClassVisitor extends MetaInfoClassVisitor {

	public AnnotationMetaInfoClassVisitor(ClassMetaInfo result) {
		super(result);
	}

	@Override
	public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
		result.internalName = name;
		this.superName = superName;
	}

	@Override
	public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
		if (result.tableAnnotation != null || result.metaAnnotation != null) {
			return super.visitField(access, name, desc, signature, value);
		}
		return null;
	}

	@Override
	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
		if (result.tableAnnotation != null || result.metaAnnotation != null) {
			return super.visitMethod(access, name, desc, signature, exceptions);
		}
		return null;
	}

	@Override
	public void visitEnd() {
		if (result.tableAnnotation != null || result.metaAnnotation != null) {
			super.visitEnd();
		}
	}

}
