<script setup>
import Page from "@/components/common/Page.vue";
import WhiteBoardComp from "@/components/common/WhiteBoardComp.vue";
import ListModal from "@/components/calendar/listModal.vue";
import { ref } from "vue";
import { allPhotos } from "@/assets/js/calendarModal";

const attributes = ref([]);

const updateAttributes = () => {
    const datesWithPhotos = {};
    allPhotos.forEach((photo) => {
        if (!datesWithPhotos[photo.date]) {
            datesWithPhotos[photo.date] = { count: 1, dot: getRandomColor() };
        } else {
            datesWithPhotos[photo.date].count += 1;
        }
    });

    attributes.value = Object.keys(datesWithPhotos).map((date) => {
        const attribute = {
            dates: new Date(date),
            dot: datesWithPhotos[date].dot,
        };
        if (datesWithPhotos[date].count > 3) {
            attribute.popover = {
                label: `+${datesWithPhotos[date].count - 3}`,
            };
        }
        return attribute;
    });
};

const getRandomColor = () => {
    const colors = ["green", "red", "blue", "yellow"];
    return colors[Math.floor(Math.random() * colors.length)];
};

const showModal = ref(false);
const selectedDate = ref("");

const openModal = (date) => {
    console.log("Date clicked:", date);
    if (typeof date === "string") {
        date = new Date(date);
    } else if (date instanceof Object && date.hasOwnProperty("date")) {
        date = new Date(date.date);
    }

    // 날짜를 로컬 시간대로 변환하여 처리
    const year = date.getFullYear();
    const month = date.getMonth() + 1; // 월은 0부터 시작하므로 1을 더해줍니다.
    const day = date.getDate();

    selectedDate.value = `${year}-${String(month).padStart(2, "0")}-${String(
        day
    ).padStart(2, "0")}`;
    showModal.value = true;
};

const closeModal = () => {
    showModal.value = false;
};

updateAttributes(); // 초기 로드 시 attributes 설정
</script>

<template>
    <Page>
        <WhiteBoardComp class="whiteboard-area-calendar">
            <div class="name-area">추억 저장소</div>
            <div class="calendar-area">
                <div class="calendar">
                    <v-calendar
                        class="my-calendar"
                        transparent
                        borderless
                        expanded
                        :attributes="attributes"
                        :masks="{ title: 'YYYY MMM' }"
                        @dayclick="openModal"
                    />
                </div>
            </div>
        </WhiteBoardComp>
        <ListModal
            :visible="showModal"
            :selectedDate="selectedDate"
            @close="closeModal"
        />
    </Page>
</template>

<style scoped>
@import url("@/assets/css/calendar.css");
</style>
