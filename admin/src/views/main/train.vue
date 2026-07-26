<template>
  <div class="train-page">
    <div class="train-toolbar">
      <div class="toolbar-left">
        <a-button type="primary" @click="onAdd">
          <plus-outlined /> 新增
        </a-button>
        <a-button @click="handleQuery()" style="margin-left: 8px">
          <reload-outlined /> 刷新
        </a-button>
        <a-date-picker v-model:value="genDate" value-format="YYYY-MM-DD" placeholder="选择日期" style="margin-left: 8px; width: 140px" />
        <a-popconfirm
            title="确认生成该日期的每日车次？"
            @confirm="onGenDaily"
            ok-text="确认" cancel-text="取消"
        >
          <a-button style="margin-left: 8px">
            <schedule-outlined /> 生成每日车次
          </a-button>
        </a-popconfirm>
        <a-popconfirm
            v-show="selectedRowKeys.length > 0"
            title="确认删除选中的车次？"
            @confirm="onBatchDelete"
            ok-text="确认" cancel-text="取消"
        >
          <a-button danger style="margin-left: 8px">
            <delete-outlined /> 删除选中 ({{ selectedRowKeys.length }})
          </a-button>
        </a-popconfirm>
      </div>
      <span class="total-tip">共 {{ pagination.total }} 个车次</span>
    </div>

    <a-table
        :dataSource="trains"
        :columns="columns"
        :pagination="tablePagination"
        @change="handleTableChange"
        :loading="loading"
        rowKey="id"
        size="middle"
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
            <a-popconfirm
                title="确认生成该车次的所有座位？"
                @confirm="onGenSeat(record)"
                ok-text="确认" cancel-text="取消"
            >
              <a-button type="link" size="small">开始售票</a-button>
            </a-popconfirm>
            <a-popconfirm
                title="确认删除该车次？"
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
      <a-form :model="train" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }">
        <a-form-item label="车次编号">
          <a-input v-model:value="train.code" placeholder="请输入车次编号，如 G101" />
        </a-form-item>
        <a-form-item label="列车类型">
          <a-select v-model:value="train.type" placeholder="请选择列车类型">
            <a-select-option v-for="t in TRAIN_TYPES" :key="t.code" :value="t.code">
              {{ t.desc }}
            </a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="始发站">
          <a-select v-model:value="train.start" placeholder="请选择始发站" show-search>
            <a-select-option v-for="s in stationList" :key="s.name" :value="s.name">
              {{ s.name }}（{{ s.namePinyin }}）
            </a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="始发站拼音">
          <a-input v-model:value="train.startPinyin" placeholder="自动生成" disabled />
        </a-form-item>
        <a-form-item label="发车时间">
          <a-time-picker v-model:value="train.startTime" value-format="HH:mm:ss" format="HH:mm:ss" placeholder="请选择发车时间" style="width: 100%" />
        </a-form-item>
        <a-form-item label="终点站">
          <a-select v-model:value="train.end" placeholder="请选择终点站" show-search>
            <a-select-option v-for="s in stationList" :key="s.name" :value="s.name">
              {{ s.name }}（{{ s.namePinyin }}）
            </a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="终点站拼音">
          <a-input v-model:value="train.endPinyin" placeholder="自动生成" disabled />
        </a-form-item>
        <a-form-item label="到达时间">
          <a-time-picker v-model:value="train.endTime" value-format="HH:mm:ss" format="HH:mm:ss" placeholder="请选择到达时间" style="width: 100%" />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script>
import { defineComponent, ref, onMounted, computed, watch } from 'vue';
import axios from 'axios';
import { notification } from 'ant-design-vue';
import { PlusOutlined, ReloadOutlined, DeleteOutlined, ScheduleOutlined } from '@ant-design/icons-vue';
import { pinyin } from 'pinyin-pro';

