<template>
  <a-layout-header class="header">
    <div class="logo">
      <span class="logo-icon">🚂</span>
      <span class="logo-text">12306 指挥部</span>
    </div>
    <a-menu v-model:selectedKeys="selectedKeys" theme="dark" mode="horizontal">
      <a-menu-item key="/welcome">
        <router-link to="/welcome">
          <home-outlined /> 首页
        </router-link>
      </a-menu-item>
    </a-menu>
  </a-layout-header>
</template>

<script>
import { defineComponent, ref, watch } from 'vue';
import router from '@/router';

export default defineComponent({
  name: "the-header-view",
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
.header {
  display: flex; align-items: center; padding: 0 24px 0 0;
  background: linear-gradient(135deg, #001529, #003a70) !important;
}
.header :deep(.ant-menu-dark) { background: transparent; }
.logo {
  width: 200px; display: flex; align-items: center; justify-content: center;
  gap: 8px; font-size: 17px; font-weight: 600; color: #fff; flex-shrink: 0;
}
</style>
