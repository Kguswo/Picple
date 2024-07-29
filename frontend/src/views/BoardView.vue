<script setup>
import WhiteBoardComp from "@/components/common/WhiteBoardComp.vue";
import BoardPhotoComp from "@/components/board/BoardPhotoComp.vue";
import Page from "@/components/common/PageComp.vue";
import { ref } from "vue";

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
                    <BoardPhotoComp />
                </div>
            </div>
        </WhiteBoardComp>
    </Page>
</template>

<style scoped>
.name-area {
    width: 100%;
    height: 20%;

    display: flex;
    justify-content: center;
    align-items: center;

    border-bottom: 5px solid rgba(0, 0, 0, 0.9);

    color: black;
    font-size: 50px;
}

.board-area {
    /* * {
        font-family: 'PFStardust';
        font-weight: normal;
        font-style: normal;
        font-size: 15px;
    } */

    width: 100%;
    height: 80%;

    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
}

.button-box {
    width: 85%;
    height: 10%;
    padding: 20px 0px;
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.input-container {
    width: 250px;
    height: 50px;
    position: relative;
    display: flex;
    justify-content: center;
    align-items: center;
    border-radius: 5px;
}

.form-input {
    width: 100%;
    box-sizing: border-box;
    border-radius: 5px;
    border: 2px solid gray;
    padding: 5px 10px;
    line-height: 35px;
    cursor: pointer;
    font-size: 15px;
}

.form-button-small {
    position: absolute;
    right: 5%;
    top: 50%;
    border: none;
    border-radius: 5px;
    transform: translateY(-50%);
    padding: 5px 10px;
    font-size: 15px;
    background-color: #62abd9;
    color: white;
    cursor: pointer;
}

.btn-group {
    button {
        border-radius: 8px;
        padding: 5px 10px;
        line-height: 30px;
        margin-left: 8px;
        font-size: 15px;
        background-color: #ffffff;
        color: black;
        transition: background-color 0.3s ease;
        cursor: pointer;
    }

    .timeSort,
    .likeSort {
        &:hover {
            background-color: rgb(250, 198, 198);
        }

        &:active {
            transform: translateY(4px);
        }
    }
}

.board {
    width: 90%;
    height: 90%;

    display: flex;
    flex-wrap: wrap;
    justify-content: center;
    align-items: center;

    overflow: scroll;
}
</style>
