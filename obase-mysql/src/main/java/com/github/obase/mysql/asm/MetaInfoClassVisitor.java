package com.github.obase.mysql.asm;

import static com.github.obase.mysql.asm.AsmKit.*;

import java.io.IOException;
import java.util.LinkedList;

import org.springframework.asm.AnnotationVisitor;
import org.springframework.asm.ClassReader;
import org.springframework.asm.ClassVisitor;
import org.springframework.asm.FieldVisitor;
import org.springframework.asm.MethodVisitor;
import org.springframework.asm.Opcodes;
import org.springframework.asm.SpringAsmInfo;
import org.springframework.asm.Type;

import com.github.obase.mysql.data.ClassMetaInfo;
import com.github.obase.mysql.data.FieldMetaInfo;
import com.github.obase.mysql.data.IndexAnnotation;
import com.github.obase.mysql.data.MetaAnnotation;
import com.github.obase.mysql.data.MethodMetaInfo;
import com.github.obase.mysql.data.OptimisticLockAnnotation;
import com.github.obase.mysql.data.PrimaryKeyAnnotation;
import com.github.obase.mysql.data.ReferenceAnnotation;
import com.github.obase.mysql.data.TableAnnotation;

class MetaInfoClassVisitor extends ClassVisitor {

	final ClassMetaInfo result;

	final boolean visitTableOrMeta;

	int order;
	String superName;

	public MetaInfoClassVisitor(ClassMetaInfo result) {
		super(SpringAsmInfo.ASM_VERSION);
		this.result = result;
		this.order = result.hierarchies * 10000;
		this.visitTableOrMeta = result.internalName == null;
	}

	@Override
	public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
		if (visitTableOrMeta) {
			result.internalName = name;
		}
		this.superName = superName;
	}

	@Override
	public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
		// @Table, @Meta是非@Inheriabled.逆向解析，存在则跳过
		if (PRIMARY_KEY_ANNOTATION_DESC.equals(desc)) {
			if (result.primaryKeyAnnotation == null) {
				result.primaryKeyAnnotation = new PrimaryKeyAnnotation();
				return new PrimaryKeyAnnotationVisitor(result.primaryKeyAnnotation);
			}
		} else if (FOREIGN_KEY_ANNOTATION_DESC.equals(desc)) {
			if (result.foreignKeyAnnotation == null) {
				result.foreignKeyAnnotation = new LinkedList<ReferenceAnnotation>();
				return new ForeignKeyAnnotationVisitor(result.foreignKeyAnnotation);
			}
		} else if (INDEXES_KEY_ANNOTATION_DESC.equals(desc)) {
			if (result.indexesAnnotation == null) {
				result.indexesAnnotation = new LinkedList<IndexAnnotation>();
				return new IndexesAnnotationVisitor(result.indexesAnnotation);
			}
		} else if (OPTIMISTIC_LOCK_ANNOTATION_DESC.equals(desc)) {
			if (result.optimisticLockAnnotation == null) {
				result.optimisticLockAnnotation = new OptimisticLockAnnotation();
				return new OptimisticLockAnnotationVisitor(result.optimisticLockAnnotation);
			}
		} else if (visitTableOrMeta) {
			if (TABLE_ANNOTATION_DESC.equals(desc)) {
				if (result.tableAnnotation == null) {
					result.tableAnnotation = new TableAnnotation();
					return new TableAnnotationVisitor(result.tableAnnotation);
				}
			} else if (META_ANNOTATION_DESC.equals(desc)) {
				if (result.metaAnnotation == null) {
					result.metaAnnotation = new MetaAnnotation();
				}
			}
		}

		return null;
	}

	@Override
	public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
		if (!result.fields.containsKey(name)) {
			FieldMetaInfo field = new FieldMetaInfo();
			field.name = name;
			field.descriptor = desc;
			field.signature = signature;
			field.order = (order--);
			result.fields.put(name, field);
			return new SimpleFieldVisitor(field);
		}
		return null;
	}

	@Override
	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
		boolean isPublic = (Opcodes.ACC_PUBLIC & access) > 0;
		int args = Type.getArgumentTypes(desc).length;
		int rsort = Type.getReturnType(desc).getSort();
		if (isPublic && args == 0 && rsort != Type.VOID && name.startsWith(GETTER_METHOD_PREFIX)) {
			if (!result.getters.containsKey(name)) {
				MethodMetaInfo getter = new MethodMetaInfo();
				getter.name = name;
				getter.descriptor = desc;
				getter.signature = signature;
				result.getters.put(name, getter);
			}
		} else if (isPublic && args == 1 && rsort == Type.VOID && name.startsWith(SETTER_METHOD_PREFIX)) {
			if (!result.setters.containsKey(name)) {
				MethodMetaInfo setter = new MethodMetaInfo();
				setter.name = name;
				setter.descriptor = desc;
				setter.signature = signature;
				result.setters.put(name, setter);
			}
		}
		return null;
	}

	@Override
	public void visitEnd() {
		if (Object_INTERNAL_NAME.equals(superName)) {
			return;
		}
		try {
			result.hierarchies++;
			ClassReader cr = new ClassReader(getClassNameFromInternalName(superName));
			cr.accept(new MetaInfoClassVisitor(result), CLASS_READER_ACCEPT_FLAGS);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
