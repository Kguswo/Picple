<script setup>
import FormComp from "@/components/form/FormComp.vue";
import FormInputComp from "@/components/form/FormInputComp.vue";
import FormButtonComp from "@/components/form/FormButtonComp.vue";
import { useRouter } from "vue-router";
import { validateNicknameDup, validateModifyAccount } from "@/stores/validation";
import { ref } from "vue";
import Swal from "sweetalert2";

const router = useRouter();

const nickname = ref({ type: "text", label: "닉네임", value: "" });
const nicknameField = ref(null);
const checkedNickname = ref("");

const checkNicknameDup = (e) => {
  e.stopPropagation();
  const isSuccess = validateNicknameDup(nicknameField.value, nickname.value.value);
  if (!isSuccess) {
    return;
  }
  checkedNickname.value = nickname.value.value;
}

const modify = async () => {
  const isSuccess = validateModifyAccount(nicknameField.value, nickname.value.value, checkedNickname.value);
  if (!isSuccess) {
    return;
  }
  // todo: 정보 수정
  await Swal.fire({ title: "닉네임이 변경되었습니다.", width: 600 });
  router.push({ name: "main" });
};
</script>

<template>
  <FormComp title="정보 수정">
    <form class="form-content" @keyup.enter="modify">
      <div class="input-container background-color-disabled">
        <input type="text" class="form-input has-content background-color-disabled" disabled />
        <label class="form-label">이메일</label>
      </div>

      <FormInputComp :params="nickname" ref="nicknameField" class="mt-10">
        <FormButtonComp size="small" @keyup.enter="checkNicknameDup" @click="checkNicknameDup">중복</FormButtonComp>
      </FormInputComp>

      <div class="input-container background-color-disabled mt-10">
        <input type="password" class="form-input has-content background-color-disabled" autocomplete="off" disabled />
        <label class="form-label">비밀번호</label>
        <button type="button" class="form-button-small" @click="router.push('modifyPassword/modify')">변경</button>
      </div>

      <FormButtonComp size="big" @click="modify">저장</FormButtonComp>

      <div class="text-align-right mt-10">
        <FormButtonComp size="none" @click="">회원탈퇴</FormButtonComp>
      </div>
    </form>
  </FormComp>
</template>

<style scoped></style>
