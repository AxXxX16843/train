<template>
  <div class="mt-page">
    <div class="mt-toolbar">
      <a-button @click="handleQuery()">
        <reload-outlined /> 刷新
      </a-button>
      <span class="total-tip">共 {{ pagination.total }} 条</span>
    </div>

    <a-table :dataSource="list" :columns="columns" :pagination="tablePagination"
      @change="handleTableChange" :loading="loading" rowKey="id" size="middle">
      <template #bodyCell="{ column, record }">
        <template v-if="column.dataIndex === 'seatType'">
          <a-tag v-if="record.seatType === '1'" color="gold">一等座</a-tag>
          <a-tag v-else-if="record.seatType === '2'" color="blue">二等座</a-tag>
          <a-tag v-else-if="record.seatType === '3'" color="green">软卧</a-tag>
          <a-tag v-else-if="record.seatType === '4'" color="orange">硬卧</a-tag>
        </template>
      </template>
    </a-table>
  </div>
</template>

<script>
import { defineComponent, ref, onMounted, computed } from 'vue';
import axios from 'axios';
import { ReloadOutlined } from '@ant-design/icons-vue';

export default defineComponent({
  name: "my-ticket-view",
  components: { ReloadOutlined },
  setup() {
    const loading = ref(false);
    const list = ref([]);
    const pagination = ref({ total: 0, current: 1, pageSize: 10 });

    const columns = [
      { title: '姓名', dataIndex: 'passengerName', key: 'passengerName', width: 70 },
      { title: '日期', dataIndex: 'trainDate', key: 'trainDate' },
      { title: '车次', dataIndex: 'trainCode', key: 'trainCode', width: 70 },
      { title: '出发站', dataIndex: 'startStation', key: 'startStation' },
      { title: '出发时间', dataIndex: 'startTime', key: 'startTime', width: 90 },
      { title: '到达站', dataIndex: 'endStation', key: 'endStation' },
      { title: '到达时间', dataIndex: 'endTime', key: 'endTime', width: 90 },
      { title: '座位类型', dataIndex: 'seatType', key: 'seatType', width: 80, align: 'center' },
      { title: '车厢', dataIndex: 'carriageIndex', key: 'carriageIndex', width: 60, align: 'center' },
      { title: '座位', dataIndex: 'seatRow', key: 'seatRow', width: 60, align: 'center' },
    ];

    const tablePagination = computed(() => ({ ...pagination.value, showSizeChanger: true, showQuickJumper: true, showTotal: (t) => `共 ${t} 条`, pageSizeOptions: ['5', '10', '20'] }));

    const handleQuery = (param) => {
      if (!param) param = { page: 1, size: pagination.value.pageSize };
      loading.value = true;
      axios.get('/member/feign/ticket/query-list', {
        params: { page: param.page, size: param.size }
      }).then((response) => {
        let data = response.data;
        if (data.success) { list.value = data.content.list; pagination.value.current = param.page; pagination.value.total = data.content.total; }
      }).finally(() => { loading.value = false; });
    };

    const handleTableChange = (page) => { pagination.value.pageSize = page.pageSize; handleQuery({ page: page.current, size: page.pageSize }); };

    onMounted(() => { handleQuery({ page: 1, size: pagination.value.pageSize }); });

    return { list, columns, pagination, tablePagination, handleQuery, handleTableChange };
  },
});
</script>

<style scoped>
.mt-page { width: 100%; }
.mt-toolbar { display: flex; align-items: center; justify-content: space-between; margin-bottom: 16px; }
.total-tip { color: #8c8c8c; font-size: 13px; }
:deep(.ant-table-pagination) { justify-content: center !important; }
</style>
