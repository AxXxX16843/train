<template>
  <div class="carriage-page">
    <div class="carriage-toolbar">
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
        <a-popconfirm
            v-show="selectedRowKeys.length > 0"
            title="确认删除选中的车厢？"
            @confirm="onBatchDelete"
            ok-text="确认" cancel-text="取消"
        >
          <a-button danger style="margin-left: 8px">
            <delete-outlined /> 删除选中 ({{ selectedRowKeys.length }})
          </a-button>
        </a-popconfirm>
      </div>
      <span class="total-tip">共 {{ pagination.total }} 节车厢</span>
    </div>

    <a-table
        :dataSource="carriages"
        :columns="columns"
        :pagination="tablePagination"
        @change="handleTableChange"
        :loading="loading"
        rowKey="id"
        size="middle"
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
            <a-popconfirm
                title="确认删除该车厢？"
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
      <a-form :model="carriage" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }">
        <a-form-item label="车次编号">
          <a-select v-model:value="carriage.trainCode" placeholder="请选择车次编号" show-search>
            <a-select-option v-for="t in trainCodeList" :key="t.code" :value="t.code">
              {{ t.code }}（{{ t.start }} → {{ t.end }}）
            </a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="车厢序号">
          <a-input-number v-model:value="carriage.index" :min="1" placeholder="请输入车厢序号" style="width: 100%" />
        </a-form-item>
        <a-form-item label="座位类型">
          <a-select v-model:value="carriage.seatType" placeholder="请选择座位类型">
            <a-select-option v-for="t in SEAT_TYPES" :key="t.code" :value="t.code">
              {{ t.desc }}
            </a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="排数">
          <a-input-number v-model:value="carriage.rowCount" :min="1" placeholder="请输入排数" style="width: 100%" />
        </a-form-item>
        <a-form-item label="列数">
          <a-input-number v-model:value="carriage.colCount" :min="1" placeholder="自动计算" disabled style="width: 100%" />
        </a-form-item>
        <a-form-item label="座位数">
          <a-input-number v-model:value="carriage.seatCount" :min="1" placeholder="自动计算" disabled style="width: 100%" />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script>
import { defineComponent, ref, onMounted, computed, watch } from 'vue';
import axios from 'axios';
import { notification } from 'ant-design-vue';
import { PlusOutlined, ReloadOutlined, DeleteOutlined } from '@ant-design/icons-vue';
import cache from '@/utils/cache';

export default defineComponent({
  name: "carriage-view",
  components: { PlusOutlined, ReloadOutlined, DeleteOutlined },
  setup() {
    const SEAT_TYPES = [
      { code: '1', desc: '一等座' },
      { code: '2', desc: '二等座' },
      { code: '3', desc: '软卧' },
      { code: '4', desc: '硬卧' },
    ];

    const visible = ref(false);
    const loading = ref(false);
    const isEdit = ref(false);
    const carriages = ref([]);
    const pagination = ref({ total: 0, current: 1, pageSize: 10 });
    const carriage = ref({ trainCode: '', index: 1, seatType: '1', seatCount: 0, rowCount: 0, colCount: 0 });
    const selectedRowKeys = ref([]);
    const trainCodeList = ref([]);
    const searchCode = ref();

    const onSearch = (val) => {
      searchCode.value = val;
      handleQuery({ page: 1, size: pagination.value.pageSize });
    };

    const SEAT_COL_MAP = { '1': 4, '2': 5, '3': 4, '4': 4 };

    watch(() => carriage.value.seatType, (type) => {
      carriage.value.colCount = type ? SEAT_COL_MAP[type] || 0 : 0;
    });
    watch([() => carriage.value.rowCount, () => carriage.value.colCount], () => {
      const r = carriage.value.rowCount, c = carriage.value.colCount;
      if (r && c) carriage.value.seatCount = r * c;
    });

    const onSelectChange = (keys) => {
      selectedRowKeys.value = keys;
    };

    const columns = [
      { title: '车次编号', dataIndex: 'trainCode', key: 'trainCode' },
      { title: '车厢序号', dataIndex: 'index', key: 'index', align: 'center' },
      { title: '座位类型', dataIndex: 'seatType', key: 'seatType', align: 'center' },
      { title: '排数', dataIndex: 'rowCount', key: 'rowCount', align: 'center', width: 60 },
      { title: '列数', dataIndex: 'colCount', key: 'colCount', align: 'center', width: 60 },
      { title: '座位数', dataIndex: 'seatCount', key: 'seatCount', align: 'center', width: 60 },
      { title: '操作', dataIndex: 'operation', key: 'operation', align: 'center', width: 140 },
    ];

    const modalTitle = computed(() => isEdit.value ? '编辑车厢' : '新增车厢');

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
        searchCode.value = undefined;
      }
      loading.value = true;
      axios.get('/business/admin/train-carriage/query-list', {
        params: { page: param.page, size: param.size, trainCode: searchCode.value }
      }).then((response) => {
        let data = response.data;
        if (data.success) {
          carriages.value = data.content.list;
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
      carriage.value = { trainCode: '', index: 1, seatType: undefined, seatCount: 0, rowCount: 0, colCount: 0 };
      visible.value = true;
    };

    const onEdit = (record) => {
      isEdit.value = true;
      carriage.value = {
        id: record.id, trainCode: record.trainCode, index: record.index,
        seatType: record.seatType, seatCount: record.seatCount,
        rowCount: record.rowCount, colCount: record.colCount,
      };
      visible.value = true;
    };

    const handleOk = () => {
      const url = isEdit.value ? '/business/admin/train-carriage/update' : '/business/admin/train-carriage/save';
      axios.post(url, carriage.value).then((response) => {
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
      axios.delete('/business/admin/train-carriage/delete/' + record.id).then((response) => {
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
      axios.delete('/business/admin/train-carriage/delete/' + ids).then((response) => {
        let data = response.data;
        if (data.success) {
          notification.success({ description: `已删除 ${selectedRowKeys.value.length} 节车厢` });
          selectedRowKeys.value = [];
          handleQuery();
        } else {
          notification.error({ description: data.message });
        }
      });
    };

    onMounted(() => {
      handleQuery({ page: 1, size: pagination.value.pageSize });
      const cached = cache.get('train_all'); if (cached) { trainCodeList.value = cached; }
      else { axios.get('/business/admin/train/query-all').then((res) => {
        if (res.data.success) { trainCodeList.value = res.data.content; cache.set('train_all', res.data.content); }
      }); }
    });

    return {
      SEAT_TYPES, carriages, columns, pagination, tablePagination,
      carriage, visible, loading, isEdit, modalTitle, selectedRowKeys, rowSelection,
      trainCodeList, searchCode, onSearch,
      onAdd, onEdit, handleOk, onDelete, onBatchDelete, onSelectChange,
      handleQuery, handleTableChange,
    };
  },
});
</script>

<style scoped>
.carriage-page {
  width: 100%;
}
.carriage-toolbar {
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
