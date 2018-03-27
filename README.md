Obase系列是一套基于"spring+mysql+redis"的Java后端开发技术, 可以说是笔者多年开发经验沉湎.

* obase-webc: 基于COC的MVC, 无web.xml启动, 很好支持前后端分离. 用户基于原生HttpServlet API开发. 
* obase-mysql: 一套使用Annotation+ASM实现hibernate + mybatis优势功能的ORM框架. 自动封装, 一条SQL多种用途: 单值查询, 区间查询, 分页查询(支持字段排序), 其实ORM也可以很轻量.
* obase-jedis: 谈不上框架, 就是对JedisPool资源获取释放的封装.
* obase-config: 实现PropertySourcePlaceholderConfiguer的功能, 并支持Redis, Mysql动态配置定期更新功能. 另外, 还支持配置项的AES128加密, 避免你的配置项明文存放.
* obase-test: 嵌入式Tomcat8 + Junit4, 支持环境变量的动态注入. 可以容易测试https和spring bean.
* obase-loader: 加密字节码发布时用的classloader. 对于商业代码比较实用!

```
新版本,新气象,obase经过0.x版本过渡后已经发行正式版本1.2.0. 
- obase-web与spring-boot的设计理念无缝对接, 
- obase-mysql支持动态sql, 完美集成hibernate与mybatis的优势! 
新源码在公司内部多个项目运行稳定, 相关文档正在整理补充!
```

开源obase扎根"spring+mysql+redis", 框架思路可以扩展到其他... 如memcache, postsql. 在此就不做讨论了. 

# obase-webc
* obase-webc最新版本
```xml
<dependency>
	<groupId>com.github.obase</groupId>
	<artifactId>obase-webc</artifactId>
	<version>1.2.0</version>
</dependency>
```
## obase-webc是什么?
obase-webc是基于servlet 3.0+的AsyncContext实现的无web.xml开发模式.在Filter层面实现了Spring MVC的功能, 并移除了HandlerMapping与ViewResolver, 以COC简化Spring MVC的烦人配置. 优点有什么? 试下呗.

## obaes-webc怎么用?

obase-webc的使用方法:

源码maven目录结构参考: https://github.com/obase/java/tree/master/obase-demo, 用户需要继承obase-parent
```
<parent>
	<groupId>com.github.obase</groupId>
	<artifactId>obase-parent</artifactId>
	<version>1.1.0</version>
</parent>
```
里面定义了spring, servlet, jsp的核心版本.

+ 第1步: 创建/META-INF/webc.xml
```
<?xml version="1.0" encoding="UTF-8"?>
<webc xmlns="http://obase.github.io/schema/webc" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://obase.github.io/schema/webc
	https://obase.github.io/schema/obase-webc-1.0.xsd">

</webc>
```

/WEB-INF/webc.xml或/META-INF/webc.xml是obase-webc启用"阀门".  

+ 第2步: 创建/META-INF/servletContext.xml
```
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:p="http://www.springframework.org/schema/p"
	xmlns:context="http://www.springframework.org/schema/context"
	xsi:schemaLocation="http://www.springframework.org/schema/beans 
	http://www.springframework.org/schema/beans/spring-beans.xsd
	http://www.springframework.org/schema/context
	http://www.springframework.org/schema/context/spring-context.xsd">

	<context:component-scan base-package="com.github.obase.demo.controller" />

</beans>
```

+ 第3步: 创建Controller
```
package com.github.obase.demo.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;

import com.github.obase.webc.Kits;
import com.github.obase.webc.annotation.ServletMethod;

@Controller
public class TestController {

	@ServletMethod
	public void hello(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String name = Kits.readParam(request, "name");
		Kits.writeSuccessMessage(response, "hello " + name);
	}

}

```

+ 第4步: 创建HttpServer
```
package com.github.obase.test;

public class HttpServer {

	public static void main(String[] args) {
		EmbedTomcat.start();
	}

}

```

启动HttpServer, 浏览器输入"http://localhost/test/hello?name=jason.he"
```
{"errno":0,"data":"hello jason.he"}
```

## obase-webc与spring-boot整合

```
DEMO: https://github.com/obase/java/tree/master/obase-spring-boot
```

