<template>
    <div class="whiteboard-area">
        <WhiteBoardComp>
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
                    />
                </div>
            </div>
        </WhiteBoardComp>
    </div>
</template>

<script setup>
import WhiteBoardComp from "@/components/common/WhiteBoardComp.vue";

import { ref } from "vue";

const attributes = ref([
    {
        dot: "green",
        dates: [
            new Date(2024, 6, 1),
            new Date(2024, 6, 10),
            new Date(2025, 2, 31),
        ],
    },
    {
        dot: "red",
        dates: [
            new Date(2024, 6, 4),
            new Date(2024, 6, 10),
            new Date(2024, 6, 15),
        ],
    },
]);
</script>

<style scoped>
.whiteboard-area {
    /* size */
    width: 70%;
    height: 80%;
    min-height: 590.08px;
    min-width: 1072.2px;
}

.name-area {
    /* size */
    width: 100%;
    height: 20%;

    /* display */
    display: flex;
    justify-content: center;
    align-items: center;

    /* color */
    border-bottom: 5px solid rgba(0, 0, 0, 0.9);

    /* font */
    color: black;
    font-size: 50px;
}

.calendar-area {
    /* size */
    width: 100%;
    height: 80%;

    /* display */
    display: flex;
    justify-content: center;
    align-items: center;
}

/* calendar */
.calendar {
    /* size */
    width: 85%;
    height: 85%;

    /* display */
    background-color: #fffef7;

    /* border */
    border: 5px solid rgba(0, 0, 0, 0.8);
    border-radius: 20px;
}

::v-deep .my-calendar .vc-weekday-1 {
    color: #6366f1;
}

::v-deep .vc-container .vc-weekday-7 {
    color: #6366f1;
}

::v-deep .vc-expanded {
    width: 100%;
    height: 100%;
    border-radius: 20px;
}

::v-deep .vc-pane-container {
    display: grid;
    align-content: space-between;
    align-items: center;
    height: 100%;
}

::v-deep .vc-header {
    margin-top: 0px;
}

::v-deep .vc-pane-header-wrapper {
    height: 20%;
    z-index: 1;
}

::v-deep div.vc-pane-header-wrapper > div {
    height: 100%;
    display: flex;
    justify-content: space-around;
}

::v-deep div.vc-header.is-lg > div > button {
    background-color: #fffef7;
    font-size: 25px;
}

::v-deep button.vc-arrow.vc-prev.vc-focus {
    background-color: #fffef7;
    width: 40px;
}

::v-deep button.vc-arrow.vc-prev.vc-focus > svg {
    width: 40px;
    height: 40px;
}

::v-deep button.vc-arrow.vc-next.vc-focus {
    background-color: #fffef7;
    width: 40px;
}

::v-deep button.vc-arrow.vc-next.vc-focus > svg {
    width: 40px;
    height: 40px;
}

::v-deep .vc-pane-layout {
    height: 100%;
    position: absolute;
    top: 0;
    width: 100%;
    z-index: 0;
}

::v-deep div.vc-pane-layout > div {
    height: 100%;
}

::v-deep div.vc-pane-layout > div > div.vc-header.is-lg {
    height: 20%;
}

::v-deep .vc-weeks {
    height: 80%;
    padding: 0px;
    padding-bottom: 20px;

    display: flex;
    flex-direction: column;
}

::v-deep .vc-weekdays {
    height: 16%;

    display: flex;
    justify-content: space-around;
    align-items: center;
}

::v-deep .vc-weekday {
    font-size: 25px;
}

::v-deep .vc-week {
    height: 16%;
}

::v-deep div.vc-day-content.vc-focusable.vc-focus.vc-attr {
    font-size: 20px;
}
</style>
