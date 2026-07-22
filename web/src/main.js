import { createApp } from 'vue'
import axios from 'axios';
import App from './App.vue'
import router from './router'
import Antd from 'ant-design-vue'
import store from './store'
import 'ant-design-vue/dist/reset.css';
import * as Icons from '@ant-design/icons-vue';

axios.defaults.baseURL = 'http://localhost:8000';

const app = createApp(App);
app.use(Antd).use(store).use(router).mount('#app')
const icons = Icons;
for (const i in icons) {
    app.component(i, icons[i]);
}
