<template>
  <div class="tk-page">
    <div class="tk-toolbar">
      <a-button @click="handleQuery()">
        <reload-outlined /> 刷新
      </a-button>
      <span class="total-tip">共 {{ pagination.total }} 条</span>
    </div>

    <a-table :dataSource="list" :columns="columns" :pagination="tablePagination"
      @change="handleTableChange" :loading="loading" rowKey="id" size="middle">
      <template #bodyCell="{ column, record }">
        <template v-if="column.dataIndex === 'operation'">
          <span v-if="editingId === record.id">
            <a-input-number v-model:value="editCount" :min="0" style="width:80px" />
            <a-button type="primary" size="small" @click="onSave(record)" style="margin-left:6px">保存</a-button>
            <a-button size="small" @click="editingId = null" style="margin-left:4px">取消</a-button>
          </span>
          <a-button v-else type="primary" size="small" @click="onEdit(record)">编辑</a-button>
        </template>
      </template>
    </a-table>
  </div>
</template>

<script>
import { defineComponent, ref, onMounted, computed } from 'vue';
import axios from 'axios';
import { notification } from 'ant-design-vue';
import { ReloadOutlined } from '@ant-design/icons-vue';

export default defineComponent({
  name: "token-view",
  components: { ReloadOutlined },
  setup() {
    const loading = ref(false);
    const list = ref([]);
    const pagination = ref({ total: 0, current: 1, pageSize: 10 });
    const editingId = ref(null);
    const editCount = ref(0);

    const columns = [
      { title: '日期', dataIndex: 'date', key: 'date' },
      { title: '车次', dataIndex: 'trainCode', key: 'trainCode', width: 80 },
      { title: '令牌余量', dataIndex: 'count', key: 'count' },
      { title: '更新时间', dataIndex: 'updateTime', key: 'updateTime' },
      { title: '操作', dataIndex: 'operation', key: 'operation', width: 200, align: 'center' },
    ];

    const tablePagination = computed(() => ({ ...pagination.value, showSizeChanger: true, showQuickJumper: true, showTotal: (t) => `共 ${t} 条`, pageSizeOptions: ['5', '10', '20'] }));

    const handleQuery = (param) => {
      if (!param) param = { page: 1, size: pagination.value.pageSize };
      loading.value = true;
      axios.get('/business/admin/sk-token/query-list', {
        params: { page: param.page, size: param.size }
      }).then((response) => {
        let data = response.data;
        if (data.success) { list.value = data.content.list; pagination.value.current = param.page; pagination.value.total = data.content.total; }
        else { notification.error({ description: data.message }); }
      }).finally(() => { loading.value = false; });
    };

    const handleTableChange = (page) => { pagination.value.pageSize = page.pageSize; handleQuery({ page: page.current, size: page.pageSize }); };

    const onEdit = (record) => { editingId.value = record.id; editCount.value = record.count; };
    const onSave = (record) => {
      axios.post('/business/admin/sk-token/update', { id: record.id, date: record.date, trainCode: record.trainCode, count: editCount.value }).then((response) => {
        if (response.data.success) {
          notification.success({ description: '修改成功' });
          editingId.value = null;
          handleQuery({ page: pagination.value.current, size: pagination.value.pageSize });
        } else {
          notification.error({ description: response.data.message });
        }
      });
    };

    onMounted(() => { handleQuery({ page: 1, size: pagination.value.pageSize }); });

    return { list, columns, pagination, tablePagination, loading, editingId, editCount, handleQuery, handleTableChange, onEdit, onSave };
  },
});
</script>

<style scoped>
.tk-page { width: 100%; }
.tk-toolbar { display: flex; align-items: center; justify-content: space-between; margin-bottom: 16px; }
.total-tip { color: #8c8c8c; font-size: 13px; }
:deep(.ant-table-pagination) { justify-content: center !important; }
</style>
