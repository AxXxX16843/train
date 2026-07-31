<template>
  <div class="ticket-page">
    <div class="ticket-toolbar">
      <div class="toolbar-left">
        <a-date-picker v-model:value="searchDate" placeholder="选择日期" value-format="YYYY-MM-DD" style="width: 150px" :disabled-date="disabledDate" />
        <a-select ref="sRef" v-model:value="searchStart" placeholder="始发站" show-search allowClear style="width: 160px; margin-left: 8px">
          <a-select-option v-for="s in stationList" :key="s.name" :value="s.name">{{ s.name }}</a-select-option>
        </a-select>
        <a-select ref="eRef" v-model:value="searchEnd" placeholder="终点站" show-search allowClear style="width: 160px; margin-left: 8px">
          <a-select-option v-for="s in stationList" :key="s.name" :value="s.name">{{ s.name }}</a-select-option>
        </a-select>
        <a-button type="primary" style="margin-left: 8px" @click="onQueryClick">查询</a-button>
        <a-button @click="handleQuery()" style="margin-left: 8px">
          <reload-outlined /> 刷新
        </a-button>
      </div>
      <span class="total-tip">共 {{ pagination.total }} 条</span>
    </div>

    <a-table :dataSource="list" :columns="columns" :pagination="tablePagination"
      @change="handleTableChange" :loading="loading" rowKey="id" size="middle"
      :expandedRowKeys="expandedKeys" @expandedRowsChange="(keys) => expandedKeys = keys"
      :expandRowByClick="true"
    >
      <template #bodyCell="{ column, record }">
        <template v-if="column.dataIndex === 'route'">
          <div class="merge-col"><span class="start-label">始</span>{{ record.start }}</div>
          <div class="merge-col"><span class="end-label">终</span>{{ record.end }}</div>
        </template>
        <template v-else-if="column.dataIndex === 'times'">
          <div class="merge-col">{{ record.startTime }}</div>
          <div class="merge-col">{{ record.endTime }}</div>
        </template>
        <template v-else-if="column.dataIndex === 'operation'">
          <a-button type="primary" size="small" @click="onBook(record)">预订</a-button>
          <a-button size="small" style="margin-left:4px" @click="onShowStations(record)">途径</a-button>
        </template>
        <template v-else-if="column.dataIndex === 'duration'">
          {{ calcDuration(record.startTime, record.endTime) }}
        </template>
        <template v-else-if="colIsTicket(column.dataIndex)">
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

    <!-- 途经车站 -->
    <a-modal v-model:visible="stationVisible" :title="stationTrainCode + ' 途经车站'" :footer="null" width="700px">
      <a-table :dataSource="stationData" :columns="stationColumns" :pagination="false" rowKey="index" size="small" :loading="stationLoading">
        <template #bodyCell="{ column, record }">
          <template v-if="column.dataIndex === 'inTime'">{{ formatTime(record.inTime) }}</template>
          <template v-else-if="column.dataIndex === 'outTime'">{{ formatTime(record.outTime) }}</template>
          <template v-else-if="column.dataIndex === 'stopTime'">{{ formatTime(record.stopTime) }}</template>
        </template>
      </a-table>
    </a-modal>
  </div>
</template>

<script>
import { defineComponent, ref, onMounted, computed } from 'vue';
import { useRouter } from 'vue-router';
import axios from 'axios';
import { notification } from 'ant-design-vue';
import { ReloadOutlined } from '@ant-design/icons-vue';
import cache from '@/utils/cache';

const TICKET_COLS = ['ydz', 'edz', 'rw', 'yw'];

