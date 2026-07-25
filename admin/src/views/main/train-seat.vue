<template>
  <div class="seat-page">
    <div class="seat-toolbar">
      <div class="toolbar-left">
        <a-button @click="handleQuery()">
          <reload-outlined /> 刷新
        </a-button>
        <a-select v-model:value="searchCode" placeholder="按车次筛选" allowClear show-search style="width: 240px; margin-left: 8px" @change="(val) => { searchCode = val; handleQuery({}); }">
          <a-select-option v-for="t in trainCodeList" :key="t.code" :value="t.code">
            {{ t.code }}（{{ t.start }} → {{ t.end }}）
          </a-select-option>
        </a-select>
      </div>
      <span class="total-tip">共 {{ pagination.total }} 个座位</span>
    </div>

    <a-table
        :dataSource="seats"
        :columns="columns"
        :pagination="tablePagination"
        @change="handleTableChange"
        :loading="loading"
        rowKey="id"
        size="middle"
    >
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
import { notification } from 'ant-design-vue';
import { ReloadOutlined } from '@ant-design/icons-vue';

export default defineComponent({
  name: "train-seat-view",
  components: { ReloadOutlined },
  setup() {
    const loading = ref(false);
    const seats = ref([]);
    const pagination = ref({ total: 0, current: 1, pageSize: 10 });
    const trainCodeList = ref([]);
    const searchCode = ref();

    const columns = [
      { title: '车次编号', dataIndex: 'trainCode', key: 'trainCode' },
      { title: '车厢序号', dataIndex: 'carriageIndex', key: 'carriageIndex', align: 'center' },
      { title: '排号', dataIndex: 'row', key: 'row', align: 'center' },
      { title: '列号', dataIndex: 'col', key: 'col', align: 'center' },
      { title: '座位序号', dataIndex: 'carriageSeatIndex', key: 'carriageSeatIndex', align: 'center' },
      { title: '座位类型', dataIndex: 'seatType', key: 'seatType', align: 'center' },
    ];

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
        searchCode.value = undefined;
      }
      loading.value = true;
      axios.get('/business/admin/train-seat/query-list', {
        params: { page: param.page, size: param.size, trainCode: searchCode.value }
      }).then((response) => {
        let data = response.data;
        if (data.success) {
          seats.value = data.content.list;
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

    onMounted(() => {
      handleQuery({ page: 1, size: pagination.value.pageSize });
      axios.get('/business/admin/train/query-all').then((res) => {
        if (res.data.success) trainCodeList.value = res.data.content;
      });
    });

    return {
      seats, columns, pagination, tablePagination,
      trainCodeList, searchCode,
      handleQuery, handleTableChange,
    };
  },
});
</script>

<style scoped>
.seat-page {
  width: 100%;
}
.seat-toolbar {
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
