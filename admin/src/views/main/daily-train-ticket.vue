<template>
  <div class="dtt-page">
    <div class="dtt-toolbar">
      <div class="toolbar-left">
        <a-button @click="handleQuery()">
          <reload-outlined /> 刷新
        </a-button>
        <a-select v-model:value="searchCode" placeholder="按车次筛选" allowClear show-search style="width: 200px; margin-left: 8px" @change="onSearch">
          <a-select-option v-for="t in trainCodeList" :key="t.code" :value="t.code">
            {{ t.code }}（{{ t.start }} → {{ t.end }}）
          </a-select-option>
        </a-select>
        <a-date-picker v-model:value="searchDate" placeholder="按日期筛选" value-format="YYYY-MM-DD" style="margin-left: 8px" @change="onSearchDate" />
        <a-select ref="startRef" v-model:value="searchStart" placeholder="始发站" show-search allowClear style="width: 160px; margin-left: 8px">
          <a-select-option v-for="s in stationList" :key="s.name" :value="s.name">{{ s.name }}</a-select-option>
        </a-select>
        <a-select ref="endRef" v-model:value="searchEnd" placeholder="终点站" show-search allowClear style="width: 160px; margin-left: 8px">
          <a-select-option v-for="s in stationList" :key="s.name" :value="s.name">{{ s.name }}</a-select-option>
        </a-select>
        <a-button type="primary" style="margin-left: 8px" @click="onQueryClick">查询</a-button>
      </div>
      <span class="total-tip">共 {{ pagination.total }} 条</span>
    </div>

    <a-table :dataSource="list" :columns="columns" :pagination="tablePagination"
      @change="handleTableChange" :loading="loading" rowKey="id" size="middle"
      :expandedRowKeys="expandedKeys" @expandedRowsChange="(keys) => expandedKeys = keys"
      :expandRowByClick="true"
      :scroll="{ x: 1000 }"
    >
      <template #bodyCell="{ column, record }">
        <template v-if="colIsTicket(column.dataIndex)">
          <span v-if="record[column.dataIndex] === -1">—</span>
          <a-tag v-else-if="record[column.dataIndex] > 50" color="green">有票</a-tag>
          <span v-else>{{ record[column.dataIndex] }}</span>
        </template>
      </template>
      <template #expandedRowRender="{ record }">
        <div class="price-row">
          <span class="price-label">票价</span>
          <div class="price-list">
            <span>一等座 {{ record.ydzPrice != null ? '¥' + record.ydzPrice : '—' }}</span>
            <span>二等座 {{ record.edzPrice != null ? '¥' + record.edzPrice : '—' }}</span>
            <span>软卧 {{ record.rwPrice != null ? '¥' + record.rwPrice : '—' }}</span>
            <span>硬卧 {{ record.ywPrice != null ? '¥' + record.ywPrice : '—' }}</span>
          </div>
        </div>
      </template>
    </a-table>
  </div>
</template>

<script>
import { defineComponent, ref, onMounted, computed } from 'vue';
import axios from 'axios';
import { notification } from 'ant-design-vue';
import { ReloadOutlined } from '@ant-design/icons-vue';
import cache from '@/utils/cache';

const TICKET_COLS = ['ydz', 'edz', 'rw', 'yw'];