- maven依赖
```
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>
	<parent>
		<groupId>com.github.obase</groupId>
		<artifactId>obase-parent</artifactId>
		<version>1.2.0</version>
	</parent>
	<artifactId>obase-spring-boot</artifactId>
	<packaging>war</packaging>

	<properties>
		<spring.boot.version>1.5.9.RELEASE</spring.boot.version>
		<spring.version>4.3.10.RELEASE</spring.version>
		<jackson.version>2.9.0</jackson.version>
		<java.version>1.8</java.version>
		<obase.version>1.2.0-SNAPSHOT</obase.version>
	</properties>

	<dependencyManagement>
		<dependencies>
			<dependency>
				<groupId>org.springframework.boot</groupId>
				<artifactId>spring-boot-dependencies</artifactId>
				<version>${spring.boot.version}</version>
				<type>pom</type>
				<scope>import</scope>
			</dependency>
		</dependencies>
	</dependencyManagement>

	<dependencies>
		<dependency>
			<groupId>javax.servlet</groupId>
			<artifactId>javax.servlet-api</artifactId>
			<scope>provided</scope>
		</dependency>
		<dependency>
			<groupId>javax.servlet.jsp</groupId>
			<artifactId>javax.servlet.jsp-api</artifactId>
			<scope>provided</scope>
		</dependency>
		<dependency>
			<groupId>com.github.obase</groupId>
			<artifactId>obase-webc</artifactId>
			<version>${obase.version}</version>
		</dependency>

		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-tomcat</artifactId>
			<scope>provided</scope>
		</dependency>
		<dependency>
			<groupId>org.apache.tomcat.embed</groupId>
			<artifactId>tomcat-embed-jasper</artifactId>
			<scope>provided</scope>
		</dependency>

		<dependency>
			<groupId>com.github.obase</groupId>
			<artifactId>obase-test</artifactId>
			<scope>test</scope>
			<version>${obase.version}</version>
		</dependency>

	</dependencies>

	<build>
		<plugins>
			<plugin>
				<groupId>org.springframework.boot</groupId>
				<artifactId>spring-boot-maven-plugin</artifactId>
				<version>${spring.boot.version}</version>
				<executions>
					<execution>
						<goals>
							<goal>repackage</goal>
						</goals>
					</execution>
				</executions>
			</plugin>
			<plugin>
				<groupId>org.apache.maven.plugins</groupId>
				<artifactId>maven-surefire-plugin</artifactId>
			</plugin>
		</plugins>
	</build>

</project>
```
其中properties的值确保obase-webc与spring-boot使用相同版本依赖.

- java代码

```

//注意org.springframework.boot.web.servlet.ServletContextInitializer
//不是javax.servlet.ServletContainerInitializer

package demo.test.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletContextInitializer;

import com.github.obase.webc.WebcServletContainerInitializer;

@SpringBootApplication
public class Main extends WebcServletContainerInitializer implements ServletContextInitializer {

	public static void main(String[] args) {
		SpringApplication.run(Main.class, args);
	}

}


```
执行mvn clean package命令即可得到执行的war.

obase-webc的初衷: 抛弃spring-webmvc, 在spring-web + Servlet 3.0+ 基础结合COC打造一款全新的MVC. 

+ 前后端分离, 实现无web.xml启动, 把src/main/webapp目录完全交给前端团队.
+ 统一URL映射, 基于COC的映射规则.
+ 可简可烦, 简单时创建一个webc.xml即可使用, 不用再配置web.xml, 也不用再配置各种框架servlet;  复杂时可以细粒度控制API访问策略, 接管Spring Security的功能. 
+ 与servlet共存, 这样就能与spring-mvc无缝整合(历史遗留原因). --- 基于Filter技术实现.
+ 支持动态session cookie校验. --- 启用后cookie自带时间戳与hash指纹.
+ 支持类似restful的API, 支持UI与API共用相同的Controller. --- @Controller + @ServletMethod
+ 支持微服务框架. --- @Service + @InvokerService
+ 支持多机部署+分布式会话.
+ 没有反射损耗. reflect太out了, 基于ASM + ServletMethodFilter 自动组装拦截代码到@ServletMethod方法.
+ 保持最小侵入. 该点最关键, 也是最重要. 使用者只需知道@InvokerService, @ServletMethod, SerlvetMethodFilter, ServletMethodProcssor几个注解或API, 其他还是HttpServlet API, 或者Spring API.

后面会具体一一介绍.

