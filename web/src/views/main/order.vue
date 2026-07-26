<template>
  <div class="order-page" v-if="ticket">
    <!-- 列车信息 -->
    <a-card :bordered="false" title="列车信息" class="order-card">
      <div class="train-header">
        <div class="train-code">{{ ticket.trainCode }}</div>
        <div class="train-route">
          <div class="route-start">
            <div class="station-name">{{ ticket.start }}</div>
            <div class="station-time">{{ ticket.startTime }}</div>
          </div>
          <div class="route-arrow">
            <div class="arrow-line" />
            <span class="arrow-text">{{ calcDuration(ticket.startTime, ticket.endTime) }}</span>
            <div class="arrow-line" />
          </div>
          <div class="route-end">
            <div class="station-name">{{ ticket.end }}</div>
            <div class="station-time">{{ ticket.endTime }}</div>
          </div>
        </div>
        <div class="train-date">{{ formatDate(ticket.date) }} {{ dayOfWeek(ticket.date) }}</div>
      </div>
      <a-divider style="margin:12px 0" />
      <div class="seat-grid">
        <div v-for="s in availSeats" :key="s.key" class="seat-card">
          <div class="seat-type">{{ s.label }}</div>
          <div class="seat-price">¥{{ ticket[s.priceKey] }}</div>
          <a-tag v-if="ticket[s.key] > 50" color="green">有票</a-tag>
          <a-tag v-else-if="ticket[s.key] > 0" color="orange">余{{ ticket[s.key] }}张</a-tag>
          <a-tag v-else color="red">售罄</a-tag>
        </div>
      </div>
    </a-card>

    <!-- 乘客信息 -->
    <a-card :bordered="false" title="乘客信息" class="order-card" style="margin-top:16px">
      <div class="passenger-grid">
        <a-checkbox-group :value="selectedKeys" @change="onPassengerChange">
          <a-checkbox v-for="p in passengers" :key="p.id" :value="p.id" class="passenger-check">
            <span class="passenger-name">{{ p.name }}</span>
          </a-checkbox>
        </a-checkbox-group>
        <span v-if="passengers.length === 0" class="empty-hint">暂无乘车人</span>
      </div>
      <a-divider style="margin:12px 0" />
      <div class="ticket-table">
        <div class="ticket-header">
          <span class="th th-name">乘车人</span>
          <span class="th th-type">票种</span>
          <span class="th th-seat">席别</span>
          <span class="th th-id">证件号</span>
        </div>
        <div v-if="orderItems.length > 0">
          <div v-for="(item, idx) in orderItems" :key="idx" class="ticket-row">
            <span class="td td-name">{{ item.name }}</span>
            <span class="td td-type">
              <a-select v-model:value="item.passengerType" size="small" style="width:80px">
                <a-select-option v-for="t in PASSENGER_TYPES" :key="t.code" :value="t.code">{{ t.desc }}</a-select-option>
              </a-select>
            </span>
            <span class="td td-seat">
              <a-select v-model:value="item.seatType" size="small" style="width:220px">
                <a-select-option v-for="s in availSeats" :key="s.key" :value="s.key">
                  {{ s.label }} ¥{{ ticket[s.priceKey] }} (余{{ seatRemain(s.key) }})
                </a-select-option>
              </a-select>
            </span>
            <span class="td td-id">{{ item.idCard }}</span>
          </div>
        </div>
        <div v-else class="ticket-empty">勾选乘车人后选择席别</div>
      </div>
    </a-card>
  </div>
  <div v-else class="order-empty">
    <a-empty description="暂无车票信息" />
  </div>
</template>

