<template>
  <div class="co-page">
    <div class="co-toolbar">
      <div class="toolbar-left">
        <a-button @click="handleQuery()">
          <reload-outlined /> 刷新
        </a-button>
        <a-date-picker v-model:value="searchDate" placeholder="按日期筛选" value-format="YYYY-MM-DD" style="margin-left: 8px" @change="handleQuery({page:1,size:pagination.pageSize})" />
      </div>
      <span class="total-tip">共 {{ pagination.total }} 条</span>
    </div>

    <a-table :dataSource="list" :columns="columns" :pagination="tablePagination"
      @change="handleTableChange" :loading="loading" rowKey="id" size="middle">
      <template #bodyCell="{ column, record }">
        <template v-if="column.dataIndex === 'status'">
          <a-tag v-if="record.status === 'PENDING'" color="orange">待处理</a-tag>
          <a-tag v-else-if="record.status === 'SUCCESS'" color="green">已成交</a-tag>
          <a-tag v-else-if="record.status === 'CANCEL'" color="default">已取消</a-tag>
          <span v-else>{{ record.status }}</span>
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
  name: "confirm-order-view",
  components: { ReloadOutlined },
  setup() {
    const loading = ref(false);
    const list = ref([]);
    const pagination = ref({ total: 0, current: 1, pageSize: 10 });
    const searchDate = ref();

    const columns = [
      { title: '日期', dataIndex: 'date', key: 'date' },
      { title: '车次', dataIndex: 'trainCode', key: 'trainCode', width: 70 },
      { title: '始发站', dataIndex: 'start', key: 'start' },
      { title: '终点站', dataIndex: 'end', key: 'end' },
      { title: '会员ID', dataIndex: 'memberId', key: 'memberId', width: 100 },
      { title: '状态', dataIndex: 'status', key: 'status', align: 'center', width: 80 },
      { title: '创建时间', dataIndex: 'createTime', key: 'createTime' },
    ];

    const tablePagination = computed(() => ({ ...pagination.value, showSizeChanger: true, showQuickJumper: true, showTotal: (t) => `共 ${t} 条`, pageSizeOptions: ['5', '10', '20'] }));

    const handleQuery = (param) => {
      if (!param) { param = { page: 1, size: pagination.value.pageSize }; searchDate.value = undefined; }
      loading.value = true;
      axios.get('/business/admin/confirm-order/query-list', {
        params: { page: param.page, size: param.size, date: searchDate.value }
      }).then((response) => {
        let data = response.data;
        if (data.success) { list.value = data.content.list; pagination.value.current = param.page; pagination.value.total = data.content.total; }
        else { notification.error({ description: data.message }); }
      }).finally(() => { loading.value = false; });
    };

    const handleTableChange = (page) => { pagination.value.pageSize = page.pageSize; handleQuery({ page: page.current, size: page.pageSize }); };

    onMounted(() => { handleQuery({ page: 1, size: pagination.value.pageSize }); });

    return { list, columns, pagination, tablePagination, searchDate, handleQuery, handleTableChange };
  },
});
</script>

<style scoped>
.co-page { width: 100%; }
.co-toolbar { display: flex; align-items: center; justify-content: space-between; margin-bottom: 16px; }
.total-tip { color: #8c8c8c; font-size: 13px; }
:deep(.ant-table-pagination) { justify-content: center !important; }
</style>
