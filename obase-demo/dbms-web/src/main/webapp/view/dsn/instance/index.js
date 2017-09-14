$(function() {
	/** *************************表格**************************** */
	var SearchFormTarget = '#searchForm';
	var grid = {
		selector : '#mygrid',
		columns : [ {
			title : '主键',
			dataIndex : 'id',
			width : '20%',
			visible : false
		}, {
			title : '地址',
			dataIndex : 'ip',
			width : '20%',
		}, {
			title : '端口',
			dataIndex : 'port',
			width : '10%'
		}, {
			title : '名称',
			dataIndex : 'name',
			width : '20%'
		}, {
			title : '项目',
			dataIndex : 'projectname',
			width : '20%'
		}, {
			title : '项目代号',
			dataIndex : 'projectcode',
			width : '20%'
		}, {
			title : '类型',
			dataIndex : 'type',
			width : '20%',
			renderer : Renderer.instanceType
		}, {
			title : '主机(扩展)',
			dataIndex : 'host',
			width : '20%',
		}, {
			title : 'VIP(扩展)',
			dataIndex : 'vip',
			width : '20%',
		}, {
			title : '测试',
			dataIndex : 'istest',
			width : '10%',
			renderer : Renderer.yesno
		}, {
			title : '备份',
			dataIndex : 'isbackup',
			width : '10%',
			renderer : Renderer.yesno
		}, {
			title : '主从',
			dataIndex : 'role',
			width : '20%',
			renderer : Renderer.instanceRole
		}, {
			title : '主地址',
			dataIndex : 'masterip',
			width : '20%',
		}, {
			title : '主端口',
			dataIndex : 'masterport',
			width : '10%',
		}, {
			title : '主DBA',
			dataIndex : 'mdba',
			width : '20%',
			visible: false
		}, {
			title : '从DBA',
			dataIndex : 'sdba',
			width : '20%',
			visible: false
		}, {
			title : '机房',
			dataIndex : 'idc',
			width : '20%',
			visible : false
		}, {
			title : '部门',
			dataIndex : 'dep',
			width : '20%',
			visible : false
		}, {
			title : '备注',
			dataIndex : 'remark',
			width : '20%',
			visible : false
		}, {
			title : '详情',
			dataIndex : 'description',
			width : '20%',
			visible : false
		}, {
			title : '版本',
			dataIndex : 'version',
			width : '20%',
			visible : false
		}, {
			title : '创建者',
			dataIndex : 'createBy',
			width : '20%',
			visible : false
		}, {
			title : '创建时间',
			dataIndex : 'createTime',
			width : '20%',
			renderer : Renderer.datetime,
			visible : false
		}, {
			title : '修改者',
			dataIndex : 'modifyBy',
			width : '20%',
			visible : false
		}, {
			title : '修改时间',
			dataIndex : 'modifyTime',
			width : '20%',
			renderer : Renderer.datetime,
			visible : false
		} ],
		store : App.buiStore('/dsn/instance/list', App.data(SearchFormTarget)),
		addClick : function() {
			showpopwin('add');
		},
		updateClick : function(item) {
			showpopwin('update', item);
		},
		deleteClick : function(item) {
			App.rest('DELETE', '/dsn/instance/delete', {
				id : item.id
			}, function(msg) {
				if (!msg.errno) {
					grid.store.load();
				} else {
					App.error(msg.errmsg)
				}
			})
		},
		detailClick : function(item) {
			showpopwin('detail', item);
		},
		itemdblclick : function(e) {
			showpopwin('detail', e.item);
		},
		refreshClick : function() {
			grid.store.load();
		}
	};
	grid.$ = App.buiGrid(grid);

	$('#searchButton').click(function() {
		grid.store.load(App.data(SearchFormTarget));
	})

	/** *************************弹出窗**************************** */
	var AddPath = '/dsn/instance/add', UpdatePath = '/dsn/instance/update', DeletePath = '/dsn/instance/delete';
	var AddTitle = '添加记录', UpdateTitle = '修改记录', DetailTitle = '记录详情';

	var popwinForm = {
		selector : '#popwinForm'
	};
	var popwin = {
		element : 'popwin',
		width : 850,
		height : 560,
		submitClick : function() {
			popwinForm.$.valid();
			if (popwinForm.$.isValid()) {
				App.rest(popwinForm.method, popwinForm.path, popwinForm.$.getRecord(), function(msg) {
					if (!msg.errno) {
						grid.store.load();
						popwin.$.close();
					} else {
						App.error(msg.errmsg)
					}
				});
			}
		},
		beforeclosed : function() {
			popwinForm.$.set('disabled', false);
			popwinForm.$.getField('id').set('disabled', false);
			if (!popwin.$.get('buttons').length) {
				popwin.$.set('buttons', popwin._buttons);
			}
		}
	};

	function showpopwin(action, params) {

		// 加载select数据
		App.select($, '#projectid', '/dsn/common/projectInfoList', function(v) {
			return '<option' + (params && v.id == params.projectid ? ' selected="selected"' : '') + ' value="' + v.id + '">' + v.name + '(' + v.projectcode + ')</option>'
		});
		
		if (!popwinForm.$) {
			popwinForm.$ = App.buiForm(popwinForm);
		} else {
			popwinForm.$.clearFields();
			popwinForm.$.clearErrors(false, true);
		}

		if (!popwin.$) {
			popwin.$ = App.buiDialog(popwin);
			popwin._buttons = popwin.$.get('buttons');// 缓存数据
		}

		if (params) {
			popwinForm.$.setRecord(params);
		}

		popwinForm.title = AddTitle;
		popwinForm.method = 'POST';
		popwinForm.path = AddPath;
		if (action == 'update') {
			popwinForm.title = UpdateTitle;
			popwinForm.method = 'PUT';
			popwinForm.path = UpdatePath;
			popwinForm.$.getField('id').set('disabled', true);
		} else if (action == 'detail') {
			popwinForm.title = DetailTitle;
			popwinForm.method = null;
			popwinForm.path = null;
			popwinForm.$.set('disabled', true);
			popwin.$.set("buttons", []);
		}

		popwin.$.set('title', popwinForm.title);
		popwin.$.show();
	}

});
