<script setup>
import { defineProps, defineEmits, watch } from "vue";
import useCalendarModal from "@/common/calendarModal";

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
    updatePhotos,
} = useCalendarModal(props, emits);

watch(
    () => props.selectedDate,
    (newDate) => {
        if (newDate) {
            updatePhotos(newDate);
        }
    }
);
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
                <div v-if="photos.length > 0" class="photo-container">
                    <div class="photo prev-photo" v-if="photos.length > 2" :class="getPhotoClass(getPrevPhoto())">
                        <div class="photo-background" :class="getPhotoClass(getPrevPhoto())">
                            <img :src="getPrevPhoto().src" alt="Previous Photo" />
                        </div>
                    </div>
                    <div class="photo current-photo" :class="[
                        getPhotoClass(getCurrentPhoto()),
                        { expanded: currentPhotoExpanded },
                    ]" @click="toggleCurrentPhoto">
                        <div class="photo-background" :class="getPhotoClass(getCurrentPhoto())">
                            <img :src="getCurrentPhoto().src" alt="Current Photo" />
                            <button class="nav-button share-button" :class="getPhotoClass(getCurrentPhoto())">
                                <img src="@/assets/img/calendar/share.png" alt="Share" />
                            </button>
                        </div>
                    </div>
                    <div class="photo next-photo" v-if="photos.length > 1" :class="getPhotoClass(getNextPhoto())">
                        <div class="photo-background" :class="getPhotoClass(getNextPhoto())">
                            <img :src="getNextPhoto().src" alt="Next Photo" />
                        </div>
                    </div>
                    <div class="nav-buttons">
                        <button class="nav-button prev-button" :class="{ expanded: currentPhotoExpanded }"
                            @click="prevPhoto">
                            <img src="@/assets/img/calendar/arrow-left.png" alt="Previous" />
                        </button>
                        <button class="nav-button next-button" :class="{ expanded: currentPhotoExpanded }"
                            @click="nextPhoto">
                            <img src="@/assets/img/calendar/arrow-right.png" alt="Next" />
                        </button>
                    </div>
                </div>
                <div v-else class="no-photos">
                    해당 날짜엔 이미지가 없습니다.
                </div>
            </div>
        </div>
    </div>
</template>

<style scoped>
@import url("@/assets/css/calendarModal.css");

.no-photos {
    display: flex;
    justify-content: center;
    align-items: center;
    height: 100%;
    font-size: 1.5rem;
    color: red;
}
</style>
