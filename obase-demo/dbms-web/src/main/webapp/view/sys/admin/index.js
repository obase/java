$(function() {
	/** *************************表格**************************** */
	var SearchFormTarget = '#searchForm';
	var grid = {
		selector : '#mygrid',
		columns : [ {
			title : '通行证',
			dataIndex : 'id',
			width : '20%',
		}, {
			title : '实名',
			dataIndex : 'realname',
			width : '20%',
		}, {
			title : '邮箱',
			dataIndex : 'email',
			width : '20%',
		}, {
			title : '手机',
			dataIndex : 'phone',
			width : '20%',
		}, {
			title : '等级',
			dataIndex : 'level',
			width : '20%',
			renderer : Renderer.level
		}, {
			title : '版本',
			dataIndex : 'version',
			visible : false
		} ],
		store : App.buiStore('/sys/admin/list', App.data(SearchFormTarget)),
		addClick : function() {
			showpopwin('add');
		},
		updateClick : function(item) {
			showpopwin('update', item);
		},
		deleteClick : function(item) {
			App.rest('DELETE', '/sys/admin/delete', {
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
	var AddPath = '/sys/admin/add', UpdatePath = '/sys/admin/update', DeletePath = '/sys/admin/delete';
	var AddTitle = '添加记录', UpdateTitle = '修改记录', DetailTitle = '记录详情';

	var popwinForm = {
		selector : '#popwinForm'
	};
	var popwin = {
		element : 'popwin',
		width : 500,
		height : 300,
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
