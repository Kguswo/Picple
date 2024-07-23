<script setup>
import FormComp from "@/components/form/FormComp.vue";
import FormMessageComp from "@/components/form/FormMessageComp.vue";
import FormInputComp from "@/components/form/FormInputComp.vue";
import FormButtonComp from "@/components/form/FormButtonComp.vue";
import { useRouter } from "vue-router";
import valdiate from "@/stores/validation";
import { ref } from "vue";

const router = useRouter();

const { message, checkNickname, checkNicknameDup } = valdiate();

const nickname = ref({ type: "text", label: "닉네임", value: "" });
const nicknameField = ref(null);

const modify = () => {
  if (!checkNickname(nickname.value.value)) {
    return;
  }
  // todo: 정보 수정
};

const changeView = (viewName, params) => {
  router.push({ name: viewName, params: params });
}
</script>

<template>
  <FormComp title="정보 수정">
    <form class="form-content" @keyup.enter="modify">
      <div class="input-container background-color-disabled">
        <input type="text" class="form-input has-content background-color-disabled" disabled />
        <label class="form-label">이메일</label>
      </div>

      <FormInputComp :params="nickname" ref="nicknameField" class="mt-10">
        <FormButtonComp size="small" @click-button="checkNicknameDup(nickname.value)">중복</FormButtonComp>
      </FormInputComp>

      <div class="input-container background-color-disabled mt-10">
        <input type="password" class="form-input has-content background-color-disabled" disabled />
        <label class="form-label">비밀번호</label>
        <button type="button" class="form-button-small"
          @click="changeView('modifyPassword', { path: 'modify' })">변경</button>
      </div>

      <FormMessageComp :message="message" />

      <FormButtonComp size="big" @click-button="modify">저장</FormButtonComp>

      <div class="text-align-right mt-10">
        <FormButtonComp size="none" @click-button="">회원탈퇴</FormButtonComp>
      </div>
    </form>
  </FormComp>
</template>

<style scoped></style>
