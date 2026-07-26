<template>
  <a-layout-sider width="200" class="sider">
    <div class="sider-title">12306 售票系统</div>
    <a-menu v-model:selectedKeys="selectedKeys" mode="inline">
      <a-menu-item key="/welcome">
        <router-link to="/welcome">
          <home-outlined /> 首页
        </router-link>
      </a-menu-item>
      <a-menu-item key="/passenger">
        <router-link to="/passenger">
          <team-outlined /> 乘车人管理
        </router-link>
      </a-menu-item>
      <a-menu-item key="/ticket">
        <router-link to="/ticket">
          <shopping-outlined /> 余票查询
        </router-link>
      </a-menu-item>
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
}
</style>