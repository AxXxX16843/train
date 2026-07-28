<template>
  <div class="dts-page">
    <div class="dts-toolbar">
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
        :dataSource="list" :columns="columns" :pagination="tablePagination"
        @change="handleTableChange" :loading="loading" rowKey="id" size="middle"
        :row-selection="rowSelection"
    >
      <template #bodyCell="{ column, record }">
        <template v-if="column.dataIndex === 'operation'">
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
          <a-select v-model:value="item.trainCode" placeholder="请选择车次编号" show-search @change="onTrainChange">
            <a-select-option v-for="t in trainCodeList" :key="t.code" :value="t.code">
              {{ t.code }}（{{ t.start }} → {{ t.end }}）
            </a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="站序">
          <a-select v-model:value="item.index" placeholder="请选择站序" @change="onIndexChange">
            <a-select-option v-for="s in stationList" :key="s.index" :value="s.index">
              第{{ s.index }}站：{{ s.name }}
            </a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="站名">
          <a-input :value="item.name" placeholder="自动填充" disabled />
        </a-form-item>
        <a-form-item label="站名拼音">
          <a-input :value="item.namePinyin" placeholder="自动填充" disabled />
        </a-form-item>
        <a-form-item label="进站时间">
          <a-input :value="item.inTime" placeholder="自动填充" disabled />
        </a-form-item>
        <a-form-item label="出站时间">
          <a-input :value="item.outTime" placeholder="自动填充" disabled />
        </a-form-item>
        <a-form-item label="里程(km)">
          <a-input :value="item.km" placeholder="自动填充" disabled />
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
  name: "daily-train-station-view",
  components: { PlusOutlined, ReloadOutlined },
  setup() {
    const visible = ref(false), loading = ref(false), isEdit = ref(false);
    const list = ref([]);
    const pagination = ref({ total: 0, current: 1, pageSize: 10 });
    const item = ref({ date: '', trainCode: '', index: null, name: '', namePinyin: '', inTime: '', outTime: '', stopTime: '', km: '' });
    const selectedRowKeys = ref([]);
    const trainCodeList = ref([]);
    const stationList = ref([]);
    const searchCode = ref();
    const searchDate = ref();

    const onSelectChange = (keys) => { selectedRowKeys.value = keys; };

    const columns = [
      { title: '日期', dataIndex: 'date', key: 'date' },
      { title: '车次编号', dataIndex: 'trainCode', key: 'trainCode' },
      { title: '站序', dataIndex: 'index', key: 'index', align: 'center', width: 60 },
      { title: '站名', dataIndex: 'name', key: 'name' },
      { title: '进站时间', dataIndex: 'inTime', key: 'inTime', align: 'center' },
      { title: '出站时间', dataIndex: 'outTime', key: 'outTime', align: 'center' },
      { title: '里程(km)', dataIndex: 'km', key: 'km', align: 'center' },
      { title: '操作', dataIndex: 'operation', key: 'operation', align: 'center', width: 140 },
    ];

    const modalTitle = computed(() => isEdit.value ? '编辑每日车站' : '新增每日车站');
    const rowSelection = computed(() => ({ selectedRowKeys: selectedRowKeys.value, onChange: onSelectChange }));
    const tablePagination = computed(() => ({ ...pagination.value, showSizeChanger: true, showQuickJumper: true, showTotal: (t) => `共 ${t} 条`, pageSizeOptions: ['5', '10', '20'] }));

    const handleQuery = (param) => {
      if (!param) { param = { page: 1, size: pagination.value.pageSize }; searchCode.value = undefined; searchDate.value = undefined; }
      loading.value = true;
      axios.get('/business/admin/daily-train-station/query-list', {
        params: { page: param.page, size: param.size, trainCode: searchCode.value, startTime: searchDate.value }
      }).then((response) => {
        let data = response.data;
        if (data.success) { list.value = data.content.list; pagination.value.current = param.page; pagination.value.total = data.content.total; }
        else { notification.error({ description: data.message }); }
      }).finally(() => { loading.value = false; });
    };

    const handleTableChange = (page) => { pagination.value.pageSize = page.pageSize; handleQuery({ page: page.current, size: page.pageSize }); };
    const onSearch = (val) => { searchCode.value = val; handleQuery({ page: 1, size: pagination.value.pageSize }); };
    const onSearchDate = () => { handleQuery({ page: 1, size: pagination.value.pageSize }); };

    const onTrainChange = (code) => {
      item.value.trainCode = code;
      item.value.index = null;
      item.value.name = ''; item.value.namePinyin = ''; item.value.inTime = ''; item.value.outTime = ''; item.value.km = '';
      if (code) {
        axios.get('/business/admin/train-station/query-list', { params: { trainCode: code, page: 1, size: 100 } }).then(res => {
          if (res.data.success) stationList.value = res.data.content.list || [];
        });
      } else {
        stationList.value = [];
      }
    };

    const onIndexChange = (idx) => {
      const s = stationList.value.find(s => s.index === idx);
      if (s) {
        item.value.name = s.name;
        item.value.namePinyin = s.namePinyin;
        item.value.inTime = s.inTime;
        item.value.outTime = s.outTime;
        item.value.stopTime = s.stopTime;
        item.value.km = s.km;
      }
    };

    const onAdd = () => {
      isEdit.value = false;
      item.value = { date: '', trainCode: '', index: null, name: '', namePinyin: '', inTime: '', outTime: '', stopTime: '', km: '' };
      stationList.value = [];
      visible.value = true;
    };

    const onEdit = (record) => {
      isEdit.value = true;
      item.value = {
        id: record.id, date: record.date, trainCode: record.trainCode, index: record.index,
        name: record.name, namePinyin: record.namePinyin,
        inTime: record.inTime, outTime: record.outTime, stopTime: record.stopTime, km: record.km,
      };
      visible.value = true;
    };

    const handleOk = () => {
      const url = isEdit.value ? '/business/admin/daily-train-station/update' : '/business/admin/daily-train-station/save';
      axios.post(url, item.value).then((response) => {
        let data = response.data;
        if (data.success) { notification.success({ description: isEdit.value ? '修改成功！' : '保存成功！' }); visible.value = false; handleQuery(); }
        else { notification.error({ description: data.message }); }
      });
    };

    const onDelete = (record) => {
      axios.delete('/business/admin/daily-train-station/delete/' + record.id).then((response) => {
        let data = response.data;
        if (data.success) { notification.success({ description: '删除成功！' }); handleQuery({ page: pagination.value.current, size: pagination.value.pageSize }); }
        else { notification.error({ description: data.message }); }
      });
    };

    const onBatchDelete = () => {
      const ids = selectedRowKeys.value.join(',');
      axios.delete('/business/admin/daily-train-station/delete/' + ids).then((response) => {
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
      list, columns, pagination, tablePagination, item, visible, loading, isEdit, modalTitle,
      selectedRowKeys, rowSelection, trainCodeList, stationList, searchCode, searchDate,
      onAdd, onEdit, handleOk, onDelete, onBatchDelete, onSelectChange, onTrainChange, onIndexChange,
      handleQuery, handleTableChange, onSearch, onSearchDate,
    };
  },
});
</script>

<style scoped>
.dts-page { width: 100%; }
.dts-toolbar { display: flex; align-items: center; justify-content: space-between; margin-bottom: 16px; }
.total-tip { color: #8c8c8c; font-size: 13px; }
:deep(.ant-table-pagination) { justify-content: center !important; }
</style>
