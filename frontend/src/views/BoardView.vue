<script setup>
import WhiteBoardComp from "@/components/common/WhiteBoardComp.vue";
import BoardPhotoComp from "@/components/board/BoardPhotoComp.vue";
import Page from "@/components/common/PageComp.vue";
import { ref } from "vue";

const nickname = ref('');

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
                        <input type="text" name="nickname" id="nickname-input" placeholder=" 닉네임을 입력해주세요!"
                            v-model="nickname">
                        <button type="submit" id="nickname-btn">검색</button>
                    </form>


                    <div class="btn-group">

                        정렬방식 :
                        <button class="likeSort" @click="likeSort">좋아요</button>
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

.board-area {
    /* size */
    width: 100%;
    height: 80%;

    /* display */
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;

    * {
        font-family: 'PFStardust';
        font-weight: normal;
        font-style: normal;
        font-size: 15px;
    }

    #nickname-input {
        padding-left: 10px;
        line-height: 30px;
    }

    #nickname-btn {
        line-height: 30px;
        padding: 0px 4px 0px 4px;

        &:hover {
            background-color: rgb(250, 198, 198);
        }
    }

    .btn-group button {
        padding: 0px 4px 3px 4px;
        line-height: 30px;
        height: 30px;
        margin-left: 4px;
    }
}


.button-box {
    width: 85%;

    padding: 20px 30px 20px 30px;
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.btn-group {
    button {
        padding: 0;
        border-radius: 8px;
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
    /* size */
    width: 80%;
    height: 90%;

    display: flex;
    flex-wrap: wrap;
    justify-content: center;
    align-items: center;

    overflow: scroll;
}
</style>
