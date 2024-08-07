<template>
    <div id="app">
        <h1>OpenVidu 테스트 애플리케이션</h1>
        <VideoRoom />
    </div>
</template>

<script setup>
import VideoRoom from '@/components/VideoRoom.vue';
import { onMounted, onUnmounted } from 'vue';
import WebSocketService from '@/services/WebSocketService';
import { useBoothStore } from '@/stores/boothStore';

const boothStore = useBoothStore();

onMounted(() => {
    WebSocketService.setBoothStore(boothStore);
    WebSocketService.on('background_info', (message) => {
        boothStore.setBgImage(message.backgroundImage);
    });
});

onUnmounted(() => {
    // 필요한 정리 작업 수행
});
</script>

<style>
#app {
    font-family: Avenir, Helvetica, Arial, sans-serif;
    -webkit-font-smoothing: antialiased;
    -moz-osx-font-smoothing: grayscale;
    text-align: center;
    color: #2c3e50;
    margin-top: 60px;
}
</style>