# obase-jedis
* obase-jedis 最新版本
```xml
<dependency>
	<groupId>com.github.obase</groupId>
	<artifactId>obase-jedis</artifactId>
	<version>1.2.0</version>
</dependen
```

# obase-test

* obase-test 最新版本
```xml
<dependency>
	<groupId>com.github.obase</groupId>
	<artifactId>obase-test</artifactId>
	<version>1.2.0</version>
</dependen
```
## obase-test是什么?
封装了embedded tomcat 8与junit4, 简单即可实现https的测试, 以及Spring Context上下文单元测试. 为什么不用Jetty9? 用过你就知道jetty9 对于Servlet 3.0+的支持有多烦!

# obase-config

* obase-config 最新版本
```xml
<dependency>
	<groupId>com.github.obase</groupId>
	<artifactId>obase-config</artifactId>
	<version>1.2.0</version>
</dependen
```
## obase-config是什么?
基于Spring, 实现PropertySourceConfigurer的功能, 更多地... 支持redis与mysql的动态配置获取与定时更新, 还有... 加密配置项, 这个对于敏感数据来说是一种简洁的处理方式.

# obase-loader

* obase-loader 最新版本
```xml
<dependency>
	<groupId>com.github.obase</groupId>
	<artifactId>obase-loader</artifactId>
	<version>1.2.0</version>
</dependen
```
## obase-loader是什么?
你的jar需要加密发布么? obase-loader基于spring context的classloader机制, 实现加载时解密字节码功能.

# obase-mysql

* obase-mysql最新版本
```xml
<dependency>
	<groupId>com.github.obase</groupId>
	<artifactId>obase-mysql</artifactId>
	<version>1.2.0</version>
</dependency>
```

## obase-mysql是什么?
顾名思议, obase-mysql是针对mysql的一个jdbc封装工具.在实际项目使用了几年的hibernate, mybatis, spring-jdbc后发现每种框架各有特点,同时存在一些不尽人意的地方:

关于Hibernate: 
* Hibernate封装Entity(配置hibernate.hbm2ddl.auto还能自动管理表结构),单表操作不需写任何SQL,这点很爽. 但多表操作呢? Hibernate的HQL太致命,不支持子查询,外连接(left join, right join)查询必须配置@OneToMany, @ManyToMany 等关联注解. 很多同学会说Hibernate支持Native SQL呀. 嗯,基于本地SQL接口可以写任何SQL,但是要求开发自己去封装Object[]结果. 如果这样的情景多了, 为什么不倒回去用Spring-jdbc的RowMapper呢? 或许你又会抱怨, 哪每张表又要去写一些雷同的CRUD SQL了!!!
* Hibernate的HQL经过antlr语法解析成ADT(抽象语法树),最后结合Dialect配置转成具体数据库的SQL语法, 例如很多喜欢的分页查询, 在mysql转成limit clause, 在oracle则配合rownum进行过滤. 这中间经过2层转换, 性能么? 一直是很多开发抱怨的焦点.
* Hibernate提供了一级缓存,甚至你可以配置二级存减少数据库查询频率,提高数据查询性能.某种层面,这种cache管理的高超技巧实在令人赏心悦目,叹为观止. 但是...但是...对于大多数据的互联网应用为了HA(高可用)和LB(负载均衡), 都会采用多机部署模式. 在这种情况下, 各色单机的缓存绝对能让你发疯再发懵, 著名的CAP理论得到最切实的验证. 最后,为了绕过缓存,每次操作完后必须调用flush(), clear()...何必呢? 我用mybatis, spring-jdbc不是更省事么? 但是...单表操作的需求又"蛋疼"了.
* Hibernate总体而言,有3个使用层次, 学习难度逐层翻倍: 
    1. 表映射, 包括单字段映射,多字段映射. 大多数开发都停留在这个阶段.
    2. 表关联, 包括一对一关联,一对多关联,多对多关联. 关联对查询性能的影响很大, 优化策略就是建议外键部分尽可能用lazy,少用eager,但要十分小心"蛋疼"的LazyInitializationException. 另外, Hibernate官方特别强调关联深度问题,建议不要超过3层. 还有, 推荐用小表作为主表关联大表...等等. 哪些没对Hibernate进行系统研究的开发, 我只能建议如无必要,别在你的项目中使用表关联.
    3. 表继承, Hibernate的继承策略有3种实现策略: 单表继承策略(table per class), 外键Joined策略(table per subclass), 和标识字段策略(table per class). 选择不同策略必须谨慎, 影响最致命的不是程序, 而是表结构与数据存储. 毕竟代码的东西大不了重写, 但数据乱了, 你还能"蛋定"地轻描淡写说一声: "大不了重新生成数据"么? 果真英雄! 

