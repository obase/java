package com.github.obase.mysql.sql;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;

import com.github.obase.mysql.data.ClassMetaInfo;
import com.github.obase.mysql.impl.SPstmtMeta;

public class SqlMetaKit extends SqlKit {

	public static SPstmtMeta genSelectAllPstmt(ClassMetaInfo classMetaInfo) {
		StringBuilder select = new StringBuilder(512);

		StringBuilder colsStr = new StringBuilder(128);
		for (String field : classMetaInfo.columns) {
			if (colsStr.length() > 0) {
				colsStr.append(',');
			}
			colsStr.append(identifier(field));
		}
		select.append("SELECT ").append(colsStr).append(" FROM ").append(identifier(classMetaInfo.tableName));

		return SPstmtMeta.getInstance(select.toString(), null);
	}

	public static SPstmtMeta genSelectPstmt(ClassMetaInfo classMetaInfo) {
		StringBuilder select = new StringBuilder(512);
		List<String> params = new LinkedList<String>();

		StringBuilder colsStr = new StringBuilder(128);
		for (String field : classMetaInfo.columns) {
			if (colsStr.length() > 0) {
				colsStr.append(',');
			}
			colsStr.append(identifier(field));
		}
		select.append("SELECT ").append(colsStr).append(" FROM ").append(identifier(classMetaInfo.tableName));

		StringBuilder whereStr = new StringBuilder(128);
		for (String field : classMetaInfo.keys) {
			if (whereStr.length() > 0) {
				whereStr.append(" AND ");
			}
			whereStr.append(identifier(field)).append("=?");
			params.add(field);
		}
		select.append(" WHERE ").append(whereStr);

		return SPstmtMeta.getInstance(select.toString(), params);
	}

	public static SPstmtMeta genInsertPstmt(ClassMetaInfo classMetaInfo) {
		StringBuilder insert = new StringBuilder(512);
		List<String> params = new LinkedList<String>();

		// Optimistic Lock
		String optLckCol = classMetaInfo.optimisticLockAnnotation == null ? null : classMetaInfo.optimisticLockAnnotation.column;

		StringBuilder colsStr = new StringBuilder(128);
		StringBuilder valsStr = new StringBuilder(128);
		for (String field : classMetaInfo.columns) {
			if (colsStr.length() > 0) {
				colsStr.append(',');
				valsStr.append(',');
			}
			colsStr.append(identifier(field));
			if (!field.equals(optLckCol)) {
				valsStr.append('?');
			} else {
				valsStr.append("(IFNULL(").append(identifier(field)).append(",IFNULL(?,0))+1)");
			}
			params.add(field);
		}
		insert.append("INSERT INTO ").append(identifier(classMetaInfo.tableName)).append('(').append(colsStr).append(") VALUES(").append(valsStr).append(')');

		return SPstmtMeta.getInstance(insert.toString(), params);
	}

	public static SPstmtMeta genMergePstmt(ClassMetaInfo classMetaInfo) {

		StringBuilder insertOrUpdate = new StringBuilder(512);
		List<String> params = new LinkedList<String>();

		// Optimistic Lock
		String optLckCol = classMetaInfo.optimisticLockAnnotation == null ? null : classMetaInfo.optimisticLockAnnotation.column;

		StringBuilder colsStr = new StringBuilder(128);
		StringBuilder valsStr = new StringBuilder(128);
		for (String field : classMetaInfo.columns) {
			if (colsStr.length() > 0) {
				colsStr.append(',');
				valsStr.append(',');
			}
			colsStr.append(identifier(field));
			if (!field.equals(optLckCol)) {
				valsStr.append('?');
			} else {
				valsStr.append("(IFNULL(").append(identifier(field)).append(",IFNULL(?,0))+1)");
			}
			params.add(field);
		}
		insertOrUpdate.append("INSERT INTO ").append(identifier(classMetaInfo.tableName)).append('(').append(colsStr).append(") VALUES(").append(valsStr).append(')');

		StringBuilder updateStr = new StringBuilder(128);
		LinkedHashSet<String> cols = new LinkedHashSet<String>();
		cols.addAll(classMetaInfo.columns);
		cols.removeAll(classMetaInfo.keys);
		if (cols.size() > 0) {// FIXBUG: all is primary key
			for (String field : cols) {
				if (updateStr.length() > 0) {
					updateStr.append(',');
				}
				if (!field.equals(optLckCol)) {
					updateStr.append(identifier(field)).append("=IFNULL(?,").append(identifier(field)).append(")");
				} else {
					updateStr.append(identifier(field)).append("=(IFNULL(").append(identifier(field)).append(",IFNULL(?,0))+1)");
				}
				params.add(field);
			}

		} else {
			for (String field : classMetaInfo.keys) {
				if (updateStr.length() > 0) {
					updateStr.append(',');
				}
				updateStr.append(identifier(field)).append("=").append(identifier(field));
			}
		}
		insertOrUpdate.append(" ON DUPLICATE KEY UPDATE ").append(updateStr);
		return SPstmtMeta.getInstance(insertOrUpdate.toString(), params);
	}