export default defineComponent({
  name: "daily-train-ticket-view",
  components: { ReloadOutlined },
  setup() {
    const loading = ref(false);
    const list = ref([]);
    const pagination = ref({ total: 0, current: 1, pageSize: 10 });
    const trainCodeList = ref([]);
    const stationList = ref([]);
    const searchCode = ref();
    const searchDate = ref();
    const searchStart = ref();
    const searchEnd = ref();
    const expandedKeys = ref([]);

    const colIsTicket = (key) => TICKET_COLS.includes(key);

    const columns = [
      { title: '日期', dataIndex: 'date', key: 'date', width: 100 },
      { title: '车次', dataIndex: 'trainCode', key: 'trainCode', width: 70 },
      { title: '始发站', dataIndex: 'start', key: 'start', width: 90 },
      { title: '发车时间', dataIndex: 'startTime', key: 'startTime', width: 80 },
      { title: '终点站', dataIndex: 'end', key: 'end', width: 90 },
      { title: '到达时间', dataIndex: 'endTime', key: 'endTime', width: 80 },
      { title: '一等座', dataIndex: 'ydz', key: 'ydz', width: 80, align: 'center' },
      { title: '二等座', dataIndex: 'edz', key: 'edz', width: 80, align: 'center' },
      { title: '软卧', dataIndex: 'rw', key: 'rw', width: 80, align: 'center' },
      { title: '硬卧', dataIndex: 'yw', key: 'yw', width: 80, align: 'center' },
    ];

    const tablePagination = computed(() => ({ ...pagination.value, showSizeChanger: true, showQuickJumper: true, showTotal: (t) => `共 ${t} 条`, pageSizeOptions: ['5', '10', '20'] }));

    const handleQuery = (param) => {
      if (!param) { param = { page: 1, size: pagination.value.pageSize }; searchCode.value = undefined; searchDate.value = undefined; searchStart.value = undefined; searchEnd.value = undefined; }
      loading.value = true;
      axios.get('/business/admin/daily-train-ticket/query-list', {
        params: { page: param.page, size: param.size, trainCode: searchCode.value, date: searchDate.value, startStation: searchStart.value, endStation: searchEnd.value }
      }).then((response) => {
        let data = response.data;
        if (data.success) { list.value = data.content.list; pagination.value.current = param.page; pagination.value.total = data.content.total; }
        else { notification.error({ description: data.message }); }
      }).finally(() => { loading.value = false; });
    };

    const onQueryClick = () => {
      if (searchStart.value && !searchEnd.value) { notification.warning({ description: '请选择终点站' }); return; }
      if (!searchStart.value && searchEnd.value) { notification.warning({ description: '请选择始发站' }); return; }
      handleQuery({ page: 1, size: pagination.value.pageSize });
    };

    const handleTableChange = (page) => { pagination.value.pageSize = page.pageSize; handleQuery({ page: page.current, size: page.pageSize }); };
    const onSearch = (val) => { searchCode.value = val; handleQuery({ page: 1, size: pagination.value.pageSize }); };
    const onSearchDate = () => { handleQuery({ page: 1, size: pagination.value.pageSize }); };

    onMounted(() => {
      handleQuery({ page: 1, size: pagination.value.pageSize });
      const cachedTrain = cache.get('train_all'); if (cachedTrain) { trainCodeList.value = cachedTrain; }
      else { axios.get('/business/admin/train/query-all').then(res => { if (res.data.success) { trainCodeList.value = res.data.content; cache.set('train_all', res.data.content); } }); }
      const cachedStation = cache.get('station_all'); if (cachedStation) { stationList.value = cachedStation; }
      else { axios.get('/business/admin/station/query-all').then(res => { if (res.data.success) { stationList.value = res.data.content; cache.set('station_all', res.data.content); } }); }
    });

    return {
      list, columns, pagination, tablePagination, trainCodeList, stationList,
      searchCode, searchDate, searchStart, searchEnd,
      expandedKeys, colIsTicket,
      handleQuery, handleTableChange, onSearch, onSearchDate, onQueryClick,
    };
  },
});
</script>

<style scoped>
.dtt-page { width: 100%; }
.dtt-toolbar { display: flex; align-items: center; justify-content: space-between; margin-bottom: 16px; }
.total-tip { color: #8c8c8c; font-size: 13px; }
:deep(.ant-table-pagination) { justify-content: center !important; }
:deep(.ant-table) { overflow-x: auto; }
.price-row { display: flex; align-items: center; padding: 8px 16px; }
.price-label { font-weight: 600; color: #333; margin-right: 16px; flex-shrink: 0; }
.price-list { display: flex; gap: 24px; }
.price-list span { font-weight: 600; }
</style>
