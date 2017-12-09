package com.github.obase.mysql.asm;

import java.io.IOException;
import java.util.Map;

import org.springframework.asm.ClassWriter;
import org.springframework.asm.Label;
import org.springframework.asm.MethodVisitor;
import org.springframework.asm.Opcodes;
import org.springframework.asm.Type;

import com.github.obase.mysql.JavaType;
import com.github.obase.mysql.JdbcMeta;
import com.github.obase.mysql.data.ClassMetaInfo;
import com.github.obase.mysql.data.MethodMetaInfo;

class JdbcMetaClassWriter implements Opcodes {

	static final String SuperInternalName = Type.getInternalName(JdbcMeta.class);

	static final int MajorJavaVersion;
	static {
		String javaVersion = System.getProperty("java.version");
		if (javaVersion.contains("1.9.")) {
			MajorJavaVersion = Opcodes.V1_8;
		} else if (javaVersion.contains("1.8.")) {
			MajorJavaVersion = Opcodes.V1_8;
		} else if (javaVersion.contains("1.7.")) {
			MajorJavaVersion = Opcodes.V1_7;
		} else if (javaVersion.contains("1.6.")) {
			MajorJavaVersion = Opcodes.V1_6;
		} else {
			MajorJavaVersion = Opcodes.V1_6;
		}
	}

	public static byte[] dump(String internalName, ClassMetaInfo classMetaInfo) throws IOException {

		StringBuilder sb = new StringBuilder(128);
		String targetDescriptor = sb.append('L').append(classMetaInfo.internalName).append(';').toString();
		sb.setLength(0);
		String descriptor = sb.append('L').append(internalName).append(';').toString();
		sb = null;

		ClassWriter cw = new ClassWriter(0);
		cw.visit(MajorJavaVersion, ACC_PUBLIC + ACC_SUPER, internalName, null, SuperInternalName, null);

		String name;
		MethodMetaInfo methodMetaInfo;
		MethodVisitor mv;
		{
			mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
			mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitLineNumber(10, l0);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitMethodInsn(INVOKESPECIAL, SuperInternalName, "<init>", "()V", false);
			mv.visitInsn(RETURN);
			Label l1 = new Label();
			mv.visitLabel(l1);
			mv.visitLocalVariable("this", descriptor, null, l0, l1, 0);
			mv.visitMaxs(1, 1);
			mv.visitEnd();
		}
		{
			mv = cw.visitMethod(ACC_PUBLIC, "getValue", "(Ljava/lang/Object;Ljava/lang/String;)Ljava/lang/Object;", null, null);
			mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitTypeInsn(CHECKCAST, classMetaInfo.internalName);
			mv.visitVarInsn(ASTORE, 3);

			int ord = 0;
			Label lb = new Label();
			for (Map.Entry<String, MethodMetaInfo> entry : classMetaInfo.getters.entrySet()) {
				name = entry.getKey();
				methodMetaInfo = entry.getValue();

				if (name == "name") {
					continue;
				}

				mv.visitLabel(lb);
				if (ord == 1) {
					mv.visitFrame(Opcodes.F_APPEND, 1, new Object[] { classMetaInfo.internalName }, 0, null);
				} else if (ord > 1) {
					mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
				}

				mv.visitLdcInsn(name);
				mv.visitVarInsn(ALOAD, 2);
				mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "equals", "(Ljava/lang/Object;)Z", false);

				lb = new Label();
				ord++;
				mv.visitJumpInsn(IFEQ, lb);
				mv.visitVarInsn(ALOAD, 3);
				mv.visitMethodInsn(INVOKEVIRTUAL, classMetaInfo.internalName, methodMetaInfo.name, methodMetaInfo.descriptor, false);
				valueOfPrimitiveType(mv, Type.getReturnType(methodMetaInfo.descriptor));
				mv.visitInsn(ARETURN);
			}
			mv.visitLabel(lb);
			if (ord == 1) {
				mv.visitFrame(Opcodes.F_APPEND, 1, new Object[] { classMetaInfo.internalName }, 0, null);
			} else if (ord > 1) {
				mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
			}
			mv.visitInsn(ACONST_NULL);
			mv.visitInsn(ARETURN);

			Label l1 = new Label();
			mv.visitLabel(l1);
			mv.visitLocalVariable("this", descriptor, null, l0, l1, 0);
			mv.visitLocalVariable("obj", "Ljava/lang/Object;", null, l0, l1, 1);
			mv.visitLocalVariable("name", "Ljava/lang/String;", null, l0, l1, 2);
			mv.visitLocalVariable("that", targetDescriptor, null, l0, l1, 3);
			mv.visitMaxs(2, 4);
			mv.visitEnd();
		}
		{
			mv = cw.visitMethod(ACC_PUBLIC, "setParam", "(Ljava/sql/PreparedStatement;ILjava/lang/Object;Ljava/lang/String;)V", null, new String[] { "java/sql/SQLException" });
			mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);

