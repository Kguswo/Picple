<template>
    <div v-if="visible" class="modal-overlay" @click="close">
        <div class="modal-content" @click.stop>
            <div class="modal-header">
                <button class="close-button" @click="close">
                    <img src="@/assets/img/common/close.png" alt="Close" />
                </button>
                <div>
                    {{ selectedDate }}
                </div>
            </div>
            <div class="modal-body">
                <div class="photo-container">
                    <div
                        class="photo prev-photo"
                        :class="getPhotoClass(getPrevPhoto())"
                    >
                        <div
                            class="photo-background"
                            :class="getPhotoClass(getPrevPhoto())"
                        >
                            <img :src="getPrevPhoto()" alt="Previous Photo" />
                        </div>
                    </div>
                    <div
                        class="photo current-photo"
                        :class="[
                            getPhotoClass(getCurrentPhoto()),
                            { expanded: currentPhotoExpanded },
                        ]"
                        @click="toggleCurrentPhoto"
                    >
                        <div
                            class="photo-background"
                            :class="getPhotoClass(getCurrentPhoto())"
                        >
                            <img
                                :src="getCurrentPhoto()"
                                alt="Current Photo"
                                :class="{ expanded: currentPhotoExpanded }"
                            />
                            <button
                                class="nav-button share-button"
                                :class="getPhotoClass(getCurrentPhoto())"
                            >
                                <img
                                    src="@/assets/img/calendar/share.png"
                                    alt="Share"
                                />
                            </button>
                        </div>
                    </div>
                    <div
                        class="photo next-photo"
                        :class="getPhotoClass(getNextPhoto())"
                    >
                        <div
                            class="photo-background"
                            :class="getPhotoClass(getNextPhoto())"
                        >
                            <img :src="getNextPhoto()" alt="Next Photo" />
                        </div>
                    </div>
                    <div class="nav-buttons">
                        <button
                            class="nav-button prev-button"
                            :class="[
                                getPhotoClass(getPrevPhoto()),
                                { expanded: currentPhotoExpanded },
                            ]"
                            @click="prevPhoto"
                        >
                            <img
                                src="@/assets/img/calendar/arrow-left.png"
                                alt="Previous"
                            />
                        </button>
                        <button
                            class="nav-button next-button"
                            :class="[
                                getPhotoClass(getNextPhoto()),
                                { expanded: currentPhotoExpanded },
                            ]"
                            @click="nextPhoto"
                        >
                            <img
                                src="@/assets/img/calendar/arrow-right.png"
                                alt="Next"
                            />
                        </button>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { defineProps, defineEmits, ref } from "vue";

// 이미지를 미리 import
import photo1 from "@/assets/img/calendar/portrait_ho1.png";
import photo2 from "@/assets/img/calendar/landscape_me2.png";
import photo3 from "@/assets/img/calendar/portrait_messi3.png";

const props = defineProps({
    visible: Boolean,
    selectedDate: String, // selectedDate prop 정의
});

const emits = defineEmits(["close"]);

const close = () => {
    emits("close");
};

// 사진 리스트 (예시)
const photos = ref([photo1, photo2, photo3]);

const currentIndex = ref(0);
const currentPhotoExpanded = ref(false);

const prevPhoto = () => {
    currentIndex.value =
        (currentIndex.value - 1 + photos.value.length) % photos.value.length;
};

const nextPhoto = () => {
    currentIndex.value = (currentIndex.value + 1) % photos.value.length;
};

const getPrevPhoto = () => {
    const prevIndex =
        (currentIndex.value - 1 + photos.value.length) % photos.value.length;
    return photos.value[prevIndex];
};

const getCurrentPhoto = () => {
    return photos.value[currentIndex.value];
};

const getNextPhoto = () => {
    const nextIndex = (currentIndex.value + 1) % photos.value.length;
    return photos.value[nextIndex];
};

const toggleCurrentPhoto = () => {
    currentPhotoExpanded.value = !currentPhotoExpanded.value;
};

const getPhotoClass = (photo) => {
    if (photo.includes("portrait")) {
        return "portrait";
    } else if (photo.includes("landscape")) {
        return "landscape";
    }
    return "";
};
</script>

<style scoped>
/* Container styles */
.modal-overlay {
    position: fixed;
    top: 0;
    left: 0;
    width: 100vw;
    height: 100vh;
    background: rgba(0, 0, 0, 0.5);
    display: flex;
    justify-content: center;
    align-items: center;
    z-index: 500;
    overflow: hidden;
}