export default defineComponent({
  name: "train-view",
  components: { PlusOutlined, ReloadOutlined, DeleteOutlined, ScheduleOutlined },
  setup() {
    const TRAIN_TYPES = [
      { code: 'G', desc: '高铁' },
      { code: 'D', desc: '动车' },
      { code: 'K', desc: '快速' },
    ];

    const visible = ref(false);
    const loading = ref(false);
    const isEdit = ref(false);
    const trains = ref([]);
    const pagination = ref({ total: 0, current: 1, pageSize: 10 });
    const train = ref({ code: '', type: 'G', start: '', startPinyin: '', startTime: '', end: '', endPinyin: '', endTime: '' });
    const stationList = ref([]);

    const fillPinyin = (name) => pinyin(name, { toneType: 'none' }).replace(/\s+/g, '');

    watch(() => train.value.start, (name) => { if (name) train.value.startPinyin = fillPinyin(name); });
    watch(() => train.value.end,   (name) => { if (name) train.value.endPinyin   = fillPinyin(name); });
    const selectedRowKeys = ref([]);
    const genDate = ref();

    const onGenDaily = () => {
      if (!genDate.value) { notification.warning({ description: '请先选择日期' }); return; }
      axios.get('/business/admin/daily-train/gen-daily/' + genDate.value).then((response) => {
        let data = response.data;
        if (data.success) { notification.success({ description: '每日车次生成成功！' }); genDate.value = undefined; }
        else { notification.error({ description: data.message }); }
      });
    };

    const onSelectChange = (keys) => {
      selectedRowKeys.value = keys;
    };

    const columns = [
      { title: '车次编号', dataIndex: 'code', key: 'code' },
      { title: '列车类型', dataIndex: 'type', key: 'type', align: 'center' },
      { title: '始发站', dataIndex: 'start', key: 'start' },
      { title: '发车时间', dataIndex: 'startTime', key: 'startTime', align: 'center' },
      { title: '终点站', dataIndex: 'end', key: 'end' },
      { title: '到达时间', dataIndex: 'endTime', key: 'endTime', align: 'center' },
      { title: '操作', dataIndex: 'operation', key: 'operation', align: 'center', width: 140 },
    ];

    const modalTitle = computed(() => isEdit.value ? '编辑车次' : '新增车次');

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
      }
      loading.value = true;
      axios.get('/business/admin/train/query-list', {
        params: { page: param.page, size: param.size }
      }).then((response) => {
        let data = response.data;
        if (data.success) {
          trains.value = data.content.list;
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
      train.value = { code: '', type: 'G', start: '', startPinyin: '', startTime: '', end: '', endPinyin: '', endTime: '' };
      visible.value = true;
    };

    const onEdit = (record) => {
      isEdit.value = true;
      train.value = {
        id: record.id, code: record.code, type: record.type,
        start: record.start, startPinyin: record.startPinyin, startTime: record.startTime,
        end: record.end, endPinyin: record.endPinyin, endTime: record.endTime,
      };
      visible.value = true;
    };

    const handleOk = () => {
      const url = isEdit.value ? '/business/admin/train/update' : '/business/admin/train/save';
      axios.post(url, train.value).then((response) => {
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

    const onGenSeat = (record) => {
      axios.post('/business/admin/train/gen-seat/' + record.code).then((response) => {
        let data = response.data;
        if (data.success) {
          notification.success({ description: '座位生成成功！' });
        } else {
          notification.error({ description: data.message });
        }
      });
    };

    const onDelete = (record) => {
      axios.delete('/business/admin/train/delete/' + record.id).then((response) => {
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
      axios.delete('/business/admin/train/delete/' + ids).then((response) => {
        let data = response.data;
        if (data.success) {
          notification.success({ description: `已删除 ${selectedRowKeys.value.length} 个车次` });
          selectedRowKeys.value = [];
          handleQuery();
        } else {
          notification.error({ description: data.message });
        }
      });
    };

    onMounted(() => {
      handleQuery({ page: 1, size: pagination.value.pageSize });
      axios.get('/business/admin/station/query-all').then((res) => {
        if (res.data.success) stationList.value = res.data.content;
      });
    });

    return {
      TRAIN_TYPES, trains, columns, pagination, tablePagination,
      train, visible, loading, isEdit, modalTitle, selectedRowKeys, rowSelection,
      stationList,
      onAdd, onEdit, handleOk, onDelete, onBatchDelete, onGenSeat, onGenDaily, onSelectChange,
      genDate,
      handleQuery, handleTableChange,
    };
  },
});
</script>

<style scoped>
.train-page {
  width: 100%;
}
.train-toolbar {
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