			mv.visitVarInsn(ALOAD, 3);
			mv.visitTypeInsn(CHECKCAST, classMetaInfo.internalName);
			mv.visitVarInsn(ASTORE, 5);

			int ord = 0;
			Label lb = new Label();
			for (Map.Entry<String, MethodMetaInfo> entry : classMetaInfo.getters.entrySet()) {
				name = entry.getKey();
				methodMetaInfo = entry.getValue();

				mv.visitLabel(lb);
				if (ord > 1) {
					mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
				} else if (ord == 1) {
					mv.visitFrame(Opcodes.F_APPEND, 1, new Object[] { classMetaInfo.internalName }, 0, null);
				}

				mv.visitLdcInsn(name);
				mv.visitVarInsn(ALOAD, 4);
				mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "equals", "(Ljava/lang/Object;)Z", false);
				lb = new Label();
				ord++;
				mv.visitJumpInsn(IFEQ, lb);

				mv.visitVarInsn(ALOAD, 1);
				mv.visitVarInsn(ILOAD, 2);
				mv.visitVarInsn(ALOAD, 5);
				mv.visitMethodInsn(INVOKEVIRTUAL, classMetaInfo.internalName, methodMetaInfo.name, methodMetaInfo.descriptor, false);
				setParamByType(mv, Type.getReturnType(methodMetaInfo.descriptor));
				mv.visitInsn(RETURN);
			}

			mv.visitLabel(lb);
			if (ord > 1) {
				mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
			} else if (ord == 1) {
				mv.visitFrame(Opcodes.F_APPEND, 1, new Object[] { classMetaInfo.internalName }, 0, null);
			}

			mv.visitInsn(RETURN);
			Label l2 = new Label();
			mv.visitLabel(l2);
			mv.visitLocalVariable("this", descriptor, null, l0, l2, 0);
			mv.visitLocalVariable("ps", "Ljava/sql/PreparedStatement;", null, l0, l2, 1);
			mv.visitLocalVariable("pos", "I", null, l0, l2, 2);
			mv.visitLocalVariable("obj", "Ljava/lang/Object;", null, l0, l2, 3);
			mv.visitLocalVariable("name", "Ljava/lang/String;", null, l0, l2, 4);
			mv.visitLocalVariable("that", targetDescriptor, null, l0, l2, 5);
			mv.visitMaxs(4, 6);
			mv.visitEnd();
		}
		{
			mv = cw.visitMethod(ACC_PUBLIC, "getResult", "(Ljava/sql/ResultSet;Ljava/util/Map;)Ljava/lang/Object;", "(Ljava/sql/ResultSet;Ljava/util/Map<Ljava/lang/String;Ljava/lang/Integer;>;)Ljava/lang/Object;", new String[] { "java/sql/SQLException" });
			mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitTypeInsn(NEW, classMetaInfo.internalName);
			mv.visitInsn(DUP);
			mv.visitMethodInsn(INVOKESPECIAL, classMetaInfo.internalName, "<init>", "()V", false);
			mv.visitVarInsn(ASTORE, 3);

			int ord = 0;
			Label lb = new Label();
			for (Map.Entry<String, MethodMetaInfo> entry : classMetaInfo.setters.entrySet()) {
				name = entry.getKey();
				methodMetaInfo = entry.getValue();

				mv.visitLabel(lb);
				if (ord > 1) {
					mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
				} else if (ord == 1) {
					mv.visitFrame(Opcodes.F_APPEND, 2, new Object[] { classMetaInfo.internalName, "java/lang/Integer" }, 0, null);
				}
				mv.visitVarInsn(ALOAD, 2);
				mv.visitLdcInsn(name);
				mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "get", "(Ljava/lang/Object;)Ljava/lang/Object;", true);
				mv.visitTypeInsn(CHECKCAST, "java/lang/Integer");
				mv.visitInsn(DUP);
				mv.visitVarInsn(ASTORE, 4);
				lb = new Label();
				ord++;
				mv.visitJumpInsn(IFNULL, lb);
				mv.visitVarInsn(ALOAD, 3);
				mv.visitVarInsn(ALOAD, 1);
				mv.visitVarInsn(ALOAD, 4);
				getResultByType(mv, Type.getArgumentTypes(methodMetaInfo.descriptor)[0]);
				mv.visitMethodInsn(INVOKEVIRTUAL, classMetaInfo.internalName, methodMetaInfo.name, methodMetaInfo.descriptor, false);
			}
			mv.visitLabel(lb);
			if (ord > 1) {
				mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
			} else if (ord == 1) {
				mv.visitFrame(Opcodes.F_APPEND, 2, new Object[] { classMetaInfo.internalName, "java/lang/Integer" }, 0, null);
			}
			mv.visitVarInsn(ALOAD, 3);
			mv.visitInsn(ARETURN);
			Label l2 = new Label();
			mv.visitLabel(l2);
			mv.visitLocalVariable("this", descriptor, null, l0, l2, 0);
			mv.visitLocalVariable("rs", "Ljava/sql/ResultSet;", null, l0, l2, 1);
			mv.visitLocalVariable("types", "Ljava/util/Map;", "Ljava/util/Map<Ljava/lang/String;Ljava/lang/Integer;>;", l0, l2, 2);
			mv.visitLocalVariable("that", targetDescriptor, null, l0, l2, 3);
			mv.visitLocalVariable("pos", "Ljava/lang/Integer;", null, l0, l2, 4);
			mv.visitMaxs(4, 6);
			mv.visitEnd();
		}
		{
			mv = cw.visitMethod(ACC_PUBLIC, "getResult2", "(Ljava/sql/ResultSet;Ljava/util/Map;Ljava/lang/Object;)V", "(Ljava/sql/ResultSet;Ljava/util/Map<Ljava/lang/String;Ljava/lang/Integer;>;Ljava/lang/Object;)V", new String[] { "java/sql/SQLException" });
			mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitVarInsn(ALOAD, 3);
			mv.visitTypeInsn(CHECKCAST, classMetaInfo.internalName);
			mv.visitVarInsn(ASTORE, 4);

			int ord = 0;
			Label lb = new Label();

			for (Map.Entry<String, MethodMetaInfo> entry : classMetaInfo.setters.entrySet()) {
				name = entry.getKey();
				methodMetaInfo = entry.getValue();

				mv.visitLabel(lb);
				if (ord > 1) {
					mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
				} else if (ord == 1) {
					mv.visitFrame(Opcodes.F_APPEND, 2, new Object[] { classMetaInfo.internalName, "java/lang/Integer" }, 0, null);
				}
				mv.visitVarInsn(ALOAD, 2);
				mv.visitLdcInsn("id");
				mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "get", "(Ljava/lang/Object;)Ljava/lang/Object;", true);
				mv.visitTypeInsn(CHECKCAST, "java/lang/Integer");
				mv.visitInsn(DUP);
				mv.visitVarInsn(ASTORE, 5);
				lb = new Label();
				ord++;
				mv.visitJumpInsn(IFNULL, lb);
				mv.visitVarInsn(ALOAD, 4);
				mv.visitVarInsn(ALOAD, 1);
				mv.visitVarInsn(ALOAD, 5);
				getResultByType(mv, Type.getArgumentTypes(methodMetaInfo.descriptor)[0]);
				mv.visitMethodInsn(INVOKEVIRTUAL, classMetaInfo.internalName, methodMetaInfo.name, methodMetaInfo.descriptor, false);
			}
			mv.visitLabel(lb);
			if (ord > 1) {
				mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
			} else if (ord == 1) {
				mv.visitFrame(Opcodes.F_APPEND, 2, new Object[] { classMetaInfo.internalName, "java/lang/Integer" }, 0, null);
			}
			mv.visitInsn(RETURN);
			Label l2 = new Label();
			mv.visitLabel(l2);
			mv.visitLocalVariable("this", descriptor, null, l0, l2, 0);
			mv.visitLocalVariable("rs", "Ljava/sql/ResultSet;", null, l0, l2, 1);
			mv.visitLocalVariable("types", "Ljava/util/Map;", "Ljava/util/Map<Ljava/lang/String;Ljava/lang/Integer;>;", l0, l2, 2);
			mv.visitLocalVariable("obj", "Ljava/lang/Object;", null, l0, l2, 3);
			mv.visitLocalVariable("that", targetDescriptor, null, l0, l2, 4);
			mv.visitLocalVariable("pos", "Ljava/lang/Integer;", null, l0, l2, 5);
			mv.visitMaxs(4, 6);
			mv.visitEnd();
		}
		cw.visitEnd();

		return cw.toByteArray();
	}

	static final void valueOfPrimitiveType(MethodVisitor mv, Type type) {
		switch (JavaType.match(type.getDescriptor())) {
		case _boolean:
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;", false);
			break;
		case _char:
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Character", "valueOf", "(C)Ljava/lang/Character;", false);
			break;
		case _byte:
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Byte", "valueOf", "(B)Ljava/lang/Byte;", false);
			break;
		case _short:
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Short", "valueOf", "(S)Ljava/lang/Short;", false);
			break;
		case _int:
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
			break;
		case _long:
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;", false);
			break;
		case _float:
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;", false);
			break;
		case _double:
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;", false);
			break;
		default:
			break;
		}
	}

	static final void setParamByType(MethodVisitor mv, Type type) {

		switch (JavaType.match(type.getDescriptor())) {
		case _boolean:
			mv.visitMethodInsn(INVOKESTATIC, SuperInternalName, "set_boolean", "(Ljava/sql/PreparedStatement;IZ)V", false);
			break;
		case _Boolean:
			mv.visitMethodInsn(INVOKESTATIC, SuperInternalName, "set_Boolean", "(Ljava/sql/PreparedStatement;ILjava/lang/Boolean;)V", false);
			break;
		case _char:
			mv.visitMethodInsn(INVOKESTATIC, SuperInternalName, "set_char", "(Ljava/sql/PreparedStatement;IC)V", false);
			break;
		case _Character:
			mv.visitMethodInsn(INVOKESTATIC, SuperInternalName, "set_Character", "(Ljava/sql/PreparedStatement;ILjava/lang/Character;)V", false);
			break;
		case _byte:
			mv.visitMethodInsn(INVOKESTATIC, SuperInternalName, "set_byte", "(Ljava/sql/PreparedStatement;IB)V", false);
			break;
		case _Byte:
			mv.visitMethodInsn(INVOKESTATIC, SuperInternalName, "set_Byte", "(Ljava/sql/PreparedStatement;ILjava/lang/Byte;)V", false);
			break;
		case _short:
			mv.visitMethodInsn(INVOKESTATIC, SuperInternalName, "set_short", "(Ljava/sql/PreparedStatement;IS)V", false);
			break;
		case _Short:
			mv.visitMethodInsn(INVOKESTATIC, SuperInternalName, "set_Short", "(Ljava/sql/PreparedStatement;ILjava/lang/Short;)V", false);
			break;
		case _int:
			mv.visitMethodInsn(INVOKESTATIC, SuperInternalName, "set_int", "(Ljava/sql/PreparedStatement;II)V", false);
			break;
		case _Integer:
			mv.visitMethodInsn(INVOKESTATIC, SuperInternalName, "set_Integer", "(Ljava/sql/PreparedStatement;ILjava/lang/Integer;)V", false);
			break;
		case _long:
			mv.visitMethodInsn(INVOKESTATIC, SuperInternalName, "set_long", "(Ljava/sql/PreparedStatement;IJ)V", false);
			break;
		case _Long:
			mv.visitMethodInsn(INVOKESTATIC, SuperInternalName, "set_Long", "(Ljava/sql/PreparedStatement;ILjava/lang/Long;)V", false);
			break;
		case _float:
			mv.visitMethodInsn(INVOKESTATIC, SuperInternalName, "set_float", "(Ljava/sql/PreparedStatement;IF)V", false);
			break;
		case _Float:
			mv.visitMethodInsn(INVOKESTATIC, SuperInternalName, "set_Float", "(Ljava/sql/PreparedStatement;ILjava/lang/Float;)V", false);
			break;
		case _double:
			mv.visitMethodInsn(INVOKESTATIC, SuperInternalName, "set_double", "(Ljava/sql/PreparedStatement;ID)V", false);
			break;
		case _Double:
			mv.visitMethodInsn(INVOKESTATIC, SuperInternalName, "set_Double", "(Ljava/sql/PreparedStatement;ILjava/lang/Double;)V", false);
			break;
		case _String:
			mv.visitMethodInsn(INVOKESTATIC, SuperInternalName, "set_String", "(Ljava/sql/PreparedStatement;ILjava/lang/String;)V", false);
			break;
		case _BigDecimal:
			mv.visitMethodInsn(INVOKESTATIC, SuperInternalName, "set_BigDecimal", "(Ljava/sql/PreparedStatement;ILjava/math/BigDecimal;)V", false);
			break;
		case _BigInteger:
			mv.visitMethodInsn(INVOKESTATIC, SuperInternalName, "set_BigInteger", "(Ljava/sql/PreparedStatement;ILjava/math/BigInteger;)V", false);
			break;
		case _JavaUtilDate:
			mv.visitMethodInsn(INVOKESTATIC, SuperInternalName, "set_JavaUtilDate", "(Ljava/sql/PreparedStatement;ILjava/util/Date;)V", false);
			break;
		case _Date:
			mv.visitMethodInsn(INVOKESTATIC, SuperInternalName, "set_Date", "(Ljava/sql/PreparedStatement;ILjava/sql/Date;)V", false);
			break;
		case _Time:
			mv.visitMethodInsn(INVOKESTATIC, SuperInternalName, "set_Time", "(Ljava/sql/PreparedStatement;ILjava/sql/Time;)V", false);
			break;
		case _Timestamp:
			mv.visitMethodInsn(INVOKESTATIC, SuperInternalName, "set_Timestamp", "(Ljava/sql/PreparedStatement;ILjava/sql/Timestamp;)V", false);
			break;
		case _bytes:
			mv.visitMethodInsn(INVOKESTATIC, SuperInternalName, "set_bytes", "(Ljava/sql/PreparedStatement;I[B)V", false);
			break;
		case _Ref:
			mv.visitMethodInsn(INVOKESTATIC, SuperInternalName, "set_Ref", "(Ljava/sql/PreparedStatement;ILjava/sql/Ref;)V", false);
			break;
		case _URL:
			mv.visitMethodInsn(INVOKESTATIC, SuperInternalName, "set_URL", "(Ljava/sql/PreparedStatement;ILjava/net/URL;)V", false);
			break;
		case _SQLXML:
			mv.visitMethodInsn(INVOKESTATIC, SuperInternalName, "set_SQLXML", "(Ljava/sql/PreparedStatement;ILjava/sql/SQLXML;)V", false);
			break;
		case _Blob:
			mv.visitMethodInsn(INVOKESTATIC, SuperInternalName, "set_Blob", "(Ljava/sql/PreparedStatement;ILjava/sql/Blob;)V", false);
			break;
		case _Clob:
			mv.visitMethodInsn(INVOKESTATIC, SuperInternalName, "set_Clob", "(Ljava/sql/PreparedStatement;ILjava/sql/Clob;)V", false);
			break;
		case _InputStream:
			mv.visitMethodInsn(INVOKESTATIC, SuperInternalName, "set_InputStream", "(Ljava/sql/PreparedStatement;ILjava/io/InputStream;)V", false);
			break;
		case _Reader:
			mv.visitMethodInsn(INVOKESTATIC, SuperInternalName, "set_Reader", "(Ljava/sql/PreparedStatement;ILjava/io/Reader;)V", false);
			break;
		case _Object:
			mv.visitMethodInsn(INVOKESTATIC, SuperInternalName, "set_Object", "(Ljava/sql/PreparedStatement;ILjava/lang/Object;)V", false);
			break;
		}

	}

	static final void getResultByType(MethodVisitor mv, Type type) {

		switch (JavaType.match(type.getDescriptor())) {
		case _boolean:
			mv.visitMethodInsn(INVOKESTATIC, SuperInternalName, "get_boolean", "(Ljava/sql/ResultSet;Ljava/lang/Integer;)Z", false);
			break;
		case _Boolean:
			mv.visitMethodInsn(INVOKESTATIC, SuperInternalName, "get_Boolean", "(Ljava/sql/ResultSet;Ljava/lang/Integer;)Ljava/lang/Boolean;", false);
			break;
		case _char:
			mv.visitMethodInsn(INVOKESTATIC, SuperInternalName, "get_char", "(Ljava/sql/ResultSet;Ljava/lang/Integer;)C", false);
			break;
		case _Character:
			mv.visitMethodInsn(INVOKESTATIC, SuperInternalName, "get_Character", "(Ljava/sql/ResultSet;Ljava/lang/Integer;)Ljava/lang/Character;", false);
			break;
		case _byte:
			mv.visitMethodInsn(INVOKESTATIC, SuperInternalName, "get_byte", "(Ljava/sql/ResultSet;Ljava/lang/Integer;)B", false);
			break;
		case _Byte:
			mv.visitMethodInsn(INVOKESTATIC, SuperInternalName, "get_Byte", "(Ljava/sql/ResultSet;Ljava/lang/Integer;)Ljava/lang/Byte;", false);
			break;
		case _short:
			mv.visitMethodInsn(INVOKESTATIC, SuperInternalName, "get_short", "(Ljava/sql/ResultSet;Ljava/lang/Integer;)S", false);
			break;
		case _Short:
			mv.visitMethodInsn(INVOKESTATIC, SuperInternalName, "get_Short", "(Ljava/sql/ResultSet;Ljava/lang/Integer;)Ljava/lang/Short;", false);
			break;
		case _int:
			mv.visitMethodInsn(INVOKESTATIC, SuperInternalName, "get_int", "(Ljava/sql/ResultSet;Ljava/lang/Integer;)I", false);
			break;
		case _Integer:
			mv.visitMethodInsn(INVOKESTATIC, SuperInternalName, "get_Integer", "(Ljava/sql/ResultSet;Ljava/lang/Integer;)Ljava/lang/Integer;", false);
			break;
		case _long:
			mv.visitMethodInsn(INVOKESTATIC, SuperInternalName, "get_long", "(Ljava/sql/ResultSet;Ljava/lang/Integer;)J", false);
			break;
		case _Long:
			mv.visitMethodInsn(INVOKESTATIC, SuperInternalName, "get_Long", "(Ljava/sql/ResultSet;Ljava/lang/Integer;)Ljava/lang/Long;", false);
			break;
		case _float:
			mv.visitMethodInsn(INVOKESTATIC, SuperInternalName, "get_float", "(Ljava/sql/ResultSet;Ljava/lang/Integer;)F", false);
			break;
		case _Float:
			mv.visitMethodInsn(INVOKESTATIC, SuperInternalName, "get_Float", "(Ljava/sql/ResultSet;Ljava/lang/Integer;)Ljava/lang/Float;", false);
			break;
		case _double:
			mv.visitMethodInsn(INVOKESTATIC, SuperInternalName, "get_double", "(Ljava/sql/ResultSet;Ljava/lang/Integer;)D", false);
			break;
		case _Double:
			mv.visitMethodInsn(INVOKESTATIC, SuperInternalName, "get_Double", "(Ljava/sql/ResultSet;Ljava/lang/Integer;)Ljava/lang/Double;", false);
			break;
		case _String:
			mv.visitMethodInsn(INVOKESTATIC, SuperInternalName, "get_String", "(Ljava/sql/ResultSet;Ljava/lang/Integer;)Ljava/lang/String;", false);
			break;
		case _BigDecimal:
			mv.visitMethodInsn(INVOKESTATIC, SuperInternalName, "get_BigDecimal", "(Ljava/sql/ResultSet;Ljava/lang/Integer;)Ljava/math/BigDecimal;", false);
			break;
		case _BigInteger:
			mv.visitMethodInsn(INVOKESTATIC, SuperInternalName, "get_BigInteger", "(Ljava/sql/ResultSet;Ljava/lang/Integer;)Ljava/math/BigInteger;", false);
			break;
		case _JavaUtilDate:
			mv.visitMethodInsn(INVOKESTATIC, SuperInternalName, "get_JavaUtilDate", "(Ljava/sql/ResultSet;Ljava/lang/Integer;)Ljava/util/Date;", false);
			break;
		case _Date:
			mv.visitMethodInsn(INVOKESTATIC, SuperInternalName, "get_Date", "(Ljava/sql/ResultSet;Ljava/lang/Integer;)Ljava/sql/Date;", false);
			break;
		case _Time:
			mv.visitMethodInsn(INVOKESTATIC, SuperInternalName, "get_Time", "(Ljava/sql/ResultSet;Ljava/lang/Integer;)Ljava/sql/Time;", false);
			break;
		case _Timestamp:
			mv.visitMethodInsn(INVOKESTATIC, SuperInternalName, "get_Timestamp", "(Ljava/sql/ResultSet;Ljava/lang/Integer;)Ljava/sql/Timestamp;", false);
			break;
		case _bytes:
			mv.visitMethodInsn(INVOKESTATIC, SuperInternalName, "get_bytes", "(Ljava/sql/ResultSet;Ljava/lang/Integer;)[B", false);
			break;
		case _Ref:
			mv.visitMethodInsn(INVOKESTATIC, SuperInternalName, "get_Ref", "(Ljava/sql/ResultSet;Ljava/lang/Integer;)Ljava/sql/Ref;", false);
			break;
		case _URL:
			mv.visitMethodInsn(INVOKESTATIC, SuperInternalName, "get_URL", "(Ljava/sql/ResultSet;Ljava/lang/Integer;)Ljava/net/URL;", false);
			break;
		case _SQLXML:
			mv.visitMethodInsn(INVOKESTATIC, SuperInternalName, "get_SQLXML", "(Ljava/sql/ResultSet;Ljava/lang/Integer;)Ljava/sql/SQLXML;", false);
			break;
		case _Blob:
			mv.visitMethodInsn(INVOKESTATIC, SuperInternalName, "get_Blob", "(Ljava/sql/ResultSet;Ljava/lang/Integer;)Ljava/sql/Blob;", false);
			break;
		case _Clob:
			mv.visitMethodInsn(INVOKESTATIC, SuperInternalName, "get_Clob", "(Ljava/sql/ResultSet;Ljava/lang/Integer;)Ljava/sql/Clob;", false);
			break;
		case _InputStream:
			mv.visitMethodInsn(INVOKESTATIC, SuperInternalName, "get_InputStream", "(Ljava/sql/ResultSet;Ljava/lang/Integer;)Ljava/io/InputStream;", false);
			break;
		case _Reader:
			mv.visitMethodInsn(INVOKESTATIC, SuperInternalName, "get_Reader", "(Ljava/sql/ResultSet;Ljava/lang/Integer;)Ljava/io/Reader;", false);
			break;
		case _Object:
			mv.visitLdcInsn(type);
			mv.visitMethodInsn(INVOKESTATIC, SuperInternalName, "get_Object", "(Ljava/sql/ResultSet;Ljava/lang/Integer;Ljava/lang/Class;)Ljava/lang/Object;", false);
			mv.visitTypeInsn(CHECKCAST, type.getInternalName());
			break;
		}

	}
}
