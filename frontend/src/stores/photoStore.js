import { ref } from 'vue';
import { defineStore } from 'pinia';

export const usePhotoStore = defineStore('photo', () => {
    const photoList = ref({}); // sessionId를 키로 가지는 객체로 변경

    const setPhotoList = (sessionId, photos) => {
        photoList.value[sessionId] = photos;
    };

    const addPhoto = (sessionId, photo) => {
        if (!photoList.value[sessionId]) {
            photoList.value[sessionId] = [];
        }
        photoList.value[sessionId].push(photo);
    };

    const clearPhotoList = (sessionId) => {
        if (photoList.value[sessionId]) {
            photoList.value[sessionId] = [];
        }
    };

    const getPhotosBySession = (sessionId) => {
        return photoList.value[sessionId] || [];
    };

    return {
        photoList,
        setPhotoList,
        addPhoto,
        clearPhotoList,
        getPhotosBySession,
    };
});
