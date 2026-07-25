<template>
  <a-layout-sider class="sider" width="200">
    <div class="sider-title">后台管理系统</div>
    <a-menu v-model:selectedKeys="selectedKeys" mode="inline">
      <a-menu-item key="/welcome">
        <router-link to="/welcome">
          <home-outlined /> 首页
        </router-link>
      </a-menu-item>
      <a-sub-menu key="basic">
        <template #title>
          <span><appstore-outlined /> 基础数据</span>
        </template>
        <a-menu-item key="/station">
          <router-link to="/station">站点管理</router-link>
        </a-menu-item>
        <a-menu-item key="/train">
          <router-link to="/train">车次管理</router-link>
        </a-menu-item>
        <a-menu-item key="/carriage">
          <router-link to="/carriage">车厢管理</router-link>
        </a-menu-item>
        <a-menu-item key="/train-station">
          <router-link to="/train-station">车站管理</router-link>
        </a-menu-item>
        <a-menu-item key="/train-seat">
          <router-link to="/train-seat">座位管理</router-link>
        </a-menu-item>
      </a-sub-menu>
      <a-sub-menu key="business">
        <template #title>
          <span><shopping-outlined /> 业务数据</span>
        </template>
        <a-menu-item key="/daily-train">
          <router-link to="/daily-train">每日车次</router-link>
        </a-menu-item>
        <a-menu-item key="/daily-train-station">
          <router-link to="/daily-train-station">每日车站</router-link>
        </a-menu-item>
        <a-menu-item key="/daily-train-carriage">
          <router-link to="/daily-train-carriage">每日车厢</router-link>
        </a-menu-item>
      </a-sub-menu>
      <a-sub-menu key="batch">
        <template #title>
          <span><schedule-outlined /> 跑批管理</span>
        </template>
        <a-menu-item key="/batch">
          <router-link to="/batch">定时任务</router-link>
        </a-menu-item>
      </a-sub-menu>
    </a-menu>
  </a-layout-sider>
</template>

<script>
import { defineComponent, ref, watch } from 'vue';
import router from "@/router";

export default defineComponent({
  name: "the-sider-view",
  setup() {
    const selectedKeys = ref([]);
    watch(() => router.currentRoute.value.path, (newValue) => {
      selectedKeys.value = [newValue];
    }, { immediate: true });
    return { selectedKeys };
  },
});
</script>

<style scoped>
.sider {
  background: #fff;
  border-right: 1px solid #f0f0f0;
}
.sider-title {
  height: 64px;
  line-height: 64px;
  text-align: center;
  font-size: 16px;
  font-weight: bold;
  color: #001529;
  border-bottom: 1px solid #f0f0f0;
  overflow: hidden;
  white-space: nowrap;
}
</style>
