import { defineStore } from "pinia";

export const usePhotoStore = defineStore("photo", {
    state: () => ({
        photoList: [],
    }),
    actions: {
        setPhotoList(photos) {
            this.photoList = photos;
        },
        clearPhotoList() {
            this.photoList = [];
        },
    },
});
