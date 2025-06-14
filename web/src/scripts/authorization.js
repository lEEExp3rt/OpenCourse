import router from "@/router";
import { getItem } from "@/utils/storage"

function isTokenValid(token) {
  if (!token) return false;
  try {
    const payload = JSON.parse(atob(token.split('.')[1]));
    const exp = payload.exp * 1000;
    return Date.now() < exp;
  } catch (e) {
    return false;
  }
}

router.beforeEach((to, from, next) => {
  const token = getItem("token");
  const requiresAuth = to.matched.some(record => record.meta.requiresAuth);
  if (requiresAuth && (!token || !isTokenValid(token))) {
    next({ path: '/login' });
  } else {
    next();
  }
});