export default defineComponent({
  name: "ticket-view",
  components: { ReloadOutlined },
  setup() {
    const router = useRouter();
    const loading = ref(false);
    const list = ref([]);
    const pagination = ref({ total: 0, current: 1, pageSize: 10 });
    const stationList = ref([]);
    const searchDate = ref();
    const searchStart = ref();
    const searchEnd = ref();
    const expandedKeys = ref([]);
    const disabledDate = (current) => {
      const today = new Date(new Date().toDateString()).getTime();
      const max = today + 15 * 24 * 3600 * 1000;
      return current.valueOf() < today || current.valueOf() > max;
    };

    const colIsTicket = (key) => TICKET_COLS.includes(key);

    const calcDuration = (startTime, endTime) => {
      const t = (s) => { if (!s) return 0; const p = s.split(':'); return parseInt(p[0])*60 + parseInt(p[1]); };
      let d = t(endTime) - t(startTime);
      if (d <= 0) { d += 24 * 60; }
      if (d <= 0) return '—';
      const prefix = t(endTime) < t(startTime) ? '隔日达 ' : '';
      return prefix + Math.floor(d/60) + '时' + (d%60) + '分';
    };

    const columns = [
      { title: '车次', dataIndex: 'trainCode', key: 'trainCode', width: 70 },
      { title: '出发站 / 到达站', dataIndex: 'route', key: 'route', width: 130 },
      { title: '出发 / 到达时间', dataIndex: 'times', key: 'times', width: 90, align: 'center' },
      { title: '历时', dataIndex: 'duration', key: 'duration', width: 80, align: 'center' },
      { title: '一等座', dataIndex: 'ydz', key: 'ydz', width: 80, align: 'center' },
      { title: '二等座', dataIndex: 'edz', key: 'edz', width: 80, align: 'center' },
      { title: '软卧', dataIndex: 'rw', key: 'rw', width: 80, align: 'center' },
      { title: '硬卧', dataIndex: 'yw', key: 'yw', width: 80, align: 'center' },
      { title: '操作', dataIndex: 'operation', key: 'operation', width: 60, align: 'center' },
    ];

    const tablePagination = computed(() => ({ ...pagination.value, showSizeChanger: true, showQuickJumper: true, showTotal: (t) => `共 ${t} 条`, pageSizeOptions: ['5', '10', '20'] }));

    const handleQuery = (param) => {
      if (!param) { param = { page: 1, size: pagination.value.pageSize }; searchDate.value = undefined; searchStart.value = undefined; searchEnd.value = undefined; }
      loading.value = true;
      axios.get('/business/admin/daily-train-ticket/query-list', {
        params: { page: param.page, size: param.size, date: searchDate.value, startStation: searchStart.value, endStation: searchEnd.value }
      }).then((response) => {
        let data = response.data;
        if (data.success) { list.value = data.content.list; pagination.value.current = param.page; pagination.value.total = data.content.total; }
        else { notification.error({ description: data.message }); }
      }).finally(() => { loading.value = false; });
    };

    const handleTableChange = (page) => { pagination.value.pageSize = page.pageSize; handleQuery({ page: page.current, size: page.pageSize }); };

    const stationVisible = ref(false);
    const stationTrainCode = ref('');
    const stationData = ref([]);
    const stationLoading = ref(false);
    const stationColumns = [
      { title: '序号', dataIndex: 'index', width: 50, align: 'center' },
      { title: '站名', dataIndex: 'name' },
      { title: '进站时间', dataIndex: 'inTime', width: 90, align: 'center' },
      { title: '出站时间', dataIndex: 'outTime', width: 90, align: 'center' },
      { title: '停留时长', dataIndex: 'stopTime', width: 90, align: 'center' },
    ];
    const formatTime = (t) => t || '—';

    const onShowStations = (record) => {
      stationTrainCode.value = record.trainCode;
      stationVisible.value = true;
      stationLoading.value = true;
      axios.get('/business/admin/daily-train-ticket/query-station', {
        params: { trainCode: record.trainCode, date: record.date, startIndex: record.startIndex, endIndex: record.endIndex }
      }).then(res => {
        if (res.data.success) { stationData.value = res.data.content || []; }
      }).finally(() => { stationLoading.value = false; });
    };

    const onBook = (record) => {
      router.push({ path: '/order', query: { d: encodeURIComponent(JSON.stringify(record)) } });
    };

    const onQueryClick = () => {
      if (!searchDate.value) { notification.warning({ description: '请选择日期' }); return; }
      if (!searchStart.value) { notification.warning({ description: '请选择始发站' }); return; }
      if (!searchEnd.value) { notification.warning({ description: '请选择终点站' }); return; }
      handleQuery({ page: 1, size: pagination.value.pageSize });
    };

    onMounted(() => {
      const cached = cache.get('station_all'); if (cached) { stationList.value = cached; return; }
      axios.get('/business/admin/station/query-all').then(res => { if (res.data.success) { stationList.value = res.data.content; cache.set('station_all', res.data.content); } });
    });

    return {
      list, columns, pagination, tablePagination, stationList, searchDate, searchStart, searchEnd, expandedKeys, disabledDate, colIsTicket, calcDuration,
      stationVisible, stationTrainCode, stationData, stationColumns, stationLoading, formatTime, onShowStations,
      handleQuery, handleTableChange, onQueryClick, onBook,
    };
  },
});
</script>

<style scoped>
.ticket-page { width: 100%; }
.ticket-toolbar {
  display: flex; align-items: center; justify-content: space-between;
  padding: 16px 20px; margin-bottom: 16px;
  background: #fafafa; border-radius: 10px; border: 1px solid #f0f0f0;
}
.total-tip { color: #8c8c8c; font-size: 13px; }
:deep(.ant-table) { border-radius: 10px; overflow: hidden; }
:deep(.ant-table-pagination) { justify-content: center !important; padding: 16px 0; }
.price-row { display: flex; align-items: center; padding: 10px 20px; }
.price-label { font-weight: 600; color: #333; margin-right: 16px; flex-shrink: 0; }
.price-list { display: flex; gap: 24px; }
.price-list span { font-weight: 600; }
.merge-col { line-height: 2; }
.start-label { background: #1677ff; color: #fff; font-size: 11px; padding: 1px 5px; border-radius: 3px; margin-right: 4px; }
.end-label { background: #ff7a45; color: #fff; font-size: 11px; padding: 1px 5px; border-radius: 3px; margin-right: 4px; }
:deep(.ant-btn-primary) { border-radius: 6px; }
:deep(.ant-tag-green) { background: #f6ffed; border-color: #b7eb8f; color: #389e0d; border-radius: 6px; }
:deep(.ant-tag) { border-radius: 6px; }
</style>
