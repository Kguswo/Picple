import { defineStore } from "pinia";

export const useBoothStore = defineStore("booth", {
    state: () => ({
        bgImage: "https://via.placeholder.com/400",
        images: [],
        remainPicCnt: 10,
        participants: [],
    }),
    actions: {
        setBgImage(image) {
            this.bgImage = image;
        },
        addImage(image) {
            this.images.push(image);
            this.remainPicCnt--;
        },
        setParticipants(participants) {
            this.participants = participants;
        },
    },
});
