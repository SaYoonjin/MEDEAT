# 🥗 MEDEAT (메딧)

AI 기반 식단 · 복약 통합 건강 관리 플랫폼

MEDEAT은 식단(EAT) 과 의약품 관리(MEDI) 를 하나의 서비스로 통합한 헬스케어 웹 플랫폼입니다.
사용자의 식사·복약 기록을 기반으로 AI 의약품 식별, 건강 정보 제공, 자동 리포트 기능을 제공합니다.

## ✨ Key Features
### 🥗 식단 관리

일자별 식단 기록 및 영양소 자동 계산

섭취 패턴 대시보드 시각화

식단 데이터 기반 PDF 리포트 자동 생성

### 💊 복약 관리

복용 의약품 및 복약 기록 관리

의약품 효능·주의사항·상호작용 정보 제공

질병 정보와 복약 데이터 연동

### 🤖 MEDISCAN (AI 의약품 식별)

알약 이미지 업로드 기반 의약품 인식

딥러닝 이미지 임베딩 → 유사도 기반 Top-K 검색

정확도 보완을 위한 공공 의약품 API 하이브리드 연동

### 💬 AI 건강 상담 챗봇

사용자 기록 기반 맞춤형 질의응답

WebSocket 기반 실시간 양방향 채팅

## 🧠 Architecture (Overview)
Vue 3 (Frontend)
      ↓
Spring Boot REST API
      ↓
FastAPI (AI / Embedding Server)
      ↓
Embedding DB + Public Drug API


REST 기반 백엔드 아키텍처

WebSocket을 활용한 실시간 채팅·알림

AI 서버 분리로 확장성과 유지보수성 확보

## 🛠 Tech Stack

### Backend

Java 17, Spring Boot, MyBatis, MySQL

REST API, WebSocket

### Frontend

Vue 3, Vite, Vue Router, Axios

AI / Data

Python, FastAPI

Image Embedding & Similarity Search

### ETC

PDF 자동 생성

공공 의약품 Open API

Git / GitHub

## 🚀 Strengths

식단 + 복약을 통합한 명확한 도메인 차별성

AI 이미지 인식 + 공공 데이터 결합한 하이브리드 설계

실사용 데이터를 기반으로 한 자동화·개인화 기능

실무 지향적인 REST / WebSocket / AI 연동 경험

## 📂 Project Structure
<img width="805" height="123" alt="image" src="https://github.com/user-attachments/assets/7a868793-8066-45ea-abe6-8a2ab78297dd" />


## 📌 Summary

MEDEAT은 단순 기록을 넘어
사용자의 건강 데이터를 연결하고 해석하는 통합 헬스케어 플랫폼입니다.
