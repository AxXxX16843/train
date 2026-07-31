<template>
  <div class="passenger-page">
    <div class="passenger-toolbar">
      <div class="toolbar-left">
        <a-button type="primary" @click="onAdd">
          <plus-outlined /> 新增
        </a-button>
        <a-button @click="handleQuery()" style="margin-left: 8px">
          <reload-outlined /> 刷新
        </a-button>
        <a-popconfirm
            v-show="selectedRowKeys.length > 0"
            title="确认删除选中的乘车人？"
            @confirm="onBatchDelete"
            ok-text="确认" cancel-text="取消"
        >
          <a-button danger style="margin-left: 8px">
            <delete-outlined /> 删除选中 ({{ selectedRowKeys.length }})
          </a-button>
        </a-popconfirm>
      </div>
      <span class="total-tip">共 {{ pagination.total }} 位乘车人</span>
    </div>

    <a-table
        :dataSource="passengers"
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
          <a-tag v-if="record.type === '1'" color="blue">成人</a-tag>
          <a-tag v-else-if="record.type === '2'" color="orange">儿童</a-tag>
          <a-tag v-else-if="record.type === '3'" color="purple">学生</a-tag>
        </template>
        <template v-else-if="column.dataIndex === 'operation'">
          <a-space>
            <a-button type="link" size="small" @click="onEdit(record)">编辑</a-button>
            <a-popconfirm
                title="确认删除该乘车人？"
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
      <a-form :model="passenger" :label-col="{ span: 4 }" :wrapper-col="{ span: 20 }">
        <a-form-item label="姓名">
          <a-input v-model:value="passenger.name" placeholder="请输入姓名" />
        </a-form-item>
        <a-form-item label="身份证">
          <a-input v-model:value="passenger.idCard" placeholder="请输入身份证号" />
        </a-form-item>
        <a-form-item label="旅客类型">
          <a-select v-model:value="passenger.type" placeholder="请选择旅客类型">
            <a-select-option v-for="item in PASSENGER_TYPES" :key="item.code" :value="item.code">
              {{ item.desc }}
            </a-select-option>
          </a-select>
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script>
import { defineComponent, ref, onMounted, computed } from 'vue';
import axios from 'axios';
import { notification } from 'ant-design-vue';
import { PlusOutlined, ReloadOutlined, DeleteOutlined } from '@ant-design/icons-vue';

export default defineComponent({
  name: "passenger-view",
  components: { PlusOutlined, ReloadOutlined, DeleteOutlined },
  setup() {
    const PASSENGER_TYPES = [
      { code: '1', desc: '成人' },
      { code: '2', desc: '儿童' },
      { code: '3', desc: '学生' },
    ];

    const visible = ref(false);
    const loading = ref(false);
    const isEdit = ref(false);
    const passengers = ref([]);
    const pagination = ref({ total: 0, current: 1, pageSize: 10 });
    const passenger = ref({ name: '', idCard: '', type: '1' });
    const selectedRowKeys = ref([]);

    const onSelectChange = (keys) => {
      selectedRowKeys.value = keys;
    };

    const columns = [
      { title: '姓名', dataIndex: 'name', key: 'name' },
      { title: '身份证', dataIndex: 'idCard', key: 'idCard' },
      { title: '旅客类型', dataIndex: 'type', key: 'type', align: 'center' },
      { title: '操作', dataIndex: 'operation', key: 'operation', align: 'center', width: 140 },
    ];

    const modalTitle = computed(() => isEdit.value ? '编辑乘车人' : '新增乘车人');

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
      axios.get('/member/passenger/query-list', {
        params: { page: param.page, size: param.size }
      }).then((response) => {
        let data = response.data;
        if (data.success) {
          passengers.value = data.content.list;
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
      passenger.value = { name: '', idCard: '', type: '1' };
      visible.value = true;
    };

    const onEdit = (record) => {
      isEdit.value = true;
      passenger.value = {
        id: record.id,
        name: record.name,
        idCard: record.idCard,
        type: record.type,
      };
      visible.value = true;
    };

    const handleOk = () => {
      const url = isEdit.value ? '/member/passenger/update' : '/member/passenger/save';
      axios.post(url, passenger.value).then((response) => {
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
      axios.delete('/member/passenger/delete/' + record.id).then((response) => {
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
      axios.delete('/member/passenger/delete/' + ids).then((response) => {
        let data = response.data;
        if (data.success) {
          notification.success({ description: `已删除 ${selectedRowKeys.value.length} 位乘车人` });
          selectedRowKeys.value = [];
          handleQuery();
        } else {
          notification.error({ description: data.message });
        }
      });
    };

    onMounted(() => {
      handleQuery({ page: 1, size: pagination.value.pageSize });
    });

    return {
      PASSENGER_TYPES, passengers, columns, pagination, tablePagination,
      passenger, visible, loading, isEdit, modalTitle, selectedRowKeys, rowSelection,
      onAdd, onEdit, handleOk, onDelete, onBatchDelete, onSelectChange,
      handleQuery, handleTableChange,
    };
  },
});
</script>

<style scoped>
.passenger-page { width: 100%; }
.passenger-toolbar {
  display: flex; align-items: center; justify-content: space-between;
  padding: 14px 20px; margin-bottom: 16px;
  background: #fafafa; border-radius: 10px; border: 1px solid #f0f0f0;
}
.total-tip { color: #8c8c8c; font-size: 13px; }
:deep(.ant-table) { border-radius: 10px; overflow: hidden; }
:deep(.ant-table-pagination) { justify-content: center !important; padding: 16px 0; }
:deep(.ant-btn-primary) { border-radius: 6px; }
:deep(.ant-modal-content) { border-radius: 14px; }
</style>
