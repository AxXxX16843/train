<template>
  <div class="dt-page">
    <div class="dt-toolbar">
      <div class="toolbar-left">
        <a-button type="primary" @click="onAdd">
          <plus-outlined /> 新增
        </a-button>
        <a-button @click="handleQuery()" style="margin-left: 8px">
          <reload-outlined /> 刷新
        </a-button>
        <a-select v-model:value="searchCode" placeholder="按车次筛选" allowClear show-search style="width: 240px; margin-left: 8px" @change="onSearch">
          <a-select-option v-for="t in trainCodeList" :key="t.code" :value="t.code">
            {{ t.code }}（{{ t.start }} → {{ t.end }}）
          </a-select-option>
        </a-select>
        <a-date-picker v-model:value="searchDate" placeholder="按日期筛选" value-format="YYYY-MM-DD" style="margin-left: 8px" @change="onSearchDate" />
      </div>
      <span class="total-tip">共 {{ pagination.total }} 条</span>
    </div>

    <a-table
        :dataSource="dailyTrains" :columns="columns" :pagination="tablePagination"
        @change="handleTableChange" :loading="loading" rowKey="id" size="middle"
        :row-selection="rowSelection"
    >
      <template #bodyCell="{ column, record }">
        <template v-if="column.dataIndex === 'type'">
          <a-tag v-if="record.type === 'G'" color="blue">高铁</a-tag>
          <a-tag v-else-if="record.type === 'D'" color="green">动车</a-tag>
          <a-tag v-else-if="record.type === 'K'" color="orange">快速</a-tag>
        </template>
        <template v-else-if="column.dataIndex === 'operation'">
          <a-space>
            <a-button type="link" size="small" @click="onEdit(record)">编辑</a-button>
            <a-popconfirm title="确认删除？" @confirm="onDelete(record)" ok-text="确认" cancel-text="取消">
              <a-button type="link" danger size="small">删除</a-button>
            </a-popconfirm>
          </a-space>
        </template>
      </template>
    </a-table>

    <a-modal v-model:visible="visible" :title="modalTitle" @ok="handleOk"
             ok-text="确认" cancel-text="取消" :destroyOnClose="true">
      <a-form :model="item" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }">
        <a-form-item label="日期">
          <a-date-picker v-model:value="item.date" value-format="YYYY-MM-DD" placeholder="请选择日期" style="width: 100%" />
        </a-form-item>
        <a-form-item label="车次编号">
          <a-select v-model:value="item.code" placeholder="请选择车次编号" show-search @change="onCodeChange">
            <a-select-option v-for="t in trainCodeList" :key="t.code" :value="t.code">
              {{ t.code }}（{{ t.start }} → {{ t.end }}）
            </a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="车次类型">
          <a-input :value="item.type" placeholder="自动填充" disabled />
        </a-form-item>
        <a-form-item label="始发站">
          <a-input :value="item.start" placeholder="自动填充" disabled />
        </a-form-item>
        <a-form-item label="出发时间">
          <a-time-picker v-model:value="item.startTime" value-format="HH:mm:ss" format="HH:mm:ss" placeholder="自动填充" style="width: 100%" />
        </a-form-item>
        <a-form-item label="终点站">
          <a-input :value="item.end" placeholder="自动填充" disabled />
        </a-form-item>
        <a-form-item label="到达时间">
          <a-time-picker v-model:value="item.endTime" value-format="HH:mm:ss" format="HH:mm:ss" placeholder="自动填充" style="width: 100%" />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script>
import { defineComponent, ref, onMounted, computed } from 'vue';
import axios from 'axios';
import { notification } from 'ant-design-vue';
import { PlusOutlined, ReloadOutlined } from '@ant-design/icons-vue';
import cache from '@/utils/cache';

