<script setup>
import { ref } from "vue";
import FormIconCancelComp from "@/components/form/FormIconCancelComp.vue";
import FormIconVisibilityComp from "@/components/form/FormIconVisibilityComp.vue";
import FormMessageComp from "@/components/form/FormMessageComp.vue";

const props = defineProps({
  params: Object,
  isSend: Boolean,
});

const message = ref(null)

const inputField = ref(null);
const maxLength = props.params.label === '이메일' ? 254 : props.params.label === '닉네임' ? 8 : props.params.label.includes('비밀번호') ? 16 : 10;

const focusInput = () => {
  inputField.value.focus();
};

const toggleVisibility = (newType) => {
  props.params.type = newType;
}

const cancelInput = () => {
  props.params.value = "";
}

defineExpose({
  focusInput,
  message
})
</script>

<template>
  <div class="form-input-conatiner">
    <div class="input-container"
      :class="{ 'input-container-selected': !message || !message.isError, 'input-container-error': message && message.isError, 'background-color-disabled': isSend === true }"
      @click="focusInput">
      <input :type="params.type" v-model="params.value" ref="inputField" class="form-input"
        :class="{ 'has-content': params.value, 'background-color-disabled': isSend === true }" :maxlength="maxLength"
        :disabled="isSend === true" autocomplete="off"/>
      <label class="form-label">{{ params.label }}</label>

      <div class="form-input-etc">
        <FormIconCancelComp v-if="params.value" class="form-icon-cancel" @click="cancelInput" />
        <FormIconVisibilityComp v-if="params.label.includes('비밀번호') && params.value" class="form-icon-visibility"
          :type="params.type" @toggle-visibility="toggleVisibility" />
        <slot></slot>
      </div>
    </div>
    <FormMessageComp v-if="message" :message="message" />
  </div>
</template>

<style scoped></style>