	public static SPstmtMeta genUpdatePstmt(ClassMetaInfo classMetaInfo) {
		StringBuilder update = new StringBuilder(512);
		List<String> params = new LinkedList<String>();

		// Optimistic Lock
		String optLckCol = classMetaInfo.optimisticLockAnnotation == null ? null : classMetaInfo.optimisticLockAnnotation.column;

		StringBuilder colsStr = new StringBuilder(128);
		LinkedHashSet<String> cols = new LinkedHashSet<String>();
		cols.addAll(classMetaInfo.columns);
		cols.removeAll(classMetaInfo.keys);
		for (String field : cols) {
			if (colsStr.length() > 0) {
				colsStr.append(',');
			}
			if (!field.equals(optLckCol)) {
				colsStr.append(identifier(field)).append("=?");
			} else {
				colsStr.append(identifier(field)).append("=(IFNULL(").append(identifier(field)).append(",IFNULL(?,0))+1)");
			}
			params.add(field);
		}
		update.append("UPDATE ").append(identifier(classMetaInfo.tableName)).append(" SET ").append(colsStr);

		StringBuilder whereStr = new StringBuilder(128);
		for (String field : classMetaInfo.keys) {
			if (whereStr.length() > 0) {
				whereStr.append(" AND ");
			}
			whereStr.append(identifier(field)).append("=?");
			params.add(field);
		}

		if (optLckCol != null) {
			if (whereStr.length() > 0) {
				whereStr.append(" AND ");
			}
			whereStr.append(identifier(optLckCol)).append("=?");
			params.add(optLckCol);
		}

		update.append(" WHERE ").append(whereStr);
		return SPstmtMeta.getInstance(update.toString(), params);
	}

	public static SPstmtMeta genDeletePstmt(ClassMetaInfo classMetaInfo) {
		if (classMetaInfo.tableAnnotation == null) {
			return null;
		}

		StringBuilder delete = new StringBuilder(512);
		List<String> params = new LinkedList<String>();

		StringBuilder whereStr = new StringBuilder(128);
		for (String field : classMetaInfo.keys) {
			if (whereStr.length() > 0) {
				whereStr.append(" AND ");
			}
			whereStr.append(identifier(field)).append("=?");
			params.add(field);
		}
		delete.append("DELETE FROM ").append(identifier(classMetaInfo.tableName)).append(" WHERE ").append(whereStr);
		return SPstmtMeta.getInstance(delete.toString(), params);
	}

	public static SPstmtMeta genInsertIgnorePstmt(ClassMetaInfo classMetaInfo) {
		StringBuilder insert = new StringBuilder(512);
		List<String> params = new LinkedList<String>();

		// Optimistic Lock
		String optLckCol = classMetaInfo.optimisticLockAnnotation == null ? null : classMetaInfo.optimisticLockAnnotation.column;

		StringBuilder colsStr = new StringBuilder(128);
		StringBuilder valsStr = new StringBuilder(128);
		for (String field : classMetaInfo.columns) {
			if (colsStr.length() > 0) {
				colsStr.append(',');
				valsStr.append(',');
			}
			colsStr.append(identifier(field));
			if (!field.equals(optLckCol)) {
				valsStr.append('?');
			} else {
				valsStr.append("(IFNULL(").append(identifier(field)).append(",IFNULL(?,0))+1)");
			}
			params.add(field);
		}
		insert.append("INSERT IGNORE INTO ").append(identifier(classMetaInfo.tableName)).append('(').append(colsStr).append(") VALUES(").append(valsStr).append(')');

		return SPstmtMeta.getInstance(insert.toString(), params);
	}

	public static SPstmtMeta genReplacePstmt(ClassMetaInfo classMetaInfo) {
		StringBuilder insert = new StringBuilder(512);
		List<String> params = new LinkedList<String>();

		// Optimistic Lock
		String optLckCol = classMetaInfo.optimisticLockAnnotation == null ? null : classMetaInfo.optimisticLockAnnotation.column;

		StringBuilder colsStr = new StringBuilder(128);
		StringBuilder valsStr = new StringBuilder(128);
		for (String field : classMetaInfo.columns) {
			if (colsStr.length() > 0) {
				colsStr.append(',');
				valsStr.append(',');
			}
			colsStr.append(identifier(field));
			if (!field.equals(optLckCol)) {
				valsStr.append('?');
			} else {
				valsStr.append("(IFNULL(").append(identifier(field)).append(",IFNULL(?,0))+1)");
			}
			params.add(field);
		}
		insert.append("REPLACE INTO ").append(identifier(classMetaInfo.tableName)).append('(').append(colsStr).append(") VALUES(").append(valsStr).append(')');

		return SPstmtMeta.getInstance(insert.toString(), params);
	}

}