export default defineComponent({
  name: "daily-train-view",
  components: { PlusOutlined, ReloadOutlined },
  setup() {
    const visible = ref(false), loading = ref(false), isEdit = ref(false);
    const dailyTrains = ref([]);
    const pagination = ref({ total: 0, current: 1, pageSize: 10 });
    const item = ref({ date: '', code: '', type: '', start: '', startPinyin: '', startTime: '', end: '', endPinyin: '', endTime: '' });
    const selectedRowKeys = ref([]);
    const trainCodeList = ref([]);
    const searchCode = ref();
    const searchDate = ref();

    const onSelectChange = (keys) => { selectedRowKeys.value = keys; };

    const columns = [
      { title: '日期', dataIndex: 'date', key: 'date' },
      { title: '车次编号', dataIndex: 'code', key: 'code' },
      { title: '类型', dataIndex: 'type', key: 'type', align: 'center', width: 60 },
      { title: '始发站', dataIndex: 'start', key: 'start' },
      { title: '发车时间', dataIndex: 'startTime', key: 'startTime', align: 'center' },
      { title: '终点站', dataIndex: 'end', key: 'end' },
      { title: '到达时间', dataIndex: 'endTime', key: 'endTime', align: 'center' },
      { title: '操作', dataIndex: 'operation', key: 'operation', align: 'center', width: 140 },
    ];

    const modalTitle = computed(() => isEdit.value ? '编辑每日车次' : '新增每日车次');

    const rowSelection = computed(() => ({ selectedRowKeys: selectedRowKeys.value, onChange: onSelectChange }));
    const tablePagination = computed(() => ({ ...pagination.value, showSizeChanger: true, showQuickJumper: true, showTotal: (t) => `共 ${t} 条`, pageSizeOptions: ['5', '10', '20'] }));

    const handleQuery = (param) => {
      if (!param) { param = { page: 1, size: pagination.value.pageSize }; searchCode.value = undefined; searchDate.value = undefined; }
      loading.value = true;
      axios.get('/business/admin/daily-train/query-list', {
        params: { page: param.page, size: param.size, trainCode: searchCode.value, startTime: searchDate.value }
      }).then((response) => {
        let data = response.data;
        if (data.success) {
          dailyTrains.value = data.content.list;
          pagination.value.current = param.page;
          pagination.value.total = data.content.total;
        } else { notification.error({ description: data.message }); }
      }).finally(() => { loading.value = false; });
    };

    const handleTableChange = (page) => { pagination.value.pageSize = page.pageSize; handleQuery({ page: page.current, size: page.pageSize }); };

    const onSearch = (val) => { searchCode.value = val; handleQuery({ page: 1, size: pagination.value.pageSize }); };
    const onSearchDate = () => { handleQuery({ page: 1, size: pagination.value.pageSize }); };

    const fillFromTrain = (code) => {
      if (!code) return;
      const train = trainCodeList.value.find(t => t.code === code);
      if (train) {
        item.value.type = train.type;
        item.value.start = train.start;
        item.value.startPinyin = train.startPinyin;
        item.value.startTime = train.startTime;
        item.value.end = train.end;
        item.value.endPinyin = train.endPinyin;
        item.value.endTime = train.endTime;
      }
    };

    const onCodeChange = (code) => { item.value.code = code; fillFromTrain(code); };

    const onAdd = () => {
      isEdit.value = false;
      item.value = { date: '', code: '', type: '', start: '', startPinyin: '', startTime: '', end: '', endPinyin: '', endTime: '' };
      visible.value = true;
    };

    const onEdit = (record) => {
      isEdit.value = true;
      item.value = {
        id: record.id, date: record.date, code: record.code, type: record.type,
        start: record.start, startPinyin: record.startPinyin, startTime: record.startTime,
        end: record.end, endPinyin: record.endPinyin, endTime: record.endTime,
      };
      visible.value = true;
    };

    const handleOk = () => {
      const url = isEdit.value ? '/business/admin/daily-train/update' : '/business/admin/daily-train/save';
      axios.post(url, item.value).then((response) => {
        let data = response.data;
        if (data.success) { notification.success({ description: isEdit.value ? '修改成功！' : '保存成功！' }); visible.value = false; handleQuery(); }
        else { notification.error({ description: data.message }); }
      });
    };

    const onDelete = (record) => {
      axios.delete('/business/admin/daily-train/delete/' + record.id).then((response) => {
        let data = response.data;
        if (data.success) { notification.success({ description: '删除成功！' }); handleQuery({ page: pagination.value.current, size: pagination.value.pageSize }); }
        else { notification.error({ description: data.message }); }
      });
    };

    const onBatchDelete = () => {
      const ids = selectedRowKeys.value.join(',');
      axios.delete('/business/admin/daily-train/delete/' + ids).then((response) => {
        let data = response.data;
        if (data.success) { notification.success({ description: `已删除 ${selectedRowKeys.value.length} 条` }); selectedRowKeys.value = []; handleQuery(); }
        else { notification.error({ description: data.message }); }
      });
    };

    onMounted(() => {
      handleQuery({ page: 1, size: pagination.value.pageSize });
      const cached = cache.get('train_all'); if (cached) { trainCodeList.value = cached; }
      axios.get('/business/admin/train/query-all').then(res => { if (res.data.success) { trainCodeList.value = res.data.content; cache.set('train_all', res.data.content); } });
    });

    return {
      dailyTrains, columns, pagination, tablePagination, item, visible, loading, isEdit, modalTitle,
      selectedRowKeys, rowSelection, trainCodeList, searchCode, searchDate,
      onAdd, onEdit, handleOk, onDelete, onBatchDelete, onSelectChange, onCodeChange,
      handleQuery, handleTableChange, onSearch, onSearchDate,
    };
  },
});
</script>

<style scoped>
.dt-page { width: 100%; }
.dt-toolbar { display: flex; align-items: center; justify-content: space-between; margin-bottom: 16px; }
.total-tip { color: #8c8c8c; font-size: 13px; }
:deep(.ant-table-pagination) { justify-content: center !important; }
</style>