以上是我从2009年开始使用Hibernate以来的一些真实感受! 简单总结: 只用hibernate的表映射, 少量使用表关联,而且层级<2. 打死不用表继承...另外,高并发性能的查询接口使用Native SQL. 必要时使用procedure.

关于Mybatis(ibatis):
* mybatis的核心就是SqlMap, 够简单, 无需赘述, 如果mybatis都看不懂呢? 
* mybatis的动态SQL标签, 看起来是功能够强的! 对于动态拼接,只能说是"山与水的关系", 仁者乐山, 智者乐水. 动态特性满足了哪些喜欢拼接SQL的开发心理, 特别是使用$参数的静态替换. 只要存在动态拼接参数, 就有SQL注入的风险! 
* mybatis支持SQL参数与结果的自动映射,这点比起hibenrate灵活很多. 但这种实现是基于反射的,直接的结果就是导致mybatis比hibernate native sql没有太多的性能优势.

以上是我这几年使用mybatis以来的一些体会, 从某种层面来说, mybatis能够符合大多数开发需求! 难怪淘宝系的基础技术中就有ibatis. 但是mybatis没有批量操作的接口, 以及不能像hibernate哪样自动管理数据表结构, 甚至分页也需要借助第三方插件...不免有些失落.

很多项目同时应用了hibernate与mybatis,取长补短,本来思路很不错! 但是都忽略了一个活生生的事实: hibernate与mybatis与spring事务的集成接口不同! 换言之, 项目里面的代码, 要么用hibernate, 要么用mybatis. 混用二者, 事务会成很大的问题. 

洋洋洒洒扯了这么多, 大家应该明白spring-mysqlclient的设计初衷了吧. 说白了就是综合了hibernate与mybatis的好用特性, 同时使用ASM字节码技术替代动态反射, 提升查询过程的性能.

## obase-mysql有哪些功能

* mysqlclient开启updateTable特性, 允许自动根据@Table类更新数据表结构. 但仅限于"增加"操作. 具体规则:
    1. 如果表不存在, 则自动创建表, 以及定义主键, 外键, 索引.
    2. 如果表存在, 则检查表结构:
        1. 比较字段, 如果存在同名字段, 则不再修改.
        1. 比较主键, 如果存在主键则不再修改. 但会检查主键字段是否相同, 并显示相关警告信息.
        1. 比较外键, 如果存在同名外名, 则不再修改.
        1. 比较索引, 如果存在同名索引, 则不再修改.
        
        详细过程, 可以查看 **com.github.risedragon.mysql.jdbc.SqlDdlKit.processUpdateTable()** 方法.
        
* mysqlclient提供单表记录的insert, update, replace, merge, delete, batchInsert, batchUpdate, batchReplace, batchMerge, batchDelete, select, select2, selectFirst, selectRange, selectPage操作. 这些操作只需配置@Table, @Column注解即可, 不用写任何SQL. 详细用法, 可以参见<快速上手>.

* mysqlclient提供命名SQL支持, 并对参数与结果的提取自动封装. 除了预定义的SqlType与JavaType类预定的scalar类型, 用户可以基于ActionMeta接口定制实现, 通过 **JdbcAction.markSqlType()** 注册到框架, 最常见就是支持复合参数IN(:list). 详细用法, 可以参见<快速上手>.

* mysqlclient提供SQL的常见查询接口: query, queryFist, queryRange, queryPage. 其中分页接口Page, 还提供字段排序功能. 详细用法, 可以参见<快速上手>.

* mysqlclient提供SQL的常见操作接口: execute, batchExecute. 

* 另外, mysqlclient提供独立事务管理接口MysqlClientExt满足不需要Spring的PlatformTransactionManager管理事务的应用, 完全编程细粒度控制事务的提交与回滚. 详细用法, 可以参见<快速上手>

## obase-mysql快速上手

* 定义实体
   
