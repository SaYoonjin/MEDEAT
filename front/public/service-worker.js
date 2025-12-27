self.addEventListener("push", function (event) {
  let data = {};

  try {
    data = event.data.json(); // JSON이면 정상 파싱
  } catch (e) {
    // 텍스트로 온 경우
    data = {
      title: "약 복용 알림 💊",
      body: event.data ? event.data.text() : "약 복용 시간입니다."
    };
  }

  const title = data.title || "약 복용 알림";
  const body = data.body || "약 복용 시간입니다.";
  const medicationId = data.medicationId;
  const doseIndex = data.doseIndex;

  event.waitUntil(
    self.registration.showNotification(title, {
      body,
      icon: "/img/medicine-icon.png",
      data: { medicationId, doseIndex },
      actions: [
        { action: "take", title: "복용 완료" },
        { action: "later", title: "나중에 다시 알림" }
      ]
    })
  );
});


// ★ 2) 알림 클릭 이벤트 처리
self.addEventListener("notificationclick", function (event) {
  const { medicationId, doseIndex } = event.notification.data || {};

  // 복용 완료 버튼 클릭
  if (event.action === "take") {
    event.waitUntil(
      fetch(`/api/medication/take`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ medicationId, doseIndex })
      })
    );
  }

  // 재알림 버튼 클릭
  if (event.action === "later") {
    event.notification.close();

    event.waitUntil(
      clients.openWindow(
        `/medication/reschedule?medicationId=${medicationId}&doseIndex=${doseIndex}`
      )
    );
    return;
  }

  // 기본 클릭(본문 클릭) → 복용 완료 로직 실행
  if (!event.action && medicationId && doseIndex) {
    event.waitUntil(
      fetch(`/api/medication/take`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ medicationId, doseIndex })
      })
    );
  }

  event.notification.close();
});
