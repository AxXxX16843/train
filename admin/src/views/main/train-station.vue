<template>
  <div class="ts-page">
    <div class="ts-toolbar">
      <div class="toolbar-left">
        <a-button type="primary" @click="onAdd">
          <plus-outlined /> 新增
        </a-button>
        <a-button @click="handleQuery()" style="margin-left: 8px">
          <reload-outlined /> 刷新
        </a-button>
        <a-select v-model:value="searchCode" placeholder="按车次筛选" allowClear show-search style="width: 240px; margin-left: 8px" @change="(val) => { searchCode = val; handleQuery({}); }">
          <a-select-option v-for="t in trainCodeList" :key="t.code" :value="t.code">
            {{ t.code }}（{{ t.start }} → {{ t.end }}）
          </a-select-option>
        </a-select>
        <a-popconfirm
            v-show="selectedRowKeys.length > 0"
            title="确认删除选中的车站？"
            @confirm="onBatchDelete"
            ok-text="确认" cancel-text="取消"
        >
          <a-button danger style="margin-left: 8px">
            <delete-outlined /> 删除选中 ({{ selectedRowKeys.length }})
          </a-button>
        </a-popconfirm>
      </div>
      <span class="total-tip">共 {{ pagination.total }} 个车站</span>
    </div>

    <a-table
        :dataSource="trainStations"
        :columns="columns"
        :pagination="tablePagination"
        @change="handleTableChange"
        :loading="loading"
        rowKey="id"
        size="middle"
        :row-selection="rowSelection"
    >
      <template #bodyCell="{ column, record }">
        <template v-if="column.dataIndex === 'operation'">
          <a-space>
            <a-button type="link" size="small" @click="onEdit(record)">编辑</a-button>
            <a-popconfirm
                title="确认删除该车站？"
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
             ok-text="确认" cancel-text="取消" :destroyOnClose="true" width="560px">
      <a-form :model="item" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }">
        <a-form-item label="车次编号">
          <a-select v-model:value="item.trainCode" placeholder="请选择车次编号" show-search>
            <a-select-option v-for="t in trainCodeList" :key="t.code" :value="t.code">
              {{ t.code }}（{{ t.start }} → {{ t.end }}）
            </a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="站序">
          <a-input-number v-model:value="item.index" :min="1" placeholder="请输入站序" style="width: 100%" />
        </a-form-item>
        <a-form-item label="站名">
          <a-input v-model:value="item.name" placeholder="请输入站名" />
        </a-form-item>
        <a-form-item label="站名拼音">
          <a-input v-model:value="item.namePinyin" placeholder="自动生成" disabled />
        </a-form-item>
        <a-form-item label="进站时间">
          <a-time-picker v-model:value="item.inTime" value-format="HH:mm:ss" format="HH:mm:ss" placeholder="请选择进站时间" style="width: 100%" />
        </a-form-item>
        <a-form-item label="出站时间">
          <a-time-picker v-model:value="item.outTime" value-format="HH:mm:ss" format="HH:mm:ss" placeholder="请选择出站时间" style="width: 100%" />
        </a-form-item>
        <a-form-item label="停留时间">
          <a-time-picker v-model:value="item.stopTime" value-format="HH:mm:ss" format="HH:mm:ss" placeholder="请选择停留时间" style="width: 100%" />
        </a-form-item>
        <a-form-item label="里程(km)">
          <a-input-number v-model:value="item.km" :min="0" :step="0.1" placeholder="请输入里程" style="width: 100%" />
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
import { pinyin } from 'pinyin-pro';

export default defineComponent({
  name: "train-station-view",
  components: { PlusOutlined, ReloadOutlined, DeleteOutlined },
  setup() {
    const visible = ref(false);
    const loading = ref(false);
    const isEdit = ref(false);
    const trainStations = ref([]);
    const pagination = ref({ total: 0, current: 1, pageSize: 10 });
    const item = ref({ trainCode: '', index: 1, name: '', namePinyin: '', inTime: '', outTime: '', stopTime: '', km: 0 });
    const selectedRowKeys = ref([]);
    const trainCodeList = ref([]);
    const searchCode = ref();

    const onSelectChange = (keys) => {
      selectedRowKeys.value = keys;
    };

    watch(() => item.value.name, (name) => {
      if (name) item.value.namePinyin = pinyin(name, { toneType: 'none' }).replace(/\s+/g, '');
    });

    const columns = [
      { title: '车次编号', dataIndex: 'trainCode', key: 'trainCode' },
      { title: '站序', dataIndex: 'index', key: 'index', align: 'center' },
      { title: '站名', dataIndex: 'name', key: 'name' },
      { title: '里程(km)', dataIndex: 'km', key: 'km', align: 'center' },
      { title: '操作', dataIndex: 'operation', key: 'operation', align: 'center', width: 140 },
    ];

    const modalTitle = computed(() => isEdit.value ? '编辑车站' : '新增车站');

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
      axios.get('/business/admin/train-station/query-list', {
        params: { page: param.page, size: param.size, trainCode: searchCode.value }
      }).then((response) => {
        let data = response.data;
        if (data.success) {
          trainStations.value = data.content.list;
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
      item.value = { trainCode: '', index: 1, name: '', namePinyin: '', inTime: '', outTime: '', stopTime: '', km: 0 };
      visible.value = true;
    };

    const onEdit = (record) => {
      isEdit.value = true;
      item.value = {
        id: record.id, trainCode: record.trainCode, index: record.index,
        name: record.name, namePinyin: record.namePinyin,
        inTime: record.inTime, outTime: record.outTime, stopTime: record.stopTime,
        km: record.km,
      };
      visible.value = true;
    };

    const handleOk = () => {
      const url = isEdit.value ? '/business/admin/train-station/update' : '/business/admin/train-station/save';
      axios.post(url, item.value).then((response) => {
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
      axios.delete('/business/admin/train-station/delete/' + record.id).then((response) => {
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
      axios.delete('/business/admin/train-station/delete/' + ids).then((response) => {
        let data = response.data;
        if (data.success) {
          notification.success({ description: `已删除 ${selectedRowKeys.value.length} 个车站` });
          selectedRowKeys.value = [];
          handleQuery();
        } else {
          notification.error({ description: data.message });
        }
      });
    };

    onMounted(() => {
      handleQuery({ page: 1, size: pagination.value.pageSize });
      axios.get('/business/admin/train/query-all').then((res) => {
        if (res.data.success) trainCodeList.value = res.data.content;
      });
    });

    return {
      trainStations, columns, pagination, tablePagination,
      item, visible, loading, isEdit, modalTitle, selectedRowKeys, rowSelection,
      trainCodeList, searchCode,
      onAdd, onEdit, handleOk, onDelete, onBatchDelete, onSelectChange,
      handleQuery, handleTableChange,
    };
  },
});
</script>

<style scoped>
.ts-page {
  width: 100%;
}
.ts-toolbar {
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
