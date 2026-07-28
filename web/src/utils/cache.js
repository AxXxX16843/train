const CACHE_PREFIX = 'train_cache_';

export default {
  get(key) {
    const raw = localStorage.getItem(CACHE_PREFIX + key);
    if (!raw) return null;
    try {
      const { data, expireAt } = JSON.parse(raw);
      if (Date.now() > expireAt) { localStorage.removeItem(CACHE_PREFIX + key); return null; }
      return data;
    } catch { localStorage.removeItem(CACHE_PREFIX + key); return null; }
  },
  set(key, data) {
    localStorage.setItem(CACHE_PREFIX + key, JSON.stringify({ data, expireAt: Date.now() + 5 * 60 * 1000 }));
  },
  remove(key) {
    localStorage.removeItem(CACHE_PREFIX + key);
  }
};
