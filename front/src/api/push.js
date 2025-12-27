// /src/api/push.js
import axios from "@/api/axios";

const publicVapidKey =
  "BGo2zo1vqBGB0L1qXvek1gKbXm5DR9LQwRIa0Ax3Rp1dYUeRt8hTkZRmluhhSTlJFSx8hrF9GrEEO5KneIDXIJo";

export async function autoSubscribePush() {
  try {
    // 1) 권한
    let permission = Notification.permission;
    if (permission === "default") {
      permission = await Notification.requestPermission();
    }
    if (permission !== "granted") return;

    // 2) SW 준비
    const registration = await navigator.serviceWorker.ready;

    // 3) 기존 구독
    const existingSubscription = await registration.pushManager.getSubscription();

    // ✅ 핵심: 기존 구독이 있어도 서버에 항상 저장(동기화)
    if (existingSubscription) {
      await axios.post("/push/subscribe", existingSubscription.toJSON());
      console.log("기존 구독 서버 동기화 완료");
      return;
    }

    // 4) 새 구독
    const subscription = await registration.pushManager.subscribe({
      userVisibleOnly: true,
      applicationServerKey: urlBase64ToUint8Array(publicVapidKey),
    });

    console.log("새 구독 생성됨:", subscription);

    // 5) 서버 저장
    await axios.post("/push/subscribe", subscription.toJSON());
    console.log("백엔드에 구독 저장 완료");
  } catch (e) {
    console.error("자동 구독 실패:", e);
  }
}

function urlBase64ToUint8Array(base64String) {
  const padding = "=".repeat((4 - (base64String.length % 4)) % 4);
  const base64 = (base64String + padding).replace(/-/g, "+").replace(/_/g, "/");
  const rawData = window.atob(base64);
  const outputArray = new Uint8Array(rawData.length);
  for (let i = 0; i < rawData.length; ++i) outputArray[i] = rawData.charCodeAt(i);
  return outputArray;
}
