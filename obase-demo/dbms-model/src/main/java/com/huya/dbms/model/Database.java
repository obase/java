package com.huya.dbms.model;

import com.github.obase.mysql.annotation.Meta;
import com.huya.dbms.entity.Proinstan;

@Meta
public class Database extends Proinstan {

	private String projectname;

	private String instancename;

	public String getProjectname() {
		return projectname;
	}

	public void setProjectname(String projectname) {
		this.projectname = projectname;
	}

	public String getInstancename() {
		return instancename;
	}

	public void setInstancename(String instancename) {
		this.instancename = instancename;
	}

}