使用@Table注解
```java
    @OptimisticLock(column = "version")
public abstract class Base {

	@Column(key = true, autoIncrement = true, comment = "自增主键")
	Long id;

	@Column(length = 16)
	String createBy;

	@Column
	Date createTime;

	@Column(length = 16)
	String modifyBy;

	@Column
	Date modifyTime;

	@Column
	Long version;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCreateBy() {
		return createBy;
	}

	public void setCreateBy(String createBy) {
		this.createBy = createBy;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getModifyBy() {
		return modifyBy;
	}

	public void setModifyBy(String modifyBy) {
		this.modifyBy = modifyBy;
	}

	public Date getModifyTime() {
		return modifyTime;
	}

	public void setModifyTime(Date modifyTime) {
		this.modifyTime = modifyTime;
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

}

@Table(engine=Engine.InnoDB, characterSet="UTF8")
public class Employee extends Base {

	@Column(length = 64, comment = "工号")
	String cardNo;
	@Column(length = 16, comment = "类型")
	String type;
	@Column(length = 16, comment = "姓名")
	String name;
	@Column(length = 8, comment = "性别")
	String gender;
	@Column(length = 16, comment = "部门")
	String groupName;
	@Column(length = 16, comment = "手机号码")
	String phone;
	@Column(length = 18, comment = "身份证号码", unique = true)
	String sid;
	@Column(length = 18, comment = "护照号码", unique = true)
	String passportNo;
	@Column(length = 18, comment = "护照汉字拼音")
	String passportAbbr;
	@Column(length = 8, comment = "虚拟房间号")
	String room;
	@Column(comment = "带薪旅游假期")
	Date paidHoliday;
	@Column(length = 8, defaultValue = "年假", comment = "抵扣假期")
	String holidayType;
	@Column(length = 18, comment = "办公地点")
	String officeLocation;

	@Column
	BigDecimal other;

	public String getCardNo() {
		return cardNo;
	}

	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}

	public String getPassportNo() {
		return passportNo;
	}

	public void setPassportNo(String passportNo) {
		this.passportNo = passportNo;
	}

	public String getPassportAbbr() {
		return passportAbbr;
	}

	public void setPassportAbbr(String passportAbbr) {
		this.passportAbbr = passportAbbr;
	}

	public String getRoom() {
		return room;
	}

	public void setRoom(String room) {
		this.room = room;
	}

	public Date getPaidHoliday() {
		return paidHoliday;
	}

	public void setPaidHoliday(Date paidHoliday) {
		this.paidHoliday = paidHoliday;
	}

	public String getHolidayType() {
		return holidayType;
	}

	public void setHolidayType(String holidayType) {
		this.holidayType = holidayType;
	}

	public String getOfficeLocation() {
		return officeLocation;
	}

	public void setOfficeLocation(String officeLocation) {
		this.officeLocation = officeLocation;
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	public BigDecimal getOther() {
		return other;
	}

	public void setOther(BigDecimal other) {
		this.other = other;
	}

	public String toString() {
		return JsonUtils.writeValueAsString(this);
	}
}

```

使用\<table\>标签
```xml
<?xml version="1.0" encoding="UTF-8"?>
<mysql>
    <table>com.yy.risedev.myweb.entity.Employee</table>
    ...
</mysql>
```
* 定义持久与临时的JdbcAction
    
    JdbcAction接口实现SQL参数设置与结果提取的封装. mysqlclient基于ASM自动生成相关的代理类型. 
    
    **注意: @Table注解或<table>标签定义的实体已经是个Meta, 无需再重复定义!**

使用@Meta注解
```java
@Meta
public class EmpPart {
	Long id;
	Long version;
	String cardNo;
	String groupName;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	public String getCardNo() {
		return cardNo;
	}

	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String toString() {
		return JsonUtils.writeValueAsString(this);
	}
}
```

使用\<meta\>标签
```xml
<?xml version="1.0" encoding="UTF-8"?>
<mysql>
    <table>com.yy.risedev.myweb.model.EmpPart</table>
    ...
</mysql>
```

* 定义sql
使用\<sql\>标签

 **注意:namespace是可选的,一旦定义,使用SQL时必须带上,例如下述xml中为test.insertPartEmployee**
