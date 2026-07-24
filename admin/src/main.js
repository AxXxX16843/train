import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import axios from 'axios'
import store from './store'
import 'ant-design-vue/dist/reset.css';
import * as Icons from '@ant-design/icons-vue';
import Antd from 'ant-design-vue';

const app = createApp(App);
app.use(Antd).use(store).use(router).mount('#app')
const icons = Icons;
for (const i in icons) {
    app.component(i, icons[i]);
}

axios.defaults.baseURL = process.env.VUE_APP_SERVER;
console.log('环境：', process.env.NODE_ENV);
console.log('服务端：', process.env.VUE_APP_SERVER);
