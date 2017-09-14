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
			title : '类型',
			dataIndex : 'projecttype',
			width : '20%'
		}, {
			title : '项目',
			dataIndex : 'projectcode',
			width : '20%'
		}, {
			title : '名称',
			dataIndex : 'name',
			width : '20%',
		}, {
			title : '项目位置',
			dataIndex : 'scm',
			width : '20%',
		}, {
			title : '管理员',
			dataIndex : 'admin',
			width : '20%',
		}, {
			title : '用户',
			dataIndex : 'member',
			width : '20%',
		}, {
			title : '运维',
			dataIndex : 'op',
			width : '20%',
		}, {
			title : '备注',
			dataIndex : 'remark',
			width : '20%',
		}, {
			title : '详情',
			dataIndex : 'description',
			width : '20%',
		}, {
			title : '版本',
			dataIndex : 'version',
			width : '20%',
			visible: false
		}, {
			title : '创建者',
			dataIndex : 'createBy',
			width : '20%',
			visible: false
		}, {
			title : '创建时间',
			dataIndex : 'createTime',
			width : '20%',
			renderer : Renderer.datetime,
			visible: false
		}, {
			title : '修改者',
			dataIndex : 'modifyBy',
			width : '20%',
			visible: false
		}, {
			title : '修改时间',
			dataIndex : 'modifyTime',
			width : '20%',
			renderer : Renderer.datetime,
			visible: false
		} ],
		store : App.buiStore('/dsn/project/list', App.data(SearchFormTarget)),
		addClick : function() {
			showpopwin('add');
		},
		updateClick : function(item) {
			showpopwin('update', item);
		},
		deleteClick : function(item) {
			App.rest('DELETE', '/dsn/project/delete', {
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
	var AddPath = '/dsn/project/add', UpdatePath = '/dsn/project/update', DeletePath = '/dsn/project/delete';
	var AddTitle = '添加记录', UpdateTitle = '修改记录', DetailTitle = '记录详情';

	var popwinForm = {
		selector : '#popwinForm'
	};
	var popwin = {
		element : 'popwin',
		width : 650,
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