.modal-content {
    position: relative;
    width: 35%;
    height: 75%;

    background: #faf9e6;
    border-radius: 15px;
    border: 4px solid black;
    overflow: visible;
}

.modal-header {
    height: 15%;
    padding-bottom: 10px;
    border-bottom: 4px solid black;
    display: flex;
    flex-direction: column;
    align-items: flex-end;
}

.modal-header > div {
    height: 60%;
    width: 100%;
    display: flex;
    justify-content: center;
    align-items: center;
    font-size: 3vw;
    font-weight: bold;
}

.modal-body {
    width: 100%;
    height: 85%;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    overflow: visible;
}

.photo-container {
    position: relative;
    width: 95%;
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-bottom: 20px;
    overflow: visible;
}

.photo-continer > div {
    display: flex;
    justify-content: center;
    align-items: center;
}

/* Button styles */
.close-button {
    width: 100%;
    height: 40%;
    background: none;
    border: none;
    display: flex;
    align-items: center;
    justify-content: flex-end;
}

.close-button img {
    width: 24px;
    height: 24px;
    margin-right: 6px;
}

.nav-buttons {
    display: flex;
    justify-content: space-between;
    align-items: center;
    width: 100%;
    height: 100%;
}

.nav-button {
    background: none;
    border: none;
    cursor: pointer;
    z-index: 4;
    transition: transform 0.5s ease-in-out, left 0.5s ease-in-out,
        right 0.5s ease-in-out;
}

.prev-button {
    display: flex;
    align-items: center;
    left: 0px;
    opacity: 0.7;
    transition: opacity 0.3s, transform 0.5s ease-in-out, left 0.5s ease-in-out;
    width: 10% !important;
    height: 100% !important;
}

.next-button {
    display: flex;
    flex-direction: row-reverse;
    justify-content: flex-start;
    align-items: center;
    right: 0px;
    opacity: 0.7;
    transition: opacity 0.3s, transform 0.5s ease-in-out, right 0.5s ease-in-out;
    width: 10% !important;
    height: 100% !important;
}

.nav-button.expanded {
    transform: scale(1.3) translateY(-10%);
}

.nav-button.expanded.prev-button.portrait {
    left: -11%;
    opacity: 0.9;
}

.nav-button.expanded.next-button.portrait {
    right: -11%;
    opacity: 0.9;
}

.nav-button.expanded.prev-button.landscape {
    left: -20%;
    opacity: 0.9;
}

.nav-button.expanded.next-button.landscape {
    right: -20%;
    opacity: 0.9;
}

.nav-button img {
    border: none;
    box-shadow: none;
    height: 90px;
    opacity: 0.8;
    transition: opacity 0.3s;
}

.nav-button img:hover {
    opacity: 1;
}

.share-button.portrait {
    top: 7%;
    right: 4%;
}

.share-button.landscape {
    top: 10%;
    right: 5%;
}

.share-button > img {
    width: 23px !important;
    height: 23px !important;
    opacity: 0.5 !important;
    border-radius: 0px !important;
}

/* Photo styles */
.photo {
    position: absolute;
    transition: transform 0.5s ease-in-out, opacity 0.5s ease-in-out;
}

.photo.portrait.prev-photo {
    transform: translateX(-50%) scale(0.75);
}

.photo.landscape.prev-photo {
    transform: translateX(-20%) scale(0.75);
}

.photo.portrait.next-photo {
    transform: translateX(50%) scale(0.75);
}

.photo.landscape.next-photo {
    transform: translateX(20%) scale(0.75);
}

.photo.expanded {
    transform: scale(1.6) translateY(-9%);
    z-index: 3;
    border-radius: 30px;
}

.photo-background {
    width: 100%;
    height: 100%;
    background-color: white;
    box-shadow: 6px 4px 20px rgba(0, 0, 0, 0.7);
    padding: 10px;
    border-radius: 10px;
}

.portrait {
    width: 55%;
    height: 90%;
}

.landscape {
    width: 80%;
    height: 50%;
}

.current-photo {
    transform: translateX(0) scale(1);
    z-index: 2;
    display: flex;
    justify-content: center;
    align-items: center;
}

.photo img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    border-radius: 10px;
}

.photo.expanded img {
    width: 100%;
    height: 100%;
    transition: transform 0.5s ease-in-out, opacity 0.5s ease-in-out;
    border-radius: 0px;
}

.photo.expanded .share-button > img {
    width: 23px;
    height: 23px;
    opacity: 0.5;
    transition: transform 0.5s ease-in-out, opacity 0.5s ease-in-out;
    border-radius: 0px;
}

.photo.expanded .share-button > img:hover {
    opacity: 1;
}
</style>
