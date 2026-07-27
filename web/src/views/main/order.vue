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
          <a-tag v-if="ticket[s.countKey] > 50" color="green">有票</a-tag>
          <a-tag v-else-if="ticket[s.countKey] > 0" color="orange">余{{ ticket[s.countKey] }}张</a-tag>
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
      <div style="text-align:center;margin-top:16px">
        <a-button type="primary" size="large" @click="onShowConfirm" :disabled="orderItems.length === 0">
          提交订单 ({{ orderItems.length }}张)
        </a-button>
      </div>
    </a-card>

    <!-- 确认订单（含选座） -->
    <a-modal v-model:visible="confirmVisible" title="确认订单" @ok="onConfirmOk" ok-text="确认下单" cancel-text="取消" width="660px">
      <div class="confirm-train">{{ ticket?.trainCode }}次 {{ ticket?.start }} — {{ ticket?.end }}</div>
      <div class="confirm-date">{{ formatDate(ticket?.date) }} {{ dayOfWeek(ticket?.date) }}</div>
      <a-divider style="margin:8px 0" />
      <div v-for="(item, idx) in orderItems" :key="idx" class="confirm-item">
        <span class="ci-name">{{ item.name }}</span>
        <span class="ci-type">{{ PASSENGER_TYPES.find(t=>t.code===item.passengerType)?.desc }}</span>
        <span class="ci-seat">{{ SEAT_MAP[item.seatType] }} ¥{{ seatPrice(item.seatType) }}</span>
        <span class="ci-id">{{ item.idCard }}</span>
      </div>

      <div v-if="canSelectSeat" style="margin-top:8px">
        <a-checkbox v-model:checked="showSeatSelect">选择座位</a-checkbox>
      </div>
      <template v-if="canSelectSeat && showSeatSelect">
        <a-divider style="margin:12px 0">选座参考（{{ SEAT_MAP[orderItems[0]?.seatType] }}）</a-divider>
        <div class="seat-ref">
          <div class="seat-ref-row" v-for="r in seatRefRows" :key="r">
            <span class="seat-ref-label">{{ r }}排</span>
            <template v-for="(c, ci) in seatCols" :key="c">
              <span v-if="ci === 0" class="seat-tag">窗</span>
              <div class="seat-ref-box" @click="onSeatRefClick(r, c)"
                :class="{ picked: seatItems.some(i => i.seat === r+c) }">{{ c }}</div>
              <span v-if="ci === firstAisle" class="seat-tag aisle">过道</span>
              <span v-if="ci === secondAisle" class="seat-tag aisle">过道</span>
              <span v-if="ci === seatCols.length - 1" class="seat-tag">窗</span>
            </template>
          </div>
        </div>
        <div class="pick-list" style="margin-top:8px">
          <div v-for="(item, idx) in seatItems" :key="idx" class="pick-item" :class="{ active: pickIdx === idx }" @click="pickIdx = idx">
            <span class="pick-name">{{ item.name }}</span>
            <span class="pick-seat">{{ item.seat || '未选' }}</span>
          </div>
        </div>
      </template>
    </a-modal>
  </div>
  <div v-else class="order-empty">
    <a-empty description="暂无车票信息" />
  </div>
</template>