[完整schema定义](https://github.com/risedragon/schema/blob/master/risedev-mysql-1.0.xsd)

```xml
<?xml version="1.0" encoding="UTF-8"?>
<mysql namespace="test">
    <sql id="insertPartEmployee">
	<![CDATA[INSERT INTO Employee (id,version,cardNo,groupName) VALUES(:id,:version,:cardNo,:groupName) ON DUPLICATE KEY UPDATE version=version+1, cardNo=:cardNo, groupName=:groupName]]>
    </sql>
    ...
</mysql>
```

* 在spring中配置mysqlclient

    容器管理事务的实现 **MysqlClientPlatformTransactionImpl**
    ```xml
    	<bean id="mysqlClient" class="com.github.risedragon.mysql.impl.MysqlClientPlatformTransactionImpl" init-method="init">
		<property name="dataSource" ref="dataSource" />
		<property name="packagesToScan" value="com.yy.risedev.myweb.entity,com.yy.risedev.myweb.model" />
		<property name="configLocations" value="classpath:config/*.xml" />
		<property name="showSql" value="true" />
		<property name="updateTable" value="true" />
	</bean>

    ```
    编程管理事务的实现 **MysqlClientConnectTransactionImpl**
    ```xml
    	<bean id="mysqlClient" class="com.github.risedragon.mysql.impl.MysqlClientConnectTransactionImpl" init-method="init">
		<property name="dataSource" ref="dataSource" />
		<property name="packagesToScan" value="com.yy.risedev.myweb.entity,com.yy.risedev.myweb.model" />
		<property name="configLocations" value="classpath:config/*.xml" />
		<property name="showSql" value="true" />
		<property name="updateTable" value="true" />
	</bean>

    ```   
    各个属性说明：
    

    属性 | 功能 | 默认值
    ---|---|---
    dataSource | 数据源引用，任何java.sql.DataSource实例 | 无
    packagesToScan | 扫描@Table或@Meta类的起始包，多值用逗号分隔，例如"a.b.c,a.b.d" | 无
    configLocations | 加载sql xml的Spring Resource Pattern, 多值用逗号分隔，例如“classpath:a/b/c/\*.xml,classpath:a/b/d/\*.xml” | 无
    showSql | 显示操作的SQL. 建议测试环境打开，生产环境关闭 | false, 默认关闭
    updateTable | 是否更新表结构. 如果为true, 则根据@Table与@Column的定义更新表结构. 详细规则参见<mysqlclient开启updateTable特性>. | false, 默认关闭此特性!

    
* 在spring中支持容器事务

    基于注解@Transactional
    ```xml
    <bean id="transactionManager" class="com.github.risedragon.spring.transaction.DataSourceTransactionManager">
		<property name="dataSource" ref="dataSource" />
	</bean>
	<tx:annotation-driven transaction-manager="transactionManager" />
    ```
    基于TransactionTemplate
    ```xml
    <bean id="transactionManager" class="com.github.risedragon.spring.transaction.DataSourceTransactionManager">
		<property name="dataSource" ref="dataSource" />
	</bean>
	<bean id="transactionTemplate" class="org.springframework.transaction.support.TransactionTemplate">
		<constructor-arg ref="transactionManager"/>
	</bean>
    ```

* 实体操作: insert, insertIgnore, replace, update, merge, delete, select, select2, batchInsert, batchInsertIgnore, batchReplace, batchUpdate, batchMerge, batchDelete, selectFirst, selectRange, selectPage等
```java
@Service
@Transactional
public class GenericService {

	@Autowired
	MysqlClient mysqlClient;

	public void insret() throws SQLException {
		Employee emp = new Employee();
		emp.setGroupName("测试部门");
		emp.setCardNo("135-137");
		Long id = mysqlClient.insert(emp, Long.class);

		System.out.println(id);
		throw new SQLException();
	}

	public void update() throws SQLException {
		Employee emp = mysqlClient.selectByKey(Employee.class, 3);
		emp.setPaidHoliday(new Date());

		System.out.println(mysqlClient.update(emp));
	}

	public void replace() throws SQLException {
		Employee emp = new Employee();
		emp.setId(4L);
		mysqlClient.select2(emp);
		emp.setCardNo("111-222-333");
		emp.setPaidHoliday(new Date());
		System.out.println(mysqlClient.replace(emp));
	}

	public void merge() throws SQLException {
		Employee emp = new Employee();
		emp.setCardNo("999-666-333");
		emp.setPaidHoliday(new Date());
		System.out.println(mysqlClient.merge(emp, BigDecimal.class));
	}

	public void delete() throws SQLException {
		Employee emp = mysqlClient.selectByKey(Employee.class, 5L);
		System.out.println(emp);
		System.out.println(mysqlClient.deleteByKey(Employee.class, 6L));
	}

	public void selectPage() throws SQLException {
		Page<Employee> page = new Page<>(2, 0, "id", true);
		mysqlClient.selectPage(Employee.class, page);
		System.out.format("total=%d,data=%s", page.getTotal(), page.getData());

	}

	public void showTables() throws SQLException {
		List<Object> list = mysqlClient.query("test.showTables", null, null);
		System.out.println(list);
	}

	public void selectBySql() throws SQLException {
		// List<EmpPart> list = mysqlClient.queryRange("test.selectPartEmployee", EmpPart.class, 0, 2, Arrays.asList(5, "%666%"));
		long start = System.currentTimeMillis();
		for (int i = 0; i < 100000; i++) {
			Page<EmpPart> page = new Page<>(0, 0, "id", true);
			mysqlClient.queryPage("test.selectPartEmployee", EmpPart.class, page, Arrays.asList(5, "%"));
		}
		long end = System.currentTimeMillis();
		System.out.println("used time:" + (end - start));
		// System.out.println(JsonUtils.writeValueAsString(page));
	}

	public void insertBySql() throws SQLException {

		long start = System.currentTimeMillis();
		int base = 1000;
		EmpPart[] array = new EmpPart[1000];
		for (int i = 0; i < 1000; i++) {
			EmpPart part = new EmpPart();
			part.setCardNo(String.format("000-001-%05d", i + 2000));
			part.setId(i * 1L + base);
			part.setGroupName("测试SQL分组");
			part.setVersion(null);
			array[i] = part;
		}
		Long[] result = mysqlClient.batchExecute("test.insertPartEmployee", array, Long.class);
		long end = System.currentTimeMillis();
		System.out.println("used time:" + (end - start));
		System.out.println(Arrays.asList(result));
	}

	public void executeCallback() throws SQLException {
		mysqlClient.callback(new ConnectionCallback<Void>() {

			@Override
			public Void doInConnection(Connection conn) throws SQLException {
				for (int i = 0; i < 10000 * 10; i++) {
					Statement stmt = conn.prepareStatement("show tables");
				}
				return null;
			}
		});
	}

	public void insertIgnore() throws SQLException {
		long start = System.currentTimeMillis();
		List<Employee> list = new LinkedList<Employee>();
		for (int i = 0; i < 10000; i++) {
			Employee emp = new Employee();
			emp.setId(i * 1L + 1);
			emp.setCardNo(String.format("000-001-%05d", i + 3000));
			emp.setGroupName("group " + i);
			emp.setOther(BigDecimal.valueOf(i));
			mysqlClient.merge(emp, Long.class);
		}
		// Long[] result = mysqlClient.batchInsertIgnore(list.toArray(), Long.class);
		long end = System.currentTimeMillis();
		System.out.println("used time:" + (end - start));
		// System.out.println(Arrays.toString(result));
	}

	public void selectRange() throws SQLException {
		List<Employee> list = mysqlClient.selectRange(Employee.class, 0, 1000);
		for (Employee item : list) {
			System.out.println(item);
		}
	}
}
```

* SQL操作: query, queryFirst, queryRange, queryPage, execute, batchExecute

    <见上

## obase-mysql 高级应用

* 灵活的乐观锁定制

* 结合JAVA继承体系，实现引擎数据操作的灵活性

* 支持灵活多变的数据返回接口

##  obase-mysql 定制扩展

* 扩展JdbcAction
    
    假设要对EmpPart自定义JdbcAction实现, 只需要按照命名规范<targetClass>$JdbcAction实现子类即可. 例如
```java
    public class EmpPart$JdbcAction extends JdbcAction{
      ...
    }
```
    通过AsmKit.newJdbcAction()就可以加载创建实例.

* 扩展ActionMeta
    
    如果对某个字段类型要特别定制, 请实现ActionMeta, 并由JdbcAction.markSqlType()注册, 后面遇到该类型的字段后会自动调用该ActionMeta设置参数或提取结果. 例如集合参数的处理.


# 联系方式
    
开发者 | 联系方式
---|---
jasonhe | jasonhe.hzw@foxmail.com, QQ:1255422783
