package com.obase.loader;

public interface LoaderErrno {

	String SOURCE = "LOADER";
	int __ = 0x40000;

	int LOADER_PASSWD_FILE_NOT_FOUND = __ | 1;
}
