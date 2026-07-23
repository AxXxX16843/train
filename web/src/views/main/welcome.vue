<template>
  <div class="welcome-wrapper">
    <div class="welcome-content">
      <div class="welcome-icon">
        <rocket-outlined />
      </div>
      <h1>12306 售票系统</h1>
      <p>欢迎回来，祝您旅途愉快</p>
    </div>
  </div>
</template>

<script>
import { defineComponent } from 'vue';
import { RocketOutlined } from '@ant-design/icons-vue';

export default defineComponent({
  name: "welcome-view",
  components: { RocketOutlined },
});
</script>

<style scoped>
.welcome-wrapper {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: calc(100vh - 64px - 32px - 48px);
}
.welcome-content {
  text-align: center;
}
.welcome-icon {
  font-size: 56px;
  color: #d9d9d9;
  margin-bottom: 24px;
}
.welcome-content h1 {
  font-size: 26px;
  font-weight: 500;
  color: #262626;
  margin: 0 0 8px;
}
.welcome-content p {
  font-size: 14px;
  color: #8c8c8c;
  margin: 0;
}
</style>
