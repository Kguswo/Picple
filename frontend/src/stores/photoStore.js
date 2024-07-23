import { defineStore } from "pinia";

export const usePhotoStore = defineStore("photoStore", {
    state: () => ({
        takenPhotos: [],
    }),
    actions: {
        setPhotos(photos) {
            this.takenPhotos = photos;
        },
        addPhoto(photo) {
            this.takenPhotos.push(photo);
        },
    },
});
