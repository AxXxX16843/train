<template>
  <div class="welcome-wrapper">
    <div class="welcome-content">
      <div class="welcome-icon">
        <div class="icon-circle">
          <rocket-outlined />
        </div>
      </div>
      <h1>12306 售票系统</h1>
      <p>欢迎回来，祝您旅途愉快</p>
      <div class="welcome-cards">
        <div class="wcard" @click="$router.push('/ticket')">
          <shopping-outlined class="wcard-icon" />
          <span>余票查询</span>
        </div>
        <div class="wcard" @click="$router.push('/my-ticket')">
          <idcard-outlined class="wcard-icon" />
          <span>我的车票</span>
        </div>
        <div class="wcard" @click="$router.push('/passenger')">
          <team-outlined class="wcard-icon" />
          <span>乘车人</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { defineComponent } from 'vue';
import { RocketOutlined, ShoppingOutlined, IdcardOutlined, TeamOutlined } from '@ant-design/icons-vue';

export default defineComponent({
  name: "welcome-view",
  components: { RocketOutlined, ShoppingOutlined, IdcardOutlined, TeamOutlined },
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
  margin-bottom: 28px;
}
.icon-circle {
  width: 80px; height: 80px;
  margin: 0 auto;
  border-radius: 50%;
  background: linear-gradient(135deg, #1677ff, #69b1ff);
  display: flex; align-items: center; justify-content: center;
  font-size: 36px; color: #fff;
  box-shadow: 0 8px 24px rgba(22,119,255,.25);
}
.welcome-content h1 {
  font-size: 26px; font-weight: 600; color: #1a1a1a; margin: 0 0 6px;
}
.welcome-content p {
  font-size: 15px; color: #999; margin: 0 0 32px;
}
.welcome-cards {
  display: flex; gap: 16px; justify-content: center;
}
.wcard {
  width: 120px; padding: 20px 12px;
  border-radius: 12px; background: #fafafa;
  cursor: pointer; transition: all .25s;
  border: 1px solid #f0f0f0;
}
.wcard:hover {
  background: #e6f4ff; border-color: #91caff;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(22,119,255,.12);
}
.wcard-icon { font-size: 26px; color: #1677ff; display: block; margin-bottom: 8px; }
.wcard span { font-size: 13px; color: #333; font-weight: 500; }
</style>
