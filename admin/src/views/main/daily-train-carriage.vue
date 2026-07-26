<template>
  <div class="dtc-page">
    <div class="dtc-toolbar">
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
        <template v-if="column.dataIndex === 'seatType'">
          <a-tag v-if="record.seatType === '1'" color="gold">一等座</a-tag>
          <a-tag v-else-if="record.seatType === '2'" color="blue">二等座</a-tag>
          <a-tag v-else-if="record.seatType === '3'" color="green">软卧</a-tag>
          <a-tag v-else-if="record.seatType === '4'" color="orange">硬卧</a-tag>
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
          <a-select v-model:value="item.trainCode" placeholder="请选择车次编号" show-search @change="onTrainChange">
            <a-select-option v-for="t in trainCodeList" :key="t.code" :value="t.code">
              {{ t.code }}（{{ t.start }} → {{ t.end }}）
            </a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="厢序">
          <a-select v-model:value="item.index" placeholder="请选择车厢" @change="onIndexChange">
            <a-select-option v-for="c in carriageList" :key="c.index" :value="c.index">
              第{{ c.index }}节（{{ SEAT_LABEL[c.seatType] }}）
            </a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="座位类型">
          <a-input :value="SEAT_LABEL[item.seatType]" placeholder="自动填充" disabled />
        </a-form-item>
        <a-form-item label="排数">
          <a-input :value="item.rowCount" placeholder="自动填充" disabled />
        </a-form-item>
        <a-form-item label="列数">
          <a-input :value="item.colCount" placeholder="自动填充" disabled />
        </a-form-item>
        <a-form-item label="座位数">
          <a-input :value="item.seatCount" placeholder="自动填充" disabled />
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

export default defineComponent({
  name: "daily-train-carriage-view",
  components: { PlusOutlined, ReloadOutlined },
  setup() {
    const SEAT_LABEL = { '1': '一等座', '2': '二等座', '3': '软卧', '4': '硬卧' };
    const visible = ref(false), loading = ref(false), isEdit = ref(false);
    const list = ref([]);
    const pagination = ref({ total: 0, current: 1, pageSize: 10 });
    const item = ref({ date: '', trainCode: '', index: null, seatType: '', seatCount: 0, rowCount: 0, colCount: 0 });
    const selectedRowKeys = ref([]);
    const trainCodeList = ref([]);
    const carriageList = ref([]);
    const searchCode = ref();
    const searchDate = ref();

    const onSelectChange = (keys) => { selectedRowKeys.value = keys; };

    const columns = [
      { title: '日期', dataIndex: 'date', key: 'date' },
      { title: '车次编号', dataIndex: 'trainCode', key: 'trainCode' },
      { title: '厢序', dataIndex: 'index', key: 'index', align: 'center', width: 60 },
      { title: '座位类型', dataIndex: 'seatType', key: 'seatType', align: 'center' },
      { title: '排数', dataIndex: 'rowCount', key: 'rowCount', align: 'center', width: 60 },
      { title: '列数', dataIndex: 'colCount', key: 'colCount', align: 'center', width: 60 },
      { title: '座位数', dataIndex: 'seatCount', key: 'seatCount', align: 'center', width: 60 },
      { title: '操作', dataIndex: 'operation', key: 'operation', align: 'center', width: 140 },
    ];

    const modalTitle = computed(() => isEdit.value ? '编辑每日车厢' : '新增每日车厢');
    const rowSelection = computed(() => ({ selectedRowKeys: selectedRowKeys.value, onChange: onSelectChange }));
    const tablePagination = computed(() => ({ ...pagination.value, showSizeChanger: true, showQuickJumper: true, showTotal: (t) => `共 ${t} 条`, pageSizeOptions: ['5', '10', '20'] }));

    const handleQuery = (param) => {
      if (!param) { param = { page: 1, size: pagination.value.pageSize }; searchCode.value = undefined; searchDate.value = undefined; }
      loading.value = true;
      axios.get('/business/admin/daily-train-carriage/query-list', {
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
      item.value.seatType = ''; item.value.seatCount = 0; item.value.rowCount = 0; item.value.colCount = 0;
      if (code) {
        axios.get('/business/admin/train-carriage/query-list', { params: { trainCode: code, page: 1, size: 100 } }).then(res => {
          if (res.data.success) carriageList.value = res.data.content.list || [];
        });
      } else {
        carriageList.value = [];
      }
    };

    const onIndexChange = (idx) => {
      const c = carriageList.value.find(c => c.index === idx);
      if (c) {
        item.value.seatType = c.seatType;
        item.value.rowCount = c.rowCount;
        item.value.colCount = c.colCount;
        item.value.seatCount = c.seatCount;
      }
    };

    const onAdd = () => {
      isEdit.value = false;
      item.value = { date: '', trainCode: '', index: null, seatType: '', seatCount: 0, rowCount: 0, colCount: 0 };
      carriageList.value = [];
      visible.value = true;
    };

    const onEdit = (record) => {
      isEdit.value = true;
      item.value = {
        id: record.id, date: record.date, trainCode: record.trainCode, index: record.index,
        seatType: record.seatType, seatCount: record.seatCount, rowCount: record.rowCount, colCount: record.colCount,
      };
      visible.value = true;
    };

    const handleOk = () => {
      const url = isEdit.value ? '/business/admin/daily-train-carriage/update' : '/business/admin/daily-train-carriage/save';
      axios.post(url, item.value).then((response) => {
        let data = response.data;
        if (data.success) { notification.success({ description: isEdit.value ? '修改成功！' : '保存成功！' }); visible.value = false; handleQuery(); }
        else { notification.error({ description: data.message }); }
      });
    };

    const onDelete = (record) => {
      axios.delete('/business/admin/daily-train-carriage/delete/' + record.id).then((response) => {
        let data = response.data;
        if (data.success) { notification.success({ description: '删除成功！' }); handleQuery({ page: pagination.value.current, size: pagination.value.pageSize }); }
        else { notification.error({ description: data.message }); }
      });
    };

    const onBatchDelete = () => {
      const ids = selectedRowKeys.value.join(',');
      axios.delete('/business/admin/daily-train-carriage/delete/' + ids).then((response) => {
        let data = response.data;
        if (data.success) { notification.success({ description: `已删除 ${selectedRowKeys.value.length} 条` }); selectedRowKeys.value = []; handleQuery(); }
        else { notification.error({ description: data.message }); }
      });
    };

    onMounted(() => {
      handleQuery({ page: 1, size: pagination.value.pageSize });
      axios.get('/business/admin/train/query-all').then(res => { if (res.data.success) trainCodeList.value = res.data.content; });
    });

    return {
      SEAT_LABEL, list, columns, pagination, tablePagination, item, visible, loading, isEdit, modalTitle,
      selectedRowKeys, rowSelection, trainCodeList, carriageList, searchCode, searchDate,
      onAdd, onEdit, handleOk, onDelete, onBatchDelete, onSelectChange, onTrainChange, onIndexChange,
      handleQuery, handleTableChange, onSearch, onSearchDate,
    };
  },
});
</script>

<style scoped>
.dtc-page { width: 100%; }
.dtc-toolbar { display: flex; align-items: center; justify-content: space-between; margin-bottom: 16px; }
.total-tip { color: #8c8c8c; font-size: 13px; }
:deep(.ant-table-pagination) { justify-content: center !important; }
</style>
