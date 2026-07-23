<template>
  <a-layout-header class="header">
    <div class="header-right">
      您好，{{ member.mobile }}
      <router-link to="/login" class="logout-link">
        <logout-outlined /> 退出
      </router-link>
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
      <a-menu-item key="/passenger">
        <router-link to="/passenger">
          <team-outlined /> 乘车人管理
        </router-link>
      </a-menu-item>
    </a-menu>
  </a-layout-header>
</template>

<script>
import { defineComponent, ref, watch } from 'vue';
import store from "@/store";
import router from '@/router';

export default defineComponent({
  name: "the-header-view",
  setup() {
    const member = store.state.member;
    const selectedKeys = ref([]);

    watch(() => router.currentRoute.value.path, (newValue) => {
      selectedKeys.value = [newValue];
    }, { immediate: true });

    return { member, selectedKeys };
  },
});
</script>

<style scoped>
.header {
  display: flex;
  align-items: center;
  padding: 0 24px;
}
.header-right {
  margin-left: auto;
  color: rgba(255, 255, 255, 0.85);
  font-size: 14px;
  white-space: nowrap;
}
.logout-link {
  color: rgba(255, 255, 255, 0.65);
  margin-left: 16px;
  font-size: 13px;
}
.logout-link:hover {
  color: #fff;
}
</style>
