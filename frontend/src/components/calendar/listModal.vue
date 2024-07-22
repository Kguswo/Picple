<script setup>
import { defineProps, defineEmits } from "vue";
import calendarModal from "@/assets/js/calendarModal";

const props = defineProps({
    visible: Boolean,
    selectedDate: String, // selectedDate prop 정의
});

const emits = defineEmits(["close"]);

const {
    photos,
    currentIndex,
    currentPhotoExpanded,
    close,
    prevPhoto,
    nextPhoto,
    getPrevPhoto,
    getCurrentPhoto,
    getNextPhoto,
    toggleCurrentPhoto,
    getPhotoClass,
} = calendarModal(props, emits);
</script>

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
                            <img :src="getCurrentPhoto()" alt="Current Photo" />
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
                            :class="{ expanded: currentPhotoExpanded }"
                            @click="prevPhoto"
                        >
                            <img
                                src="@/assets/img/calendar/arrow-left.png"
                                alt="Previous"
                            />
                        </button>
                        <button
                            class="nav-button next-button"
                            :class="{ expanded: currentPhotoExpanded }"
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

<style scoped>
@import url("@/assets/css/calendarModal.css");
</style>
