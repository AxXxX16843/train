<template>
  <div class="login-page">
    <div class="login-card">
      <h1 class="login-title">
        <rocket-two-tone /> 12306 售票系统
      </h1>
      <a-form
          :model="loginForm"
          name="basic"
          autocomplete="off"
          size="large"
      >
        <a-form-item
            name="mobile"
            :rules="[{ required: true, message: '请输入手机号' }]"
        >
          <a-input v-model:value="loginForm.mobile" placeholder="手机号">
            <template #prefix>
              <user-outlined />
            </template>
          </a-input>
        </a-form-item>

        <a-form-item
            name="code"
            :rules="[{ required: true, message: '请输入验证码' }]"
        >
          <a-input v-model:value="loginForm.code" placeholder="验证码">
            <template #prefix>
              <lock-outlined />
            </template>
            <template #addonAfter>
              <a @click="sendCode">获取验证码</a>
            </template>
          </a-input>
        </a-form-item>

        <a-form-item>
          <a-button type="primary" block @click="login" size="large">
            登 录
          </a-button>
        </a-form-item>
      </a-form>
    </div>
  </div>
</template>

<script>
import { defineComponent, reactive } from 'vue';
import axios from 'axios';
import { notification } from 'ant-design-vue';
import { useRouter } from 'vue-router'
import store from "@/store";

export default defineComponent({
  name: "login-view",
  setup() {
    const router = useRouter();

    const loginForm = reactive({
      mobile: '',
      code: '',
    });

    const sendCode = () => {
      axios.post("/member/send-code", {
        mobile: loginForm.mobile
      }).then(response => {
        let data = response.data;
        if (data.success) {
          notification.success({ description: '发送验证码成功！' });
          loginForm.code = "666666";
        } else {
          notification.error({ description: data.message });
        }
      });
    };

    const login = () => {
      axios.post("/member/login", loginForm).then((response) => {
        let data = response.data;
        if (data.success) {
          notification.success({ description: '登录成功！' });
          router.push("/welcome");
          store.commit("setMember", data.content);
        } else {
          notification.error({ description: data.message });
        }
      })
    };

    return {
      loginForm,
      sendCode,
      login
    };
  },
});
</script>

<style>
.login-page {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 50%, #0f3460 100%);
}
.login-card {
  width: 400px;
  padding: 44px 40px;
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 16px 48px rgba(0, 0, 0, 0.25);
}
.login-title {
  text-align: center;
  font-size: 24px;
  font-weight: 700;
  margin-bottom: 8px;
  color: #1a1a1a;
}
.login-subtitle {
  text-align: center;
  font-size: 13px;
  color: #999;
  margin-bottom: 32px;
}
.login-card :deep(.ant-btn-primary) { border-radius: 8px; height: 42px; font-size: 15px; }
.login-card :deep(.ant-input-affix-wrapper) { border-radius: 8px; }
</style>
