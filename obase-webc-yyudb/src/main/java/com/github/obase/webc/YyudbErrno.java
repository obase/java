package com.github.obase.webc;

public interface YyudbErrno extends WebcErrno {

	String SOURCE = "WEBC_YYUDB";
	int __ = WebcErrno.__ | 0x01000;

	int HIIDO_VALID_FAILED = __ | 0x0001;
}
