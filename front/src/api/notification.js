import api from "./axios";

// src/api/notification.js
export const fetchNotifications = () =>
  api.get("/notifications/feed");

export const fetchUnreadCount = () =>
  api.get("/notifications/feed/unread-count");

export const markAsRead = (id) =>
  api.patch(`/notifications/feed/${id}/read`);

export const fetchAllNotifications = () =>
  api.get("/notifications/feed/all");

export const markAllAsRead = () =>
  api.patch("/notifications/feed/read-all");

export const markRecentAsRead = () =>
  api.patch("/notifications/feed/read-recent");

export const markAsReadBatch = (ids) =>
  api.patch("/notifications/feed/read-batch", { ids });