<script>
import { defineComponent, ref, computed, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import axios from 'axios';

const PASSENGER_TYPES = [
  { code: '1', desc: '成人' },
  { code: '2', desc: '儿童' },
  { code: '3', desc: '学生' },
];

const SEAT_LIST = [
  { key: 'ydz', label: '一等座', priceKey: 'ydzPrice' },
  { key: 'edz', label: '二等座', priceKey: 'edzPrice' },
  { key: 'rw', label: '软卧', priceKey: 'rwPrice' },
  { key: 'yw', label: '硬卧', priceKey: 'ywPrice' },
];

export default defineComponent({
  name: "order-view",
  setup() {
    const route = useRoute();
    const ticket = ref(null);
    const passengers = ref([]);
    const selectedKeys = ref([]);
    const orderItems = ref([]);

    const availSeats = computed(() => {
      if (!ticket.value) return [];
      return SEAT_LIST.filter(s => {
        const v = ticket.value[s.key];
        return v > 0 || v > 50;
      });
    });

    const seatRemain = (key) => {
      if (!ticket.value) return 0;
      const total = ticket.value[key];
      const used = orderItems.value.filter(i => i.seatType === key).length;
      if (total > 50) return total;
      return Math.max(0, total - used);
    };

    const calcDuration = (startTime, endTime) => {
      const t = (s) => { if (!s) return 0; const p = s.split(':'); return parseInt(p[0])*60 + parseInt(p[1]); };
      let d = t(endTime) - t(startTime);
      if (d <= 0) d += 24*60;
      if (d <= 0) return '—';
      return Math.floor(d/60)+'时'+(d%60)+'分';
    };

    const formatDate = (d) => {
      if (!d) return '';
      return String(d).substring(0, 10);
    };

    const dayOfWeek = (d) => {
      if (!d) return '';
      const days = ['周日','周一','周二','周三','周四','周五','周六'];
      return days[new Date(d).getDay()];
    };

    const onPassengerChange = (keys) => {
      const addIds = keys.filter(k => !selectedKeys.value.includes(k));
      const rmIds = selectedKeys.value.filter(k => !keys.includes(k));
      for (const id of addIds) {
        const p = passengers.value.find(pp => pp.id === id);
        if (p) {
          orderItems.value.push({
            passengerId: p.id, passengerType: p.type,
            seatType: availSeats.value[0]?.key || '',
            name: p.name, idCard: p.idCard,
          });
        }
      }
      orderItems.value = orderItems.value.filter(i => !rmIds.includes(i.passengerId));
      selectedKeys.value = keys;
    };

    onMounted(() => {
      const raw = route.query.d;
      if (raw) {
        try { ticket.value = JSON.parse(decodeURIComponent(raw)); } catch (e) { ticket.value = null; }
      }
      axios.get('/member/passenger/query-list', { params: { page: 1, size: 100 } }).then(res => {
        if (res.data.success) passengers.value = res.data.content.list || [];
      });
    });

    return { ticket, passengers, selectedKeys, orderItems, availSeats, seatRemain, calcDuration,
      PASSENGER_TYPES, formatDate, dayOfWeek, onPassengerChange };
  },
});
</script>

<style scoped>
.order-page { padding: 24px; max-width: 960px; margin: 0 auto; }
.order-card { border-radius: 12px; }
:deep(.ant-card-head) { background: #fafafa; }
.order-card { border-radius: 12px; }

/* 车次头部 */
.train-header { text-align: center; padding: 4px 0; }
.train-code { font-size: 26px; font-weight: 700; color: #1a1a1a; }
.train-route { display: flex; align-items: center; justify-content: center; gap: 16px; margin: 12px 0 6px; }
.route-start, .route-end { text-align: center; }
.station-name { font-size: 17px; font-weight: 600; color: #333; }
.station-time { font-size: 13px; color: #888; margin-top: 2px; }
.route-arrow { display: flex; align-items: center; gap: 6px; min-width: 100px; justify-content: center; }
.arrow-line { flex: 1; height: 1px; background: #d9d9d9; }
.arrow-text { font-size: 12px; color: #999; white-space: nowrap; }
.train-date { font-size: 13px; color: #888; }

/* 座位 */
.seat-grid { display: flex; gap: 8px; flex-wrap: wrap; }
.seat-card { flex: 0 0 auto; min-width: 90px; text-align: center; padding: 8px 10px; background: #fafafa; border-radius: 8px; border: 1px solid #f0f0f0; transition: all 0.2s; }
.seat-card:hover { border-color: #1677ff; }
.seat-type { font-size: 13px; font-weight: 600; color: #333; margin-bottom: 2px; }
.seat-price { font-size: 14px; color: #e65c41; font-weight: 600; margin-bottom: 2px; }

/* 乘车人 */
.passenger-grid { min-height: 30px; display: flex; flex-wrap: wrap; gap: 4px 16px; }
.passenger-check { margin-right: 0 !important; }
.passenger-name { font-size: 14px; }
.empty-hint { color: #c0c0c0; font-size: 13px; }

/* 车票表格 */
.ticket-table { border: 1px solid #f0f0f0; border-radius: 10px; overflow: hidden; }
.ticket-header { display: flex; background: #fafafa; padding: 10px 16px; border-bottom: 1px solid #f0f0f0; }
.ticket-row { display: flex; align-items: center; padding: 10px 16px; border-bottom: 1px solid #f5f5f5; }
.ticket-row:last-child { border-bottom: none; }
.th, .td { font-size: 13px; }
.th { color: #888; font-weight: 500; }
.th-name, .td-name { width: 80px; }
.th-type, .td-type { width: 100px; }
.th-seat, .td-seat { flex: 1; }
.th-id, .td-id { width: 180px; text-align: right; }
.td-id { color: #666; }
.ticket-empty { text-align: center; padding: 24px; color: #c0c0c0; font-size: 13px; }
</style>
