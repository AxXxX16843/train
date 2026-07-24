<template>
  <div class="station-page">
    <div class="station-toolbar">
      <div class="toolbar-left">
        <a-button type="primary" @click="onAdd">
          <plus-outlined /> 新增
        </a-button>
        <a-button @click="handleQuery()" style="margin-left: 8px">
          <reload-outlined /> 刷新
        </a-button>
        <a-popconfirm
            v-show="selectedRowKeys.length > 0"
            title="确认删除选中的站点？"
            @confirm="onBatchDelete"
            ok-text="确认" cancel-text="取消"
        >
          <a-button danger style="margin-left: 8px">
            <delete-outlined /> 删除选中 ({{ selectedRowKeys.length }})
          </a-button>
        </a-popconfirm>
      </div>
      <span class="total-tip">共 {{ pagination.total }} 个站点</span>
    </div>

    <a-table
        :dataSource="stations"
        :columns="columns"
        :pagination="tablePagination"
        @change="handleTableChange"
        :loading="loading"
        rowKey="id"
        size="middle"
        :row-selection="rowSelection"
    >
      <template #bodyCell="{ column, record }">
        <template v-if="column.dataIndex === 'operation'">
          <a-space>
            <a-button type="link" size="small" @click="onEdit(record)">编辑</a-button>
            <a-popconfirm
                title="确认删除该站点？"
                @confirm="onDelete(record)"
                ok-text="确认" cancel-text="取消"
            >
              <a-button type="link" danger size="small">删除</a-button>
            </a-popconfirm>
          </a-space>
        </template>
      </template>
    </a-table>

    <a-modal v-model:visible="visible" :title="modalTitle" @ok="handleOk"
             ok-text="确认" cancel-text="取消" :destroyOnClose="true">
      <a-form :model="station" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }">
        <a-form-item label="站名">
          <a-input v-model:value="station.name" placeholder="请输入站名" />
        </a-form-item>
        <a-form-item label="站名拼音">
          <a-input v-model:value="station.namePinyin" placeholder="请输入站名拼音" />
        </a-form-item>
        <a-form-item label="拼音首字母">
          <a-input v-model:value="station.namePy" placeholder="请输入拼音首字母" />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script>
import { defineComponent, ref, onMounted, computed } from 'vue';
import axios from 'axios';
import { notification } from 'ant-design-vue';
import { PlusOutlined, ReloadOutlined, DeleteOutlined } from '@ant-design/icons-vue';

export default defineComponent({
  name: "station-view",
  components: { PlusOutlined, ReloadOutlined, DeleteOutlined },
  setup() {
    const visible = ref(false);
    const loading = ref(false);
    const isEdit = ref(false);
    const stations = ref([]);
    const pagination = ref({ total: 0, current: 1, pageSize: 10 });
    const station = ref({ name: '', namePinyin: '', namePy: '' });
    const selectedRowKeys = ref([]);

    const onSelectChange = (keys) => {
      selectedRowKeys.value = keys;
    };

    const columns = [
      { title: '站名', dataIndex: 'name', key: 'name' },
      { title: '站名拼音', dataIndex: 'namePinyin', key: 'namePinyin' },
      { title: '拼音首字母', dataIndex: 'namePy', key: 'namePy' },
      { title: '操作', dataIndex: 'operation', key: 'operation', align: 'center', width: 140 },
    ];

    const modalTitle = computed(() => isEdit.value ? '编辑站点' : '新增站点');

    const rowSelection = computed(() => ({
      selectedRowKeys: selectedRowKeys.value,
      onChange: onSelectChange,
    }));

    const tablePagination = computed(() => ({
      ...pagination.value,
      showSizeChanger: true,
      showQuickJumper: true,
      showTotal: (total) => `共 ${total} 条`,
      pageSizeOptions: ['5', '10', '20'],
    }));

    const handleQuery = (param) => {
      if (!param) {
        param = { page: 1, size: pagination.value.pageSize };
      }
      loading.value = true;
      axios.get('/business/admin/station/query-list', {
        params: { page: param.page, size: param.size }
      }).then((response) => {
        let data = response.data;
        if (data.success) {
          stations.value = data.content.list;
          pagination.value.current = param.page;
          pagination.value.total = data.content.total;
        } else {
          notification.error({ description: data.message });
        }
      }).finally(() => {
        loading.value = false;
      });
    };

    const handleTableChange = (page) => {
      pagination.value.pageSize = page.pageSize;
      handleQuery({ page: page.current, size: page.pageSize });
    };

    const onAdd = () => {
      isEdit.value = false;
      station.value = { name: '', namePinyin: '', namePy: '' };
      visible.value = true;
    };

    const onEdit = (record) => {
      isEdit.value = true;
      station.value = {
        id: record.id,
        name: record.name,
        namePinyin: record.namePinyin,
        namePy: record.namePy,
      };
      visible.value = true;
    };

    const handleOk = () => {
      const url = isEdit.value ? '/business/admin/station/update' : '/business/admin/station/save';
      axios.post(url, station.value).then((response) => {
        let data = response.data;
        if (data.success) {
          notification.success({ description: isEdit.value ? '修改成功！' : '保存成功！' });
          visible.value = false;
          handleQuery();
        } else {
          notification.error({ description: data.message });
        }
      });
    };

    const onDelete = (record) => {
      axios.delete('/business/admin/station/delete/' + record.id).then((response) => {
        let data = response.data;
        if (data.success) {
          notification.success({ description: '删除成功！' });
          handleQuery({ page: pagination.value.current, size: pagination.value.pageSize });
        } else {
          notification.error({ description: data.message });
        }
      });
    };

    const onBatchDelete = () => {
      const ids = selectedRowKeys.value.join(',');
      axios.delete('/business/admin/station/delete/' + ids).then((response) => {
        let data = response.data;
        if (data.success) {
          notification.success({ description: `已删除 ${selectedRowKeys.value.length} 个站点` });
          selectedRowKeys.value = [];
          handleQuery();
        } else {
          notification.error({ description: data.message });
        }
      });
    };

    onMounted(() => {
      handleQuery({ page: 1, size: pagination.value.pageSize });
    });

    return {
      stations, columns, pagination, tablePagination,
      station, visible, loading, isEdit, modalTitle, selectedRowKeys, rowSelection,
      onAdd, onEdit, handleOk, onDelete, onBatchDelete, onSelectChange,
      handleQuery, handleTableChange,
    };
  },
});
</script>

<style scoped>
.station-page {
  width: 100%;
}
.station-toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}
.total-tip {
  color: #8c8c8c;
  font-size: 13px;
}
:deep(.ant-table-pagination) {
  justify-content: center !important;
}
</style>
