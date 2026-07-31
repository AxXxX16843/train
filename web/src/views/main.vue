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
.app-layout { min-height: 100vh; background: #f5f7fa; }
.app-content {
  margin: 16px; padding: 24px;
  background: #fff; border-radius: 12px;
  min-height: calc(100vh - 64px - 32px);
  box-shadow: 0 2px 8px rgba(0,0,0,.04);
}
.app-content .ant-table { border-radius: 10px; overflow: hidden; }
.app-content .ant-table-pagination { justify-content: center !important; padding: 16px 0; }
.app-content .ant-tag { border-radius: 6px; }
.app-content .ant-btn { border-radius: 6px; }
.app-content .ant-modal-content { border-radius: 14px; }
.app-content .ant-card { border-radius: 12px; }
</style>
