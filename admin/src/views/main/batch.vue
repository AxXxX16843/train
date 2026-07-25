<template>
  <div class="batch-page">
    <div class="batch-toolbar">
      <a-space>
        <a-button type="primary" @click="onAdd">
          <plus-outlined /> 新增任务
        </a-button>
        <a-button @click="handleQuery">
          <reload-outlined /> 刷新
        </a-button>
      </a-space>
    </div>

    <a-table :dataSource="jobs" :columns="columns" rowKey="name" size="middle" :loading="loading" :pagination="false">
      <template #bodyCell="{ column, record }">
        <template v-if="column.dataIndex === 'state'">
          <a-tag v-if="record.state === 'NORMAL'" color="green">运行中</a-tag>
          <a-tag v-else-if="record.state === 'PAUSED'" color="orange">已暂停</a-tag>
          <a-tag v-else>{{ record.state }}</a-tag>
        </template>
        <template v-else-if="column.dataIndex === 'operation'">
          <a-space>
            <a-button type="link" size="small" @click="onRun(record)">执行</a-button>
            <a-button v-if="record.state === 'NORMAL'" type="link" size="small" @click="onPause(record)">暂停</a-button>
            <a-button v-else type="link" size="small" @click="onResume(record)">恢复</a-button>
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
      <a-form :model="job" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }">
        <a-form-item label="任务类名">
          <a-input v-model:value="job.name" placeholder="如 com.xzit.train.batch.job.TestJob" />
        </a-form-item>
        <a-form-item label="任务组">
          <a-input v-model:value="job.group" placeholder="如 default" />
        </a-form-item>
        <a-form-item label="Cron 表达式">
          <a-input v-model:value="job.cronExpression" placeholder="如 0/5 * * * * ?" />
        </a-form-item>
        <a-form-item label="描述">
          <a-input v-model:value="job.description" placeholder="请输入任务描述" />
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
  name: "batch-view",
  components: { PlusOutlined, ReloadOutlined },
  setup() {
    const visible = ref(false);
    const loading = ref(false);
    const isEdit = ref(false);
    const jobs = ref([]);
    const job = ref({ name: '', group: 'default', cronExpression: '', description: '' });

    const columns = [
      { title: '任务名称', dataIndex: 'name', key: 'name' },
      { title: '任务组', dataIndex: 'group', key: 'group' },
      { title: 'Cron 表达式', dataIndex: 'cronExpression', key: 'cronExpression' },
      { title: '描述', dataIndex: 'description', key: 'description' },
      { title: '状态', dataIndex: 'state', key: 'state', align: 'center' },
      { title: '上次执行', dataIndex: 'preFireTime', key: 'preFireTime' },
      { title: '下次执行', dataIndex: 'nextFireTime', key: 'nextFireTime' },
      { title: '操作', dataIndex: 'operation', key: 'operation', width: 240 },
    ];

    const modalTitle = computed(() => isEdit.value ? '编辑任务' : '新增任务');

    const handleQuery = () => {
      loading.value = true;
      axios.get('/batch/admin/job/query').then((response) => {
        let data = response.data;
        if (data.success) {
          jobs.value = data.content || [];
        } else {
          notification.error({ description: data.message });
        }
      }).finally(() => {
        loading.value = false;
      });
    };

    const onAdd = () => {
      isEdit.value = false;
      job.value = { name: '', group: 'default', cronExpression: '', description: '' };
      visible.value = true;
    };

    const onEdit = (record) => {
      isEdit.value = true;
      job.value = {
        name: record.name, group: record.group,
        cronExpression: record.cronExpression, description: record.description,
      };
      visible.value = true;
    };

    const handleOk = () => {
      const url = isEdit.value ? '/batch/admin/job/reschedule' : '/batch/admin/job/add';
      axios.post(url, job.value).then((response) => {
        let data = response.data;
        if (data.success) {
          notification.success({ description: isEdit.value ? '修改成功！' : '创建成功！' });
          visible.value = false;
          handleQuery();
        } else {
          notification.error({ description: data.message });
        }
      });
    };

    const onRun = (record) => {
      axios.post('/batch/admin/job/run', { name: record.name, group: record.group }).then((response) => {
        let data = response.data;
        if (data.success) {
          notification.success({ description: '执行成功！' });
        } else {
          notification.error({ description: data.message });
        }
      });
    };

    const onPause = (record) => {
      axios.post('/batch/admin/job/pause', { name: record.name, group: record.group }).then((response) => {
        let data = response.data;
        if (data.success) {
          notification.success({ description: '已暂停！' });
          handleQuery();
        } else {
          notification.error({ description: data.message });
        }
      });
    };

    const onResume = (record) => {
      axios.post('/batch/admin/job/resume', { name: record.name, group: record.group }).then((response) => {
        let data = response.data;
        if (data.success) {
          notification.success({ description: '已恢复！' });
          handleQuery();
        } else {
          notification.error({ description: data.message });
        }
      });
    };

    const onDelete = (record) => {
      axios.post('/batch/admin/job/delete', { name: record.name, group: record.group }).then((response) => {
        let data = response.data;
        if (data.success) {
          notification.success({ description: '删除成功！' });
          handleQuery();
        } else {
          notification.error({ description: data.message });
        }
      });
    };

    onMounted(() => {
      handleQuery();
    });

    return {
      jobs, columns, job, visible, loading, isEdit, modalTitle,
      onAdd, onEdit, handleOk, onRun, onPause, onResume, onDelete, handleQuery,
    };
  },
});
</script>

<style scoped>
.batch-page {
  width: 100%;
}
.batch-toolbar {
  margin-bottom: 16px;
}
</style>
