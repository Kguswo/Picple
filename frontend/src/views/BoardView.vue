<script setup>
import WhiteBoardComp from "@/components/common/WhiteBoardComp.vue";
import BoardPhotoComp from "@/components/board/BoardPhotoComp.vue";
import Page from "@/components/common/PageComp.vue";
import { onMounted, ref } from "vue";
import { boardListApi } from "@/api/boardApi";

const photos = ref([]);

onMounted(async () => {
    const data = await boardListApi();
    photos.value = data.result;
})

const nickname = ref({ type: "text", label: "", value: "" });

const submitForm = () => {
    if (nickname.value == "") {
        window.alert("값을 입력해주세요!");
    }
    else {
        console.log('전송됨', nickname.value)
    }
}

const timeSort = () => {
    console.log('시간 정렬')
}

const likeSort = () => {
    console.log('like 정렬')
}

</script>

<template>
    <Page>
        <WhiteBoardComp class="whiteboard-area-calendar">
            <div class="name-area">게시판</div>

            <div class="board-area">
                <div class="button-box">
                    <form @submit.prevent="submitForm">
                        <div class="input-container">
                            <input type="text" name="nickname" placeholder=" 닉네임을 입력해주세요!" class="form-input"
                                maxlength="8" autocomplete="off" />
                            <button type="button" class="form-button-small">검색</button>
                        </div>
                    </form>

                    <div class="btn-group">
                        <button class="likeSort" @click="likeSort">좋아요 순</button>
                        <button class="timeSort" @click="timeSort">최신순</button>
                    </div>
                </div>

                <div class="board">
                    <BoardPhotoComp :photos="photos"/>
                </div>
            </div>
        </WhiteBoardComp>
    </Page>
</template>

<style scoped>
@import "@/assets/css/board.css";
</style>
