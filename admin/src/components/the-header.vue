<template>
  <a-layout-header class="header">
    <div class="logo">
      <span class="logo-icon">🚂</span>
      <span>12306 指挥部</span>
    </div>
    <a-menu
        v-model:selectedKeys="selectedKeys"
        theme="dark"
        mode="horizontal"
    >
      <a-menu-item key="/welcome">
        <router-link to="/welcome">
          <home-outlined /> 首页
        </router-link>
      </a-menu-item>
      <a-menu-item key="/station">
        <router-link to="/station">
          <environment-outlined /> 站点管理
        </router-link>
      </a-menu-item>
      <a-menu-item key="/train">
        <router-link to="/train">
          <rocket-outlined /> 车次管理
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
  display: flex;
  align-items: center;
  padding: 0 24px 0 0;
}
.logo {
  width: 200px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  font-size: 17px;
  font-weight: 600;
  color: #fff;
  letter-spacing: 1px;
  flex-shrink: 0;
}
</style>
