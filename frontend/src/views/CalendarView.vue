<template>

  <WhiteBoardComp>

    <div class="calendarName">
      추억 저장소
    </div>

    <Calendar />

  </WhiteBoardComp>

</template>

<script setup>
import WhiteBoardComp from '@/components/common/WhiteBoardComp.vue'
import Calendar from '@/components/calendar/CalendarComp.vue'
</script>

<style scoped>
.calendarName {
  /* size */
  width: 100%;
  height: 30%;

  /* display */
  display: flex;
  justify-content: center;
  align-items: center;

  /* color */
  border-bottom: 5px solid rgba(0, 0, 0, 0.9);

  /* font */
  font-family: 'DNFBitBitv2', sans-serif;
  /* 폰트 패밀리 설정 */
  color: black;
  font-size: 50px;
}
</style>