<script>
import { defineComponent, ref, computed, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import axios from 'axios';
import { notification } from 'ant-design-vue';

const PASSENGER_TYPES = [
  { code: '1', desc: '成人' },
  { code: '2', desc: '儿童' },
  { code: '3', desc: '学生' },
];

const SEAT_LIST = [
  { key: '1', label: '一等座', priceKey: 'ydzPrice', countKey: 'ydz' },
  { key: '2', label: '二等座', priceKey: 'edzPrice', countKey: 'edz' },
  { key: '3', label: '软卧', priceKey: 'rwPrice', countKey: 'rw' },
  { key: '4', label: '硬卧', priceKey: 'ywPrice', countKey: 'yw' },
];

const SEAT_MAP = { '1': '一等座', '2': '二等座', '3': '软卧', '4': '硬卧' };

export default defineComponent({
  name: "order-view",
  setup() {
    const route = useRoute();
    const ticket = ref(null);
    const passengers = ref([]);
    const selectedKeys = ref([]);
    const orderItems = ref([]);
    const confirmVisible = ref(false);
    const showSeatSelect = ref(false);

    const availSeats = computed(() => {
      if (!ticket.value) return [];
      return SEAT_LIST.filter(s => {
        const v = ticket.value[s.countKey];
        return v > 0 || v > 50;
      });
    });

    const seatRemain = (key) => {
      if (!ticket.value) return 0;
      const seat = SEAT_LIST.find(s => s.key === key);
      const total = seat ? ticket.value[seat.countKey] : 0;
      const used = orderItems.value.filter(i => i.seatType === key).length;
      if (total > 50) return total;
      return Math.max(0, total - used);
    };

    const seatPrice = (key) => {
      const s = SEAT_LIST.find(s => s.key === key);
      return s && ticket.value ? ticket.value[s.priceKey] : '—';
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

    const seatItems = ref([]);
    const pickIdx = ref(0);
    const SEAT_COLS = { '1': ['A','C','D','F'], '2': ['A','B','C','D','F'], '3': ['A','B','C','D'], '4': ['A','B','C','D','E','F'] };

    const canSelectSeat = computed(() => {
      if (orderItems.value.length === 0) return false;
      const firstType = orderItems.value[0].seatType;
      const sameType = orderItems.value.every(i => i.seatType === firstType);
      if (!sameType) return false;
      const remain = seatRemain(firstType);
      return remain >= 10 && remain >= orderItems.value.length;
    });

    const seatRefRows = computed(() => orderItems.value.length >= 2 ? [1, 2] : [1]);

    const onShowConfirm = () => {
      showSeatSelect.value = false;
      if (canSelectSeat.value) {
        seatItems.value = orderItems.value.map(i => ({ ...i, seat: '' }));
        pickIdx.value = 0;
      } else {
        seatItems.value = [];
      }
      confirmVisible.value = true;
    };

    const onSeatRefClick = (row, col) => {
      if (!canSelectSeat.value || seatItems.value.length === 0) return;
      const pos = row + col;
      const existed = seatItems.value.findIndex(i => i.seat === pos);
      const cur = seatItems.value[pickIdx.value];
      if (existed >= 0) {
        if (existed === pickIdx.value) seatItems.value[pickIdx.value].seat = '';
      } else if (!cur.seat) {
        seatItems.value[pickIdx.value].seat = pos;
        if (pickIdx.value < seatItems.value.length - 1) pickIdx.value++;
      }
    };

    const seatCols = computed(() => {
      if (orderItems.value.length === 0) return [];
      return SEAT_COLS[orderItems.value[0].seatType] || [];
    });
    const firstAisle = computed(() => {
      const cols = seatCols.value;
      if (cols.length <= 4) return 1; // after C (index 1), between C-D
      return 1; // after B (index 1), between B-C
    });
    const secondAisle = computed(() => -1);

    const onConfirmOk = () => {
      const useSeat = canSelectSeat.value && showSeatSelect.value;
      const source = useSeat ? seatItems.value : orderItems.value;
      axios.post('/business/admin/confirm-order/do-confirm', {
        trainCode: ticket.value.trainCode,
        date: ticket.value.date,
        startStation: ticket.value.start,
        endStation: ticket.value.end,
        dailyTrainTicketId: ticket.value.id,
        tickets: source.map(i => ({
          trainCode: ticket.value.trainCode,
          date: ticket.value.date,
          seatType: i.seatType,
          passengerCard: i.idCard,
          passengerType: i.passengerType,
          passengerId: String(i.passengerId),
          seat: useSeat ? (i.seat || '') : '',
        })),
      }).then(res => {
        if (res.data.success) {
          notification.success({ description: '下单成功！' });
          confirmVisible.value = false;
        } else {
          notification.error({ description: res.data.message });
        }
      });
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

    return { ticket, passengers, selectedKeys, orderItems, availSeats, seatRemain, seatPrice, calcDuration,
      confirmVisible, showSeatSelect, seatItems, seatCols, seatRefRows, pickIdx, canSelectSeat, firstAisle, secondAisle, SEAT_MAP,
      onSeatRefClick,
      PASSENGER_TYPES, formatDate, dayOfWeek, onPassengerChange, onShowConfirm, onConfirmOk };
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
.confirm-train { font-size: 16px; font-weight: 600; }
.confirm-date { font-size: 13px; color: #888; margin-top: 2px; }
.confirm-item { display: flex; gap: 16px; padding: 6px 0; border-bottom: 1px dashed #f0f0f0; font-size: 13px; }
.confirm-item:last-child { border-bottom: none; }
.ci-name { width: 70px; font-weight: 600; }
.ci-type { width: 50px; }
.ci-seat { flex: 1; color: #e65c41; }
.ci-id { width: 180px; color: #666; text-align: right; }
/* 选座 */
.seat-ref { display: flex; flex-direction: column; align-items: center; margin: 4px 0; }
.seat-ref-row { display: flex; align-items: center; gap: 6px; margin-bottom: 6px; }
.seat-ref-label { width: 36px; font-size: 12px; color: #666; flex-shrink: 0; }
.seat-ref-box { width: 44px; height: 32px; display: flex; align-items: center; justify-content: center; font-size: 13px; border: 1px solid #d9d9d9; border-radius: 4px; cursor: pointer; background: #fff; color: #333; transition: all 0.15s; }
.seat-ref-box:hover { border-color: #1677ff; background: #e6f4ff; }
.seat-ref-box.picked { background: #1677ff; color: #fff; border-color: #1677ff; }
.seat-tag { font-size: 11px; color: #999; width: 20px; text-align: center; flex-shrink: 0; }
.seat-tag.aisle { width: 28px; }
.pick-list { display: flex; gap: 8px; flex-wrap: wrap; }
.pick-item { padding: 4px 12px; border-radius: 6px; border: 1px solid #e8e8e8; cursor: pointer; font-size: 13px; transition: all 0.2s; }
.pick-item:hover, .pick-item.active { border-color: #1677ff; background: #e6f4ff; }
.pick-name { font-weight: 600; margin-right: 8px; }
.pick-seat { color: #1677ff; }
</style>
