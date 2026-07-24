<template>
  <a-layout class="app-layout">
    <the-header-view />
    <a-layout>
      <the-sider-view />
      <a-layout-content class="app-content">
        <router-view />
      </a-layout-content>
    </a-layout>
  </a-layout>
</template>

<script>
import { defineComponent } from 'vue';
import TheHeaderView from "@/components/the-header";
import TheSiderView from "@/components/the-sider";

export default defineComponent({
  name: "main-view",
  components: { TheHeaderView, TheSiderView },
});
</script>

<style>
.app-layout {
  min-height: 100vh;
}
.app-content {
  margin: 16px;
  padding: 24px;
  background: #fff;
  border-radius: 8px;
  min-height: calc(100vh - 64px - 32px);
}
</style>
